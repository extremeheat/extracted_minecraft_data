package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.tags.PoiTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.storage.ChunkIOErrorReporter;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;
import net.minecraft.world.level.chunk.storage.SectionStorage;
import net.minecraft.world.level.chunk.storage.SimpleRegionStorage;

public class PoiManager extends SectionStorage<PoiSection, PoiSection.Packed> {
   public static final int MAX_VILLAGE_DISTANCE = 6;
   public static final int VILLAGE_SECTION_SIZE = 1;
   private final PoiManager.DistanceTracker distanceTracker;
   private final LongSet loadedChunks = new LongOpenHashSet();

   public PoiManager(RegionStorageInfo var1, Path var2, DataFixer var3, boolean var4, RegistryAccess var5, ChunkIOErrorReporter var6, LevelHeightAccessor var7) {
      super(
         new SimpleRegionStorage(var1, var2, var3, var4, DataFixTypes.POI_CHUNK),
         PoiSection.Packed.CODEC,
         PoiSection::pack,
         PoiSection.Packed::unpack,
         PoiSection::new,
         var5,
         var6,
         var7
      );
      this.distanceTracker = new PoiManager.DistanceTracker();
   }

   public void add(BlockPos var1, Holder<PoiType> var2) {
      this.getOrCreate(SectionPos.asLong(var1)).add(var1, var2);
   }

   public void remove(BlockPos var1) {
      this.getOrLoad(SectionPos.asLong(var1)).ifPresent(var1x -> var1x.remove(var1));
   }

   public long getCountInRange(Predicate<Holder<PoiType>> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return this.getInRange(var1, var2, var3, var4).count();
   }

   public boolean existsAtPosition(ResourceKey<PoiType> var1, BlockPos var2) {
      return this.exists(var2, var1x -> var1x.is(var1));
   }

   public Stream<PoiRecord> getInSquare(Predicate<Holder<PoiType>> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      int var5 = Math.floorDiv(var3, 16) + 1;
      return ChunkPos.rangeClosed(new ChunkPos(var2), var5).flatMap(var3x -> this.getInChunk(var1, var3x, var4)).filter(var2x -> {
         BlockPos var3x = var2x.getPos();
         return Math.abs(var3x.getX() - var2.getX()) <= var3 && Math.abs(var3x.getZ() - var2.getZ()) <= var3;
      });
   }

   public Stream<PoiRecord> getInRange(Predicate<Holder<PoiType>> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      int var5 = var3 * var3;
      return this.getInSquare(var1, var2, var3, var4).filter(var2x -> var2x.getPos().distSqr(var2) <= (double)var5);
   }

   @VisibleForDebug
   public Stream<PoiRecord> getInChunk(Predicate<Holder<PoiType>> var1, ChunkPos var2, PoiManager.Occupancy var3) {
      return IntStream.range(this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection())
         .boxed()
         .map(var2x -> this.getOrLoad(SectionPos.of(var2, var2x).asLong()))
         .filter(Optional::isPresent)
         .flatMap(var2x -> var2x.get().getRecords(var1, var3));
   }

   public Stream<BlockPos> findAll(Predicate<Holder<PoiType>> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.getInRange(var1, var3, var4, var5).map(PoiRecord::getPos).filter(var2);
   }

   public Stream<Pair<Holder<PoiType>, BlockPos>> findAllWithType(
      Predicate<Holder<PoiType>> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5
   ) {
      return this.getInRange(var1, var3, var4, var5).filter(var1x -> var2.test(var1x.getPos())).map(var0 -> Pair.of(var0.getPoiType(), var0.getPos()));
   }

   public Stream<Pair<Holder<PoiType>, BlockPos>> findAllClosestFirstWithType(
      Predicate<Holder<PoiType>> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5
   ) {
      return this.findAllWithType(var1, var2, var3, var4, var5).sorted(Comparator.comparingDouble(var1x -> ((BlockPos)var1x.getSecond()).distSqr(var3)));
   }

   public Optional<BlockPos> find(Predicate<Holder<PoiType>> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.findAll(var1, var2, var3, var4, var5).findFirst();
   }

   public Optional<BlockPos> findClosest(Predicate<Holder<PoiType>> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return this.getInRange(var1, var2, var3, var4).map(PoiRecord::getPos).min(Comparator.comparingDouble(var1x -> var1x.distSqr(var2)));
   }

   public Optional<Pair<Holder<PoiType>, BlockPos>> findClosestWithType(Predicate<Holder<PoiType>> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return this.getInRange(var1, var2, var3, var4)
         .min(Comparator.comparingDouble(var1x -> var1x.getPos().distSqr(var2)))
         .map(var0 -> Pair.of(var0.getPoiType(), var0.getPos()));
   }

   public Optional<BlockPos> findClosest(Predicate<Holder<PoiType>> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.getInRange(var1, var3, var4, var5).map(PoiRecord::getPos).filter(var2).min(Comparator.comparingDouble(var1x -> var1x.distSqr(var3)));
   }

   public Optional<BlockPos> take(Predicate<Holder<PoiType>> var1, BiPredicate<Holder<PoiType>, BlockPos> var2, BlockPos var3, int var4) {
      return this.getInRange(var1, var3, var4, PoiManager.Occupancy.HAS_SPACE)
         .filter(var1x -> var2.test(var1x.getPoiType(), var1x.getPos()))
         .findFirst()
         .map(var0 -> {
            var0.acquireTicket();
            return var0.getPos();
         });
   }

