package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Bee;

public class BeeRenderer extends MobRenderer<Bee, BeeModel<Bee>> {
   private static final ResourceLocation ANGRY_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry.png");
   private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry_nectar.png");
   private static final ResourceLocation BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee.png");
   private static final ResourceLocation NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_nectar.png");

   public BeeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BeeModel(var1.bakeLayer(ModelLayers.BEE)), 0.4F);
   }

   public ResourceLocation getTextureLocation(Bee var1) {
      if (var1.isAngry()) {
         return var1.hasNectar() ? ANGRY_NECTAR_BEE_TEXTURE : ANGRY_BEE_TEXTURE;
      } else {
         return var1.hasNectar() ? NECTAR_BEE_TEXTURE : BEE_TEXTURE;
      }
   }
}
