package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

public class CaveSpiderRenderer extends SpiderRenderer<CaveSpider> {
   private static final ResourceLocation CAVE_SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius *= 0.7F;
   }

   protected void scale(CaveSpider var1, float var2) {
      GlStateManager.scalef(0.7F, 0.7F, 0.7F);
   }

   protected ResourceLocation getTextureLocation(CaveSpider var1) {
      return CAVE_SPIDER_LOCATION;
   }
}
