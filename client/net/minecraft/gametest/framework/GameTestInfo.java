package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class GameTestInfo {
   private final TestFunction testFunction;
   @Nullable
   private BlockPos structureBlockPos;
   @Nullable
   private BlockPos northWestCorner;
   private final ServerLevel level;
   private final Collection<GameTestListener> listeners = Lists.newArrayList();
   private final int timeoutTicks;
   private final Collection<GameTestSequence> sequences = Lists.newCopyOnWriteArrayList();
   private final Object2LongMap<Runnable> runAtTickTimeMap = new Object2LongOpenHashMap();
   private long startTick;
   private int ticksToWaitForChunkLoading = 20;
   private boolean placedStructure;
   private boolean chunksLoaded;
   private long tickCount;
   private boolean started;
   private final RetryOptions retryOptions;
   private final Stopwatch timer = Stopwatch.createUnstarted();
   private boolean done;
   private final Rotation rotation;
   @Nullable
   private Throwable error;
   @Nullable
   private StructureBlockEntity structureBlockEntity;

   public GameTestInfo(TestFunction var1, Rotation var2, ServerLevel var3, RetryOptions var4) {
      super();
      this.testFunction = var1;
      this.level = var3;
      this.retryOptions = var4;
      this.timeoutTicks = var1.maxTicks();
      this.rotation = var1.rotation().getRotated(var2);
   }

   void setStructureBlockPos(BlockPos var1) {
      this.structureBlockPos = var1;
   }

   public GameTestInfo startExecution(int var1) {
      this.startTick = this.level.getGameTime() + this.testFunction.setupTicks() + (long)var1;
      this.timer.start();
      return this;
   }

   public GameTestInfo placeStructure() {
      if (this.placedStructure) {
         return this;
      } else {
         this.ticksToWaitForChunkLoading = 0;
         this.placedStructure = true;
         StructureBlockEntity var1 = this.getStructureBlockEntity();
         var1.placeStructure(this.level);
         BoundingBox var2 = StructureUtils.getStructureBoundingBox(var1);
         this.level.getBlockTicks().clearArea(var2);
         this.level.clearBlockEvents(var2);
         return this;
      }
   }

   private boolean ensureStructureIsPlaced() {
      if (this.placedStructure) {
         return true;
      } else if (this.ticksToWaitForChunkLoading > 0) {
         --this.ticksToWaitForChunkLoading;
         return false;
      } else {
         this.placeStructure().startExecution(0);
         return true;
      }
   }

   public void tick(GameTestRunner var1) {
      if (!this.isDone()) {
         if (this.structureBlockEntity == null) {
            this.fail(new IllegalStateException("Running test without structure block entity"));
         }

         if (this.chunksLoaded || StructureUtils.getStructureBoundingBox(this.structureBlockEntity).intersectingChunks().allMatch((var1x) -> {
            return this.level.isPositionEntityTicking(var1x.getWorldPosition());
         })) {
            this.chunksLoaded = true;
            if (this.ensureStructureIsPlaced()) {
               this.tickInternal();
               if (this.isDone()) {
                  if (this.error != null) {
                     this.listeners.forEach((var2) -> {
                        var2.testFailed(this, var1);
                     });
                  } else {
                     this.listeners.forEach((var2) -> {
                        var2.testPassed(this, var1);
                     });
                  }
               }

            }
         }
      }
   }

   private void tickInternal() {
      this.tickCount = this.level.getGameTime() - this.startTick;
      if (this.tickCount >= 0L) {
         if (!this.started) {
            this.startTest();
         }

         ObjectIterator var1 = this.runAtTickTimeMap.object2LongEntrySet().iterator();

         while(var1.hasNext()) {
            Object2LongMap.Entry var2 = (Object2LongMap.Entry)var1.next();
            if (var2.getLongValue() <= this.tickCount) {
               try {
                  ((Runnable)var2.getKey()).run();
               } catch (Exception var4) {
                  this.fail(var4);
               }

               var1.remove();
            }
         }

         if (this.tickCount > (long)this.timeoutTicks) {
            if (this.sequences.isEmpty()) {
               this.fail(new GameTestTimeoutException("Didn't succeed or fail within " + this.testFunction.maxTicks() + " ticks"));
            } else {
               this.sequences.forEach((var1x) -> {
                  var1x.tickAndFailIfNotComplete(this.tickCount);
               });
               if (this.error == null) {
                  this.fail(new GameTestTimeoutException("No sequences finished"));
               }
            }
         } else {
            this.sequences.forEach((var1x) -> {
               var1x.tickAndContinue(this.tickCount);
            });
         }

      }
   }

   private void startTest() {
      if (!this.started) {
         this.started = true;

         try {
            this.testFunction.run(new GameTestHelper(this));
         } catch (Exception var2) {
            this.fail(var2);
         }

      }
   }

   public void setRunAtTickTime(long var1, Runnable var3) {
      this.runAtTickTimeMap.put(var3, var1);
   }

   public String getTestName() {
      return this.testFunction.testName();
   }

   @Nullable
   public BlockPos getStructureBlockPos() {
      return this.structureBlockPos;
   }

   public BlockPos getTestOrigin() {
      return StructureUtils.getStructureOrigin(this.structureBlockEntity);
   }

   public AABB getStructureBounds() {
      StructureBlockEntity var1 = this.getStructureBlockEntity();
      return StructureUtils.getStructureBounds(var1);
   }

   public StructureBlockEntity getStructureBlockEntity() {
      if (this.structureBlockEntity == null) {
         if (this.structureBlockPos == null) {
            throw new IllegalStateException("Could not find a structureBlockEntity for this GameTestInfo");
         }

         this.structureBlockEntity = (StructureBlockEntity)this.level.getBlockEntity(this.structureBlockPos);
         if (this.structureBlockEntity == null) {
            throw new IllegalStateException("Could not find a structureBlockEntity at the given coordinate " + String.valueOf(this.structureBlockPos));
         }
      }

      return this.structureBlockEntity;
   }

   public ServerLevel getLevel() {
      return this.level;
   }

   public boolean hasSucceeded() {
      return this.done && this.error == null;
   }

   public boolean hasFailed() {
      return this.error != null;
   }

   public boolean hasStarted() {
      return this.started;
   }

   public boolean isDone() {
      return this.done;
   }

   public long getRunTime() {
      return this.timer.elapsed(TimeUnit.MILLISECONDS);
   }

   private void finish() {
      if (!this.done) {
         this.done = true;
         if (this.timer.isRunning()) {
            this.timer.stop();
         }
      }

   }

   public void succeed() {
      if (this.error == null) {
         this.finish();
         AABB var1 = this.getStructureBounds();
         List var2 = this.getLevel().getEntitiesOfClass(Entity.class, var1.inflate(1.0), (var0) -> {
            return !(var0 instanceof Player);
         });
         var2.forEach((var0) -> {
            var0.remove(Entity.RemovalReason.DISCARDED);
         });
      }

   }

   public void fail(Throwable var1) {
      this.error = var1;
      this.finish();
   }

   @Nullable
   public Throwable getError() {
      return this.error;
   }

   public String toString() {
      return this.getTestName();
   }

   public void addListener(GameTestListener var1) {
      this.listeners.add(var1);
   }

   public GameTestInfo prepareTestStructure() {
      BlockPos var1 = this.getOrCalculateNorthwestCorner();
      this.structureBlockEntity = StructureUtils.prepareTestStructure(this, var1, this.getRotation(), this.level);
      this.structureBlockPos = this.structureBlockEntity.getBlockPos();
      StructureUtils.addCommandBlockAndButtonToStartTest(this.structureBlockPos, new BlockPos(1, 0, -1), this.getRotation(), this.level);
      StructureUtils.encaseStructure(this.getStructureBounds(), this.level, !this.testFunction.skyAccess());
      this.listeners.forEach((var1x) -> {
         var1x.testStructureLoaded(this);
      });
      return this;
   }

   long getTick() {
      return this.tickCount;
   }

   GameTestSequence createSequence() {
      GameTestSequence var1 = new GameTestSequence(this);
      this.sequences.add(var1);
      return var1;
   }

   public boolean isRequired() {
      return this.testFunction.required();
   }

   public boolean isOptional() {
      return !this.testFunction.required();
   }

   public String getStructureName() {
      return this.testFunction.structureName();
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public TestFunction getTestFunction() {
      return this.testFunction;
   }

   public int getTimeoutTicks() {
      return this.timeoutTicks;
   }

   public boolean isFlaky() {
      return this.testFunction.isFlaky();
   }

   public int maxAttempts() {
      return this.testFunction.maxAttempts();
   }

   public int requiredSuccesses() {
      return this.testFunction.requiredSuccesses();
   }

   public RetryOptions retryOptions() {
      return this.retryOptions;
   }

   public Stream<GameTestListener> getListeners() {
      return this.listeners.stream();
   }

   public GameTestInfo copyReset() {
      GameTestInfo var1 = new GameTestInfo(this.testFunction, this.rotation, this.level, this.retryOptions());
      if (this.northWestCorner != null) {
         var1.setNorthWestCorner(this.northWestCorner);
      }

      if (this.structureBlockPos != null) {
         var1.setStructureBlockPos(this.structureBlockPos);
      }

      return var1;
   }

   public BlockPos getOrCalculateNorthwestCorner() {
      if (this.northWestCorner == null) {
         BoundingBox var1 = StructureUtils.getStructureBoundingBox(this.getStructureBlockEntity());
         this.northWestCorner = new BlockPos(var1.minX(), var1.minY(), var1.minZ());
      }

      return this.northWestCorner;
   }

   public void setNorthWestCorner(BlockPos var1) {
      this.northWestCorner = var1;
   }
}
