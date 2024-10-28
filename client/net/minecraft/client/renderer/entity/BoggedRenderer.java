package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.BoggedModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SkeletonClothingLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Bogged;

public class BoggedRenderer extends SkeletonRenderer<Bogged> {
   private static final ResourceLocation BOGGED_SKELETON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/bogged.png");
   private static final ResourceLocation BOGGED_OUTER_LAYER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/skeleton/bogged_overlay.png");

   public BoggedRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.BOGGED_INNER_ARMOR, ModelLayers.BOGGED_OUTER_ARMOR, (SkeletonModel)(new BoggedModel(var1.bakeLayer(ModelLayers.BOGGED))));
      this.addLayer(new SkeletonClothingLayer(this, var1.getModelSet(), ModelLayers.BOGGED_OUTER_LAYER, BOGGED_OUTER_LAYER_LOCATION));
   }

   public ResourceLocation getTextureLocation(Bogged var1) {
      return BOGGED_SKELETON_LOCATION;
   }
}
