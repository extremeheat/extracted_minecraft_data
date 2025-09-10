package net.minecraft.util.debug;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundDebugBlockValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugChunkValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEntityValuePacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;

public abstract class TrackingDebugSynchronizer<T> {
   protected final DebugSubscription<T> subscription;
   private final Set<UUID> subscribedPlayers = new ObjectOpenHashSet();

   public TrackingDebugSynchronizer(DebugSubscription<T> var1) {
      super();
      this.subscription = var1;
   }

   public final void tick(ServerLevel var1) {
      for(ServerPlayer var3 : var1.players()) {
         boolean var4 = this.subscribedPlayers.contains(var3.getUUID());
         boolean var5 = var3.debugSubscriptions().contains(this.subscription);
         if (var5 != var4) {
            if (var5) {
               this.addSubscriber(var3);
            } else {
               this.subscribedPlayers.remove(var3.getUUID());
            }
         }
      }

      this.subscribedPlayers.removeIf((var1x) -> var1.getPlayerByUUID(var1x) == null);
      if (!this.subscribedPlayers.isEmpty()) {
         this.pollAndSendUpdates(var1);
      }

   }

   private void addSubscriber(ServerPlayer var1) {
      this.subscribedPlayers.add(var1.getUUID());
      var1.getChunkTrackingView().forEach((var2) -> {
         if (!var1.connection.chunkSender.isPending(var2.toLong())) {
            this.startTrackingChunk(var1, var2);
         }

      });
      var1.level().getChunkSource().chunkMap.forEachEntityTrackedBy(var1, (var2) -> this.startTrackingEntity(var1, var2));
   }

   protected final void sendToPlayersTrackingChunk(ServerLevel var1, ChunkPos var2, Packet<? super ClientGamePacketListener> var3) {
      ChunkMap var4 = var1.getChunkSource().chunkMap;

      for(UUID var6 : this.subscribedPlayers) {
         Player var8 = var1.getPlayerByUUID(var6);
         if (var8 instanceof ServerPlayer var7) {
            if (var4.isChunkTracked(var7, var2.x, var2.z)) {
               var7.connection.send(var3);
            }
         }
      }

   }

   protected final void sendToPlayersTrackingEntity(ServerLevel var1, Entity var2, Packet<? super ClientGamePacketListener> var3) {
      ChunkMap var4 = var1.getChunkSource().chunkMap;
      var4.sendToTrackingPlayersFiltered(var2, var3, (var1x) -> this.subscribedPlayers.contains(var1x.getUUID()));
   }

   public final void startTrackingChunk(ServerPlayer var1, ChunkPos var2) {
      if (this.subscribedPlayers.contains(var1.getUUID())) {
         this.sendInitialChunk(var1, var2);
      }

   }

   public final void startTrackingEntity(ServerPlayer var1, Entity var2) {
      if (this.subscribedPlayers.contains(var1.getUUID())) {
         this.sendInitialEntity(var1, var2);
      }

   }

   protected void clear() {
   }

   protected void pollAndSendUpdates(ServerLevel var1) {
   }

   protected void sendInitialChunk(ServerPlayer var1, ChunkPos var2) {
   }

   protected void sendInitialEntity(ServerPlayer var1, Entity var2) {
   }

   public static class SourceSynchronizer<T> extends TrackingDebugSynchronizer<T> {
      private final Map<ChunkPos, ValueSource<T>> chunkSources = new HashMap();
      private final Map<BlockPos, ValueSource<T>> blockEntitySources = new HashMap();
      private final Map<UUID, ValueSource<T>> entitySources = new HashMap();

      public SourceSynchronizer(DebugSubscription<T> var1) {
         super(var1);
      }

      protected void clear() {
         this.chunkSources.clear();
         this.blockEntitySources.clear();
         this.entitySources.clear();
      }

