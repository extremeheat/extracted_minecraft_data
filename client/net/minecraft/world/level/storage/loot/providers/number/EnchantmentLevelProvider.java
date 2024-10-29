package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnchantmentLevelProvider(LevelBasedValue amount) implements NumberProvider {
   public static final MapCodec<EnchantmentLevelProvider> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentLevelProvider::amount)).apply(var0, EnchantmentLevelProvider::new);
   });

   public EnchantmentLevelProvider(LevelBasedValue var1) {
      super();
      this.amount = var1;
   }

   public float getFloat(LootContext var1) {
      int var2 = (Integer)var1.getParameter(LootContextParams.ENCHANTMENT_LEVEL);
      return this.amount.calculate(var2);
   }

   public LootNumberProviderType getType() {
      return NumberProviders.ENCHANTMENT_LEVEL;
   }

   public static EnchantmentLevelProvider forEnchantmentLevel(LevelBasedValue var0) {
      return new EnchantmentLevelProvider(var0);
   }

   public LevelBasedValue amount() {
      return this.amount;
   }
}
