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
import java.util.function.Consumer;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.core.SectionPos;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

public class EntitySectionStorage<T extends EntityAccess> {
   private final Class<T> entityClass;
   private final Long2ObjectFunction<Visibility> intialSectionVisibility;
   private final Long2ObjectMap<EntitySection<T>> sections = new Long2ObjectOpenHashMap();
   private final LongSortedSet sectionIds = new LongAVLTreeSet();

   public EntitySectionStorage(Class<T> var1, Long2ObjectFunction<Visibility> var2) {
      super();
      this.entityClass = var1;
      this.intialSectionVisibility = var2;
   }

   public void forEachAccessibleNonEmptySection(AABB var1, Consumer<EntitySection<T>> var2) {
      boolean var3 = true;
      int var4 = SectionPos.posToSectionCoord(var1.minX - 2.0);
      int var5 = SectionPos.posToSectionCoord(var1.minY - 4.0);
      int var6 = SectionPos.posToSectionCoord(var1.minZ - 2.0);
      int var7 = SectionPos.posToSectionCoord(var1.maxX + 2.0);
      int var8 = SectionPos.posToSectionCoord(var1.maxY + 0.0);
      int var9 = SectionPos.posToSectionCoord(var1.maxZ + 2.0);

      for(int var10 = var4; var10 <= var7; ++var10) {
         long var11 = SectionPos.asLong(var10, 0, 0);
         long var13 = SectionPos.asLong(var10, -1, -1);
         LongBidirectionalIterator var15 = this.sectionIds.subSet(var11, var13 + 1L).iterator();

         while(var15.hasNext()) {
            long var16 = var15.nextLong();
            int var18 = SectionPos.y(var16);
            int var19 = SectionPos.z(var16);
            if (var18 >= var5 && var18 <= var8 && var19 >= var6 && var19 <= var9) {
               EntitySection var20 = (EntitySection)this.sections.get(var16);
               if (var20 != null && !var20.isEmpty() && var20.getStatus().isAccessible()) {
                  var2.accept(var20);
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
      return this.getExistingSectionPositionsInChunk(var1).<EntitySection<T>>mapToObj(this.sections::get).filter(Objects::nonNull);
   }

   private static long getChunkKeyFromSectionKey(long var0) {
      return ChunkPos.asLong(SectionPos.x(var0), SectionPos.z(var0));
   }

   public EntitySection<T> getOrCreateSection(long var1) {
      return (EntitySection<T>)this.sections.computeIfAbsent(var1, this::createSection);
   }

   @Nullable
   public EntitySection<T> getSection(long var1) {
      return (EntitySection<T>)this.sections.get(var1);
   }

   private EntitySection<T> createSection(long var1) {
      long var3 = getChunkKeyFromSectionKey(var1);
      Visibility var5 = (Visibility)this.intialSectionVisibility.get(var3);
      this.sectionIds.add(var1);
      return new EntitySection<>(this.entityClass, var5);
   }

   public LongSet getAllChunksWithExistingSections() {
      LongOpenHashSet var1 = new LongOpenHashSet();
      this.sections.keySet().forEach(var1x -> var1.add(getChunkKeyFromSectionKey(var1x)));
      return var1;
   }

   public void getEntities(AABB var1, Consumer<T> var2) {
      this.forEachAccessibleNonEmptySection(var1, var2x -> var2x.getEntities(var1, var2));
   }

   public <U extends T> void getEntities(EntityTypeTest<T, U> var1, AABB var2, Consumer<U> var3) {
      this.forEachAccessibleNonEmptySection(var2, var3x -> var3x.getEntities(var1, var2, var3));
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
