package net.minecraft.world.level.storage.loot.functions;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
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
   private static final Codec<HolderSet<Enchantment>> ENCHANTMENT_SET_CODEC = BuiltInRegistries.ENCHANTMENT
      .holderByNameCodec()
      .listOf()
      .xmap(HolderSet::direct, var0 -> var0.stream().toList());
   public static final Codec<EnchantRandomlyFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(ExtraCodecs.strictOptionalField(ENCHANTMENT_SET_CODEC, "enchantments").forGetter(var0x -> var0x.enchantments))
            .apply(var0, EnchantRandomlyFunction::new)
   );
   private final Optional<HolderSet<Enchantment>> enchantments;

   EnchantRandomlyFunction(List<LootItemCondition> var1, Optional<HolderSet<Enchantment>> var2) {
      super(var1);
      this.enchantments = var2;
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.ENCHANT_RANDOMLY;
   }

   @Override
   public ItemStack run(ItemStack var1, LootContext var2) {
      RandomSource var3 = var2.getRandom();
      Optional var4 = this.enchantments
         .<Holder<Enchantment>>flatMap(var1x -> var1x.getRandomElement(var3))
         .or(
            () -> {
               boolean var2xx = var1.is(Items.BOOK);
               List var3xx = BuiltInRegistries.ENCHANTMENT
                  .holders()
                  .filter(var0x -> var0x.value().isDiscoverable())
                  .filter(var2xx -> var2x || var2xx.value().canEnchant(var1))
                  .toList();
               return Util.getRandomSafe(var3xx, var3);
            }
         );
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

   public static EnchantRandomlyFunction.Builder randomEnchantment() {
      return new EnchantRandomlyFunction.Builder();
   }

   public static LootItemConditionalFunction.Builder<?> randomApplicableEnchantment() {
      return simpleBuilder(var0 -> new EnchantRandomlyFunction(var0, Optional.empty()));
   }

   public static class Builder extends LootItemConditionalFunction.Builder<EnchantRandomlyFunction.Builder> {
      private final List<Holder<Enchantment>> enchantments = new ArrayList<>();

      public Builder() {
         super();
      }

      protected EnchantRandomlyFunction.Builder getThis() {
         return this;
      }

      public EnchantRandomlyFunction.Builder withEnchantment(Enchantment var1) {
         this.enchantments.add(var1.builtInRegistryHolder());
         return this;
      }

      @Override
      public LootItemFunction build() {
         return new EnchantRandomlyFunction(
            this.getConditions(), this.enchantments.isEmpty() ? Optional.empty() : Optional.of(HolderSet.direct(this.enchantments))
         );
      }
   }
}
