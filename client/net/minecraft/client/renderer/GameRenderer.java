package net.minecraft.client.renderer;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
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
import java.util.function.BiFunction;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.GuiLayer;
import net.minecraft.client.gui.render.GuiRenderer;
import net.minecraft.client.gui.render.pip.GuiBannerResultRenderer;
import net.minecraft.client.gui.render.pip.GuiBookModelRenderer;
import net.minecraft.client.gui.render.pip.GuiEntityRenderer;
import net.minecraft.client.gui.render.pip.GuiProfilerChartRenderer;
import net.minecraft.client.gui.render.pip.GuiSignRenderer;
import net.minecraft.client.gui.render.pip.GuiSkinRenderer;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import org.slf4j.Logger;

public class GameRenderer implements TrackedWaypoint.Projector, AutoCloseable {
   private static final ResourceLocation BLUR_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("blur");
   public static final int MAX_BLUR_RADIUS = 10;
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final float PROJECTION_Z_NEAR = 0.05F;
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
   private boolean panoramicMode;
   private final CrossFrameResourcePool resourcePool = new CrossFrameResourcePool(3);
   private final GuiRenderer guiRenderer;
   private final GuiRenderState guiRenderState;
   @Nullable
   private ResourceLocation postEffectId;
   private boolean effectActive;
   private final Camera mainCamera = new Camera();

   public GameRenderer(Minecraft var1, ItemInHandRenderer var2, RenderBuffers var3) {
      super();
      this.minecraft = var1;
      this.itemInHandRenderer = var2;
      this.lightTexture = new LightTexture(this, var1);
      this.renderBuffers = var3;
      this.guiRenderState = new GuiRenderState();
      MultiBufferSource.BufferSource var4 = var3.bufferSource();
      this.guiRenderer = new GuiRenderer(this.guiRenderState, var4, List.of(new GuiEntityRenderer(var4, var1.getEntityRenderDispatcher()), new GuiSkinRenderer(var4), new GuiBookModelRenderer(var4), new GuiBannerResultRenderer(var4), new GuiSignRenderer(var4), new GuiProfilerChartRenderer(var4)));
      this.screenEffectRenderer = new ScreenEffectRenderer(var1, var4);
   }

   public void close() {
      this.lightTexture.close();
      this.overlayTexture.close();
      this.resourcePool.close();
      this.guiRenderer.close();
   }

   public void setRenderBlockOutline(boolean var1) {
      this.renderBlockOutline = var1;
   }

   public void setPanoramicMode(boolean var1) {
      this.panoramicMode = var1;
   }

   public boolean isPanoramicMode() {
      return this.panoramicMode;
   }

   public void clearPostEffect() {
      this.postEffectId = null;
   }

   public void togglePostEffect() {
      this.effectActive = !this.effectActive;
   }

   public void checkEntityPostEffect(@Nullable Entity var1) {
      this.postEffectId = null;
      if (var1 instanceof Creeper) {
         this.setPostEffect(ResourceLocation.withDefaultNamespace("creeper"));
      } else if (var1 instanceof Spider) {
         this.setPostEffect(ResourceLocation.withDefaultNamespace("spider"));
      } else if (var1 instanceof EnderMan) {
         this.setPostEffect(ResourceLocation.withDefaultNamespace("invert"));
      }

   }

   private void setPostEffect(ResourceLocation var1) {
      this.postEffectId = var1;
      this.effectActive = true;
   }

   public void processBlurEffect() {
      float var1 = (float)this.minecraft.options.getMenuBackgroundBlurriness();
      if (!(var1 < 1.0F)) {
         PostChain var2 = this.minecraft.getShaderManager().getPostChain(BLUR_POST_CHAIN_ID, LevelTargetBundle.MAIN_TARGETS);
         if (var2 != null) {
            var2.process(this.minecraft.getMainRenderTarget(), this.resourcePool, (var1x) -> var1x.setUniform("Radius", var1));
         }

      }
   }

   public void preloadUiShader(ResourceProvider var1) {
      GpuDevice var2 = RenderSystem.getDevice();
      BiFunction var3 = (var1x, var2x) -> {
         ResourceLocation var3 = var2x.idConverter().idToFile(var1x);

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
         this.minecraft.levelRenderer.tickParticles(this.mainCamera);
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
      }
   }

