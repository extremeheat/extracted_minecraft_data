package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
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
   private static final Codec<HolderSet<Enchantment>> ENCHANTMENT_SET_CODEC;
   public static final MapCodec<EnchantRandomlyFunction> CODEC;
   private final Optional<HolderSet<Enchantment>> enchantments;

   EnchantRandomlyFunction(List<LootItemCondition> var1, Optional<HolderSet<Enchantment>> var2) {
      super(var1);
      this.enchantments = var2;
   }

   public LootItemFunctionType<EnchantRandomlyFunction> getType() {
      return LootItemFunctions.ENCHANT_RANDOMLY;
   }

   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();
      Optional var4 = this.enchantments.flatMap((var1x) -> {
         return var1x.getRandomElement(var3);
      }).or(() -> {
         boolean var3x = var1.is(Items.BOOK);
         List var4 = BuiltInRegistries.ENCHANTMENT.holders().filter((var1x) -> {
            return ((Enchantment)var1x.value()).isEnabled(var2.getLevel().enabledFeatures());
         }).filter((var0) -> {
            return ((Enchantment)var0.value()).isDiscoverable();
         }).filter((var2x) -> {
            return var3x || ((Enchantment)var2x.value()).canEnchant(var1);
         }).toList();
         return Util.getRandomSafe(var4, var3);
      });
      if (var4.isEmpty()) {
         LOGGER.warn("Couldn't find a compatible enchantment for {}", var1);
         return var1;
      } else {
         return enchantItem(var1, (Enchantment)((Holder)var4.get()).value(), var3);
      }
   }

   private static ItemStack enchantItem(ItemStack var0, Enchantment var1, RandomSource var2) {
      int var3 = Mth.nextInt(var2, var1.getMinLevel(), var1.getMaxLevel());
      if (var0.is(Items.BOOK)) {
         var0 = new ItemStack(Items.ENCHANTED_BOOK);
      }

      var0.enchant(var1, var3);
      return var0;
   }

   public static Builder randomEnchantment() {
      return new Builder();
   }

   public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
      return simpleBuilder((var0) -> {
         return new EnchantRandomlyFunction(var0, Optional.empty());
      });
   }

   static {
      ENCHANTMENT_SET_CODEC = BuiltInRegistries.ENCHANTMENT.holderByNameCodec().listOf().xmap(HolderSet::direct, (var0) -> {
         return var0.stream().toList();
      });
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return commonFields(var0).and(ENCHANTMENT_SET_CODEC.optionalFieldOf("enchantments").forGetter((var0x) -> {
            return var0x.enchantments;
         })).apply(var0, EnchantRandomlyFunction::new);
      });
   }

   public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
      private final List<Holder<Enchantment>> enchantments = new ArrayList();

      public Builder() {
         super();
      }

      protected Builder getThis() {
         return this;
      }

      public Builder withEnchantment(Enchantment var1) {
         this.enchantments.add(var1.builtInRegistryHolder());
         return this;
      }

      public LootItemFunction build() {
         return new EnchantRandomlyFunction(this.getConditions(), this.enchantments.isEmpty() ? Optional.empty() : Optional.of(HolderSet.direct(this.enchantments)));
      }

      // $FF: synthetic method
      protected LootItemConditionalFunction.Builder getThis() {
         return this.getThis();
      }
   }
}
