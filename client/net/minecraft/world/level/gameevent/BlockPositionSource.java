package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class BlockPositionSource implements PositionSource {
   public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(BlockPos.CODEC.fieldOf("pos").forGetter(var0x -> var0x.pos)).apply(var0, BlockPositionSource::new)
   );
   final BlockPos pos;

   public BlockPositionSource(BlockPos var1) {
      super();
      this.pos = var1;
   }

   @Override
   public Optional<Vec3> getPosition(Level var1) {
      return Optional.of(Vec3.atCenterOf(this.pos));
   }

   @Override
   public PositionSourceType<?> getType() {
      return PositionSourceType.BLOCK;
   }

   public static class Type implements PositionSourceType<BlockPositionSource> {
      public Type() {
         super();
      }

      public BlockPositionSource read(FriendlyByteBuf var1) {
         return new BlockPositionSource(var1.readBlockPos());
      }

      public void write(FriendlyByteBuf var1, BlockPositionSource var2) {
         var1.writeBlockPos(var2.pos);
      }

      @Override
      public Codec<BlockPositionSource> codec() {
         return BlockPositionSource.CODEC;
      }
   }
}
