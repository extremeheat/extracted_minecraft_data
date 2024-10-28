package net.minecraft.tags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;

public record TagFile(List<TagEntry> entries, boolean replace) {
   public static final Codec<TagFile> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(TagEntry.CODEC.listOf().fieldOf("values").forGetter(TagFile::entries), Codec.BOOL.optionalFieldOf("replace", false).forGetter(TagFile::replace)).apply(var0, TagFile::new);
   });

   public TagFile(List<TagEntry> entries, boolean replace) {
      super();
      this.entries = entries;
      this.replace = replace;
   }

   public List<TagEntry> entries() {
      return this.entries;
   }

   public boolean replace() {
      return this.replace;
   }
}
