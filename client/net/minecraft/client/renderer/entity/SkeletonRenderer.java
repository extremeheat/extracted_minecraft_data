package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.SkeletonRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.skeleton.Skeleton;

public class SkeletonRenderer extends AbstractSkeletonRenderer<Skeleton, SkeletonRenderState> {
   private static final Identifier SKELETON_LOCATION = Identifier.withDefaultNamespace("textures/entity/skeleton/skeleton.png");

   public SkeletonRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.SKELETON, ModelLayers.SKELETON_ARMOR);
   }

   public Identifier getTextureLocation(SkeletonRenderState var1) {
      return SKELETON_LOCATION;
   }

   public SkeletonRenderState createRenderState() {
      return new SkeletonRenderState();
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
