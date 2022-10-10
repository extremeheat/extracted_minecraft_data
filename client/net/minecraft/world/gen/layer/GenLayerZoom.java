package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IContextExtended;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum GenLayerZoom implements IAreaTransformer1 {
   NORMAL,
   FUZZY {
      protected int func_202715_a(IContextExtended<?> var1, int var2, int var3, int var4, int var5) {
         return var1.func_202697_a(var2, var3, var4, var5);
      }
   };

   private GenLayerZoom() {
   }

   public AreaDimension func_202706_a(AreaDimension var1) {
      int var2 = var1.func_202690_a() >> 1;
      int var3 = var1.func_202691_b() >> 1;
      int var4 = (var1.func_202688_c() >> 1) + 3;
      int var5 = (var1.func_202689_d() >> 1) + 3;
      return new AreaDimension(var2, var3, var4, var5);
   }

   public int func_202712_a(IContextExtended<?> var1, AreaDimension var2, IArea var3, int var4, int var5) {
      int var6 = var2.func_202690_a() >> 1;
      int var7 = var2.func_202691_b() >> 1;
      int var8 = var4 + var2.func_202690_a();
      int var9 = var5 + var2.func_202691_b();
      int var10 = (var8 >> 1) - var6;
      int var11 = var10 + 1;
      int var12 = (var9 >> 1) - var7;
      int var13 = var12 + 1;
      int var14 = var3.func_202678_a(var10, var12);
      var1.func_202698_a((long)(var8 >> 1 << 1), (long)(var9 >> 1 << 1));
      int var15 = var8 & 1;
      int var16 = var9 & 1;
      if (var15 == 0 && var16 == 0) {
         return var14;
      } else {
         int var17 = var3.func_202678_a(var10, var13);
         int var18 = var1.func_202697_a(var14, var17);
         if (var15 == 0 && var16 == 1) {
            return var18;
         } else {
            int var19 = var3.func_202678_a(var11, var12);
            int var20 = var1.func_202697_a(var14, var19);
            if (var15 == 1 && var16 == 0) {
               return var20;
            } else {
               int var21 = var3.func_202678_a(var11, var13);
               return this.func_202715_a(var1, var14, var19, var17, var21);
            }
         }
      }
   }

   protected int func_202715_a(IContextExtended<?> var1, int var2, int var3, int var4, int var5) {
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
         return var4 == var5 && var2 != var3 ? var4 : var1.func_202697_a(var2, var3, var4, var5);
      }
   }

   // $FF: synthetic method
   GenLayerZoom(Object var3) {
      this();
   }
}
