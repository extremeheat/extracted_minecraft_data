package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.chunk.CompiledSectionMesh;
import net.minecraft.client.renderer.chunk.SectionMesh;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ChunkTrackingView;
import net.minecraft.util.Mth;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class SectionOcclusionGraph {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final Direction[] DIRECTIONS = Direction.values();
   private static final int MINIMUM_ADVANCED_CULLING_DISTANCE = 60;
   private static final int MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE = SectionPos.blockToSectionCoord(60);
   private static final double CEILED_SECTION_DIAGONAL = Math.ceil(Math.sqrt(3.0) * 16.0);
   private boolean needsFullUpdate = true;
   private @Nullable Future<?> fullUpdateTask;
   private @Nullable ViewArea viewArea;
   private final AtomicReference<@Nullable GraphState> currentGraph = new AtomicReference();
   private final AtomicReference<@Nullable GraphEvents> nextGraphEvents = new AtomicReference();
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
         this.currentGraph.set(new GraphState(var1));
         this.invalidate();
      } else {
         this.currentGraph.set((Object)null);
      }

   }

   public void invalidate() {
      this.needsFullUpdate = true;
   }

   public void addSectionsInFrustum(Frustum var1, List<SectionRenderDispatcher.RenderSection> var2, List<SectionRenderDispatcher.RenderSection> var3) {
      ((GraphState)this.currentGraph.get()).storage().sectionTree.visitNodes((var2x, var3x, var4, var5) -> {
         SectionRenderDispatcher.RenderSection var6 = var2x.getSection();
         if (var6 != null) {
            var2.add(var6);
            if (var5) {
               var3.add(var6);
            }
         }

      }, var1, 32);
   }

   public boolean consumeFrustumUpdate() {
      return this.needsFrustumUpdate.compareAndSet(true, false);
   }

   public void onChunkReadyToRender(ChunkPos var1) {
      GraphEvents var2 = (GraphEvents)this.nextGraphEvents.get();
      if (var2 != null) {
         this.addNeighbors(var2, var1);
      }

      GraphEvents var3 = ((GraphState)this.currentGraph.get()).events;
      if (var3 != var2) {
         this.addNeighbors(var3, var1);
      }

   }

   public void schedulePropagationFrom(SectionRenderDispatcher.RenderSection var1) {
      GraphEvents var2 = (GraphEvents)this.nextGraphEvents.get();
      if (var2 != null) {
         var2.sectionsToPropagateFrom.add(var1);
      }

      GraphEvents var3 = ((GraphState)this.currentGraph.get()).events;
      if (var3 != var2) {
         var3.sectionsToPropagateFrom.add(var1);
      }

   }

   public void update(boolean var1, Camera var2, Frustum var3, List<SectionRenderDispatcher.RenderSection> var4, LongOpenHashSet var5) {
      Vec3 var6 = var2.position();
      if (this.needsFullUpdate && (this.fullUpdateTask == null || this.fullUpdateTask.isDone())) {
         this.scheduleFullUpdate(var1, var2, var6, var5);
      }

      this.runPartialUpdate(var1, var3, var4, var6, var5);
   }

   private void scheduleFullUpdate(boolean var1, Camera var2, Vec3 var3, LongOpenHashSet var4) {
      this.needsFullUpdate = false;
      LongOpenHashSet var5 = var4.clone();
      this.fullUpdateTask = CompletableFuture.runAsync(() -> {
         GraphState var5x = new GraphState(this.viewArea);
         this.nextGraphEvents.set(var5x.events);
         ArrayDeque var6 = Queues.newArrayDeque();
         this.initializeQueueForFullUpdate(var2, var6);
         var6.forEach((var1x) -> var5x.storage.sectionToNodeMap.put(var1x.section, var1x));
         this.runUpdates(var5x.storage, var3, var6, var1, (var0) -> {
         }, var5);
         this.currentGraph.set(var5x);
         this.nextGraphEvents.set((Object)null);
         this.needsFrustumUpdate.set(true);
      }, Util.backgroundExecutor());
   }

   private void runPartialUpdate(boolean var1, Frustum var2, List<SectionRenderDispatcher.RenderSection> var3, Vec3 var4, LongOpenHashSet var5) {
      GraphState var6 = (GraphState)this.currentGraph.get();
      this.queueSectionsWithNewNeighbors(var6);
      if (!var6.events.sectionsToPropagateFrom.isEmpty()) {
         ArrayDeque var7 = Queues.newArrayDeque();

         while(!var6.events.sectionsToPropagateFrom.isEmpty()) {
            SectionRenderDispatcher.RenderSection var8 = (SectionRenderDispatcher.RenderSection)var6.events.sectionsToPropagateFrom.poll();
            Node var9 = var6.storage.sectionToNodeMap.get(var8);
            if (var9 != null && var9.section == var8) {
               var7.add(var9);
            }
         }

         Frustum var10 = LevelRenderer.offsetFrustum(var2);
         Consumer var11 = (var2x) -> {
            if (var10.isVisible(var2x.getBoundingBox())) {
               this.needsFrustumUpdate.set(true);
            }

         };
         this.runUpdates(var6.storage, var4, var7, var1, var11, var5);
      }

   }

   private void queueSectionsWithNewNeighbors(GraphState var1) {
      LongIterator var2 = var1.events.chunksWhichReceivedNeighbors.iterator();

      while(var2.hasNext()) {
         long var3 = var2.nextLong();
         List var5 = (List)var1.storage.chunksWaitingForNeighbors.get(var3);
         if (var5 != null && ((SectionRenderDispatcher.RenderSection)var5.get(0)).hasAllNeighbors()) {
            var1.events.sectionsToPropagateFrom.addAll(var5);
            var1.storage.chunksWaitingForNeighbors.remove(var3);
         }
      }

      var1.events.chunksWhichReceivedNeighbors.clear();
   }

   private void addNeighbors(GraphEvents var1, ChunkPos var2) {
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x - 1, var2.z));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x, var2.z - 1));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x + 1, var2.z));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x, var2.z + 1));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x - 1, var2.z - 1));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x - 1, var2.z + 1));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x + 1, var2.z - 1));
      var1.chunksWhichReceivedNeighbors.add(ChunkPos.asLong(var2.x + 1, var2.z + 1));
   }

   private void initializeQueueForFullUpdate(Camera var1, Queue<Node> var2) {
      BlockPos var3 = var1.blockPosition();
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

         for(int var15 = -var11; var15 <= var11; ++var15) {
            for(int var16 = -var11; var16 <= var11; ++var16) {
               SectionRenderDispatcher.RenderSection var17 = this.viewArea.getRenderSection(SectionPos.asLong(var15 + var13, var10, var16 + var14));
               if (var17 != null && this.isInViewDistance(var4, var17.getSectionNode())) {
                  Direction var18 = var9 ? Direction.UP : Direction.DOWN;
                  Node var19 = new Node(var17, var18, 0);
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

         var12.sort(Comparator.comparingDouble((var1x) -> var3.distSqr(SectionPos.of(var1x.section.getSectionNode()).center())));
         var2.addAll(var12);
      } else {
         var2.add(new Node(var7, (Direction)null, 0));
      }

   }

   private void runUpdates(GraphStorage var1, Vec3 var2, Queue<Node> var3, boolean var4, Consumer<SectionRenderDispatcher.RenderSection> var5, LongOpenHashSet var6) {
      SectionPos var7 = SectionPos.of((Position)var2);
      long var8 = var7.asLong();
      BlockPos var10 = var7.center();

      while(!var3.isEmpty()) {
         Node var11 = (Node)var3.poll();
         SectionRenderDispatcher.RenderSection var12 = var11.section;
         if (!var6.contains(var11.section.getSectionNode())) {
            if (var1.sectionTree.add(var11.section)) {
               var5.accept(var11.section);
            }
         } else {
            var11.section.sectionMesh.compareAndSet(CompiledSectionMesh.UNCOMPILED, CompiledSectionMesh.EMPTY);
         }

         long var13 = var12.getSectionNode();
         boolean var15 = Math.abs(SectionPos.x(var13) - var7.x()) > MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE || Math.abs(SectionPos.y(var13) - var7.y()) > MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE || Math.abs(SectionPos.z(var13) - var7.z()) > MINIMUM_ADVANCED_CULLING_SECTION_DISTANCE;

         for(Direction var19 : DIRECTIONS) {
            SectionRenderDispatcher.RenderSection var20 = this.getRelativeFrom(var8, var12, var19);
            if (var20 != null && (!var4 || !var11.hasDirection(var19.getOpposite()))) {
               if (var4 && var11.hasSourceDirections()) {
                  SectionMesh var21 = var12.getSectionMesh();
                  boolean var22 = false;

                  for(int var23 = 0; var23 < DIRECTIONS.length; ++var23) {
                     if (var11.hasSourceDirection(var23) && var21.facesCanSeeEachother(DIRECTIONS[var23].getOpposite(), var19)) {
                        var22 = true;
                        break;
                     }
                  }

                  if (!var22) {
                     continue;
                  }
               }

               if (var4 && var15) {
                  int var32 = SectionPos.sectionToBlockCoord(SectionPos.x(var13));
                  int var34 = SectionPos.sectionToBlockCoord(SectionPos.y(var13));
                  int var36 = SectionPos.sectionToBlockCoord(SectionPos.z(var13));
                  boolean var24 = var19.getAxis() == Direction.Axis.X ? var10.getX() > var32 : var10.getX() < var32;
                  boolean var25 = var19.getAxis() == Direction.Axis.Y ? var10.getY() > var34 : var10.getY() < var34;
                  boolean var26 = var19.getAxis() == Direction.Axis.Z ? var10.getZ() > var36 : var10.getZ() < var36;
                  Vector3d var27 = new Vector3d((double)(var32 + (var24 ? 16 : 0)), (double)(var34 + (var25 ? 16 : 0)), (double)(var36 + (var26 ? 16 : 0)));
                  Vector3d var28 = (new Vector3d(var2.x, var2.y, var2.z)).sub(var27).normalize().mul(CEILED_SECTION_DIAGONAL);
                  boolean var29 = true;

                  while(var27.distanceSquared(var2.x, var2.y, var2.z) > 3600.0) {
                     var27.add(var28);
                     LevelHeightAccessor var30 = this.viewArea.getLevelHeightAccessor();
                     if (var27.y > (double)var30.getMaxY() || var27.y < (double)var30.getMinY()) {
                        break;
                     }

                     SectionRenderDispatcher.RenderSection var31 = this.viewArea.getRenderSectionAt(BlockPos.containing(var27.x, var27.y, var27.z));
                     if (var31 == null || var1.sectionToNodeMap.get(var31) == null) {
                        var29 = false;
                        break;
                     }
                  }

                  if (!var29) {
                     continue;
                  }
               }

               Node var33 = var1.sectionToNodeMap.get(var20);
               if (var33 != null) {
                  var33.addSourceDirection(var19);
               } else {
                  Node var35 = new Node(var20, var19, var11.step + 1);
                  var35.setDirections(var11.directions, var19);
                  if (var20.hasAllNeighbors()) {
                     var3.add(var35);
                     var1.sectionToNodeMap.put(var20, var35);
                  } else if (this.isInViewDistance(var8, var20.getSectionNode())) {
                     var1.sectionToNodeMap.put(var20, var35);
                     long var37 = SectionPos.sectionToChunk(var20.getSectionNode());
                     ((List)var1.chunksWaitingForNeighbors.computeIfAbsent(var37, (var0) -> new ArrayList())).add(var20);
                  }
               }
            }
         }
      }

   }

   private boolean isInViewDistance(long var1, long var3) {
      return ChunkTrackingView.isInViewDistance(SectionPos.x(var1), SectionPos.z(var1), this.viewArea.getViewDistance(), SectionPos.x(var3), SectionPos.z(var3));
   }

   private SectionRenderDispatcher.@Nullable RenderSection getRelativeFrom(long var1, SectionRenderDispatcher.RenderSection var3, Direction var4) {
      long var5 = var3.getNeighborSectionNode(var4);
      if (!this.isInViewDistance(var1, var5)) {
         return null;
      } else {
         return Mth.abs(SectionPos.y(var1) - SectionPos.y(var5)) > this.viewArea.getViewDistance() ? null : this.viewArea.getRenderSection(var5);
      }
   }

   @VisibleForDebug
   public @Nullable Node getNode(SectionRenderDispatcher.RenderSection var1) {
      return ((GraphState)this.currentGraph.get()).storage.sectionToNodeMap.get(var1);
   }

   public Octree getOctree() {
      return ((GraphState)this.currentGraph.get()).storage.sectionTree;
   }

   static record GraphState(GraphStorage storage, GraphEvents events) {
      final GraphStorage storage;
      final GraphEvents events;

      GraphState(ViewArea var1) {
         this(new GraphStorage(var1), new GraphEvents());
      }

      private GraphState(GraphStorage var1, GraphEvents var2) {
         super();
         this.storage = var1;
         this.events = var2;
      }
   }

   static record GraphEvents(LongSet chunksWhichReceivedNeighbors, BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom) {
      final LongSet chunksWhichReceivedNeighbors;
      final BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom;

      GraphEvents() {
         this(new LongOpenHashSet(), new LinkedBlockingQueue());
      }

      private GraphEvents(LongSet var1, BlockingQueue<SectionRenderDispatcher.RenderSection> var2) {
         super();
         this.chunksWhichReceivedNeighbors = var1;
         this.sectionsToPropagateFrom = var2;
      }
   }

   static class GraphStorage {
      public final SectionToNodeMap sectionToNodeMap;
      public final Octree sectionTree;
      public final Long2ObjectMap<List<SectionRenderDispatcher.RenderSection>> chunksWaitingForNeighbors;

      public GraphStorage(ViewArea var1) {
         super();
         this.sectionToNodeMap = new SectionToNodeMap(var1.sections.length);
         this.sectionTree = new Octree(var1.getCameraSectionPos(), var1.getViewDistance(), var1.sectionGridSizeY, var1.level.getMinY());
         this.chunksWaitingForNeighbors = new Long2ObjectOpenHashMap();
      }
   }

   static class SectionToNodeMap {
      private final Node[] nodes;

      SectionToNodeMap(int var1) {
         super();
         this.nodes = new Node[var1];
      }

      public void put(SectionRenderDispatcher.RenderSection var1, Node var2) {
         this.nodes[var1.index] = var2;
      }

      public @Nullable Node get(SectionRenderDispatcher.RenderSection var1) {
         int var2 = var1.index;
         return var2 >= 0 && var2 < this.nodes.length ? this.nodes[var2] : null;
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

      public int hashCode() {
         return Long.hashCode(this.section.getSectionNode());
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Node var2)) {
            return false;
         } else {
            return this.section.getSectionNode() == var2.section.getSectionNode();
         }
      }
   }
}
