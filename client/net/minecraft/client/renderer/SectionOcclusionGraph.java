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
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
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
   private final AtomicReference<GraphState> currentGraph = new AtomicReference();
   private final AtomicReference<GraphEvents> nextGraphEvents = new AtomicReference();
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
         this.currentGraph.set(new GraphState(var1.sections.length));
         this.invalidate();
      } else {
         this.currentGraph.set((Object)null);
      }

   }

   public void invalidate() {
      this.needsFullUpdate = true;
   }

   public void addSectionsInFrustum(Frustum var1, List<SectionRenderDispatcher.RenderSection> var2) {
      Iterator var3 = ((GraphState)this.currentGraph.get()).storage().renderSections.iterator();

      while(var3.hasNext()) {
         Node var4 = (Node)var3.next();
         if (var1.isVisible(var4.section.getBoundingBox())) {
            var2.add(var4.section);
         }
      }

   }

   public boolean consumeFrustumUpdate() {
      return this.needsFrustumUpdate.compareAndSet(true, false);
   }

   public void onChunkLoaded(ChunkPos var1) {
      GraphEvents var2 = (GraphEvents)this.nextGraphEvents.get();
      if (var2 != null) {
         this.addNeighbors(var2, var1);
      }

      GraphEvents var3 = ((GraphState)this.currentGraph.get()).events;
      if (var3 != var2) {
         this.addNeighbors(var3, var1);
      }

   }

   public void onSectionCompiled(SectionRenderDispatcher.RenderSection var1) {
      GraphEvents var2 = (GraphEvents)this.nextGraphEvents.get();
      if (var2 != null) {
         var2.sectionsToPropagateFrom.add(var1);
      }

      GraphEvents var3 = ((GraphState)this.currentGraph.get()).events;
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
         GraphState var4 = new GraphState(this.viewArea.sections.length);
         this.nextGraphEvents.set(var4.events);
         ArrayDeque var5 = Queues.newArrayDeque();
         this.initializeQueueForFullUpdate(var2, var5);
         var5.forEach((var1x) -> {
            var4.storage.sectionToNodeMap.put(var1x.section, var1x);
         });
         this.runUpdates(var4.storage, var3, var5, var1, (var0) -> {
         });
         this.currentGraph.set(var4);
         this.nextGraphEvents.set((Object)null);
         this.needsFrustumUpdate.set(true);
      });
   }

   private void runPartialUpdate(boolean var1, Frustum var2, List<SectionRenderDispatcher.RenderSection> var3, Vec3 var4) {
      GraphState var5 = (GraphState)this.currentGraph.get();
      this.queueSectionsWithNewNeighbors(var5);
      if (!var5.events.sectionsToPropagateFrom.isEmpty()) {
         ArrayDeque var6 = Queues.newArrayDeque();

         while(!var5.events.sectionsToPropagateFrom.isEmpty()) {
            SectionRenderDispatcher.RenderSection var7 = (SectionRenderDispatcher.RenderSection)var5.events.sectionsToPropagateFrom.poll();
            Node var8 = var5.storage.sectionToNodeMap.get(var7);
            if (var8 != null && var8.section == var7) {
               var6.add(var8);
            }
         }

         Frustum var9 = LevelRenderer.offsetFrustum(var2);
         Consumer var10 = (var2x) -> {
            if (var9.isVisible(var2x.getBoundingBox())) {
               var3.add(var2x);
            }

         };
         this.runUpdates(var5.storage, var4, var6, var1, var10);
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
   }

   private void initializeQueueForFullUpdate(Camera var1, Queue<Node> var2) {
      boolean var3 = true;
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

         for(int var14 = -var12; var14 <= var12; ++var14) {
            for(int var15 = -var12; var15 <= var12; ++var15) {
               SectionRenderDispatcher.RenderSection var16 = this.viewArea.getRenderSectionAt(new BlockPos(var10 + SectionPos.sectionToBlockCoord(var14, 8), var9, var11 + SectionPos.sectionToBlockCoord(var15, 8)));
               if (var16 != null && this.isInViewDistance(var5, var16.getOrigin())) {
                  Direction var17 = var8 ? Direction.DOWN : Direction.UP;
                  Node var18 = new Node(var16, var17, 0);
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

         var13.sort(Comparator.comparingDouble((var1x) -> {
            return var5.distSqr(var1x.section.getOrigin().offset(8, 8, 8));
         }));
         var2.addAll(var13);
      } else {
         var2.add(new Node(var6, (Direction)null, 0));
      }

   }

   private void runUpdates(GraphStorage var1, Vec3 var2, Queue<Node> var3, boolean var4, Consumer<SectionRenderDispatcher.RenderSection> var5) {
      boolean var6 = true;
      BlockPos var7 = new BlockPos(Mth.floor(var2.x / 16.0) * 16, Mth.floor(var2.y / 16.0) * 16, Mth.floor(var2.z / 16.0) * 16);
      BlockPos var8 = var7.offset(8, 8, 8);

      while(!var3.isEmpty()) {
         Node var9 = (Node)var3.poll();
         SectionRenderDispatcher.RenderSection var10 = var9.section;
         if (var1.renderSections.add(var9)) {
            var5.accept(var9.section);
         }

         boolean var11 = Math.abs(var10.getOrigin().getX() - var7.getX()) > 60 || Math.abs(var10.getOrigin().getY() - var7.getY()) > 60 || Math.abs(var10.getOrigin().getZ() - var7.getZ()) > 60;
         Direction[] var12 = DIRECTIONS;
         int var13 = var12.length;

         for(int var14 = 0; var14 < var13; ++var14) {
            Direction var15 = var12[var14];
            SectionRenderDispatcher.RenderSection var16 = this.getRelativeFrom(var7, var10, var15);
            if (var16 != null && (!var4 || !var9.hasDirection(var15.getOpposite()))) {
               if (var4 && var9.hasSourceDirections()) {
                  SectionRenderDispatcher.CompiledSection var17 = var10.getCompiled();
                  boolean var18 = false;

                  for(int var19 = 0; var19 < DIRECTIONS.length; ++var19) {
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
                  byte var10001;
                  BlockPos var24;
                  label130: {
                     label129: {
                        var24 = var16.getOrigin();
                        if (var15.getAxis() == Direction.Axis.X) {
                           if (var8.getX() > var24.getX()) {
                              break label129;
                           }
                        } else if (var8.getX() < var24.getX()) {
                           break label129;
                        }

                        var10001 = 0;
                        break label130;
                     }

                     var10001 = 16;
                  }

                  byte var10002;
                  label122: {
                     label121: {
                        if (var15.getAxis() == Direction.Axis.Y) {
                           if (var8.getY() > var24.getY()) {
                              break label121;
                           }
                        } else if (var8.getY() < var24.getY()) {
                           break label121;
                        }

                        var10002 = 0;
                        break label122;
                     }

                     var10002 = 16;
                  }

                  byte var10003;
                  label114: {
                     label113: {
                        if (var15.getAxis() == Direction.Axis.Z) {
                           if (var8.getZ() > var24.getZ()) {
                              break label113;
                           }
                        } else if (var8.getZ() < var24.getZ()) {
                           break label113;
                        }

                        var10003 = 0;
                        break label114;
                     }

                     var10003 = 16;
                  }

                  BlockPos var26 = var24.offset(var10001, var10002, var10003);
                  Vec3 var28 = new Vec3((double)var26.getX(), (double)var26.getY(), (double)var26.getZ());
                  Vec3 var20 = var2.subtract(var28).normalize().scale(CEILED_SECTION_DIAGONAL);
                  boolean var21 = true;

                  label105: {
                     SectionRenderDispatcher.RenderSection var23;
                     do {
                        if (!(var2.subtract(var28).lengthSqr() > 3600.0)) {
                           break label105;
                        }

                        var28 = var28.add(var20);
                        LevelHeightAccessor var22 = this.viewArea.getLevelHeightAccessor();
                        if (var28.y > (double)var22.getMaxBuildHeight() || var28.y < (double)var22.getMinBuildHeight()) {
                           break label105;
                        }

                        var23 = this.viewArea.getRenderSectionAt(BlockPos.containing(var28.x, var28.y, var28.z));
                     } while(var23 != null && var1.sectionToNodeMap.get(var23) != null);

                     var21 = false;
                  }

                  if (!var21) {
                     continue;
                  }
               }

               Node var25 = var1.sectionToNodeMap.get(var16);
               if (var25 != null) {
                  var25.addSourceDirection(var15);
               } else {
                  Node var27 = new Node(var16, var15, var9.step + 1);
                  var27.setDirections(var9.directions, var15);
                  if (var16.hasAllNeighbors()) {
                     var3.add(var27);
                     var1.sectionToNodeMap.put(var16, var27);
                  } else if (this.isInViewDistance(var7, var16.getOrigin())) {
                     var1.sectionToNodeMap.put(var16, var27);
                     ((List)var1.chunksWaitingForNeighbors.computeIfAbsent(ChunkPos.asLong(var16.getOrigin()), (var0) -> {
                        return new ArrayList();
                     })).add(var16);
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
   protected Node getNode(SectionRenderDispatcher.RenderSection var1) {
      return ((GraphState)this.currentGraph.get()).storage.sectionToNodeMap.get(var1);
   }

   private static record GraphState(GraphStorage storage, GraphEvents events) {
      final GraphStorage storage;
      final GraphEvents events;

      public GraphState(int var1) {
         this(new GraphStorage(var1), new GraphEvents());
      }

      private GraphState(GraphStorage var1, GraphEvents var2) {
         super();
         this.storage = var1;
         this.events = var2;
      }

      public GraphStorage storage() {
         return this.storage;
      }

      public GraphEvents events() {
         return this.events;
      }
   }

   private static class GraphStorage {
      public final SectionToNodeMap sectionToNodeMap;
      public final LinkedHashSet<Node> renderSections;
      public final Long2ObjectMap<List<SectionRenderDispatcher.RenderSection>> chunksWaitingForNeighbors;

      public GraphStorage(int var1) {
         super();
         this.sectionToNodeMap = new SectionToNodeMap(var1);
         this.renderSections = new LinkedHashSet(var1);
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

      public int hashCode() {
         return this.section.getOrigin().hashCode();
      }

      public boolean equals(Object var1) {
         if (!(var1 instanceof Node var2)) {
            return false;
         } else {
            return this.section.getOrigin().equals(var2.section.getOrigin());
         }
      }
   }

   private static record GraphEvents(LongSet chunksWhichReceivedNeighbors, BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom) {
      final LongSet chunksWhichReceivedNeighbors;
      final BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom;

      public GraphEvents() {
         this(new LongOpenHashSet(), new LinkedBlockingQueue());
      }

      private GraphEvents(LongSet var1, BlockingQueue<SectionRenderDispatcher.RenderSection> var2) {
         super();
         this.chunksWhichReceivedNeighbors = var1;
         this.sectionsToPropagateFrom = var2;
      }

      public LongSet chunksWhichReceivedNeighbors() {
         return this.chunksWhichReceivedNeighbors;
      }

      public BlockingQueue<SectionRenderDispatcher.RenderSection> sectionsToPropagateFrom() {
         return this.sectionsToPropagateFrom;
      }
   }

   private static class SectionToNodeMap {
      private final Node[] nodes;

      SectionToNodeMap(int var1) {
         super();
         this.nodes = new Node[var1];
      }

      public void put(SectionRenderDispatcher.RenderSection var1, Node var2) {
         this.nodes[var1.index] = var2;
      }

      @Nullable
      public Node get(SectionRenderDispatcher.RenderSection var1) {
         int var2 = var1.index;
         return var2 >= 0 && var2 < this.nodes.length ? this.nodes[var2] : null;
      }
   }
}
