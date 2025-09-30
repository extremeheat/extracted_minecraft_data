package net.minecraft.server.level;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ChunkLoadCounter {
   private final List<ChunkHolder> pendingChunks = new ArrayList();
   private int totalChunks;

   public ChunkLoadCounter() {
      super();
   }

   public void track(ServerLevel var1, Runnable var2) {
      ServerChunkCache var3 = var1.getChunkSource();
      LongOpenHashSet var4 = new LongOpenHashSet();
      var3.runDistanceManagerUpdates();
      var3.chunkMap.allChunksWithAtLeastStatus(ChunkStatus.FULL).forEach((var1x) -> var4.add(var1x.getPos().toLong()));
      var2.run();
      var3.runDistanceManagerUpdates();
      var3.chunkMap.allChunksWithAtLeastStatus(ChunkStatus.FULL).forEach((var2x) -> {
         if (!var4.contains(var2x.getPos().toLong())) {
            this.pendingChunks.add(var2x);
            ++this.totalChunks;
         }

      });
   }

   public int readyChunks() {
      return this.totalChunks - this.pendingChunks();
   }

   public int pendingChunks() {
      this.pendingChunks.removeIf((var0) -> var0.getLatestStatus() == ChunkStatus.FULL);
      return this.pendingChunks.size();
   }

   public int totalChunks() {
      return this.totalChunks;
   }
}
