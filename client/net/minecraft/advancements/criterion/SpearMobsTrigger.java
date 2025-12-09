package net.minecraft.advancements.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;

public class SpearMobsTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public SpearMobsTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return SpearMobsTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, int var2) {
      this.trigger(var1, (var1x) -> var1x.matches(var2));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<Integer> count) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ExtraCodecs.POSITIVE_INT.optionalFieldOf("count").forGetter(TriggerInstance::count)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<Integer> var2) {
         super();
         this.player = var1;
         this.count = var2;
      }

      public static Criterion<TriggerInstance> spearMobs(int var0) {
         return CriteriaTriggers.SPEAR_MOBS_TRIGGER.createCriterion(new TriggerInstance(Optional.empty(), Optional.of(var0)));
      }

      public boolean matches(int var1) {
         return this.count.isEmpty() || var1 >= (Integer)this.count.get();
      }
   }
}
