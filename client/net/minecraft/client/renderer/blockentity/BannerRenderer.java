package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.Set;
import net.minecraft.client.model.BannerFlagModel;
import net.minecraft.client.model.BannerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
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
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity> {
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

   public void render(BannerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, Vec3 var7) {
      BlockState var11 = var1.getBlockState();
      BannerModel var8;
      BannerFlagModel var9;
      float var10;
      if (var11.getBlock() instanceof BannerBlock) {
         var10 = -RotationSegment.convertToDegrees((Integer)var11.getValue(BannerBlock.ROTATION));
         var8 = this.standingModel;
         var9 = this.standingFlagModel;
      } else {
         var10 = -((Direction)var11.getValue(WallBannerBlock.FACING)).toYRot();
         var8 = this.wallModel;
         var9 = this.wallFlagModel;
      }

      long var12 = var1.getLevel().getGameTime();
      BlockPos var14 = var1.getBlockPos();
      float var15 = ((float)Math.floorMod((long)(var14.getX() * 7 + var14.getY() * 9 + var14.getZ() * 13) + var12, 100L) + var2) / 100.0F;
      renderBanner(this.materials, var3, var4, var5, var6, var10, var8, var9, var15, var1.getBaseColor(), var1.getPatterns());
   }

   public void renderInHand(PoseStack var1, MultiBufferSource var2, int var3, int var4, DyeColor var5, BannerPatternLayers var6) {
      renderBanner(this.materials, var1, var2, var3, var4, 0.0F, this.standingModel, this.standingFlagModel, 0.0F, var5, var6);
   }

   private static void renderBanner(MaterialSet var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, float var5, BannerModel var6, BannerFlagModel var7, float var8, DyeColor var9, BannerPatternLayers var10) {
      var1.pushPose();
      var1.translate(0.5F, 0.0F, 0.5F);
      var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var5));
      var1.scale(0.6666667F, -0.6666667F, -0.6666667F);
      var6.renderToBuffer(var1, ModelBakery.BANNER_BASE.buffer(var0, var2, RenderType::entitySolid), var3, var4);
      var7.setupAnim(var8);
      renderPatterns(var0, var1, var2, var3, var4, var7.root(), ModelBakery.BANNER_BASE, true, var9, var10);
      var1.popPose();
   }

   public static void renderPatterns(MaterialSet var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, ModelPart var5, Material var6, boolean var7, DyeColor var8, BannerPatternLayers var9) {
      renderPatterns(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, false, true);
   }

   public static void renderPatterns(MaterialSet var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, ModelPart var5, Material var6, boolean var7, DyeColor var8, BannerPatternLayers var9, boolean var10, boolean var11) {
      var5.render(var1, var6.buffer(var0, var2, RenderType::entitySolid, var11, var10), var3, var4);
      renderPatternLayer(var0, var1, var2, var3, var4, var5, var7 ? Sheets.BANNER_BASE : Sheets.SHIELD_BASE, var8);

      for(int var12 = 0; var12 < 16 && var12 < var9.layers().size(); ++var12) {
         BannerPatternLayers.Layer var13 = (BannerPatternLayers.Layer)var9.layers().get(var12);
         Material var14 = var7 ? Sheets.getBannerMaterial(var13.pattern()) : Sheets.getShieldMaterial(var13.pattern());
         renderPatternLayer(var0, var1, var2, var3, var4, var5, var14, var13.color());
      }

   }

   private static void renderPatternLayer(MaterialSet var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, ModelPart var5, Material var6, DyeColor var7) {
      int var8 = var7.getTextureDiffuseColor();
      var5.render(var1, var6.buffer(var0, var2, RenderType::entityNoOutline), var3, var4, var8);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.translate(0.5F, 0.0F, 0.5F);
      var2.scale(0.6666667F, -0.6666667F, -0.6666667F);
      this.standingModel.root().getExtentsForGui(var2, var1);
      this.standingFlagModel.setupAnim(0.0F);
      this.standingFlagModel.root().getExtentsForGui(var2, var1);
   }
}
