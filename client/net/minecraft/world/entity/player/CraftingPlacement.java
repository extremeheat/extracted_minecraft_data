package net.minecraft.world.entity.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public record CraftingPlacement(BlockPos blockPos, BlockState state, boolean required) {
   public static final Codec<CraftingPlacement> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPos.CODEC.fieldOf("block_pos").forGetter(CraftingPlacement::blockPos), BlockState.CODEC.fieldOf("state").forGetter(CraftingPlacement::state), Codec.BOOL.fieldOf("required").forGetter(CraftingPlacement::required)).apply(i, CraftingPlacement::new));
   public static final StreamCodec<ByteBuf, CraftingPlacement> STREAM_CODEC;

   public CraftingPlacement {
      super();
   }

   public boolean isFence() {
      return this.required && this.state.is(Blocks.NETHER_BRICK_FENCE);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, CraftingPlacement::blockPos, ByteBufCodecs.VAR_INT.map(Block::stateById, Block::getId), CraftingPlacement::state, ByteBufCodecs.BOOL, CraftingPlacement::required, CraftingPlacement::new);
   }
}
