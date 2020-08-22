package net.minecraft.client.renderer.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.List;
import net.minecraft.client.model.geom.ModelPart;
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

public class BannerRenderer extends BlockEntityRenderer {
   private final ModelPart flag = makeFlag();
   private final ModelPart pole = new ModelPart(64, 64, 44, 0);
   private final ModelPart bar;

   public BannerRenderer(BlockEntityRenderDispatcher var1) {
      super(var1);
      this.pole.addBox(-1.0F, -30.0F, -1.0F, 2.0F, 42.0F, 2.0F, 0.0F);
      this.bar = new ModelPart(64, 64, 0, 42);
      this.bar.addBox(-10.0F, -32.0F, -1.0F, 20.0F, 2.0F, 2.0F, 0.0F);
   }

   public static ModelPart makeFlag() {
      ModelPart var0 = new ModelPart(64, 64, 0, 0);
      var0.addBox(-10.0F, 0.0F, -2.0F, 20.0F, 40.0F, 1.0F, 0.0F);
      return var0;
   }

   public void render(BannerBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      if (var1.getPatterns() != null) {
         float var7 = 0.6666667F;
         boolean var8 = var1.getLevel() == null;
         var3.pushPose();
         long var9;
         if (var8) {
            var9 = 0L;
            var3.translate(0.5D, 0.5D, 0.5D);
            this.pole.visible = !var1.onlyRenderPattern();
         } else {
            var9 = var1.getLevel().getGameTime();
            BlockState var11 = var1.getBlockState();
            float var12;
            if (var11.getBlock() instanceof BannerBlock) {
               var3.translate(0.5D, 0.5D, 0.5D);
               var12 = (float)(-(Integer)var11.getValue(BannerBlock.ROTATION) * 360) / 16.0F;
               var3.mulPose(Vector3f.YP.rotationDegrees(var12));
               this.pole.visible = true;
            } else {
               var3.translate(0.5D, -0.1666666716337204D, 0.5D);
               var12 = -((Direction)var11.getValue(WallBannerBlock.FACING)).toYRot();
               var3.mulPose(Vector3f.YP.rotationDegrees(var12));
               var3.translate(0.0D, -0.3125D, -0.4375D);
               this.pole.visible = false;
            }
         }

         var3.pushPose();
         var3.scale(0.6666667F, -0.6666667F, -0.6666667F);
         VertexConsumer var14 = ModelBakery.BANNER_BASE.buffer(var4, RenderType::entitySolid);
         this.pole.render(var3, var14, var5, var6);
         this.bar.render(var3, var14, var5, var6);
         if (var1.onlyRenderPattern()) {
            this.flag.xRot = 0.0F;
         } else {
            BlockPos var15 = var1.getBlockPos();
            float var13 = ((float)Math.floorMod((long)(var15.getX() * 7 + var15.getY() * 9 + var15.getZ() * 13) + var9, 100L) + var2) / 100.0F;
            this.flag.xRot = (-0.0125F + 0.01F * Mth.cos(6.2831855F * var13)) * 3.1415927F;
         }

         this.flag.y = -32.0F;
         renderPatterns(var1, var3, var4, var5, var6, this.flag, ModelBakery.BANNER_BASE, true);
         var3.popPose();
         var3.popPose();
      }
   }

   public static void renderPatterns(BannerBlockEntity var0, PoseStack var1, MultiBufferSource var2, int var3, int var4, ModelPart var5, Material var6, boolean var7) {
      var5.render(var1, var6.buffer(var2, RenderType::entitySolid), var3, var4);
      List var8 = var0.getPatterns();
      List var9 = var0.getColors();

      for(int var10 = 0; var10 < 17 && var10 < var8.size() && var10 < var9.size(); ++var10) {
         BannerPattern var11 = (BannerPattern)var8.get(var10);
         DyeColor var12 = (DyeColor)var9.get(var10);
         float[] var13 = var12.getTextureDiffuseColors();
         Material var14 = new Material(var7 ? Sheets.BANNER_SHEET : Sheets.SHIELD_SHEET, var11.location(var7));
         var5.render(var1, var14.buffer(var2, RenderType::entityNoOutline), var3, var4, var13[0], var13[1], var13[2], 1.0F);
      }

   }
}
