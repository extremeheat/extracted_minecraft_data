package net.minecraft.server.level;

import net.minecraft.world.level.chunk.status.ChunkStatus;

public class ChunkLevel {
   private static final int FULL_CHUNK_LEVEL = 33;
   private static final int BLOCK_TICKING_LEVEL = 32;
   private static final int ENTITY_TICKING_LEVEL = 31;
   public static final int MAX_LEVEL = 33 + ChunkStatus.maxDistance();

   public ChunkLevel() {
      super();
   }

   public static ChunkStatus generationStatus(int var0) {
      return var0 < 33 ? ChunkStatus.FULL : ChunkStatus.getStatusAroundFullChunk(var0 - 33);
   }

   public static int byStatus(ChunkStatus var0) {
      return 33 + ChunkStatus.getDistance(var0);
   }

   public static FullChunkStatus fullStatus(int var0) {
      if (var0 <= 31) {
         return FullChunkStatus.ENTITY_TICKING;
      } else if (var0 <= 32) {
         return FullChunkStatus.BLOCK_TICKING;
      } else {
         return var0 <= 33 ? FullChunkStatus.FULL : FullChunkStatus.INACCESSIBLE;
      }
   }

   public static int byStatus(FullChunkStatus var0) {
      int var10000;
      switch (var0) {
         case INACCESSIBLE -> var10000 = MAX_LEVEL;
         case FULL -> var10000 = 33;
         case BLOCK_TICKING -> var10000 = 32;
         case ENTITY_TICKING -> var10000 = 31;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public static boolean isEntityTicking(int var0) {
      return var0 <= 31;
   }

   public static boolean isBlockTicking(int var0) {
      return var0 <= 32;
   }

   public static boolean isLoaded(int var0) {
      return var0 <= MAX_LEVEL;
   }
}
