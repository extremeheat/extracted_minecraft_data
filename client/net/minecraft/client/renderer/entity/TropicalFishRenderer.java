package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.animal.fish.TropicalFishLargeModel;
import net.minecraft.client.model.animal.fish.TropicalFishSmallModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.TropicalFishPatternLayer;
import net.minecraft.client.renderer.entity.state.TropicalFishRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.util.CommonColors;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.level.block.ColorCollection;

public class TropicalFishRenderer extends MobRenderer<TropicalFish, TropicalFishRenderState, EntityModel<TropicalFishRenderState>> {
   private static final ColorCollection<Integer> BASE_COLORS;
   private static final ColorCollection<Integer> PATTERN_COLORS;
   private final EntityModel<TropicalFishRenderState> smallModel = this.getModel();
   private final EntityModel<TropicalFishRenderState> largeModel;
   private static final Identifier SMALL_TEXTURE;
   private static final Identifier LARGE_TEXTURE;

   public TropicalFishRenderer(final EntityRendererProvider.Context context) {
      super(context, new TropicalFishSmallModel(context.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)), 0.15F);
      this.largeModel = new TropicalFishLargeModel(context.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));
      this.addLayer(new TropicalFishPatternLayer(this, context.getModelSet()));
   }

   public Identifier getTextureLocation(final TropicalFishRenderState state) {
      Identifier var10000;
      switch (state.pattern.base()) {
         case SMALL -> var10000 = SMALL_TEXTURE;
         case LARGE -> var10000 = LARGE_TEXTURE;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public TropicalFishRenderState createRenderState() {
      return new TropicalFishRenderState();
   }

   public void extractRenderState(final TropicalFish entity, final TropicalFishRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.pattern = entity.getPattern();
      state.baseColor = (Integer)BASE_COLORS.pick(entity.getBaseColor());
      state.patternColor = (Integer)PATTERN_COLORS.pick(entity.getPatternColor());
   }

   public void submit(final TropicalFishRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      EntityModel var10001;
      switch (state.pattern.base()) {
         case SMALL -> var10001 = this.smallModel;
         case LARGE -> var10001 = this.largeModel;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      this.model = var10001;
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   protected int getModelTint(final TropicalFishRenderState state) {
      return state.baseColor;
   }

   protected void setupRotations(final TropicalFishRenderState state, final PoseStack poseStack, final float bodyRot, final float entityScale) {
      super.setupRotations(state, poseStack, bodyRot, entityScale);
      float bodyZRot = 4.3F * Mth.sin((double)(0.6F * state.ageInTicks));
      poseStack.rotateDegrees(Axis.YP, bodyZRot);
      if (!state.isInWater) {
         poseStack.translate(0.2F, 0.1F, 0.0F);
         poseStack.rotateDegrees(Axis.ZP, 90.0F);
      }

   }

   static {
      BASE_COLORS = CommonColors.TEXTURE_TINT_COLORS;
      PATTERN_COLORS = CommonColors.TEXTURE_TINT_COLORS;
      SMALL_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_a.png");
      LARGE_TEXTURE = Identifier.withDefaultNamespace("textures/entity/fish/tropical_b.png");
   }
}
