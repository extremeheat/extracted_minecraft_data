package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConditionalProvider;

public record ConditionalValue(Holder<LootItemCondition> condition, Holder<ContextIntProvider> onTrue, Holder<ContextIntProvider> onFalse) implements ContextIntProvider, ConditionalProvider<ContextIntProvider> {
   public static final MapCodec<ConditionalValue> MAP_CODEC;
   public static final Codec<ConditionalValue> CODEC;

   public ConditionalValue {
      super();
   }

   public int getIntUnsafe(final LootContext context) {
      return ((ContextIntProvider)this.selectValue(context).value()).getIntUnsafe(context);
   }

   public MapCodec<ConditionalValue> codec() {
      return MAP_CODEC;
   }

   static {
      MAP_CODEC = ConditionalProvider.mapCodec(ContextIntProviders.CODEC, ContextIntProviders.exactly(0), ConditionalValue::new);
      CODEC = MAP_CODEC.codec();
   }
}
