package net.minecraft.server.network;

import io.netty.buffer.ByteBuf;
import java.nio.charset.StandardCharsets;

public class LegacyProtocolUtils {
   public static final int CUSTOM_PAYLOAD_PACKET_ID = 250;
   public static final String CUSTOM_PAYLOAD_PACKET_PING_CHANNEL = "MC|PingHost";
   public static final int GET_INFO_PACKET_ID = 254;
   public static final int GET_INFO_PACKET_VERSION_1 = 1;
   public static final int DISCONNECT_PACKET_ID = 255;
   public static final int FAKE_PROTOCOL_VERSION = 127;

   public LegacyProtocolUtils() {
      super();
   }

   public static void writeLegacyString(ByteBuf var0, String var1) {
      var0.writeShort(var1.length());
      var0.writeCharSequence(var1, StandardCharsets.UTF_16BE);
   }

   public static String readLegacyString(ByteBuf var0) {
      short var1 = var0.readShort();
      int var2 = var1 * 2;
      String var3 = var0.toString(var0.readerIndex(), var2, StandardCharsets.UTF_16BE);
      var0.skipBytes(var2);
      return var3;
   }
}
