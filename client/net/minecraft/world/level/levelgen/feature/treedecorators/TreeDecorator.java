package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class TreeDecorator {
   public static final Codec<TreeDecorator> CODEC;

   public TreeDecorator() {
      super();
   }

   protected abstract TreeDecoratorType<?> type();

   public abstract void place(LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, Random var3, List<BlockPos> var4, List<BlockPos> var5);

   protected static void placeVine(BiConsumer<BlockPos, BlockState> var0, BlockPos var1, BooleanProperty var2) {
      var0.accept(var1, (BlockState)Blocks.VINE.defaultBlockState().setValue(var2, true));
   }

   static {
      CODEC = Registry.TREE_DECORATOR_TYPES.byNameCodec().dispatch(TreeDecorator::type, TreeDecoratorType::codec);
   }
}
