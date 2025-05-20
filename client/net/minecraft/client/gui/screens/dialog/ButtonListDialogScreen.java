package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.server.dialog.ButtonListDialog;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.CommonButtonData;

public abstract class ButtonListDialogScreen<T extends ButtonListDialog> extends DialogScreen<T> {
   public static final CommonButtonData DEFAULT_CANCEL_BUTTON_DATA;
   public static final CommonButtonData DEFAULT_BACK_BUTTON_DATA;

   public ButtonListDialogScreen(@Nullable Screen var1, T var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected void populateBodyElements(LinearLayout var1, T var2, DialogConnectionAccess var3) {
      super.populateBodyElements(var1, var2, var3);
      List var4 = this.createListActions(var2, var3).map((var1x) -> this.createClickActionButton(var1x).build()).toList();
      var1.addChild(packControlsIntoColumns(var4, var2.columns()));
   }

   protected abstract Stream<ClickAction> createListActions(T var1, DialogConnectionAccess var2);

   protected ClickAction createCancelAction(T var1) {
      Optional var2 = var1.onCancel();
      boolean var3 = var2.isPresent();
      return new ClickAction(var3 ? DEFAULT_CANCEL_BUTTON_DATA : DEFAULT_BACK_BUTTON_DATA, var2);
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, T var2, DialogConnectionAccess var3) {
      super.updateHeaderAndFooter(var1, var2, var3);
      var1.addToFooter(this.createClickActionButton(this.createCancelAction(var2)).build());
   }

   static {
      DEFAULT_CANCEL_BUTTON_DATA = new CommonButtonData(CommonComponents.GUI_CANCEL, 200);
      DEFAULT_BACK_BUTTON_DATA = new CommonButtonData(CommonComponents.GUI_BACK, 200);
   }
}
