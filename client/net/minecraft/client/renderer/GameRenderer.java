package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.resource.CrossFrameResourcePool;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import com.mojang.logging.LogUtils;
import com.mojang.math.Axis;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
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
import net.minecraft.world.item.ItemDisplayContext;
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
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;

public class GameRenderer implements AutoCloseable {
   private static final ResourceLocation BLUR_POST_CHAIN_ID = ResourceLocation.withDefaultNamespace("blur");
   public static final int MAX_BLUR_RADIUS = 10;
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final boolean DEPTH_BUFFER_DEBUG = false;
   public static final float PROJECTION_Z_NEAR = 0.05F;
   private static final float GUI_Z_NEAR = 1000.0F;
   private final Minecraft minecraft;
   private final ResourceManager resourceManager;
   private final RandomSource random = RandomSource.create();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final RenderBuffers renderBuffers;
   private int confusionAnimationTick;
   private float fovModifier;
   private float oldFovModifier;
   private float darkenWorldAmount;
   private float darkenWorldAmountO;
   private boolean renderHand = true;
   private boolean renderBlockOutline = true;
   private long lastScreenshotAttempt;
   private boolean hasWorldScreenshot;
   private long lastActiveTime = Util.getMillis();
   private final LightTexture lightTexture;
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private boolean panoramicMode;
   private float zoom = 1.0F;
   private float zoomX;
   private float zoomY;
   public static final int ITEM_ACTIVATION_ANIMATION_LENGTH = 40;
   @Nullable
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   private final CrossFrameResourcePool resourcePool = new CrossFrameResourcePool(3);
   @Nullable
   private ResourceLocation postEffectId;
   private boolean effectActive;
   private final Camera mainCamera = new Camera();

   public GameRenderer(Minecraft var1, ItemInHandRenderer var2, ResourceManager var3, RenderBuffers var4) {
      super();
      this.minecraft = var1;
      this.resourceManager = var3;
      this.itemInHandRenderer = var2;
      this.lightTexture = new LightTexture(this, var1);
      this.renderBuffers = var4;
   }

   @Override
   public void close() {
      this.lightTexture.close();
      this.overlayTexture.close();
      this.resourcePool.close();
   }

