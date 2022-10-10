package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsScrolledSelectionList;

public class RealmsScrolledSelectionListProxy extends GuiSlot {
   private final RealmsScrolledSelectionList field_207725_v;

   public RealmsScrolledSelectionListProxy(RealmsScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.func_71410_x(), var2, var3, var4, var5, var6);
      this.field_207725_v = var1;
   }

   protected int func_148127_b() {
      return this.field_207725_v.getItemCount();
   }

   protected boolean func_195078_a(int var1, int var2, double var3, double var5) {
      return this.field_207725_v.selectItem(var1, var2, var3, var5);
   }

   protected boolean func_148131_a(int var1) {
      return this.field_207725_v.isSelectedItem(var1);
   }

   protected void func_148123_a() {
      this.field_207725_v.renderBackground();
   }

   protected void func_192637_a(int var1, int var2, int var3, int var4, int var5, int var6, float var7) {
      this.field_207725_v.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int func_207724_c() {
      return this.field_148155_a;
   }

   protected int func_148138_e() {
      return this.field_207725_v.getMaxPosition();
   }

   protected int func_148137_d() {
      return this.field_207725_v.getScrollbarPosition();
   }

   public boolean mouseScrolled(double var1) {
      return this.field_207725_v.mouseScrolled(var1) ? true : super.mouseScrolled(var1);
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.field_207725_v.mouseClicked(var1, var3, var5) ? true : super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.field_207725_v.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.field_207725_v.mouseDragged(var1, var3, var5, var6, var8);
   }
}
