package net.minecraft.client.gui.screens.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandlers;
import net.minecraft.client.gui.screens.dialog.submit.SubmitMethodHandler;
import net.minecraft.client.gui.screens.dialog.submit.SubmitMethodHandlers;
import net.minecraft.server.dialog.InputFormDialog;

public class InputFormControlSet {
   public static final String ACTION_KEY = "action";
   private final Minecraft minecraft;
   private final Screen screen;
   private final Map<String, Supplier<String>> valueGetters = new HashMap();

   public InputFormControlSet(Minecraft var1, Screen var2) {
      super();
      this.minecraft = var1;
      this.screen = var2;
   }

   public void addInput(InputFormDialog.Input var1, Consumer<LayoutElement> var2) {
      String var3 = var1.key();
      InputControlHandlers.createHandler(var1.control(), this.screen, (var3x, var4) -> {
         this.valueGetters.put(var3, var4);
         var2.accept(var3x);
      });
   }

   public Button.Builder createActionButton(InputFormDialog.SubmitAction var1) {
      SubmitMethodHandler.Callback var2 = SubmitMethodHandlers.createCallback(var1.method());
      return DialogScreen.createDialogButton(var1.buttonData(), (var3) -> {
         HashMap var4 = new HashMap(this.valueGetters.size());
         this.valueGetters.forEach((var1x, var2x) -> var4.put(var1x, (String)var2x.get()));
         var4.put("action", var1.id());
         var2.run(this.minecraft.player.connection, var4);
         this.screen.onClose();
      });
   }
}
