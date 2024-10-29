package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.slf4j.Logger;

public class EnchantRandomlyFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final MapCodec<EnchantRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("options").forGetter((var0x) -> {
         return var0x.options;
      }), Codec.BOOL.optionalFieldOf("only_compatible", true).forGetter((var0x) -> {
         return var0x.onlyCompatible;
      }))).apply(var0, EnchantRandomlyFunction::new);
   });
   private final Optional<HolderSet<Enchantment>> options;
   private final boolean onlyCompatible;

   EnchantRandomlyFunction(List<LootItemCondition> var1, Optional<HolderSet<Enchantment>> var2, boolean var3) {
      super(var1);
      this.options = var2;
      this.onlyCompatible = var3;
   }

   public LootItemFunctionType<EnchantRandomlyFunction> getType() {
      return LootItemFunctions.ENCHANT_RANDOMLY;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();
      boolean var4 = var1.is(Items.BOOK);
      boolean var5 = !var4 && this.onlyCompatible;
      Stream var6 = ((Stream)this.options.map(HolderSet::stream).orElseGet(() -> {
         return var2.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).listElements().map(Function.identity());
      })).filter((var2x) -> {
         return !var5 || ((Enchantment)var2x.value()).canEnchant(var1);
      });
      List var7 = var6.toList();
      Optional var8 = Util.getRandomSafe(var7, var3);
      if (var8.isEmpty()) {
         LOGGER.warn("Couldn't find a compatible enchantment for {}", var1);
         return var1;
      } else {
         return enchantItem(var1, (Holder)var8.get(), var3);
      }
   }

   private static ItemStack enchantItem(ItemStack var0, Holder<Enchantment> var1, RandomSource var2) {
      int var3 = Mth.nextInt(var2, ((Enchantment)var1.value()).getMinLevel(), ((Enchantment)var1.value()).getMaxLevel());
      if (var0.is(Items.BOOK)) {
         var0 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      var0.enchant(var1, var3);
      return var0;
   }

   public static Builder randomEnchantment() {
      return new Builder();
   }

   public static Builder randomApplicableEnchantment(HolderLookup.Provider var0) {
      return randomEnchantment().withOneOf(var0.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private Optional<HolderSet<Enchantment>> options = Optional.empty();
      private boolean onlyCompatible = true;

      public Builder() {
         super();
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withEnchantment(Holder<Enchantment> var1) {
         this.options = Optional.of(HolderSet.direct(var1));
         return this;
      }

      public Builder withOneOf(HolderSet<Enchantment> var1) {
         this.options = Optional.of(var1);
         return this;
      }

      public Builder allowingIncompatibleEnchantments() {
         this.onlyCompatible = false;
         return this;
      }

      public LootItemFunction build() {
         return new EnchantRandomlyFunction(this.getConditions(), this.options, this.onlyCompatible);
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
