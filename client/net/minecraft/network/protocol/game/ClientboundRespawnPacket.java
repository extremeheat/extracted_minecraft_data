package net.minecraft.network.protocol.game;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;

public class ClientboundRespawnPacket implements Packet<ClientGamePacketListener> {
   private final DimensionType dimensionType;
   private final ResourceKey<Level> dimension;
   private final long seed;
   private final GameType playerGameType;
   @Nullable
   private final GameType previousPlayerGameType;
   private final boolean isDebug;
   private final boolean isFlat;
   private final boolean keepAllPlayerData;

   public ClientboundRespawnPacket(DimensionType var1, ResourceKey<Level> var2, long var3, GameType var5, @Nullable GameType var6, boolean var7, boolean var8, boolean var9) {
      super();
      this.dimensionType = var1;
      this.dimension = var2;
      this.seed = var3;
      this.playerGameType = var5;
      this.previousPlayerGameType = var6;
      this.isDebug = var7;
      this.isFlat = var8;
      this.keepAllPlayerData = var9;
   }

   public ClientboundRespawnPacket(FriendlyByteBuf var1) {
      super();
      this.dimensionType = (DimensionType)((Supplier)var1.readWithCodec(DimensionType.CODEC)).get();
      this.dimension = ResourceKey.create(Registry.DIMENSION_REGISTRY, var1.readResourceLocation());
      this.seed = var1.readLong();
      this.playerGameType = GameType.byId(var1.readUnsignedByte());
      this.previousPlayerGameType = GameType.byNullableId(var1.readByte());
      this.isDebug = var1.readBoolean();
      this.isFlat = var1.readBoolean();
      this.keepAllPlayerData = var1.readBoolean();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeWithCodec(DimensionType.CODEC, () -> {
         return this.dimensionType;
      });
      var1.writeResourceLocation(this.dimension.location());
      var1.writeLong(this.seed);
      var1.writeByte(this.playerGameType.getId());
      var1.writeByte(GameType.getNullableId(this.previousPlayerGameType));
      var1.writeBoolean(this.isDebug);
      var1.writeBoolean(this.isFlat);
      var1.writeBoolean(this.keepAllPlayerData);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleRespawn(this);
   }

   public DimensionType getDimensionType() {
      return this.dimensionType;
   }

   public ResourceKey<Level> getDimension() {
      return this.dimension;
   }

   public long getSeed() {
      return this.seed;
   }

   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   @Nullable
   public GameType getPreviousPlayerGameType() {
      return this.previousPlayerGameType;
   }

   public boolean isDebug() {
      return this.isDebug;
   }

   public boolean isFlat() {
      return this.isFlat;
   }

   public boolean shouldKeepAllPlayerData() {
      return this.keepAllPlayerData;
   }
}
