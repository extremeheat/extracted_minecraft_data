package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ScrolledSelectionList;

public class RealmsClickableScrolledSelectionListProxy extends ScrolledSelectionList {
   private final RealmsClickableScrolledSelectionList realmsClickableScrolledSelectionList;

   public RealmsClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.getInstance(), var2, var3, var4, var5, var6);
      this.realmsClickableScrolledSelectionList = var1;
   }

   public int getItemCount() {
      return this.realmsClickableScrolledSelectionList.getItemCount();
   }

   public boolean selectItem(int var1, int var2, double var3, double var5) {
      return this.realmsClickableScrolledSelectionList.selectItem(var1, var2, var3, var5);
   }

   public boolean isSelectedItem(int var1) {
      return this.realmsClickableScrolledSelectionList.isSelectedItem(var1);
   }

   public void renderBackground() {
      this.realmsClickableScrolledSelectionList.renderBackground();
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.realmsClickableScrolledSelectionList.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int getWidth() {
      return this.width;
   }

   public int getMaxPosition() {
      return this.realmsClickableScrolledSelectionList.getMaxPosition();
   }

   public int getScrollbarPosition() {
      return this.realmsClickableScrolledSelectionList.getScrollbarPosition();
   }

   public void itemClicked(int var1, int var2, int var3, int var4, int var5) {
      this.realmsClickableScrolledSelectionList.itemClicked(var1, var2, (double)var3, (double)var4, var5);
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return this.realmsClickableScrolledSelectionList.mouseScrolled(var1, var3, var5) ? true : super.mouseScrolled(var1, var3, var5);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.realmsClickableScrolledSelectionList.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.realmsClickableScrolledSelectionList.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.realmsClickableScrolledSelectionList.mouseDragged(var1, var3, var5, var6, var8) ? true : super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public void renderSelected(int var1, int var2, int var3, Tezzelator var4) {
      this.realmsClickableScrolledSelectionList.renderSelected(var1, var2, var3, var4);
   }

   public void renderList(int var1, int var2, int var3, int var4, float var5) {
      int var6 = this.getItemCount();

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var2 + var7 * this.itemHeight + this.headerHeight;
         int var9 = this.itemHeight - 4;
         if (var8 > this.y1 || var8 + var9 < this.y0) {
            this.updateItemPosition(var7, var1, var8, var5);
         }

         if (this.renderSelection && this.isSelectedItem(var7)) {
            this.renderSelected(this.width, var8, var9, Tezzelator.instance);
         }

         this.renderItem(var7, var1, var8, var9, var3, var4, var5);
      }

   }

   public int y0() {
      return this.y0;
   }

   public int y1() {
      return this.y1;
   }

   public int headerHeight() {
      return this.headerHeight;
   }

   public double yo() {
      return this.yo;
   }

   public int itemHeight() {
      return this.itemHeight;
   }
}
