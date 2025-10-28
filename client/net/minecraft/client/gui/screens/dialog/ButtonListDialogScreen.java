package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.ButtonListDialog;
import org.jspecify.annotations.Nullable;

public abstract class ButtonListDialogScreen<T extends ButtonListDialog> extends DialogScreen<T> {
   public static final int FOOTER_MARGIN = 5;

   public ButtonListDialogScreen(@Nullable Screen var1, T var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected void populateBodyElements(LinearLayout var1, DialogControlSet var2, T var3, DialogConnectionAccess var4) {
      super.populateBodyElements(var1, var2, var3, var4);
      List var5 = this.createListActions(var3, var4).map((var1x) -> var2.createActionButton(var1x).build()).toList();
      var1.addChild(packControlsIntoColumns(var5, var3.columns()));
   }

   protected abstract Stream<ActionButton> createListActions(T var1, DialogConnectionAccess var2);

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, DialogControlSet var2, T var3, DialogConnectionAccess var4) {
      super.updateHeaderAndFooter(var1, var2, var3, var4);
      var3.exitAction().ifPresentOrElse((var2x) -> var1.addToFooter(var2.createActionButton(var2x).build()), () -> var1.setFooterHeight(5));
   }
}
