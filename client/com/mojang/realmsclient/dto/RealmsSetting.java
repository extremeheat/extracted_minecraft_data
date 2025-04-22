package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record RealmsSetting(String name, String value) implements ReflectionBasedSerialization {
   public RealmsSetting(String var1, String var2) {
      super();
      this.name = var1;
      this.value = var2;
   }

   public static RealmsSetting hardcoreSetting(boolean var0) {
      return new RealmsSetting("hardcore", Boolean.toString(var0));
   }

   public static boolean isHardcore(List<RealmsSetting> var0) {
      for(RealmsSetting var2 : var0) {
         if (var2.name().equals("hardcore")) {
            return Boolean.parseBoolean(var2.value());
         }
      }

      return false;
   }

   @SerializedName("name")
   public String name() {
      return this.name;
   }

   @SerializedName("value")
   public String value() {
      return this.value;
   }
}
