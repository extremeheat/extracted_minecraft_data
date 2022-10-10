package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.util.math.MathHelper;

public class ModelBoat extends ModelBase implements IMultipassModel {
   private final ModelRenderer[] field_78103_a = new ModelRenderer[5];
   private final ModelRenderer[] field_187057_b = new ModelRenderer[2];
   private final ModelRenderer field_187058_c;

   public ModelBoat() {
      super();
      this.field_78103_a[0] = (new ModelRenderer(this, 0, 0)).func_78787_b(128, 64);
      this.field_78103_a[1] = (new ModelRenderer(this, 0, 19)).func_78787_b(128, 64);
      this.field_78103_a[2] = (new ModelRenderer(this, 0, 27)).func_78787_b(128, 64);
      this.field_78103_a[3] = (new ModelRenderer(this, 0, 35)).func_78787_b(128, 64);
      this.field_78103_a[4] = (new ModelRenderer(this, 0, 43)).func_78787_b(128, 64);
      boolean var1 = true;
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      this.field_78103_a[0].func_78790_a(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.field_78103_a[0].func_78793_a(0.0F, 3.0F, 1.0F);
      this.field_78103_a[1].func_78790_a(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
      this.field_78103_a[1].func_78793_a(-15.0F, 4.0F, 4.0F);
      this.field_78103_a[2].func_78790_a(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
      this.field_78103_a[2].func_78793_a(15.0F, 4.0F, 0.0F);
      this.field_78103_a[3].func_78790_a(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.field_78103_a[3].func_78793_a(0.0F, 4.0F, -9.0F);
      this.field_78103_a[4].func_78790_a(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.field_78103_a[4].func_78793_a(0.0F, 4.0F, 9.0F);
      this.field_78103_a[0].field_78795_f = 1.5707964F;
      this.field_78103_a[1].field_78796_g = 4.712389F;
      this.field_78103_a[2].field_78796_g = 1.5707964F;
      this.field_78103_a[3].field_78796_g = 3.1415927F;
      this.field_187057_b[0] = this.func_187056_a(true);
      this.field_187057_b[0].func_78793_a(3.0F, -5.0F, 9.0F);
      this.field_187057_b[1] = this.func_187056_a(false);
      this.field_187057_b[1].func_78793_a(3.0F, -5.0F, -9.0F);
      this.field_187057_b[1].field_78796_g = 3.1415927F;
      this.field_187057_b[0].field_78808_h = 0.19634955F;
      this.field_187057_b[1].field_78808_h = 0.19634955F;
      this.field_187058_c = (new ModelRenderer(this, 0, 0)).func_78787_b(128, 64);
      this.field_187058_c.func_78790_a(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.field_187058_c.func_78793_a(0.0F, -3.0F, 1.0F);
      this.field_187058_c.field_78795_f = 1.5707964F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      EntityBoat var8 = (EntityBoat)var1;
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);

      for(int var9 = 0; var9 < 5; ++var9) {
         this.field_78103_a[var9].func_78785_a(var7);
      }

      this.func_187055_a(var8, 0, var7, var2);
      this.func_187055_a(var8, 1, var7, var2);
   }

   public void func_187054_b(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.func_179114_b(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179135_a(false, false, false, false);
      this.field_187058_c.func_78785_a(var7);
      GlStateManager.func_179135_a(true, true, true, true);
   }

   protected ModelRenderer func_187056_a(boolean var1) {
      ModelRenderer var2 = (new ModelRenderer(this, 62, var1 ? 0 : 20)).func_78787_b(128, 64);
      boolean var3 = true;
      boolean var4 = true;
      boolean var5 = true;
      float var6 = -5.0F;
      var2.func_78789_a(-1.0F, 0.0F, -5.0F, 2, 2, 18);
      var2.func_78789_a(var1 ? -1.001F : 0.001F, -3.0F, 8.0F, 1, 6, 7);
      return var2;
   }

   protected void func_187055_a(EntityBoat var1, int var2, float var3, float var4) {
      float var5 = var1.func_184448_a(var2, var4);
      ModelRenderer var6 = this.field_187057_b[var2];
      var6.field_78795_f = (float)MathHelper.func_151238_b(-1.0471975803375244D, -0.2617993950843811D, (double)((MathHelper.func_76126_a(-var5) + 1.0F) / 2.0F));
      var6.field_78796_g = (float)MathHelper.func_151238_b(-0.7853981852531433D, 0.7853981852531433D, (double)((MathHelper.func_76126_a(-var5 + 1.0F) + 1.0F) / 2.0F));
      if (var2 == 1) {
         var6.field_78796_g = 3.1415927F - var6.field_78796_g;
      }

      var6.func_78785_a(var3);
   }
}
