package net.minecraft.realms;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.RealmsClickableScrolledSelectionListProxy;

public abstract class RealmsClickableScrolledSelectionList extends RealmsGuiEventListener {
   private final RealmsClickableScrolledSelectionListProxy proxy;

   public RealmsClickableScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      super();
      this.proxy = new RealmsClickableScrolledSelectionListProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.func_148128_a(var1, var2, var3);
   }

   public int width() {
      return this.proxy.func_207716_c();
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
      return this.proxy.func_207716_c() / 2 + 124;
   }

   public IGuiEventListener getProxy() {
      return this.proxy;
   }

   public void scroll(int var1) {
      this.proxy.func_148145_f(var1);
   }

   public int getScroll() {
      return this.proxy.func_148148_g();
   }

   protected void renderList(int var1, int var2, int var3, int var4) {
   }

   public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
   }

   public void renderSelected(int var1, int var2, int var3, Tezzelator var4) {
   }

   public void setLeftPos(int var1) {
      this.proxy.func_148140_g(var1);
   }

   public int y0() {
      return this.proxy.func_207720_g();
   }

   public int y1() {
      return this.proxy.func_207721_h();
   }

   public int headerHeight() {
      return this.proxy.func_207722_i();
   }

   public double yo() {
      return this.proxy.func_207717_j();
   }

   public int itemHeight() {
      return this.proxy.func_207718_k();
   }

   public boolean isVisible() {
      return this.proxy.func_195082_l();
   }
}
