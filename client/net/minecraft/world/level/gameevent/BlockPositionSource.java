package net.minecraft.world.level.gameevent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;

public class BlockPositionSource implements PositionSource {
   public static final Codec<BlockPositionSource> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockPos.CODEC.fieldOf("pos").xmap(Optional::of, Optional::get).forGetter((var0x) -> {
         return var0x.pos;
      })).apply(var0, BlockPositionSource::new);
   });
   final Optional<BlockPos> pos;

   public BlockPositionSource(BlockPos var1) {
      this(Optional.of(var1));
   }

   public BlockPositionSource(Optional<BlockPos> var1) {
      super();
      this.pos = var1;
   }

   public Optional<BlockPos> getPosition(Level var1) {
      return this.pos;
   }

   public PositionSourceType<?> getType() {
      return PositionSourceType.BLOCK;
   }

   public static class Type implements PositionSourceType<BlockPositionSource> {
      public Type() {
         super();
      }

      public BlockPositionSource read(FriendlyByteBuf var1) {
         return new BlockPositionSource(Optional.of(var1.readBlockPos()));
      }

      public void write(FriendlyByteBuf var1, BlockPositionSource var2) {
         Optional var10000 = var2.pos;
         Objects.requireNonNull(var1);
         var10000.ifPresent(var1::writeBlockPos);
      }

      public Codec<BlockPositionSource> codec() {
         return BlockPositionSource.CODEC;
      }

      // $FF: synthetic method
      public PositionSource read(FriendlyByteBuf var1) {
         return this.read(var1);
      }
   }
}
