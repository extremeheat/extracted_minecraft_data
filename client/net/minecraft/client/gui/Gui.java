package net.minecraft.client.gui;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.Window;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import net.minecraft.client.gui.contextualbar.LocatorBarRenderer;
import net.minecraft.client.gui.screens.LevelLoadingScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.numbers.NumberFormat;
import net.minecraft.network.chat.numbers.StyledFormat;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringUtil;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.apache.commons.lang3.tuple.Pair;

public class Gui {
   private static final ResourceLocation CROSSHAIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair");
   private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_full");
   private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_background");
   private static final ResourceLocation CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/crosshair_attack_indicator_progress");
   private static final ResourceLocation EFFECT_BACKGROUND_AMBIENT_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background_ambient");
   private static final ResourceLocation EFFECT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/effect_background");
   private static final ResourceLocation HOTBAR_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar");
   private static final ResourceLocation HOTBAR_SELECTION_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_selection");
   private static final ResourceLocation HOTBAR_OFFHAND_LEFT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_left");
   private static final ResourceLocation HOTBAR_OFFHAND_RIGHT_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_offhand_right");
   private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_background");
   private static final ResourceLocation HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE = ResourceLocation.withDefaultNamespace("hud/hotbar_attack_indicator_progress");
   private static final ResourceLocation ARMOR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_empty");
   private static final ResourceLocation ARMOR_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_half");
   private static final ResourceLocation ARMOR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/armor_full");
   private static final ResourceLocation FOOD_EMPTY_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty_hunger");
   private static final ResourceLocation FOOD_HALF_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half_hunger");
   private static final ResourceLocation FOOD_FULL_HUNGER_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full_hunger");
   private static final ResourceLocation FOOD_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_empty");
   private static final ResourceLocation FOOD_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_half");
   private static final ResourceLocation FOOD_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/food_full");
   private static final ResourceLocation AIR_SPRITE = ResourceLocation.withDefaultNamespace("hud/air");
   private static final ResourceLocation AIR_POPPING_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_bursting");
   private static final ResourceLocation AIR_EMPTY_SPRITE = ResourceLocation.withDefaultNamespace("hud/air_empty");
   private static final ResourceLocation HEART_VEHICLE_CONTAINER_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_container");
   private static final ResourceLocation HEART_VEHICLE_FULL_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_full");
   private static final ResourceLocation HEART_VEHICLE_HALF_SPRITE = ResourceLocation.withDefaultNamespace("hud/heart/vehicle_half");
   private static final ResourceLocation VIGNETTE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/vignette.png");
   public static final ResourceLocation NAUSEA_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/nausea.png");
   private static final ResourceLocation SPYGLASS_SCOPE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/spyglass_scope.png");
   private static final ResourceLocation POWDER_SNOW_OUTLINE_LOCATION = ResourceLocation.withDefaultNamespace("textures/misc/powder_snow_outline.png");
   private static final Comparator<PlayerScoreEntry> SCORE_DISPLAY_ORDER;
   private static final Component DEMO_EXPIRED_TEXT;
   private static final Component SAVING_TEXT;
   private static final float MIN_CROSSHAIR_ATTACK_SPEED = 5.0F;
   private static final int EXPERIENCE_BAR_DISPLAY_TICKS = 100;
   private static final int NUM_HEARTS_PER_ROW = 10;
   private static final int LINE_HEIGHT = 10;
   private static final String SPACER = ": ";
   private static final float PORTAL_OVERLAY_ALPHA_MIN = 0.2F;
   private static final int HEART_SIZE = 9;
   private static final int HEART_SEPARATION = 8;
   private static final int NUM_AIR_BUBBLES = 10;
   private static final int AIR_BUBBLE_SIZE = 9;
   private static final int AIR_BUBBLE_SEPERATION = 8;
   private static final int AIR_BUBBLE_POPPING_DURATION = 2;
   private static final int EMPTY_AIR_BUBBLE_DELAY_DURATION = 1;
   private static final float AIR_BUBBLE_POP_SOUND_VOLUME_BASE = 0.5F;
   private static final float AIR_BUBBLE_POP_SOUND_VOLUME_INCREMENT = 0.1F;
   private static final float AIR_BUBBLE_POP_SOUND_PITCH_BASE = 1.0F;
   private static final float AIR_BUBBLE_POP_SOUND_PITCH_INCREMENT = 0.1F;
   private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_VOLUME_INCREASE = 3;
   private static final int NUM_AIR_BUBBLE_POPPED_BEFORE_SOUND_PITCH_INCREASE = 5;
   private static final float AUTOSAVE_FADE_SPEED_FACTOR = 0.2F;
   private static final int SAVING_INDICATOR_WIDTH_PADDING_RIGHT = 5;
   private static final int SAVING_INDICATOR_HEIGHT_PADDING_BOTTOM = 5;
   private final RandomSource random = RandomSource.create();
   private final Minecraft minecraft;
   private final ChatComponent chat;
   private int tickCount;
   @Nullable
   private Component overlayMessageString;
   private int overlayMessageTime;
   private boolean animateOverlayMessageColor;
   private boolean chatDisabledByPlayerShown;
   public float vignetteBrightness = 1.0F;
   private int toolHighlightTimer;
   private ItemStack lastToolHighlight;
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
   private int lastBubblePopSoundPlayed;
   @Nullable
   private Runnable deferredSubtitles;
   private float autosaveIndicatorValue;
   private float lastAutosaveIndicatorValue;
   private Pair<ContextualInfo, ContextualBarRenderer> contextualInfoBar;
   private final Map<ContextualInfo, Supplier<ContextualBarRenderer>> contextualInfoBarRenderers;
   private float scopeScale;

   public Gui(Minecraft var1) {
      super();
      this.lastToolHighlight = ItemStack.EMPTY;
      this.contextualInfoBar = Pair.of(Gui.ContextualInfo.EMPTY, ContextualBarRenderer.EMPTY);
      this.minecraft = var1;
      this.debugOverlay = new DebugScreenOverlay(var1);
      this.spectatorGui = new SpectatorGui(var1);
      this.chat = new ChatComponent(var1);
      this.tabList = new PlayerTabOverlay(var1, this);
      this.bossOverlay = new BossHealthOverlay(var1);
      this.subtitleOverlay = new SubtitleOverlay(var1);
      this.contextualInfoBarRenderers = ImmutableMap.of(Gui.ContextualInfo.EMPTY, (Supplier)() -> ContextualBarRenderer.EMPTY, Gui.ContextualInfo.EXPERIENCE, (Supplier)() -> new ExperienceBarRenderer(var1), Gui.ContextualInfo.LOCATOR, (Supplier)() -> new LocatorBarRenderer(var1), Gui.ContextualInfo.JUMPABLE_VEHICLE, (Supplier)() -> new JumpableVehicleBarRenderer(var1));
      this.resetTitleTimes();
   }

