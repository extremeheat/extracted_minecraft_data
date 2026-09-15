package net.minecraft.world.level.storage.loot.providers.number.floats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.EnvironmentAttributeProvider;

public record EnvironmentAttributeValue(EnvironmentAttribute<?> attribute) implements ContextFloatProvider, EnvironmentAttributeProvider {
   private static final Codec<EnvironmentAttribute<?>> ATTRIBUTE_CODEC;
   public static final MapCodec<EnvironmentAttributeValue> MAP_CODEC;

   public EnvironmentAttributeValue {
      super();
   }

   public MapCodec<EnvironmentAttributeValue> codec() {
      return MAP_CODEC;
   }

   public float getFloatUnsafe(final LootContext context) {
      return getAsFloat(context, this.attribute);
   }

   private static <Value> float getAsFloat(final LootContext context, final EnvironmentAttribute<Value> attribute) {
      Value value = (Value)context.getLevel().environmentAttributes().getValue(context, attribute);
      return attribute.type().toFloat(value);
   }

   static {
      ATTRIBUTE_CODEC = EnvironmentAttributes.CODEC.validate((attribute) -> attribute.type().toFloat() == null ? DataResult.error(() -> String.valueOf(attribute) + " cannot be converted to a float") : DataResult.success(attribute));
      MAP_CODEC = EnvironmentAttributeProvider.<EnvironmentAttributeValue>mapCodec(ATTRIBUTE_CODEC, EnvironmentAttributeValue::new);
   }
}
