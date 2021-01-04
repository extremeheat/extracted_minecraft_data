package net.minecraft.network.chat;

import java.util.function.Function;
import java.util.function.Supplier;

public class KeybindComponent extends BaseComponent {
   public static Function<String, Supplier<String>> keyResolver = (var0) -> {
      return () -> {
         return var0;
      };
   };
   private final String name;
   private Supplier<String> nameResolver;

   public KeybindComponent(String var1) {
      super();
      this.name = var1;
   }

   public String getContents() {
      if (this.nameResolver == null) {
         this.nameResolver = (Supplier)keyResolver.apply(this.name);
      }

      return (String)this.nameResolver.get();
   }

   public KeybindComponent copy() {
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
      return "KeybindComponent{keybind='" + this.name + '\'' + ", siblings=" + this.siblings + ", style=" + this.getStyle() + '}';
   }

   public String getName() {
      return this.name;
   }

   // $FF: synthetic method
   public Component copy() {
      return this.copy();
   }
}
