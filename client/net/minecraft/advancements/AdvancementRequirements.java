package net.minecraft.advancements;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record AdvancementRequirements(List<List<String>> requirements) {
   public static final Codec<AdvancementRequirements> CODEC;
   public static final StreamCodec<ByteBuf, AdvancementRequirements> STREAM_CODEC;
   public static final AdvancementRequirements EMPTY;

   public AdvancementRequirements {
      super();
   }

   public static AdvancementRequirements allOf(final Collection<String> criteria) {
      return new AdvancementRequirements(criteria.stream().map(List::of).toList());
   }

   public static AdvancementRequirements anyOf(final Collection<String> criteria) {
      return new AdvancementRequirements(List.of(List.copyOf(criteria)));
   }

   public int size() {
      return this.requirements.size();
   }

   public boolean test(final Predicate<String> predicate) {
      if (this.requirements.isEmpty()) {
         return false;
      } else {
         for(List<String> set : this.requirements) {
            if (!anyMatch(set, predicate)) {
               return false;
            }
         }

         return true;
      }
   }

   public int count(final Predicate<String> predicate) {
      int count = 0;

      for(List<String> set : this.requirements) {
         if (anyMatch(set, predicate)) {
            ++count;
         }
      }

      return count;
   }

   private static boolean anyMatch(final List<String> criteria, final Predicate<String> predicate) {
      for(String criterion : criteria) {
         if (predicate.test(criterion)) {
            return true;
         }
      }

      return false;
   }

   public DataResult<AdvancementRequirements> validate(final Set<String> expectedCriteria) {
      Set<String> referencedCriteria = new ObjectOpenHashSet();

      for(List<String> set : this.requirements) {
         if (set.isEmpty() && expectedCriteria.isEmpty()) {
            return DataResult.error(() -> "Requirement entry cannot be empty");
         }

         referencedCriteria.addAll(set);
      }

      if (!expectedCriteria.equals(referencedCriteria)) {
         Set<String> missingCriteria = Sets.difference(expectedCriteria, referencedCriteria);
         Set<String> unknownCriteria = Sets.difference(referencedCriteria, expectedCriteria);
         return DataResult.error(() -> {
            String var10000 = String.valueOf(missingCriteria);
            return "Advancement completion requirements did not exactly match specified criteria. Missing: " + var10000 + ". Unknown: " + String.valueOf(unknownCriteria);
         });
      } else {
         return DataResult.success(this);
      }
   }

   public boolean isEmpty() {
      return this.requirements.isEmpty();
   }

   public String toString() {
      return this.requirements.toString();
   }

   public Set<String> names() {
      Set<String> names = new ObjectOpenHashSet();

      for(List<String> set : this.requirements) {
         names.addAll(set);
      }

      return names;
   }

   static {
      CODEC = Codec.STRING.listOf().listOf().xmap(AdvancementRequirements::new, AdvancementRequirements::requirements);
      STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()).apply(ByteBufCodecs.list()).map(AdvancementRequirements::new, AdvancementRequirements::requirements);
      EMPTY = new AdvancementRequirements(List.of());
   }

   public interface Strategy {
      Strategy AND = AdvancementRequirements::allOf;
      Strategy OR = AdvancementRequirements::anyOf;

      AdvancementRequirements create(Collection<String> criteria);
   }
}
