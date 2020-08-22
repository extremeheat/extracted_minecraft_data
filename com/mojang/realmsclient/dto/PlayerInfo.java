package com.mojang.realmsclient.dto;

public class PlayerInfo extends ValueObject {
   private String name;
   private String uuid;
   private boolean operator;
   private boolean accepted;
   private boolean online;

   public String getName() {
      return this.name;
   }

   public void setName(String var1) {
      this.name = var1;
   }

   public String getUuid() {
      return this.uuid;
   }

   public void setUuid(String var1) {
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
