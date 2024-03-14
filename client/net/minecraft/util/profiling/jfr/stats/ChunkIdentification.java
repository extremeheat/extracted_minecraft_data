package net.minecraft.util.profiling.jfr.stats;

import jdk.jfr.consumer.RecordedEvent;

public record ChunkIdentification(String a, String b, int c, int d) {
   private final String level;
   private final String dimension;
   private final int x;
   private final int z;

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
}
