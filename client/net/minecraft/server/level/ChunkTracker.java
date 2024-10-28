package net.minecraft.server.level;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class ChunkTracker extends DynamicGraphMinFixedPoint {
   protected ChunkTracker(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected boolean isSource(long var1) {
      return var1 == ChunkPos.INVALID_CHUNK_POS;
   }

   protected void checkNeighborsAfterUpdate(long var1, int var3, boolean var4) {
      if (!var4 || var3 < this.levelCount - 2) {
         ChunkPos var5 = new ChunkPos(var1);
         int var6 = var5.x;
         int var7 = var5.z;

         for(int var8 = -1; var8 <= 1; ++var8) {
            for(int var9 = -1; var9 <= 1; ++var9) {
               long var10 = ChunkPos.asLong(var6 + var8, var7 + var9);
               if (var10 != var1) {
                  this.checkNeighbor(var1, var10, var3, var4);
               }
            }
         }

      }
   }

   protected int getComputedLevel(long var1, long var3, int var5) {
      int var6 = var5;
      ChunkPos var7 = new ChunkPos(var1);
      int var8 = var7.x;
      int var9 = var7.z;

      for(int var10 = -1; var10 <= 1; ++var10) {
         for(int var11 = -1; var11 <= 1; ++var11) {
            long var12 = ChunkPos.asLong(var8 + var10, var9 + var11);
            if (var12 == var1) {
               var12 = ChunkPos.INVALID_CHUNK_POS;
            }

            if (var12 != var3) {
               int var14 = this.computeLevelFromNeighbor(var12, var1, this.getLevel(var12));
               if (var6 > var14) {
                  var6 = var14;
               }

               if (var6 == 0) {
                  return var6;
               }
            }
         }
      }

      return var6;
   }

   protected int computeLevelFromNeighbor(long var1, long var3, int var5) {
      return var1 == ChunkPos.INVALID_CHUNK_POS ? this.getLevelFromSource(var3) : var5 + 1;
   }

   protected abstract int getLevelFromSource(long var1);

   public void update(long var1, int var3, boolean var4) {
      this.checkEdge(ChunkPos.INVALID_CHUNK_POS, var1, var3, var4);
   }
}
