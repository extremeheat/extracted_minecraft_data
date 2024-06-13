package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record ChunkIdentification(String level, String dimension, int x, int z) {
   public ChunkIdentification(String level, String dimension, int x, int z) {
      super();
      this.level = level;
      this.dimension = dimension;
      this.x = x;
      this.z = z;
   }

   public static ChunkIdentification from(RecordedEvent var0) {
      return new ChunkIdentification(var0.getString("level"), var0.getString("dimension"), var0.getInt("chunkPosX"), var0.getInt("chunkPosZ"));
   }
}
