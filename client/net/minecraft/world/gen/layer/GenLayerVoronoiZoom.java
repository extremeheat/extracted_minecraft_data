package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum GenLayerVoronoiZoom implements IAreaTransformer1 {
   INSTANCE;

   private GenLayerVoronoiZoom() {
   }

   public int func_202712_a(IContextExtended<?> var1, AreaDimension var2, IArea var3, int var4, int var5) {
      int var6 = var4 + var2.func_202690_a() - 2;
      int var7 = var5 + var2.func_202691_b() - 2;
      int var8 = var2.func_202690_a() >> 2;
      int var9 = var2.func_202691_b() >> 2;
      int var10 = (var6 >> 2) - var8;
      int var11 = (var7 >> 2) - var9;
      var1.func_202698_a((long)(var10 + var8 << 2), (long)(var11 + var9 << 2));
      double var12 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D;
      double var14 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D;
      var1.func_202698_a((long)(var10 + var8 + 1 << 2), (long)(var11 + var9 << 2));
      double var16 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      double var18 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D;
      var1.func_202698_a((long)(var10 + var8 << 2), (long)(var11 + var9 + 1 << 2));
      double var20 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D;
      double var22 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      var1.func_202698_a((long)(var10 + var8 + 1 << 2), (long)(var11 + var9 + 1 << 2));
      double var24 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      double var26 = ((double)var1.func_202696_a(1024) / 1024.0D - 0.5D) * 3.6D + 4.0D;
      int var28 = var6 & 3;
      int var29 = var7 & 3;
      double var30 = ((double)var29 - var14) * ((double)var29 - var14) + ((double)var28 - var12) * ((double)var28 - var12);
      double var32 = ((double)var29 - var18) * ((double)var29 - var18) + ((double)var28 - var16) * ((double)var28 - var16);
      double var34 = ((double)var29 - var22) * ((double)var29 - var22) + ((double)var28 - var20) * ((double)var28 - var20);
      double var36 = ((double)var29 - var26) * ((double)var29 - var26) + ((double)var28 - var24) * ((double)var28 - var24);
      if (var30 < var32 && var30 < var34 && var30 < var36) {
         return var3.func_202678_a(var10 + 0, var11 + 0);
      } else if (var32 < var30 && var32 < var34 && var32 < var36) {
         return var3.func_202678_a(var10 + 1, var11 + 0) & 255;
      } else {
         return var34 < var30 && var34 < var32 && var34 < var36 ? var3.func_202678_a(var10 + 0, var11 + 1) : var3.func_202678_a(var10 + 1, var11 + 1) & 255;
      }
   }

   public AreaDimension func_202706_a(AreaDimension var1) {
      int var2 = var1.func_202690_a() >> 2;
      int var3 = var1.func_202691_b() >> 2;
      int var4 = (var1.func_202688_c() >> 2) + 2;
      int var5 = (var1.func_202689_d() >> 2) + 2;
      return new AreaDimension(var2, var3, var4, var5);
   }
}
