package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CowModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Cow;

public class CowRenderer extends AgeableMobRenderer<Cow, LivingEntityRenderState, CowModel> {
   private static final ResourceLocation COW_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/cow/cow.png");

   public CowRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CowModel(var1.bakeLayer(ModelLayers.COW)), new CowModel(var1.bakeLayer(ModelLayers.COW_BABY)), 0.7F);
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return COW_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   public void extractRenderState(Cow var1, LivingEntityRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
