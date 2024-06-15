package net.minecraft.world.level.storage.loot.predicates;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.Set;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public record LootItemEntityPropertyCondition(Optional<EntityPredicate> predicate, LootContext.EntityTarget entityTarget) implements LootItemCondition {
   public static final MapCodec<LootItemEntityPropertyCondition> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               EntityPredicate.CODEC.optionalFieldOf("predicate").forGetter(LootItemEntityPropertyCondition::predicate),
               LootContext.EntityTarget.CODEC.fieldOf("entity").forGetter(LootItemEntityPropertyCondition::entityTarget)
            )
            .apply(var0, LootItemEntityPropertyCondition::new)
   );

   public LootItemEntityPropertyCondition(Optional<EntityPredicate> predicate, LootContext.EntityTarget entityTarget) {
      super();
      this.predicate = predicate;
      this.entityTarget = entityTarget;
   }

   @Override
   public LootItemConditionType getType() {
      return LootItemConditions.ENTITY_PROPERTIES;
   }

   @Override
   public Set<LootContextParam<?>> getReferencedContextParams() {
      return ImmutableSet.of(LootContextParams.ORIGIN, this.entityTarget.getParam());
   }

   public boolean test(LootContext var1) {
      Entity var2 = var1.getParamOrNull(this.entityTarget.getParam());
      Vec3 var3 = var1.getParamOrNull(LootContextParams.ORIGIN);
      return this.predicate.isEmpty() || this.predicate.get().matches(var1.getLevel(), var3, var2);
   }

   public static LootItemCondition.Builder entityPresent(LootContext.EntityTarget var0) {
      return hasProperties(var0, EntityPredicate.Builder.entity());
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget var0, EntityPredicate.Builder var1) {
      return () -> new LootItemEntityPropertyCondition(Optional.of(var1.build()), var0);
   }

   public static LootItemCondition.Builder hasProperties(LootContext.EntityTarget var0, EntityPredicate var1) {
      return () -> new LootItemEntityPropertyCondition(Optional.of(var1), var0);
   }
}
