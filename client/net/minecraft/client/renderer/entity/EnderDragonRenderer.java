package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.monster.dragon.EnderDragonModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EnderDragonRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
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
import org.joml.Quaternionfc;
import org.joml.Vector3f;

public class EnderDragonRenderer extends EntityRenderer<EnderDragon, EnderDragonRenderState> {
   public static final Identifier CRYSTAL_BEAM_LOCATION = Identifier.withDefaultNamespace("textures/entity/end_crystal/end_crystal_beam.png");
   private static final Identifier DRAGON_EXPLODING_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_exploding.png");
   private static final Identifier DRAGON_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon.png");
   private static final Identifier DRAGON_EYES_LOCATION = Identifier.withDefaultNamespace("textures/entity/enderdragon/dragon_eyes.png");
   private static final RenderType RENDER_TYPE;
   private static final RenderType DECAL;
   private static final RenderType EYES;
   private static final RenderType BEAM;
   private static final float HALF_SQRT_3;
   private final EnderDragonModel model;

   public EnderDragonRenderer(final EntityRendererProvider.Context context) {
      super(context);
      this.shadowRadius = 0.5F;
      this.model = new EnderDragonModel(context.bakeLayer(ModelLayers.ENDER_DRAGON));
   }

   public void submit(final EnderDragonRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      float yr = state.getHistoricalPos(7).yRot();
      float rot2 = (float)(state.getHistoricalPos(5).y() - state.getHistoricalPos(10).y());
      poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-yr));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(rot2 * 10.0F));
      poseStack.translate(0.0F, 0.0F, 1.0F);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      poseStack.translate(0.0F, -1.501F, 0.0F);
      int overlayCoords = OverlayTexture.pack(0.0F, state.hasRedOverlay);
      if (state.deathTime > 0.0F) {
         int color = ARGB.white(state.deathTime / 200.0F);
         submitNodeCollector.order(0).submitModel(this.model, state, poseStack, RenderTypes.dragonExplosionAlpha(DRAGON_EXPLODING_LOCATION), state.lightCoords, OverlayTexture.NO_OVERLAY, color, (TextureAtlasSprite)null, state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
         submitNodeCollector.order(1).submitModel(this.model, state, poseStack, DECAL, state.lightCoords, overlayCoords, -1, (TextureAtlasSprite)null, state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      } else {
         submitNodeCollector.order(0).submitModel(this.model, state, poseStack, RENDER_TYPE, state.lightCoords, overlayCoords, -1, (TextureAtlasSprite)null, state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      }

      submitNodeCollector.submitModel(this.model, state, poseStack, EYES, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
      if (state.deathTime > 0.0F) {
         float deathTime = state.deathTime / 200.0F;
         poseStack.pushPose();
         poseStack.translate(0.0F, -1.0F, -2.0F);
         submitRays(poseStack, deathTime, submitNodeCollector, RenderTypes.dragonRays());
         submitRays(poseStack, deathTime, submitNodeCollector, RenderTypes.dragonRaysDepth());
         poseStack.popPose();
      }

      poseStack.popPose();
      if (state.beamOffset != null) {
         submitCrystalBeams((float)state.beamOffset.x, (float)state.beamOffset.y, (float)state.beamOffset.z, state.ageInTicks, poseStack, submitNodeCollector, state.lightCoords);
      }

      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   private static void submitRays(final PoseStack poseStack, final float deathTime, final SubmitNodeCollector submitNodeCollector, final RenderType renderType) {
      submitNodeCollector.submitCustomGeometry(poseStack, renderType, (pose, buffer) -> {
         float overDrive = Math.min(deathTime > 0.8F ? (deathTime - 0.8F) / 0.2F : 0.0F, 1.0F);
         int innerColor = ARGB.colorFromFloat(1.0F - overDrive, 1.0F, 1.0F, 1.0F);
         int outerColor = 16711935;
         RandomSource random = RandomSource.create(432L);
         Vector3f origin = new Vector3f();
         Vector3f outerLeft = new Vector3f();
         Vector3f outerRight = new Vector3f();
         Vector3f outerBottom = new Vector3f();
         Quaternionf rayRotation = new Quaternionf();
         int rayCount = Mth.floor((deathTime + deathTime * deathTime) / 2.0F * 60.0F);

         for(int i = 0; i < rayCount; ++i) {
            rayRotation.rotationXYZ(random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F).rotateXYZ(random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F, random.nextFloat() * 6.2831855F + deathTime * 1.5707964F);
            pose.rotate(rayRotation);
            float length = random.nextFloat() * 20.0F + 5.0F + overDrive * 10.0F;
            float width = random.nextFloat() * 2.0F + 1.0F + overDrive * 2.0F;
            outerLeft.set(-HALF_SQRT_3 * width, length, -0.5F * width);
            outerRight.set(HALF_SQRT_3 * width, length, -0.5F * width);
            outerBottom.set(0.0F, length, width);
            buffer.addVertex(pose, origin).setColor(innerColor);
            buffer.addVertex(pose, outerLeft).setColor(16711935);
            buffer.addVertex(pose, outerRight).setColor(16711935);
            buffer.addVertex(pose, origin).setColor(innerColor);
            buffer.addVertex(pose, outerRight).setColor(16711935);
            buffer.addVertex(pose, outerBottom).setColor(16711935);
            buffer.addVertex(pose, origin).setColor(innerColor);
            buffer.addVertex(pose, outerBottom).setColor(16711935);
            buffer.addVertex(pose, outerLeft).setColor(16711935);
         }

      });
   }

   public static void submitCrystalBeams(final float deltaX, final float deltaY, final float deltaZ, final float timeInTicks, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords) {
      float horizontalLength = Mth.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      float length = Mth.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
      poseStack.pushPose();
      poseStack.translate(0.0F, 2.0F, 0.0F);
      poseStack.mulPose((Quaternionfc)Axis.YP.rotation((float)(-Math.atan2((double)deltaZ, (double)deltaX)) - 1.5707964F));
      poseStack.mulPose((Quaternionfc)Axis.XP.rotation((float)(-Math.atan2((double)horizontalLength, (double)deltaY)) - 1.5707964F));
      float v0 = 0.0F - timeInTicks * 0.01F;
      float v1 = length / 32.0F - timeInTicks * 0.01F;
      submitNodeCollector.submitCustomGeometry(poseStack, BEAM, (pose, buffer) -> {
         int steps = 8;
         float lastSin = 0.0F;
         float lastCos = 0.75F;
         float lastU = 0.0F;

         for(int i = 1; i <= 8; ++i) {
            float sin = Mth.sin((double)((float)i * 6.2831855F / 8.0F)) * 0.75F;
            float cos = Mth.cos((double)((float)i * 6.2831855F / 8.0F)) * 0.75F;
            float u = (float)i / 8.0F;
            buffer.addVertex(pose, lastSin * 0.2F, lastCos * 0.2F, 0.0F).setColor(-16777216).setUv(lastU, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0.0F, -1.0F, 0.0F);
            buffer.addVertex(pose, lastSin, lastCos, length).setColor(-1).setUv(lastU, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0.0F, -1.0F, 0.0F);
            buffer.addVertex(pose, sin, cos, length).setColor(-1).setUv(u, v1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0.0F, -1.0F, 0.0F);
            buffer.addVertex(pose, sin * 0.2F, cos * 0.2F, 0.0F).setColor(-16777216).setUv(u, v0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(lightCoords).setNormal(pose, 0.0F, -1.0F, 0.0F);
            lastSin = sin;
            lastCos = cos;
            lastU = u;
         }

      });
      poseStack.popPose();
   }

   public EnderDragonRenderState createRenderState() {
      return new EnderDragonRenderState();
   }

   public void extractRenderState(final EnderDragon entity, final EnderDragonRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.flapTime = Mth.lerp(partialTicks, entity.oFlapTime, entity.flapTime);
      state.deathTime = entity.dragonDeathTime > 0 ? (float)entity.dragonDeathTime + partialTicks : 0.0F;
      state.hasRedOverlay = entity.hurtTime > 0;
      EndCrystal nearestCrystal = entity.nearestCrystal;
      if (nearestCrystal != null) {
         Vec3 crystalPosition = nearestCrystal.getPosition(partialTicks).add(0.0, (double)EndCrystalRenderer.getY((float)nearestCrystal.time + partialTicks), 0.0);
         state.beamOffset = crystalPosition.subtract(entity.getPosition(partialTicks));
      } else {
         state.beamOffset = null;
      }

      DragonPhaseInstance phase = entity.getPhaseManager().getCurrentPhase();
      state.isLandingOrTakingOff = phase == EnderDragonPhase.LANDING || phase == EnderDragonPhase.TAKEOFF;
      state.isSitting = phase.isSitting();
      BlockPos egg = entity.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.getLocation(entity.getFightOrigin()));
      state.distanceToEgg = egg.distToCenterSqr(entity.position());
      state.partialTicks = entity.isDeadOrDying() ? 0.0F : partialTicks;
      state.flightHistory.copyFrom(entity.flightHistory);
   }

   protected boolean affectedByCulling(final EnderDragon entity) {
      return false;
   }

   static {
      RENDER_TYPE = RenderTypes.entityCutoutNoCull(DRAGON_LOCATION);
      DECAL = RenderTypes.entityDecal(DRAGON_LOCATION);
      EYES = RenderTypes.eyes(DRAGON_EYES_LOCATION);
      BEAM = RenderTypes.entitySmoothCutout(CRYSTAL_BEAM_LOCATION);
      HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);
   }
}
