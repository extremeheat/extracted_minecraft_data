package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;

public class RenderCaveSpider extends RenderSpider<EntityCaveSpider> {
   private static final ResourceLocation field_110893_a = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public RenderCaveSpider(RenderManager var1) {
      super(var1);
      this.field_76989_e *= 0.7F;
   }

   protected void func_77041_b(EntityCaveSpider var1, float var2) {
      GlStateManager.func_179152_a(0.7F, 0.7F, 0.7F);
   }

   protected ResourceLocation func_110775_a(EntityCaveSpider var1) {
      return field_110893_a;
   }
}
