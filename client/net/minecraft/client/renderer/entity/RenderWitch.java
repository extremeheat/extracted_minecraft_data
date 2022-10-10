package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerHeldItemWitch;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelWitch;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.ResourceLocation;

public class RenderWitch extends RenderLiving<EntityWitch> {
   private static final ResourceLocation field_110910_a = new ResourceLocation("textures/entity/witch.png");

   public RenderWitch(RenderManager var1) {
      super(var1, new ModelWitch(0.0F), 0.5F);
      this.func_177094_a(new LayerHeldItemWitch(this));
   }

   public ModelWitch func_177087_b() {
      return (ModelWitch)super.func_177087_b();
   }

   public void func_76986_a(EntityWitch var1, double var2, double var4, double var6, float var8, float var9) {
      ((ModelWitch)this.field_77045_g).func_205074_a(!var1.func_184614_ca().func_190926_b());
      super.func_76986_a((EntityLiving)var1, var2, var4, var6, var8, var9);
   }

   protected ResourceLocation func_110775_a(EntityWitch var1) {
      return field_110910_a;
   }

   protected void func_77041_b(EntityWitch var1, float var2) {
      float var3 = 0.9375F;
      GlStateManager.func_179152_a(0.9375F, 0.9375F, 0.9375F);
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
