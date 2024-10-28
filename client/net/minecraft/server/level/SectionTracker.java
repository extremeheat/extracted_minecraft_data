package net.minecraft.server.level;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.lighting.DynamicGraphMinFixedPoint;

public abstract class SectionTracker extends DynamicGraphMinFixedPoint {
   protected SectionTracker(int var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected void checkNeighborsAfterUpdate(long var1, int var3, boolean var4) {
      if (!var4 || var3 < this.levelCount - 2) {
         for(int var5 = -1; var5 <= 1; ++var5) {
            for(int var6 = -1; var6 <= 1; ++var6) {
               for(int var7 = -1; var7 <= 1; ++var7) {
                  long var8 = SectionPos.offset(var1, var5, var6, var7);
                  if (var8 != var1) {
                     this.checkNeighbor(var1, var8, var3, var4);
                  }
               }
            }
         }

      }
   }

   protected int getComputedLevel(long var1, long var3, int var5) {
      int var6 = var5;

      for(int var7 = -1; var7 <= 1; ++var7) {
         for(int var8 = -1; var8 <= 1; ++var8) {
            for(int var9 = -1; var9 <= 1; ++var9) {
               long var10 = SectionPos.offset(var1, var7, var8, var9);
               if (var10 == var1) {
                  var10 = 9223372036854775807L;
               }

               if (var10 != var3) {
                  int var12 = this.computeLevelFromNeighbor(var10, var1, this.getLevel(var10));
                  if (var6 > var12) {
                     var6 = var12;
                  }

                  if (var6 == 0) {
                     return var6;
                  }
               }
            }
         }
      }

      return var6;
   }

   protected int computeLevelFromNeighbor(long var1, long var3, int var5) {
      return this.isSource(var1) ? this.getLevelFromSource(var3) : var5 + 1;
   }

   protected abstract int getLevelFromSource(long var1);

   public void update(long var1, int var3, boolean var4) {
      this.checkEdge(9223372036854775807L, var1, var3, var4);
   }
}
