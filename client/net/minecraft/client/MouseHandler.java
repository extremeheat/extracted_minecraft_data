package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

public class MouseHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Minecraft minecraft;
   private boolean isLeftPressed;
   private boolean isMiddlePressed;
   private boolean isRightPressed;
   private double xpos;
   private double ypos;
   private int fakeRightMouse;
   private int activeButton = -1;
   private boolean ignoreFirstMove = true;
   private int clickDepth;
   private double mousePressedTime;
   private final SmoothDouble smoothTurnX = new SmoothDouble();
   private final SmoothDouble smoothTurnY = new SmoothDouble();
   private double accumulatedDX;
   private double accumulatedDY;
   private final ScrollWheelHandler scrollWheelHandler;
   private double lastHandleMovementTime = 4.9E-324;
   private boolean mouseGrabbed;

   public MouseHandler(Minecraft var1) {
      super();
      this.minecraft = var1;
      this.scrollWheelHandler = new ScrollWheelHandler();
   }

   private void onPress(long var1, int var3, int var4, int var5) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
         this.minecraft.getFramerateLimitTracker().onInputReceived();
         if (this.minecraft.screen != null) {
            this.minecraft.setLastInputType(InputType.MOUSE);
         }

         boolean var6 = var4 == 1;
         if (Minecraft.ON_OSX && var3 == 0) {
            if (var6) {
               if ((var5 & 2) == 2) {
                  var3 = 1;
                  ++this.fakeRightMouse;
               }
            } else if (this.fakeRightMouse > 0) {
               var3 = 1;
               --this.fakeRightMouse;
            }
         }

         int var7 = var3;
         if (var6) {
            if ((Boolean)this.minecraft.options.touchscreen().get() && this.clickDepth++ > 0) {
               return;
            }

            this.activeButton = var3;
            this.mousePressedTime = Blaze3D.getTime();
         } else if (this.activeButton != -1) {
            if ((Boolean)this.minecraft.options.touchscreen().get() && --this.clickDepth > 0) {
               return;
            }

            this.activeButton = -1;
         }

         if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen == null) {
               if (!this.mouseGrabbed && var6) {
                  this.grabMouse();
               }
            } else {
               double var8 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var10 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               Screen var12 = this.minecraft.screen;
               CrashReport var14;
               CrashReportCategory var15;
               if (var6) {
                  var12.afterMouseAction();

                  try {
                     if (var12.mouseClicked(var8, var10, var7)) {
                        return;
                     }
                  } catch (Throwable var17) {
                     var14 = CrashReport.forThrowable(var17, "mouseClicked event handler");
                     var12.fillCrashDetails(var14);
                     var15 = var14.addCategory("Mouse");
                     var15.setDetail("Scaled X", (Object)var8);
                     var15.setDetail("Scaled Y", (Object)var10);
                     var15.setDetail("Button", (Object)var3);
                     throw new ReportedException(var14);
                  }
               } else {
                  try {
                     if (var12.mouseReleased(var8, var10, var7)) {
                        return;
                     }
                  } catch (Throwable var16) {
                     var14 = CrashReport.forThrowable(var16, "mouseReleased event handler");
                     var12.fillCrashDetails(var14);
                     var15 = var14.addCategory("Mouse");
                     var15.setDetail("Scaled X", (Object)var8);
                     var15.setDetail("Scaled Y", (Object)var10);
                     var15.setDetail("Button", (Object)var3);
                     throw new ReportedException(var14);
                  }
               }
            }
         }

         if (this.minecraft.screen == null && this.minecraft.getOverlay() == null) {
            if (var3 == 0) {
               this.isLeftPressed = var6;
            } else if (var3 == 2) {
               this.isMiddlePressed = var6;
            } else if (var3 == 1) {
               this.isRightPressed = var6;
            }

            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(var3), var6);
            if (var6) {
               if (this.minecraft.player.isSpectator() && var3 == 2) {
                  this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
               } else {
                  KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(var3));
               }
            }
         }

      }
   }

   private void onScroll(long var1, double var3, double var5) {
      if (var1 == Minecraft.getInstance().getWindow().getWindow()) {
         this.minecraft.getFramerateLimitTracker().onInputReceived();
         boolean var7 = (Boolean)this.minecraft.options.discreteMouseScroll().get();
         double var8 = (Double)this.minecraft.options.mouseWheelSensitivity().get();
         double var10 = (var7 ? Math.signum(var3) : var3) * var8;
         double var12 = (var7 ? Math.signum(var5) : var5) * var8;
         if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen != null) {
               double var14 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var16 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               this.minecraft.screen.mouseScrolled(var14, var16, var10, var12);
               this.minecraft.screen.afterMouseAction();
            } else if (this.minecraft.player != null) {
               Vector2i var18 = this.scrollWheelHandler.onMouseScroll(var10, var12);
               if (var18.x == 0 && var18.y == 0) {
                  return;
               }

               int var15 = var18.y == 0 ? -var18.x : var18.y;
               if (this.minecraft.player.isSpectator()) {
                  if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                     this.minecraft.gui.getSpectatorGui().onMouseScrolled(-var15);
                  } else {
                     float var19 = Mth.clamp(this.minecraft.player.getAbilities().getFlyingSpeed() + (float)var18.y * 0.005F, 0.0F, 0.2F);
                     this.minecraft.player.getAbilities().setFlyingSpeed(var19);
                  }
               } else {
                  Inventory var20 = this.minecraft.player.getInventory();
                  var20.setSelectedHotbarSlot(ScrollWheelHandler.getNextScrollWheelSelection((double)var15, var20.selected, Inventory.getSelectionSize()));
               }
            }
         }
      }

   }

   private void onDrop(long var1, List<Path> var3, int var4) {
      this.minecraft.getFramerateLimitTracker().onInputReceived();
      if (this.minecraft.screen != null) {
         this.minecraft.screen.onFilesDrop(var3);
      }

      if (var4 > 0) {
         SystemToast.onFileDropFailure(this.minecraft, var4);
      }

   }

   public void setup(long var1) {
      InputConstants.setupMouseCallbacks(var1, (var1x, var3, var5) -> {
         this.minecraft.execute(() -> {
            this.onMove(var1x, var3, var5);
         });
      }, (var1x, var3, var4, var5) -> {
         this.minecraft.execute(() -> {
            this.onPress(var1x, var3, var4, var5);
         });
      }, (var1x, var3, var5) -> {
         this.minecraft.execute(() -> {
            this.onScroll(var1x, var3, var5);
         });
      }, (var1x, var3, var4) -> {
         ArrayList var6 = new ArrayList(var3);
         int var7 = 0;

         for(int var8 = 0; var8 < var3; ++var8) {
            String var9 = GLFWDropCallback.getName(var4, var8);

            try {
               var6.add(Paths.get(var9));
            } catch (InvalidPathException var11) {
               ++var7;
               LOGGER.error("Failed to parse path '{}'", var9, var11);
            }
         }

         if (!var6.isEmpty()) {
            this.minecraft.execute(() -> {
               this.onDrop(var1x, var6, var7);
            });
         }

      });
   }

   private void onMove(long var1, double var3, double var5) {
      if (var1 == Minecraft.getInstance().getWindow().getWindow()) {
         if (this.ignoreFirstMove) {
            this.xpos = var3;
            this.ypos = var5;
            this.ignoreFirstMove = false;
         } else {
            if (this.minecraft.isWindowActive()) {
               this.accumulatedDX += var3 - this.xpos;
               this.accumulatedDY += var5 - this.ypos;
            }

            this.xpos = var3;
            this.ypos = var5;
         }
      }
   }

   public void handleAccumulatedMovement() {
      double var1 = Blaze3D.getTime();
      double var3 = var1 - this.lastHandleMovementTime;
      this.lastHandleMovementTime = var1;
      if (this.minecraft.isWindowActive()) {
         Screen var5 = this.minecraft.screen;
         boolean var6 = this.accumulatedDX != 0.0 || this.accumulatedDY != 0.0;
         if (var6) {
            this.minecraft.getFramerateLimitTracker().onInputReceived();
         }

         if (var5 != null && this.minecraft.getOverlay() == null && var6) {
            double var7 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double var9 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();

            try {
               var5.mouseMoved(var7, var9);
            } catch (Throwable var19) {
               CrashReport var12 = CrashReport.forThrowable(var19, "mouseMoved event handler");
               var5.fillCrashDetails(var12);
               CrashReportCategory var13 = var12.addCategory("Mouse");
               var13.setDetail("Scaled X", (Object)var7);
               var13.setDetail("Scaled Y", (Object)var9);
               throw new ReportedException(var12);
            }

            if (this.activeButton != -1 && this.mousePressedTime > 0.0) {
               double var11 = this.accumulatedDX * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var20 = this.accumulatedDY * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();

               try {
                  var5.mouseDragged(var7, var9, this.activeButton, var11, var20);
               } catch (Throwable var18) {
                  CrashReport var16 = CrashReport.forThrowable(var18, "mouseDragged event handler");
                  var5.fillCrashDetails(var16);
                  CrashReportCategory var17 = var16.addCategory("Mouse");
                  var17.setDetail("Scaled X", (Object)var7);
                  var17.setDetail("Scaled Y", (Object)var9);
                  throw new ReportedException(var16);
               }
            }

            var5.afterMouseMove();
         }

         if (this.isMouseGrabbed() && this.minecraft.player != null) {
            this.turnPlayer(var3);
         }
      }

      this.accumulatedDX = 0.0;
      this.accumulatedDY = 0.0;
   }

   private void turnPlayer(double var1) {
      double var7 = (Double)this.minecraft.options.sensitivity().get() * 0.6000000238418579 + 0.20000000298023224;
      double var9 = var7 * var7 * var7;
      double var11 = var9 * 8.0;
      double var3;
      double var5;
      if (this.minecraft.options.smoothCamera) {
         double var13 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * var11, var1 * var11);
         double var15 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * var11, var1 * var11);
         var3 = var13;
         var5 = var15;
      } else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
         this.smoothTurnX.reset();
         this.smoothTurnY.reset();
         var3 = this.accumulatedDX * var9;
         var5 = this.accumulatedDY * var9;
      } else {
         this.smoothTurnX.reset();
         this.smoothTurnY.reset();
         var3 = this.accumulatedDX * var11;
         var5 = this.accumulatedDY * var11;
      }

      byte var17 = 1;
      if ((Boolean)this.minecraft.options.invertYMouse().get()) {
         var17 = -1;
      }

      this.minecraft.getTutorial().onMouse(var3, var5);
      if (this.minecraft.player != null) {
         this.minecraft.player.turn(var3, var5 * (double)var17);
      }

   }

   public boolean isLeftPressed() {
      return this.isLeftPressed;
   }

   public boolean isMiddlePressed() {
      return this.isMiddlePressed;
   }

   public boolean isRightPressed() {
      return this.isRightPressed;
   }

   public double xpos() {
      return this.xpos;
   }

   public double ypos() {
      return this.ypos;
   }

   public void setIgnoreFirstMove() {
      this.ignoreFirstMove = true;
   }

   public boolean isMouseGrabbed() {
      return this.mouseGrabbed;
   }

   public void grabMouse() {
      if (this.minecraft.isWindowActive()) {
         if (!this.mouseGrabbed) {
            if (!Minecraft.ON_OSX) {
               KeyMapping.setAll();
            }

            this.mouseGrabbed = true;
            this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
            this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212995, this.xpos, this.ypos);
            this.minecraft.setScreen((Screen)null);
            this.minecraft.missTime = 10000;
            this.ignoreFirstMove = true;
         }
      }
   }

   public void releaseMouse() {
      if (this.mouseGrabbed) {
         this.mouseGrabbed = false;
         this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
         this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
         InputConstants.grabOrReleaseMouse(this.minecraft.getWindow().getWindow(), 212993, this.xpos, this.ypos);
      }
   }

   public void cursorEntered() {
      this.ignoreFirstMove = true;
   }
}
