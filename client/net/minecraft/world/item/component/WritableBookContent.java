package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;

public record WritableBookContent(List<Filterable<String>> pages) implements BookContent<String, WritableBookContent> {
   public static final WritableBookContent EMPTY = new WritableBookContent(List.of());
   public static final int PAGE_EDIT_LENGTH = 1024;
   public static final int MAX_PAGES = 100;
   private static final Codec<Filterable<String>> PAGE_CODEC = Filterable.codec(Codec.string(0, 1024));
   public static final Codec<List<Filterable<String>>> PAGES_CODEC;
   public static final Codec<WritableBookContent> CODEC;
   public static final StreamCodec<ByteBuf, WritableBookContent> STREAM_CODEC;

   public WritableBookContent(List<Filterable<String>> pages) {
      super();
      if (pages.size() > 100) {
         throw new IllegalArgumentException("Got " + pages.size() + " pages, but maximum is 100");
      } else {
         this.pages = pages;
      }
   }

   public Stream<String> getPages(boolean var1) {
      return this.pages.stream().map((var1x) -> {
         return (String)var1x.get(var1);
      });
   }

   public WritableBookContent withReplacedPages(List<Filterable<String>> var1) {
      return new WritableBookContent(var1);
   }

   public List<Filterable<String>> pages() {
      return this.pages;
   }

   // $FF: synthetic method
   public Object withReplacedPages(final List var1) {
      return this.withReplacedPages(var1);
   }

   static {
      PAGES_CODEC = PAGE_CODEC.sizeLimitedListOf(100);
      CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(PAGES_CODEC.optionalFieldOf("pages", List.of()).forGetter(WritableBookContent::pages)).apply(var0, WritableBookContent::new);
      });
      STREAM_CODEC = Filterable.streamCodec(ByteBufCodecs.stringUtf8(1024)).apply(ByteBufCodecs.list(100)).map(WritableBookContent::new, WritableBookContent::pages);
   }
}
