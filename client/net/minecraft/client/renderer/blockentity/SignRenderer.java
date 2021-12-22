package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.minecraft.world.phys.Vec3;

public class SignRenderer implements BlockEntityRenderer<SignBlockEntity> {
   public static final int MAX_LINE_WIDTH = 90;
   private static final int LINE_HEIGHT = 10;
   private static final String STICK = "stick";
   private static final int BLACK_TEXT_OUTLINE_COLOR = -988212;
   private static final int OUTLINE_RENDER_DISTANCE = Mth.square(16);
   private final Map<WoodType, SignRenderer.SignModel> signModels;
   private final Font font;

   public SignRenderer(BlockEntityRendererProvider.Context var1) {
      super();
      this.signModels = (Map)WoodType.values().collect(ImmutableMap.toImmutableMap((var0) -> {
         return var0;
      }, (var1x) -> {
         return new SignRenderer.SignModel(var1.bakeLayer(ModelLayers.createSignModelName(var1x)));
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
         var3.mulPose(Vector3f.field_292.rotationDegrees(var11));
         var10.stick.visible = true;
      } else {
         var3.translate(0.5D, 0.5D, 0.5D);
         var11 = -((Direction)var7.getValue(WallSignBlock.FACING)).toYRot();
         var3.mulPose(Vector3f.field_292.rotationDegrees(var11));
         var3.translate(0.0D, -0.3125D, -0.4375D);
         var10.stick.visible = false;
      }

      var3.pushPose();
      var3.scale(0.6666667F, -0.6666667F, -0.6666667F);
      Material var23 = Sheets.getSignMaterial(var9);
      Objects.requireNonNull(var10);
      VertexConsumer var12 = var23.buffer(var4, var10::renderType);
      var10.root.render(var3, var12, var5, var6);
      var3.popPose();
      float var13 = 0.010416667F;
      var3.translate(0.0D, 0.3333333432674408D, 0.046666666865348816D);
      var3.scale(0.010416667F, -0.010416667F, 0.010416667F);
      int var14 = getDarkColor(var1);
      boolean var15 = true;
      FormattedCharSequence[] var16 = var1.getRenderMessages(Minecraft.getInstance().isTextFilteringEnabled(), (var1x) -> {
         List var2 = this.font.split(var1x, 90);
         return var2.isEmpty() ? FormattedCharSequence.EMPTY : (FormattedCharSequence)var2.get(0);
      });
      int var17;
      boolean var18;
      int var19;
      if (var1.hasGlowingText()) {
         var17 = var1.getColor().getTextColor();
         var18 = isOutlineVisible(var1, var17);
         var19 = 15728880;
      } else {
         var17 = var14;
         var18 = false;
         var19 = var5;
      }

      for(int var20 = 0; var20 < 4; ++var20) {
         FormattedCharSequence var21 = var16[var20];
         float var22 = (float)(-this.font.width(var21) / 2);
         if (var18) {
            this.font.drawInBatch8xOutline(var21, var22, (float)(var20 * 10 - 20), var17, var14, var3.last().pose(), var4, var19);
         } else {
            this.font.drawInBatch((FormattedCharSequence)var21, var22, (float)(var20 * 10 - 20), var17, false, var3.last().pose(), var4, false, 0, var19);
         }
      }

      var3.popPose();
   }

   private static boolean isOutlineVisible(SignBlockEntity var0, int var1) {
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

   private static int getDarkColor(SignBlockEntity var0) {
      int var1 = var0.getColor().getTextColor();
      double var2 = 0.4D;
      int var4 = (int)((double)NativeImage.getR(var1) * 0.4D);
      int var5 = (int)((double)NativeImage.getG(var1) * 0.4D);
      int var6 = (int)((double)NativeImage.getB(var1) * 0.4D);
      return var1 == DyeColor.BLACK.getTextColor() && var0.hasGlowingText() ? -988212 : NativeImage.combine(0, var6, var5, var4);
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

      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         this.root.render(var1, var2, var3, var4, var5, var6, var7, var8);
      }
   }
}
