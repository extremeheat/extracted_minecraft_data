package com.mojang.realmsclient.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;
import javax.annotation.Nullable;

public class OutboundPlayer implements ReflectionBasedSerialization {
   @Nullable
   public String name;
   @Nullable
   @SerializedName("uuid")
   @JsonAdapter(UUIDTypeAdapter.class)
   public UUID uuid;

   public OutboundPlayer() {
      super();
   }
}
