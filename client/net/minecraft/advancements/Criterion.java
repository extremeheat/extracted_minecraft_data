package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public record Criterion<T extends CriterionTriggerInstance>(CriterionTrigger<T> a, T b) {
   private final CriterionTrigger<T> trigger;
   private final T triggerInstance;

   public Criterion(CriterionTrigger<T> var1, T var2) {
      super();
      this.trigger = var1;
      this.triggerInstance = var2;
   }

   public static Criterion<?> criterionFromJson(JsonObject var0, DeserializationContext var1) {
      ResourceLocation var2 = new ResourceLocation(GsonHelper.getAsString(var0, "trigger"));
      CriterionTrigger var3 = CriteriaTriggers.getCriterion(var2);
      if (var3 == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + var2);
      } else {
         return criterionFromJson(var0, var1, var3);
      }
   }

   private static <T extends CriterionTriggerInstance> Criterion<T> criterionFromJson(JsonObject var0, DeserializationContext var1, CriterionTrigger<T> var2) {
      CriterionTriggerInstance var3 = var2.createInstance(GsonHelper.getAsJsonObject(var0, "conditions", new JsonObject()), var1);
      return new Criterion<>(var2, (T)var3);
   }

   public static Map<String, Criterion<?>> criteriaFromJson(JsonObject var0, DeserializationContext var1) {
      HashMap var2 = Maps.newHashMap();

      for(Entry var4 : var0.entrySet()) {
         var2.put((String)var4.getKey(), criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), "criterion"), var1));
      }

      return var2;
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("trigger", Objects.requireNonNull(CriteriaTriggers.getId(this.trigger), "Unregistered trigger").toString());
      JsonObject var2 = this.triggerInstance.serializeToJson();
      if (var2.size() != 0) {
         var1.add("conditions", var2);
      }

      return var1;
   }
}
