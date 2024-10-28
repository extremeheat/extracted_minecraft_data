package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

   protected static <T extends CompositeLootItemCondition> MapCodec<T> createCodec(Function<List<LootItemCondition>, T> var0) {
      return RecordCodecBuilder.mapCodec((var1) -> {
         return var1.group(LootItemCondition.DIRECT_CODEC.listOf().fieldOf("terms").forGetter((var0x) -> {
            return var0x.terms;
         })).apply(var1, var0);
      });
   }

   protected static <T extends CompositeLootItemCondition> Codec<T> createInlineCodec(Function<List<LootItemCondition>, T> var0) {
      return LootItemCondition.DIRECT_CODEC.listOf().xmap(var0, (var0x) -> {
         return var0x.terms;
      });
   }

   public final boolean test(LootContext var1) {
      return this.composedPredicate.test(var1);
   }

   public void validate(ValidationContext var1) {
      LootItemCondition.super.validate(var1);

      for(int var2 = 0; var2 < this.terms.size(); ++var2) {
         ((LootItemCondition)this.terms.get(var2)).validate(var1.forChild(".term[" + var2 + "]"));
      }

   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }

   public abstract static class Builder implements LootItemCondition.Builder {
      private final ImmutableList.Builder<LootItemCondition> terms = ImmutableList.builder();

      protected Builder(LootItemCondition.Builder... var1) {
         super();
         LootItemCondition.Builder[] var2 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            LootItemCondition.Builder var5 = var2[var4];
            this.terms.add(var5.build());
         }

      }

      public void addTerm(LootItemCondition.Builder var1) {
         this.terms.add(var1.build());
      }

      public LootItemCondition build() {
         return this.create(this.terms.build());
      }

      protected abstract LootItemCondition create(List<LootItemCondition> var1);
   }
}
