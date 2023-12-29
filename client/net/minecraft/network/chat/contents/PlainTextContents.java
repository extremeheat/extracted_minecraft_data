package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public interface PlainTextContents extends ComponentContents {
   MapCodec<PlainTextContents> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContents::text)).apply(var0, PlainTextContents::create)
   );
   ComponentContents.Type<PlainTextContents> TYPE = new ComponentContents.Type<>(CODEC, "text");
   PlainTextContents EMPTY = new PlainTextContents() {
      @Override
      public String toString() {
         return "empty";
      }

      @Override
      public String text() {
         return "";
      }
   };

   static PlainTextContents create(String var0) {
      return (PlainTextContents)(var0.isEmpty() ? EMPTY : new PlainTextContents.LiteralContents(var0));
   }

   String text();

   @Override
   default ComponentContents.Type<?> type() {
      return TYPE;
   }

   public static record LiteralContents(String d) implements PlainTextContents {
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
}
