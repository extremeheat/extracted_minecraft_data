package net.minecraft.client.gui.components;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class Button extends AbstractButton {
   public static final int SMALL_WIDTH = 120;
   public static final int DEFAULT_WIDTH = 150;
   public static final int BIG_WIDTH = 200;
   public static final int DEFAULT_HEIGHT = 20;
   public static final int DEFAULT_SPACING = 8;
   protected static final CreateNarration DEFAULT_NARRATION = (var0) -> (MutableComponent)var0.get();
   protected final OnPress onPress;
   protected final CreateNarration createNarration;

   public static Builder builder(Component var0, OnPress var1) {
      return new Builder(var0, var1);
   }

   protected Button(int var1, int var2, int var3, int var4, Component var5, OnPress var6, CreateNarration var7) {
      super(var1, var2, var3, var4, var5);
      this.onPress = var6;
      this.createNarration = var7;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   protected MutableComponent createNarrationMessage() {
      return this.createNarration.createNarrationMessage(() -> super.createNarrationMessage());
   }

   public void updateWidgetNarration(NarrationElementOutput var1) {
      this.defaultButtonNarrationText(var1);
   }

   public static class Builder {
      private final Component message;
      private final OnPress onPress;
      @Nullable
      private Tooltip tooltip;
      private int x;
      private int y;
      private int width = 150;
      private int height = 20;
      private CreateNarration createNarration;

      public Builder(Component var1, OnPress var2) {
         super();
         this.createNarration = Button.DEFAULT_NARRATION;
         this.message = var1;
         this.onPress = var2;
      }

      public Builder pos(int var1, int var2) {
         this.x = var1;
         this.y = var2;
         return this;
      }

      public Builder width(int var1) {
         this.width = var1;
         return this;
      }

      public Builder size(int var1, int var2) {
         this.width = var1;
         this.height = var2;
         return this;
      }

      public Builder bounds(int var1, int var2, int var3, int var4) {
         return this.pos(var1, var2).size(var3, var4);
      }

      public Builder tooltip(@Nullable Tooltip var1) {
         this.tooltip = var1;
         return this;
      }

      public Builder createNarration(CreateNarration var1) {
         this.createNarration = var1;
         return this;
      }

      public Button build() {
         Button var1 = new Button(this.x, this.y, this.width, this.height, this.message, this.onPress, this.createNarration);
         var1.setTooltip(this.tooltip);
         return var1;
      }
   }

   public interface CreateNarration {
      MutableComponent createNarrationMessage(Supplier<MutableComponent> var1);
   }

   public interface OnPress {
      void onPress(Button var1);
   }
}
