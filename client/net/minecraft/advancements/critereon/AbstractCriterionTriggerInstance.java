package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;

public abstract class AbstractCriterionTriggerInstance implements CriterionTriggerInstance {
   private final ResourceLocation criterion;
   private final EntityPredicate.Composite player;

   public AbstractCriterionTriggerInstance(ResourceLocation var1, EntityPredicate.Composite var2) {
      super();
      this.criterion = var1;
      this.player = var2;
   }

   public ResourceLocation getCriterion() {
      return this.criterion;
   }

   protected EntityPredicate.Composite getPlayerPredicate() {
      return this.player;
   }

   public JsonObject serializeToJson(SerializationContext var1) {
      JsonObject var2 = new JsonObject();
      var2.add("player", this.player.toJson(var1));
      return var2;
   }

   public String toString() {
      return "AbstractCriterionInstance{criterion=" + this.criterion + "}";
   }
}
