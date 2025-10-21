package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.dragon.EnderDragonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class EnderDragonRenderer extends EntityRenderer<EnderDragon, EnderDragonRenderState> {
   public static final ResourceLocation CRYSTAL_BEAM_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
   private static final ResourceLocation DRAGON_EXPLODING_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
   private static final ResourceLocation DRAGON_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
   private static final ResourceLocation DRAGON_EYES_LOCATION = ResourceLocation.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderType RENDER_TYPE;
   private static final RenderType DECAL;
   private static final RenderType EYES;
   private static final RenderType BEAM;
   private static final float HALF_SQRT_3;
   private final EnderDragonModel model;

   public EnderDragonRenderer(EntityRendererProvider.Context var1) {
      super(var1);
      this.shadowRadius = 0.5F;
      this.model = new EnderDragonModel(var1.bakeLayer(ModelLayers.ENDER_DRAGON));
   }

   public void submit(EnderDragonRenderState var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      var2.pushPose();
      float var5 = var1.getHistoricalPos(7).yRot();
      float var6 = (float)(var1.getHistoricalPos(5).y() - var1.getHistoricalPos(10).y());
      var2.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var5));
      var2.mulPose((Quaternionfc)Axis.XP.rotationDegrees(var6 * 10.0F));
      var2.translate(0.0F, 0.0F, 1.0F);
      var2.scale(-1.0F, -1.0F, 1.0F);
      var2.translate(0.0F, -1.501F, 0.0F);
      int var7 = OverlayTexture.pack(0.0F, var1.hasRedOverlay);
      if (var1.deathTime > 0.0F) {
         int var8 = ARGB.white(var1.deathTime / 200.0F);
         var3.order(0).submitModel(this.model, var1, var2, RenderTypes.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION), var1.lightCoords, OverlayTexture.NO_OVERLAY, var8, (TextureAtlasSprite)null, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         var3.order(1).submitModel(this.model, var1, var2, DECAL, var1.lightCoords, var7, -1, (TextureAtlasSprite)null, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      } else {
         var3.order(0).submitModel(this.model, var1, var2, RENDER_TYPE, var1.lightCoords, var7, -1, (TextureAtlasSprite)null, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

      var3.submitModel(this.model, var1, var2, EYES, var1.lightCoords, OverlayTexture.NO_OVERLAY, var1.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      if (var1.deathTime > 0.0F) {
         float var9 = var1.deathTime / 200.0F;
         var2.pushPose();
         var2.translate(0.0F, -1.0F, -2.0F);
         submitRays(var2, var9, var3, RenderTypes.dragonRays());
         submitRays(var2, var9, var3, RenderTypes.dragonRaysDepth());
         var2.popPose();
      }

      var2.popPose();
      if (var1.beamOffset != null) {
         submitCrystalBeams((float)var1.beamOffset.x, (float)var1.beamOffset.y, (float)var1.beamOffset.z, var1.ageInTicks, var2, var3, var1.lightCoords);
      }

      super.submit(var1, var2, var3, var4);
   }

   private static void submitRays(PoseStack var0, float var1, SubmitNodeCollector var2, RenderType var3) {
      var2.submitCustomGeometry(var0, var3, (var1x, var2x) -> {
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

         for(int var13 = 0; var13 < var12; ++var13) {
            var11.rotationXYZ(var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F).rotateXYZ(var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F, var6.nextFloat() * 6.2831855F + var1 * 1.5707964F);
            var1x.rotate(var11);
            float var14 = var6.nextFloat() * 20.0F + 5.0F + var3 * 10.0F;
            float var15 = var6.nextFloat() * 2.0F + 1.0F + var3 * 2.0F;
            var8.set(-HALF_SQRT_3 * var15, var14, -0.5F * var15);
            var9.set(HALF_SQRT_3 * var15, var14, -0.5F * var15);
            var10.set(0.0F, var14, var15);
            var2x.addVertex(var1x, var7).setColor(var4);
            var2x.addVertex(var1x, var8).setColor(16711935);
            var2x.addVertex(var1x, var9).setColor(16711935);
            var2x.addVertex(var1x, var7).setColor(var4);
            var2x.addVertex(var1x, var9).setColor(16711935);
            var2x.addVertex(var1x, var10).setColor(16711935);
            var2x.addVertex(var1x, var7).setColor(var4);
            var2x.addVertex(var1x, var10).setColor(16711935);
            var2x.addVertex(var1x, var8).setColor(16711935);
         }

      });
   }

   public static void submitCrystalBeams(float var0, float var1, float var2, float var3, PoseStack var4, SubmitNodeCollector var5, int var6) {
      float var7 = Mth.sqrt(var0 * var0 + var2 * var2);
      float var8 = Mth.sqrt(var0 * var0 + var1 * var1 + var2 * var2);
      var4.pushPose();
      var4.translate(0.0F, 2.0F, 0.0F);
      var4.mulPose((Quaternionfc)Axis.YP.rotation((float)(-Math.atan2((double)var2, (double)var0)) - 1.5707964F));
      var4.mulPose((Quaternionfc)Axis.XP.rotation((float)(-Math.atan2((double)var7, (double)var1)) - 1.5707964F));
      float var9 = 0.0F - var3 * 0.01F;
      float var10 = var8 / 32.0F - var3 * 0.01F;
      var5.submitCustomGeometry(var4, BEAM, (var4x, var5x) -> {
         boolean var6x = true;
         float var7 = 0.0F;
         float var8x = 0.75F;
         float var9x = 0.0F;

         for(int var10x = 1; var10x <= 8; ++var10x) {
            float var11 = Mth.sin((float)var10x * 6.2831855F / 8.0F) * 0.75F;
            float var12 = Mth.cos((float)var10x * 6.2831855F / 8.0F) * 0.75F;
            float var13 = (float)var10x / 8.0F;
            var5x.addVertex(var4x, var7 * 0.2F, var8x * 0.2F, 0.0F).setColor(-16777216).setUv(var9x, var9).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var6).setNormal(var4x, 0.0F, -1.0F, 0.0F);
            var5x.addVertex(var4x, var7, var8x, var8).setColor(-1).setUv(var9x, var10).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var6).setNormal(var4x, 0.0F, -1.0F, 0.0F);
            var5x.addVertex(var4x, var11, var12, var8).setColor(-1).setUv(var13, var10).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var6).setNormal(var4x, 0.0F, -1.0F, 0.0F);
            var5x.addVertex(var4x, var11 * 0.2F, var12 * 0.2F, 0.0F).setColor(-16777216).setUv(var13, var9).setOverlay(OverlayTexture.NO_OVERLAY).setLight(var6).setNormal(var4x, 0.0F, -1.0F, 0.0F);
            var7 = var11;
            var8x = var12;
            var9x = var13;
         }

      });
      var4.popPose();
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

   // $FF: synthetic method
   public EntityRenderState createRenderState() {
      return this.createRenderState();
   }

   // $FF: synthetic method
   protected boolean affectedByCulling(final Entity var1) {
      return this.affectedByCulling((EnderDragon)var1);
   }

   static {
      RENDER_TYPE = RenderTypes.entityCutoutNoCull(DRAGON_LOCATION);
      DECAL = RenderTypes.entityDecal(DRAGON_LOCATION);
      EYES = RenderTypes.eyes(DRAGON_EYES_LOCATION);
      BEAM = RenderTypes.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
      HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
   }
}
