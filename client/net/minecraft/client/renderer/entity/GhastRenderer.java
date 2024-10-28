package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.GhastModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.GhastRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Ghast;

public class GhastRenderer extends MobRenderer<Ghast, GhastRenderState, GhastModel> {
   private static final ResourceLocation GHAST_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/ghast.png");
   private static final ResourceLocation GHAST_SHOOTING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/ghast/ghast_shooting.png");

   public GhastRenderer(EntityRendererProvider.Context var1) {
      super(var1, new GhastModel(var1.bakeLayer(ModelLayers.GHAST)), 1.5F);
   }

   public ResourceLocation getTextureLocation(GhastRenderState var1) {
      return var1.isCharging ? GHAST_SHOOTING_LOCATION : GHAST_LOCATION;
   }

   public GhastRenderState createRenderState() {
      return new GhastRenderState();
   }

   public void extractRenderState(Ghast var1, GhastRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isCharging = var1.isCharging();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((GhastRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
