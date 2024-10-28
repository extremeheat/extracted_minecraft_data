package net.minecraft.world.level.pathfinder;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.profiling.metrics.MetricCategory;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;

public class PathFinder {
   private static final float FUDGING = 1.5F;
   private final Node[] neighbors = new Node[32];
   private final int maxVisitedNodes;
   private final NodeEvaluator nodeEvaluator;
   private static final boolean DEBUG = false;
   private final BinaryHeap openSet = new BinaryHeap();

   public PathFinder(NodeEvaluator var1, int var2) {
      super();
      this.nodeEvaluator = var1;
      this.maxVisitedNodes = var2;
   }

   @Nullable
   public Path findPath(PathNavigationRegion var1, Mob var2, Set<BlockPos> var3, float var4, int var5, float var6) {
      this.openSet.clear();
      this.nodeEvaluator.prepare(var1, var2);
      Node var7 = this.nodeEvaluator.getStart();
      if (var7 == null) {
         return null;
      } else {
         Map var8 = (Map)var3.stream().collect(Collectors.toMap((var1x) -> {
            return this.nodeEvaluator.getTarget((double)var1x.getX(), (double)var1x.getY(), (double)var1x.getZ());
         }, Function.identity()));
         Path var9 = this.findPath(var1.getProfiler(), var7, var8, var4, var5, var6);
         this.nodeEvaluator.done();
         return var9;
      }
   }

   @Nullable
   private Path findPath(ProfilerFiller var1, Node var2, Map<Target, BlockPos> var3, float var4, int var5, float var6) {
      var1.push("find_path");
      var1.markForCharting(MetricCategory.PATH_FINDING);
      Set var7 = var3.keySet();
      var2.g = 0.0F;
      var2.h = this.getBestH(var2, var7);
      var2.f = var2.h;
      this.openSet.clear();
      this.openSet.insert(var2);
      ImmutableSet var8 = ImmutableSet.of();
      int var9 = 0;
      HashSet var10 = Sets.newHashSetWithExpectedSize(var7.size());
      int var11 = (int)((float)this.maxVisitedNodes * var6);

      while(!this.openSet.isEmpty()) {
         ++var9;
         if (var9 >= var11) {
            break;
         }

         Node var12 = this.openSet.pop();
         var12.closed = true;
         Iterator var13 = var7.iterator();

         while(var13.hasNext()) {
            Target var14 = (Target)var13.next();
            if (var12.distanceManhattan((Node)var14) <= (float)var5) {
               var14.setReached();
               var10.add(var14);
            }
         }

         if (!var10.isEmpty()) {
            break;
         }

         if (!(var12.distanceTo(var2) >= var4)) {
            int var19 = this.nodeEvaluator.getNeighbors(this.neighbors, var12);

            for(int var21 = 0; var21 < var19; ++var21) {
               Node var15 = this.neighbors[var21];
               float var16 = this.distance(var12, var15);
               var15.walkedDistance = var12.walkedDistance + var16;
               float var17 = var12.g + var16 + var15.costMalus;
               if (var15.walkedDistance < var4 && (!var15.inOpenSet() || var17 < var15.g)) {
                  var15.cameFrom = var12;
                  var15.g = var17;
                  var15.h = this.getBestH(var15, var7) * 1.5F;
                  if (var15.inOpenSet()) {
                     this.openSet.changeCost(var15, var15.g + var15.h);
                  } else {
                     var15.f = var15.g + var15.h;
                     this.openSet.insert(var15);
                  }
               }
            }
         }
      }

      Optional var18 = !var10.isEmpty() ? var10.stream().map((var2x) -> {
         return this.reconstructPath(var2x.getBestNode(), (BlockPos)var3.get(var2x), true);
      }).min(Comparator.comparingInt(Path::getNodeCount)) : var7.stream().map((var2x) -> {
         return this.reconstructPath(var2x.getBestNode(), (BlockPos)var3.get(var2x), false);
      }).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
      var1.pop();
      if (var18.isEmpty()) {
         return null;
      } else {
         Path var20 = (Path)var18.get();
         return var20;
      }
   }

   protected float distance(Node var1, Node var2) {
      return var1.distanceTo(var2);
   }

   private float getBestH(Node var1, Set<Target> var2) {
      float var3 = 3.4028235E38F;

      float var6;
      for(Iterator var4 = var2.iterator(); var4.hasNext(); var3 = Math.min(var6, var3)) {
         Target var5 = (Target)var4.next();
         var6 = var1.distanceTo((Node)var5);
         var5.updateBest(var6, var1);
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

   // $FF: synthetic method
   private static Node[] lambda$findPath$3(int var0) {
      return new Node[var0];
   }
}
