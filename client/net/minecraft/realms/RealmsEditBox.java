package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;

public class RealmsEditBox {
   private final GuiTextField editBox;

   public RealmsEditBox(int var1, int var2, int var3, int var4, int var5) {
      super();
      this.editBox = new GuiTextField(var1, Minecraft.func_71410_x().field_71466_p, var2, var3, var4, var5);
   }

   public String getValue() {
      return this.editBox.func_146179_b();
   }

   public void tick() {
      this.editBox.func_146178_a();
   }

   public void setFocus(boolean var1) {
      this.editBox.func_146195_b(var1);
   }

   public void setValue(String var1) {
      this.editBox.func_146180_a(var1);
   }

   public void keyPressed(char var1, int var2) {
      this.editBox.func_146201_a(var1, var2);
   }

   public boolean isFocused() {
      return this.editBox.func_146206_l();
   }

   public void mouseClicked(int var1, int var2, int var3) {
      this.editBox.func_146192_a(var1, var2, var3);
   }

   public void render() {
      this.editBox.func_146194_f();
   }

   public void setMaxLength(int var1) {
      this.editBox.func_146203_f(var1);
   }

   public void setIsEditable(boolean var1) {
      this.editBox.func_146184_c(var1);
   }
}
