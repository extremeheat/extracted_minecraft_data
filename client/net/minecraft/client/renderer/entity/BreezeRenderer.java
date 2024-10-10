package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.BreezeModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.BreezeEyesLayer;
import net.minecraft.client.renderer.entity.layers.BreezeWindLayer;
import net.minecraft.client.renderer.entity.state.BreezeRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.breeze.Breeze;

public class BreezeRenderer extends MobRenderer<Breeze, BreezeRenderState, BreezeModel> {
   private static final ResourceLocation TEXTURE_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/breeze/breeze.png");

   public BreezeRenderer(EntityRendererProvider.Context var1) {
      super(var1, new BreezeModel(var1.bakeLayer(ModelLayers.BREEZE)), 0.5F);
      this.addLayer(new BreezeWindLayer(var1, this));
      this.addLayer(new BreezeEyesLayer(this));
   }

   public void render(BreezeRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      BreezeModel var5 = this.getModel();
      enable(var5, var5.head(), var5.rods());
      super.render(var1, var2, var3, var4);
   }

   public ResourceLocation getTextureLocation(BreezeRenderState var1) {
      return TEXTURE_LOCATION;
   }

   public BreezeRenderState createRenderState() {
      return new BreezeRenderState();
   }

   public void extractRenderState(Breeze var1, BreezeRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.idle.copyFrom(var1.idle);
      var2.shoot.copyFrom(var1.shoot);
      var2.slide.copyFrom(var1.slide);
      var2.slideBack.copyFrom(var1.slideBack);
      var2.inhale.copyFrom(var1.inhale);
      var2.longJump.copyFrom(var1.longJump);
   }

   public static BreezeModel enable(BreezeModel var0, ModelPart... var1) {
      var0.head().visible = false;
      var0.eyes().visible = false;
      var0.rods().visible = false;
      var0.wind().visible = false;

      for (ModelPart var5 : var1) {
         var5.visible = true;
      }

      return var0;
   }
}