   @Nullable
   public ResourceLocation currentPostEffect() {
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
      if (this.panoramicMode) {
         return 90.0F;
      } else {
         float var4 = 70.0F;
         if (var3) {
            var4 = (float)(Integer)this.minecraft.options.fov().get();
            var4 *= Mth.lerp(var2, this.oldFovModifier, this.fovModifier);
         }

         Entity var6 = var1.getEntity();
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
         var7 = Mth.sin(var7 * var7 * var7 * var7 * 3.1415927F);
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
         float var7 = var3.walkDist - var3.walkDistO;
         float var5 = -(var3.walkDist + var7 * var2);
         float var6 = Mth.lerp(var2, var3.oBob, var3.bob);
         var1.translate(Mth.sin(var5 * 3.1415927F) * var6 * 0.5F, -Math.abs(Mth.cos(var5 * 3.1415927F) * var6), 0.0F);
         var1.mulPose((Quaternionfc)Axis.ZP.rotationDegrees(Mth.sin(var5 * 3.1415927F) * var6 * 3.0F));
         var1.mulPose((Quaternionfc)Axis.XP.rotationDegrees(Math.abs(Mth.cos(var5 * 3.1415927F - 0.2F) * var6) * 5.0F));
      }
   }

   private void renderItemInHand(float var1, boolean var2, Matrix4f var3) {
      if (!this.panoramicMode) {
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
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems(var1, var4, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, var1));
            this.lightTexture.turnOffLightLayer();
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
      return this.renderDistance * 4.0F;
   }

   public static float getNightVisionScale(LivingEntity var0, float var1) {
      MobEffectInstance var2 = var0.getEffect(MobEffects.NIGHT_VISION);
      return !var2.endsWithin(200) ? 1.0F : 0.7F + Mth.sin(((float)var2.getDuration() - var1) * 3.1415927F * 0.2F) * 0.3F;
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
         boolean var4 = this.minecraft.isGameLoadFinished();
         int var5 = (int)this.minecraft.mouseHandler.getScaledXPos(this.minecraft.getWindow());
         int var6 = (int)this.minecraft.mouseHandler.getScaledYPos(this.minecraft.getWindow());
         if (var4 && var2 && this.minecraft.level != null) {
            var3.push("level");
            this.renderLevel(var1);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffectId != null && this.effectActive) {
               RenderSystem.resetTextureMatrix();
               PostChain var7 = this.minecraft.getShaderManager().getPostChain(this.postEffectId, LevelTargetBundle.MAIN_TARGETS);
               if (var7 != null) {
                  var7.process(this.minecraft.getMainRenderTarget(), this.resourcePool, (Consumer)null);
               }
            }
         }

