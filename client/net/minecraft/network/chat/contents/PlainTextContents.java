package net.minecraft.network.chat.contents;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;

public interface PlainTextContents extends ComponentContents {
   MapCodec<PlainTextContents> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(Codec.STRING.fieldOf("text").forGetter(PlainTextContents::text)).apply(var0, PlainTextContents::create));
   ComponentContents.Type<PlainTextContents> TYPE = new ComponentContents.Type<PlainTextContents>(CODEC, "text");
   PlainTextContents EMPTY = new PlainTextContents() {
      public String toString() {
         return "empty";
      }

      public String text() {
         return "";
      }
   };

   static PlainTextContents create(String var0) {
      return (PlainTextContents)(var0.isEmpty() ? EMPTY : new LiteralContents(var0));
   }

   String text();

   default ComponentContents.Type<?> type() {
      return TYPE;
   }

   public static record LiteralContents(String text) implements PlainTextContents {
      public LiteralContents(String var1) {
         super();
         this.text = var1;
      }

      public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
         return var1.accept(this.text);
      }

      public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
         return var1.accept(var2, this.text);
      }

      public String toString() {
         return "literal{" + this.text + "}";
      }
   }
}