      protected void pollAndSendUpdates(ServerLevel var1) {
         for(Map.Entry var3 : this.chunkSources.entrySet()) {
            DebugSubscription.Update var4 = ((ValueSource)var3.getValue()).pollUpdate(this.subscription);
            if (var4 != null) {
               ChunkPos var5 = (ChunkPos)var3.getKey();
               this.sendToPlayersTrackingChunk(var1, var5, new ClientboundDebugChunkValuePacket(var5, var4));
            }
         }

         for(Map.Entry var9 : this.blockEntitySources.entrySet()) {
            DebugSubscription.Update var11 = ((ValueSource)var9.getValue()).pollUpdate(this.subscription);
            if (var11 != null) {
               BlockPos var13 = (BlockPos)var9.getKey();
               ChunkPos var6 = new ChunkPos(var13);
               this.sendToPlayersTrackingChunk(var1, var6, new ClientboundDebugBlockValuePacket(var13, var11));
            }
         }

         for(Map.Entry var10 : this.entitySources.entrySet()) {
            DebugSubscription.Update var12 = ((ValueSource)var10.getValue()).pollUpdate(this.subscription);
            if (var12 != null) {
               Entity var14 = (Entity)Objects.requireNonNull(var1.getEntity((UUID)var10.getKey()));
               this.sendToPlayersTrackingEntity(var1, var14, new ClientboundDebugEntityValuePacket(var14.getId(), var12));
            }
         }

      }

      public void registerChunk(ChunkPos var1, DebugValueSource.ValueGetter<T> var2) {
         this.chunkSources.put(var1, new ValueSource(var2));
      }

      public void registerBlockEntity(BlockPos var1, DebugValueSource.ValueGetter<T> var2) {
         this.blockEntitySources.put(var1, new ValueSource(var2));
      }

      public void registerEntity(UUID var1, DebugValueSource.ValueGetter<T> var2) {
         this.entitySources.put(var1, new ValueSource(var2));
      }

      public void dropChunk(ChunkPos var1) {
         this.chunkSources.remove(var1);
         Set var10000 = this.blockEntitySources.keySet();
         Objects.requireNonNull(var1);
         var10000.removeIf(var1::contains);
      }

      public void dropBlockEntity(ServerLevel var1, BlockPos var2) {
         ValueSource var3 = (ValueSource)this.blockEntitySources.remove(var2);
         if (var3 != null) {
            ChunkPos var4 = new ChunkPos(var2);
            this.sendToPlayersTrackingChunk(var1, var4, new ClientboundDebugBlockValuePacket(var2, this.subscription.emptyUpdate()));
         }

      }

      public void dropEntity(Entity var1) {
         this.entitySources.remove(var1.getUUID());
      }

      protected void sendInitialChunk(ServerPlayer var1, ChunkPos var2) {
         ValueSource var3 = (ValueSource)this.chunkSources.get(var2);
         if (var3 != null && var3.lastSyncedValue != null) {
            var1.connection.send(new ClientboundDebugChunkValuePacket(var2, this.subscription.packUpdate(var3.lastSyncedValue)));
         }

         for(Map.Entry var5 : this.blockEntitySources.entrySet()) {
            Object var6 = ((ValueSource)var5.getValue()).lastSyncedValue;
            if (var6 != null) {
               BlockPos var7 = (BlockPos)var5.getKey();
               if (var2.contains(var7)) {
                  var1.connection.send(new ClientboundDebugBlockValuePacket(var7, this.subscription.packUpdate(var6)));
               }
            }
         }

      }

      protected void sendInitialEntity(ServerPlayer var1, Entity var2) {
         ValueSource var3 = (ValueSource)this.entitySources.get(var2.getUUID());
         if (var3 != null && var3.lastSyncedValue != null) {
            var1.connection.send(new ClientboundDebugEntityValuePacket(var2.getId(), this.subscription.packUpdate(var3.lastSyncedValue)));
         }

      }
   }

   static class ValueSource<T> {
      private final DebugValueSource.ValueGetter<T> getter;
      @Nullable
      T lastSyncedValue;

      ValueSource(DebugValueSource.ValueGetter<T> var1) {
         super();
         this.getter = var1;
      }

      @Nullable
      public DebugSubscription.Update<T> pollUpdate(DebugSubscription<T> var1) {
         Object var2 = this.getter.get();
         if (!Objects.equals(var2, this.lastSyncedValue)) {
            this.lastSyncedValue = (T)var2;
            return var1.packUpdate(var2);
         } else {
            return null;
         }
      }
   }

