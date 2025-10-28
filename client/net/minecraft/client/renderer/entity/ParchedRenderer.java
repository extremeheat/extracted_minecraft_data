package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Parched;

public class ParchedRenderer extends AbstractSkeletonRenderer<Parched, SkeletonRenderState> {
   private static final ResourceLocation PARCHED_SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/parched.png");

   public ParchedRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.PARCHED, ModelLayers.PARCHED_ARMOR);
   }

   public ResourceLocation getTextureLocation(SkeletonRenderState var1) {
      return PARCHED_SKELETON_LOCATION;
   }

   public SkeletonRenderState createRenderState() {
      return new SkeletonRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
