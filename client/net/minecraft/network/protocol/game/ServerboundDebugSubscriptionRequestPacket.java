package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.debug.DebugSubscription;

public record ServerboundDebugSubscriptionRequestPacket(Set<DebugSubscription<?>> subscriptions) implements Packet<ServerGamePacketListener> {
   private static final StreamCodec<RegistryFriendlyByteBuf, Set<DebugSubscription<?>>> SET_STREAM_CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundDebugSubscriptionRequestPacket> STREAM_CODEC;

   public ServerboundDebugSubscriptionRequestPacket(Set<DebugSubscription<?>> var1) {
      super();
      this.subscriptions = var1;
   }

   public PacketType<ServerboundDebugSubscriptionRequestPacket> type() {
      return GamePacketTypes.SERVERBOUND_DEBUG_SUBSCRIPTION_REQUEST;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleDebugSubscriptionRequest(this);
   }

   static {
      SET_STREAM_CODEC = ByteBufCodecs.registry(Registries.DEBUG_SUBSCRIPTION).apply(ByteBufCodecs.collection(ReferenceOpenHashSet::new));
      STREAM_CODEC = SET_STREAM_CODEC.map(ServerboundDebugSubscriptionRequestPacket::new, ServerboundDebugSubscriptionRequestPacket::subscriptions);
   }
}
