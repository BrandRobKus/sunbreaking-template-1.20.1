package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.entity.custom.StormBallEntity;
import com.brandrobkus.sunbreaking.item.ModItems;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SnowballBondItem extends Item {
    private static final Set<UUID> playersOnCooldown = new HashSet<>();

    public SnowballBondItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack itemStack = user.getStackInHand(hand);

        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
            return TypedActionResult.fail(itemStack);
        }

        world.playSound(null, user.getX(), user.getY(), user.getZ(),
                SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.PLAYERS, 1.5F, 1.0F);

        if (!world.isClient) {
            StormBallEntity stormBallEntity = new StormBallEntity(user, world);
            stormBallEntity.setItem(new ItemStack(Items.SNOWBALL));
            stormBallEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 1.0F, 1.0F);
            world.spawnEntity(stormBallEntity);

            if (!user.getAbilities().creativeMode) {
                itemStack.damage(1, user, (player) -> player.sendToolBreakStatus(hand));

                int bondCooldownReduction = EnchantmentHelper.getLevel(ModEnchantments.AMPLITUDE, itemStack);

                int cooldownTime = 60 - (bondCooldownReduction * 10);
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
                if (player != null && !player.getItemCooldownManager().isCoolingDown(ModItems.STORMCALLERS_BOND)) {
                    player.playSound(SoundEvents.ITEM_BUCKET_FILL_POWDER_SNOW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    return true;
                }
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
}
