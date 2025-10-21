package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.platform.Window;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.util.Locale;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ServerboundChangeGameModePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.VersionCommand;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.FeatureCountTracker;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class KeyboardHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final int DEBUG_CRASH_TIME = 10000;
   private final Minecraft minecraft;
   private final ClipboardManager clipboardManager = new ClipboardManager();
   private long debugCrashKeyTime = -1L;
   private long debugCrashKeyReportedTime = -1L;
   private long debugCrashKeyReportedCount = -1L;
   private boolean usedDebugKeyAsModifier;

   public KeyboardHandler(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   private boolean handleChunkDebugKeys(KeyEvent var1) {
      switch (var1.key()) {
         case 69:
            if (this.minecraft.player == null) {
               return false;
            }

            boolean var2 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_SECTION_PATHS);
            this.debugFeedback("SectionPath: " + (var2 ? "shown" : "hidden"));
            return true;
         case 70:
            boolean var4 = FogRenderer.toggleFog();
            this.debugFeedbackEnabledStatus("Fog: ", var4);
            return true;
         case 71:
         case 72:
         case 73:
         case 74:
         case 75:
         case 77:
         case 78:
         case 80:
         case 81:
         case 82:
         case 83:
         case 84:
         default:
            return false;
         case 76:
            this.minecraft.smartCull = !this.minecraft.smartCull;
            this.debugFeedbackEnabledStatus("SmartCull: ", this.minecraft.smartCull);
            return true;
         case 79:
            if (this.minecraft.player == null) {
               return false;
            }

            boolean var3 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_SECTION_OCTREE);
            this.debugFeedbackEnabledStatus("Frustum culling Octree: ", var3);
            return true;
         case 85:
            if (var1.hasShiftDown()) {
               this.minecraft.levelRenderer.killFrustum();
               this.debugFeedback("Killed frustum");
            } else {
               this.minecraft.levelRenderer.captureFrustum();
               this.debugFeedback("Captured frustum");
            }

            return true;
         case 86:
            if (this.minecraft.player == null) {
               return false;
            }

            boolean var5 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_SECTION_VISIBILITY);
            this.debugFeedbackEnabledStatus("SectionVisibility: ", var5);
            return true;
         case 87:
            this.minecraft.wireframe = !this.minecraft.wireframe;
            this.debugFeedbackEnabledStatus("WireFrame: ", this.minecraft.wireframe);
            return true;
      }
   }

   private void debugFeedbackEnabledStatus(String var1, boolean var2) {
      this.debugFeedback(var1 + (var2 ? "enabled" : "disabled"));
   }

   private void showDebugChat(Component var1) {
      this.minecraft.gui.getChat().addMessage(var1);
      this.minecraft.getNarrator().saySystemQueued(var1);
   }

   private static Component decorateDebugComponent(ChatFormatting var0, Component var1) {
      return Component.empty().append((Component)Component.translatable("debug.prefix").withStyle(var0, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(var1);
   }

   private void debugWarningComponent(Component var1) {
      this.showDebugChat(decorateDebugComponent(ChatFormatting.RED, var1));
   }

   private void debugFeedbackComponent(Component var1) {
      this.showDebugChat(decorateDebugComponent(ChatFormatting.YELLOW, var1));
   }

   private void debugFeedbackTranslated(String var1, Object... var2) {
      this.debugFeedbackComponent(Component.translatable(var1, var2));
   }

   private void debugFeedback(String var1) {
      this.debugFeedbackComponent(Component.literal(var1));
   }

   private boolean handleDebugKeys(KeyEvent var1) {
      if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
         return true;
      } else if (SharedConstants.DEBUG_HOTKEYS && this.handleChunkDebugKeys(var1)) {
         return true;
      } else {
         if (SharedConstants.DEBUG_FEATURE_COUNT) {
            switch (var1.key()) {
               case 76:
                  FeatureCountTracker.logCounts();
                  return true;
               case 82:
                  FeatureCountTracker.clearCounts();
                  return true;
            }
         }

         Options var2 = this.minecraft.options;
         boolean var3 = false;
         if (var2.keyDebugReloadChunk.matches(var1)) {
            this.minecraft.levelRenderer.allChanged();
            this.debugFeedbackTranslated("debug.reload_chunks.message");
            var3 = true;
         }

         if (var2.keyDebugShowHitboxes.matches(var1) && this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
            boolean var4 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.ENTITY_HITBOXES);
            this.debugFeedbackTranslated(var4 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
            var3 = true;
         }

         if (var2.keyDebugClearChat.matches(var1)) {
            this.minecraft.gui.getChat().clearMessages(false);
            var3 = true;
         }

         if (var2.keyDebugShowChunkBorders.matches(var1) && this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
            boolean var7 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_BORDERS);
            this.debugFeedbackTranslated(var7 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
            var3 = true;
         }

         if (var2.keyDebugShowAdvancedTooltips.matches(var1)) {
            var2.advancedItemTooltips = !var2.advancedItemTooltips;
            this.debugFeedbackTranslated(var2.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
            var2.save();
            var3 = true;
         }

         if (var2.keyDebugCopyRecreateCommand.matches(var1)) {
            if (this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
               this.copyRecreateCommand(this.minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER), !var1.hasShiftDown());
            }

            var3 = true;
         }

         if (var2.keyDebugSpectate.matches(var1)) {
            if (this.minecraft.player != null && GameModeCommand.PERMISSION_CHECK.check(this.minecraft.player.permissions())) {
               if (!this.minecraft.player.isSpectator()) {
                  this.minecraft.player.connection.send(new ServerboundChangeGameModePacket(GameType.SPECTATOR));
               } else {
                  GameType var8 = (GameType)MoreObjects.firstNonNull(this.minecraft.gameMode.getPreviousPlayerMode(), GameType.CREATIVE);
                  this.minecraft.player.connection.send(new ServerboundChangeGameModePacket(var8));
               }
            } else {
               this.debugFeedbackTranslated("debug.creative_spectator.error");
            }

            var3 = true;
         }

         if (var2.keyDebugSwitchGameMode.matches(var1) && this.minecraft.level != null && this.minecraft.screen == null) {
            if (this.minecraft.canSwitchGameMode() && GameModeCommand.PERMISSION_CHECK.check(this.minecraft.player.permissions())) {
               this.minecraft.setScreen(new GameModeSwitcherScreen());
            } else {
               this.debugFeedbackTranslated("debug.gamemodes.error");
            }

            var3 = true;
         }

         if (var2.keyDebugDebugOptions.matches(var1)) {
            if (this.minecraft.screen instanceof DebugOptionsScreen) {
               this.minecraft.screen.onClose();
            } else if (this.minecraft.canInterruptScreen()) {
               if (this.minecraft.screen != null) {
                  this.minecraft.screen.onClose();
               }

               this.minecraft.setScreen(new DebugOptionsScreen());
            }

            var3 = true;
         }

         if (var2.keyDebugFocusPause.matches(var1)) {
            var2.pauseOnLostFocus = !var2.pauseOnLostFocus;
            var2.save();
            this.debugFeedbackTranslated(var2.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
            var3 = true;
         }

         if (var2.keyDebugDumpDynamicTextures.matches(var1)) {
            Path var9 = this.minecraft.gameDirectory.toPath().toAbsolutePath();
            Path var5 = TextureUtil.getDebugTexturePath(var9);
            this.minecraft.getTextureManager().dumpAllSheets(var5);
            MutableComponent var6 = Component.literal(var9.relativize(var5).toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1x) -> var1x.withClickEvent(new ClickEvent.OpenFile(var5))));
            this.debugFeedbackComponent(Component.translatable("debug.dump_dynamic_textures", var6));
            var3 = true;
         }

         if (var2.keyDebugReloadResourcePacks.matches(var1)) {
            this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
            this.minecraft.reloadResourcePacks();
            var3 = true;
         }

         if (var2.keyDebugProfiling.matches(var1)) {
            if (this.minecraft.debugClientMetricsStart(this::debugFeedbackComponent)) {
               this.debugFeedbackComponent(Component.translatable("debug.profiling.start", 10, var2.keyDebugModifier.getTranslatedKeyMessage(), var2.keyDebugProfiling.getTranslatedKeyMessage()));
            }

            var3 = true;
         }

         if (var2.keyDebugCopyLocation.matches(var1) && this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
            this.debugFeedbackTranslated("debug.copy_location.message");
            this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.minecraft.player.level().dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), this.minecraft.player.getYRot(), this.minecraft.player.getXRot()));
            var3 = true;
         }

         if (var2.keyDebugDumpVersion.matches(var1)) {
            this.debugFeedbackTranslated("debug.version.header");
            VersionCommand.dumpVersion(this::showDebugChat);
            var3 = true;
         }

         if (var2.keyDebugPofilingChart.matches(var1)) {
            this.minecraft.getDebugOverlay().toggleProfilerChart();
            var3 = true;
         }

         if (var2.keyDebugFpsCharts.matches(var1)) {
            this.minecraft.getDebugOverlay().toggleFpsCharts();
            var3 = true;
         }

         if (var2.keyDebugNetworkCharts.matches(var1)) {
            this.minecraft.getDebugOverlay().toggleNetworkCharts();
            var3 = true;
         }

         return var3;
      }
   }

   private void copyRecreateCommand(boolean var1, boolean var2) {
      HitResult var3 = this.minecraft.hitResult;
      if (var3 != null) {
         switch (var3.getType()) {
            case BLOCK:
               BlockPos var11 = ((BlockHitResult)var3).getBlockPos();
               Level var12 = this.minecraft.player.level();
               BlockState var13 = var12.getBlockState(var11);
               if (var1) {
                  if (var2) {
                     this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(var11, (var3x) -> {
                        this.copyCreateBlockCommand(var13, var11, var3x);
                        this.debugFeedbackTranslated("debug.inspect.server.block");
                     });
                  } else {
                     BlockEntity var14 = var12.getBlockEntity(var11);
                     CompoundTag var8 = var14 != null ? var14.saveWithoutMetadata((HolderLookup.Provider)var12.registryAccess()) : null;
                     this.copyCreateBlockCommand(var13, var11, var8);
                     this.debugFeedbackTranslated("debug.inspect.client.block");
                  }
               } else {
                  this.copyCreateBlockCommand(var13, var11, (CompoundTag)null);
                  this.debugFeedbackTranslated("debug.inspect.client.block");
               }
               break;
            case ENTITY:
               Entity var4 = ((EntityHitResult)var3).getEntity();
               ResourceLocation var5 = BuiltInRegistries.ENTITY_TYPE.getKey(var4.getType());
               if (var1) {
                  if (var2) {
                     this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(var4.getId(), (var3x) -> {
                        this.copyCreateEntityCommand(var5, var4.position(), var3x);
                        this.debugFeedbackTranslated("debug.inspect.server.entity");
                     });
                  } else {
                     try (ProblemReporter.ScopedCollector var6 = new ProblemReporter.ScopedCollector(var4.problemPath(), LOGGER)) {
                        TagValueOutput var7 = TagValueOutput.createWithContext(var6, var4.registryAccess());
                        var4.saveWithoutId(var7);
                        this.copyCreateEntityCommand(var5, var4.position(), var7.buildResult());
                     }

                     this.debugFeedbackTranslated("debug.inspect.client.entity");
                  }
               } else {
                  this.copyCreateEntityCommand(var5, var4.position(), (CompoundTag)null);
                  this.debugFeedbackTranslated("debug.inspect.client.entity");
               }
         }

      }
   }

   private void copyCreateBlockCommand(BlockState var1, BlockPos var2, @Nullable CompoundTag var3) {
      StringBuilder var4 = new StringBuilder(BlockStateParser.serialize(var1));
      if (var3 != null) {
         var4.append(var3);
      }

      String var5 = String.format(Locale.ROOT, "/setblock %d %d %d %s", var2.getX(), var2.getY(), var2.getZ(), var4);
      this.setClipboard(var5);
   }

   private void copyCreateEntityCommand(ResourceLocation var1, Vec3 var2, @Nullable CompoundTag var3) {
      String var4;
      if (var3 != null) {
         var3.remove("UUID");
         var3.remove("Pos");
         String var5 = NbtUtils.toPrettyComponent(var3).getString();
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", var1, var2.x, var2.y, var2.z, var5);
      } else {
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", var1, var2.x, var2.y, var2.z);
      }

      this.setClipboard(var4);
   }

   private void keyPress(long var1, @KeyEvent.Action int var3, KeyEvent var4) {
      Window var5 = this.minecraft.getWindow();
      if (var1 == var5.handle()) {
         this.minecraft.getFramerateLimitTracker().onInputReceived();
         Options var6 = this.minecraft.options;
         boolean var7 = var6.keyDebugModifier.key.getValue() == var6.keyDebugOverlay.key.getValue();
         boolean var8 = var6.keyDebugModifier.isDown();
         boolean var9 = !var6.keyDebugCrash.isUnbound() && InputConstants.isKeyDown(this.minecraft.getWindow(), var6.keyDebugCrash.key.getValue());
         if (this.debugCrashKeyTime > 0L) {
            if (!var9 || !var8) {
               this.debugCrashKeyTime = -1L;
            }
         } else if (var9 && var8) {
            this.usedDebugKeyAsModifier = var7;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
         }

         Screen var10 = this.minecraft.screen;
         if (var10 != null) {
            switch (var4.key()) {
               case 258:
                  this.minecraft.setLastInputType(InputType.KEYBOARD_TAB);
               case 259:
               case 260:
               case 261:
               default:
                  break;
               case 262:
               case 263:
               case 264:
               case 265:
                  this.minecraft.setLastInputType(InputType.KEYBOARD_ARROW);
            }
         }

         if (var3 == 1 && (!(this.minecraft.screen instanceof KeyBindsScreen) || ((KeyBindsScreen)var10).lastKeySelection <= Util.getMillis() - 20L)) {
            if (var6.keyFullscreen.matches(var4)) {
               var5.toggleFullScreen();
               boolean var19 = var5.isFullscreen();
               var6.fullscreen().set(var19);
               var6.save();
               Screen var25 = this.minecraft.screen;
               if (var25 instanceof VideoSettingsScreen) {
                  VideoSettingsScreen var23 = (VideoSettingsScreen)var25;
                  var23.updateFullscreenButton(var19);
               }

               return;
            }

            if (var6.keyScreenshot.matches(var4)) {
               if (var4.hasControlDownWithQuirk() && SharedConstants.DEBUG_PANORAMA_SCREENSHOT) {
                  this.showDebugChat(this.minecraft.grabPanoramixScreenshot(this.minecraft.gameDirectory));
               } else {
                  Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.getMainRenderTarget(), (var1x) -> this.minecraft.execute(() -> this.showDebugChat(var1x)));
               }

               return;
            }
         }

         if (var3 != 0) {
            boolean var11 = var10 == null || !(var10.getFocused() instanceof EditBox) || !((EditBox)var10.getFocused()).canConsumeInput();
            if (var11) {
               if (var4.hasControlDownWithQuirk() && var4.key() == 66 && this.minecraft.getNarrator().isActive() && (Boolean)var6.narratorHotkey().get()) {
                  boolean var12 = var6.narrator().get() == NarratorStatus.OFF;
                  var6.narrator().set(NarratorStatus.byId(((NarratorStatus)var6.narrator().get()).getId() + 1));
                  var6.save();
                  if (var10 != null) {
                     var10.updateNarratorStatus(var12);
                  }
               }

               LocalPlayer var20 = this.minecraft.player;
            }
         }

         if (var10 != null) {
            try {
               if (var3 != 1 && var3 != 2) {
                  if (var3 == 0 && var10.keyReleased(var4)) {
                     if (var6.keyDebugModifier.matches(var4)) {
                        this.usedDebugKeyAsModifier = false;
                     }

                     return;
                  }
               } else {
                  var10.afterKeyboardAction();
                  if (var10.keyPressed(var4)) {
                     if (this.minecraft.screen == null) {
                        InputConstants.Key var17 = InputConstants.getKey(var4);
                        KeyMapping.set(var17, false);
                     }

                     return;
                  }
               }
            } catch (Throwable var16) {
               CrashReport var21 = CrashReport.forThrowable(var16, "keyPressed event handler");
               var10.fillCrashDetails(var21);
               CrashReportCategory var13 = var21.addCategory("Key");
               var13.setDetail("Key", var4.key());
               var13.setDetail("Scancode", var4.scancode());
               var13.setDetail("Mods", var4.modifiers());
               throw new ReportedException(var21);
            }
         }

         InputConstants.Key var18;
         boolean var22;
         boolean var10000;
         label197: {
            var18 = InputConstants.getKey(var4);
            var22 = this.minecraft.screen == null;
            if (!var22) {
               label195: {
                  Screen var15 = this.minecraft.screen;
                  if (var15 instanceof PauseScreen) {
                     PauseScreen var14 = (PauseScreen)var15;
                     if (!var14.showsPauseMenu()) {
                        break label195;
                     }
                  }

                  if (!(this.minecraft.screen instanceof GameModeSwitcherScreen)) {
                     var10000 = false;
                     break label197;
                  }
               }
            }

            var10000 = true;
         }

         boolean var24 = var10000;
         if (var7 && var6.keyDebugModifier.matches(var4) && var3 == 0) {
            if (this.usedDebugKeyAsModifier) {
               this.usedDebugKeyAsModifier = false;
            } else {
               this.minecraft.debugEntries.toggleDebugOverlay();
            }
         } else if (!var7 && var6.keyDebugOverlay.matches(var4) && var3 == 1) {
            this.minecraft.debugEntries.toggleDebugOverlay();
         }

         if (var3 == 0) {
            KeyMapping.set(var18, false);
         } else {
            boolean var26 = false;
            if (var24 && var4.isEscape()) {
               this.minecraft.pauseGame(var8);
               var26 = var8;
            } else if (var8) {
               var26 = this.handleDebugKeys(var4);
            } else if (var24 && var6.keyToggleGui.matches(var4)) {
               var6.hideGui = !var6.hideGui;
            } else if (var24 && var6.keyToggleSpectatorShaderEffects.matches(var4)) {
               this.minecraft.gameRenderer.togglePostEffect();
            }

            if (var7) {
               this.usedDebugKeyAsModifier |= var26;
            }

            if (this.minecraft.getDebugOverlay().showProfilerChart() && !var8) {
               int var27 = var4.getDigit();
               if (var27 != -1) {
                  this.minecraft.getDebugOverlay().getProfilerPieChart().profilerPieChartKeyPress(var27);
               }
            }

            if (var22 || var18 == var6.keyDebugModifier.key) {
               if (var26) {
                  KeyMapping.set(var18, false);
               } else {
                  KeyMapping.set(var18, true);
                  KeyMapping.click(var18);
               }
            }

         }
      }
   }

   private void charTyped(long var1, CharacterEvent var3) {
      if (var1 == this.minecraft.getWindow().handle()) {
         Screen var4 = this.minecraft.screen;
         if (var4 != null && this.minecraft.getOverlay() == null) {
            try {
               var4.charTyped(var3);
            } catch (Throwable var8) {
               CrashReport var6 = CrashReport.forThrowable(var8, "charTyped event handler");
               var4.fillCrashDetails(var6);
               CrashReportCategory var7 = var6.addCategory("Key");
               var7.setDetail("Codepoint", var3.codepoint());
               var7.setDetail("Mods", var3.modifiers());
               throw new ReportedException(var6);
            }
         }
      }
   }

   public void setup(Window var1) {
      InputConstants.setupKeyboardCallbacks(var1, (var1x, var3, var4, var5, var6) -> {
         KeyEvent var7 = new KeyEvent(var3, var4, var6);
         this.minecraft.execute(() -> this.keyPress(var1x, var5, var7));
      }, (var1x, var3, var4) -> {
         CharacterEvent var5 = new CharacterEvent(var3, var4);
         this.minecraft.execute(() -> this.charTyped(var1x, var5));
      });
   }

   public String getClipboard() {
      return this.clipboardManager.getClipboard(this.minecraft.getWindow(), (var1, var2) -> {
         if (var1 != 65545) {
            this.minecraft.getWindow().defaultErrorCallback(var1, var2);
         }

      });
   }

   public void setClipboard(String var1) {
      if (!var1.isEmpty()) {
         this.clipboardManager.setClipboard(this.minecraft.getWindow(), var1);
      }

   }

   public void tick() {
      if (this.debugCrashKeyTime > 0L) {
         long var1 = Util.getMillis();
         long var3 = 10000L - (var1 - this.debugCrashKeyTime);
         long var5 = var1 - this.debugCrashKeyReportedTime;
         if (var3 < 0L) {
            if (this.minecraft.hasControlDown()) {
               Blaze3D.youJustLostTheGame();
            }

            String var7 = "Manually triggered debug crash";
            CrashReport var8 = new CrashReport("Manually triggered debug crash", new Throwable("Manually triggered debug crash"));
            CrashReportCategory var9 = var8.addCategory("Manual crash details");
            NativeModuleLister.addCrashSection(var9);
            throw new ReportedException(var8);
         }

         if (var5 >= 1000L) {
            if (this.debugCrashKeyReportedCount == 0L) {
               this.debugFeedbackTranslated("debug.crash.message", this.minecraft.options.keyDebugModifier.getTranslatedKeyMessage().getString(), this.minecraft.options.keyDebugCrash.getTranslatedKeyMessage().getString());
            } else {
               this.debugWarningComponent(Component.translatable("debug.crash.warning", Mth.ceil((float)var3 / 1000.0F)));
            }

            this.debugCrashKeyReportedTime = var1;
            ++this.debugCrashKeyReportedCount;
         }
      }

   }
}
