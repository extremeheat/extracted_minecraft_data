package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

public class ChangeDimensionTrigger extends SimpleCriterionTrigger<ChangeDimensionTrigger.TriggerInstance> {
   public ChangeDimensionTrigger() {
      super();
   }

   @Override
   public Codec<ChangeDimensionTrigger.TriggerInstance> codec() {
      return ChangeDimensionTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ResourceKey<Level> var2, ResourceKey<Level> var3) {
      this.trigger(var1, var2x -> var2x.matches(var2, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ResourceKey<Level>> c, Optional<ResourceKey<Level>> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ResourceKey<Level>> from;
      private final Optional<ResourceKey<Level>> to;
      public static final Codec<ChangeDimensionTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(ChangeDimensionTrigger.TriggerInstance::player),
                  ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("from").forGetter(ChangeDimensionTrigger.TriggerInstance::from),
                  ResourceKey.codec(Registries.DIMENSION).optionalFieldOf("to").forGetter(ChangeDimensionTrigger.TriggerInstance::to)
               )
               .apply(var0, ChangeDimensionTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ResourceKey<Level>> var2, Optional<ResourceKey<Level>> var3) {
         super();
         this.player = var1;
         this.from = var2;
         this.to = var3;
      }

      public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimension() {
         return CriteriaTriggers.CHANGED_DIMENSION
            .createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimension(ResourceKey<Level> var0, ResourceKey<Level> var1) {
         return CriteriaTriggers.CHANGED_DIMENSION
            .createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.of(var0), Optional.of(var1)));
      }

      public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimensionTo(ResourceKey<Level> var0) {
         return CriteriaTriggers.CHANGED_DIMENSION
            .createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.of(var0)));
      }

      public static Criterion<ChangeDimensionTrigger.TriggerInstance> changedDimensionFrom(ResourceKey<Level> var0) {
         return CriteriaTriggers.CHANGED_DIMENSION
            .createCriterion(new ChangeDimensionTrigger.TriggerInstance(Optional.empty(), Optional.of(var0), Optional.empty()));
      }

      public boolean matches(ResourceKey<Level> var1, ResourceKey<Level> var2) {
         if (this.from.isPresent() && this.from.get() != var1) {
            return false;
         } else {
            return !this.to.isPresent() || this.to.get() == var2;
         }
      }
   }
}
