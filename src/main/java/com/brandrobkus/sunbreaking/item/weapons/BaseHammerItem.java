package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.BaseHammerProjectileEntity;
import com.brandrobkus.sunbreaking.entity.custom.BaseHammerProjectileEntity;
import com.brandrobkus.sunbreaking.network.ModNetworking;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.util.ModDamageTypes;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.registry.RegistryKeys;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BaseHammerItem extends ToolItem implements Vanishable {

    private final float attackDamage;
    public static final int USE_THRESHOLD = 10;
    public static final float PROJECTILE_SPEED = 2.0F;
    private static final Set<UUID> playersOnCooldown = new HashSet<>();

    public BaseHammerItem(ToolMaterial toolMaterial, int attackDamage, float attackSpeed, Item.Settings settings) {
        super(toolMaterial, settings);
        this.attackDamage = (float)attackDamage + toolMaterial.getAttackDamage();

        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(
                EntityAttributes.GENERIC_ATTACK_DAMAGE,
                new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier",
                        this.attackDamage, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.put(
                EntityAttributes.GENERIC_ATTACK_SPEED,
                new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier",
                        attackSpeed, EntityAttributeModifier.Operation.ADDITION)
        );
        builder.build();
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

            BaseHammerProjectileEntity projectile = new BaseHammerProjectileEntity(world, player, stack);
            projectile.setHasBulk(hasBulk(stack));

            float velocity = hasBulk(stack) ? PROJECTILE_SPEED / 2.0F : PROJECTILE_SPEED;
            projectile.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, velocity, 1.0F);

            if (player.getAbilities().creativeMode) {
                projectile.pickupType = PersistentProjectileEntity.PickupPermission.CREATIVE_ONLY;
            }

            world.spawnEntity(projectile);
            world.playSoundFromEntity(null, projectile, ModSounds.BASE_HAMMER_THROW, SoundCategory.PLAYERS, 2.0F, 1.3F);

            if (!player.getAbilities().creativeMode) {
                player.getInventory().removeOne(stack);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(this));
        }
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
            return TypedActionResult.fail(itemStack);
        } else {
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        }
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        World world = attacker.getWorld();
        RegistryEntry<DamageType> entry = world.getRegistryManager()
                .get(RegistryKeys.DAMAGE_TYPE)
                .getEntry(ModDamageTypes.HAMMER_STRIKE)
                .orElseThrow();

        DamageSource src = new DamageSource(
                entry,
                attacker,
                attacker
        );

        float damageAmount = this.attackDamage;

        if (hasBulk(stack)) {
            damageAmount += 4.0F;

        } else {
            int bludgeoningLevel = EnchantmentHelper.getLevel(ModEnchantments.BLUDGEONING, stack);

            if (bludgeoningLevel > 0) {
                float extra = 1.0F + (bludgeoningLevel - 1) * 0.5F;
                damageAmount += extra;
            }
        }

        target.damage(src, damageAmount);

        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if ((double)state.getHardness(world, pos) != 0.0) {
            stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(ItemStack stack, EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();

            if (hasBulk(stack)) {
                builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                ATTACK_DAMAGE_MODIFIER_ID,
                                "Weapon modifier",
                                this.attackDamage + 4,
                                EntityAttributeModifier.Operation.ADDITION));

                builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(
                                ATTACK_SPEED_MODIFIER_ID,
                                "Bulk Tool modifier",
                                -3.1F,
                                EntityAttributeModifier.Operation.ADDITION));
            } else {
                builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE,
                        new EntityAttributeModifier(
                                ATTACK_DAMAGE_MODIFIER_ID,
                                "Weapon modifier",
                                this.attackDamage,
                                EntityAttributeModifier.Operation.ADDITION));

                builder.put(EntityAttributes.GENERIC_ATTACK_SPEED,
                        new EntityAttributeModifier(
                                ATTACK_SPEED_MODIFIER_ID,
                                "Tool modifier",
                                -2.8F,
                                EntityAttributeModifier.Operation.ADDITION));
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
}
