package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

   @Override
   public Codec<FishingRodHookedTrigger.TriggerInstance> codec() {
      return FishingRodHookedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
      LootContext var5 = EntityPredicate.createContext(var1, (Entity)(var3.getHookedIn() != null ? var3.getHookedIn() : var3));
      this.trigger(var1, var3x -> var3x.matches(var2, var5, var4));
   }

   public static record TriggerInstance(
      Optional<ContextAwarePredicate> player, Optional<ItemPredicate> rod, Optional<ContextAwarePredicate> entity, Optional<ItemPredicate> item
   ) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<FishingRodHookedTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(FishingRodHookedTrigger.TriggerInstance::player),
                  ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(FishingRodHookedTrigger.TriggerInstance::rod),
                  EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(FishingRodHookedTrigger.TriggerInstance::entity),
                  ItemPredicate.CODEC.optionalFieldOf("item").forGetter(FishingRodHookedTrigger.TriggerInstance::item)
               )
               .apply(var0, FishingRodHookedTrigger.TriggerInstance::new)
      );

      public TriggerInstance(
         Optional<ContextAwarePredicate> player, Optional<ItemPredicate> rod, Optional<ContextAwarePredicate> entity, Optional<ItemPredicate> item
      ) {
         super();
         this.player = player;
         this.rod = rod;
         this.entity = entity;
         this.item = item;
      }

      public static Criterion<FishingRodHookedTrigger.TriggerInstance> fishedItem(
         Optional<ItemPredicate> var0, Optional<EntityPredicate> var1, Optional<ItemPredicate> var2
      ) {
         return CriteriaTriggers.FISHING_ROD_HOOKED
            .createCriterion(new FishingRodHookedTrigger.TriggerInstance(Optional.empty(), var0, EntityPredicate.wrap(var1), var2));
      }

      public boolean matches(ItemStack var1, LootContext var2, Collection<ItemStack> var3) {
         if (this.rod.isPresent() && !this.rod.get().test(var1)) {
            return false;
         } else if (this.entity.isPresent() && !this.entity.get().matches(var2)) {
            return false;
         } else {
            if (this.item.isPresent()) {
               boolean var4 = false;
               Entity var5 = var2.getParamOrNull(LootContextParams.THIS_ENTITY);
               if (var5 instanceof ItemEntity var6 && this.item.get().test(var6.getItem())) {
                  var4 = true;
               }

               for (ItemStack var7 : var3) {
                  if (this.item.get().test(var7)) {
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
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.entity, ".entity");
      }
   }
}