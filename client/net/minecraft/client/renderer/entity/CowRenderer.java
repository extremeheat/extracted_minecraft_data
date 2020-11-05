package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CowModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

public class CowRenderer extends MobRenderer<Cow, CowModel<Cow>> {
   private static final ResourceLocation COW_LOCATION = new ResourceLocation("textures/entity/cow/cow.png");

   public CowRenderer(EntityRenderDispatcher var1) {
      super(var1, new CowModel(), 0.7F);
   }

   public ResourceLocation getTextureLocation(Cow var1) {
      return COW_LOCATION;
   }
}
