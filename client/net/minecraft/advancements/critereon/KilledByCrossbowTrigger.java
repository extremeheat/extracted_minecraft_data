package net.minecraft.advancements.critereon;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
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

public class KilledByCrossbowTrigger extends SimpleCriterionTrigger<KilledByCrossbowTrigger.TriggerInstance> {
   public KilledByCrossbowTrigger() {
      super();
   }

   @Override
   public Codec<KilledByCrossbowTrigger.TriggerInstance> codec() {
      return KilledByCrossbowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2) {
      ArrayList var3 = Lists.newArrayList();
      HashSet var4 = Sets.newHashSet();

      for(Entity var6 : var2) {
         var4.add(var6.getType());
         var3.add(EntityPredicate.createContext(var1, var6));
      }

      this.trigger(var1, var2x -> var2x.matches(var3, var4.size()));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, List<ContextAwarePredicate> c, MinMaxBounds.Ints d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final List<ContextAwarePredicate> victims;
      private final MinMaxBounds.Ints uniqueEntityTypes;
      public static final Codec<KilledByCrossbowTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(KilledByCrossbowTrigger.TriggerInstance::player),
                  EntityPredicate.ADVANCEMENT_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(KilledByCrossbowTrigger.TriggerInstance::victims),
                  MinMaxBounds.Ints.CODEC
                     .optionalFieldOf("unique_entity_types", MinMaxBounds.Ints.ANY)
                     .forGetter(KilledByCrossbowTrigger.TriggerInstance::uniqueEntityTypes)
               )
               .apply(var0, KilledByCrossbowTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, List<ContextAwarePredicate> var2, MinMaxBounds.Ints var3) {
         super();
         this.player = var1;
         this.victims = var2;
         this.uniqueEntityTypes = var3;
      }

      public static Criterion<KilledByCrossbowTrigger.TriggerInstance> crossbowKilled(EntityPredicate.Builder... var0) {
         return CriteriaTriggers.KILLED_BY_CROSSBOW
            .createCriterion(new KilledByCrossbowTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0), MinMaxBounds.Ints.ANY));
      }

      public static Criterion<KilledByCrossbowTrigger.TriggerInstance> crossbowKilled(MinMaxBounds.Ints var0) {
         return CriteriaTriggers.KILLED_BY_CROSSBOW.createCriterion(new KilledByCrossbowTrigger.TriggerInstance(Optional.empty(), List.of(), var0));
      }

      public boolean matches(Collection<LootContext> var1, int var2) {
         if (!this.victims.isEmpty()) {
            ArrayList var3 = Lists.newArrayList(var1);

            for(ContextAwarePredicate var5 : this.victims) {
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

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntities(this.victims, ".victims");
      }
   }
}
