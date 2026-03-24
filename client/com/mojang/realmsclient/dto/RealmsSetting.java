package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public record RealmsSetting(String name, String value) implements ReflectionBasedSerialization {
   public RealmsSetting {
      super();
   }

   public static RealmsSetting hardcoreSetting(final boolean hardcore) {
      return new RealmsSetting("hardcore", Boolean.toString(hardcore));
   }

   public static boolean isHardcore(final List<RealmsSetting> settings) {
      for(RealmsSetting setting : settings) {
         if (setting.name().equals("hardcore")) {
            return Boolean.parseBoolean(setting.value());
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
