package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Set;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
   public static final Codec<EnchantWithLevelsFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  NumberProviders.CODEC.fieldOf("levels").forGetter(var0x -> var0x.levels),
                  Codec.BOOL.fieldOf("treasure").orElse(false).forGetter(var0x -> var0x.treasure)
               )
            )
            .apply(var0, EnchantWithLevelsFunction::new)
   );
   private final NumberProvider levels;
   private final boolean treasure;

   EnchantWithLevelsFunction(List<LootItemCondition> var1, NumberProvider var2, boolean var3) {
      super(var1);
      this.levels = var2;
      this.treasure = var3;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.ENCHANT_WITH_LEVELS;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return this.levels.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();
      return EnchantmentHelper.enchantItem(var3, var1, this.levels.getInt(var2), this.treasure);
   }

   public static EnchantWithLevelsFunction.Builder enchantWithLevels(NumberProvider var0) {
      return new EnchantWithLevelsFunction.Builder(var0);
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantWithLevelsFunction.Builder> {
      private final NumberProvider levels;
      private boolean treasure;

      public Builder(NumberProvider var1) {
         super();
         this.levels = var1;
      }

      protected EnchantWithLevelsFunction.Builder getThis() {
         return this;
      }

      public EnchantWithLevelsFunction.Builder allowTreasure() {
         this.treasure = true;
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.treasure);
      }
   }
}
