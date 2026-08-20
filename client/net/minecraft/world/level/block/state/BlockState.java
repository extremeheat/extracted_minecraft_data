package net.minecraft.world.level.block.state;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockState extends BlockBehaviour.BlockStateBase {
   private static final Codec<Either<Block, BlockState>> CONSTANT_OR_DISPATCH_CODEC;
   public static final Codec<BlockState> CODEC;

   public BlockState(final Block owner, final Property<?>[] propertyKeys, final Comparable<?>[] propertyValues) {
      super(owner, propertyKeys, propertyValues);
   }

   protected BlockState asState() {
      return this;
   }

   static {
      CONSTANT_OR_DISPATCH_CODEC = Codec.either(BuiltInRegistries.BLOCK.byNameCodec(), codec(BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState, Block::getStateDefinition).stable());
      CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap((either) -> (BlockState)either.map(Block::defaultBlockState, (f) -> f), (state) -> state == state.getBlock().defaultBlockState() ? Either.left(state.getBlock()) : Either.right(state));
   }
}
