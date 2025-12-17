package com.brandrobkus.sunbreaking.command.fireteam;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class FireteamManager {

    private static final Map<UUID, Fireteam> playerToTeam = new HashMap<>();
    private static final Map<UUID, UUID> pendingInvites = new HashMap<>();

    public static boolean isInTeam(ServerPlayerEntity player) {
        return playerToTeam.containsKey(player.getUuid());
    }

    public static Fireteam getTeam(ServerPlayerEntity player) {
        return playerToTeam.get(player.getUuid());
    }

    public static void createTeam(ServerPlayerEntity leader) {
        Fireteam team = new Fireteam(leader.getUuid());
        team.addMember(leader.getUuid());
        playerToTeam.put(leader.getUuid(), team);
    }

    public static void invitePlayer(ServerPlayerEntity leader, ServerPlayerEntity invitee) {
        pendingInvites.put(invitee.getUuid(), leader.getUuid());
    }

    public static UUID getInvite(ServerPlayerEntity invitee) {
        return pendingInvites.get(invitee.getUuid());
    }

    public static void removeInvite(ServerPlayerEntity invitee) {
        pendingInvites.remove(invitee.getUuid());
    }

    /** Send a message to all team members *except* a given player */
    private static void notifyTeam(Fireteam team, ServerPlayerEntity exclude, String msg) {
        for (UUID memberUuid : team.getMembers()) {
            if (exclude != null && memberUuid.equals(exclude.getUuid())) continue;

            ServerPlayerEntity member =
                    exclude.getServer().getPlayerManager().getPlayer(memberUuid);

            if (member != null) {
                member.sendMessage(Text.literal(msg), false);
            }
        }
    }

    public static void addMember(ServerPlayerEntity inviter, ServerPlayerEntity newMember) {
        Fireteam team = getTeam(inviter);
        if (team == null) {
            createTeam(inviter);
            team = getTeam(inviter);
        }

        team.addMember(newMember.getUuid());
        playerToTeam.put(newMember.getUuid(), team);

        notifyTeam(team, newMember, newMember.getName().getString() + " has joined the Fireteam!");

        if (team.getLeader() == null) {
            team.setLeader(newMember.getUuid());
            newMember.sendMessage(Text.literal("You are now the Fireteam Leader!"), false);
        }
    }

    public static void removeMember(ServerPlayerEntity player) {
        Fireteam team = getTeam(player);
        if (team == null) return;

        UUID leavingUuid = player.getUuid();

        team.removeMember(leavingUuid);
        playerToTeam.remove(leavingUuid);

        notifyTeam(team, player, player.getName().getString() + " has left the Fireteam.");

        if (team.getMembers().size() == 1) {
            UUID lastPlayerUuid = team.getMembers().iterator().next();
            ServerPlayerEntity lastPlayer =
                    player.getServer().getPlayerManager().getPlayer(lastPlayerUuid);

            if (lastPlayer != null) {
                lastPlayer.sendMessage(Text.literal("Your Fireteam has been disbanded."), false);
            }

            playerToTeam.remove(lastPlayerUuid);
            return;
        }

        if (team.getLeader().equals(leavingUuid)) {
            UUID newLeaderUuid = team.getRandomMember();
            team.setLeader(newLeaderUuid);

            ServerPlayerEntity newLeader =
                    player.getServer().getPlayerManager().getPlayer(newLeaderUuid);

            if (newLeader != null) {
                newLeader.sendMessage(Text.literal("You are now the Fireteam Leader!"), false);
            }
        }
    }

    public static boolean areTeammates(ServerPlayerEntity a, ServerPlayerEntity b) {
        Fireteam teamA = getTeam(a);
        Fireteam teamB = getTeam(b);
        return teamA != null && teamA.equals(teamB);
    }
}
