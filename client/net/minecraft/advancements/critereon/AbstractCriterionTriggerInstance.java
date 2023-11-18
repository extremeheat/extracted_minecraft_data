package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;

public abstract class AbstractCriterionTriggerInstance implements SimpleCriterionTrigger.SimpleInstance {
   private final Optional<ContextAwarePredicate> player;

   public AbstractCriterionTriggerInstance(Optional<ContextAwarePredicate> var1) {
      super();
      this.player = var1;
   }

   @Override
   public Optional<ContextAwarePredicate> playerPredicate() {
      return this.player;
   }

   @Override
   public JsonObject serializeToJson() {
      JsonObject var1 = new JsonObject();
      this.player.ifPresent(var1x -> var1.add("player", var1x.toJson()));
      return var1;
   }
}
