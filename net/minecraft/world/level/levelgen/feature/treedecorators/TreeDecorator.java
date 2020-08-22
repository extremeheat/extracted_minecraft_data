package net.minecraft.world.level.levelgen.feature.treedecorators;

import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Serializable;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public abstract class TreeDecorator implements Serializable {
   protected final TreeDecoratorType type;

   protected TreeDecorator(TreeDecoratorType var1) {
      this.type = var1;
   }

   public abstract void place(LevelAccessor var1, Random var2, List var3, List var4, Set var5, BoundingBox var6);

   protected void placeVine(LevelWriter var1, BlockPos var2, BooleanProperty var3, Set var4, BoundingBox var5) {
      this.setBlock(var1, var2, (BlockState)Blocks.VINE.defaultBlockState().setValue(var3, true), var4, var5);
   }

   protected void setBlock(LevelWriter var1, BlockPos var2, BlockState var3, Set var4, BoundingBox var5) {
      var1.setBlock(var2, var3, 19);
      var4.add(var2);
      var5.expand(new BoundingBox(var2, var2));
   }
}
