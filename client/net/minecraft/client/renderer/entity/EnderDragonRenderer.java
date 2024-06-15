package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.joml.Matrix4f;

public class EnderDragonRenderer extends EntityRenderer<EnderDragon> {
   public static final ResourceLocation CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   private static final ResourceLocation DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
   private static final RenderType DECAL = RenderType.entityDecal(DRAGON_LOCATION);
   private static final RenderType EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
   private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
   private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
   private final EnderDragonRenderer.DragonModel model;

   public EnderDragonRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.model = new EnderDragonRenderer.DragonModel(var1.bakeLayer(ModelLayers.ENDER_DRAGON));
   }

   public void render(EnderDragon var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      float var7 = (float)var1.getLatencyPos(7, var3)[0];
      float var8 = (float)(var1.getLatencyPos(5, var3)[1] - var1.getLatencyPos(10, var3)[1]);
      var4.mulPose(Axis.YP.rotationDegrees(-var7));
      var4.mulPose(Axis.XP.rotationDegrees(var8 * 10.0F));
      var4.translate(0.0F, 0.0F, 1.0F);
      var4.scale(-1.0F, -1.0F, 1.0F);
      var4.translate(0.0F, -1.501F, 0.0F);
      boolean var9 = var1.hurtTime > 0;
      this.model.prepareMobModel(var1, 0.0F, 0.0F, var3);
      if (var1.dragonDeathTime > 0) {
         float var10 = (float)var1.dragonDeathTime / 200.0F;
         VertexConsumer var11 = var5.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION));
         this.model.renderToBuffer(var4, var11, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, var10);
         VertexConsumer var12 = var5.getBuffer(DECAL);
         this.model.renderToBuffer(var4, var12, var6, OverlayTexture.pack(0.0F, var9), 1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         VertexConsumer var20 = var5.getBuffer(RENDER_TYPE);
         this.model.renderToBuffer(var4, var20, var6, OverlayTexture.pack(0.0F, var9), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      VertexConsumer var21 = var5.getBuffer(EYES);
      this.model.renderToBuffer(var4, var21, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      if (var1.dragonDeathTime > 0) {
         float var22 = ((float)var1.dragonDeathTime + var3) / 200.0F;
         float var24 = Math.min(var22 > 0.8F ? (var22 - 0.8F) / 0.2F : 0.0F, 1.0F);
         RandomSource var13 = RandomSource.create(432L);
         VertexConsumer var14 = var5.getBuffer(RenderType.lightning());
         var4.pushPose();
         var4.translate(0.0F, -1.0F, -2.0F);

         for (int var15 = 0; (float)var15 < (var22 + var22 * var22) / 2.0F * 60.0F; var15++) {
            var4.mulPose(Axis.XP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Axis.YP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Axis.ZP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Axis.XP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Axis.YP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Axis.ZP.rotationDegrees(var13.nextFloat() * 360.0F + var22 * 90.0F));
            float var16 = var13.nextFloat() * 20.0F + 5.0F + var24 * 10.0F;
            float var17 = var13.nextFloat() * 2.0F + 1.0F + var24 * 2.0F;
            Matrix4f var18 = var4.last().pose();
            int var19 = (int)(255.0F * (1.0F - var24));
            vertex01(var14, var18, var19);
            vertex2(var14, var18, var16, var17);
            vertex3(var14, var18, var16, var17);
            vertex01(var14, var18, var19);
            vertex3(var14, var18, var16, var17);
            vertex4(var14, var18, var16, var17);
            vertex01(var14, var18, var19);
            vertex4(var14, var18, var16, var17);
            vertex2(var14, var18, var16, var17);
         }

         var4.popPose();
      }

      var4.popPose();
      if (var1.nearestCrystal != null) {
         var4.pushPose();
         float var23 = (float)(var1.nearestCrystal.getX() - Mth.lerp((double)var3, var1.xo, var1.getX()));
         float var25 = (float)(var1.nearestCrystal.getY() - Mth.lerp((double)var3, var1.yo, var1.getY()));
         float var26 = (float)(var1.nearestCrystal.getZ() - Mth.lerp((double)var3, var1.zo, var1.getZ()));
         renderCrystalBeams(var23, var25 + EndCrystalRenderer.getY(var1.nearestCrystal, var3), var26, var3, var1.tickCount, var4, var5, var6);
         var4.popPose();
      }

      super.render(var1, var2, var3, var4, var5, var6);
   }

   private static void vertex01(VertexConsumer var0, Matrix4f var1, int var2) {
      var0.vertex(var1, 0.0F, 0.0F, 0.0F).color(255, 255, 255, var2).endVertex();
   }

   private static void vertex2(VertexConsumer var0, Matrix4f var1, float var2, float var3) {
      var0.vertex(var1, -HALF_SQRT_3 * var3, var2, -0.5F * var3).color(255, 0, 255, 0).endVertex();
   }

   private static void vertex3(VertexConsumer var0, Matrix4f var1, float var2, float var3) {
      var0.vertex(var1, HALF_SQRT_3 * var3, var2, -0.5F * var3).color(255, 0, 255, 0).endVertex();
   }

   private static void vertex4(VertexConsumer var0, Matrix4f var1, float var2, float var3) {
      var0.vertex(var1, 0.0F, var2, 1.0F * var3).color(255, 0, 255, 0).endVertex();
   }

   public static void renderCrystalBeams(float var0, float var1, float var2, float var3, int var4, PoseStack var5, MultiBufferSource var6, int var7) {
      float var8 = Mth.sqrt(var0 * var0 + var2 * var2);
      float var9 = Mth.sqrt(var0 * var0 + var1 * var1 + var2 * var2);
      var5.pushPose();
      var5.translate(0.0F, 2.0F, 0.0F);
      var5.mulPose(Axis.YP.rotation((float)(-Math.atan2((double)var2, (double)var0)) - 1.5707964F));
      var5.mulPose(Axis.XP.rotation((float)(-Math.atan2((double)var8, (double)var1)) - 1.5707964F));
      VertexConsumer var10 = var6.getBuffer(BEAM);
      float var11 = 0.0F - ((float)var4 + var3) * 0.01F;
      float var12 = Mth.sqrt(var0 * var0 + var1 * var1 + var2 * var2) / 32.0F - ((float)var4 + var3) * 0.01F;
      byte var13 = 8;
      float var14 = 0.0F;
      float var15 = 0.75F;
      float var16 = 0.0F;
      PoseStack.Pose var17 = var5.last();

      for (int var18 = 1; var18 <= 8; var18++) {
         float var19 = Mth.sin((float)var18 * 6.2831855F / 8.0F) * 0.75F;
         float var20 = Mth.cos((float)var18 * 6.2831855F / 8.0F) * 0.75F;
         float var21 = (float)var18 / 8.0F;
         var10.vertex(var17, var14 * 0.2F, var15 * 0.2F, 0.0F)
            .color(0, 0, 0, 255)
            .uv(var16, var11)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(var7)
            .normal(var17, 0.0F, -1.0F, 0.0F)
            .endVertex();
         var10.vertex(var17, var14, var15, var9)
            .color(255, 255, 255, 255)
            .uv(var16, var12)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(var7)
            .normal(var17, 0.0F, -1.0F, 0.0F)
            .endVertex();
         var10.vertex(var17, var19, var20, var9)
            .color(255, 255, 255, 255)
            .uv(var21, var12)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(var7)
            .normal(var17, 0.0F, -1.0F, 0.0F)
            .endVertex();
         var10.vertex(var17, var19 * 0.2F, var20 * 0.2F, 0.0F)
            .color(0, 0, 0, 255)
            .uv(var21, var11)
            .overlayCoords(OverlayTexture.NO_OVERLAY)
            .uv2(var7)
            .normal(var17, 0.0F, -1.0F, 0.0F)
            .endVertex();
         var14 = var19;
         var15 = var20;
         var16 = var21;
      }

      var5.popPose();
   }

   public ResourceLocation getTextureLocation(EnderDragon var1) {
      return DRAGON_LOCATION;
   }

   public static LayerDefinition createBodyLayer() {
      MeshDefinition var0 = new MeshDefinition();
      PartDefinition var1 = var0.getRoot();
      float var2 = -16.0F;
      PartDefinition var3 = var1.addOrReplaceChild(
         "head",
         CubeListBuilder.create()
            .addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 176, 44)
            .addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 112, 30)
            .mirror()
            .addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
            .addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0)
            .mirror()
            .addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0, 0)
            .addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 112, 0),
         PartPose.ZERO
      );
      var3.addOrReplaceChild("jaw", CubeListBuilder.create().addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 176, 65), PartPose.offset(0.0F, 4.0F, -8.0F));
      var1.addOrReplaceChild(
         "neck",
         CubeListBuilder.create().addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 192, 104).addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 48, 0),
         PartPose.ZERO
      );
      var1.addOrReplaceChild(
         "body",
         CubeListBuilder.create()
            .addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0, 0)
            .addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 220, 53)
            .addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 220, 53)
            .addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 220, 53),
         PartPose.offset(0.0F, 4.0F, 8.0F)
      );
      PartDefinition var4 = var1.addOrReplaceChild(
         "left_wing",
         CubeListBuilder.create().mirror().addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
         PartPose.offset(12.0F, 5.0F, 2.0F)
      );
      var4.addOrReplaceChild(
         "left_wing_tip",
         CubeListBuilder.create().mirror().addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
         PartPose.offset(56.0F, 0.0F, 0.0F)
      );
      PartDefinition var5 = var1.addOrReplaceChild(
         "left_front_leg", CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104), PartPose.offset(12.0F, 20.0F, 2.0F)
      );
      PartDefinition var6 = var5.addOrReplaceChild(
         "left_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138), PartPose.offset(0.0F, 20.0F, -1.0F)
      );
      var6.addOrReplaceChild(
         "left_front_foot", CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104), PartPose.offset(0.0F, 23.0F, 0.0F)
      );
      PartDefinition var7 = var1.addOrReplaceChild(
         "left_hind_leg", CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0), PartPose.offset(16.0F, 16.0F, 42.0F)
      );
      PartDefinition var8 = var7.addOrReplaceChild(
         "left_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0), PartPose.offset(0.0F, 32.0F, -4.0F)
      );
      var8.addOrReplaceChild(
         "left_hind_foot", CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0), PartPose.offset(0.0F, 31.0F, 4.0F)
      );
      PartDefinition var9 = var1.addOrReplaceChild(
         "right_wing",
         CubeListBuilder.create().addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 112, 88).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 88),
         PartPose.offset(-12.0F, 5.0F, 2.0F)
      );
      var9.addOrReplaceChild(
         "right_wing_tip",
         CubeListBuilder.create().addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 112, 136).addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, -56, 144),
         PartPose.offset(-56.0F, 0.0F, 0.0F)
      );
      PartDefinition var10 = var1.addOrReplaceChild(
         "right_front_leg", CubeListBuilder.create().addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 112, 104), PartPose.offset(-12.0F, 20.0F, 2.0F)
      );
      PartDefinition var11 = var10.addOrReplaceChild(
         "right_front_leg_tip", CubeListBuilder.create().addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 226, 138), PartPose.offset(0.0F, 20.0F, -1.0F)
      );
      var11.addOrReplaceChild(
         "right_front_foot", CubeListBuilder.create().addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 144, 104), PartPose.offset(0.0F, 23.0F, 0.0F)
      );
      PartDefinition var12 = var1.addOrReplaceChild(
         "right_hind_leg", CubeListBuilder.create().addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0, 0), PartPose.offset(-16.0F, 16.0F, 42.0F)
      );
      PartDefinition var13 = var12.addOrReplaceChild(
         "right_hind_leg_tip", CubeListBuilder.create().addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 196, 0), PartPose.offset(0.0F, 32.0F, -4.0F)
      );
      var13.addOrReplaceChild(
         "right_hind_foot", CubeListBuilder.create().addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 112, 0), PartPose.offset(0.0F, 31.0F, 4.0F)
      );
      return LayerDefinition.create(var0, 256, 256);
   }

   public static class DragonModel extends EntityModel<EnderDragon> {
      private final ModelPart head;
      private final ModelPart neck;
      private final ModelPart jaw;
      private final ModelPart body;
      private final ModelPart leftWing;
      private final ModelPart leftWingTip;
      private final ModelPart leftFrontLeg;
      private final ModelPart leftFrontLegTip;
      private final ModelPart leftFrontFoot;
      private final ModelPart leftRearLeg;
      private final ModelPart leftRearLegTip;
      private final ModelPart leftRearFoot;
      private final ModelPart rightWing;
      private final ModelPart rightWingTip;
      private final ModelPart rightFrontLeg;
      private final ModelPart rightFrontLegTip;
      private final ModelPart rightFrontFoot;
      private final ModelPart rightRearLeg;
      private final ModelPart rightRearLegTip;
      private final ModelPart rightRearFoot;
      @Nullable
      private EnderDragon entity;
      private float a;

      public DragonModel(ModelPart var1) {
         super();
         this.head = var1.getChild("head");
         this.jaw = this.head.getChild("jaw");
         this.neck = var1.getChild("neck");
         this.body = var1.getChild("body");
         this.leftWing = var1.getChild("left_wing");
         this.leftWingTip = this.leftWing.getChild("left_wing_tip");
         this.leftFrontLeg = var1.getChild("left_front_leg");
         this.leftFrontLegTip = this.leftFrontLeg.getChild("left_front_leg_tip");
         this.leftFrontFoot = this.leftFrontLegTip.getChild("left_front_foot");
         this.leftRearLeg = var1.getChild("left_hind_leg");
         this.leftRearLegTip = this.leftRearLeg.getChild("left_hind_leg_tip");
         this.leftRearFoot = this.leftRearLegTip.getChild("left_hind_foot");
         this.rightWing = var1.getChild("right_wing");
         this.rightWingTip = this.rightWing.getChild("right_wing_tip");
         this.rightFrontLeg = var1.getChild("right_front_leg");
         this.rightFrontLegTip = this.rightFrontLeg.getChild("right_front_leg_tip");
         this.rightFrontFoot = this.rightFrontLegTip.getChild("right_front_foot");
         this.rightRearLeg = var1.getChild("right_hind_leg");
         this.rightRearLegTip = this.rightRearLeg.getChild("right_hind_leg_tip");
         this.rightRearFoot = this.rightRearLegTip.getChild("right_hind_foot");
      }

      public void prepareMobModel(EnderDragon var1, float var2, float var3, float var4) {
         this.entity = var1;
         this.a = var4;
      }

      public void setupAnim(EnderDragon var1, float var2, float var3, float var4, float var5, float var6) {
      }

      @Override
      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         var1.pushPose();
         float var9 = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
         this.jaw.xRot = (float)(Math.sin((double)(var9 * 6.2831855F)) + 1.0) * 0.2F;
         float var10 = (float)(Math.sin((double)(var9 * 6.2831855F - 1.0F)) + 1.0);
         var10 = (var10 * var10 + var10 * 2.0F) * 0.05F;
         var1.translate(0.0F, var10 - 2.0F, -3.0F);
         var1.mulPose(Axis.XP.rotationDegrees(var10 * 2.0F));
         float var11 = 0.0F;
         float var12 = 20.0F;
         float var13 = -12.0F;
         float var14 = 1.5F;
         double[] var15 = this.entity.getLatencyPos(6, this.a);
         float var16 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] - this.entity.getLatencyPos(10, this.a)[0]));
         float var17 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] + (double)(var16 / 2.0F)));
         float var18 = var9 * 6.2831855F;

         for (int var19 = 0; var19 < 5; var19++) {
            double[] var20 = this.entity.getLatencyPos(5 - var19, this.a);
            float var21 = (float)Math.cos((double)((float)var19 * 0.45F + var18)) * 0.15F;
            this.neck.yRot = Mth.wrapDegrees((float)(var20[0] - var15[0])) * 0.017453292F * 1.5F;
            this.neck.xRot = var21 + this.entity.getHeadPartYOffset(var19, var15, var20) * 0.017453292F * 1.5F * 5.0F;
            this.neck.zRot = -Mth.wrapDegrees((float)(var20[0] - (double)var17)) * 0.017453292F * 1.5F;
            this.neck.y = var12;
            this.neck.z = var13;
            this.neck.x = var11;
            var12 += Mth.sin(this.neck.xRot) * 10.0F;
            var13 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            var11 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            this.neck.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var8);
         }

         this.head.y = var12;
         this.head.z = var13;
         this.head.x = var11;
         double[] var29 = this.entity.getLatencyPos(0, this.a);
         this.head.yRot = Mth.wrapDegrees((float)(var29[0] - var15[0])) * 0.017453292F;
         this.head.xRot = Mth.wrapDegrees(this.entity.getHeadPartYOffset(6, var15, var29)) * 0.017453292F * 1.5F * 5.0F;
         this.head.zRot = -Mth.wrapDegrees((float)(var29[0] - (double)var17)) * 0.017453292F;
         this.head.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var8);
         var1.pushPose();
         var1.translate(0.0F, 1.0F, 0.0F);
         var1.mulPose(Axis.ZP.rotationDegrees(-var16 * 1.5F));
         var1.translate(0.0F, -1.0F, 0.0F);
         this.body.zRot = 0.0F;
         this.body.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var8);
         float var31 = var9 * 6.2831855F;
         this.leftWing.xRot = 0.125F - (float)Math.cos((double)var31) * 0.2F;
         this.leftWing.yRot = -0.25F;
         this.leftWing.zRot = -((float)(Math.sin((double)var31) + 0.125)) * 0.8F;
         this.leftWingTip.zRot = (float)(Math.sin((double)(var31 + 2.0F)) + 0.5) * 0.75F;
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
         this.rightWing.zRot = -this.leftWing.zRot;
         this.rightWingTip.zRot = -this.leftWingTip.zRot;
         this.renderSide(
            var1,
            var2,
            var3,
            var4,
            var10,
            this.leftWing,
            this.leftFrontLeg,
            this.leftFrontLegTip,
            this.leftFrontFoot,
            this.leftRearLeg,
            this.leftRearLegTip,
            this.leftRearFoot,
            var8
         );
         this.renderSide(
            var1,
            var2,
            var3,
            var4,
            var10,
            this.rightWing,
            this.rightFrontLeg,
            this.rightFrontLegTip,
            this.rightFrontFoot,
            this.rightRearLeg,
            this.rightRearLegTip,
            this.rightRearFoot,
            var8
         );
         var1.popPose();
         float var32 = -Mth.sin(var9 * 6.2831855F) * 0.0F;
         var18 = var9 * 6.2831855F;
         var12 = 10.0F;
         var13 = 60.0F;
         var11 = 0.0F;
         var15 = this.entity.getLatencyPos(11, this.a);

         for (int var22 = 0; var22 < 12; var22++) {
            var29 = this.entity.getLatencyPos(12 + var22, this.a);
            var32 += Mth.sin((float)var22 * 0.45F + var18) * 0.05F;
            this.neck.yRot = (Mth.wrapDegrees((float)(var29[0] - var15[0])) * 1.5F + 180.0F) * 0.017453292F;
            this.neck.xRot = var32 + (float)(var29[1] - var15[1]) * 0.017453292F * 1.5F * 5.0F;
            this.neck.zRot = Mth.wrapDegrees((float)(var29[0] - (double)var17)) * 0.017453292F * 1.5F;
            this.neck.y = var12;
            this.neck.z = var13;
            this.neck.x = var11;
            var12 += Mth.sin(this.neck.xRot) * 10.0F;
            var13 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            var11 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            this.neck.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var8);
         }

         var1.popPose();
      }

      private void renderSide(
         PoseStack var1,
         VertexConsumer var2,
         int var3,
         int var4,
         float var5,
         ModelPart var6,
         ModelPart var7,
         ModelPart var8,
         ModelPart var9,
         ModelPart var10,
         ModelPart var11,
         ModelPart var12,
         float var13
      ) {
         var10.xRot = 1.0F + var5 * 0.1F;
         var11.xRot = 0.5F + var5 * 0.1F;
         var12.xRot = 0.75F + var5 * 0.1F;
         var7.xRot = 1.3F + var5 * 0.1F;
         var8.xRot = -0.5F - var5 * 0.1F;
         var9.xRot = 0.75F + var5 * 0.1F;
         var6.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var13);
         var7.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var13);
         var10.render(var1, var2, var3, var4, 1.0F, 1.0F, 1.0F, var13);
      }
   }
}
