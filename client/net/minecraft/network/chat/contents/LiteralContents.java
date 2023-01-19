package net.minecraft.network.chat.contents;

import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public record LiteralContents(String b) implements ComponentContents {
   private final String text;

   public LiteralContents(String var1) {
      super();
      this.text = var1;
   }

   @Override
   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return var1.accept(this.text);
   }

   @Override
   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return var1.accept(var2, this.text);
   }

   @Override
   public String toString() {
      return "literal{" + this.text + "}";
   }
}
