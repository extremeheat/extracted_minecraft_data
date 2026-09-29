package net.minecraft.client.gui.components.debug;

import java.util.Locale;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

public class DebugFact {
   private static final Style VALUE_STYLE;
   private final MutableComponent component = Component.empty();

   public DebugFact() {
      super();
   }

   public DebugFact text(final Component component) {
      this.component.append(component);
      return this;
   }

   public DebugFact text(final String string) {
      this.component.append(string);
      return this;
   }

   public DebugFact value(final String value) {
      this.component.append((Component)Component.literal(value).withStyle(VALUE_STYLE));
      return this;
   }

   public DebugFact value(final long value) {
      return this.value(String.valueOf(value));
   }

   public DebugFact value(final int value) {
      return this.value(String.valueOf(value));
   }

   public DebugFact formattedValue(final String format, final Object... args) {
      return this.value(String.format(Locale.ROOT, format, args));
   }

   public Component result() {
      return this.component;
   }

   static {
      VALUE_STYLE = Style.EMPTY.withColor(-1);
   }
}
