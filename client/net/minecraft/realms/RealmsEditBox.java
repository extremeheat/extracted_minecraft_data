package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.IGuiEventListener;

public class RealmsEditBox extends RealmsGuiEventListener {
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

   public void setValue(String var1) {
      this.editBox.func_146180_a(var1);
   }

   public boolean charTyped(char var1, int var2) {
      return this.editBox.charTyped(var1, var2);
   }

   public IGuiEventListener getProxy() {
      return this.editBox;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.editBox.keyPressed(var1, var2, var3);
   }

   public boolean isFocused() {
      return this.editBox.func_146206_l();
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.editBox.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.editBox.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.editBox.mouseDragged(var1, var3, var5, var6, var8);
   }

   public boolean mouseScrolled(double var1) {
      return this.editBox.mouseScrolled(var1);
   }

   public void render(int var1, int var2, float var3) {
      this.editBox.func_195608_a(var1, var2, var3);
   }

   public void setMaxLength(int var1) {
      this.editBox.func_146203_f(var1);
   }

   public void setIsEditable(boolean var1) {
      this.editBox.func_146184_c(var1);
   }
}
