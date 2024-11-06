package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;

public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity> {
   private static final int MAX_PATTERNS = 16;
   private static final float SIZE = 0.6666667F;
   private final BannerModel standingModel;
   private final BannerModel wallModel;
   private final BannerFlagModel standingFlagModel;
   private final BannerFlagModel wallFlagModel;

   public BannerRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.getModelSet());
   }

   public BannerRenderer(EntityModelSet var1) {
      super();
      this.standingModel = new BannerModel(var1.bakeLayer(ModelLayers.STANDING_BANNER));
      this.wallModel = new BannerModel(var1.bakeLayer(ModelLayers.WALL_BANNER));
      this.standingFlagModel = new BannerFlagModel(var1.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
      this.wallFlagModel = new BannerFlagModel(var1.bakeLayer(ModelLayers.WALL_BANNER_FLAG));
   }

   public void render(BannerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var10 = var1.getBlockState();
      BannerModel var7;
      BannerFlagModel var8;
      float var9;
      if (var10.getBlock() instanceof BannerBlock) {
         var9 = -RotationSegment.convertToDegrees((Integer)var10.getValue(BannerBlock.ROTATION));
         var7 = this.standingModel;
         var8 = this.standingFlagModel;
      } else {
         var9 = -((Direction)var10.getValue(WallBannerBlock.FACING)).toYRot();
         var7 = this.wallModel;
         var8 = this.wallFlagModel;
      }

      long var11 = var1.getLevel().getGameTime();
      BlockPos var13 = var1.getBlockPos();
      float var14 = ((float)Math.floorMod((long)(var13.getX() * 7 + var13.getY() * 9 + var13.getZ() * 13) + var11, 100L) + var2) / 100.0F;
      renderBanner(var3, var4, var5, var6, var9, var7, var8, var14, var1.getBaseColor(), var1.getPatterns());
   }

   public void renderInHand(PoseStack var1, MultiBufferSource var2, int var3, int var4, DyeColor var5, BannerPatternLayers var6) {
      renderBanner(var1, var2, var3, var4, 0.0F, this.standingModel, this.standingFlagModel, 0.0F, var5, var6);
   }

   private static void renderBanner(PoseStack var0, MultiBufferSource var1, int var2, int var3, float var4, BannerModel var5, BannerFlagModel var6, float var7, DyeColor var8, BannerPatternLayers var9) {
      var0.pushPose();
      var0.translate(0.5F, 0.0F, 0.5F);
      var0.mulPose(Axis.YP.rotationDegrees(var4));
      var0.scale(0.6666667F, -0.6666667F, -0.6666667F);
      var5.renderToBuffer(var0, ModelBakery.BANNER_BASE.buffer(var1, RenderType::entitySolid), var2, var3);
      var6.setupAnim(var7);
      renderPatterns(var0, var1, var2, var3, var6.root(), ModelBakery.BANNER_BASE, true, var8, var9);
      var0.popPose();
   }

   public static void renderPatterns(PoseStack var0, MultiBufferSource var1, int var2, int var3, ModelPart var4, Material var5, boolean var6, DyeColor var7, BannerPatternLayers var8) {
      renderPatterns(var0, var1, var2, var3, var4, var5, var6, var7, var8, false, true);
   }

   public static void renderPatterns(PoseStack var0, MultiBufferSource var1, int var2, int var3, ModelPart var4, Material var5, boolean var6, DyeColor var7, BannerPatternLayers var8, boolean var9, boolean var10) {
      var4.render(var0, var5.buffer(var1, RenderType::entitySolid, var10, var9), var2, var3);
      renderPatternLayer(var0, var1, var2, var3, var4, var6 ? Sheets.BANNER_BASE : Sheets.SHIELD_BASE, var7);

      for(int var11 = 0; var11 < 16 && var11 < var8.layers().size(); ++var11) {
         BannerPatternLayers.Layer var12 = (BannerPatternLayers.Layer)var8.layers().get(var11);
         Material var13 = var6 ? Sheets.getBannerMaterial(var12.pattern()) : Sheets.getShieldMaterial(var12.pattern());
         renderPatternLayer(var0, var1, var2, var3, var4, var13, var12.color());
      }

   }

   private static void renderPatternLayer(PoseStack var0, MultiBufferSource var1, int var2, int var3, ModelPart var4, Material var5, DyeColor var6) {
      int var7 = var6.getTextureDiffuseColor();
      var4.render(var0, var5.buffer(var1, RenderType::entityNoOutline), var2, var3, var7);
   }
}
