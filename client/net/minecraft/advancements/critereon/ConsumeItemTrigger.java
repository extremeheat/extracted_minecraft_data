package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ConsumeItemTrigger extends SimpleCriterionTrigger<ConsumeItemTrigger.TriggerInstance> {
   public ConsumeItemTrigger() {
      super();
   }

   public ConsumeItemTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      return new ConsumeItemTrigger.TriggerInstance(var2, ItemPredicate.fromJson(var1.get("item")));
   }

   public void trigger(ServerPlayer var1, ItemStack var2) {
      this.trigger(var1, var1x -> var1x.matches(var2));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final Optional<ItemPredicate> item;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, Optional<ItemPredicate> var2) {
         super(var1);
         this.item = var2;
      }

      public static Criterion<ConsumeItemTrigger.TriggerInstance> usedItem() {
         return CriteriaTriggers.CONSUME_ITEM.createCriterion(new ConsumeItemTrigger.TriggerInstance(Optional.empty(), Optional.empty()));
      }

      public static Criterion<ConsumeItemTrigger.TriggerInstance> usedItem(ItemLike var0) {
         return usedItem(ItemPredicate.Builder.item().of(var0.asItem()));
      }

      public static Criterion<ConsumeItemTrigger.TriggerInstance> usedItem(ItemPredicate.Builder var0) {
         return CriteriaTriggers.CONSUME_ITEM.createCriterion(new ConsumeItemTrigger.TriggerInstance(Optional.empty(), Optional.of(var0.build())));
      }

      public boolean matches(ItemStack var1) {
         return this.item.isEmpty() || this.item.get().matches(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         return var1;
      }
   }
}
