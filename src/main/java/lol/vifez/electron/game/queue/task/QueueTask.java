package lol.vifez.electron.game.queue.task;

import lol.vifez.electron.Practice;
import lol.vifez.electron.game.kit.Kit;
import lol.vifez.electron.game.queue.Queue;
import lol.vifez.electron.game.queue.QueueManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

@RequiredArgsConstructor
public class QueueTask extends BukkitRunnable {

    private final QueueManager queueManager;

    @Override
    public void run() {
        for (Kit kit : Practice.getInstance().getKitManager().getKits().values()) {
            if (queueManager.getQueue(kit, false) == null) {
                queueManager.getQueueMap().put(kit.getName(), new Queue(Practice.getInstance(), kit, false));

                if (kit.isRanked()) {
                    queueManager.getQueueMap().put("ranked_" + kit.getName(), new Queue(Practice.getInstance(), kit, true));
                }
            }
        }

        queueManager.getQueueMap().values().forEach(Queue::move);
    }
}
