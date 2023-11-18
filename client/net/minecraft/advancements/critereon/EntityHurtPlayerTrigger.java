package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;

public class EntityHurtPlayerTrigger extends SimpleCriterionTrigger<EntityHurtPlayerTrigger.TriggerInstance> {
   public EntityHurtPlayerTrigger() {
      super();
   }

   public EntityHurtPlayerTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = DamagePredicate.fromJson(var1.get("damage"));
      return new EntityHurtPlayerTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, DamageSource var2, float var3, float var4, boolean var5) {
      this.trigger(var1, var5x -> var5x.matches(var1, var2, var3, var4, var5));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<DamagePredicate> damage;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<DamagePredicate> var2) {
         super(var1);
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
         return !this.damage.isPresent() || this.damage.get().matches(var1, var2, var3, var4, var5);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.damage.ifPresent(var1x -> var1.add("damage", var1x.serializeToJson()));
         return var1;
      }
   }
}
