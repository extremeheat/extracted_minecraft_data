package net.minecraft.client.gui.layouts;

import java.util.function.Consumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.network.chat.Component;

public class CommonLayouts {
   private static final int LABEL_SPACING = 4;

   private CommonLayouts() {
      super();
   }

   public static Layout labeledElement(Font var0, LayoutElement var1, Component var2) {
      return labeledElement(var0, var1, var2, (var0x) -> {
      });
   }

   public static Layout labeledElement(Font var0, LayoutElement var1, Component var2, Consumer<LayoutSettings> var3) {
      LinearLayout var4 = LinearLayout.vertical().spacing(4);
      var4.addChild(new StringWidget(var2, var0));
      var4.addChild(var1, var3);
      return var4;
   }
}
