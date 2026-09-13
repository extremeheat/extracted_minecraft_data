package net.minecraft.world.level.storage.loot.providers.number.ints;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.EnvironmentAttributeProvider;

public record EnvironmentAttributeValue(EnvironmentAttribute<?> attribute) implements ContextIntProvider, EnvironmentAttributeProvider {
   private static final Codec<EnvironmentAttribute<?>> ATTRIBUTE_CODEC;
   public static final MapCodec<EnvironmentAttributeValue> MAP_CODEC;

   public EnvironmentAttributeValue {
      super();
   }

   public MapCodec<EnvironmentAttributeValue> codec() {
      return MAP_CODEC;
   }

   public int getIntUnsafe(final LootContext context) throws ArithmeticException {
      return getAsInt(context, this.attribute);
   }

   private static <Value> int getAsInt(final LootContext context, final EnvironmentAttribute<Value> attribute) {
      Value value = (Value)context.getLevel().environmentAttributes().getValue(context, attribute);
      return attribute.type().toInt(value);
   }

   static {
      ATTRIBUTE_CODEC = EnvironmentAttributes.CODEC.validate((attribute) -> attribute.type().toInt() == null ? DataResult.error(() -> String.valueOf(attribute) + " cannot be converted to an integer") : DataResult.success(attribute));
      MAP_CODEC = EnvironmentAttributeProvider.<EnvironmentAttributeValue>mapCodec(ATTRIBUTE_CODEC, EnvironmentAttributeValue::new);
   }
}
