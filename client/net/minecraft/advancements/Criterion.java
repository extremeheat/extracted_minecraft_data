package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Criterion {
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

   public static Criterion criterionFromJson(JsonObject var0, JsonDeserializationContext var1) {
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

   public static Map<String, Criterion> criteriaFromJson(JsonObject var0, JsonDeserializationContext var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.put(var4.getKey(), criterionFromJson(GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), "criterion"), var1));
      }

      return var2;
   }

   public static Map<String, Criterion> criteriaFromNetwork(FriendlyByteBuf var0) {
      HashMap var1 = Maps.newHashMap();
      int var2 = var0.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.put(var0.readUtf(32767), criterionFromNetwork(var0));
      }

      return var1;
   }

   public static void serializeToNetwork(Map<String, Criterion> var0, FriendlyByteBuf var1) {
      var1.writeVarInt(var0.size());
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.writeUtf((String)var3.getKey());
         ((Criterion)var3.getValue()).serializeToNetwork(var1);
      }

   }

   @Nullable
   public CriterionTriggerInstance getTrigger() {
      return this.trigger;
   }

   public JsonElement serializeToJson() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("trigger", this.trigger.getCriterion().toString());
      var1.add("conditions", this.trigger.serializeToJson());
      return var1;
   }
}
