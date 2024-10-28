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

   public ClientboundLoginPacket(int playerId, boolean hardcore, Set<ResourceKey<Level>> levels, int maxPlayers, int chunkRadius, int simulationDistance, boolean reducedDebugInfo, boolean showDeathScreen, boolean doLimitedCrafting, CommonPlayerSpawnInfo commonPlayerSpawnInfo, boolean enforcesSecureChat) {
      super();
      this.playerId = playerId;
      this.hardcore = hardcore;
      this.levels = levels;
      this.maxPlayers = maxPlayers;
      this.chunkRadius = chunkRadius;
      this.simulationDistance = simulationDistance;
      this.reducedDebugInfo = reducedDebugInfo;
      this.showDeathScreen = showDeathScreen;
      this.doLimitedCrafting = doLimitedCrafting;
      this.commonPlayerSpawnInfo = commonPlayerSpawnInfo;
      this.enforcesSecureChat = enforcesSecureChat;
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
