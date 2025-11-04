package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Screenshot;
import net.minecraft.client.entity.ClientAvatarState;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.font.ActiveArea;
import net.minecraft.client.gui.font.EmptyArea;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.GuiBannerResultRenderer;
import net.minecraft.client.gui.render.pip.GuiBookModelRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.GuiProfilerChartRenderer;
import net.minecraft.client.gui.render.pip.GuiSignRenderer;
import net.minecraft.client.gui.render.pip.GuiSkinRenderer;
import net.minecraft.client.gui.render.state.ColoredRectangleRenderState;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.Zone;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class GameRenderer implements TrackedWaypoint.Projector, AutoCloseable {
   private static final Identifier BLUR_POST_CHAIN_ID = Identifier.withDefaultNamespace("blur");
   public static final int MAX_BLUR_RADIUS = 10;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final float PROJECTION_Z_NEAR = 0.05F;
   public static final float PROJECTION_3D_HUD_Z_FAR = 100.0F;
   private static final float PORTAL_SPINNING_SPEED = 20.0F;
   private static final float NAUSEA_SPINNING_SPEED = 7.0F;
   private final Minecraft minecraft;
   private final RandomSource random = RandomSource.create();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final ScreenEffectRenderer screenEffectRenderer;
   private final RenderBuffers renderBuffers;
   private float spinningEffectTime;
   private float spinningEffectSpeed;
   private float fovModifier;
   private float oldFovModifier;
   private float darkenWorldAmount;
   private float darkenWorldAmountO;
   private boolean renderBlockOutline = true;
   private long lastScreenshotAttempt;
   private boolean hasWorldScreenshot;
   private long lastActiveTime = Util.getMillis();
   private final LightTexture lightTexture;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private @Nullable PanoramicScreenshotParameters panoramicScreenshotParameters;
   protected final CubeMap cubeMap = new CubeMap(Identifier.withDefaultNamespace("textures/gui/title/background/panorama"));
   protected final PanoramaRenderer panorama;
   private final CrossFrameResourcePool resourcePool;
   private final FogRenderer fogRenderer;
   private final GuiRenderer guiRenderer;
   final GuiRenderState guiRenderState;
   private final LevelRenderState levelRenderState;
   private final SubmitNodeStorage submitNodeStorage;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private @Nullable Identifier postEffectId;
   private boolean effectActive;
   private final Camera mainCamera;
   private final Lighting lighting;
   private final GlobalSettingsUniform globalSettingsUniform;
   private final PerspectiveProjectionMatrixBuffer levelProjectionMatrixBuffer;
   private final CachedPerspectiveProjectionMatrixBuffer hud3dProjectionMatrixBuffer;

   public GameRenderer(Minecraft var1, ItemInHandRenderer var2, RenderBuffers var3, BlockRenderDispatcher var4) {
      super();
      this.panorama = new PanoramaRenderer(this.cubeMap);
      this.resourcePool = new CrossFrameResourcePool(3);
      this.fogRenderer = new FogRenderer();
      this.levelRenderState = new LevelRenderState();
      this.mainCamera = new Camera();
      this.lighting = new Lighting();
      this.globalSettingsUniform = new GlobalSettingsUniform();
      this.levelProjectionMatrixBuffer = new PerspectiveProjectionMatrixBuffer("level");
      this.hud3dProjectionMatrixBuffer = new CachedPerspectiveProjectionMatrixBuffer("3d hud", 0.05F, 100.0F);
      this.minecraft = var1;
      this.itemInHandRenderer = var2;
      this.lightTexture = new LightTexture(this, var1);
      this.renderBuffers = var3;
      this.guiRenderState = new GuiRenderState();
      MultiBufferSource.BufferSource var5 = var3.bufferSource();
      AtlasManager var6 = var1.getAtlasManager();
      this.submitNodeStorage = new SubmitNodeStorage();
      this.featureRenderDispatcher = new FeatureRenderDispatcher(this.submitNodeStorage, var4, var5, var6, var3.outlineBufferSource(), var3.crumblingBufferSource(), var1.font);
      this.guiRenderer = new GuiRenderer(this.guiRenderState, var5, this.submitNodeStorage, this.featureRenderDispatcher, List.of(new GuiEntityRenderer(var5, var1.getEntityRenderDispatcher()), new GuiSkinRenderer(var5), new GuiBookModelRenderer(var5), new GuiBannerResultRenderer(var5, var6), new GuiSignRenderer(var5, var6), new GuiProfilerChartRenderer(var5)));
      this.screenEffectRenderer = new ScreenEffectRenderer(var1, var6, var5);
   }

   public void close() {
      this.globalSettingsUniform.close();
      this.lightTexture.close();
      this.overlayTexture.close();
      this.resourcePool.close();
      this.guiRenderer.close();
      this.levelProjectionMatrixBuffer.close();
      this.hud3dProjectionMatrixBuffer.close();
      this.lighting.close();
      this.cubeMap.close();
      this.fogRenderer.close();
      this.featureRenderDispatcher.close();
   }

   public SubmitNodeStorage getSubmitNodeStorage() {
      return this.submitNodeStorage;
   }

   public FeatureRenderDispatcher getFeatureRenderDispatcher() {
      return this.featureRenderDispatcher;
   }

   public LevelRenderState getLevelRenderState() {
      return this.levelRenderState;
   }

   public void setRenderBlockOutline(boolean var1) {
      this.renderBlockOutline = var1;
   }

   public void setPanoramicScreenshotParameters(@Nullable PanoramicScreenshotParameters var1) {
      this.panoramicScreenshotParameters = var1;
   }

   public @Nullable PanoramicScreenshotParameters getPanoramicScreenshotParameters() {
      return this.panoramicScreenshotParameters;
   }

   public boolean isPanoramicMode() {
      return this.panoramicScreenshotParameters != null;
   }

   public void clearPostEffect() {
      this.postEffectId = null;
      this.effectActive = false;
   }

   public void togglePostEffect() {
      this.effectActive = !this.effectActive;
   }

   public void checkEntityPostEffect(@Nullable Entity var1) {
      byte var3 = 0;
      //$FF: var3->value
      //0->net/minecraft/world/entity/monster/Creeper
      //1->net/minecraft/world/entity/monster/Spider
      //2->net/minecraft/world/entity/monster/EnderMan
      switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
         case -1:
         default:
            this.clearPostEffect();
            break;
         case 0:
            Creeper var4 = (Creeper)var1;
            this.setPostEffect(Identifier.withDefaultNamespace("creeper"));
            break;
         case 1:
            Spider var5 = (Spider)var1;
            this.setPostEffect(Identifier.withDefaultNamespace("spider"));
            break;
         case 2:
            EnderMan var6 = (EnderMan)var1;
            this.setPostEffect(Identifier.withDefaultNamespace("invert"));
      }

   }

   private void setPostEffect(Identifier var1) {
      this.postEffectId = var1;
      this.effectActive = true;
   }

   public void processBlurEffect() {
      PostChain var1 = this.minecraft.getShaderManager().getPostChain(BLUR_POST_CHAIN_ID, LevelTargetBundle.MAIN_TARGETS);
      if (var1 != null) {
         var1.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
      }

   }

   public void preloadUiShader(ResourceProvider var1) {
      GpuDevice var2 = RenderSystem.getDevice();
      ShaderSource var3 = (var1x, var2x) -> {
         Identifier var3 = var2x.idConverter().idToFile(var1x);

         try {
            BufferedReader var4 = var1.getResourceOrThrow(var3).openAsReader();

            String var5;
            try {
               var5 = IOUtils.toString(var4);
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     ((Reader)var4).close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               ((Reader)var4).close();
            }

            return var5;
         } catch (IOException var9) {
            LOGGER.error("Coudln't preload {} shader {}: {}", new Object[]{var2x, var1x, var9});
            return null;
         }
      };
      var2.precompilePipeline(RenderPipelines.GUI, var3);
      var2.precompilePipeline(RenderPipelines.GUI_TEXTURED, var3);
      if (TracyClient.isAvailable()) {
         var2.precompilePipeline(RenderPipelines.TRACY_BLIT, var3);
      }

   }

   public void tick() {
      this.tickFov();
      this.lightTexture.tick();
      LocalPlayer var1 = this.minecraft.player;
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(var1);
      }

      this.mainCamera.tick();
      this.itemInHandRenderer.tick();
      float var2 = var1.portalEffectIntensity;
      float var3 = var1.getEffectBlendFactor(MobEffects.NAUSEA, 1.0F);
      if (!(var2 > 0.0F) && !(var3 > 0.0F)) {
         this.spinningEffectSpeed = 0.0F;
      } else {
         this.spinningEffectSpeed = (var2 * 20.0F + var3 * 7.0F) / (var2 + var3);
         this.spinningEffectTime += this.spinningEffectSpeed;
      }

      if (this.minecraft.level.tickRateManager().runsNormally()) {
         this.darkenWorldAmountO = this.darkenWorldAmount;
         if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.darkenWorldAmount += 0.05F;
            if (this.darkenWorldAmount > 1.0F) {
               this.darkenWorldAmount = 1.0F;
            }
         } else if (this.darkenWorldAmount > 0.0F) {
            this.darkenWorldAmount -= 0.0125F;
         }

         this.screenEffectRenderer.tick();
         ProfilerFiller var4 = Profiler.get();
         var4.push("levelRenderer");
         this.minecraft.levelRenderer.tick(this.mainCamera);
         var4.pop();
      }
   }

   public @Nullable Identifier currentPostEffect() {
      return this.postEffectId;
   }

   public void resize(int var1, int var2) {
      this.resourcePool.clear();
      this.minecraft.levelRenderer.resize(var1, var2);
   }

   public void pick(float var1) {
      Entity var2 = this.minecraft.getCameraEntity();
      if (var2 != null) {
         if (this.minecraft.level != null && this.minecraft.player != null) {
            Profiler.get().push("pick");
            double var3 = this.minecraft.player.blockInteractionRange();
            double var5 = this.minecraft.player.entityInteractionRange();
            HitResult var7 = this.pick(var2, var3, var5, var1);
            this.minecraft.hitResult = var7;
            Minecraft var10000 = this.minecraft;
            Entity var10001;
            if (var7 instanceof EntityHitResult) {
               EntityHitResult var8 = (EntityHitResult)var7;
               var10001 = var8.getEntity();
            } else {
               var10001 = null;
            }

            var10000.crosshairPickEntity = var10001;
            Profiler.get().pop();
         }
      }
   }

   private HitResult pick(Entity var1, double var2, double var4, float var6) {
      double var7 = Math.max(var2, var4);
      double var9 = Mth.square(var7);
      Vec3 var11 = var1.getEyePosition(var6);
      HitResult var12 = var1.pick(var7, var6, false);
      double var13 = var12.getLocation().distanceToSqr(var11);
      if (var12.getType() != HitResult.Type.MISS) {
         var9 = var13;
         var7 = Math.sqrt(var13);
      }

      Vec3 var15 = var1.getViewVector(var6);
      Vec3 var16 = var11.add(var15.x * var7, var15.y * var7, var15.z * var7);
      float var17 = 1.0F;
      AABB var18 = var1.getBoundingBox().expandTowards(var15.scale(var7)).inflate(1.0, 1.0, 1.0);
      EntityHitResult var19 = ProjectileUtil.getEntityHitResult(var1, var11, var16, var18, EntitySelector.CAN_BE_PICKED, var9);
      return var19 != null && var19.getLocation().distanceToSqr(var11) < var13 ? filterHitResult(var19, var11, var4) : filterHitResult(var12, var11, var2);
   }

   private static HitResult filterHitResult(HitResult var0, Vec3 var1, double var2) {
      Vec3 var4 = var0.getLocation();
      if (!var4.closerThan(var1, var2)) {
         Vec3 var5 = var0.getLocation();
         Direction var6 = Direction.getApproximateNearest(var5.x - var1.x, var5.y - var1.y, var5.z - var1.z);
         return BlockHitResult.miss(var5, var6, BlockPos.containing(var5));
      } else {
         return var0;
      }
   }

   private void tickFov() {
      Entity var3 = this.minecraft.getCameraEntity();
      float var1;
      if (var3 instanceof AbstractClientPlayer var2) {
         Options var6 = this.minecraft.options;
         boolean var4 = var6.getCameraType().isFirstPerson();
         float var5 = ((Double)var6.fovEffectScale().get()).floatValue();
         var1 = var2.getFieldOfViewModifier(var4, var5);
      } else {
         var1 = 1.0F;
      }

      this.oldFovModifier = this.fovModifier;
      this.fovModifier += (var1 - this.fovModifier) * 0.5F;
      this.fovModifier = Mth.clamp(this.fovModifier, 0.1F, 1.5F);
   }

   private float getFov(Camera var1, float var2, boolean var3) {
      if (this.isPanoramicMode()) {
         return 90.0F;
      } else {
         float var4 = 70.0F;
         if (var3) {
            var4 = (float)(Integer)this.minecraft.options.fov().get();
            var4 *= Mth.lerp(var2, this.oldFovModifier, this.fovModifier);
         }

         Entity var6 = var1.entity();
         if (var6 instanceof LivingEntity) {
            LivingEntity var5 = (LivingEntity)var6;
            if (var5.isDeadOrDying()) {
               float var9 = Math.min((float)var5.deathTime + var2, 20.0F);
               var4 /= (1.0F - 500.0F / (var9 + 500.0F)) * 2.0F + 1.0F;
            }
         }

         FogType var8 = var1.getFluidInCamera();
         if (var8 == FogType.LAVA || var8 == FogType.WATER) {
            float var10 = ((Double)this.minecraft.options.fovEffectScale().get()).floatValue();
            var4 *= Mth.lerp(var10, 1.0F, 0.85714287F);
         }

         return var4;
      }
   }

   private void bobHurt(PoseStack var1, float var2) {
      Entity var4 = this.minecraft.getCameraEntity();
      if (var4 instanceof LivingEntity var3) {
         float var7 = (float)var3.hurtTime - var2;
         if (var3.isDeadOrDying()) {
            float var5 = Math.min((float)var3.deathTime + var2, 20.0F);
            var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(40.0F - 8000.0F / (var5 + 200.0F)));
         }

         if (var7 < 0.0F) {
            return;
         }

         var7 /= (float)var3.hurtDuration;
         var7 = Mth.sin((double)(var7 * var7 * var7 * var7 * 3.1415927F));
         float var10 = var3.getHurtDir();
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-var10));
         float var6 = (float)((double)(-var7) * 14.0 * (Double)this.minecraft.options.damageTiltStrength().get());
         var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(var6));
         var1.mulPose((Quaternionfc)Axis.YP.rotationDegrees(var10));
      }

   }

   private void bobView(PoseStack var1, float var2) {
      Entity var4 = this.minecraft.getCameraEntity();
      if (var4 instanceof AbstractClientPlayer var3) {
         ClientAvatarState var7 = var3.avatarState();
         float var5 = var7.getBackwardsInterpolatedWalkDistance(var2);
         float var6 = var7.getInterpolatedBob(var2);
         var1.translate(Mth.sin((double)(var5 * 3.1415927F)) * var6 * 0.5F, -Math.abs(Mth.cos((double)(var5 * 3.1415927F)) * var6), 0.0F);
         var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.sin((double)(var5 * 3.1415927F)) * var6 * 3.0F));
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Math.abs(Mth.cos((double)(var5 * 3.1415927F - 0.2F)) * var6) * 5.0F));
      }
   }

   private void renderItemInHand(float var1, boolean var2, Matrix4f var3) {
      if (!this.isPanoramicMode()) {
         this.featureRenderDispatcher.renderAllFeatures();
         this.renderBuffers.bufferSource().endBatch();
         PoseStack var4 = new PoseStack();
         var4.pushPose();
         var4.mulPose((Matrix4fc)var3.invert(new Matrix4f()));
         Matrix4fStack var5 = RenderSystem.getModelViewStack();
         var5.pushMatrix().mul(var3);
         this.bobHurt(var4, var1);
         if ((Boolean)this.minecraft.options.bobView().get()) {
            this.bobView(var4, var1);
         }

         if (this.minecraft.options.getCameraType().isFirstPerson() && !var2 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.itemInHandRenderer.renderHandsWithItems(var1, var4, this.minecraft.gameRenderer.getSubmitNodeStorage(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, var1));
         }

         var5.popMatrix();
         var4.popPose();
      }
   }

   public Matrix4f getProjectionMatrix(float var1) {
      Matrix4f var2 = new Matrix4f();
      return var2.perspective(var1 * 0.017453292F, (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.getDepthFar());
   }

   public float getDepthFar() {
      return Math.max(this.renderDistance * 4.0F, (float)((Integer)this.minecraft.options.cloudRange().get() * 16));
   }

   public static float getNightVisionScale(LivingEntity var0, float var1) {
      MobEffectInstance var2 = var0.getEffect(MobEffects.NIGHT_VISION);
      return !var2.endsWithin(200) ? 1.0F : 0.7F + Mth.sin((double)(((float)var2.getDuration() - var1) * 3.1415927F * 0.2F)) * 0.3F;
   }

   public void render(DeltaTracker var1, boolean var2) {
      if (!this.minecraft.isWindowActive() && this.minecraft.options.pauseOnLostFocus && (!(Boolean)this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         ProfilerFiller var3 = Profiler.get();
         var3.push("camera");
         this.updateCamera(var1);
         var3.pop();
         this.globalSettingsUniform.update(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), (Double)this.minecraft.options.glintStrength().get(), this.minecraft.level == null ? 0L : this.minecraft.level.getGameTime(), var1, this.minecraft.options.getMenuBackgroundBlurriness(), this.mainCamera);
         boolean var4 = this.minecraft.isGameLoadFinished();
         int var5 = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
         int var6 = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
         if (var4 && var2 && this.minecraft.level != null) {
            var3.push("world");
            this.renderLevel(var1);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffectId != null && this.effectActive) {
               PostChain var7 = this.minecraft.getShaderManager().getPostChain(this.postEffectId, LevelTargetBundle.MAIN_TARGETS);
               if (var7 != null) {
                  var7.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
               }
            }

            var3.pop();
         }

         this.fogRenderer.endFrame();
         RenderTarget var17 = this.minecraft.getMainRenderTarget();
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var17.getDepthTexture(), 1.0);
         this.minecraft.gameRenderer.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
         this.guiRenderState.reset();
         var3.push("guiExtraction");
         GuiGraphics var8 = new GuiGraphics(this.minecraft, this.guiRenderState, var5, var6);
         if (var4 && var2 && this.minecraft.level != null) {
            this.minecraft.gui.render(var8, var1);
         }

         if (this.minecraft.getOverlay() != null) {
            try {
               this.minecraft.getOverlay().render(var8, var5, var6, var1.getGameTimeDeltaTicks());
            } catch (Throwable var15) {
               CrashReport var10 = CrashReport.forThrowable(var15, "Rendering overlay");
               CrashReportCategory var11 = var10.addCategory("Overlay render details");
               var11.setDetail("Overlay name", (CrashReportDetail)(() -> this.minecraft.getOverlay().getClass().getCanonicalName()));
               throw new ReportedException(var10);
            }
         } else if (var4 && this.minecraft.screen != null) {
            try {
               this.minecraft.screen.renderWithTooltipAndSubtitles(var8, var5, var6, var1.getGameTimeDeltaTicks());
            } catch (Throwable var14) {
               CrashReport var18 = CrashReport.forThrowable(var14, "Rendering screen");
               CrashReportCategory var20 = var18.addCategory("Screen render details");
               var20.setDetail("Screen name", (CrashReportDetail)(() -> this.minecraft.screen.getClass().getCanonicalName()));
               this.minecraft.mouseHandler.fillMousePositionDetails(var20, this.minecraft.getWindow());
               throw new ReportedException(var18);
            }

            if (SharedConstants.DEBUG_CURSOR_POS) {
               this.minecraft.mouseHandler.drawDebugMouseInfo(this.minecraft.font, var8);
            }

            try {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.handleDelayedNarration();
               }
            } catch (Throwable var13) {
               CrashReport var19 = CrashReport.forThrowable(var13, "Narrating screen");
               CrashReportCategory var21 = var19.addCategory("Screen details");
               var21.setDetail("Screen name", (CrashReportDetail)(() -> this.minecraft.screen.getClass().getCanonicalName()));
               throw new ReportedException(var19);
            }
         }

         if (var4 && var2 && this.minecraft.level != null) {
            this.minecraft.gui.renderSavingIndicator(var8, var1);
         }

         if (var4) {
            try (Zone var9 = var3.zone("toasts")) {
               this.minecraft.getToastManager().render(var8);
            }
         }

         if (!(this.minecraft.screen instanceof DebugOptionsScreen)) {
            this.minecraft.gui.renderDebugOverlay(var8);
         }

         this.minecraft.gui.renderDeferredSubtitles();
         if (SharedConstants.DEBUG_ACTIVE_TEXT_AREAS) {
            this.renderActiveTextDebug();
         }

         var3.popPush("guiRendering");
         this.guiRenderer.render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
         this.guiRenderer.incrementFrameNumber();
         var3.pop();
         var8.applyCursor(this.minecraft.getWindow());
         this.submitNodeStorage.endFrame();
         this.featureRenderDispatcher.endFrame();
         this.resourcePool.endFrame();
      }
   }

   private void renderActiveTextDebug() {
      this.guiRenderState.nextStratum();
      this.guiRenderState.forEachText((var1) -> var1.ensurePrepared().visit(new Font.GlyphVisitor() {
            private int index;

            public void acceptGlyph(TextRenderable.Styled var1x) {
               this.renderDebugMarkers(var1x, false);
            }

            public void acceptEmptyArea(EmptyArea var1x) {
               this.renderDebugMarkers(var1x, true);
            }

            private void renderDebugMarkers(ActiveArea var1x, boolean var2) {
               int var3 = (var2 ? 128 : 255) - (this.index++ & 1) * 64;
               Style var4 = var1x.style();
               int var5 = var4.getClickEvent() != null ? var3 : 0;
               int var6 = var4.getHoverEvent() != null ? var3 : 0;
               int var7 = var5 != 0 && var6 != 0 ? 0 : var3;
               int var8 = ARGB.color(128, var5, var6, var7);
               GameRenderer.this.guiRenderState.submitGuiElement(new ColoredRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), var1.pose, (int)var1x.activeLeft(), (int)var1x.activeTop(), (int)var1x.activeRight(), (int)var1x.activeBottom(), var8, var8, var1.scissor));
            }
         }));
   }

   private void tryTakeScreenshotIfNeeded() {
      if (!this.hasWorldScreenshot && this.minecraft.isLocalServer()) {
         long var1 = Util.getMillis();
         if (var1 - this.lastScreenshotAttempt >= 1000L) {
            this.lastScreenshotAttempt = var1;
            IntegratedServer var3 = this.minecraft.getSingleplayerServer();
            if (var3 != null && !var3.isStopped()) {
               var3.getWorldScreenshotFile().ifPresent((var1x) -> {
                  if (Files.isRegularFile(var1x, new LinkOption[0])) {
                     this.hasWorldScreenshot = true;
                  } else {
                     this.takeAutoScreenshot(var1x);
                  }

               });
            }
         }
      }
   }

   private void takeAutoScreenshot(Path var1) {
      if (this.minecraft.levelRenderer.countRenderedSections() > 10 && this.minecraft.levelRenderer.hasRenderedAllSections()) {
         Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget(), (var1x) -> Util.ioPool().execute(() -> {
               int var2 = var1x.getWidth();
               int var3 = var1x.getHeight();
               int var4 = 0;
               int var5 = 0;
               if (var2 > var3) {
                  var4 = (var2 - var3) / 2;
                  var2 = var3;
               } else {
                  var5 = (var3 - var2) / 2;
                  var3 = var2;
               }

               try {
                  NativeImage var6 = new NativeImage(64, 64, false);

                  try {
                     var1x.resizeSubRectTo(var4, var5, var2, var3, var6);
                     var6.writeToFile(var1);
                  } catch (Throwable var15) {
                     try {
                        var6.close();
                     } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                     }

                     throw var15;
                  }

                  var6.close();
               } catch (IOException var16) {
                  LOGGER.warn("Couldn't save auto screenshot", var16);
               } finally {
                  var1x.close();
               }

            }));
      }

   }

   private boolean shouldRenderBlockOutline() {
      if (!this.renderBlockOutline) {
         return false;
      } else {
         Entity var1 = this.minecraft.getCameraEntity();
         boolean var2 = var1 instanceof Player && !this.minecraft.options.hideGui;
         if (var2 && !((Player)var1).getAbilities().mayBuild) {
            ItemStack var3 = ((LivingEntity)var1).getMainHandItem();
            HitResult var4 = this.minecraft.hitResult;
            if (var4 != null && var4.getType() == HitResult.Type.BLOCK) {
               BlockPos var5 = ((BlockHitResult)var4).getBlockPos();
               BlockState var6 = this.minecraft.level.getBlockState(var5);
               if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                  var2 = var6.getMenuProvider(this.minecraft.level, var5) != null;
               } else {
                  BlockInWorld var7 = new BlockInWorld(this.minecraft.level, var5, false);
                  Registry var8 = this.minecraft.level.registryAccess().lookupOrThrow(Registries.BLOCK);
                  var2 = !var3.isEmpty() && (var3.canBreakBlockInAdventureMode(var7) || var3.canPlaceOnBlockInAdventureMode(var7));
               }
            }
         }

         return var2;
      }
   }

   public void updateCamera(DeltaTracker var1) {
      float var2 = var1.getGameTimeDeltaPartialTick(true);
      LocalPlayer var3 = this.minecraft.player;
      if (var3 != null && this.minecraft.level != null) {
         if (this.minecraft.getCameraEntity() == null) {
            this.minecraft.setCameraEntity(var3);
         }

         Object var4 = this.minecraft.getCameraEntity() == null ? var3 : this.minecraft.getCameraEntity();
         float var5 = this.minecraft.level.tickRateManager().isEntityFrozen((Entity)var4) ? 1.0F : var2;
         this.mainCamera.setup(this.minecraft.level, (Entity)var4, !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), var5);
      }
   }

   public void renderLevel(DeltaTracker var1) {
      float var2 = var1.getGameTimeDeltaPartialTick(true);
      LocalPlayer var3 = this.minecraft.player;
      this.lightTexture.updateLightTexture(1.0F);
      this.pick(var2);
      ProfilerFiller var4 = Profiler.get();
      boolean var5 = this.shouldRenderBlockOutline();
      this.extractCamera(var2);
      this.renderDistance = (float)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      var4.push("matrices");
      float var6 = this.getFov(this.mainCamera, var2, true);
      Matrix4f var7 = this.getProjectionMatrix(var6);
      PoseStack var8 = new PoseStack();
      this.bobHurt(var8, this.mainCamera.getPartialTickTime());
      if ((Boolean)this.minecraft.options.bobView().get()) {
         this.bobView(var8, this.mainCamera.getPartialTickTime());
      }

      var7.mul(var8.last().pose());
      float var9 = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
      float var10 = Mth.lerp(var2, var3.oPortalEffectIntensity, var3.portalEffectIntensity);
      float var11 = var3.getEffectBlendFactor(MobEffects.NAUSEA, var2);
      float var12 = Math.max(var10, var11) * var9 * var9;
      if (var12 > 0.0F) {
         float var13 = 5.0F / (var12 * var12 + 5.0F) - var12 * 0.04F;
         var13 *= var13;
         Vector3f var14 = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         float var15 = (this.spinningEffectTime + var2 * this.spinningEffectSpeed) * 0.017453292F;
         var7.rotate(var15, var14);
         var7.scale(1.0F / var13, 1.0F, 1.0F);
         var7.rotate(-var15, var14);
      }

      RenderSystem.setProjectionMatrix(this.levelProjectionMatrixBuffer.getBuffer(var7), ProjectionType.PERSPECTIVE);
      Quaternionf var21 = this.mainCamera.rotation().conjugate(new Quaternionf());
      Matrix4f var22 = (new Matrix4f()).rotation(var21);
      var4.popPush("fog");
      Vector4f var23 = this.fogRenderer.setupFog(this.mainCamera, this.minecraft.options.getEffectiveRenderDistance(), var1, this.getDarkenWorldAmount(var2), this.minecraft.level);
      GpuBufferSlice var16 = this.fogRenderer.getBuffer(FogRenderer.FogMode.WORLD);
      var4.popPush("level");
      boolean var17 = this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      this.minecraft.levelRenderer.renderLevel(this.resourcePool, var1, var5, this.mainCamera, var22, var7, this.getProjectionMatrixForCulling(var6), var16, var23, !var17);
      var4.popPush("hand");
      boolean var18 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
      RenderSystem.setProjectionMatrix(this.hud3dProjectionMatrixBuffer.getBuffer(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.getFov(this.mainCamera, var2, false)), ProjectionType.PERSPECTIVE);
      RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.minecraft.getMainRenderTarget().getDepthTexture(), 1.0);
      this.renderItemInHand(var2, var18, var22);
      var4.popPush("screenEffects");
      MultiBufferSource.BufferSource var19 = this.renderBuffers.bufferSource();
      this.screenEffectRenderer.renderScreenEffect(var18, var2, this.submitNodeStorage);
      this.featureRenderDispatcher.renderAllFeatures();
      var19.endBatch();
      var4.pop();
      RenderSystem.setShaderFog(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
      if (this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR) && this.minecraft.options.getCameraType().isFirstPerson() && !this.minecraft.options.hideGui) {
         this.minecraft.getDebugOverlay().render3dCrosshair(this.mainCamera);
      }

   }

   private void extractCamera(float var1) {
      CameraRenderState var2 = this.levelRenderState.cameraRenderState;
      var2.initialized = this.mainCamera.isInitialized();
      var2.pos = this.mainCamera.position();
      var2.blockPos = this.mainCamera.blockPosition();
      var2.entityPos = this.mainCamera.entity().getPosition(var1);
      var2.orientation = new Quaternionf(this.mainCamera.rotation());
   }

   private Matrix4f getProjectionMatrixForCulling(float var1) {
      float var2 = Math.max(var1, (float)(Integer)this.minecraft.options.fov().get());
      return this.getProjectionMatrix(var2);
   }

   public void resetData() {
      this.screenEffectRenderer.resetItemActivation();
      this.minecraft.getMapTextureManager().resetData();
      this.mainCamera.reset();
      this.hasWorldScreenshot = false;
   }

   public void displayItemActivation(ItemStack var1) {
      this.screenEffectRenderer.displayItemActivation(var1, this.random);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public float getDarkenWorldAmount(float var1) {
      return Mth.lerp(var1, this.darkenWorldAmountO, this.darkenWorldAmount);
   }

   public float getRenderDistance() {
      return this.renderDistance;
   }

   public Camera getMainCamera() {
      return this.mainCamera;
   }

   public LightTexture lightTexture() {
      return this.lightTexture;
   }

   public OverlayTexture overlayTexture() {
      return this.overlayTexture;
   }

   public Vec3 projectPointToScreen(Vec3 var1) {
      Matrix4f var2 = this.getProjectionMatrix(this.getFov(this.mainCamera, 0.0F, true));
      Quaternionf var3 = this.mainCamera.rotation().conjugate(new Quaternionf());
      Matrix4f var4 = (new Matrix4f()).rotation(var3);
      Matrix4f var5 = var2.mul(var4);
      Vec3 var6 = this.mainCamera.position();
      Vec3 var7 = var1.subtract(var6);
      Vector3f var8 = var5.transformProject(var7.toVector3f());
      return new Vec3(var8);
   }

   public double projectHorizonToScreen() {
      float var1 = this.mainCamera.xRot();
      if (var1 <= -90.0F) {
         return -1.0 / 0.0;
      } else if (var1 >= 90.0F) {
         return 1.0 / 0.0;
      } else {
         float var2 = this.getFov(this.mainCamera, 0.0F, true);
         return Math.tan((double)(var1 * 0.017453292F)) / Math.tan((double)(var2 / 2.0F * 0.017453292F));
      }
   }

   public GlobalSettingsUniform getGlobalSettingsUniform() {
      return this.globalSettingsUniform;
   }

   public Lighting getLighting() {
      return this.lighting;
   }

   public void setLevel(@Nullable ClientLevel var1) {
      if (var1 != null) {
         this.lighting.updateLevel(var1.dimensionType().cardinalLightType());
      }

   }

   public PanoramaRenderer getPanorama() {
      return this.panorama;
   }
}
