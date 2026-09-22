package net.minecraft.network.protocol.handshake;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import net.minecraft.network.codec.EnumStreamCodec;
import net.minecraft.network.codec.StreamCodec;

public enum ClientIntent {
   STATUS,
   LOGIN,
   TRANSFER;

   public static final StreamCodec<ByteBuf, ClientIntent> STREAM_CODEC = EnumStreamCodec.custom(ClientIntent::id, ClientIntent::byId);
   private static final int STATUS_ID = 1;
   private static final int LOGIN_ID = 2;
   private static final int TRANSFER_ID = 3;

   private ClientIntent() {
   }

   private static ClientIntent byId(final int id) {
      ClientIntent var10000;
      switch (id) {
         case 1 -> var10000 = STATUS;
         case 2 -> var10000 = LOGIN;
         case 3 -> var10000 = TRANSFER;
         default -> throw new DecoderException("Unknown connection intent: " + id);
      }

      return var10000;
   }

   private int id() {
      byte var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = 1;
         case 1 -> var10000 = 2;
         case 2 -> var10000 = 3;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static ClientIntent[] $values() {
      return new ClientIntent[]{STATUS, LOGIN, TRANSFER};
   }
}
