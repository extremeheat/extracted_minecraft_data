package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton extends AbstractWidget {
   protected double value;

   public AbstractSliderButton(int var1, int var2, int var3, int var4, Component var5, double var6) {
      super(var1, var2, var3, var4, var5);
      this.value = var6;
   }

   @Override
   protected int getYImage(boolean var1) {
      return 0;
   }

   @Override
   protected MutableComponent createNarrationMessage() {
      return Component.translatable("gui.narrate.slider", this.getMessage());
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.focused"));
         } else {
            var1.add(NarratedElementType.USAGE, Component.translatable("narration.slider.usage.hovered"));
         }
      }
   }

   @Override
   protected void renderBg(PoseStack var1, Minecraft var2, int var3, int var4) {
      RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      int var5 = (this.isHoveredOrFocused() ? 2 : 1) * 20;
      this.blit(var1, this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 0, 46 + var5, 4, 20);
      this.blit(var1, this.getX() + (int)(this.value * (double)(this.width - 8)) + 4, this.getY(), 196, 46 + var5, 4, 20);
   }

   @Override
   public void onClick(double var1, double var3) {
      this.setValueFromMouse(var1);
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      boolean var4 = var1 == 263;
      if (var4 || var1 == 262) {
         float var5 = var4 ? -1.0F : 1.0F;
         this.setValue(this.value + (double)(var5 / (float)(this.width - 8)));
      }

      return false;
   }

   private void setValueFromMouse(double var1) {
      this.setValue((var1 - (double)(this.getX() + 4)) / (double)(this.width - 8));
   }

   private void setValue(double var1) {
      double var3 = this.value;
      this.value = Mth.clamp(var1, 0.0, 1.0);
      if (var3 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   @Override
   protected void onDrag(double var1, double var3, double var5, double var7) {
      this.setValueFromMouse(var1);
      super.onDrag(var1, var3, var5, var7);
   }

   @Override
   public void playDownSound(SoundManager var1) {
   }

   @Override
   public void onRelease(double var1, double var3) {
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}
