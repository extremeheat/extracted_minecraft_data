package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class GuiSlotRealmsProxy extends GuiSlot {
   private final RealmsScrolledSelectionList field_154340_k;

   public GuiSlotRealmsProxy(RealmsScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.func_71410_x(), var2, var3, var4, var5, var6);
      this.field_154340_k = var1;
   }

   protected int func_148127_b() {
      return this.field_154340_k.getItemCount();
   }

   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      this.field_154340_k.selectItem(var1, var2, var3, var4);
   }

   protected boolean func_148131_a(int var1) {
      return this.field_154340_k.isSelectedItem(var1);
   }

   protected void func_148123_a() {
      this.field_154340_k.renderBackground();
   }

   protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_154340_k.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int func_154338_k() {
      return super.field_148155_a;
   }

   public int func_154339_l() {
      return super.field_148162_h;
   }

   public int func_154337_m() {
      return super.field_148150_g;
   }

   protected int func_148138_e() {
      return this.field_154340_k.getMaxPosition();
   }

   protected int func_148137_d() {
      return this.field_154340_k.getScrollbarPosition();
   }

   public void func_178039_p() {
      super.func_178039_p();
   }
}
