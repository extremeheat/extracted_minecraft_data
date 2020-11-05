package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum ZoomLayer implements AreaTransformer1 {
   NORMAL,
   FUZZY {
      protected int modeOrRandom(BigContext<?> var1, int var2, int var3, int var4, int var5) {
         return var1.random(var2, var3, var4, var5);
      }
   };

   private ZoomLayer() {
   }

   public int getParentX(int var1) {
      return var1 >> 1;
   }

   public int getParentY(int var1) {
      return var1 >> 1;
   }

   public int applyPixel(BigContext<?> var1, Area var2, int var3, int var4) {
      int var5 = var2.get(this.getParentX(var3), this.getParentY(var4));
      var1.initRandom((long)(var3 >> 1 << 1), (long)(var4 >> 1 << 1));
      int var6 = var3 & 1;
      int var7 = var4 & 1;
      if (var6 == 0 && var7 == 0) {
         return var5;
      } else {
         int var8 = var2.get(this.getParentX(var3), this.getParentY(var4 + 1));
         int var9 = var1.random(var5, var8);
         if (var6 == 0 && var7 == 1) {
            return var9;
         } else {
            int var10 = var2.get(this.getParentX(var3 + 1), this.getParentY(var4));
            int var11 = var1.random(var5, var10);
            if (var6 == 1 && var7 == 0) {
               return var11;
            } else {
               int var12 = var2.get(this.getParentX(var3 + 1), this.getParentY(var4 + 1));
               return this.modeOrRandom(var1, var5, var10, var8, var12);
            }
         }
      }
   }

   protected int modeOrRandom(BigContext<?> var1, int var2, int var3, int var4, int var5) {
      if (var3 == var4 && var4 == var5) {
         return var3;
      } else if (var2 == var3 && var2 == var4) {
         return var2;
      } else if (var2 == var3 && var2 == var5) {
         return var2;
      } else if (var2 == var4 && var2 == var5) {
         return var2;
      } else if (var2 == var3 && var4 != var5) {
         return var2;
      } else if (var2 == var4 && var3 != var5) {
         return var2;
      } else if (var2 == var5 && var3 != var4) {
         return var2;
      } else if (var3 == var4 && var2 != var5) {
         return var3;
      } else if (var3 == var5 && var2 != var4) {
         return var3;
      } else {
         return var4 == var5 && var2 != var3 ? var4 : var1.random(var2, var3, var4, var5);
      }
   }

   // $FF: synthetic method
   ZoomLayer(Object var3) {
      this();
   }
}
