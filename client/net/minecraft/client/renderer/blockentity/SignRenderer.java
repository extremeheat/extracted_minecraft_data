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
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class SignRenderer implements BlockEntityRenderer<SignBlockEntity> {
   private static final String STICK = "stick";
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private static final float RENDER_SCALE = 0.6666667F;
   private static final Vec3 TEXT_OFFSET = new Vec3(0.0, 0.3333333432674408, 0.046666666865348816);
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
      SignBlock var8 = (SignBlock)var7.getBlock();
      WoodType var9 = SignBlock.getWoodType(var8);
      SignRenderer.SignModel var10 = this.signModels.get(var9);
      var10.stick.visible = var7.getBlock() instanceof StandingSignBlock;
      this.renderSignWithText(var1, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public float getSignModelRenderScale() {
      return 0.6666667F;
   }

   public float getSignTextRenderScale() {
      return 0.6666667F;
   }

   void renderSignWithText(
      SignBlockEntity var1, PoseStack var2, MultiBufferSource var3, int var4, int var5, BlockState var6, SignBlock var7, WoodType var8, Model var9
   ) {
      var2.pushPose();
      this.translateSign(var2, -var7.getYRotationDegrees(var6), var6);
      this.renderSign(var2, var3, var4, var5, var8, var9);
      this.renderSignText(var1.getBlockPos(), var1.getFrontText(), var2, var3, var4, var1.getTextLineHeight(), var1.getMaxTextLineWidth(), true);
      this.renderSignText(var1.getBlockPos(), var1.getBackText(), var2, var3, var4, var1.getTextLineHeight(), var1.getMaxTextLineWidth(), false);
      var2.popPose();
   }

   void translateSign(PoseStack var1, float var2, BlockState var3) {
      var1.translate(0.5F, 0.75F * this.getSignModelRenderScale(), 0.5F);
      var1.mulPose(Axis.YP.rotationDegrees(var2));
      if (!(var3.getBlock() instanceof StandingSignBlock)) {
         var1.translate(0.0F, -0.3125F, -0.4375F);
      }
   }

   void renderSign(PoseStack var1, MultiBufferSource var2, int var3, int var4, WoodType var5, Model var6) {
      var1.pushPose();
      float var7 = this.getSignModelRenderScale();
      var1.scale(var7, -var7, -var7);
      Material var8 = this.getSignMaterial(var5);
      VertexConsumer var9 = var8.buffer(var2, var6::renderType);
      this.renderSignModel(var1, var3, var4, var6, var9);
      var1.popPose();
   }

   void renderSignModel(PoseStack var1, int var2, int var3, Model var4, VertexConsumer var5) {
      SignRenderer.SignModel var6 = (SignRenderer.SignModel)var4;
      var6.root.render(var1, var5, var2, var3);
   }

   Material getSignMaterial(WoodType var1) {
      return Sheets.getSignMaterial(var1);
   }

   void renderSignText(BlockPos var1, SignText var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, int var7, boolean var8) {
      var3.pushPose();
      this.translateSignText(var3, var8, this.getTextOffset());
      int var9 = getDarkColor(var2);
      int var10 = 4 * var6 / 2;
      FormattedCharSequence[] var11 = var2.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), var2x -> {
         List var3x = this.font.split(var2x, var7);
         return var3x.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)var3x.get(0);
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
            this.font
               .drawInBatch(var16, var17, (float)(var15 * var6 - var10), var12, false, var3.last().pose(), var4, Font.DisplayMode.POLYGON_OFFSET, 0, var14);
         }
      }

      var3.popPose();
   }

   private void translateSignText(PoseStack var1, boolean var2, Vec3 var3) {
      if (!var2) {
         var1.mulPose(Axis.YP.rotationDegrees(180.0F));
      }

      float var4 = 0.015625F * this.getSignTextRenderScale();
      var1.translate(var3.x, var3.y, var3.z);
      var1.scale(var4, -var4, var4);
   }

   Vec3 getTextOffset() {
      return TEXT_OFFSET;
   }

   static boolean isOutlineVisible(BlockPos var0, int var1) {
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
