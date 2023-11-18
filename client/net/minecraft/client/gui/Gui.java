package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.datafixers.util.Pair;
import com.mojang.math.Axis;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class Gui {
   private static final ResourceLocation CROSSHAIR_SPRITE = new ResourceLocation("hud/crosshair");
   private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = new ResourceLocation("hud/crosshair_attack_indicator_full");
   private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = new ResourceLocation("hud/crosshair_attack_indicator_background");
   private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = new ResourceLocation("hud/crosshair_attack_indicator_progress");
   private static final ResourceLocation EFFECT_BACKGROUND_AMBIENT_SPRITE = new ResourceLocation("hud/effect_background_ambient");
   private static final ResourceLocation EFFECT_BACKGROUND_SPRITE = new ResourceLocation("hud/effect_background");
   private static final ResourceLocation HOTBAR_SPRITE = new ResourceLocation("hud/hotbar");
   private static final ResourceLocation HOTBAR_SELECTION_SPRITE = new ResourceLocation("hud/hotbar_selection");
   private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = new ResourceLocation("hud/hotbar_offhand_left");
   private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = new ResourceLocation("hud/hotbar_offhand_right");
   private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = new ResourceLocation("hud/hotbar_attack_indicator_background");
   private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = new ResourceLocation("hud/hotbar_attack_indicator_progress");
   private static final ResourceLocation JUMP_BAR_BACKGROUND_SPRITE = new ResourceLocation("hud/jump_bar_background");
   private static final ResourceLocation JUMP_BAR_COOLDOWN_SPRITE = new ResourceLocation("hud/jump_bar_cooldown");
   private static final ResourceLocation JUMP_BAR_PROGRESS_SPRITE = new ResourceLocation("hud/jump_bar_progress");
   private static final ResourceLocation EXPERIENCE_BAR_BACKGROUND_SPRITE = new ResourceLocation("hud/experience_bar_background");
   private static final ResourceLocation EXPERIENCE_BAR_PROGRESS_SPRITE = new ResourceLocation("hud/experience_bar_progress");
   private static final ResourceLocation ARMOR_EMPTY_SPRITE = new ResourceLocation("hud/armor_empty");
   private static final ResourceLocation ARMOR_HALF_SPRITE = new ResourceLocation("hud/armor_half");
   private static final ResourceLocation ARMOR_FULL_SPRITE = new ResourceLocation("hud/armor_full");
   private static final ResourceLocation FOOD_EMPTY_HUNGER_SPRITE = new ResourceLocation("hud/food_empty_hunger");
   private static final ResourceLocation FOOD_HALF_HUNGER_SPRITE = new ResourceLocation("hud/food_half_hunger");
   private static final ResourceLocation FOOD_FULL_HUNGER_SPRITE = new ResourceLocation("hud/food_full_hunger");
   private static final ResourceLocation FOOD_EMPTY_SPRITE = new ResourceLocation("hud/food_empty");
   private static final ResourceLocation FOOD_HALF_SPRITE = new ResourceLocation("hud/food_half");
   private static final ResourceLocation FOOD_FULL_SPRITE = new ResourceLocation("hud/food_full");
   private static final ResourceLocation AIR_SPRITE = new ResourceLocation("hud/air");
   private static final ResourceLocation AIR_BURSTING_SPRITE = new ResourceLocation("hud/air_bursting");
   private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = new ResourceLocation("hud/heart/vehicle_container");
   private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = new ResourceLocation("hud/heart/vehicle_full");
   private static final ResourceLocation HEART_VEHICLE_HALF_SPRITE = new ResourceLocation("hud/heart/vehicle_half");
   private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
   private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = new ResourceLocation("textures/misc/spyglass_scope.png");
   private static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = new ResourceLocation("textures/misc/powder_snow_outline.png");
   private static final Component DEMO_EXPIRED_TEXT = Component.translatable("demo.demoExpired");
   private static final Component SAVING_TEXT = Component.translatable("menu.savingLevel");
   private static final int COLOR_WHITE = 16777215;
   private static final float MIN_CROSSHAIR_ATTACK_SPEED = 5.0F;
   private static final int NUM_HEARTS_PER_ROW = 10;
   private static final int LINE_HEIGHT = 10;
   private static final String SPACER = ": ";
   private static final float PORTAL_OVERLAY_ALPHA_MIN = 0.2F;
   private static final int HEART_SIZE = 9;
   private static final int HEART_SEPARATION = 8;
   private static final float AUTOSAVE_FADE_SPEED_FACTOR = 0.2F;
   private final RandomSource random = RandomSource.create();
   private final Minecraft minecraft;
   private final ItemRenderer itemRenderer;
   private final ChatComponent chat;
   private int tickCount;
   @Nullable
   private Component overlayMessageString;
   private int overlayMessageTime;
   private boolean animateOverlayMessageColor;
   private boolean chatDisabledByPlayerShown;
   public float vignetteBrightness = 1.0F;
   private int toolHighlightTimer;
   private ItemStack lastToolHighlight = ItemStack.EMPTY;
   private final DebugScreenOverlay debugOverlay;
   private final SubtitleOverlay subtitleOverlay;
   private final SpectatorGui spectatorGui;
   private final PlayerTabOverlay tabList;
   private final BossHealthOverlay bossOverlay;
   private int titleTime;
   @Nullable
   private Component title;
   @Nullable
   private Component subtitle;
   private int titleFadeInTime;
   private int titleStayTime;
   private int titleFadeOutTime;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private int screenWidth;
   private int screenHeight;
   private float autosaveIndicatorValue;
   private float lastAutosaveIndicatorValue;
   private float scopeScale;

   public Gui(Minecraft var1, ItemRenderer var2) {
      super();
      this.minecraft = var1;
      this.itemRenderer = var2;
      this.debugOverlay = new DebugScreenOverlay(var1);
      this.spectatorGui = new SpectatorGui(var1);
      this.chat = new ChatComponent(var1);
      this.tabList = new PlayerTabOverlay(var1, this);
      this.bossOverlay = new BossHealthOverlay(var1);
      this.subtitleOverlay = new SubtitleOverlay(var1);
      this.resetTitleTimes();
   }

   public void resetTitleTimes() {
      this.titleFadeInTime = 10;
      this.titleStayTime = 70;
      this.titleFadeOutTime = 20;
   }

   public void render(GuiGraphics var1, float var2) {
      Window var3 = this.minecraft.getWindow();
      this.screenWidth = var1.guiWidth();
      this.screenHeight = var1.guiHeight();
      Font var4 = this.getFont();
      RenderSystem.enableBlend();
      if (Minecraft.useFancyGraphics()) {
         this.renderVignette(var1, this.minecraft.getCameraEntity());
      } else {
         RenderSystem.enableDepthTest();
      }

      float var5 = this.minecraft.getDeltaFrameTime();
      this.scopeScale = Mth.lerp(0.5F * var5, this.scopeScale, 1.125F);
      if (this.minecraft.options.getCameraType().isFirstPerson()) {
         if (this.minecraft.player.isScoping()) {
            this.renderSpyglassOverlay(var1, this.scopeScale);
         } else {
            this.scopeScale = 0.5F;
            ItemStack var6 = this.minecraft.player.getInventory().getArmor(3);
            if (var6.is(Blocks.CARVED_PUMPKIN.asItem())) {
               this.renderTextureOverlay(var1, PUMPKIN_BLUR_LOCATION, 1.0F);
            }
         }
      }

      if (this.minecraft.player.getTicksFrozen() > 0) {
         this.renderTextureOverlay(var1, POWDER_SNOW_OUTLINE_LOCATION, this.minecraft.player.getPercentFrozen());
      }

      float var13 = Mth.lerp(var2, this.minecraft.player.oSpinningEffectIntensity, this.minecraft.player.spinningEffectIntensity);
      if (var13 > 0.0F && !this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
         this.renderPortalOverlay(var1, var13);
      }

      if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
         this.spectatorGui.renderHotbar(var1);
      } else if (!this.minecraft.options.hideGui) {
         this.renderHotbar(var2, var1);
      }

      if (!this.minecraft.options.hideGui) {
         RenderSystem.enableBlend();
         this.renderCrosshair(var1);
         this.minecraft.getProfiler().push("bossHealth");
         this.bossOverlay.render(var1);
         this.minecraft.getProfiler().pop();
         if (this.minecraft.gameMode.canHurtPlayer()) {
            this.renderPlayerHealth(var1);
         }

         this.renderVehicleHealth(var1);
         RenderSystem.disableBlend();
         int var7 = this.screenWidth / 2 - 91;
         PlayerRideableJumping var8 = this.minecraft.player.jumpableVehicle();
         if (var8 != null) {
            this.renderJumpMeter(var8, var1, var7);
         } else if (this.minecraft.gameMode.hasExperience()) {
            this.renderExperienceBar(var1, var7);
         }

         if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.renderSelectedItemName(var1);
         } else if (this.minecraft.player.isSpectator()) {
            this.spectatorGui.renderTooltip(var1);
         }
      }

      if (this.minecraft.player.getSleepTimer() > 0) {
         this.minecraft.getProfiler().push("sleep");
         float var14 = (float)this.minecraft.player.getSleepTimer();
         float var18 = var14 / 100.0F;
         if (var18 > 1.0F) {
            var18 = 1.0F - (var14 - 100.0F) / 10.0F;
         }

         int var9 = (int)(220.0F * var18) << 24 | 1052704;
         var1.fill(RenderType.guiOverlay(), 0, 0, this.screenWidth, this.screenHeight, var9);
         this.minecraft.getProfiler().pop();
      }

      if (this.minecraft.isDemo()) {
         this.renderDemoOverlay(var1);
      }

      this.renderEffects(var1);
      if (this.debugOverlay.showDebugScreen()) {
         this.debugOverlay.render(var1);
      }

      if (!this.minecraft.options.hideGui) {
         if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
            this.minecraft.getProfiler().push("overlayMessage");
            float var15 = (float)this.overlayMessageTime - var2;
            int var19 = (int)(var15 * 255.0F / 20.0F);
            if (var19 > 255) {
               var19 = 255;
            }

            if (var19 > 8) {
               var1.pose().pushPose();
               var1.pose().translate((float)(this.screenWidth / 2), (float)(this.screenHeight - 68), 0.0F);
               int var23 = 16777215;
               if (this.animateOverlayMessageColor) {
                  var23 = Mth.hsvToRgb(var15 / 50.0F, 0.7F, 0.6F) & 16777215;
               }

               int var10 = var19 << 24 & 0xFF000000;
               int var11 = var4.width(this.overlayMessageString);
               this.drawBackdrop(var1, var4, -4, var11, 16777215 | var10);
               var1.drawString(var4, this.overlayMessageString, -var11 / 2, -4, var23 | var10);
               var1.pose().popPose();
            }

            this.minecraft.getProfiler().pop();
         }

         if (this.title != null && this.titleTime > 0) {
            this.minecraft.getProfiler().push("titleAndSubtitle");
            float var16 = (float)this.titleTime - var2;
            int var20 = 255;
            if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
               float var24 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - var16;
               var20 = (int)(var24 * 255.0F / (float)this.titleFadeInTime);
            }

            if (this.titleTime <= this.titleFadeOutTime) {
               var20 = (int)(var16 * 255.0F / (float)this.titleFadeOutTime);
            }

            var20 = Mth.clamp(var20, 0, 255);
            if (var20 > 8) {
               var1.pose().pushPose();
               var1.pose().translate((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), 0.0F);
               RenderSystem.enableBlend();
               var1.pose().pushPose();
               var1.pose().scale(4.0F, 4.0F, 4.0F);
               int var25 = var20 << 24 & 0xFF000000;
               int var27 = var4.width(this.title);
               this.drawBackdrop(var1, var4, -10, var27, 16777215 | var25);
               var1.drawString(var4, this.title, -var27 / 2, -10, 16777215 | var25);
               var1.pose().popPose();
               if (this.subtitle != null) {
                  var1.pose().pushPose();
                  var1.pose().scale(2.0F, 2.0F, 2.0F);
                  int var31 = var4.width(this.subtitle);
                  this.drawBackdrop(var1, var4, 5, var31, 16777215 | var25);
                  var1.drawString(var4, this.subtitle, -var31 / 2, 5, 16777215 | var25);
                  var1.pose().popPose();
               }

               RenderSystem.disableBlend();
               var1.pose().popPose();
            }

            this.minecraft.getProfiler().pop();
         }

         this.subtitleOverlay.render(var1);
         Scoreboard var17 = this.minecraft.level.getScoreboard();
         Objective var22 = null;
         PlayerTeam var26 = var17.getPlayersTeam(this.minecraft.player.getScoreboardName());
         if (var26 != null) {
            DisplaySlot var28 = DisplaySlot.teamColorToSlot(var26.getColor());
            if (var28 != null) {
               var22 = var17.getDisplayObjective(var28);
            }
         }

         Objective var29 = var22 != null ? var22 : var17.getDisplayObjective(DisplaySlot.SIDEBAR);
         if (var29 != null) {
            this.displayScoreboardSidebar(var1, var29);
         }

         RenderSystem.enableBlend();
         int var32 = Mth.floor(this.minecraft.mouseHandler.xpos() * (double)var3.getGuiScaledWidth() / (double)var3.getScreenWidth());
         int var12 = Mth.floor(this.minecraft.mouseHandler.ypos() * (double)var3.getGuiScaledHeight() / (double)var3.getScreenHeight());
         this.minecraft.getProfiler().push("chat");
         this.chat.render(var1, this.tickCount, var32, var12);
         this.minecraft.getProfiler().pop();
         var29 = var17.getDisplayObjective(DisplaySlot.LIST);
         if (!this.minecraft.options.keyPlayerList.isDown()
            || this.minecraft.isLocalServer() && this.minecraft.player.connection.getListedOnlinePlayers().size() <= 1 && var29 == null) {
            this.tabList.setVisible(false);
         } else {
            this.tabList.setVisible(true);
            this.tabList.render(var1, this.screenWidth, var17, var29);
         }

         this.renderSavingIndicator(var1);
      }
   }

   private void drawBackdrop(GuiGraphics var1, Font var2, int var3, int var4, int var5) {
      int var6 = this.minecraft.options.getBackgroundColor(0.0F);
      if (var6 != 0) {
         int var7 = -var4 / 2;
         var1.fill(var7 - 2, var3 - 2, var7 + var4 + 2, var3 + 9 + 2, FastColor.ARGB32.multiply(var6, var5));
      }
   }

   private void renderCrosshair(GuiGraphics var1) {
      Options var2 = this.minecraft.options;
      if (var2.getCameraType().isFirstPerson()) {
         if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            if (this.debugOverlay.showDebugScreen() && !this.minecraft.player.isReducedDebugInfo() && !var2.reducedDebugInfo().get()) {
               Camera var9 = this.minecraft.gameRenderer.getMainCamera();
               PoseStack var10 = RenderSystem.getModelViewStack();
               var10.pushPose();
               var10.mulPoseMatrix(var1.pose().last().pose());
               var10.translate((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), 0.0F);
               var10.mulPose(Axis.XN.rotationDegrees(var9.getXRot()));
               var10.mulPose(Axis.YP.rotationDegrees(var9.getYRot()));
               var10.scale(-1.0F, -1.0F, -1.0F);
               RenderSystem.applyModelViewMatrix();
               RenderSystem.renderCrosshair(10);
               var10.popPose();
               RenderSystem.applyModelViewMatrix();
            } else {
               RenderSystem.blendFuncSeparate(
                  GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR,
                  GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                  GlStateManager.SourceFactor.ONE,
                  GlStateManager.DestFactor.ZERO
               );
               boolean var3 = true;
               var1.blitSprite(CROSSHAIR_SPRITE, (this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 15, 15);
               if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                  float var4 = this.minecraft.player.getAttackStrengthScale(0.0F);
                  boolean var5 = false;
                  if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && var4 >= 1.0F) {
                     var5 = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                     var5 &= this.minecraft.crosshairPickEntity.isAlive();
                  }

                  int var6 = this.screenHeight / 2 - 7 + 16;
                  int var7 = this.screenWidth / 2 - 8;
                  if (var5) {
                     var1.blitSprite(CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, var7, var6, 16, 16);
                  } else if (var4 < 1.0F) {
                     int var8 = (int)(var4 * 17.0F);
                     var1.blitSprite(CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, var7, var6, 16, 4);
                     var1.blitSprite(CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, var7, var6, var8, 4);
                  }
               }

               RenderSystem.defaultBlendFunc();
            }
         }
      }
   }

   private boolean canRenderCrosshairForSpectator(HitResult var1) {
      if (var1 == null) {
         return false;
      } else if (var1.getType() == HitResult.Type.ENTITY) {
         return ((EntityHitResult)var1).getEntity() instanceof MenuProvider;
      } else if (var1.getType() == HitResult.Type.BLOCK) {
         BlockPos var2 = ((BlockHitResult)var1).getBlockPos();
         ClientLevel var3 = this.minecraft.level;
         return var3.getBlockState(var2).getMenuProvider(var3, var2) != null;
      } else {
         return false;
      }
   }

   protected void renderEffects(GuiGraphics var1) {
      Collection var2 = this.minecraft.player.getActiveEffects();
      if (!var2.isEmpty()) {
         Screen var4 = this.minecraft.screen;
         if (var4 instanceof EffectRenderingInventoryScreen var3 && var3.canSeeEffects()) {
            return;
         }

         RenderSystem.enableBlend();
         int var17 = 0;
         int var18 = 0;
         MobEffectTextureManager var5 = this.minecraft.getMobEffectTextures();
         ArrayList var6 = Lists.newArrayListWithExpectedSize(var2.size());

         for(MobEffectInstance var8 : Ordering.natural().reverse().sortedCopy(var2)) {
            MobEffect var9 = var8.getEffect();
            if (var8.showIcon()) {
               int var10 = this.screenWidth;
               int var11 = 1;
               if (this.minecraft.isDemo()) {
                  var11 += 15;
               }

               if (var9.isBeneficial()) {
                  ++var17;
                  var10 -= 25 * var17;
               } else {
                  ++var18;
                  var10 -= 25 * var18;
                  var11 += 26;
               }

               float var12 = 1.0F;
               if (var8.isAmbient()) {
                  var1.blitSprite(EFFECT_BACKGROUND_AMBIENT_SPRITE, var10, var11, 24, 24);
               } else {
                  var1.blitSprite(EFFECT_BACKGROUND_SPRITE, var10, var11, 24, 24);
                  if (var8.endsWithin(200)) {
                     int var13 = var8.getDuration();
                     int var14 = 10 - var13 / 20;
                     var12 = Mth.clamp((float)var13 / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F)
                        + Mth.cos((float)var13 * 3.1415927F / 5.0F) * Mth.clamp((float)var14 / 10.0F * 0.25F, 0.0F, 0.25F);
                  }
               }

               TextureAtlasSprite var20 = var5.get(var9);
               int var21 = var10;
               int var15 = var11;
               float var16 = var12;
               var6.add(() -> {
                  var1.setColor(1.0F, 1.0F, 1.0F, var16);
                  var1.blit(var21 + 3, var15 + 3, 0, 18, 18, var20);
                  var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
               });
            }
         }

         var6.forEach(Runnable::run);
      }
   }

   private void renderHotbar(float var1, GuiGraphics var2) {
      Player var3 = this.getCameraPlayer();
      if (var3 != null) {
         ItemStack var4 = var3.getOffhandItem();
         HumanoidArm var5 = var3.getMainArm().getOpposite();
         int var6 = this.screenWidth / 2;
         boolean var7 = true;
         boolean var8 = true;
         var2.pose().pushPose();
         var2.pose().translate(0.0F, 0.0F, -90.0F);
         var2.blitSprite(HOTBAR_SPRITE, var6 - 91, this.screenHeight - 22, 182, 22);
         var2.blitSprite(HOTBAR_SELECTION_SPRITE, var6 - 91 - 1 + var3.getInventory().selected * 20, this.screenHeight - 22 - 1, 24, 23);
         if (!var4.isEmpty()) {
            if (var5 == HumanoidArm.LEFT) {
               var2.blitSprite(HOTBAR_OFFHAND_LEFT_SPRITE, var6 - 91 - 29, this.screenHeight - 23, 29, 24);
            } else {
               var2.blitSprite(HOTBAR_OFFHAND_RIGHT_SPRITE, var6 + 91, this.screenHeight - 23, 29, 24);
            }
         }

         var2.pose().popPose();
         int var9 = 1;

         for(int var10 = 0; var10 < 9; ++var10) {
            int var11 = var6 - 90 + var10 * 20 + 2;
            int var12 = this.screenHeight - 16 - 3;
            this.renderSlot(var2, var11, var12, var1, var3, var3.getInventory().items.get(var10), var9++);
         }

         if (!var4.isEmpty()) {
            int var16 = this.screenHeight - 16 - 3;
            if (var5 == HumanoidArm.LEFT) {
               this.renderSlot(var2, var6 - 91 - 26, var16, var1, var3, var4, var9++);
            } else {
               this.renderSlot(var2, var6 + 91 + 10, var16, var1, var3, var4, var9++);
            }
         }

         RenderSystem.enableBlend();
         if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            float var17 = this.minecraft.player.getAttackStrengthScale(0.0F);
            if (var17 < 1.0F) {
               int var18 = this.screenHeight - 20;
               int var19 = var6 + 91 + 6;
               if (var5 == HumanoidArm.RIGHT) {
                  var19 = var6 - 91 - 22;
               }

               int var13 = (int)(var17 * 19.0F);
               var2.blitSprite(HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, var19, var18, 18, 18);
               var2.blitSprite(HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - var13, var19, var18 + 18 - var13, 18, var13);
            }
         }

         RenderSystem.disableBlend();
      }
   }

   public void renderJumpMeter(PlayerRideableJumping var1, GuiGraphics var2, int var3) {
      this.minecraft.getProfiler().push("jumpBar");
      float var4 = this.minecraft.player.getJumpRidingScale();
      boolean var5 = true;
      int var6 = (int)(var4 * 183.0F);
      int var7 = this.screenHeight - 32 + 3;
      var2.blitSprite(JUMP_BAR_BACKGROUND_SPRITE, var3, var7, 182, 5);
      if (var1.getJumpCooldown() > 0) {
         var2.blitSprite(JUMP_BAR_COOLDOWN_SPRITE, var3, var7, 182, 5);
      } else if (var6 > 0) {
         var2.blitSprite(JUMP_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, var3, var7, var6, 5);
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderExperienceBar(GuiGraphics var1, int var2) {
      this.minecraft.getProfiler().push("expBar");
      int var3 = this.minecraft.player.getXpNeededForNextLevel();
      if (var3 > 0) {
         boolean var4 = true;
         int var5 = (int)(this.minecraft.player.experienceProgress * 183.0F);
         int var6 = this.screenHeight - 32 + 3;
         var1.blitSprite(EXPERIENCE_BAR_BACKGROUND_SPRITE, var2, var6, 182, 5);
         if (var5 > 0) {
            var1.blitSprite(EXPERIENCE_BAR_PROGRESS_SPRITE, 182, 5, 0, 0, var2, var6, var5, 5);
         }
      }

      this.minecraft.getProfiler().pop();
      if (this.minecraft.player.experienceLevel > 0) {
         this.minecraft.getProfiler().push("expLevel");
         String var7 = this.minecraft.player.experienceLevel + "";
         int var8 = (this.screenWidth - this.getFont().width(var7)) / 2;
         int var9 = this.screenHeight - 31 - 4;
         var1.drawString(this.getFont(), var7, var8 + 1, var9, 0, false);
         var1.drawString(this.getFont(), var7, var8 - 1, var9, 0, false);
         var1.drawString(this.getFont(), var7, var8, var9 + 1, 0, false);
         var1.drawString(this.getFont(), var7, var8, var9 - 1, 0, false);
         var1.drawString(this.getFont(), var7, var8, var9, 8453920, false);
         this.minecraft.getProfiler().pop();
      }
   }

   public void renderSelectedItemName(GuiGraphics var1) {
      this.minecraft.getProfiler().push("selectedItemName");
      if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
         MutableComponent var2 = Component.empty().append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color);
         if (this.lastToolHighlight.hasCustomHoverName()) {
            var2.withStyle(ChatFormatting.ITALIC);
         }

         int var3 = this.getFont().width(var2);
         int var4 = (this.screenWidth - var3) / 2;
         int var5 = this.screenHeight - 59;
         if (!this.minecraft.gameMode.canHurtPlayer()) {
            var5 += 14;
         }

         int var6 = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
         if (var6 > 255) {
            var6 = 255;
         }

         if (var6 > 0) {
            var1.fill(var4 - 2, var5 - 2, var4 + var3 + 2, var5 + 9 + 2, this.minecraft.options.getBackgroundColor(0));
            var1.drawString(this.getFont(), var2, var4, var5, 16777215 + (var6 << 24));
         }
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderDemoOverlay(GuiGraphics var1) {
      this.minecraft.getProfiler().push("demo");
      Object var2;
      if (this.minecraft.level.getGameTime() >= 120500L) {
         var2 = DEMO_EXPIRED_TEXT;
      } else {
         var2 = Component.translatable("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime())));
      }

      int var3 = this.getFont().width((FormattedText)var2);
      var1.drawString(this.getFont(), (Component)var2, this.screenWidth - var3 - 10, 5, 16777215);
      this.minecraft.getProfiler().pop();
   }

   private void displayScoreboardSidebar(GuiGraphics var1, Objective var2) {
      Scoreboard var3 = var2.getScoreboard();
      Collection var4 = var3.getPlayerScores(var2);
      List var5 = var4.stream().filter(var0 -> var0.getOwner() != null && !var0.getOwner().startsWith("#")).collect(Collectors.toList());
      if (var5.size() > 15) {
         var4 = Lists.newArrayList(Iterables.skip(var5, var4.size() - 15));
      } else {
         var4 = var5;
      }

      ArrayList var6 = Lists.newArrayListWithCapacity(var4.size());
      Component var7 = var2.getDisplayName();
      int var8 = this.getFont().width(var7);
      int var9 = var8;
      int var10 = this.getFont().width(": ");

      for(Score var12 : var4) {
         PlayerTeam var13 = var3.getPlayersTeam(var12.getOwner());
         MutableComponent var14 = PlayerTeam.formatNameForTeam(var13, Component.literal(var12.getOwner()));
         var6.add(Pair.of(var12, var14));
         var9 = Math.max(var9, this.getFont().width(var14) + var10 + this.getFont().width(Integer.toString(var12.getScore())));
      }

      int var27 = var4.size() * 9;
      int var28 = this.screenHeight / 2 + var27 / 3;
      boolean var29 = true;
      int var30 = this.screenWidth - var9 - 3;
      int var15 = 0;
      int var16 = this.minecraft.options.getBackgroundColor(0.3F);
      int var17 = this.minecraft.options.getBackgroundColor(0.4F);

      for(Pair var19 : var6) {
         ++var15;
         Score var20 = (Score)var19.getFirst();
         Component var21 = (Component)var19.getSecond();
         String var22 = "" + ChatFormatting.RED + var20.getScore();
         int var24 = var28 - var15 * 9;
         int var25 = this.screenWidth - 3 + 2;
         var1.fill(var30 - 2, var24, var25, var24 + 9, var16);
         var1.drawString(this.getFont(), var21, var30, var24, -1, false);
         var1.drawString(this.getFont(), var22, var25 - this.getFont().width(var22), var24, -1, false);
         if (var15 == var4.size()) {
            var1.fill(var30 - 2, var24 - 9 - 1, var25, var24 - 1, var17);
            var1.fill(var30 - 2, var24 - 1, var25, var24, var16);
            var1.drawString(this.getFont(), var7, var30 + var9 / 2 - var8 / 2, var24 - 9, -1, false);
         }
      }
   }

   private Player getCameraPlayer() {
      return !(this.minecraft.getCameraEntity() instanceof Player) ? null : (Player)this.minecraft.getCameraEntity();
   }

   private LivingEntity getPlayerVehicleWithHealth() {
      Player var1 = this.getCameraPlayer();
      if (var1 != null) {
         Entity var2 = var1.getVehicle();
         if (var2 == null) {
            return null;
         }

         if (var2 instanceof LivingEntity) {
            return (LivingEntity)var2;
         }
      }

      return null;
   }

   private int getVehicleMaxHearts(LivingEntity var1) {
      if (var1 != null && var1.showVehicleHealth()) {
         float var2 = var1.getMaxHealth();
         int var3 = (int)(var2 + 0.5F) / 2;
         if (var3 > 30) {
            var3 = 30;
         }

         return var3;
      } else {
         return 0;
      }
   }

   private int getVisibleVehicleHeartRows(int var1) {
      return (int)Math.ceil((double)var1 / 10.0);
   }

   private void renderPlayerHealth(GuiGraphics var1) {
      Player var2 = this.getCameraPlayer();
      if (var2 != null) {
         int var3 = Mth.ceil(var2.getHealth());
         boolean var4 = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
         long var5 = Util.getMillis();
         if (var3 < this.lastHealth && var2.invulnerableTime > 0) {
            this.lastHealthTime = var5;
            this.healthBlinkTime = (long)(this.tickCount + 20);
         } else if (var3 > this.lastHealth && var2.invulnerableTime > 0) {
            this.lastHealthTime = var5;
            this.healthBlinkTime = (long)(this.tickCount + 10);
         }

         if (var5 - this.lastHealthTime > 1000L) {
            this.lastHealth = var3;
            this.displayHealth = var3;
            this.lastHealthTime = var5;
         }

         this.lastHealth = var3;
         int var7 = this.displayHealth;
         this.random.setSeed((long)(this.tickCount * 312871));
         FoodData var8 = var2.getFoodData();
         int var9 = var8.getFoodLevel();
         int var10 = this.screenWidth / 2 - 91;
         int var11 = this.screenWidth / 2 + 91;
         int var12 = this.screenHeight - 39;
         float var13 = Math.max((float)var2.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(var7, var3));
         int var14 = Mth.ceil(var2.getAbsorptionAmount());
         int var15 = Mth.ceil((var13 + (float)var14) / 2.0F / 10.0F);
         int var16 = Math.max(10 - (var15 - 2), 3);
         int var17 = var12 - (var15 - 1) * var16 - 10;
         int var18 = var12 - 10;
         int var19 = var2.getArmorValue();
         int var20 = -1;
         if (var2.hasEffect(MobEffects.REGENERATION)) {
            var20 = this.tickCount % Mth.ceil(var13 + 5.0F);
         }

         this.minecraft.getProfiler().push("armor");

         for(int var21 = 0; var21 < 10; ++var21) {
            if (var19 > 0) {
               int var22 = var10 + var21 * 8;
               if (var21 * 2 + 1 < var19) {
                  var1.blitSprite(ARMOR_FULL_SPRITE, var22, var17, 9, 9);
               }

               if (var21 * 2 + 1 == var19) {
                  var1.blitSprite(ARMOR_HALF_SPRITE, var22, var17, 9, 9);
               }

               if (var21 * 2 + 1 > var19) {
                  var1.blitSprite(ARMOR_EMPTY_SPRITE, var22, var17, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().popPush("health");
         this.renderHearts(var1, var2, var10, var12, var16, var20, var13, var3, var7, var14, var4);
         LivingEntity var30 = this.getPlayerVehicleWithHealth();
         int var31 = this.getVehicleMaxHearts(var30);
         if (var31 == 0) {
            this.minecraft.getProfiler().popPush("food");

            for(int var23 = 0; var23 < 10; ++var23) {
               int var24 = var12;
               ResourceLocation var25;
               ResourceLocation var26;
               ResourceLocation var27;
               if (var2.hasEffect(MobEffects.HUNGER)) {
                  var25 = FOOD_EMPTY_HUNGER_SPRITE;
                  var26 = FOOD_HALF_HUNGER_SPRITE;
                  var27 = FOOD_FULL_HUNGER_SPRITE;
               } else {
                  var25 = FOOD_EMPTY_SPRITE;
                  var26 = FOOD_HALF_SPRITE;
                  var27 = FOOD_FULL_SPRITE;
               }

               if (var2.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (var9 * 3 + 1) == 0) {
                  var24 = var12 + (this.random.nextInt(3) - 1);
               }

               int var28 = var11 - var23 * 8 - 9;
               var1.blitSprite(var25, var28, var24, 9, 9);
               if (var23 * 2 + 1 < var9) {
                  var1.blitSprite(var27, var28, var24, 9, 9);
               }

               if (var23 * 2 + 1 == var9) {
                  var1.blitSprite(var26, var28, var24, 9, 9);
               }
            }

            var18 -= 10;
         }

         this.minecraft.getProfiler().popPush("air");
         int var32 = var2.getMaxAirSupply();
         int var33 = Math.min(var2.getAirSupply(), var32);
         if (var2.isEyeInFluid(FluidTags.WATER) || var33 < var32) {
            int var34 = this.getVisibleVehicleHeartRows(var31) - 1;
            var18 -= var34 * 10;
            int var35 = Mth.ceil((double)(var33 - 2) * 10.0 / (double)var32);
            int var36 = Mth.ceil((double)var33 * 10.0 / (double)var32) - var35;

            for(int var37 = 0; var37 < var35 + var36; ++var37) {
               if (var37 < var35) {
                  var1.blitSprite(AIR_SPRITE, var11 - var37 * 8 - 9, var18, 9, 9);
               } else {
                  var1.blitSprite(AIR_BURSTING_SPRITE, var11 - var37 * 8 - 9, var18, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void renderHearts(GuiGraphics var1, Player var2, int var3, int var4, int var5, int var6, float var7, int var8, int var9, int var10, boolean var11) {
      Gui.HeartType var12 = Gui.HeartType.forPlayer(var2);
      boolean var13 = var2.level().getLevelData().isHardcore();
      int var14 = Mth.ceil((double)var7 / 2.0);
      int var15 = Mth.ceil((double)var10 / 2.0);
      int var16 = var14 * 2;

      for(int var17 = var14 + var15 - 1; var17 >= 0; --var17) {
         int var18 = var17 / 10;
         int var19 = var17 % 10;
         int var20 = var3 + var19 * 8;
         int var21 = var4 - var18 * var5;
         if (var8 + var10 <= 4) {
            var21 += this.random.nextInt(2);
         }

         if (var17 < var14 && var17 == var6) {
            var21 -= 2;
         }

         this.renderHeart(var1, Gui.HeartType.CONTAINER, var20, var21, var13, var11, false);
         int var22 = var17 * 2;
         boolean var23 = var17 >= var14;
         if (var23) {
            int var24 = var22 - var16;
            if (var24 < var10) {
               boolean var25 = var24 + 1 == var10;
               this.renderHeart(var1, var12 == Gui.HeartType.WITHERED ? var12 : Gui.HeartType.ABSORBING, var20, var21, var13, false, var25);
            }
         }

         if (var11 && var22 < var9) {
            boolean var26 = var22 + 1 == var9;
            this.renderHeart(var1, var12, var20, var21, var13, true, var26);
         }

         if (var22 < var8) {
            boolean var27 = var22 + 1 == var8;
            this.renderHeart(var1, var12, var20, var21, var13, false, var27);
         }
      }
   }

   private void renderHeart(GuiGraphics var1, Gui.HeartType var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      var1.blitSprite(var2.getSprite(var5, var7, var6), var3, var4, 9, 9);
   }

   private void renderVehicleHealth(GuiGraphics var1) {
      LivingEntity var2 = this.getPlayerVehicleWithHealth();
      if (var2 != null) {
         int var3 = this.getVehicleMaxHearts(var2);
         if (var3 != 0) {
            int var4 = (int)Math.ceil((double)var2.getHealth());
            this.minecraft.getProfiler().popPush("mountHealth");
            int var5 = this.screenHeight - 39;
            int var6 = this.screenWidth / 2 + 91;
            int var7 = var5;

            for(int var8 = 0; var3 > 0; var8 += 20) {
               int var9 = Math.min(var3, 10);
               var3 -= var9;

               for(int var10 = 0; var10 < var9; ++var10) {
                  int var11 = var6 - var10 * 8 - 9;
                  var1.blitSprite(HEART_VEHICLE_CONTAINER_SPRITE, var11, var7, 9, 9);
                  if (var10 * 2 + 1 + var8 < var4) {
                     var1.blitSprite(HEART_VEHICLE_FULL_SPRITE, var11, var7, 9, 9);
                  }

                  if (var10 * 2 + 1 + var8 == var4) {
                     var1.blitSprite(HEART_VEHICLE_HALF_SPRITE, var11, var7, 9, 9);
                  }
               }

               var7 -= 10;
            }
         }
      }
   }

   private void renderTextureOverlay(GuiGraphics var1, ResourceLocation var2, float var3) {
      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      var1.setColor(1.0F, 1.0F, 1.0F, var3);
      var1.blit(var2, 0, 0, -90, 0.0F, 0.0F, this.screenWidth, this.screenHeight, this.screenWidth, this.screenHeight);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderSpyglassOverlay(GuiGraphics var1, float var2) {
      float var3 = (float)Math.min(this.screenWidth, this.screenHeight);
      float var5 = Math.min((float)this.screenWidth / var3, (float)this.screenHeight / var3) * var2;
      int var6 = Mth.floor(var3 * var5);
      int var7 = Mth.floor(var3 * var5);
      int var8 = (this.screenWidth - var6) / 2;
      int var9 = (this.screenHeight - var7) / 2;
      int var10 = var8 + var6;
      int var11 = var9 + var7;
      var1.blit(SPYGLASS_SCOPE_LOCATION, var8, var9, -90, 0.0F, 0.0F, var6, var7, var6, var7);
      var1.fill(RenderType.guiOverlay(), 0, var11, this.screenWidth, this.screenHeight, -90, -16777216);
      var1.fill(RenderType.guiOverlay(), 0, 0, this.screenWidth, var9, -90, -16777216);
      var1.fill(RenderType.guiOverlay(), 0, var9, var8, var11, -90, -16777216);
      var1.fill(RenderType.guiOverlay(), var10, var9, this.screenWidth, var11, -90, -16777216);
   }

   private void updateVignetteBrightness(Entity var1) {
      if (var1 != null) {
         BlockPos var2 = BlockPos.containing(var1.getX(), var1.getEyeY(), var1.getZ());
         float var3 = LightTexture.getBrightness(var1.level().dimensionType(), var1.level().getMaxLocalRawBrightness(var2));
         float var4 = Mth.clamp(1.0F - var3, 0.0F, 1.0F);
         this.vignetteBrightness += (var4 - this.vignetteBrightness) * 0.01F;
      }
   }

   private void renderVignette(GuiGraphics var1, Entity var2) {
      WorldBorder var3 = this.minecraft.level.getWorldBorder();
      float var4 = (float)var3.getDistanceToBorder(var2);
      double var5 = Math.min(var3.getLerpSpeed() * (double)var3.getWarningTime() * 1000.0, Math.abs(var3.getLerpTarget() - var3.getSize()));
      double var7 = Math.max((double)var3.getWarningBlocks(), var5);
      if ((double)var4 < var7) {
         var4 = 1.0F - (float)((double)var4 / var7);
      } else {
         var4 = 0.0F;
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      RenderSystem.blendFuncSeparate(
         GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
      );
      if (var4 > 0.0F) {
         var4 = Mth.clamp(var4, 0.0F, 1.0F);
         var1.setColor(0.0F, var4, var4, 1.0F);
      } else {
         float var9 = this.vignetteBrightness;
         var9 = Mth.clamp(var9, 0.0F, 1.0F);
         var1.setColor(var9, var9, var9, 1.0F);
      }

      var1.blit(VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, this.screenWidth, this.screenHeight, this.screenWidth, this.screenHeight);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.defaultBlendFunc();
   }

   private void renderPortalOverlay(GuiGraphics var1, float var2) {
      if (var2 < 1.0F) {
         var2 *= var2;
         var2 *= var2;
         var2 = var2 * 0.8F + 0.2F;
      }

      RenderSystem.disableDepthTest();
      RenderSystem.depthMask(false);
      var1.setColor(1.0F, 1.0F, 1.0F, var2);
      TextureAtlasSprite var3 = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
      var1.blit(0, 0, -90, this.screenWidth, this.screenHeight, var3);
      RenderSystem.depthMask(true);
      RenderSystem.enableDepthTest();
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderSlot(GuiGraphics var1, int var2, int var3, float var4, Player var5, ItemStack var6, int var7) {
      if (!var6.isEmpty()) {
         float var8 = (float)var6.getPopTime() - var4;
         if (var8 > 0.0F) {
            float var9 = 1.0F + var8 / 5.0F;
            var1.pose().pushPose();
            var1.pose().translate((float)(var2 + 8), (float)(var3 + 12), 0.0F);
            var1.pose().scale(1.0F / var9, (var9 + 1.0F) / 2.0F, 1.0F);
            var1.pose().translate((float)(-(var2 + 8)), (float)(-(var3 + 12)), 0.0F);
         }

         var1.renderItem(var5, var6, var2, var3, var7);
         if (var8 > 0.0F) {
            var1.pose().popPose();
         }

         var1.renderItemDecorations(this.minecraft.font, var6, var2, var3);
      }
   }

   public void tick(boolean var1) {
      this.tickAutosaveIndicator();
      if (!var1) {
         this.tick();
      }
   }

   private void tick() {
      if (this.overlayMessageTime > 0) {
         --this.overlayMessageTime;
      }

      if (this.titleTime > 0) {
         --this.titleTime;
         if (this.titleTime <= 0) {
            this.title = null;
            this.subtitle = null;
         }
      }

      ++this.tickCount;
      Entity var1 = this.minecraft.getCameraEntity();
      if (var1 != null) {
         this.updateVignetteBrightness(var1);
      }

      if (this.minecraft.player != null) {
         ItemStack var2 = this.minecraft.player.getInventory().getSelected();
         if (var2.isEmpty()) {
            this.toolHighlightTimer = 0;
         } else if (this.lastToolHighlight.isEmpty()
            || !var2.is(this.lastToolHighlight.getItem())
            || !var2.getHoverName().equals(this.lastToolHighlight.getHoverName())) {
            this.toolHighlightTimer = (int)(40.0 * this.minecraft.options.notificationDisplayTime().get());
         } else if (this.toolHighlightTimer > 0) {
            --this.toolHighlightTimer;
         }

         this.lastToolHighlight = var2;
      }

      this.chat.tick();
   }

   private void tickAutosaveIndicator() {
      IntegratedServer var1 = this.minecraft.getSingleplayerServer();
      boolean var2 = var1 != null && var1.isCurrentlySaving();
      this.lastAutosaveIndicatorValue = this.autosaveIndicatorValue;
      this.autosaveIndicatorValue = Mth.lerp(0.2F, this.autosaveIndicatorValue, var2 ? 1.0F : 0.0F);
   }

   public void setNowPlaying(Component var1) {
      MutableComponent var2 = Component.translatable("record.nowPlaying", var1);
      this.setOverlayMessage(var2, true);
      this.minecraft.getNarrator().sayNow(var2);
   }

   public void setOverlayMessage(Component var1, boolean var2) {
      this.setChatDisabledByPlayerShown(false);
      this.overlayMessageString = var1;
      this.overlayMessageTime = 60;
      this.animateOverlayMessageColor = var2;
   }

   public void setChatDisabledByPlayerShown(boolean var1) {
      this.chatDisabledByPlayerShown = var1;
   }

   public boolean isShowingChatDisabledByPlayer() {
      return this.chatDisabledByPlayerShown && this.overlayMessageTime > 0;
   }

   public void setTimes(int var1, int var2, int var3) {
      if (var1 >= 0) {
         this.titleFadeInTime = var1;
      }

      if (var2 >= 0) {
         this.titleStayTime = var2;
      }

      if (var3 >= 0) {
         this.titleFadeOutTime = var3;
      }

      if (this.titleTime > 0) {
         this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
      }
   }

   public void setSubtitle(Component var1) {
      this.subtitle = var1;
   }

   public void setTitle(Component var1) {
      this.title = var1;
      this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
   }

   public void clear() {
      this.title = null;
      this.subtitle = null;
      this.titleTime = 0;
   }

   public ChatComponent getChat() {
      return this.chat;
   }

   public int getGuiTicks() {
      return this.tickCount;
   }

   public Font getFont() {
      return this.minecraft.font;
   }

   public SpectatorGui getSpectatorGui() {
      return this.spectatorGui;
   }

   public PlayerTabOverlay getTabList() {
      return this.tabList;
   }

   public void onDisconnected() {
      this.tabList.reset();
      this.bossOverlay.reset();
      this.minecraft.getToasts().clear();
      this.debugOverlay.reset();
      this.chat.clearMessages(true);
   }

   public BossHealthOverlay getBossOverlay() {
      return this.bossOverlay;
   }

   public DebugScreenOverlay getDebugOverlay() {
      return this.debugOverlay;
   }

   public void clearCache() {
      this.debugOverlay.clearChunkCache();
   }

   private void renderSavingIndicator(GuiGraphics var1) {
      if (this.minecraft.options.showAutosaveIndicator().get() && (this.autosaveIndicatorValue > 0.0F || this.lastAutosaveIndicatorValue > 0.0F)) {
         int var2 = Mth.floor(
            255.0F * Mth.clamp(Mth.lerp(this.minecraft.getFrameTime(), this.lastAutosaveIndicatorValue, this.autosaveIndicatorValue), 0.0F, 1.0F)
         );
         if (var2 > 8) {
            Font var3 = this.getFont();
            int var4 = var3.width(SAVING_TEXT);
            int var5 = 16777215 | var2 << 24 & 0xFF000000;
            var1.drawString(var3, SAVING_TEXT, this.screenWidth - var4 - 10, this.screenHeight - 15, var5);
         }
      }
   }

   static enum HeartType {
      CONTAINER(
         new ResourceLocation("hud/heart/container"),
         new ResourceLocation("hud/heart/container_blinking"),
         new ResourceLocation("hud/heart/container"),
         new ResourceLocation("hud/heart/container_blinking"),
         new ResourceLocation("hud/heart/container_hardcore"),
         new ResourceLocation("hud/heart/container_hardcore_blinking"),
         new ResourceLocation("hud/heart/container_hardcore"),
         new ResourceLocation("hud/heart/container_hardcore_blinking")
      ),
      NORMAL(
         new ResourceLocation("hud/heart/full"),
         new ResourceLocation("hud/heart/full_blinking"),
         new ResourceLocation("hud/heart/half"),
         new ResourceLocation("hud/heart/half_blinking"),
         new ResourceLocation("hud/heart/hardcore_full"),
         new ResourceLocation("hud/heart/hardcore_full_blinking"),
         new ResourceLocation("hud/heart/hardcore_half"),
         new ResourceLocation("hud/heart/hardcore_half_blinking")
      ),
      POISIONED(
         new ResourceLocation("hud/heart/poisoned_full"),
         new ResourceLocation("hud/heart/poisoned_full_blinking"),
         new ResourceLocation("hud/heart/poisoned_half"),
         new ResourceLocation("hud/heart/poisoned_half_blinking"),
         new ResourceLocation("hud/heart/poisoned_hardcore_full"),
         new ResourceLocation("hud/heart/poisoned_hardcore_full_blinking"),
         new ResourceLocation("hud/heart/poisoned_hardcore_half"),
         new ResourceLocation("hud/heart/poisoned_hardcore_half_blinking")
      ),
      WITHERED(
         new ResourceLocation("hud/heart/withered_full"),
         new ResourceLocation("hud/heart/withered_full_blinking"),
         new ResourceLocation("hud/heart/withered_half"),
         new ResourceLocation("hud/heart/withered_half_blinking"),
         new ResourceLocation("hud/heart/withered_hardcore_full"),
         new ResourceLocation("hud/heart/withered_hardcore_full_blinking"),
         new ResourceLocation("hud/heart/withered_hardcore_half"),
         new ResourceLocation("hud/heart/withered_hardcore_half_blinking")
      ),
      ABSORBING(
         new ResourceLocation("hud/heart/absorbing_full"),
         new ResourceLocation("hud/heart/absorbing_full_blinking"),
         new ResourceLocation("hud/heart/absorbing_half"),
         new ResourceLocation("hud/heart/absorbing_half_blinking"),
         new ResourceLocation("hud/heart/absorbing_hardcore_full"),
         new ResourceLocation("hud/heart/absorbing_hardcore_full_blinking"),
         new ResourceLocation("hud/heart/absorbing_hardcore_half"),
         new ResourceLocation("hud/heart/absorbing_hardcore_half_blinking")
      ),
      FROZEN(
         new ResourceLocation("hud/heart/frozen_full"),
         new ResourceLocation("hud/heart/frozen_full_blinking"),
         new ResourceLocation("hud/heart/frozen_half"),
         new ResourceLocation("hud/heart/frozen_half_blinking"),
         new ResourceLocation("hud/heart/frozen_hardcore_full"),
         new ResourceLocation("hud/heart/frozen_hardcore_full_blinking"),
         new ResourceLocation("hud/heart/frozen_hardcore_half"),
         new ResourceLocation("hud/heart/frozen_hardcore_half_blinking")
      );

      private final ResourceLocation full;
      private final ResourceLocation fullBlinking;
      private final ResourceLocation half;
      private final ResourceLocation halfBlinking;
      private final ResourceLocation hardcoreFull;
      private final ResourceLocation hardcoreFullBlinking;
      private final ResourceLocation hardcoreHalf;
      private final ResourceLocation hardcoreHalfBlinking;

      private HeartType(
         ResourceLocation var3,
         ResourceLocation var4,
         ResourceLocation var5,
         ResourceLocation var6,
         ResourceLocation var7,
         ResourceLocation var8,
         ResourceLocation var9,
         ResourceLocation var10
      ) {
         this.full = var3;
         this.fullBlinking = var4;
         this.half = var5;
         this.halfBlinking = var6;
         this.hardcoreFull = var7;
         this.hardcoreFullBlinking = var8;
         this.hardcoreHalf = var9;
         this.hardcoreHalfBlinking = var10;
      }

      public ResourceLocation getSprite(boolean var1, boolean var2, boolean var3) {
         if (!var1) {
            if (var2) {
               return var3 ? this.halfBlinking : this.half;
            } else {
               return var3 ? this.fullBlinking : this.full;
            }
         } else if (var2) {
            return var3 ? this.hardcoreHalfBlinking : this.hardcoreHalf;
         } else {
            return var3 ? this.hardcoreFullBlinking : this.hardcoreFull;
         }
      }

      static Gui.HeartType forPlayer(Player var0) {
         Gui.HeartType var1;
         if (var0.hasEffect(MobEffects.POISON)) {
            var1 = POISIONED;
         } else if (var0.hasEffect(MobEffects.WITHER)) {
            var1 = WITHERED;
         } else if (var0.isFullyFrozen()) {
            var1 = FROZEN;
         } else {
            var1 = NORMAL;
         }

         return var1;
      }
   }
}
