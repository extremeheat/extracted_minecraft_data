package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Set;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.level.storage.loot.IntRange;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

public record ValueCheckCondition(NumberProvider provider, IntRange range) implements LootItemCondition {
   public static final MapCodec<ValueCheckCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(NumberProviders.CODEC.fieldOf("value").forGetter(ValueCheckCondition::provider), IntRange.CODEC.fieldOf("range").forGetter(ValueCheckCondition::range)).apply(var0, ValueCheckCondition::new));

   public ValueCheckCondition(NumberProvider var1, IntRange var2) {
      super();
      this.provider = var1;
      this.range = var2;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.VALUE_CHECK;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Sets.union(this.provider.getReferencedContextParams(), this.range.getReferencedContextParams());
   }

   public boolean test(LootContext var1) {
      return this.range.test(var1, this.provider.getInt(var1));
   }

   public static LootItemCondition.Builder hasValue(NumberProvider var0, IntRange var1) {
      return () -> new ValueCheckCondition(var0, var1);
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
