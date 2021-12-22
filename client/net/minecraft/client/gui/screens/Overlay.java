package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;

public abstract class Overlay extends GuiComponent implements Widget {
   public Overlay() {
      super();
   }

   public boolean isPauseScreen() {
      return true;
   }
}
