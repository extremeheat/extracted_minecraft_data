package net.minecraft.client.gui.components;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class SpacerWidget extends AbstractWidget {
   public SpacerWidget(int var1, int var2) {
      this(0, 0, var1, var2);
   }

   public SpacerWidget(int var1, int var2, int var3, int var4) {
      super(var1, var2, var3, var4, Component.empty());
   }

   @Override
   public void updateWidgetNarration(NarrationElementOutput var1) {
   }

   @Override
   public boolean changeFocus(boolean var1) {
      return false;
   }

   public static AbstractWidget width(int var0) {
      return new SpacerWidget(var0, 0);
   }

   public static AbstractWidget height(int var0) {
      return new SpacerWidget(0, var0);
   }
}
