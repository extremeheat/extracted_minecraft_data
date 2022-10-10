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
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion {
   private final ICriterionInstance field_192147_a;

   public Criterion(ICriterionInstance var1) {
      super();
      this.field_192147_a = var1;
   }

   public Criterion() {
      super();
      this.field_192147_a = null;
   }

   public void func_192140_a(PacketBuffer var1) {
   }

   public static Criterion func_192145_a(JsonObject var0, JsonDeserializationContext var1) {
      ResourceLocation var2 = new ResourceLocation(JsonUtils.func_151200_h(var0, "trigger"));
      ICriterionTrigger var3 = CriteriaTriggers.func_192119_a(var2);
      if (var3 == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + var2);
      } else {
         ICriterionInstance var4 = var3.func_192166_a(JsonUtils.func_151218_a(var0, "conditions", new JsonObject()), var1);
         return new Criterion(var4);
      }
   }

   public static Criterion func_192146_b(PacketBuffer var0) {
      return new Criterion();
   }

   public static Map<String, Criterion> func_192144_b(JsonObject var0, JsonDeserializationContext var1) {
      HashMap var2 = Maps.newHashMap();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         var2.put(var4.getKey(), func_192145_a(JsonUtils.func_151210_l((JsonElement)var4.getValue(), "criterion"), var1));
      }

      return var2;
   }

   public static Map<String, Criterion> func_192142_c(PacketBuffer var0) {
      HashMap var1 = Maps.newHashMap();
      int var2 = var0.func_150792_a();

      for(int var3 = 0; var3 < var2; ++var3) {
         var1.put(var0.func_150789_c(32767), func_192146_b(var0));
      }

      return var1;
   }

   public static void func_192141_a(Map<String, Criterion> var0, PacketBuffer var1) {
      var1.func_150787_b(var0.size());
      Iterator var2 = var0.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.func_180714_a((String)var3.getKey());
         ((Criterion)var3.getValue()).func_192140_a(var1);
      }

   }

   @Nullable
   public ICriterionInstance func_192143_a() {
      return this.field_192147_a;
   }

   public JsonElement func_200287_b() {
      JsonObject var1 = new JsonObject();
      var1.addProperty("trigger", this.field_192147_a.func_192244_a().toString());
      var1.add("conditions", this.field_192147_a.func_200288_b());
      return var1;
   }
}
