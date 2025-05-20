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

   public MultiActionInputFormDialogScreen(@Nullable Screen var1, MultiActionInputFormDialog var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected void populateBodyElements(LinearLayout var1, MultiActionInputFormDialog var2, DialogConnectionAccess var3) {
      super.populateBodyElements(var1, var2, var3);
      InputFormControlSet var4 = new InputFormControlSet(this);

      for(InputFormDialog.Input var6 : var2.inputs()) {
         Objects.requireNonNull(var1);
         var4.addInput(var6, var1::addChild);
      }

      List var7 = var2.actions().stream().map((var3x) -> var4.createActionButton(var3x, var3, this).build()).toList();
      var1.addChild(packControlsIntoColumns(var7, var2.columns()));
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, MultiActionInputFormDialog var2, DialogConnectionAccess var3) {
      super.updateHeaderAndFooter(var1, var2, var3);
      var1.setFooterHeight(5);
   }
}
