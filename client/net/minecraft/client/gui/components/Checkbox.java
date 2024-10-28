package net.minecraft.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.ResourceLocation;

public class Checkbox extends AbstractButton {
   private static final ResourceLocation CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected_highlighted");
   private static final ResourceLocation CHECKBOX_SELECTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_selected");
   private static final ResourceLocation CHECKBOX_HIGHLIGHTED_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox_highlighted");
   private static final ResourceLocation CHECKBOX_SPRITE = ResourceLocation.withDefaultNamespace("widget/checkbox");
   private static final int TEXT_COLOR = 14737632;
   private static final int SPACING = 4;
   private static final int BOX_PADDING = 8;
   private boolean selected;
   private final OnValueChange onValueChange;
   private final MultiLineTextWidget textWidget;

   Checkbox(int var1, int var2, int var3, Component var4, Font var5, boolean var6, OnValueChange var7) {
      super(var1, var2, 0, 0, var4);
      this.width = this.getAdjustedWidth(var3, var4, var5);
      this.textWidget = (new MultiLineTextWidget(var4, var5)).setMaxWidth(this.width).setColor(14737632);
      this.height = this.getAdjustedHeight(var5);
      this.selected = var6;
      this.onValueChange = var7;
   }

   private int getAdjustedWidth(int var1, Component var2, Font var3) {
      return Math.min(getDefaultWidth(var2, var3), var1);
   }

   private int getAdjustedHeight(Font var1) {
      return Math.max(getBoxSize(var1), this.textWidget.getHeight());
   }

   static int getDefaultWidth(Component var0, Font var1) {
      return getBoxSize(var1) + 4 + var1.width((FormattedText)var0);
   }

   public static Builder builder(Component var0, Font var1) {
      return new Builder(var0, var1);
   }

   public static int getBoxSize(Font var0) {
      Objects.requireNonNull(var0);
      return 9 + 8;
   }

   public void onPress() {
      this.selected = !this.selected;
      this.onValueChange.onValueChange(this, this.selected);
   }

   public boolean selected() {
      return this.selected;
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)this.createNarrationMessage());
      if (this.active) {
         if (this.isFocused()) {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.checkbox.usage.focused"));
         } else {
            var1.add(NarratedElementType.USAGE, (Component)Component.translatable("narration.checkbox.usage.hovered"));
         }
      }

   }

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      Minecraft var5 = Minecraft.getInstance();
      RenderSystem.enableDepthTest();
      Font var6 = var5.font;
      var1.setColor(1.0F, 1.0F, 1.0F, this.alpha);
      RenderSystem.enableBlend();
      ResourceLocation var7;
      if (this.selected) {
         var7 = this.isFocused() ? CHECKBOX_SELECTED_HIGHLIGHTED_SPRITE : CHECKBOX_SELECTED_SPRITE;
      } else {
         var7 = this.isFocused() ? CHECKBOX_HIGHLIGHTED_SPRITE : CHECKBOX_SPRITE;
      }

      int var8 = getBoxSize(var6);
      var1.blitSprite(var7, this.getX(), this.getY(), var8, var8);
      int var9 = this.getX() + var8 + 4;
      int var10 = this.getY() + var8 / 2 - this.textWidget.getHeight() / 2;
      this.textWidget.setPosition(var9, var10);
      this.textWidget.renderWidget(var1, var2, var3, var4);
   }

   public interface OnValueChange {
      OnValueChange NOP = (var0, var1) -> {
      };

      void onValueChange(Checkbox var1, boolean var2);
   }

   public static class Builder {
      private final Component message;
      private final Font font;
      private int maxWidth;
      private int x = 0;
      private int y = 0;
      private OnValueChange onValueChange;
      private boolean selected;
      @Nullable
      private OptionInstance<Boolean> option;
      @Nullable
      private Tooltip tooltip;

      Builder(Component var1, Font var2) {
         super();
         this.onValueChange = Checkbox.OnValueChange.NOP;
         this.selected = false;
         this.option = null;
         this.tooltip = null;
         this.message = var1;
         this.font = var2;
         this.maxWidth = Checkbox.getDefaultWidth(var1, var2);
      }

      public Builder pos(int var1, int var2) {
         this.x = var1;
         this.y = var2;
         return this;
      }

      public Builder onValueChange(OnValueChange var1) {
         this.onValueChange = var1;
         return this;
      }

      public Builder selected(boolean var1) {
         this.selected = var1;
         this.option = null;
         return this;
      }

      public Builder selected(OptionInstance<Boolean> var1) {
         this.option = var1;
         this.selected = (Boolean)var1.get();
         return this;
      }

      public Builder tooltip(Tooltip var1) {
         this.tooltip = var1;
         return this;
      }

      public Builder maxWidth(int var1) {
         this.maxWidth = var1;
         return this;
      }

      public Checkbox build() {
         OnValueChange var1 = this.option == null ? this.onValueChange : (var1x, var2x) -> {
            this.option.set(var2x);
            this.onValueChange.onValueChange(var1x, var2x);
         };
         Checkbox var2 = new Checkbox(this.x, this.y, this.maxWidth, this.message, this.font, this.selected, var1);
         var2.setTooltip(this.tooltip);
         return var2;
      }
   }
}
