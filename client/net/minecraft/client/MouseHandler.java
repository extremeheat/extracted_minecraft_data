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
   private double accumulatedScrollX;
   private double accumulatedScrollY;
   private double lastHandleMovementTime = 5.0E-324;
   private boolean mouseGrabbed;

   public MouseHandler(Minecraft var1) {
      super();
      this.minecraft = var1;
   }

   private void onPress(long var1, int var3, int var4, int var5) {
      if (var1 == this.minecraft.getWindow().getWindow()) {
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
            if (this.minecraft.options.touchscreen().get() && this.clickDepth++ > 0) {
               return;
            }

            this.activeButton = var7;
            this.mousePressedTime = Blaze3D.getTime();
         } else if (this.activeButton != -1) {
            if (this.minecraft.options.touchscreen().get() && --this.clickDepth > 0) {
               return;
            }

            this.activeButton = -1;
         }

         boolean[] var8 = new boolean[]{false};
         if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen == null) {
               if (!this.mouseGrabbed && var6) {
                  this.grabMouse();
               }
            } else {
               double var9 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var11 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               Screen var13 = this.minecraft.screen;
               if (var6) {
                  var13.afterMouseAction();
                  Screen.wrapScreenError(
                     () -> var8[0] = var13.mouseClicked(var9, var11, var7), "mouseClicked event handler", var13.getClass().getCanonicalName()
                  );
               } else {
                  Screen.wrapScreenError(
                     () -> var8[0] = var13.mouseReleased(var9, var11, var7), "mouseReleased event handler", var13.getClass().getCanonicalName()
                  );
               }
            }
         }

         if (!var8[0] && this.minecraft.screen == null && this.minecraft.getOverlay() == null) {
            if (var7 == 0) {
               this.isLeftPressed = var6;
            } else if (var7 == 2) {
               this.isMiddlePressed = var6;
            } else if (var7 == 1) {
               this.isRightPressed = var6;
            }

            KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(var7), var6);
            if (var6) {
               if (this.minecraft.player.isSpectator() && var7 == 2) {
                  this.minecraft.gui.getSpectatorGui().onMouseMiddleClick();
               } else {
                  KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(var7));
               }
            }
         }
      }
   }

   private void onScroll(long var1, double var3, double var5) {
      if (var1 == Minecraft.getInstance().getWindow().getWindow()) {
         boolean var7 = this.minecraft.options.discreteMouseScroll().get();
         double var8 = this.minecraft.options.mouseWheelSensitivity().get();
         double var10 = (var7 ? Math.signum(var3) : var3) * var8;
         double var12 = (var7 ? Math.signum(var5) : var5) * var8;
         if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen != null) {
               double var14 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var16 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
               this.minecraft.screen.mouseScrolled(var14, var16, var10, var12);
               this.minecraft.screen.afterMouseAction();
            } else if (this.minecraft.player != null) {
               if (this.accumulatedScrollX != 0.0 && Math.signum(var10) != Math.signum(this.accumulatedScrollX)) {
                  this.accumulatedScrollX = 0.0;
               }

               if (this.accumulatedScrollY != 0.0 && Math.signum(var12) != Math.signum(this.accumulatedScrollY)) {
                  this.accumulatedScrollY = 0.0;
               }

               this.accumulatedScrollX += var10;
               this.accumulatedScrollY += var12;
               int var18 = (int)this.accumulatedScrollX;
               int var15 = (int)this.accumulatedScrollY;
               if (var18 == 0 && var15 == 0) {
                  return;
               }

               this.accumulatedScrollX -= (double)var18;
               this.accumulatedScrollY -= (double)var15;
               int var19 = var15 == 0 ? -var18 : var15;
               if (this.minecraft.player.isSpectator()) {
                  if (this.minecraft.gui.getSpectatorGui().isMenuActive()) {
                     this.minecraft.gui.getSpectatorGui().onMouseScrolled(-var19);
                  } else {
                     float var17 = Mth.clamp(this.minecraft.player.getAbilities().getFlyingSpeed() + (float)var15 * 0.005F, 0.0F, 0.2F);
                     this.minecraft.player.getAbilities().setFlyingSpeed(var17);
                  }
               } else {
                  this.minecraft.player.getInventory().swapPaint((double)var19);
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
      InputConstants.setupMouseCallbacks(
         var1,
         (var1x, var3, var5) -> this.minecraft.execute(() -> this.onMove(var1x, var3, var5)),
         (var1x, var3, var4, var5) -> this.minecraft.execute(() -> this.onPress(var1x, var3, var4, var5)),
         (var1x, var3, var5) -> this.minecraft.execute(() -> this.onScroll(var1x, var3, var5)),
         (var1x, var3, var4) -> {
            Path[] var6 = new Path[var3];
   
            for(int var7 = 0; var7 < var3; ++var7) {
               var6[var7] = Paths.get(GLFWDropCallback.getName(var4, var7));
            }
   
            this.minecraft.execute(() -> this.onDrop(var1x, Arrays.asList(var6)));
         }
      );
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
         if (var5 != null && this.minecraft.getOverlay() == null && (this.accumulatedDX != 0.0 || this.accumulatedDY != 0.0)) {
            double var6 = this.xpos * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
            double var8 = this.ypos * (double)this.minecraft.getWindow().getGuiScaledHeight() / (double)this.minecraft.getWindow().getScreenHeight();
            Screen.wrapScreenError(() -> var5.mouseMoved(var6, var8), "mouseMoved event handler", var5.getClass().getCanonicalName());
            if (this.activeButton != -1 && this.mousePressedTime > 0.0) {
               double var10 = this.accumulatedDX * (double)this.minecraft.getWindow().getGuiScaledWidth() / (double)this.minecraft.getWindow().getScreenWidth();
               double var12 = this.accumulatedDY
                  * (double)this.minecraft.getWindow().getGuiScaledHeight()
                  / (double)this.minecraft.getWindow().getScreenHeight();
               Screen.wrapScreenError(
                  () -> var5.mouseDragged(var6, var8, this.activeButton, var10, var12), "mouseDragged event handler", var5.getClass().getCanonicalName()
               );
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
      double var7 = this.minecraft.options.sensitivity().get() * 0.6000000238418579 + 0.20000000298023224;
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
      if (this.minecraft.options.invertYMouse().get()) {
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
            this.minecraft.setScreen(null);
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
