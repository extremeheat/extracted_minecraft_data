package net.minecraft.client.renderer.texture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;

public class TextureClock extends TextureAtlasSprite {
   private double field_94239_h;
   private double field_94240_i;

   public TextureClock(String var1) {
      super(var1);
   }

   public void func_94219_l() {
      if (!this.field_110976_a.isEmpty()) {
         Minecraft var1 = Minecraft.func_71410_x();
         double var2 = 0.0D;
         if (var1.field_71441_e != null && var1.field_71439_g != null) {
            var2 = (double)var1.field_71441_e.func_72826_c(1.0F);
            if (!var1.field_71441_e.field_73011_w.func_76569_d()) {
               var2 = Math.random();
            }
         }

         double var4;
         for(var4 = var2 - this.field_94239_h; var4 < -0.5D; ++var4) {
         }

         while(var4 >= 0.5D) {
            --var4;
         }

         var4 = MathHelper.func_151237_a(var4, -1.0D, 1.0D);
         this.field_94240_i += var4 * 0.1D;
         this.field_94240_i *= 0.8D;
         this.field_94239_h += this.field_94240_i;

         int var6;
         for(var6 = (int)((this.field_94239_h + 1.0D) * (double)this.field_110976_a.size()) % this.field_110976_a.size(); var6 < 0; var6 = (var6 + this.field_110976_a.size()) % this.field_110976_a.size()) {
         }

         if (var6 != this.field_110973_g) {
            this.field_110973_g = var6;
            TextureUtil.func_147955_a((int[][])this.field_110976_a.get(this.field_110973_g), this.field_130223_c, this.field_130224_d, this.field_110975_c, this.field_110974_d, false, false);
         }

      }
   }
}
