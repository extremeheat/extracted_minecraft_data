package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnchantmentLevelProvider(LevelBasedValue amount) implements ContextFloatProvider, LootContextUser {
   public static final MapCodec<EnchantmentLevelProvider> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(LevelBasedValue.CODEC.fieldOf("amount").forGetter(EnchantmentLevelProvider::amount)).apply(i, EnchantmentLevelProvider::new));

   public EnchantmentLevelProvider {
      super();
   }

   public MapCodec<EnchantmentLevelProvider> codec() {
      return MAP_CODEC;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ENCHANTMENT_LEVEL);
   }

   public float getFloatUnsafe(final LootContext context) {
      Integer level = (Integer)context.getOptional(LootContextParams.ENCHANTMENT_LEVEL);
      return this.amount.calculate(level != null ? level : 0);
   }
}
