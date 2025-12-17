package com.brandrobkus.sunbreaking;

import com.brandrobkus.sunbreaking.command.FireteamCommand;
import com.brandrobkus.sunbreaking.enchantment.ModEnchantments;
import com.brandrobkus.sunbreaking.entity.ModEntities;
import com.brandrobkus.sunbreaking.event.DamageTracker;
import com.brandrobkus.sunbreaking.event.ServerTickHandler;
import com.brandrobkus.sunbreaking.command.fireteam.FireteamEvents;
import com.brandrobkus.sunbreaking.item.custom.ModVoidArmorItem;
import com.brandrobkus.sunbreaking.item.weapons.BondItem;
import com.brandrobkus.sunbreaking.item.ModItemGroups;
import com.brandrobkus.sunbreaking.item.ModItems;
import com.brandrobkus.sunbreaking.item.weapons.ShadowshotBowItem;
import com.brandrobkus.sunbreaking.network.ModNetworking;
import com.brandrobkus.sunbreaking.util.*;
import com.brandrobkus.sunbreaking.sound.ModSounds;
import com.brandrobkus.sunbreaking.util.gui.PlayerSuperAccessor;
import com.brandrobkus.sunbreaking.util.gui.SunbreakingServerTicks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sunbreaking implements ModInitializer {
	public static final String MOD_ID = "sunbreaking";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModEnchantments.registerModEnchantments();
		ModEntities.registerModEntities();
		ModEventHandler.register();
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModSounds.registerSounds();
		ModDamageTypes.registerModDamageTypes();
		ServerPlayNetworking.registerGlobalReceiver(
				ModNetworking.TOGGLE_INVISIBILITY,
				(server, player, handler, buf, responseSender) -> {

					server.execute(() -> {
						ModVoidArmorItem.toggleInvisibility(player);
					});
				}
		);

		ServerTickEvents.END_WORLD_TICK.register((ServerWorld world) -> {
			BondItem.tick(world);
			ShadowshotBowItem.tick(world);
		});

		DamageTracker.register();
		ServerTickHandler.register();
		SunbreakingServerTicks.register();
		FireteamCommand.register();
		FireteamEvents.register();
		ModLootTableModifiers.modifyLootTables();

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {

				int resolve = PlayerSuperAccessor.get(player).getResolveTicks();
				if (resolve > 0) {
					PlayerSuperAccessor.get(player).addSuper(0.1f);

					PlayerSuperAccessor.get(player).setResolveTicks(resolve - 1);

					float newSuper = PlayerSuperAccessor.get(player).getSuper();
					float gear = PlayerSuperAccessor.get(player).getGear();
					PacketByteBuf buf = PacketByteBufs.create();
					buf.writeFloat(newSuper);
					buf.writeFloat(gear);
					ServerPlayNetworking.send(player, ModNetworking.SUPER_GEAR_SYNC, buf);
				}
			}
		});
	}
}