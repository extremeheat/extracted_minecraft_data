package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class PlayerInteractTrigger extends SimpleCriterionTrigger<TriggerInstance> {
   public PlayerInteractTrigger() {
      super();
   }

   public Codec<TriggerInstance> codec() {
      return PlayerInteractTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, ItemStack var2, Entity var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var3);
      this.trigger(var1, (var2x) -> {
         return var2x.matches(var2, var4);
      });
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity) implements SimpleCriterionTrigger.SimpleInstance {
      public static final Codec<TriggerInstance> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(TriggerInstance::player), ItemPredicate.CODEC.optionalFieldOf("item").forGetter(TriggerInstance::item), EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("entity").forGetter(TriggerInstance::entity)).apply(var0, TriggerInstance::new);
      });

      public TriggerInstance(Optional<ContextAwarePredicate> player, Optional<ItemPredicate> item, Optional<ContextAwarePredicate> entity) {
         super();
         this.player = player;
         this.item = item;
         this.entity = entity;
      }

      public static Criterion<TriggerInstance> itemUsedOnEntity(Optional<ContextAwarePredicate> var0, ItemPredicate.Builder var1, Optional<ContextAwarePredicate> var2) {
         return CriteriaTriggers.PLAYER_INTERACTED_WITH_ENTITY.createCriterion(new TriggerInstance(var0, Optional.of(var1.build()), var2));
      }

      public static Criterion<TriggerInstance> itemUsedOnEntity(ItemPredicate.Builder var0, Optional<ContextAwarePredicate> var1) {
         return itemUsedOnEntity(Optional.empty(), var0, var1);
      }

      public boolean matches(ItemStack var1, LootContext var2) {
         if (this.item.isPresent() && !((ItemPredicate)this.item.get()).test(var1)) {
            return false;
         } else {
            return this.entity.isEmpty() || ((ContextAwarePredicate)this.entity.get()).matches(var2);
         }
      }

      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.entity, ".entity");
      }

      public Optional<ContextAwarePredicate> player() {
         return this.player;
      }

      public Optional<ItemPredicate> item() {
         return this.item;
      }

      public Optional<ContextAwarePredicate> entity() {
         return this.entity;
      }
   }
}
