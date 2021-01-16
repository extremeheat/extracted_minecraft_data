package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttPublishVariableHeader {
   private final String topicName;
   private final int packetId;

   public MqttPublishVariableHeader(String var1, int var2) {
      super();
      this.topicName = var1;
      this.packetId = var2;
   }

   public String topicName() {
      return this.topicName;
   }

   /** @deprecated */
   @Deprecated
   public int messageId() {
      return this.packetId;
   }

   public int packetId() {
      return this.packetId;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "topicName=" + this.topicName + ", packetId=" + this.packetId + ']';
   }
}
