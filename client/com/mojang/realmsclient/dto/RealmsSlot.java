package com.mojang.realmsclient.dto;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public final class RealmsSlot implements ReflectionBasedSerialization {
   @SerializedName("slotId")
   public int slotId;
   @SerializedName("options")
   @JsonAdapter(RealmsWorldOptionsJsonAdapter.class)
   public RealmsWorldOptions options;
   @SerializedName("settings")
   public List<RealmsSetting> settings;

   public RealmsSlot(int var1, RealmsWorldOptions var2, List<RealmsSetting> var3) {
      super();
      this.slotId = var1;
      this.options = var2;
      this.settings = var3;
   }

   public static RealmsSlot defaults(int var0) {
      return new RealmsSlot(var0, RealmsWorldOptions.createEmptyDefaults(), List.of(RealmsSetting.hardcoreSetting(false)));
   }

   public RealmsSlot clone() {
      return new RealmsSlot(this.slotId, this.options.clone(), new ArrayList(this.settings));
   }

   public boolean isHardcore() {
      return RealmsSetting.isHardcore(this.settings);
   }

   // $FF: synthetic method
   public Object clone() throws CloneNotSupportedException {
      return this.clone();
   }

   static class RealmsWorldOptionsJsonAdapter extends TypeAdapter<RealmsWorldOptions> {
      private RealmsWorldOptionsJsonAdapter() {
         super();
      }

      public void write(JsonWriter var1, RealmsWorldOptions var2) throws IOException {
         var1.jsonValue((new GuardedSerializer()).toJson((ReflectionBasedSerialization)var2));
      }

      public RealmsWorldOptions read(JsonReader var1) throws IOException {
         String var2 = var1.nextString();
         return RealmsWorldOptions.parse(new GuardedSerializer(), var2);
      }

      // $FF: synthetic method
      public Object read(final JsonReader var1) throws IOException {
         return this.read(var1);
      }

      // $FF: synthetic method
      public void write(final JsonWriter var1, final Object var2) throws IOException {
         this.write(var1, (RealmsWorldOptions)var2);
      }
   }
}
