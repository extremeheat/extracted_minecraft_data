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
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;

public class KilledByArrowTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public KilledByArrowTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return KilledByArrowTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, Collection<Entity> var2, @Nullable ItemStack var3) {
      ArrayList var4 = Lists.newArrayList();
      HashSet var5 = Sets.newHashSet();
      Iterator var6 = var2.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         var5.add(var7.getType());
         var4.add(EntityPredicate.createContext(var1, var7));
      }

      this.trigger(var1, (var3x) -> {
         return var3x.matches(var4, var5.size(), var3);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, List<ContextAwarePredicate> victims, MinMaxBounds.Ints uniqueEntityTypes, Optional<ItemPredicate> firedFromWeapon) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), EntityPredicate.ADVANCEMENT_CODEC.listOf().optionalFieldOf("victims", List.of()).forGetter(TriggerInstance::victims), MinMaxBounds.Ints.CODEC.optionalFieldOf("unique_entity_types", MinMaxBounds.Ints.ANY).forGetter(TriggerInstance::uniqueEntityTypes), ItemPredicate.CODEC.optionalFieldOf("fired_from_weapon").forGetter(TriggerInstance::firedFromWeapon)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> var1, List<ContextAwarePredicate> var2, MinMaxBounds.Ints var3, Optional<ItemPredicate> var4) {
         super();
         this.player = var1;
         this.victims = var2;
         this.uniqueEntityTypes = var3;
         this.firedFromWeapon = var4;
      }

      public static Criterion<TriggerInstance> crossbowKilled(HolderGetter<Item> var0, EntityPredicate.Builder... var1) {
         return CriteriaTriggers.KILLED_BY_ARROW.createCriterion(new TriggerInstance(Optional.empty(), EntityPredicate.wrap(var1), MinMaxBounds.Ints.ANY, Optional.of(ItemPredicate.Builder.item().of(var0, Items.CROSSBOW).build())));
      }

      public static Criterion<TriggerInstance> crossbowKilled(HolderGetter<Item> var0, MinMaxBounds.Ints var1) {
         return CriteriaTriggers.KILLED_BY_ARROW.createCriterion(new TriggerInstance(Optional.empty(), List.of(), var1, Optional.of(ItemPredicate.Builder.item().of(var0, Items.CROSSBOW).build())));
      }

      public boolean matches(Collection<LootContext> var1, int var2, @Nullable ItemStack var3) {
         if (this.firedFromWeapon.isPresent() && (var3 == null || !((ItemPredicate)this.firedFromWeapon.get()).test(var3))) {
            return false;
         } else {
            if (!this.victims.isEmpty()) {
               ArrayList var4 = Lists.newArrayList(var1);
               Iterator var5 = this.victims.iterator();

               while(var5.hasNext()) {
                  ContextAwarePredicate var6 = (ContextAwarePredicate)var5.next();
                  boolean var7 = false;
                  Iterator var8 = var4.iterator();

                  while(var8.hasNext()) {
                     LootContext var9 = (LootContext)var8.next();
                     if (var6.matches(var9)) {
                        var8.remove();
                        var7 = true;
                        break;
                     }
                  }

                  if (!var7) {
                     return false;
                  }
               }
            }

            return this.uniqueEntityTypes.matches(var2);
         }
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

      public Optional<ItemPredicate> firedFromWeapon() {
         return this.firedFromWeapon;
      }
   }
}
