package net.minecraft.client;

import net.minecraft.client.gui.GuiYesNoCallback;

class Minecraft$16 implements GuiYesNoCallback {
   Minecraft$16(Minecraft var1) {
      super();
      this.field_152128_a = var1;
   }

   @Override
   public void func_73878_a(boolean var1, int var2) {
      if (var1) {
         this.field_152128_a.func_152346_Z().func_152930_t();
      }

      this.field_152128_a.func_147108_a(null);
   }
}
