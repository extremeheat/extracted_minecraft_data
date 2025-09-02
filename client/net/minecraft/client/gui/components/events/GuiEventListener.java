package net.minecraft.client.gui.components.events;

import javax.annotation.Nullable;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.TabOrderedElement;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public interface GuiEventListener extends TabOrderedElement {
   default void mouseMoved(double var1, double var3) {
   }

   default boolean mouseClicked(MouseButtonEvent var1, boolean var2) {
      return false;
   }

   default boolean mouseReleased(MouseButtonEvent var1) {
      return false;
   }

   default boolean mouseDragged(MouseButtonEvent var1, double var2, double var4) {
      return false;
   }

   default boolean mouseScrolled(double var1, double var3, double var5, double var7) {
      return false;
   }

   default boolean keyPressed(KeyEvent var1) {
      return false;
   }

   default boolean keyReleased(KeyEvent var1) {
      return false;
   }

   default boolean charTyped(CharacterEvent var1) {
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

   default boolean shouldTakeFocusAfterInteraction() {
      return true;
   }

   @Nullable
   default ComponentPath getCurrentFocusPath() {
      return this.isFocused() ? ComponentPath.leaf(this) : null;
   }

   default ScreenRectangle getRectangle() {
      return ScreenRectangle.empty();
   }

   default ScreenRectangle getBorderForArrowNavigation(ScreenDirection var1) {
      return this.getRectangle().getBorder(var1);
   }
}
