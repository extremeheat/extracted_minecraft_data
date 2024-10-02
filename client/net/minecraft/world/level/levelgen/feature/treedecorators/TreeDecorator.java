package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public abstract class TreeDecorator {
   public static final Codec<TreeDecorator> CODEC = BuiltInRegistries.TREE_DECORATOR_TYPE.byNameCodec().dispatch(TreeDecorator::type, TreeDecoratorType::codec);

   public TreeDecorator() {
      super();
   }

   protected abstract TreeDecoratorType<?> type();

   public abstract void place(TreeDecorator.Context var1);

   public static final class Context {
      private final LevelSimulatedReader level;
      private final BiConsumer<BlockPos, BlockState> decorationSetter;
      private final RandomSource random;
      private final ObjectArrayList<BlockPos> logs;
      private final ObjectArrayList<BlockPos> leaves;
      private final ObjectArrayList<BlockPos> roots;

      public Context(
         LevelSimulatedReader var1, BiConsumer<BlockPos, BlockState> var2, RandomSource var3, Set<BlockPos> var4, Set<BlockPos> var5, Set<BlockPos> var6
      ) {
         super();
         this.level = var1;
         this.decorationSetter = var2;
         this.random = var3;
         this.roots = new ObjectArrayList(var6);
         this.logs = new ObjectArrayList(var4);
         this.leaves = new ObjectArrayList(var5);
         this.logs.sort(Comparator.comparingInt(Vec3i::getY));
         this.leaves.sort(Comparator.comparingInt(Vec3i::getY));
         this.roots.sort(Comparator.comparingInt(Vec3i::getY));
      }

      public void placeVine(BlockPos var1, BooleanProperty var2) {
         this.setBlock(var1, Blocks.VINE.defaultBlockState().setValue(var2, Boolean.valueOf(true)));
      }

      public void setBlock(BlockPos var1, BlockState var2) {
         this.decorationSetter.accept(var1, var2);
      }

      public boolean isAir(BlockPos var1) {
         return this.level.isStateAtPosition(var1, BlockBehaviour.BlockStateBase::isAir);
      }

      public boolean checkBlock(BlockPos var1, Predicate<BlockState> var2) {
         return this.level.isStateAtPosition(var1, var2);
      }

      public LevelSimulatedReader level() {
         return this.level;
      }

      public RandomSource random() {
         return this.random;
      }

      public ObjectArrayList<BlockPos> logs() {
         return this.logs;
      }

      public ObjectArrayList<BlockPos> leaves() {
         return this.leaves;
      }

      public ObjectArrayList<BlockPos> roots() {
         return this.roots;
      }
   }
}
