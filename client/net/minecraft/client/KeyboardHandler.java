package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.DebugOptionsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
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
import net.minecraft.server.commands.VersionCommand;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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
   private boolean handledDebugKey;

   public KeyboardHandler(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   private boolean handleChunkDebugKeys(int var1) {
      switch (var1) {
         case 69:
            if (this.minecraft.player == null) {
               return false;
            }

            boolean var2 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_SECTION_PATHS);
            this.debugFeedbackFormatted("SectionPath: {0}", var2 ? "shown" : "hidden");
            return true;
         case 70:
            boolean var4 = FogRenderer.toggleFog();
            this.debugFeedbackFormatted("Fog: {0}", var4 ? "enabled" : "disabled");
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
            this.debugFeedbackFormatted("SmartCull: {0}", this.minecraft.smartCull ? "enabled" : "disabled");
            return true;
         case 79:
            if (this.minecraft.player == null) {
               return false;
            }

            boolean var3 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_SECTION_OCTREE);
            this.debugFeedbackFormatted("Frustum culling Octree: {0}", var3 ? "enabled" : "disabled");
            return true;
         case 85:
            if (Screen.hasShiftDown()) {
               this.minecraft.levelRenderer.killFrustum();
               this.debugFeedbackFormatted("Killed frustum");
            } else {
               this.minecraft.levelRenderer.captureFrustum();
               this.debugFeedbackFormatted("Captured frustum");
            }

            return true;
         case 86:
            if (this.minecraft.player == null) {
               return false;
            }

            boolean var5 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_SECTION_VISIBILITY);
            this.debugFeedbackFormatted("SectionVisibility: {0}", var5 ? "enabled" : "disabled");
            return true;
         case 87:
            this.minecraft.wireframe = !this.minecraft.wireframe;
            this.debugFeedbackFormatted("WireFrame: {0}", this.minecraft.wireframe ? "enabled" : "disabled");
            return true;
      }
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

   private void debugFeedbackTranslated(String var1) {
      this.debugFeedbackComponent(Component.translatable(var1));
   }

   private void debugFeedbackFormatted(String var1, Object... var2) {
      this.debugFeedbackComponent(Component.literal(MessageFormat.format(var1, var2)));
   }

   private boolean handleDebugKeys(int var1) {
      if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
         return true;
      } else {
         switch (var1) {
            case 49:
               this.minecraft.getDebugOverlay().toggleProfilerChart();
               return true;
            case 50:
               this.minecraft.getDebugOverlay().toggleFpsCharts();
               return true;
            case 51:
               this.minecraft.getDebugOverlay().toggleNetworkCharts();
               return true;
            case 65:
               this.minecraft.levelRenderer.allChanged();
               this.debugFeedbackTranslated("debug.reload_chunks.message");
               return true;
            case 66:
               if (this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
                  boolean var2 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.ENTITY_HITBOXES);
                  this.debugFeedbackTranslated(var2 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
                  return true;
               }

               return false;
            case 67:
               if (this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
                  ClientPacketListener var7 = this.minecraft.player.connection;
                  if (var7 == null) {
                     return false;
                  }

                  this.debugFeedbackTranslated("debug.copy_location.message");
                  this.setClipboard(String.format(Locale.ROOT, "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f", this.minecraft.player.level().dimension().location(), this.minecraft.player.getX(), this.minecraft.player.getY(), this.minecraft.player.getZ(), this.minecraft.player.getYRot(), this.minecraft.player.getXRot()));
                  return true;
               }

               return false;
            case 68:
               if (this.minecraft.gui != null) {
                  this.minecraft.gui.getChat().clearMessages(false);
               }

               return true;
            case 71:
               if (this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
                  boolean var3 = this.minecraft.debugEntries.toggleStatus(DebugScreenEntries.CHUNK_BORDERS);
                  this.debugFeedbackTranslated(var3 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
                  return true;
               }

               return false;
            case 72:
               this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
               this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
               this.minecraft.options.save();
               return true;
            case 73:
               if (this.minecraft.player != null && !this.minecraft.player.isReducedDebugInfo()) {
                  this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
               }

               return true;
            case 76:
               if (this.minecraft.debugClientMetricsStart(this::debugFeedbackComponent)) {
                  this.debugFeedbackComponent(Component.translatable("debug.profiling.start", 10));
               }

               return true;
            case 78:
               if (this.minecraft.player != null && this.minecraft.player.hasPermissions(2)) {
                  if (!this.minecraft.player.isSpectator()) {
                     this.minecraft.player.connection.send(new ServerboundChangeGameModePacket(GameType.SPECTATOR));
                  } else {
                     GameType var8 = (GameType)MoreObjects.firstNonNull(this.minecraft.gameMode.getPreviousPlayerMode(), GameType.CREATIVE);
                     this.minecraft.player.connection.send(new ServerboundChangeGameModePacket(var8));
                  }
               } else {
                  this.debugFeedbackTranslated("debug.creative_spectator.error");
               }

               return true;
            case 80:
               this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
               this.minecraft.options.save();
               this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
               return true;
            case 81:
               this.debugFeedbackTranslated("debug.help.message");
               this.showDebugChat(Component.translatable("debug.reload_chunks.help"));
               this.showDebugChat(Component.translatable("debug.show_hitboxes.help"));
               this.showDebugChat(Component.translatable("debug.copy_location.help"));
               this.showDebugChat(Component.translatable("debug.clear_chat.help"));
               this.showDebugChat(Component.translatable("debug.chunk_boundaries.help"));
               this.showDebugChat(Component.translatable("debug.advanced_tooltips.help"));
               this.showDebugChat(Component.translatable("debug.inspect.help"));
               this.showDebugChat(Component.translatable("debug.profiling.help"));
               this.showDebugChat(Component.translatable("debug.creative_spectator.help"));
               this.showDebugChat(Component.translatable("debug.pause_focus.help"));
               this.showDebugChat(Component.translatable("debug.help.help"));
               this.showDebugChat(Component.translatable("debug.dump_dynamic_textures.help"));
               this.showDebugChat(Component.translatable("debug.reload_resourcepacks.help"));
               this.showDebugChat(Component.translatable("debug.version.help"));
               this.showDebugChat(Component.translatable("debug.pause.help"));
               this.showDebugChat(Component.translatable("debug.gamemodes.help"));
               this.showDebugChat(Component.translatable("debug.options.help"));
               return true;
            case 83:
               Path var4 = this.minecraft.gameDirectory.toPath().toAbsolutePath();
               Path var5 = TextureUtil.getDebugTexturePath(var4);
               this.minecraft.getTextureManager().dumpAllSheets(var5);
               MutableComponent var6 = Component.literal(var4.relativize(var5).toString()).withStyle(ChatFormatting.UNDERLINE).withStyle((UnaryOperator)((var1x) -> var1x.withClickEvent(new ClickEvent.OpenFile(var5))));
               this.debugFeedbackComponent(Component.translatable("debug.dump_dynamic_textures", var6));
               return true;
            case 84:
               this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
               this.minecraft.reloadResourcePacks();
               return true;
            case 86:
               this.debugFeedbackTranslated("debug.version.header");
               VersionCommand.dumpVersion(this::showDebugChat);
               return true;
            case 293:
               if (this.minecraft.player != null && this.minecraft.player.hasPermissions(2)) {
                  this.minecraft.setScreen(new GameModeSwitcherScreen());
               } else {
                  this.debugFeedbackTranslated("debug.gamemodes.error");
               }

               return true;
            case 294:
               if (this.minecraft.screen instanceof DebugOptionsScreen) {
                  this.minecraft.screen.onClose();
               } else {
                  this.minecraft.setScreen(new DebugOptionsScreen());
               }

               return true;
            default:
               return false;
         }
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

   public void keyPress(long var1, int var3, int var4, int var5, int var6) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
         this.minecraft.getFramerateLimitTracker().onInputReceived();
         boolean var7 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
         if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) || !var7) {
               this.debugCrashKeyTime = -1L;
            }
         } else if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67) && var7) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
         }

         Screen var8 = this.minecraft.screen;
         if (var8 != null) {
            switch (var3) {
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

         if (var5 == 1 && (!(this.minecraft.screen instanceof KeyBindsScreen) || ((KeyBindsScreen)var8).lastKeySelection <= Util.getMillis() - 20L)) {
            if (this.minecraft.options.keyFullscreen.matches(var3, var4)) {
               this.minecraft.getWindow().toggleFullScreen();
               boolean var17 = this.minecraft.getWindow().isFullscreen();
               this.minecraft.options.fullscreen().set(var17);
               this.minecraft.options.save();
               Screen var23 = this.minecraft.screen;
               if (var23 instanceof VideoSettingsScreen) {
                  VideoSettingsScreen var21 = (VideoSettingsScreen)var23;
                  var21.updateFullscreenButton(var17);
               }

               return;
            }

            if (this.minecraft.options.keyScreenshot.matches(var3, var4)) {
               if (Screen.hasControlDown()) {
               }

               Screenshot.grab(this.minecraft.gameDirectory, this.minecraft.getMainRenderTarget(), (var1x) -> this.minecraft.execute(() -> this.showDebugChat(var1x)));
               return;
            }
         }

         if (var5 != 0) {
            boolean var9 = var8 == null || !(var8.getFocused() instanceof EditBox) || !((EditBox)var8.getFocused()).canConsumeInput();
            if (var9) {
               if (Screen.hasControlDown() && var3 == 66 && this.minecraft.getNarrator().isActive() && (Boolean)this.minecraft.options.narratorHotkey().get()) {
                  boolean var10 = this.minecraft.options.narrator().get() == NarratorStatus.OFF;
                  this.minecraft.options.narrator().set(NarratorStatus.byId(((NarratorStatus)this.minecraft.options.narrator().get()).getId() + 1));
                  this.minecraft.options.save();
                  if (var8 != null) {
                     var8.updateNarratorStatus(var10);
                  }
               }

               LocalPlayer var18 = this.minecraft.player;
            }
         }

         if (var8 != null) {
            try {
               if (var5 != 1 && var5 != 2) {
                  if (var5 == 0 && var8.keyReleased(var3, var4, var6)) {
                     return;
                  }
               } else {
                  var8.afterKeyboardAction();
                  if (var8.keyPressed(var3, var4, var6)) {
                     if (this.minecraft.screen == null) {
                        InputConstants.Key var15 = InputConstants.getKey(var3, var4);
                        KeyMapping.set(var15, false);
                     }

                     return;
                  }
               }
            } catch (Throwable var14) {
               CrashReport var19 = CrashReport.forThrowable(var14, "keyPressed event handler");
               var8.fillCrashDetails(var19);
               CrashReportCategory var11 = var19.addCategory("Key");
               var11.setDetail("Key", var3);
               var11.setDetail("Scancode", var4);
               var11.setDetail("Mods", var6);
               throw new ReportedException(var19);
            }
         }

         InputConstants.Key var16;
         boolean var20;
         boolean var10000;
         label161: {
            var16 = InputConstants.getKey(var3, var4);
            var20 = this.minecraft.screen == null;
            if (!var20) {
               label159: {
                  Screen var13 = this.minecraft.screen;
                  if (var13 instanceof PauseScreen) {
                     PauseScreen var12 = (PauseScreen)var13;
                     if (!var12.showsPauseMenu()) {
                        break label159;
                     }
                  }

                  if (!(this.minecraft.screen instanceof GameModeSwitcherScreen)) {
                     var10000 = false;
                     break label161;
                  }
               }
            }

            var10000 = true;
         }

         boolean var22 = var10000;
         if (var5 == 0) {
            KeyMapping.set(var16, false);
            if (var3 == 292) {
               if (this.handledDebugKey) {
                  this.handledDebugKey = false;
               } else {
                  this.minecraft.debugEntries.toggleF3Visible();
               }
            }

         } else {
            boolean var24 = false;
            if (var22 && var3 == 256) {
               this.minecraft.pauseGame(var7);
               var24 = var7;
            } else if (var7) {
               var24 = this.handleDebugKeys(var3);
            } else if (var22 && var3 == 290) {
               this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
            } else if (var22 && var3 == 293) {
               this.minecraft.gameRenderer.togglePostEffect();
            }

            this.handledDebugKey |= var24;
            if (this.minecraft.getDebugOverlay().showProfilerChart() && !var7 && var3 >= 48 && var3 <= 57) {
               this.minecraft.getDebugOverlay().getProfilerPieChart().profilerPieChartKeyPress(var3 - 48);
            }

            if (var20) {
               if (var24) {
                  KeyMapping.set(var16, false);
               } else {
                  KeyMapping.set(var16, true);
                  KeyMapping.click(var16);
               }
            }

         }
      }
   }

   private void charTyped(long var1, int var3, int var4) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
         Screen var5 = this.minecraft.screen;
         if (var5 != null && this.minecraft.getOverlay() == null) {
            try {
               if (Character.isBmpCodePoint(var3)) {
                  var5.charTyped((char)var3, var4);
               } else if (Character.isValidCodePoint(var3)) {
                  var5.charTyped(Character.highSurrogate(var3), var4);
                  var5.charTyped(Character.lowSurrogate(var3), var4);
               }

            } catch (Throwable var9) {
               CrashReport var7 = CrashReport.forThrowable(var9, "charTyped event handler");
               var5.fillCrashDetails(var7);
               CrashReportCategory var8 = var7.addCategory("Key");
               var8.setDetail("Codepoint", var3);
               var8.setDetail("Mods", var4);
               throw new ReportedException(var7);
            }
         }
      }
   }

   public void setup(long var1) {
      InputConstants.setupKeyboardCallbacks(var1, (var1x, var3, var4, var5, var6) -> this.minecraft.execute(() -> this.keyPress(var1x, var3, var4, var5, var6)), (var1x, var3, var4) -> this.minecraft.execute(() -> this.charTyped(var1x, var3, var4)));
   }

   public String getClipboard() {
      return this.clipboardManager.getClipboard(this.minecraft.getWindow().getWindow(), (var1, var2) -> {
         if (var1 != 65545) {
            this.minecraft.getWindow().defaultErrorCallback(var1, var2);
         }

      });
   }

   public void setClipboard(String var1) {
      if (!var1.isEmpty()) {
         this.clipboardManager.setClipboard(this.minecraft.getWindow().getWindow(), var1);
      }

   }

   public void tick() {
      if (this.debugCrashKeyTime > 0L) {
         long var1 = Util.getMillis();
         long var3 = 10000L - (var1 - this.debugCrashKeyTime);
         long var5 = var1 - this.debugCrashKeyReportedTime;
         if (var3 < 0L) {
            if (Screen.hasControlDown()) {
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
               this.debugFeedbackTranslated("debug.crash.message");
            } else {
               this.debugWarningComponent(Component.translatable("debug.crash.warning", Mth.ceil((float)var3 / 1000.0F)));
            }

            this.debugCrashKeyReportedTime = var1;
            ++this.debugCrashKeyReportedCount;
         }
      }

   }
}
