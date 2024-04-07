package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record DamageSourceCondition(Optional<DamageSourcePredicate> predicate) implements LootItemCondition {
   public static final MapCodec<DamageSourceCondition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(DamageSourcePredicate.CODEC.optionalFieldOf("predicate").forGetter(DamageSourceCondition::predicate))
            .apply(var0, DamageSourceCondition::new)
   );

   public DamageSourceCondition(Optional<DamageSourcePredicate> predicate) {
      super();
      this.predicate = predicate;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
   }

   public boolean test(LootContext var1) {
      DamageSource var2 = var1.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
      Vec3 var3 = var1.getParamOrNull(LootContextParams.ORIGIN);
      return var3 != null && var2 != null ? this.predicate.isEmpty() || this.predicate.get().matches(var1.getLevel(), var3, var2) : false;
   }

   public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder var0) {
      return () -> new DamageSourceCondition(Optional.of(var0.build()));
   }
}
