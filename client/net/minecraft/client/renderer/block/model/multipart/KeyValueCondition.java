package net.minecraft.client.renderer.block.model.multipart;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.StateHolder;
import net.minecraft.world.level.block.state.properties.Property;
import org.slf4j.Logger;

public record KeyValueCondition(Map<String, Terms> tests) implements Condition {
   static final Logger LOGGER = LogUtils.getLogger();
   public static final Codec<KeyValueCondition> CODEC;

   public KeyValueCondition(Map<String, Terms> var1) {
      super();
      this.tests = var1;
   }

   public <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> var1) {
      ArrayList var2 = new ArrayList(this.tests.size());
      this.tests.forEach((var2x, var3) -> var2.add(instantiate(var1, var2x, var3)));
      return Util.allOf(var2);
   }

   private static <O, S extends StateHolder<O, S>> Predicate<S> instantiate(StateDefinition<O, S> var0, String var1, Terms var2) {
      Property var3 = var0.getProperty(var1);
      if (var3 == null) {
         throw new IllegalArgumentException(String.format(Locale.ROOT, "Unknown property '%s' on '%s'", var1, var0.getOwner()));
      } else {
         return var2.instantiate(var0.getOwner(), var3);
      }
   }

   static {
      CODEC = ExtraCodecs.nonEmptyMap(Codec.unboundedMap(Codec.STRING, KeyValueCondition.Terms.CODEC)).xmap(KeyValueCondition::new, KeyValueCondition::tests);
   }

   public static record Terms(List<Term> entries) {
      private static final char SEPARATOR = '|';
      private static final Joiner JOINER = Joiner.on('|');
      private static final Splitter SPLITTER = Splitter.on('|');
      private static final Codec<String> LEGACY_REPRESENTATION_CODEC;
      public static final Codec<Terms> CODEC;

      public Terms(List<Term> var1) {
         super();
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("Empty value for property");
         } else {
            this.entries = var1;
         }
      }

      public static DataResult<Terms> parse(String var0) {
         List var1 = SPLITTER.splitToStream(var0).map(Term::parse).toList();
         if (var1.isEmpty()) {
            return DataResult.error(() -> "Empty value for property");
         } else {
            for(Term var3 : var1) {
               if (var3.value.isEmpty()) {
                  return DataResult.error(() -> "Empty term in value '" + var0 + "'");
               }
            }

            return DataResult.success(new Terms(var1));
         }
      }

      public String toString() {
         return JOINER.join(this.entries);
      }

      public <O, S extends StateHolder<O, S>, T extends Comparable<T>> Predicate<S> instantiate(O var1, Property<T> var2) {
         Predicate var3 = Util.anyOf(Lists.transform(this.entries, (var3x) -> this.instantiate(var1, var2, var3x)));
         ArrayList var4 = new ArrayList(var2.getPossibleValues());
         int var5 = var4.size();
         var4.removeIf(var3.negate());
         int var6 = var4.size();
         if (var6 == 0) {
            KeyValueCondition.LOGGER.warn("Condition {} for property {} on {} is always false", new Object[]{this, var2.getName(), var1});
            return (var0) -> false;
         } else {
            int var7 = var5 - var6;
            if (var7 == 0) {
               KeyValueCondition.LOGGER.warn("Condition {} for property {} on {} is always true", new Object[]{this, var2.getName(), var1});
               return (var0) -> true;
            } else {
               boolean var8;
               ArrayList var9;
               if (var6 <= var7) {
                  var8 = false;
                  var9 = var4;
               } else {
                  var8 = true;
                  ArrayList var10 = new ArrayList(var2.getPossibleValues());
                  var10.removeIf(var3);
                  var9 = var10;
               }

               if (var9.size() == 1) {
                  Comparable var11 = (Comparable)var9.getFirst();
                  return (var3x) -> {
                     Comparable var4 = var3x.getValue(var2);
                     return var11.equals(var4) ^ var8;
                  };
               } else {
                  return (var3x) -> {
                     Comparable var4 = var3x.getValue(var2);
                     return var9.contains(var4) ^ var8;
                  };
               }
            }
         }
      }

      private <T extends Comparable<T>> T getValueOrThrow(Object var1, Property<T> var2, String var3) {
         Optional var4 = var2.getValue(var3);
         if (var4.isEmpty()) {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown value '%s' for property '%s' on '%s' in '%s'", var3, var2, var1, this));
         } else {
            return (T)(var4.get());
         }
      }

      private <T extends Comparable<T>> Predicate<T> instantiate(Object var1, Property<T> var2, Term var3) {
         Comparable var4 = this.getValueOrThrow(var1, var2, var3.value);
         return var3.negated ? (var1x) -> !var1x.equals(var4) : (var1x) -> var1x.equals(var4);
      }

      static {
         LEGACY_REPRESENTATION_CODEC = Codec.either(Codec.INT, Codec.BOOL).flatComapMap((var0) -> (String)var0.map(String::valueOf, String::valueOf), (var0) -> DataResult.error(() -> "This codec can't be used for encoding"));
         CODEC = Codec.withAlternative(Codec.STRING, LEGACY_REPRESENTATION_CODEC).comapFlatMap(Terms::parse, Terms::toString);
      }
   }

   public static record Term(String value, boolean negated) {
      final String value;
      final boolean negated;
      private static final String NEGATE = "!";

      public Term(String var1, boolean var2) {
         super();
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("Empty term");
         } else {
            this.value = var1;
            this.negated = var2;
         }
      }

      public static Term parse(String var0) {
         return var0.startsWith("!") ? new Term(var0.substring(1), true) : new Term(var0, false);
      }

      public String toString() {
         return this.negated ? "!" + this.value : this.value;
      }
   }
}
