package net.minecraft.server.level;

import net.minecraft.world.level.TicketStorage;

class LoadingChunkTracker extends ChunkTracker {
   private static final int MAX_LEVEL;
   private final DistanceManager distanceManager;
   private final TicketStorage ticketStorage;

   public LoadingChunkTracker(DistanceManager var1, TicketStorage var2) {
      super(MAX_LEVEL + 1, 16, 256);
      this.distanceManager = var1;
      this.ticketStorage = var2;
      var2.setLoadingChunkUpdatedListener(this::update);
   }

   protected int getLevelFromSource(long var1) {
      return this.ticketStorage.getTicketLevelAt(var1, false);
   }

   protected int getLevel(long var1) {
      if (!this.distanceManager.isChunkToRemove(var1)) {
         ChunkHolder var3 = this.distanceManager.getChunk(var1);
         if (var3 != null) {
            return var3.getTicketLevel();
         }
      }

      return MAX_LEVEL;
   }

   protected void setLevel(long var1, int var3) {
      ChunkHolder var4 = this.distanceManager.getChunk(var1);
      int var5 = var4 == null ? MAX_LEVEL : var4.getTicketLevel();
      if (var5 != var3) {
         var4 = this.distanceManager.updateChunkScheduling(var1, var3, var4, var5);
         if (var4 != null) {
            this.distanceManager.chunksToUpdateFutures.add(var4);
         }

      }
   }

   public int runDistanceUpdates(int var1) {
      return this.runUpdates(var1);
   }

   static {
      MAX_LEVEL = ChunkLevel.MAX_LEVEL + 1;
   }
}
