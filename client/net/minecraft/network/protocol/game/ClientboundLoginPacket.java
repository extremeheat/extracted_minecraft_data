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

public record ClientboundLoginPacket(
   int b, boolean c, Set<ResourceKey<Level>> d, int e, int f, int g, boolean h, boolean i, boolean j, CommonPlayerSpawnInfo k, boolean l
) implements Packet<ClientGamePacketListener> {
   private final int playerId;
   private final boolean hardcore;
   private final Set<ResourceKey<Level>> levels;
   private final int maxPlayers;
   private final int chunkRadius;
   private final int simulationDistance;
   private final boolean reducedDebugInfo;
   private final boolean showDeathScreen;
   private final boolean doLimitedCrafting;
   private final CommonPlayerSpawnInfo commonPlayerSpawnInfo;
   private final boolean enforcesSecureChat;
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundLoginPacket> STREAM_CODEC = Packet.codec(
      ClientboundLoginPacket::write, ClientboundLoginPacket::new
   );

   private ClientboundLoginPacket(RegistryFriendlyByteBuf var1) {
      this(
         var1.readInt(),
         var1.readBoolean(),
         var1.readCollection(Sets::newHashSetWithExpectedSize, var0 -> var0.readResourceKey(Registries.DIMENSION)),
         var1.readVarInt(),
         var1.readVarInt(),
         var1.readVarInt(),
         var1.readBoolean(),
         var1.readBoolean(),
         var1.readBoolean(),
         new CommonPlayerSpawnInfo(var1),
         var1.readBoolean()
      );
   }

   public ClientboundLoginPacket(
      int var1,
      boolean var2,
      Set<ResourceKey<Level>> var3,
      int var4,
      int var5,
      int var6,
      boolean var7,
      boolean var8,
      boolean var9,
      CommonPlayerSpawnInfo var10,
      boolean var11
   ) {
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

   @Override
   public PacketType<ClientboundLoginPacket> type() {
      return GamePacketTypes.CLIENTBOUND_LOGIN;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLogin(this);
   }
}
