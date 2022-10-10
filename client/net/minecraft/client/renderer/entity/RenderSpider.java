package net.minecraft.client.renderer.entity;

import net.minecraft.client.renderer.entity.layers.LayerSpiderEyes;
import net.minecraft.client.renderer.entity.model.ModelSpider;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.util.ResourceLocation;

public class RenderSpider<T extends EntitySpider> extends RenderLiving<T> {
   private static final ResourceLocation field_110890_f = new ResourceLocation("textures/entity/spider/spider.png");

   public RenderSpider(RenderManager var1) {
      super(var1, new ModelSpider(), 1.0F);
      this.func_177094_a(new LayerSpiderEyes(this));
   }

   protected float func_77037_a(T var1) {
      return 180.0F;
   }

   protected ResourceLocation func_110775_a(T var1) {
      return field_110890_f;
   }
}
