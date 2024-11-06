package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Fox;

public class FoxRenderer extends AgeableMobRenderer<Fox, FoxRenderState, FoxModel> {
   private static final ResourceLocation RED_FOX_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/fox.png");
   private static final ResourceLocation RED_FOX_SLEEP_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/fox_sleep.png");
   private static final ResourceLocation SNOW_FOX_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/snow_fox.png");
   private static final ResourceLocation SNOW_FOX_SLEEP_TEXTURE = ResourceLocation.withDefaultNamespace("textures/entity/fox/snow_fox_sleep.png");

   public FoxRenderer(EntityRendererProvider.Context var1) {
      super(var1, new FoxModel(var1.bakeLayer(ModelLayers.FOX)), new FoxModel(var1.bakeLayer(ModelLayers.FOX_BABY)), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this));
   }

   protected void setupRotations(FoxRenderState var1, PoseStack var2, float var3, float var4) {
      super.setupRotations(var1, var2, var3, var4);
      if (var1.isPouncing || var1.isFaceplanted) {
         var2.mulPose(Axis.XP.rotationDegrees(-var1.xRot));
      }

   }

   public ResourceLocation getTextureLocation(FoxRenderState var1) {
      if (var1.variant == Fox.Variant.RED) {
         return var1.isSleeping ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
      } else {
         return var1.isSleeping ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
      }
   }

   public FoxRenderState createRenderState() {
      return new FoxRenderState();
   }

   public void extractRenderState(Fox var1, FoxRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      HoldingEntityRenderState.extractHoldingEntityRenderState(var1, var2, this.itemModelResolver);
      var2.headRollAngle = var1.getHeadRollAngle(var3);
      var2.isCrouching = var1.isCrouching();
      var2.crouchAmount = var1.getCrouchAmount(var3);
      var2.isSleeping = var1.isSleeping();
      var2.isSitting = var1.isSitting();
      var2.isFaceplanted = var1.isFaceplanted();
      var2.isPouncing = var1.isPouncing();
      var2.variant = var1.getVariant();
   }

   // $FF: synthetic method
   public ResourceLocation getTextureLocation(final LivingEntityRenderState var1) {
      return this.getTextureLocation((FoxRenderState)var1);
   }

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
