package net.minecraft.util.profiling.jfr.event;

import java.net.SocketAddress;
import jdk.jfr.EventType;
import jdk.jfr.Label;
import jdk.jfr.Name;
import net.minecraft.obfuscate.DontObfuscate;

@Name("minecraft.PacketReceived")
@Label("Network Packet Received")
@DontObfuscate
public class PacketReceivedEvent extends PacketEvent {
   public static final String NAME = "minecraft.PacketReceived";
   public static final EventType TYPE = EventType.getEventType(PacketReceivedEvent.class);

   public PacketReceivedEvent(int var1, int var2, SocketAddress var3, int var4) {
      super(var1, var2, var3, var4);
   }
}
