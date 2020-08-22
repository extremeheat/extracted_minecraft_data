package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.OcelotModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Ocelot;

public class OcelotRenderer extends MobRenderer {
   private static final ResourceLocation CAT_OCELOT_LOCATION = new ResourceLocation("textures/entity/cat/ocelot.png");

   public OcelotRenderer(EntityRenderDispatcher var1) {
      super(var1, new OcelotModel(0.0F), 0.4F);
   }

   public ResourceLocation getTextureLocation(Ocelot var1) {
      return CAT_OCELOT_LOCATION;
   }
}
