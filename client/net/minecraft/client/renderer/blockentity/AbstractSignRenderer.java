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
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
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

public abstract class AbstractSignRenderer implements BlockEntityRenderer<SignBlockEntity> {
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

   public void submit(SignBlockEntity var1, float var2, PoseStack var3, int var4, int var5, Vec3 var6, @Nullable ModelFeatureRenderer.CrumblingOverlay var7, SubmitNodeCollector var8) {
      BlockState var9 = var1.getBlockState();
      SignBlock var10 = (SignBlock)var9.getBlock();
      Model.Simple var11 = this.getSignModel(var9, var10.type());
      this.submitSignWithText(var1, var3, var4, var5, var9, var10, var10.type(), var11, var7, var8);
   }

   private void submitSignWithText(SignBlockEntity var1, PoseStack var2, int var3, int var4, BlockState var5, SignBlock var6, WoodType var7, Model.Simple var8, @Nullable ModelFeatureRenderer.CrumblingOverlay var9, SubmitNodeCollector var10) {
      var2.pushPose();
      this.translateSign(var2, -var6.getYRotationDegrees(var5), var5);
      this.submitSign(var2, var3, var4, var7, var8, var9, var10);
      this.submitSignText(var1.getBlockPos(), var1.getFrontText(), var2, var10, var3, var1.getTextLineHeight(), var1.getMaxTextLineWidth(), true);
      this.submitSignText(var1.getBlockPos(), var1.getBackText(), var2, var10, var3, var1.getTextLineHeight(), var1.getMaxTextLineWidth(), false);
      var2.popPose();
   }

   protected void submitSign(PoseStack var1, int var2, int var3, WoodType var4, Model.Simple var5, @Nullable ModelFeatureRenderer.CrumblingOverlay var6, SubmitNodeCollector var7) {
      var1.pushPose();
      float var8 = this.getSignModelRenderScale();
      var1.scale(var8, -var8, -var8);
      Material var9 = this.getSignMaterial(var4);
      Objects.requireNonNull(var5);
      RenderType var10 = var9.renderType(var5::renderType);
      var7.submitModel(var5, Unit.INSTANCE, var1, var10, var2, var3, -1, this.materials.get(var9), 0, var6);
      var1.popPose();
   }

   private void submitSignText(BlockPos var1, SignText var2, PoseStack var3, SubmitNodeCollector var4, int var5, int var6, int var7, boolean var8) {
      var3.pushPose();
      this.translateSignText(var3, var8, this.getTextOffset());
      int var9 = getDarkColor(var2);
      int var10 = 4 * var6 / 2;
      FormattedCharSequence[] var11 = var2.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (var2x) -> {
         List var3 = this.font.split(var2x, var7);
         return var3.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)var3.get(0);
      });
      int var12;
      boolean var13;
      int var14;
      if (var2.hasGlowingText()) {
         var12 = var2.getColor().getTextColor();
         var13 = isOutlineVisible(var1, var12);
         var14 = 15728880;
      } else {
         var12 = var9;
         var13 = false;
         var14 = var5;
      }

      for(int var15 = 0; var15 < 4; ++var15) {
         FormattedCharSequence var16 = var11[var15];
         float var17 = (float)(-this.font.width(var16) / 2);
         var4.submitText(var3, var17, (float)(var15 * var6 - var10), var16, false, Font.DisplayMode.POLYGON_OFFSET, var14, var12, 0, var13 ? var9 : 0);
      }

      var3.popPose();
   }

   private void translateSignText(PoseStack var1, boolean var2, Vec3 var3) {
      if (!var2) {
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(180.0F));
      }

      float var4 = 0.015625F * this.getSignTextRenderScale();
      var1.translate(var3);
      var1.scale(var4, -var4, var4);
   }

   private static boolean isOutlineVisible(BlockPos var0, int var1) {
      if (var1 == DyeColor.BLACK.getTextColor()) {
         return true;
      } else {
         Minecraft var2 = Minecraft.getInstance();
         LocalPlayer var3 = var2.player;
         if (var3 != null && var2.options.getCameraType().isFirstPerson() && var3.isScoping()) {
            return true;
         } else {
            Entity var4 = var2.getCameraEntity();
            return var4 != null && var4.distanceToSqr(Vec3.atCenterOf(var0)) < (double)OUTLINE_RENDER_DISTANCE;
         }
      }
   }

   public static int getDarkColor(SignText var0) {
      int var1 = var0.getColor().getTextColor();
      return var1 == DyeColor.BLACK.getTextColor() && var0.hasGlowingText() ? -988212 : ARGB.scaleRGB(var1, 0.4F);
   }
}
