package net.minecraft.network;

import io.netty.handler.codec.EncoderException;
import net.minecraft.network.codec.IdDispatchCodec;

public class SkipPacketEncoderException extends EncoderException implements IdDispatchCodec.DontDecorateException, SkipPacketException {
   public SkipPacketEncoderException(String var1) {
      super(var1);
   }

   public SkipPacketEncoderException(Throwable var1) {
      super(var1);
   }
}
