package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.chat.ChatListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.chat.OverlayChatListener;
import net.minecraft.client.gui.chat.StandardChatListener;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.PlayerTabOverlay;
import net.minecraft.client.gui.components.SubtitleOverlay;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.MobEffectTextureManager;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringUtil;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.SharedMonsterAttributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class Gui extends GuiComponent {
   private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("textures/misc/vignette.png");
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private static final ResourceLocation PUMPKIN_BLUR_LOCATION = new ResourceLocation("textures/misc/pumpkinblur.png");
   private final Random random = new Random();
   private final Minecraft minecraft;
   private final ItemRenderer itemRenderer;
   private final ChatComponent chat;
   private int tickCount;
   private String overlayMessageString = "";
   private int overlayMessageTime;
   private boolean animateOverlayMessageColor;
   public float vignetteBrightness = 1.0F;
   private int toolHighlightTimer;
   private ItemStack lastToolHighlight;
   private final DebugScreenOverlay debugScreen;
   private final SubtitleOverlay subtitleOverlay;
   private final SpectatorGui spectatorGui;
   private final PlayerTabOverlay tabList;
   private final BossHealthOverlay bossOverlay;
   private int titleTime;
   private String title;
   private String subtitle;
   private int titleFadeInTime;
   private int titleStayTime;
   private int titleFadeOutTime;
   private int lastHealth;
   private int displayHealth;
   private long lastHealthTime;
   private long healthBlinkTime;
   private int screenWidth;
   private int screenHeight;
   private final Map<ChatType, List<ChatListener>> chatListeners;

   public Gui(Minecraft var1) {
      super();
      this.lastToolHighlight = ItemStack.EMPTY;
      this.title = "";
      this.subtitle = "";
      this.chatListeners = Maps.newHashMap();
      this.minecraft = var1;
      this.itemRenderer = var1.getItemRenderer();
      this.debugScreen = new DebugScreenOverlay(var1);
      this.spectatorGui = new SpectatorGui(var1);
      this.chat = new ChatComponent(var1);
      this.tabList = new PlayerTabOverlay(var1, this);
      this.bossOverlay = new BossHealthOverlay(var1);
      this.subtitleOverlay = new SubtitleOverlay(var1);
      ChatType[] var2 = ChatType.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChatType var5 = var2[var4];
         this.chatListeners.put(var5, Lists.newArrayList());
      }

      NarratorChatListener var6 = NarratorChatListener.INSTANCE;
      ((List)this.chatListeners.get(ChatType.CHAT)).add(new StandardChatListener(var1));
      ((List)this.chatListeners.get(ChatType.CHAT)).add(var6);
      ((List)this.chatListeners.get(ChatType.SYSTEM)).add(new StandardChatListener(var1));
      ((List)this.chatListeners.get(ChatType.SYSTEM)).add(var6);
      ((List)this.chatListeners.get(ChatType.GAME_INFO)).add(new OverlayChatListener(var1));
      this.resetTitleTimes();
   }

   public void resetTitleTimes() {
      this.titleFadeInTime = 10;
      this.titleStayTime = 70;
      this.titleFadeOutTime = 20;
   }

   public void render(float var1) {
      this.screenWidth = this.minecraft.window.getGuiScaledWidth();
      this.screenHeight = this.minecraft.window.getGuiScaledHeight();
      Font var2 = this.getFont();
      GlStateManager.enableBlend();
      if (Minecraft.useFancyGraphics()) {
         this.renderVignette(this.minecraft.getCameraEntity());
      } else {
         GlStateManager.enableDepthTest();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      }

      ItemStack var3 = this.minecraft.player.inventory.getArmor(3);
      if (this.minecraft.options.thirdPersonView == 0 && var3.getItem() == Blocks.CARVED_PUMPKIN.asItem()) {
         this.renderPumpkin();
      }

      float var4;
      if (!this.minecraft.player.hasEffect(MobEffects.CONFUSION)) {
         var4 = Mth.lerp(var1, this.minecraft.player.oPortalTime, this.minecraft.player.portalTime);
         if (var4 > 0.0F) {
            this.renderPortalOverlay(var4);
         }
      }

      if (this.minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
         this.spectatorGui.renderHotbar(var1);
      } else if (!this.minecraft.options.hideGui) {
         this.renderHotbar(var1);
      }

      if (!this.minecraft.options.hideGui) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         GlStateManager.enableBlend();
         GlStateManager.enableAlphaTest();
         this.renderCrosshair();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         this.minecraft.getProfiler().push("bossHealth");
         this.bossOverlay.render();
         this.minecraft.getProfiler().pop();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(GUI_ICONS_LOCATION);
         if (this.minecraft.gameMode.canHurtPlayer()) {
            this.renderPlayerHealth();
         }

         this.renderVehicleHealth();
         GlStateManager.disableBlend();
         int var9 = this.screenWidth / 2 - 91;
         if (this.minecraft.player.isRidingJumpable()) {
            this.renderJumpMeter(var9);
         } else if (this.minecraft.gameMode.hasExperience()) {
            this.renderExperienceBar(var9);
         }

         if (this.minecraft.options.heldItemTooltips && this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR) {
            this.renderSelectedItemName();
         } else if (this.minecraft.player.isSpectator()) {
            this.spectatorGui.renderTooltip();
         }
      }

      int var6;
      if (this.minecraft.player.getSleepTimer() > 0) {
         this.minecraft.getProfiler().push("sleep");
         GlStateManager.disableDepthTest();
         GlStateManager.disableAlphaTest();
         var4 = (float)this.minecraft.player.getSleepTimer();
         float var5 = var4 / 100.0F;
         if (var5 > 1.0F) {
            var5 = 1.0F - (var4 - 100.0F) / 10.0F;
         }

         var6 = (int)(220.0F * var5) << 24 | 1052704;
         fill(0, 0, this.screenWidth, this.screenHeight, var6);
         GlStateManager.enableAlphaTest();
         GlStateManager.enableDepthTest();
         this.minecraft.getProfiler().pop();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if (this.minecraft.isDemo()) {
         this.renderDemoOverlay();
      }

      this.renderEffects();
      if (this.minecraft.options.renderDebug) {
         this.debugScreen.render();
      }

      if (!this.minecraft.options.hideGui) {
         int var7;
         int var11;
         if (this.overlayMessageTime > 0) {
            this.minecraft.getProfiler().push("overlayMessage");
            var4 = (float)this.overlayMessageTime - var1;
            var11 = (int)(var4 * 255.0F / 20.0F);
            if (var11 > 255) {
               var11 = 255;
            }

            if (var11 > 8) {
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight - 68), 0.0F);
               GlStateManager.enableBlend();
               GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               var6 = 16777215;
               if (this.animateOverlayMessageColor) {
                  var6 = Mth.hsvToRgb(var4 / 50.0F, 0.7F, 0.6F) & 16777215;
               }

               var7 = var11 << 24 & -16777216;
               this.drawBackdrop(var2, -4, var2.width(this.overlayMessageString));
               var2.draw(this.overlayMessageString, (float)(-var2.width(this.overlayMessageString) / 2), -4.0F, var6 | var7);
               GlStateManager.disableBlend();
               GlStateManager.popMatrix();
            }

            this.minecraft.getProfiler().pop();
         }

         if (this.titleTime > 0) {
            this.minecraft.getProfiler().push("titleAndSubtitle");
            var4 = (float)this.titleTime - var1;
            var11 = 255;
            if (this.titleTime > this.titleFadeOutTime + this.titleStayTime) {
               float var12 = (float)(this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime) - var4;
               var11 = (int)(var12 * 255.0F / (float)this.titleFadeInTime);
            }

            if (this.titleTime <= this.titleFadeOutTime) {
               var11 = (int)(var4 * 255.0F / (float)this.titleFadeOutTime);
            }

            var11 = Mth.clamp(var11, 0, 255);
            if (var11 > 8) {
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), 0.0F);
               GlStateManager.enableBlend();
               GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               GlStateManager.pushMatrix();
               GlStateManager.scalef(4.0F, 4.0F, 4.0F);
               var6 = var11 << 24 & -16777216;
               var7 = var2.width(this.title);
               this.drawBackdrop(var2, -10, var7);
               var2.drawShadow(this.title, (float)(-var7 / 2), -10.0F, 16777215 | var6);
               GlStateManager.popMatrix();
               if (!this.subtitle.isEmpty()) {
                  GlStateManager.pushMatrix();
                  GlStateManager.scalef(2.0F, 2.0F, 2.0F);
                  int var8 = var2.width(this.subtitle);
                  this.drawBackdrop(var2, 5, var8);
                  var2.drawShadow(this.subtitle, (float)(-var8 / 2), 5.0F, 16777215 | var6);
                  GlStateManager.popMatrix();
               }

               GlStateManager.disableBlend();
               GlStateManager.popMatrix();
            }

            this.minecraft.getProfiler().pop();
         }

         this.subtitleOverlay.render();
         Scoreboard var10 = this.minecraft.level.getScoreboard();
         Objective var15 = null;
         PlayerTeam var13 = var10.getPlayersTeam(this.minecraft.player.getScoreboardName());
         if (var13 != null) {
            var7 = var13.getColor().getId();
            if (var7 >= 0) {
               var15 = var10.getDisplayObjective(3 + var7);
            }
         }

         Objective var14 = var15 != null ? var15 : var10.getDisplayObjective(1);
         if (var14 != null) {
            this.displayScoreboardSidebar(var14);
         }

         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.disableAlphaTest();
         GlStateManager.pushMatrix();
         GlStateManager.translatef(0.0F, (float)(this.screenHeight - 48), 0.0F);
         this.minecraft.getProfiler().push("chat");
         this.chat.render(this.tickCount);
         this.minecraft.getProfiler().pop();
         GlStateManager.popMatrix();
         var14 = var10.getDisplayObjective(0);
         if (!this.minecraft.options.keyPlayerList.isDown() || this.minecraft.isLocalServer() && this.minecraft.player.connection.getOnlinePlayers().size() <= 1 && var14 == null) {
            this.tabList.setVisible(false);
         } else {
            this.tabList.setVisible(true);
            this.tabList.render(this.screenWidth, var10, var14);
         }
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      GlStateManager.enableAlphaTest();
   }

   private void drawBackdrop(Font var1, int var2, int var3) {
      int var4 = this.minecraft.options.getBackgroundColor(0.0F);
      if (var4 != 0) {
         int var5 = -var3 / 2;
         int var10000 = var5 - 2;
         int var10001 = var2 - 2;
         int var10002 = var5 + var3 + 2;
         var1.getClass();
         fill(var10000, var10001, var10002, var2 + 9 + 2, var4);
      }

   }

   private void renderCrosshair() {
      Options var1 = this.minecraft.options;
      if (var1.thirdPersonView == 0) {
         if (this.minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR || this.canRenderCrosshairForSpectator(this.minecraft.hitResult)) {
            if (var1.renderDebug && !var1.hideGui && !this.minecraft.player.isReducedDebugInfo() && !var1.reducedDebugInfo) {
               GlStateManager.pushMatrix();
               GlStateManager.translatef((float)(this.screenWidth / 2), (float)(this.screenHeight / 2), (float)this.blitOffset);
               Camera var8 = this.minecraft.gameRenderer.getMainCamera();
               GlStateManager.rotatef(var8.getXRot(), -1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef(var8.getYRot(), 0.0F, 1.0F, 0.0F);
               GlStateManager.scalef(-1.0F, -1.0F, -1.0F);
               GLX.renderCrosshair(10);
               GlStateManager.popMatrix();
            } else {
               GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
               boolean var2 = true;
               this.blit((this.screenWidth - 15) / 2, (this.screenHeight - 15) / 2, 0, 0, 15, 15);
               if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.CROSSHAIR) {
                  float var3 = this.minecraft.player.getAttackStrengthScale(0.0F);
                  boolean var4 = false;
                  if (this.minecraft.crosshairPickEntity != null && this.minecraft.crosshairPickEntity instanceof LivingEntity && var3 >= 1.0F) {
                     var4 = this.minecraft.player.getCurrentItemAttackStrengthDelay() > 5.0F;
                     var4 &= this.minecraft.crosshairPickEntity.isAlive();
                  }

                  int var5 = this.screenHeight / 2 - 7 + 16;
                  int var6 = this.screenWidth / 2 - 8;
                  if (var4) {
                     this.blit(var6, var5, 68, 94, 16, 16);
                  } else if (var3 < 1.0F) {
                     int var7 = (int)(var3 * 17.0F);
                     this.blit(var6, var5, 36, 94, 16, 4);
                     this.blit(var6, var5, 52, 94, var7, 4);
                  }
               }
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
         MultiPlayerLevel var3 = this.minecraft.level;
         return var3.getBlockState(var2).getMenuProvider(var3, var2) != null;
      } else {
         return false;
      }
   }

   protected void renderEffects() {
      Collection var1 = this.minecraft.player.getActiveEffects();
      if (!var1.isEmpty()) {
         GlStateManager.enableBlend();
         int var2 = 0;
         int var3 = 0;
         MobEffectTextureManager var4 = this.minecraft.getMobEffectTextures();
         ArrayList var5 = Lists.newArrayListWithExpectedSize(var1.size());
         this.minecraft.getTextureManager().bind(AbstractContainerScreen.INVENTORY_LOCATION);
         Iterator var6 = Ordering.natural().reverse().sortedCopy(var1).iterator();

         while(var6.hasNext()) {
            MobEffectInstance var7 = (MobEffectInstance)var6.next();
            MobEffect var8 = var7.getEffect();
            if (var7.showIcon()) {
               int var9 = this.screenWidth;
               int var10 = 1;
               if (this.minecraft.isDemo()) {
                  var10 += 15;
               }

               if (var8.isBeneficial()) {
                  ++var2;
                  var9 -= 25 * var2;
               } else {
                  ++var3;
                  var9 -= 25 * var3;
                  var10 += 26;
               }

               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               float var11 = 1.0F;
               if (var7.isAmbient()) {
                  this.blit(var9, var10, 165, 166, 24, 24);
               } else {
                  this.blit(var9, var10, 141, 166, 24, 24);
                  if (var7.getDuration() <= 200) {
                     int var12 = 10 - var7.getDuration() / 20;
                     var11 = Mth.clamp((float)var7.getDuration() / 10.0F / 5.0F * 0.5F, 0.0F, 0.5F) + Mth.cos((float)var7.getDuration() * 3.1415927F / 5.0F) * Mth.clamp((float)var12 / 10.0F * 0.25F, 0.0F, 0.25F);
                  }
               }

               TextureAtlasSprite var16 = var4.get(var8);
               var5.add(() -> {
                  GlStateManager.color4f(1.0F, 1.0F, 1.0F, var11);
                  blit(var9 + 3, var10 + 3, this.blitOffset, 18, 18, var16);
               });
            }
         }

         this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_MOB_EFFECTS);
         var5.forEach(Runnable::run);
      }
   }

   protected void renderHotbar(float var1) {
      Player var2 = this.getCameraPlayer();
      if (var2 != null) {
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
         ItemStack var3 = var2.getOffhandItem();
         HumanoidArm var4 = var2.getMainArm().getOpposite();
         int var5 = this.screenWidth / 2;
         int var6 = this.blitOffset;
         boolean var7 = true;
         boolean var8 = true;
         this.blitOffset = -90;
         this.blit(var5 - 91, this.screenHeight - 22, 0, 0, 182, 22);
         this.blit(var5 - 91 - 1 + var2.inventory.selected * 20, this.screenHeight - 22 - 1, 0, 22, 24, 22);
         if (!var3.isEmpty()) {
            if (var4 == HumanoidArm.LEFT) {
               this.blit(var5 - 91 - 29, this.screenHeight - 23, 24, 22, 29, 24);
            } else {
               this.blit(var5 + 91, this.screenHeight - 23, 53, 22, 29, 24);
            }
         }

         this.blitOffset = var6;
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableBlend();
         GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         Lighting.turnOnGui();

         int var9;
         int var10;
         int var11;
         for(var9 = 0; var9 < 9; ++var9) {
            var10 = var5 - 90 + var9 * 20 + 2;
            var11 = this.screenHeight - 16 - 3;
            this.renderSlot(var10, var11, var1, var2, (ItemStack)var2.inventory.items.get(var9));
         }

         if (!var3.isEmpty()) {
            var9 = this.screenHeight - 16 - 3;
            if (var4 == HumanoidArm.LEFT) {
               this.renderSlot(var5 - 91 - 26, var9, var1, var2, var3);
            } else {
               this.renderSlot(var5 + 91 + 10, var9, var1, var2, var3);
            }
         }

         if (this.minecraft.options.attackIndicator == AttackIndicatorStatus.HOTBAR) {
            float var13 = this.minecraft.player.getAttackStrengthScale(0.0F);
            if (var13 < 1.0F) {
               var10 = this.screenHeight - 20;
               var11 = var5 + 91 + 6;
               if (var4 == HumanoidArm.RIGHT) {
                  var11 = var5 - 91 - 22;
               }

               this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
               int var12 = (int)(var13 * 19.0F);
               GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
               this.blit(var11, var10, 0, 94, 18, 18);
               this.blit(var11, var10 + 18 - var12, 18, 112 - var12, 18, var12);
            }
         }

         Lighting.turnOff();
         GlStateManager.disableRescaleNormal();
         GlStateManager.disableBlend();
      }
   }

   public void renderJumpMeter(int var1) {
      this.minecraft.getProfiler().push("jumpBar");
      this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
      float var2 = this.minecraft.player.getJumpRidingScale();
      boolean var3 = true;
      int var4 = (int)(var2 * 183.0F);
      int var5 = this.screenHeight - 32 + 3;
      this.blit(var1, var5, 0, 84, 182, 5);
      if (var4 > 0) {
         this.blit(var1, var5, 0, 89, var4, 5);
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderExperienceBar(int var1) {
      this.minecraft.getProfiler().push("expBar");
      this.minecraft.getTextureManager().bind(GuiComponent.GUI_ICONS_LOCATION);
      int var2 = this.minecraft.player.getXpNeededForNextLevel();
      int var4;
      int var5;
      if (var2 > 0) {
         boolean var3 = true;
         var4 = (int)(this.minecraft.player.experienceProgress * 183.0F);
         var5 = this.screenHeight - 32 + 3;
         this.blit(var1, var5, 0, 64, 182, 5);
         if (var4 > 0) {
            this.blit(var1, var5, 0, 69, var4, 5);
         }
      }

      this.minecraft.getProfiler().pop();
      if (this.minecraft.player.experienceLevel > 0) {
         this.minecraft.getProfiler().push("expLevel");
         String var6 = "" + this.minecraft.player.experienceLevel;
         var4 = (this.screenWidth - this.getFont().width(var6)) / 2;
         var5 = this.screenHeight - 31 - 4;
         this.getFont().draw(var6, (float)(var4 + 1), (float)var5, 0);
         this.getFont().draw(var6, (float)(var4 - 1), (float)var5, 0);
         this.getFont().draw(var6, (float)var4, (float)(var5 + 1), 0);
         this.getFont().draw(var6, (float)var4, (float)(var5 - 1), 0);
         this.getFont().draw(var6, (float)var4, (float)var5, 8453920);
         this.minecraft.getProfiler().pop();
      }

   }

   public void renderSelectedItemName() {
      this.minecraft.getProfiler().push("selectedItemName");
      if (this.toolHighlightTimer > 0 && !this.lastToolHighlight.isEmpty()) {
         Component var1 = (new TextComponent("")).append(this.lastToolHighlight.getHoverName()).withStyle(this.lastToolHighlight.getRarity().color);
         if (this.lastToolHighlight.hasCustomHoverName()) {
            var1.withStyle(ChatFormatting.ITALIC);
         }

         String var2 = var1.getColoredString();
         int var3 = (this.screenWidth - this.getFont().width(var2)) / 2;
         int var4 = this.screenHeight - 59;
         if (!this.minecraft.gameMode.canHurtPlayer()) {
            var4 += 14;
         }

         int var5 = (int)((float)this.toolHighlightTimer * 256.0F / 10.0F);
         if (var5 > 255) {
            var5 = 255;
         }

         if (var5 > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            int var10000 = var3 - 2;
            int var10001 = var4 - 2;
            int var10002 = var3 + this.getFont().width(var2) + 2;
            this.getFont().getClass();
            fill(var10000, var10001, var10002, var4 + 9 + 2, this.minecraft.options.getBackgroundColor(0));
            this.getFont().drawShadow(var2, (float)var3, (float)var4, 16777215 + (var5 << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }

      this.minecraft.getProfiler().pop();
   }

   public void renderDemoOverlay() {
      this.minecraft.getProfiler().push("demo");
      String var1;
      if (this.minecraft.level.getGameTime() >= 120500L) {
         var1 = I18n.get("demo.demoExpired");
      } else {
         var1 = I18n.get("demo.remainingTime", StringUtil.formatTickDuration((int)(120500L - this.minecraft.level.getGameTime())));
      }

      int var2 = this.getFont().width(var1);
      this.getFont().drawShadow(var1, (float)(this.screenWidth - var2 - 10), 5.0F, 16777215);
      this.minecraft.getProfiler().pop();
   }

   private void displayScoreboardSidebar(Objective var1) {
      Scoreboard var2 = var1.getScoreboard();
      Collection var3 = var2.getPlayerScores(var1);
      List var4 = (List)var3.stream().filter((var0) -> {
         return var0.getOwner() != null && !var0.getOwner().startsWith("#");
      }).collect(Collectors.toList());
      Object var23;
      if (var4.size() > 15) {
         var23 = Lists.newArrayList(Iterables.skip(var4, var3.size() - 15));
      } else {
         var23 = var4;
      }

      String var5 = var1.getDisplayName().getColoredString();
      int var6 = this.getFont().width(var5);
      int var7 = var6;

      String var11;
      for(Iterator var8 = ((Collection)var23).iterator(); var8.hasNext(); var7 = Math.max(var7, this.getFont().width(var11))) {
         Score var9 = (Score)var8.next();
         PlayerTeam var10 = var2.getPlayersTeam(var9.getOwner());
         var11 = PlayerTeam.formatNameForTeam(var10, new TextComponent(var9.getOwner())).getColoredString() + ": " + ChatFormatting.RED + var9.getScore();
      }

      int var10000 = ((Collection)var23).size();
      this.getFont().getClass();
      int var24 = var10000 * 9;
      int var25 = this.screenHeight / 2 + var24 / 3;
      boolean var26 = true;
      int var27 = this.screenWidth - var7 - 3;
      int var12 = 0;
      int var13 = this.minecraft.options.getBackgroundColor(0.3F);
      int var14 = this.minecraft.options.getBackgroundColor(0.4F);
      Iterator var15 = ((Collection)var23).iterator();

      while(var15.hasNext()) {
         Score var16 = (Score)var15.next();
         ++var12;
         PlayerTeam var17 = var2.getPlayersTeam(var16.getOwner());
         String var18 = PlayerTeam.formatNameForTeam(var17, new TextComponent(var16.getOwner())).getColoredString();
         String var19 = ChatFormatting.RED + "" + var16.getScore();
         this.getFont().getClass();
         int var21 = var25 - var12 * 9;
         int var22 = this.screenWidth - 3 + 2;
         var10000 = var27 - 2;
         this.getFont().getClass();
         fill(var10000, var21, var22, var21 + 9, var13);
         this.getFont().draw(var18, (float)var27, (float)var21, 553648127);
         this.getFont().draw(var19, (float)(var22 - this.getFont().width(var19)), (float)var21, 553648127);
         if (var12 == ((Collection)var23).size()) {
            var10000 = var27 - 2;
            this.getFont().getClass();
            fill(var10000, var21 - 9 - 1, var22, var21 - 1, var14);
            fill(var27 - 2, var21 - 1, var22, var21, var13);
            Font var28 = this.getFont();
            float var10002 = (float)(var27 + var7 / 2 - var6 / 2);
            this.getFont().getClass();
            var28.draw(var5, var10002, (float)(var21 - 9), 553648127);
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
      return (int)Math.ceil((double)var1 / 10.0D);
   }

   private void renderPlayerHealth() {
      Player var1 = this.getCameraPlayer();
      if (var1 != null) {
         int var2 = Mth.ceil(var1.getHealth());
         boolean var3 = this.healthBlinkTime > (long)this.tickCount && (this.healthBlinkTime - (long)this.tickCount) / 3L % 2L == 1L;
         long var4 = Util.getMillis();
         if (var2 < this.lastHealth && var1.invulnerableTime > 0) {
            this.lastHealthTime = var4;
            this.healthBlinkTime = (long)(this.tickCount + 20);
         } else if (var2 > this.lastHealth && var1.invulnerableTime > 0) {
            this.lastHealthTime = var4;
            this.healthBlinkTime = (long)(this.tickCount + 10);
         }

         if (var4 - this.lastHealthTime > 1000L) {
            this.lastHealth = var2;
            this.displayHealth = var2;
            this.lastHealthTime = var4;
         }

         this.lastHealth = var2;
         int var6 = this.displayHealth;
         this.random.setSeed((long)(this.tickCount * 312871));
         FoodData var7 = var1.getFoodData();
         int var8 = var7.getFoodLevel();
         AttributeInstance var9 = var1.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
         int var10 = this.screenWidth / 2 - 91;
         int var11 = this.screenWidth / 2 + 91;
         int var12 = this.screenHeight - 39;
         float var13 = (float)var9.getValue();
         int var14 = Mth.ceil(var1.getAbsorptionAmount());
         int var15 = Mth.ceil((var13 + (float)var14) / 2.0F / 10.0F);
         int var16 = Math.max(10 - (var15 - 2), 3);
         int var17 = var12 - (var15 - 1) * var16 - 10;
         int var18 = var12 - 10;
         int var19 = var14;
         int var20 = var1.getArmorValue();
         int var21 = -1;
         if (var1.hasEffect(MobEffects.REGENERATION)) {
            var21 = this.tickCount % Mth.ceil(var13 + 5.0F);
         }

         this.minecraft.getProfiler().push("armor");

         int var22;
         int var23;
         for(var22 = 0; var22 < 10; ++var22) {
            if (var20 > 0) {
               var23 = var10 + var22 * 8;
               if (var22 * 2 + 1 < var20) {
                  this.blit(var23, var17, 34, 9, 9, 9);
               }

               if (var22 * 2 + 1 == var20) {
                  this.blit(var23, var17, 25, 9, 9, 9);
               }

               if (var22 * 2 + 1 > var20) {
                  this.blit(var23, var17, 16, 9, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().popPush("health");

         int var25;
         int var26;
         int var27;
         for(var22 = Mth.ceil((var13 + (float)var14) / 2.0F) - 1; var22 >= 0; --var22) {
            var23 = 16;
            if (var1.hasEffect(MobEffects.POISON)) {
               var23 += 36;
            } else if (var1.hasEffect(MobEffects.WITHER)) {
               var23 += 72;
            }

            byte var24 = 0;
            if (var3) {
               var24 = 1;
            }

            var25 = Mth.ceil((float)(var22 + 1) / 10.0F) - 1;
            var26 = var10 + var22 % 10 * 8;
            var27 = var12 - var25 * var16;
            if (var2 <= 4) {
               var27 += this.random.nextInt(2);
            }

            if (var19 <= 0 && var22 == var21) {
               var27 -= 2;
            }

            byte var28 = 0;
            if (var1.level.getLevelData().isHardcore()) {
               var28 = 5;
            }

            this.blit(var26, var27, 16 + var24 * 9, 9 * var28, 9, 9);
            if (var3) {
               if (var22 * 2 + 1 < var6) {
                  this.blit(var26, var27, var23 + 54, 9 * var28, 9, 9);
               }

               if (var22 * 2 + 1 == var6) {
                  this.blit(var26, var27, var23 + 63, 9 * var28, 9, 9);
               }
            }

            if (var19 > 0) {
               if (var19 == var14 && var14 % 2 == 1) {
                  this.blit(var26, var27, var23 + 153, 9 * var28, 9, 9);
                  --var19;
               } else {
                  this.blit(var26, var27, var23 + 144, 9 * var28, 9, 9);
                  var19 -= 2;
               }
            } else {
               if (var22 * 2 + 1 < var2) {
                  this.blit(var26, var27, var23 + 36, 9 * var28, 9, 9);
               }

               if (var22 * 2 + 1 == var2) {
                  this.blit(var26, var27, var23 + 45, 9 * var28, 9, 9);
               }
            }
         }

         LivingEntity var30 = this.getPlayerVehicleWithHealth();
         var23 = this.getVehicleMaxHearts(var30);
         int var31;
         int var33;
         if (var23 == 0) {
            this.minecraft.getProfiler().popPush("food");

            for(var31 = 0; var31 < 10; ++var31) {
               var25 = var12;
               var26 = 16;
               byte var32 = 0;
               if (var1.hasEffect(MobEffects.HUNGER)) {
                  var26 += 36;
                  var32 = 13;
               }

               if (var1.getFoodData().getSaturationLevel() <= 0.0F && this.tickCount % (var8 * 3 + 1) == 0) {
                  var25 = var12 + (this.random.nextInt(3) - 1);
               }

               var33 = var11 - var31 * 8 - 9;
               this.blit(var33, var25, 16 + var32 * 9, 27, 9, 9);
               if (var31 * 2 + 1 < var8) {
                  this.blit(var33, var25, var26 + 36, 27, 9, 9);
               }

               if (var31 * 2 + 1 == var8) {
                  this.blit(var33, var25, var26 + 45, 27, 9, 9);
               }
            }

            var18 -= 10;
         }

         this.minecraft.getProfiler().popPush("air");
         var31 = var1.getAirSupply();
         var25 = var1.getMaxAirSupply();
         if (var1.isUnderLiquid(FluidTags.WATER) || var31 < var25) {
            var26 = this.getVisibleVehicleHeartRows(var23) - 1;
            var18 -= var26 * 10;
            var27 = Mth.ceil((double)(var31 - 2) * 10.0D / (double)var25);
            var33 = Mth.ceil((double)var31 * 10.0D / (double)var25) - var27;

            for(int var29 = 0; var29 < var27 + var33; ++var29) {
               if (var29 < var27) {
                  this.blit(var11 - var29 * 8 - 9, var18, 16, 18, 9, 9);
               } else {
                  this.blit(var11 - var29 * 8 - 9, var18, 25, 18, 9, 9);
               }
            }
         }

         this.minecraft.getProfiler().pop();
      }
   }

   private void renderVehicleHealth() {
      LivingEntity var1 = this.getPlayerVehicleWithHealth();
      if (var1 != null) {
         int var2 = this.getVehicleMaxHearts(var1);
         if (var2 != 0) {
            int var3 = (int)Math.ceil((double)var1.getHealth());
            this.minecraft.getProfiler().popPush("mountHealth");
            int var4 = this.screenHeight - 39;
            int var5 = this.screenWidth / 2 + 91;
            int var6 = var4;
            int var7 = 0;

            for(boolean var8 = false; var2 > 0; var7 += 20) {
               int var9 = Math.min(var2, 10);
               var2 -= var9;

               for(int var10 = 0; var10 < var9; ++var10) {
                  boolean var11 = true;
                  byte var12 = 0;
                  int var13 = var5 - var10 * 8 - 9;
                  this.blit(var13, var6, 52 + var12 * 9, 9, 9, 9);
                  if (var10 * 2 + 1 + var7 < var3) {
                     this.blit(var13, var6, 88, 9, 9, 9);
                  }

                  if (var10 * 2 + 1 + var7 == var3) {
                     this.blit(var13, var6, 97, 9, 9, 9);
                  }
               }

               var6 -= 10;
            }

         }
      }
   }

   private void renderPumpkin() {
      GlStateManager.disableDepthTest();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableAlphaTest();
      this.minecraft.getTextureManager().bind(PUMPKIN_BLUR_LOCATION);
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      var2.begin(7, DefaultVertexFormat.POSITION_TEX);
      var2.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(0.0D, 1.0D).endVertex();
      var2.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(1.0D, 1.0D).endVertex();
      var2.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(1.0D, 0.0D).endVertex();
      var2.vertex(0.0D, 0.0D, -90.0D).uv(0.0D, 0.0D).endVertex();
      var1.end();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void updateVignetteBrightness(Entity var1) {
      if (var1 != null) {
         float var2 = Mth.clamp(1.0F - var1.getBrightness(), 0.0F, 1.0F);
         this.vignetteBrightness = (float)((double)this.vignetteBrightness + (double)(var2 - this.vignetteBrightness) * 0.01D);
      }
   }

   private void renderVignette(Entity var1) {
      WorldBorder var2 = this.minecraft.level.getWorldBorder();
      float var3 = (float)var2.getDistanceToBorder(var1);
      double var4 = Math.min(var2.getLerpSpeed() * (double)var2.getWarningTime() * 1000.0D, Math.abs(var2.getLerpTarget() - var2.getSize()));
      double var6 = Math.max((double)var2.getWarningBlocks(), var4);
      if ((double)var3 < var6) {
         var3 = 1.0F - (float)((double)var3 / var6);
      } else {
         var3 = 0.0F;
      }

      GlStateManager.disableDepthTest();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      if (var3 > 0.0F) {
         GlStateManager.color4f(0.0F, var3, var3, 1.0F);
      } else {
         GlStateManager.color4f(this.vignetteBrightness, this.vignetteBrightness, this.vignetteBrightness, 1.0F);
      }

      this.minecraft.getTextureManager().bind(VIGNETTE_LOCATION);
      Tesselator var8 = Tesselator.getInstance();
      BufferBuilder var9 = var8.getBuilder();
      var9.begin(7, DefaultVertexFormat.POSITION_TEX);
      var9.vertex(0.0D, (double)this.screenHeight, -90.0D).uv(0.0D, 1.0D).endVertex();
      var9.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv(1.0D, 1.0D).endVertex();
      var9.vertex((double)this.screenWidth, 0.0D, -90.0D).uv(1.0D, 0.0D).endVertex();
      var9.vertex(0.0D, 0.0D, -90.0D).uv(0.0D, 0.0D).endVertex();
      var8.end();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepthTest();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
   }

   private void renderPortalOverlay(float var1) {
      if (var1 < 1.0F) {
         var1 *= var1;
         var1 *= var1;
         var1 = var1 * 0.8F + 0.2F;
      }

      GlStateManager.disableAlphaTest();
      GlStateManager.disableDepthTest();
      GlStateManager.depthMask(false);
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, var1);
      this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
      TextureAtlasSprite var2 = this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());
      float var3 = var2.getU0();
      float var4 = var2.getV0();
      float var5 = var2.getU1();
      float var6 = var2.getV1();
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();
      var8.begin(7, DefaultVertexFormat.POSITION_TEX);
      var8.vertex(0.0D, (double)this.screenHeight, -90.0D).uv((double)var3, (double)var6).endVertex();
      var8.vertex((double)this.screenWidth, (double)this.screenHeight, -90.0D).uv((double)var5, (double)var6).endVertex();
      var8.vertex((double)this.screenWidth, 0.0D, -90.0D).uv((double)var5, (double)var4).endVertex();
      var8.vertex(0.0D, 0.0D, -90.0D).uv((double)var3, (double)var4).endVertex();
      var7.end();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepthTest();
      GlStateManager.enableAlphaTest();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderSlot(int var1, int var2, float var3, Player var4, ItemStack var5) {
      if (!var5.isEmpty()) {
         float var6 = (float)var5.getPopTime() - var3;
         if (var6 > 0.0F) {
            GlStateManager.pushMatrix();
            float var7 = 1.0F + var6 / 5.0F;
            GlStateManager.translatef((float)(var1 + 8), (float)(var2 + 12), 0.0F);
            GlStateManager.scalef(1.0F / var7, (var7 + 1.0F) / 2.0F, 1.0F);
            GlStateManager.translatef((float)(-(var1 + 8)), (float)(-(var2 + 12)), 0.0F);
         }

         this.itemRenderer.renderAndDecorateItem(var4, var5, var1, var2);
         if (var6 > 0.0F) {
            GlStateManager.popMatrix();
         }

         this.itemRenderer.renderGuiItemDecorations(this.minecraft.font, var5, var1, var2);
      }
   }

   public void tick() {
      if (this.overlayMessageTime > 0) {
         --this.overlayMessageTime;
      }

      if (this.titleTime > 0) {
         --this.titleTime;
         if (this.titleTime <= 0) {
            this.title = "";
            this.subtitle = "";
         }
      }

      ++this.tickCount;
      Entity var1 = this.minecraft.getCameraEntity();
      if (var1 != null) {
         this.updateVignetteBrightness(var1);
      }

      if (this.minecraft.player != null) {
         ItemStack var2 = this.minecraft.player.inventory.getSelected();
         if (var2.isEmpty()) {
            this.toolHighlightTimer = 0;
         } else if (!this.lastToolHighlight.isEmpty() && var2.getItem() == this.lastToolHighlight.getItem() && var2.getHoverName().equals(this.lastToolHighlight.getHoverName())) {
            if (this.toolHighlightTimer > 0) {
               --this.toolHighlightTimer;
            }
         } else {
            this.toolHighlightTimer = 40;
         }

         this.lastToolHighlight = var2;
      }

   }

   public void setNowPlaying(String var1) {
      this.setOverlayMessage(I18n.get("record.nowPlaying", var1), true);
   }

   public void setOverlayMessage(String var1, boolean var2) {
      this.overlayMessageString = var1;
      this.overlayMessageTime = 60;
      this.animateOverlayMessageColor = var2;
   }

   public void setTitles(String var1, String var2, int var3, int var4, int var5) {
      if (var1 == null && var2 == null && var3 < 0 && var4 < 0 && var5 < 0) {
         this.title = "";
         this.subtitle = "";
         this.titleTime = 0;
      } else if (var1 != null) {
         this.title = var1;
         this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
      } else if (var2 != null) {
         this.subtitle = var2;
      } else {
         if (var3 >= 0) {
            this.titleFadeInTime = var3;
         }

         if (var4 >= 0) {
            this.titleStayTime = var4;
         }

         if (var5 >= 0) {
            this.titleFadeOutTime = var5;
         }

         if (this.titleTime > 0) {
            this.titleTime = this.titleFadeInTime + this.titleStayTime + this.titleFadeOutTime;
         }

      }
   }

   public void setOverlayMessage(Component var1, boolean var2) {
      this.setOverlayMessage(var1.getString(), var2);
   }

   public void handleChat(ChatType var1, Component var2) {
      Iterator var3 = ((List)this.chatListeners.get(var1)).iterator();

      while(var3.hasNext()) {
         ChatListener var4 = (ChatListener)var3.next();
         var4.handle(var1, var2);
      }

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
   }

   public BossHealthOverlay getBossOverlay() {
      return this.bossOverlay;
   }

   public void clearCache() {
      this.debugScreen.clearChunkCache();
   }
}
