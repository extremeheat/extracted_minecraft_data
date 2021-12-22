package net.minecraft.network.chat;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindComponent extends BaseComponent {
   private static Function<String, Supplier<Component>> keyResolver = (var0) -> {
      return () -> {
         return new TextComponent(var0);
      };
   };
   private final String name;
   private Supplier<Component> nameResolver;

   public KeybindComponent(String var1) {
      super();
      this.name = var1;
   }

   public static void setKeyResolver(Function<String, Supplier<Component>> var0) {
      keyResolver = var0;
   }

   private Component getNestedComponent() {
      if (this.nameResolver == null) {
         this.nameResolver = (Supplier)keyResolver.apply(this.name);
      }

      return (Component)this.nameResolver.get();
   }

   public <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> var1) {
      return this.getNestedComponent().visit(var1);
   }

   public <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return this.getNestedComponent().visit(var1, var2);
   }

   public KeybindComponent plainCopy() {
      return new KeybindComponent(this.name);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof KeybindComponent)) {
         return false;
      } else {
         KeybindComponent var2 = (KeybindComponent)var1;
         return this.name.equals(var2.name) && super.equals(var1);
      }
   }

   public String toString() {
      String var10000 = this.name;
      return "KeybindComponent{keybind='" + var10000 + "', siblings=" + this.siblings + ", style=" + this.getStyle() + "}";
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   public BaseComponent plainCopy() {
      return this.plainCopy();
   }

   // $FF: synthetic method
   public MutableComponent plainCopy() {
      return this.plainCopy();
   }
}
