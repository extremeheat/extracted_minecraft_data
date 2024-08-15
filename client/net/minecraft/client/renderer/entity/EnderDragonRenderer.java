package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.dragon.EnderDragonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class EnderDragonRenderer extends EntityRenderer<EnderDragon, EnderDragonRenderState> {
   public static final ResourceLocation CRYSTAL_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
   private static final ResourceLocation DRAGON_EYES_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderType RENDER_TYPE = RenderType.entityCutoutNoCull(DRAGON_LOCATION);
   private static final RenderType DECAL = RenderType.entityDecal(DRAGON_LOCATION);
   private static final RenderType EYES = RenderType.eyes(DRAGON_EYES_LOCATION);
   private static final RenderType BEAM = RenderType.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
   private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
   private final EnderDragonModel model;

   public EnderDragonRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.model = new EnderDragonModel(var1.bakeLayer(ModelLayers.ENDER_DRAGON));
   }

   public void render(EnderDragonRenderState var1, PoseStack var2, MultiBufferSource var3, int var4) {
      var2.pushPose();
      float var5 = var1.getHistoricalPos(7).yRot();
      float var6 = (float)(var1.getHistoricalPos(5).y() - var1.getHistoricalPos(10).y());
      var2.mulPose(Axis.YP.rotationDegrees(-var5));
      var2.mulPose(Axis.XP.rotationDegrees(var6 * 10.0F));
      var2.translate(0.0F, 0.0F, 1.0F);
      var2.scale(-1.0F, -1.0F, 1.0F);
      var2.translate(0.0F, -1.501F, 0.0F);
      this.model.setupAnim(var1);
      if (var1.deathTime > 0.0F) {
         float var7 = var1.deathTime / 200.0F;
         int var8 = ARGB.color(Mth.floor(var7 * 255.0F), -1);
         VertexConsumer var9 = var3.getBuffer(RenderType.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION));
         this.model.renderToBuffer(var2, var9, var4, OverlayTexture.NO_OVERLAY, var8);
         VertexConsumer var10 = var3.getBuffer(DECAL);
         this.model.renderToBuffer(var2, var10, var4, OverlayTexture.pack(0.0F, var1.hasRedOverlay));
      } else {
         VertexConsumer var11 = var3.getBuffer(RENDER_TYPE);
         this.model.renderToBuffer(var2, var11, var4, OverlayTexture.pack(0.0F, var1.hasRedOverlay));
      }

      VertexConsumer var12 = var3.getBuffer(EYES);
      this.model.renderToBuffer(var2, var12, var4, OverlayTexture.NO_OVERLAY);
      if (var1.deathTime > 0.0F) {
         float var13 = var1.deathTime / 200.0F;
         var2.pushPose();
         var2.translate(0.0F, -1.0F, -2.0F);
         renderRays(var2, var13, var3.getBuffer(RenderType.dragonRays()));
         renderRays(var2, var13, var3.getBuffer(RenderType.dragonRaysDepth()));
         var2.popPose();
      }

      var2.popPose();
      if (var1.beamOffset != null) {
         renderCrystalBeams((float)var1.beamOffset.x, (float)var1.beamOffset.y, (float)var1.beamOffset.z, var1.ageInTicks, var2, var3, var4);
      }

      super.render(var1, var2, var3, var4);
   }

   private static void renderRays(PoseStack var0, float var1, VertexConsumer var2) {
      var0.pushPose();
      float var3 = Math.min(var1 > 0.8F ? (var1 - 0.8F) / 0.2F : 0.0F, 1.0F);
      int var4 = ARGB.colorFromFloat(1.0F - var3, 1.0F, 1.0F, 1.0F);
      int var5 = 16711935;
      RandomSource var6 = RandomSource.create(432L);
      Vector3f var7 = new Vector3f();
      Vector3f var8 = new Vector3f();
      Vector3f var9 = new Vector3f();
      Vector3f var10 = new Vector3f();
      Quaternionf var11 = new Quaternionf();
      int var12 = Mth.floor((var1 + var1 * var1) / 2.0F * 60.0F);

      for (int var13 = 0; var13 < var12; var13++) {
         var11.rotationXYZ(var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F)
            .rotateXYZ(var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F + var1 * 1.5707964F);
         var0.mulPose(var11);
         float var14 = var6.nextFloat() * 20.0F + 5.0F + var3 * 10.0F;
         float var15 = var6.nextFloat() * 2.0F + 1.0F + var3 * 2.0F;
         var8.set(-HALF_SQRT_3 * var15, var14, -0.5F * var15);
         var9.set(HALF_SQRT_3 * var15, var14, -0.5F * var15);
         var10.set(0.0F, var14, var15);
         PoseStack.Pose var16 = var0.last();
         var2.addVertex(var16, var7).setColor(var4);
         var2.addVertex(var16, var8).setColor(16711935);
         var2.addVertex(var16, var9).setColor(16711935);
         var2.addVertex(var16, var7).setColor(var4);
         var2.addVertex(var16, var9).setColor(16711935);
         var2.addVertex(var16, var10).setColor(16711935);
         var2.addVertex(var16, var7).setColor(var4);
         var2.addVertex(var16, var10).setColor(16711935);
         var2.addVertex(var16, var8).setColor(16711935);
      }

      var0.popPose();
   }

   public static void renderCrystalBeams(float var0, float var1, float var2, float var3, PoseStack var4, MultiBufferSource var5, int var6) {
      float var7 = Mth.sqrt(var0 * var0 + var2 * var2);
      float var8 = Mth.sqrt(var0 * var0 + var1 * var1 + var2 * var2);
      var4.pushPose();
      var4.translate(0.0F, 2.0F, 0.0F);
      var4.mulPose(Axis.YP.rotation((float)(-Math.atan2((double)var2, (double)var0)) - 1.5707964F));
      var4.mulPose(Axis.XP.rotation((float)(-Math.atan2((double)var7, (double)var1)) - 1.5707964F));
      VertexConsumer var9 = var5.getBuffer(BEAM);
      float var10 = 0.0F - var3 * 0.01F;
      float var11 = var8 / 32.0F - var3 * 0.01F;
      byte var12 = 8;
      float var13 = 0.0F;
      float var14 = 0.75F;
      float var15 = 0.0F;
      PoseStack.Pose var16 = var4.last();

      for (int var17 = 1; var17 <= 8; var17++) {
         float var18 = Mth.sin((float)var17 * 6.2831855F / 8.0F) * 0.75F;
         float var19 = Mth.cos((float)var17 * 6.2831855F / 8.0F) * 0.75F;
         float var20 = (float)var17 / 8.0F;
         var9.addVertex(var16, var13 * 0.2F, var14 * 0.2F, 0.0F)
            .setColor(-16777216)
            .setUv(var15, var10)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var6)
            .setNormal(var16, 0.0F, -1.0F, 0.0F);
         var9.addVertex(var16, var13, var14, var8)
            .setColor(-1)
            .setUv(var15, var11)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var6)
            .setNormal(var16, 0.0F, -1.0F, 0.0F);
         var9.addVertex(var16, var18, var19, var8)
            .setColor(-1)
            .setUv(var20, var11)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var6)
            .setNormal(var16, 0.0F, -1.0F, 0.0F);
         var9.addVertex(var16, var18 * 0.2F, var19 * 0.2F, 0.0F)
            .setColor(-16777216)
            .setUv(var20, var10)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(var6)
            .setNormal(var16, 0.0F, -1.0F, 0.0F);
         var13 = var18;
         var14 = var19;
         var15 = var20;
      }

      var4.popPose();
   }

   public ResourceLocation getTextureLocation(EnderDragonRenderState var1) {
      return DRAGON_LOCATION;
   }

   public EnderDragonRenderState createRenderState() {
      return new EnderDragonRenderState();
   }

   public void extractRenderState(EnderDragon var1, EnderDragonRenderState var2, float var3) {
      super.extractRenderState(var1, var2, var3);
      var2.flapTime = Mth.lerp(var3, var1.oFlapTime, var1.flapTime);
      var2.deathTime = var1.dragonDeathTime > 0 ? (float)var1.dragonDeathTime + var3 : 0.0F;
      var2.hasRedOverlay = var1.hurtTime > 0;
      EndCrystal var4 = var1.nearestCrystal;
      if (var4 != null) {
         Vec3 var5 = var4.getPosition(var3).add(0.0, (double)EndCrystalRenderer.getY((float)var4.time + var3), 0.0);
         var2.beamOffset = var5.subtract(var1.getPosition(var3));
      } else {
         var2.beamOffset = null;
      }

      DragonPhaseInstance var7 = var1.getPhaseManager().getCurrentPhase();
      var2.isLandingOrTakingOff = var7 == EnderDragonPhase.LANDING || var7 == EnderDragonPhase.TAKEOFF;
      var2.isSitting = var7.isSitting();
      BlockPos var6 = var1.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(var1.getFightOrigin()));
      var2.distanceToEgg = var6.distToCenterSqr(var1.position());
      var2.partialTicks = var1.isDeadOrDying() ? 0.0F : var3;
      var2.flightHistory.copyFrom(var1.flightHistory);
   }

   protected boolean affectedByCulling(EnderDragon var1) {
      return false;
   }
}
