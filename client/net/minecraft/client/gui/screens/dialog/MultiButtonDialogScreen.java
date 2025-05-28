package net.minecraft.client.gui.screens.dialog;

import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.MultiActionDialog;

public class MultiButtonDialogScreen extends ButtonListDialogScreen<MultiActionDialog> {
   public MultiButtonDialogScreen(@Nullable Screen var1, MultiActionDialog var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected Stream<ActionButton> createListActions(MultiActionDialog var1, DialogConnectionAccess var2) {
      return var1.actions().stream();
   }
}
