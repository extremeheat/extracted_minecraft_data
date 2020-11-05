package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Map;
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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class SignRenderer implements BlockEntityRenderer<SignBlockEntity> {
   private final Map<WoodType, SignRenderer.SignModel> signModels;
   private final Font font;

   public SignRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.signModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var1x) -> {
         return new SignRenderer.SignModel(var1.getLayer(ModelLayers.createSignModelName(var1x)));
      }));
      this.font = var1.getFont();
   }

   public void render(SignBlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4, int var5, int var6) {
      BlockState var7 = var1.getBlockState();
      var3.pushPose();
      float var8 = 0.6666667F;
      WoodType var9 = getWoodType(var7.getBlock());
      SignRenderer.SignModel var10 = (SignRenderer.SignModel)this.signModels.get(var9);
      float var11;
      if (var7.getBlock() instanceof StandingSignBlock) {
         var3.translate(0.5D, 0.5D, 0.5D);
         var11 = -((float)((Integer)var7.getValue(StandingSignBlock.ROTATION) * 360) / 16.0F);
         var3.mulPose(Vector3f.YP.rotationDegrees(var11));
         var10.stick.visible = true;
      } else {
         var3.translate(0.5D, 0.5D, 0.5D);
         var11 = -((Direction)var7.getValue(WallSignBlock.FACING)).toYRot();
         var3.mulPose(Vector3f.YP.rotationDegrees(var11));
         var3.translate(0.0D, -0.3125D, -0.4375D);
         var10.stick.visible = false;
      }

      var3.pushPose();
      var3.scale(0.6666667F, -0.6666667F, -0.6666667F);
      Material var25 = Sheets.signTexture(var9);
      var10.getClass();
      VertexConsumer var12 = var25.buffer(var4, var10::renderType);
      var10.root.render(var3, var12, var5, var6);
      var3.popPose();
      float var13 = 0.010416667F;
      var3.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
      var3.scale(0.010416667F, -0.010416667F, 0.010416667F);
      int var14 = var1.getColor().getTextColor();
      double var15 = 0.4D;
      int var17 = (int)((double)NativeImage.getR(var14) * 0.4D);
      int var18 = (int)((double)NativeImage.getG(var14) * 0.4D);
      int var19 = (int)((double)NativeImage.getB(var14) * 0.4D);
      int var20 = NativeImage.combine(0, var19, var18, var17);
      boolean var21 = true;

      for(int var22 = 0; var22 < 4; ++var22) {
         FormattedCharSequence var23 = var1.getRenderMessage(var22, (var1x) -> {
            List var2 = this.font.split(var1x, 90);
            return var2.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)var2.get(0);
         });
         if (var23 != null) {
            float var24 = (float)(-this.font.width(var23) / 2);
            this.font.drawInBatch((FormattedCharSequence)var23, var24, (float)(var22 * 10 - 20), var20, false, var3.last().pose(), var4, false, 0, var5);
         }
      }

      var3.popPose();
   }

   public static WoodType getWoodType(Block var0) {
      WoodType var1;
      if (var0 instanceof SignBlock) {
         var1 = ((SignBlock)var0).type();
      } else {
         var1 = WoodType.OAK;
      }

      return var1;
   }

   public static SignRenderer.SignModel createSignModel(EntityModelSet var0, WoodType var1) {
      return new SignRenderer.SignModel(var0.getLayer(ModelLayers.createSignModelName(var1)));
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

      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         this.root.render(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
