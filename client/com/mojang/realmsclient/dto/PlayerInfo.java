package com.mojang.realmsclient.dto;

import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.mojang.util.UUIDTypeAdapter;
import java.util.UUID;

public class PlayerInfo extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("name")
   public final String name;
   @SerializedName("uuid")
   @JsonAdapter(UUIDTypeAdapter.class)
   public final UUID uuid;
   @SerializedName("operator")
   public boolean operator;
   @SerializedName("accepted")
   public final boolean accepted;
   @SerializedName("online")
   public final boolean online;

   public PlayerInfo(String var1, UUID var2, boolean var3, boolean var4, boolean var5) {
      super();
      this.name = var1;
      this.uuid = var2;
      this.operator = var3;
      this.accepted = var4;
      this.online = var5;
   }
}
