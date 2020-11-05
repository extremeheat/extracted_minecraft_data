package net.minecraft.world.level;

import java.util.Objects;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CollisionSpliterator extends AbstractSpliterator<VoxelShape> {
   @Nullable
   private final Entity source;
   private final AABB box;
   private final CollisionContext context;
   private final Cursor3D cursor;
   private final BlockPos.MutableBlockPos pos;
   private final VoxelShape entityShape;
   private final CollisionGetter collisionGetter;
   private boolean needsBorderCheck;
   private final BiPredicate<BlockState, BlockPos> predicate;

   public CollisionSpliterator(CollisionGetter var1, @Nullable Entity var2, AABB var3) {
      this(var1, var2, var3, (var0, var1x) -> {
         return true;
      });
   }

   public CollisionSpliterator(CollisionGetter var1, @Nullable Entity var2, AABB var3, BiPredicate<BlockState, BlockPos> var4) {
      super(9223372036854775807L, 1280);
      this.context = var2 == null ? CollisionContext.empty() : CollisionContext.of(var2);
      this.pos = new BlockPos.MutableBlockPos();
      this.entityShape = Shapes.create(var3);
      this.collisionGetter = var1;
      this.needsBorderCheck = var2 != null;
      this.source = var2;
      this.box = var3;
      this.predicate = var4;
      int var5 = Mth.floor(var3.minX - 1.0E-7D) - 1;
      int var6 = Mth.floor(var3.maxX + 1.0E-7D) + 1;
      int var7 = Mth.floor(var3.minY - 1.0E-7D) - 1;
      int var8 = Mth.floor(var3.maxY + 1.0E-7D) + 1;
      int var9 = Mth.floor(var3.minZ - 1.0E-7D) - 1;
      int var10 = Mth.floor(var3.maxZ + 1.0E-7D) + 1;
      this.cursor = new Cursor3D(var5, var7, var9, var6, var8, var10);
   }

   public boolean tryAdvance(Consumer<? super VoxelShape> var1) {
      return this.needsBorderCheck && this.worldBorderCheck(var1) || this.collisionCheck(var1);
   }

   boolean collisionCheck(Consumer<? super VoxelShape> var1) {
      while(true) {
         if (this.cursor.advance()) {
            int var2 = this.cursor.nextX();
            int var3 = this.cursor.nextY();
            int var4 = this.cursor.nextZ();
            int var5 = this.cursor.getNextType();
            if (var5 == 3) {
               continue;
            }

            BlockGetter var6 = this.getChunk(var2, var4);
            if (var6 == null) {
               continue;
            }

            this.pos.set(var2, var3, var4);
            BlockState var7 = var6.getBlockState(this.pos);
            if (!this.predicate.test(var7, this.pos) || var5 == 1 && !var7.hasLargeCollisionShape() || var5 == 2 && !var7.is(Blocks.MOVING_PISTON)) {
               continue;
            }

            VoxelShape var8 = var7.getCollisionShape(this.collisionGetter, this.pos, this.context);
            if (var8 == Shapes.block()) {
               if (!this.box.intersects((double)var2, (double)var3, (double)var4, (double)var2 + 1.0D, (double)var3 + 1.0D, (double)var4 + 1.0D)) {
                  continue;
               }

               var1.accept(var8.move((double)var2, (double)var3, (double)var4));
               return true;
            }

            VoxelShape var9 = var8.move((double)var2, (double)var3, (double)var4);
            if (!Shapes.joinIsNotEmpty(var9, this.entityShape, BooleanOp.AND)) {
               continue;
            }

            var1.accept(var9);
            return true;
         }

         return false;
      }
   }

   @Nullable
   private BlockGetter getChunk(int var1, int var2) {
      int var3 = SectionPos.blockToSectionCoord(var1);
      int var4 = SectionPos.blockToSectionCoord(var2);
      return this.collisionGetter.getChunkForCollisions(var3, var4);
   }

   boolean worldBorderCheck(Consumer<? super VoxelShape> var1) {
      Objects.requireNonNull(this.source);
      this.needsBorderCheck = false;
      WorldBorder var2 = this.collisionGetter.getWorldBorder();
      AABB var3 = this.source.getBoundingBox();
      if (!isBoxFullyWithinWorldBorder(var2, var3)) {
         VoxelShape var4 = var2.getCollisionShape();
         if (!isOutsideBorder(var4, var3) && isCloseToBorder(var4, var3)) {
            var1.accept(var4);
            return true;
         }
      }

      return false;
   }

   private static boolean isCloseToBorder(VoxelShape var0, AABB var1) {
      return Shapes.joinIsNotEmpty(var0, Shapes.create(var1.inflate(1.0E-7D)), BooleanOp.AND);
   }

   private static boolean isOutsideBorder(VoxelShape var0, AABB var1) {
      return Shapes.joinIsNotEmpty(var0, Shapes.create(var1.deflate(1.0E-7D)), BooleanOp.AND);
   }

   public static boolean isBoxFullyWithinWorldBorder(WorldBorder var0, AABB var1) {
      double var2 = (double)Mth.floor(var0.getMinX());
      double var4 = (double)Mth.floor(var0.getMinZ());
      double var6 = (double)Mth.ceil(var0.getMaxX());
      double var8 = (double)Mth.ceil(var0.getMaxZ());
      return var1.minX > var2 && var1.minX < var6 && var1.minZ > var4 && var1.minZ < var8 && var1.maxX > var2 && var1.maxX < var6 && var1.maxZ > var4 && var1.maxZ < var8;
   }
}
