package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;

public class TextureClock extends TextureAtlasSprite {
   private double field_94239_h;
   private double field_94240_i;

   public TextureClock(String var1) {
      super(var1);
   }

   @Override
   public void func_94219_l() {
      if (!this.field_110976_a.isEmpty()) {
         Minecraft var1 = Minecraft.func_71410_x();
         double var2 = 0.0;
         if (var1.field_71441_e != null && var1.field_71439_g != null) {
            float var4 = var1.field_71441_e.func_72826_c(1.0F);
            var2 = (double)var4;
            if (!var1.field_71441_e.field_73011_w.func_76569_d()) {
               var2 = Math.random();
            }
         }

         double var7 = var2 - this.field_94239_h;

         while(var7 < -0.5) {
            ++var7;
         }

         while(var7 >= 0.5) {
            --var7;
         }

         if (var7 < -1.0) {
            var7 = -1.0;
         }

         if (var7 > 1.0) {
            var7 = 1.0;
         }

         this.field_94240_i += var7 * 0.1;
         this.field_94240_i *= 0.8;
         this.field_94239_h += this.field_94240_i;
         int var6 = (int)((this.field_94239_h + 1.0) * (double)this.field_110976_a.size()) % this.field_110976_a.size();

         while(var6 < 0) {
            var6 = (var6 + this.field_110976_a.size()) % this.field_110976_a.size();
         }

         if (var6 != this.field_110973_g) {
            this.field_110973_g = var6;
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
