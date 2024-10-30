package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.StriderModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.SaddleLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.StriderRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Strider;

public class StriderRenderer extends AgeableMobRenderer<Strider, StriderRenderState, StriderModel> {
   private static final ResourceLocation STRIDER_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/strider/strider.png");
   private static final ResourceLocation COLD_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/strider/strider_cold.png");
   private static final float SHADOW_RADIUS = 0.5F;

   public StriderRenderer(EntityRendererProvider.Context var1) {
      super(var1, new StriderModel(var1.bakeLayer(ModelLayers.STRIDER)), new StriderModel(var1.bakeLayer(ModelLayers.STRIDER_BABY)), 0.5F);
      this.addLayer(new SaddleLayer(this, new StriderModel(var1.bakeLayer(ModelLayers.STRIDER_SADDLE)), new StriderModel(var1.bakeLayer(ModelLayers.STRIDER_BABY_SADDLE)), ResourceLocation.withDefaultNamespace("textures/entity/strider/strider_saddle.png")));
   }

   public ResourceLocation getTextureLocation(StriderRenderState var1) {
      return var1.isSuffocating ? COLD_LOCATION : STRIDER_LOCATION;
   }

   protected float getShadowRadius(StriderRenderState var1) {
      float var2 = super.getShadowRadius(var1);
      return var1.isBaby ? var2 * 0.5F : var2;
   }

   public StriderRenderState createRenderState() {
      return new StriderRenderState();
   }

   public void extractRenderState(Strider var1, StriderRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.isSaddled = var1.isSaddled();
      var2.isSuffocating = var1.isSuffocating();
      var2.isRidden = var1.isVehicle();
   }

   protected boolean isShaking(StriderRenderState var1) {
      return super.isShaking(var1) || var1.isSuffocating;
   }

   // $FF: synthetic method
   protected float getShadowRadius(final LivingEntityRenderState var1) {
      return this.getShadowRadius((StriderRenderState)var1);
   }

   // $FF: synthetic method
   protected boolean isShaking(final LivingEntityRenderState var1) {
      return this.isShaking((StriderRenderState)var1);
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((StriderRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected float getShadowRadius(final EntityRenderState var1) {
      return this.getShadowRadius((StriderRenderState)var1);
   }
}
