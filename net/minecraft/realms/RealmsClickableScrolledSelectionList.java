package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
   private final RealmsClickableScrolledSelectionListProxy proxy;

   public RealmsClickableScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      this.proxy = new RealmsClickableScrolledSelectionListProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.render(var1, var2, var3);
   }

   public int width() {
      return this.proxy.getWidth();
   }

   protected void renderItem(int var1, int var2, int var3, int var4, Tezzelator var5, int var6, int var7) {
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, Tezzelator.instance, var5, var6);
   }

   public int getItemCount() {
      return 0;
   }

   public boolean selectItem(int var1, int var2, double var3, double var5) {
      return true;
   }

   public boolean isSelectedItem(int var1) {
      return false;
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getWidth() / 2 + 124;
   }

   public GuiEventListener getProxy() {
      return this.proxy;
   }

   public void scroll(int var1) {
      this.proxy.scroll(var1);
   }

   public int getScroll() {
      return this.proxy.getScroll();
   }

   protected void renderList(int var1, int var2, int var3, int var4) {
   }

   public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
   }

   public void renderSelected(int var1, int var2, int var3, Tezzelator var4) {
   }

   public void setLeftPos(int var1) {
      this.proxy.setLeftPos(var1);
   }

   public int y0() {
      return this.proxy.y0();
   }

   public int y1() {
      return this.proxy.y1();
   }

   public int headerHeight() {
      return this.proxy.headerHeight();
   }

   public double yo() {
      return this.proxy.yo();
   }

   public int itemHeight() {
      return this.proxy.itemHeight();
   }

   public boolean isVisible() {
      return this.proxy.isVisible();
   }
}
