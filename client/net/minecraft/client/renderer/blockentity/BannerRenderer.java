package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.WallBannerBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.state.BlockState;

public class BannerRenderer implements BlockEntityRenderer<BannerBlockEntity> {
   private final ModelPart flag;
   private final ModelPart pole;
   private final ModelPart bar;

   public BannerRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      ModelPart var2 = var1.getLayer(ModelLayers.BANNER);
      this.flag = var2.getChild("flag");
      this.pole = var2.getChild("pole");
      this.bar = var2.getChild("bar");
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("flag", CubeListBuilder.create().texOffs(0, 0).addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F), PartPose.ZERO);
      var1.addOrReplaceChild("pole", CubeListBuilder.create().texOffs(44, 0).addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild("bar", CubeListBuilder.create().texOffs(0, 42).addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 64);
   }

   public void render(BannerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      List var7 = var1.getPatterns();
      if (var7 != null) {
         float var8 = 0.6666667F;
         boolean var9 = var1.getLevel() == null;
         var3.pushPose();
         long var10;
         if (var9) {
            var10 = 0L;
            var3.translate(0.5D, 0.5D, 0.5D);
            this.pole.visible = true;
         } else {
            var10 = var1.getLevel().getGameTime();
            BlockState var12 = var1.getBlockState();
            float var13;
            if (var12.getBlock() instanceof BannerBlock) {
               var3.translate(0.5D, 0.5D, 0.5D);
               var13 = (float)(-(Integer)var12.getValue(BannerBlock.ROTATION) * 360) / 16.0F;
               var3.mulPose(Vector3f.YP.rotationDegrees(var13));
               this.pole.visible = true;
            } else {
               var3.translate(0.5D, -0.1666666716337204D, 0.5D);
               var13 = -((Direction)var12.getValue(WallBannerBlock.FACING)).toYRot();
               var3.mulPose(Vector3f.YP.rotationDegrees(var13));
               var3.translate(0.0D, -0.3125D, -0.4375D);
               this.pole.visible = false;
            }
         }

         var3.pushPose();
         var3.scale(0.6666667F, -0.6666667F, -0.6666667F);
         VertexConsumer var15 = ModelBakery.BANNER_BASE.buffer(var4, RenderType::entitySolid);
         this.pole.render(var3, var15, var5, var6);
         this.bar.render(var3, var15, var5, var6);
         BlockPos var16 = var1.getBlockPos();
         float var14 = ((float)Math.floorMod((long)(var16.getX() * 7 + var16.getY() * 9 + var16.getZ() * 13) + var10, 100L) + var2) / 100.0F;
         this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(6.2831855F * var14)) * 3.1415927F;
         this.flag.y = -32.0F;
         renderPatterns(var3, var4, var5, var6, this.flag, ModelBakery.BANNER_BASE, true, var7);
         var3.popPose();
         var3.popPose();
      }
   }

   public static void renderPatterns(PoseStack var0, MultiBufferSource var1, int var2, int var3, ModelPart var4, Material var5, boolean var6, List<Pair<BannerPattern, DyeColor>> var7) {
      renderPatterns(var0, var1, var2, var3, var4, var5, var6, var7, false);
   }

   public static void renderPatterns(PoseStack var0, MultiBufferSource var1, int var2, int var3, ModelPart var4, Material var5, boolean var6, List<Pair<BannerPattern, DyeColor>> var7, boolean var8) {
      var4.render(var0, var5.buffer(var1, RenderType::entitySolid, var8), var2, var3);

      for(int var9 = 0; var9 < 17 && var9 < var7.size(); ++var9) {
         Pair var10 = (Pair)var7.get(var9);
         float[] var11 = ((DyeColor)var10.getSecond()).getTextureDiffuseColors();
         Material var12 = new Material(var6 ? Sheets.BANNER_SHEET : Sheets.SHIELD_SHEET, ((BannerPattern)var10.getFirst()).location(var6));
         var4.render(var0, var12.buffer(var1, RenderType::entityNoOutline), var2, var3, var11[0], var11[1], var11[2], 1.0F);
      }

   }
}
