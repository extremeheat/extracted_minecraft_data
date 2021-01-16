package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;

public final class MqttTopicSubscription {
   private final String topicFilter;
   private final MqttQoS qualityOfService;

   public MqttTopicSubscription(String var1, MqttQoS var2) {
      super();
      this.topicFilter = var1;
      this.qualityOfService = var2;
   }

   public String topicName() {
      return this.topicFilter;
   }

   public MqttQoS qualityOfService() {
      return this.qualityOfService;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "topicFilter=" + this.topicFilter + ", qualityOfService=" + this.qualityOfService + ']';
   }
}
