package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public record ClientboundLoginPacket(
   int a,
   boolean b,
   GameType c,
   @Nullable GameType d,
   Set<ResourceKey<Level>> e,
   RegistryAccess.Frozen f,
   ResourceKey<DimensionType> g,
   ResourceKey<Level> h,
   long i,
   int j,
   int k,
   int l,
   boolean m,
   boolean n,
   boolean o,
   boolean p,
   Optional<GlobalPos> q
) implements Packet<ClientGamePacketListener> {
   private final int playerId;
   private final boolean hardcore;
   private final GameType gameType;
   @Nullable
   private final GameType previousGameType;
   private final Set<ResourceKey<Level>> levels;
   private final RegistryAccess.Frozen registryHolder;
   private final ResourceKey<DimensionType> dimensionType;
   private final ResourceKey<Level> dimension;
   private final long seed;
   private final int maxPlayers;
   private final int chunkRadius;
   private final int simulationDistance;
   private final boolean reducedDebugInfo;
   private final boolean showDeathScreen;
   private final boolean isDebug;
   private final boolean isFlat;
   private final Optional<GlobalPos> lastDeathLocation;

   public ClientboundLoginPacket(FriendlyByteBuf var1) {
      this(
         var1.readInt(),
         var1.readBoolean(),
         GameType.byId(var1.readByte()),
         GameType.byNullableId(var1.readByte()),
         var1.readCollection(Sets::newHashSetWithExpectedSize, var0 -> var0.readResourceKey(Registry.DIMENSION_REGISTRY)),
         var1.<RegistryAccess>readWithCodec(RegistryAccess.NETWORK_CODEC).freeze(),
         var1.readResourceKey(Registry.DIMENSION_TYPE_REGISTRY),
         var1.readResourceKey(Registry.DIMENSION_REGISTRY),
         var1.readLong(),
         var1.readVarInt(),
         var1.readVarInt(),
         var1.readVarInt(),
         var1.readBoolean(),
         var1.readBoolean(),
         var1.readBoolean(),
         var1.readBoolean(),
         var1.readOptional(FriendlyByteBuf::readGlobalPos)
      );
   }

   public ClientboundLoginPacket(
      int var1,
      boolean var2,
      GameType var3,
      @Nullable GameType var4,
      Set<ResourceKey<Level>> var5,
      RegistryAccess.Frozen var6,
      ResourceKey<DimensionType> var7,
      ResourceKey<Level> var8,
      long var9,
      int var11,
      int var12,
      int var13,
      boolean var14,
      boolean var15,
      boolean var16,
      boolean var17,
      Optional<GlobalPos> var18
   ) {
      super();
      this.playerId = var1;
      this.hardcore = var2;
      this.gameType = var3;
      this.previousGameType = var4;
      this.levels = var5;
      this.registryHolder = var6;
      this.dimensionType = var7;
      this.dimension = var8;
      this.seed = var9;
      this.maxPlayers = var11;
      this.chunkRadius = var12;
      this.simulationDistance = var13;
      this.reducedDebugInfo = var14;
      this.showDeathScreen = var15;
      this.isDebug = var16;
      this.isFlat = var17;
      this.lastDeathLocation = var18;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeInt(this.playerId);
      var1.writeBoolean(this.hardcore);
      var1.writeByte(this.gameType.getId());
      var1.writeByte(GameType.getNullableId(this.previousGameType));
      var1.writeCollection(this.levels, FriendlyByteBuf::writeResourceKey);
      var1.writeWithCodec(RegistryAccess.NETWORK_CODEC, this.registryHolder);
      var1.writeResourceKey(this.dimensionType);
      var1.writeResourceKey(this.dimension);
      var1.writeLong(this.seed);
      var1.writeVarInt(this.maxPlayers);
      var1.writeVarInt(this.chunkRadius);
      var1.writeVarInt(this.simulationDistance);
      var1.writeBoolean(this.reducedDebugInfo);
      var1.writeBoolean(this.showDeathScreen);
      var1.writeBoolean(this.isDebug);
      var1.writeBoolean(this.isFlat);
      var1.writeOptional(this.lastDeathLocation, FriendlyByteBuf::writeGlobalPos);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLogin(this);
   }
}
