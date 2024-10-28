package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLevelChunkWithLightPacket> STREAM_CODEC = Packet.codec(ClientboundLevelChunkWithLightPacket::write, ClientboundLevelChunkWithLightPacket::new);
   private final int x;
   private final int z;
   private final ClientboundLevelChunkPacketData chunkData;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLevelChunkWithLightPacket(LevelChunk var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4) {
      super();
      ChunkPos var5 = var1.getPos();
      this.x = var5.x;
      this.z = var5.z;
      this.chunkData = new ClientboundLevelChunkPacketData(var1);
      this.lightData = new ClientboundLightUpdatePacketData(var5, var2, var3, var4);
   }

   private ClientboundLevelChunkWithLightPacket(RegistryFriendlyByteBuf var1) {
      super();
      this.x = var1.readInt();
      this.z = var1.readInt();
      this.chunkData = new ClientboundLevelChunkPacketData(var1, this.x, this.z);
      this.lightData = new ClientboundLightUpdatePacketData(var1, this.x, this.z);
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeInt(this.x);
      var1.writeInt(this.z);
      this.chunkData.write(var1);
      this.lightData.write(var1);
   }

   public PacketType<ClientboundLevelChunkWithLightPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LEVEL_CHUNK_WITH_LIGHT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLevelChunkWithLight(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public ClientboundLevelChunkPacketData getChunkData() {
      return this.chunkData;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}
