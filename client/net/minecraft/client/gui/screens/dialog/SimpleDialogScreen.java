package net.minecraft.client.gui.screens.dialog;

import javax.annotation.Nullable;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.server.dialog.ClickAction;
import net.minecraft.server.dialog.SimpleDialog;

public class SimpleDialogScreen<T extends SimpleDialog> extends DialogScreen<T> {
   public SimpleDialogScreen(@Nullable Screen var1, T var2) {
      super(var1, var2);
   }

   protected void updateHeaderAndFooter(HeaderAndFooterLayout var1, T var2) {
      LinearLayout var3 = LinearLayout.horizontal().spacing(8);

      for(ClickAction var5 : var2.mainActions()) {
         var3.addChild(this.createClickActionButton(var5).build());
      }

      var1.addToFooter(var3);
   }
}
