package net.minecraft.network;

import io.netty.handler.codec.EncoderException;

public class SkipableEncoderException extends EncoderException {
   public SkipableEncoderException(Throwable var1) {
      super(var1);
   }
}
