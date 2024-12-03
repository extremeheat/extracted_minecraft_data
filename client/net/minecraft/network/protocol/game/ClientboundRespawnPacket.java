package net.minecraft.network.protocol.game;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public record ClientboundRespawnPacket(CommonPlayerSpawnInfo commonPlayerSpawnInfo, byte dataToKeep) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRespawnPacket> STREAM_CODEC = Packet.<RegistryFriendlyByteBuf, ClientboundRespawnPacket>codec(ClientboundRespawnPacket::write, ClientboundRespawnPacket::new);
   public static final byte KEEP_ATTRIBUTE_MODIFIERS = 1;
   public static final byte KEEP_ENTITY_DATA = 2;
   public static final byte KEEP_ALL_DATA = 3;

   private ClientboundRespawnPacket(RegistryFriendlyByteBuf var1) {
      this(new CommonPlayerSpawnInfo(var1), var1.readByte());
   }

   public ClientboundRespawnPacket(CommonPlayerSpawnInfo var1, byte var2) {
      super();
      this.commonPlayerSpawnInfo = var1;
      this.dataToKeep = var2;
   }

   private void write(RegistryFriendlyByteBuf var1) {
      this.commonPlayerSpawnInfo.write(var1);
      var1.writeByte(this.dataToKeep);
   }

   public PacketType<ClientboundRespawnPacket> type() {
      return GamePacketTypes.CLIENTBOUND_RESPAWN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRespawn(this);
   }

   public boolean shouldKeep(byte var1) {
      return (this.dataToKeep & var1) != 0;
   }
}
