package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.CaveSpider;

public class CaveSpiderRenderer extends SpiderRenderer {
   private static final ResourceLocation CAVE_SPIDER_LOCATION = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public CaveSpiderRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius *= 0.7F;
   }

   protected void scale(CaveSpider var1, PoseStack var2, float var3) {
      var2.scale(0.7F, 0.7F, 0.7F);
   }

   public ResourceLocation getTextureLocation(CaveSpider var1) {
      return CAVE_SPIDER_LOCATION;
   }
}
