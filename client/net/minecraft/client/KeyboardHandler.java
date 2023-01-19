package net.minecraft.client;

import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.ClipboardManager;
import com.mojang.blaze3d.platform.InputConstants;
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
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.SimpleOptionsSubScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.NativeModuleLister;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.GameType;
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
      switch(var1) {
         case 69:
            this.minecraft.chunkPath = !this.minecraft.chunkPath;
            this.debugFeedback("ChunkPath: {0}", this.minecraft.chunkPath ? "shown" : "hidden");
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
            this.minecraft.chunkVisibility = !this.minecraft.chunkVisibility;
            this.debugFeedback("ChunkVisibility: {0}", this.minecraft.chunkVisibility ? "enabled" : "disabled");
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
      this.debugFeedbackComponent(Component.translatable(var1, var2));
   }

   private void debugWarningTranslated(String var1, Object... var2) {
      this.debugComponent(ChatFormatting.RED, Component.translatable(var1, var2));
   }

   private void debugFeedback(String var1, Object... var2) {
      this.debugFeedbackComponent(Component.literal(MessageFormat.format(var1, var2)));
   }

   private boolean handleDebugKeys(int var1) {
      if (this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L) {
         return true;
      } else {
         switch(var1) {
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
                  ClientPacketListener var5 = this.minecraft.player.connection;
                  if (var5 == null) {
                     return false;
                  }

                  this.debugFeedbackTranslated("debug.copy_location.message");
                  this.setClipboard(
                     String.format(
                        Locale.ROOT,
                        "/execute in %s run tp @s %.2f %.2f %.2f %.2f %.2f",
                        this.minecraft.player.level.dimension().location(),
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
               var4.addMessage(Component.translatable("debug.reload_resourcepacks.help"));
               var4.addMessage(Component.translatable("debug.pause.help"));
               var4.addMessage(Component.translatable("debug.gamemodes.help"));
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
         switch(var3.getType()) {
            case BLOCK:
               BlockPos var8 = ((BlockHitResult)var3).getBlockPos();
               BlockState var9 = this.minecraft.player.level.getBlockState(var8);
               if (var1) {
                  if (var2) {
                     this.minecraft.player.connection.getDebugQueryHandler().queryBlockEntityTag(var8, var3x -> {
                        this.copyCreateBlockCommand(var9, var8, var3x);
                        this.debugFeedbackTranslated("debug.inspect.server.block");
                     });
                  } else {
                     BlockEntity var10 = this.minecraft.player.level.getBlockEntity(var8);
                     CompoundTag var7 = var10 != null ? var10.saveWithoutMetadata() : null;
                     this.copyCreateBlockCommand(var9, var8, var7);
                     this.debugFeedbackTranslated("debug.inspect.client.block");
                  }
               } else {
                  this.copyCreateBlockCommand(var9, var8, null);
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
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", var1.toString(), var2.x, var2.y, var2.z, var5);
      } else {
         var4 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", var1.toString(), var2.x, var2.y, var2.z);
      }

      this.setClipboard(var4);
   }

   public void keyPress(long var1, int var3, int var4, int var5, int var6) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
         if (this.debugCrashKeyTime > 0L) {
            if (!InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67)
               || !InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
               this.debugCrashKeyTime = -1L;
            }
         } else if (InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 67)
            && InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292)) {
            this.handledDebugKey = true;
            this.debugCrashKeyTime = Util.getMillis();
            this.debugCrashKeyReportedTime = Util.getMillis();
            this.debugCrashKeyReportedCount = 0L;
         }

         this.minecraft.setLastInputType(var3 == 258 ? InputType.KEYBOARD_TAB : InputType.KEYBOARD_OTHER);
         Screen var7 = this.minecraft.screen;
         if (var5 == 1 && (!(this.minecraft.screen instanceof KeyBindsScreen) || ((KeyBindsScreen)var7).lastKeySelection <= Util.getMillis() - 20L)) {
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

         if (this.minecraft.getNarrator().isActive()) {
            boolean var8 = var7 == null || !(var7.getFocused() instanceof EditBox) || !((EditBox)var7.getFocused()).canConsumeInput();
            if (var5 != 0 && var3 == 66 && Screen.hasControlDown() && var8) {
               boolean var9 = this.minecraft.options.narrator().get() == NarratorStatus.OFF;
               this.minecraft.options.narrator().set(NarratorStatus.byId(this.minecraft.options.narrator().get().getId() + 1));
               if (var7 instanceof SimpleOptionsSubScreen) {
                  ((SimpleOptionsSubScreen)var7).updateNarratorButton();
               }

               if (var9 && var7 != null) {
                  var7.narrationEnabled();
               }
            }
         }

         if (var7 != null) {
            boolean[] var11 = new boolean[]{false};
            Screen.wrapScreenError(() -> {
               if (var5 == 1 || var5 == 2) {
                  var7.afterKeyboardAction();
                  var11[0] = var7.keyPressed(var3, var4, var6);
               } else if (var5 == 0) {
                  var11[0] = var7.keyReleased(var3, var4, var6);
               }
            }, "keyPressed event handler", var7.getClass().getCanonicalName());
            if (var11[0]) {
               return;
            }
         }

         if (this.minecraft.screen == null || this.minecraft.screen.passEvents) {
            InputConstants.Key var12 = InputConstants.getKey(var3, var4);
            if (var5 == 0) {
               KeyMapping.set(var12, false);
               if (var3 == 292) {
                  if (this.handledDebugKey) {
                     this.handledDebugKey = false;
                  } else {
                     this.minecraft.options.renderDebug = !this.minecraft.options.renderDebug;
                     this.minecraft.options.renderDebugCharts = this.minecraft.options.renderDebug && Screen.hasShiftDown();
                     this.minecraft.options.renderFpsChart = this.minecraft.options.renderDebug && Screen.hasAltDown();
                  }
               }
            } else {
               if (var3 == 293 && this.minecraft.gameRenderer != null) {
                  this.minecraft.gameRenderer.togglePostEffect();
               }

               boolean var13 = false;
               if (this.minecraft.screen == null) {
                  if (var3 == 256) {
                     boolean var10 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292);
                     this.minecraft.pauseGame(var10);
                  }

                  var13 = InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 292) && this.handleDebugKeys(var3);
                  this.handledDebugKey |= var13;
                  if (var3 == 290) {
                     this.minecraft.options.hideGui = !this.minecraft.options.hideGui;
                  }
               }

               if (var13) {
                  KeyMapping.set(var12, false);
               } else {
                  KeyMapping.set(var12, true);
                  KeyMapping.click(var12);
               }

               if (this.minecraft.options.renderDebugCharts && var3 >= 48 && var3 <= 57) {
                  this.minecraft.debugFpsMeterKeyPress(var3 - 48);
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
               for(char var9 : Character.toChars(var3)) {
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
            ++this.debugCrashKeyReportedCount;
         }
      }
   }
}
