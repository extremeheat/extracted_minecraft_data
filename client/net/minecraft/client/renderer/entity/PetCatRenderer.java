package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.CatModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CatCollarLayer;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.pets.PetCat;

public class PetCatRenderer extends AgeableMobRenderer<PetCat, CatRenderState, CatModel> {
   public PetCatRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CatModel(var1.bakeLayer(ModelLayers.CAT)), new CatModel(var1.bakeLayer(ModelLayers.CAT_BABY)), 0.4F);
      this.addLayer(new CatCollarLayer(this, var1.getModelSet()));
   }

   public ResourceLocation getTextureLocation(CatRenderState var1) {
      return var1.texture;
   }

   public CatRenderState createRenderState() {
      return new CatRenderState();
   }

   public void extractRenderState(PetCat var1, CatRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.texture = ((CatVariant)var1.getVariant().value()).assetInfo().texturePath();
      var2.collarColor = var1.isTame() ? var1.getCollarColor() : null;
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CatRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
