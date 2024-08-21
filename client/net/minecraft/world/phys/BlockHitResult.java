package net.minecraft.world.phys;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockHitResult extends HitResult {
   private final Direction direction;
   private final BlockPos blockPos;
   private final boolean miss;
   private final boolean inside;
   private final boolean worldBorderHit;

   public static BlockHitResult miss(Vec3 var0, Direction var1, BlockPos var2) {
      return new BlockHitResult(true, var0, var1, var2, false, false);
   }

   public BlockHitResult(Vec3 var1, Direction var2, BlockPos var3, boolean var4) {
      this(false, var1, var2, var3, var4, false);
   }

   public BlockHitResult(Vec3 var1, Direction var2, BlockPos var3, boolean var4, boolean var5) {
      this(false, var1, var2, var3, var4, var5);
   }

   private BlockHitResult(boolean var1, Vec3 var2, Direction var3, BlockPos var4, boolean var5, boolean var6) {
      super(var2);
      this.miss = var1;
      this.direction = var3;
      this.blockPos = var4;
      this.inside = var5;
      this.worldBorderHit = var6;
   }

   public BlockHitResult withDirection(Direction var1) {
      return new BlockHitResult(this.miss, this.location, var1, this.blockPos, this.inside, this.worldBorderHit);
   }

   public BlockHitResult withPosition(BlockPos var1) {
      return new BlockHitResult(this.miss, this.location, this.direction, var1, this.inside, this.worldBorderHit);
   }

   public BlockHitResult hitBorder() {
      return new BlockHitResult(this.miss, this.location, this.direction, this.blockPos, this.inside, true);
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   public Direction getDirection() {
      return this.direction;
   }

   @Override
   public HitResult.Type getType() {
      return this.miss ? HitResult.Type.MISS : HitResult.Type.BLOCK;
   }

   public boolean isInside() {
      return this.inside;
   }

   public boolean isWorldBorderHit() {
      return this.worldBorderHit;
   }
}
