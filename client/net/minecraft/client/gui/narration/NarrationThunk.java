package net.minecraft.client.gui.narration;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;

public class NarrationThunk<T> {
   private final T contents;
   private final BiConsumer<Consumer<String>, T> converter;
   public static final NarrationThunk<?> EMPTY = new NarrationThunk<>(Unit.INSTANCE, (var0, var1) -> {
   });

   private NarrationThunk(T var1, BiConsumer<Consumer<String>, T> var2) {
      super();
      this.contents = (T)var1;
      this.converter = var2;
   }

   public static NarrationThunk<?> from(String var0) {
      return new NarrationThunk<>(var0, Consumer::accept);
   }

   public static NarrationThunk<?> from(Component var0) {
      return new NarrationThunk<>(var0, (var0x, var1) -> var0x.accept(var1.getString()));
   }

   public static NarrationThunk<?> from(List<Component> var0) {
      return new NarrationThunk<>(var0, (var1, var2) -> var0.stream().map(Component::getString).forEach(var1));
   }

   public void getText(Consumer<String> var1) {
      this.converter.accept(var1, this.contents);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof NarrationThunk var2) ? false : var2.converter == this.converter && var2.contents.equals(this.contents);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.contents.hashCode();
      return 31 * var1 + this.converter.hashCode();
   }
}
