package net.minecraft.realms;

public interface RealmsAbstractButtonProxy {
   AbstractRealmsButton getButton();

   boolean active();

   void active(boolean var1);

   boolean isVisible();

   void setVisible(boolean var1);
}
