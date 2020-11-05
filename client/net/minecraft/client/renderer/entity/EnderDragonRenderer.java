package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;

public class EnderDragonRenderer extends EntityRenderer<EnderDragon> {
   public static final ResourceLocation CRYSTAL_BEAM_LOCATION = new ResourceLocation("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon.png");
   private static final ResourceLocation DRAGON_EYES_LOCATION = new ResourceLocation("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderType RENDER_TYPE;
   private static final RenderType DECAL;
   private static final RenderType EYES;
   private static final RenderType BEAM;
   private static final float HALF_SQRT_3;
   private final EnderDragonRenderer.DragonModel model = new EnderDragonRenderer.DragonModel();

   public EnderDragonRenderer(EntityRenderDispatcher var1) {
      super(var1);
      this.shadowRadius = 0.5F;
   }

   public void render(EnderDragon var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      var4.pushPose();
      float var7 = (float)var1.getLatencyPos(7, var3)[0];
      float var8 = (float)(var1.getLatencyPos(5, var3)[1] - var1.getLatencyPos(10, var3)[1]);
      var4.mulPose(Vector3f.YP.rotationDegrees(-var7));
      var4.mulPose(Vector3f.XP.rotationDegrees(var8 * 10.0F));
      var4.translate(0.0D, 0.0D, 1.0D);
      var4.scale(-1.0F, -1.0F, 1.0F);
      var4.translate(0.0D, -1.5010000467300415D, 0.0D);
      boolean var9 = var1.hurtTime > 0;
      this.model.prepareMobModel(var1, 0.0F, 0.0F, var3);
      VertexConsumer var20;
      if (var1.dragonDeathTime > 0) {
         float var10 = (float)var1.dragonDeathTime / 200.0F;
         VertexConsumer var11 = var5.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION, var10));
         this.model.renderToBuffer(var4, var11, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
         VertexConsumer var12 = var5.getBuffer(DECAL);
         this.model.renderToBuffer(var4, var12, var6, OverlayTexture.pack(0.0F, var9), 1.0F, 1.0F, 1.0F, 1.0F);
      } else {
         var20 = var5.getBuffer(RENDER_TYPE);
         this.model.renderToBuffer(var4, var20, var6, OverlayTexture.pack(0.0F, var9), 1.0F, 1.0F, 1.0F, 1.0F);
      }

      var20 = var5.getBuffer(EYES);
      this.model.renderToBuffer(var4, var20, var6, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      float var21;
      float var22;
      if (var1.dragonDeathTime > 0) {
         var21 = ((float)var1.dragonDeathTime + var3) / 200.0F;
         var22 = Math.min(var21 > 0.8F ? (var21 - 0.8F) / 0.2F : 0.0F, 1.0F);
         Random var13 = new Random(432L);
         VertexConsumer var14 = var5.getBuffer(RenderType.lightning());
         var4.pushPose();
         var4.translate(0.0D, -1.0D, -2.0D);

         for(int var15 = 0; (float)var15 < (var21 + var21 * var21) / 2.0F * 60.0F; ++var15) {
            var4.mulPose(Vector3f.XP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Vector3f.YP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Vector3f.ZP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Vector3f.XP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Vector3f.YP.rotationDegrees(var13.nextFloat() * 360.0F));
            var4.mulPose(Vector3f.ZP.rotationDegrees(var13.nextFloat() * 360.0F + var21 * 90.0F));
            float var16 = var13.nextFloat() * 20.0F + 5.0F + var22 * 10.0F;
            float var17 = var13.nextFloat() * 2.0F + 1.0F + var22 * 2.0F;
            Matrix4f var18 = var4.last().pose();
            int var19 = (int)(255.0F * (1.0F - var22));
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
         var21 = (float)(var1.nearestCrystal.getX() - Mth.lerp((double)var3, var1.xo, var1.getX()));
         var22 = (float)(var1.nearestCrystal.getY() - Mth.lerp((double)var3, var1.yo, var1.getY()));
         float var23 = (float)(var1.nearestCrystal.getZ() - Mth.lerp((double)var3, var1.zo, var1.getZ()));
         renderCrystalBeams(var21, var22 + EndCrystalRenderer.getY(var1.nearestCrystal, var3), var23, var3, var1.tickCount, var4, var5, var6);
         var4.popPose();
      }

      super.render(var1, var2, var3, var4, var5, var6);
   }

   private static void vertex01(VertexConsumer var0, Matrix4f var1, int var2) {
      var0.vertex(var1, 0.0F, 0.0F, 0.0F).color(255, 255, 255, var2).endVertex();
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
      var5.translate(0.0D, 2.0D, 0.0D);
      var5.mulPose(Vector3f.YP.rotation((float)(-Math.atan2((double)var2, (double)var0)) - 1.5707964F));
      var5.mulPose(Vector3f.XP.rotation((float)(-Math.atan2((double)var8, (double)var1)) - 1.5707964F));
      VertexConsumer var10 = var6.getBuffer(BEAM);
      float var11 = 0.0F - ((float)var4 + var3) * 0.01F;
      float var12 = Mth.sqrt(var0 * var0 + var1 * var1 + var2 * var2) / 32.0F - ((float)var4 + var3) * 0.01F;
      boolean var13 = true;
      float var14 = 0.0F;
      float var15 = 0.75F;
      float var16 = 0.0F;
      PoseStack.Pose var17 = var5.last();
      Matrix4f var18 = var17.pose();
      Matrix3f var19 = var17.normal();

      for(int var20 = 1; var20 <= 8; ++var20) {
         float var21 = Mth.sin((float)var20 * 6.2831855F / 8.0F) * 0.75F;
         float var22 = Mth.cos((float)var20 * 6.2831855F / 8.0F) * 0.75F;
         float var23 = (float)var20 / 8.0F;
         var10.vertex(var18, var14 * 0.2F, var15 * 0.2F, 0.0F).color(0, 0, 0, 255).uv(var16, var11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var7).normal(var19, 0.0F, -1.0F, 0.0F).endVertex();
         var10.vertex(var18, var14, var15, var9).color(255, 255, 255, 255).uv(var16, var12).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var7).normal(var19, 0.0F, -1.0F, 0.0F).endVertex();
         var10.vertex(var18, var21, var22, var9).color(255, 255, 255, 255).uv(var23, var12).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var7).normal(var19, 0.0F, -1.0F, 0.0F).endVertex();
         var10.vertex(var18, var21 * 0.2F, var22 * 0.2F, 0.0F).color(0, 0, 0, 255).uv(var23, var11).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(var7).normal(var19, 0.0F, -1.0F, 0.0F).endVertex();
         var14 = var21;
         var15 = var22;
         var16 = var23;
      }

      var5.popPose();
   }

