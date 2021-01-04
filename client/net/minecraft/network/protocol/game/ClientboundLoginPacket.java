package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelType;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundLoginPacket implements Packet<ClientGamePacketListener> {
   private int playerId;
   private boolean hardcore;
   private GameType gameType;
   private DimensionType dimension;
   private int maxPlayers;
   private LevelType levelType;
   private int chunkRadius;
   private boolean reducedDebugInfo;

   public ClientboundLoginPacket() {
      super();
   }

   public ClientboundLoginPacket(int var1, GameType var2, boolean var3, DimensionType var4, int var5, LevelType var6, int var7, boolean var8) {
      super();
      this.playerId = var1;
      this.dimension = var4;
      this.gameType = var2;
      this.maxPlayers = var5;
      this.hardcore = var3;
      this.levelType = var6;
      this.chunkRadius = var7;
      this.reducedDebugInfo = var8;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.playerId = var1.readInt();
      short var2 = var1.readUnsignedByte();
      this.hardcore = (var2 & 8) == 8;
      int var3 = var2 & -9;
      this.gameType = GameType.byId(var3);
      this.dimension = DimensionType.getById(var1.readInt());
      this.maxPlayers = var1.readUnsignedByte();
      this.levelType = LevelType.getLevelType(var1.readUtf(16));
      if (this.levelType == null) {
         this.levelType = LevelType.NORMAL;
      }

      this.chunkRadius = var1.readVarInt();
      this.reducedDebugInfo = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.playerId);
      int var2 = this.gameType.getId();
      if (this.hardcore) {
         var2 |= 8;
      }

      var1.writeByte(var2);
      var1.writeInt(this.dimension.getId());
      var1.writeByte(this.maxPlayers);
      var1.writeUtf(this.levelType.getName());
      var1.writeVarInt(this.chunkRadius);
      var1.writeBoolean(this.reducedDebugInfo);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLogin(this);
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public DimensionType getDimension() {
      return this.dimension;
   }

   public LevelType getLevelType() {
      return this.levelType;
   }

   public int getChunkRadius() {
      return this.chunkRadius;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }
}
