package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.TextureUtil;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class KeyboardHandler {
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
            this.minecraft.sectionPath = !this.minecraft.sectionPath;
            this.debugFeedback("SectionPath: {0}", this.minecraft.sectionPath ? "shown" : "hidden");
            return true;
         case 76:
            this.minecraft.smartCull = !this.minecraft.smartCull;
            this.debugFeedback("SmartCull: {0}", this.minecraft.smartCull ? "enabled" : "disabled");
            return true;
         case 85:
            if (Screen.hasShiftDown()) {
               this.minecraft.levelRenderer.killFrustum();
               this.debugFeedback("Killed frustum");
            } else {
               this.minecraft.levelRenderer.captureFrustum();
               this.debugFeedback("Captured frustum");
            }

            return true;
         case 86:
            this.minecraft.sectionVisibility = !this.minecraft.sectionVisibility;
            this.debugFeedback("SectionVisibility: {0}", this.minecraft.sectionVisibility ? "enabled" : "disabled");
            return true;
         case 87:
            this.minecraft.wireframe = !this.minecraft.wireframe;
            this.debugFeedback("WireFrame: {0}", this.minecraft.wireframe ? "enabled" : "disabled");
            return true;
         default:
            return false;
      }
   }

   private void debugComponent(ChatFormatting var1, Component var2) {
      this.minecraft
         .gui
         .getChat()
         .addMessage(
            Component.empty().append(Component.translatable("debug.prefix").withStyle(var1, ChatFormatting.BOLD)).append(CommonComponents.SPACE).append(var2)
         );
   }

   private void debugFeedbackComponent(Component var1) {
      this.debugComponent(ChatFormatting.YELLOW, var1);
   }

   private void debugFeedbackTranslated(String var1, Object... var2) {
      this.debugFeedbackComponent(Component.translatableEscape(var1, var2));
   }

   private void debugWarningTranslated(String var1, Object... var2) {
      this.debugComponent(ChatFormatting.RED, Component.translatableEscape(var1, var2));
   }

   private void debugFeedback(String var1, Object... var2) {
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
               boolean var2 = !this.minecraft.getEntityRenderDispatcher().shouldRenderHitBoxes();
               this.minecraft.getEntityRenderDispatcher().setRenderHitBoxes(var2);
               this.debugFeedbackTranslated(var2 ? "debug.show_hitboxes.on" : "debug.show_hitboxes.off");
               return true;
            case 67:
               if (this.minecraft.player.isReducedDebugInfo()) {
                  return false;
               } else {
                  ClientPacketListener var8 = this.minecraft.player.connection;
                  if (var8 == null) {
                     return false;
                  }

                  this.debugFeedbackTranslated("debug.copy_location.message");
                  this.setClipboard(
                     String.format(
                        Locale.ROOT,
                        "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
                        this.minecraft.player.level().dimension().location(),
                        this.minecraft.player.getX(),
                        this.minecraft.player.getY(),
                        this.minecraft.player.getZ(),
                        this.minecraft.player.getYRot(),
                        this.minecraft.player.getXRot()
                     )
                  );
                  return true;
               }
            case 68:
               if (this.minecraft.gui != null) {
                  this.minecraft.gui.getChat().clearMessages(false);
               }

               return true;
            case 71:
               boolean var3 = this.minecraft.debugRenderer.switchRenderChunkborder();
               this.debugFeedbackTranslated(var3 ? "debug.chunk_boundaries.on" : "debug.chunk_boundaries.off");
               return true;
            case 72:
               this.minecraft.options.advancedItemTooltips = !this.minecraft.options.advancedItemTooltips;
               this.debugFeedbackTranslated(this.minecraft.options.advancedItemTooltips ? "debug.advanced_tooltips.on" : "debug.advanced_tooltips.off");
               this.minecraft.options.save();
               return true;
            case 73:
               if (!this.minecraft.player.isReducedDebugInfo()) {
                  this.copyRecreateCommand(this.minecraft.player.hasPermissions(2), !Screen.hasShiftDown());
               }

               return true;
            case 76:
               if (this.minecraft.debugClientMetricsStart(this::debugFeedbackComponent)) {
                  this.debugFeedbackTranslated("debug.profiling.start", 10);
               }

               return true;
            case 78:
               if (!this.minecraft.player.hasPermissions(2)) {
                  this.debugFeedbackTranslated("debug.creative_spectator.error");
               } else if (!this.minecraft.player.isSpectator()) {
                  this.minecraft.player.connection.sendUnsignedCommand("gamemode spectator");
               } else {
                  this.minecraft
                     .player
                     .connection
                     .sendUnsignedCommand(
                        "gamemode " + ((GameType)MoreObjects.firstNonNull(this.minecraft.gameMode.getPreviousPlayerMode(), GameType.CREATIVE)).getName()
                     );
               }

               return true;
            case 80:
               this.minecraft.options.pauseOnLostFocus = !this.minecraft.options.pauseOnLostFocus;
               this.minecraft.options.save();
               this.debugFeedbackTranslated(this.minecraft.options.pauseOnLostFocus ? "debug.pause_focus.on" : "debug.pause_focus.off");
               return true;
            case 81:
               this.debugFeedbackTranslated("debug.help.message");
               ChatComponent var4 = this.minecraft.gui.getChat();
               var4.addMessage(Component.translatable("debug.reload_chunks.help"));
               var4.addMessage(Component.translatable("debug.show_hitboxes.help"));
               var4.addMessage(Component.translatable("debug.copy_location.help"));
               var4.addMessage(Component.translatable("debug.clear_chat.help"));
               var4.addMessage(Component.translatable("debug.chunk_boundaries.help"));
               var4.addMessage(Component.translatable("debug.advanced_tooltips.help"));
               var4.addMessage(Component.translatable("debug.inspect.help"));
               var4.addMessage(Component.translatable("debug.profiling.help"));
               var4.addMessage(Component.translatable("debug.creative_spectator.help"));
               var4.addMessage(Component.translatable("debug.pause_focus.help"));
               var4.addMessage(Component.translatable("debug.help.help"));
               var4.addMessage(Component.translatable("debug.dump_dynamic_textures.help"));
               var4.addMessage(Component.translatable("debug.reload_resourcepacks.help"));
               var4.addMessage(Component.translatable("debug.pause.help"));
               var4.addMessage(Component.translatable("debug.gamemodes.help"));
               return true;
            case 83:
               Path var5 = this.minecraft.gameDirectory.toPath().toAbsolutePath();
               Path var6 = TextureUtil.getDebugTexturePath(var5);
               this.minecraft.getTextureManager().dumpAllSheets(var6);
               MutableComponent var7 = Component.literal(var5.relativize(var6).toString())
                  .withStyle(ChatFormatting.UNDERLINE)
                  .withStyle(var1x -> var1x.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, var6.toFile().toString())));
               this.debugFeedbackTranslated("debug.dump_dynamic_textures", var7);
               return true;
            case 84:
               this.debugFeedbackTranslated("debug.reload_resourcepacks.message");
               this.minecraft.reloadResourcePacks();
               return true;
            case 293:
               if (!this.minecraft.player.hasPermissions(2)) {
                  this.debugFeedbackTranslated("debug.gamemodes.error");
               } else {
                  this.minecraft.setScreen(new GameModeSwitcherScreen());
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
               BlockPos var9 = ((BlockHitResult)var3).getBlockPos();
               Level var10 = this.minecraft.player.level();
               BlockState var11 = var10.getBlockState(var9);
               if (var1) {
                  if (var2) {
                     this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(var9, var3x -> {
                        this.copyCreateBlockCommand(var11, var9, var3x);
                        this.debugFeedbackTranslated("debug.inspect.server.block");
                     });
                  } else {
                     BlockEntity var7 = var10.getBlockEntity(var9);
                     CompoundTag var8 = var7 != null ? var7.saveWithoutMetadata(var10.registryAccess()) : null;
                     this.copyCreateBlockCommand(var11, var9, var8);
                     this.debugFeedbackTranslated("debug.inspect.client.block");
                  }
               } else {
                  this.copyCreateBlockCommand(var11, var9, null);
                  this.debugFeedbackTranslated("debug.inspect.client.block");
               }
               break;
            case ENTITY:
               Entity var4 = ((EntityHitResult)var3).getEntity();
               ResourceLocation var5 = BuiltInRegistries.ENTITY_TYPE.getKey(var4.getType());
               if (var1) {
                  if (var2) {
                     this.minecraft.player.connection.getDebugQueryHandler().queryEntityTag(var4.getId(), var3x -> {
                        this.copyCreateEntityCommand(var5, var4.position(), var3x);
                        this.debugFeedbackTranslated("debug.inspect.server.entity");
                     });
                  } else {
                     CompoundTag var6 = var4.saveWithoutId(new CompoundTag());
                     this.copyCreateEntityCommand(var5, var4.position(), var6);
                     this.debugFeedbackTranslated("debug.inspect.client.entity");
                  }
               } else {
                  this.copyCreateEntityCommand(var5, var4.position(), null);
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
         var3.remove("Dimension");
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
               this.minecraft.options.fullscreen().set(this.minecraft.getWindow().isFullscreen());
               return;
            }

            if (this.minecraft.options.keyScreenshot.matches(var3, var4)) {
               if (Screen.hasControlDown()) {
               }

               Screenshot.grab(
                  this.minecraft.gameDirectory,
                  this.minecraft.getMainRenderTarget(),
                  var1x -> this.minecraft.execute(() -> this.minecraft.gui.getChat().addMessage(var1x))
               );
               return;
            }
         }

         if (var5 != 0) {
            boolean var9 = var8 == null || !(var8.getFocused() instanceof EditBox) || !((EditBox)var8.getFocused()).canConsumeInput();
            if (var9) {
               if (Screen.hasControlDown() && var3 == 66 && this.minecraft.getNarrator().isActive() && this.minecraft.options.narratorHotkey().get()) {
                  boolean var10 = this.minecraft.options.narrator().get() == NarratorStatus.OFF;
                  this.minecraft.options.narrator().set(NarratorStatus.byId(this.minecraft.options.narrator().get().getId() + 1));
                  this.minecraft.options.save();
                  if (var8 != null) {
                     var8.updateNarratorStatus(var10);
                  }
               }

               LocalPlayer var16 = this.minecraft.player;
            }
         }

         if (var8 != null) {
            boolean[] var14 = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if (var5 == 1 || var5 == 2) {
                  var8.afterKeyboardAction();
                  var14[0] = var8.keyPressed(var3, var4, var6);
               } else if (var5 == 0) {
                  var14[0] = var8.keyReleased(var3, var4, var6);
               }
            }, "keyPressed event handler", var8.getClass().getCanonicalName());
            if (var14[0]) {
               return;
            }
         }

         InputConstants.Key var15;
         boolean var17;
         boolean var10000;
         label180: {
            var15 = InputConstants.getKey(var3, var4);
            var17 = this.minecraft.screen == null;
            label141:
            if (!var17) {
               if (this.minecraft.screen instanceof PauseScreen var12 && !var12.showsPauseMenu()) {
                  break label141;
               }

               var10000 = false;
               break label180;
            }

            var10000 = true;
         }

         boolean var11 = var10000;
         if (var5 == 0) {
            KeyMapping.set(var15, false);
            if (var11 && var3 == 292) {
               if (this.handledDebugKey) {
                  this.handledDebugKey = false;
               } else {
                  this.minecraft.getDebugOverlay().toggleOverlay();
               }
            }
         } else {
            boolean var18 = false;
            if (var11) {
               if (var3 == 293 && this.minecraft.gameRenderer != null) {
                  this.minecraft.gameRenderer.togglePostEffect();
               }

               if (var3 == 256) {
                  this.minecraft.pauseGame(var7);
                  var18 |= var7;
               }

               var18 |= var7 && this.handleDebugKeys(var3);
               this.handledDebugKey |= var18;
               if (var3 == 290) {
                  this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
               }

               if (this.minecraft.getDebugOverlay().showProfilerChart() && !var7 && var3 >= 48 && var3 <= 57) {
                  this.minecraft.getDebugOverlay().getProfilerPieChart().profilerPieChartKeyPress(var3 - 48);
               }
            }

            if (var17) {
               if (var18) {
                  KeyMapping.set(var15, false);
               } else {
                  KeyMapping.set(var15, true);
                  KeyMapping.click(var15);
               }
            }
         }
      }
   }

   private void charTyped(long var1, int var3, int var4) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
         Screen var5 = this.minecraft.screen;
         if (var5 != null && this.minecraft.getOverlay() == null) {
            if (Character.charCount(var3) == 1) {
               Screen.wrapScreenError(() -> var5.charTyped((char)var3, var4), "charTyped event handler", var5.getClass().getCanonicalName());
            } else {
               for (char var9 : Character.toChars(var3)) {
                  Screen.wrapScreenError(() -> var5.charTyped(var9, var4), "charTyped event handler", var5.getClass().getCanonicalName());
               }
            }
         }
      }
   }

   public void setup(long var1) {
      InputConstants.setupKeyboardCallbacks(
         var1,
         (var1x, var3, var4, var5, var6) -> this.minecraft.execute(() -> this.keyPress(var1x, var3, var4, var5, var6)),
         (var1x, var3, var4) -> this.minecraft.execute(() -> this.charTyped(var1x, var3, var4))
      );
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
               this.debugWarningTranslated("debug.crash.warning", Mth.ceil((float)var3 / 1000.0F));
            }

            this.debugCrashKeyReportedTime = var1;
            this.debugCrashKeyReportedCount++;
         }
      }
   }
}
