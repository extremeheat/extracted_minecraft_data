package net.minecraft.client.gui.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;

public class NarrationThunk<T> {
   private final T contents;
   private final BiConsumer<Consumer<String>, T> converter;
   public static final NarrationThunk<?> EMPTY;

   private NarrationThunk(final T contents, final BiConsumer<Consumer<String>, T> converter) {
      super();
      this.contents = contents;
      this.converter = converter;
   }

   public static NarrationThunk<?> from(final String text) {
      return new NarrationThunk(text, Consumer::accept);
   }

   public static NarrationThunk<?> from(final Component text) {
      return new NarrationThunk(text, (o, c) -> o.accept(c.getString()));
   }

   public static NarrationThunk<?> from(final List<Component> lines) {
      return new NarrationThunk(lines, (o, c) -> lines.stream().map(Component::getString).forEach(o));
   }

   public void getText(final Consumer<String> output) {
      this.converter.accept(output, this.contents);
   }

   public boolean equals(final Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof NarrationThunk)) {
         return false;
      } else {
         NarrationThunk<?> thunk = (NarrationThunk)o;
         return thunk.converter == this.converter && thunk.contents.equals(this.contents);
      }
   }

   public int hashCode() {
      int result = this.contents.hashCode();
      result = 31 * result + this.converter.hashCode();
      return result;
   }

   static {
      EMPTY = new NarrationThunk(Unit.INSTANCE, (o, c) -> {
      });
   }
}
