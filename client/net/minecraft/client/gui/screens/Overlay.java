package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Renderable;

public abstract class Overlay extends GuiComponent implements Renderable {
   public Overlay() {
      super();
   }

   public boolean isPauseScreen() {
      return true;
   }
}
