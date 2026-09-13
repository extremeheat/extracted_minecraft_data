package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;

class GuiBeacon$CancelButton extends GuiBeacon$Button {
   public GuiBeacon$CancelButton(GuiBeacon var1, int var2, int var3, int var4) {
      super(var2, var3, var4, GuiBeacon.access$000(), 112, 220);
      this.field_146146_o = var1;
   }

   @Override
   public void func_146111_b(int var1, int var2) {
      GuiBeacon.access$300(this.field_146146_o, I18n.func_135052_a("gui.cancel"), var1, var2);
   }
}
