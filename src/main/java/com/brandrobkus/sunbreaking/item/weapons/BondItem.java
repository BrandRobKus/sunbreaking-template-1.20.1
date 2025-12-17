package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.StormBallEntity;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.network.ModNetworking;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;
import java.util.stream.Stream;

public class BondItem extends Item {
    private static final Set<UUID> playersOnCooldown = new HashSet<>();
    private static final int MAX_STORAGE = 128;

    public BondItem(Settings settings) {
        super(settings.maxDamage(300));
    }

    public float getArcSuperCost(ItemStack stack){
        return 37.5f;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);
        float arcSuperCost = getArcSuperCost(stack);
        float superValue = PlayerSuperAccessor.get(user).getSuper();
        if (superValue < arcSuperCost && !user.isCreative()) {
            if (world.isClient)
                user.playSound(ModSounds.COOLDOWN_INDICATOR, SoundCategory.PLAYERS, 1, 1);
            int cooldownTime = 15;
            user.getItemCooldownManager().set(this, cooldownTime);
            playersOnCooldown.add(user.getUuid());

            return TypedActionResult.fail(user.getStackInHand(hand));
        }

        PlayerSuperAccessor.get(user).addSuper(-arcSuperCost);

        float newSuper = PlayerSuperAccessor.get(user).getSuper();
        float newGear = PlayerSuperAccessor.get(user).getGear();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(newSuper);
        buf.writeFloat(newGear);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, ModNetworking.SUPER_GEAR_SYNC, buf);
        }

        ItemStack itemStack = user.getStackInHand(hand);

        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
            return TypedActionResult.fail(itemStack);
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                ModSounds.STORM_BALL, SoundCategory.PLAYERS, 1.5F, 1.0F);

        if (!world.isClient) {
            StormBallEntity stormBallEntity = new StormBallEntity(user, world);
            stormBallEntity.setItem(new ItemStack(ModItems.STORM_BALL));
            stormBallEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.0F, 1.0F);
            world.spawnEntity(stormBallEntity);

            if (!user.getAbilities().creativeMode) {
                itemStack.damage(1, user, (player) -> player.sendToolBreakStatus(hand));

                int cooldownTime = 20;
                user.getItemCooldownManager().set(this, cooldownTime);
                playersOnCooldown.add(user.getUuid());
            }
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }

    public static void tick(World world) {
        if (!world.isClient) {
            playersOnCooldown.removeIf(playerId -> {
                ServerPlayerEntity player = (ServerPlayerEntity) world.getPlayerByUuid(playerId);
                return false;
            });
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public int getEnchantability() {
        return 30;
    }

    // ========================= BUNDLE STORAGE =========================
    @Override
    public boolean onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType != ClickType.RIGHT) {
            return false;
        } else {
            ItemStack itemStack = slot.getStack();
            if (itemStack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeFirstStack(stack).ifPresent(removedStack -> addToBundle(stack, slot.insertStack(removedStack)));
            } else if (itemStack.isIn(ModTags.Items.ARC_FRAGMENTS)) {
                int amount = (MAX_STORAGE - getBundleOccupancy(stack)) / getItemOccupancy(itemStack);
                int added = addToBundle(stack, slot.takeStackRange(itemStack.getCount(), amount, player));
                if (added > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        }
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT && slot.canTakePartial(player)) {
            if (otherStack.isEmpty()) {
                removeFirstStack(stack).ifPresent(itemStack -> {
                    this.playRemoveOneSound(player);
                    cursorStackReference.set(itemStack);
                });
            } else if (otherStack.isIn(ModTags.Items.ARC_FRAGMENTS)) {
                int i = addToBundle(stack, otherStack);
                if (i > 0) {
                    this.playInsertSound(player);
                    otherStack.decrement(i);
                }
            }
            else {
                player.playSound(ModSounds.COOLDOWN_INDICATOR, 0.5F, 1.0F);
            }
            return true;
        }
        return false;

    }

    // ========================= BUNDLE LOGIC =========================
    private static int getUniqueFragmentCount(NbtList items) {
        return (int) items.stream()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .map(ItemStack::fromNbt)
                .map(ItemStack::getItem)
                .distinct()
                .count();
    }

    private static int addToBundle(ItemStack bundle, ItemStack stack) {
        if (stack.isEmpty()
                || !stack.getItem().canBeNested()
                || !stack.isIn(ModTags.Items.ARC_FRAGMENTS)) {
            return 0;
        }

        NbtCompound nbt = bundle.getOrCreateNbt();
        if (!nbt.contains("Items")) {
            nbt.put("Items", new NbtList());
        }

        NbtList items = nbt.getList("Items", NbtElement.COMPOUND_TYPE);

        int currentOccupancy = getBundleOccupancy(bundle);
        int itemOccupancy = getItemOccupancy(stack);
        int maxAddable = (MAX_STORAGE - currentOccupancy) / itemOccupancy;

        if (maxAddable <= 0) {
            return 0;
        }

        Optional<NbtCompound> mergeTarget = canMergeStack(stack, items);

        if (mergeTarget.isPresent()) {
            NbtCompound existingNbt = mergeTarget.get();
            ItemStack existingStack = ItemStack.fromNbt(existingNbt);

            int spaceLeft = existingStack.getMaxCount() - existingStack.getCount();
            if (spaceLeft <= 0) {
                return 0;
            }

            int toAdd = Math.min(stack.getCount(), Math.min(spaceLeft, maxAddable));
            if (toAdd <= 0) {
                return 0;
            }

            existingStack.increment(toAdd);
            existingStack.writeNbt(existingNbt);

            items.remove(existingNbt);
            items.add(0, existingNbt);

            return toAdd;
        }

        int uniqueCount = getUniqueFragmentCount(items);
        if (uniqueCount >= 2) {
            return 0;
        }

        int toAdd = Math.min(stack.getCount(), maxAddable);
        toAdd = Math.min(toAdd, stack.getMaxCount());

        if (toAdd <= 0) {
            return 0;
        }

        ItemStack copy = stack.copyWithCount(toAdd);
        NbtCompound itemNbt = new NbtCompound();
        copy.writeNbt(itemNbt);

        items.add(0, itemNbt);
        return toAdd;
    }

    private static Optional<NbtCompound> canMergeStack(ItemStack stack, NbtList items) {
        return stack.isOf(Items.BUNDLE)
                ? Optional.empty()
                : items.stream()
                .filter(NbtCompound.class::isInstance)
                .map(NbtCompound.class::cast)
                .filter(item -> ItemStack.canCombine(ItemStack.fromNbt(item), stack))
                .findFirst();
    }

    private static int getItemOccupancy(ItemStack stack) {
        if (stack.isOf(Items.BUNDLE)) {
            return 4 + getBundleOccupancy(stack);
        } else {
            if ((stack.isOf(Items.BEEHIVE) || stack.isOf(Items.BEE_NEST)) && stack.hasNbt()) {
                NbtCompound nbtCompound = BlockItem.getBlockEntityNbt(stack);
                if (nbtCompound != null && !nbtCompound.getList("Bees", NbtElement.COMPOUND_TYPE).isEmpty()) {
                    return 64;
                }
            }

            return 64 / stack.getMaxCount();
        }
    }

    private static int getBundleOccupancy(ItemStack stack) {
        return getBundledStacks(stack).mapToInt(itemStack -> getItemOccupancy(itemStack) * itemStack.getCount()).sum();
    }

    private static Optional<ItemStack> removeFirstStack(ItemStack stack) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        if (!nbtCompound.contains("Items")) {
            return Optional.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            if (nbtList.isEmpty()) {
                return Optional.empty();
            } else {
                int i = 0;
                NbtCompound nbtCompound2 = nbtList.getCompound(0);
                ItemStack itemStack = ItemStack.fromNbt(nbtCompound2);
                nbtList.remove(0);
                if (nbtList.isEmpty()) {
                    stack.removeSubNbt("Items");
                }

                return Optional.of(itemStack);
            }
        }
    }

    private static Stream<ItemStack> getBundledStacks(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        if (nbtCompound == null) {
            return Stream.empty();
        } else {
            NbtList nbtList = nbtCompound.getList("Items", NbtElement.COMPOUND_TYPE);
            return nbtList.stream().map(NbtCompound.class::cast).map(ItemStack::fromNbt);
        }
    }

    public static List<Text> getBundledItemNames(ItemStack bundle) {
        return getBundledStacks(bundle)
                .map(ItemStack::getName)
                .toList();
    }

    // ========================= TOOLTIP =========================
    private static String toRoman(int number) {
        if (number <= 0) return "";

        int[] values = {5, 4, 1};
        String[] numerals = {"V", "IV", "I"};

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (number >= values[i]) {
                number -= values[i];
                result.append(numerals[i]);
            }
        }
        return result.toString();
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
        DefaultedList<ItemStack> defaultedList = DefaultedList.of();
        getBundledStacks(stack).forEach(defaultedList::add);
        return Optional.of(new BundleTooltipData(defaultedList, getBundleOccupancy(stack)));
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {

        Map<Item, Integer> fragmentCounts = new LinkedHashMap<>();

        getBundledStacks(stack).forEach(fragmentStack -> {
            fragmentCounts.merge(
                    fragmentStack.getItem(),
                    fragmentStack.getCount(),
                    Integer::sum
            );
        });

        if (!fragmentCounts.isEmpty()) {
            tooltip.add(Text.literal("Contents:").formatted(Formatting.YELLOW));

            fragmentCounts.forEach((item, count) -> {
                Text name = item.getName();
                if (count > 1) {
                    name = Text.literal(name.getString() + " - " + toRoman(count));
                }

                tooltip.add(
                        Text.literal("- ")
                                .append(name)
                                .formatted(Formatting.AQUA)
                );
            });
        }
        if (fragmentCounts.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_sunbreaker_fragment.tooltip").formatted(Formatting.AQUA));
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_stormcaller_fragment.tooltip_1").formatted(Formatting.AQUA));
        }
    }
    // ========================= ENTITY EVENTS =========================
    @Override
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
