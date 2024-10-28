package com.mojang.realmsclient.dto;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import javax.annotation.Nullable;

public class GuardedSerializer {
   private final Gson gson = new Gson();

   public GuardedSerializer() {
      super();
   }

   public String toJson(ReflectionBasedSerialization var1) {
      return this.gson.toJson(var1);
   }

   public String toJson(JsonElement var1) {
      return this.gson.toJson(var1);
   }

   @Nullable
   public <T extends ReflectionBasedSerialization> T fromJson(String var1, Class<T> var2) {
      return (ReflectionBasedSerialization)this.gson.fromJson(var1, var2);
   }
}
