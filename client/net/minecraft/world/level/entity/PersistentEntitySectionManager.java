package net.minecraft.world.level.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.FullChunkStatus;
import net.minecraft.util.CsvOutput;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class PersistentEntitySectionManager<T extends EntityAccess> implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   final Set<UUID> knownUuids = Sets.newHashSet();
   final LevelCallback<T> callbacks;
   private final EntityPersistentStorage<T> permanentStorage;
   private final EntityLookup<T> visibleEntityStorage;
   final EntitySectionStorage<T> sectionStorage;
   private final LevelEntityGetter<T> entityGetter;
   private final Long2ObjectMap<Visibility> chunkVisibility = new Long2ObjectOpenHashMap();
   private final Long2ObjectMap<PersistentEntitySectionManager.ChunkLoadStatus> chunkLoadStatuses = new Long2ObjectOpenHashMap();
   private final LongSet chunksToUnload = new LongOpenHashSet();
   private final Queue<ChunkEntities<T>> loadingInbox = Queues.newConcurrentLinkedQueue();

   public PersistentEntitySectionManager(Class<T> var1, LevelCallback<T> var2, EntityPersistentStorage<T> var3) {
      super();
      this.visibleEntityStorage = new EntityLookup<>();
      this.sectionStorage = new EntitySectionStorage<>(var1, this.chunkVisibility);
      this.chunkVisibility.defaultReturnValue(Visibility.HIDDEN);
      this.chunkLoadStatuses.defaultReturnValue(PersistentEntitySectionManager.ChunkLoadStatus.FRESH);
      this.callbacks = var2;
      this.permanentStorage = var3;
      this.entityGetter = new LevelEntityGetterAdapter<>(this.visibleEntityStorage, this.sectionStorage);
   }

   void removeSectionIfEmpty(long var1, EntitySection<T> var3) {
      if (var3.isEmpty()) {
         this.sectionStorage.remove(var1);
      }
   }

   private boolean addEntityUuid(T var1) {
      if (!this.knownUuids.add(var1.getUUID())) {
         LOGGER.warn("UUID of added entity already exists: {}", var1);
         return false;
      } else {
         return true;
      }
   }

   public boolean addNewEntity(T var1) {
      return this.addEntity((T)var1, false);
   }

   private boolean addEntity(T var1, boolean var2) {
      if (!this.addEntityUuid((T)var1)) {
         return false;
      } else {
         long var3 = SectionPos.asLong(var1.blockPosition());
         EntitySection var5 = this.sectionStorage.getOrCreateSection(var3);
         var5.add(var1);
         var1.setLevelCallback(new PersistentEntitySectionManager.Callback(var1, var3, var5));
         if (!var2) {
            this.callbacks.onCreated((T)var1);
         }

         Visibility var6 = getEffectiveStatus(var1, var5.getStatus());
         if (var6.isAccessible()) {
            this.startTracking((T)var1);
         }

         if (var6.isTicking()) {
            this.startTicking((T)var1);
         }

         return true;
      }
   }

   static <T extends EntityAccess> Visibility getEffectiveStatus(T var0, Visibility var1) {
      return var0.isAlwaysTicking() ? Visibility.TICKING : var1;
   }

   public void addLegacyChunkEntities(Stream<T> var1) {
      var1.forEach(var1x -> this.addEntity((T)var1x, true));
   }

   public void addWorldGenChunkEntities(Stream<T> var1) {
      var1.forEach(var1x -> this.addEntity((T)var1x, false));
   }

   void startTicking(T var1) {
      this.callbacks.onTickingStart((T)var1);
   }

   void stopTicking(T var1) {
      this.callbacks.onTickingEnd((T)var1);
   }

   void startTracking(T var1) {
      this.visibleEntityStorage.add((T)var1);
      this.callbacks.onTrackingStart((T)var1);
   }

   void stopTracking(T var1) {
      this.callbacks.onTrackingEnd((T)var1);
      this.visibleEntityStorage.remove((T)var1);
   }

   public void updateChunkStatus(ChunkPos var1, FullChunkStatus var2) {
      Visibility var3 = Visibility.fromFullChunkStatus(var2);
      this.updateChunkStatus(var1, var3);
   }

   public void updateChunkStatus(ChunkPos var1, Visibility var2) {
      long var3 = var1.toLong();
      if (var2 == Visibility.HIDDEN) {
         this.chunkVisibility.remove(var3);
         this.chunksToUnload.add(var3);
      } else {
         this.chunkVisibility.put(var3, var2);
         this.chunksToUnload.remove(var3);
         this.ensureChunkQueuedForLoad(var3);
      }

      this.sectionStorage.getExistingSectionsInChunk(var3).forEach(var2x -> {
         Visibility var3x = var2x.updateChunkStatus(var2);
         boolean var4 = var3x.isAccessible();
         boolean var5 = var2.isAccessible();
         boolean var6 = var3x.isTicking();
         boolean var7 = var2.isTicking();
         if (var6 && !var7) {
            var2x.getEntities().filter(var0 -> !var0.isAlwaysTicking()).forEach(this::stopTicking);
         }

         if (var4 && !var5) {
            var2x.getEntities().filter(var0 -> !var0.isAlwaysTicking()).forEach(this::stopTracking);
         } else if (!var4 && var5) {
            var2x.getEntities().filter(var0 -> !var0.isAlwaysTicking()).forEach(this::startTracking);
         }

         if (!var6 && var7) {
            var2x.getEntities().filter(var0 -> !var0.isAlwaysTicking()).forEach(this::startTicking);
         }
      });
   }

   private void ensureChunkQueuedForLoad(long var1) {
      PersistentEntitySectionManager.ChunkLoadStatus var3 = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(var1);
      if (var3 == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
         this.requestChunkLoad(var1);
      }
   }

   private boolean storeChunkSections(long var1, Consumer<T> var3) {
      PersistentEntitySectionManager.ChunkLoadStatus var4 = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(var1);
      if (var4 == PersistentEntitySectionManager.ChunkLoadStatus.PENDING) {
         return false;
      } else {
         List var5 = this.sectionStorage
            .getExistingSectionsInChunk(var1)
            .flatMap(var0 -> var0.getEntities().filter(EntityAccess::shouldBeSaved))
            .collect(Collectors.toList());
         if (var5.isEmpty()) {
            if (var4 == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
               this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkPos(var1), ImmutableList.of()));
            }

            return true;
         } else if (var4 == PersistentEntitySectionManager.ChunkLoadStatus.FRESH) {
            this.requestChunkLoad(var1);
            return false;
         } else {
            this.permanentStorage.storeEntities(new ChunkEntities<>(new ChunkPos(var1), var5));
            var5.forEach(var3);
            return true;
         }
      }
   }

   private void requestChunkLoad(long var1) {
      this.chunkLoadStatuses.put(var1, PersistentEntitySectionManager.ChunkLoadStatus.PENDING);
      ChunkPos var3 = new ChunkPos(var1);
      this.permanentStorage.loadEntities(var3).thenAccept(this.loadingInbox::add).exceptionally(var1x -> {
         LOGGER.error("Failed to read chunk {}", var3, var1x);
         return null;
      });
   }

   private boolean processChunkUnload(long var1) {
      boolean var3 = this.storeChunkSections(var1, var1x -> var1x.getPassengersAndSelf().forEach(this::unloadEntity));
      if (!var3) {
         return false;
      } else {
         this.chunkLoadStatuses.remove(var1);
         return true;
      }
   }

   private void unloadEntity(EntityAccess var1) {
      var1.setRemoved(Entity.RemovalReason.UNLOADED_TO_CHUNK);
      var1.setLevelCallback(EntityInLevelCallback.NULL);
   }

   private void processUnloads() {
      this.chunksToUnload.removeIf(var1 -> this.chunkVisibility.get(var1) != Visibility.HIDDEN ? true : this.processChunkUnload(var1));
   }

   private void processPendingLoads() {
      ChunkEntities var1;
      while((var1 = this.loadingInbox.poll()) != null) {
         var1.getEntities().forEach(var1x -> this.addEntity((T)var1x, true));
         this.chunkLoadStatuses.put(var1.getPos().toLong(), PersistentEntitySectionManager.ChunkLoadStatus.LOADED);
      }
   }

   public void tick() {
      this.processPendingLoads();
      this.processUnloads();
   }

   private LongSet getAllChunksToSave() {
      LongSet var1 = this.sectionStorage.getAllChunksWithExistingSections();
      ObjectIterator var2 = Long2ObjectMaps.fastIterable(this.chunkLoadStatuses).iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (var3.getValue() == PersistentEntitySectionManager.ChunkLoadStatus.LOADED) {
            var1.add(var3.getLongKey());
         }
      }

      return var1;
   }

   public void autoSave() {
      this.getAllChunksToSave().forEach(var1 -> {
         boolean var3 = this.chunkVisibility.get(var1) == Visibility.HIDDEN;
         if (var3) {
            this.processChunkUnload(var1);
         } else {
            this.storeChunkSections(var1, var0 -> {
            });
         }
      });
   }

   public void saveAll() {
      LongSet var1 = this.getAllChunksToSave();

      while(!var1.isEmpty()) {
         this.permanentStorage.flush(false);
         this.processPendingLoads();
         var1.removeIf(var1x -> {
            boolean var3 = this.chunkVisibility.get(var1x) == Visibility.HIDDEN;
            return var3 ? this.processChunkUnload(var1x) : this.storeChunkSections(var1x, var0 -> {
            });
         });
      }

      this.permanentStorage.flush(true);
   }

   @Override
   public void close() throws IOException {
      this.saveAll();
      this.permanentStorage.close();
   }

   public boolean isLoaded(UUID var1) {
      return this.knownUuids.contains(var1);
   }

   public LevelEntityGetter<T> getEntityGetter() {
      return this.entityGetter;
   }

   public boolean canPositionTick(BlockPos var1) {
      return ((Visibility)this.chunkVisibility.get(ChunkPos.asLong(var1))).isTicking();
   }

   public boolean canPositionTick(ChunkPos var1) {
      return ((Visibility)this.chunkVisibility.get(var1.toLong())).isTicking();
   }

   public boolean areEntitiesLoaded(long var1) {
      return this.chunkLoadStatuses.get(var1) == PersistentEntitySectionManager.ChunkLoadStatus.LOADED;
   }

   public void dumpSections(Writer var1) throws IOException {
      CsvOutput var2 = CsvOutput.builder()
         .addColumn("x")
         .addColumn("y")
         .addColumn("z")
         .addColumn("visibility")
         .addColumn("load_status")
         .addColumn("entity_count")
         .build(var1);
      this.sectionStorage.getAllChunksWithExistingSections().forEach(var2x -> {
         PersistentEntitySectionManager.ChunkLoadStatus var4 = (PersistentEntitySectionManager.ChunkLoadStatus)this.chunkLoadStatuses.get(var2x);
         this.sectionStorage.getExistingSectionPositionsInChunk(var2x).forEach(var3 -> {
            EntitySection var5 = this.sectionStorage.getSection(var3);
            if (var5 != null) {
               try {
                  var2.writeRow(SectionPos.x(var3), SectionPos.y(var3), SectionPos.z(var3), var5.getStatus(), var4, var5.size());
               } catch (IOException var7) {
                  throw new UncheckedIOException(var7);
               }
            }
         });
      });
   }

   @VisibleForDebug
   public String gatherStats() {
      return this.knownUuids.size()
         + ","
         + this.visibleEntityStorage.count()
         + ","
         + this.sectionStorage.count()
         + ","
         + this.chunkLoadStatuses.size()
         + ","
         + this.chunkVisibility.size()
         + ","
         + this.loadingInbox.size()
         + ","
         + this.chunksToUnload.size();
   }

   class Callback implements EntityInLevelCallback {
      private final T entity;
      private long currentSectionKey;
      private EntitySection<T> currentSection;

      Callback(T var2, long var3, EntitySection<T> var5) {
         super();
         this.entity = var2;
         this.currentSectionKey = var3;
         this.currentSection = var5;
      }

      @Override
      public void onMove() {
         BlockPos var1 = this.entity.blockPosition();
         long var2 = SectionPos.asLong(var1);
         if (var2 != this.currentSectionKey) {
            Visibility var4 = this.currentSection.getStatus();
            if (!this.currentSection.remove(this.entity)) {
               PersistentEntitySectionManager.LOGGER
                  .warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), var2});
            }

            PersistentEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
            EntitySection var5 = PersistentEntitySectionManager.this.sectionStorage.getOrCreateSection(var2);
            var5.add(this.entity);
            this.currentSection = var5;
            this.currentSectionKey = var2;
            this.updateStatus(var4, var5.getStatus());
         }
      }

      private void updateStatus(Visibility var1, Visibility var2) {
         Visibility var3 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, var1);
         Visibility var4 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, var2);
         if (var3 == var4) {
            if (var4.isAccessible()) {
               PersistentEntitySectionManager.this.callbacks.onSectionChange(this.entity);
            }
         } else {
            boolean var5 = var3.isAccessible();
            boolean var6 = var4.isAccessible();
            if (var5 && !var6) {
               PersistentEntitySectionManager.this.stopTracking(this.entity);
            } else if (!var5 && var6) {
               PersistentEntitySectionManager.this.startTracking(this.entity);
            }

            boolean var7 = var3.isTicking();
            boolean var8 = var4.isTicking();
            if (var7 && !var8) {
               PersistentEntitySectionManager.this.stopTicking(this.entity);
            } else if (!var7 && var8) {
               PersistentEntitySectionManager.this.startTicking(this.entity);
            }

            if (var6) {
               PersistentEntitySectionManager.this.callbacks.onSectionChange(this.entity);
            }
         }
      }

      @Override
      public void onRemove(Entity.RemovalReason var1) {
         if (!this.currentSection.remove(this.entity)) {
            PersistentEntitySectionManager.LOGGER
               .warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), var1});
         }

         Visibility var2 = PersistentEntitySectionManager.getEffectiveStatus(this.entity, this.currentSection.getStatus());
         if (var2.isTicking()) {
            PersistentEntitySectionManager.this.stopTicking(this.entity);
         }

         if (var2.isAccessible()) {
            PersistentEntitySectionManager.this.stopTracking(this.entity);
         }

         if (var1.shouldDestroy()) {
            PersistentEntitySectionManager.this.callbacks.onDestroyed(this.entity);
         }

         PersistentEntitySectionManager.this.knownUuids.remove(this.entity.getUUID());
         this.entity.setLevelCallback(NULL);
         PersistentEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
      }
   }

   static enum ChunkLoadStatus {
      FRESH,
      PENDING,
      LOADED;

      private ChunkLoadStatus() {
      }
   }
}
