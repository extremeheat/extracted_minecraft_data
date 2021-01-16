package io.netty.handler.codec.mqtt;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public final class MqttFixedHeader {
   private final MqttMessageType messageType;
   private final boolean isDup;
   private final MqttQoS qosLevel;
   private final boolean isRetain;
   private final int remainingLength;

   public MqttFixedHeader(MqttMessageType var1, boolean var2, MqttQoS var3, boolean var4, int var5) {
      super();
      this.messageType = (MqttMessageType)ObjectUtil.checkNotNull(var1, "messageType");
      this.isDup = var2;
      this.qosLevel = (MqttQoS)ObjectUtil.checkNotNull(var3, "qosLevel");
      this.isRetain = var4;
      this.remainingLength = var5;
   }

   public MqttMessageType messageType() {
      return this.messageType;
   }

   public boolean isDup() {
      return this.isDup;
   }

   public MqttQoS qosLevel() {
      return this.qosLevel;
   }

   public boolean isRetain() {
      return this.isRetain;
   }

   public int remainingLength() {
      return this.remainingLength;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "messageType=" + this.messageType + ", isDup=" + this.isDup + ", qosLevel=" + this.qosLevel + ", isRetain=" + this.isRetain + ", remainingLength=" + this.remainingLength + ']';
   }
}
