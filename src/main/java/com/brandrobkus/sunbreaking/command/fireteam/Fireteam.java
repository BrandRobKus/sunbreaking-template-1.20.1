package com.brandrobkus.sunbreaking.command.fireteam;

import java.util.*;

public class Fireteam {
    private UUID leader;
    private final Set<UUID> members = new HashSet<>();

    public Fireteam(UUID leader) {
        this.leader = leader;
    }

    public UUID getLeader() {
        return leader;
    }

    public void setLeader(UUID newLeader) {
        if (members.contains(newLeader)) {
            this.leader = newLeader;
        }
    }

    public void addMember(UUID player) {
        members.add(player);
    }

    public void removeMember(UUID player) {
        members.remove(player);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public Set<UUID> getMembers() {
        return members;
    }

    public UUID getRandomMember() {
        List<UUID> list = new ArrayList<>(members);
        return list.get(new Random().nextInt(list.size()));
    }
}
