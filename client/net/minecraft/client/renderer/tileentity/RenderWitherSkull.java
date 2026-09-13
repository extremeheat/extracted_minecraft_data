package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderWitherSkull extends Render {
   private static final ResourceLocation field_110811_a = new ResourceLocation("textures/entity/wither/wither_invulnerable.png");
   private static final ResourceLocation field_110810_f = new ResourceLocation("textures/entity/wither/wither.png");
   private final ModelSkeletonHead field_82401_a = new ModelSkeletonHead();

   public RenderWitherSkull() {
      super();
   }

   private float func_82400_a(float var1, float var2, float var3) {
      float var4 = var2 - var1;

      while(var4 < -180.0F) {
         var4 += 360.0F;
      }

      while(var4 >= 180.0F) {
         var4 -= 360.0F;
      }

      return var1 + var3 * var4;
   }

   public void func_76986_a(EntityWitherSkull var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      GL11.glDisable(2884);
      float var10 = this.func_82400_a(var1.field_70126_B, var1.field_70177_z, var9);
      float var11 = var1.field_70127_C + (var1.field_70125_A - var1.field_70127_C) * var9;
      GL11.glTranslatef((float)var2, (float)var4, (float)var6);
      float var12 = 0.0625F;
      GL11.glEnable(32826);
      GL11.glScalef(-1.0F, -1.0F, 1.0F);
      GL11.glEnable(3008);
      this.func_110777_b(var1);
      this.field_82401_a.func_78088_a(var1, 0.0F, 0.0F, 0.0F, var10, var11, var12);
      GL11.glPopMatrix();
   }

   protected ResourceLocation func_110775_a(EntityWitherSkull var1) {
      return var1.func_82342_d() ? field_110811_a : field_110810_f;
   }
}
