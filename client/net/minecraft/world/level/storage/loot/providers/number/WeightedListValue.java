package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record WeightedListValue(WeightedList<NumberProvider> distribution) implements NumberProvider {
   public static final MapCodec<WeightedListValue> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WeightedList.nonEmptyCodec(NumberProviders.DIRECT_CODEC).fieldOf("distribution").forGetter((c) -> c.distribution)).apply(i, WeightedListValue::new));

   public WeightedListValue {
      super();
   }

   public int getInt(final LootContext context) {
      return ((NumberProvider)this.distribution.getRandomOrThrow(context.getRandom())).getInt(context);
   }

   public float getFloat(final LootContext context) {
      return ((NumberProvider)this.distribution.getRandomOrThrow(context.getRandom())).getFloat(context);
   }

   public MapCodec<WeightedListValue> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
      NumberProvider.super.validate(context);
      Validatable.validate(context, "distribution", this.distribution.unwrap().stream().map(Weighted::value).toList());
   }
}
