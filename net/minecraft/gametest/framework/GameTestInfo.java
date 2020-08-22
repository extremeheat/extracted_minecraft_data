package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap.Entry;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.StructureBlockEntity;

public class GameTestInfo {
   private final TestFunction testFunction;
   private BlockPos testPos;
   private final ServerLevel level;
   private final Collection listeners;
   private final int timeoutTicks;
   private final Collection sequences;
   private Object2LongMap runAtTickTimeMap;
   private long startTick;
   private long tickCount;
   private boolean started;
   private final Stopwatch timer;
   private boolean done;
   @Nullable
   private Throwable error;

   public GameTestInfo(TestFunction var1, ServerLevel var2) {
      this.listeners = Lists.newArrayList();
      this.sequences = Lists.newCopyOnWriteArrayList();
      this.runAtTickTimeMap = new Object2LongOpenHashMap();
      this.started = false;
      this.timer = Stopwatch.createUnstarted();
      this.done = false;
      this.testFunction = var1;
      this.level = var2;
      this.timeoutTicks = var1.getMaxTicks();
   }

   public GameTestInfo(TestFunction var1, BlockPos var2, ServerLevel var3) {
      this(var1, var3);
      this.assignPosition(var2);
   }

   void assignPosition(BlockPos var1) {
      this.testPos = var1;
   }

   void startExecution() {
      this.startTick = this.level.getGameTime() + 1L + this.testFunction.getSetupTicks();
      this.timer.start();
   }

   public void tick() {
      if (!this.isDone()) {
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

   public String getTestName() {
      return this.testFunction.getTestName();
   }

   public BlockPos getTestPos() {
      return this.testPos;
   }

   @Nullable
   public BlockPos getStructureSize() {
      StructureBlockEntity var1 = this.getStructureBlockEntity();
      return var1 == null ? null : var1.getStructureSize();
   }

   @Nullable
   private StructureBlockEntity getStructureBlockEntity() {
      return (StructureBlockEntity)this.level.getBlockEntity(this.testPos);
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

   private void finish() {
      if (!this.done) {
         this.done = true;
         this.timer.stop();
      }

   }

   public void fail(Throwable var1) {
      this.finish();
      this.error = var1;
      this.listeners.forEach((var1x) -> {
         var1x.testFailed(this);
      });
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

   public void spawnStructure(int var1) {
      StructureBlockEntity var2 = StructureUtils.spawnStructure(this.testFunction.getStructureName(), this.testPos, var1, this.level, false);
      var2.setStructureName(this.getTestName());
      StructureUtils.addCommandBlockAndButtonToStartTest(this.testPos.offset(1, 0, -1), this.level);
      this.listeners.forEach((var1x) -> {
         var1x.testStructureLoaded(this);
      });
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
}
