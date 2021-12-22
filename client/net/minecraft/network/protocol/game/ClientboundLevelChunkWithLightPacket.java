package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLevelChunkWithLightPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x int
   private final int field_344;
   // $FF: renamed from: z int
   private final int field_345;
   private final ClientboundLevelChunkPacketData chunkData;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLevelChunkWithLightPacket(LevelChunk var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4, boolean var5) {
      super();
      ChunkPos var6 = var1.getPos();
      this.field_344 = var6.field_504;
      this.field_345 = var6.field_505;
      this.chunkData = new ClientboundLevelChunkPacketData(var1);
      this.lightData = new ClientboundLightUpdatePacketData(var6, var2, var3, var4, var5);
   }

   public ClientboundLevelChunkWithLightPacket(FriendlyByteBuf var1) {
      super();
      this.field_344 = var1.readInt();
      this.field_345 = var1.readInt();
      this.chunkData = new ClientboundLevelChunkPacketData(var1, this.field_344, this.field_345);
      this.lightData = new ClientboundLightUpdatePacketData(var1, this.field_344, this.field_345);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.field_344);
      var1.writeInt(this.field_345);
      this.chunkData.write(var1);
      this.lightData.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLevelChunkWithLight(this);
   }

   public int getX() {
      return this.field_344;
   }

   public int getZ() {
      return this.field_345;
   }

   public ClientboundLevelChunkPacketData getChunkData() {
      return this.chunkData;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}
