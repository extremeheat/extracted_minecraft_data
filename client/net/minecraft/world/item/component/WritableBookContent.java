package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import io.netty.buffer.ByteBuf;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;

public record WritableBookContent(List<Filterable<String>> f) implements BookContent<String, WritableBookContent> {
   private final List<Filterable<String>> pages;
   public static final WritableBookContent EMPTY = new WritableBookContent(List.of());
   public static final int PAGE_EDIT_LENGTH = 1024;
   private static final Codec<Filterable<String>> PAGE_CODEC = Filterable.codec(ExtraCodecs.sizeLimitedString(0, 1024));
   public static final Codec<List<Filterable<String>>> PAGES_CODEC = ExtraCodecs.sizeLimitedList(PAGE_CODEC.listOf(), 100);
   public static final Codec<WritableBookContent> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ExtraCodecs.strictOptionalField(PAGES_CODEC, "pages", List.of()).forGetter(WritableBookContent::pages))
            .apply(var0, WritableBookContent::new)
   );
   public static final StreamCodec<ByteBuf, WritableBookContent> STREAM_CODEC = Filterable.streamCodec(ByteBufCodecs.stringUtf8(1024))
      .<List<Filterable<String>>>apply(ByteBufCodecs.list(100))
      .map(WritableBookContent::new, WritableBookContent::pages);

   public WritableBookContent(List<Filterable<String>> var1) {
      super();
      this.pages = var1;
   }

   public Stream<String> getPages(boolean var1) {
      return this.pages.stream().map(var1x -> (String)var1x.get(var1));
   }

   public WritableBookContent withReplacedPages(List<Filterable<String>> var1) {
      return new WritableBookContent(var1);
   }
}
