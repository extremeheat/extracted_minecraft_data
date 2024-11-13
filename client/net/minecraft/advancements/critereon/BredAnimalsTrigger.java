package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.storage.loot.LootContext;

public class BredAnimalsTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public BredAnimalsTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return BredAnimalsTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Animal var2, Animal var3, @Nullable AgeableMob var4) {
      LootContext var5 = EntityPredicate.createContext(var1, var2);
      LootContext var6 = EntityPredicate.createContext(var1, var3);
      LootContext var7 = var4 != null ? EntityPredicate.createContext(var1, var4) : null;
      this.trigger(var1, (var3x) -> var3x.matches(var5, var6, var7));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ContextAwarePredicate> parent, Optional<ContextAwarePredicate> partner, Optional<ContextAwarePredicate> child) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("parent").forGetter(TriggerInstance::parent), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("partner").forGetter(TriggerInstance::partner), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("child").forGetter(TriggerInstance::child)).apply(var0, TriggerInstance::new));

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ContextAwarePredicate> var3, Optional<ContextAwarePredicate> var4) {
         super();
         this.player = var1;
         this.parent = var2;
         this.partner = var3;
         this.child = var4;
      }

      public static Criterion<TriggerInstance> bredAnimals() {
         return CriteriaTriggers.BRED_ANIMALS.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TriggerInstance> bredAnimals(EntityPredicate.Builder var0) {
         return CriteriaTriggers.BRED_ANIMALS.createCriterion(new TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(EntityPredicate.wrap(var0))));
      }

      public static Criterion<TriggerInstance> bredAnimals(Optional<EntityPredicate> var0, Optional<EntityPredicate> var1, Optional<EntityPredicate> var2) {
         return CriteriaTriggers.BRED_ANIMALS.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), EntityPredicate.wrap(var1), EntityPredicate.wrap(var2)));
      }

      public boolean matches(LootContext var1, LootContext var2, @Nullable LootContext var3) {
         if (!this.child.isPresent() || var3 != null && ((ContextAwarePredicate)this.child.get()).matches(var3)) {
            return matches(this.parent, var1) && matches(this.partner, var2) || matches(this.parent, var2) && matches(this.partner, var1);
         } else {
            return false;
         }
      }

      private static boolean matches(Optional<ContextAwarePredicate> var0, LootContext var1) {
         return var0.isEmpty() || ((ContextAwarePredicate)var0.get()).matches(var1);
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.parent, ".parent");
         var1.validateEntity(this.partner, ".partner");
         var1.validateEntity(this.child, ".child");
      }
   }
}
