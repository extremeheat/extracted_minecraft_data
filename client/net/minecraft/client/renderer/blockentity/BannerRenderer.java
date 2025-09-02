package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.state.BannerRenderState;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity, BannerRenderState> {
   private static final int MAX_PATTERNS = 16;
   private static final float SIZE = 0.6666667F;
   private final MaterialSet materials;
   private final BannerModel standingModel;
   private final BannerModel wallModel;
   private final BannerFlagModel standingFlagModel;
   private final BannerFlagModel wallFlagModel;

   public BannerRenderer(BlockEntityRendererProvider.Context var1) {
      this(var1.entityModelSet(), var1.materials());
   }

   public BannerRenderer(SpecialModelRenderer.BakingContext var1) {
      this(var1.entityModelSet(), var1.materials());
   }

   public BannerRenderer(EntityModelSet var1, MaterialSet var2) {
      super();
      this.materials = var2;
      this.standingModel = new BannerModel(var1.bakeLayer(ModelLayers.STANDING_BANNER));
      this.wallModel = new BannerModel(var1.bakeLayer(ModelLayers.WALL_BANNER));
      this.standingFlagModel = new BannerFlagModel(var1.bakeLayer(ModelLayers.STANDING_BANNER_FLAG));
      this.wallFlagModel = new BannerFlagModel(var1.bakeLayer(ModelLayers.WALL_BANNER_FLAG));
   }

   public BannerRenderState createRenderState() {
      return new BannerRenderState();
   }

   public void extractRenderState(BannerBlockEntity var1, BannerRenderState var2, float var3, Vec3 var4, @Nullable ModelFeatureRenderer.CrumblingOverlay var5) {
      BlockEntityRenderer.super.extractRenderState(var1, var2, var3, var4, var5);
      var2.baseColor = var1.getBaseColor();
      var2.patterns = var1.getPatterns();
      BlockState var6 = var1.getBlockState();
      if (var6.getBlock() instanceof BannerBlock) {
         var2.angle = -RotationSegment.convertToDegrees((Integer)var6.getValue(BannerBlock.ROTATION));
         var2.standing = true;
      } else {
         var2.angle = -((Direction)var6.getValue(WallBannerBlock.FACING)).toYRot();
         var2.standing = false;
      }

      long var7 = var1.getLevel() != null ? var1.getLevel().getGameTime() : 0L;
      BlockPos var9 = var1.getBlockPos();
      var2.phase = ((float)Math.floorMod((long)(var9.getX() * 7 + var9.getY() * 9 + var9.getZ() * 13) + var7, 100L) + var3) / 100.0F;
   }

   public void submit(BannerRenderState var1, PoseStack var2, SubmitNodeCollector var3) {
      BannerModel var4;
      BannerFlagModel var5;
      if (var1.standing) {
         var4 = this.standingModel;
         var5 = this.standingFlagModel;
      } else {
         var4 = this.wallModel;
         var5 = this.wallFlagModel;
      }

      submitBanner(this.materials, var2, var3, var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.angle, var4, var5, var1.phase, var1.baseColor, var1.patterns, var1.breakProgress);
   }

   public void submitSpecial(PoseStack var1, SubmitNodeCollector var2, int var3, int var4, DyeColor var5, BannerPatternLayers var6) {
      submitBanner(this.materials, var1, var2, var3, var4, 0.0F, this.standingModel, this.standingFlagModel, 0.0F, var5, var6, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   private static void submitBanner(MaterialSet var0, PoseStack var1, SubmitNodeCollector var2, int var3, int var4, float var5, BannerModel var6, BannerFlagModel var7, float var8, DyeColor var9, BannerPatternLayers var10, @Nullable ModelFeatureRenderer.CrumblingOverlay var11) {
      var1.pushPose();
      var1.translate(0.5F, 0.0F, 0.5F);
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var5));
      var1.scale(0.6666667F, -0.6666667F, -0.6666667F);
      Material var12 = ModelBakery.BANNER_BASE;
      var2.submitModel(var6, Unit.INSTANCE, var1, var12.renderType(RenderType::entitySolid), var3, var4, -1, var0.get(var12), 0, var11);
      var7.setupAnim(var8);
      submitPatterns(var0, var1, var2, var3, var4, var7.root(), var12, true, var9, var10, var11);
      var1.popPose();
   }

   public static void submitPatterns(MaterialSet var0, PoseStack var1, SubmitNodeCollector var2, int var3, int var4, ModelPart var5, Material var6, boolean var7, DyeColor var8, BannerPatternLayers var9, @Nullable ModelFeatureRenderer.CrumblingOverlay var10) {
      submitPatterns(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, false, true, var10);
   }

   public static void submitPatterns(MaterialSet var0, PoseStack var1, SubmitNodeCollector var2, int var3, int var4, ModelPart var5, Material var6, boolean var7, DyeColor var8, BannerPatternLayers var9, boolean var10, boolean var11, @Nullable ModelFeatureRenderer.CrumblingOverlay var12) {
      var2.submitModelPart(var5, var1, var6.renderType(RenderType::entitySolid), var3, var4, var0.get(var6), var11, var10);
      submitPatternLayer(var0, var1, var2, var3, var4, var5, var7 ? Sheets.BANNER_BASE : Sheets.SHIELD_BASE, var8, var12);

      for(int var13 = 0; var13 < 16 && var13 < var9.layers().size(); ++var13) {
         BannerPatternLayers.Layer var14 = (BannerPatternLayers.Layer)var9.layers().get(var13);
         Material var15 = var7 ? Sheets.getBannerMaterial(var14.pattern()) : Sheets.getShieldMaterial(var14.pattern());
         submitPatternLayer(var0, var1, var2, var3, var4, var5, var15, var14.color(), (ModelFeatureRenderer.CrumblingOverlay)null);
      }

   }

   private static void submitPatternLayer(MaterialSet var0, PoseStack var1, SubmitNodeCollector var2, int var3, int var4, ModelPart var5, Material var6, DyeColor var7, @Nullable ModelFeatureRenderer.CrumblingOverlay var8) {
      int var9 = var7.getTextureDiffuseColor();
      var2.submitModelPart(var5, var1, var6.renderType(RenderType::entityNoOutline), var3, var4, var0.get(var6), var9, var8);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.translate(0.5F, 0.0F, 0.5F);
      var2.scale(0.6666667F, -0.6666667F, -0.6666667F);
      this.standingModel.root().getExtentsForGui(var2, var1);
      this.standingFlagModel.setupAnim(0.0F);
      this.standingFlagModel.root().getExtentsForGui(var2, var1);
   }

   // $FF: synthetic method
   public BlockEntityRenderState createRenderState() {
      return this.createRenderState();
   }
}
