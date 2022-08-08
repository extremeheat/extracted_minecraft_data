package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class Button extends AbstractButton {
   public static final OnTooltip NO_TOOLTIP = (var0, var1, var2, var3) -> {
   };
   public static final int SMALL_WIDTH = 120;
   public static final int DEFAULT_WIDTH = 150;
   public static final int DEFAULT_HEIGHT = 20;
   protected final OnPress onPress;
   protected final OnTooltip onTooltip;

   public Button(int var1, int var2, int var3, int var4, Component var5, OnPress var6) {
      this(var1, var2, var3, var4, var5, var6, NO_TOOLTIP);
   }

   public Button(int var1, int var2, int var3, int var4, Component var5, OnPress var6, OnTooltip var7) {
      super(var1, var2, var3, var4, var5);
      this.onPress = var6;
      this.onTooltip = var7;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      super.renderButton(var1, var2, var3, var4);
      if (this.isHoveredOrFocused()) {
         this.renderToolTip(var1, var2, var3);
      }

   }

   public void renderToolTip(PoseStack var1, int var2, int var3) {
      this.onTooltip.onTooltip(this, var1, var2, var3);
   }

   public void updateNarration(NarrationElementOutput var1) {
      this.defaultButtonNarrationText(var1);
      this.onTooltip.narrateTooltip((var1x) -> {
         var1.add(NarratedElementType.HINT, var1x);
      });
   }

   public interface OnTooltip {
      void onTooltip(Button var1, PoseStack var2, int var3, int var4);

      default void narrateTooltip(Consumer<Component> var1) {
      }
   }

   public interface OnPress {
      void onPress(Button var1);
   }
}
