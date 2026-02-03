package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.jspecify.annotations.Nullable;

public abstract class AbstractSignRenderer implements BlockEntityRenderer<SignBlockEntity, SignRenderState> {
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private final Font font;
   private final MaterialSet materials;

   public AbstractSignRenderer(final BlockEntityRendererProvider.Context context) {
      super();
      this.font = context.font();
      this.materials = context.materials();
   }

   protected abstract Model.Simple getSignModel(BlockState blockState, WoodType type);

   protected abstract Material getSignMaterial(WoodType type);

   protected abstract float getSignModelRenderScale();

   protected abstract float getSignTextRenderScale();

   protected abstract Vec3 getTextOffset();

   protected abstract void translateSign(PoseStack poseStack, float angle, BlockState blockState);

   public void submit(final SignRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      BlockState blockState = state.blockState;
      SignBlock signBlock = (SignBlock)blockState.getBlock();
      Model.Simple signModel = this.getSignModel(blockState, signBlock.type());
      this.submitSignWithText(state, poseStack, blockState, signBlock, signBlock.type(), signModel, state.breakProgress, submitNodeCollector);
   }

   private void submitSignWithText(final SignRenderState state, final PoseStack poseStack, final BlockState blockState, final SignBlock signBlock, final WoodType type, final Model.Simple signModel, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final SubmitNodeCollector submitNodeCollector) {
      poseStack.pushPose();
      this.translateSign(poseStack, -signBlock.getYRotationDegrees(blockState), blockState);
      this.submitSign(poseStack, state.lightCoords, type, signModel, breakProgress, submitNodeCollector);
      this.submitSignText(state, poseStack, submitNodeCollector, true);
      this.submitSignText(state, poseStack, submitNodeCollector, false);
      poseStack.popPose();
   }

   protected void submitSign(final PoseStack poseStack, final int lightCoords, final WoodType type, final Model.Simple signModel, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress, final SubmitNodeCollector submitNodeCollector) {
      poseStack.pushPose();
      float scale = this.getSignModelRenderScale();
      poseStack.scale(scale, -scale, -scale);
      Material material = this.getSignMaterial(type);
      Objects.requireNonNull(signModel);
      RenderType renderType = material.renderType(signModel::renderType);
      submitNodeCollector.submitModel(signModel, Unit.INSTANCE, poseStack, renderType, lightCoords, OverlayTexture.NO_OVERLAY, -1, this.materials.get(material), 0, breakProgress);
      poseStack.popPose();
   }

   private void submitSignText(final SignRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final boolean isFrontText) {
      SignText signText = isFrontText ? state.frontText : state.backText;
      if (signText != null) {
         poseStack.pushPose();
         this.translateSignText(poseStack, isFrontText, this.getTextOffset());
         int darkColor = getDarkColor(signText);
         int signMidpoint = 4 * state.textLineHeight / 2;
         FormattedCharSequence[] formattedLines = signText.getRenderMessages(state.isTextFilteringEnabled, (input) -> {
            List<FormattedCharSequence> components = this.font.split(input, state.maxTextLineWidth);
            return components.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)components.get(0);
         });
         int textColor;
         boolean drawOutline;
         int lightVal;
         if (signText.hasGlowingText()) {
            textColor = signText.getColor().getTextColor();
            drawOutline = textColor == DyeColor.BLACK.getTextColor() || state.drawOutline;
            lightVal = 15728880;
         } else {
            textColor = darkColor;
            drawOutline = false;
            lightVal = state.lightCoords;
         }

         for(int i = 0; i < 4; ++i) {
            FormattedCharSequence actualLine = formattedLines[i];
            float x1 = (float)(-this.font.width(actualLine) / 2);
            submitNodeCollector.submitText(poseStack, x1, (float)(i * state.textLineHeight - signMidpoint), actualLine, false, Font.DisplayMode.POLYGON_OFFSET, lightVal, textColor, 0, drawOutline ? darkColor : 0);
         }

         poseStack.popPose();
      }
   }

   private void translateSignText(final PoseStack poseStack, final boolean isFrontText, final Vec3 textOffset) {
      if (!isFrontText) {
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      }

      float s = 0.015625F * this.getSignTextRenderScale();
      poseStack.translate(textOffset);
      poseStack.scale(s, -s, s);
   }

   private static boolean isOutlineVisible(final BlockPos pos) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      if (player != null && minecraft.options.getCameraType().isFirstPerson() && player.isScoping()) {
         return true;
      } else {
         Entity camera = minecraft.getCameraEntity();
         return camera != null && camera.distanceToSqr(Vec3.atCenterOf(pos)) < (double)OUTLINE_RENDER_DISTANCE;
      }
   }

   public static int getDarkColor(final SignText signText) {
      int color = signText.getColor().getTextColor();
      return color == DyeColor.BLACK.getTextColor() && signText.hasGlowingText() ? -988212 : ARGB.scaleRGB(color, 0.4F);
   }

   public SignRenderState createRenderState() {
      return new SignRenderState();
   }

   public void extractRenderState(final SignBlockEntity blockEntity, final SignRenderState state, final float partialTicks, final Vec3 cameraPosition, final ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
      BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);
      state.maxTextLineWidth = blockEntity.getMaxTextLineWidth();
      state.textLineHeight = blockEntity.getTextLineHeight();
      state.frontText = blockEntity.getFrontText();
      state.backText = blockEntity.getBackText();
      state.isTextFilteringEnabled = Minecraft.getInstance().isTextFilteringEnabled();
      state.drawOutline = isOutlineVisible(blockEntity.getBlockPos());
   }
}
