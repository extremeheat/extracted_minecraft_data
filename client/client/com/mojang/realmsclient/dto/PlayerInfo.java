package com.mojang.realmsclient.dto;

import com.google.gson.annotations.SerializedName;
import java.util.UUID;

public class PlayerInfo extends ValueObject implements ReflectionBasedSerialization {
   @SerializedName("name")
   private String name;
   @SerializedName("uuid")
   private UUID uuid;
   @SerializedName("operator")
   private boolean operator;
   @SerializedName("accepted")
   private boolean accepted;
   @SerializedName("online")
   private boolean online;

   public PlayerInfo() {
      super();
   }

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public void setUuid(UUID var1) {
      this.uuid = var1;
   }

   public boolean isOperator() {
      return this.operator;
   }

   public void setOperator(boolean var1) {
      this.operator = var1;
   }

   public boolean getAccepted() {
      return this.accepted;
   }

   public void setAccepted(boolean var1) {
      this.accepted = var1;
   }

   public boolean getOnline() {
      return this.online;
   }

   public void setOnline(boolean var1) {
      this.online = var1;
   }
}
