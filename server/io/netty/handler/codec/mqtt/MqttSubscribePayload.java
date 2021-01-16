package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public final class MqttSubscribePayload {
   private final List<MqttTopicSubscription> topicSubscriptions;

   public MqttSubscribePayload(List<MqttTopicSubscription> var1) {
      super();
      this.topicSubscriptions = Collections.unmodifiableList(var1);
   }

   public List<MqttTopicSubscription> topicSubscriptions() {
      return this.topicSubscriptions;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(StringUtil.simpleClassName((Object)this))).append('[');

      for(int var2 = 0; var2 < this.topicSubscriptions.size() - 1; ++var2) {
         var1.append(this.topicSubscriptions.get(var2)).append(", ");
      }

      var1.append(this.topicSubscriptions.get(this.topicSubscriptions.size() - 1));
      var1.append(']');
      return var1.toString();
   }
}
