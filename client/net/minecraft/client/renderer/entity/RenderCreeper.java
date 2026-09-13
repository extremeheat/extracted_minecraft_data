package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelCreeper;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCreeper extends RenderLiving {
   private static final ResourceLocation field_110831_a = new ResourceLocation("textures/entity/creeper/creeper_armor.png");
   private static final ResourceLocation field_110830_f = new ResourceLocation("textures/entity/creeper/creeper.png");
   private ModelBase field_77064_a = new ModelCreeper(2.0F);

   public RenderCreeper() {
      super(new ModelCreeper(), 0.5F);
   }

   protected void func_77041_b(EntityCreeper var1, float var2) {
      float var3 = var1.func_70831_j(var2);
      float var4 = 1.0F + MathHelper.func_76126_a(var3 * 100.0F) * var3 * 0.01F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      var3 *= var3;
      var3 *= var3;
      float var5 = (1.0F + var3 * 0.4F) * var4;
      float var6 = (1.0F + var3 * 0.1F) / var4;
      GL11.glScalef(var5, var6, var5);
   }

   protected int func_77030_a(EntityCreeper var1, float var2, float var3) {
      float var4 = var1.func_70831_j(var3);
      if ((int)(var4 * 10.0F) % 2 == 0) {
         return 0;
      } else {
         int var5 = (int)(var4 * 0.2F * 255.0F);
         if (var5 < 0) {
            var5 = 0;
         }

         if (var5 > 255) {
            var5 = 255;
         }

         short var6 = 255;
         short var7 = 255;
         short var8 = 255;
         return var5 << 24 | var6 << 16 | var7 << 8 | var8;
      }
   }

   protected int func_77032_a(EntityCreeper var1, int var2, float var3) {
      if (var1.func_70830_n()) {
         if (var1.func_82150_aj()) {
            GL11.glDepthMask(false);
         } else {
            GL11.glDepthMask(true);
         }

         if (var2 == 1) {
            float var4 = (float)var1.field_70173_aa + var3;
            this.func_110776_a(field_110831_a);
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            float var5 = var4 * 0.01F;
            float var6 = var4 * 0.01F;
            GL11.glTranslatef(var5, var6, 0.0F);
            this.func_77042_a(this.field_77064_a);
            GL11.glMatrixMode(5888);
            GL11.glEnable(3042);
            float var7 = 0.5F;
            GL11.glColor4f(var7, var7, var7, 1.0F);
            GL11.glDisable(2896);
            GL11.glBlendFunc(1, 1);
            return 1;
         }

         if (var2 == 2) {
            GL11.glMatrixMode(5890);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glEnable(2896);
            GL11.glDisable(3042);
         }
      }

      return -1;
   }

   protected int func_77035_b(EntityCreeper var1, int var2, float var3) {
      return -1;
   }

   protected ResourceLocation func_110775_a(EntityCreeper var1) {
      return field_110830_f;
   }
}
