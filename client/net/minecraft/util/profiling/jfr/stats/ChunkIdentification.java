package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record ChunkIdentification(String level, String dimension, int x, int z) {
   public ChunkIdentification(String var1, String var2, int var3, int var4) {
      super();
      this.level = var1;
      this.dimension = var2;
      this.x = var3;
      this.z = var4;
   }

   public static ChunkIdentification from(RecordedEvent var0) {
      return new ChunkIdentification(var0.getString("level"), var0.getString("dimension"), var0.getInt("chunkPosX"), var0.getInt("chunkPosZ"));
   }

   public String level() {
      return this.level;
   }

   public String dimension() {
      return this.dimension;
   }

   public int x() {
      return this.x;
   }

   public int z() {
      return this.z;
   }
}
