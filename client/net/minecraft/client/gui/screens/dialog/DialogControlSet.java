package net.minecraft.client.gui.screens.dialog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.dialog.input.InputControlHandlers;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Input;
import net.minecraft.server.dialog.action.Action;

public class DialogControlSet {
   public static final Supplier<Optional<ClickEvent>> EMPTY_ACTION = Optional::empty;
   private final DialogScreen<?> screen;
   private final Map<String, Action.ValueGetter> valueGetters = new HashMap();

   public DialogControlSet(DialogScreen<?> var1) {
      super();
      this.screen = var1;
   }

   public void addInput(Input var1, Consumer<LayoutElement> var2) {
      String var3 = var1.key();
      InputControlHandlers.createHandler(var1.control(), this.screen, (var3x, var4) -> {
         this.valueGetters.put(var3, var4);
         var2.accept(var3x);
      });
   }

   private static Button.Builder createDialogButton(CommonButtonData var0, Button.OnPress var1) {
      Button.Builder var2 = Button.builder(var0.label(), var1);
      var2.width(var0.width());
      if (var0.tooltip().isPresent()) {
         var2 = var2.tooltip(Tooltip.create((Component)var0.tooltip().get()));
      }

      return var2;
   }

   public Supplier<Optional<ClickEvent>> bindAction(Optional<Action> var1) {
      if (var1.isPresent()) {
         Action var2 = (Action)var1.get();
         return () -> var2.createAction(this.valueGetters);
      } else {
         return EMPTY_ACTION;
      }
   }

   public Button.Builder createActionButton(ActionButton var1) {
      Supplier var2 = this.bindAction(var1.action());
      return createDialogButton(var1.button(), (var2x) -> this.screen.runAction((Optional)var2.get()));
   }
}
