package net.minecraft.client.gui.screens;

import net.minecraft.client.gui.components.Renderable;

public abstract class Overlay implements Renderable {
   public Overlay() {
      super();
   }

   public boolean isPauseScreen() {
      return true;
   }
}
