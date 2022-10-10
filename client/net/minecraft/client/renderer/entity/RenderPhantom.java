package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerPhantomEyes;
import net.minecraft.client.renderer.entity.model.ModelPhantom;
import net.minecraft.entity.monster.EntityPhantom;
import net.minecraft.util.ResourceLocation;

public class RenderPhantom extends RenderLiving<EntityPhantom> {
   private static final ResourceLocation field_203090_j = new ResourceLocation("textures/entity/phantom.png");

   public RenderPhantom(RenderManager var1) {
      super(var1, new ModelPhantom(), 0.75F);
      this.func_177094_a(new LayerPhantomEyes(this));
   }

   protected ResourceLocation func_110775_a(EntityPhantom var1) {
      return field_203090_j;
   }

   protected void func_77041_b(EntityPhantom var1, float var2) {
      int var3 = var1.func_203032_dq();
      float var4 = 1.0F + 0.15F * (float)var3;
      GlStateManager.func_179152_a(var4, var4, var4);
      GlStateManager.func_179109_b(0.0F, 1.3125F, 0.1875F);
   }

   protected void func_77043_a(EntityPhantom var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
      GlStateManager.func_179114_b(var1.field_70125_A, 1.0F, 0.0F, 0.0F);
   }
}
