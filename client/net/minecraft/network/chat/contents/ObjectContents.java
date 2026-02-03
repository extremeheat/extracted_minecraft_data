package net.minecraft.network.chat.contents;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.objects.ObjectInfo;
import net.minecraft.network.chat.contents.objects.ObjectInfos;

public record ObjectContents(ObjectInfo contents) implements ComponentContents {
   private static final String PLACEHOLDER = Character.toString('\ufffc');
   public static final MapCodec<ObjectContents> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ObjectInfos.CODEC.forGetter(ObjectContents::contents)).apply(i, ObjectContents::new));

   public ObjectContents {
      super();
   }

   public MapCodec<ObjectContents> codec() {
      return MAP_CODEC;
   }

   public <T> Optional<T> visit(final FormattedText.ContentConsumer<T> output) {
      return output.accept(this.contents.description());
   }

   public <T> Optional<T> visit(final FormattedText.StyledContentConsumer<T> output, final Style currentStyle) {
      return output.accept(currentStyle.withFont(this.contents.fontDescription()), PLACEHOLDER);
   }
}
