package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelCow;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;

public class RenderMooshroom extends RenderLiving<EntityMooshroom> {
   private static final ResourceLocation field_110880_a = new ResourceLocation("textures/entity/cow/mooshroom.png");

   public RenderMooshroom(RenderManager var1) {
      super(var1, new ModelCow(), 0.7F);
      this.func_177094_a(new LayerMooshroomMushroom(this));
   }

   public ModelCow func_177087_b() {
      return (ModelCow)super.func_177087_b();
   }

   protected ResourceLocation func_110775_a(EntityMooshroom var1) {
      return field_110880_a;
   }

   // $FF: synthetic method
   public ModelBase func_177087_b() {
      return this.func_177087_b();
   }
}
