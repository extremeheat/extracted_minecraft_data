package net.minecraft.server.packs.resources;

import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.ExtraCodecs;
import org.slf4j.Logger;

public class ResourceFilterSection {
   static final Logger LOGGER = LogUtils.getLogger();
   static final Codec<ResourceFilterSection> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.list(ResourceFilterSection.ResourceLocationPattern.CODEC).fieldOf("block").forGetter((var0x) -> {
         return var0x.blockList;
      })).apply(var0, ResourceFilterSection::new);
   });
   public static final MetadataSectionSerializer<ResourceFilterSection> SERIALIZER = new MetadataSectionSerializer<ResourceFilterSection>() {
      public String getMetadataSectionName() {
         return "filter";
      }

      public ResourceFilterSection fromJson(JsonObject var1) {
         DataResult var10000 = ResourceFilterSection.CODEC.parse(JsonOps.INSTANCE, var1);
         Logger var10002 = ResourceFilterSection.LOGGER;
         Objects.requireNonNull(var10002);
         return (ResourceFilterSection)var10000.getOrThrow(false, var10002::error);
      }

      // $FF: synthetic method
      public Object fromJson(JsonObject var1) {
         return this.fromJson(var1);
      }
   };
   private final List<ResourceLocationPattern> blockList;

   public ResourceFilterSection(List<ResourceLocationPattern> var1) {
      super();
      this.blockList = List.copyOf(var1);
   }

   public boolean isNamespaceFiltered(String var1) {
      return this.blockList.stream().anyMatch((var1x) -> {
         return var1x.namespacePredicate.test(var1);
      });
   }

   public boolean isPathFiltered(String var1) {
      return this.blockList.stream().anyMatch((var1x) -> {
         return var1x.pathPredicate.test(var1);
      });
   }

   static class ResourceLocationPattern implements Predicate<ResourceLocation> {
      static final Codec<ResourceLocationPattern> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((var0x) -> {
            return var0x.namespacePattern;
         }), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((var0x) -> {
            return var0x.pathPattern;
         })).apply(var0, ResourceLocationPattern::new);
      });
      private final Optional<Pattern> namespacePattern;
      final Predicate<String> namespacePredicate;
      private final Optional<Pattern> pathPattern;
      final Predicate<String> pathPredicate;

      private ResourceLocationPattern(Optional<Pattern> var1, Optional<Pattern> var2) {
         super();
         this.namespacePattern = var1;
         this.namespacePredicate = (Predicate)var1.map(Pattern::asPredicate).orElse((var0) -> {
            return true;
         });
         this.pathPattern = var2;
         this.pathPredicate = (Predicate)var2.map(Pattern::asPredicate).orElse((var0) -> {
            return true;
         });
      }

      public boolean test(ResourceLocation var1) {
         return this.namespacePredicate.test(var1.getNamespace()) && this.pathPredicate.test(var1.getPath());
      }

      // $FF: synthetic method
      public boolean test(Object var1) {
         return this.test((ResourceLocation)var1);
      }
   }
}
