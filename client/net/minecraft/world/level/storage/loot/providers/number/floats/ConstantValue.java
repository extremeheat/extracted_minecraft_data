package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.ValidationContext;

public record ConstantValue(float value) implements ContextFloatProvider {
   public static final MapCodec<ConstantValue> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("value").forGetter(ConstantValue::value)).apply(i, ConstantValue::new));
   public static final Codec<ConstantValue> INLINE_CODEC;

   public ConstantValue {
      super();
   }

   public MapCodec<ConstantValue> codec() {
      return MAP_CODEC;
   }

   public void validate(final ValidationContext context) {
   }

   public float getFloatUnsafe(final LootContext random) {
      return this.value;
   }

   static {
      INLINE_CODEC = Codec.FLOAT.xmap(ConstantValue::new, ConstantValue::value);
   }
}
