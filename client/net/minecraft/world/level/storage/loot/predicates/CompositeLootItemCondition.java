package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public abstract class CompositeLootItemCondition implements LootItemCondition {
   protected final List<LootItemCondition> terms;
   private final Predicate<LootContext> composedPredicate;

   protected CompositeLootItemCondition(List<LootItemCondition> var1, Predicate<LootContext> var2) {
      super();
      this.terms = var1;
      this.composedPredicate = var2;
   }

   protected static <T extends CompositeLootItemCondition> Codec<T> createCodec(Function<List<LootItemCondition>, T> var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(LootItemConditions.CODEC.listOf().fieldOf("terms").forGetter(var0xx -> var0xx.terms)).apply(var1, var0)
      );
   }

   protected static <T extends CompositeLootItemCondition> Codec<T> createInlineCodec(Function<List<LootItemCondition>, T> var0) {
      return LootItemConditions.CODEC.listOf().xmap(var0, var0x -> var0x.terms);
   }

   public final boolean test(LootContext var1) {
      return this.composedPredicate.test(var1);
   }

   @Override
   public void validate(ValidationContext var1) {
      LootItemCondition.super.validate(var1);

      for(int var2 = 0; var2 < this.terms.size(); ++var2) {
         this.terms.get(var2).validate(var1.forChild(".term[" + var2 + "]"));
      }
   }

   public abstract static class Builder implements LootItemCondition.Builder {
      private final com.google.common.collect.ImmutableList.Builder<LootItemCondition> terms = ImmutableList.builder();

      protected Builder(LootItemCondition.Builder... var1) {
         super();

         for(LootItemCondition.Builder var5 : var1) {
            this.terms.add(var5.build());
         }
      }

      public void addTerm(LootItemCondition.Builder var1) {
         this.terms.add(var1.build());
      }

      @Override
      public LootItemCondition build() {
         return this.create(this.terms.build());
      }

      protected abstract LootItemCondition create(List<LootItemCondition> var1);
   }
}
