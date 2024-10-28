package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.HoglinModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.hoglin.Hoglin;

public class HoglinRenderer extends MobRenderer<Hoglin, HoglinModel<Hoglin>> {
   private static final ResourceLocation HOGLIN_LOCATION = new ResourceLocation("textures/entity/hoglin/hoglin.png");

   public HoglinRenderer(EntityRendererProvider.Context var1) {
      super(var1, new HoglinModel(var1.bakeLayer(ModelLayers.HOGLIN)), 0.7F);
   }

   public ResourceLocation getTextureLocation(Hoglin var1) {
      return HOGLIN_LOCATION;
   }

   protected boolean isShaking(Hoglin var1) {
      return super.isShaking(var1) || var1.isConverting();
   }

   // $FF: synthetic method
   protected boolean isShaking(LivingEntity var1) {
      return this.isShaking((Hoglin)var1);
   }
}
