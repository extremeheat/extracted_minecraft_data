package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConditionalProvider;

public record ConditionalValue(Holder<LootItemCondition> condition, Holder<ContextFloatProvider> onTrue, Holder<ContextFloatProvider> onFalse) implements ContextFloatProvider, ConditionalProvider<ContextFloatProvider> {
   public static final MapCodec<ConditionalValue> MAP_CODEC;
   public static final Codec<ConditionalValue> CODEC;

   public ConditionalValue {
      super();
   }

   public float getFloatUnsafe(final LootContext context) {
      return ((ContextFloatProvider)this.selectValue(context).value()).getFloatUnsafe(context);
   }

   public MapCodec<ConditionalValue> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = ConditionalProvider.mapCodec(ContextFloatProviders.CODEC, ContextFloatProviders.exactly(0.0F), ConditionalValue::new);
      CODEC = MAP_CODEC.codec();
   }
}
