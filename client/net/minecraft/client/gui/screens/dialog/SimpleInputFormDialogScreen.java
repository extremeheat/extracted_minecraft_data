package net.minecraft.client.gui.screens.dialog;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.InputFormDialog;
import net.minecraft.server.dialog.SimpleInputFormDialog;

public class SimpleInputFormDialogScreen extends DialogScreen<SimpleInputFormDialog> {
   private InputFormControlSet inputSet;

   public SimpleInputFormDialogScreen(@Nullable Screen var1, SimpleInputFormDialog var2) {
      super(var1, var2);
   }

   protected void dialogInit(SimpleInputFormDialog var1) {
      this.inputSet = new InputFormControlSet(this.minecraft, this);
   }

   protected void populateBodyElements(LinearLayout var1, SimpleInputFormDialog var2) {
      super.populateBodyElements(var1, var2);

      for(InputFormDialog.Input var4 : var2.inputs()) {
         InputFormControlSet var10000 = this.inputSet;
         Objects.requireNonNull(var1);
         var10000.addInput(var4, var1::addChild);
      }

   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, SimpleInputFormDialog var2) {
      var1.addToFooter(this.inputSet.createActionButton(var2.action()).build());
   }
}
