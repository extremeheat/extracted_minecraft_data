package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiListButton extends GuiButton {
   private boolean field_175216_o;
   private String field_175215_p;
   private final GuiPageButtonList.GuiResponder field_175214_q;

   public GuiListButton(GuiPageButtonList.GuiResponder var1, int var2, int var3, int var4, String var5, boolean var6) {
      super(var2, var3, var4, 150, 20, "");
      this.field_175215_p = var5;
      this.field_175216_o = var6;
      this.field_146126_j = this.func_175213_c();
      this.field_175214_q = var1;
   }

   private String func_175213_c() {
      return I18n.func_135052_a(this.field_175215_p) + ": " + (this.field_175216_o ? I18n.func_135052_a("gui.yes") : I18n.func_135052_a("gui.no"));
   }

   public void func_175212_b(boolean var1) {
      this.field_175216_o = var1;
      this.field_146126_j = this.func_175213_c();
      this.field_175214_q.func_175321_a(this.field_146127_k, var1);
   }

   public boolean func_146116_c(Minecraft var1, int var2, int var3) {
      if (super.func_146116_c(var1, var2, var3)) {
         this.field_175216_o = !this.field_175216_o;
         this.field_146126_j = this.func_175213_c();
         this.field_175214_q.func_175321_a(this.field_146127_k, this.field_175216_o);
         return true;
      } else {
         return false;
      }
   }
}
