package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.animal.fox.FoxModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.layers.FoxHeldItemLayer;
import net.minecraft.client.renderer.entity.state.FoxRenderState;
import net.minecraft.client.renderer.entity.state.HoldingEntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.fox.Fox;
import org.joml.Quaternionfc;

public class FoxRenderer extends AgeableMobRenderer<Fox, FoxRenderState, FoxModel> {
   private static final Identifier RED_FOX_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fox/fox.png");
   private static final Identifier RED_FOX_SLEEP_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fox/fox_sleep.png");
   private static final Identifier SNOW_FOX_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fox/fox_snow.png");
   private static final Identifier SNOW_FOX_SLEEP_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fox/fox_snow_sleep.png");

   public FoxRenderer(final EntityRendererProvider.Context context) {
      super(context, new FoxModel(context.bakeLayer(ModelLayers.FOX)), new FoxModel(context.bakeLayer(ModelLayers.FOX_BABY)), 0.4F);
      this.addLayer(new FoxHeldItemLayer(this));
   }

   protected void setupRotations(final FoxRenderState state, final PoseStack poseStack, final float bodyRot, final float entityScale) {
      super.setupRotations(state, poseStack, bodyRot, entityScale);
      if (state.isPouncing || state.isFaceplanted) {
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(-state.xRot));
      }

   }

   public Identifier getTextureLocation(final FoxRenderState state) {
      if (state.variant == Fox.Variant.RED) {
         return state.isSleeping ? RED_FOX_SLEEP_TEXTURE : RED_FOX_TEXTURE;
      } else {
         return state.isSleeping ? SNOW_FOX_SLEEP_TEXTURE : SNOW_FOX_TEXTURE;
      }
   }

   public FoxRenderState createRenderState() {
      return new FoxRenderState();
   }

   public void extractRenderState(final Fox entity, final FoxRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      HoldingEntityRenderState.extractHoldingEntityRenderState(entity, state, this.itemModelResolver);
      state.headRollAngle = entity.getHeadRollAngle(partialTicks);
      state.isCrouching = entity.isCrouching();
      state.crouchAmount = entity.getCrouchAmount(partialTicks);
      state.isSleeping = entity.isSleeping();
      state.isSitting = entity.isSitting();
      state.isFaceplanted = entity.isFaceplanted();
      state.isPouncing = entity.isPouncing();
      state.variant = entity.getVariant();
   }
}
