package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.EnumHandSide;

public class ModelArmorStand extends ModelArmorStandArmor {
   private final ModelRenderer field_178740_a;
   private final ModelRenderer field_178738_b;
   private final ModelRenderer field_178739_c;
   private final ModelRenderer field_178737_d;

   public ModelArmorStand() {
      this(0.0F);
   }

   public ModelArmorStand(float var1) {
      super(var1, 64, 64);
      this.field_78116_c = new ModelRenderer(this, 0, 0);
      this.field_78116_c.func_78790_a(-1.0F, -7.0F, -1.0F, 2, 7, 2, var1);
      this.field_78116_c.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_78115_e = new ModelRenderer(this, 0, 26);
      this.field_78115_e.func_78790_a(-6.0F, 0.0F, -1.5F, 12, 3, 3, var1);
      this.field_78115_e.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_178723_h = new ModelRenderer(this, 24, 0);
      this.field_178723_h.func_78790_a(-2.0F, -2.0F, -1.0F, 2, 12, 2, var1);
      this.field_178723_h.func_78793_a(-5.0F, 2.0F, 0.0F);
      this.field_178724_i = new ModelRenderer(this, 32, 16);
      this.field_178724_i.field_78809_i = true;
      this.field_178724_i.func_78790_a(0.0F, -2.0F, -1.0F, 2, 12, 2, var1);
      this.field_178724_i.func_78793_a(5.0F, 2.0F, 0.0F);
      this.field_178721_j = new ModelRenderer(this, 8, 0);
      this.field_178721_j.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 11, 2, var1);
      this.field_178721_j.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.field_178722_k = new ModelRenderer(this, 40, 16);
      this.field_178722_k.field_78809_i = true;
      this.field_178722_k.func_78790_a(-1.0F, 0.0F, -1.0F, 2, 11, 2, var1);
      this.field_178722_k.func_78793_a(1.9F, 12.0F, 0.0F);
      this.field_178740_a = new ModelRenderer(this, 16, 0);
      this.field_178740_a.func_78790_a(-3.0F, 3.0F, -1.0F, 2, 7, 2, var1);
      this.field_178740_a.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_178740_a.field_78806_j = true;
      this.field_178738_b = new ModelRenderer(this, 48, 16);
      this.field_178738_b.func_78790_a(1.0F, 3.0F, -1.0F, 2, 7, 2, var1);
      this.field_178738_b.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_178739_c = new ModelRenderer(this, 0, 48);
      this.field_178739_c.func_78790_a(-4.0F, 10.0F, -1.0F, 8, 2, 2, var1);
      this.field_178739_c.func_78793_a(0.0F, 0.0F, 0.0F);
      this.field_178737_d = new ModelRenderer(this, 0, 32);
      this.field_178737_d.func_78790_a(-6.0F, 11.0F, -6.0F, 12, 1, 12, var1);
      this.field_178737_d.func_78793_a(0.0F, 12.0F, 0.0F);
      this.field_178720_f.field_78806_j = false;
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      if (var7 instanceof EntityArmorStand) {
         EntityArmorStand var8 = (EntityArmorStand)var7;
         this.field_178724_i.field_78806_j = var8.func_175402_q();
         this.field_178723_h.field_78806_j = var8.func_175402_q();
         this.field_178737_d.field_78806_j = !var8.func_175414_r();
         this.field_178722_k.func_78793_a(1.9F, 12.0F, 0.0F);
         this.field_178721_j.func_78793_a(-1.9F, 12.0F, 0.0F);
         this.field_178740_a.field_78795_f = 0.017453292F * var8.func_175408_t().func_179415_b();
         this.field_178740_a.field_78796_g = 0.017453292F * var8.func_175408_t().func_179416_c();
         this.field_178740_a.field_78808_h = 0.017453292F * var8.func_175408_t().func_179413_d();
         this.field_178738_b.field_78795_f = 0.017453292F * var8.func_175408_t().func_179415_b();
         this.field_178738_b.field_78796_g = 0.017453292F * var8.func_175408_t().func_179416_c();
         this.field_178738_b.field_78808_h = 0.017453292F * var8.func_175408_t().func_179413_d();
         this.field_178739_c.field_78795_f = 0.017453292F * var8.func_175408_t().func_179415_b();
         this.field_178739_c.field_78796_g = 0.017453292F * var8.func_175408_t().func_179416_c();
         this.field_178739_c.field_78808_h = 0.017453292F * var8.func_175408_t().func_179413_d();
         this.field_178737_d.field_78795_f = 0.0F;
         this.field_178737_d.field_78796_g = 0.017453292F * -var7.field_70177_z;
         this.field_178737_d.field_78808_h = 0.0F;
      }
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      GlStateManager.func_179094_E();
      if (this.field_78091_s) {
         float var8 = 2.0F;
         GlStateManager.func_179152_a(0.5F, 0.5F, 0.5F);
         GlStateManager.func_179109_b(0.0F, 24.0F * var7, 0.0F);
         this.field_178740_a.func_78785_a(var7);
         this.field_178738_b.func_78785_a(var7);
         this.field_178739_c.func_78785_a(var7);
         this.field_178737_d.func_78785_a(var7);
      } else {
         if (var1.func_70093_af()) {
            GlStateManager.func_179109_b(0.0F, 0.2F, 0.0F);
         }

         this.field_178740_a.func_78785_a(var7);
         this.field_178738_b.func_78785_a(var7);
         this.field_178739_c.func_78785_a(var7);
         this.field_178737_d.func_78785_a(var7);
      }

      GlStateManager.func_179121_F();
   }

   public void func_187073_a(float var1, EnumHandSide var2) {
      ModelRenderer var3 = this.func_187074_a(var2);
      boolean var4 = var3.field_78806_j;
      var3.field_78806_j = true;
      super.func_187073_a(var1, var2);
      var3.field_78806_j = var4;
   }
}
