package net.minecraft.world.level;

import com.google.common.collect.AbstractIterator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BlockCollisions extends AbstractIterator<VoxelShape> {
   private final AABB box;
   private final CollisionContext context;
   private final Cursor3D cursor;
   private final BlockPos.MutableBlockPos pos;
   private final VoxelShape entityShape;
   private final CollisionGetter collisionGetter;
   private final boolean onlySuffocatingBlocks;
   @Nullable
   private BlockGetter cachedBlockGetter;
   private long cachedBlockGetterPos;

   public BlockCollisions(CollisionGetter var1, @Nullable Entity var2, AABB var3) {
      this(var1, var2, var3, false);
   }

   public BlockCollisions(CollisionGetter var1, @Nullable Entity var2, AABB var3, boolean var4) {
      super();
      this.context = var2 == null ? CollisionContext.empty() : CollisionContext.method_14(var2);
      this.pos = new BlockPos.MutableBlockPos();
      this.entityShape = Shapes.create(var3);
      this.collisionGetter = var1;
      this.box = var3;
      this.onlySuffocatingBlocks = var4;
      int var5 = Mth.floor(var3.minX - 1.0E-7D) - 1;
      int var6 = Mth.floor(var3.maxX + 1.0E-7D) + 1;
      int var7 = Mth.floor(var3.minY - 1.0E-7D) - 1;
      int var8 = Mth.floor(var3.maxY + 1.0E-7D) + 1;
      int var9 = Mth.floor(var3.minZ - 1.0E-7D) - 1;
      int var10 = Mth.floor(var3.maxZ + 1.0E-7D) + 1;
      this.cursor = new Cursor3D(var5, var7, var9, var6, var8, var10);
   }

   @Nullable
   private BlockGetter getChunk(int var1, int var2) {
      int var3 = SectionPos.blockToSectionCoord(var1);
      int var4 = SectionPos.blockToSectionCoord(var2);
      long var5 = ChunkPos.asLong(var3, var4);
      if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == var5) {
         return this.cachedBlockGetter;
      } else {
         BlockGetter var7 = this.collisionGetter.getChunkForCollisions(var3, var4);
         this.cachedBlockGetter = var7;
         this.cachedBlockGetterPos = var5;
         return var7;
      }
   }

   protected VoxelShape computeNext() {
      while(true) {
         if (this.cursor.advance()) {
            int var1 = this.cursor.nextX();
            int var2 = this.cursor.nextY();
            int var3 = this.cursor.nextZ();
            int var4 = this.cursor.getNextType();
            if (var4 == 3) {
               continue;
            }

            BlockGetter var5 = this.getChunk(var1, var3);
            if (var5 == null) {
               continue;
            }

            this.pos.set(var1, var2, var3);
            BlockState var6 = var5.getBlockState(this.pos);
            if (this.onlySuffocatingBlocks && !var6.isSuffocating(var5, this.pos) || var4 == 1 && !var6.hasLargeCollisionShape() || var4 == 2 && !var6.is(Blocks.MOVING_PISTON)) {
               continue;
            }

            VoxelShape var7 = var6.getCollisionShape(this.collisionGetter, this.pos, this.context);
            if (var7 == Shapes.block()) {
               if (!this.box.intersects((double)var1, (double)var2, (double)var3, (double)var1 + 1.0D, (double)var2 + 1.0D, (double)var3 + 1.0D)) {
                  continue;
               }

               return var7.move((double)var1, (double)var2, (double)var3);
            }

            VoxelShape var8 = var7.move((double)var1, (double)var2, (double)var3);
            if (!Shapes.joinIsNotEmpty(var8, this.entityShape, BooleanOp.AND)) {
               continue;
            }

            return var8;
         }

         return (VoxelShape)this.endOfData();
      }
   }

   // $FF: synthetic method
   protected Object computeNext() {
      return this.computeNext();
   }
}