         RenderTarget var17 = this.minecraft.getMainRenderTarget();
         RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var17.getDepthTexture(), 1.0);
         Lighting.setupFor3DItems();
         GuiGraphics var8 = new GuiGraphics(this.minecraft, this.guiRenderState);
         if (var4 && var2 && this.minecraft.level != null) {
            var3.popPush("gui");
            this.minecraft.gui.render(var8, var1);
            this.guiRenderer.render();
            RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(var17.getDepthTexture(), 1.0);
            var3.pop();
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
               this.minecraft.screen.renderWithTooltip(var8, var5, var6, var1.getGameTimeDeltaTicks());
            } catch (Throwable var14) {
               CrashReport var18 = CrashReport.forThrowable(var14, "Rendering screen");
               CrashReportCategory var20 = var18.addCategory("Screen render details");
               var20.setDetail("Screen name", (CrashReportDetail)(() -> this.minecraft.screen.getClass().getCanonicalName()));
               this.minecraft.mouseHandler.fillMousePositionDetails(var20, this.minecraft.getWindow());
               throw new ReportedException(var18);
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
               var8.pushGuiLayer(GuiLayer.SCREEN);
               this.minecraft.getToastManager().render(var8);
               var8.popGuiLayer();
            }
         }

         this.guiRenderer.render();
         this.guiRenderer.incrementFrameNumber();
         this.resourcePool.endFrame();
      }
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

   public void renderLevel(DeltaTracker var1) {
      float var2 = var1.getGameTimeDeltaPartialTick(true);
      LocalPlayer var3 = this.minecraft.player;
      this.lightTexture.updateLightTexture(var2);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(var3);
      }

      this.pick(var2);
      ProfilerFiller var4 = Profiler.get();
      var4.push("center");
      boolean var5 = this.shouldRenderBlockOutline();
      var4.popPush("camera");
      Camera var6 = this.mainCamera;
      Object var7 = this.minecraft.getCameraEntity() == null ? var3 : this.minecraft.getCameraEntity();
      float var8 = this.minecraft.level.tickRateManager().isEntityFrozen((Entity)var7) ? 1.0F : var2;
      var6.setup(this.minecraft.level, (Entity)var7, !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), var8);
      this.renderDistance = (float)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      float var9 = this.getFov(var6, var2, true);
      Matrix4f var10 = this.getProjectionMatrix(var9);
      PoseStack var11 = new PoseStack();
      this.bobHurt(var11, var6.getPartialTickTime());
      if ((Boolean)this.minecraft.options.bobView().get()) {
         this.bobView(var11, var6.getPartialTickTime());
      }

      var10.mul(var11.last().pose());
      float var12 = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
      float var13 = Mth.lerp(var2, var3.oPortalEffectIntensity, var3.portalEffectIntensity);
      float var14 = var3.getEffectBlendFactor(MobEffects.NAUSEA, var2);
      float var15 = Math.max(var13, var14) * var12 * var12;
      if (var15 > 0.0F) {
         float var16 = 5.0F / (var15 * var15 + 5.0F) - var15 * 0.04F;
         var16 *= var16;
         Vector3f var17 = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         float var18 = (this.spinningEffectTime + var2 * this.spinningEffectSpeed) * 0.017453292F;
         var10.rotate(var18, var17);
         var10.scale(1.0F / var16, 1.0F, 1.0F);
         var10.rotate(-var18, var17);
      }

      float var23 = Math.max(var9, (float)(Integer)this.minecraft.options.fov().get());
      Matrix4f var24 = this.getProjectionMatrix(var23);
      RenderSystem.setProjectionMatrix(var10, ProjectionType.PERSPECTIVE);
      Quaternionf var25 = var6.rotation().conjugate(new Quaternionf());
      Matrix4f var19 = (new Matrix4f()).rotation(var25);
      this.minecraft.levelRenderer.prepareCullFrustum(var6.getPosition(), var19, var24);
      this.minecraft.levelRenderer.renderLevel(this.resourcePool, var1, var5, var6, this, var19, var10);
      boolean var20 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
      RenderSystem.setProjectionMatrix(this.getProjectionMatrix(this.getFov(var6, var2, false)), ProjectionType.PERSPECTIVE);
      var4.popPush("hand");
      RenderSystem.getDevice().createCommandEncoder().clearDepthTexture(this.minecraft.getMainRenderTarget().getDepthTexture(), 1.0);
      this.renderItemInHand(var2, var20, var19);
      var4.popPush("screen effects");
      MultiBufferSource.BufferSource var21 = this.renderBuffers.bufferSource();
      this.screenEffectRenderer.renderScreenEffect(var20, var2);
      var21.endBatch();
      var4.pop();
      if (this.minecraft.gui.shouldRenderDebugCrosshair()) {
         this.minecraft.getDebugOverlay().render3dCrosshair(var6);
      }

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
      Vec3 var6 = this.mainCamera.getPosition();
      Vec3 var7 = var1.subtract(var6);
      Vector3f var8 = var5.transformProject(var7.toVector3f());
      return new Vec3(var8);
   }

   public double projectHorizonToScreen() {
      float var1 = this.mainCamera.getXRot();
      if (var1 <= -90.0F) {
         return -1.0 / 0.0;
      } else if (var1 >= 90.0F) {
         return 1.0 / 0.0;
      } else {
         float var2 = this.getFov(this.mainCamera, 0.0F, true);
         return Math.tan((double)(var1 * 0.017453292F)) / Math.tan((double)(var2 / 2.0F * 0.017453292F));
      }
   }
}
