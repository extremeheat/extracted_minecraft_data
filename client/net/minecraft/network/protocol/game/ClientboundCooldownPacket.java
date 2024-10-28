package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCooldownPacket(ResourceLocation cooldownGroup, int duration) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCooldownPacket> STREAM_CODEC;

   public ClientboundCooldownPacket(ResourceLocation var1, int var2) {
      super();
      this.cooldownGroup = var1;
      this.duration = var2;
   }

   public PacketType<ClientboundCooldownPacket> type() {
      return GamePacketTypes.CLIENTBOUND_COOLDOWN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleItemCooldown(this);
   }

   public ResourceLocation cooldownGroup() {
      return this.cooldownGroup;
   }

   public int duration() {
      return this.duration;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, ClientboundCooldownPacket::cooldownGroup, ByteBufCodecs.VAR_INT, ClientboundCooldownPacket::duration, ClientboundCooldownPacket::new);
   }
}
