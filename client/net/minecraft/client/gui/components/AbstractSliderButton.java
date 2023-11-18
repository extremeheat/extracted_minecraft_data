package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton extends AbstractWidget {
   private static final ResourceLocation SLIDER_SPRITE = new ResourceLocation("widget/slider");
   private static final ResourceLocation HIGHLIGHTED_SPRITE = new ResourceLocation("widget/slider_highlighted");
   private static final ResourceLocation SLIDER_HANDLE_SPRITE = new ResourceLocation("widget/slider_handle");
   private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = new ResourceLocation("widget/slider_handle_highlighted");
   protected static final int TEXT_MARGIN = 2;
   private static final int HANDLE_WIDTH = 8;
   private static final int HANDLE_HALF_WIDTH = 4;
   protected double value;
   private boolean canChangeValue;

   public AbstractSliderButton(int var1, int var2, int var3, int var4, Component var5, double var6) {
      super(var1, var2, var3, var4, var5);
      this.value = var6;
   }

   private ResourceLocation getSprite() {
      return this.isFocused() && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
   }

   private ResourceLocation getHandleSprite() {
      return !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
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
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      var1.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.enableDepthTest();
      var1.blitSprite(this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight());
      var1.blitSprite(this.getHandleSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight());
      var1.setColor(1.0F, 1.0F, 1.0F, 1.0F);
      int var6 = this.active ? 16777215 : 10526880;
      this.renderScrollingString(var1, var5.font, 2, var6 | Mth.ceil(this.alpha * 255.0F) << 24);
   }

   @Override
   public void onClick(double var1, double var3) {
      this.setValueFromMouse(var1);
   }

   @Override
   public void setFocused(boolean var1) {
      super.setFocused(var1);
      if (!var1) {
         this.canChangeValue = false;
      } else {
         InputType var2 = Minecraft.getInstance().getLastInputType();
         if (var2 == InputType.MOUSE || var2 == InputType.KEYBOARD_TAB) {
            this.canChangeValue = true;
         }
      }
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (CommonInputs.selected(var1)) {
         this.canChangeValue = !this.canChangeValue;
         return true;
      } else {
         if (this.canChangeValue) {
            boolean var4 = var1 == 263;
            if (var4 || var1 == 262) {
               float var5 = var4 ? -1.0F : 1.0F;
               this.setValue(this.value + (double)(var5 / (float)(this.width - 8)));
               return true;
            }
         }

         return false;
      }
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
