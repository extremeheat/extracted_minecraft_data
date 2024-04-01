package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.world.level.storage.loot.LootContext;

public record ConstantValue(float c) implements NumberProvider {
   private final float value;
   public static final Codec<ConstantValue> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(Codec.FLOAT.fieldOf("value").forGetter(ConstantValue::value)).apply(var0, ConstantValue::new)
   );
   public static final Codec<ConstantValue> INLINE_CODEC = Codec.FLOAT.xmap(ConstantValue::new, ConstantValue::value);

   public ConstantValue(float var1) {
      super();
      this.value = var1;
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
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         return Float.compare(((ConstantValue)var1).value, this.value) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.value != 0.0F ? Float.floatToIntBits(this.value) : 0;
   }
}
