package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.realms.RealmsButton;

public class GuiButtonRealmsProxy extends GuiButton {
   private RealmsButton field_154318_o;

   public GuiButtonRealmsProxy(RealmsButton var1, int var2, int var3, int var4, String var5) {
      super(var2, var3, var4, var5);
      this.field_154318_o = var1;
   }

   public GuiButtonRealmsProxy(RealmsButton var1, int var2, int var3, int var4, String var5, int var6, int var7) {
      super(var2, var3, var4, var6, var7, var5);
      this.field_154318_o = var1;
   }

   public int func_154314_d() {
      return super.field_146127_k;
   }

   public boolean func_154315_e() {
      return super.field_146124_l;
   }

   public void func_154313_b(boolean var1) {
      super.field_146124_l = var1;
   }

   public void func_154311_a(String var1) {
      super.field_146126_j = var1;
   }

   public int func_146117_b() {
      return super.func_146117_b();
   }

   public int func_154316_f() {
      return super.field_146129_i;
   }

   public boolean func_146116_c(Minecraft var1, int var2, int var3) {
      if (super.func_146116_c(var1, var2, var3)) {
         this.field_154318_o.clicked(var2, var3);
      }

      return super.func_146116_c(var1, var2, var3);
   }

   public void func_146118_a(int var1, int var2) {
      this.field_154318_o.released(var1, var2);
   }

   public void func_146119_b(Minecraft var1, int var2, int var3) {
      this.field_154318_o.renderBg(var2, var3);
   }

   public RealmsButton func_154317_g() {
      return this.field_154318_o;
   }

   public int func_146114_a(boolean var1) {
      return this.field_154318_o.getYImage(var1);
   }

   public int func_154312_c(boolean var1) {
      return super.func_146114_a(var1);
   }

   public int func_175232_g() {
      return this.field_146121_g;
   }
}
