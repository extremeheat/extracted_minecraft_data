package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton extends AbstractWidget {
   protected final Options options;
   protected double value;

   protected AbstractSliderButton(int var1, int var2, int var3, int var4, double var5) {
      this(Minecraft.getInstance().options, var1, var2, var3, var4, var5);
   }

   protected AbstractSliderButton(Options var1, int var2, int var3, int var4, int var5, double var6) {
      super(var2, var3, var4, var5, "");
      this.options = var1;
      this.value = var6;
   }

   protected int getYImage(boolean var1) {
      return 0;
   }

   protected String getNarrationMessage() {
      return I18n.get("gui.narrate.slider", this.getMessage());
   }

   protected void renderBg(Minecraft var1, int var2, int var3) {
      var1.getTextureManager().bind(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      int var4 = (this.isHovered() ? 2 : 1) * 20;
      this.blit(this.x + (int)(this.value * (double)(this.width - 8)), this.y, 0, 46 + var4, 4, 20);
      this.blit(this.x + (int)(this.value * (double)(this.width - 8)) + 4, this.y, 196, 46 + var4, 4, 20);
   }

   public void onClick(double var1, double var3) {
      this.setValueFromMouse(var1);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = var1 == 263;
      if (var4 || var1 == 262) {
         float var5 = var4 ? -1.0F : 1.0F;
         this.setValue(this.value + (double)(var5 / (float)(this.width - 8)));
      }

      return false;
   }

   private void setValueFromMouse(double var1) {
      this.setValue((var1 - (double)(this.x + 4)) / (double)(this.width - 8));
   }

   private void setValue(double var1) {
      double var3 = this.value;
      this.value = Mth.clamp(var1, 0.0D, 1.0D);
      if (var3 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(double var1, double var3, double var5, double var7) {
      this.setValueFromMouse(var1);
      super.onDrag(var1, var3, var5, var7);
   }

   public void playDownSound(SoundManager var1) {
   }

   public void onRelease(double var1, double var3) {
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}
