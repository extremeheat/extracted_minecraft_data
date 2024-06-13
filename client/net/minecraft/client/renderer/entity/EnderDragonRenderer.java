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
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.joml.Matrix4f;

public class EnderDragonRenderer extends EntityRenderer<EnderDragon> {
   public static final ResourceLocation CRYSTAL_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
   private static final ResourceLocation DRAGON_EYES_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
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
         int var11 = FastColor.ARGB32.color(Mth.floor(var10 * 255.0F), -1);
         VertexConsumer var12 = var5.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION));
         this.model.renderToBuffer(var4, var12, var6, OverlayTexture.NO_OVERLAY, var11);
         VertexConsumer var13 = var5.getBuffer(DECAL);
         this.model.renderToBuffer(var4, var13, var6, OverlayTexture.pack(0.0F, var9));
      } else {
         VertexConsumer var20 = var5.getBuffer(RENDER_TYPE);
         this.model.renderToBuffer(var4, var20, var6, OverlayTexture.pack(0.0F, var9));
      }

      VertexConsumer var21 = var5.getBuffer(EYES);
      this.model.renderToBuffer(var4, var21, var6, OverlayTexture.NO_OVERLAY);
      if (var1.dragonDeathTime > 0) {
         float var22 = ((float)var1.dragonDeathTime + var3) / 200.0F;
         float var24 = Math.min(var22 > 0.8F ? (var22 - 0.8F) / 0.2F : 0.0F, 1.0F);
         RandomSource var26 = RandomSource.create(432L);
         VertexConsumer var14 = var5.getBuffer(RenderType.lightning());
         var4.pushPose();
         var4.translate(0.0F, -1.0F, -2.0F);

         for (int var15 = 0; (float)var15 < (var22 + var22 * var22) / 2.0F * 60.0F; var15++) {
            var4.mulPose(Axis.XP.rotationDegrees(var26.nextFloat() * 360.0F));
            var4.mulPose(Axis.YP.rotationDegrees(var26.nextFloat() * 360.0F));
            var4.mulPose(Axis.ZP.rotationDegrees(var26.nextFloat() * 360.0F));
            var4.mulPose(Axis.XP.rotationDegrees(var26.nextFloat() * 360.0F));
            var4.mulPose(Axis.YP.rotationDegrees(var26.nextFloat() * 360.0F));
            var4.mulPose(Axis.ZP.rotationDegrees(var26.nextFloat() * 360.0F + var22 * 90.0F));
            float var16 = var26.nextFloat() * 20.0F + 5.0F + var24 * 10.0F;
            float var17 = var26.nextFloat() * 2.0F + 1.0F + var24 * 2.0F;
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
         float var27 = (float)(var1.nearestCrystal.getZ() - Mth.lerp((double)var3, var1.zo, var1.getZ()));
         renderCrystalBeams(var23, var25 + EndCrystalRenderer.getY(var1.nearestCrystal, var3), var27, var3, var1.tickCount, var4, var5, var6);
         var4.popPose();
      }

      super.render(var1, var2, var3, var4, var5, var6);
   }

   private static void vertex01(VertexConsumer var0, Matrix4f var1, int var2) {
      var0.addVertex(var1, 0.0F, 0.0F, 0.0F).setWhiteAlpha(var2);
   }

   private static void vertex2(VertexConsumer var0, Matrix4f var1, float var2, float var3) {
      var0.addVertex(var1, -HALF_SQRT_3 * var3, var2, -0.5F * var3).setColor(16711935);
   }

   private static void vertex3(VertexConsumer var0, Matrix4f var1, float var2, float var3) {
      var0.addVertex(var1, HALF_SQRT_3 * var3, var2, -0.5F * var3).setColor(16711935);
   }

   private static void vertex4(VertexConsumer var0, Matrix4f var1, float var2, float var3) {
      var0.addVertex(var1, 0.0F, var2, 1.0F * var3).setColor(16711935);
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
         var10.addVertex(var17, var14 * 0.2F, var15 * 0.2F, 0.0F)
            .setColor(-16777216)
            .setUv(var16, var11)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var7)
            .setNormal(var17, 0.0F, -1.0F, 0.0F);
         var10.addVertex(var17, var14, var15, var9)
            .setColor(-1)
            .setUv(var16, var12)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var7)
            .setNormal(var17, 0.0F, -1.0F, 0.0F);
         var10.addVertex(var17, var19, var20, var9)
            .setColor(-1)
            .setUv(var21, var12)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var7)
            .setNormal(var17, 0.0F, -1.0F, 0.0F);
         var10.addVertex(var17, var19 * 0.2F, var20 * 0.2F, 0.0F)
            .setColor(-16777216)
            .setUv(var21, var11)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var7)
            .setNormal(var17, 0.0F, -1.0F, 0.0F);
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
      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, int var5) {
         var1.pushPose();
         float var6 = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
         this.jaw.xRot = (float)(Math.sin((double)(var6 * 6.2831855F)) + 1.0) * 0.2F;
         float var7 = (float)(Math.sin((double)(var6 * 6.2831855F - 1.0F)) + 1.0);
         var7 = (var7 * var7 + var7 * 2.0F) * 0.05F;
         var1.translate(0.0F, var7 - 2.0F, -3.0F);
         var1.mulPose(Axis.XP.rotationDegrees(var7 * 2.0F));
         float var8 = 0.0F;
         float var9 = 20.0F;
         float var10 = -12.0F;
         float var11 = 1.5F;
         double[] var12 = this.entity.getLatencyPos(6, this.a);
         float var13 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] - this.entity.getLatencyPos(10, this.a)[0]));
         float var14 = Mth.wrapDegrees((float)(this.entity.getLatencyPos(5, this.a)[0] + (double)(var13 / 2.0F)));
         float var15 = var6 * 6.2831855F;

         for (int var16 = 0; var16 < 5; var16++) {
            double[] var17 = this.entity.getLatencyPos(5 - var16, this.a);
            float var18 = (float)Math.cos((double)((float)var16 * 0.45F + var15)) * 0.15F;
            this.neck.yRot = Mth.wrapDegrees((float)(var17[0] - var12[0])) * 0.017453292F * 1.5F;
            this.neck.xRot = var18 + this.entity.getHeadPartYOffset(var16, var12, var17) * 0.017453292F * 1.5F * 5.0F;
            this.neck.zRot = -Mth.wrapDegrees((float)(var17[0] - (double)var14)) * 0.017453292F * 1.5F;
            this.neck.y = var9;
            this.neck.z = var10;
            this.neck.x = var8;
            var9 += Mth.sin(this.neck.xRot) * 10.0F;
            var10 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            var8 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            this.neck.render(var1, var2, var3, var4, var5);
         }

         this.head.y = var9;
         this.head.z = var10;
         this.head.x = var8;
         double[] var26 = this.entity.getLatencyPos(0, this.a);
         this.head.yRot = Mth.wrapDegrees((float)(var26[0] - var12[0])) * 0.017453292F;
         this.head.xRot = Mth.wrapDegrees(this.entity.getHeadPartYOffset(6, var12, var26)) * 0.017453292F * 1.5F * 5.0F;
         this.head.zRot = -Mth.wrapDegrees((float)(var26[0] - (double)var14)) * 0.017453292F;
         this.head.render(var1, var2, var3, var4, var5);
         var1.pushPose();
         var1.translate(0.0F, 1.0F, 0.0F);
         var1.mulPose(Axis.ZP.rotationDegrees(-var13 * 1.5F));
         var1.translate(0.0F, -1.0F, 0.0F);
         this.body.zRot = 0.0F;
         this.body.render(var1, var2, var3, var4, var5);
         float var28 = var6 * 6.2831855F;
         this.leftWing.xRot = 0.125F - (float)Math.cos((double)var28) * 0.2F;
         this.leftWing.yRot = -0.25F;
         this.leftWing.zRot = -((float)(Math.sin((double)var28) + 0.125)) * 0.8F;
         this.leftWingTip.zRot = (float)(Math.sin((double)(var28 + 2.0F)) + 0.5) * 0.75F;
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
         this.rightWing.zRot = -this.leftWing.zRot;
         this.rightWingTip.zRot = -this.leftWingTip.zRot;
         this.renderSide(
            var1,
            var2,
            var3,
            var4,
            var7,
            this.leftWing,
            this.leftFrontLeg,
            this.leftFrontLegTip,
            this.leftFrontFoot,
            this.leftRearLeg,
            this.leftRearLegTip,
            this.leftRearFoot,
            var5
         );
         this.renderSide(
            var1,
            var2,
            var3,
            var4,
            var7,
            this.rightWing,
            this.rightFrontLeg,
            this.rightFrontLegTip,
            this.rightFrontFoot,
            this.rightRearLeg,
            this.rightRearLegTip,
            this.rightRearFoot,
            var5
         );
         var1.popPose();
         float var29 = -Mth.sin(var6 * 6.2831855F) * 0.0F;
         var15 = var6 * 6.2831855F;
         var9 = 10.0F;
         var10 = 60.0F;
         var8 = 0.0F;
         var12 = this.entity.getLatencyPos(11, this.a);

         for (int var19 = 0; var19 < 12; var19++) {
            var26 = this.entity.getLatencyPos(12 + var19, this.a);
            var29 += Mth.sin((float)var19 * 0.45F + var15) * 0.05F;
            this.neck.yRot = (Mth.wrapDegrees((float)(var26[0] - var12[0])) * 1.5F + 180.0F) * 0.017453292F;
            this.neck.xRot = var29 + (float)(var26[1] - var12[1]) * 0.017453292F * 1.5F * 5.0F;
            this.neck.zRot = Mth.wrapDegrees((float)(var26[0] - (double)var14)) * 0.017453292F * 1.5F;
            this.neck.y = var9;
            this.neck.z = var10;
            this.neck.x = var8;
            var9 += Mth.sin(this.neck.xRot) * 10.0F;
            var10 -= Mth.cos(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            var8 -= Mth.sin(this.neck.yRot) * Mth.cos(this.neck.xRot) * 10.0F;
            this.neck.render(var1, var2, var3, var4, var5);
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
         int var13
      ) {
         var10.xRot = 1.0F + var5 * 0.1F;
         var11.xRot = 0.5F + var5 * 0.1F;
         var12.xRot = 0.75F + var5 * 0.1F;
         var7.xRot = 1.3F + var5 * 0.1F;
         var8.xRot = -0.5F - var5 * 0.1F;
         var9.xRot = 0.75F + var5 * 0.1F;
         var6.render(var1, var2, var3, var4, var13);
         var7.render(var1, var2, var3, var4, var13);
         var10.render(var1, var2, var3, var4, var13);
      }
   }
}
