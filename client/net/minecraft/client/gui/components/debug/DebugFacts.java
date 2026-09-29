package net.minecraft.client.gui.components.debug;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public class DebugFacts {
   private static final Style VALUE_STYLE;

   private DebugFacts() {
      super();
   }

   public static Component value(final String value) {
      return Component.literal(value).withStyle(VALUE_STYLE);
   }

   public static Component value(final int value) {
      return Component.literal(Integer.toString(value)).withStyle(VALUE_STYLE);
   }

   public static Component filler(final String value) {
      return Component.literal(value);
   }

   static {
      VALUE_STYLE = Style.EMPTY.withColor(-1);
   }
}
