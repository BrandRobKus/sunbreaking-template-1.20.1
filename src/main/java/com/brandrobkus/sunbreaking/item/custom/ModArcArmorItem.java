package com.brandrobkus.sunbreaking.item.custom;

import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.util.ModTags;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ModArcArmorItem extends ArmorItem {
    private static final int MAX_STORAGE = 128;
    private static final String SPEED_KEY = "aspectSpeed";
    private static final String SPEED_KEY_2 = "aspectSpeed2";
    private static final String SPRINT_TIME = "sprintTime";

    public ModArcArmorItem(ArmorMaterial material, Type type, Settings settings) {
        super(material, type, settings);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient() && entity instanceof PlayerEntity player && hasFullSuitOfArmorOn(player)) {
            evaluateArmorEffects(player);
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    private void evaluateArmorEffects(PlayerEntity player) {
        World world = player.getWorld();
        ItemStack chestplate = player.getInventory().getArmorStack(2);
        if (!(chestplate.getItem() instanceof ModArcArmorItem)) return;

        NbtCompound nbt = chestplate.getOrCreateNbt();

        boolean hasSurge = hasItemInBundle(chestplate, ModItems.ASPECT_OF_SURGE);

        int sprintTime = nbt.getInt(SPRINT_TIME);

        if (hasSurge && player.isSprinting()) {
            sprintTime++;

            if (sprintTime >= 180 && sprintTime < 600) {
                int remaining = player.hasStatusEffect(StatusEffects.SPEED)
                        ? player.getStatusEffect(StatusEffects.SPEED).getDuration()
                        : 0;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, remaining, 0, false, true, true));
                nbt.putBoolean(SPEED_KEY, true);
            }

            if (sprintTime >= 600) {
                int remaining = player.hasStatusEffect(StatusEffects.SPEED)
                        ? player.getStatusEffect(StatusEffects.SPEED).getDuration()
                        : 0;
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, remaining, 1, false, true, true));
                nbt.putBoolean(SPEED_KEY_2, true);
                nbt.putBoolean(SPEED_KEY, false);
            }

        } else {
            sprintTime = 0;
            nbt.putBoolean(SPEED_KEY, false);
            nbt.putBoolean(SPEED_KEY_2, false);
        }
        nbt.putInt(SPRINT_TIME, sprintTime);
    }


    public static boolean hasFullSuitOfArmorOn(PlayerEntity player) {
        return player.getInventory().getArmorStack(0).getItem() instanceof ModArcArmorItem &&
                player.getInventory().getArmorStack(1).getItem() instanceof ModArcArmorItem &&
                player.getInventory().getArmorStack(2).getItem() instanceof ModArcArmorItem &&
                player.getInventory().getArmorStack(3).getItem() instanceof ModArcArmorItem;
    }

    @Override
    public boolean isFireproof() {
        return true;
    }

    public static void tryApplyIonsResistanceIfEquipped(PlayerEntity player) {
        if (player == null || player.getWorld().isClient) return;

        if (!hasFullSuitOfArmorOn(player)) return;

        ItemStack chest = player.getInventory().getArmorStack(2);
        if (!(chest.getItem() instanceof ModArcArmorItem)) return;

        if (!hasItemInBundle(chest, ModItems.ASPECT_OF_IONS)) return;

        player.addStatusEffect(new StatusEffectInstance(
                StatusEffects.RESISTANCE,
                100,
                0,
                false,
                true,
                true
        ));
    }

    // ========================= BUNDLE STORAGE =========================
    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (!isChestplate()) return false;
        if (clickType != ClickType.RIGHT) return false;

        ItemStack itemStack = slot.getStack();
        if (itemStack.isEmpty()) {
            this.playRemoveOneSound(player);
            removeFirstStack(stack).ifPresent(removedStack -> addToBundle(stack, slot.insertStack(removedStack)));
        } else if (itemStack.isIn(ModTags.Items.ARC_ASPECTS)) {
            int amount = (MAX_STORAGE - getBundleOccupancy(stack)) / getItemOccupancy(itemStack);
            int added = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), amount, player));
            if (added > 0) {
                this.playInsertSound(player);
            }
        } else {
            player.playSound(ModSounds.COOLDOWN_INDICATOR, 0.5F, 1.0F);
        }

        return true;
    }

    @Override
    public boolean onClicked(ItemStack bundle, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStack) {
        if (!isChestplate()) return false;
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
            if (otherStack.isEmpty()) {
                removeFirstStack(bundle).ifPresent(item -> {
                    playRemoveOneSound(player);
                    cursorStack.set(item);
                });
            } else if (otherStack.isIn(ModTags.Items.ARC_ASPECTS)) {
                int added = addToBundle(bundle, otherStack);
                if (added > 0) {
                    playInsertSound(player);
                    otherStack.decrement(added);
                }
            } else {
                player.playSound(ModSounds.COOLDOWN_INDICATOR, 0.5F, 1.0F);
            }
            return true;
        }
        return false;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return this.equipAndSwap(this, world, user, hand);
    }

    // ========================= BUNDLE LOGIC =========================
    private boolean isChestplate() {
        return this.type == Type.CHESTPLATE;
    }

    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        Item item = bundle.getItem();
        if (!(item instanceof ModArcArmorItem armor) || !armor.isChestplate()) {
            return 0;
        }
        if (stack.isEmpty() || !stack.getItem().canBeNested() || !stack.isIn(ModTags.Items.ARC_ASPECTS)) return 0;

        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains("Items")) nbt.put("Items", new NbtList());

        NbtList nbtList = nbt.getList("Items", 10);
        boolean exists = nbtList.stream()
                .filter(e -> e instanceof NbtCompound)
                .map(e -> ItemStack.fromNbt((NbtCompound) e))
                .anyMatch(existing -> ItemStack.areItemsEqual(existing, stack));

        if (exists) return 0;

        int current = getBundleOccupancy(bundle);
        int occupancy = getItemOccupancy(stack);
        int amountToAdd = Math.min(stack.getCount(), (MAX_STORAGE - current) / occupancy);
        if (amountToAdd <= 0) return 0;

        ItemStack copy = stack.copyWithCount(amountToAdd);
        NbtCompound newNbt = new NbtCompound();
        copy.writeNbt(newNbt);
        nbtList.add(newNbt);

        return amountToAdd;
    }

    public static boolean hasItemInBundle(ItemStack bundle, Item target) {
        return getBundledStacks(bundle)
                .anyMatch(stack -> stack.isOf(target));
    }

    public static List<Text> getBundledItemNames(ItemStack bundle) {
        return getBundledStacks(bundle)
                .map(ItemStack::getName)
                .toList();
    }

    private static int getItemOccupancy(ItemStack stack) {
        if (stack.isOf(ModItems.STORMCALLERS_ROBES)) return 4 + getBundleOccupancy(stack);
        return 64 / stack.getMaxCount();
    }

    private static int getBundleOccupancy(ItemStack stack) {
        if (!(stack.getItem() instanceof ModArcArmorItem armor) || !armor.isChestplate()) {
            return 0;
        }
        return getBundledStacks(stack).mapToInt(s -> getItemOccupancy(s) * s.getCount()).sum();
    }

    private static Optional<ItemStack> removeFirstStack(ItemStack bundle) {
        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains("Items")) return Optional.empty();

        NbtList list = nbt.getList("Items", 10);
        if (list.isEmpty()) return Optional.empty();

        NbtCompound first = list.getCompound(0);
        list.remove(0);
        if (list.isEmpty()) bundle.removeSubNbt("Items");

        return Optional.of(ItemStack.fromNbt(first));
    }

    private static boolean dropAllBundledItems(ItemStack bundle, PlayerEntity player) {
        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains("Items")) return false;

        if (player instanceof ServerPlayerEntity) {
            NbtList list = nbt.getList("Items", 10);
            for (int i = 0; i < list.size(); i++) {
                ItemStack stack = ItemStack.fromNbt(list.getCompound(i));
                player.dropItem(stack, true);
            }
        }

        bundle.removeSubNbt("Items");
        return true;
    }

    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null || !nbt.contains("Items")) return Stream.empty();

        if (!(stack.getItem() instanceof ModArcArmorItem armor) || !armor.isChestplate()) {
            return Stream.empty();
        }

        NbtList list = nbt.getList("Items", 10);
        return list.stream()
                .filter(e -> e instanceof NbtCompound)
                .map(e -> ItemStack.fromNbt((NbtCompound) e));
    }

    // ========================= TOOLTIP =========================
    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        if (!isChestplate()) return Optional.empty();

        DefaultedList<ItemStack> list = DefaultedList.of();
        getBundledStacks(stack).forEach(list::add);
        return Optional.of(new BundleTooltipData(list, getBundleOccupancy(stack)));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        if (!isChestplate()) return;

        List<Text> itemNames = getBundledItemNames(stack);
        if (!itemNames.isEmpty()) {
            tooltip.add(Text.literal("Contents:").formatted(Formatting.YELLOW));
            itemNames.forEach(name -> tooltip.add(
                    Text.literal("- ").append(name).formatted(Formatting.AQUA)
            ));
        }
        if (itemNames.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_sunbreaker_fragment.tooltip").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_stormcaller_aspect.tooltip").formatted(Formatting.AQUA));
        }
    }

    // ========================= ENTITY EVENTS =========================
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }

    // ========================= SOUNDS =========================
    private void playRemoveOneSound(Entity entity) {
        entity.playSound(ModSounds.ASPECT_REMOVE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(ModSounds.ASPECT_EQUIP, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.4F);
    }
}
