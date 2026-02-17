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
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.jtracy.TracyClient;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.TextureFilteringMethod;
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
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.state.LevelRenderState;
import net.minecraft.client.renderer.state.LightmapRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.TrackedWaypoint;
import org.apache.commons.io.IOUtils;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Matrix4fc;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class GameRenderer implements AutoCloseable, TrackedWaypoint.Projector {
   private static final Identifier BLUR_POST_CHAIN_ID = Identifier.withDefaultNamespace("blur");
   public static final int MAX_BLUR_RADIUS = 10;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final float PROJECTION_3D_HUD_Z_FAR = 100.0F;
   private static final float PORTAL_SPINNING_SPEED = 20.0F;
   private static final float NAUSEA_SPINNING_SPEED = 7.0F;
   private final Minecraft minecraft;
   private final RandomSource random = RandomSource.create();
   public final ItemInHandRenderer itemInHandRenderer;
   private final ScreenEffectRenderer screenEffectRenderer;
   private final RenderBuffers renderBuffers;
   private float spinningEffectTime;
   private float spinningEffectSpeed;
   private float bossOverlayWorldDarkening;
   private float bossOverlayWorldDarkeningO;
   private boolean renderBlockOutline = true;
   private long lastScreenshotAttempt;
   private boolean hasWorldScreenshot;
   private long lastActiveTime = Util.getMillis();
   private final Lightmap lightmap = new Lightmap();
   private final LightmapRenderStateExtractor lightmapRenderStateExtractor;
   private final LightmapRenderState lightmapRenderState = new LightmapRenderState();
   private final UiLightmap uiLightmap = new UiLightmap();
   private boolean useUiLightmap;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   protected final CubeMap cubeMap = new CubeMap(Identifier.withDefaultNamespace("textures/gui/title/background/panorama"));
   protected final PanoramaRenderer panorama;
   private final CrossFrameResourcePool resourcePool;
   private final FogRenderer fogRenderer;
   private final GuiRenderer guiRenderer;
   private final GuiRenderState guiRenderState;
   private final LevelRenderState levelRenderState;
   private final SubmitNodeStorage submitNodeStorage;
   private final FeatureRenderDispatcher featureRenderDispatcher;
   private @Nullable Identifier postEffectId;
   private boolean effectActive;
   private final Camera mainCamera;
   private final Projection hudProjection;
   private final Lighting lighting;
   private final GlobalSettingsUniform globalSettingsUniform;
   private final ProjectionMatrixBuffer levelProjectionMatrixBuffer;
   private final ProjectionMatrixBuffer hud3dProjectionMatrixBuffer;

   public GameRenderer(final Minecraft minecraft, final ItemInHandRenderer itemInHandRenderer, final RenderBuffers renderBuffers, final BlockRenderDispatcher blockRenderer) {
      super();
      this.panorama = new PanoramaRenderer(this.cubeMap);
      this.resourcePool = new CrossFrameResourcePool(3);
      this.fogRenderer = new FogRenderer();
      this.levelRenderState = new LevelRenderState();
      this.mainCamera = new Camera();
      this.hudProjection = new Projection();
      this.lighting = new Lighting();
      this.globalSettingsUniform = new GlobalSettingsUniform();
      this.levelProjectionMatrixBuffer = new ProjectionMatrixBuffer("level");
      this.hud3dProjectionMatrixBuffer = new ProjectionMatrixBuffer("3d hud");
      this.minecraft = minecraft;
      this.itemInHandRenderer = itemInHandRenderer;
      this.lightmapRenderStateExtractor = new LightmapRenderStateExtractor(this, minecraft);
      this.renderBuffers = renderBuffers;
      this.guiRenderState = new GuiRenderState();
      MultiBufferSource.BufferSource bufferSource = renderBuffers.bufferSource();
      AtlasManager atlasManager = minecraft.getAtlasManager();
      this.submitNodeStorage = new SubmitNodeStorage();
      this.featureRenderDispatcher = new FeatureRenderDispatcher(this.submitNodeStorage, blockRenderer, bufferSource, atlasManager, renderBuffers.outlineBufferSource(), renderBuffers.crumblingBufferSource(), minecraft.font);
      this.guiRenderer = new GuiRenderer(this.guiRenderState, bufferSource, this.submitNodeStorage, this.featureRenderDispatcher, List.of(new GuiEntityRenderer(bufferSource, minecraft.getEntityRenderDispatcher()), new GuiSkinRenderer(bufferSource), new GuiBookModelRenderer(bufferSource), new GuiBannerResultRenderer(bufferSource, atlasManager), new GuiSignRenderer(bufferSource, atlasManager), new GuiProfilerChartRenderer(bufferSource)));
      this.screenEffectRenderer = new ScreenEffectRenderer(minecraft, atlasManager, bufferSource);
   }

   public void close() {
      this.globalSettingsUniform.close();
      this.lightmap.close();
      this.overlayTexture.close();
      this.uiLightmap.close();
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

   public void setRenderBlockOutline(final boolean renderBlockOutline) {
      this.renderBlockOutline = renderBlockOutline;
   }

   public void clearPostEffect() {
      this.postEffectId = null;
      this.effectActive = false;
   }

   public void togglePostEffect() {
      this.effectActive = !this.effectActive;
   }

   public void checkEntityPostEffect(final @Nullable Entity cameraEntity) {
      byte var3 = 0;
      //$FF: var3->value
      //0->net/minecraft/world/entity/monster/Creeper
      //1->net/minecraft/world/entity/monster/spider/Spider
      //2->net/minecraft/world/entity/monster/EnderMan
      switch (cameraEntity.typeSwitch<invokedynamic>(cameraEntity, var3)) {
         case -1:
         default:
            this.clearPostEffect();
            break;
         case 0:
            Creeper ignored = (Creeper)cameraEntity;
            this.setPostEffect(Identifier.withDefaultNamespace("creeper"));
            break;
         case 1:
            Spider ignored = (Spider)cameraEntity;
            this.setPostEffect(Identifier.withDefaultNamespace("spider"));
            break;
         case 2:
            EnderMan ignored = (EnderMan)cameraEntity;
            this.setPostEffect(Identifier.withDefaultNamespace("invert"));
      }

   }

   private void setPostEffect(final Identifier id) {
      this.postEffectId = id;
      this.effectActive = true;
   }

   public void processBlurEffect() {
      PostChain postChain = this.minecraft.getShaderManager().getPostChain(BLUR_POST_CHAIN_ID, LevelTargetBundle.MAIN_TARGETS);
      if (postChain != null) {
         postChain.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
      }

   }

   public void preloadUiShader(final ResourceProvider resourceProvider) {
      GpuDevice device = RenderSystem.getDevice();
      ShaderSource shaderSource = (id, type) -> {
         Identifier location = type.idConverter().idToFile(id);

         try {
            Reader reader = resourceProvider.getResourceOrThrow(location).openAsReader();

            String var5;
            try {
               var5 = IOUtils.toString(reader);
            } catch (Throwable var8) {
               if (reader != null) {
                  try {
                     reader.close();
                  } catch (Throwable x2) {
                     var8.addSuppressed(x2);
                  }
               }

               throw var8;
            }

            if (reader != null) {
               reader.close();
            }

            return var5;
         } catch (IOException exception) {
            LOGGER.error("Coudln't preload {} shader {}: {}", new Object[]{type, id, exception});
            return null;
         }
      };
      device.precompilePipeline(RenderPipelines.GUI, shaderSource);
      device.precompilePipeline(RenderPipelines.GUI_TEXTURED, shaderSource);
      if (TracyClient.isAvailable()) {
         device.precompilePipeline(RenderPipelines.TRACY_BLIT, shaderSource);
      }

   }

   public void tick() {
      this.lightmapRenderStateExtractor.tick();
      LocalPlayer player = this.minecraft.player;
      if (this.mainCamera.entity() == null) {
         this.mainCamera.setEntity(player);
      }

      this.mainCamera.tick();
      this.itemInHandRenderer.tick();
      float portalIntensity = player.portalEffectIntensity;
      float nauseaIntensity = player.getEffectBlendFactor(MobEffects.NAUSEA, 1.0F);
      if (!(portalIntensity > 0.0F) && !(nauseaIntensity > 0.0F)) {
         this.spinningEffectSpeed = 0.0F;
      } else {
         this.spinningEffectSpeed = (portalIntensity * 20.0F + nauseaIntensity * 7.0F) / (portalIntensity + nauseaIntensity);
         this.spinningEffectTime += this.spinningEffectSpeed;
      }

      if (this.minecraft.level.tickRateManager().runsNormally()) {
         this.bossOverlayWorldDarkeningO = this.bossOverlayWorldDarkening;
         if (this.minecraft.gui.getBossOverlay().shouldDarkenScreen()) {
            this.bossOverlayWorldDarkening += 0.05F;
            if (this.bossOverlayWorldDarkening > 1.0F) {
               this.bossOverlayWorldDarkening = 1.0F;
            }
         } else if (this.bossOverlayWorldDarkening > 0.0F) {
            this.bossOverlayWorldDarkening -= 0.0125F;
         }

         this.screenEffectRenderer.tick();
         ProfilerFiller profiler = Profiler.get();
         profiler.push("levelRenderer");
         this.minecraft.levelRenderer.tick(this.mainCamera);
         profiler.pop();
      }
   }

   public @Nullable Identifier currentPostEffect() {
      return this.postEffectId;
   }

   public void resize(final int width, final int height) {
      this.resourcePool.clear();
      this.minecraft.levelRenderer.resize(width, height);
   }

   public void pick(final float a) {
      Entity cameraEntity = this.minecraft.getCameraEntity();
      if (cameraEntity != null) {
         if (this.minecraft.level != null && this.minecraft.player != null) {
            Profiler.get().push("pick");
            this.minecraft.hitResult = this.minecraft.player.raycastHitResult(a, cameraEntity);
            Minecraft var10000 = this.minecraft;
            HitResult var4 = this.minecraft.hitResult;
            Entity var10001;
            if (var4 instanceof EntityHitResult) {
               EntityHitResult entityHitResult = (EntityHitResult)var4;
               var10001 = entityHitResult.getEntity();
            } else {
               var10001 = null;
            }

            var10000.crosshairPickEntity = var10001;
            Profiler.get().pop();
         }
      }
   }

   private void bobHurt(final CameraRenderState cameraState, final PoseStack poseStack) {
      if (cameraState.entityRenderState.isLiving) {
         float hurt = cameraState.entityRenderState.hurtTime;
         if (cameraState.entityRenderState.isDeadOrDying) {
            float duration = Math.min(cameraState.entityRenderState.deathTime, 20.0F);
            poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(40.0F - 8000.0F / (duration + 200.0F)));
         }

         if (hurt < 0.0F) {
            return;
         }

         hurt /= (float)cameraState.entityRenderState.hurtDuration;
         hurt = Mth.sin((double)(hurt * hurt * hurt * hurt * 3.1415927F));
         float rr = cameraState.entityRenderState.hurtDir;
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(-rr));
         float tiltAmount = (float)((double)(-hurt) * 14.0 * (Double)this.minecraft.options.damageTiltStrength().get());
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(tiltAmount));
         poseStack.mulPose((Quaternionfc)Axis.YP.rotationDegrees(rr));
      }

   }

   private void bobView(final CameraRenderState cameraState, final PoseStack poseStack) {
      if (cameraState.entityRenderState.isPlayer) {
         float backwardsInterpolatedWalkDistance = cameraState.entityRenderState.backwardsInterpolatedWalkDistance;
         float bob = cameraState.entityRenderState.bob;
         poseStack.translate(Mth.sin((double)(backwardsInterpolatedWalkDistance * 3.1415927F)) * bob * 0.5F, -Math.abs(Mth.cos((double)(backwardsInterpolatedWalkDistance * 3.1415927F)) * bob), 0.0F);
         poseStack.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.sin((double)(backwardsInterpolatedWalkDistance * 3.1415927F)) * bob * 3.0F));
         poseStack.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Math.abs(Mth.cos((double)(backwardsInterpolatedWalkDistance * 3.1415927F - 0.2F)) * bob) * 5.0F));
      }
   }

   private void renderItemInHand(final CameraRenderState cameraState, final float deltaPartialTick, final Matrix4f modelViewMatrix) {
      if (!cameraState.isPanoramicMode) {
         this.featureRenderDispatcher.renderAllFeatures();
         this.renderBuffers.bufferSource().endBatch();
         PoseStack poseStack = new PoseStack();
         poseStack.pushPose();
         poseStack.mulPose((Matrix4fc)modelViewMatrix.invert(new Matrix4f()));
         Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
         modelViewStack.pushMatrix().mul(modelViewMatrix);
         this.bobHurt(cameraState, poseStack);
         if ((Boolean)this.minecraft.options.bobView().get()) {
            this.bobView(cameraState, poseStack);
         }

         if (this.minecraft.options.getCameraType().isFirstPerson() && !cameraState.entityRenderState.isSleeping && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.itemInHandRenderer.renderHandsWithItems(deltaPartialTick, poseStack, this.minecraft.gameRenderer.getSubmitNodeStorage(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, deltaPartialTick));
         }

         modelViewStack.popMatrix();
         poseStack.popPose();
      }
   }

   public static float getNightVisionScale(final LivingEntity camera, final float a) {
      MobEffectInstance nightVision = camera.getEffect(MobEffects.NIGHT_VISION);
      return !nightVision.endsWithin(200) ? 1.0F : 0.7F + Mth.sin((double)(((float)nightVision.getDuration() - a) * 3.1415927F * 0.2F)) * 0.3F;
   }

   public void render(final DeltaTracker deltaTracker, final boolean renderLevel) {
      if (!this.minecraft.isWindowActive() && this.minecraft.options.pauseOnLostFocus && (!(Boolean)this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         ProfilerFiller profiler = Profiler.get();
         profiler.push("camera");
         this.mainCamera.update(deltaTracker);
         profiler.pop();
         this.globalSettingsUniform.update(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), (Double)this.minecraft.options.glintStrength().get(), this.minecraft.level == null ? 0L : this.minecraft.level.getGameTime(), deltaTracker, this.minecraft.options.getMenuBackgroundBlurriness(), this.mainCamera, this.minecraft.options.textureFiltering().get() == TextureFilteringMethod.RGSS);
         boolean resourcesLoaded = this.minecraft.isGameLoadFinished();
         int xMouse = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
         int yMouse = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
         if (resourcesLoaded && renderLevel && this.minecraft.level != null) {
            profiler.push("world");
            this.minecraft.levelRenderer.update(this.mainCamera);
            this.renderLevel(deltaTracker);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffectId != null && this.effectActive) {
               PostChain postChain = this.minecraft.getShaderManager().getPostChain(this.postEffectId, LevelTargetBundle.MAIN_TARGETS);
               if (postChain != null) {
                  postChain.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
               }
            }

            profiler.pop();
         }

         this.fogRenderer.endFrame();
         RenderTarget mainRenderTarget = this.minecraft.getMainRenderTarget();
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(mainRenderTarget.getDepthTexture(), 1.0);
         this.getLighting().setupFor(Lighting.Entry.ITEMS_3D);
         this.useUiLightmap = true;
         this.guiRenderState.reset();
         profiler.push("guiExtraction");
         GuiGraphics graphics = new GuiGraphics(this.minecraft, this.guiRenderState, xMouse, yMouse);
         if (resourcesLoaded && renderLevel && this.minecraft.level != null) {
            this.minecraft.gui.render(graphics, deltaTracker);
         }

         if (this.minecraft.getOverlay() != null) {
            try {
               this.minecraft.getOverlay().render(graphics, xMouse, yMouse, deltaTracker.getGameTimeDeltaTicks());
            } catch (Throwable t) {
               CrashReport report = CrashReport.forThrowable(t, "Rendering overlay");
               CrashReportCategory category = report.addCategory("Overlay render details");
               category.setDetail("Overlay name", (CrashReportDetail)(() -> this.minecraft.getOverlay().getClass().getCanonicalName()));
               throw new ReportedException(report);
            }
         } else if (resourcesLoaded && this.minecraft.screen != null) {
            try {
               this.minecraft.screen.renderWithTooltipAndSubtitles(graphics, xMouse, yMouse, deltaTracker.getGameTimeDeltaTicks());
            } catch (Throwable t) {
               CrashReport report = CrashReport.forThrowable(t, "Rendering screen");
               CrashReportCategory category = report.addCategory("Screen render details");
               category.setDetail("Screen name", (CrashReportDetail)(() -> this.minecraft.screen.getClass().getCanonicalName()));
               this.minecraft.mouseHandler.fillMousePositionDetails(category, this.minecraft.getWindow());
               throw new ReportedException(report);
            }

            if (SharedConstants.DEBUG_CURSOR_POS) {
               this.minecraft.mouseHandler.drawDebugMouseInfo(this.minecraft.font, graphics);
            }

            try {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.handleDelayedNarration();
               }
            } catch (Throwable t) {
               CrashReport report = CrashReport.forThrowable(t, "Narrating screen");
               CrashReportCategory category = report.addCategory("Screen details");
               category.setDetail("Screen name", (CrashReportDetail)(() -> this.minecraft.screen.getClass().getCanonicalName()));
               throw new ReportedException(report);
            }
         }

         if (resourcesLoaded && renderLevel && this.minecraft.level != null) {
            this.minecraft.gui.renderSavingIndicator(graphics, deltaTracker);
         }

         if (resourcesLoaded) {
            try (Zone ignored = profiler.zone("toasts")) {
               this.minecraft.getToastManager().render(graphics);
            }
         }

         if (!(this.minecraft.screen instanceof DebugOptionsScreen)) {
            this.minecraft.gui.renderDebugOverlay(graphics);
         }

         this.minecraft.gui.renderDeferredSubtitles();
         if (SharedConstants.DEBUG_ACTIVE_TEXT_AREAS) {
            this.renderActiveTextDebug();
         }

         profiler.popPush("guiRendering");
         this.guiRenderer.render(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
         this.guiRenderer.endFrame();
         profiler.pop();
         this.useUiLightmap = false;
         graphics.applyCursor(this.minecraft.getWindow());
         this.submitNodeStorage.endFrame();
         this.featureRenderDispatcher.endFrame();
         this.resourcePool.endFrame();
      }
   }

   private void renderActiveTextDebug() {
      this.guiRenderState.nextStratum();
      this.guiRenderState.forEachText((text) -> text.ensurePrepared().visit(new Font.GlyphVisitor() {
            private int index;

            {
               Objects.requireNonNull(GameRenderer.this);
            }

            public void acceptGlyph(final TextRenderable.Styled glyph) {
               this.renderDebugMarkers(glyph, false);
            }

            public void acceptEmptyArea(final EmptyArea empty) {
               this.renderDebugMarkers(empty, true);
            }

            private void renderDebugMarkers(final ActiveArea glyph, final boolean isEmpty) {
               int intensity = (isEmpty ? 128 : 255) - (this.index++ & 1) * 64;
               Style style = glyph.style();
               int red = style.getClickEvent() != null ? intensity : 0;
               int green = style.getHoverEvent() != null ? intensity : 0;
               int blue = red != 0 && green != 0 ? 0 : intensity;
               int color = ARGB.color(128, red, green, blue);
               GameRenderer.this.guiRenderState.submitGuiElement(new ColoredRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), text.pose, (int)glyph.activeLeft(), (int)glyph.activeTop(), (int)glyph.activeRight(), (int)glyph.activeBottom(), color, color, text.scissor));
            }
         }));
   }

   private void tryTakeScreenshotIfNeeded() {
      if (!this.hasWorldScreenshot && this.minecraft.isLocalServer()) {
         long time = Util.getMillis();
         if (time - this.lastScreenshotAttempt >= 1000L) {
            this.lastScreenshotAttempt = time;
            IntegratedServer server = this.minecraft.getSingleplayerServer();
            if (server != null && !server.isStopped()) {
               server.getWorldScreenshotFile().ifPresent((path) -> {
                  if (Files.isRegularFile(path, new LinkOption[0])) {
                     this.hasWorldScreenshot = true;
                  } else {
                     this.takeAutoScreenshot(path);
                  }

               });
            }
         }
      }
   }

   private void takeAutoScreenshot(final Path screenshotFile) {
      if (this.minecraft.levelRenderer.countRenderedSections() > 10 && this.minecraft.levelRenderer.hasRenderedAllSections()) {
         Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget(), (screenshot) -> Util.ioPool().execute(() -> {
               int width = screenshot.getWidth();
               int height = screenshot.getHeight();
               int x = 0;
               int y = 0;
               if (width > height) {
                  x = (width - height) / 2;
                  width = height;
               } else {
                  y = (height - width) / 2;
                  height = width;
               }

               try {
                  NativeImage scaled = new NativeImage(64, 64, false);

                  try {
                     screenshot.resizeSubRectTo(x, y, width, height, scaled);
                     scaled.writeToFile(screenshotFile);
                  } catch (Throwable var15) {
                     try {
                        scaled.close();
                     } catch (Throwable x2) {
                        var15.addSuppressed(x2);
                     }

                     throw var15;
                  }

                  scaled.close();
               } catch (IOException e) {
                  LOGGER.warn("Couldn't save auto screenshot", e);
               } finally {
                  screenshot.close();
               }

            }));
      }

   }

   private boolean shouldRenderBlockOutline() {
      if (!this.renderBlockOutline) {
         return false;
      } else {
         Entity cameraEntity = this.minecraft.getCameraEntity();
         boolean renderOutline = cameraEntity instanceof Player && !this.minecraft.options.hideGui;
         if (renderOutline && !((Player)cameraEntity).getAbilities().mayBuild) {
            ItemStack itemStack = ((LivingEntity)cameraEntity).getMainHandItem();
            HitResult hitResult = this.minecraft.hitResult;
            if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
               BlockPos pos = ((BlockHitResult)hitResult).getBlockPos();
               BlockState blockState = this.minecraft.level.getBlockState(pos);
               if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                  renderOutline = blockState.getMenuProvider(this.minecraft.level, pos) != null;
               } else {
                  BlockInWorld blockInWorld = new BlockInWorld(this.minecraft.level, pos, false);
                  Registry<Block> blockRegistry = this.minecraft.level.registryAccess().lookupOrThrow(Registries.BLOCK);
                  renderOutline = !itemStack.isEmpty() && (itemStack.canBreakBlockInAdventureMode(blockInWorld) || itemStack.canPlaceOnBlockInAdventureMode(blockInWorld));
               }
            }
         }

         return renderOutline;
      }
   }

   public void renderLevel(final DeltaTracker deltaTracker) {
      float worldPartialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
      float cameraEntityPartialTicks = this.mainCamera.getCameraEntityPartialTicks(deltaTracker);
      LocalPlayer player = this.minecraft.player;
      this.lightmapRenderStateExtractor.extract(this.lightmapRenderState, 1.0F);
      this.lightmap.update(this.lightmapRenderState);
      this.pick(worldPartialTicks);
      ProfilerFiller profiler = Profiler.get();
      boolean renderOutline = this.shouldRenderBlockOutline();
      this.extractCamera(deltaTracker, worldPartialTicks, cameraEntityPartialTicks);
      CameraRenderState cameraState = this.levelRenderState.cameraRenderState;
      Matrix4f modelViewMatrix = cameraState.viewRotationMatrix;
      profiler.push("perapareChunkDraws");
      ChunkSectionsToRender chunkSectionsToRender = this.minecraft.levelRenderer.prepareChunkRenders(modelViewMatrix);
      profiler.popPush("extractLevel");
      this.minecraft.levelRenderer.extractLevel(deltaTracker, this.mainCamera, worldPartialTicks);
      profiler.popPush("matrices");
      Matrix4f projectionMatrix = new Matrix4f(cameraState.projectionMatrix);
      PoseStack bobStack = new PoseStack();
      this.bobHurt(cameraState, bobStack);
      if ((Boolean)this.minecraft.options.bobView().get()) {
         this.bobView(cameraState, bobStack);
      }

      projectionMatrix.mul(bobStack.last().pose());
      float screenEffectScale = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
      float portalIntensity = Mth.lerp(worldPartialTicks, player.oPortalEffectIntensity, player.portalEffectIntensity);
      float nauseaIntensity = player.getEffectBlendFactor(MobEffects.NAUSEA, worldPartialTicks);
      float spinningEffectIntensity = Math.max(portalIntensity, nauseaIntensity) * screenEffectScale * screenEffectScale;
      if (spinningEffectIntensity > 0.0F) {
         float skew = 5.0F / (spinningEffectIntensity * spinningEffectIntensity + 5.0F) - spinningEffectIntensity * 0.04F;
         skew *= skew;
         Vector3f axis = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         float angle = (this.spinningEffectTime + worldPartialTicks * this.spinningEffectSpeed) * 0.017453292F;
         projectionMatrix.rotate(angle, axis);
         projectionMatrix.scale(1.0F / skew, 1.0F, 1.0F);
         projectionMatrix.rotate(-angle, axis);
      }

      RenderSystem.setProjectionMatrix(this.levelProjectionMatrixBuffer.getBuffer(projectionMatrix), ProjectionType.PERSPECTIVE);
      profiler.popPush("fog");
      this.fogRenderer.updateBuffer(cameraState.fogData);
      GpuBufferSlice terrainFog = this.fogRenderer.getBuffer(FogRenderer.FogMode.WORLD);
      profiler.popPush("level");
      boolean shouldCreateBossFog = this.minecraft.gui.getBossOverlay().shouldCreateWorldFog();
      this.minecraft.levelRenderer.renderLevel(this.resourcePool, deltaTracker, renderOutline, cameraState, modelViewMatrix, terrainFog, cameraState.fogData.color, !shouldCreateBossFog, chunkSectionsToRender);
      profiler.popPush("hand");
      boolean isSleeping = cameraState.entityRenderState.isSleeping;
      this.hudProjection.setupPerspective(0.05F, 100.0F, cameraState.hudFov, (float)this.minecraft.getWindow().getWidth(), (float)this.minecraft.getWindow().getHeight());
      RenderSystem.setProjectionMatrix(this.hud3dProjectionMatrixBuffer.getBuffer(this.hudProjection), ProjectionType.PERSPECTIVE);
      RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.minecraft.getMainRenderTarget().getDepthTexture(), 1.0);
      this.renderItemInHand(cameraState, cameraEntityPartialTicks, modelViewMatrix);
      profiler.popPush("screenEffects");
      MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
      this.screenEffectRenderer.renderScreenEffect(isSleeping, worldPartialTicks, this.submitNodeStorage);
      this.featureRenderDispatcher.renderAllFeatures();
      bufferSource.endBatch();
      profiler.pop();
      RenderSystem.setShaderFog(this.fogRenderer.getBuffer(FogRenderer.FogMode.NONE));
      if (this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR) && this.minecraft.options.getCameraType().isFirstPerson() && !this.minecraft.options.hideGui) {
         this.minecraft.getDebugOverlay().render3dCrosshair(cameraState);
      }

   }

   private void extractCamera(final DeltaTracker deltaTracker, final float worldPartialTicks, final float cameraEntityPartialTicks) {
      CameraRenderState cameraState = this.levelRenderState.cameraRenderState;
      this.mainCamera.extractRenderState(cameraState, cameraEntityPartialTicks);
      cameraState.fogType = this.mainCamera.getFluidInCamera();
      cameraState.fogData = this.fogRenderer.setupFog(this.mainCamera, this.minecraft.options.getEffectiveRenderDistance(), deltaTracker, this.getBossOverlayWorldDarkening(worldPartialTicks), this.minecraft.level);
   }

   public void resetData() {
      this.screenEffectRenderer.resetItemActivation();
      this.minecraft.getMapTextureManager().resetData();
      this.mainCamera.reset();
      this.hasWorldScreenshot = false;
   }

   public void displayItemActivation(final ItemStack itemStack) {
      this.screenEffectRenderer.displayItemActivation(itemStack, this.random);
   }

   public Minecraft getMinecraft() {
      return this.minecraft;
   }

   public float getBossOverlayWorldDarkening(final float a) {
      return Mth.lerp(a, this.bossOverlayWorldDarkeningO, this.bossOverlayWorldDarkening);
   }

   public Camera getMainCamera() {
      return this.mainCamera;
   }

   public GpuTextureView lightmap() {
      return this.useUiLightmap ? this.uiLightmap.getTextureView() : this.lightmap.getTextureView();
   }

   public GpuTextureView levelLightmap() {
      return this.lightmap.getTextureView();
   }

   public OverlayTexture overlayTexture() {
      return this.overlayTexture;
   }

   public Vec3 projectPointToScreen(final Vec3 point) {
      Matrix4f mvp = this.mainCamera.getViewRotationProjectionMatrix(new Matrix4f());
      Vec3 camPos = this.mainCamera.position();
      Vec3 offset = point.subtract(camPos);
      Vector3f vector3f = mvp.transformProject(offset.toVector3f());
      return new Vec3(vector3f);
   }

   public double projectHorizonToScreen() {
      float xRot = this.mainCamera.xRot();
      if (xRot <= -90.0F) {
         return -1.0 / 0.0;
      } else if (xRot >= 90.0F) {
         return 1.0 / 0.0;
      } else {
         float fov = this.mainCamera.getFov();
         return Math.tan((double)(xRot * 0.017453292F)) / Math.tan((double)(fov / 2.0F * 0.017453292F));
      }
   }

   public GlobalSettingsUniform getGlobalSettingsUniform() {
      return this.globalSettingsUniform;
   }

   public Lighting getLighting() {
      return this.lighting;
   }

   public void setLevel(final @Nullable ClientLevel level) {
      if (level != null) {
         this.lighting.updateLevel(level.dimensionType().cardinalLightType());
      }

      this.mainCamera.setLevel(level);
   }

   public PanoramaRenderer getPanorama() {
      return this.panorama;
   }
}
