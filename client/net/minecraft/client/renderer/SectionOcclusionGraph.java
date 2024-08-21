package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
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
         this.currentGraph.set(new SectionOcclusionGraph.GraphState(var1));
         this.invalidate();
      } else {
         this.currentGraph.set(null);
      }
   }

   public void invalidate() {
      this.needsFullUpdate = true;
   }

   public void addSectionsInFrustum(Frustum var1, List<SectionRenderDispatcher.RenderSection> var2) {
      this.currentGraph.get().storage().sectionTree.visitNodes((var1x, var2x, var3) -> {
         SectionRenderDispatcher.RenderSection var4 = var1x.getSection();
         if (var4 != null) {
            var2.add(var4);
         }
      }, var1);
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

   public void schedulePropagationFrom(SectionRenderDispatcher.RenderSection var1) {
      SectionOcclusionGraph.GraphEvents var2 = this.nextGraphEvents.get();
      if (var2 != null) {
         var2.sectionsToPropagateFrom.add(var1);
      }

      SectionOcclusionGraph.GraphEvents var3 = this.currentGraph.get().events;
      if (var3 != var2) {
         var3.sectionsToPropagateFrom.add(var1);
      }
   }

   public void update(boolean var1, Camera var2, Frustum var3, List<SectionRenderDispatcher.RenderSection> var4, LongOpenHashSet var5) {
      Vec3 var6 = var2.getPosition();
      if (this.needsFullUpdate && (this.fullUpdateTask == null || this.fullUpdateTask.isDone())) {
         this.scheduleFullUpdate(var1, var2, var6, var5);
      }

      this.runPartialUpdate(var1, var3, var4, var6, var5);
   }

   private void scheduleFullUpdate(boolean var1, Camera var2, Vec3 var3, LongOpenHashSet var4) {
      this.needsFullUpdate = false;
      LongOpenHashSet var5 = var4.clone();
      this.fullUpdateTask = Util.backgroundExecutor().submit(() -> {
         SectionOcclusionGraph.GraphState var5x = new SectionOcclusionGraph.GraphState(this.viewArea);
         this.nextGraphEvents.set(var5x.events);
         ArrayDeque var6 = Queues.newArrayDeque();
         this.initializeQueueForFullUpdate(var2, var6);
         var6.forEach(var1xx -> var5x.storage.sectionToNodeMap.put(var1xx.section, var1xx));
         this.runUpdates(var5x.storage, var3, var6, var1, var0 -> {
         }, var5);
         this.currentGraph.set(var5x);
         this.nextGraphEvents.set(null);
         this.needsFrustumUpdate.set(true);
      });
   }

   private void runPartialUpdate(boolean var1, Frustum var2, List<SectionRenderDispatcher.RenderSection> var3, Vec3 var4, LongOpenHashSet var5) {
      SectionOcclusionGraph.GraphState var6 = this.currentGraph.get();
      this.queueSectionsWithNewNeighbors(var6);
      if (!var6.events.sectionsToPropagateFrom.isEmpty()) {
         ArrayDeque var7 = Queues.newArrayDeque();

         while (!var6.events.sectionsToPropagateFrom.isEmpty()) {
            SectionRenderDispatcher.RenderSection var8 = var6.events.sectionsToPropagateFrom.poll();
            SectionOcclusionGraph.Node var9 = var6.storage.sectionToNodeMap.get(var8);
            if (var9 != null && var9.section == var8) {
               var7.add(var9);
            }
         }

         Frustum var10 = LevelRenderer.offsetFrustum(var2);
         Consumer var11 = var2x -> {
            if (var10.isVisible(var2x.getBoundingBox())) {
               var3.add(var2x);
            }
         };
         this.runUpdates(var6.storage, var4, var7, var1, var11, var5);
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
      BlockPos var3 = var1.getBlockPosition();
      long var4 = SectionPos.asLong(var3);
      int var6 = SectionPos.y(var4);
      SectionRenderDispatcher.RenderSection var7 = this.viewArea.getRenderSection(var4);
      if (var7 == null) {
         LevelHeightAccessor var8 = this.viewArea.getLevelHeightAccessor();
         boolean var9 = var6 < var8.getMinSectionY();
         int var10 = var9 ? var8.getMinSectionY() : var8.getMaxSectionY();
         int var11 = this.viewArea.getViewDistance();
         ArrayList var12 = Lists.newArrayList();
         int var13 = SectionPos.x(var4);
         int var14 = SectionPos.z(var4);

         for (int var15 = -var11; var15 <= var11; var15++) {
            for (int var16 = -var11; var16 <= var11; var16++) {
               SectionRenderDispatcher.RenderSection var17 = this.viewArea.getRenderSection(SectionPos.asLong(var15 + var13, var10, var16 + var14));
               if (var17 != null && this.isInViewDistance(var4, var17.getSectionNode())) {
                  Direction var18 = var9 ? Direction.UP : Direction.DOWN;
                  SectionOcclusionGraph.Node var19 = new SectionOcclusionGraph.Node(var17, var18, 0);
                  var19.setDirections(var19.directions, var18);
                  if (var15 > 0) {
                     var19.setDirections(var19.directions, Direction.EAST);
                  } else if (var15 < 0) {
                     var19.setDirections(var19.directions, Direction.WEST);
                  }

                  if (var16 > 0) {
                     var19.setDirections(var19.directions, Direction.SOUTH);
                  } else if (var16 < 0) {
                     var19.setDirections(var19.directions, Direction.NORTH);
                  }

                  var12.add(var19);
               }
            }
         }

         var12.sort(Comparator.comparingDouble(var1x -> var3.distSqr(var1x.section.getOrigin().offset(8, 8, 8))));
         var2.addAll(var12);
      } else {
         var2.add(new SectionOcclusionGraph.Node(var7, null, 0));
      }
   }

   private void runUpdates(
      SectionOcclusionGraph.GraphStorage var1,
      Vec3 var2,
      Queue<SectionOcclusionGraph.Node> var3,
      boolean var4,
      Consumer<SectionRenderDispatcher.RenderSection> var5,
      LongOpenHashSet var6
   ) {
      byte var7 = 16;
      BlockPos var8 = new BlockPos(Mth.floor(var2.x / 16.0) * 16, Mth.floor(var2.y / 16.0) * 16, Mth.floor(var2.z / 16.0) * 16);
      long var9 = SectionPos.asLong(var8);
      BlockPos var11 = var8.offset(8, 8, 8);

      while (!var3.isEmpty()) {
         SectionOcclusionGraph.Node var12 = (SectionOcclusionGraph.Node)var3.poll();
         SectionRenderDispatcher.RenderSection var13 = var12.section;
         if (!var6.contains(var12.section.getSectionNode())) {
            if (var1.sectionTree.add(var12.section)) {
               var5.accept(var12.section);
            }
         } else {
            var12.section.compiled.compareAndSet(SectionRenderDispatcher.CompiledSection.UNCOMPILED, SectionRenderDispatcher.CompiledSection.EMPTY);
         }

         boolean var14 = Math.abs(var13.getOrigin().getX() - var8.getX()) > 60
            || Math.abs(var13.getOrigin().getY() - var8.getY()) > 60
            || Math.abs(var13.getOrigin().getZ() - var8.getZ()) > 60;

         for (Direction var18 : DIRECTIONS) {
            SectionRenderDispatcher.RenderSection var19 = this.getRelativeFrom(var9, var13, var18);
            if (var19 != null && (!var4 || !var12.hasDirection(var18.getOpposite()))) {
               if (var4 && var12.hasSourceDirections()) {
                  SectionRenderDispatcher.CompiledSection var20 = var13.getCompiled();
                  boolean var21 = false;

                  for (int var22 = 0; var22 < DIRECTIONS.length; var22++) {
                     if (var12.hasSourceDirection(var22) && var20.facesCanSeeEachother(DIRECTIONS[var22].getOpposite(), var18)) {
                        var21 = true;
                        break;
                     }
                  }

                  if (!var21) {
                     continue;
                  }
               }

               if (var4 && var14) {
                  BlockPos var27 = var19.getOrigin();
                  BlockPos var29 = var27.offset(
                     (var18.getAxis() == Direction.Axis.X ? var11.getX() <= var27.getX() : var11.getX() >= var27.getX()) ? 0 : 16,
                     (var18.getAxis() == Direction.Axis.Y ? var11.getY() <= var27.getY() : var11.getY() >= var27.getY()) ? 0 : 16,
                     (var18.getAxis() == Direction.Axis.Z ? var11.getZ() <= var27.getZ() : var11.getZ() >= var27.getZ()) ? 0 : 16
                  );
                  Vec3 var31 = new Vec3((double)var29.getX(), (double)var29.getY(), (double)var29.getZ());
                  Vec3 var23 = var2.subtract(var31).normalize().scale(CEILED_SECTION_DIAGONAL);
                  boolean var24 = true;

                  while (var2.subtract(var31).lengthSqr() > 3600.0) {
                     var31 = var31.add(var23);
                     LevelHeightAccessor var25 = this.viewArea.getLevelHeightAccessor();
                     if (var31.y > (double)var25.getMaxY() || var31.y < (double)var25.getMinY()) {
                        break;
                     }

                     SectionRenderDispatcher.RenderSection var26 = this.viewArea.getRenderSectionAt(BlockPos.containing(var31.x, var31.y, var31.z));
                     if (var26 == null || var1.sectionToNodeMap.get(var26) == null) {
                        var24 = false;
                        break;
                     }
                  }

                  if (!var24) {
                     continue;
                  }
               }

               SectionOcclusionGraph.Node var28 = var1.sectionToNodeMap.get(var19);
               if (var28 != null) {
                  var28.addSourceDirection(var18);
               } else {
                  SectionOcclusionGraph.Node var30 = new SectionOcclusionGraph.Node(var19, var18, var12.step + 1);
                  var30.setDirections(var12.directions, var18);
                  if (var19.hasAllNeighbors()) {
                     var3.add(var30);
                     var1.sectionToNodeMap.put(var19, var30);
                  } else if (this.isInViewDistance(var9, var19.getSectionNode())) {
                     var1.sectionToNodeMap.put(var19, var30);
                     ((List)var1.chunksWaitingForNeighbors.computeIfAbsent(ChunkPos.asLong(var19.getOrigin()), var0 -> new ArrayList())).add(var19);
                  }
               }
            }
         }
      }
   }

   private boolean isInViewDistance(long var1, long var3) {
      return ChunkTrackingView.isInViewDistance(SectionPos.x(var1), SectionPos.z(var1), this.viewArea.getViewDistance(), SectionPos.x(var3), SectionPos.z(var3));
   }

   @Nullable
   private SectionRenderDispatcher.RenderSection getRelativeFrom(long var1, SectionRenderDispatcher.RenderSection var3, Direction var4) {
      long var5 = var3.getNeighborSectionNode(var4);
      if (!this.isInViewDistance(var1, var5)) {
         return null;
      } else {
         return Mth.abs(SectionPos.y(var1) - SectionPos.y(var5)) > this.viewArea.getViewDistance() ? null : this.viewArea.getRenderSection(var5);
      }
   }

   @Nullable
   @VisibleForDebug
   public SectionOcclusionGraph.Node getNode(SectionRenderDispatcher.RenderSection var1) {
      return this.currentGraph.get().storage.sectionToNodeMap.get(var1);
   }

   public Octree getOctree() {
      return this.currentGraph.get().storage.sectionTree;
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
      public final Octree sectionTree;
      public final Long2ObjectMap<List<SectionRenderDispatcher.RenderSection>> chunksWaitingForNeighbors;

      public GraphStorage(ViewArea var1) {
         super();
         this.sectionToNodeMap = new SectionOcclusionGraph.SectionToNodeMap(var1.sections.length);
         this.sectionTree = new Octree(var1.getCameraSectionPos(), var1.getViewDistance(), var1.sectionGridSizeY, var1.level.getMinY());
         this.chunksWaitingForNeighbors = new Long2ObjectOpenHashMap();
      }
   }

   @VisibleForDebug
   public static class Node {
      @VisibleForDebug
      protected final SectionRenderDispatcher.RenderSection section;
      private byte sourceDirections;
      byte directions;
      @VisibleForDebug
      public final int step;

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
      public boolean hasSourceDirection(int var1) {
         return (this.sourceDirections & 1 << var1) > 0;
      }

      boolean hasSourceDirections() {
         return this.sourceDirections != 0;
      }

      @Override
      public int hashCode() {
         return Long.hashCode(this.section.getSectionNode());
      }

      @Override
      public boolean equals(Object var1) {
         return !(var1 instanceof SectionOcclusionGraph.Node var2) ? false : this.section.getSectionNode() == var2.section.getSectionNode();
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
