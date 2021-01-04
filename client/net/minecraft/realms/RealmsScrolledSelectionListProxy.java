package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrolledSelectionList;

public class RealmsScrolledSelectionListProxy extends ScrolledSelectionList {
   private final RealmsScrolledSelectionList realmsScrolledSelectionList;

   public RealmsScrolledSelectionListProxy(RealmsScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getInstance(), var2, var3, var4, var5, var6);
      this.realmsScrolledSelectionList = var1;
   }

   public int getItemCount() {
      return this.realmsScrolledSelectionList.getItemCount();
   }

   public boolean selectItem(int var1, int var2, double var3, double var5) {
      return this.realmsScrolledSelectionList.selectItem(var1, var2, var3, var5);
   }

   public boolean isSelectedItem(int var1) {
      return this.realmsScrolledSelectionList.isSelectedItem(var1);
   }

   public void renderBackground() {
      this.realmsScrolledSelectionList.renderBackground();
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.realmsScrolledSelectionList.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsScrolledSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsScrolledSelectionList.getScrollbarPosition();
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.realmsScrolledSelectionList.mouseScrolled(var1, var3, var5) ? true : super.mouseScrolled(var1, var3, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.realmsScrolledSelectionList.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.realmsScrolledSelectionList.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.realmsScrolledSelectionList.mouseDragged(var1, var3, var5, var6, var8);
   }
}
