package net.minecraft.client.gui.screens.dialog;

import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogListDialog;

public class DialogListDialogScreen extends ButtonListDialogScreen<DialogListDialog> {
   public DialogListDialogScreen(@Nullable Screen var1, DialogListDialog var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected Stream<ClickAction> createListActions(DialogListDialog var1, DialogConnectionAccess var2) {
      return var1.dialogs().stream().map((var1x) -> createDialogClickAction(var1, var1x));
   }

   private static ClickAction createDialogClickAction(DialogListDialog var0, Holder<Dialog> var1) {
      return new ClickAction(new CommonButtonData(((Dialog)var1.value()).common().computeExternalTitle(), var0.buttonWidth()), Optional.of(new ClickEvent.ShowDialog(var1)));
   }
}
