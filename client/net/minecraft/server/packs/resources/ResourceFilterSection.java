package net.minecraft.server.packs.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.util.IdentifierPattern;

public class ResourceFilterSection {
   private static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.list(IdentifierPattern.CODEC).fieldOf("block").forGetter((var0x) -> var0x.blockList)).apply(var0, ResourceFilterSection::new));
   public static final MetadataSectionType<ResourceFilterSection> TYPE;
   private final List<IdentifierPattern> blockList;

   public ResourceFilterSection(List<IdentifierPattern> var1) {
      super();
      this.blockList = List.copyOf(var1);
   }

   public boolean isNamespaceFiltered(String var1) {
      return this.blockList.stream().anyMatch((var1x) -> var1x.namespacePredicate().test(var1));
   }

   public boolean isPathFiltered(String var1) {
      return this.blockList.stream().anyMatch((var1x) -> var1x.pathPredicate().test(var1));
   }

   static {
      TYPE = new MetadataSectionType<ResourceFilterSection>("filter", CODEC);
   }
}
