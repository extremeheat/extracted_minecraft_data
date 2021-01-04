package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public abstract class RealmsSimpleScrolledSelectionList extends RealmsGuiEventListener {
   private final RealmsSimpleScrolledSelectionListProxy proxy;

   public RealmsSimpleScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      super();
      this.proxy = new RealmsSimpleScrolledSelectionListProxy(this, var1, var2, var3, var4, var5);
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
}
