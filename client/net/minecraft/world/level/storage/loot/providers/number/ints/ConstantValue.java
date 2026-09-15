package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record ConstantValue(int value) implements ContextIntProvider {
   public static final MapCodec<ConstantValue> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.INT.fieldOf("value").forGetter(ConstantValue::value)).apply(i, ConstantValue::new));
   public static final Codec<ConstantValue> INLINE_CODEC;

   public ConstantValue {
      super();
   }

   public MapCodec<ConstantValue> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
   }

   public int getIntUnsafe(final LootContext random) {
      return this.value;
   }

   static {
      INLINE_CODEC = Codec.INT.xmap(ConstantValue::new, ConstantValue::value);
   }
}
