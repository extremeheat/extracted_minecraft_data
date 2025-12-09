package net.minecraft.client;

import com.mojang.blaze3d.Blaze3D;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.logging.LogUtils;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputQuirks;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.Mth;
import net.minecraft.util.SmoothDouble;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.Inventory;
import org.joml.Vector2i;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFWDropCallback;
import org.slf4j.Logger;

public class MouseHandler {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final long DOUBLE_CLICK_THRESHOLD_MS = 250L;
   private final Minecraft minecraft;
   private boolean isLeftPressed;
   private boolean isMiddlePressed;
   private boolean isRightPressed;
   private double xpos;
   private double ypos;
   private @Nullable LastClick lastClick;
   protected @MouseButtonInfo.MouseButton int lastClickButton;
   private int fakeRightMouse;
   private @Nullable MouseButtonInfo activeButton = null;
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

   private void onButton(long var1, MouseButtonInfo var3, @MouseButtonInfo.Action int var4) {
      Window var5 = this.minecraft.getWindow();
      if (var1 == var5.handle()) {
         this.minecraft.getFramerateLimitTracker().onInputReceived();
         if (this.minecraft.screen != null) {
            this.minecraft.setLastInputType(InputType.MOUSE);
         }

         boolean var6 = var4 == 1;
         MouseButtonInfo var7 = this.simulateRightClick(var3, var6);
         if (var6) {
            if ((Boolean)this.minecraft.options.touchscreen().get() && this.clickDepth++ > 0) {
               return;
            }

            this.activeButton = var7;
            this.mousePressedTime = Blaze3D.getTime();
         } else if (this.activeButton != null) {
            if ((Boolean)this.minecraft.options.touchscreen().get() && --this.clickDepth > 0) {
               return;
            }

            this.activeButton = null;
         }

         if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen == null) {
               if (!this.mouseGrabbed && var6) {
                  this.grabMouse();
               }
            } else {
               double var8 = this.getScaledXPos(var5);
               double var10 = this.getScaledYPos(var5);
               Screen var12 = this.minecraft.screen;
               MouseButtonEvent var13 = new MouseButtonEvent(var8, var10, var7);
               if (var6) {
                  var12.afterMouseAction();

                  try {
                     long var14 = Util.getMillis();
                     boolean var21 = this.lastClick != null && var14 - this.lastClick.time() < 250L && this.lastClick.screen() == var12 && this.lastClickButton == var13.button();
                     if (var12.mouseClicked(var13, var21)) {
                        this.lastClick = new LastClick(var14, var12);
                        this.lastClickButton = var7.button();
                        return;
                     }
                  } catch (Throwable var18) {
                     CrashReport var15 = CrashReport.forThrowable(var18, "mouseClicked event handler");
                     var12.fillCrashDetails(var15);
                     CrashReportCategory var16 = var15.addCategory("Mouse");
                     this.fillMousePositionDetails(var16, var5);
                     var16.setDetail("Button", var13.button());
                     throw new ReportedException(var15);
                  }
               } else {
                  try {
                     if (var12.mouseReleased(var13)) {
                        return;
                     }
                  } catch (Throwable var17) {
                     CrashReport var20 = CrashReport.forThrowable(var17, "mouseReleased event handler");
                     var12.fillCrashDetails(var20);
                     CrashReportCategory var22 = var20.addCategory("Mouse");
                     this.fillMousePositionDetails(var22, var5);
                     var22.setDetail("Button", var13.button());
                     throw new ReportedException(var20);
                  }
               }
            }
         }

