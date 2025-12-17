package com.brandrobkus.sunbreaking.item.weapons;

import com.brandrobkus.sunbreaking.entity.custom.SmokeBombProjectileEntity;
import com.brandrobkus.sunbreaking.network.ModNetworking;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class SmokeBombItem extends Item {
    public SmokeBombItem(Item.Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);

        float gearValue = PlayerSuperAccessor.get(user).getGear();
        if (gearValue < 12.5f) {
            if (world.isClient)
                user.playSound(ModSounds.COOLDOWN_INDICATOR, SoundCategory.PLAYERS, 1, 1);
            user.getItemCooldownManager().set(this, 20);

            return TypedActionResult.fail(itemStack);
        }

        PlayerSuperAccessor.get(user).addGear(-12.5F);

        float newSuper = PlayerSuperAccessor.get(user).getSuper();
        float newGear = PlayerSuperAccessor.get(user).getGear();

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeFloat(newSuper);
        buf.writeFloat(newGear);

        if (user instanceof ServerPlayerEntity serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, ModNetworking.SUPER_GEAR_SYNC, buf);
        }

        world.playSound(
                null,
                user.getX(),
                user.getY(),
                user.getZ(),
                ModSounds.SMOKE_BOMB_THROW,
                SoundCategory.NEUTRAL,
                0.5F,
                1.0F
        );
        user.getItemCooldownManager().set(this, 20);
        if (!world.isClient) {
            SmokeBombProjectileEntity smokeBombProjectileEntity = new SmokeBombProjectileEntity(world, user);
            smokeBombProjectileEntity.setItem(itemStack);
            smokeBombProjectileEntity.setVelocity(user, user.getPitch(), user.getYaw(), 0.0F, 0.75F, 1.0F);
            world.spawnEntity(smokeBombProjectileEntity);
        }

        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.decrement(1);
        }

        return TypedActionResult.success(itemStack, world.isClient());
    }
}
