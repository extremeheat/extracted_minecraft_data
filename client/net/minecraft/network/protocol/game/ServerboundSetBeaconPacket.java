package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.effect.MobEffect;

public record ServerboundSetBeaconPacket(Optional<Holder<MobEffect>> b, Optional<Holder<MobEffect>> c) implements Packet<ServerGamePacketListener> {
   private final Optional<Holder<MobEffect>> primary;
   private final Optional<Holder<MobEffect>> secondary;
   public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundSetBeaconPacket> STREAM_CODEC = StreamCodec.composite(
      ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT).apply(ByteBufCodecs::optional),
      ServerboundSetBeaconPacket::primary,
      ByteBufCodecs.holderRegistry(Registries.MOB_EFFECT).apply(ByteBufCodecs::optional),
      ServerboundSetBeaconPacket::secondary,
      ServerboundSetBeaconPacket::new
   );

   public ServerboundSetBeaconPacket(Optional<Holder<MobEffect>> var1, Optional<Holder<MobEffect>> var2) {
      super();
      this.primary = var1;
      this.secondary = var2;
   }

   @Override
   public PacketType<ServerboundSetBeaconPacket> type() {
      return GamePacketTypes.SERVERBOUND_SET_BEACON;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetBeaconPacket(this);
   }
}
