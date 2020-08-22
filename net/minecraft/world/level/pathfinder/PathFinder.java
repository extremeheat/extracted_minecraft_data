package net.minecraft.world.level.pathfinder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;

public class PathFinder {
   private final BinaryHeap openSet = new BinaryHeap();
   private final Set closedSet = Sets.newHashSet();
   private final Node[] neighbors = new Node[32];
   private final int maxVisitedNodes;
   private final NodeEvaluator nodeEvaluator;

   public PathFinder(NodeEvaluator var1, int var2) {
      this.nodeEvaluator = var1;
      this.maxVisitedNodes = var2;
   }

   @Nullable
   public Path findPath(PathNavigationRegion var1, Mob var2, Set var3, float var4, int var5, float var6) {
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
   private Path findPath(Node var1, Map var2, float var3, int var4, float var5) {
      Set var6 = var2.keySet();
      var1.g = 0.0F;
      var1.h = this.getBestH(var1, var6);
      var1.f = var1.h;
      this.openSet.clear();
      this.closedSet.clear();
      this.openSet.insert(var1);
      int var7 = 0;
      int var8 = (int)((float)this.maxVisitedNodes * var5);

      while(!this.openSet.isEmpty()) {
         ++var7;
         if (var7 >= var8) {
            break;
         }

         Node var9 = this.openSet.pop();
         var9.closed = true;
         var6.stream().filter((var2x) -> {
            return var9.distanceManhattan((Node)var2x) <= (float)var4;
         }).forEach(Target::setReached);
         if (var6.stream().anyMatch(Target::isReached)) {
            break;
         }

         if (var9.distanceTo(var1) < var3) {
            int var10 = this.nodeEvaluator.getNeighbors(this.neighbors, var9);

            for(int var11 = 0; var11 < var10; ++var11) {
               Node var12 = this.neighbors[var11];
               float var13 = var9.distanceTo(var12);
               var12.walkedDistance = var9.walkedDistance + var13;
               float var14 = var9.g + var13 + var12.costMalus;
               if (var12.walkedDistance < var3 && (!var12.inOpenSet() || var14 < var12.g)) {
                  var12.cameFrom = var9;
                  var12.g = var14;
                  var12.h = this.getBestH(var12, var6) * 1.5F;
                  if (var12.inOpenSet()) {
                     this.openSet.changeCost(var12, var12.g + var12.h);
                  } else {
                     var12.f = var12.g + var12.h;
                     this.openSet.insert(var12);
                  }
               }
            }
         }
      }

      Stream var15;
      if (var6.stream().anyMatch(Target::isReached)) {
         var15 = var6.stream().filter(Target::isReached).map((var2x) -> {
            return this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), true);
         }).sorted(Comparator.comparingInt(Path::getSize));
      } else {
         var15 = var6.stream().map((var2x) -> {
            return this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), false);
         }).sorted(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getSize));
      }

      Optional var16 = var15.findFirst();
      if (!var16.isPresent()) {
         return null;
      } else {
         Path var17 = (Path)var16.get();
         return var17;
      }
   }

   private float getBestH(Node var1, Set var2) {
      float var3 = Float.MAX_VALUE;

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
