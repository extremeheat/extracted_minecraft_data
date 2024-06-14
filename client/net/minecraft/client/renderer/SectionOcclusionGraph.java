package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;

public class SectionOcclusionGraph {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final int MINIMUM_ADVANCED_CULLING_DISTANCE = 60;
   private static final double CEILED_SECTION_DIAGONAL = Math.ceil(Math.sqrt(3.0) * 16.0);
   private boolean needsFullUpdate = true;
   @Nullable
   private Future<?> fullUpdateTask;
   @Nullable
   private ViewArea viewArea;
   private final AtomicReference<SectionOcclusionGraph.GraphState> currentGraph = new AtomicReference<>();
   private final AtomicReference<SectionOcclusionGraph.GraphEvents> nextGraphEvents = new AtomicReference<>();
   private final AtomicBoolean needsFrustumUpdate = new AtomicBoolean(false);

   public SectionOcclusionGraph() {
      super();
   }

   public void waitAndReset(@Nullable ViewArea var1) {
      if (this.fullUpdateTask != null) {
         try {
            this.fullUpdateTask.get();
            this.fullUpdateTask = null;
         } catch (Exception var3) {
            LOGGER.warn("Full update failed", var3);
         }
      }

      this.viewArea = var1;
      if (var1 != null) {
         this.currentGraph.set(new SectionOcclusionGraph.GraphState(var1.sections.length));
         this.invalidate();
      } else {
         this.currentGraph.set(null);
      }
   }

   public void invalidate() {
      this.needsFullUpdate = true;
   }

   public void addSectionsInFrustum(Frustum var1, List<SectionRenderDispatcher.RenderSection> var2) {
      for (SectionOcclusionGraph.Node var4 : this.currentGraph.get().storage().renderSections) {
         if (var1.isVisible(var4.section.getBoundingBox())) {
            var2.add(var4.section);
         }
      }
   }

   public boolean consumeFrustumUpdate() {
      return this.needsFrustumUpdate.compareAndSet(true, false);
   }

   public void onChunkLoaded(ChunkPos var1) {
      SectionOcclusionGraph.GraphEvents var2 = this.nextGraphEvents.get();
      if (var2 != null) {
         this.addNeighbors(var2, var1);
      }

      SectionOcclusionGraph.GraphEvents var3 = this.currentGraph.get().events;
      if (var3 != var2) {
         this.addNeighbors(var3, var1);
      }
   }

   public void onSectionCompiled(SectionRenderDispatcher.RenderSection var1) {
      SectionOcclusionGraph.GraphEvents var2 = this.nextGraphEvents.get();
      if (var2 != null) {
         var2.sectionsToPropagateFrom.add(var1);
      }

      SectionOcclusionGraph.GraphEvents var3 = this.currentGraph.get().events;
      if (var3 != var2) {
         var3.sectionsToPropagateFrom.add(var1);
      }
   }

   public void update(boolean var1, Camera var2, Frustum var3, List<SectionRenderDispatcher.RenderSection> var4) {
      Vec3 var5 = var2.getPosition();
      if (this.needsFullUpdate && (this.fullUpdateTask == null || this.fullUpdateTask.isDone())) {
         this.scheduleFullUpdate(var1, var2, var5);
      }

      this.runPartialUpdate(var1, var3, var4, var5);
   }

   private void scheduleFullUpdate(boolean var1, Camera var2, Vec3 var3) {
      this.needsFullUpdate = false;
      this.fullUpdateTask = Util.backgroundExecutor().submit(() -> {
         SectionOcclusionGraph.GraphState var4 = new SectionOcclusionGraph.GraphState(this.viewArea.sections.length);
         this.nextGraphEvents.set(var4.events);
         ArrayDeque var5 = Queues.newArrayDeque();
         this.initializeQueueForFullUpdate(var2, var5);
         var5.forEach(var1xx -> var4.storage.sectionToNodeMap.put(var1xx.section, var1xx));
         this.runUpdates(var4.storage, var3, var5, var1, var0 -> {
         });
         this.currentGraph.set(var4);
         this.nextGraphEvents.set(null);
         this.needsFrustumUpdate.set(true);
      });
   }

