package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

public class CaveSpiderRenderer extends SpiderRenderer<CaveSpider> {
   private static final ResourceLocation CAVE_SPIDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.CAVE_SPIDER);
      this.shadowRadius = 0.56F;
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return CAVE_SPIDER_LOCATION;
   }
}
