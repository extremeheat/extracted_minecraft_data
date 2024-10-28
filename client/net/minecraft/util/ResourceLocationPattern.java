package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocationPattern {
   public static final Codec<ResourceLocationPattern> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((var0x) -> {
         return var0x.namespacePattern;
      }), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((var0x) -> {
         return var0x.pathPattern;
      })).apply(var0, ResourceLocationPattern::new);
   });
   private final Optional<Pattern> namespacePattern;
   private final Predicate<String> namespacePredicate;
   private final Optional<Pattern> pathPattern;
   private final Predicate<String> pathPredicate;
   private final Predicate<ResourceLocation> locationPredicate;

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
      this.locationPredicate = (var1x) -> {
         return this.namespacePredicate.test(var1x.getNamespace()) && this.pathPredicate.test(var1x.getPath());
      };
   }

   public Predicate<String> namespacePredicate() {
      return this.namespacePredicate;
   }

   public Predicate<String> pathPredicate() {
      return this.pathPredicate;
   }

   public Predicate<ResourceLocation> locationPredicate() {
      return this.locationPredicate;
   }
}
