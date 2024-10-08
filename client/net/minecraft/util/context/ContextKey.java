package net.minecraft.util.context;

import net.minecraft.resources.ResourceLocation;

public class ContextKey<T> {
   private final ResourceLocation name;

   public ContextKey(ResourceLocation var1) {
      super();
      this.name = var1;
   }

   public static <T> ContextKey<T> vanilla(String var0) {
      return new ContextKey<>(ResourceLocation.withDefaultNamespace(var0));
   }

   public ResourceLocation name() {
      return this.name;
   }

   @Override
   public String toString() {
      return "<parameter " + this.name + ">";
   }
}