   public void setRenderHand(boolean var1) {
      this.renderHand = var1;
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
            var2.setUniform("Radius", var1);
            var2.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
         }
      }
   }

   public void preloadUiShader(ResourceProvider var1) {
      try {
         this.minecraft
            .getShaderManager()
            .preloadForStartup(var1, CoreShaders.RENDERTYPE_GUI, CoreShaders.RENDERTYPE_GUI_OVERLAY, CoreShaders.POSITION_TEX_COLOR);
      } catch (ShaderManager.CompilationException | IOException var3) {
         throw new RuntimeException("Could not preload shaders for loading UI", var3);
      }
   }

   public void tick() {
      this.tickFov();
      this.lightTexture.tick();
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.mainCamera.tick();
      this.itemInHandRenderer.tick();
      this.confusionAnimationTick++;
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

         if (this.itemActivationTicks > 0) {
            this.itemActivationTicks--;
            if (this.itemActivationTicks == 0) {
               this.itemActivationItem = null;
            }
         }
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
            this.minecraft.getProfiler().push("pick");
            double var3 = this.minecraft.player.blockInteractionRange();
            double var5 = this.minecraft.player.entityInteractionRange();
            HitResult var7 = this.pick(var2, var3, var5, var1);
            this.minecraft.hitResult = var7;
            this.minecraft.crosshairPickEntity = var7 instanceof EntityHitResult var8 ? var8.getEntity() : null;
            this.minecraft.getProfiler().pop();
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
      float var1;
      if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer var2) {
         Options var6 = this.minecraft.options;
         boolean var4 = var6.getCameraType().isFirstPerson();
         float var5 = var6.fovEffectScale().get().floatValue();
         var1 = var2.getFieldOfViewModifier(var4, var5);
      } else {
         var1 = 1.0F;
      }

      this.oldFovModifier = this.fovModifier;
      this.fovModifier = this.fovModifier + (var1 - this.fovModifier) * 0.5F;
      this.fovModifier = Mth.clamp(this.fovModifier, 0.1F, 1.5F);
   }

   private float getFov(Camera var1, float var2, boolean var3) {
      if (this.panoramicMode) {
         return 90.0F;
      } else {
         float var4 = 70.0F;
         if (var3) {
            var4 = (float)this.minecraft.options.fov().get().intValue();
            var4 *= Mth.lerp(var2, this.oldFovModifier, this.fovModifier);
         }

         if (var1.getEntity() instanceof LivingEntity var5 && var5.isDeadOrDying()) {
            float var9 = Math.min((float)var5.deathTime + var2, 20.0F);
            var4 /= (1.0F - 500.0F / (var9 + 500.0F)) * 2.0F + 1.0F;
         }

         FogType var8 = var1.getFluidInCamera();
         if (var8 == FogType.LAVA || var8 == FogType.WATER) {
            float var10 = this.minecraft.options.fovEffectScale().get().floatValue();
            var4 *= Mth.lerp(var10, 1.0F, 0.85714287F);
         }

         return var4;
      }
   }

   private void bobHurt(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof LivingEntity var3) {
         float var7 = (float)var3.hurtTime - var2;
         if (var3.isDeadOrDying()) {
            float var5 = Math.min((float)var3.deathTime + var2, 20.0F);
            var1.mulPose(Axis.ZP.rotationDegrees(40.0F - 8000.0F / (var5 + 200.0F)));
         }

         if (var7 < 0.0F) {
            return;
         }

         var7 /= (float)var3.hurtDuration;
         var7 = Mth.sin(var7 * var7 * var7 * var7 * 3.1415927F);
         float var10 = var3.getHurtDir();
         var1.mulPose(Axis.YP.rotationDegrees(-var10));
         float var6 = (float)((double)(-var7) * 14.0 * this.minecraft.options.damageTiltStrength().get());
         var1.mulPose(Axis.ZP.rotationDegrees(var6));
         var1.mulPose(Axis.YP.rotationDegrees(var10));
      }
   }

   private void bobView(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer var3) {
         float var7 = var3.walkDist - var3.walkDistO;
         float var5 = -(var3.walkDist + var7 * var2);
         float var6 = Mth.lerp(var2, var3.oBob, var3.bob);
         var1.translate(Mth.sin(var5 * 3.1415927F) * var6 * 0.5F, -Math.abs(Mth.cos(var5 * 3.1415927F) * var6), 0.0F);
         var1.mulPose(Axis.ZP.rotationDegrees(Mth.sin(var5 * 3.1415927F) * var6 * 3.0F));
         var1.mulPose(Axis.XP.rotationDegrees(Math.abs(Mth.cos(var5 * 3.1415927F - 0.2F) * var6) * 5.0F));
      }
   }

   public void renderZoomed(float var1, float var2, float var3) {
      this.zoom = var1;
      this.zoomX = var2;
      this.zoomY = var3;
      this.setRenderBlockOutline(false);
      this.setRenderHand(false);
      this.renderLevel(DeltaTracker.ZERO);
      this.zoom = 1.0F;
   }

   private void renderItemInHand(Camera var1, float var2, Matrix4f var3) {
      if (!this.panoramicMode) {
         Matrix4f var4 = this.getProjectionMatrix(this.getFov(var1, var2, false));
         RenderSystem.setProjectionMatrix(var4, VertexSorting.DISTANCE_TO_ORIGIN);
         PoseStack var5 = new PoseStack();
         var5.pushPose();
         var5.mulPose(var3.invert(new Matrix4f()));
         Matrix4fStack var6 = RenderSystem.getModelViewStack();
         var6.pushMatrix().mul(var3);
         this.bobHurt(var5, var2);
         if (this.minecraft.options.bobView().get()) {
            this.bobView(var5, var2);
         }

         boolean var7 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
         if (this.minecraft.options.getCameraType().isFirstPerson()
            && !var7
            && !this.minecraft.options.hideGui
            && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer
               .renderHandsWithItems(
                  var2,
                  var5,
                  this.renderBuffers.bufferSource(),
                  this.minecraft.player,
                  this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, var2)
               );
            this.lightTexture.turnOffLightLayer();
         }

         var6.popMatrix();
         var5.popPose();
         if (this.minecraft.options.getCameraType().isFirstPerson() && !var7) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, var5);
         }
      }
   }

   public Matrix4f getProjectionMatrix(float var1) {
      Matrix4f var2 = new Matrix4f();
      if (this.zoom != 1.0F) {
         var2.translate(this.zoomX, -this.zoomY, 0.0F);
         var2.scale(this.zoom, this.zoom, 1.0F);
      }

      return var2.perspective(
         var1 * 0.017453292F, (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.getDepthFar()
      );
   }

   public float getDepthFar() {
      return this.renderDistance * 4.0F;
   }

   public static float getNightVisionScale(LivingEntity var0, float var1) {
      MobEffectInstance var2 = var0.getEffect(MobEffects.NIGHT_VISION);
      return !var2.endsWithin(200) ? 1.0F : 0.7F + Mth.sin(((float)var2.getDuration() - var1) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void render(DeltaTracker var1, boolean var2) {
      if (!this.minecraft.isWindowActive()
         && this.minecraft.options.pauseOnLostFocus
         && (!this.minecraft.options.touchscreen().get() || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         boolean var3 = this.minecraft.isGameLoadFinished();
         int var4 = (int)(
            this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth()
         );
         int var5 = (int)(
            this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight()
         );
         RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         if (var3 && var2 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel(var1);
            this.tryTakeScreenshotIfNeeded();
            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffectId != null && this.effectActive) {
               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.resetTextureMatrix();
               PostChain var6 = this.minecraft.getShaderManager().getPostChain(this.postEffectId, LevelTargetBundle.MAIN_TARGETS);
               if (var6 != null) {
                  var6.process(this.minecraft.getMainRenderTarget(), this.resourcePool);
               }
            }

            this.minecraft.getMainRenderTarget().bindWrite(true);
         }

         Window var16 = this.minecraft.getWindow();
         RenderSystem.clear(256);
         Matrix4f var7 = new Matrix4f()
            .setOrtho(
               0.0F, (float)((double)var16.getWidth() / var16.getGuiScale()), (float)((double)var16.getHeight() / var16.getGuiScale()), 0.0F, 1000.0F, 21000.0F
            );
         RenderSystem.setProjectionMatrix(var7, VertexSorting.ORTHOGRAPHIC_Z);
         Matrix4fStack var8 = RenderSystem.getModelViewStack();
         var8.pushMatrix();
         var8.translation(0.0F, 0.0F, -11000.0F);
         Lighting.setupFor3DItems();
         GuiGraphics var9 = new GuiGraphics(this.minecraft, this.renderBuffers.bufferSource());
         if (var3 && var2 && this.minecraft.level != null) {
            this.minecraft.getProfiler().popPush("gui");
            if (!this.minecraft.options.hideGui) {
               this.renderItemActivationAnimation(var9, var1.getGameTimeDeltaPartialTick(false));
            }

            this.minecraft.gui.render(var9, var1);
            var9.flush();
            RenderSystem.clear(256);
            this.minecraft.getProfiler().pop();
         }

         if (this.minecraft.getOverlay() != null) {
            try {
               this.minecraft.getOverlay().render(var9, var4, var5, var1.getGameTimeDeltaTicks());
            } catch (Throwable var15) {
               CrashReport var11 = CrashReport.forThrowable(var15, "Rendering overlay");
               CrashReportCategory var12 = var11.addCategory("Overlay render details");
               var12.setDetail("Overlay name", () -> this.minecraft.getOverlay().getClass().getCanonicalName());
               throw new ReportedException(var11);
            }
         } else if (var3 && this.minecraft.screen != null) {
            try {
               this.minecraft.screen.renderWithTooltip(var9, var4, var5, var1.getGameTimeDeltaTicks());
            } catch (Throwable var14) {
               CrashReport var17 = CrashReport.forThrowable(var14, "Rendering screen");
               CrashReportCategory var19 = var17.addCategory("Screen render details");
               var19.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
               var19.setDetail(
                  "Mouse location",
                  () -> String.format(
                        Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", var4, var5, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos()
                     )
               );
               var19.setDetail(
                  "Screen size",
                  () -> String.format(
                        Locale.ROOT,
                        "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f",
                        this.minecraft.getWindow().getGuiScaledWidth(),
                        this.minecraft.getWindow().getGuiScaledHeight(),
                        this.minecraft.getWindow().getWidth(),
                        this.minecraft.getWindow().getHeight(),
                        this.minecraft.getWindow().getGuiScale()
                     )
               );
               throw new ReportedException(var17);
            }

            try {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.handleDelayedNarration();
               }
            } catch (Throwable var13) {
               CrashReport var18 = CrashReport.forThrowable(var13, "Narrating screen");
               CrashReportCategory var20 = var18.addCategory("Screen details");
               var20.setDetail("Screen name", () -> this.minecraft.screen.getClass().getCanonicalName());
               throw new ReportedException(var18);
            }
         }

         if (var3 && var2 && this.minecraft.level != null) {
            this.minecraft.gui.renderSavingIndicator(var9, var1);
         }

         if (var3) {
            this.minecraft.getProfiler().push("toasts");
            this.minecraft.getToastManager().render(var9);
            this.minecraft.getProfiler().pop();
         }

         var9.flush();
         var8.popMatrix();
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
               var3.getWorldScreenshotFile().ifPresent(var1x -> {
                  if (Files.isRegularFile(var1x)) {
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
         NativeImage var2 = Screenshot.takeScreenshot(this.minecraft.getMainRenderTarget());
         Util.ioPool().execute(() -> {
            int var2x = var2.getWidth();
            int var3 = var2.getHeight();
            int var4 = 0;
            int var5 = 0;
            if (var2x > var3) {
               var4 = (var2x - var3) / 2;
               var2x = var3;
            } else {
               var5 = (var3 - var2x) / 2;
               var3 = var2x;
            }

            try (NativeImage var6 = new NativeImage(64, 64, false)) {
               var2.resizeSubRectTo(var4, var5, var2x, var3, var6);
               var6.writeToFile(var1);
            } catch (IOException var16) {
               LOGGER.warn("Couldn't save auto screenshot", var16);
            } finally {
               var2.close();
            }
         });
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
                  Registry var8 = this.minecraft.level.registryAccess().registryOrThrow(Registries.BLOCK);
                  var2 = !var3.isEmpty() && (var3.canBreakBlockInAdventureMode(var7) || var3.canPlaceOnBlockInAdventureMode(var7));
               }
            }
         }

         return var2;
      }
   }

   public void renderLevel(DeltaTracker var1) {
      float var2 = var1.getGameTimeDeltaPartialTick(true);
      this.lightTexture.updateLightTexture(var2);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.pick(var2);
      this.minecraft.getProfiler().push("center");
      boolean var3 = this.shouldRenderBlockOutline();
      this.minecraft.getProfiler().popPush("camera");
      Camera var4 = this.mainCamera;
      Object var5 = this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity();
      float var6 = this.minecraft.level.tickRateManager().isEntityFrozen((Entity)var5) ? 1.0F : var2;
      var4.setup(
         this.minecraft.level, (Entity)var5, !this.minecraft.options.getCameraType().isFirstPerson(), this.minecraft.options.getCameraType().isMirrored(), var6
      );
      this.renderDistance = (float)(this.minecraft.options.getEffectiveRenderDistance() * 16);
      float var7 = this.getFov(var4, var2, true);
      Matrix4f var8 = this.getProjectionMatrix(var7);
      PoseStack var9 = new PoseStack();
      this.bobHurt(var9, var4.getPartialTickTime());
      if (this.minecraft.options.bobView().get()) {
         this.bobView(var9, var4.getPartialTickTime());
      }

      var8.mul(var9.last().pose());
      float var10 = this.minecraft.options.screenEffectScale().get().floatValue();
      float var11 = Mth.lerp(var2, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity) * var10 * var10;
      if (var11 > 0.0F) {
         int var12 = this.minecraft.player.hasEffect(MobEffects.CONFUSION) ? 7 : 20;
         float var13 = 5.0F / (var11 * var11 + 5.0F) - var11 * 0.04F;
         var13 *= var13;
         Vector3f var14 = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         float var15 = ((float)this.confusionAnimationTick + var2) * (float)var12 * 0.017453292F;
         var8.rotate(var15, var14);
         var8.scale(1.0F / var13, 1.0F, 1.0F);
         var8.rotate(-var15, var14);
      }

      float var16 = Math.max(var7, (float)this.minecraft.options.fov().get().intValue());
      Matrix4f var18 = this.getProjectionMatrix(var16);
      RenderSystem.setProjectionMatrix(var8, VertexSorting.DISTANCE_TO_ORIGIN);
      Quaternionf var19 = var4.rotation().conjugate(new Quaternionf());
      Matrix4f var20 = new Matrix4f().rotation(var19);
      this.minecraft.levelRenderer.prepareCullFrustum(var4.getPosition(), var20, var18);
      this.minecraft.getMainRenderTarget().bindWrite(true);
      this.minecraft.levelRenderer.renderLevel(this.resourcePool, var1, var3, var4, this, this.lightTexture, var20, var8);
      this.minecraft.getProfiler().popPush("hand");
      if (this.renderHand) {
         RenderSystem.clear(256);
         this.renderItemInHand(var4, var2, var20);
      }

      this.minecraft.getProfiler().pop();
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.minecraft.getMapTextureManager().resetData();
      this.mainCamera.reset();
      this.hasWorldScreenshot = false;
   }

   public void displayItemActivation(ItemStack var1) {
      this.itemActivationItem = var1;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivationAnimation(GuiGraphics var1, float var2) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int var3 = 40 - this.itemActivationTicks;
         float var4 = ((float)var3 + var2) / 40.0F;
         float var5 = var4 * var4;
         float var6 = var4 * var5;
         float var7 = 10.25F * var6 * var5 - 24.95F * var5 * var5 + 25.5F * var6 - 13.8F * var5 + 4.0F * var4;
         float var8 = var7 * 3.1415927F;
         float var9 = this.itemActivationOffX * (float)(var1.guiWidth() / 4);
         float var10 = this.itemActivationOffY * (float)(var1.guiHeight() / 4);
         PoseStack var11 = new PoseStack();
         var11.pushPose();
         var11.translate(
            (float)(var1.guiWidth() / 2) + var9 * Mth.abs(Mth.sin(var8 * 2.0F)), (float)(var1.guiHeight() / 2) + var10 * Mth.abs(Mth.sin(var8 * 2.0F)), -50.0F
         );
         float var12 = 50.0F + 175.0F * Mth.sin(var8);
         var11.scale(var12, -var12, var12);
         var11.mulPose(Axis.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(var8))));
         var11.mulPose(Axis.XP.rotationDegrees(6.0F * Mth.cos(var4 * 8.0F)));
         var11.mulPose(Axis.ZP.rotationDegrees(6.0F * Mth.cos(var4 * 8.0F)));
         this.minecraft
            .getItemRenderer()
            .renderStatic(
               this.itemActivationItem, ItemDisplayContext.FIXED, 15728880, OverlayTexture.NO_OVERLAY, var11, var1.bufferSource(), this.minecraft.level, 0
            );
         var11.popPose();
      }
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
}
