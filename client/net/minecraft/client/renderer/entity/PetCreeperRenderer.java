package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.CreeperModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.CreeperPowerLayer;
import net.minecraft.client.renderer.entity.state.CreeperRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.pets.PetCreeper;

public class PetCreeperRenderer extends MobRenderer<PetCreeper, CreeperRenderState, CreeperModel> {
   private static final ResourceLocation CREEPER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/creeper/creeper.png");

   public PetCreeperRenderer(EntityRendererProvider.Context var1) {
      super(var1, new CreeperModel(var1.bakeLayer(ModelLayers.CREEPER)), 0.2F);
      this.addLayer(new CreeperPowerLayer(this, var1.getModelSet()));
   }

   protected void scale(CreeperRenderState var1, PoseStack var2) {
      var2.scale(0.5F, 0.5F, 0.5F);
   }

   public ResourceLocation getTextureLocation(CreeperRenderState var1) {
      return CREEPER_LOCATION;
   }

   public CreeperRenderState createRenderState() {
      return new CreeperRenderState();
   }

   public void extractRenderState(PetCreeper var1, CreeperRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((CreeperRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
