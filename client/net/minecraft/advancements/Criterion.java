package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
   @Nullable
   private final CriterionTriggerInstance trigger;

   public Criterion(CriterionTriggerInstance var1) {
      super();
      this.trigger = var1;
   }

   public Criterion() {
      super();
      this.trigger = null;
   }

   public void serializeToNetwork(FriendlyByteBuf var1) {
   }

   public static Criterion criterionFromJson(JsonObject var0, DeserializationContext var1) {
      ResourceLocation var2 = new ResourceLocation(GsonHelper.getAsString(var0, "trigger"));
      CriterionTrigger var3 = CriteriaTriggers.getCriterion(var2);
      if (var3 == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + var2);
      } else {
         CriterionTriggerInstance var4 = var3.createInstance(GsonHelper.getAsJsonObject(var0, "conditions", new JsonObject()), var1);
         return new Criterion(var4);
      }
   }

   public static Criterion criterionFromNetwork(FriendlyByteBuf var0) {
      return new Criterion();
   }

   public static Map<String, Criterion> criteriaFromJson(JsonObject var0, DeserializationContext var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.put((String)var4.getKey(), criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), "criterion"), var1));
      }

      return var2;
   }

   public static Map<String, Criterion> criteriaFromNetwork(FriendlyByteBuf var0) {
      return var0.readMap(FriendlyByteBuf::readUtf, Criterion::criterionFromNetwork);
   }

   public static void serializeToNetwork(Map<String, Criterion> var0, FriendlyByteBuf var1) {
      var1.writeMap(var0, FriendlyByteBuf::writeUtf, (var0x, var1x) -> {
         var1x.serializeToNetwork(var0x);
      });
   }

   @Nullable
   public CriterionTriggerInstance getTrigger() {
      return this.trigger;
   }

   public JsonElement serializeToJson() {
      if (this.trigger == null) {
         throw new JsonSyntaxException("Missing trigger");
      } else {
         JsonObject var1 = new JsonObject();
         var1.addProperty("trigger", this.trigger.getCriterion().toString());
         JsonObject var2 = this.trigger.serializeToJson(SerializationContext.INSTANCE);
         if (var2.size() != 0) {
            var1.add("conditions", var2);
         }

         return var1;
      }
   }
}
