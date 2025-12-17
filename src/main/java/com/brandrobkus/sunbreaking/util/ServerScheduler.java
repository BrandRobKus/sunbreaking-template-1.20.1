package com.brandrobkus.sunbreaking.util;

import net.minecraft.server.MinecraftServer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ServerScheduler {

    private static class ScheduledTask {
        int ticksRemaining;
        Runnable task;

        ScheduledTask(int ticks, Runnable task) {
            this.ticksRemaining = ticks;
            this.task = task;
        }
    }

    private static final List<ScheduledTask> TASKS = new ArrayList<>();

    public static void schedule(int delayTicks, Runnable task) {
        TASKS.add(new ScheduledTask(delayTicks, task));
    }

    public static void tick(MinecraftServer server) {
        Iterator<ScheduledTask> it = TASKS.iterator();
        while (it.hasNext()) {
            ScheduledTask scheduled = it.next();
            scheduled.ticksRemaining--;
            if (scheduled.ticksRemaining <= 0) {
                scheduled.task.run();
                it.remove();
            }
        }
    }
}
