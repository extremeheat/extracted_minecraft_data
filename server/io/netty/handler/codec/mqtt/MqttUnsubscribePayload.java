package io.netty.handler.codec.mqtt;

import io.netty.util.internal.StringUtil;
import java.util.Collections;
import java.util.List;

public final class MqttUnsubscribePayload {
   private final List<String> topics;

   public MqttUnsubscribePayload(List<String> var1) {
      super();
      this.topics = Collections.unmodifiableList(var1);
   }

   public List<String> topics() {
      return this.topics;
   }

   public String toString() {
      StringBuilder var1 = (new StringBuilder(StringUtil.simpleClassName((Object)this))).append('[');

      for(int var2 = 0; var2 < this.topics.size() - 1; ++var2) {
         var1.append("topicName = ").append((String)this.topics.get(var2)).append(", ");
      }

      var1.append("topicName = ").append((String)this.topics.get(this.topics.size() - 1)).append(']');
      return var1.toString();
   }
}
