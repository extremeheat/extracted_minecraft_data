package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;

public class TradeTrigger extends SimpleCriterionTrigger<TradeTrigger.TriggerInstance> {
   public TradeTrigger() {
      super();
   }

   @Override
   public Codec<TradeTrigger.TriggerInstance> codec() {
      return TradeTrigger.TriggerInstance.CODEC;
   }

   public void trigger(ServerPlayer var1, AbstractVillager var2, ItemStack var3) {
      LootContext var4 = EntityPredicate.createContext(var1, var2);
      this.trigger(var1, var2x -> var2x.matches(var4, var3));
   }

   public static record TriggerInstance(Optional<ContextAwarePredicate> b, Optional<ContextAwarePredicate> c, Optional<ItemPredicate> d)
      implements SimpleCriterionTrigger.SimpleInstance {
      private final Optional<ContextAwarePredicate> player;
      private final Optional<ContextAwarePredicate> villager;
      private final Optional<ItemPredicate> item;
      public static final Codec<TradeTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TradeTrigger.TriggerInstance::player),
                  ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "villager").forGetter(TradeTrigger.TriggerInstance::villager),
                  ExtraCodecs.strictOptionalField(ItemPredicate.CODEC, "item").forGetter(TradeTrigger.TriggerInstance::item)
               )
               .apply(var0, TradeTrigger.TriggerInstance::new)
      );

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ContextAwarePredicate> var2, Optional<ItemPredicate> var3) {
         super();
         this.player = var1;
         this.villager = var2;
         this.item = var3;
      }

      public static Criterion<TradeTrigger.TriggerInstance> tradedWithVillager() {
         return CriteriaTriggers.TRADE.createCriterion(new TradeTrigger.TriggerInstance(Optional.empty(), Optional.empty(), Optional.empty()));
      }

      public static Criterion<TradeTrigger.TriggerInstance> tradedWithVillager(EntityPredicate.Builder var0) {
         return CriteriaTriggers.TRADE
            .createCriterion(new TradeTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.empty(), Optional.empty()));
      }

      public boolean matches(LootContext var1, ItemStack var2) {
         if (this.villager.isPresent() && !this.villager.get().matches(var1)) {
            return false;
         } else {
            return !this.item.isPresent() || ((ItemPredicate)this.item.get()).matches(var2);
         }
      }

      @Override
      public void validate(CriterionValidator var1) {
         SimpleCriterionTrigger.SimpleInstance.super.validate(var1);
         var1.validateEntity(this.villager, ".villager");
      }
   }
}
