package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerDolphinCarriedItem;
import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.util.ResourceLocation;

public class RenderDolphin extends RenderLiving<EntityDolphin> {
   private static final ResourceLocation field_205128_a = new ResourceLocation("textures/entity/dolphin.png");

   public RenderDolphin(RenderManager var1) {
      super(var1, new DolphinModel(), 0.7F);
      this.func_177094_a(new LayerDolphinCarriedItem(this));
   }

   protected ResourceLocation func_110775_a(EntityDolphin var1) {
      return field_205128_a;
   }

   protected void func_77041_b(EntityDolphin var1, float var2) {
      float var3 = 1.0F;
      GlStateManager.func_179152_a(1.0F, 1.0F, 1.0F);
   }

   protected void func_77043_a(EntityDolphin var1, float var2, float var3, float var4) {
      super.func_77043_a(var1, var2, var3, var4);
   }
}
