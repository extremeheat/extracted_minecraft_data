package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.PathNavigationRegion;

public abstract class NodeEvaluator {
   protected PathNavigationRegion level;
   protected Mob mob;
   protected final Int2ObjectMap<Node> nodes = new Int2ObjectOpenHashMap();
   protected int entityWidth;
   protected int entityHeight;
   protected int entityDepth;
   protected boolean canPassDoors;
   protected boolean canOpenDoors;
   protected boolean canFloat;
   protected boolean canWalkOverFences;

   public NodeEvaluator() {
      super();
   }

   public void prepare(PathNavigationRegion var1, Mob var2) {
      this.level = var1;
      this.mob = var2;
      this.nodes.clear();
      this.entityWidth = Mth.floor(var2.getBbWidth() + 1.0F);
      this.entityHeight = Mth.floor(var2.getBbHeight() + 1.0F);
      this.entityDepth = Mth.floor(var2.getBbWidth() + 1.0F);
   }

   public void done() {
      this.level = null;
      this.mob = null;
   }

   protected Node getNode(BlockPos var1) {
      return this.getNode(var1.getX(), var1.getY(), var1.getZ());
   }

   protected Node getNode(int var1, int var2, int var3) {
      return (Node)this.nodes.computeIfAbsent(Node.createHash(var1, var2, var3), var3x -> new Node(var1, var2, var3));
   }

   public abstract Node getStart();

   public abstract Target getGoal(double var1, double var3, double var5);

   protected Target getTargetFromNode(Node var1) {
      return new Target(var1);
   }

   public abstract int getNeighbors(Node[] var1, Node var2);

   public abstract BlockPathTypes getBlockPathType(
      BlockGetter var1, int var2, int var3, int var4, Mob var5, int var6, int var7, int var8, boolean var9, boolean var10
   );

   public abstract BlockPathTypes getBlockPathType(BlockGetter var1, int var2, int var3, int var4);

   public void setCanPassDoors(boolean var1) {
      this.canPassDoors = var1;
   }

   public void setCanOpenDoors(boolean var1) {
      this.canOpenDoors = var1;
   }

   public void setCanFloat(boolean var1) {
      this.canFloat = var1;
   }

   public void setCanWalkOverFences(boolean var1) {
      this.canWalkOverFences = var1;
   }

   public boolean canPassDoors() {
      return this.canPassDoors;
   }

   public boolean canOpenDoors() {
      return this.canOpenDoors;
   }

   public boolean canFloat() {
      return this.canFloat;
   }

   public boolean canWalkOverFences() {
      return this.canWalkOverFences;
   }
}
