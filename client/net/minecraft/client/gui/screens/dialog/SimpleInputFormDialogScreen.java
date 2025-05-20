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

   public SimpleInputFormDialogScreen(@Nullable Screen var1, SimpleInputFormDialog var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected void dialogInit(SimpleInputFormDialog var1, DialogConnectionAccess var2) {
      super.dialogInit(var1, var2);
      this.inputSet = new InputFormControlSet(this);
   }

   protected void populateBodyElements(LinearLayout var1, SimpleInputFormDialog var2, DialogConnectionAccess var3) {
      super.populateBodyElements(var1, var2, var3);

      for(InputFormDialog.Input var5 : var2.inputs()) {
         InputFormControlSet var10000 = this.inputSet;
         Objects.requireNonNull(var1);
         var10000.addInput(var5, var1::addChild);
      }

   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, SimpleInputFormDialog var2, DialogConnectionAccess var3) {
      super.updateHeaderAndFooter(var1, var2, var3);
      var1.addToFooter(this.inputSet.createActionButton(var2.action(), var3, this).build());
   }
}
