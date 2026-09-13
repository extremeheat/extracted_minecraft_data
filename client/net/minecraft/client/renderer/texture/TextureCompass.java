package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TextureCompass extends TextureAtlasSprite {
   public double field_94244_i;
   public double field_94242_j;

   public TextureCompass(String var1) {
      super(var1);
   }

   @Override
   public void func_94219_l() {
      Minecraft var1 = Minecraft.func_71410_x();
      if (var1.field_71441_e != null && var1.field_71439_g != null) {
         this.func_94241_a(
            var1.field_71441_e, var1.field_71439_g.field_70165_t, var1.field_71439_g.field_70161_v, (double)var1.field_71439_g.field_70177_z, false, false
         );
      } else {
         this.func_94241_a(null, 0.0, 0.0, 0.0, true, false);
      }
   }

   public void func_94241_a(World var1, double var2, double var4, double var6, boolean var8, boolean var9) {
      if (!this.field_110976_a.isEmpty()) {
         double var10 = 0.0;
         if (var1 != null && !var8) {
            ChunkCoordinates var12 = var1.func_72861_E();
            double var13 = (double)var12.field_71574_a - var2;
            double var15 = (double)var12.field_71573_c - var4;
            var6 %= 360.0;
            var10 = -((var6 - 90.0) * 3.141592653589793 / 180.0 - Math.atan2(var15, var13));
            if (!var1.field_73011_w.func_76569_d()) {
               var10 = Math.random() * 3.1415927410125732 * 2.0;
            }
         }

         if (var9) {
            this.field_94244_i = var10;
         } else {
            double var18 = var10 - this.field_94244_i;

            while(var18 < -3.141592653589793) {
               var18 += 6.283185307179586;
            }

            while(var18 >= 3.141592653589793) {
               var18 -= 6.283185307179586;
            }

            if (var18 < -1.0) {
               var18 = -1.0;
            }

            if (var18 > 1.0) {
               var18 = 1.0;
            }

            this.field_94242_j += var18 * 0.1;
            this.field_94242_j *= 0.8;
            this.field_94244_i += this.field_94242_j;
         }

         int var19 = (int)((this.field_94244_i / 6.283185307179586 + 1.0) * (double)this.field_110976_a.size()) % this.field_110976_a.size();

         while(var19 < 0) {
            var19 = (var19 + this.field_110976_a.size()) % this.field_110976_a.size();
         }

         if (var19 != this.field_110973_g) {
            this.field_110973_g = var19;
            TextureUtil.func_147955_a(
               (int[][])this.field_110976_a.get(this.field_110973_g),
               this.field_130223_c,
               this.field_130224_d,
               this.field_110975_c,
               this.field_110974_d,
               false,
               false
            );
         }
      }
   }
}
