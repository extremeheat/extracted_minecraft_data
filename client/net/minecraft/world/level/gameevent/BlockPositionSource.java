package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource implements PositionSource {
   public static final MapCodec<BlockPositionSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockPos.CODEC.fieldOf("pos").forGetter((var0x) -> {
         return var0x.pos;
      })).apply(var0, BlockPositionSource::new);
   });
   public static final StreamCodec<ByteBuf, BlockPositionSource> STREAM_CODEC;
   private final BlockPos pos;

   public BlockPositionSource(BlockPos var1) {
      super();
      this.pos = var1;
   }

   public Optional<Vec3> getPosition(Level var1) {
      return Optional.of(Vec3.atCenterOf(this.pos));
   }

   public PositionSourceType<BlockPositionSource> getType() {
      return PositionSourceType.BLOCK;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, (var0) -> {
         return var0.pos;
      }, BlockPositionSource::new);
   }

   public static class Type implements PositionSourceType<BlockPositionSource> {
      public Type() {
         super();
      }

      public MapCodec<BlockPositionSource> codec() {
         return BlockPositionSource.CODEC;
      }

      public StreamCodec<ByteBuf, BlockPositionSource> streamCodec() {
         return BlockPositionSource.STREAM_CODEC;
      }
   }
}
