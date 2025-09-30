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
   public static final MapCodec<ObjectContents> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(ObjectInfos.CODEC.forGetter(ObjectContents::contents)).apply(var0, ObjectContents::new));

   public ObjectContents(ObjectInfo var1) {
      super();
      this.contents = var1;
   }

   public MapCodec<ObjectContents> codec() {
      return MAP_CODEC;
   }

   public <T> Optional<T> visit(FormattedText.ContentConsumer<T> var1) {
      return var1.accept(this.contents.description());
   }

   public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> var1, Style var2) {
      return var1.accept(var2.withFont(this.contents.fontDescription()), PLACEHOLDER);
   }
}
