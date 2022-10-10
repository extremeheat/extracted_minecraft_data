package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelVex extends ModelBiped {
   private final ModelRenderer field_191229_a;
   private final ModelRenderer field_191230_b;

   public ModelVex() {
      this(0.0F);
   }

   public ModelVex(float var1) {
      super(var1, 0.0F, 64, 64);
      this.field_178722_k.field_78806_j = false;
      this.field_178720_f.field_78806_j = false;
      this.field_178721_j = new ModelRenderer(this, 32, 0);
      this.field_178721_j.func_78790_a(-1.0F, -1.0F, -2.0F, 6, 10, 4, 0.0F);
      this.field_178721_j.func_78793_a(-1.9F, 12.0F, 0.0F);
      this.field_191230_b = new ModelRenderer(this, 0, 32);
      this.field_191230_b.func_78789_a(-20.0F, 0.0F, 0.0F, 20, 12, 1);
      this.field_191229_a = new ModelRenderer(this, 0, 32);
      this.field_191229_a.field_78809_i = true;
      this.field_191229_a.func_78789_a(0.0F, 0.0F, 0.0F, 20, 12, 1);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.func_78088_a(var1, var2, var3, var4, var5, var6, var7);
      this.field_191230_b.func_78785_a(var7);
      this.field_191229_a.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      super.func_78087_a(var1, var2, var3, var4, var5, var6, var7);
      EntityVex var8 = (EntityVex)var7;
      if (var8.func_190647_dj()) {
         if (var8.func_184591_cq() == EnumHandSide.RIGHT) {
            this.field_178723_h.field_78795_f = 3.7699115F;
         } else {
            this.field_178724_i.field_78795_f = 3.7699115F;
         }
      }

      ModelRenderer var10000 = this.field_178721_j;
      var10000.field_78795_f += 0.62831855F;
      this.field_191230_b.field_78798_e = 2.0F;
      this.field_191229_a.field_78798_e = 2.0F;
      this.field_191230_b.field_78797_d = 1.0F;
      this.field_191229_a.field_78797_d = 1.0F;
      this.field_191230_b.field_78796_g = 0.47123894F + MathHelper.func_76134_b(var3 * 0.8F) * 3.1415927F * 0.05F;
      this.field_191229_a.field_78796_g = -this.field_191230_b.field_78796_g;
      this.field_191229_a.field_78808_h = -0.47123894F;
      this.field_191229_a.field_78795_f = 0.47123894F;
      this.field_191230_b.field_78795_f = 0.47123894F;
      this.field_191230_b.field_78808_h = 0.47123894F;
   }
}
