package net.minecraft.world.level.newbiome.layer;

import net.minecraft.world.level.newbiome.area.Area;
import net.minecraft.world.level.newbiome.context.BigContext;
import net.minecraft.world.level.newbiome.layer.traits.AreaTransformer1;

public enum VoronoiZoom implements AreaTransformer1 {
   INSTANCE;

   private VoronoiZoom() {
   }

   public int applyPixel(BigContext<?> var1, Area var2, int var3, int var4) {
      int var5 = var3 - 2;
      int var6 = var4 - 2;
      int var7 = var5 >> 2;
      int var8 = var6 >> 2;
      int var9 = var7 << 2;
      int var10 = var8 << 2;
      var1.initRandom((long)var9, (long)var10);
      double var11 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      double var13 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      var1.initRandom((long)(var9 + 4), (long)var10);
      double var15 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      double var17 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      var1.initRandom((long)var9, (long)(var10 + 4));
      double var19 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D;
      double var21 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      var1.initRandom((long)(var9 + 4), (long)(var10 + 4));
      double var23 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      double var25 = ((double)var1.nextRandom(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      int var27 = var5 & 3;
      int var28 = var6 & 3;
      double var29 = ((double)var28 - var13) * ((double)var28 - var13) + ((double)var27 - var11) * ((double)var27 - var11);
      double var31 = ((double)var28 - var17) * ((double)var28 - var17) + ((double)var27 - var15) * ((double)var27 - var15);
      double var33 = ((double)var28 - var21) * ((double)var28 - var21) + ((double)var27 - var19) * ((double)var27 - var19);
      double var35 = ((double)var28 - var25) * ((double)var28 - var25) + ((double)var27 - var23) * ((double)var27 - var23);
      if (var29 < var31 && var29 < var33 && var29 < var35) {
         return var2.get(this.getParentX(var9), this.getParentY(var10));
      } else if (var31 < var29 && var31 < var33 && var31 < var35) {
         return var2.get(this.getParentX(var9 + 4), this.getParentY(var10)) & 255;
      } else {
         return var33 < var29 && var33 < var31 && var33 < var35 ? var2.get(this.getParentX(var9), this.getParentY(var10 + 4)) : var2.get(this.getParentX(var9 + 4), this.getParentY(var10 + 4)) & 255;
      }
   }

   public int getParentX(int var1) {
      return var1 >> 2;
   }

   public int getParentY(int var1) {
      return var1 >> 2;
   }
}
