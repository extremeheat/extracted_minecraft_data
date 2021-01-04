package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.shaders.ProgramManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.CloudStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.ParticleStatus;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.FrustumCuller;
import net.minecraft.client.renderer.culling.FrustumData;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleResource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRenderer implements AutoCloseable, ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ResourceLocation RAIN_LOCATION = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation SNOW_LOCATION = new ResourceLocation("textures/environment/snow.png");
   private final Minecraft minecraft;
   private final ResourceManager resourceManager;
   private final Random random = new Random();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final MapRenderer mapRenderer;
   private int tick;
   private float fov;
   private float oldFov;
   private float darkenWorldAmount;
   private float darkenWorldAmountO;
   private boolean renderHand = true;
   private boolean renderBlockOutline = true;
   private long lastScreenshotAttempt;
   private long lastActiveTime = Util.getMillis();
   private final LightTexture lightTexture;
   private int rainSoundTime;
   private final float[] rainSizeX = new float[1024];
   private final float[] rainSizeZ = new float[1024];
   private final FogRenderer fog;
   private boolean panoramicMode;
   private double zoom = 1.0D;
   private double zoom_x;
   private double zoom_y;
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   private PostChain postEffect;
   private static final ResourceLocation[] EFFECTS = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int EFFECT_NONE;
   private int effectIndex;
   private boolean effectActive;
   private int frameId;
   private final Camera mainCamera;

   public GameRenderer(Minecraft var1, ResourceManager var2) {
      super();
      this.effectIndex = EFFECT_NONE;
      this.mainCamera = new Camera();
      this.minecraft = var1;
      this.resourceManager = var2;
      this.itemInHandRenderer = var1.getItemInHandRenderer();
      this.mapRenderer = new MapRenderer(var1.getTextureManager());
      this.lightTexture = new LightTexture(this);
      this.fog = new FogRenderer(this);
      this.postEffect = null;

      for(int var3 = 0; var3 < 32; ++var3) {
         for(int var4 = 0; var4 < 32; ++var4) {
            float var5 = (float)(var4 - 16);
            float var6 = (float)(var3 - 16);
            float var7 = Mth.sqrt(var5 * var5 + var6 * var6);
            this.rainSizeX[var3 << 5 | var4] = -var6 / var7;
            this.rainSizeZ[var3 << 5 | var4] = var5 / var7;
         }
      }

   }

   public void close() {
      this.lightTexture.close();
      this.mapRenderer.close();
      this.shutdownEffect();
   }

   public boolean postEffectActive() {
      return GLX.usePostProcess && this.postEffect != null;
   }

   public void shutdownEffect() {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      this.postEffect = null;
      this.effectIndex = EFFECT_NONE;
   }

   public void togglePostEffect() {
      this.effectActive = !this.effectActive;
   }

   public void checkEntityPostEffect(@Nullable Entity var1) {
      if (GLX.usePostProcess) {
         if (this.postEffect != null) {
            this.postEffect.close();
         }

         this.postEffect = null;
         if (var1 instanceof Creeper) {
            this.loadEffect(new ResourceLocation("shaders/post/creeper.json"));
         } else if (var1 instanceof Spider) {
            this.loadEffect(new ResourceLocation("shaders/post/spider.json"));
         } else if (var1 instanceof EnderMan) {
            this.loadEffect(new ResourceLocation("shaders/post/invert.json"));
         }

      }
   }

   private void loadEffect(ResourceLocation var1) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      try {
         this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), var1);
         this.postEffect.resize(this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
         this.effectActive = true;
      } catch (IOException var3) {
         LOGGER.warn("Failed to load shader: {}", var1, var3);
         this.effectIndex = EFFECT_NONE;
         this.effectActive = false;
      } catch (JsonSyntaxException var4) {
         LOGGER.warn("Failed to load shader: {}", var1, var4);
         this.effectIndex = EFFECT_NONE;
         this.effectActive = false;
      }

   }

   public void onResourceManagerReload(ResourceManager var1) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      this.postEffect = null;
      if (this.effectIndex == EFFECT_NONE) {
         this.checkEntityPostEffect(this.minecraft.getCameraEntity());
      } else {
         this.loadEffect(EFFECTS[this.effectIndex]);
      }

   }

   public void tick() {
      if (GLX.usePostProcess && ProgramManager.getInstance() == null) {
         ProgramManager.createInstance();
      }

      this.tickFov();
      this.lightTexture.tick();
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.mainCamera.tick();
      ++this.tick;
      this.itemInHandRenderer.tick();
      this.tickRain();
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
         --this.itemActivationTicks;
         if (this.itemActivationTicks == 0) {
            this.itemActivationItem = null;
         }
      }

   }

   public PostChain currentEffect() {
      return this.postEffect;
   }

   public void resize(int var1, int var2) {
      if (GLX.usePostProcess) {
         if (this.postEffect != null) {
            this.postEffect.resize(var1, var2);
         }

         this.minecraft.levelRenderer.resize(var1, var2);
      }
   }

   public void pick(float var1) {
      Entity var2 = this.minecraft.getCameraEntity();
      if (var2 != null) {
         if (this.minecraft.level != null) {
            this.minecraft.getProfiler().push("pick");
            this.minecraft.crosshairPickEntity = null;
            double var3 = (double)this.minecraft.gameMode.getPickRange();
            this.minecraft.hitResult = var2.pick(var3, var1, false);
            Vec3 var5 = var2.getEyePosition(var1);
            boolean var6 = false;
            boolean var7 = true;
            double var8 = var3;
            if (this.minecraft.gameMode.hasFarPickRange()) {
               var8 = 6.0D;
               var3 = var8;
            } else {
               if (var3 > 3.0D) {
                  var6 = true;
               }

               var3 = var3;
            }

            var8 *= var8;
            if (this.minecraft.hitResult != null) {
               var8 = this.minecraft.hitResult.getLocation().distanceToSqr(var5);
            }

            Vec3 var10 = var2.getViewVector(1.0F);
            Vec3 var11 = var5.add(var10.x * var3, var10.y * var3, var10.z * var3);
            float var12 = 1.0F;
            AABB var13 = var2.getBoundingBox().expandTowards(var10.scale(var3)).inflate(1.0D, 1.0D, 1.0D);
            EntityHitResult var14 = ProjectileUtil.getEntityHitResult(var2, var5, var11, var13, (var0) -> {
               return !var0.isSpectator() && var0.isPickable();
            }, var8);
            if (var14 != null) {
               Entity var15 = var14.getEntity();
               Vec3 var16 = var14.getLocation();
               double var17 = var5.distanceToSqr(var16);
               if (var6 && var17 > 9.0D) {
                  this.minecraft.hitResult = BlockHitResult.miss(var16, Direction.getNearest(var10.x, var10.y, var10.z), new BlockPos(var16));
               } else if (var17 < var8 || this.minecraft.hitResult == null) {
                  this.minecraft.hitResult = var14;
                  if (var15 instanceof LivingEntity || var15 instanceof ItemFrame) {
                     this.minecraft.crosshairPickEntity = var15;
                  }
               }
            }

            this.minecraft.getProfiler().pop();
         }
      }
   }

   private void tickFov() {
      float var1 = 1.0F;
      if (this.minecraft.getCameraEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer var2 = (AbstractClientPlayer)this.minecraft.getCameraEntity();
         var1 = var2.getFieldOfViewModifier();
      }

      this.oldFov = this.fov;
      this.fov += (var1 - this.fov) * 0.5F;
      if (this.fov > 1.5F) {
         this.fov = 1.5F;
      }

      if (this.fov < 0.1F) {
         this.fov = 0.1F;
      }

   }

   private double getFov(Camera var1, float var2, boolean var3) {
      if (this.panoramicMode) {
         return 90.0D;
      } else {
         double var4 = 70.0D;
         if (var3) {
            var4 = this.minecraft.options.fov;
            var4 *= (double)Mth.lerp(var2, this.oldFov, this.fov);
         }

         if (var1.getEntity() instanceof LivingEntity && ((LivingEntity)var1.getEntity()).getHealth() <= 0.0F) {
            float var6 = (float)((LivingEntity)var1.getEntity()).deathTime + var2;
            var4 /= (double)((1.0F - 500.0F / (var6 + 500.0F)) * 2.0F + 1.0F);
         }

         FluidState var7 = var1.getFluidInCamera();
         if (!var7.isEmpty()) {
            var4 = var4 * 60.0D / 70.0D;
         }

         return var4;
      }
   }

   private void bobHurt(float var1) {
      if (this.minecraft.getCameraEntity() instanceof LivingEntity) {
         LivingEntity var2 = (LivingEntity)this.minecraft.getCameraEntity();
         float var3 = (float)var2.hurtTime - var1;
         float var4;
         if (var2.getHealth() <= 0.0F) {
            var4 = (float)var2.deathTime + var1;
            GlStateManager.rotatef(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (var3 < 0.0F) {
            return;
         }

         var3 /= (float)var2.hurtDuration;
         var3 = Mth.sin(var3 * var3 * var3 * var3 * 3.1415927F);
         var4 = var2.hurtDir;
         GlStateManager.rotatef(-var4, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(var4, 0.0F, 1.0F, 0.0F);
      }

   }

   private void bobView(float var1) {
      if (this.minecraft.getCameraEntity() instanceof Player) {
         Player var2 = (Player)this.minecraft.getCameraEntity();
         float var3 = var2.walkDist - var2.walkDistO;
         float var4 = -(var2.walkDist + var3 * var1);
         float var5 = Mth.lerp(var1, var2.oBob, var2.bob);
         GlStateManager.translatef(Mth.sin(var4 * 3.1415927F) * var5 * 0.5F, -Math.abs(Mth.cos(var4 * 3.1415927F) * var5), 0.0F);
         GlStateManager.rotatef(Mth.sin(var4 * 3.1415927F) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(Math.abs(Mth.cos(var4 * 3.1415927F - 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
      }
   }

   private void setupCamera(float var1) {
      this.renderDistance = (float)(this.minecraft.options.renderDistance * 16);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      if (this.zoom != 1.0D) {
         GlStateManager.translatef((float)this.zoom_x, (float)(-this.zoom_y), 0.0F);
         GlStateManager.scaled(this.zoom, this.zoom, 1.0D);
      }

      GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(this.mainCamera, var1, true), (float)this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05F, this.renderDistance * Mth.SQRT_OF_TWO));
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      this.bobHurt(var1);
      if (this.minecraft.options.bobView) {
         this.bobView(var1);
      }

      float var2 = Mth.lerp(var1, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
      if (var2 > 0.0F) {
         byte var3 = 20;
         if (this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
            var3 = 7;
         }

         float var4 = 5.0F / (var2 * var2 + 5.0F) - var2 * 0.04F;
         var4 *= var4;
         GlStateManager.rotatef(((float)this.tick + var1) * (float)var3, 0.0F, 1.0F, 1.0F);
         GlStateManager.scalef(1.0F / var4, 1.0F, 1.0F);
         GlStateManager.rotatef(-((float)this.tick + var1) * (float)var3, 0.0F, 1.0F, 1.0F);
      }

   }

   private void renderItemInHand(Camera var1, float var2) {
      if (!this.panoramicMode) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(var1, var2, false), (float)this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05F, this.renderDistance * 2.0F));
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         GlStateManager.pushMatrix();
         this.bobHurt(var2);
         if (this.minecraft.options.bobView) {
            this.bobView(var2);
         }

         boolean var3 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
         if (this.minecraft.options.thirdPersonView == 0 && !var3 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.turnOnLightLayer();
            this.itemInHandRenderer.render(var2);
            this.turnOffLightLayer();
         }

         GlStateManager.popMatrix();
         if (this.minecraft.options.thirdPersonView == 0 && !var3) {
            this.itemInHandRenderer.renderScreenEffect(var2);
            this.bobHurt(var2);
         }

         if (this.minecraft.options.bobView) {
            this.bobView(var2);
         }

      }
   }

   public void turnOffLightLayer() {
      this.lightTexture.turnOffLightLayer();
   }

   public void turnOnLightLayer() {
      this.lightTexture.turnOnLightLayer();
   }

   public float getNightVisionScale(LivingEntity var1, float var2) {
      int var3 = var1.getEffect(MobEffects.NIGHT_VISION).getDuration();
      return var3 > 200 ? 1.0F : 0.7F + Mth.sin(((float)var3 - var2) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void render(float var1, long var2, boolean var4) {
      if (!this.minecraft.isWindowActive() && this.minecraft.options.pauseOnLostFocus && (!this.minecraft.options.touchscreen || !this.minecraft.mouseHandler.isRightPressed())) {
         if (Util.getMillis() - this.lastActiveTime > 500L) {
            this.minecraft.pauseGame(false);
         }
      } else {
         this.lastActiveTime = Util.getMillis();
      }

      if (!this.minecraft.noRender) {
         int var5 = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.window.getGuiScaledWidth() / (double)this.minecraft.window.getScreenWidth());
         int var6 = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.window.getGuiScaledHeight() / (double)this.minecraft.window.getScreenHeight());
         int var7 = this.minecraft.options.framerateLimit;
         if (var4 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            int var8 = Math.min(Minecraft.getAverageFps(), var7);
            var8 = Math.max(var8, 60);
            long var9 = Util.getNanos() - var2;
            long var11 = Math.max((long)(1000000000 / var8 / 4) - var9, 0L);
            this.renderLevel(var1, Util.getNanos() + var11);
            if (this.minecraft.hasSingleplayerServer() && this.lastScreenshotAttempt < Util.getMillis() - 1000L) {
               this.lastScreenshotAttempt = Util.getMillis();
               if (!this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
                  this.takeAutoScreenshot();
               }
            }

            if (GLX.usePostProcess) {
               this.minecraft.levelRenderer.doEntityOutline();
               if (this.postEffect != null && this.effectActive) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.postEffect.process(var1);
                  GlStateManager.popMatrix();
               }

               this.minecraft.getMainRenderTarget().bindWrite(true);
            }

            this.minecraft.getProfiler().popPush("gui");
            if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.minecraft.window.setupGuiState(Minecraft.ON_OSX);
               this.renderItemActivationAnimation(this.minecraft.window.getGuiScaledWidth(), this.minecraft.window.getGuiScaledHeight(), var1);
               this.minecraft.gui.render(var1);
            }

            this.minecraft.getProfiler().pop();
         } else {
            GlStateManager.viewport(0, 0, this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.minecraft.window.setupGuiState(Minecraft.ON_OSX);
         }

         CrashReportCategory var10;
         CrashReport var15;
         if (this.minecraft.overlay != null) {
            GlStateManager.clear(256, Minecraft.ON_OSX);

            try {
               this.minecraft.overlay.render(var5, var6, this.minecraft.getDeltaFrameTime());
            } catch (Throwable var14) {
               var15 = CrashReport.forThrowable(var14, "Rendering overlay");
               var10 = var15.addCategory("Overlay render details");
               var10.setDetail("Overlay name", () -> {
                  return this.minecraft.overlay.getClass().getCanonicalName();
               });
               throw new ReportedException(var15);
            }
         } else if (this.minecraft.screen != null) {
            GlStateManager.clear(256, Minecraft.ON_OSX);

            try {
               this.minecraft.screen.render(var5, var6, this.minecraft.getDeltaFrameTime());
            } catch (Throwable var13) {
               var15 = CrashReport.forThrowable(var13, "Rendering screen");
               var10 = var15.addCategory("Screen render details");
               var10.setDetail("Screen name", () -> {
                  return this.minecraft.screen.getClass().getCanonicalName();
               });
               var10.setDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", var5, var6, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos());
               });
               var10.setDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.minecraft.window.getGuiScaledWidth(), this.minecraft.window.getGuiScaledHeight(), this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), this.minecraft.window.getGuiScale());
               });
               throw new ReportedException(var15);
            }
         }

      }
   }

   private void takeAutoScreenshot() {
      if (this.minecraft.levelRenderer.countRenderedChunks() > 10 && this.minecraft.levelRenderer.hasRenderedAllChunks() && !this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
         NativeImage var1 = Screenshot.takeScreenshot(this.minecraft.window.getWidth(), this.minecraft.window.getHeight(), this.minecraft.getMainRenderTarget());
         SimpleResource.IO_EXECUTOR.execute(() -> {
            int var2 = var1.getWidth();
            int var3 = var1.getHeight();
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
               Throwable var7 = null;

               try {
                  var1.resizeSubRectTo(var4, var5, var2, var3, var6);
                  var6.writeToFile(this.minecraft.getSingleplayerServer().getWorldScreenshotFile());
               } catch (Throwable var25) {
                  var7 = var25;
                  throw var25;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var24) {
                           var7.addSuppressed(var24);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (IOException var27) {
               LOGGER.warn("Couldn't save auto screenshot", var27);
            } finally {
               var1.close();
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
         if (var2 && !((Player)var1).abilities.mayBuild) {
            ItemStack var3 = ((LivingEntity)var1).getMainHandItem();
            HitResult var4 = this.minecraft.hitResult;
            if (var4 != null && var4.getType() == HitResult.Type.BLOCK) {
               BlockPos var5 = ((BlockHitResult)var4).getBlockPos();
               BlockState var6 = this.minecraft.level.getBlockState(var5);
               if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
                  var2 = var6.getMenuProvider(this.minecraft.level, var5) != null;
               } else {
                  BlockInWorld var7 = new BlockInWorld(this.minecraft.level, var5, false);
                  var2 = !var3.isEmpty() && (var3.hasAdventureModeBreakTagForBlock(this.minecraft.level.getTagManager(), var7) || var3.hasAdventureModePlaceTagForBlock(this.minecraft.level.getTagManager(), var7));
               }
            }
         }

         return var2;
      }
   }

   public void renderLevel(float var1, long var2) {
      this.lightTexture.updateLightTexture(var1);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.pick(var1);
      GlStateManager.enableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.alphaFunc(516, 0.5F);
      this.minecraft.getProfiler().push("center");
      this.render(var1, var2);
      this.minecraft.getProfiler().pop();
   }

   private void render(float var1, long var2) {
      LevelRenderer var4 = this.minecraft.levelRenderer;
      ParticleEngine var5 = this.minecraft.particleEngine;
      boolean var6 = this.shouldRenderBlockOutline();
      GlStateManager.enableCull();
      this.minecraft.getProfiler().popPush("camera");
      this.setupCamera(var1);
      Camera var7 = this.mainCamera;
      var7.setup(this.minecraft.level, (Entity)(this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity()), this.minecraft.options.thirdPersonView > 0, this.minecraft.options.thirdPersonView == 2, var1);
      FrustumData var8 = Frustum.getFrustum();
      var4.prepare(var7);
      this.minecraft.getProfiler().popPush("clear");
      GlStateManager.viewport(0, 0, this.minecraft.window.getWidth(), this.minecraft.window.getHeight());
      this.fog.setupClearColor(var7, var1);
      GlStateManager.clear(16640, Minecraft.ON_OSX);
      this.minecraft.getProfiler().popPush("culling");
      FrustumCuller var9 = new FrustumCuller(var8);
      double var10 = var7.getPosition().x;
      double var12 = var7.getPosition().y;
      double var14 = var7.getPosition().z;
      var9.prepare(var10, var12, var14);
      if (this.minecraft.options.renderDistance >= 4) {
         this.fog.setupFog(var7, -1);
         this.minecraft.getProfiler().popPush("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(var7, var1, true), (float)this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05F, this.renderDistance * 2.0F));
         GlStateManager.matrixMode(5888);
         var4.renderSky(var1);
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(var7, var1, true), (float)this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05F, this.renderDistance * Mth.SQRT_OF_TWO));
         GlStateManager.matrixMode(5888);
      }

      this.fog.setupFog(var7, 0);
      GlStateManager.shadeModel(7425);
      if (var7.getPosition().y < 128.0D) {
         this.prepareAndRenderClouds(var7, var4, var1, var10, var12, var14);
      }

      this.minecraft.getProfiler().popPush("prepareterrain");
      this.fog.setupFog(var7, 0);
      this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
      Lighting.turnOff();
      this.minecraft.getProfiler().popPush("terrain_setup");
      this.minecraft.level.getChunkSource().getLightEngine().runUpdates(2147483647, true, true);
      var4.setupRender(var7, var9, this.frameId++, this.minecraft.player.isSpectator());
      this.minecraft.getProfiler().popPush("updatechunks");
      this.minecraft.levelRenderer.compileChunksUntil(var2);
      this.minecraft.getProfiler().popPush("terrain");
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlphaTest();
      var4.render(BlockLayer.SOLID, var7);
      GlStateManager.enableAlphaTest();
      var4.render(BlockLayer.CUTOUT_MIPPED, var7);
      this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
      var4.render(BlockLayer.CUTOUT, var7);
      this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      Lighting.turnOn();
      this.minecraft.getProfiler().popPush("entities");
      var4.renderEntities(var7, var9, var1);
      Lighting.turnOff();
      this.turnOffLightLayer();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (var6 && this.minecraft.hitResult != null) {
         GlStateManager.disableAlphaTest();
         this.minecraft.getProfiler().popPush("outline");
         var4.renderHitOutline(var7, this.minecraft.hitResult, 0);
         GlStateManager.enableAlphaTest();
      }

      if (this.minecraft.debugRenderer.shouldRender()) {
         this.minecraft.debugRenderer.render(var2);
      }

      this.minecraft.getProfiler().popPush("destroyProgress");
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).pushFilter(false, false);
      var4.renderDestroyAnimation(Tesselator.getInstance(), Tesselator.getInstance().getBuilder(), var7);
      this.minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS).popFilter();
      GlStateManager.disableBlend();
      this.turnOnLightLayer();
      this.fog.setupFog(var7, 0);
      this.minecraft.getProfiler().popPush("particles");
      var5.render(var7, var1);
      this.turnOffLightLayer();
      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.minecraft.getProfiler().popPush("weather");
      this.renderSnowAndRain(var1);
      GlStateManager.depthMask(true);
      var4.renderWorldBounds(var7, var1);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.alphaFunc(516, 0.1F);
      this.fog.setupFog(var7, 0);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
      GlStateManager.shadeModel(7425);
      this.minecraft.getProfiler().popPush("translucent");
      var4.render(BlockLayer.TRANSLUCENT, var7);
      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (var7.getPosition().y >= 128.0D) {
         this.minecraft.getProfiler().popPush("aboveClouds");
         this.prepareAndRenderClouds(var7, var4, var1, var10, var12, var14);
      }

      this.minecraft.getProfiler().popPush("hand");
      if (this.renderHand) {
         GlStateManager.clear(256, Minecraft.ON_OSX);
         this.renderItemInHand(var7, var1);
      }

   }

   private void prepareAndRenderClouds(Camera var1, LevelRenderer var2, float var3, double var4, double var6, double var8) {
      if (this.minecraft.options.getCloudsType() != CloudStatus.OFF) {
         this.minecraft.getProfiler().popPush("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(var1, var3, true), (float)this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05F, this.renderDistance * 4.0F));
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.fog.setupFog(var1, 0);
         var2.renderClouds(var3, var4, var6, var8);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         GlStateManager.multMatrix(Matrix4f.perspective(this.getFov(var1, var3, true), (float)this.minecraft.window.getWidth() / (float)this.minecraft.window.getHeight(), 0.05F, this.renderDistance * Mth.SQRT_OF_TWO));
         GlStateManager.matrixMode(5888);
      }

   }

   private void tickRain() {
      float var1 = this.minecraft.level.getRainLevel(1.0F);
      if (!this.minecraft.options.fancyGraphics) {
         var1 /= 2.0F;
      }

      if (var1 != 0.0F) {
         this.random.setSeed((long)this.tick * 312987231L);
         MultiPlayerLevel var2 = this.minecraft.level;
         BlockPos var3 = new BlockPos(this.mainCamera.getPosition());
         boolean var4 = true;
         double var5 = 0.0D;
         double var7 = 0.0D;
         double var9 = 0.0D;
         int var11 = 0;
         int var12 = (int)(100.0F * var1 * var1);
         if (this.minecraft.options.particles == ParticleStatus.DECREASED) {
            var12 >>= 1;
         } else if (this.minecraft.options.particles == ParticleStatus.MINIMAL) {
            var12 = 0;
         }

         for(int var13 = 0; var13 < var12; ++var13) {
            BlockPos var14 = var2.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var3.offset(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            Biome var15 = var2.getBiome(var14);
            BlockPos var16 = var14.below();
            if (var14.getY() <= var3.getY() + 10 && var14.getY() >= var3.getY() - 10 && var15.getPrecipitation() == Biome.Precipitation.RAIN && var15.getTemperature(var14) >= 0.15F) {
               double var17 = this.random.nextDouble();
               double var19 = this.random.nextDouble();
               BlockState var21 = var2.getBlockState(var16);
               FluidState var22 = var2.getFluidState(var14);
               VoxelShape var23 = var21.getCollisionShape(var2, var16);
               double var28 = var23.max(Direction.Axis.Y, var17, var19);
               double var30 = (double)var22.getHeight(var2, var14);
               double var24;
               double var26;
               if (var28 >= var30) {
                  var24 = var28;
                  var26 = var23.min(Direction.Axis.Y, var17, var19);
               } else {
                  var24 = 0.0D;
                  var26 = 0.0D;
               }

               if (var24 > -1.7976931348623157E308D) {
                  if (!var22.is(FluidTags.LAVA) && var21.getBlock() != Blocks.MAGMA_BLOCK && (var21.getBlock() != Blocks.CAMPFIRE || !(Boolean)var21.getValue(CampfireBlock.LIT))) {
                     ++var11;
                     if (this.random.nextInt(var11) == 0) {
                        var5 = (double)var16.getX() + var17;
                        var7 = (double)((float)var16.getY() + 0.1F) + var24 - 1.0D;
                        var9 = (double)var16.getZ() + var19;
                     }

                     this.minecraft.level.addParticle(ParticleTypes.RAIN, (double)var16.getX() + var17, (double)((float)var16.getY() + 0.1F) + var24, (double)var16.getZ() + var19, 0.0D, 0.0D, 0.0D);
                  } else {
                     this.minecraft.level.addParticle(ParticleTypes.SMOKE, (double)var14.getX() + var17, (double)((float)var14.getY() + 0.1F) - var26, (double)var14.getZ() + var19, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (var11 > 0 && this.random.nextInt(3) < this.rainSoundTime++) {
            this.rainSoundTime = 0;
            if (var7 > (double)(var3.getY() + 1) && var2.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var3).getY() > Mth.floor((float)var3.getY())) {
               this.minecraft.level.playLocalSound(var5, var7, var9, SoundEvents.WEATHER_RAIN_ABOVE, SoundSource.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.minecraft.level.playLocalSound(var5, var7, var9, SoundEvents.WEATHER_RAIN, SoundSource.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   protected void renderSnowAndRain(float var1) {
      float var2 = this.minecraft.level.getRainLevel(var1);
      if (var2 > 0.0F) {
         this.turnOnLightLayer();
         MultiPlayerLevel var3 = this.minecraft.level;
         int var4 = Mth.floor(this.mainCamera.getPosition().x);
         int var5 = Mth.floor(this.mainCamera.getPosition().y);
         int var6 = Mth.floor(this.mainCamera.getPosition().z);
         Tesselator var7 = Tesselator.getInstance();
         BufferBuilder var8 = var7.getBuilder();
         GlStateManager.disableCull();
         GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.alphaFunc(516, 0.1F);
         double var9 = this.mainCamera.getPosition().x;
         double var11 = this.mainCamera.getPosition().y;
         double var13 = this.mainCamera.getPosition().z;
         int var15 = Mth.floor(var11);
         byte var16 = 5;
         if (this.minecraft.options.fancyGraphics) {
            var16 = 10;
         }

         byte var17 = -1;
         float var18 = (float)this.tick + var1;
         var8.offset(-var9, -var11, -var13);
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos var19 = new BlockPos.MutableBlockPos();

         for(int var20 = var6 - var16; var20 <= var6 + var16; ++var20) {
            for(int var21 = var4 - var16; var21 <= var4 + var16; ++var21) {
               int var22 = (var20 - var6 + 16) * 32 + var21 - var4 + 16;
               double var23 = (double)this.rainSizeX[var22] * 0.5D;
               double var25 = (double)this.rainSizeZ[var22] * 0.5D;
               var19.set(var21, 0, var20);
               Biome var27 = var3.getBiome(var19);
               if (var27.getPrecipitation() != Biome.Precipitation.NONE) {
                  int var28 = var3.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, var19).getY();
                  int var29 = var5 - var16;
                  int var30 = var5 + var16;
                  if (var29 < var28) {
                     var29 = var28;
                  }

                  if (var30 < var28) {
                     var30 = var28;
                  }

                  int var31 = var28;
                  if (var28 < var15) {
                     var31 = var15;
                  }

                  if (var29 != var30) {
                     this.random.setSeed((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                     var19.set(var21, var29, var20);
                     float var32 = var27.getTemperature(var19);
                     double var33;
                     double var35;
                     double var37;
                     if (var32 >= 0.15F) {
                        if (var17 != 0) {
                           if (var17 >= 0) {
                              var7.end();
                           }

                           var17 = 0;
                           this.minecraft.getTextureManager().bind(RAIN_LOCATION);
                           var8.begin(7, DefaultVertexFormat.PARTICLE);
                        }

                        var33 = -((double)(this.tick + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31) + (double)var1) / 32.0D * (3.0D + this.random.nextDouble());
                        var35 = (double)((float)var21 + 0.5F) - this.mainCamera.getPosition().x;
                        var37 = (double)((float)var20 + 0.5F) - this.mainCamera.getPosition().z;
                        float var39 = Mth.sqrt(var35 * var35 + var37 * var37) / (float)var16;
                        float var40 = ((1.0F - var39 * var39) * 0.5F + 0.5F) * var2;
                        var19.set(var21, var31, var20);
                        int var41 = var3.getLightColor(var19, 0);
                        int var42 = var41 >> 16 & '\uffff';
                        int var43 = var41 & '\uffff';
                        var8.vertex((double)var21 - var23 + 0.5D, (double)var30, (double)var20 - var25 + 0.5D).uv(0.0D, (double)var29 * 0.25D + var33).color(1.0F, 1.0F, 1.0F, var40).uv2(var42, var43).endVertex();
                        var8.vertex((double)var21 + var23 + 0.5D, (double)var30, (double)var20 + var25 + 0.5D).uv(1.0D, (double)var29 * 0.25D + var33).color(1.0F, 1.0F, 1.0F, var40).uv2(var42, var43).endVertex();
                        var8.vertex((double)var21 + var23 + 0.5D, (double)var29, (double)var20 + var25 + 0.5D).uv(1.0D, (double)var30 * 0.25D + var33).color(1.0F, 1.0F, 1.0F, var40).uv2(var42, var43).endVertex();
                        var8.vertex((double)var21 - var23 + 0.5D, (double)var29, (double)var20 - var25 + 0.5D).uv(0.0D, (double)var30 * 0.25D + var33).color(1.0F, 1.0F, 1.0F, var40).uv2(var42, var43).endVertex();
                     } else {
                        if (var17 != 1) {
                           if (var17 >= 0) {
                              var7.end();
                           }

                           var17 = 1;
                           this.minecraft.getTextureManager().bind(SNOW_LOCATION);
                           var8.begin(7, DefaultVertexFormat.PARTICLE);
                        }

                        var33 = (double)(-((float)(this.tick & 511) + var1) / 512.0F);
                        var35 = this.random.nextDouble() + (double)var18 * 0.01D * (double)((float)this.random.nextGaussian());
                        var37 = this.random.nextDouble() + (double)(var18 * (float)this.random.nextGaussian()) * 0.001D;
                        double var49 = (double)((float)var21 + 0.5F) - this.mainCamera.getPosition().x;
                        double var50 = (double)((float)var20 + 0.5F) - this.mainCamera.getPosition().z;
                        float var48 = Mth.sqrt(var49 * var49 + var50 * var50) / (float)var16;
                        float var44 = ((1.0F - var48 * var48) * 0.3F + 0.5F) * var2;
                        var19.set(var21, var31, var20);
                        int var45 = (var3.getLightColor(var19, 0) * 3 + 15728880) / 4;
                        int var46 = var45 >> 16 & '\uffff';
                        int var47 = var45 & '\uffff';
                        var8.vertex((double)var21 - var23 + 0.5D, (double)var30, (double)var20 - var25 + 0.5D).uv(0.0D + var35, (double)var29 * 0.25D + var33 + var37).color(1.0F, 1.0F, 1.0F, var44).uv2(var46, var47).endVertex();
                        var8.vertex((double)var21 + var23 + 0.5D, (double)var30, (double)var20 + var25 + 0.5D).uv(1.0D + var35, (double)var29 * 0.25D + var33 + var37).color(1.0F, 1.0F, 1.0F, var44).uv2(var46, var47).endVertex();
                        var8.vertex((double)var21 + var23 + 0.5D, (double)var29, (double)var20 + var25 + 0.5D).uv(1.0D + var35, (double)var30 * 0.25D + var33 + var37).color(1.0F, 1.0F, 1.0F, var44).uv2(var46, var47).endVertex();
                        var8.vertex((double)var21 - var23 + 0.5D, (double)var29, (double)var20 - var25 + 0.5D).uv(0.0D + var35, (double)var30 * 0.25D + var33 + var37).color(1.0F, 1.0F, 1.0F, var44).uv2(var46, var47).endVertex();
                     }
                  }
               }
            }
         }

         if (var17 >= 0) {
            var7.end();
         }

         var8.offset(0.0D, 0.0D, 0.0D);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.turnOffLightLayer();
      }
   }

   public void resetFogColor(boolean var1) {
      this.fog.resetFogColor(var1);
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.mapRenderer.resetData();
      this.mainCamera.reset();
   }

   public MapRenderer getMapRenderer() {
      return this.mapRenderer;
   }

   public static void renderNameTagInWorld(Font var0, String var1, float var2, float var3, float var4, int var5, float var6, float var7, boolean var8) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef(var2, var3, var4);
      GlStateManager.normal3f(0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(-var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var7, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(-0.025F, -0.025F, 0.025F);
      GlStateManager.disableLighting();
      GlStateManager.depthMask(false);
      if (!var8) {
         GlStateManager.disableDepthTest();
      }

      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      int var9 = var0.width(var1) / 2;
      GlStateManager.disableTexture();
      Tesselator var10 = Tesselator.getInstance();
      BufferBuilder var11 = var10.getBuilder();
      var11.begin(7, DefaultVertexFormat.POSITION_COLOR);
      float var12 = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
      var11.vertex((double)(-var9 - 1), (double)(-1 + var5), 0.0D).color(0.0F, 0.0F, 0.0F, var12).endVertex();
      var11.vertex((double)(-var9 - 1), (double)(8 + var5), 0.0D).color(0.0F, 0.0F, 0.0F, var12).endVertex();
      var11.vertex((double)(var9 + 1), (double)(8 + var5), 0.0D).color(0.0F, 0.0F, 0.0F, var12).endVertex();
      var11.vertex((double)(var9 + 1), (double)(-1 + var5), 0.0D).color(0.0F, 0.0F, 0.0F, var12).endVertex();
      var10.end();
      GlStateManager.enableTexture();
      if (!var8) {
         var0.draw(var1, (float)(-var0.width(var1) / 2), (float)var5, 553648127);
         GlStateManager.enableDepthTest();
      }

      GlStateManager.depthMask(true);
      var0.draw(var1, (float)(-var0.width(var1) / 2), (float)var5, var8 ? 553648127 : -1);
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }

   public void displayItemActivation(ItemStack var1) {
      this.itemActivationItem = var1;
      this.itemActivationTicks = 40;
      this.itemActivationOffX = this.random.nextFloat() * 2.0F - 1.0F;
      this.itemActivationOffY = this.random.nextFloat() * 2.0F - 1.0F;
   }

   private void renderItemActivationAnimation(int var1, int var2, float var3) {
      if (this.itemActivationItem != null && this.itemActivationTicks > 0) {
         int var4 = 40 - this.itemActivationTicks;
         float var5 = ((float)var4 + var3) / 40.0F;
         float var6 = var5 * var5;
         float var7 = var5 * var6;
         float var8 = 10.25F * var7 * var6 - 24.95F * var6 * var6 + 25.5F * var7 - 13.8F * var6 + 4.0F * var5;
         float var9 = var8 * 3.1415927F;
         float var10 = this.itemActivationOffX * (float)(var1 / 4);
         float var11 = this.itemActivationOffY * (float)(var2 / 4);
         GlStateManager.enableAlphaTest();
         GlStateManager.pushMatrix();
         GlStateManager.pushLightingAttributes();
         GlStateManager.enableDepthTest();
         GlStateManager.disableCull();
         Lighting.turnOn();
         GlStateManager.translatef((float)(var1 / 2) + var10 * Mth.abs(Mth.sin(var9 * 2.0F)), (float)(var2 / 2) + var11 * Mth.abs(Mth.sin(var9 * 2.0F)), -50.0F);
         float var12 = 50.0F + 175.0F * Mth.sin(var9);
         GlStateManager.scalef(var12, -var12, var12);
         GlStateManager.rotatef(900.0F * Mth.abs(Mth.sin(var9)), 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(6.0F * Mth.cos(var5 * 8.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(6.0F * Mth.cos(var5 * 8.0F), 0.0F, 0.0F, 1.0F);
         this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemTransforms.TransformType.FIXED);
         GlStateManager.popAttributes();
         GlStateManager.popMatrix();
         Lighting.turnOff();
         GlStateManager.enableCull();
         GlStateManager.disableDepthTest();
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

   static {
      EFFECT_NONE = EFFECTS.length;
   }
}
