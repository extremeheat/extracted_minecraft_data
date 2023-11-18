package net.minecraft.network.protocol.common;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.BeeDebugPayload;
import net.minecraft.network.protocol.common.custom.BrainDebugPayload;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.common.custom.DiscardedPayload;
import net.minecraft.network.protocol.common.custom.GameEventDebugPayload;
import net.minecraft.network.protocol.common.custom.GameEventListenerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestAddMarkerDebugPayload;
import net.minecraft.network.protocol.common.custom.GameTestClearMarkersDebugPayload;
import net.minecraft.network.protocol.common.custom.GoalDebugPayload;
import net.minecraft.network.protocol.common.custom.HiveDebugPayload;
import net.minecraft.network.protocol.common.custom.NeighborUpdatesDebugPayload;
import net.minecraft.network.protocol.common.custom.PathfindingDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiAddedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiRemovedDebugPayload;
import net.minecraft.network.protocol.common.custom.PoiTicketCountDebugPayload;
import net.minecraft.network.protocol.common.custom.RaidsDebugPayload;
import net.minecraft.network.protocol.common.custom.StructuresDebugPayload;
import net.minecraft.network.protocol.common.custom.VillageSectionsDebugPayload;
import net.minecraft.network.protocol.common.custom.WorldGenAttemptDebugPayload;
import net.minecraft.resources.ResourceLocation;

public record ClientboundCustomPayloadPacket(CustomPacketPayload a) implements Packet<ClientCommonPacketListener> {
   private final CustomPacketPayload payload;
   private static final int MAX_PAYLOAD_SIZE = 1048576;
   private static final Map<ResourceLocation, FriendlyByteBuf.Reader<? extends CustomPacketPayload>> KNOWN_TYPES = ImmutableMap.builder()
      .put(BrandPayload.ID, BrandPayload::new)
      .put(BeeDebugPayload.ID, BeeDebugPayload::new)
      .put(BrainDebugPayload.ID, BrainDebugPayload::new)
      .put(GameEventDebugPayload.ID, GameEventDebugPayload::new)
      .put(GameEventListenerDebugPayload.ID, GameEventListenerDebugPayload::new)
      .put(GameTestAddMarkerDebugPayload.ID, GameTestAddMarkerDebugPayload::new)
      .put(GameTestClearMarkersDebugPayload.ID, GameTestClearMarkersDebugPayload::new)
      .put(GoalDebugPayload.ID, GoalDebugPayload::new)
      .put(HiveDebugPayload.ID, HiveDebugPayload::new)
      .put(NeighborUpdatesDebugPayload.ID, NeighborUpdatesDebugPayload::new)
      .put(PathfindingDebugPayload.ID, PathfindingDebugPayload::new)
      .put(PoiAddedDebugPayload.ID, PoiAddedDebugPayload::new)
      .put(PoiRemovedDebugPayload.ID, PoiRemovedDebugPayload::new)
      .put(PoiTicketCountDebugPayload.ID, PoiTicketCountDebugPayload::new)
      .put(RaidsDebugPayload.ID, RaidsDebugPayload::new)
      .put(StructuresDebugPayload.ID, StructuresDebugPayload::new)
      .put(VillageSectionsDebugPayload.ID, VillageSectionsDebugPayload::new)
      .put(WorldGenAttemptDebugPayload.ID, WorldGenAttemptDebugPayload::new)
      .build();

   public ClientboundCustomPayloadPacket(FriendlyByteBuf var1) {
      this(readPayload(var1.readResourceLocation(), var1));
   }

   public ClientboundCustomPayloadPacket(CustomPacketPayload var1) {
      super();
      this.payload = var1;
   }

   private static CustomPacketPayload readPayload(ResourceLocation var0, FriendlyByteBuf var1) {
      FriendlyByteBuf.Reader var2 = KNOWN_TYPES.get(var0);
      return (CustomPacketPayload)(var2 != null ? (CustomPacketPayload)var2.apply(var1) : readUnknownPayload(var0, var1));
   }

   private static DiscardedPayload readUnknownPayload(ResourceLocation var0, FriendlyByteBuf var1) {
      int var2 = var1.readableBytes();
      if (var2 >= 0 && var2 <= 1048576) {
         var1.skipBytes(var2);
         return new DiscardedPayload(var0);
      } else {
         throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
      }
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeResourceLocation(this.payload.id());
      this.payload.write(var1);
   }

   public void handle(ClientCommonPacketListener var1) {
      var1.handleCustomPayload(this);
   }
}
