package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public KilledByCrossbowTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return KilledByCrossbowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2) {
      ArrayList var3 = Lists.newArrayList();
      HashSet var4 = Sets.newHashSet();
      Iterator var5 = var2.iterator();

      while(var5.hasNext()) {
         Entity var6 = (Entity)var5.next();
         var4.add(var6.getType());
         var3.add(EntityPredicate.createContext(var1, var6));
      }

      this.trigger(var1, (var2x) -> {
         return var2x.matches(var3, var4.size());
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, List<ContextAwarePredicate> victims, MinMaxBounds.Ints uniqueEntityTypes) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(TriggerInstance::victims), MinMaxBounds.Ints.CODEC.optionalFieldOf("unique_entity_types", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::uniqueEntityTypes)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, List<ContextAwarePredicate> victims, MinMaxBounds.Ints uniqueEntityTypes) {
         super();
         this.player = player;
         this.victims = victims;
         this.uniqueEntityTypes = uniqueEntityTypes;
      }

      public static Criterion<TriggerInstance> crossbowKilled(EntityPredicate.Builder... var0) {
         return CriteriaTriggers.KILLED_BY_CROSSBOW.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), MinMaxBounds.Ints.ANY));
      }

      public static Criterion<TriggerInstance> crossbowKilled(MinMaxBounds.Ints var0) {
         return CriteriaTriggers.KILLED_BY_CROSSBOW.createCriterion(new TriggerInstance(Optional.empty(), List.of(), var0));
      }

      public boolean matches(Collection<LootContext> var1, int var2) {
         if (!this.victims.isEmpty()) {
            ArrayList var3 = Lists.newArrayList(var1);
            Iterator var4 = this.victims.iterator();

            while(var4.hasNext()) {
               ContextAwarePredicate var5 = (ContextAwarePredicate)var4.next();
               boolean var6 = false;
               Iterator var7 = var3.iterator();

               while(var7.hasNext()) {
                  LootContext var8 = (LootContext)var7.next();
                  if (var5.matches(var8)) {
                     var7.remove();
                     var6 = true;
                     break;
                  }
               }

               if (!var6) {
                  return false;
               }
            }
         }

         return this.uniqueEntityTypes.matches(var2);
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntities(this.victims, ".victims");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public List<ContextAwarePredicate> victims() {
         return this.victims;
      }

      public MinMaxBounds.Ints uniqueEntityTypes() {
         return this.uniqueEntityTypes;
      }
   }
}
