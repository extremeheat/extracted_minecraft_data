package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractChestHorse;

public class ModelHorseArmorChests extends ModelHorseArmorBase {
   private final ModelRenderer field_199057_c = new ModelRenderer(this, 26, 21);
   private final ModelRenderer field_199058_d;

   public ModelHorseArmorChests() {
      super();
      this.field_199057_c.func_78789_a(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199058_d = new ModelRenderer(this, 26, 21);
      this.field_199058_d.func_78789_a(-4.0F, 0.0F, -2.0F, 8, 8, 3);
      this.field_199057_c.field_78796_g = -1.5707964F;
      this.field_199058_d.field_78796_g = 1.5707964F;
      this.field_199057_c.func_78793_a(6.0F, -8.0F, 0.0F);
      this.field_199058_d.func_78793_a(-6.0F, -8.0F, 0.0F);
      this.field_199049_a.func_78792_a(this.field_199057_c);
      this.field_199049_a.func_78792_a(this.field_199058_d);
   }

   protected void func_199047_a(ModelRenderer var1) {
      ModelRenderer var2 = new ModelRenderer(this, 0, 12);
      var2.func_78789_a(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      var2.func_78793_a(1.25F, -10.0F, 4.0F);
      ModelRenderer var3 = new ModelRenderer(this, 0, 12);
      var3.func_78789_a(-1.0F, -7.0F, 0.0F, 2, 7, 1);
      var3.func_78793_a(-1.25F, -10.0F, 4.0F);
      var2.field_78795_f = 0.2617994F;
      var2.field_78808_h = 0.2617994F;
      var3.field_78795_f = 0.2617994F;
      var3.field_78808_h = -0.2617994F;
      var1.func_78792_a(var2);
      var1.func_78792_a(var3);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (((AbstractChestHorse)var1).func_190695_dh()) {
         this.field_199057_c.field_78806_j = true;
         this.field_199058_d.field_78806_j = true;
      } else {
         this.field_199057_c.field_78806_j = false;
         this.field_199058_d.field_78806_j = false;
      }

      super.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
   }
}
