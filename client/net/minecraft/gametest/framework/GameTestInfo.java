package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class GameTestInfo {
   private final TestFunction testFunction;
   @Nullable
   private BlockPos structureBlockPos;
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
   private boolean rerunUntilFailed;
   private final Stopwatch timer = Stopwatch.createUnstarted();
   private boolean done;
   private final Rotation rotation;
   @Nullable
   private Throwable error;
   @Nullable
   private StructureBlockEntity structureBlockEntity;

   public GameTestInfo(TestFunction var1, Rotation var2, ServerLevel var3) {
      super();
      this.testFunction = var1;
      this.level = var3;
      this.timeoutTicks = var1.getMaxTicks();
      this.rotation = var1.getRotation().getRotated(var2);
   }

   void setStructureBlockPos(BlockPos var1) {
      this.structureBlockPos = var1;
   }

   void startExecution() {
      this.startTick = this.level.getGameTime() + this.testFunction.getSetupTicks();
      this.timer.start();
   }

   public void tick() {
      if (!this.isDone()) {
         if (this.structureBlockEntity == null) {
            this.fail(new IllegalStateException("Running test without structure block entity"));
         }

         if (this.chunksLoaded
            || StructureUtils.getStructureBoundingBox(this.structureBlockEntity)
               .intersectingChunks()
               .allMatch(var1x -> this.level.isPositionEntityTicking(var1x.getWorldPosition()))) {
            this.chunksLoaded = true;
            if (this.ticksToWaitForChunkLoading > 0) {
               --this.ticksToWaitForChunkLoading;
            } else {
               if (!this.placedStructure) {
                  this.placedStructure = true;
                  this.structureBlockEntity.placeStructure(this.level);
                  BoundingBox var1 = StructureUtils.getStructureBoundingBox(this.structureBlockEntity);
                  this.level.getBlockTicks().clearArea(var1);
                  this.level.clearBlockEvents(var1);
                  this.startExecution();
               }

               this.tickInternal();
               if (this.isDone()) {
                  if (this.error != null) {
                     this.listeners.forEach(var1x -> var1x.testFailed(this));
                  } else {
                     this.listeners.forEach(var1x -> var1x.testPassed(this));
                  }
               }
            }
         }
      }
   }

   private void tickInternal() {
      this.tickCount = this.level.getGameTime() - this.startTick;
      if (this.tickCount >= 0L) {
         if (this.tickCount == 0L) {
            this.startTest();
         }

         ObjectIterator var1 = this.runAtTickTimeMap.object2LongEntrySet().iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
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
               this.fail(new GameTestTimeoutException("Didn't succeed or fail within " + this.testFunction.getMaxTicks() + " ticks"));
            } else {
               this.sequences.forEach(var1x -> var1x.tickAndFailIfNotComplete(this.tickCount));
               if (this.error == null) {
                  this.fail(new GameTestTimeoutException("No sequences finished"));
               }
            }
         } else {
            this.sequences.forEach(var1x -> var1x.tickAndContinue(this.tickCount));
         }
      }
   }

   private void startTest() {
      if (this.started) {
         throw new IllegalStateException("Test already started");
      } else {
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
      return this.testFunction.getTestName();
   }

   public BlockPos getStructureBlockPos() {
      return this.structureBlockPos;
   }

   @Nullable
   public BlockPos getStructureOrigin() {
      StructureBlockEntity var1 = this.getStructureBlockEntity();
      return var1 == null ? null : StructureUtils.getStructureOrigin(var1);
   }

   @Nullable
   public Vec3i getStructureSize() {
      StructureBlockEntity var1 = this.getStructureBlockEntity();
      return var1 == null ? null : var1.getStructureSize();
   }

   @Nullable
   public AABB getStructureBounds() {
      StructureBlockEntity var1 = this.getStructureBlockEntity();
      return var1 == null ? null : StructureUtils.getStructureBounds(var1);
   }

   @Nullable
   private StructureBlockEntity getStructureBlockEntity() {
      return (StructureBlockEntity)this.level.getBlockEntity(this.structureBlockPos);
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
         List var2 = this.getLevel().getEntitiesOfClass(Entity.class, var1.inflate(1.0), var0 -> !(var0 instanceof Player));
         var2.forEach(var0 -> var0.remove(Entity.RemovalReason.DISCARDED));
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

   @Override
   public String toString() {
      return this.getTestName();
   }

   public void addListener(GameTestListener var1) {
      this.listeners.add(var1);
   }

   public void prepareTestStructure(BlockPos var1) {
      this.structureBlockEntity = StructureUtils.prepareTestStructure(this, var1, this.getRotation(), this.level);
      this.structureBlockPos = this.structureBlockEntity.getBlockPos();
      StructureUtils.addCommandBlockAndButtonToStartTest(this.structureBlockPos, new BlockPos(1, 0, -1), this.getRotation(), this.level);
      this.listeners.forEach(var1x -> var1x.testStructureLoaded(this));
   }

   public void clearStructure() {
      if (this.structureBlockEntity == null) {
         throw new IllegalStateException("Expected structure to be initialized, but it was null");
      } else {
         BoundingBox var1 = StructureUtils.getStructureBoundingBox(this.structureBlockEntity);
         StructureUtils.clearSpaceForStructure(var1, this.level);
      }
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
      return this.testFunction.isRequired();
   }

   public boolean isOptional() {
      return !this.testFunction.isRequired();
   }

   public String getStructureName() {
      return this.testFunction.getStructureName();
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
      return this.testFunction.getMaxAttempts();
   }

   public int requiredSuccesses() {
      return this.testFunction.getRequiredSuccesses();
   }

   public void setRerunUntilFailed(boolean var1) {
      this.rerunUntilFailed = var1;
   }

   public boolean rerunUntilFailed() {
      return this.rerunUntilFailed;
   }
}
