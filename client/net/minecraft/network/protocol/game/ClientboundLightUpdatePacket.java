package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket implements Packet<ClientGamePacketListener> {
   private final int x;
   private final int z;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLightUpdatePacket(ChunkPos var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4) {
      super();
      this.x = var1.x;
      this.z = var1.z;
      this.lightData = new ClientboundLightUpdatePacketData(var1, var2, var3, var4);
   }

   public ClientboundLightUpdatePacket(FriendlyByteBuf var1) {
      super();
      this.x = var1.readVarInt();
      this.z = var1.readVarInt();
      this.lightData = new ClientboundLightUpdatePacketData(var1, this.x, this.z);
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.x);
      var1.writeVarInt(this.z);
      this.lightData.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLightUpdatePacket(this);
   }

   public int getX() {
      return this.x;
   }

   public int getZ() {
      return this.z;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}
