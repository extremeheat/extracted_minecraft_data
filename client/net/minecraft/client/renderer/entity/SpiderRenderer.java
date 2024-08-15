package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SpiderEyesLayer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Spider;

public class SpiderRenderer<T extends Spider> extends MobRenderer<T, LivingEntityRenderState, SpiderModel> {
   private static final ResourceLocation SPIDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/spider/spider.png");

   public SpiderRenderer(EntityRendererProvider.Context var1) {
      this(var1, ModelLayers.SPIDER);
   }

   public SpiderRenderer(EntityRendererProvider.Context var1, ModelLayerLocation var2) {
      super(var1, new SpiderModel(var1.bakeLayer(var2)), 0.8F);
      this.addLayer(new SpiderEyesLayer<>(this));
   }

   @Override
   protected float getFlipDegrees() {
      return 180.0F;
   }

   public ResourceLocation getTextureLocation(LivingEntityRenderState var1) {
      return SPIDER_LOCATION;
   }

   public LivingEntityRenderState createRenderState() {
      return new LivingEntityRenderState();
   }

   public void extractRenderState(T var1, LivingEntityRenderState var2, float var3) {
      super.extractRenderState((T)var1, var2, var3);
   }
}
