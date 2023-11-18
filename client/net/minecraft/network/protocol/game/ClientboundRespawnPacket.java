package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public record ClientboundRespawnPacket(CommonPlayerSpawnInfo d, byte e) implements Packet<ClientGamePacketListener> {
   private final CommonPlayerSpawnInfo commonPlayerSpawnInfo;
   private final byte dataToKeep;
   public static final byte KEEP_ATTRIBUTES = 1;
   public static final byte KEEP_ENTITY_DATA = 2;
   public static final byte KEEP_ALL_DATA = 3;

   public ClientboundRespawnPacket(FriendlyByteBuf var1) {
      this(new CommonPlayerSpawnInfo(var1), var1.readByte());
   }

   public ClientboundRespawnPacket(CommonPlayerSpawnInfo var1, byte var2) {
      super();
      this.commonPlayerSpawnInfo = var1;
      this.dataToKeep = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      this.commonPlayerSpawnInfo.write(var1);
      var1.writeByte(this.dataToKeep);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRespawn(this);
   }

   public boolean shouldKeep(byte var1) {
      return (this.dataToKeep & var1) != 0;
   }
}
