package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Set;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record ValueCheckCondition(NumberProvider b, IntRange c) implements LootItemCondition {
   private final NumberProvider provider;
   private final IntRange range;
   public static final Codec<ValueCheckCondition> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               NumberProviders.CODEC.fieldOf("value").forGetter(ValueCheckCondition::provider),
               IntRange.CODEC.fieldOf("range").forGetter(ValueCheckCondition::range)
            )
            .apply(var0, ValueCheckCondition::new)
   );

   public ValueCheckCondition(NumberProvider var1, IntRange var2) {
      super();
      this.provider = var1;
      this.range = var2;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.VALUE_CHECK;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return Sets.union(this.provider.getReferencedContextParams(), this.range.getReferencedContextParams());
   }

   public boolean test(LootContext var1) {
      return this.range.test(var1, this.provider.getInt(var1));
   }

   public static LootItemCondition.Builder hasValue(NumberProvider var0, IntRange var1) {
      return () -> new ValueCheckCondition(var0, var1);
   }
}
