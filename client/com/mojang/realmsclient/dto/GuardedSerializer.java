package com.mojang.realmsclient.dto;

import com.google.gson.Gson;
import javax.annotation.Nullable;

public class GuardedSerializer {
   private final Gson gson = new Gson();

   public GuardedSerializer() {
      super();
   }

   public String toJson(ReflectionBasedSerialization var1) {
      return this.gson.toJson(var1);
   }

   @Nullable
   public <T extends ReflectionBasedSerialization> T fromJson(String var1, Class<T> var2) {
      return (T)this.gson.fromJson(var1, var2);
   }
}
