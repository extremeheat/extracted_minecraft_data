package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.layers.LayerMooshroomMushroom;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.util.ResourceLocation;

public class RenderMooshroom extends RenderLiving<EntityMooshroom> {
   private static final ResourceLocation field_110880_a = new ResourceLocation("textures/entity/cow/mooshroom.png");

   public RenderMooshroom(RenderManager var1, ModelBase var2, float var3) {
      super(var1, var2, var3);
      this.func_177094_a(new LayerMooshroomMushroom(this));
   }

   protected ResourceLocation func_110775_a(EntityMooshroom var1) {
      return field_110880_a;
   }
}