   public static class PoiSynchronizer extends TrackingDebugSynchronizer<DebugPoiInfo> {
      public PoiSynchronizer() {
         super(DebugSubscriptions.POIS);
      }

      protected void sendInitialChunk(ServerPlayer var1, ChunkPos var2) {
         ServerLevel var3 = var1.level();
         PoiManager var4 = var3.getPoiManager();
         var4.getInChunk((var0) -> true, var2, PoiManager.Occupancy.ANY).forEach((var2x) -> var1.connection.send(new ClientboundDebugBlockValuePacket(var2x.getPos(), this.subscription.packUpdate(new DebugPoiInfo(var2x)))));
      }

      public void onPoiAdded(ServerLevel var1, PoiRecord var2) {
         this.sendToPlayersTrackingChunk(var1, new ChunkPos(var2.getPos()), new ClientboundDebugBlockValuePacket(var2.getPos(), this.subscription.packUpdate(new DebugPoiInfo(var2))));
      }

      public void onPoiRemoved(ServerLevel var1, BlockPos var2) {
         this.sendToPlayersTrackingChunk(var1, new ChunkPos(var2), new ClientboundDebugBlockValuePacket(var2, this.subscription.emptyUpdate()));
      }

      public void onPoiTicketCountChanged(ServerLevel var1, BlockPos var2) {
         this.sendToPlayersTrackingChunk(var1, new ChunkPos(var2), new ClientboundDebugBlockValuePacket(var2, this.subscription.packUpdate(var1.getPoiManager().getDebugPoiInfo(var2))));
      }
   }

   public static class VillageSectionSynchronizer extends TrackingDebugSynchronizer<Unit> {
      public VillageSectionSynchronizer() {
         super(DebugSubscriptions.VILLAGE_SECTIONS);
      }

      protected void sendInitialChunk(ServerPlayer var1, ChunkPos var2) {
         ServerLevel var3 = var1.level();
         PoiManager var4 = var3.getPoiManager();
         var4.getInChunk((var0) -> true, var2, PoiManager.Occupancy.ANY).forEach((var3x) -> {
            SectionPos var4 = SectionPos.of(var3x.getPos());
            forEachVillageSectionUpdate(var3, var4, (var2, var3xx) -> {
               BlockPos var4 = var2.center();
               var1.connection.send(new ClientboundDebugBlockValuePacket(var4, this.subscription.packUpdate(var3xx ? Unit.INSTANCE : null)));
            });
         });
      }

      public void onPoiAdded(ServerLevel var1, PoiRecord var2) {
         this.sendVillageSectionsPacket(var1, var2.getPos());
      }

      public void onPoiRemoved(ServerLevel var1, BlockPos var2) {
         this.sendVillageSectionsPacket(var1, var2);
      }

      private void sendVillageSectionsPacket(ServerLevel var1, BlockPos var2) {
         forEachVillageSectionUpdate(var1, SectionPos.of(var2), (var2x, var3) -> {
            BlockPos var4 = var2x.center();
            if (var3) {
               this.sendToPlayersTrackingChunk(var1, new ChunkPos(var4), new ClientboundDebugBlockValuePacket(var4, this.subscription.packUpdate(Unit.INSTANCE)));
            } else {
               this.sendToPlayersTrackingChunk(var1, new ChunkPos(var4), new ClientboundDebugBlockValuePacket(var4, this.subscription.emptyUpdate()));
            }

         });
      }

      private static void forEachVillageSectionUpdate(ServerLevel var0, SectionPos var1, BiConsumer<SectionPos, Boolean> var2) {
         for(int var3 = -1; var3 <= 1; ++var3) {
            for(int var4 = -1; var4 <= 1; ++var4) {
               for(int var5 = -1; var5 <= 1; ++var5) {
                  SectionPos var6 = var1.offset(var4, var5, var3);
                  if (var0.isVillage(var6.center())) {
                     var2.accept(var6, true);
                  } else {
                     var2.accept(var6, false);
                  }
               }
            }
         }

      }
   }
}
