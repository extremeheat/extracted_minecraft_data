package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

public abstract class RealmsButton extends AbstractRealmsButton<RealmsButtonProxy> {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private final int id;
   private final RealmsButtonProxy proxy;

   public RealmsButton(int var1, int var2, int var3, String var4) {
      this(var1, var2, var3, 200, 20, var4);
   }

   public RealmsButton(int var1, int var2, int var3, int var4, int var5, String var6) {
      super();
      this.id = var1;
      this.proxy = new RealmsButtonProxy(this, var2, var3, var6, var4, var5, (var1x) -> {
         this.onPress();
      });
   }

   public RealmsButtonProxy getProxy() {
      return this.proxy;
   }

   public int id() {
      return this.id;
   }

   public void setMessage(String var1) {
      this.proxy.setMessage(var1);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int y() {
      return this.proxy.y();
   }

   public int x() {
      return this.proxy.x;
   }

   public void renderBg(int var1, int var2) {
   }

   public int getYImage(boolean var1) {
      return this.proxy.getSuperYImage(var1);
   }

   public abstract void onPress();

   public void onRelease(double var1, double var3) {
   }

   public void renderButton(int var1, int var2, float var3) {
      this.getProxy().superRenderButton(var1, var2, var3);
   }

   public void drawCenteredString(String var1, int var2, int var3, int var4) {
      this.getProxy().drawCenteredString(Minecraft.getInstance().font, var1, var2, var3, var4);
   }
}
