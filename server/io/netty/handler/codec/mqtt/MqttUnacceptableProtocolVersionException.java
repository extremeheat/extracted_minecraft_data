package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderException;

public final class MqttUnacceptableProtocolVersionException extends DecoderException {
   private static final long serialVersionUID = 4914652213232455749L;

   public MqttUnacceptableProtocolVersionException() {
      super();
   }

   public MqttUnacceptableProtocolVersionException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public MqttUnacceptableProtocolVersionException(String var1) {
      super(var1);
   }

   public MqttUnacceptableProtocolVersionException(Throwable var1) {
      super(var1);
   }
}
