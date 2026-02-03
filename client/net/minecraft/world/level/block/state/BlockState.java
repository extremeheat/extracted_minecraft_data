package net.minecraft.world.level.block.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockState extends BlockBehaviour.BlockStateBase {
   public static final Codec<BlockState> CODEC;

   public BlockState(final Block owner, final Reference2ObjectArrayMap<Property<?>, Comparable<?>> values, final MapCodec<BlockState> propertiesCodec) {
      super(owner, values, propertiesCodec);
   }

   protected BlockState asState() {
      return this;
   }

   static {
      CODEC = codec(BuiltInRegistries.BLOCK.byNameCodec(), Block::defaultBlockState).stable();
   }
}
