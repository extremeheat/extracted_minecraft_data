package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttConnectVariableHeader {
   private final String name;
   private final int version;
   private final boolean hasUserName;
   private final boolean hasPassword;
   private final boolean isWillRetain;
   private final int willQos;
   private final boolean isWillFlag;
   private final boolean isCleanSession;
   private final int keepAliveTimeSeconds;

   public MqttConnectVariableHeader(String var1, int var2, boolean var3, boolean var4, boolean var5, int var6, boolean var7, boolean var8, int var9) {
      super();
      this.name = var1;
      this.version = var2;
      this.hasUserName = var3;
      this.hasPassword = var4;
      this.isWillRetain = var5;
      this.willQos = var6;
      this.isWillFlag = var7;
      this.isCleanSession = var8;
      this.keepAliveTimeSeconds = var9;
   }

   public String name() {
      return this.name;
   }

   public int version() {
      return this.version;
   }

   public boolean hasUserName() {
      return this.hasUserName;
   }

   public boolean hasPassword() {
      return this.hasPassword;
   }

   public boolean isWillRetain() {
      return this.isWillRetain;
   }

   public int willQos() {
      return this.willQos;
   }

   public boolean isWillFlag() {
      return this.isWillFlag;
   }

   public boolean isCleanSession() {
      return this.isCleanSession;
   }

   public int keepAliveTimeSeconds() {
      return this.keepAliveTimeSeconds;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "name=" + this.name + ", version=" + this.version + ", hasUserName=" + this.hasUserName + ", hasPassword=" + this.hasPassword + ", isWillRetain=" + this.isWillRetain + ", isWillFlag=" + this.isWillFlag + ", isCleanSession=" + this.isCleanSession + ", keepAliveTimeSeconds=" + this.keepAliveTimeSeconds + ']';
   }
}
