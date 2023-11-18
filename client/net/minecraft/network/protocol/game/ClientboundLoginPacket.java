package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public record ClientboundLoginPacket(int a, boolean b, Set<ResourceKey<Level>> c, int d, int e, int f, boolean g, boolean h, boolean i, CommonPlayerSpawnInfo j)
   implements Packet<ClientGamePacketListener> {
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

   public ClientboundLoginPacket(FriendlyByteBuf var1) {
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
         new CommonPlayerSpawnInfo(var1)
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
      CommonPlayerSpawnInfo var10
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
   }

   @Override
   public void write(FriendlyByteBuf var1) {
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
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLogin(this);
   }
}
