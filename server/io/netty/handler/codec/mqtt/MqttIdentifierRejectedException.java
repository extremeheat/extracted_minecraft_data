package io.netty.handler.codec.mqtt;

import io.netty.handler.codec.DecoderException;

public final class MqttIdentifierRejectedException extends DecoderException {
   private static final long serialVersionUID = -1323503322689614981L;

   public MqttIdentifierRejectedException() {
      super();
   }

   public MqttIdentifierRejectedException(String var1, Throwable var2) {
      super(var1, var2);
   }

   public MqttIdentifierRejectedException(String var1) {
      super(var1);
   }

   public MqttIdentifierRejectedException(Throwable var1) {
      super(var1);
   }
}