   public Optional<BlockPos> getRandom(
      Predicate<Holder<PoiType>> var1, Predicate<BlockPos> var2, PoiManager.Occupancy var3, BlockPos var4, int var5, RandomSource var6
   ) {
      List var7 = Util.toShuffledList(this.getInRange(var1, var4, var5, var3), var6);
      return var7.stream().filter(var1x -> var2.test(var1x.getPos())).findFirst().map(PoiRecord::getPos);
   }

   public boolean release(BlockPos var1) {
      return this.getOrLoad(SectionPos.asLong(var1))
         .map(var1x -> var1x.release(var1))
         .orElseThrow(() -> Util.pauseInIde(new IllegalStateException("POI never registered at " + var1)));
   }

   public boolean exists(BlockPos var1, Predicate<Holder<PoiType>> var2) {
      return this.getOrLoad(SectionPos.asLong(var1)).map(var2x -> var2x.exists(var1, var2)).orElse(false);
   }

   public Optional<Holder<PoiType>> getType(BlockPos var1) {
      return this.getOrLoad(SectionPos.asLong(var1)).flatMap(var1x -> var1x.getType(var1));
   }

   @Deprecated
   @VisibleForDebug
   public int getFreeTickets(BlockPos var1) {
      return this.getOrLoad(SectionPos.asLong(var1)).map(var1x -> var1x.getFreeTickets(var1)).orElse(0);
   }

   public int sectionsToVillage(SectionPos var1) {
      this.distanceTracker.runAllUpdates();
      return this.distanceTracker.getLevel(var1.asLong());
   }

   boolean isVillageCenter(long var1) {
      Optional var3 = this.get(var1);
      return var3 == null
         ? false
         : var3.<Boolean>map(var0 -> var0.getRecords(var0x -> var0x.is(PoiTypeTags.VILLAGE), PoiManager.Occupancy.IS_OCCUPIED).findAny().isPresent())
            .orElse(false);
   }

   @Override
   public void tick(BooleanSupplier var1) {
      super.tick(var1);
      this.distanceTracker.runAllUpdates();
   }

   @Override
   protected void setDirty(long var1) {
      super.setDirty(var1);
      this.distanceTracker.update(var1, this.distanceTracker.getLevelFromSource(var1), false);
   }

   @Override
   protected void onSectionLoad(long var1) {
      this.distanceTracker.update(var1, this.distanceTracker.getLevelFromSource(var1), false);
   }

   public void checkConsistencyWithBlocks(SectionPos var1, LevelChunkSection var2) {
      Util.ifElse(this.getOrLoad(var1.asLong()), var3 -> var3.refresh(var3x -> {
            if (mayHavePoi(var2)) {
               this.updateFromSection(var2, var1, var3x);
            }
         }), () -> {
         if (mayHavePoi(var2)) {
            PoiSection var3 = this.getOrCreate(var1.asLong());
            this.updateFromSection(var2, var1, var3::add);
         }
      });
   }

   private static boolean mayHavePoi(LevelChunkSection var0) {
      return var0.maybeHas(PoiTypes::hasPoi);
   }

   private void updateFromSection(LevelChunkSection var1, SectionPos var2, BiConsumer<BlockPos, Holder<PoiType>> var3) {
      var2.blocksInside()
         .forEach(
            var2x -> {
               BlockState var3x = var1.getBlockState(
                  SectionPos.sectionRelative(var2x.getX()), SectionPos.sectionRelative(var2x.getY()), SectionPos.sectionRelative(var2x.getZ())
               );
               PoiTypes.forState(var3x).ifPresent(var2xx -> var3.accept(var2x, var2xx));
            }
         );
   }

   public void ensureLoadedAndValid(LevelReader var1, BlockPos var2, int var3) {
      SectionPos.aroundChunk(new ChunkPos(var2), Math.floorDiv(var3, 16), this.levelHeightAccessor.getMinSection(), this.levelHeightAccessor.getMaxSection())
         .map(var1x -> Pair.of(var1x, this.getOrLoad(var1x.asLong())))
         .filter(var0 -> !((Optional)var0.getSecond()).map(PoiSection::isValid).orElse(false))
         .map(var0 -> ((SectionPos)var0.getFirst()).chunk())
         .filter(var1x -> this.loadedChunks.add(var1x.toLong()))
         .forEach(var1x -> var1.getChunk(var1x.x, var1x.z, ChunkStatus.EMPTY));
   }

   final class DistanceTracker extends SectionTracker {
      private final Long2ByteMap levels = new Long2ByteOpenHashMap();

      protected DistanceTracker() {
         super(7, 16, 256);
         this.levels.defaultReturnValue((byte)7);
      }

      @Override
      protected int getLevelFromSource(long var1) {
         return PoiManager.this.isVillageCenter(var1) ? 0 : 7;
      }

      @Override
      protected int getLevel(long var1) {
         return this.levels.get(var1);
      }

      @Override
      protected void setLevel(long var1, int var3) {
         if (var3 > 6) {
            this.levels.remove(var1);
         } else {
            this.levels.put(var1, (byte)var3);
         }
      }

      public void runAllUpdates() {
         super.runUpdates(2147483647);
      }
   }

   public static enum Occupancy {
      HAS_SPACE(PoiRecord::hasSpace),
      IS_OCCUPIED(PoiRecord::isOccupied),
      ANY(var0 -> true);

      private final Predicate<? super PoiRecord> test;

      private Occupancy(final Predicate<? super PoiRecord> nullxx) {
         this.test = nullxx;
      }

      public Predicate<? super PoiRecord> getTest() {
         return this.test;
      }
   }
}
