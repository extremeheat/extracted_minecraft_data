package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.blockentity.state.SignRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
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

public abstract class AbstractSignRenderer implements BlockEntityRenderer<SignBlockEntity, SignRenderState> {
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private final Font font;
   private final MaterialSet materials;

   public AbstractSignRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.font = var1.font();
      this.materials = var1.materials();
   }

   protected abstract Model.Simple getSignModel(BlockState var1, WoodType var2);

   protected abstract Material getSignMaterial(WoodType var1);

   protected abstract float getSignModelRenderScale();

   protected abstract float getSignTextRenderScale();

   protected abstract Vec3 getTextOffset();

   protected abstract void translateSign(PoseStack var1, float var2, BlockState var3);

   public void submit(SignRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      BlockState var5 = var1.blockState;
      SignBlock var6 = (SignBlock)var5.getBlock();
      Model.Simple var7 = this.getSignModel(var5, var6.type());
      this.submitSignWithText(var1, var2, var5, var6, var6.type(), var7, var1.breakProgress, var3);
   }

   private void submitSignWithText(SignRenderState var1, PoseStack var2, BlockState var3, SignBlock var4, WoodType var5, Model.Simple var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      var2.pushPose();
      this.translateSign(var2, -var4.getYRotationDegrees(var3), var3);
      this.submitSign(var2, var1.lightCoords, var5, var6, var7, var8);
      this.submitSignText(var1, var2, var8, true);
      this.submitSignText(var1, var2, var8, false);
      var2.popPose();
   }

   protected void submitSign(PoseStack var1, int var2, WoodType var3, Model.Simple var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5, SubmitNodeCollector var6) {
      var1.pushPose();
      float var7 = this.getSignModelRenderScale();
      var1.scale(var7, -var7, -var7);
      Material var8 = this.getSignMaterial(var3);
      Objects.requireNonNull(var4);
      RenderType var9 = var8.renderType(var4::renderType);
      var6.submitModel(var4, Unit.INSTANCE, var1, var9, var2, OverlayTexture.NO_OVERLAY, -1, this.materials.get(var8), 0, var5);
      var1.popPose();
   }

   private void submitSignText(SignRenderState var1, PoseStack var2, SubmitNodeCollector var3, boolean var4) {
      SignText var5 = var4 ? var1.frontText : var1.backText;
      if (var5 != null) {
         var2.pushPose();
         this.translateSignText(var2, var4, this.getTextOffset());
         int var6 = getDarkColor(var5);
         int var7 = 4 * var1.textLineHeight / 2;
         FormattedCharSequence[] var8 = var5.getRenderMessages(var1.isTextFilteringEnabled, (var2x) -> {
            List var3 = this.font.split(var2x, var1.maxTextLineWidth);
            return var3.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)var3.get(0);
         });
         int var9;
         boolean var10;
         int var11;
         if (var5.hasGlowingText()) {
            var9 = var5.getColor().getTextColor();
            var10 = var9 == DyeColor.BLACK.getTextColor() || var1.drawOutline;
            var11 = 15728880;
         } else {
            var9 = var6;
            var10 = false;
            var11 = var1.lightCoords;
         }

         for(int var12 = 0; var12 < 4; ++var12) {
            FormattedCharSequence var13 = var8[var12];
            float var14 = (float)(-this.font.width(var13) / 2);
            var3.submitText(var2, var14, (float)(var12 * var1.textLineHeight - var7), var13, false, Font.DisplayMode.POLYGON_OFFSET, var11, var9, 0, var10 ? var6 : 0);
         }

         var2.popPose();
      }
   }

   private void translateSignText(PoseStack var1, boolean var2, Vec3 var3) {
      if (!var2) {
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      }

      float var4 = 0.015625F * this.getSignTextRenderScale();
      var1.translate(var3);
      var1.scale(var4, -var4, var4);
   }

   private static boolean isOutlineVisible(BlockPos var0) {
      Minecraft var1 = Minecraft.getInstance();
      LocalPlayer var2 = var1.player;
      if (var2 != null && var1.options.getCameraType().isFirstPerson() && var2.isScoping()) {
         return true;
      } else {
         Entity var3 = var1.getCameraEntity();
         return var3 != null && var3.distanceToSqr(Vec3.atCenterOf(var0)) < (double)OUTLINE_RENDER_DISTANCE;
      }
   }

   public static int getDarkColor(SignText var0) {
      int var1 = var0.getColor().getTextColor();
      return var1 == DyeColor.BLACK.getTextColor() && var0.hasGlowingText() ? -988212 : ARGB.scaleRGB(var1, 0.4F);
   }

   public SignRenderState createRenderState() {
      return new SignRenderState();
   }

   public void extractRenderState(SignBlockEntity var1, SignRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.maxTextLineWidth = var1.getMaxTextLineWidth();
      var2.textLineHeight = var1.getTextLineHeight();
      var2.frontText = var1.getFrontText();
      var2.backText = var1.getBackText();
      var2.isTextFilteringEnabled = Minecraft.getInstance().isTextFilteringEnabled();
      var2.drawOutline = isOutlineVisible(var1.getBlockPos());
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
