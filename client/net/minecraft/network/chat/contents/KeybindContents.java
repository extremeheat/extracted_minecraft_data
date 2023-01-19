package net.minecraft.network.chat.contents;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public class KeybindContents implements ComponentContents {
   private final String name;
   @Nullable
   private Supplier<Component> nameResolver;

   public KeybindContents(String var1) {
      super();
      this.name = var1;
   }

   private Component getNestedComponent() {
      if (this.nameResolver == null) {
         this.nameResolver = KeybindResolver.keyResolver.apply(this.name);
      }

      return this.nameResolver.get();
   }

   @Override
   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return this.getNestedComponent().visit(var1);
   }

   @Override
   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return this.getNestedComponent().visit(var1, var2);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         if (var1 instanceof KeybindContents var2 && this.name.equals(var2.name)) {
            return true;
         }

         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.name.hashCode();
   }

   @Override
   public String toString() {
      return "keybind{" + this.name + "}";
   }

   public String getName() {
      return this.name;
   }
}
