package net.minecraft.server.packs.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.ResourceLocationPattern;

public class ResourceFilterSection {
   private static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.list(ResourceLocationPattern.CODEC).fieldOf("block").forGetter(var0x -> var0x.blockList))
            .apply(var0, ResourceFilterSection::new)
   );
   public static final MetadataSectionType<ResourceFilterSection> TYPE = MetadataSectionType.fromCodec("filter", CODEC);
   private final List<ResourceLocationPattern> blockList;

   public ResourceFilterSection(List<ResourceLocationPattern> var1) {
      super();
      this.blockList = List.copyOf(var1);
   }

   public boolean isNamespaceFiltered(String var1) {
      return this.blockList.stream().anyMatch(var1x -> var1x.namespacePredicate().test(var1));
   }

   public boolean isPathFiltered(String var1) {
      return this.blockList.stream().anyMatch(var1x -> var1x.pathPredicate().test(var1));
   }
}
