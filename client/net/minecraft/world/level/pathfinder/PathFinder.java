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
import net.minecraft.world.level.LevelReader;

public class PathFinder {
   private final BinaryHeap openSet = new BinaryHeap();
   private final Set<Node> closedSet = Sets.newHashSet();
   private final Node[] neighbors = new Node[32];
   private final int maxVisitedNodes;
   private NodeEvaluator nodeEvaluator;

   public PathFinder(NodeEvaluator var1, int var2) {
      super();
      this.nodeEvaluator = var1;
      this.maxVisitedNodes = var2;
   }

   @Nullable
   public Path findPath(LevelReader var1, Mob var2, Set<BlockPos> var3, float var4, int var5) {
      this.openSet.clear();
      this.nodeEvaluator.prepare(var1, var2);
      Node var6 = this.nodeEvaluator.getStart();
      Map var7 = (Map)var3.stream().collect(Collectors.toMap((var1x) -> {
         return this.nodeEvaluator.getGoal((double)var1x.getX(), (double)var1x.getY(), (double)var1x.getZ());
      }, Function.identity()));
      Path var8 = this.findPath(var6, var7, var4, var5);
      this.nodeEvaluator.done();
      return var8;
   }

   @Nullable
   private Path findPath(Node var1, Map<Target, BlockPos> var2, float var3, int var4) {
      Set var5 = var2.keySet();
      var1.g = 0.0F;
      var1.h = this.getBestH(var1, var5);
      var1.f = var1.h;
      this.openSet.clear();
      this.closedSet.clear();
      this.openSet.insert(var1);
      int var6 = 0;

      while(!this.openSet.isEmpty()) {
         ++var6;
         if (var6 >= this.maxVisitedNodes) {
            break;
         }

         Node var7 = this.openSet.pop();
         var7.closed = true;
         var5.stream().filter((var2x) -> {
            return var7.distanceManhattan((Node)var2x) <= (float)var4;
         }).forEach(Target::setReached);
         if (var5.stream().anyMatch(Target::isReached)) {
            break;
         }

         if (var7.distanceTo(var1) < var3) {
            int var8 = this.nodeEvaluator.getNeighbors(this.neighbors, var7);

            for(int var9 = 0; var9 < var8; ++var9) {
               Node var10 = this.neighbors[var9];
               float var11 = var7.distanceTo(var10);
               var10.walkedDistance = var7.walkedDistance + var11;
               float var12 = var7.g + var11 + var10.costMalus;
               if (var10.walkedDistance < var3 && (!var10.inOpenSet() || var12 < var10.g)) {
                  var10.cameFrom = var7;
                  var10.g = var12;
                  var10.h = this.getBestH(var10, var5) * 1.5F;
                  if (var10.inOpenSet()) {
                     this.openSet.changeCost(var10, var10.g + var10.h);
                  } else {
                     var10.f = var10.g + var10.h;
                     this.openSet.insert(var10);
                  }
               }
            }
         }
      }

      Stream var13;
      if (var5.stream().anyMatch(Target::isReached)) {
         var13 = var5.stream().filter(Target::isReached).map((var2x) -> {
            return this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), true);
         }).sorted(Comparator.comparingInt(Path::getSize));
      } else {
         var13 = var5.stream().map((var2x) -> {
            return this.reconstructPath(var2x.getBestNode(), (BlockPos)var2.get(var2x), false);
         }).sorted(Comparator.comparingDouble(Path::getDistToTarget).thenComparingInt(Path::getSize));
      }

      Optional var14 = var13.findFirst();
      if (!var14.isPresent()) {
         return null;
      } else {
         Path var15 = (Path)var14.get();
         return var15;
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
