package com.mojang.realmsclient.dto;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.jspecify.annotations.Nullable;

public class GuardedSerializer {
   ExclusionStrategy strategy = new ExclusionStrategy() {
      public boolean shouldSkipClass(Class<?> var1) {
         return false;
      }

      public boolean shouldSkipField(FieldAttributes var1) {
         return var1.getAnnotation(Exclude.class) != null;
      }
   };
   private final Gson gson;

   public GuardedSerializer() {
      super();
      this.gson = (new GsonBuilder()).addSerializationExclusionStrategy(this.strategy).addDeserializationExclusionStrategy(this.strategy).create();
   }

   public String toJson(ReflectionBasedSerialization var1) {
      return this.gson.toJson(var1);
   }

   public String toJson(JsonElement var1) {
      return this.gson.toJson(var1);
   }

   public <T extends ReflectionBasedSerialization> @Nullable T fromJson(String var1, Class<T> var2) {
      return (T)(this.gson.fromJson(var1, var2));
   }
}
