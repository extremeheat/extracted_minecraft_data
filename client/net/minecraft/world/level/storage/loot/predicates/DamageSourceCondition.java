package net.minecraft.world.level.storage.loot.predicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record DamageSourceCondition(Optional<DamageSourcePredicate> predicate) implements LootItemCondition {
   public static final MapCodec<DamageSourceCondition> CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(DamageSourcePredicate.CODEC.optionalFieldOf("predicate").forGetter(DamageSourceCondition::predicate)).apply(var0, DamageSourceCondition::new));

   public DamageSourceCondition(Optional<DamageSourcePredicate> var1) {
      super();
      this.predicate = var1;
   }

   public LootItemConditionType getType() {
      return LootItemConditions.DAMAGE_SOURCE_PROPERTIES;
   }

   public Set<ContextKey<?>> getReferencedContextParams() {
      return Set.of(LootContextParams.ORIGIN, LootContextParams.DAMAGE_SOURCE);
   }

   public boolean test(LootContext var1) {
      DamageSource var2 = (DamageSource)var1.getOptionalParameter(LootContextParams.DAMAGE_SOURCE);
      Vec3 var3 = (Vec3)var1.getOptionalParameter(LootContextParams.ORIGIN);
      if (var3 != null && var2 != null) {
         return this.predicate.isEmpty() || ((DamageSourcePredicate)this.predicate.get()).matches(var1.getLevel(), var3, var2);
      } else {
         return false;
      }
   }

   public static LootItemCondition.Builder hasDamageSource(DamageSourcePredicate.Builder var0) {
      return () -> new DamageSourceCondition(Optional.of(var0.build()));
   }

   // $FF: synthetic method
   public boolean test(final Object var1) {
      return this.test((LootContext)var1);
   }
}
