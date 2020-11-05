package net.minecraft.network.protocol.game;

import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundLoginPacket implements Packet<ClientGamePacketListener> {
   private int playerId;
   private long seed;
   private boolean hardcore;
   private GameType gameType;
   private GameType previousGameType;
   private Set<ResourceKey<Level>> levels;
   private RegistryAccess.RegistryHolder registryHolder;
   private DimensionType dimensionType;
   private ResourceKey<Level> dimension;
   private int maxPlayers;
   private int chunkRadius;
   private boolean reducedDebugInfo;
   private boolean showDeathScreen;
   private boolean isDebug;
   private boolean isFlat;

   public ClientboundLoginPacket() {
      super();
   }

   public ClientboundLoginPacket(int var1, GameType var2, GameType var3, long var4, boolean var6, Set<ResourceKey<Level>> var7, RegistryAccess.RegistryHolder var8, DimensionType var9, ResourceKey<Level> var10, int var11, int var12, boolean var13, boolean var14, boolean var15, boolean var16) {
      super();
      this.playerId = var1;
      this.levels = var7;
      this.registryHolder = var8;
      this.dimensionType = var9;
      this.dimension = var10;
      this.seed = var4;
      this.gameType = var2;
      this.previousGameType = var3;
      this.maxPlayers = var11;
      this.hardcore = var6;
      this.chunkRadius = var12;
      this.reducedDebugInfo = var13;
      this.showDeathScreen = var14;
      this.isDebug = var15;
      this.isFlat = var16;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.playerId = var1.readInt();
      this.hardcore = var1.readBoolean();
      this.gameType = GameType.byId(var1.readByte());
      this.previousGameType = GameType.byId(var1.readByte());
      int var2 = var1.readVarInt();
      this.levels = Sets.newHashSet();

      for(int var3 = 0; var3 < var2; ++var3) {
         this.levels.add(ResourceKey.create(Registry.DIMENSION_REGISTRY, var1.readResourceLocation()));
      }

      this.registryHolder = (RegistryAccess.RegistryHolder)var1.readWithCodec(RegistryAccess.RegistryHolder.NETWORK_CODEC);
      this.dimensionType = (DimensionType)((Supplier)var1.readWithCodec(DimensionType.CODEC)).get();
      this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, var1.readResourceLocation());
      this.seed = var1.readLong();
      this.maxPlayers = var1.readVarInt();
      this.chunkRadius = var1.readVarInt();
      this.reducedDebugInfo = var1.readBoolean();
      this.showDeathScreen = var1.readBoolean();
      this.isDebug = var1.readBoolean();
      this.isFlat = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeInt(this.playerId);
      var1.writeBoolean(this.hardcore);
      var1.writeByte(this.gameType.getId());
      var1.writeByte(this.previousGameType.getId());
      var1.writeVarInt(this.levels.size());
      Iterator var2 = this.levels.iterator();

      while(var2.hasNext()) {
         ResourceKey var3 = (ResourceKey)var2.next();
         var1.writeResourceLocation(var3.location());
      }

      var1.writeWithCodec(RegistryAccess.RegistryHolder.NETWORK_CODEC, this.registryHolder);
      var1.writeWithCodec(DimensionType.CODEC, () -> {
         return this.dimensionType;
      });
      var1.writeResourceLocation(this.dimension.location());
      var1.writeLong(this.seed);
      var1.writeVarInt(this.maxPlayers);
      var1.writeVarInt(this.chunkRadius);
      var1.writeBoolean(this.reducedDebugInfo);
      var1.writeBoolean(this.showDeathScreen);
      var1.writeBoolean(this.isDebug);
      var1.writeBoolean(this.isFlat);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLogin(this);
   }

   public int getPlayerId() {
      return this.playerId;
   }

   public long getSeed() {
      return this.seed;
   }

   public boolean isHardcore() {
      return this.hardcore;
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public GameType getPreviousGameType() {
      return this.previousGameType;
   }

   public Set<ResourceKey<Level>> levels() {
      return this.levels;
   }

   public RegistryAccess registryAccess() {
      return this.registryHolder;
   }

   public DimensionType getDimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   public int getChunkRadius() {
      return this.chunkRadius;
   }

   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   public boolean shouldShowDeathScreen() {
      return this.showDeathScreen;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }
}
