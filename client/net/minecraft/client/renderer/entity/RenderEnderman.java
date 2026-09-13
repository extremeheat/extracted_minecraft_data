package net.minecraft.client.renderer.entity;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.model.ModelEnderman;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEnderman extends RenderLiving {
   private static final ResourceLocation field_110840_a = new ResourceLocation("textures/entity/enderman/enderman_eyes.png");
   private static final ResourceLocation field_110839_f = new ResourceLocation("textures/entity/enderman/enderman.png");
   private ModelEnderman field_77078_a;
   private Random field_77077_b = new Random();

   public RenderEnderman() {
      super(new ModelEnderman(), 0.5F);
      this.field_77078_a = (ModelEnderman)super.field_77045_g;
      this.func_77042_a(this.field_77078_a);
   }

   public void func_76986_a(EntityEnderman var1, double var2, double var4, double var6, float var8, float var9) {
      this.field_77078_a.field_78126_a = var1.func_146080_bZ().func_149688_o() != Material.field_151579_a;
      this.field_77078_a.field_78125_b = var1.func_70823_r();
      if (var1.func_70823_r()) {
         double var10 = 0.02;
         var2 += this.field_77077_b.nextGaussian() * var10;
         var6 += this.field_77077_b.nextGaussian() * var10;
      }

      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityEnderman var1) {
      return field_110839_f;
   }

   protected void func_77029_c(EntityEnderman var1, float var2) {
      super.func_77029_c(var1, var2);
      if (var1.func_146080_bZ().func_149688_o() != Material.field_151579_a) {
         GL11.glEnable(32826);
         GL11.glPushMatrix();
         float var3 = 0.5F;
         GL11.glTranslatef(0.0F, 0.6875F, -0.75F);
         var3 *= 1.0F;
         GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
         GL11.glScalef(-var3, -var3, var3);
         int var4 = var1.func_70070_b(var2);
         int var5 = var4 % 65536;
         int var6 = var4 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var5 / 1.0F, (float)var6 / 1.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_110776_a(TextureMap.field_110575_b);
         this.field_147909_c.func_147800_a(var1.func_146080_bZ(), var1.func_70824_q(), 1.0F);
         GL11.glPopMatrix();
         GL11.glDisable(32826);
      }
   }

   protected int func_77032_a(EntityEnderman var1, int var2, float var3) {
      if (var2 != 0) {
         return -1;
      } else {
         this.func_110776_a(field_110840_a);
         float var4 = 1.0F;
         GL11.glEnable(3042);
         GL11.glDisable(3008);
         GL11.glBlendFunc(1, 1);
         GL11.glDisable(2896);
         if (var1.func_82150_aj()) {
            GL11.glDepthMask(false);
         } else {
            GL11.glDepthMask(true);
         }

         char var5 = '\uf0f0';
         int var6 = var5 % 65536;
         int var7 = var5 / 65536;
         OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, (float)var6 / 1.0F, (float)var7 / 1.0F);
         GL11.glEnable(2896);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, var4);
         return 1;
      }
   }
}
