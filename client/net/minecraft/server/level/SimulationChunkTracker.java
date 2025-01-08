package net.minecraft.server.level;

import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.TicketStorage;

public class SimulationChunkTracker extends ChunkTracker {
   public static final int MAX_LEVEL = 33;
   protected final Long2ByteMap chunks = new Long2ByteOpenHashMap();
   private final TicketStorage ticketStorage;

   public SimulationChunkTracker(TicketStorage var1) {
      super(34, 16, 256);
      this.ticketStorage = var1;
      var1.setSimulationChunkUpdatedListener(this::update);
      this.chunks.defaultReturnValue((byte)33);
   }

   protected int getLevelFromSource(long var1) {
      return this.ticketStorage.getTicketLevelAt(var1, true);
   }

   public int getLevel(ChunkPos var1) {
      return this.getLevel(var1.toLong());
   }

   protected int getLevel(long var1) {
      return this.chunks.get(var1);
   }

   protected void setLevel(long var1, int var3) {
      if (var3 >= 33) {
         this.chunks.remove(var1);
      } else {
         this.chunks.put(var1, (byte)var3);
      }

   }

   public void runAllUpdates() {
      this.runUpdates(2147483647);
   }
}
