package net.minecraft.network;

import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.IdDispatchCodec;

public class SkipPacketDecoderException extends DecoderException implements IdDispatchCodec.DontDecorateException, SkipPacketException {
   public SkipPacketDecoderException(String var1) {
      super(var1);
   }

   public SkipPacketDecoderException(Throwable var1) {
      super(var1);
   }
}
