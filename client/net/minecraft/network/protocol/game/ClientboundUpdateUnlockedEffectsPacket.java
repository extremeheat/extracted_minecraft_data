package net.minecraft.network.protocol.game;

import java.util.List;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.mines.WorldEffect;

public record ClientboundUpdateUnlockedEffectsPacket(List<WorldEffect> unlockedEffects) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUpdateUnlockedEffectsPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundUpdateUnlockedEffectsPacket>codec(ClientboundUpdateUnlockedEffectsPacket::write, ClientboundUpdateUnlockedEffectsPacket::new);

   private ClientboundUpdateUnlockedEffectsPacket(RegistryFriendlyByteBuf var1) {
      this((List)WorldEffect.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(var1));
   }

   public ClientboundUpdateUnlockedEffectsPacket(List<WorldEffect> var1) {
      super();
      this.unlockedEffects = var1;
   }

   private void write(RegistryFriendlyByteBuf var1) {
      WorldEffect.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(var1, this.unlockedEffects);
   }

   public PacketType<ClientboundUpdateUnlockedEffectsPacket> type() {
      return GamePacketTypes.CLIENTBOUND_UPDATE_UNLOCKED_EFFECTS;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleUpdateUnlockedEffects(this);
   }
}
