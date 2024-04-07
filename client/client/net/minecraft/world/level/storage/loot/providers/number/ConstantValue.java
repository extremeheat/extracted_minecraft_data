package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.storage.loot.LootContext;

public record ConstantValue(float value) implements NumberProvider {
   public static final MapCodec<ConstantValue> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(Codec.FLOAT.fieldOf("value").forGetter(ConstantValue::value)).apply(var0, ConstantValue::new)
   );
   public static final Codec<ConstantValue> INLINE_CODEC = Codec.FLOAT.xmap(ConstantValue::new, ConstantValue::value);

   public ConstantValue(float value) {
      super();
      this.value = value;
   }

   @Override
   public LootNumberProviderType getType() {
      return NumberProviders.CONSTANT;
   }

   @Override
   public float getFloat(LootContext var1) {
      return this.value;
   }

   public static ConstantValue exactly(float var0) {
      return new ConstantValue(var0);
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return var1 != null && this.getClass() == var1.getClass() ? Float.compare(((ConstantValue)var1).value, this.value) == 0 : false;
      }
   }

   @Override
   public int hashCode() {
      return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
   }
}
