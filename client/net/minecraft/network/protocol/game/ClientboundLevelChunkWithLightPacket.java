package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;
   private final ClientboundLevelChunkPacketData chunkData;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLevelChunkWithLightPacket(LevelChunk var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4, boolean var5) {
      super();
      ChunkPos var6 = var1.getPos();
      this.x = var6.x;
      this.z = var6.z;
      this.chunkData = new ClientboundLevelChunkPacketData(var1);
      this.lightData = new ClientboundLightUpdatePacketData(var6, var2, var3, var4, var5);
   }

   public ClientboundLevelChunkWithLightPacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readInt();
      this.z = var1.readInt();
      this.chunkData = new ClientboundLevelChunkPacketData(var1, this.x, this.z);
      this.lightData = new ClientboundLightUpdatePacketData(var1, this.x, this.z);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.x);
      var1.writeInt(this.z);
      this.chunkData.write(var1);
      this.lightData.write(var1);
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
