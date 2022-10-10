package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class ModelPufferFishMedium extends ModelBase {
   private final ModelRenderer field_203730_a;
   private final ModelRenderer field_203731_b;
   private final ModelRenderer field_203732_c;
   private final ModelRenderer field_203733_d;
   private final ModelRenderer field_203734_e;
   private final ModelRenderer field_203735_f;
   private final ModelRenderer field_203736_g;
   private final ModelRenderer field_203737_h;
   private final ModelRenderer field_203738_i;
   private final ModelRenderer field_203739_j;
   private final ModelRenderer field_203740_k;

   public ModelPufferFishMedium() {
      super();
      this.field_78090_t = 32;
      this.field_78089_u = 32;
      boolean var1 = true;
      this.field_203730_a = new ModelRenderer(this, 12, 22);
      this.field_203730_a.func_78789_a(-2.5F, -5.0F, -2.5F, 5, 5, 5);
      this.field_203730_a.func_78793_a(0.0F, 22.0F, 0.0F);
      this.field_203731_b = new ModelRenderer(this, 24, 0);
      this.field_203731_b.func_78789_a(-2.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203731_b.func_78793_a(-2.5F, 17.0F, -1.5F);
      this.field_203732_c = new ModelRenderer(this, 24, 3);
      this.field_203732_c.func_78789_a(0.0F, 0.0F, 0.0F, 2, 0, 2);
      this.field_203732_c.func_78793_a(2.5F, 17.0F, -1.5F);
      this.field_203733_d = new ModelRenderer(this, 15, 16);
      this.field_203733_d.func_78789_a(-2.5F, -1.0F, 0.0F, 5, 1, 1);
      this.field_203733_d.func_78793_a(0.0F, 17.0F, -2.5F);
      this.field_203733_d.field_78795_f = 0.7853982F;
      this.field_203734_e = new ModelRenderer(this, 10, 16);
      this.field_203734_e.func_78789_a(-2.5F, -1.0F, -1.0F, 5, 1, 1);
      this.field_203734_e.func_78793_a(0.0F, 17.0F, 2.5F);
      this.field_203734_e.field_78795_f = -0.7853982F;
      this.field_203735_f = new ModelRenderer(this, 8, 16);
      this.field_203735_f.func_78789_a(-1.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203735_f.func_78793_a(-2.5F, 22.0F, -2.5F);
      this.field_203735_f.field_78796_g = -0.7853982F;
      this.field_203736_g = new ModelRenderer(this, 8, 16);
      this.field_203736_g.func_78789_a(-1.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203736_g.func_78793_a(-2.5F, 22.0F, 2.5F);
      this.field_203736_g.field_78796_g = 0.7853982F;
      this.field_203737_h = new ModelRenderer(this, 4, 16);
      this.field_203737_h.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203737_h.func_78793_a(2.5F, 22.0F, 2.5F);
      this.field_203737_h.field_78796_g = -0.7853982F;
      this.field_203738_i = new ModelRenderer(this, 0, 16);
      this.field_203738_i.func_78789_a(0.0F, -5.0F, 0.0F, 1, 5, 1);
      this.field_203738_i.func_78793_a(2.5F, 22.0F, -2.5F);
      this.field_203738_i.field_78796_g = 0.7853982F;
      this.field_203739_j = new ModelRenderer(this, 8, 22);
      this.field_203739_j.func_78789_a(0.0F, 0.0F, 0.0F, 1, 1, 1);
      this.field_203739_j.func_78793_a(0.5F, 22.0F, 2.5F);
      this.field_203739_j.field_78795_f = 0.7853982F;
      this.field_203740_k = new ModelRenderer(this, 17, 21);
      this.field_203740_k.func_78789_a(-2.5F, 0.0F, 0.0F, 5, 1, 1);
      this.field_203740_k.func_78793_a(0.0F, 22.0F, -2.5F);
      this.field_203740_k.field_78795_f = -0.7853982F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_203730_a.func_78785_a(var7);
      this.field_203731_b.func_78785_a(var7);
      this.field_203732_c.func_78785_a(var7);
      this.field_203733_d.func_78785_a(var7);
      this.field_203734_e.func_78785_a(var7);
      this.field_203735_f.func_78785_a(var7);
      this.field_203736_g.func_78785_a(var7);
      this.field_203737_h.func_78785_a(var7);
      this.field_203738_i.func_78785_a(var7);
      this.field_203739_j.func_78785_a(var7);
      this.field_203740_k.func_78785_a(var7);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_203731_b.field_78808_h = -0.2F + 0.4F * MathHelper.func_76126_a(var3 * 0.2F);
      this.field_203732_c.field_78808_h = 0.2F - 0.4F * MathHelper.func_76126_a(var3 * 0.2F);
   }
}
