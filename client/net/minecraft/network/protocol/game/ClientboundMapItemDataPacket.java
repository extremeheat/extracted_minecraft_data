package net.minecraft.network.protocol.game;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

public record ClientboundMapItemDataPacket(MapId mapId, byte scale, boolean locked, Optional<List<MapDecoration>> decorations, Optional<MapItemSavedData.MapPatch> colorPatch) implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMapItemDataPacket> STREAM_CODEC;

   public ClientboundMapItemDataPacket(MapId var1, byte var2, boolean var3, @Nullable Collection<MapDecoration> var4, @Nullable MapItemSavedData.MapPatch var5) {
      this(var1, var2, var3, var4 != null ? Optional.of(List.copyOf(var4)) : Optional.empty(), Optional.ofNullable(var5));
   }

   public ClientboundMapItemDataPacket(MapId var1, byte var2, boolean var3, Optional<List<MapDecoration>> var4, Optional<MapItemSavedData.MapPatch> var5) {
      super();
      this.mapId = var1;
      this.scale = var2;
      this.locked = var3;
      this.decorations = var4;
      this.colorPatch = var5;
   }

   public PacketType<ClientboundMapItemDataPacket> type() {
      return GamePacketTypes.CLIENTBOUND_MAP_ITEM_DATA;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleMapItemData(this);
   }

   public void applyToMap(MapItemSavedData var1) {
      Optional var10000 = this.decorations;
      Objects.requireNonNull(var1);
      var10000.ifPresent(var1::addClientSideDecorations);
      this.colorPatch.ifPresent((var1x) -> {
         var1x.applyToMap(var1);
      });
   }

   public MapId mapId() {
      return this.mapId;
   }

   public byte scale() {
      return this.scale;
   }

   public boolean locked() {
      return this.locked;
   }

   public Optional<List<MapDecoration>> decorations() {
      return this.decorations;
   }

   public Optional<MapItemSavedData.MapPatch> colorPatch() {
      return this.colorPatch;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(MapId.STREAM_CODEC, ClientboundMapItemDataPacket::mapId, ByteBufCodecs.BYTE, ClientboundMapItemDataPacket::scale, ByteBufCodecs.BOOL, ClientboundMapItemDataPacket::locked, MapDecoration.STREAM_CODEC.apply(ByteBufCodecs.list()).apply(ByteBufCodecs::optional), ClientboundMapItemDataPacket::decorations, MapItemSavedData.MapPatch.STREAM_CODEC, ClientboundMapItemDataPacket::colorPatch, ClientboundMapItemDataPacket::new);
   }
}
