package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class RealmsGuiEventListener {
   public RealmsGuiEventListener() {
      super();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return false;
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return false;
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return false;
   }

   public boolean keyReleased(int var1, int var2, int var3) {
      return false;
   }

   public boolean charTyped(char var1, int var2) {
      return false;
   }

   public abstract GuiEventListener getProxy();
}
