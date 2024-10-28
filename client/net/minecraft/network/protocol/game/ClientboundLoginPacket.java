package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ClientboundLoginPacket(int playerId, boolean hardcore, Set<ResourceKey<Level>> levels, int maxPlayers, int chunkRadius, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean doLimitedCrafting, CommonPlayerSpawnInfo commonPlayerSpawnInfo, boolean enforcesSecureChat) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLoginPacket> STREAM_CODEC = Packet.codec(ClientboundLoginPacket::write, ClientboundLoginPacket::new);

   private ClientboundLoginPacket(RegistryFriendlyByteBuf var1) {
      this(var1.readInt(), var1.readBoolean(), (Set)var1.readCollection(Sets::newHashSetWithExpectedSize, (var0) -> {
         return var0.readResourceKey(Registries.DIMENSION);
      }), var1.readVarInt(), var1.readVarInt(), var1.readVarInt(), var1.readBoolean(), var1.readBoolean(), var1.readBoolean(), new CommonPlayerSpawnInfo(var1), var1.readBoolean());
   }

   public ClientboundLoginPacket(int var1, boolean var2, Set<ResourceKey<Level>> var3, int var4, int var5, int var6, boolean var7, boolean var8, boolean var9, CommonPlayerSpawnInfo var10, boolean var11) {
      super();
      this.playerId = var1;
      this.hardcore = var2;
      this.levels = var3;
      this.maxPlayers = var4;
      this.chunkRadius = var5;
      this.simulationDistance = var6;
      this.reducedDebugInfo = var7;
      this.showDeathScreen = var8;
      this.doLimitedCrafting = var9;
      this.commonPlayerSpawnInfo = var10;
      this.enforcesSecureChat = var11;
   }

   private void write(RegistryFriendlyByteBuf var1) {
      var1.writeInt(this.playerId);
      var1.writeBoolean(this.hardcore);
      var1.writeCollection(this.levels, FriendlyByteBuf::writeResourceKey);
      var1.writeVarInt(this.maxPlayers);
      var1.writeVarInt(this.chunkRadius);
      var1.writeVarInt(this.simulationDistance);
      var1.writeBoolean(this.reducedDebugInfo);
      var1.writeBoolean(this.showDeathScreen);
      var1.writeBoolean(this.doLimitedCrafting);
      this.commonPlayerSpawnInfo.write(var1);
      var1.writeBoolean(this.enforcesSecureChat);
   }

   public PacketType<ClientboundLoginPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LOGIN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLogin(this);
   }

   public int playerId() {
      return this.playerId;
   }

   public boolean hardcore() {
      return this.hardcore;
   }

   public Set<ResourceKey<Level>> levels() {
      return this.levels;
   }

   public int maxPlayers() {
      return this.maxPlayers;
   }

   public int chunkRadius() {
      return this.chunkRadius;
   }

   public int simulationDistance() {
      return this.simulationDistance;
   }

   public boolean reducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public boolean showDeathScreen() {
      return this.showDeathScreen;
   }

   public boolean doLimitedCrafting() {
      return this.doLimitedCrafting;
   }

   public CommonPlayerSpawnInfo commonPlayerSpawnInfo() {
      return this.commonPlayerSpawnInfo;
   }

   public boolean enforcesSecureChat() {
      return this.enforcesSecureChat;
   }
}
