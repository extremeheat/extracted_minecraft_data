package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public interface PlainTextContents extends ComponentContents {
   MapCodec<PlainTextContents> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContents::text)).apply(i, PlainTextContents::create));
   PlainTextContents EMPTY = new PlainTextContents() {
      public String toString() {
         return "empty";
      }

      public String text() {
         return "";
      }
   };

   static PlainTextContents create(final String text) {
      return (PlainTextContents)(text.isEmpty() ? EMPTY : new LiteralContents(text));
   }

   String text();

   default MapCodec<PlainTextContents> codec() {
      return MAP_CODEC;
   }

   public static record LiteralContents(String text) implements PlainTextContents {
      public LiteralContents {
         super();
      }

      public <T> Optional<T> visit(final FormattedText.ContentConsumer<T> output) {
         return output.accept(this.text);
      }

      public <T> Optional<T> visit(final FormattedText.StyledContentConsumer<T> output, final Style currentStyle) {
         return output.accept(currentStyle, this.text);
      }

      public String toString() {
         return "literal{" + this.text + "}";
      }
   }
}
