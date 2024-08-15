package net.minecraft.world.level;

import com.google.common.collect.AbstractIterator;
import java.util.function.BiFunction;
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

public class BlockCollisions<T> extends AbstractIterator<T> {
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
   private final BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> resultProvider;

   public BlockCollisions(CollisionGetter var1, @Nullable Entity var2, AABB var3, boolean var4, BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> var5) {
      this(var1, var2 == null ? CollisionContext.empty() : CollisionContext.of(var2), var3, var4, var5);
   }

   public BlockCollisions(CollisionGetter var1, CollisionContext var2, AABB var3, boolean var4, BiFunction<BlockPos.MutableBlockPos, VoxelShape, T> var5) {
      super();
      this.context = var2;
      this.pos = new BlockPos.MutableBlockPos();
      this.entityShape = Shapes.create(var3);
      this.collisionGetter = var1;
      this.box = var3;
      this.onlySuffocatingBlocks = var4;
      this.resultProvider = var5;
      int var6 = Mth.floor(var3.minX - 1.0E-7) - 1;
      int var7 = Mth.floor(var3.maxX + 1.0E-7) + 1;
      int var8 = Mth.floor(var3.minY - 1.0E-7) - 1;
      int var9 = Mth.floor(var3.maxY + 1.0E-7) + 1;
      int var10 = Mth.floor(var3.minZ - 1.0E-7) - 1;
      int var11 = Mth.floor(var3.maxZ + 1.0E-7) + 1;
      this.cursor = new Cursor3D(var6, var8, var10, var7, var9, var11);
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

   protected T computeNext() {
      while (this.cursor.advance()) {
         int var1 = this.cursor.nextX();
         int var2 = this.cursor.nextY();
         int var3 = this.cursor.nextZ();
         int var4 = this.cursor.getNextType();
         if (var4 != 3) {
            BlockGetter var5 = this.getChunk(var1, var3);
            if (var5 != null) {
               this.pos.set(var1, var2, var3);
               BlockState var6 = var5.getBlockState(this.pos);
               if ((!this.onlySuffocatingBlocks || var6.isSuffocating(var5, this.pos))
                  && (var4 != 1 || var6.hasLargeCollisionShape())
                  && (var4 != 2 || var6.is(Blocks.MOVING_PISTON))) {
                  VoxelShape var7 = var6.getCollisionShape(this.collisionGetter, this.pos, this.context);
                  if (var7 == Shapes.block()) {
                     if (this.box.intersects((double)var1, (double)var2, (double)var3, (double)var1 + 1.0, (double)var2 + 1.0, (double)var3 + 1.0)) {
                        return this.resultProvider.apply(this.pos, var7.move((double)var1, (double)var2, (double)var3));
                     }
                  } else {
                     VoxelShape var8 = var7.move((double)var1, (double)var2, (double)var3);
                     if (!var8.isEmpty() && Shapes.joinIsNotEmpty(var8, this.entityShape, BooleanOp.AND)) {
                        return this.resultProvider.apply(this.pos, var8);
                     }
                  }
               }
            }
         }
      }

      return (T)this.endOfData();
   }
}
