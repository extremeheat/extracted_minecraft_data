package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;

public class ResourceFilterSection {
   static final Logger LOGGER = LogUtils.getLogger();
   static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.list(ResourceFilterSection.ResourceLocationPattern.CODEC).fieldOf("block").forGetter(var0x -> var0x.blockList))
            .apply(var0, ResourceFilterSection::new)
   );
   public static final MetadataSectionSerializer<ResourceFilterSection> SERIALIZER = new MetadataSectionSerializer<ResourceFilterSection>() {
      @Override
      public String getMetadataSectionName() {
         return "filter";
      }

      public ResourceFilterSection fromJson(JsonObject var1) {
         return (ResourceFilterSection)ResourceFilterSection.CODEC.parse(JsonOps.INSTANCE, var1).getOrThrow(false, ResourceFilterSection.LOGGER::error);
      }
   };
   private final List<ResourceFilterSection.ResourceLocationPattern> blockList;

   public ResourceFilterSection(List<ResourceFilterSection.ResourceLocationPattern> var1) {
      super();
      this.blockList = List.copyOf(var1);
   }

   public boolean isNamespaceFiltered(String var1) {
      return this.blockList.stream().anyMatch(var1x -> var1x.namespacePredicate.test(var1));
   }

   public boolean isPathFiltered(String var1) {
      return this.blockList.stream().anyMatch(var1x -> var1x.pathPredicate.test(var1));
   }

   static class ResourceLocationPattern implements Predicate<ResourceLocation> {
      static final Codec<ResourceFilterSection.ResourceLocationPattern> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter(var0x -> var0x.namespacePattern),
                  ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter(var0x -> var0x.pathPattern)
               )
               .apply(var0, ResourceFilterSection.ResourceLocationPattern::new)
      );
      private final Optional<Pattern> namespacePattern;
      final Predicate<String> namespacePredicate;
      private final Optional<Pattern> pathPattern;
      final Predicate<String> pathPredicate;

      private ResourceLocationPattern(Optional<Pattern> var1, Optional<Pattern> var2) {
         super();
         this.namespacePattern = var1;
         this.namespacePredicate = var1.map(Pattern::asPredicate).orElse(var0 -> true);
         this.pathPattern = var2;
         this.pathPredicate = var2.map(Pattern::asPredicate).orElse(var0 -> true);
      }

      public boolean test(ResourceLocation var1) {
         return this.namespacePredicate.test(var1.getNamespace()) && this.pathPredicate.test(var1.getPath());
      }
   }
}
