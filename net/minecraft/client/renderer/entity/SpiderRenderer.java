package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;

public class SpiderRenderer extends MobRenderer {
   private static final ResourceLocation SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/spider.png");

   public SpiderRenderer(EntityRenderDispatcher var1) {
      super(var1, new SpiderModel(), 0.8F);
      this.addLayer(new SpiderEyesLayer(this));
   }

   protected float getFlipDegrees(Spider var1) {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(Spider var1) {
      return SPIDER_LOCATION;
   }
}
