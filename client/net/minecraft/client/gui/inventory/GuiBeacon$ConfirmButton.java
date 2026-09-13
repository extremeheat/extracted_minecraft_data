package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;

class GuiBeacon$ConfirmButton extends GuiBeacon$Button {
   public GuiBeacon$ConfirmButton(GuiBeacon var1, int var2, int var3, int var4) {
      super(var2, var3, var4, GuiBeacon.access$000(), 90, 220);
      this.field_146147_o = var1;
   }

   @Override
   public void func_146111_b(int var1, int var2) {
      GuiBeacon.access$200(this.field_146147_o, I18n.func_135052_a("gui.done"), var1, var2);
   }
}
