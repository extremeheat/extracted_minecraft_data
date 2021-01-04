package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket implements Packet<ClientGamePacketListener> {
   private DimensionType dimension;
   private GameType playerGameType;
   private LevelType levelType;

   public ClientboundRespawnPacket() {
      super();
   }

   public ClientboundRespawnPacket(DimensionType var1, LevelType var2, GameType var3) {
      super();
      this.dimension = var1;
      this.playerGameType = var3;
      this.levelType = var2;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRespawn(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.dimension = DimensionType.getById(var1.readInt());
      this.playerGameType = GameType.byId(var1.readUnsignedByte());
      this.levelType = LevelType.getLevelType(var1.readUtf(16));
      if (this.levelType == null) {
         this.levelType = LevelType.NORMAL;
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.dimension.getId());
      var1.writeByte(this.playerGameType.getId());
      var1.writeUtf(this.levelType.getName());
   }

   public DimensionType getDimension() {
      return this.dimension;
   }

   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }
}
