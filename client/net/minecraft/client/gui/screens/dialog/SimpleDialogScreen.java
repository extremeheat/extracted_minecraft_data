package net.minecraft.client.gui.screens.dialog;

import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.SimpleDialog;

public class SimpleDialogScreen<T extends SimpleDialog> extends DialogScreen<T> {
   public SimpleDialogScreen(@Nullable Screen var1, T var2, DialogConnectionAccess var3) {
      super(var1, var2, var3);
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, T var2, DialogConnectionAccess var3) {
      super.updateHeaderAndFooter(var1, var2, var3);
      LinearLayout var4 = LinearLayout.horizontal().spacing(8);

      for(ClickAction var6 : var2.mainActions()) {
         var4.addChild(this.createClickActionButton(var6).build());
      }

      var1.addToFooter(var4);
   }
}
