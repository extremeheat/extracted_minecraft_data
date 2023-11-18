package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class UsingItemTrigger extends SimpleCriterionTrigger<UsingItemTrigger.TriggerInstance> {
   public UsingItemTrigger() {
      super();
   }

   public UsingItemTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      Optional var4 = ItemPredicate.fromJson(var1.get("item"));
      return new UsingItemTrigger.TriggerInstance(var2, var4);
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

      public static Criterion<UsingItemTrigger.TriggerInstance> lookingAt(EntityPredicate.Builder var0, ItemPredicate.Builder var1) {
         return CriteriaTriggers.USING_ITEM
            .createCriterion(new UsingItemTrigger.TriggerInstance(Optional.of(EntityPredicate.wrap(var0)), Optional.of(var1.build())));
      }

      public boolean matches(ItemStack var1) {
         return !this.item.isPresent() || this.item.get().matches(var1);
      }

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         this.item.ifPresent(var1x -> var1.add("item", var1x.serializeToJson()));
         return var1;
      }
   }
}
