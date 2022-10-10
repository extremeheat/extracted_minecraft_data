package net.minecraft.client.renderer.entity;

import javax.annotation.Nullable;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

public class RenderEntity extends Render<Entity> {
   public RenderEntity(RenderManager var1) {
      super(var1);
   }

   public void func_76986_a(Entity var1, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.func_179094_E();
      func_76978_a(var1.func_174813_aQ(), var2 - var1.field_70142_S, var4 - var1.field_70137_T, var6 - var1.field_70136_U);
      GlStateManager.func_179121_F();
      super.func_76986_a(var1, var2, var4, var6, var8, var9);
   }

   @Nullable
   protected ResourceLocation func_110775_a(Entity var1) {
      return null;
   }
}
