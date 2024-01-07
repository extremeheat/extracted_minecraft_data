package net.minecraft.tags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;

public record TagFile(List<TagEntry> b, boolean c) {
   private final List<TagEntry> entries;
   private final boolean replace;
   public static final Codec<TagFile> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               TagEntry.CODEC.listOf().fieldOf("values").forGetter(TagFile::entries), Codec.BOOL.optionalFieldOf("replace", false).forGetter(TagFile::replace)
            )
            .apply(var0, TagFile::new)
   );

   public TagFile(List<TagEntry> var1, boolean var2) {
      super();
      this.entries = var1;
      this.replace = var2;
   }
}
