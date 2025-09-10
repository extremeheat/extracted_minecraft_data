package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;

public class PathFinder {
   private static final float FUDGING = 1.5F;
   private final Node[] neighbors = new Node[32];
   private int maxVisitedNodes;
   private final NodeEvaluator nodeEvaluator;
   private final BinaryHeap openSet = new BinaryHeap();
   private BooleanSupplier captureDebug = () -> false;

   public PathFinder(NodeEvaluator var1, int var2) {
      super();
      this.nodeEvaluator = var1;
      this.maxVisitedNodes = var2;
   }

   public void setCaptureDebug(BooleanSupplier var1) {
      this.captureDebug = var1;
   }

   public void setMaxVisitedNodes(int var1) {
      this.maxVisitedNodes = var1;
   }

   @Nullable
   public Path findPath(PathNavigationRegion var1, Mob var2, Set<BlockPos> var3, float var4, int var5, float var6) {
      this.openSet.clear();
      this.nodeEvaluator.prepare(var1, var2);
      Node var7 = this.nodeEvaluator.getStart();
      if (var7 == null) {
         return null;
      } else {
         Map var8 = (Map)var3.stream().collect(Collectors.toMap((var1x) -> this.nodeEvaluator.getTarget((double)var1x.getX(), (double)var1x.getY(), (double)var1x.getZ()), Function.identity()));
         Path var9 = this.findPath(var7, var8, var4, var5, var6);
         this.nodeEvaluator.done();
         return var9;
      }
   }

   @Nullable
   private Path findPath(Node var1, Map<Target, BlockPos> var2, float var3, int var4, float var5) {
      ProfilerFiller var6 = Profiler.get();
      var6.push("find_path");
      var6.markForCharting(MetricCategory.PATH_FINDING);
      Set var7 = var2.keySet();
      var1.g = 0.0F;
      var1.h = this.getBestH(var1, var7);
      var1.f = var1.h;
      this.openSet.clear();
      this.openSet.insert(var1);
      boolean var8 = this.captureDebug.getAsBoolean();
      Object var9 = var8 ? new HashSet() : Set.of();
      int var10 = 0;
      HashSet var11 = Sets.newHashSetWithExpectedSize(var7.size());
      int var12 = (int)((float)this.maxVisitedNodes * var5);

      while(!this.openSet.isEmpty()) {
         ++var10;
         if (var10 >= var12) {
            break;
         }

         Node var13 = this.openSet.pop();
         var13.closed = true;

         for(Target var15 : var7) {
            if (var13.distanceManhattan((Node)var15) <= (float)var4) {
               var15.setReached();
               var11.add(var15);
            }
         }

         if (!var11.isEmpty()) {
            break;
         }

         if (var8) {
            ((Set)var9).add(var13);
         }

         if (!(var13.distanceTo(var1) >= var3)) {
            int var20 = this.nodeEvaluator.getNeighbors(this.neighbors, var13);

            for(int var22 = 0; var22 < var20; ++var22) {
               Node var16 = this.neighbors[var22];
               float var17 = this.distance(var13, var16);
               var16.walkedDistance = var13.walkedDistance + var17;
               float var18 = var13.g + var17 + var16.costMalus;
               if (var16.walkedDistance < var3 && (!var16.inOpenSet() || var18 < var16.g)) {
                  var16.cameFrom = var13;
                  var16.g = var18;
                  var16.h = this.getBestH(var16, var7) * 1.5F;
                  if (var16.inOpenSet()) {
                     this.openSet.changeCost(var16, var16.g + var16.h);
                  } else {
                     var16.f = var16.g + var16.h;
                     this.openSet.insert(var16);
                  }
               }
            }
         }
      }

      Optional var19 = !var11.isEmpty() ? var11.stream().map((var2x) -> this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), true)).min(Comparator.comparingInt(Path::getNodeCount)) : var7.stream().map((var2x) -> this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), false)).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
      var6.pop();
      if (var19.isEmpty()) {
         return null;
      } else {
         Path var21 = (Path)var19.get();
         if (var8) {
            var21.setDebug(this.openSet.getHeap(), (Node[])((Set)var9).toArray((var0) -> new Node[var0]), var7);
         }

         return var21;
      }
   }

   protected float distance(Node var1, Node var2) {
      return var1.distanceTo(var2);
   }

   private float getBestH(Node var1, Set<Target> var2) {
      float var3 = 3.4028235E38F;

      for(Target var5 : var2) {
         float var6 = var1.distanceTo((Node)var5);
         var5.updateBest(var6, var1);
         var3 = Math.min(var6, var3);
      }

      return var3;
   }

   private Path reconstructPath(Node var1, BlockPos var2, boolean var3) {
      ArrayList var4 = Lists.newArrayList();
      Node var5 = var1;
      var4.add(0, var1);

      while(var5.cameFrom != null) {
         var5 = var5.cameFrom;
         var4.add(0, var5);
      }

      return new Path(var4, var2, var3);
   }
}
