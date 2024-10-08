package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public class EnchantWithLevelsFunction extends LootItemConditionalFunction {
   public static final MapCodec<EnchantWithLevelsFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  NumberProviders.CODEC.fieldOf("levels").forGetter(var0x -> var0x.levels),
                  RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter(var0x -> var0x.options)
               )
            )
            .apply(var0, EnchantWithLevelsFunction::new)
   );
   private final NumberProvider levels;
   private final Optional<HolderSet<Enchantment>> options;

   EnchantWithLevelsFunction(List<LootItemCondition> var1, NumberProvider var2, Optional<HolderSet<Enchantment>> var3) {
      super(var1);
      this.levels = var2;
      this.options = var3;
   }

   @Override
   public LootItemFunctionType<EnchantWithLevelsFunction> getType() {
      return LootItemFunctions.ENCHANT_WITH_LEVELS;
   }

   @Override
   public Set<ContextKey<?>> getReferencedContextParams() {
      return this.levels.getReferencedContextParams();
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();
      RegistryAccess var4 = var2.getLevel().registryAccess();
      return EnchantmentHelper.enchantItem(var3, var1, this.levels.getInt(var2), var4, this.options);
   }

   public static EnchantWithLevelsFunction.Builder enchantWithLevels(HolderLookup.Provider var0, NumberProvider var1) {
      return new EnchantWithLevelsFunction.Builder(var1).fromOptions(var0.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantWithLevelsFunction.Builder> {
      private final NumberProvider levels;
      private Optional<HolderSet<Enchantment>> options = Optional.empty();

      public Builder(NumberProvider var1) {
         super();
         this.levels = var1;
      }

      protected EnchantWithLevelsFunction.Builder getThis() {
         return this;
      }

      public EnchantWithLevelsFunction.Builder fromOptions(HolderSet<Enchantment> var1) {
         this.options = Optional.of(var1);
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new EnchantWithLevelsFunction(this.getConditions(), this.levels, this.options);
      }
   }
}
