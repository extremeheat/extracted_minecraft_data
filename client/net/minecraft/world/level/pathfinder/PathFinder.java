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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;

public class PathFinder {
   private final Node[] neighbors = new Node[32];
   private final int maxVisitedNodes;
   private final NodeEvaluator nodeEvaluator;
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
      Map var8 = (Map)var3.stream().collect(Collectors.toMap((var1x) -> {
         return this.nodeEvaluator.getGoal((double)var1x.getX(), (double)var1x.getY(), (double)var1x.getZ());
      }, Function.identity()));
      Path var9 = this.findPath(var7, var8, var4, var5, var6);
      this.nodeEvaluator.done();
      return var9;
   }

   @Nullable
   private Path findPath(Node var1, Map<Target, BlockPos> var2, float var3, int var4, float var5) {
      Set var6 = var2.keySet();
      var1.g = 0.0F;
      var1.h = this.getBestH(var1, var6);
      var1.f = var1.h;
      this.openSet.clear();
      this.openSet.insert(var1);
      ImmutableSet var7 = ImmutableSet.of();
      int var8 = 0;
      HashSet var9 = Sets.newHashSetWithExpectedSize(var6.size());
      int var10 = (int)((float)this.maxVisitedNodes * var5);

      while(!this.openSet.isEmpty()) {
         ++var8;
         if (var8 >= var10) {
            break;
         }

         Node var11 = this.openSet.pop();
         var11.closed = true;
         Iterator var12 = var6.iterator();

         while(var12.hasNext()) {
            Target var13 = (Target)var12.next();
            if (var11.distanceManhattan((Node)var13) <= (float)var4) {
               var13.setReached();
               var9.add(var13);
            }
         }

         if (!var9.isEmpty()) {
            break;
         }

         if (var11.distanceTo(var1) < var3) {
            int var18 = this.nodeEvaluator.getNeighbors(this.neighbors, var11);

            for(int var20 = 0; var20 < var18; ++var20) {
               Node var14 = this.neighbors[var20];
               float var15 = var11.distanceTo(var14);
               var14.walkedDistance = var11.walkedDistance + var15;
               float var16 = var11.g + var15 + var14.costMalus;
               if (var14.walkedDistance < var3 && (!var14.inOpenSet() || var16 < var14.g)) {
                  var14.cameFrom = var11;
                  var14.g = var16;
                  var14.h = this.getBestH(var14, var6) * 1.5F;
                  if (var14.inOpenSet()) {
                     this.openSet.changeCost(var14, var14.g + var14.h);
                  } else {
                     var14.f = var14.g + var14.h;
                     this.openSet.insert(var14);
                  }
               }
            }
         }
      }

      Optional var17 = !var9.isEmpty() ? var9.stream().map((var2x) -> {
         return this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), true);
      }).min(Comparator.comparingInt(Path::getNodeCount)) : var6.stream().map((var2x) -> {
         return this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), false);
      }).min(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getNodeCount));
      if (!var17.isPresent()) {
         return null;
      } else {
         Path var19 = (Path)var17.get();
         return var19;
      }
   }

   private float getBestH(Node var1, Set<Target> var2) {
      float var3 = 3.4028235E38F;

      float var6;
      for(Iterator var4 = var2.iterator(); var4.hasNext(); var3 = Math.min(var6, var3)) {
         Target var5 = (Target)var4.next();
         var6 = var1.distanceTo(var5);
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
}
