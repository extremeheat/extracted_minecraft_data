package net.minecraft.realms;

import net.minecraft.client.gui.components.AbstractWidget;

public abstract class AbstractRealmsButton<P extends AbstractWidget & RealmsAbstractButtonProxy<?>> {
   public AbstractRealmsButton() {
      super();
   }

   public abstract P getProxy();

   public boolean active() {
      return ((RealmsAbstractButtonProxy)this.getProxy()).active();
   }

   public void active(boolean var1) {
      ((RealmsAbstractButtonProxy)this.getProxy()).active(var1);
   }

   public boolean isVisible() {
      return ((RealmsAbstractButtonProxy)this.getProxy()).isVisible();
   }

   public void setVisible(boolean var1) {
      ((RealmsAbstractButtonProxy)this.getProxy()).setVisible(var1);
   }

   public void render(int var1, int var2, float var3) {
      this.getProxy().render(var1, var2, var3);
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.getProxy().blit(var1, var2, var3, var4, var5, var6);
   }

   public void tick() {
   }
}
