package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.util.math.MathHelper;

public class ModelShulker extends ModelBase {
   private final ModelRenderer field_187067_b;
   private final ModelRenderer field_187068_c;
   private final ModelRenderer field_187066_a;

   public ModelShulker() {
      super();
      this.field_78089_u = 64;
      this.field_78090_t = 64;
      this.field_187068_c = new ModelRenderer(this);
      this.field_187067_b = new ModelRenderer(this);
      this.field_187066_a = new ModelRenderer(this);
      this.field_187068_c.func_78784_a(0, 0).func_78789_a(-8.0F, -16.0F, -8.0F, 16, 12, 16);
      this.field_187068_c.func_78793_a(0.0F, 24.0F, 0.0F);
      this.field_187067_b.func_78784_a(0, 28).func_78789_a(-8.0F, -8.0F, -8.0F, 16, 8, 16);
      this.field_187067_b.func_78793_a(0.0F, 24.0F, 0.0F);
      this.field_187066_a.func_78784_a(0, 52).func_78789_a(-3.0F, 0.0F, -3.0F, 6, 6, 6);
      this.field_187066_a.func_78793_a(0.0F, 12.0F, 0.0F);
   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      EntityShulker var8 = (EntityShulker)var7;
      float var9 = var3 - (float)var8.field_70173_aa;
      float var10 = (0.5F + var8.func_184688_a(var9)) * 3.1415927F;
      float var11 = -1.0F + MathHelper.func_76126_a(var10);
      float var12 = 0.0F;
      if (var10 > 3.1415927F) {
         var12 = MathHelper.func_76126_a(var3 * 0.1F) * 0.7F;
      }

      this.field_187068_c.func_78793_a(0.0F, 16.0F + MathHelper.func_76126_a(var10) * 8.0F + var12, 0.0F);
      if (var8.func_184688_a(var9) > 0.3F) {
         this.field_187068_c.field_78796_g = var11 * var11 * var11 * var11 * 3.1415927F * 0.125F;
      } else {
         this.field_187068_c.field_78796_g = 0.0F;
      }

      this.field_187066_a.field_78795_f = var5 * 0.017453292F;
      this.field_187066_a.field_78796_g = var4 * 0.017453292F;
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.field_187067_b.func_78785_a(var7);
      this.field_187068_c.func_78785_a(var7);
   }

   public ModelRenderer func_205069_a() {
      return this.field_187067_b;
   }

   public ModelRenderer func_205068_b() {
      return this.field_187068_c;
   }

   public ModelRenderer func_205067_c() {
      return this.field_187066_a;
   }
}
