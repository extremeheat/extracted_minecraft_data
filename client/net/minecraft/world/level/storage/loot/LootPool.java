package net.minecraft.world.level.storage.loot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.FunctionUserBuilder;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraft.world.level.storage.loot.predicates.ConditionUserBuilder;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   public static final Codec<LootPool> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(LootPoolEntries.CODEC.listOf().fieldOf("entries").forGetter((var0x) -> {
         return var0x.entries;
      }), LootItemConditions.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter((var0x) -> {
         return var0x.conditions;
      }), LootItemFunctions.ROOT_CODEC.listOf().optionalFieldOf("functions", List.of()).forGetter((var0x) -> {
         return var0x.functions;
      }), NumberProviders.CODEC.fieldOf("rolls").forGetter((var0x) -> {
         return var0x.rolls;
      }), NumberProviders.CODEC.fieldOf("bonus_rolls").orElse(ConstantValue.exactly(0.0F)).forGetter((var0x) -> {
         return var0x.bonusRolls;
      })).apply(var0, LootPool::new);
   });
   private final List<LootPoolEntryContainer> entries;
   private final List<LootItemCondition> conditions;
   private final Predicate<LootContext> compositeCondition;
   private final List<LootItemFunction> functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> compositeFunction;
   private final NumberProvider rolls;
   private final NumberProvider bonusRolls;

   LootPool(List<LootPoolEntryContainer> var1, List<LootItemCondition> var2, List<LootItemFunction> var3, NumberProvider var4, NumberProvider var5) {
      super();
      this.entries = var1;
      this.conditions = var2;
      this.compositeCondition = Util.allOf(var2);
      this.functions = var3;
      this.compositeFunction = LootItemFunctions.compose(var3);
      this.rolls = var4;
      this.bonusRolls = var5;
   }

   private void addRandomItem(Consumer<ItemStack> var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();
      ArrayList var4 = Lists.newArrayList();
      MutableInt var5 = new MutableInt();
      Iterator var6 = this.entries.iterator();

      while(var6.hasNext()) {
         LootPoolEntryContainer var7 = (LootPoolEntryContainer)var6.next();
         var7.expand(var2, (var3x) -> {
            int var4x = var3x.getWeight(var2.getLuck());
            if (var4x > 0) {
               var4.add(var3x);
               var5.add(var4x);
            }

         });
      }

      int var10 = var4.size();
      if (var5.intValue() != 0 && var10 != 0) {
         if (var10 == 1) {
            ((LootPoolEntry)var4.get(0)).createItemStack(var1, var2);
         } else {
            int var11 = var3.nextInt(var5.intValue());
            Iterator var8 = var4.iterator();

            LootPoolEntry var9;
            do {
               if (!var8.hasNext()) {
                  return;
               }

               var9 = (LootPoolEntry)var8.next();
               var11 -= var9.getWeight(var2.getLuck());
            } while(var11 >= 0);

            var9.createItemStack(var1, var2);
         }
      }
   }

   public void addRandomItems(Consumer<ItemStack> var1, LootContext var2) {
      if (this.compositeCondition.test(var2)) {
         Consumer var3 = LootItemFunction.decorate(this.compositeFunction, var1, var2);
         int var4 = this.rolls.getInt(var2) + Mth.floor(this.bonusRolls.getFloat(var2) * var2.getLuck());

         for(int var5 = 0; var5 < var4; ++var5) {
            this.addRandomItem(var3, var2);
         }

      }
   }

   public void validate(ValidationContext var1) {
      int var2;
      for(var2 = 0; var2 < this.conditions.size(); ++var2) {
         ((LootItemCondition)this.conditions.get(var2)).validate(var1.forChild(".condition[" + var2 + "]"));
      }

      for(var2 = 0; var2 < this.functions.size(); ++var2) {
         ((LootItemFunction)this.functions.get(var2)).validate(var1.forChild(".functions[" + var2 + "]"));
      }

      for(var2 = 0; var2 < this.entries.size(); ++var2) {
         ((LootPoolEntryContainer)this.entries.get(var2)).validate(var1.forChild(".entries[" + var2 + "]"));
      }

      this.rolls.validate(var1.forChild(".rolls"));
      this.bonusRolls.validate(var1.forChild(".bonusRolls"));
   }

   public static Builder lootPool() {
      return new Builder();
   }

   public static class Builder implements FunctionUserBuilder<Builder>, ConditionUserBuilder<Builder> {
      private final ImmutableList.Builder<LootPoolEntryContainer> entries = ImmutableList.builder();
      private final ImmutableList.Builder<LootItemCondition> conditions = ImmutableList.builder();
      private final ImmutableList.Builder<LootItemFunction> functions = ImmutableList.builder();
      private NumberProvider rolls = ConstantValue.exactly(1.0F);
      private NumberProvider bonusRolls = ConstantValue.exactly(0.0F);

      public Builder() {
         super();
      }

      public Builder setRolls(NumberProvider var1) {
         this.rolls = var1;
         return this;
      }

      public Builder unwrap() {
         return this;
      }

      public Builder setBonusRolls(NumberProvider var1) {
         this.bonusRolls = var1;
         return this;
      }

      public Builder add(LootPoolEntryContainer.Builder<?> var1) {
         this.entries.add(var1.build());
         return this;
      }

      public Builder when(LootItemCondition.Builder var1) {
         this.conditions.add(var1.build());
         return this;
      }

      public Builder apply(LootItemFunction.Builder var1) {
         this.functions.add(var1.build());
         return this;
      }

      public LootPool build() {
         return new LootPool(this.entries.build(), this.conditions.build(), this.functions.build(), this.rolls, this.bonusRolls);
      }

      // $FF: synthetic method
      public FunctionUserBuilder unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public FunctionUserBuilder apply(LootItemFunction.Builder var1) {
         return this.apply(var1);
      }

      // $FF: synthetic method
      public ConditionUserBuilder unwrap() {
         return this.unwrap();
      }

      // $FF: synthetic method
      public ConditionUserBuilder when(LootItemCondition.Builder var1) {
         return this.when(var1);
      }
   }
}
