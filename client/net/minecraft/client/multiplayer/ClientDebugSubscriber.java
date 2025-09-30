package net.minecraft.client.multiplayer;

import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ServerboundDebugSubscriptionRequestPacket;
import net.minecraft.util.debug.DebugSubscription;
import net.minecraft.util.debug.DebugSubscriptions;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.util.debugchart.RemoteDebugSampleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class ClientDebugSubscriber {
   private final ClientPacketListener connection;
   private final DebugScreenOverlay debugScreenOverlay;
   private Set<DebugSubscription<?>> remoteSubscriptions = Set.of();
   private final Map<DebugSubscription<?>, ValueMaps<?>> valuesBySubscription = new HashMap();

   public ClientDebugSubscriber(ClientPacketListener var1, DebugScreenOverlay var2) {
      super();
      this.debugScreenOverlay = var2;
      this.connection = var1;
   }

   private static void addFlag(Set<DebugSubscription<?>> var0, DebugSubscription<?> var1, boolean var2) {
      if (var2) {
         var0.add(var1);
      }

   }

   private Set<DebugSubscription<?>> requestedSubscriptions() {
      ReferenceOpenHashSet var1 = new ReferenceOpenHashSet();
      addFlag(var1, RemoteDebugSampleType.TICK_TIME.subscription(), this.debugScreenOverlay.showFpsCharts());
      if (SharedConstants.DEBUG_ENABLED) {
         addFlag(var1, DebugSubscriptions.BEES, SharedConstants.DEBUG_BEES);
         addFlag(var1, DebugSubscriptions.BEE_HIVES, SharedConstants.DEBUG_BEES);
         addFlag(var1, DebugSubscriptions.BRAINS, SharedConstants.DEBUG_BRAIN);
         addFlag(var1, DebugSubscriptions.BREEZES, SharedConstants.DEBUG_BREEZE_MOB);
         addFlag(var1, DebugSubscriptions.ENTITY_BLOCK_INTERSECTIONS, SharedConstants.DEBUG_ENTITY_BLOCK_INTERSECTION);
         addFlag(var1, DebugSubscriptions.ENTITY_PATHS, SharedConstants.DEBUG_PATHFINDING);
         addFlag(var1, DebugSubscriptions.GAME_EVENTS, SharedConstants.DEBUG_GAME_EVENT_LISTENERS);
         addFlag(var1, DebugSubscriptions.GAME_EVENT_LISTENERS, SharedConstants.DEBUG_GAME_EVENT_LISTENERS);
         addFlag(var1, DebugSubscriptions.GOAL_SELECTORS, SharedConstants.DEBUG_GOAL_SELECTOR || SharedConstants.DEBUG_BEES);
         addFlag(var1, DebugSubscriptions.NEIGHBOR_UPDATES, SharedConstants.DEBUG_NEIGHBORSUPDATE);
         addFlag(var1, DebugSubscriptions.POIS, SharedConstants.DEBUG_POI);
         addFlag(var1, DebugSubscriptions.RAIDS, SharedConstants.DEBUG_RAIDS);
         addFlag(var1, DebugSubscriptions.REDSTONE_WIRE_ORIENTATIONS, SharedConstants.DEBUG_EXPERIMENTAL_REDSTONEWIRE_UPDATE_ORDER);
         addFlag(var1, DebugSubscriptions.STRUCTURES, SharedConstants.DEBUG_STRUCTURES);
         addFlag(var1, DebugSubscriptions.VILLAGE_SECTIONS, SharedConstants.DEBUG_VILLAGE_SECTIONS);
      }

      return var1;
   }

   public void clear() {
      this.remoteSubscriptions = Set.of();
      this.dropLevel();
   }

   public void tick(long var1) {
      Set var3 = this.requestedSubscriptions();
      if (!var3.equals(this.remoteSubscriptions)) {
         this.remoteSubscriptions = var3;
         this.onSubscriptionsChanged(var3);
      }

      this.valuesBySubscription.forEach((var2, var3x) -> {
         if (var2.expireAfterTicks() != 0) {
            var3x.purgeExpired(var1);
         }

      });
   }

   private void onSubscriptionsChanged(Set<DebugSubscription<?>> var1) {
      this.valuesBySubscription.keySet().retainAll(var1);

      for(DebugSubscription var3 : var1) {
         this.valuesBySubscription.computeIfAbsent(var3, (var0) -> new ValueMaps());
      }

      this.connection.send(new ServerboundDebugSubscriptionRequestPacket(var1));
   }

   @Nullable
   <V> ValueMaps<V> getValueMaps(DebugSubscription<V> var1) {
      return (ValueMaps)this.valuesBySubscription.get(var1);
   }

   @Nullable
   private <K, V> ValueMap<K, V> getValueMap(DebugSubscription<V> var1, ValueMapType<K, V> var2) {
      ValueMaps var3 = this.getValueMaps(var1);
      return var3 != null ? var2.get(var3) : null;
   }

   @Nullable
   <K, V> V getValue(DebugSubscription<V> var1, K var2, ValueMapType<K, V> var3) {
      ValueMap var4 = this.getValueMap(var1, var3);
      return (V)(var4 != null ? var4.getValue(var2) : null);
   }

   public DebugValueAccess createDebugValueAccess(final Level var1) {
      return new DebugValueAccess() {
         public <T> void forEachChunk(DebugSubscription<T> var1x, BiConsumer<ChunkPos, T> var2) {
            ClientDebugSubscriber.this.forEachValue(var1x, ClientDebugSubscriber.chunks(), var2);
         }

         @Nullable
         public <T> T getChunkValue(DebugSubscription<T> var1x, ChunkPos var2) {
            return (T)ClientDebugSubscriber.this.getValue(var1x, var2, ClientDebugSubscriber.chunks());
         }

         public <T> void forEachBlock(DebugSubscription<T> var1x, BiConsumer<BlockPos, T> var2) {
            ClientDebugSubscriber.this.forEachValue(var1x, ClientDebugSubscriber.blocks(), var2);
         }

         @Nullable
         public <T> T getBlockValue(DebugSubscription<T> var1x, BlockPos var2) {
            return (T)ClientDebugSubscriber.this.getValue(var1x, var2, ClientDebugSubscriber.blocks());
         }

         public <T> void forEachEntity(DebugSubscription<T> var1x, BiConsumer<Entity, T> var2) {
            ClientDebugSubscriber.this.forEachValue(var1x, ClientDebugSubscriber.entities(), (var2x, var3) -> {
               Entity var4 = var1.getEntity(var2x);
               if (var4 != null) {
                  var2.accept(var4, var3);
               }

            });
         }

         @Nullable
         public <T> T getEntityValue(DebugSubscription<T> var1x, Entity var2) {
            return (T)ClientDebugSubscriber.this.getValue(var1x, var2.getUUID(), ClientDebugSubscriber.entities());
         }

         public <T> void forEachEvent(DebugSubscription<T> var1x, DebugValueAccess.EventVisitor<T> var2) {
            ValueMaps var3 = ClientDebugSubscriber.this.getValueMaps(var1x);
            if (var3 != null) {
               long var4 = var1.getGameTime();

               for(ValueWrapper var7 : var3.events) {
                  int var8 = (int)(var7.expiresAfterTime() - var4);
                  int var9 = var1x.expireAfterTicks();
                  var2.accept(var7.value(), var8, var9);
               }

            }
         }
      };
   }

   public <T> void updateChunk(long var1, ChunkPos var3, DebugSubscription.Update<T> var4) {
      this.updateMap(var1, var3, var4, chunks());
   }

   public <T> void updateBlock(long var1, BlockPos var3, DebugSubscription.Update<T> var4) {
      this.updateMap(var1, var3, var4, blocks());
   }

   public <T> void updateEntity(long var1, Entity var3, DebugSubscription.Update<T> var4) {
      this.updateMap(var1, var3.getUUID(), var4, entities());
   }

   public <T> void pushEvent(long var1, DebugSubscription.Event<T> var3) {
      ValueMaps var4 = this.getValueMaps(var3.subscription());
      if (var4 != null) {
         var4.events.add(new ValueWrapper(var3.value(), var1 + (long)var3.subscription().expireAfterTicks()));
      }

   }

   private <K, V> void updateMap(long var1, K var3, DebugSubscription.Update<V> var4, ValueMapType<K, V> var5) {
      ValueMap var6 = this.getValueMap(var4.subscription(), var5);
      if (var6 != null) {
         var6.apply(var1, var3, var4);
      }

   }

   <K, V> void forEachValue(DebugSubscription<V> var1, ValueMapType<K, V> var2, BiConsumer<K, V> var3) {
      ValueMap var4 = this.getValueMap(var1, var2);
      if (var4 != null) {
         var4.forEach(var3);
      }

   }

   public void dropLevel() {
      this.valuesBySubscription.clear();
   }

   public void dropChunk(ChunkPos var1) {
      if (!this.valuesBySubscription.isEmpty()) {
         for(ValueMaps var3 : this.valuesBySubscription.values()) {
            var3.dropChunkAndBlocks(var1);
         }

      }
   }

   public void dropEntity(Entity var1) {
      if (!this.valuesBySubscription.isEmpty()) {
         for(ValueMaps var3 : this.valuesBySubscription.values()) {
            var3.entityValues.removeKey(var1.getUUID());
         }

      }
   }

   static <T> ValueMapType<UUID, T> entities() {
      return (var0) -> var0.entityValues;
   }

   static <T> ValueMapType<BlockPos, T> blocks() {
      return (var0) -> var0.blockValues;
   }

   static <T> ValueMapType<ChunkPos, T> chunks() {
      return (var0) -> var0.chunkValues;
   }

   static class ValueMap<K, V> {
      private final Map<K, ValueWrapper<V>> values = new HashMap();

      ValueMap() {
         super();
      }

      public void removeValues(Predicate<ValueWrapper<V>> var1) {
         this.values.values().removeIf(var1);
      }

      public void removeKey(K var1) {
         this.values.remove(var1);
      }

      public void removeKeys(Predicate<K> var1) {
         this.values.keySet().removeIf(var1);
      }

      @Nullable
      public V getValue(K var1) {
         ValueWrapper var2 = (ValueWrapper)this.values.get(var1);
         return (V)(var2 != null ? var2.value() : null);
      }

      public void apply(long var1, K var3, DebugSubscription.Update<V> var4) {
         if (var4.value().isPresent()) {
            this.values.put(var3, new ValueWrapper(var4.value().get(), var1 + (long)var4.subscription().expireAfterTicks()));
         } else {
            this.values.remove(var3);
         }

      }

      public void forEach(BiConsumer<K, V> var1) {
         this.values.forEach((var1x, var2) -> var1.accept(var1x, var2.value()));
      }
   }

   static class ValueMaps<V> {
      final ValueMap<ChunkPos, V> chunkValues = new ValueMap<ChunkPos, V>();
      final ValueMap<BlockPos, V> blockValues = new ValueMap<BlockPos, V>();
      final ValueMap<UUID, V> entityValues = new ValueMap<UUID, V>();
      final List<ValueWrapper<V>> events = new ArrayList();

      ValueMaps() {
         super();
      }

      public void purgeExpired(long var1) {
         Predicate var3 = (var2) -> var2.hasExpired(var1);
         this.chunkValues.removeValues(var3);
         this.blockValues.removeValues(var3);
         this.entityValues.removeValues(var3);
         this.events.removeIf(var3);
      }

      public void dropChunkAndBlocks(ChunkPos var1) {
         this.chunkValues.removeKey(var1);
         ValueMap var10000 = this.blockValues;
         Objects.requireNonNull(var1);
         var10000.removeKeys(var1::contains);
      }
   }

   static record ValueWrapper<T>(T value, long expiresAfterTime) {
      private static final long NO_EXPIRY = -1L;

      ValueWrapper(T var1, long var2) {
         super();
         this.value = var1;
         this.expiresAfterTime = var2;
      }

      public boolean hasExpired(long var1) {
         if (this.expiresAfterTime == -1L) {
            return false;
         } else {
            return var1 >= this.expiresAfterTime;
         }
      }
   }

   @FunctionalInterface
   interface ValueMapType<K, V> {
      ValueMap<K, V> get(ValueMaps<V> var1);
   }
}
