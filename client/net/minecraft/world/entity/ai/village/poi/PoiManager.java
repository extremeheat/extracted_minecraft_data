package net.minecraft.world.entity.ai.village.poi;

import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ByteMap;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.SectionTracker;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.storage.SectionStorage;

public class PoiManager extends SectionStorage<PoiSection> {
   private final PoiManager.DistanceTracker distanceTracker = new PoiManager.DistanceTracker();
   private final LongSet loadedChunks = new LongOpenHashSet();

   public PoiManager(File var1, DataFixer var2, boolean var3) {
      super(var1, PoiSection::codec, PoiSection::new, var2, DataFixTypes.POI_CHUNK, var3);
   }

   public void add(BlockPos var1, PoiType var2) {
      ((PoiSection)this.getOrCreate(SectionPos.of(var1).asLong())).add(var1, var2);
   }

   public void remove(BlockPos var1) {
      ((PoiSection)this.getOrCreate(SectionPos.of(var1).asLong())).remove(var1);
   }

   public long getCountInRange(Predicate<PoiType> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return this.getInRange(var1, var2, var3, var4).count();
   }

   public boolean existsAtPosition(PoiType var1, BlockPos var2) {
      Optional var3 = ((PoiSection)this.getOrCreate(SectionPos.of(var2).asLong())).getType(var2);
      return var3.isPresent() && ((PoiType)var3.get()).equals(var1);
   }

   public Stream<PoiRecord> getInSquare(Predicate<PoiType> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      int var5 = Math.floorDiv(var3, 16) + 1;
      return ChunkPos.rangeClosed(new ChunkPos(var2), var5).flatMap((var3x) -> {
         return this.getInChunk(var1, var3x, var4);
      }).filter((var2x) -> {
         BlockPos var3x = var2x.getPos();
         return Math.abs(var3x.getX() - var2.getX()) <= var3 && Math.abs(var3x.getZ() - var2.getZ()) <= var3;
      });
   }

   public Stream<PoiRecord> getInRange(Predicate<PoiType> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      int var5 = var3 * var3;
      return this.getInSquare(var1, var2, var3, var4).filter((var2x) -> {
         return var2x.getPos().distSqr(var2) <= (double)var5;
      });
   }

   public Stream<PoiRecord> getInChunk(Predicate<PoiType> var1, ChunkPos var2, PoiManager.Occupancy var3) {
      return IntStream.range(0, 16).boxed().map((var2x) -> {
         return this.getOrLoad(SectionPos.of(var2, var2x).asLong());
      }).filter(Optional::isPresent).flatMap((var2x) -> {
         return ((PoiSection)var2x.get()).getRecords(var1, var3);
      });
   }

   public Stream<BlockPos> findAll(Predicate<PoiType> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.getInRange(var1, var3, var4, var5).map(PoiRecord::getPos).filter(var2);
   }

   public Stream<BlockPos> findAllClosestFirst(Predicate<PoiType> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.findAll(var1, var2, var3, var4, var5).sorted(Comparator.comparingDouble((var1x) -> {
         return var1x.distSqr(var3);
      }));
   }

   public Optional<BlockPos> find(Predicate<PoiType> var1, Predicate<BlockPos> var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.findAll(var1, var2, var3, var4, var5).findFirst();
   }

   public Optional<BlockPos> findClosest(Predicate<PoiType> var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return this.getInRange(var1, var2, var3, var4).map(PoiRecord::getPos).min(Comparator.comparingDouble((var1x) -> {
         return var1x.distSqr(var2);
      }));
   }

   public Optional<BlockPos> take(Predicate<PoiType> var1, Predicate<BlockPos> var2, BlockPos var3, int var4) {
      return this.getInRange(var1, var3, var4, PoiManager.Occupancy.HAS_SPACE).filter((var1x) -> {
         return var2.test(var1x.getPos());
      }).findFirst().map((var0) -> {
         var0.acquireTicket();
         return var0.getPos();
      });
   }

   public Optional<BlockPos> getRandom(Predicate<PoiType> var1, Predicate<BlockPos> var2, PoiManager.Occupancy var3, BlockPos var4, int var5, Random var6) {
      List var7 = (List)this.getInRange(var1, var4, var5, var3).collect(Collectors.toList());
      Collections.shuffle(var7, var6);
      return var7.stream().filter((var1x) -> {
         return var2.test(var1x.getPos());
      }).findFirst().map(PoiRecord::getPos);
   }

