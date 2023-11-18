package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;

public interface GuiEventListener extends TabOrderedElement {
   long DOUBLE_CLICK_THRESHOLD_MS = 250L;

   default void mouseMoved(double var1, double var3) {
   }

   default boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseReleased(double var1, double var3, int var5) {
      return false;
   }

   default boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return false;
   }

   default boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   default boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   default boolean keyReleased(int var1, int var2, int var3) {
      return false;
   }

   default boolean charTyped(char var1, int var2) {
      return false;
   }

   @Nullable
   default ComponentPath nextFocusPath(FocusNavigationEvent var1) {
      return null;
   }

   default boolean isMouseOver(double var1, double var3) {
      return false;
   }

   void setFocused(boolean var1);

   boolean isFocused();

   @Nullable
   default ComponentPath getCurrentFocusPath() {
      return this.isFocused() ? ComponentPath.leaf(this) : null;
   }

   default ScreenRectangle getRectangle() {
      return ScreenRectangle.empty();
   }
}
