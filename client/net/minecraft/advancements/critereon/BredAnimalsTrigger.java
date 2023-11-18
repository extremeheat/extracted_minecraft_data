package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger extends SimpleCriterionTrigger<BredAnimalsTrigger.TriggerInstance> {
   public BredAnimalsTrigger() {
      super();
   }

   public BredAnimalsTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = EntityPredicate.fromJson(var1, "parent", var3);
      Optional var5 = EntityPredicate.fromJson(var1, "partner", var3);
      Optional var6 = EntityPredicate.fromJson(var1, "child", var3);
      return new BredAnimalsTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, Animal var2, Animal var3, @Nullable AgeableMob var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      LootContext var6 = EntityPredicate.createContext(var1, var3);
      LootContext var7 = var4 != null ? EntityPredicate.createContext(var1, var4) : null;
      this.trigger(var1, var3x -> var3x.matches(var5, var6, var7));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ContextAwarePredicate> parent;
      private final Optional<ContextAwarePredicate> partner;
      private final Optional<ContextAwarePredicate> child;

      public TriggerInstance(
         Optional<ContextAwarePredicate> var1,
         Optional<ContextAwarePredicate> var2,
         Optional<ContextAwarePredicate> var3,
         Optional<ContextAwarePredicate> var4
      ) {
         super(var1);
         this.parent = var2;
         this.partner = var3;
         this.child = var4;
      }

      public static Criterion<BredAnimalsTrigger.TriggerInstance> bredAnimals() {
         return CriteriaTriggers.BRED_ANIMALS
            .createCriterion(new BredAnimalsTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<BredAnimalsTrigger.TriggerInstance> bredAnimals(EntityPredicate.Builder var0) {
         return CriteriaTriggers.BRED_ANIMALS
            .createCriterion(
               new BredAnimalsTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(var0)))
            );
      }

      public static Criterion<BredAnimalsTrigger.TriggerInstance> bredAnimals(
         Optional<EntityPredicate> var0, Optional<EntityPredicate> var1, Optional<EntityPredicate> var2
      ) {
         return CriteriaTriggers.BRED_ANIMALS
            .createCriterion(
               new BredAnimalsTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), EntityPredicate.wrap(var1), EntityPredicate.wrap(var2))
            );
      }

      public boolean matches(LootContext var1, LootContext var2, @Nullable LootContext var3) {
         if (!this.child.isPresent() || var3 != null && this.child.get().matches(var3)) {
            return matches(this.parent, var1) && matches(this.partner, var2) || matches(this.parent, var2) && matches(this.partner, var1);
         } else {
            return false;
         }
      }

      private static boolean matches(Optional<ContextAwarePredicate> var0, LootContext var1) {
         return var0.isEmpty() || ((ContextAwarePredicate)var0.get()).matches(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.parent.ifPresent(var1x -> var1.add("parent", var1x.toJson()));
         this.partner.ifPresent(var1x -> var1.add("partner", var1x.toJson()));
         this.child.ifPresent(var1x -> var1.add("child", var1x.toJson()));
         return var1;
      }
   }
}
