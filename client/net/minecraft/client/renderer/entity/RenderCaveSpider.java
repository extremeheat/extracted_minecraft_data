package net.minecraft.client.renderer.entity;

import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCaveSpider extends RenderSpider {
   private static final ResourceLocation field_110893_a = new ResourceLocation("textures/entity/spider/cave_spider.png");

   public RenderCaveSpider() {
      super();
      this.field_76989_e *= 0.7F;
   }

   protected void func_77041_b(EntityCaveSpider var1, float var2) {
      GL11.glScalef(0.7F, 0.7F, 0.7F);
   }

   protected ResourceLocation func_110775_a(EntityCaveSpider var1) {
      return field_110893_a;
   }
}
