package net.minecraft.network.chat.contents;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;

public class KeybindResolver {
   static Function<String, Supplier<Component>> keyResolver = (var0) -> {
      return () -> {
         return Component.literal(var0);
      };
   };

   public KeybindResolver() {
      super();
   }

   public static void setKeyResolver(Function<String, Supplier<Component>> var0) {
      keyResolver = var0;
   }
}
