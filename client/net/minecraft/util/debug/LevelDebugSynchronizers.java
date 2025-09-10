package net.minecraft.util.debug;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundDebugBlockValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEntityValuePacket;
import net.minecraft.network.protocol.game.ClientboundDebugEventPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.village.poi.PoiRecord;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

public class LevelDebugSynchronizers {
   private final ServerLevel level;
   private final List<TrackingDebugSynchronizer<?>> allSynchronizers = new ArrayList();
   private final Map<DebugSubscription<?>, TrackingDebugSynchronizer.SourceSynchronizer<?>> sourceSynchronizers = new HashMap();
   private final TrackingDebugSynchronizer.PoiSynchronizer poiSynchronizer = new TrackingDebugSynchronizer.PoiSynchronizer();
   private final TrackingDebugSynchronizer.VillageSectionSynchronizer villageSectionSynchronizer = new TrackingDebugSynchronizer.VillageSectionSynchronizer();
   private boolean sleeping = true;
   private Set<DebugSubscription<?>> enabledSubscriptions = Set.of();

   public LevelDebugSynchronizers(ServerLevel var1) {
      super();
      this.level = var1;

      for(DebugSubscription var3 : BuiltInRegistries.DEBUG_SUBSCRIPTION) {
         if (var3.valueStreamCodec() != null) {
            this.sourceSynchronizers.put(var3, new TrackingDebugSynchronizer.SourceSynchronizer(var3));
         }
      }

      this.allSynchronizers.addAll(this.sourceSynchronizers.values());
      this.allSynchronizers.add(this.poiSynchronizer);
      this.allSynchronizers.add(this.villageSectionSynchronizer);
   }

   public void tick(ServerDebugSubscribers var1) {
      this.enabledSubscriptions = var1.enabledSubscriptions();
      boolean var2 = this.enabledSubscriptions.isEmpty();
      if (this.sleeping != var2) {
         if (var2) {
            for(TrackingDebugSynchronizer var4 : this.allSynchronizers) {
               var4.clear();
            }
         } else {
            this.wakeUp();
         }

         this.sleeping = var2;
      }

      if (!this.sleeping) {
         for(TrackingDebugSynchronizer var6 : this.allSynchronizers) {
            var6.tick(this.level);
         }
      }

   }

   private void wakeUp() {
      ChunkMap var1 = this.level.getChunkSource().chunkMap;
      var1.forEachReadyToSendChunk(this::registerChunk);

      for(Entity var3 : this.level.getAllEntities()) {
         if (var1.isTrackedByAnyPlayer(var3)) {
            this.registerEntity(var3);
         }
      }

   }

   <T> TrackingDebugSynchronizer.SourceSynchronizer<T> getSourceSynchronizer(DebugSubscription<T> var1) {
      return (TrackingDebugSynchronizer.SourceSynchronizer)this.sourceSynchronizers.get(var1);
   }

   public void registerChunk(final LevelChunk var1) {
      if (!this.sleeping) {
         var1.registerDebugValues(this.level, new DebugValueSource.Registration() {
            public <T> void register(DebugSubscription<T> var1x, DebugValueSource.ValueGetter<T> var2) {
               LevelDebugSynchronizers.this.getSourceSynchronizer(var1x).registerChunk(var1.getPos(), var2);
            }
         });
         var1.getBlockEntities().values().forEach(this::registerBlockEntity);
      }
   }

   public void dropChunk(ChunkPos var1) {
      if (!this.sleeping) {
         for(TrackingDebugSynchronizer.SourceSynchronizer var3 : this.sourceSynchronizers.values()) {
            var3.dropChunk(var1);
         }

      }
   }

   public void registerBlockEntity(final BlockEntity var1) {
      if (!this.sleeping) {
         var1.registerDebugValues(this.level, new DebugValueSource.Registration() {
            public <T> void register(DebugSubscription<T> var1x, DebugValueSource.ValueGetter<T> var2) {
               LevelDebugSynchronizers.this.getSourceSynchronizer(var1x).registerBlockEntity(var1.getBlockPos(), var2);
            }
         });
      }
   }

   public void dropBlockEntity(BlockPos var1) {
      if (!this.sleeping) {
         for(TrackingDebugSynchronizer.SourceSynchronizer var3 : this.sourceSynchronizers.values()) {
            var3.dropBlockEntity(this.level, var1);
         }

      }
   }

