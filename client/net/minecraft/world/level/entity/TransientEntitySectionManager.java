package net.minecraft.world.level.entity;

import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;

public class TransientEntitySectionManager<T extends EntityAccess> {
   static final Logger LOGGER = LogUtils.getLogger();
   final LevelCallback<T> callbacks;
   final EntityLookup<T> entityStorage;
   final EntitySectionStorage<T> sectionStorage;
   private final LongSet tickingChunks = new LongOpenHashSet();
   private final LevelEntityGetter<T> entityGetter;

   public TransientEntitySectionManager(Class<T> var1, LevelCallback<T> var2) {
      super();
      this.entityStorage = new EntityLookup<>();
      this.sectionStorage = new EntitySectionStorage<>(var1, var1x -> this.tickingChunks.contains(var1x) ? Visibility.TICKING : Visibility.TRACKED);
      this.callbacks = var2;
      this.entityGetter = new LevelEntityGetterAdapter<>(this.entityStorage, this.sectionStorage);
   }

   public void startTicking(ChunkPos var1) {
      long var2 = var1.toLong();
      this.tickingChunks.add(var2);
      this.sectionStorage.getExistingSectionsInChunk(var2).forEach(var1x -> {
         Visibility var2xx = var1x.updateChunkStatus(Visibility.TICKING);
         if (!var2xx.isTicking()) {
            var1x.getEntities().filter(var0 -> !var0.isAlwaysTicking()).forEach(this.callbacks::onTickingStart);
         }
      });
   }

   public void stopTicking(ChunkPos var1) {
      long var2 = var1.toLong();
      this.tickingChunks.remove(var2);
      this.sectionStorage.getExistingSectionsInChunk(var2).forEach(var1x -> {
         Visibility var2xx = var1x.updateChunkStatus(Visibility.TRACKED);
         if (var2xx.isTicking()) {
            var1x.getEntities().filter(var0 -> !var0.isAlwaysTicking()).forEach(this.callbacks::onTickingEnd);
         }
      });
   }

   public LevelEntityGetter<T> getEntityGetter() {
      return this.entityGetter;
   }

   public void addEntity(T var1) {
      this.entityStorage.add((T)var1);
      long var2 = SectionPos.asLong(var1.blockPosition());
      EntitySection var4 = this.sectionStorage.getOrCreateSection(var2);
      var4.add(var1);
      var1.setLevelCallback(new TransientEntitySectionManager.Callback(var1, var2, var4));
      this.callbacks.onCreated((T)var1);
      this.callbacks.onTrackingStart((T)var1);
      if (var1.isAlwaysTicking() || var4.getStatus().isTicking()) {
         this.callbacks.onTickingStart((T)var1);
      }
   }

   @VisibleForDebug
   public int count() {
      return this.entityStorage.count();
   }

   void removeSectionIfEmpty(long var1, EntitySection<T> var3) {
      if (var3.isEmpty()) {
         this.sectionStorage.remove(var1);
      }
   }

   @VisibleForDebug
   public String gatherStats() {
      return this.entityStorage.count() + "," + this.sectionStorage.count() + "," + this.tickingChunks.size();
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
               TransientEntitySectionManager.LOGGER
                  .warn("Entity {} wasn't found in section {} (moving to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), var2});
            }

            TransientEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
            EntitySection var5 = TransientEntitySectionManager.this.sectionStorage.getOrCreateSection(var2);
            var5.add(this.entity);
            this.currentSection = var5;
            this.currentSectionKey = var2;
            TransientEntitySectionManager.this.callbacks.onSectionChange(this.entity);
            if (!this.entity.isAlwaysTicking()) {
               boolean var6 = var4.isTicking();
               boolean var7 = var5.getStatus().isTicking();
               if (var6 && !var7) {
                  TransientEntitySectionManager.this.callbacks.onTickingEnd(this.entity);
               } else if (!var6 && var7) {
                  TransientEntitySectionManager.this.callbacks.onTickingStart(this.entity);
               }
            }
         }
      }

      @Override
      public void onRemove(Entity.RemovalReason var1) {
         if (!this.currentSection.remove(this.entity)) {
            TransientEntitySectionManager.LOGGER
               .warn("Entity {} wasn't found in section {} (destroying due to {})", new Object[]{this.entity, SectionPos.of(this.currentSectionKey), var1});
         }

         Visibility var2 = this.currentSection.getStatus();
         if (var2.isTicking() || this.entity.isAlwaysTicking()) {
            TransientEntitySectionManager.this.callbacks.onTickingEnd(this.entity);
         }

         TransientEntitySectionManager.this.callbacks.onTrackingEnd(this.entity);
         TransientEntitySectionManager.this.callbacks.onDestroyed(this.entity);
         TransientEntitySectionManager.this.entityStorage.remove(this.entity);
         this.entity.setLevelCallback(NULL);
         TransientEntitySectionManager.this.removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
      }
   }
}
