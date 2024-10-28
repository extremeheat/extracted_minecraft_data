package net.minecraft.core.particles;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class BlockParticleOption implements ParticleOptions {
   private static final Codec<BlockState> BLOCK_STATE_CODEC;
   private final ParticleType<BlockParticleOption> type;
   private final BlockState state;

   public static MapCodec<BlockParticleOption> codec(ParticleType<BlockParticleOption> var0) {
      return BLOCK_STATE_CODEC.xmap((var1) -> {
         return new BlockParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.state;
      }).fieldOf("block_state");
   }

   public static StreamCodec<? super RegistryFriendlyByteBuf, BlockParticleOption> streamCodec(ParticleType<BlockParticleOption> var0) {
      return ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY).map((var1) -> {
         return new BlockParticleOption(var0, var1);
      }, (var0x) -> {
         return var0x.state;
      });
   }

   public BlockParticleOption(ParticleType<BlockParticleOption> var1, BlockState var2) {
      super();
      this.type = var1;
      this.state = var2;
   }

   public ParticleType<BlockParticleOption> getType() {
      return this.type;
   }

   public BlockState getState() {
      return this.state;
   }

   static {
      BLOCK_STATE_CODEC = Codec.withAlternative(BlockState.CODEC, BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState);
   }
}
