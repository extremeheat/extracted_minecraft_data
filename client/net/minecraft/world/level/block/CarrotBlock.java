package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CarrotBlock extends CropBlock {
   public static final MapCodec<CarrotBlock> CODEC = simpleCodec(CarrotBlock::new);
   private static final VoxelShape[] SHAPES = Block.boxes(7, (var0) -> Block.column(16.0, 0.0, (double)(2 + var0)));

   public MapCodec<CarrotBlock> codec() {
      return CODEC;
   }

   public CarrotBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected ItemLike getBaseSeedId() {
      return Items.CARROT;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPES[this.getAge(var1)];
   }
}
