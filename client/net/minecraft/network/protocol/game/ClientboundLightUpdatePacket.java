package net.minecraft.network.protocol.game;

import java.util.BitSet;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class ClientboundLightUpdatePacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x int
   private final int field_362;
   // $FF: renamed from: z int
   private final int field_363;
   private final ClientboundLightUpdatePacketData lightData;

   public ClientboundLightUpdatePacket(ChunkPos var1, LevelLightEngine var2, @Nullable BitSet var3, @Nullable BitSet var4, boolean var5) {
      super();
      this.field_362 = var1.field_504;
      this.field_363 = var1.field_505;
      this.lightData = new ClientboundLightUpdatePacketData(var1, var2, var3, var4, var5);
   }

   public ClientboundLightUpdatePacket(FriendlyByteBuf var1) {
      super();
      this.field_362 = var1.readVarInt();
      this.field_363 = var1.readVarInt();
      this.lightData = new ClientboundLightUpdatePacketData(var1, this.field_362, this.field_363);
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.field_362);
      var1.writeVarInt(this.field_363);
      this.lightData.write(var1);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLightUpdatePacket(this);
   }

   public int getX() {
      return this.field_362;
   }

   public int getZ() {
      return this.field_363;
   }

   public ClientboundLightUpdatePacketData getLightData() {
      return this.lightData;
   }
}
