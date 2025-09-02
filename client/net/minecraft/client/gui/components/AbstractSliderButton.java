package net.minecraft.client.gui.components;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.InputType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

public abstract class AbstractSliderButton extends AbstractWidget {
   private static final ResourceLocation SLIDER_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider");
   private static final ResourceLocation HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_highlighted");
   private static final ResourceLocation SLIDER_HANDLE_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle");
   private static final ResourceLocation SLIDER_HANDLE_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/slider_handle_highlighted");
   protected static final int TEXT_MARGIN = 2;
   public static final int DEFAULT_HEIGHT = 20;
   private static final int HANDLE_WIDTH = 8;
   private static final int HANDLE_HALF_WIDTH = 4;
   protected double value;
   private boolean canChangeValue;
   private boolean dragging;

   public AbstractSliderButton(int var1, int var2, int var3, int var4, Component var5, double var6) {
      super(var1, var2, var3, var4, var5);
      this.value = var6;
   }

   private ResourceLocation getSprite() {
      return this.isActive() && this.isFocused() && !this.canChangeValue ? HIGHLIGHTED_SPRITE : SLIDER_SPRITE;
   }

   private ResourceLocation getHandleSprite() {
      return !this.isActive() || !this.isHovered && !this.canChangeValue ? SLIDER_HANDLE_SPRITE : SLIDER_HANDLE_HIGHLIGHTED_SPRITE;
   }

   protected MutableComponent createNarrationMessage() {
      return Component.translatable("gui.narrate.slider", this.getMessage());
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            if (this.canChangeValue) {
               var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.slider.usage.focused"));
            } else {
               var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.slider.usage.focused.keyboard_cannot_change_value"));
            }
         } else {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.slider.usage.hovered"));
         }
      }

   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, this.getSprite(), this.getX(), this.getY(), this.getWidth(), this.getHeight(), ARGB.white(this.alpha));
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, (ResourceLocation)this.getHandleSprite(), this.getX() + (int)(this.value * (double)(this.width - 8)), this.getY(), 8, this.getHeight(), ARGB.white(this.alpha));
      int var6 = ARGB.color(this.alpha, this.active ? -1 : -6250336);
      this.renderScrollingString(var1, var5.font, 2, var6);
      if (this.isHovered()) {
         var1.requestCursor(this.dragging ? CursorTypes.RESIZE_EW : CursorTypes.POINTING_HAND);
      }

   }

   public void onClick(MouseButtonEvent var1, boolean var2) {
      this.dragging = this.active;
      this.setValueFromMouse(var1);
   }

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

   public boolean keyPressed(KeyEvent var1) {
      if (var1.isSelection()) {
         this.canChangeValue = !this.canChangeValue;
         return true;
      } else {
         if (this.canChangeValue) {
            boolean var2 = var1.isLeft();
            boolean var3 = var1.isRight();
            if (var2 || var3) {
               float var4 = var2 ? -1.0F : 1.0F;
               this.setValue(this.value + (double)(var4 / (float)(this.width - 8)));
               return true;
            }
         }

         return false;
      }
   }

   private void setValueFromMouse(MouseButtonEvent var1) {
      this.setValue((var1.x() - (double)(this.getX() + 4)) / (double)(this.width - 8));
   }

   private void setValue(double var1) {
      double var3 = this.value;
      this.value = Mth.clamp(var1, 0.0, 1.0);
      if (var3 != this.value) {
         this.applyValue();
      }

      this.updateMessage();
   }

   protected void onDrag(MouseButtonEvent var1, double var2, double var4) {
      this.setValueFromMouse(var1);
      super.onDrag(var1, var2, var4);
   }

   public void playDownSound(SoundManager var1) {
   }

   public void onRelease(MouseButtonEvent var1) {
      this.dragging = false;
      super.playDownSound(Minecraft.getInstance().getSoundManager());
   }

   protected abstract void updateMessage();

   protected abstract void applyValue();
}
