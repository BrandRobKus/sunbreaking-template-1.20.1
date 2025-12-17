package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.ShadowshotArrowEntity;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.weapons.fragments.FragmentHelper;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;

import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class ShadowshotBowItem extends BowItem {
    private static final Set<UUID> playersOnCooldown = new HashSet<>();
    private static final int MAX_STORAGE = 128;

    public ShadowshotBowItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return stack -> stack.isIn(ModTags.Items.ARROWS);
    }

    public float getVoidSuperCost(ItemStack stack){
        return 37.5f;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getStackInHand(hand);
        float voidSuperCost = getVoidSuperCost(stack);
        float currentSuper = PlayerSuperAccessor.get(player).getSuper();
        boolean canUseShadowshot = player.isInSneakingPose() && currentSuper >= voidSuperCost;
        stack.getOrCreateNbt().putBoolean("sunbreaking_selectedShadowshot", canUseShadowshot);
        player.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        float voidSuperCost = getVoidSuperCost(stack);
        if (!(user instanceof PlayerEntity player)) return;

        boolean isClient = world.isClient;
        boolean selectedShadowshot = stack.getOrCreateNbt().getBoolean("sunbreaking_selectedShadowshot");
        ItemStack projectileStack = player.getProjectileType(stack);
        boolean shootShadowshot = selectedShadowshot && PlayerSuperAccessor.get(player).getSuper() >= voidSuperCost;

        if (shootShadowshot || player.isCreative()) {
            projectileStack = findShadowshotArrow(player);
        }

        if (projectileStack.isEmpty()) {
            projectileStack = player.getProjectileType(stack);
            if (projectileStack.isEmpty()) return;
        }

        int useTime = this.getMaxUseTime(stack) - remainingUseTicks;
        float pullProgress = getPullProgress(useTime);
        if (pullProgress < 0.1F) return;

        boolean hasInfinity = EnchantmentHelper.getLevel(Enchantments.INFINITY, stack) > 0;
        boolean isNormalArrow = projectileStack.isOf(Items.ARROW);
        boolean infinite = hasInfinity && isNormalArrow;

        if (!isClient) {
            PersistentProjectileEntity projectile;

            if (shootShadowshot && projectileStack.getItem() == ModItems.SHADOWSHOT_ARROW) {
                ShadowshotArrowEntity arrow = new ShadowshotArrowEntity(world, player);
                arrow.setBowStack(stack);
                arrow.setVelocity(player, player.getPitch(), player.getYaw(), 0f, pullProgress * 4f, 0f);
                arrow.setCritical(pullProgress == 1.0F);
                projectile = arrow;

                player.getItemCooldownManager().set(this, 20);
                playersOnCooldown.add(player.getUuid());

                if(!player.isCreative()) {
                    PlayerSuperAccessor.get(player).addSuper(-voidSuperCost);
                }

            } else {
                ArrowItem arrowItem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem ? projectileStack.getItem() : Items.ARROW);
                projectile = arrowItem.createArrow(world, projectileStack, player);
                projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0f, pullProgress * 4f, 0f);
                projectile.setCritical(pullProgress == 1.0F);

                int power = EnchantmentHelper.getLevel(Enchantments.POWER, stack);
                if (power > 0) projectile.setDamage(projectile.getDamage() + power * 0.5 + 0.5);

                int punch = EnchantmentHelper.getLevel(Enchantments.PUNCH, stack);
                if (punch > 0) projectile.setPunch(punch);

                if (EnchantmentHelper.getLevel(Enchantments.FLAME, stack) > 0) {
                    projectile.setOnFireFor(100);
                }

                if (!isNormalArrow) {
                    projectile.pickupType = PersistentProjectileEntity.PickupPermission.DISALLOWED;
                } else if (infinite) {
                    projectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
                }
            }

            world.spawnEntity(projectile);

            boolean consumeArrow = !infinite && !player.getAbilities().creativeMode;

            if (consumeArrow && shootShadowshot) {
                int vigilanceStacks =
                        FragmentHelper.getFragmentCount(stack, ModItems.FRAGMENT_OF_VIGILANCE);

                float saveChance = Math.min(1.0f, vigilanceStacks * 0.25f);

                if (world.getRandom().nextFloat() < saveChance) {
                    consumeArrow = false;
                }
            }

            if (consumeArrow) {
                projectileStack.decrement(1);
                if (projectileStack.isEmpty()) {
                    player.getInventory().removeOne(projectileStack);
                }
            }

        }

        world.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F,
                1.0F / (world.getRandom().nextFloat() * 0.4F + 1.2F));

        player.incrementStat(Stats.USED.getOrCreateStat(this));
    }

    private ItemStack findShadowshotArrow(PlayerEntity player) {
        for (ItemStack s : player.getInventory().main) {
            if (!s.isEmpty() && s.getItem() == ModItems.SHADOWSHOT_ARROW) return s;
        }
        for (ItemStack s : player.getInventory().offHand) {
            if (!s.isEmpty() && s.getItem() == ModItems.SHADOWSHOT_ARROW) return s;
        }
        return ItemStack.EMPTY;
    }

    public static void tick(World world) {

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
            } else if (itemStack.isIn(ModTags.Items.VOID_FRAGMENTS)) {
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
            } else if (otherStack.isIn(ModTags.Items.VOID_FRAGMENTS)) {
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
                || !stack.isIn(ModTags.Items.VOID_FRAGMENTS)) {
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
                                .formatted(Formatting.DARK_PURPLE)
                );
            });
        }
        if (fragmentCounts.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_sunbreaker_fragment.tooltip").formatted(Formatting.DARK_PURPLE));
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_nightstalker_fragment.tooltip").formatted(Formatting.DARK_PURPLE));
        }

        tooltip.add(Text.translatable("tooltip.sunbreaking.shadowshot_bow.tooltip").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable("tooltip.sunbreaking.shadowshot_bow.tooltip_1").formatted(Formatting.LIGHT_PURPLE));
        tooltip.add(Text.translatable("tooltip.sunbreaking.shadowshot_bow.tooltip_2").formatted(Formatting.LIGHT_PURPLE));


        super.appendTooltip(stack, world, tooltip, context);
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
