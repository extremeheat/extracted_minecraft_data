package net.minecraft.client.gui;

import javax.annotation.Nullable;

public interface IGuiEventListenerDeferred extends IGuiEventListener {
   @Nullable
   IGuiEventListener getFocused();

   default boolean mouseClicked(double var1, double var3, int var5) {
      return this.getFocused() != null && this.getFocused().mouseClicked(var1, var3, var5);
   }

   default boolean mouseReleased(double var1, double var3, int var5) {
      return this.getFocused() != null && this.getFocused().mouseReleased(var1, var3, var5);
   }

   default boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.getFocused() != null && this.getFocused().mouseDragged(var1, var3, var5, var6, var8);
   }

   default boolean mouseScrolled(double var1) {
      return this.getFocused() != null && this.getFocused().mouseScrolled(var1);
   }

   default boolean keyPressed(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyPressed(var1, var2, var3);
   }

   default boolean keyReleased(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyReleased(var1, var2, var3);
   }

   default boolean charTyped(char var1, int var2) {
      return this.getFocused() != null && this.getFocused().charTyped(var1, var2);
   }
}
