package net.minecraft.core;

import io.netty.buffer.ByteBuf;
import java.util.Iterator;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.AABB;

public record BlockBox(BlockPos min, BlockPos max) implements Iterable<BlockPos> {
   public static final StreamCodec<ByteBuf, BlockBox> STREAM_CODEC = new StreamCodec<ByteBuf, BlockBox>() {
      public BlockBox decode(ByteBuf var1) {
         return new BlockBox(FriendlyByteBuf.readBlockPos(var1), FriendlyByteBuf.readBlockPos(var1));
      }

      public void encode(ByteBuf var1, BlockBox var2) {
         FriendlyByteBuf.writeBlockPos(var1, var2.min());
         FriendlyByteBuf.writeBlockPos(var1, var2.max());
      }
   };

   public BlockBox(BlockPos min, BlockPos max) {
      super();
      this.min = BlockPos.min(min, max);
      this.max = BlockPos.max(min, max);
   }

   public static BlockBox of(BlockPos var0) {
      return new BlockBox(var0, var0);
   }

   public static BlockBox of(BlockPos var0, BlockPos var1) {
      return new BlockBox(var0, var1);
   }

   public BlockBox include(BlockPos var1) {
      return new BlockBox(BlockPos.min(this.min, var1), BlockPos.max(this.max, var1));
   }

   public boolean isBlock() {
      return this.min.equals(this.max);
   }

   public boolean contains(BlockPos var1) {
      return var1.getX() >= this.min.getX()
         && var1.getY() >= this.min.getY()
         && var1.getZ() >= this.min.getZ()
         && var1.getX() <= this.max.getX()
         && var1.getY() <= this.max.getY()
         && var1.getZ() <= this.max.getZ();
   }

   public AABB aabb() {
      return AABB.encapsulatingFullBlocks(this.min, this.max);
   }

   @Override
   public Iterator<BlockPos> iterator() {
      return BlockPos.betweenClosed(this.min, this.max).iterator();
   }

   public int sizeX() {
      return this.max.getX() - this.min.getX() + 1;
   }

   public int sizeY() {
      return this.max.getY() - this.min.getY() + 1;
   }

   public int sizeZ() {
      return this.max.getZ() - this.min.getZ() + 1;
   }

   public BlockBox extend(Direction var1, int var2) {
      if (var2 == 0) {
         return this;
      } else {
         return var1.getAxisDirection() == Direction.AxisDirection.POSITIVE
            ? of(this.min, BlockPos.max(this.min, this.max.relative(var1, var2)))
            : of(BlockPos.min(this.min.relative(var1, var2), this.max), this.max);
      }
   }

   public BlockBox move(Direction var1, int var2) {
      return var2 == 0 ? this : new BlockBox(this.min.relative(var1, var2), this.max.relative(var1, var2));
   }

   public BlockBox offset(Vec3i var1) {
      return new BlockBox(this.min.offset(var1), this.max.offset(var1));
   }
}
