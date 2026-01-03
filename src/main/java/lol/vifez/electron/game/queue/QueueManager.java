package lol.vifez.electron.game.queue;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.queue.task.ActionBarTask;
import lol.vifez.electron.game.queue.task.QueueTask;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@Getter
public class QueueManager {

    private final Map<String, Queue> queueMap;
    private final Map<UUID, Queue> playersQueue;

    public QueueManager() {
        this.queueMap = new ConcurrentHashMap<>();
        this.playersQueue = new HashMap<>();

        for (Kit kit : Practice.getInstance().getKitManager().getKits().values()) {
            queueMap.put(kit.getName(), new Queue(Practice.getInstance(), kit, false));

            if (kit.isRanked()) {
                queueMap.put("ranked_" + kit.getName(), new Queue(Practice.getInstance(), kit, true));
            }
        }

        new QueueTask(this).runTaskTimerAsynchronously(Practice.getInstance(), 20L, 20L);
        new ActionBarTask(Practice.getInstance()).runTaskTimer(Practice.getInstance(), 0L, 20L);
    }

    public Queue getQueue(Kit kit, boolean ranked) {
        return queueMap.get((ranked ? "ranked_" : "") + kit.getName());
    }

    public Queue getQueue(UUID uuid) {
        return queueMap.get(Practice.getInstance().getProfileManager().getProfile(uuid).getCurrentQueue());
    }

    public int getAllQueueSize() {
        int size = 0;

        for (Queue queue : queueMap.values()) {
            size += queue.getQueueSize();
        }

        return size;
    }

    public List<UUID> getPlayersInQueue(Kit kit, boolean ranked) {
        Queue queue = getQueue(kit, ranked);
        if (queue == null) return Collections.emptyList();
        return new ArrayList<>(queue.getPlayerJoinTimes().keySet());
    }
}