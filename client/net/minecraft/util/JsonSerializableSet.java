package net.minecraft.util;

import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import java.util.Set;

public class JsonSerializableSet extends ForwardingSet implements IJsonSerializable {
   private final Set field_151004_a = Sets.newHashSet();

   public JsonSerializableSet() {
      super();
   }

   @Override
   public void func_152753_a(JsonElement var1) {
      if (var1.isJsonArray()) {
         for(JsonElement var3 : var1.getAsJsonArray()) {
            this.add(var3.getAsString());
         }
      }
   }

   @Override
   public JsonElement func_151003_a() {
      JsonArray var1 = new JsonArray();

      for(String var3 : this) {
         var1.add(new JsonPrimitive(var3));
      }

      return var1;
   }

   protected Set delegate() {
      return this.field_151004_a;
   }
}
