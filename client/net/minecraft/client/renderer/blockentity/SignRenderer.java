package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class SignRenderer implements BlockEntityRenderer<SignBlockEntity> {
   private static final String STICK = "stick";
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private final Map<WoodType, SignRenderer.SignModel> signModels;
   private final Font font;

   public SignRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.signModels = WoodType.values()
         .collect(ImmutableMap.toImmutableMap(var0 -> var0, var1x -> new SignRenderer.SignModel(var1.bakeLayer(ModelLayers.createSignModelName(var1x)))));
      this.font = var1.getFont();
   }

   public void render(SignBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var7 = var1.getBlockState();
      var3.pushPose();
      float var8 = 0.6666667F;
      WoodType var9 = SignBlock.getWoodType(var7.getBlock());
      SignRenderer.SignModel var10 = this.signModels.get(var9);
      if (var7.getBlock() instanceof StandingSignBlock) {
         var3.translate(0.5F, 0.5F, 0.5F);
         float var11 = -RotationSegment.convertToDegrees(var7.getValue(StandingSignBlock.ROTATION));
         var3.mulPose(Axis.YP.rotationDegrees(var11));
         var10.stick.visible = true;
      } else {
         var3.translate(0.5F, 0.5F, 0.5F);
         float var12 = -var7.getValue(WallSignBlock.FACING).toYRot();
         var3.mulPose(Axis.YP.rotationDegrees(var12));
         var3.translate(0.0F, -0.3125F, -0.4375F);
         var10.stick.visible = false;
      }

      this.renderSign(var3, var4, var5, var6, 0.6666667F, var9, var10);
      this.renderSignText(var1, var3, var4, var5, 0.6666667F);
   }

   void renderSign(PoseStack var1, MultiBufferSource var2, int var3, int var4, float var5, WoodType var6, Model var7) {
      var1.pushPose();
      var1.scale(var5, -var5, -var5);
      Material var8 = this.getSignMaterial(var6);
      VertexConsumer var9 = var8.buffer(var2, var7::renderType);
      this.renderSignModel(var1, var3, var4, var7, var9);
      var1.popPose();
   }

   void renderSignModel(PoseStack var1, int var2, int var3, Model var4, VertexConsumer var5) {
      SignRenderer.SignModel var6 = (SignRenderer.SignModel)var4;
      var6.root.render(var1, var5, var2, var3);
   }

   Material getSignMaterial(WoodType var1) {
      return Sheets.getSignMaterial(var1);
   }

   void renderSignText(SignBlockEntity var1, PoseStack var2, MultiBufferSource var3, int var4, float var5) {
      float var6 = 0.015625F * var5;
      Vec3 var7 = this.getTextOffset(var5);
      var2.translate(var7.x, var7.y, var7.z);
      var2.scale(var6, -var6, var6);
      int var8 = getDarkColor(var1);
      int var9 = 4 * var1.getTextLineHeight() / 2;
      FormattedCharSequence[] var10 = var1.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), var2x -> {
         List var3x = this.font.split(var2x, var1.getMaxTextLineWidth());
         return var3x.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)var3x.get(0);
      });
      int var11;
      boolean var12;
      int var13;
      if (var1.hasGlowingText()) {
         var11 = var1.getColor().getTextColor();
         var12 = isOutlineVisible(var1, var11);
         var13 = 15728880;
      } else {
         var11 = var8;
         var12 = false;
         var13 = var4;
      }

      for(int var14 = 0; var14 < 4; ++var14) {
         FormattedCharSequence var15 = var10[var14];
         float var16 = (float)(-this.font.width(var15) / 2);
         if (var12) {
            this.font.drawInBatch8xOutline(var15, var16, (float)(var14 * var1.getTextLineHeight() - var9), var11, var8, var2.last().pose(), var3, var13);
         } else {
            this.font
               .drawInBatch(
                  var15, var16, (float)(var14 * var1.getTextLineHeight() - var9), var11, false, var2.last().pose(), var3, Font.DisplayMode.NORMAL, 0, var13
               );
         }
      }

      var2.popPose();
   }

   Vec3 getTextOffset(float var1) {
      return new Vec3(0.0, (double)(0.5F * var1), (double)(0.07F * var1));
   }

   static boolean isOutlineVisible(SignBlockEntity var0, int var1) {
      if (var1 == DyeColor.BLACK.getTextColor()) {
         return true;
      } else {
         Minecraft var2 = Minecraft.getInstance();
         LocalPlayer var3 = var2.player;
         if (var3 != null && var2.options.getCameraType().isFirstPerson() && var3.isScoping()) {
            return true;
         } else {
            Entity var4 = var2.getCameraEntity();
            return var4 != null && var4.distanceToSqr(Vec3.atCenterOf(var0.getBlockPos())) < (double)OUTLINE_RENDER_DISTANCE;
         }
      }
   }

   static int getDarkColor(SignBlockEntity var0) {
      int var1 = var0.getColor().getTextColor();
      if (var1 == DyeColor.BLACK.getTextColor() && var0.hasGlowingText()) {
         return -988212;
      } else {
         double var2 = 0.4;
         int var4 = (int)((double)FastColor.ARGB32.red(var1) * 0.4);
         int var5 = (int)((double)FastColor.ARGB32.green(var1) * 0.4);
         int var6 = (int)((double)FastColor.ARGB32.blue(var1) * 0.4);
         return FastColor.ARGB32.color(0, var4, var5, var6);
      }
   }

   public static SignRenderer.SignModel createSignModel(EntityModelSet var0, WoodType var1) {
      return new SignRenderer.SignModel(var0.bakeLayer(ModelLayers.createSignModelName(var1)));
   }

   public static LayerDefinition createSignLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      var1.addOrReplaceChild("sign", CubeListBuilder.create().texOffs(0, 0).addBox(-12.0F, -14.0F, -1.0F, 24.0F, 12.0F, 2.0F), PartPose.ZERO);
      var1.addOrReplaceChild("stick", CubeListBuilder.create().texOffs(0, 14).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 14.0F, 2.0F), PartPose.ZERO);
      return LayerDefinition.create(var0, 64, 32);
   }

   public static final class SignModel extends Model {
      public final ModelPart root;
      public final ModelPart stick;

      public SignModel(ModelPart var1) {
         super(RenderType::entityCutoutNoCull);
         this.root = var1;
         this.stick = var1.getChild("stick");
      }

      @Override
      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         this.root.render(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
