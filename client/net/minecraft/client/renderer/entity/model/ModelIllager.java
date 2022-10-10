package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelIllager extends ModelBase {
   private final ModelRenderer field_191217_a;
   private final ModelRenderer field_193775_b;
   private final ModelRenderer field_191218_b;
   private final ModelRenderer field_191219_c;
   private final ModelRenderer field_191220_d;
   private final ModelRenderer field_191221_e;
   private final ModelRenderer field_191222_f;
   private final ModelRenderer field_191223_g;
   private final ModelRenderer field_191224_h;

   public ModelIllager(float var1, float var2, int var3, int var4) {
      super();
      this.field_191217_a = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_191217_a.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_191217_a.func_78784_a(0, 0).func_78790_a(-4.0F, -10.0F, -4.0F, 8, 10, 8, var1);
      this.field_193775_b = (new ModelRenderer(this, 32, 0)).func_78787_b(var3, var4);
      this.field_193775_b.func_78790_a(-4.0F, -10.0F, -4.0F, 8, 12, 8, var1 + 0.45F);
      this.field_191217_a.func_78792_a(this.field_193775_b);
      this.field_193775_b.field_78806_j = false;
      this.field_191222_f = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_191222_f.func_78793_a(0.0F, var2 - 2.0F, 0.0F);
      this.field_191222_f.func_78784_a(24, 0).func_78790_a(-1.0F, -1.0F, -6.0F, 2, 4, 2, var1);
      this.field_191217_a.func_78792_a(this.field_191222_f);
      this.field_191218_b = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_191218_b.func_78793_a(0.0F, 0.0F + var2, 0.0F);
      this.field_191218_b.func_78784_a(16, 20).func_78790_a(-4.0F, 0.0F, -3.0F, 8, 12, 6, var1);
      this.field_191218_b.func_78784_a(0, 38).func_78790_a(-4.0F, 0.0F, -3.0F, 8, 18, 6, var1 + 0.5F);
      this.field_191219_c = (new ModelRenderer(this)).func_78787_b(var3, var4);
      this.field_191219_c.func_78793_a(0.0F, 0.0F + var2 + 2.0F, 0.0F);
      this.field_191219_c.func_78784_a(44, 22).func_78790_a(-8.0F, -2.0F, -2.0F, 4, 8, 4, var1);
      ModelRenderer var5 = (new ModelRenderer(this, 44, 22)).func_78787_b(var3, var4);
      var5.field_78809_i = true;
      var5.func_78790_a(4.0F, -2.0F, -2.0F, 4, 8, 4, var1);
      this.field_191219_c.func_78792_a(var5);
      this.field_191219_c.func_78784_a(40, 38).func_78790_a(-4.0F, 2.0F, -2.0F, 8, 4, 4, var1);
      this.field_191220_d = (new ModelRenderer(this, 0, 22)).func_78787_b(var3, var4);
      this.field_191220_d.func_78793_a(-2.0F, 12.0F + var2, 0.0F);
      this.field_191220_d.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.field_191221_e = (new ModelRenderer(this, 0, 22)).func_78787_b(var3, var4);
      this.field_191221_e.field_78809_i = true;
      this.field_191221_e.func_78793_a(2.0F, 12.0F + var2, 0.0F);
      this.field_191221_e.func_78790_a(-2.0F, 0.0F, -2.0F, 4, 12, 4, var1);
      this.field_191223_g = (new ModelRenderer(this, 40, 46)).func_78787_b(var3, var4);
      this.field_191223_g.func_78790_a(-3.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.field_191223_g.func_78793_a(-5.0F, 2.0F + var2, 0.0F);
      this.field_191224_h = (new ModelRenderer(this, 40, 46)).func_78787_b(var3, var4);
      this.field_191224_h.field_78809_i = true;
      this.field_191224_h.func_78790_a(-1.0F, -2.0F, -2.0F, 4, 12, 4, var1);
      this.field_191224_h.func_78793_a(5.0F, 2.0F + var2, 0.0F);
   }

   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.func_78087_a(var2, var3, var4, var5, var6, var7, var1);
      this.field_191217_a.func_78785_a(var7);
      this.field_191218_b.func_78785_a(var7);
      this.field_191220_d.func_78785_a(var7);
      this.field_191221_e.func_78785_a(var7);
      AbstractIllager var8 = (AbstractIllager)var1;
      if (var8.func_193077_p() == AbstractIllager.IllagerArmPose.CROSSED) {
         this.field_191219_c.func_78785_a(var7);
      } else {
         this.field_191223_g.func_78785_a(var7);
         this.field_191224_h.func_78785_a(var7);
      }

   }

   public void func_78087_a(float var1, float var2, float var3, float var4, float var5, float var6, Entity var7) {
      this.field_191217_a.field_78796_g = var4 * 0.017453292F;
      this.field_191217_a.field_78795_f = var5 * 0.017453292F;
      this.field_191219_c.field_78797_d = 3.0F;
      this.field_191219_c.field_78798_e = -1.0F;
      this.field_191219_c.field_78795_f = -0.75F;
      this.field_191220_d.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F) * 1.4F * var2 * 0.5F;
      this.field_191221_e.field_78795_f = MathHelper.func_76134_b(var1 * 0.6662F + 3.1415927F) * 1.4F * var2 * 0.5F;
      this.field_191220_d.field_78796_g = 0.0F;
      this.field_191221_e.field_78796_g = 0.0F;
      AbstractIllager.IllagerArmPose var8 = ((AbstractIllager)var7).func_193077_p();
      if (var8 == AbstractIllager.IllagerArmPose.ATTACKING) {
         float var9 = MathHelper.func_76126_a(this.field_78095_p * 3.1415927F);
         float var10 = MathHelper.func_76126_a((1.0F - (1.0F - this.field_78095_p) * (1.0F - this.field_78095_p)) * 3.1415927F);
         this.field_191223_g.field_78808_h = 0.0F;
         this.field_191224_h.field_78808_h = 0.0F;
         this.field_191223_g.field_78796_g = 0.15707964F;
         this.field_191224_h.field_78796_g = -0.15707964F;
         ModelRenderer var10000;
         if (((EntityLivingBase)var7).func_184591_cq() == EnumHandSide.RIGHT) {
            this.field_191223_g.field_78795_f = -1.8849558F + MathHelper.func_76134_b(var3 * 0.09F) * 0.15F;
            this.field_191224_h.field_78795_f = -0.0F + MathHelper.func_76134_b(var3 * 0.19F) * 0.5F;
            var10000 = this.field_191223_g;
            var10000.field_78795_f += var9 * 2.2F - var10 * 0.4F;
            var10000 = this.field_191224_h;
            var10000.field_78795_f += var9 * 1.2F - var10 * 0.4F;
         } else {
            this.field_191223_g.field_78795_f = -0.0F + MathHelper.func_76134_b(var3 * 0.19F) * 0.5F;
            this.field_191224_h.field_78795_f = -1.8849558F + MathHelper.func_76134_b(var3 * 0.09F) * 0.15F;
            var10000 = this.field_191223_g;
            var10000.field_78795_f += var9 * 1.2F - var10 * 0.4F;
            var10000 = this.field_191224_h;
            var10000.field_78795_f += var9 * 2.2F - var10 * 0.4F;
         }

         var10000 = this.field_191223_g;
         var10000.field_78808_h += MathHelper.func_76134_b(var3 * 0.09F) * 0.05F + 0.05F;
         var10000 = this.field_191224_h;
         var10000.field_78808_h -= MathHelper.func_76134_b(var3 * 0.09F) * 0.05F + 0.05F;
         var10000 = this.field_191223_g;
         var10000.field_78795_f += MathHelper.func_76126_a(var3 * 0.067F) * 0.05F;
         var10000 = this.field_191224_h;
         var10000.field_78795_f -= MathHelper.func_76126_a(var3 * 0.067F) * 0.05F;
      } else if (var8 == AbstractIllager.IllagerArmPose.SPELLCASTING) {
         this.field_191223_g.field_78798_e = 0.0F;
         this.field_191223_g.field_78800_c = -5.0F;
         this.field_191224_h.field_78798_e = 0.0F;
         this.field_191224_h.field_78800_c = 5.0F;
         this.field_191223_g.field_78795_f = MathHelper.func_76134_b(var3 * 0.6662F) * 0.25F;
         this.field_191224_h.field_78795_f = MathHelper.func_76134_b(var3 * 0.6662F) * 0.25F;
         this.field_191223_g.field_78808_h = 2.3561945F;
         this.field_191224_h.field_78808_h = -2.3561945F;
         this.field_191223_g.field_78796_g = 0.0F;
         this.field_191224_h.field_78796_g = 0.0F;
      } else if (var8 == AbstractIllager.IllagerArmPose.BOW_AND_ARROW) {
         this.field_191223_g.field_78796_g = -0.1F + this.field_191217_a.field_78796_g;
         this.field_191223_g.field_78795_f = -1.5707964F + this.field_191217_a.field_78795_f;
         this.field_191224_h.field_78795_f = -0.9424779F + this.field_191217_a.field_78795_f;
         this.field_191224_h.field_78796_g = this.field_191217_a.field_78796_g - 0.4F;
         this.field_191224_h.field_78808_h = 1.5707964F;
      }

   }

   public ModelRenderer func_191216_a(EnumHandSide var1) {
      return var1 == EnumHandSide.LEFT ? this.field_191224_h : this.field_191223_g;
   }

   public ModelRenderer func_205062_a() {
      return this.field_193775_b;
   }
}
