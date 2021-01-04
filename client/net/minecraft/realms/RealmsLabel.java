package net.minecraft.realms;

import net.minecraft.client.gui.components.events.GuiEventListener;

public class RealmsLabel extends RealmsGuiEventListener {
   private final RealmsLabelProxy proxy = new RealmsLabelProxy(this);
   private final String text;
   private final int x;
   private final int y;
   private final int color;

   public RealmsLabel(String var1, int var2, int var3, int var4) {
      super();
      this.text = var1;
      this.x = var2;
      this.y = var3;
      this.color = var4;
   }

   public void render(RealmsScreen var1) {
      var1.drawCenteredString(this.text, this.x, this.y, this.color);
   }

   public GuiEventListener getProxy() {
      return this.proxy;
   }

   public String getText() {
      return this.text;
   }
}
