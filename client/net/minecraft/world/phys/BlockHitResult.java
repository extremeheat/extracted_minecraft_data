package net.minecraft.world.phys;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.StreamCodec;

public class BlockHitResult extends HitResult {
   public static final StreamCodec<ByteBuf, BlockHitResult> STREAM_CODEC = new StreamCodec<ByteBuf, BlockHitResult>() {
      public BlockHitResult decode(final ByteBuf input) {
         BlockPos pos = (BlockPos)BlockPos.STREAM_CODEC.decode(input);
         Direction face = (Direction)Direction.STREAM_CODEC.decode(input);
         float clickX = input.readFloat();
         float clickY = input.readFloat();
         float clickZ = input.readFloat();
         boolean inside = input.readBoolean();
         boolean worldBorder = input.readBoolean();
         return new BlockHitResult(new Vec3((double)pos.getX() + (double)clickX, (double)pos.getY() + (double)clickY, (double)pos.getZ() + (double)clickZ), face, pos, inside, worldBorder);
      }

      public void encode(final ByteBuf output, final BlockHitResult blockHit) {
         BlockPos blockPos = blockHit.getBlockPos();
         BlockPos.STREAM_CODEC.encode(output, blockPos);
         Direction.STREAM_CODEC.encode(output, blockHit.getDirection());
         Vec3 location = blockHit.getLocation();
         output.writeFloat((float)(location.x - (double)blockPos.getX()));
         output.writeFloat((float)(location.y - (double)blockPos.getY()));
         output.writeFloat((float)(location.z - (double)blockPos.getZ()));
         output.writeBoolean(blockHit.isInside());
         output.writeBoolean(blockHit.isWorldBorderHit());
      }
   };
   private final Direction direction;
   private final BlockPos blockPos;
   private final boolean miss;
   private final boolean inside;
   private final boolean worldBorderHit;

   public static BlockHitResult miss(final Vec3 location, final Direction direction, final BlockPos pos) {
      return new BlockHitResult(true, location, direction, pos, false, false);
   }

   public BlockHitResult(final Vec3 location, final Direction direction, final BlockPos pos, final boolean inside) {
      this(false, location, direction, pos, inside, false);
   }

   public BlockHitResult(final Vec3 location, final Direction direction, final BlockPos pos, final boolean inside, final boolean worldBorderHit) {
      this(false, location, direction, pos, inside, worldBorderHit);
   }

   private BlockHitResult(final boolean miss, final Vec3 location, final Direction direction, final BlockPos blockPos, final boolean inside, final boolean worldBorderHit) {
      super(location);
      this.miss = miss;
      this.direction = direction;
      this.blockPos = blockPos;
      this.inside = inside;
      this.worldBorderHit = worldBorderHit;
   }

   public BlockHitResult withDirection(final Direction direction) {
      return new BlockHitResult(this.miss, this.location, direction, this.blockPos, this.inside, this.worldBorderHit);
   }

   public BlockHitResult withPosition(final BlockPos blockPos) {
      return new BlockHitResult(this.miss, this.location, this.direction, blockPos, this.inside, this.worldBorderHit);
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
