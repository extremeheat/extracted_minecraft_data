package net.minecraft.client.gui;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class GuiResourcePackSelected extends GuiResourcePackList {
   public GuiResourcePackSelected(Minecraft var1, int var2, int var3, List var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   protected String func_148202_k() {
      return I18n.func_135052_a("resourcePack.selected.title");
   }
}