   public ResourceLocation getTextureLocation(EnderDragon var1) {
      return DRAGON_LOCATION;
   }

   static {
      RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
      DECAL = RenderType.entityDecal(DRAGON_LOCATION);
      EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
      BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
      HALF_SQRT_3 = (float)(Math.sqrt(3.0D) / 2.0D);
   }

   public static class DragonModel extends EntityModel<EnderDragon> {
      private final ModelPart head;
      private final ModelPart neck;
      private final ModelPart jaw;
      private final ModelPart body;
      private ModelPart leftWing;
      private ModelPart leftWingTip;
      private ModelPart leftFrontLeg;
      private ModelPart leftFrontLegTip;
      private ModelPart leftFrontFoot;
      private ModelPart leftRearLeg;
      private ModelPart leftRearLegTip;
      private ModelPart leftRearFoot;
      private ModelPart rightWing;
      private ModelPart rightWingTip;
      private ModelPart rightFrontLeg;
      private ModelPart rightFrontLegTip;
      private ModelPart rightFrontFoot;
      private ModelPart rightRearLeg;
      private ModelPart rightRearLegTip;
      private ModelPart rightRearFoot;
      @Nullable
      private EnderDragon entity;
      private float a;

      public DragonModel() {
         super();
         this.texWidth = 256;
         this.texHeight = 256;
         float var1 = -16.0F;
         this.head = new ModelPart(this);
         this.head.addBox("upperlip", -6.0F, -1.0F, -24.0F, 12, 5, 16, 0.0F, 176, 44);
         this.head.addBox("upperhead", -8.0F, -8.0F, -10.0F, 16, 16, 16, 0.0F, 112, 30);
         this.head.mirror = true;
         this.head.addBox("scale", -5.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
         this.head.addBox("nostril", -5.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
         this.head.mirror = false;
         this.head.addBox("scale", 3.0F, -12.0F, -4.0F, 2, 4, 6, 0.0F, 0, 0);
         this.head.addBox("nostril", 3.0F, -3.0F, -22.0F, 2, 2, 4, 0.0F, 112, 0);
         this.jaw = new ModelPart(this);
         this.jaw.setPos(0.0F, 4.0F, -8.0F);
         this.jaw.addBox("jaw", -6.0F, 0.0F, -16.0F, 12, 4, 16, 0.0F, 176, 65);
         this.head.addChild(this.jaw);
         this.neck = new ModelPart(this);
         this.neck.addBox("box", -5.0F, -5.0F, -5.0F, 10, 10, 10, 0.0F, 192, 104);
         this.neck.addBox("scale", -1.0F, -9.0F, -3.0F, 2, 4, 6, 0.0F, 48, 0);
         this.body = new ModelPart(this);
         this.body.setPos(0.0F, 4.0F, 8.0F);
         this.body.addBox("body", -12.0F, 0.0F, -16.0F, 24, 24, 64, 0.0F, 0, 0);
         this.body.addBox("scale", -1.0F, -6.0F, -10.0F, 2, 6, 12, 0.0F, 220, 53);
         this.body.addBox("scale", -1.0F, -6.0F, 10.0F, 2, 6, 12, 0.0F, 220, 53);
         this.body.addBox("scale", -1.0F, -6.0F, 30.0F, 2, 6, 12, 0.0F, 220, 53);
         this.leftWing = new ModelPart(this);
         this.leftWing.mirror = true;
         this.leftWing.setPos(12.0F, 5.0F, 2.0F);
         this.leftWing.addBox("bone", 0.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
         this.leftWing.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
         this.leftWingTip = new ModelPart(this);
         this.leftWingTip.mirror = true;
         this.leftWingTip.setPos(56.0F, 0.0F, 0.0F);
         this.leftWingTip.addBox("bone", 0.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
         this.leftWingTip.addBox("skin", 0.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
         this.leftWing.addChild(this.leftWingTip);
         this.leftFrontLeg = new ModelPart(this);
         this.leftFrontLeg.setPos(12.0F, 20.0F, 2.0F);
         this.leftFrontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
         this.leftFrontLegTip = new ModelPart(this);
         this.leftFrontLegTip.setPos(0.0F, 20.0F, -1.0F);
         this.leftFrontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
         this.leftFrontLeg.addChild(this.leftFrontLegTip);
         this.leftFrontFoot = new ModelPart(this);
         this.leftFrontFoot.setPos(0.0F, 23.0F, 0.0F);
         this.leftFrontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
         this.leftFrontLegTip.addChild(this.leftFrontFoot);
         this.leftRearLeg = new ModelPart(this);
         this.leftRearLeg.setPos(16.0F, 16.0F, 42.0F);
         this.leftRearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
         this.leftRearLegTip = new ModelPart(this);
         this.leftRearLegTip.setPos(0.0F, 32.0F, -4.0F);
         this.leftRearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
         this.leftRearLeg.addChild(this.leftRearLegTip);
         this.leftRearFoot = new ModelPart(this);
         this.leftRearFoot.setPos(0.0F, 31.0F, 4.0F);
         this.leftRearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
         this.leftRearLegTip.addChild(this.leftRearFoot);
         this.rightWing = new ModelPart(this);
         this.rightWing.setPos(-12.0F, 5.0F, 2.0F);
         this.rightWing.addBox("bone", -56.0F, -4.0F, -4.0F, 56, 8, 8, 0.0F, 112, 88);
         this.rightWing.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 88);
         this.rightWingTip = new ModelPart(this);
         this.rightWingTip.setPos(-56.0F, 0.0F, 0.0F);
         this.rightWingTip.addBox("bone", -56.0F, -2.0F, -2.0F, 56, 4, 4, 0.0F, 112, 136);
         this.rightWingTip.addBox("skin", -56.0F, 0.0F, 2.0F, 56, 0, 56, 0.0F, -56, 144);
         this.rightWing.addChild(this.rightWingTip);
         this.rightFrontLeg = new ModelPart(this);
         this.rightFrontLeg.setPos(-12.0F, 20.0F, 2.0F);
         this.rightFrontLeg.addBox("main", -4.0F, -4.0F, -4.0F, 8, 24, 8, 0.0F, 112, 104);
         this.rightFrontLegTip = new ModelPart(this);
         this.rightFrontLegTip.setPos(0.0F, 20.0F, -1.0F);
         this.rightFrontLegTip.addBox("main", -3.0F, -1.0F, -3.0F, 6, 24, 6, 0.0F, 226, 138);
         this.rightFrontLeg.addChild(this.rightFrontLegTip);
         this.rightFrontFoot = new ModelPart(this);
         this.rightFrontFoot.setPos(0.0F, 23.0F, 0.0F);
         this.rightFrontFoot.addBox("main", -4.0F, 0.0F, -12.0F, 8, 4, 16, 0.0F, 144, 104);
         this.rightFrontLegTip.addChild(this.rightFrontFoot);
         this.rightRearLeg = new ModelPart(this);
         this.rightRearLeg.setPos(-16.0F, 16.0F, 42.0F);
         this.rightRearLeg.addBox("main", -8.0F, -4.0F, -8.0F, 16, 32, 16, 0.0F, 0, 0);
         this.rightRearLegTip = new ModelPart(this);
         this.rightRearLegTip.setPos(0.0F, 32.0F, -4.0F);
         this.rightRearLegTip.addBox("main", -6.0F, -2.0F, 0.0F, 12, 32, 12, 0.0F, 196, 0);
         this.rightRearLeg.addChild(this.rightRearLegTip);
         this.rightRearFoot = new ModelPart(this);
         this.rightRearFoot.setPos(0.0F, 31.0F, 4.0F);
         this.rightRearFoot.addBox("main", -9.0F, 0.0F, -20.0F, 18, 6, 24, 0.0F, 112, 0);
         this.rightRearLegTip.addChild(this.rightRearFoot);
      }

      public void prepareMobModel(EnderDragon var1, float var2, float var3, float var4) {
         this.entity = var1;
         this.a = var4;
      }

      public void setupAnim(EnderDragon var1, float var2, float var3, float var4, float var5, float var6) {
      }

      public void renderToBuffer(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         var1.pushPose();
         float var9 = Mth.lerp(this.a, this.entity.oFlapTime, this.entity.flapTime);
         this.jaw.xRot = (float)(Math.sin((double)(var9 * 6.2831855F)) + 1.0D) * 0.2F;
         float var10 = (float)(Math.sin((double)(var9 * 6.2831855F - 1.0F)) + 1.0D);
         var10 = (var10 * var10 + var10 * 2.0F) * 0.05F;
         var1.translate(0.0D, (double)(var10 - 2.0F), -3.0D);
         var1.mulPose(Vector3f.XP.rotationDegrees(var10 * 2.0F));
         float var11 = 0.0F;
         float var12 = 20.0F;
         float var13 = -12.0F;
         float var14 = 1.5F;
         double[] var15 = this.entity.getLatencyPos(6, this.a);
         float var16 = Mth.rotWrap(this.entity.getLatencyPos(5, this.a)[0] - this.entity.getLatencyPos(10, this.a)[0]);
         float var17 = Mth.rotWrap(this.entity.getLatencyPos(5, this.a)[0] + (double)(var16 / 2.0F));
         float var18 = var9 * 6.2831855F;

         float var21;
         for(int var19 = 0; var19 < 5; ++var19) {
            double[] var20 = this.entity.getLatencyPos(5 - var19, this.a);
            var21 = (float)Math.cos((double)((float)var19 * 0.45F + var18)) * 0.15F;
            this.neck.yRot = Mth.rotWrap(var20[0] - var15[0]) * 0.017453292F * 1.5F;
            this.neck.xRot = var21 + this.entity.getHeadPartYOffset(var19, var15, var20) * 0.017453292F * 1.5F * 5.0F;
            this.neck.zRot = -Mth.rotWrap(var20[0] - (double)var17) * 0.017453292F * 1.5F;
            this.neck.y = var12;
            this.neck.z = var13;
            this.neck.x = var11;
            var12 = (float)((double)var12 + Math.sin((double)this.neck.xRot) * 10.0D);
            var13 = (float)((double)var13 - Math.cos((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
            var11 = (float)((double)var11 - Math.sin((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
            this.neck.render(var1, var2, var3, var4);
         }

         this.head.y = var12;
         this.head.z = var13;
         this.head.x = var11;
         double[] var23 = this.entity.getLatencyPos(0, this.a);
         this.head.yRot = Mth.rotWrap(var23[0] - var15[0]) * 0.017453292F;
         this.head.xRot = Mth.rotWrap((double)this.entity.getHeadPartYOffset(6, var15, var23)) * 0.017453292F * 1.5F * 5.0F;
         this.head.zRot = -Mth.rotWrap(var23[0] - (double)var17) * 0.017453292F;
         this.head.render(var1, var2, var3, var4);
         var1.pushPose();
         var1.translate(0.0D, 1.0D, 0.0D);
         var1.mulPose(Vector3f.ZP.rotationDegrees(-var16 * 1.5F));
         var1.translate(0.0D, -1.0D, 0.0D);
         this.body.zRot = 0.0F;
         this.body.render(var1, var2, var3, var4);
         float var24 = var9 * 6.2831855F;
         this.leftWing.xRot = 0.125F - (float)Math.cos((double)var24) * 0.2F;
         this.leftWing.yRot = -0.25F;
         this.leftWing.zRot = -((float)(Math.sin((double)var24) + 0.125D)) * 0.8F;
         this.leftWingTip.zRot = (float)(Math.sin((double)(var24 + 2.0F)) + 0.5D) * 0.75F;
         this.rightWing.xRot = this.leftWing.xRot;
         this.rightWing.yRot = -this.leftWing.yRot;
         this.rightWing.zRot = -this.leftWing.zRot;
         this.rightWingTip.zRot = -this.leftWingTip.zRot;
         this.renderSide(var1, var2, var3, var4, var10, this.leftWing, this.leftFrontLeg, this.leftFrontLegTip, this.leftFrontFoot, this.leftRearLeg, this.leftRearLegTip, this.leftRearFoot);
         this.renderSide(var1, var2, var3, var4, var10, this.rightWing, this.rightFrontLeg, this.rightFrontLegTip, this.rightFrontFoot, this.rightRearLeg, this.rightRearLegTip, this.rightRearFoot);
         var1.popPose();
         var21 = -((float)Math.sin((double)(var9 * 6.2831855F))) * 0.0F;
         var18 = var9 * 6.2831855F;
         var12 = 10.0F;
         var13 = 60.0F;
         var11 = 0.0F;
         var15 = this.entity.getLatencyPos(11, this.a);

         for(int var22 = 0; var22 < 12; ++var22) {
            var23 = this.entity.getLatencyPos(12 + var22, this.a);
            var21 = (float)((double)var21 + Math.sin((double)((float)var22 * 0.45F + var18)) * 0.05000000074505806D);
            this.neck.yRot = (Mth.rotWrap(var23[0] - var15[0]) * 1.5F + 180.0F) * 0.017453292F;
            this.neck.xRot = var21 + (float)(var23[1] - var15[1]) * 0.017453292F * 1.5F * 5.0F;
            this.neck.zRot = Mth.rotWrap(var23[0] - (double)var17) * 0.017453292F * 1.5F;
            this.neck.y = var12;
            this.neck.z = var13;
            this.neck.x = var11;
            var12 = (float)((double)var12 + Math.sin((double)this.neck.xRot) * 10.0D);
            var13 = (float)((double)var13 - Math.cos((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
            var11 = (float)((double)var11 - Math.sin((double)this.neck.yRot) * Math.cos((double)this.neck.xRot) * 10.0D);
            this.neck.render(var1, var2, var3, var4);
         }

         var1.popPose();
      }

      private void renderSide(PoseStack var1, VertexConsumer var2, int var3, int var4, float var5, ModelPart var6, ModelPart var7, ModelPart var8, ModelPart var9, ModelPart var10, ModelPart var11, ModelPart var12) {
         var10.xRot = 1.0F + var5 * 0.1F;
         var11.xRot = 0.5F + var5 * 0.1F;
         var12.xRot = 0.75F + var5 * 0.1F;
         var7.xRot = 1.3F + var5 * 0.1F;
         var8.xRot = -0.5F - var5 * 0.1F;
         var9.xRot = 0.75F + var5 * 0.1F;
         var6.render(var1, var2, var3, var4);
         var7.render(var1, var2, var3, var4);
         var10.render(var1, var2, var3, var4);
      }
   }
}