   public void resetTitleTimes() {
      this.titleFadeInTime = 10;
      this.titleStayTime = 70;
      this.titleFadeOutTime = 20;
   }

   public void render(GuiGraphics var1, DeltaTracker var2) {
      if (!(this.minecraft.screen instanceof LevelLoadingScreen)) {
         if (!this.minecraft.options.hideGui) {
            this.renderCameraOverlays(var1, var2);
            this.renderCrosshair(var1, var2);
            var1.nextStratum();
            this.renderHotbarAndDecorations(var1, var2);
            this.renderEffects(var1, var2);
            this.renderBossOverlay(var1, var2);
         }

         this.renderSleepOverlay(var1, var2);
         boolean var3 = this.minecraft.screen instanceof AbstractContainerScreen;
         if (!this.minecraft.options.hideGui) {
            this.renderDemoOverlay(var1, var2);
            this.renderScoreboardSidebar(var1, var2);
            this.renderOverlayMessage(var1, var2);
            this.renderTitle(var1, var2);
            this.renderChat(var1, var2);
            this.renderTabList(var1, var2);
            this.renderSubtitleOverlay(var1, var3 || this.minecraft.screen == null);
         } else if (var3) {
            this.renderSubtitleOverlay(var1, true);
         }

      }
   }

   private void renderBossOverlay(GuiGraphics var1, DeltaTracker var2) {
      this.bossOverlay.render(var1);
   }

   public void renderDebugOverlay(GuiGraphics var1) {
      this.debugOverlay.render(var1);
   }

   private void renderSubtitleOverlay(GuiGraphics var1, boolean var2) {
      if (var2) {
         this.deferredSubtitles = () -> this.subtitleOverlay.render(var1);
      } else {
         this.deferredSubtitles = null;
         this.subtitleOverlay.render(var1);
      }

   }

   public void renderDeferredSubtitles() {
      if (this.deferredSubtitles != null) {
         this.deferredSubtitles.run();
         this.deferredSubtitles = null;
      }

   }

   private void renderCameraOverlays(GuiGraphics var1, DeltaTracker var2) {
      if (Minecraft.useFancyGraphics()) {
         this.renderVignette(var1, this.minecraft.getCameraEntity());
      }

      LocalPlayer var3 = this.minecraft.player;
      float var4 = var2.getGameTimeDeltaTicks();
      this.scopeScale = Mth.lerp(0.5F * var4, this.scopeScale, 1.125F);
      if (this.minecraft.options.getCameraType().isFirstPerson()) {
         if (var3.isScoping()) {
            this.renderSpyglassOverlay(var1, this.scopeScale);
         } else {
            this.scopeScale = 0.5F;

            for(EquipmentSlot var8 : EquipmentSlot.values()) {
               ItemStack var9 = var3.getItemBySlot(var8);
               Equippable var10 = (Equippable)var9.get(DataComponents.EQUIPPABLE);
               if (var10 != null && var10.slot() == var8 && var10.cameraOverlay().isPresent()) {
                  this.renderTextureOverlay(var1, ((ResourceLocation)var10.cameraOverlay().get()).withPath((UnaryOperator)((var0) -> "textures/" + var0 + ".png")), 1.0F);
               }
            }
         }
      }

      if (var3.getTicksFrozen() > 0) {
         this.renderTextureOverlay(var1, POWDER_SNOW_OUTLINE_LOCATION, var3.getPercentFrozen());
      }

      float var11 = var2.getGameTimeDeltaPartialTick(false);
      float var12 = Mth.lerp(var11, var3.oPortalEffectIntensity, var3.portalEffectIntensity);
      float var13 = var3.getEffectBlendFactor(MobEffects.NAUSEA, var11);
      if (var12 > 0.0F) {
         this.renderPortalOverlay(var1, var12);
      } else if (var13 > 0.0F) {
         float var14 = ((Double)this.minecraft.options.screenEffectScale().get()).floatValue();
         if (var14 < 1.0F) {
            float var15 = var13 * (1.0F - var14);
            this.renderConfusionOverlay(var1, var15);
         }
      }

   }

   private void renderSleepOverlay(GuiGraphics var1, DeltaTracker var2) {
      if (this.minecraft.player.getSleepTimer() > 0) {
         Profiler.get().push("sleep");
         var1.nextStratum();
         float var3 = (float)this.minecraft.player.getSleepTimer();
         float var4 = var3 / 100.0F;
         if (var4 > 1.0F) {
            var4 = 1.0F - (var3 - 100.0F) / 10.0F;
         }

         int var5 = (int)(220.0F * var4) << 24 | 1052704;
         var1.fill(0, 0, var1.guiWidth(), var1.guiHeight(), var5);
         Profiler.get().pop();
      }
   }

   private void renderOverlayMessage(GuiGraphics var1, DeltaTracker var2) {
      Font var3 = this.getFont();
      if (this.overlayMessageString != null && this.overlayMessageTime > 0) {
         Profiler.get().push("overlayMessage");
         float var4 = (float)this.overlayMessageTime - var2.getGameTimeDeltaPartialTick(false);
         int var5 = (int)(var4 * 255.0F / 20.0F);
         if (var5 > 255) {
            var5 = 255;
         }

         if (var5 > 0) {
            var1.nextStratum();
            var1.pose().pushMatrix();
            var1.pose().translate((float)(var1.guiWidth() / 2), (float)(var1.guiHeight() - 68));
            int var6;
            if (this.animateOverlayMessageColor) {
               var6 = Mth.hsvToArgb(var4 / 50.0F, 0.7F, 0.6F, var5);
            } else {
               var6 = ARGB.color(var5, -1);
            }

            int var7 = var3.width((FormattedText)this.overlayMessageString);
            var1.drawStringWithBackdrop(var3, this.overlayMessageString, -var7 / 2, -4, var7, var6);
            var1.pose().popMatrix();
         }

         Profiler.get().pop();
      }
   }

