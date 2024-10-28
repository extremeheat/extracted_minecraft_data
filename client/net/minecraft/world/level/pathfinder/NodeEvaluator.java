package net.minecraft.world.level.pathfinder;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.PathNavigationRegion;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public abstract class NodeEvaluator {
   protected PathfindingContext currentContext;
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
      this.currentContext = new PathfindingContext(var1, var2);
      this.mob = var2;
      this.nodes.clear();
      this.entityWidth = Mth.floor(var2.getBbWidth() + 1.0F);
      this.entityHeight = Mth.floor(var2.getBbHeight() + 1.0F);
      this.entityDepth = Mth.floor(var2.getBbWidth() + 1.0F);
   }

   public void done() {
      this.currentContext = null;
      this.mob = null;
   }

   protected Node getNode(BlockPos var1) {
      return this.getNode(var1.getX(), var1.getY(), var1.getZ());
   }

   protected Node getNode(int var1, int var2, int var3) {
      return (Node)this.nodes.computeIfAbsent(Node.createHash(var1, var2, var3), (var3x) -> {
         return new Node(var1, var2, var3);
      });
   }

   public abstract Node getStart();

   public abstract Target getTarget(double var1, double var3, double var5);

   protected Target getTargetNodeAt(double var1, double var3, double var5) {
      return new Target(this.getNode(Mth.floor(var1), Mth.floor(var3), Mth.floor(var5)));
   }

   public abstract int getNeighbors(Node[] var1, Node var2);

   public abstract PathType getPathTypeOfMob(PathfindingContext var1, int var2, int var3, int var4, Mob var5);

   public abstract PathType getPathType(PathfindingContext var1, int var2, int var3, int var4);

   public PathType getPathType(Mob var1, BlockPos var2) {
      return this.getPathType(new PathfindingContext(var1.level(), var1), var2.getX(), var2.getY(), var2.getZ());
   }

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

   public static boolean isBurningBlock(BlockState var0) {
      return var0.is(BlockTags.FIRE) || var0.is(Blocks.LAVA) || var0.is(Blocks.MAGMA_BLOCK) || CampfireBlock.isLitCampfire(var0) || var0.is(Blocks.LAVA_CAULDRON);
   }
}
