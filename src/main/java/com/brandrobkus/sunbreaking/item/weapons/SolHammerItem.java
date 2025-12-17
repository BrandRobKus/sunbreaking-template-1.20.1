package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.SolHammerProjectileEntity;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.weapons.fragments.FragmentHelper;
import com.brandrobkus.sunbreaking.network.ModNetworking;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.util.ModDamageTypes;
import com.brandrobkus.sunbreaking.util.ModTags;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.BundleTooltipData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.item.TooltipData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;

import java.util.*;
import java.util.stream.Stream;

public class SolHammerItem extends ToolItem implements Vanishable {
    private final float attackDamage;
    public static final int USE_THRESHOLD = 10;
    public static final float PROJECTILE_SPEED = 2.5F;
    private static final Set<UUID> playersOnCooldown = new HashSet<>();
    private static final int MAX_STORAGE = 128;

    public SolHammerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, settings);
        this.attackDamage = (float) attackDamage + toolMaterial.getAttackDamage();
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.SPEAR;
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (!(user instanceof PlayerEntity player)) return;

        int usedTicks = this.getMaxUseTime(stack) - remainingUseTicks;
        if (usedTicks < USE_THRESHOLD) return;

        if (!world.isClient) {
            stack.damage(1, player, p -> p.sendToolBreakStatus(user.getActiveHand()));

            SolHammerProjectileEntity projectile = new SolHammerProjectileEntity(world, player, stack);

            projectile.setFragmentData(
                    FragmentHelper.getFragmentCount(stack, ModItems.FRAGMENT_OF_COMBUSTION),
                    FragmentHelper.hasFragment(stack, ModItems.FRAGMENT_OF_BLISTERING),
                    FragmentHelper.hasFragment(stack, ModItems.FRAGMENT_OF_SEARING),
                    FragmentHelper.hasFragment(stack, ModItems.FRAGMENT_OF_ASHES)
            );
            projectile.setHasBulk(hasBulk(stack));

            float velocity = hasBulk(stack) ? PROJECTILE_SPEED / 2.0F : PROJECTILE_SPEED;
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, velocity, 1.0F);

            if (player.getAbilities().creativeMode) {
                projectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }

            world.spawnEntity(projectile);
            world.playSoundFromEntity(null, projectile, ModSounds.HAMMER_THROW, SoundCategory.PLAYERS, 2.0F, 1.0F);

            if (!player.getAbilities().creativeMode) {
                player.getInventory().removeOne(stack);
            }

            int cooldownTime = 20;
            player.getItemCooldownManager().set(this, cooldownTime);
            playersOnCooldown.add(player.getUuid());

            float superCost = getSuperCost(stack);
            PlayerSuperAccessor.get(player).addSuper(-superCost);

            float newSuper = PlayerSuperAccessor.get(player).getSuper();
            float newGear = PlayerSuperAccessor.get(player).getGear();

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeFloat(newSuper);
            buf.writeFloat(newGear);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                ServerPlayNetworking.send(serverPlayer, ModNetworking.SUPER_GEAR_SYNC, buf);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }

    public float getSuperCost(ItemStack stack) {
        int combustion = FragmentHelper.getFragmentCount(stack, ModItems.FRAGMENT_OF_COMBUSTION);
        int blistering = FragmentHelper.getFragmentCount(stack, ModItems.FRAGMENT_OF_BLISTERING);
        return 25f + ((25f * combustion) + (12f* blistering));
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        float superValue = PlayerSuperAccessor.get(user).getSuper();
        float superCost = getSuperCost(stack);

        if (!user.isCreative() && superValue < superCost) {
            if (world.isClient) {
                user.playSound(ModSounds.COOLDOWN_INDICATOR, SoundCategory.PLAYERS, 1, 1);
            }

            user.getItemCooldownManager().set(this, 15);
            playersOnCooldown.add(user.getUuid());
            return TypedActionResult.fail(stack);
        }

        if (stack.getDamage() >= stack.getMaxDamage()) {
            return TypedActionResult.fail(stack);
        }

        user.setCurrentHand(hand);
        return TypedActionResult.consume(stack);
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

        World world = attacker.getWorld();
        float damageAmount = this.attackDamage;

        if (hasBulk(stack)) {
            damageAmount += 4.0F;

            RegistryEntry<DamageType> entry = world.getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE)
                    .getEntry(ModDamageTypes.BULK_HAMMER_STRIKE)
                    .orElseThrow();

            DamageSource src = new DamageSource(entry, attacker, attacker);
            target.damage(src, damageAmount);

        } else {
            int bludgeoningLevel = EnchantmentHelper.getLevel(ModEnchantments.BLUDGEONING, stack);
            if (bludgeoningLevel > 0) {
                float extra = 1.0F + (bludgeoningLevel - 1) * 0.5F;
                damageAmount += extra;
            }

            RegistryEntry<DamageType> entry = world.getRegistryManager()
                    .get(RegistryKeys.DAMAGE_TYPE)
                    .getEntry(ModDamageTypes.HAMMER_STRIKE)
                    .orElseThrow();

            DamageSource src = new DamageSource(entry, attacker, attacker);
            target.damage(src, damageAmount);
        }

        int searing = FragmentHelper.getFragmentCount(stack, ModItems.FRAGMENT_OF_SEARING);
        if (FragmentHelper.hasFragment(stack, ModItems.FRAGMENT_OF_SEARING)) {
            target.setOnFireFor(3 * searing);
        }

        return true;
    }


    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getHardness(world, pos) != 0.0) {
            stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

            float extra = 0;
            int bludgeoning = EnchantmentHelper.getLevel(ModEnchantments.BLUDGEONING, stack);
            if (bludgeoning > 0) {
                extra = 1.0F + (bludgeoning - 1) * 0.5F;
            }

            if (hasBulk(stack)) {
                builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                                this.attackDamage + 4 + extra,
                                EntityAttributeModifier.Operation.ADDITION));
                builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Bulk Tool modifier",
                                -3.1F, EntityAttributeModifier.Operation.ADDITION));
            } else {
                builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                                this.attackDamage + extra,
                                EntityAttributeModifier.Operation.ADDITION));
                builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Tool modifier",
                                -2.8F, EntityAttributeModifier.Operation.ADDITION));
            }

            return builder.build();
        }
        return super.getAttributeModifiers(stack, slot);
    }

    private boolean hasBulk(ItemStack stack) {
        return EnchantmentHelper.getLevel(ModEnchantments.BULK, stack) > 0;
    }

    @Override
    public boolean isDamageable() {
        return true;
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
            } else if (itemStack.isIn(ModTags.Items.SOLAR_FRAGMENTS)) {
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
            } else if (otherStack.isIn(ModTags.Items.SOLAR_FRAGMENTS)) {
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
                || !stack.isIn(ModTags.Items.SOLAR_FRAGMENTS)) {
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
                                .formatted(Formatting.GOLD)
                );
            });
        }
        if (fragmentCounts.isEmpty()) {
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_sunbreaker_fragment.tooltip").formatted(Formatting.GOLD));
            tooltip.add(Text.translatable("tooltip.sunbreaking.requires_sunbreaker_fragment.tooltip_1").formatted(Formatting.GOLD));
        }
    }

    // ========================= ENTITY EVENTS =========================
    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        ItemUsage.spawnItemContents(entity, getBundledStacks(entity.getStack()));
    }

    // ========================= SOUNDS =========================
    private void playRemoveOneSound(Entity entity) {
        entity.playSound(ModSounds.ASPECT_REMOVE, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.2F);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(ModSounds.ASPECT_EQUIP, 0.8F, 0.8F + entity.getWorld().getRandom().nextFloat() * 0.2F);
    }
}
