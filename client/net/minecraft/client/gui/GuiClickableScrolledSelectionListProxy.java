package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.input.Mouse;

public class GuiClickableScrolledSelectionListProxy extends GuiSlot {
   private final RealmsClickableScrolledSelectionList field_178046_u;

   public GuiClickableScrolledSelectionListProxy(RealmsClickableScrolledSelectionList var1, int var2, int var3, int var4, int var5, int var6) {
      super(Minecraft.func_71410_x(), var2, var3, var4, var5, var6);
      this.field_178046_u = var1;
   }

   protected int func_148127_b() {
      return this.field_178046_u.getItemCount();
   }

   protected void func_148144_a(int var1, boolean var2, int var3, int var4) {
      this.field_178046_u.selectItem(var1, var2, var3, var4);
   }

   protected boolean func_148131_a(int var1) {
      return this.field_178046_u.isSelectedItem(var1);
   }

   protected void func_148123_a() {
      this.field_178046_u.renderBackground();
   }

   protected void func_180791_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_178046_u.renderItem(var1, var2, var3, var4, var5, var6);
   }

   public int func_178044_e() {
      return super.field_148155_a;
   }

   public int func_178042_f() {
      return super.field_148162_h;
   }

   public int func_178045_g() {
      return super.field_148150_g;
   }

   protected int func_148138_e() {
      return this.field_178046_u.getMaxPosition();
   }

   protected int func_148137_d() {
      return this.field_178046_u.getScrollbarPosition();
   }

   public void func_178039_p() {
      super.func_178039_p();
      if (this.field_148170_p > 0.0F && Mouse.getEventButtonState()) {
         this.field_178046_u.customMouseEvent(this.field_148153_b, this.field_148154_c, this.field_148160_j, this.field_148169_q, this.field_148149_f);
      }

   }

   public void func_178043_a(int var1, int var2, int var3, Tezzelator var4) {
      this.field_178046_u.renderSelected(var1, var2, var3, var4);
   }

   protected void func_148120_b(int var1, int var2, int var3, int var4) {
      int var5 = this.func_148127_b();

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var2 + var6 * this.field_148149_f + this.field_148160_j;
         int var8 = this.field_148149_f - 4;
         if (var7 > this.field_148154_c || var7 + var8 < this.field_148153_b) {
            this.func_178040_a(var6, var1, var7);
         }

         if (this.field_148166_t && this.func_148131_a(var6)) {
            this.func_178043_a(this.field_148155_a, var7, var8, Tezzelator.instance);
         }

         this.func_180791_a(var6, var1, var7, var8, var3, var4);
      }

   }
}
