package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.monster.spider.CaveSpider;

public class CaveSpiderRenderer extends SpiderRenderer<CaveSpider> {
   private static final Identifier CAVE_SPIDER_LOCATION = Identifier.withDefaultNamespace("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRendererProvider.Context var1) {
      super(var1, ModelLayers.CAVE_SPIDER);
      this.shadowRadius = 0.56F;
   }

   public Identifier getTextureLocation(LivingEntityRenderState var1) {
      return CAVE_SPIDER_LOCATION;
   }
}