   private void renderTitle(GuiGraphics var1, DeltaTracker var2) {
      if (this.title != null && this.titleTime > 0) {
         Font var3 = this.getFont();
         Profiler.get().push("titleAndSubtitle");
         float var4 = (float)this.titleTime - var2.getGameTimeDeltaPartialTick(false);
         int var5 = 255;
         if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
            float var6 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - var4;
            var5 = (int)(var6 * 255.0F / (float)this.titleFadeInTime);
         }

         if (this.titleTime <= this.titleFadeOutTime) {
            var5 = (int)(var4 * 255.0F / (float)this.titleFadeOutTime);
         }

         var5 = Mth.clamp(var5, 0, 255);
         if (var5 > 0) {
            var1.nextStratum();
            var1.pose().pushMatrix();
            var1.pose().translate((float)(var1.guiWidth() / 2), (float)(var1.guiHeight() / 2));
            var1.pose().pushMatrix();
            var1.pose().scale(4.0F, 4.0F);
            int var10 = var3.width((FormattedText)this.title);
            int var7 = ARGB.color(var5, -1);
            var1.drawStringWithBackdrop(var3, this.title, -var10 / 2, -10, var10, var7);
            var1.pose().popMatrix();
            if (this.subtitle != null) {
               var1.pose().pushMatrix();
               var1.pose().scale(2.0F, 2.0F);
               int var8 = var3.width((FormattedText)this.subtitle);
               var1.drawStringWithBackdrop(var3, this.subtitle, -var8 / 2, 5, var8, var7);
               var1.pose().popMatrix();
            }

            var1.pose().popMatrix();
         }

         Profiler.get().pop();
      }
   }

   private void renderChat(GuiGraphics var1, DeltaTracker var2) {
      if (!this.chat.isChatFocused()) {
         Window var3 = this.minecraft.getWindow();
         int var4 = Mth.floor(this.minecraft.mouseHandler.getScaledXPos(var3));
         int var5 = Mth.floor(this.minecraft.mouseHandler.getScaledYPos(var3));
         var1.nextStratum();
         this.chat.render(var1, this.tickCount, var4, var5, false);
      }

   }

   private void renderScoreboardSidebar(GuiGraphics var1, DeltaTracker var2) {
      Scoreboard var3 = this.minecraft.level.getScoreboard();
      Objective var4 = null;
      PlayerTeam var5 = var3.getPlayersTeam(this.minecraft.player.getScoreboardName());
      if (var5 != null) {
         DisplaySlot var6 = DisplaySlot.teamColorToSlot(var5.getColor());
         if (var6 != null) {
            var4 = var3.getDisplayObjective(var6);
         }
      }

      Objective var7 = var4 != null ? var4 : var3.getDisplayObjective(DisplaySlot.SIDEBAR);
      if (var7 != null) {
         var1.nextStratum();
         this.displayScoreboardSidebar(var1, var7);
      }

   }

   private void renderTabList(GuiGraphics var1, DeltaTracker var2) {
      Scoreboard var3 = this.minecraft.level.getScoreboard();
      Objective var4 = var3.getDisplayObjective(DisplaySlot.LIST);
      if (!this.minecraft.options.keyPlayerList.isDown() || this.minecraft.isLocalServer() && this.minecraft.player.connection.getListedOnlinePlayers().size() <= 1 && var4 == null) {
         this.tabList.setVisible(false);
      } else {
         this.tabList.setVisible(true);
         var1.nextStratum();
         this.tabList.render(var1, var1.guiWidth(), var3, var4);
      }

   }

   private void renderCrosshair(GuiGraphics var1, DeltaTracker var2) {
      Options var3 = this.minecraft.options;
      if (var3.getCameraType().isFirstPerson()) {
         if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            if (!this.minecraft.debugEntries.isCurrentlyEnabled(DebugScreenEntries.THREE_DIMENSIONAL_CROSSHAIR)) {
               var1.nextStratum();
               boolean var4 = true;
               var1.blitSprite(RenderPipelines.CROSSHAIR, (ResourceLocation)CROSSHAIR_SPRITE, (var1.guiWidth() - 15) / 2, (var1.guiHeight() - 15) / 2, 15, 15);
               if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.CROSSHAIR) {
                  float var5 = this.minecraft.player.getAttackStrengthScale(0.0F);
                  boolean var6 = false;
                  if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && var5 >= 1.0F) {
                     var6 = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                     var6 &= this.minecraft.crosshairPickEntity.isAlive();
                  }

                  int var7 = var1.guiHeight() / 2 - 7 + 16;
                  int var8 = var1.guiWidth() / 2 - 8;
                  if (var6) {
                     var1.blitSprite(RenderPipelines.CROSSHAIR, (ResourceLocation)CROSSHAIR_ATTACK_INDICATOR_FULL_SPRITE, var8, var7, 16, 16);
                  } else if (var5 < 1.0F) {
                     int var9 = (int)(var5 * 17.0F);
                     var1.blitSprite(RenderPipelines.CROSSHAIR, (ResourceLocation)CROSSHAIR_ATTACK_INDICATOR_BACKGROUND_SPRITE, var8, var7, 16, 4);
                     var1.blitSprite(RenderPipelines.CROSSHAIR, CROSSHAIR_ATTACK_INDICATOR_PROGRESS_SPRITE, 16, 4, 0, 0, var8, var7, var9, 4);
                  }
               }
            }

         }
      }
   }

   private boolean canRenderCrosshairForSpectator(@Nullable HitResult var1) {
      if (var1 == null) {
         return false;
      } else if (var1.getType() == HitResult.Type.ENTITY) {
         return ((EntityHitResult)var1).getEntity() instanceof MenuProvider;
      } else if (var1.getType() == HitResult.Type.BLOCK) {
         BlockPos var2 = ((BlockHitResult)var1).getBlockPos();
         ClientLevel var3 = this.minecraft.level;
         return ((Level)var3).getBlockState(var2).getMenuProvider(var3, var2) != null;
      } else {
         return false;
      }
   }

   private void renderEffects(GuiGraphics var1, DeltaTracker var2) {
      Collection var3 = this.minecraft.player.getActiveEffects();
      if (!var3.isEmpty() && (this.minecraft.screen == null || !this.minecraft.screen.showsActiveEffects())) {
         int var4 = 0;
         int var5 = 0;

         for(MobEffectInstance var7 : Ordering.natural().reverse().sortedCopy(var3)) {
            Holder var8 = var7.getEffect();
            if (var7.showIcon()) {
               int var9 = var1.guiWidth();
               int var10 = 1;
               if (this.minecraft.isDemo()) {
                  var10 += 15;
               }

               if (((MobEffect)var8.value()).isBeneficial()) {
                  ++var4;
                  var9 -= 25 * var4;
               } else {
                  ++var5;
                  var9 -= 25 * var5;
                  var10 += 26;
               }

               float var11 = 1.0F;
               if (var7.isAmbient()) {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)EFFECT_BACKGROUND_AMBIENT_SPRITE, var9, var10, 24, 24);
               } else {
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)EFFECT_BACKGROUND_SPRITE, var9, var10, 24, 24);
                  if (var7.endsWithin(200)) {
                     int var12 = var7.getDuration();
                     int var13 = 10 - var12 / 20;
                     var11 = Mth.clamp((float)var12 / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)var12 * 3.1415927F / 5.0F) * Mth.clamp((float)var13 / 10.0F * 0.25F, 0.0F, 0.25F);
                     var11 = Mth.clamp(var11, 0.0F, 1.0F);
                  }
               }

               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)getMobEffectSprite(var8), var9 + 3, var10 + 3, 18, 18, ARGB.white(var11));
            }
         }

      }
   }

   public static ResourceLocation getMobEffectSprite(Holder<MobEffect> var0) {
      return (ResourceLocation)var0.unwrapKey().map(ResourceKey::location).map((var0x) -> var0x.withPrefix("mob_effect/")).orElseGet(MissingTextureAtlasSprite::getLocation);
   }

   private void renderHotbarAndDecorations(GuiGraphics var1, DeltaTracker var2) {
      if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
         this.spectatorGui.renderHotbar(var1);
      } else {
         this.renderItemHotbar(var1, var2);
      }

      if (this.minecraft.gameMode.canHurtPlayer()) {
         this.renderPlayerHealth(var1);
      }

      this.renderVehicleHealth(var1);
      ContextualInfo var3 = this.nextContextualInfoState();
      if (var3 != this.contextualInfoBar.getKey()) {
         this.contextualInfoBar = Pair.of(var3, (ContextualBarRenderer)((Supplier)this.contextualInfoBarRenderers.get(var3)).get());
      }

      ((ContextualBarRenderer)this.contextualInfoBar.getValue()).renderBackground(var1, var2);
      if (this.minecraft.gameMode.hasExperience() && this.minecraft.player.experienceLevel > 0) {
         ContextualBarRenderer.renderExperienceLevel(var1, this.minecraft.font, this.minecraft.player.experienceLevel);
      }

      ((ContextualBarRenderer)this.contextualInfoBar.getValue()).render(var1, var2);
      if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
         this.renderSelectedItemName(var1);
      } else if (this.minecraft.player.isSpectator()) {
         this.spectatorGui.renderAction(var1);
      }

   }

   private void renderItemHotbar(GuiGraphics var1, DeltaTracker var2) {
      Player var3 = this.getCameraPlayer();
      if (var3 != null) {
         ItemStack var4 = var3.getOffhandItem();
         HumanoidArm var5 = var3.getMainArm().getOpposite();
         int var6 = var1.guiWidth() / 2;
         boolean var7 = true;
         boolean var8 = true;
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HOTBAR_SPRITE, var6 - 91, var1.guiHeight() - 22, 182, 22);
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HOTBAR_SELECTION_SPRITE, var6 - 91 - 1 + var3.getInventory().getSelectedSlot() * 20, var1.guiHeight() - 22 - 1, 24, 23);
         if (!var4.isEmpty()) {
            if (var5 == HumanoidArm.LEFT) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HOTBAR_OFFHAND_LEFT_SPRITE, var6 - 91 - 29, var1.guiHeight() - 23, 29, 24);
            } else {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HOTBAR_OFFHAND_RIGHT_SPRITE, var6 + 91, var1.guiHeight() - 23, 29, 24);
            }
         }

         int var9 = 1;

         for(int var10 = 0; var10 < 9; ++var10) {
            int var11 = var6 - 90 + var10 * 20 + 2;
            int var12 = var1.guiHeight() - 16 - 3;
            this.renderSlot(var1, var11, var12, var2, var3, var3.getInventory().getItem(var10), var9++);
         }

         if (!var4.isEmpty()) {
            int var16 = var1.guiHeight() - 16 - 3;
            if (var5 == HumanoidArm.LEFT) {
               this.renderSlot(var1, var6 - 91 - 26, var16, var2, var3, var4, var9++);
            } else {
               this.renderSlot(var1, var6 + 91 + 10, var16, var2, var3, var4, var9++);
            }
         }

         if (this.minecraft.options.attackIndicator().get() == AttackIndicatorStatus.HOTBAR) {
            float var17 = this.minecraft.player.getAttackStrengthScale(0.0F);
            if (var17 < 1.0F) {
               int var18 = var1.guiHeight() - 20;
               int var19 = var6 + 91 + 6;
               if (var5 == HumanoidArm.RIGHT) {
                  var19 = var6 - 91 - 22;
               }

               int var13 = (int)(var17 * 19.0F);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HOTBAR_ATTACK_INDICATOR_BACKGROUND_SPRITE, var19, var18, 18, 18);
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, HOTBAR_ATTACK_INDICATOR_PROGRESS_SPRITE, 18, 18, 0, 18 - var13, var19, var18 + 18 - var13, 18, var13);
            }
         }

      }
   }

   private void renderSelectedItemName(GuiGraphics var1) {
      Profiler.get().push("selectedItemName");
      if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
         MutableComponent var2 = Component.empty().append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color());
         if (this.lastToolHighlight.has(DataComponents.CUSTOM_NAME)) {
            var2.withStyle(ChatFormatting.ITALIC);
         }

         int var3 = this.getFont().width((FormattedText)var2);
         int var4 = (var1.guiWidth() - var3) / 2;
         int var5 = var1.guiHeight() - 59;
         if (!this.minecraft.gameMode.canHurtPlayer()) {
            var5 += 14;
         }

         int var6 = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
         if (var6 > 255) {
            var6 = 255;
         }

         if (var6 > 0) {
            var1.drawStringWithBackdrop(this.getFont(), var2, var4, var5, var3, ARGB.color(var6, -1));
         }
      }

      Profiler.get().pop();
   }

   private void renderDemoOverlay(GuiGraphics var1, DeltaTracker var2) {
      if (this.minecraft.isDemo()) {
         Profiler.get().push("demo");
         var1.nextStratum();
         Object var3;
         if (this.minecraft.level.getGameTime() >= 120500L) {
            var3 = DEMO_EXPIRED_TEXT;
         } else {
            var3 = Component.translatable("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime()), this.minecraft.level.tickRateManager().tickrate()));
         }

         int var4 = this.getFont().width((FormattedText)var3);
         int var5 = var1.guiWidth() - var4 - 10;
         boolean var6 = true;
         var1.drawStringWithBackdrop(this.getFont(), (Component)var3, var5, 5, var4, -1);
         Profiler.get().pop();
      }
   }

   private void displayScoreboardSidebar(GuiGraphics var1, Objective var2) {
      Scoreboard var3 = var2.getScoreboard();
      NumberFormat var4 = var2.numberFormatOrDefault(StyledFormat.SIDEBAR_DEFAULT);
      1DisplayEntry[] var5 = (1DisplayEntry[])var3.listPlayerScores(var2).stream().filter((var0) -> !var0.isHidden()).sorted(SCORE_DISPLAY_ORDER).limit(15L).map((var3x) -> {
         PlayerTeam var4x = var3.getPlayersTeam(var3x.owner());
         Component var5 = var3x.ownerName();
         MutableComponent var6 = PlayerTeam.formatNameForTeam(var4x, var5);
         MutableComponent var7 = var3x.formatValue(var4);
         int var8 = this.getFont().width((FormattedText)var7);

         record 1DisplayEntry(Component name, Component score, int scoreWidth) {
            final Component name;
            final Component score;
            final int scoreWidth;

            _DisplayEntry/* $FF was: 1DisplayEntry*/(Component var1, Component var2, int var3) {
               super();
               this.name = var1;
               this.score = var2;
               this.scoreWidth = var3;
            }
         }

         return new 1DisplayEntry(var6, var7, var8);
      }).toArray((var0) -> new 1DisplayEntry[var0]);
      Component var6 = var2.getDisplayName();
      int var7 = this.getFont().width((FormattedText)var6);
      int var8 = var7;
      int var9 = this.getFont().width(": ");

      for(1DisplayEntry var13 : var5) {
         var8 = Math.max(var8, this.getFont().width((FormattedText)var13.name) + (var13.scoreWidth > 0 ? var9 + var13.scoreWidth : 0));
      }

      int var23 = var5.length;
      Objects.requireNonNull(this.getFont());
      int var24 = var23 * 9;
      int var25 = var1.guiHeight() / 2 + var24 / 3;
      boolean var14 = true;
      int var15 = var1.guiWidth() - var8 - 3;
      int var16 = var1.guiWidth() - 3 + 2;
      int var17 = this.minecraft.options.getBackgroundColor(0.3F);
      int var18 = this.minecraft.options.getBackgroundColor(0.4F);
      Objects.requireNonNull(this.getFont());
      int var19 = var25 - var23 * 9;
      int var10001 = var15 - 2;
      Objects.requireNonNull(this.getFont());
      var1.fill(var10001, var19 - 9 - 1, var16, var19 - 1, var18);
      var1.fill(var15 - 2, var19 - 1, var16, var25, var17);
      Font var26 = this.getFont();
      int var10003 = var15 + var8 / 2 - var7 / 2;
      Objects.requireNonNull(this.getFont());
      var1.drawString(var26, (Component)var6, var10003, var19 - 9, -1, false);

      for(int var20 = 0; var20 < var23; ++var20) {
         1DisplayEntry var21 = var5[var20];
         int var27 = var23 - var20;
         Objects.requireNonNull(this.getFont());
         int var22 = var25 - var27 * 9;
         var1.drawString(this.getFont(), (Component)var21.name, var15, var22, -1, false);
         var1.drawString(this.getFont(), (Component)var21.score, var16 - var21.scoreWidth, var22, -1, false);
      }

   }

   @Nullable
   private Player getCameraPlayer() {
      Entity var2 = this.minecraft.getCameraEntity();
      Player var10000;
      if (var2 instanceof Player var1) {
         var10000 = var1;
      } else {
         var10000 = null;
      }

      return var10000;
   }

   @Nullable
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

   private int getVehicleMaxHearts(@Nullable LivingEntity var1) {
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
            this.displayHealth = var3;
            this.lastHealthTime = var5;
         }

         this.lastHealth = var3;
         int var7 = this.displayHealth;
         this.random.setSeed((long)(this.tickCount * 312871));
         int var8 = var1.guiWidth() / 2 - 91;
         int var9 = var1.guiWidth() / 2 + 91;
         int var10 = var1.guiHeight() - 39;
         float var11 = Math.max((float)var2.getAttributeValue(Attributes.MAX_HEALTH), (float)Math.max(var7, var3));
         int var12 = Mth.ceil(var2.getAbsorptionAmount());
         int var13 = Mth.ceil((var11 + (float)var12) / 2.0F / 10.0F);
         int var14 = Math.max(10 - (var13 - 2), 3);
         int var15 = var10 - 10;
         int var16 = -1;
         if (var2.hasEffect(MobEffects.REGENERATION)) {
            var16 = this.tickCount % Mth.ceil(var11 + 5.0F);
         }

         Profiler.get().push("armor");
         renderArmor(var1, var2, var10, var13, var14, var8);
         Profiler.get().popPush("health");
         this.renderHearts(var1, var2, var8, var10, var14, var16, var11, var3, var7, var12, var4);
         LivingEntity var17 = this.getPlayerVehicleWithHealth();
         int var18 = this.getVehicleMaxHearts(var17);
         if (var18 == 0) {
            Profiler.get().popPush("food");
            this.renderFood(var1, var2, var10, var9);
            var15 -= 10;
         }

         Profiler.get().popPush("air");
         this.renderAirBubbles(var1, var2, var18, var15, var9);
         Profiler.get().pop();
      }
   }

   private static void renderArmor(GuiGraphics var0, Player var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.getArmorValue();
      if (var6 > 0) {
         int var7 = var2 - (var3 - 1) * var4 - 10;

         for(int var8 = 0; var8 < 10; ++var8) {
            int var9 = var5 + var8 * 8;
            if (var8 * 2 + 1 < var6) {
               var0.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ARMOR_FULL_SPRITE, var9, var7, 9, 9);
            }

            if (var8 * 2 + 1 == var6) {
               var0.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ARMOR_HALF_SPRITE, var9, var7, 9, 9);
            }

            if (var8 * 2 + 1 > var6) {
               var0.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)ARMOR_EMPTY_SPRITE, var9, var7, 9, 9);
            }
         }

      }
   }

   private void renderHearts(GuiGraphics var1, Player var2, int var3, int var4, int var5, int var6, float var7, int var8, int var9, int var10, boolean var11) {
      HeartType var12 = Gui.HeartType.forPlayer(var2);
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

   private void renderHeart(GuiGraphics var1, HeartType var2, int var3, int var4, boolean var5, boolean var6, boolean var7) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var2.getSprite(var5, var7, var6), var3, var4, 9, 9);
   }

   private void renderAirBubbles(GuiGraphics var1, Player var2, int var3, int var4, int var5) {
      int var6 = var2.getMaxAirSupply();
      int var7 = Math.clamp((long)var2.getAirSupply(), 0, var6);
      boolean var8 = var2.isEyeInFluid(FluidTags.WATER);
      if (var8 || var7 < var6) {
         var4 = this.getAirBubbleYLine(var3, var4);
         int var9 = getCurrentAirSupplyBubble(var7, var6, -2);
         int var10 = getCurrentAirSupplyBubble(var7, var6, 0);
         int var11 = 10 - getCurrentAirSupplyBubble(var7, var6, getEmptyBubbleDelayDuration(var7, var8));
         boolean var12 = var9 != var10;
         if (!var8) {
            this.lastBubblePopSoundPlayed = 0;
         }

         for(int var13 = 1; var13 <= 10; ++var13) {
            int var14 = var5 - (var13 - 1) * 8 - 9;
            if (var13 <= var9) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)AIR_SPRITE, var14, var4, 9, 9);
            } else if (var12 && var13 == var10 && var8) {
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)AIR_POPPING_SPRITE, var14, var4, 9, 9);
               this.playAirBubblePoppedSound(var13, var2, var11);
            } else if (var13 > 10 - var11) {
               int var15 = var11 == 10 && this.tickCount % 2 == 0 ? this.random.nextInt(2) : 0;
               var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)AIR_EMPTY_SPRITE, var14, var4 + var15, 9, 9);
            }
         }
      }

   }

   private int getAirBubbleYLine(int var1, int var2) {
      int var3 = this.getVisibleVehicleHeartRows(var1) - 1;
      var2 -= var3 * 10;
      return var2;
   }

   private static int getCurrentAirSupplyBubble(int var0, int var1, int var2) {
      return Mth.ceil((float)((var0 + var2) * 10) / (float)var1);
   }

   private static int getEmptyBubbleDelayDuration(int var0, boolean var1) {
      return var0 != 0 && var1 ? 1 : 0;
   }

   private void playAirBubblePoppedSound(int var1, Player var2, int var3) {
      if (this.lastBubblePopSoundPlayed != var1) {
         float var4 = 0.5F + 0.1F * (float)Math.max(0, var3 - 3 + 1);
         float var5 = 1.0F + 0.1F * (float)Math.max(0, var3 - 5 + 1);
         var2.playSound(SoundEvents.BUBBLE_POP, var4, var5);
         this.lastBubblePopSoundPlayed = var1;
      }

   }

   private void renderFood(GuiGraphics var1, Player var2, int var3, int var4) {
      FoodData var5 = var2.getFoodData();
      int var6 = var5.getFoodLevel();

      for(int var7 = 0; var7 < 10; ++var7) {
         int var8 = var3;
         ResourceLocation var9;
         ResourceLocation var10;
         ResourceLocation var11;
         if (var2.hasEffect(MobEffects.HUNGER)) {
            var9 = FOOD_EMPTY_HUNGER_SPRITE;
            var10 = FOOD_HALF_HUNGER_SPRITE;
            var11 = FOOD_FULL_HUNGER_SPRITE;
         } else {
            var9 = FOOD_EMPTY_SPRITE;
            var10 = FOOD_HALF_SPRITE;
            var11 = FOOD_FULL_SPRITE;
         }

         if (var2.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (var6 * 3 + 1) == 0) {
            var8 = var3 + (this.random.nextInt(3) - 1);
         }

         int var12 = var4 - var7 * 8 - 9;
         var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var9, var12, var8, 9, 9);
         if (var7 * 2 + 1 < var6) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var11, var12, var8, 9, 9);
         }

         if (var7 * 2 + 1 == var6) {
            var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)var10, var12, var8, 9, 9);
         }
      }

   }

   private void renderVehicleHealth(GuiGraphics var1) {
      LivingEntity var2 = this.getPlayerVehicleWithHealth();
      if (var2 != null) {
         int var3 = this.getVehicleMaxHearts(var2);
         if (var3 != 0) {
            int var4 = (int)Math.ceil((double)var2.getHealth());
            Profiler.get().popPush("mountHealth");
            int var5 = var1.guiHeight() - 39;
            int var6 = var1.guiWidth() / 2 + 91;
            int var7 = var5;

            for(int var8 = 0; var3 > 0; var8 += 20) {
               int var9 = Math.min(var3, 10);
               var3 -= var9;

               for(int var10 = 0; var10 < var9; ++var10) {
                  int var11 = var6 - var10 * 8 - 9;
                  var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HEART_VEHICLE_CONTAINER_SPRITE, var11, var7, 9, 9);
                  if (var10 * 2 + 1 + var8 < var4) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HEART_VEHICLE_FULL_SPRITE, var11, var7, 9, 9);
                  }

                  if (var10 * 2 + 1 + var8 == var4) {
                     var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)HEART_VEHICLE_HALF_SPRITE, var11, var7, 9, 9);
                  }
               }

               var7 -= 10;
            }

         }
      }
   }

   private void renderTextureOverlay(GuiGraphics var1, ResourceLocation var2, float var3) {
      int var4 = ARGB.white(var3);
      var1.blit(RenderPipelines.GUI_TEXTURED, var2, 0, 0, 0.0F, 0.0F, var1.guiWidth(), var1.guiHeight(), var1.guiWidth(), var1.guiHeight(), var4);
   }

   private void renderSpyglassOverlay(GuiGraphics var1, float var2) {
      float var3 = (float)Math.min(var1.guiWidth(), var1.guiHeight());
      float var5 = Math.min((float)var1.guiWidth() / var3, (float)var1.guiHeight() / var3) * var2;
      int var6 = Mth.floor(var3 * var5);
      int var7 = Mth.floor(var3 * var5);
      int var8 = (var1.guiWidth() - var6) / 2;
      int var9 = (var1.guiHeight() - var7) / 2;
      int var10 = var8 + var6;
      int var11 = var9 + var7;
      var1.blit(RenderPipelines.GUI_TEXTURED, SPYGLASS_SCOPE_LOCATION, var8, var9, 0.0F, 0.0F, var6, var7, var6, var7);
      var1.fill(RenderPipelines.GUI, 0, var11, var1.guiWidth(), var1.guiHeight(), -16777216);
      var1.fill(RenderPipelines.GUI, 0, 0, var1.guiWidth(), var9, -16777216);
      var1.fill(RenderPipelines.GUI, 0, var9, var8, var11, -16777216);
      var1.fill(RenderPipelines.GUI, var10, var9, var1.guiWidth(), var11, -16777216);
   }

   private void updateVignetteBrightness(Entity var1) {
      BlockPos var2 = BlockPos.containing(var1.getX(), var1.getEyeY(), var1.getZ());
      float var3 = LightTexture.getBrightness(var1.level().dimensionType(), var1.level().getMaxLocalRawBrightness(var2));
      float var4 = Mth.clamp(1.0F - var3, 0.0F, 1.0F);
      this.vignetteBrightness += (var4 - this.vignetteBrightness) * 0.01F;
   }

   private void renderVignette(GuiGraphics var1, @Nullable Entity var2) {
      WorldBorder var3 = this.minecraft.level.getWorldBorder();
      float var4 = 0.0F;
      if (var2 != null) {
         float var5 = (float)var3.getDistanceToBorder(var2);
         double var6 = Math.min(var3.getLerpSpeed() * (double)var3.getWarningTime() * 1000.0, Math.abs(var3.getLerpTarget() - var3.getSize()));
         double var8 = Math.max((double)var3.getWarningBlocks(), var6);
         if ((double)var5 < var8) {
            var4 = 1.0F - (float)((double)var5 / var8);
         }
      }

      int var11;
      if (var4 > 0.0F) {
         var4 = Mth.clamp(var4, 0.0F, 1.0F);
         var11 = ARGB.colorFromFloat(1.0F, 0.0F, var4, var4);
      } else {
         float var12 = this.vignetteBrightness;
         var12 = Mth.clamp(var12, 0.0F, 1.0F);
         var11 = ARGB.colorFromFloat(1.0F, var12, var12, var12);
      }

      var1.blit(RenderPipelines.VIGNETTE, VIGNETTE_LOCATION, 0, 0, 0.0F, 0.0F, var1.guiWidth(), var1.guiHeight(), var1.guiWidth(), var1.guiHeight(), var11);
   }

   private void renderPortalOverlay(GuiGraphics var1, float var2) {
      if (var2 < 1.0F) {
         var2 *= var2;
         var2 *= var2;
         var2 = var2 * 0.8F + 0.2F;
      }

      int var3 = ARGB.white(var2);
      TextureAtlasSprite var4 = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (TextureAtlasSprite)var4, 0, 0, var1.guiWidth(), var1.guiHeight(), var3);
   }

   private void renderConfusionOverlay(GuiGraphics var1, float var2) {
      int var3 = var1.guiWidth();
      int var4 = var1.guiHeight();
      var1.pose().pushMatrix();
      float var5 = Mth.lerp(var2, 2.0F, 1.0F);
      var1.pose().translate((float)var3 / 2.0F, (float)var4 / 2.0F);
      var1.pose().scale(var5, var5);
      var1.pose().translate((float)(-var3) / 2.0F, (float)(-var4) / 2.0F);
      float var6 = 0.2F * var2;
      float var7 = 0.4F * var2;
      float var8 = 0.2F * var2;
      var1.blit(RenderPipelines.GUI_NAUSEA_OVERLAY, NAUSEA_LOCATION, 0, 0, 0.0F, 0.0F, var3, var4, var3, var4, ARGB.colorFromFloat(1.0F, var6, var7, var8));
      var1.pose().popMatrix();
   }

   private void renderSlot(GuiGraphics var1, int var2, int var3, DeltaTracker var4, Player var5, ItemStack var6, int var7) {
      if (!var6.isEmpty()) {
         float var8 = (float)var6.getPopTime() - var4.getGameTimeDeltaPartialTick(false);
         if (var8 > 0.0F) {
            float var9 = 1.0F + var8 / 5.0F;
            var1.pose().pushMatrix();
            var1.pose().translate((float)(var2 + 8), (float)(var3 + 12));
            var1.pose().scale(1.0F / var9, (var9 + 1.0F) / 2.0F);
            var1.pose().translate((float)(-(var2 + 8)), (float)(-(var3 + 12)));
         }

         var1.renderItem(var5, var6, var2, var3, var7);
         if (var8 > 0.0F) {
            var1.pose().popMatrix();
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
         ItemStack var2 = this.minecraft.player.getInventory().getSelectedItem();
         if (var2.isEmpty()) {
            this.toolHighlightTimer = 0;
         } else if (!this.lastToolHighlight.isEmpty() && var2.is(this.lastToolHighlight.getItem()) && var2.getHoverName().equals(this.lastToolHighlight.getHoverName())) {
            if (this.toolHighlightTimer > 0) {
               --this.toolHighlightTimer;
            }
         } else {
            this.toolHighlightTimer = (int)(40.0 * (Double)this.minecraft.options.notificationDisplayTime().get());
         }

         this.lastToolHighlight = var2;
      }

      this.chat.tick();
   }

   private void tickAutosaveIndicator() {
      IntegratedServer var1 = this.minecraft.getSingleplayerServer();
      boolean var2 = var1 != null && ((MinecraftServer)var1).isCurrentlySaving();
      this.lastAutosaveIndicatorValue = this.autosaveIndicatorValue;
      this.autosaveIndicatorValue = Mth.lerp(0.2F, this.autosaveIndicatorValue, var2 ? 1.0F : 0.0F);
   }

   public void setNowPlaying(Component var1) {
      MutableComponent var2 = Component.translatable("record.nowPlaying", var1);
      this.setOverlayMessage(var2, true);
      this.minecraft.getNarrator().saySystemNow((Component)var2);
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

   public void clearTitles() {
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
      this.minecraft.getToastManager().clear();
      this.debugOverlay.reset();
      this.chat.clearMessages(true);
      this.clearTitles();
      this.resetTitleTimes();
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

   public void renderSavingIndicator(GuiGraphics var1, DeltaTracker var2) {
      if ((Boolean)this.minecraft.options.showAutosaveIndicator().get() && (this.autosaveIndicatorValue > 0.0F || this.lastAutosaveIndicatorValue > 0.0F)) {
         int var3 = Mth.floor(255.0F * Mth.clamp(Mth.lerp(var2.getRealtimeDeltaTicks(), this.lastAutosaveIndicatorValue, this.autosaveIndicatorValue), 0.0F, 1.0F));
         if (var3 > 0) {
            Font var4 = this.getFont();
            int var5 = var4.width((FormattedText)SAVING_TEXT);
            int var6 = ARGB.color(var3, -1);
            int var7 = var1.guiWidth() - var5 - 5;
            int var10000 = var1.guiHeight();
            Objects.requireNonNull(var4);
            int var8 = var10000 - 9 - 5;
            var1.nextStratum();
            var1.drawStringWithBackdrop(var4, SAVING_TEXT, var7, var8, var5, var6);
         }
      }

   }

   private boolean willPrioritizeExperienceInfo() {
      return this.minecraft.player.experienceDisplayStartTick + 100 > this.minecraft.player.tickCount;
   }

   private boolean willPrioritizeJumpInfo() {
      return this.minecraft.player.getJumpRidingScale() > 0.0F || (Integer)Optionull.mapOrDefault(this.minecraft.player.jumpableVehicle(), PlayerRideableJumping::getJumpCooldown, 0) > 0;
   }

   private ContextualInfo nextContextualInfoState() {
      boolean var1 = this.minecraft.player.connection.getWaypointManager().hasWaypoints();
      boolean var2 = this.minecraft.player.jumpableVehicle() != null;
      boolean var3 = this.minecraft.gameMode.hasExperience();
      if (var1) {
         if (var2 && this.willPrioritizeJumpInfo()) {
            return Gui.ContextualInfo.JUMPABLE_VEHICLE;
         } else {
            return var3 && this.willPrioritizeExperienceInfo() ? Gui.ContextualInfo.EXPERIENCE : Gui.ContextualInfo.LOCATOR;
         }
      } else if (var2) {
         return Gui.ContextualInfo.JUMPABLE_VEHICLE;
      } else {
         return var3 ? Gui.ContextualInfo.EXPERIENCE : Gui.ContextualInfo.EMPTY;
      }
   }

   static {
      SCORE_DISPLAY_ORDER = Comparator.comparing(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER);
      DEMO_EXPIRED_TEXT = Component.translatable("demo.demoExpired");
      SAVING_TEXT = Component.translatable("menu.savingLevel");
   }

   static enum HeartType {
      CONTAINER(ResourceLocation.withDefaultNamespace("hud/heart/container"), ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/container"), ResourceLocation.withDefaultNamespace("hud/heart/container_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore"), ResourceLocation.withDefaultNamespace("hud/heart/container_hardcore_blinking")),
      NORMAL(ResourceLocation.withDefaultNamespace("hud/heart/full"), ResourceLocation.withDefaultNamespace("hud/heart/full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/half"), ResourceLocation.withDefaultNamespace("hud/heart/half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/hardcore_half_blinking")),
      POISIONED(ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_half"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/poisoned_hardcore_half_blinking")),
      WITHERED(ResourceLocation.withDefaultNamespace("hud/heart/withered_full"), ResourceLocation.withDefaultNamespace("hud/heart/withered_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/withered_half"), ResourceLocation.withDefaultNamespace("hud/heart/withered_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/withered_hardcore_half_blinking")),
      ABSORBING(ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/absorbing_hardcore_half_blinking")),
      FROZEN(ResourceLocation.withDefaultNamespace("hud/heart/frozen_full"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_half"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_half_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_full_blinking"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_half"), ResourceLocation.withDefaultNamespace("hud/heart/frozen_hardcore_half_blinking"));

      private final ResourceLocation full;
      private final ResourceLocation fullBlinking;
      private final ResourceLocation half;
      private final ResourceLocation halfBlinking;
      private final ResourceLocation hardcoreFull;
      private final ResourceLocation hardcoreFullBlinking;
      private final ResourceLocation hardcoreHalf;
      private final ResourceLocation hardcoreHalfBlinking;

      private HeartType(final ResourceLocation var3, final ResourceLocation var4, final ResourceLocation var5, final ResourceLocation var6, final ResourceLocation var7, final ResourceLocation var8, final ResourceLocation var9, final ResourceLocation var10) {
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

      static HeartType forPlayer(Player var0) {
         HeartType var1;
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

      // $FF: synthetic method
      private static HeartType[] $values() {
         return new HeartType[]{CONTAINER, NORMAL, POISIONED, WITHERED, ABSORBING, FROZEN};
      }
   }

   static enum ContextualInfo {
      EMPTY,
      EXPERIENCE,
      LOCATOR,
      JUMPABLE_VEHICLE;

      private ContextualInfo() {
      }

      // $FF: synthetic method
      private static ContextualInfo[] $values() {
         return new ContextualInfo[]{EMPTY, EXPERIENCE, LOCATOR, JUMPABLE_VEHICLE};
      }
   }

   public interface RenderFunction {
      void render(GuiGraphics var1, DeltaTracker var2);
   }
}
