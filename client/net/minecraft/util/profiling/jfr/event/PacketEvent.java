package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.Category;
import jdk.jfr.DataAmount;
import jdk.jfr.Enabled;
import jdk.jfr.Event;
import jdk.jfr.Label;
import jdk.jfr.Name;
import jdk.jfr.StackTrace;

@Category({"Minecraft", "Network"})
@StackTrace(false)
@Enabled(false)
public abstract class PacketEvent extends Event {
   @Name("protocolId")
   @Label("Protocol Id")
   public final String protocolId;
   @Name("packetDirection")
   @Label("Packet Direction")
   public final String packetDirection;
   @Name("packetId")
   @Label("Packet Id")
   public final String packetId;
   @Name("remoteAddress")
   @Label("Remote Address")
   public final String remoteAddress;
   @Name("bytes")
   @Label("Bytes")
   @DataAmount
   public final int bytes;

   public PacketEvent(String var1, String var2, String var3, SocketAddress var4, int var5) {
      super();
      this.protocolId = var1;
      this.packetDirection = var2;
      this.packetId = var3;
      this.remoteAddress = var4.toString();
      this.bytes = var5;
   }

   public static final class Fields {
      public static final String REMOTE_ADDRESS = "remoteAddress";
      public static final String PROTOCOL_ID = "protocolId";
      public static final String PACKET_DIRECTION = "packetDirection";
      public static final String PACKET_ID = "packetId";
      public static final String BYTES = "bytes";

      private Fields() {
         super();
      }
   }
}
