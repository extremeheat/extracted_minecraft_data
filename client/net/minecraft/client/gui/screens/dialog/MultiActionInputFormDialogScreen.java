package net.minecraft.client.gui.screens.dialog;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.InputFormDialog;
import net.minecraft.server.dialog.MultiActionInputFormDialog;

public class MultiActionInputFormDialogScreen extends DialogScreen<MultiActionInputFormDialog> {
   public static final int FOOTER_MARGIN = 5;

   public MultiActionInputFormDialogScreen(@Nullable Screen var1, MultiActionInputFormDialog var2) {
      super(var1, var2);
   }

   protected void populateBodyElements(LinearLayout var1, MultiActionInputFormDialog var2) {
      super.populateBodyElements(var1, var2);
      InputFormControlSet var3 = new InputFormControlSet(this.minecraft, this);

      for(InputFormDialog.Input var5 : var2.inputs()) {
         Objects.requireNonNull(var1);
         var3.addInput(var5, var1::addChild);
      }

      List var6 = var2.actions().stream().map((var1x) -> var3.createActionButton(var1x).build()).toList();
      var1.addChild(packControlsIntoColumns(var6, 2));
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, MultiActionInputFormDialog var2) {
      var1.setFooterHeight(5);
   }
}
