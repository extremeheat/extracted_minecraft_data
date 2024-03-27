package net.minecraft.world.level.gameevent;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource implements PositionSource {
   public static final MapCodec<BlockPositionSource> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BlockPos.CODEC.fieldOf("pos").forGetter(var0x -> var0x.pos)).apply(var0, BlockPositionSource::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, BlockPositionSource> STREAM_CODEC = StreamCodec.composite(
      BlockPos.STREAM_CODEC, var0 -> var0.pos, BlockPositionSource::new
   );
   private final BlockPos pos;

   public BlockPositionSource(BlockPos var1) {
      super();
      this.pos = var1;
   }

   @Override
   public Optional<Vec3> getPosition(Level var1) {
      return Optional.of(Vec3.atCenterOf(this.pos));
   }

   @Override
   public PositionSourceType<BlockPositionSource> getType() {
      return PositionSourceType.BLOCK;
   }

   public static class Type implements PositionSourceType<BlockPositionSource> {
      public Type() {
         super();
      }

      @Override
      public MapCodec<BlockPositionSource> codec() {
         return BlockPositionSource.CODEC;
      }

      @Override
      public StreamCodec<RegistryFriendlyByteBuf, BlockPositionSource> streamCodec() {
         return BlockPositionSource.STREAM_CODEC;
      }
   }
}
