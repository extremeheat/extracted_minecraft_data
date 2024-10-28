package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.Iterator;
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

public class FishingRodHookedTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public FishingRodHookedTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return FishingRodHookedTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, FishingHook var3, Collection<ItemStack> var4) {
      LootContext var5 = EntityPredicate.createContext(var1, (Entity)(var3.getHookedIn() != null ? var3.getHookedIn() : var3));
      this.trigger(var1, (var3x) -> {
         return var3x.matches(var2, var5, var4);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> rod, Optional<ContextAwarePredicate> entity, Optional<ItemPredicate> item) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("rod").forGetter(TriggerInstance::rod), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2, Optional<ContextAwarePredicate> var3, Optional<ItemPredicate> var4) {
         super();
         this.player = var1;
         this.rod = var2;
         this.entity = var3;
         this.item = var4;
      }

      public static Criterion<TriggerInstance> fishedItem(Optional<ItemPredicate> var0, Optional<EntityPredicate> var1, Optional<ItemPredicate> var2) {
         return CriteriaTriggers.FISHING_ROD_HOOKED.createCriterion(new TriggerInstance(Optional.empty(), var0, EntityPredicate.wrap(var1), var2));
      }

      public boolean matches(ItemStack var1, LootContext var2, Collection<ItemStack> var3) {
         if (this.rod.isPresent() && !((ItemPredicate)this.rod.get()).matches(var1)) {
            return false;
         } else if (this.entity.isPresent() && !((ContextAwarePredicate)this.entity.get()).matches(var2)) {
            return false;
         } else {
            if (this.item.isPresent()) {
               boolean var4 = false;
               Entity var5 = (Entity)var2.getParamOrNull(LootContextParams.THIS_ENTITY);
               if (var5 instanceof ItemEntity) {
                  ItemEntity var6 = (ItemEntity)var5;
                  if (((ItemPredicate)this.item.get()).matches(var6.getItem())) {
                     var4 = true;
                  }
               }

               Iterator var8 = var3.iterator();

               while(var8.hasNext()) {
                  ItemStack var7 = (ItemStack)var8.next();
                  if (((ItemPredicate)this.item.get()).matches(var7)) {
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

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.entity, ".entity");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<ItemPredicate> rod() {
         return this.rod;
      }

      public Optional<ContextAwarePredicate> entity() {
         return this.entity;
      }

      public Optional<ItemPredicate> item() {
         return this.item;
      }
   }
}
