package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSnowmanHead;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelSnowMan;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.util.ResourceLocation;

public class RenderSnowMan extends RenderLiving<EntitySnowman> {
   private static final ResourceLocation field_110895_a = new ResourceLocation("textures/entity/snow_golem.png");

   public RenderSnowMan(RenderManager var1) {
      super(var1, new ModelSnowMan(), 0.5F);
      this.func_177094_a(new LayerSnowmanHead(this));
   }

   protected ResourceLocation func_110775_a(EntitySnowman var1) {
      return field_110895_a;
   }

   public ModelSnowMan func_177087_b() {
      return (ModelSnowMan)super.func_177087_b();
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
