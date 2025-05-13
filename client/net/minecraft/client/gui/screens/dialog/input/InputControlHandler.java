package net.minecraft.client.gui.screens.dialog.input;

import java.util.function.Supplier;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.input.InputControl;

@FunctionalInterface
public interface InputControlHandler<T extends InputControl> {
   void addControl(T var1, Screen var2, Output var3);

   @FunctionalInterface
   public interface Output {
      void accept(LayoutElement var1, Supplier<String> var2);
   }
}
