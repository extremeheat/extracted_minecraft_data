package io.netty.handler.codec.mqtt;

public final class MqttUnsubAckMessage extends MqttMessage {
   public MqttUnsubAckMessage(MqttFixedHeader var1, MqttMessageIdVariableHeader var2) {
      super(var1, var2, (Object)null);
   }

   public MqttMessageIdVariableHeader variableHeader() {
      return (MqttMessageIdVariableHeader)super.variableHeader();
   }
}
