package net.minecraft.client.gui.screens.dialog;

import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.SimpleDialog;

public class SimpleDialogScreen<T extends SimpleDialog> extends DialogScreen<T> {
   public SimpleDialogScreen(@Nullable Screen var1, T var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, DialogControlSet var2, T var3, DialogConnectionAccess var4) {
      super.updateHeaderAndFooter(var1, var2, var3, var4);
      LinearLayout var5 = LinearLayout.horizontal().spacing(8);

      for(ActionButton var7 : var3.mainActions()) {
         var5.addChild(var2.createActionButton(var7).build());
      }

      var1.addToFooter(var5);
   }
}
