package net.minecraft.client.gui.screens.resourcepacks.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TranslatableComponent;

public class SelectedResourcePackList extends ResourcePackList {
   public SelectedResourcePackList(Minecraft var1, int var2, int var3) {
      super(var1, var2, var3, new TranslatableComponent("resourcePack.selected.title", new Object[0]));
   }
}
