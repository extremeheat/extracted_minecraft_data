package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;

public class RealmsClickableScrolledSelectionListProxy extends GuiSlot {
   private final RealmsClickableScrolledSelectionList field_207723_v;

   public RealmsClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.func_71410_x(), var2, var3, var4, var5, var6);
      this.field_207723_v = var1;
   }

   protected int func_148127_b() {
      return this.field_207723_v.getItemCount();
   }

   protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
      return this.field_207723_v.selectItem(var1, var2, var3, var5);
   }

   protected boolean func_148131_a(int var1) {
      return this.field_207723_v.isSelectedItem(var1);
   }

   protected void func_148123_a() {
      this.field_207723_v.renderBackground();
   }

   protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.field_207723_v.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int func_207716_c() {
      return this.field_148155_a;
   }

   protected int func_148138_e() {
      return this.field_207723_v.getMaxPosition();
   }

   protected int func_148137_d() {
      return this.field_207723_v.getScrollbarPosition();
   }

   public boolean mouseScrolled(double var1) {
      return this.field_207723_v.mouseScrolled(var1) ? true : super.mouseScrolled(var1);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.field_207723_v.mouseClicked(var1, var3, var5) ? true : func_207715_a(this, var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.field_207723_v.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.field_207723_v.mouseDragged(var1, var3, var5, var6, var8) ? true : super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public void func_207719_a(int var1, int var2, int var3, Tezzelator var4) {
      this.field_207723_v.renderSelected(var1, var2, var3, var4);
   }

   protected void func_192638_a(int var1, int var2, int var3, int var4, float var5) {
      int var6 = this.func_148127_b();

      for(int var7 = 0; var7 < var6; ++var7) {
         int var8 = var2 + var7 * this.field_148149_f + this.field_148160_j;
         int var9 = this.field_148149_f - 4;
         if (var8 > this.field_148154_c || var8 + var9 < this.field_148153_b) {
            this.func_192639_a(var7, var1, var8, var5);
         }

         if (this.field_148166_t && this.func_148131_a(var7)) {
            this.func_207719_a(this.field_148155_a, var8, var9, Tezzelator.instance);
         }

         this.func_192637_a(var7, var1, var8, var9, var3, var4, var5);
      }

   }

   public int func_207720_g() {
      return this.field_148153_b;
   }

   public int func_207721_h() {
      return this.field_148154_c;
   }

   public int func_207722_i() {
      return this.field_148160_j;
   }

   public double func_207717_j() {
      return this.field_148169_q;
   }

   public int func_207718_k() {
      return this.field_148149_f;
   }

   // $FF: synthetic method
   static boolean func_207715_a(RealmsClickableScrolledSelectionListProxy var0, double var1, double var3, int var5) {
      return var0.mouseClicked(var1, var3, var5);
   }
}
