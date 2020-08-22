package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket implements Packet {
   private DimensionType dimension;
   private long seed;
   private GameType playerGameType;
   private LevelType levelType;

   public ClientboundRespawnPacket() {
   }

   public ClientboundRespawnPacket(DimensionType var1, long var2, LevelType var4, GameType var5) {
      this.dimension = var1;
      this.seed = var2;
      this.playerGameType = var5;
      this.levelType = var4;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRespawn(this);
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.dimension = DimensionType.getById(var1.readInt());
      this.seed = var1.readLong();
      this.playerGameType = GameType.byId(var1.readUnsignedByte());
      this.levelType = LevelType.getLevelType(var1.readUtf(16));
      if (this.levelType == null) {
         this.levelType = LevelType.NORMAL;
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.dimension.getId());
      var1.writeLong(this.seed);
      var1.writeByte(this.playerGameType.getId());
      var1.writeUtf(this.levelType.getName());
   }

   public DimensionType getDimension() {
      return this.dimension;
   }

   public long getSeed() {
      return this.seed;
   }

   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }
}
