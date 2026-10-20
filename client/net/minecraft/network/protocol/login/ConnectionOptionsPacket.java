package net.minecraft.network.protocol.login;

import io.netty.buffer.ByteBuf;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public interface ConnectionOptionsPacket {
   StreamCodec<ByteBuf, Byte> FLAG_FIELD_STREAM_CODEC = ByteBufCodecs.BYTE;
   byte FLAG_ENCRYPTION = 1;
   byte FLAG_COMPRESSION = 2;

   static boolean isEncryption(final @ConnectionOptionsPacket.Flags byte value) {
      return (value & 1) != 0;
   }

   static boolean isCompression(final @ConnectionOptionsPacket.Flags byte value) {
      return (value & 2) != 0;
   }

   @ConnectionOptionsPacket.Flags byte options();

   @Retention(RetentionPolicy.CLASS)
   @Target({ElementType.TYPE_USE})
   public @interface Flags {
   }
}
