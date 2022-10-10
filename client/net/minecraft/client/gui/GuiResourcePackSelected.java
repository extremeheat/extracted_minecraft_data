package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiResourcePackSelected extends GuiResourcePackList {
   public GuiResourcePackSelected(Minecraft var1, int var2, int var3) {
      super(var1, var2, var3);
   }

   protected String func_148202_k() {
      return I18n.func_135052_a("resourcePack.selected.title");
   }
}
