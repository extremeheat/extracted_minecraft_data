package net.minecraft.client.model;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import org.lwjgl.opengl.GL11;

public class ModelDragon extends ModelBase {
   private ModelRenderer field_78221_a;
   private ModelRenderer field_78219_b;
   private ModelRenderer field_78220_c;
   private ModelRenderer field_78217_d;
   private ModelRenderer field_78218_e;
   private ModelRenderer field_78215_f;
   private ModelRenderer field_78216_g;
   private ModelRenderer field_78226_h;
   private ModelRenderer field_78227_i;
   private ModelRenderer field_78224_j;
   private ModelRenderer field_78225_k;
   private ModelRenderer field_78222_l;
   private float field_78223_m;

   public ModelDragon(float var1) {
      super();
      this.field_78090_t = 256;
      this.field_78089_u = 256;
      this.func_78085_a("body.body", 0, 0);
      this.func_78085_a("wing.skin", -56, 88);
      this.func_78085_a("wingtip.skin", -56, 144);
      this.func_78085_a("rearleg.main", 0, 0);
      this.func_78085_a("rearfoot.main", 112, 0);
      this.func_78085_a("rearlegtip.main", 196, 0);
      this.func_78085_a("head.upperhead", 112, 30);
      this.func_78085_a("wing.bone", 112, 88);
      this.func_78085_a("head.upperlip", 176, 44);
      this.func_78085_a("jaw.jaw", 176, 65);
      this.func_78085_a("frontleg.main", 112, 104);
      this.func_78085_a("wingtip.bone", 112, 136);
      this.func_78085_a("frontfoot.main", 144, 104);
      this.func_78085_a("neck.box", 192, 104);
      this.func_78085_a("frontlegtip.main", 226, 138);
      this.func_78085_a("body.scale", 220, 53);
      this.func_78085_a("head.scale", 0, 0);
      this.func_78085_a("neck.scale", 48, 0);
      this.func_78085_a("head.nostril", 112, 0);
      float var2 = -16.0F;
      this.field_78221_a = new ModelRenderer(this, "head");
      this.field_78221_a.func_78786_a("upperlip", -6.0F, -1.0F, -8.0F + var2, 12, 5, 16);
      this.field_78221_a.func_78786_a("upperhead", -8.0F, -8.0F, 6.0F + var2, 16, 16, 16);
      this.field_78221_a.field_78809_i = true;
      this.field_78221_a.func_78786_a("scale", -5.0F, -12.0F, 12.0F + var2, 2, 4, 6);
      this.field_78221_a.func_78786_a("nostril", -5.0F, -3.0F, -6.0F + var2, 2, 2, 4);
      this.field_78221_a.field_78809_i = false;
      this.field_78221_a.func_78786_a("scale", 3.0F, -12.0F, 12.0F + var2, 2, 4, 6);
      this.field_78221_a.func_78786_a("nostril", 3.0F, -3.0F, -6.0F + var2, 2, 2, 4);
      this.field_78220_c = new ModelRenderer(this, "jaw");
      this.field_78220_c.func_78793_a(0.0F, 4.0F, 8.0F + var2);
      this.field_78220_c.func_78786_a("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16);
      this.field_78221_a.func_78792_a(this.field_78220_c);
      this.field_78219_b = new ModelRenderer(this, "neck");
      this.field_78219_b.func_78786_a("box", -5.0F, -5.0F, -5.0F, 10, 10, 10);
      this.field_78219_b.func_78786_a("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6);
      this.field_78217_d = new ModelRenderer(this, "body");
      this.field_78217_d.func_78793_a(0.0F, 4.0F, 8.0F);
      this.field_78217_d.func_78786_a("body", -12.0F, 0.0F, -16.0F, 24, 24, 64);
      this.field_78217_d.func_78786_a("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12);
      this.field_78217_d.func_78786_a("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12);
      this.field_78217_d.func_78786_a("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12);
      this.field_78225_k = new ModelRenderer(this, "wing");
      this.field_78225_k.func_78793_a(-12.0F, 5.0F, 2.0F);
      this.field_78225_k.func_78786_a("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8);
      this.field_78225_k.func_78786_a("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.field_78222_l = new ModelRenderer(this, "wingtip");
      this.field_78222_l.func_78793_a(-56.0F, 0.0F, 0.0F);
      this.field_78222_l.func_78786_a("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4);
      this.field_78222_l.func_78786_a("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56);
      this.field_78225_k.func_78792_a(this.field_78222_l);
      this.field_78215_f = new ModelRenderer(this, "frontleg");
      this.field_78215_f.func_78793_a(-12.0F, 20.0F, 2.0F);
      this.field_78215_f.func_78786_a("main", -4.0F, -4.0F, -4.0F, 8, 24, 8);
      this.field_78226_h = new ModelRenderer(this, "frontlegtip");
      this.field_78226_h.func_78793_a(0.0F, 20.0F, -1.0F);
      this.field_78226_h.func_78786_a("main", -3.0F, -1.0F, -3.0F, 6, 24, 6);
      this.field_78215_f.func_78792_a(this.field_78226_h);
      this.field_78224_j = new ModelRenderer(this, "frontfoot");
      this.field_78224_j.func_78793_a(0.0F, 23.0F, 0.0F);
      this.field_78224_j.func_78786_a("main", -4.0F, 0.0F, -12.0F, 8, 4, 16);
      this.field_78226_h.func_78792_a(this.field_78224_j);
      this.field_78218_e = new ModelRenderer(this, "rearleg");
      this.field_78218_e.func_78793_a(-16.0F, 16.0F, 42.0F);
      this.field_78218_e.func_78786_a("main", -8.0F, -4.0F, -8.0F, 16, 32, 16);
      this.field_78216_g = new ModelRenderer(this, "rearlegtip");
      this.field_78216_g.func_78793_a(0.0F, 32.0F, -4.0F);
      this.field_78216_g.func_78786_a("main", -6.0F, -2.0F, 0.0F, 12, 32, 12);
      this.field_78218_e.func_78792_a(this.field_78216_g);
      this.field_78227_i = new ModelRenderer(this, "rearfoot");
      this.field_78227_i.func_78793_a(0.0F, 31.0F, 4.0F);
      this.field_78227_i.func_78786_a("main", -9.0F, 0.0F, -20.0F, 18, 6, 24);
      this.field_78216_g.func_78792_a(this.field_78227_i);
   }

   @Override
   public void func_78086_a(EntityLivingBase var1, float var2, float var3, float var4) {
      this.field_78223_m = var4;
   }

   @Override
   public void func_78088_a(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      GL11.glPushMatrix();
      EntityDragon var8 = (EntityDragon)var1;
      float var9 = var8.field_70991_bC + (var8.field_70988_bD - var8.field_70991_bC) * this.field_78223_m;
      this.field_78220_c.field_78795_f = (float)(Math.sin((double)(var9 * 3.1415927F * 2.0F)) + 1.0) * 0.2F;
      float var10 = (float)(Math.sin((double)(var9 * 3.1415927F * 2.0F - 1.0F)) + 1.0);
      var10 = (var10 * var10 * 1.0F + var10 * 2.0F) * 0.05F;
      GL11.glTranslatef(0.0F, var10 - 2.0F, -3.0F);
      GL11.glRotatef(var10 * 2.0F, 1.0F, 0.0F, 0.0F);
      float var11 = -30.0F;
      float var13 = 0.0F;
      float var14 = 1.5F;
      double[] var15 = var8.func_70974_a(6, this.field_78223_m);
      float var16 = this.func_78214_a(var8.func_70974_a(5, this.field_78223_m)[0] - var8.func_70974_a(10, this.field_78223_m)[0]);
      float var17 = this.func_78214_a(var8.func_70974_a(5, this.field_78223_m)[0] + (double)(var16 / 2.0F));
      var11 += 2.0F;
      float var18 = var9 * 3.1415927F * 2.0F;
      var11 = 20.0F;
      float var12 = -12.0F;

      for(int var19 = 0; var19 < 5; ++var19) {
         double[] var20 = var8.func_70974_a(5 - var19, this.field_78223_m);
         float var21 = (float)Math.cos((double)((float)var19 * 0.45F + var18)) * 0.15F;
         this.field_78219_b.field_78796_g = this.func_78214_a(var20[0] - var15[0]) * 3.1415927F / 180.0F * var14;
         this.field_78219_b.field_78795_f = var21 + (float)(var20[1] - var15[1]) * 3.1415927F / 180.0F * var14 * 5.0F;
         this.field_78219_b.field_78808_h = -this.func_78214_a(var20[0] - (double)var17) * 3.1415927F / 180.0F * var14;
         this.field_78219_b.field_78797_d = var11;
         this.field_78219_b.field_78798_e = var12;
         this.field_78219_b.field_78800_c = var13;
         var11 = (float)((double)var11 + Math.sin((double)this.field_78219_b.field_78795_f) * 10.0);
         var12 = (float)((double)var12 - Math.cos((double)this.field_78219_b.field_78796_g) * Math.cos((double)this.field_78219_b.field_78795_f) * 10.0);
         var13 = (float)((double)var13 - Math.sin((double)this.field_78219_b.field_78796_g) * Math.cos((double)this.field_78219_b.field_78795_f) * 10.0);
         this.field_78219_b.func_78785_a(var7);
      }

      this.field_78221_a.field_78797_d = var11;
      this.field_78221_a.field_78798_e = var12;
      this.field_78221_a.field_78800_c = var13;
      double[] var30 = var8.func_70974_a(0, this.field_78223_m);
      this.field_78221_a.field_78796_g = this.func_78214_a(var30[0] - var15[0]) * 3.1415927F / 180.0F * 1.0F;
      this.field_78221_a.field_78808_h = -this.func_78214_a(var30[0] - (double)var17) * 3.1415927F / 180.0F * 1.0F;
      this.field_78221_a.func_78785_a(var7);
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 1.0F, 0.0F);
      GL11.glRotatef(-var16 * var14 * 1.0F, 0.0F, 0.0F, 1.0F);
      GL11.glTranslatef(0.0F, -1.0F, 0.0F);
      this.field_78217_d.field_78808_h = 0.0F;
      this.field_78217_d.func_78785_a(var7);

      for(int var32 = 0; var32 < 2; ++var32) {
         GL11.glEnable(2884);
         float var34 = var9 * 3.1415927F * 2.0F;
         this.field_78225_k.field_78795_f = 0.125F - (float)Math.cos((double)var34) * 0.2F;
         this.field_78225_k.field_78796_g = 0.25F;
         this.field_78225_k.field_78808_h = (float)(Math.sin((double)var34) + 0.125) * 0.8F;
         this.field_78222_l.field_78808_h = -((float)(Math.sin((double)(var34 + 2.0F)) + 0.5)) * 0.75F;
         this.field_78218_e.field_78795_f = 1.0F + var10 * 0.1F;
         this.field_78216_g.field_78795_f = 0.5F + var10 * 0.1F;
         this.field_78227_i.field_78795_f = 0.75F + var10 * 0.1F;
         this.field_78215_f.field_78795_f = 1.3F + var10 * 0.1F;
         this.field_78226_h.field_78795_f = -0.5F - var10 * 0.1F;
         this.field_78224_j.field_78795_f = 0.75F + var10 * 0.1F;
         this.field_78225_k.func_78785_a(var7);
         this.field_78215_f.func_78785_a(var7);
         this.field_78218_e.func_78785_a(var7);
         GL11.glScalef(-1.0F, 1.0F, 1.0F);
         if (var32 == 0) {
            GL11.glCullFace(1028);
         }
      }

      GL11.glPopMatrix();
      GL11.glCullFace(1029);
      GL11.glDisable(2884);
      float var33 = -((float)Math.sin((double)(var9 * 3.1415927F * 2.0F))) * 0.0F;
      var18 = var9 * 3.1415927F * 2.0F;
      var11 = 10.0F;
      var12 = 60.0F;
      var13 = 0.0F;
      var15 = var8.func_70974_a(11, this.field_78223_m);

      for(int var35 = 0; var35 < 12; ++var35) {
         var30 = var8.func_70974_a(12 + var35, this.field_78223_m);
         var33 = (float)((double)var33 + Math.sin((double)((float)var35 * 0.45F + var18)) * 0.05000000074505806);
         this.field_78219_b.field_78796_g = (this.func_78214_a(var30[0] - var15[0]) * var14 + 180.0F) * 3.1415927F / 180.0F;
         this.field_78219_b.field_78795_f = var33 + (float)(var30[1] - var15[1]) * 3.1415927F / 180.0F * var14 * 5.0F;
         this.field_78219_b.field_78808_h = this.func_78214_a(var30[0] - (double)var17) * 3.1415927F / 180.0F * var14;
         this.field_78219_b.field_78797_d = var11;
         this.field_78219_b.field_78798_e = var12;
         this.field_78219_b.field_78800_c = var13;
         var11 = (float)((double)var11 + Math.sin((double)this.field_78219_b.field_78795_f) * 10.0);
         var12 = (float)((double)var12 - Math.cos((double)this.field_78219_b.field_78796_g) * Math.cos((double)this.field_78219_b.field_78795_f) * 10.0);
         var13 = (float)((double)var13 - Math.sin((double)this.field_78219_b.field_78796_g) * Math.cos((double)this.field_78219_b.field_78795_f) * 10.0);
         this.field_78219_b.func_78785_a(var7);
      }

      GL11.glPopMatrix();
   }

   private float func_78214_a(double var1) {
      while(var1 >= 180.0) {
         var1 -= 360.0;
      }

      while(var1 < -180.0) {
         var1 += 360.0;
      }

      return (float)var1;
   }
}