   public boolean release(BlockPos var1) {
      return ((PoiSection)this.getOrCreate(SectionPos.of(var1).asLong())).release(var1);
   }

   public boolean exists(BlockPos var1, Predicate<PoiType> var2) {
      return (Boolean)this.getOrLoad(SectionPos.of(var1).asLong()).map((var2x) -> {
         return var2x.exists(var1, var2);
      }).orElse(false);
   }

   public Optional<PoiType> getType(BlockPos var1) {
      PoiSection var2 = (PoiSection)this.getOrCreate(SectionPos.of(var1).asLong());
      return var2.getType(var1);
   }

   public int sectionsToVillage(SectionPos var1) {
      this.distanceTracker.runAllUpdates();
      return this.distanceTracker.getLevel(var1.asLong());
   }

   private boolean isVillageCenter(long var1) {
      Optional var3 = this.get(var1);
      return var3 == null ? false : (Boolean)var3.map((var0) -> {
         return var0.getRecords(PoiType.ALL, PoiManager.Occupancy.IS_OCCUPIED).count() > 0L;
      }).orElse(false);
   }

   public void tick(BooleanSupplier var1) {
      super.tick(var1);
      this.distanceTracker.runAllUpdates();
   }

   protected void setDirty(long var1) {
      super.setDirty(var1);
      this.distanceTracker.update(var1, this.distanceTracker.getLevelFromSource(var1), false);
   }

   protected void onSectionLoad(long var1) {
      this.distanceTracker.update(var1, this.distanceTracker.getLevelFromSource(var1), false);
   }

   public void checkConsistencyWithBlocks(ChunkPos var1, LevelChunkSection var2) {
      SectionPos var3 = SectionPos.of(var1, var2.bottomBlockY() >> 4);
      Util.ifElse(this.getOrLoad(var3.asLong()), (var3x) -> {
         var3x.refresh((var3xx) -> {
            if (mayHavePoi(var2)) {
               this.updateFromSection(var2, var3, var3xx);
            }

         });
      }, () -> {
         if (mayHavePoi(var2)) {
            PoiSection var3x = (PoiSection)this.getOrCreate(var3.asLong());
            this.updateFromSection(var2, var3, var3x::add);
         }

      });
   }

   private static boolean mayHavePoi(LevelChunkSection var0) {
      Set var10001 = PoiType.ALL_STATES;
      var10001.getClass();
      return var0.maybeHas(var10001::contains);
   }

   private void updateFromSection(LevelChunkSection var1, SectionPos var2, BiConsumer<BlockPos, PoiType> var3) {
      var2.blocksInside().forEach((var2x) -> {
         BlockState var3x = var1.getBlockState(SectionPos.sectionRelative(var2x.getX()), SectionPos.sectionRelative(var2x.getY()), SectionPos.sectionRelative(var2x.getZ()));
         PoiType.forState(var3x).ifPresent((var2) -> {
            var3.accept(var2x, var2);
         });
      });
   }

   public void ensureLoadedAndValid(LevelReader var1, BlockPos var2, int var3) {
      SectionPos.aroundChunk(new ChunkPos(var2), Math.floorDiv(var3, 16)).map((var1x) -> {
         return Pair.of(var1x, this.getOrLoad(var1x.asLong()));
      }).filter((var0) -> {
         return !(Boolean)((Optional)var0.getSecond()).map(PoiSection::isValid).orElse(false);
      }).map((var0) -> {
         return ((SectionPos)var0.getFirst()).chunk();
      }).filter((var1x) -> {
         return this.loadedChunks.add(var1x.toLong());
      }).forEach((var1x) -> {
         var1.getChunk(var1x.x, var1x.z, ChunkStatus.EMPTY);
      });
   }

   final class DistanceTracker extends SectionTracker {
      private final Long2ByteMap levels = new Long2ByteOpenHashMap();

      protected DistanceTracker() {
         super(7, 16, 256);
         this.levels.defaultReturnValue((byte)7);
      }

      protected int getLevelFromSource(long var1) {
         return PoiManager.this.isVillageCenter(var1) ? 0 : 7;
      }

      protected int getLevel(long var1) {
         return this.levels.get(var1);
      }

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
      ANY((var0) -> {
         return true;
      });

      private final Predicate<? super PoiRecord> test;

      private Occupancy(Predicate<? super PoiRecord> var3) {
         this.test = var3;
      }

      public Predicate<? super PoiRecord> getTest() {
         return this.test;
      }
   }
}
