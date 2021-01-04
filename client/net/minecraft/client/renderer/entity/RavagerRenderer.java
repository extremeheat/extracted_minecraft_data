package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.RavagerModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ravager;

public class RavagerRenderer extends MobRenderer<Ravager, RavagerModel> {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/entity/illager/ravager.png");

   public RavagerRenderer(EntityRenderDispatcher var1) {
      super(var1, new RavagerModel(), 1.1F);
   }

   protected ResourceLocation getTextureLocation(Ravager var1) {
      return TEXTURE_LOCATION;
   }
}
