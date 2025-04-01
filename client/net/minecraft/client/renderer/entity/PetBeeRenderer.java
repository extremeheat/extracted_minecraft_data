package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BeeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.BeeRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.pets.PetBee;

public class PetBeeRenderer extends AgeableMobRenderer<PetBee, BeeRenderState, BeeModel> {
   private static final ResourceLocation ANGRY_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry.png");
   private static final ResourceLocation ANGRY_NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_angry_nectar.png");
   private static final ResourceLocation BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee.png");
   private static final ResourceLocation NECTAR_BEE_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/bee/bee_nectar.png");

   public PetBeeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BeeModel(var1.bakeLayer(ModelLayers.BEE)), new BeeModel(var1.bakeLayer(ModelLayers.BEE_BABY)), 0.4F);
   }

   public ResourceLocation getTextureLocation(BeeRenderState var1) {
      return BEE_TEXTURE;
   }

   public BeeRenderState createRenderState() {
      return new BeeRenderState();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((BeeRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
