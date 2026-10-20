package net.minecraft.network.protocol.login;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.Identifier;

public class LoginPacketTypes {
   public static final PacketType<ClientboundCustomQueryPacket> CLIENTBOUND_CUSTOM_QUERY = createClientbound("custom_query");
   public static final PacketType<ClientboundLoginFinishedPacket> CLIENTBOUND_LOGIN_FINISHED = createClientbound("login_finished");
   public static final PacketType<ClientboundKeyPacket> CLIENTBOUND_KEY = createClientbound("key");
   public static final PacketType<ClientboundSetupCompressionPacket> CLIENTBOUND_SETUP_COMPRESSION = createClientbound("setup_compression");
   public static final PacketType<ClientboundApplyOptionsPacket> CLIENTBOUND_APPLY_OPTIONS = createClientbound("apply_options");
   public static final PacketType<ClientboundLoginDisconnectPacket> CLIENTBOUND_LOGIN_DISCONNECT = createClientbound("login_disconnect");
   public static final PacketType<ServerboundCustomQueryAnswerPacket> SERVERBOUND_CUSTOM_QUERY_ANSWER = createServerbound("custom_query_answer");
   public static final PacketType<ServerboundHelloPacket> SERVERBOUND_HELLO = createServerbound("hello");
   public static final PacketType<ServerboundKeyPacket> SERVERBOUND_KEY = createServerbound("key");
   public static final PacketType<ServerboundOptionsAppliedPacket> SERVERBOUND_OPTIONS_APPLIED = createServerbound("options_applied");
   public static final PacketType<ServerboundLoginAcknowledgedPacket> SERVERBOUND_LOGIN_ACKNOWLEDGED = createServerbound("login_acknowledged");

   public LoginPacketTypes() {
      super();
   }

   private static <T extends Packet<ClientLoginPacketListener>> PacketType<T> createClientbound(final String id) {
      return new PacketType<T>(PacketFlow.CLIENTBOUND, Identifier.withDefaultNamespace(id));
   }

   private static <T extends Packet<ServerLoginPacketListener>> PacketType<T> createServerbound(final String id) {
      return new PacketType<T>(PacketFlow.SERVERBOUND, Identifier.withDefaultNamespace(id));
   }
}
