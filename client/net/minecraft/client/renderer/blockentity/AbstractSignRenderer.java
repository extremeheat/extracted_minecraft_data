package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public abstract class AbstractSignRenderer implements BlockEntityRenderer<SignBlockEntity> {
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private final Font font;

   public AbstractSignRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.font = var1.getFont();
   }

   protected abstract Model getSignModel(BlockState var1, WoodType var2);

   protected abstract Material getSignMaterial(WoodType var1);

   protected abstract float getSignModelRenderScale();

   protected abstract float getSignTextRenderScale();

   protected abstract Vec3 getTextOffset();

   protected abstract void translateSign(PoseStack var1, float var2, BlockState var3);

   public void render(SignBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var7 = var1.getBlockState();
      SignBlock var8 = (SignBlock)var7.getBlock();
      Model var9 = this.getSignModel(var7, var8.type());
      this.renderSignWithText(var1, var3, var4, var5, var6, var7, var8, var8.type(), var9);
   }

   private void renderSignWithText(SignBlockEntity var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, BlockState var6, SignBlock var7, WoodType var8, Model var9) {
      var2.pushPose();
      this.translateSign(var2, -var7.getYRotationDegrees(var6), var6);
      this.renderSign(var2, var3, var4, var5, var8, var9);
      this.renderSignText(var1.getBlockPos(), var1.getFrontText(), var2, var3, var4, var1.getTextLineHeight(), var1.getMaxTextLineWidth(), true);
      this.renderSignText(var1.getBlockPos(), var1.getBackText(), var2, var3, var4, var1.getTextLineHeight(), var1.getMaxTextLineWidth(), false);
      var2.popPose();
   }

   protected void renderSign(PoseStack var1, MultiBufferSource var2, int var3, int var4, WoodType var5, Model var6) {
      var1.pushPose();
      float var7 = this.getSignModelRenderScale();
      var1.scale(var7, -var7, -var7);
      Material var8 = this.getSignMaterial(var5);
      Objects.requireNonNull(var6);
      VertexConsumer var9 = var8.buffer(var2, var6::renderType);
      var6.renderToBuffer(var1, var9, var3, var4);
      var1.popPose();
   }

   private void renderSignText(BlockPos var1, SignText var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, int var7, boolean var8) {
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
         if (var13) {
            this.font.drawInBatch8xOutline(var16, var17, (float)(var15 * var6 - var10), var12, var9, var3.last().pose(), var4, var14);
         } else {
            this.font.drawInBatch((FormattedCharSequence)var16, var17, (float)(var15 * var6 - var10), var12, false, var3.last().pose(), var4, Font.DisplayMode.POLYGON_OFFSET, 0, var14);
         }
      }

      var3.popPose();
   }

   private void translateSignText(PoseStack var1, boolean var2, Vec3 var3) {
      if (!var2) {
         var1.mulPose(Axis.YP.rotationDegrees(180.0F));
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
      if (var1 == DyeColor.BLACK.getTextColor() && var0.hasGlowingText()) {
         return -988212;
      } else {
         double var2 = 0.4;
         int var4 = (int)((double)ARGB.red(var1) * 0.4);
         int var5 = (int)((double)ARGB.green(var1) * 0.4);
         int var6 = (int)((double)ARGB.blue(var1) * 0.4);
         return ARGB.color(0, var4, var5, var6);
      }
   }
}
