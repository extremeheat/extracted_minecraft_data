package net.minecraft.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import net.minecraft.resources.Identifier;

public class IdentifierPattern {
   public static final Codec<IdentifierPattern> CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.PATTERN.optionalFieldOf("namespace").forGetter((o) -> o.namespacePattern), ExtraCodecs.PATTERN.optionalFieldOf("path").forGetter((o) -> o.pathPattern)).apply(i, IdentifierPattern::new));
   private final Optional<Pattern> namespacePattern;
   private final Predicate<String> namespacePredicate;
   private final Optional<Pattern> pathPattern;
   private final Predicate<String> pathPredicate;
   private final Predicate<Identifier> locationPredicate;

   private IdentifierPattern(final Optional<Pattern> namespacePattern, final Optional<Pattern> pathPattern) {
      super();
      this.namespacePattern = namespacePattern;
      this.namespacePredicate = (Predicate)namespacePattern.map(Pattern::asPredicate).orElse((Predicate)(r) -> true);
      this.pathPattern = pathPattern;
      this.pathPredicate = (Predicate)pathPattern.map(Pattern::asPredicate).orElse((Predicate)(r) -> true);
      this.locationPredicate = (location) -> this.namespacePredicate.test(location.getNamespace()) && this.pathPredicate.test(location.getPath());
   }

   public Predicate<String> namespacePredicate() {
      return this.namespacePredicate;
   }

   public Predicate<String> pathPredicate() {
      return this.pathPredicate;
   }

   public Predicate<Identifier> locationPredicate() {
      return this.locationPredicate;
   }
}
