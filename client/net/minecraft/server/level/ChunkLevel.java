package net.minecraft.server.level;

import javax.annotation.Nullable;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.status.ChunkStep;
import org.jetbrains.annotations.Contract;

public class ChunkLevel {
   private static final int FULL_CHUNK_LEVEL = 33;
   private static final int BLOCK_TICKING_LEVEL = 32;
   private static final int ENTITY_TICKING_LEVEL = 31;
   private static final ChunkStep FULL_CHUNK_STEP;
   public static final int RADIUS_AROUND_FULL_CHUNK;
   public static final int MAX_LEVEL;

   public ChunkLevel() {
      super();
   }

   @Nullable
   public static ChunkStatus generationStatus(int var0) {
      return getStatusAroundFullChunk(var0 - 33, (ChunkStatus)null);
   }

   @Nullable
   @Contract("_,!null->!null;_,_->_")
   public static ChunkStatus getStatusAroundFullChunk(int var0, @Nullable ChunkStatus var1) {
      if (var0 > RADIUS_AROUND_FULL_CHUNK) {
         return var1;
      } else {
         return var0 <= 0 ? ChunkStatus.FULL : FULL_CHUNK_STEP.accumulatedDependencies().get(var0);
      }
   }

   public static ChunkStatus getStatusAroundFullChunk(int var0) {
      return getStatusAroundFullChunk(var0, ChunkStatus.EMPTY);
   }

   public static int byStatus(ChunkStatus var0) {
      return 33 + FULL_CHUNK_STEP.getAccumulatedRadiusOf(var0);
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

   static {
      FULL_CHUNK_STEP = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FULL);
      RADIUS_AROUND_FULL_CHUNK = FULL_CHUNK_STEP.accumulatedDependencies().getRadius();
      MAX_LEVEL = 33 + RADIUS_AROUND_FULL_CHUNK;
   }
}