   private void runPartialUpdate(boolean var1, Frustum var2, List<SectionRenderDispatcher.RenderSection> var3, Vec3 var4) {
      SectionOcclusionGraph.GraphState var5 = this.currentGraph.get();
      this.queueSectionsWithNewNeighbors(var5);
      if (!var5.events.sectionsToPropagateFrom.isEmpty()) {
         ArrayDeque var6 = Queues.newArrayDeque();

         while (!var5.events.sectionsToPropagateFrom.isEmpty()) {
            SectionRenderDispatcher.RenderSection var7 = var5.events.sectionsToPropagateFrom.poll();
            SectionOcclusionGraph.Node var8 = var5.storage.sectionToNodeMap.get(var7);
            if (var8 != null && var8.section == var7) {
               var6.add(var8);
            }
         }

         Frustum var9 = LevelRenderer.offsetFrustum(var2);
         Consumer var10 = var2x -> {
            if (var9.isVisible(var2x.getBoundingBox())) {
               var3.add(var2x);
            }
         };
         this.runUpdates(var5.storage, var4, var6, var1, var10);
      }
   }

   private void queueSectionsWithNewNeighbors(SectionOcclusionGraph.GraphState var1) {
      LongIterator var2 = var1.events.chunksWhichReceivedNeighbors.iterator();

      while (var2.hasNext()) {
         long var3 = var2.nextLong();
         List var5 = (List)var1.storage.chunksWaitingForNeighbors.get(var3);
         if (var5 != null && ((SectionRenderDispatcher.RenderSection)var5.get(0)).hasAllNeighbors()) {
            var1.events.sectionsToPropagateFrom.addAll(var5);
            var1.storage.chunksWaitingForNeighbors.remove(var3);
         }
      }

      var1.events.chunksWhichReceivedNeighbors.clear();
   }

