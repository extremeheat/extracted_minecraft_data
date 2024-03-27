package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger<EntityHurtPlayerTrigger.TriggerInstance> {
   public EntityHurtPlayerTrigger() {
      super();
   }

   @Override
   public Codec<EntityHurtPlayerTrigger.TriggerInstance> codec() {
      return EntityHurtPlayerTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      this.trigger(var1, var5x -> var5x.matches(var1, var2, var3, var4, var5));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<DamagePredicate> c) implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<DamagePredicate> damage;
      public static final Codec<EntityHurtPlayerTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(EntityHurtPlayerTrigger.TriggerInstance::player),
                  DamagePredicate.CODEC.optionalFieldOf("damage").forGetter(EntityHurtPlayerTrigger.TriggerInstance::damage)
               )
               .apply(var0, EntityHurtPlayerTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<DamagePredicate> var2) {
         super();
         this.player = var1;
         this.damage = var2;
      }

      public static Criterion<EntityHurtPlayerTrigger.TriggerInstance> entityHurtPlayer() {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new EntityHurtPlayerTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<EntityHurtPlayerTrigger.TriggerInstance> entityHurtPlayer(DamagePredicate var0) {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new EntityHurtPlayerTrigger.TriggerInstance(Optional.empty(), Optional.of(var0)));
      }

      public static Criterion<EntityHurtPlayerTrigger.TriggerInstance> entityHurtPlayer(DamagePredicate.Builder var0) {
         return CriteriaTriggers.ENTITY_HURT_PLAYER.createCriterion(new EntityHurtPlayerTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.build())));
      }

      public boolean matches(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
         return !this.damage.isPresent() || ((DamagePredicate)this.damage.get()).matches(var1, var2, var3, var4, var5);
      }
   }
}
