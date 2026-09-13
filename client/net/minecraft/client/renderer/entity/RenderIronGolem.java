package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelIronGolem;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderIronGolem extends RenderLiving {
   private static final ResourceLocation field_110899_a = new ResourceLocation("textures/entity/iron_golem.png");
   private final ModelIronGolem field_77050_a = (ModelIronGolem)this.field_77045_g;

   public RenderIronGolem() {
      super(new ModelIronGolem(), 0.5F);
   }

   public void func_76986_a(EntityIronGolem var1, double var2, double var4, double var6, float var8, float var9) {
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityIronGolem var1) {
      return field_110899_a;
   }

   protected void func_77043_a(EntityIronGolem var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
      if (!((double)var1.field_70721_aZ < 0.01)) {
         float var5 = 13.0F;
         float var6 = var1.field_70754_ba - var1.field_70721_aZ * (1.0F - var4) + 6.0F;
         float var7 = (Math.abs(var6 % var5 - var5 * 0.5F) - var5 * 0.25F) / (var5 * 0.25F);
         GL11.glRotatef(6.5F * var7, 0.0F, 0.0F, 1.0F);
      }
   }

   protected void func_77029_c(EntityIronGolem var1, float var2) {
      super.func_77029_c(var1, var2);
      if (var1.func_70853_p() != 0) {
         GL11.glEnable(32826);
         GL11.glPushMatrix();
         GL11.glRotatef(5.0F + 180.0F * this.field_77050_a.field_78177_c.field_78795_f / 3.1415927F, 1.0F, 0.0F, 0.0F);
         GL11.glTranslatef(-0.6875F, 1.25F, -0.9375F);
         GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         float var3 = 0.8F;
         GL11.glScalef(var3, -var3, var3);
         int var4 = var1.func_70070_b(var2);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_110776_a(TextureMap.field_110575_b);
         this.field_147909_c.func_147800_a(Blocks.field_150328_O, 0, 1.0F);
         GL11.glPopMatrix();
         GL11.glDisable(32826);
      }
   }
}