   private void addNeighbors(SectionOcclusionGraph.GraphEvents var1, ChunkPos var2) {
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x - 1, var2.z));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x, var2.z - 1));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x + 1, var2.z));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x, var2.z + 1));
   }

   private void initializeQueueForFullUpdate(Camera var1, Queue<SectionOcclusionGraph.Node> var2) {
      byte var3 = 16;
      Vec3 var4 = var1.getPosition();
      BlockPos var5 = var1.getBlockPosition();
      SectionRenderDispatcher.RenderSection var6 = this.viewArea.getRenderSectionAt(var5);
      if (var6 == null) {
         LevelHeightAccessor var7 = this.viewArea.getLevelHeightAccessor();
         boolean var8 = var5.getY() > var7.getMinBuildHeight();
         int var9 = var8 ? var7.getMaxBuildHeight() - 8 : var7.getMinBuildHeight() + 8;
         int var10 = Mth.floor(var4.x / 16.0) * 16;
         int var11 = Mth.floor(var4.z / 16.0) * 16;
         int var12 = this.viewArea.getViewDistance();
         ArrayList var13 = Lists.newArrayList();

         for (int var14 = -var12; var14 <= var12; var14++) {
            for (int var15 = -var12; var15 <= var12; var15++) {
               SectionRenderDispatcher.RenderSection var16 = this.viewArea
                  .getRenderSectionAt(new BlockPos(var10 + SectionPos.sectionToBlockCoord(var14, 8), var9, var11 + SectionPos.sectionToBlockCoord(var15, 8)));
               if (var16 != null && this.isInViewDistance(var5, var16.getOrigin())) {
                  Direction var17 = var8 ? Direction.DOWN : Direction.UP;
                  SectionOcclusionGraph.Node var18 = new SectionOcclusionGraph.Node(var16, var17, 0);
                  var18.setDirections(var18.directions, var17);
                  if (var14 > 0) {
                     var18.setDirections(var18.directions, Direction.EAST);
                  } else if (var14 < 0) {
                     var18.setDirections(var18.directions, Direction.WEST);
                  }

                  if (var15 > 0) {
                     var18.setDirections(var18.directions, Direction.SOUTH);
                  } else if (var15 < 0) {
                     var18.setDirections(var18.directions, Direction.NORTH);
                  }

                  var13.add(var18);
               }
            }
         }

         var13.sort(Comparator.comparingDouble(var1x -> var5.distSqr(var1x.section.getOrigin().offset(8, 8, 8))));
         var2.addAll(var13);
      } else {
         var2.add(new SectionOcclusionGraph.Node(var6, null, 0));
      }
   }

   private void runUpdates(
      SectionOcclusionGraph.GraphStorage var1,
      Vec3 var2,
      Queue<SectionOcclusionGraph.Node> var3,
      boolean var4,
      Consumer<SectionRenderDispatcher.RenderSection> var5
   ) {
      byte var6 = 16;
      BlockPos var7 = new BlockPos(Mth.floor(var2.x / 16.0) * 16, Mth.floor(var2.y / 16.0) * 16, Mth.floor(var2.z / 16.0) * 16);
      BlockPos var8 = var7.offset(8, 8, 8);

      while (!var3.isEmpty()) {
         SectionOcclusionGraph.Node var9 = (SectionOcclusionGraph.Node)var3.poll();
         SectionRenderDispatcher.RenderSection var10 = var9.section;
         if (var1.renderSections.add(var9)) {
            var5.accept(var9.section);
         }

         boolean var11 = Math.abs(var10.getOrigin().getX() - var7.getX()) > 60
            || Math.abs(var10.getOrigin().getY() - var7.getY()) > 60
            || Math.abs(var10.getOrigin().getZ() - var7.getZ()) > 60;

         for (Direction var15 : DIRECTIONS) {
            SectionRenderDispatcher.RenderSection var16 = this.getRelativeFrom(var7, var10, var15);
            if (var16 != null && (!var4 || !var9.hasDirection(var15.getOpposite()))) {
               if (var4 && var9.hasSourceDirections()) {
                  SectionRenderDispatcher.CompiledSection var17 = var10.getCompiled();
                  boolean var18 = false;

                  for (int var19 = 0; var19 < DIRECTIONS.length; var19++) {
                     if (var9.hasSourceDirection(var19) && var17.facesCanSeeEachother(DIRECTIONS[var19].getOpposite(), var15)) {
                        var18 = true;
                        break;
                     }
                  }

                  if (!var18) {
                     continue;
                  }
               }

               if (var4 && var11) {
                  BlockPos var24 = var16.getOrigin();
                  BlockPos var26 = var24.offset(
                     (var15.getAxis() == Direction.Axis.X ? var8.getX() <= var24.getX() : var8.getX() >= var24.getX()) ? 0 : 16,
                     (var15.getAxis() == Direction.Axis.Y ? var8.getY() <= var24.getY() : var8.getY() >= var24.getY()) ? 0 : 16,
                     (var15.getAxis() == Direction.Axis.Z ? var8.getZ() <= var24.getZ() : var8.getZ() >= var24.getZ()) ? 0 : 16
                  );
                  Vec3 var28 = new Vec3((double)var26.getX(), (double)var26.getY(), (double)var26.getZ());
                  Vec3 var20 = var2.subtract(var28).normalize().scale(CEILED_SECTION_DIAGONAL);
                  boolean var21 = true;

                  while (var2.subtract(var28).lengthSqr() > 3600.0) {
                     var28 = var28.add(var20);
                     LevelHeightAccessor var22 = this.viewArea.getLevelHeightAccessor();
                     if (var28.y > (double)var22.getMaxBuildHeight() || var28.y < (double)var22.getMinBuildHeight()) {
                        break;
                     }

                     SectionRenderDispatcher.RenderSection var23 = this.viewArea.getRenderSectionAt(BlockPos.containing(var28.x, var28.y, var28.z));
                     if (var23 == null || var1.sectionToNodeMap.get(var23) == null) {
                        var21 = false;
                        break;
                     }
                  }

                  if (!var21) {
                     continue;
                  }
               }

               SectionOcclusionGraph.Node var25 = var1.sectionToNodeMap.get(var16);
               if (var25 != null) {
                  var25.addSourceDirection(var15);
               } else {
                  SectionOcclusionGraph.Node var27 = new SectionOcclusionGraph.Node(var16, var15, var9.step + 1);
                  var27.setDirections(var9.directions, var15);
                  if (var16.hasAllNeighbors()) {
                     var3.add(var27);
                     var1.sectionToNodeMap.put(var16, var27);
                  } else if (this.isInViewDistance(var7, var16.getOrigin())) {
                     var1.sectionToNodeMap.put(var16, var27);
                     ((List)var1.chunksWaitingForNeighbors.computeIfAbsent(ChunkPos.asLong(var16.getOrigin()), var0 -> new ArrayList())).add(var16);
                  }
               }
            }
         }
      }
   }

   private boolean isInViewDistance(BlockPos var1, BlockPos var2) {
      int var3 = SectionPos.blockToSectionCoord(var1.getX());
      int var4 = SectionPos.blockToSectionCoord(var1.getZ());
      int var5 = SectionPos.blockToSectionCoord(var2.getX());
      int var6 = SectionPos.blockToSectionCoord(var2.getZ());
      return ChunkTrackingView.isInViewDistance(var3, var4, this.viewArea.getViewDistance(), var5, var6);
   }

   @Nullable
   private SectionRenderDispatcher.RenderSection getRelativeFrom(BlockPos var1, SectionRenderDispatcher.RenderSection var2, Direction var3) {
      BlockPos var4 = var2.getRelativeOrigin(var3);
      if (!this.isInViewDistance(var1, var4)) {
         return null;
      } else {
         return Mth.abs(var1.getY() - var4.getY()) > this.viewArea.getViewDistance() * 16 ? null : this.viewArea.getRenderSectionAt(var4);
      }
   }

   @Nullable
   @VisibleForDebug
   protected SectionOcclusionGraph.Node getNode(SectionRenderDispatcher.RenderSection var1) {
      return this.currentGraph.get().storage.sectionToNodeMap.get(var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static class GraphStorage {
      public final SectionOcclusionGraph.SectionToNodeMap sectionToNodeMap;
      public final LinkedHashSet<SectionOcclusionGraph.Node> renderSections;
      public final Long2ObjectMap<List<SectionRenderDispatcher.RenderSection>> chunksWaitingForNeighbors;

      public GraphStorage(int var1) {
         super();
         this.sectionToNodeMap = new SectionOcclusionGraph.SectionToNodeMap(var1);
         this.renderSections = new LinkedHashSet<>(var1);
         this.chunksWaitingForNeighbors = new Long2ObjectOpenHashMap();
      }
   }

   @VisibleForDebug
   protected static class Node {
      @VisibleForDebug
      protected final SectionRenderDispatcher.RenderSection section;
      private byte sourceDirections;
      byte directions;
      @VisibleForDebug
      protected final int step;

      Node(SectionRenderDispatcher.RenderSection var1, @Nullable Direction var2, int var3) {
         super();
         this.section = var1;
         if (var2 != null) {
            this.addSourceDirection(var2);
         }

         this.step = var3;
      }

      void setDirections(byte var1, Direction var2) {
         this.directions = (byte)(this.directions | var1 | 1 << var2.ordinal());
      }

      boolean hasDirection(Direction var1) {
         return (this.directions & 1 << var1.ordinal()) > 0;
      }

      void addSourceDirection(Direction var1) {
         this.sourceDirections = (byte)(this.sourceDirections | this.sourceDirections | 1 << var1.ordinal());
      }

      @VisibleForDebug
      protected boolean hasSourceDirection(int var1) {
         return (this.sourceDirections & 1 << var1) > 0;
      }

      boolean hasSourceDirections() {
         return this.sourceDirections != 0;
      }

      @Override
      public int hashCode() {
         return this.section.getOrigin().hashCode();
      }

      @Override
      public boolean equals(Object var1) {
         return !(var1 instanceof SectionOcclusionGraph.Node var2) ? false : this.section.getOrigin().equals(var2.section.getOrigin());
      }
   }

   static class SectionToNodeMap {
      private final SectionOcclusionGraph.Node[] nodes;

      SectionToNodeMap(int var1) {
         super();
         this.nodes = new SectionOcclusionGraph.Node[var1];
      }

      public void put(SectionRenderDispatcher.RenderSection var1, SectionOcclusionGraph.Node var2) {
         this.nodes[var1.index] = var2;
      }

      @Nullable
      public SectionOcclusionGraph.Node get(SectionRenderDispatcher.RenderSection var1) {
         int var2 = var1.index;
         return var2 >= 0 && var2 < this.nodes.length ? this.nodes[var2] : null;
      }
   }
}
