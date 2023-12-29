package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record DamageSourceCondition(Optional<DamageSourcePredicate> b) implements LootItemCondition {
   private final Optional<DamageSourcePredicate> predicate;
   public static final Codec<DamageSourceCondition> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(ExtraCodecs.strictOptionalField(DamageSourcePredicate.CODEC, "predicate").forGetter(DamageSourceCondition::predicate))
            .apply(var0, DamageSourceCondition::new)
   );

   public DamageSourceCondition(Optional<DamageSourcePredicate> var1) {
      super();
      this.predicate = var1;
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
      if (var3 != null && var2 != null) {
         return this.predicate.isEmpty() || this.predicate.get().matches(var1.getLevel(), var3, var2);
      } else {
         return false;
      }
   }

   public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder var0) {
      return () -> new DamageSourceCondition(Optional.of(var0.build()));
   }
}
