package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class LootingEnchantFunction extends LootItemConditionalFunction {
   public static final int NO_LIMIT = 0;
   public static final Codec<LootingEnchantFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  NumberProviders.CODEC.fieldOf("count").forGetter(var0x -> var0x.value),
                  ExtraCodecs.strictOptionalField(Codec.INT, "limit", 0).forGetter(var0x -> var0x.limit)
               )
            )
            .apply(var0, LootingEnchantFunction::new)
   );
   private final NumberProvider value;
   private final int limit;

   LootingEnchantFunction(List<LootItemCondition> var1, NumberProvider var2, int var3) {
      super(var1);
      this.value = var2;
      this.limit = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.LOOTING_ENCHANT;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(ImmutableSet.of(LootContextParams.KILLER_ENTITY), this.value.getReferencedContextParams());
   }

   private boolean hasLimit() {
      return this.limit > 0;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      Entity var3 = var2.getParamOrNull(LootContextParams.KILLER_ENTITY);
      if (var3 instanceof LivingEntity) {
         int var4 = EnchantmentHelper.getMobLooting((LivingEntity)var3);
         if (var4 == 0) {
            return var1;
         }

         float var5 = (float)var4 * this.value.getFloat(var2);
         var1.grow(Math.round(var5));
         if (this.hasLimit() && var1.getCount() > this.limit) {
            var1.setCount(this.limit);
         }
      }

      return var1;
   }

   public static LootingEnchantFunction.Builder lootingMultiplier(NumberProvider var0) {
      return new LootingEnchantFunction.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<LootingEnchantFunction.Builder> {
      private final NumberProvider count;
      private int limit = 0;

      public Builder(NumberProvider var1) {
         super();
         this.count = var1;
      }

      protected LootingEnchantFunction.Builder getThis() {
         return this;
      }

      public LootingEnchantFunction.Builder setLimit(int var1) {
         this.limit = var1;
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new LootingEnchantFunction(this.getConditions(), this.count, this.limit);
      }
   }
}
