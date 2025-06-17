package net.minecraft.gametest.framework;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class GameTestInfo {
   private final Holder.Reference<GameTestInstance> test;
   @Nullable
   private BlockPos testBlockPos;
   private final ServerLevel level;
   private final Collection<GameTestListener> listeners = Lists.newArrayList();
   private final int timeoutTicks;
   private final Collection<GameTestSequence> sequences = Lists.newCopyOnWriteArrayList();
   private final Object2LongMap<Runnable> runAtTickTimeMap = new Object2LongOpenHashMap();
   private boolean placedStructure;
   private boolean chunksLoaded;
   private int tickCount;
   private boolean started;
   private final RetryOptions retryOptions;
   private final Stopwatch timer = Stopwatch.createUnstarted();
   private boolean done;
   private final Rotation extraRotation;
   @Nullable
   private GameTestException error;
   @Nullable
   private TestInstanceBlockEntity testInstanceBlockEntity;

   public GameTestInfo(Holder.Reference<GameTestInstance> var1, Rotation var2, ServerLevel var3, RetryOptions var4) {
      super();
      this.test = var1;
      this.level = var3;
      this.retryOptions = var4;
      this.timeoutTicks = ((GameTestInstance)var1.value()).maxTicks();
      this.extraRotation = var2;
   }

   public void setTestBlockPos(@Nullable BlockPos var1) {
      this.testBlockPos = var1;
   }

   public GameTestInfo startExecution(int var1) {
      this.tickCount = -(((GameTestInstance)this.test.value()).setupTicks() + var1 + 1);
      return this;
   }

   public void placeStructure() {
      if (!this.placedStructure) {
         TestInstanceBlockEntity var1 = this.getTestInstanceBlockEntity();
         if (!var1.placeStructure()) {
            this.fail((Component)Component.translatable("test.error.structure.failure", var1.getTestName().getString()));
         }

         this.placedStructure = true;
         var1.encaseStructure();
         BoundingBox var2 = var1.getStructureBoundingBox();
         this.level.getBlockTicks().clearArea(var2);
         this.level.clearBlockEvents(var2);
         this.listeners.forEach((var1x) -> var1x.testStructureLoaded(this));
      }
   }

   public void tick(GameTestRunner var1) {
      if (!this.isDone()) {
         if (!this.placedStructure) {
            this.fail((Component)Component.translatable("test.error.ticking_without_structure"));
         }

         if (this.testInstanceBlockEntity == null) {
            this.fail((Component)Component.translatable("test.error.missing_block_entity"));
         }

         if (this.error != null) {
            this.finish();
         }

         if (!this.chunksLoaded) {
            Stream var10000 = this.testInstanceBlockEntity.getStructureBoundingBox().intersectingChunks();
            ServerLevel var10001 = this.level;
            Objects.requireNonNull(var10001);
            if (!var10000.allMatch(var10001::areEntitiesActuallyLoadedAndTicking)) {
               return;
            }
         }

         this.chunksLoaded = true;
         this.tickInternal();
         if (this.isDone()) {
            if (this.error != null) {
               this.listeners.forEach((var2) -> var2.testFailed(this, var1));
            } else {
               this.listeners.forEach((var2) -> var2.testPassed(this, var1));
            }
         }

      }
   }

   private void tickInternal() {
      ++this.tickCount;
      if (this.tickCount >= 0) {
         if (!this.started) {
            this.startTest();
         }

         ObjectIterator var1 = this.runAtTickTimeMap.object2LongEntrySet().iterator();

         while(var1.hasNext()) {
            Object2LongMap.Entry var2 = (Object2LongMap.Entry)var1.next();
            if (var2.getLongValue() <= (long)this.tickCount) {
               try {
                  ((Runnable)var2.getKey()).run();
               } catch (GameTestException var4) {
                  this.fail(var4);
               } catch (Exception var5) {
                  this.fail((GameTestException)(new UnknownGameTestException(var5)));
               }

               var1.remove();
            }
         }

         if (this.tickCount > this.timeoutTicks) {
            if (this.sequences.isEmpty()) {
               this.fail((GameTestException)(new GameTestTimeoutException(Component.translatable("test.error.timeout.no_result", ((GameTestInstance)this.test.value()).maxTicks()))));
            } else {
               this.sequences.forEach((var1x) -> var1x.tickAndFailIfNotComplete(this.tickCount));
               if (this.error == null) {
                  this.fail((GameTestException)(new GameTestTimeoutException(Component.translatable("test.error.timeout.no_sequences_finished", ((GameTestInstance)this.test.value()).maxTicks()))));
               }
            }
         } else {
            this.sequences.forEach((var1x) -> var1x.tickAndContinue(this.tickCount));
         }

      }
   }

   private void startTest() {
      if (!this.started) {
         this.started = true;
         this.getTestInstanceBlockEntity().setRunning();

         try {
            ((GameTestInstance)this.test.value()).run(new GameTestHelper(this));
         } catch (GameTestException var2) {
            this.fail(var2);
         } catch (Exception var3) {
            this.fail((GameTestException)(new UnknownGameTestException(var3)));
         }

      }
   }

   public void setRunAtTickTime(long var1, Runnable var3) {
      this.runAtTickTimeMap.put(var3, var1);
   }

   public ResourceLocation id() {
      return this.test.key().location();
   }

   @Nullable
   public BlockPos getTestBlockPos() {
      return this.testBlockPos;
   }

   public BlockPos getTestOrigin() {
      return this.testInstanceBlockEntity.getStartCorner();
   }

   public AABB getStructureBounds() {
      TestInstanceBlockEntity var1 = this.getTestInstanceBlockEntity();
      return var1.getStructureBounds();
   }

   public TestInstanceBlockEntity getTestInstanceBlockEntity() {
      if (this.testInstanceBlockEntity == null) {
         if (this.testBlockPos == null) {
            throw new IllegalStateException("This GameTestInfo has no position");
         }

         BlockEntity var2 = this.level.getBlockEntity(this.testBlockPos);
         if (var2 instanceof TestInstanceBlockEntity) {
            TestInstanceBlockEntity var1 = (TestInstanceBlockEntity)var2;
            this.testInstanceBlockEntity = var1;
         }

         if (this.testInstanceBlockEntity == null) {
            throw new IllegalStateException("Could not find a test instance block entity at the given coordinate " + String.valueOf(this.testBlockPos));
         }
      }

      return this.testInstanceBlockEntity;
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
         List var2 = this.getLevel().getEntitiesOfClass(Entity.class, var1.inflate(1.0), (var0) -> !(var0 instanceof Player));
         var2.forEach((var0) -> var0.remove(Entity.RemovalReason.DISCARDED));
      }

   }

   public void fail(Component var1) {
      this.fail((GameTestException)(new GameTestAssertException(var1, this.tickCount)));
   }

   public void fail(GameTestException var1) {
      this.error = var1;
   }

   @Nullable
   public GameTestException getError() {
      return this.error;
   }

   public String toString() {
      return this.id().toString();
   }

   public void addListener(GameTestListener var1) {
      this.listeners.add(var1);
   }

   @Nullable
   public GameTestInfo prepareTestStructure() {
      TestInstanceBlockEntity var1 = this.createTestInstanceBlock((BlockPos)Objects.requireNonNull(this.testBlockPos), this.extraRotation, this.level);
      if (var1 != null) {
         this.testInstanceBlockEntity = var1;
         this.placeStructure();
         return this;
      } else {
         return null;
      }
   }

   @Nullable
   private TestInstanceBlockEntity createTestInstanceBlock(BlockPos var1, Rotation var2, ServerLevel var3) {
      var3.setBlockAndUpdate(var1, Blocks.TEST_INSTANCE_BLOCK.defaultBlockState());
      BlockEntity var5 = var3.getBlockEntity(var1);
      if (var5 instanceof TestInstanceBlockEntity var4) {
         ResourceKey var7 = this.getTestHolder().key();
         Vec3i var6 = (Vec3i)TestInstanceBlockEntity.getStructureSize(var3, var7).orElse(new Vec3i(1, 1, 1));
         var4.set(new TestInstanceBlockEntity.Data(Optional.of(var7), var6, var2, false, TestInstanceBlockEntity.Status.CLEARED, Optional.empty()));
         return var4;
      } else {
         return null;
      }
   }

   int getTick() {
      return this.tickCount;
   }

   GameTestSequence createSequence() {
      GameTestSequence var1 = new GameTestSequence(this);
      this.sequences.add(var1);
      return var1;
   }

   public boolean isRequired() {
      return ((GameTestInstance)this.test.value()).required();
   }

   public boolean isOptional() {
      return !((GameTestInstance)this.test.value()).required();
   }

   public ResourceLocation getStructure() {
      return ((GameTestInstance)this.test.value()).structure();
   }

   public Rotation getRotation() {
      return ((GameTestInstance)this.test.value()).info().rotation().getRotated(this.extraRotation);
   }

   public GameTestInstance getTest() {
      return this.test.value();
   }

   public Holder.Reference<GameTestInstance> getTestHolder() {
      return this.test;
   }

   public int getTimeoutTicks() {
      return this.timeoutTicks;
   }

   public boolean isFlaky() {
      return ((GameTestInstance)this.test.value()).maxAttempts() > 1;
   }

   public int maxAttempts() {
      return ((GameTestInstance)this.test.value()).maxAttempts();
   }

   public int requiredSuccesses() {
      return ((GameTestInstance)this.test.value()).requiredSuccesses();
   }

   public RetryOptions retryOptions() {
      return this.retryOptions;
   }

   public Stream<GameTestListener> getListeners() {
      return this.listeners.stream();
   }

   public GameTestInfo copyReset() {
      GameTestInfo var1 = new GameTestInfo(this.test, this.extraRotation, this.level, this.retryOptions());
      if (this.testBlockPos != null) {
         var1.setTestBlockPos(this.testBlockPos);
      }

      return var1;
   }
}
