package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonRealmsProxy;
import net.minecraft.util.ResourceLocation;

public class RealmsButton {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private GuiButtonRealmsProxy proxy;

   public RealmsButton(int var1, int var2, int var3, String var4) {
      super();
      this.proxy = new GuiButtonRealmsProxy(this, var1, var2, var3, var4);
   }

   public RealmsButton(int var1, int var2, int var3, int var4, int var5, String var6) {
      super();
      this.proxy = new GuiButtonRealmsProxy(this, var1, var2, var3, var6, var4, var5);
   }

   public GuiButton getProxy() {
      return this.proxy;
   }

   public int id() {
      return this.proxy.func_154314_d();
   }

   public boolean active() {
      return this.proxy.func_154315_e();
   }

   public void active(boolean var1) {
      this.proxy.func_154313_b(var1);
   }

   public void msg(String var1) {
      this.proxy.func_154311_a(var1);
   }

   public int getWidth() {
      return this.proxy.func_146117_b();
   }

   public int getHeight() {
      return this.proxy.func_175232_g();
   }

   public int y() {
      return this.proxy.func_154316_f();
   }

   public void render(int var1, int var2) {
      this.proxy.func_146112_a(Minecraft.func_71410_x(), var1, var2);
   }

   public void clicked(int var1, int var2) {
   }

   public void released(int var1, int var2) {
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.func_73729_b(var1, var2, var3, var4, var5, var6);
   }

   public void renderBg(int var1, int var2) {
   }

   public int getYImage(boolean var1) {
      return this.proxy.func_154312_c(var1);
   }
}