         if (this.minecraft.screen == null && this.minecraft.getOverlay() == null) {
            if (var7.button() == 0) {
               this.isLeftPressed = var6;
            } else if (var7.button() == 2) {
               this.isMiddlePressed = var6;
            } else if (var7.button() == 1) {
               this.isRightPressed = var6;
            }

            InputConstants.Key var19 = InputConstants.Type.MOUSE.getOrCreate(var7.button());
            KeyMapping.set(var19, var6);
            if (var6) {
               KeyMapping.click(var19);
            }
         }

      }
   }

   private MouseButtonInfo simulateRightClick(MouseButtonInfo var1, boolean var2) {
      if (InputQuirks.SIMULATE_RIGHT_CLICK_WITH_LONG_LEFT_CLICK && var1.button() == 0) {
         if (var2) {
            if ((var1.modifiers() & 2) == 2) {
               ++this.fakeRightMouse;
               return new MouseButtonInfo(1, var1.modifiers());
            }
         } else if (this.fakeRightMouse > 0) {
            --this.fakeRightMouse;
            return new MouseButtonInfo(1, var1.modifiers());
         }
      }

      return var1;
   }

   public void fillMousePositionDetails(CrashReportCategory var1, Window var2) {
      var1.setDetail("Mouse location", (CrashReportDetail)(() -> String.format(Locale.ROOT, "Scaled: (%f, %f). Absolute: (%f, %f)", getScaledXPos(var2, this.xpos), getScaledYPos(var2, this.ypos), this.xpos, this.ypos)));
      var1.setDetail("Screen size", (CrashReportDetail)(() -> String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d", var2.getGuiScaledWidth(), var2.getGuiScaledHeight(), var2.getWidth(), var2.getHeight(), var2.getGuiScale())));
   }

   private void onScroll(long var1, double var3, double var5) {
      if (var1 == this.minecraft.getWindow().handle()) {
         this.minecraft.getFramerateLimitTracker().onInputReceived();
         boolean var7 = (Boolean)this.minecraft.options.discreteMouseScroll().get();
         double var8 = (Double)this.minecraft.options.mouseWheelSensitivity().get();
         double var10 = (var7 ? Math.signum(var3) : var3) * var8;
         double var12 = (var7 ? Math.signum(var5) : var5) * var8;
         if (this.minecraft.getOverlay() == null) {
            if (this.minecraft.screen != null) {
               double var14 = this.getScaledXPos(this.minecraft.getWindow());
               double var16 = this.getScaledYPos(this.minecraft.getWindow());
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
                  var20.setSelectedSlot(ScrollWheelHandler.getNextScrollWheelSelection((double)var15, var20.getSelectedSlot(), Inventory.getSelectionSize()));
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

   public void setup(Window var1) {
      InputConstants.setupMouseCallbacks(var1, (var1x, var3, var5) -> this.minecraft.execute(() -> this.onMove(var1x, var3, var5)), (var1x, var3, var4, var5) -> {
         MouseButtonInfo var6 = new MouseButtonInfo(var3, var5);
         this.minecraft.execute(() -> this.onButton(var1x, var6, var4));
      }, (var1x, var3, var5) -> this.minecraft.execute(() -> this.onScroll(var1x, var3, var5)), (var1x, var3, var4) -> {
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
            this.minecraft.execute(() -> this.onDrop(var1x, var6, var7));
         }

      });
   }

   private void onMove(long var1, double var3, double var5) {
      if (var1 == this.minecraft.getWindow().handle()) {
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
            Window var7 = this.minecraft.getWindow();
            double var8 = this.getScaledXPos(var7);
            double var10 = this.getScaledYPos(var7);

            try {
               var5.mouseMoved(var8, var10);
            } catch (Throwable var20) {
               CrashReport var13 = CrashReport.forThrowable(var20, "mouseMoved event handler");
               var5.fillCrashDetails(var13);
               CrashReportCategory var14 = var13.addCategory("Mouse");
               this.fillMousePositionDetails(var14, var7);
               throw new ReportedException(var13);
            }

            if (this.activeButton != null && this.mousePressedTime > 0.0) {
               double var12 = getScaledXPos(var7, this.accumulatedDX);
               double var21 = getScaledYPos(var7, this.accumulatedDY);

               try {
                  var5.mouseDragged(new MouseButtonEvent(var8, var10, this.activeButton), var12, var21);
               } catch (Throwable var19) {
                  CrashReport var17 = CrashReport.forThrowable(var19, "mouseDragged event handler");
                  var5.fillCrashDetails(var17);
                  CrashReportCategory var18 = var17.addCategory("Mouse");
                  this.fillMousePositionDetails(var18, var7);
                  throw new ReportedException(var17);
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

   public static double getScaledXPos(Window var0, double var1) {
      return var1 * (double)var0.getGuiScaledWidth() / (double)var0.getScreenWidth();
   }

   public double getScaledXPos(Window var1) {
      return getScaledXPos(var1, this.xpos);
   }

   public static double getScaledYPos(Window var0, double var1) {
      return var1 * (double)var0.getGuiScaledHeight() / (double)var0.getScreenHeight();
   }

   public double getScaledYPos(Window var1) {
      return getScaledYPos(var1, this.ypos);
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

      this.minecraft.getTutorial().onMouse(var3, var5);
      if (this.minecraft.player != null) {
         this.minecraft.player.turn((Boolean)this.minecraft.options.invertMouseX().get() ? -var3 : var3, (Boolean)this.minecraft.options.invertMouseY().get() ? -var5 : var5);
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
            if (InputQuirks.RESTORE_KEY_STATE_AFTER_MOUSE_GRAB) {
               KeyMapping.setAll();
            }

            this.mouseGrabbed = true;
            this.xpos = (double)(this.minecraft.getWindow().getScreenWidth() / 2);
            this.ypos = (double)(this.minecraft.getWindow().getScreenHeight() / 2);
            InputConstants.grabOrReleaseMouse(this.minecraft.getWindow(), 212995, this.xpos, this.ypos);
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
         InputConstants.grabOrReleaseMouse(this.minecraft.getWindow(), 212993, this.xpos, this.ypos);
      }
   }

   public void cursorEntered() {
      this.ignoreFirstMove = true;
   }

   public void drawDebugMouseInfo(Font var1, GuiGraphics var2) {
      Window var3 = this.minecraft.getWindow();
      double var4 = this.getScaledXPos(var3);
      double var6 = this.getScaledYPos(var3) - 8.0;
      String var8 = String.format(Locale.ROOT, "%.0f,%.0f", var4, var6);
      var2.drawString(var1, (String)var8, (int)var4, (int)var6, -1);
   }

   static record LastClick(long time, Screen screen) {
      LastClick(long var1, Screen var3) {
         super();
         this.time = var1;
         this.screen = var3;
      }
   }
}
