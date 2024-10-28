package net.minecraft.world.level.entity;

import it.unimi.dsi.fastutil.longs.Long2ObjectFunction;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongBidirectionalIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import java.util.Objects;
import java.util.Spliterators;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.util.AbortableIterationConsumer;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

public class EntitySectionStorage<T extends EntityAccess> {
   public static final int CHONKY_ENTITY_SEARCH_GRACE = 2;
   public static final int MAX_NON_CHONKY_ENTITY_SIZE = 4;
   private final Class<T> entityClass;
   private final Long2ObjectFunction<Visibility> intialSectionVisibility;
   private final Long2ObjectMap<EntitySection<T>> sections = new Long2ObjectOpenHashMap();
   private final LongSortedSet sectionIds = new LongAVLTreeSet();

   public EntitySectionStorage(Class<T> var1, Long2ObjectFunction<Visibility> var2) {
      super();
      this.entityClass = var1;
      this.intialSectionVisibility = var2;
   }

   public void forEachAccessibleNonEmptySection(AABB var1, AbortableIterationConsumer<EntitySection<T>> var2) {
      int var3 = SectionPos.posToSectionCoord(var1.minX - 2.0);
      int var4 = SectionPos.posToSectionCoord(var1.minY - 4.0);
      int var5 = SectionPos.posToSectionCoord(var1.minZ - 2.0);
      int var6 = SectionPos.posToSectionCoord(var1.maxX + 2.0);
      int var7 = SectionPos.posToSectionCoord(var1.maxY + 0.0);
      int var8 = SectionPos.posToSectionCoord(var1.maxZ + 2.0);

      for(int var9 = var3; var9 <= var6; ++var9) {
         long var10 = SectionPos.asLong(var9, 0, 0);
         long var12 = SectionPos.asLong(var9, -1, -1);
         LongBidirectionalIterator var14 = this.sectionIds.subSet(var10, var12 + 1L).iterator();

         while(var14.hasNext()) {
            long var15 = var14.nextLong();
            int var17 = SectionPos.y(var15);
            int var18 = SectionPos.z(var15);
            if (var17 >= var4 && var17 <= var7 && var18 >= var5 && var18 <= var8) {
               EntitySection var19 = (EntitySection)this.sections.get(var15);
               if (var19 != null && !var19.isEmpty() && var19.getStatus().isAccessible() && var2.accept(var19).shouldAbort()) {
                  return;
               }
            }
         }
      }

   }

   public LongStream getExistingSectionPositionsInChunk(long var1) {
      int var3 = ChunkPos.getX(var1);
      int var4 = ChunkPos.getZ(var1);
      LongSortedSet var5 = this.getChunkSections(var3, var4);
      if (var5.isEmpty()) {
         return LongStream.empty();
      } else {
         LongBidirectionalIterator var6 = var5.iterator();
         return StreamSupport.longStream(Spliterators.spliteratorUnknownSize(var6, 1301), false);
      }
   }

   private LongSortedSet getChunkSections(int var1, int var2) {
      long var3 = SectionPos.asLong(var1, 0, var2);
      long var5 = SectionPos.asLong(var1, -1, var2);
      return this.sectionIds.subSet(var3, var5 + 1L);
   }

   public Stream<EntitySection<T>> getExistingSectionsInChunk(long var1) {
      LongStream var10000 = this.getExistingSectionPositionsInChunk(var1);
      Long2ObjectMap var10001 = this.sections;
      Objects.requireNonNull(var10001);
      return var10000.mapToObj(var10001::get).filter(Objects::nonNull);
   }

   private static long getChunkKeyFromSectionKey(long var0) {
      return ChunkPos.asLong(SectionPos.x(var0), SectionPos.z(var0));
   }

   public EntitySection<T> getOrCreateSection(long var1) {
      return (EntitySection)this.sections.computeIfAbsent(var1, this::createSection);
   }

   @Nullable
   public EntitySection<T> getSection(long var1) {
      return (EntitySection)this.sections.get(var1);
   }

   private EntitySection<T> createSection(long var1) {
      long var3 = getChunkKeyFromSectionKey(var1);
      Visibility var5 = (Visibility)this.intialSectionVisibility.get(var3);
      this.sectionIds.add(var1);
      return new EntitySection(this.entityClass, var5);
   }

   public LongSet getAllChunksWithExistingSections() {
      LongOpenHashSet var1 = new LongOpenHashSet();
      this.sections.keySet().forEach((var1x) -> {
         var1.add(getChunkKeyFromSectionKey(var1x));
      });
      return var1;
   }

   public void getEntities(AABB var1, AbortableIterationConsumer<T> var2) {
      this.forEachAccessibleNonEmptySection(var1, (var2x) -> {
         return var2x.getEntities(var1, var2);
      });
   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> var1, AABB var2, AbortableIterationConsumer<U> var3) {
      this.forEachAccessibleNonEmptySection(var2, (var3x) -> {
         return var3x.getEntities(var1, var2, var3);
      });
   }

   public void remove(long var1) {
      this.sections.remove(var1);
      this.sectionIds.remove(var1);
   }

   @VisibleForDebug
   public int count() {
      return this.sectionIds.size();
   }
}
