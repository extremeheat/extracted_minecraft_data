package net.minecraft.client.resources.server;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.server.packs.DownloadQueue;

public interface PackDownloader {
   void download(Map<UUID, DownloadQueue.DownloadRequest> var1, Consumer<DownloadQueue.BatchResult> var2);
}
