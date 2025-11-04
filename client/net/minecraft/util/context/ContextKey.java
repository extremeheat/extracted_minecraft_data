package net.minecraft.util.context;

import net.minecraft.resources.Identifier;

public class ContextKey<T> {
   private final Identifier name;

   public ContextKey(Identifier var1) {
      super();
      this.name = var1;
   }

   public static <T> ContextKey<T> vanilla(String var0) {
      return new ContextKey<T>(Identifier.withDefaultNamespace(var0));
   }

   public Identifier name() {
      return this.name;
   }

   public String toString() {
      return "<parameter " + String.valueOf(this.name) + ">";
   }
}
