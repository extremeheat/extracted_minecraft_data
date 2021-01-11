package net.minecraft.client.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.util.MathHelper;

public class ModelRabbit extends ModelBase {
   ModelRenderer field_178698_a;
   ModelRenderer field_178696_b;
   ModelRenderer field_178697_c;
   ModelRenderer field_178694_d;
   ModelRenderer field_178695_e;
   ModelRenderer field_178692_f;
   ModelRenderer field_178693_g;
   ModelRenderer field_178704_h;
   ModelRenderer field_178705_i;
   ModelRenderer field_178702_j;
   ModelRenderer field_178703_k;
   ModelRenderer field_178700_l;
   private float field_178701_m = 0.0F;
   private float field_178699_n = 0.0F;

   public ModelRabbit() {
      super();
      this.func_78085_a("head.main", 0, 0);
      this.func_78085_a("head.nose", 0, 24);
      this.func_78085_a("head.ear1", 0, 10);
      this.func_78085_a("head.ear2", 6, 10);
      this.field_178698_a = new ModelRenderer(this, 26, 24);
      this.field_178698_a.func_78789_a(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.field_178698_a.func_78793_a(3.0F, 17.5F, 3.7F);
      this.field_178698_a.field_78809_i = true;
      this.func_178691_a(this.field_178698_a, 0.0F, 0.0F, 0.0F);
      this.field_178696_b = new ModelRenderer(this, 8, 24);
      this.field_178696_b.func_78789_a(-1.0F, 5.5F, -3.7F, 2, 1, 7);
      this.field_178696_b.func_78793_a(-3.0F, 17.5F, 3.7F);
      this.field_178696_b.field_78809_i = true;
      this.func_178691_a(this.field_178696_b, 0.0F, 0.0F, 0.0F);
      this.field_178697_c = new ModelRenderer(this, 30, 15);
      this.field_178697_c.func_78789_a(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.field_178697_c.func_78793_a(3.0F, 17.5F, 3.7F);
      this.field_178697_c.field_78809_i = true;
      this.func_178691_a(this.field_178697_c, -0.34906584F, 0.0F, 0.0F);
      this.field_178694_d = new ModelRenderer(this, 16, 15);
      this.field_178694_d.func_78789_a(-1.0F, 0.0F, 0.0F, 2, 4, 5);
      this.field_178694_d.func_78793_a(-3.0F, 17.5F, 3.7F);
      this.field_178694_d.field_78809_i = true;
      this.func_178691_a(this.field_178694_d, -0.34906584F, 0.0F, 0.0F);
      this.field_178695_e = new ModelRenderer(this, 0, 0);
      this.field_178695_e.func_78789_a(-3.0F, -2.0F, -10.0F, 6, 5, 10);
      this.field_178695_e.func_78793_a(0.0F, 19.0F, 8.0F);
      this.field_178695_e.field_78809_i = true;
      this.func_178691_a(this.field_178695_e, -0.34906584F, 0.0F, 0.0F);
      this.field_178692_f = new ModelRenderer(this, 8, 15);
      this.field_178692_f.func_78789_a(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.field_178692_f.func_78793_a(3.0F, 17.0F, -1.0F);
      this.field_178692_f.field_78809_i = true;
      this.func_178691_a(this.field_178692_f, -0.17453292F, 0.0F, 0.0F);
      this.field_178693_g = new ModelRenderer(this, 0, 15);
      this.field_178693_g.func_78789_a(-1.0F, 0.0F, -1.0F, 2, 7, 2);
      this.field_178693_g.func_78793_a(-3.0F, 17.0F, -1.0F);
      this.field_178693_g.field_78809_i = true;
      this.func_178691_a(this.field_178693_g, -0.17453292F, 0.0F, 0.0F);
      this.field_178704_h = new ModelRenderer(this, 32, 0);
      this.field_178704_h.func_78789_a(-2.5F, -4.0F, -5.0F, 5, 4, 5);
      this.field_178704_h.func_78793_a(0.0F, 16.0F, -1.0F);
      this.field_178704_h.field_78809_i = true;
      this.func_178691_a(this.field_178704_h, 0.0F, 0.0F, 0.0F);
      this.field_178705_i = new ModelRenderer(this, 52, 0);
      this.field_178705_i.func_78789_a(-2.5F, -9.0F, -1.0F, 2, 5, 1);
      this.field_178705_i.func_78793_a(0.0F, 16.0F, -1.0F);
      this.field_178705_i.field_78809_i = true;
      this.func_178691_a(this.field_178705_i, 0.0F, -0.2617994F, 0.0F);
      this.field_178702_j = new ModelRenderer(this, 58, 0);
      this.field_178702_j.func_78789_a(0.5F, -9.0F, -1.0F, 2, 5, 1);
      this.field_178702_j.func_78793_a(0.0F, 16.0F, -1.0F);
      this.field_178702_j.field_78809_i = true;
      this.func_178691_a(this.field_178702_j, 0.0F, 0.2617994F, 0.0F);
      this.field_178703_k = new ModelRenderer(this, 52, 6);
      this.field_178703_k.func_78789_a(-1.5F, -1.5F, 0.0F, 3, 3, 2);
      this.field_178703_k.func_78793_a(0.0F, 20.0F, 7.0F);
      this.field_178703_k.field_78809_i = true;
      this.func_178691_a(this.field_178703_k, -0.3490659F, 0.0F, 0.0F);
      this.field_178700_l = new ModelRenderer(this, 32, 9);
      this.field_178700_l.func_78789_a(-0.5F, -2.5F, -5.5F, 1, 1, 1);
      this.field_178700_l.func_78793_a(0.0F, 16.0F, -1.0F);
      this.field_178700_l.field_78809_i = true;
      this.func_178691_a(this.field_178700_l, 0.0F, 0.0F, 0.0F);
   }

   private void func_178691_a(ModelRenderer var1, float var2, float var3, float var4) {
      var1.field_78795_f = var2;
      var1.field_78796_g = var3;
      var1.field_78808_h = var4;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      if (this.field_78091_s) {
         float var8 = 2.0F;
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, 5.0F * var7, 2.0F * var7);
         this.field_178704_h.func_78785_a(var7);
         this.field_178702_j.func_78785_a(var7);
         this.field_178705_i.func_78785_a(var7);
         this.field_178700_l.func_78785_a(var7);
         GlStateManager.func_179121_F();
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(1.0F / var8, 1.0F / var8, 1.0F / var8);
         GlStateManager.func_179109_b(0.0F, 24.0F * var7, 0.0F);
         this.field_178698_a.func_78785_a(var7);
         this.field_178696_b.func_78785_a(var7);
         this.field_178697_c.func_78785_a(var7);
         this.field_178694_d.func_78785_a(var7);
         this.field_178695_e.func_78785_a(var7);
         this.field_178692_f.func_78785_a(var7);
         this.field_178693_g.func_78785_a(var7);
         this.field_178703_k.func_78785_a(var7);
         GlStateManager.func_179121_F();
      } else {
         this.field_178698_a.func_78785_a(var7);
         this.field_178696_b.func_78785_a(var7);
         this.field_178697_c.func_78785_a(var7);
         this.field_178694_d.func_78785_a(var7);
         this.field_178695_e.func_78785_a(var7);
         this.field_178692_f.func_78785_a(var7);
         this.field_178693_g.func_78785_a(var7);
         this.field_178704_h.func_78785_a(var7);
         this.field_178705_i.func_78785_a(var7);
         this.field_178702_j.func_78785_a(var7);
         this.field_178703_k.func_78785_a(var7);
         this.field_178700_l.func_78785_a(var7);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      float var8 = var3 - (float)var7.field_70173_aa;
      EntityRabbit var9 = (EntityRabbit)var7;
      this.field_178700_l.field_78795_f = this.field_178704_h.field_78795_f = this.field_178705_i.field_78795_f = this.field_178702_j.field_78795_f = var5 * 0.017453292F;
      this.field_178700_l.field_78796_g = this.field_178704_h.field_78796_g = var4 * 0.017453292F;
      this.field_178705_i.field_78796_g = this.field_178700_l.field_78796_g - 0.2617994F;
      this.field_178702_j.field_78796_g = this.field_178700_l.field_78796_g + 0.2617994F;
      this.field_178701_m = MathHelper.func_76126_a(var9.func_175521_o(var8) * 3.1415927F);
      this.field_178697_c.field_78795_f = this.field_178694_d.field_78795_f = (this.field_178701_m * 50.0F - 21.0F) * 0.017453292F;
      this.field_178698_a.field_78795_f = this.field_178696_b.field_78795_f = this.field_178701_m * 50.0F * 0.017453292F;
      this.field_178692_f.field_78795_f = this.field_178693_g.field_78795_f = (this.field_178701_m * -40.0F - 11.0F) * 0.017453292F;
   }

   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
   }
}
