package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockState extends BlockBehaviour.BlockStateBase {
   public static final Codec<BlockState> CODEC = codec(Registry.BLOCK.byNameCodec(), Block::defaultBlockState).stable();

   public BlockState(Block var1, ImmutableMap<Property<?>, Comparable<?>> var2, MapCodec<BlockState> var3) {
      super(var1, var2, var3);
   }

   @Override
   protected BlockState asState() {
      return this;
   }
}
