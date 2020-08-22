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

public class PoiManager extends SectionStorage {
   private final PoiManager.DistanceTracker distanceTracker = new PoiManager.DistanceTracker();
   private final LongSet loadedChunks = new LongOpenHashSet();

   public PoiManager(File var1, DataFixer var2) {
      super(var1, PoiSection::new, PoiSection::new, var2, DataFixTypes.POI_CHUNK);
   }

   public void add(BlockPos var1, PoiType var2) {
      ((PoiSection)this.getOrCreate(SectionPos.of(var1).asLong())).add(var1, var2);
   }

   public void remove(BlockPos var1) {
      ((PoiSection)this.getOrCreate(SectionPos.of(var1).asLong())).remove(var1);
   }

   public long getCountInRange(Predicate var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return this.getInRange(var1, var2, var3, var4).count();
   }

   public Stream getInSquare(Predicate var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      return ChunkPos.rangeClosed(new ChunkPos(var2), Math.floorDiv(var3, 16)).flatMap((var3x) -> {
         return this.getInChunk(var1, var3x, var4);
      });
   }

   public Stream getInRange(Predicate var1, BlockPos var2, int var3, PoiManager.Occupancy var4) {
      int var5 = var3 * var3;
      return this.getInSquare(var1, var2, var3, var4).filter((var2x) -> {
         return var2x.getPos().distSqr(var2) <= (double)var5;
      });
   }

   public Stream getInChunk(Predicate var1, ChunkPos var2, PoiManager.Occupancy var3) {
      return IntStream.range(0, 16).boxed().flatMap((var4) -> {
         return this.getInSection(var1, SectionPos.of(var2, var4).asLong(), var3);
      });
   }

   private Stream getInSection(Predicate var1, long var2, PoiManager.Occupancy var4) {
      return (Stream)this.getOrLoad(var2).map((var2x) -> {
         return var2x.getRecords(var1, var4);
      }).orElseGet(Stream::empty);
   }

   public Stream findAll(Predicate var1, Predicate var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.getInRange(var1, var3, var4, var5).map(PoiRecord::getPos).filter(var2);
   }

   public Optional find(Predicate var1, Predicate var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.findAll(var1, var2, var3, var4, var5).findFirst();
   }

   public Optional findClosest(Predicate var1, Predicate var2, BlockPos var3, int var4, PoiManager.Occupancy var5) {
      return this.getInRange(var1, var3, var4, var5).map(PoiRecord::getPos).sorted(Comparator.comparingDouble((var1x) -> {
         return var1x.distSqr(var3);
      })).filter(var2).findFirst();
   }

   public Optional take(Predicate var1, Predicate var2, BlockPos var3, int var4) {
      return this.getInRange(var1, var3, var4, PoiManager.Occupancy.HAS_SPACE).filter((var1x) -> {
         return var2.test(var1x.getPos());
      }).findFirst().map((var0) -> {
         var0.acquireTicket();
         return var0.getPos();
      });
   }

   public Optional getRandom(Predicate var1, Predicate var2, PoiManager.Occupancy var3, BlockPos var4, int var5, Random var6) {
      List var7 = (List)this.getInRange(var1, var4, var5, var3).collect(Collectors.toList());
      Collections.shuffle(var7, var6);
      return var7.stream().filter((var1x) -> {
         return var2.test(var1x.getPos());
      }).findFirst().map(PoiRecord::getPos);
   }

   public boolean release(BlockPos var1) {
      return ((PoiSection)this.getOrCreate(SectionPos.of(var1).asLong())).release(var1);
   }

   public boolean exists(BlockPos var1, Predicate var2) {
      return (Boolean)this.getOrLoad(SectionPos.of(var1).asLong()).map((var2x) -> {
         return var2x.exists(var1, var2);
      }).orElse(false);
   }

   public Optional getType(BlockPos var1) {
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
      Stream var10000 = PoiType.allPoiStates();
      var0.getClass();
      return var10000.anyMatch(var0::maybeHas);
   }

   private void updateFromSection(LevelChunkSection var1, SectionPos var2, BiConsumer var3) {
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
         super.runUpdates(Integer.MAX_VALUE);
      }
   }

   public static enum Occupancy {
      HAS_SPACE(PoiRecord::hasSpace),
      IS_OCCUPIED(PoiRecord::isOccupied),
      ANY((var0) -> {
         return true;
      });

      private final Predicate test;

      private Occupancy(Predicate var3) {
         this.test = var3;
      }

      public Predicate getTest() {
         return this.test;
      }
   }
}
