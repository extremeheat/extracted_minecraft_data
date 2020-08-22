package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import java.io.IOException;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.MapRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.SimpleResource;
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
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRenderer implements AutoCloseable, ResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final ResourceManager resourceManager;
   private final Random random = new Random();
   private float renderDistance;
   public final ItemInHandRenderer itemInHandRenderer;
   private final MapRenderer mapRenderer;
   private final RenderBuffers renderBuffers;
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
   private final OverlayTexture overlayTexture = new OverlayTexture();
   private boolean panoramicMode;
   private float zoom = 1.0F;
   private float zoomX;
   private float zoomY;
   @Nullable
   private ItemStack itemActivationItem;
   private int itemActivationTicks;
   private float itemActivationOffX;
   private float itemActivationOffY;
   @Nullable
   private PostChain postEffect;
   private static final ResourceLocation[] EFFECTS = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int EFFECT_NONE;
   private int effectIndex;
   private boolean effectActive;
   private final Camera mainCamera;

   public GameRenderer(Minecraft var1, ResourceManager var2, RenderBuffers var3) {
      this.effectIndex = EFFECT_NONE;
      this.mainCamera = new Camera();
      this.minecraft = var1;
      this.resourceManager = var2;
      this.itemInHandRenderer = var1.getItemInHandRenderer();
      this.mapRenderer = new MapRenderer(var1.getTextureManager());
      this.lightTexture = new LightTexture(this, var1);
      this.renderBuffers = var3;
      this.postEffect = null;
   }

   public void close() {
      this.lightTexture.close();
      this.mapRenderer.close();
      this.overlayTexture.close();
      this.shutdownEffect();
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

   private void loadEffect(ResourceLocation var1) {
      if (this.postEffect != null) {
         this.postEffect.close();
      }

      try {
         this.postEffect = new PostChain(this.minecraft.getTextureManager(), this.resourceManager, this.minecraft.getMainRenderTarget(), var1);
         this.postEffect.resize(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
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
      this.tickFov();
      this.lightTexture.tick();
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.mainCamera.tick();
      ++this.tick;
      this.itemInHandRenderer.tick();
      this.minecraft.levelRenderer.tickRain(this.mainCamera);
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

   @Nullable
   public PostChain currentEffect() {
      return this.postEffect;
   }

   public void resize(int var1, int var2) {
      if (this.postEffect != null) {
         this.postEffect.resize(var1, var2);
      }

      this.minecraft.levelRenderer.resize(var1, var2);
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
            float var6 = Math.min((float)((LivingEntity)var1.getEntity()).deathTime + var2, 20.0F);
            var4 /= (double)((1.0F - 500.0F / (var6 + 500.0F)) * 2.0F + 1.0F);
         }

         FluidState var7 = var1.getFluidInCamera();
         if (!var7.isEmpty()) {
            var4 = var4 * 60.0D / 70.0D;
         }

         return var4;
      }
   }

   private void bobHurt(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof LivingEntity) {
         LivingEntity var3 = (LivingEntity)this.minecraft.getCameraEntity();
         float var4 = (float)var3.hurtTime - var2;
         float var5;
         if (var3.getHealth() <= 0.0F) {
            var5 = Math.min((float)var3.deathTime + var2, 20.0F);
            var1.mulPose(Vector3f.ZP.rotationDegrees(40.0F - 8000.0F / (var5 + 200.0F)));
         }

         if (var4 < 0.0F) {
            return;
         }

         var4 /= (float)var3.hurtDuration;
         var4 = Mth.sin(var4 * var4 * var4 * var4 * 3.1415927F);
         var5 = var3.hurtDir;
         var1.mulPose(Vector3f.YP.rotationDegrees(-var5));
         var1.mulPose(Vector3f.ZP.rotationDegrees(-var4 * 14.0F));
         var1.mulPose(Vector3f.YP.rotationDegrees(var5));
      }

   }

   private void bobView(PoseStack var1, float var2) {
      if (this.minecraft.getCameraEntity() instanceof Player) {
         Player var3 = (Player)this.minecraft.getCameraEntity();
         float var4 = var3.walkDist - var3.walkDistO;
         float var5 = -(var3.walkDist + var4 * var2);
         float var6 = Mth.lerp(var2, var3.oBob, var3.bob);
         var1.translate((double)(Mth.sin(var5 * 3.1415927F) * var6 * 0.5F), (double)(-Math.abs(Mth.cos(var5 * 3.1415927F) * var6)), 0.0D);
         var1.mulPose(Vector3f.ZP.rotationDegrees(Mth.sin(var5 * 3.1415927F) * var6 * 3.0F));
         var1.mulPose(Vector3f.XP.rotationDegrees(Math.abs(Mth.cos(var5 * 3.1415927F - 0.2F) * var6) * 5.0F));
      }
   }

   private void renderItemInHand(PoseStack var1, Camera var2, float var3) {
      if (!this.panoramicMode) {
         this.resetProjectionMatrix(this.getProjectionMatrix(var2, var3, false));
         PoseStack.Pose var4 = var1.last();
         var4.pose().setIdentity();
         var4.normal().setIdentity();
         var1.pushPose();
         this.bobHurt(var1, var3);
         if (this.minecraft.options.bobView) {
            this.bobView(var1, var3);
         }

         boolean var5 = this.minecraft.getCameraEntity() instanceof LivingEntity && ((LivingEntity)this.minecraft.getCameraEntity()).isSleeping();
         if (this.minecraft.options.thirdPersonView == 0 && !var5 && !this.minecraft.options.hideGui && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.lightTexture.turnOnLightLayer();
            this.itemInHandRenderer.renderHandsWithItems(var3, var1, this.renderBuffers.bufferSource(), this.minecraft.player, this.minecraft.getEntityRenderDispatcher().getPackedLightCoords(this.minecraft.player, var3));
            this.lightTexture.turnOffLightLayer();
         }

         var1.popPose();
         if (this.minecraft.options.thirdPersonView == 0 && !var5) {
            ScreenEffectRenderer.renderScreenEffect(this.minecraft, var1);
            this.bobHurt(var1, var3);
         }

         if (this.minecraft.options.bobView) {
            this.bobView(var1, var3);
         }

      }
   }

   public void resetProjectionMatrix(Matrix4f var1) {
      RenderSystem.matrixMode(5889);
      RenderSystem.loadIdentity();
      RenderSystem.multMatrix(var1);
      RenderSystem.matrixMode(5888);
   }

   public Matrix4f getProjectionMatrix(Camera var1, float var2, boolean var3) {
      PoseStack var4 = new PoseStack();
      var4.last().pose().setIdentity();
      if (this.zoom != 1.0F) {
         var4.translate((double)this.zoomX, (double)(-this.zoomY), 0.0D);
         var4.scale(this.zoom, this.zoom, 1.0F);
      }

      var4.last().pose().multiply(Matrix4f.perspective(this.getFov(var1, var2, var3), (float)this.minecraft.getWindow().getWidth() / (float)this.minecraft.getWindow().getHeight(), 0.05F, this.renderDistance * 4.0F));
      return var4.last().pose();
   }

   public static float getNightVisionScale(LivingEntity var0, float var1) {
      int var2 = var0.getEffect(MobEffects.NIGHT_VISION).getDuration();
      return var2 > 200 ? 1.0F : 0.7F + Mth.sin(((float)var2 - var1) * 3.1415927F * 0.2F) * 0.3F;
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
         int var5 = (int)(this.minecraft.mouseHandler.xpos() * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth());
         int var6 = (int)(this.minecraft.mouseHandler.ypos() * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight());
         PoseStack var7 = new PoseStack();
         RenderSystem.viewport(0, 0, this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight());
         if (var4 && this.minecraft.level != null) {
            this.minecraft.getProfiler().push("level");
            this.renderLevel(var1, var2, var7);
            if (this.minecraft.hasSingleplayerServer() && this.lastScreenshotAttempt < Util.getMillis() - 1000L) {
               this.lastScreenshotAttempt = Util.getMillis();
               if (!this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
                  this.takeAutoScreenshot();
               }
            }

            this.minecraft.levelRenderer.doEntityOutline();
            if (this.postEffect != null && this.effectActive) {
               RenderSystem.disableBlend();
               RenderSystem.disableDepthTest();
               RenderSystem.disableAlphaTest();
               RenderSystem.enableTexture();
               RenderSystem.matrixMode(5890);
               RenderSystem.pushMatrix();
               RenderSystem.loadIdentity();
               this.postEffect.process(var1);
               RenderSystem.popMatrix();
            }

            this.minecraft.getMainRenderTarget().bindWrite(true);
         }

         Window var8 = this.minecraft.getWindow();
         RenderSystem.clear(256, Minecraft.ON_OSX);
         RenderSystem.matrixMode(5889);
         RenderSystem.loadIdentity();
         RenderSystem.ortho(0.0D, (double)var8.getWidth() / var8.getGuiScale(), (double)var8.getHeight() / var8.getGuiScale(), 0.0D, 1000.0D, 3000.0D);
         RenderSystem.matrixMode(5888);
         RenderSystem.loadIdentity();
         RenderSystem.translatef(0.0F, 0.0F, -2000.0F);
         Lighting.setupFor3DItems();
         if (var4 && this.minecraft.level != null) {
            this.minecraft.getProfiler().popPush("gui");
            if (!this.minecraft.options.hideGui || this.minecraft.screen != null) {
               RenderSystem.defaultAlphaFunc();
               this.renderItemActivationAnimation(this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), var1);
               this.minecraft.gui.render(var1);
               RenderSystem.clear(256, Minecraft.ON_OSX);
            }

            this.minecraft.getProfiler().pop();
         }

         CrashReport var10;
         CrashReportCategory var11;
         if (this.minecraft.overlay != null) {
            try {
               this.minecraft.overlay.render(var5, var6, this.minecraft.getDeltaFrameTime());
            } catch (Throwable var13) {
               var10 = CrashReport.forThrowable(var13, "Rendering overlay");
               var11 = var10.addCategory("Overlay render details");
               var11.setDetail("Overlay name", () -> {
                  return this.minecraft.overlay.getClass().getCanonicalName();
               });
               throw new ReportedException(var10);
            }
         } else if (this.minecraft.screen != null) {
            try {
               this.minecraft.screen.render(var5, var6, this.minecraft.getDeltaFrameTime());
            } catch (Throwable var12) {
               var10 = CrashReport.forThrowable(var12, "Rendering screen");
               var11 = var10.addCategory("Screen render details");
               var11.setDetail("Screen name", () -> {
                  return this.minecraft.screen.getClass().getCanonicalName();
               });
               var11.setDetail("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", var5, var6, this.minecraft.mouseHandler.xpos(), this.minecraft.mouseHandler.ypos());
               });
               var11.setDetail("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.minecraft.getWindow().getGuiScaledWidth(), this.minecraft.getWindow().getGuiScaledHeight(), this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getWindow().getGuiScale());
               });
               throw new ReportedException(var10);
            }
         }

      }
   }

   private void takeAutoScreenshot() {
      if (this.minecraft.levelRenderer.countRenderedChunks() > 10 && this.minecraft.levelRenderer.hasRenderedAllChunks() && !this.minecraft.getSingleplayerServer().hasWorldScreenshot()) {
         NativeImage var1 = Screenshot.takeScreenshot(this.minecraft.getWindow().getWidth(), this.minecraft.getWindow().getHeight(), this.minecraft.getMainRenderTarget());
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

   public void renderLevel(float var1, long var2, PoseStack var4) {
      this.lightTexture.updateLightTexture(var1);
      if (this.minecraft.getCameraEntity() == null) {
         this.minecraft.setCameraEntity(this.minecraft.player);
      }

      this.pick(var1);
      this.minecraft.getProfiler().push("center");
      boolean var5 = this.shouldRenderBlockOutline();
      this.minecraft.getProfiler().popPush("camera");
      Camera var6 = this.mainCamera;
      this.renderDistance = (float)(this.minecraft.options.renderDistance * 16);
      PoseStack var7 = new PoseStack();
      var7.last().pose().multiply(this.getProjectionMatrix(var6, var1, true));
      this.bobHurt(var7, var1);
      if (this.minecraft.options.bobView) {
         this.bobView(var7, var1);
      }

      float var8 = Mth.lerp(var1, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
      if (var8 > 0.0F) {
         byte var9 = 20;
         if (this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
            var9 = 7;
         }

         float var10 = 5.0F / (var8 * var8 + 5.0F) - var8 * 0.04F;
         var10 *= var10;
         Vector3f var11 = new Vector3f(0.0F, Mth.SQRT_OF_TWO / 2.0F, Mth.SQRT_OF_TWO / 2.0F);
         var7.mulPose(var11.rotationDegrees(((float)this.tick + var1) * (float)var9));
         var7.scale(1.0F / var10, 1.0F, 1.0F);
         float var12 = -((float)this.tick + var1) * (float)var9;
         var7.mulPose(var11.rotationDegrees(var12));
      }

      Matrix4f var13 = var7.last().pose();
      this.resetProjectionMatrix(var13);
      var6.setup(this.minecraft.level, (Entity)(this.minecraft.getCameraEntity() == null ? this.minecraft.player : this.minecraft.getCameraEntity()), this.minecraft.options.thirdPersonView > 0, this.minecraft.options.thirdPersonView == 2, var1);
      var4.mulPose(Vector3f.XP.rotationDegrees(var6.getXRot()));
      var4.mulPose(Vector3f.YP.rotationDegrees(var6.getYRot() + 180.0F));
      this.minecraft.levelRenderer.renderLevel(var4, var1, var2, var5, var6, this, this.lightTexture, var13);
      this.minecraft.getProfiler().popPush("hand");
      if (this.renderHand) {
         RenderSystem.clear(256, Minecraft.ON_OSX);
         this.renderItemInHand(var4, var6, var1);
      }

      this.minecraft.getProfiler().pop();
   }

   public void resetData() {
      this.itemActivationItem = null;
      this.mapRenderer.resetData();
      this.mainCamera.reset();
   }

   public MapRenderer getMapRenderer() {
      return this.mapRenderer;
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
         RenderSystem.enableAlphaTest();
         RenderSystem.pushMatrix();
         RenderSystem.pushLightingAttributes();
         RenderSystem.enableDepthTest();
         RenderSystem.disableCull();
         PoseStack var12 = new PoseStack();
         var12.pushPose();
         var12.translate((double)((float)(var1 / 2) + var10 * Mth.abs(Mth.sin(var9 * 2.0F))), (double)((float)(var2 / 2) + var11 * Mth.abs(Mth.sin(var9 * 2.0F))), -50.0D);
         float var13 = 50.0F + 175.0F * Mth.sin(var9);
         var12.scale(var13, -var13, var13);
         var12.mulPose(Vector3f.YP.rotationDegrees(900.0F * Mth.abs(Mth.sin(var9))));
         var12.mulPose(Vector3f.XP.rotationDegrees(6.0F * Mth.cos(var5 * 8.0F)));
         var12.mulPose(Vector3f.ZP.rotationDegrees(6.0F * Mth.cos(var5 * 8.0F)));
         MultiBufferSource.BufferSource var14 = this.renderBuffers.bufferSource();
         this.minecraft.getItemRenderer().renderStatic(this.itemActivationItem, ItemTransforms.TransformType.FIXED, 15728880, OverlayTexture.NO_OVERLAY, var12, var14);
         var12.popPose();
         var14.endBatch();
         RenderSystem.popAttributes();
         RenderSystem.popMatrix();
         RenderSystem.enableCull();
         RenderSystem.disableDepthTest();
      }
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

   static {
      EFFECT_NONE = EFFECTS.length;
   }
}
