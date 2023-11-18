package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.Criterion;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootContext;

public class ChanneledLightningTrigger extends SimpleCriterionTrigger<ChanneledLightningTrigger.TriggerInstance> {
   public ChanneledLightningTrigger() {
      super();
   }

   public ChanneledLightningTrigger.TriggerInstance createInstance(JsonObject var1, Optional<ContextAwarePredicate> var2, DeserializationContext var3) {
      List var4 = EntityPredicate.fromJsonArray(var1, "victims", var3);
      return new ChanneledLightningTrigger.TriggerInstance(var2, var4);
   }

   public void trigger(ServerPlayer var1, Collection<? extends Entity> var2) {
      List var3 = var2.stream().map(var1x -> EntityPredicate.createContext(var1, var1x)).collect(Collectors.toList());
      this.trigger(var1, var1x -> var1x.matches(var3));
   }

   public static class TriggerInstance extends AbstractCriterionTriggerInstance {
      private final List<ContextAwarePredicate> victims;

      public TriggerInstance(Optional<ContextAwarePredicate> var1, List<ContextAwarePredicate> var2) {
         super(var1);
         this.victims = var2;
      }

      public static Criterion<ChanneledLightningTrigger.TriggerInstance> channeledLightning(EntityPredicate.Builder... var0) {
         return CriteriaTriggers.CHANNELED_LIGHTNING
            .createCriterion(new ChanneledLightningTrigger.TriggerInstance(Optional.empty(), EntityPredicate.wrap(var0)));
      }

      public boolean matches(Collection<? extends LootContext> var1) {
         for(ContextAwarePredicate var3 : this.victims) {
            boolean var4 = false;

            for(LootContext var6 : var1) {
               if (var3.matches(var6)) {
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

      @Override
      public JsonObject serializeToJson() {
         JsonObject var1 = super.serializeToJson();
         var1.add("victims", ContextAwarePredicate.toJson(this.victims));
         return var1;
      }
   }
}
