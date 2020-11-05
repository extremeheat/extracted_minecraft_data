package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import org.lwjgl.glfw.GLFWDropCallback;

public class MouseHandler {
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
   private double accumulatedScroll;
   private double lastMouseEventTime = 4.9E-324D;
   private boolean mouseGrabbed;

   public MouseHandler(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   private void onPress(long var1, int var3, int var4, int var5) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
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

         if (var6) {
            if (this.minecraft.options.touchscreen && this.clickDepth++ > 0) {
               return;
            }

            this.activeButton = var3;
            this.mousePressedTime = Blaze3D.getTime();
         } else if (this.activeButton != -1) {
            if (this.minecraft.options.touchscreen && --this.clickDepth > 0) {
               return;
            }

            this.activeButton = -1;
         }

         boolean[] var8 = new boolean[]{false};
         if (this.minecraft.overlay == null) {
            if (this.minecraft.screen == null) {
               if (!this.mouseGrabbed && var6) {
                  this.grabMouse();
               }
            } else {
               double var9 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var11 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               if (var6) {
                  Screen.wrapScreenError(() -> {
                     var8[0] = this.minecraft.screen.mouseClicked(var9, var11, var3);
                  }, "mouseClicked event handler", this.minecraft.screen.getClass().getCanonicalName());
               } else {
                  Screen.wrapScreenError(() -> {
                     var8[0] = this.minecraft.screen.mouseReleased(var9, var11, var3);
                  }, "mouseReleased event handler", this.minecraft.screen.getClass().getCanonicalName());
               }
            }
         }

         if (!var8[0] && (this.minecraft.screen == null || this.minecraft.screen.passEvents) && this.minecraft.overlay == null) {
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
         double var7 = (this.minecraft.options.discreteMouseScroll ? Math.signum(var5) : var5) * this.minecraft.options.mouseWheelSensitivity;
         if (this.minecraft.overlay == null) {
            if (this.minecraft.screen != null) {
               double var9 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var11 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               this.minecraft.screen.mouseScrolled(var9, var11, var7);
            } else if (this.minecraft.player != null) {
               if (this.accumulatedScroll != 0.0D && Math.signum(var7) != Math.signum(this.accumulatedScroll)) {
                  this.accumulatedScroll = 0.0D;
               }

               this.accumulatedScroll += var7;
               float var13 = (float)((int)this.accumulatedScroll);
               if (var13 == 0.0F) {
                  return;
               }

               this.accumulatedScroll -= (double)var13;
               if (this.minecraft.player.isSpectator()) {
                  if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                     this.minecraft.gui.getSpectatorGui().onMouseScrolled((double)(-var13));
                  } else {
                     float var10 = Mth.clamp(this.minecraft.player.abilities.getFlyingSpeed() + var13 * 0.005F, 0.0F, 0.2F);
                     this.minecraft.player.abilities.setFlyingSpeed(var10);
                  }
               } else {
                  this.minecraft.player.inventory.swapPaint((double)var13);
               }
            }
         }
      }

   }

   private void onDrop(long var1, List<Path> var3) {
      if (this.minecraft.screen != null) {
         this.minecraft.screen.onFilesDrop(var3);
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
         Path[] var6 = new Path[var3];

         for(int var7 = 0; var7 < var3; ++var7) {
            var6[var7] = Paths.get(GLFWDropCallback.getName(var4, var7));
         }

         this.minecraft.execute(() -> {
            this.onDrop(var1x, Arrays.asList(var6));
         });
      });
   }

   private void onMove(long var1, double var3, double var5) {
      if (var1 == Minecraft.getInstance().getWindow().getWindow()) {
         if (this.ignoreFirstMove) {
            this.xpos = var3;
            this.ypos = var5;
            this.ignoreFirstMove = false;
         }

         Screen var7 = this.minecraft.screen;
         if (var7 != null && this.minecraft.overlay == null) {
            double var8 = var3 * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double var10 = var5 * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
            Screen.wrapScreenError(() -> {
               var7.mouseMoved(var8, var10);
            }, "mouseMoved event handler", var7.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.mousePressedTime > 0.0D) {
               double var12 = (var3 - this.xpos) * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var14 = (var5 - this.ypos) * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               Screen.wrapScreenError(() -> {
                  var7.mouseDragged(var8, var10, this.activeButton, var12, var14);
               }, "mouseDragged event handler", var7.getClass().getCanonicalName());
            }
         }

         this.minecraft.getProfiler().push("mouse");
         if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
            this.accumulatedDX += var3 - this.xpos;
            this.accumulatedDY += var5 - this.ypos;
         }

         this.turnPlayer();
         this.xpos = var3;
         this.ypos = var5;
         this.minecraft.getProfiler().pop();
      }
   }

   public void turnPlayer() {
      double var1 = Blaze3D.getTime();
      double var3 = var1 - this.lastMouseEventTime;
      this.lastMouseEventTime = var1;
      if (this.isMouseGrabbed() && this.minecraft.isWindowActive()) {
         double var9 = this.minecraft.options.sensitivity * 0.6000000238418579D + 0.20000000298023224D;
         double var11 = var9 * var9 * var9 * 8.0D;
         double var5;
         double var7;
         if (this.minecraft.options.smoothCamera) {
            double var13 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * var11, var3 * var11);
            double var15 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * var11, var3 * var11);
            var5 = var13;
            var7 = var15;
         } else {
            this.smoothTurnX.reset();
            this.smoothTurnY.reset();
            var5 = this.accumulatedDX * var11;
            var7 = this.accumulatedDY * var11;
         }

         this.accumulatedDX = 0.0D;
         this.accumulatedDY = 0.0D;
         byte var17 = 1;
         if (this.minecraft.options.invertYMouse) {
            var17 = -1;
         }

         this.minecraft.getTutorial().onMouse(var5, var7);
         if (this.minecraft.player != null) {
            this.minecraft.player.turn(var5, var7 * (double)var17);
         }

      } else {
         this.accumulatedDX = 0.0D;
         this.accumulatedDY = 0.0D;
      }
   }

   public boolean isLeftPressed() {
      return this.isLeftPressed;
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
