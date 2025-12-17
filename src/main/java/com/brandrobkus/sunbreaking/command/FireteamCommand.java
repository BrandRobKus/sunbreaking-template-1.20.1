package com.brandrobkus.sunbreaking.command;

import com.brandrobkus.sunbreaking.command.fireteam.Fireteam;
import com.brandrobkus.sunbreaking.command.fireteam.FireteamManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.command.argument.EntityArgumentType.player;

public class FireteamCommand {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(literal("fireteam")
                    .executes(ctx -> {
                        ServerPlayerEntity player = ctx.getSource().getPlayer();
                        if (!FireteamManager.isInTeam(player)) {
                            player.sendMessage(Text.literal("You are not in a Fireteam!"), false);
                            return 0;
                        }

                        var team = FireteamManager.getTeam(player);
                        StringBuilder sb = new StringBuilder("Fireteam members: ");

                        for (UUID memberUuid : team.getMembers()) {
                            ServerPlayerEntity member = player.getServer().getPlayerManager().getPlayer(memberUuid);
                            if (member == null) continue; // Player might be offline

                            if (memberUuid.equals(team.getLeader())) {
                                sb.append("§e").append(member.getName().getString()).append("§r, "); // Yellow leader
                            } else {
                                sb.append(member.getName().getString()).append(", ");
                            }
                        }

                        if (sb.length() > 0) sb.setLength(sb.length() - 2);

                        player.sendMessage(Text.literal(sb.toString()), false);
                        return 1;
                    })
                    .then(literal("invite")
                            .then(argument("player", player())
                                    .executes(ctx -> {
                                        ServerPlayerEntity inviter = ctx.getSource().getPlayer();
                                        ServerPlayerEntity invitee = EntityArgumentType.getPlayer(ctx, "player");


                                        if (FireteamManager.isInTeam(invitee)) {
                                            inviter.sendMessage(Text.literal(invitee.getName().getString() + " is already in a Fireteam!"), false);
                                            return 0;
                                        }

                                        FireteamManager.invitePlayer(inviter, invitee);
                                        invitee.sendMessage(Text.literal(inviter.getName().getString() + " invited you to a Fireteam!"), false);
                                        inviter.sendMessage(Text.literal("Invite sent to " + invitee.getName().getString()), false);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("accept")
                            .then(argument("player", player())
                                    .executes(ctx -> {
                                        ServerPlayerEntity invitee = ctx.getSource().getPlayer();
                                        ServerPlayerEntity inviter = EntityArgumentType.getPlayer(ctx, "player");

                                        UUID invitedBy = FireteamManager.getInvite(invitee);
                                        if (invitedBy == null || !invitedBy.equals(inviter.getUuid())) {
                                            invitee.sendMessage(Text.literal("No invite from " + inviter.getName().getString()), false);
                                            return 0;
                                        }

                                        FireteamManager.addMember(inviter, invitee);
                                        FireteamManager.removeInvite(invitee);

                                        invitee.sendMessage(Text.literal("You joined " + inviter.getName().getString() + "'s Fireteam!"), false);
                                        inviter.sendMessage(Text.literal(invitee.getName().getString() + " joined your Fireteam!"), false);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("reject")
                            .then(argument("player", player())
                                    .executes(ctx -> {
                                        ServerPlayerEntity invitee = ctx.getSource().getPlayer();
                                        ServerPlayerEntity inviter = EntityArgumentType.getPlayer(ctx, "player");

                                        UUID invitedBy = FireteamManager.getInvite(invitee);
                                        if (invitedBy == null || !invitedBy.equals(inviter.getUuid())) {
                                            invitee.sendMessage(Text.literal("No invite from " + inviter.getName().getString()), false);
                                            return 0;
                                        }

                                        FireteamManager.removeInvite(invitee);

                                        invitee.sendMessage(Text.literal("You rejected " + inviter.getName().getString() + "'s Fireteam invite."), false);
                                        inviter.sendMessage(Text.literal(invitee.getName().getString() + " rejected your Fireteam invite."), false);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("leave")
                            .executes(ctx -> {
                                ServerPlayerEntity player = ctx.getSource().getPlayer();
                                if (!FireteamManager.isInTeam(player)) {
                                    player.sendMessage(Text.literal("You are not in a Fireteam!"), false);
                                    return 0;
                                }

                                FireteamManager.removeMember(player);
                                player.sendMessage(Text.literal("You left your Fireteam."), false);
                                return 1;
                            })
                    )
                    .then(literal("kick")
                            .then(argument("player", player())
                                    .executes(ctx -> {
                                        ServerPlayerEntity leader = ctx.getSource().getPlayer();
                                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");

                                        if (!FireteamManager.isInTeam(leader)) {
                                            leader.sendMessage(Text.literal("You are not in a Fireteam!"), false);
                                            return 0;
                                        }

                                        if (!FireteamManager.getTeam(leader).getLeader().equals(leader.getUuid())) {
                                            leader.sendMessage(Text.literal("Only the Fireteam Leader can kick!"), false);
                                            return 0;
                                        }

                                        FireteamManager.removeMember(target);
                                        leader.sendMessage(Text.literal(target.getName().getString() + " was kicked from the Fireteam."), false);
                                        target.sendMessage(Text.literal("You were kicked from the Fireteam."), false);
                                        return 1;
                                    })
                            )
                    )
                    .then(literal("promote")
                            .then(argument("player", player())
                                    .executes(ctx -> {
                                        ServerPlayerEntity leader = ctx.getSource().getPlayer();
                                        ServerPlayerEntity target = EntityArgumentType.getPlayer(ctx, "player");

                                        if (!FireteamManager.isInTeam(leader)) {
                                            leader.sendMessage(Text.literal("You are not in a Fireteam!"), false);
                                            return 0;
                                        }

                                        Fireteam team = FireteamManager.getTeam(leader);

                                        if (!team.getLeader().equals(leader.getUuid())) {
                                            leader.sendMessage(Text.literal("Only the Fireteam Leader can promote!"), false);
                                            return 0;
                                        }

                                        if (!team.getMembers().contains(target.getUuid())) {
                                            leader.sendMessage(Text.literal(target.getName().getString() + " is not in your Fireteam!"), false);
                                            return 0;
                                        }

                                        team.setLeader(target.getUuid());
                                        target.sendMessage(Text.literal("You are now the Fireteam Leader!"), false);
                                        leader.sendMessage(Text.literal("You promoted " + target.getName().getString() + " to Fireteam Leader."), false);
                                        return 1;
                                    })
                            )
                    )

            );
        });
    }
}
