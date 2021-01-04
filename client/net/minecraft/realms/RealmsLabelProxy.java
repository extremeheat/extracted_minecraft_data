package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public class RealmsLabelProxy implements GuiEventListener {
   private final RealmsLabel label;

   public RealmsLabelProxy(RealmsLabel var1) {
      super();
      this.label = var1;
   }

   public RealmsLabel getLabel() {
      return this.label;
   }
}