   public void registerEntity(final Entity var1) {
      if (!this.sleeping) {
         var1.registerDebugValues(this.level, new DebugValueSource.Registration() {
            public <T> void register(DebugSubscription<T> var1x, DebugValueSource.ValueGetter<T> var2) {
               LevelDebugSynchronizers.this.getSourceSynchronizer(var1x).registerEntity(var1.getUUID(), var2);
            }
         });
      }
   }

   public void dropEntity(Entity var1) {
      if (!this.sleeping) {
         for(TrackingDebugSynchronizer.SourceSynchronizer var3 : this.sourceSynchronizers.values()) {
            var3.dropEntity(var1);
         }

      }
   }

   public void startTrackingChunk(ServerPlayer var1, ChunkPos var2) {
      if (!this.sleeping) {
         for(TrackingDebugSynchronizer var4 : this.allSynchronizers) {
            var4.startTrackingChunk(var1, var2);
         }

      }
   }

   public void startTrackingEntity(ServerPlayer var1, Entity var2) {
      if (!this.sleeping) {
         for(TrackingDebugSynchronizer var4 : this.allSynchronizers) {
            var4.startTrackingEntity(var1, var2);
         }

      }
   }

   public void registerPoi(PoiRecord var1) {
      if (!this.sleeping) {
         this.poiSynchronizer.onPoiAdded(this.level, var1);
         this.villageSectionSynchronizer.onPoiAdded(this.level, var1);
      }
   }

   public void updatePoi(BlockPos var1) {
      if (!this.sleeping) {
         this.poiSynchronizer.onPoiTicketCountChanged(this.level, var1);
      }
   }

   public void dropPoi(BlockPos var1) {
      if (!this.sleeping) {
         this.poiSynchronizer.onPoiRemoved(this.level, var1);
         this.villageSectionSynchronizer.onPoiRemoved(this.level, var1);
      }
   }

   public boolean hasAnySubscriberFor(DebugSubscription<?> var1) {
      return this.enabledSubscriptions.contains(var1);
   }

   public <T> void sendBlockValue(BlockPos var1, DebugSubscription<T> var2, T var3) {
      if (this.hasAnySubscriberFor(var2)) {
         this.broadcastToTracking((ChunkPos)(new ChunkPos(var1)), var2, new ClientboundDebugBlockValuePacket(var1, var2.packUpdate(var3)));
      }

   }

   public <T> void clearBlockValue(BlockPos var1, DebugSubscription<T> var2) {
      if (this.hasAnySubscriberFor(var2)) {
         this.broadcastToTracking((ChunkPos)(new ChunkPos(var1)), var2, new ClientboundDebugBlockValuePacket(var1, var2.emptyUpdate()));
      }

   }

   public <T> void sendEntityValue(Entity var1, DebugSubscription<T> var2, T var3) {
      if (this.hasAnySubscriberFor(var2)) {
         this.broadcastToTracking((Entity)var1, var2, new ClientboundDebugEntityValuePacket(var1.getId(), var2.packUpdate(var3)));
      }

   }

   public <T> void clearEntityValue(Entity var1, DebugSubscription<T> var2) {
      if (this.hasAnySubscriberFor(var2)) {
         this.broadcastToTracking((Entity)var1, var2, new ClientboundDebugEntityValuePacket(var1.getId(), var2.emptyUpdate()));
      }

   }

   public <T> void broadcastEventToTracking(BlockPos var1, DebugSubscription<T> var2, T var3) {
      if (this.hasAnySubscriberFor(var2)) {
         this.broadcastToTracking((ChunkPos)(new ChunkPos(var1)), var2, new ClientboundDebugEventPacket(var2.packEvent(var3)));
      }

   }

   private void broadcastToTracking(ChunkPos var1, DebugSubscription<?> var2, Packet<? super ClientGamePacketListener> var3) {
      ChunkMap var4 = this.level.getChunkSource().chunkMap;

      for(ServerPlayer var6 : var4.getPlayers(var1, false)) {
         if (var6.debugSubscriptions().contains(var2)) {
            var6.connection.send(var3);
         }
      }

   }

   private void broadcastToTracking(Entity var1, DebugSubscription<?> var2, Packet<? super ClientGamePacketListener> var3) {
      ChunkMap var4 = this.level.getChunkSource().chunkMap;
      var4.sendToTrackingPlayersFiltered(var1, var3, (var1x) -> var1x.debugSubscriptions().contains(var2));
   }
}
