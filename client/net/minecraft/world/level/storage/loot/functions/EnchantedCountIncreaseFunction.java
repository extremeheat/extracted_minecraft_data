package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class EnchantedCountIncreaseFunction extends LootItemConditionalFunction {
   public static final int NO_LIMIT = 0;
   public static final MapCodec<EnchantedCountIncreaseFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(Enchantment.CODEC.fieldOf("enchantment").forGetter((var0x) -> {
         return var0x.enchantment;
      }), NumberProviders.CODEC.fieldOf("count").forGetter((var0x) -> {
         return var0x.value;
      }), Codec.INT.optionalFieldOf("limit", 0).forGetter((var0x) -> {
         return var0x.limit;
      }))).apply(var0, EnchantedCountIncreaseFunction::new);
   });
   private final Holder<Enchantment> enchantment;
   private final NumberProvider value;
   private final int limit;

   EnchantedCountIncreaseFunction(List<LootItemCondition> var1, Holder<Enchantment> var2, NumberProvider var3, int var4) {
      super(var1);
      this.enchantment = var2;
      this.value = var3;
      this.limit = var4;
   }

   public LootItemFunctionType<EnchantedCountIncreaseFunction> getType() {
      return LootItemFunctions.ENCHANTED_COUNT_INCREASE;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Sets.union(ImmutableSet.of(LootContextParams.ATTACKING_ENTITY), this.value.getReferencedContextParams());
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      Entity var3 = (Entity)var2.getOptionalParameter(LootContextParams.ATTACKING_ENTITY);
      if (var3 instanceof LivingEntity var4) {
         int var5 = EnchantmentHelper.getEnchantmentLevel(this.enchantment, var4);
         if (var5 == 0) {
            return var1;
         }

         float var6 = (float)var5 * this.value.getFloat(var2);
         var1.grow(Math.round(var6));
         if (this.hasLimit()) {
            var1.limitSize(this.limit);
         }
      }

      return var1;
   }

   public static Builder lootingMultiplier(HolderLookup.Provider var0, NumberProvider var1) {
      HolderLookup.RegistryLookup var2 = var0.lookupOrThrow(Registries.ENCHANTMENT);
      return new Builder(var2.getOrThrow(Enchantments.LOOTING), var1);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final Holder<Enchantment> enchantment;
      private final NumberProvider count;
      private int limit = 0;

      public Builder(Holder<Enchantment> var1, NumberProvider var2) {
         super();
         this.enchantment = var1;
         this.count = var2;
      }

      protected Builder getThis() {
         return this;
      }

      public Builder setLimit(int var1) {
         this.limit = var1;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantedCountIncreaseFunction(this.getConditions(), this.enchantment, this.count, this.limit);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
