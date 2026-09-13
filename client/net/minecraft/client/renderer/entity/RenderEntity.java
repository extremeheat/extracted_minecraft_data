package net.minecraft.client.renderer.entity;

import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderEntity extends Render {
   public RenderEntity() {
      super();
   }

   @Override
   public void func_76986_a(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GL11.glPushMatrix();
      func_76978_a(var1.field_70121_D, var2 - var1.field_70142_S, var4 - var1.field_70137_T, var6 - var1.field_70136_U);
      GL11.glPopMatrix();
   }

   @Override
   protected ResourceLocation func_110775_a(Entity var1) {
      return null;
   }
}
