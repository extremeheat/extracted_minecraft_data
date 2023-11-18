package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<FishingRodHookedTrigger.TriggerInstance> {
   public FishingRodHookedTrigger() {
      super();
   }

   public FishingRodHookedTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ItemPredicate.fromJson(var1.get("rod"));
      Optional var5 = EntityPredicate.fromJson(var1, "entity", var3);
      Optional var6 = ItemPredicate.fromJson(var1.get("item"));
      return new FishingRodHookedTrigger.TriggerInstance(var2, var4, var5, var6);
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
      LootContext var5 = EntityPredicate.createContext(var1, (Entity)(var3.getHookedIn() != null ? var3.getHookedIn() : var3));
      this.trigger(var1, var3x -> var3x.matches(var2, var5, var4));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ItemPredicate> rod;
      private final Optional<ContextAwarePredicate> entity;
      private final Optional<ItemPredicate> item;

      public TriggerInstance(
         Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, Optional<ContextAwarePredicate> var3, Optional<ItemPredicate> var4
      ) {
         super(var1);
         this.rod = var2;
         this.entity = var3;
         this.item = var4;
      }

      public static Criterion<FishingRodHookedTrigger.TriggerInstance> fishedItem(
         Optional<ItemPredicate> var0, Optional<EntityPredicate> var1, Optional<ItemPredicate> var2
      ) {
         return CriteriaTriggers.FISHING_ROD_HOOKED
            .createCriterion(new FishingRodHookedTrigger.TriggerInstance(Optional.empty(), var0, EntityPredicate.wrap(var1), var2));
      }

      public boolean matches(ItemStack var1, LootContext var2, Collection<ItemStack> var3) {
         if (this.rod.isPresent() && !this.rod.get().matches(var1)) {
            return false;
         } else if (this.entity.isPresent() && !this.entity.get().matches(var2)) {
            return false;
         } else {
            if (this.item.isPresent()) {
               boolean var4 = false;
               Entity var5 = var2.getParamOrNull(LootContextParams.THIS_ENTITY);
               if (var5 instanceof ItemEntity var6 && this.item.get().matches(var6.getItem())) {
                  var4 = true;
               }

               for(ItemStack var7 : var3) {
                  if (this.item.get().matches(var7)) {
                     var4 = true;
                     break;
                  }
               }

               if (!var4) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.rod.ifPresent(var1x -> var1.add("rod", var1x.serializeToJson()));
         this.entity.ifPresent(var1x -> var1.add("entity", var1x.toJson()));
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         return var1;
      }
   }
}
