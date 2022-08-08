package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Spider;

public class SpiderRenderer<T extends Spider> extends MobRenderer<T, SpiderModel<T>> {
   private static final ResourceLocation SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/spider.png");

   public SpiderRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.SPIDER);
   }

   public SpiderRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1, new SpiderModel(var1.bakeLayer(var2)), 0.8F);
      this.addLayer(new SpiderEyesLayer(this));
   }

   protected float getFlipDegrees(T var1) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(T var1) {
      return SPIDER_LOCATION;
   }

   // $FF: synthetic method
   protected float getFlipDegrees(LivingEntity var1) {
      return this.getFlipDegrees((Spider)var1);
   }
}
