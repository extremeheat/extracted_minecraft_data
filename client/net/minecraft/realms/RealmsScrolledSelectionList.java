package net.minecraft.realms;

import net.minecraft.client.gui.GuiSlotRealmsProxy;

public class RealmsScrolledSelectionList {
   private final GuiSlotRealmsProxy proxy;

   public RealmsScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      super();
      this.proxy = new GuiSlotRealmsProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.func_148128_a(var1, var2, var3);
   }

   public int width() {
      return this.proxy.func_154338_k();
   }

   public int ym() {
      return this.proxy.func_154339_l();
   }

   public int xm() {
      return this.proxy.func_154337_m();
   }

   protected void renderItem(int var1, int var2, int var3, int var4, Tezzelator var5, int var6, int var7) {
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, Tezzelator.instance, var5, var6);
   }

   public int getItemCount() {
      return 0;
   }

   public void selectItem(int var1, boolean var2, int var3, int var4) {
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
      return this.proxy.func_154338_k() / 2 + 124;
   }

   public void mouseEvent() {
      this.proxy.func_178039_p();
   }

   public void scroll(int var1) {
      this.proxy.func_148145_f(var1);
   }

   public int getScroll() {
      return this.proxy.func_148148_g();
   }

   protected void renderList(int var1, int var2, int var3, int var4) {
   }
}
