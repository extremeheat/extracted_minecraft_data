package net.minecraft.world.level.storage.loot.providers.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.attribute.EnvironmentAttribute;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public record EnvironmentAttributeValue(EnvironmentAttribute<?> attribute) implements NumberProvider {
   private static final Codec<EnvironmentAttribute<?>> ATTRIBUTE_CODEC;
   public static final MapCodec<EnvironmentAttributeValue> MAP_CODEC;

   public EnvironmentAttributeValue {
      super();
   }

   public MapCodec<EnvironmentAttributeValue> codec() {
      return MAP_CODEC;
   }

   public float getFloat(final LootContext context) {
      return getAsFloat(context, this.attribute);
   }

   private static <Value> float getAsFloat(final LootContext context, final EnvironmentAttribute<Value> attribute) {
      Value value = (Value)context.getLevel().environmentAttributes().getValue(context, attribute);
      return attribute.type().toFloat(value);
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return this.attribute.isPositional() ? Set.of(LootContextParams.ORIGIN) : Set.of();
   }

   public static EnvironmentAttributeValue forEnvironmentAttribute(final EnvironmentAttribute<?> attribute) {
      return new EnvironmentAttributeValue(attribute);
   }

   static {
      ATTRIBUTE_CODEC = EnvironmentAttributes.CODEC.validate((attribute) -> attribute.type().toFloat() == null ? DataResult.error(() -> String.valueOf(attribute) + " cannot be converted to a number") : DataResult.success(attribute));
      MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(ATTRIBUTE_CODEC.fieldOf("attribute").forGetter(EnvironmentAttributeValue::attribute)).apply(i, EnvironmentAttributeValue::new));
   }
}
