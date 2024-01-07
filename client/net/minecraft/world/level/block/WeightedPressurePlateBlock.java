package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class WeightedPressurePlateBlock extends BasePressurePlateBlock {
   public static final MapCodec<WeightedPressurePlateBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(
               Codec.intRange(1, 1024).fieldOf("max_weight").forGetter(var0x -> var0x.maxWeight),
               BlockSetType.CODEC.fieldOf("block_set_type").forGetter(var0x -> var0x.type),
               propertiesCodec()
            )
            .apply(var0, WeightedPressurePlateBlock::new)
   );
   public static final IntegerProperty POWER = BlockStateProperties.POWER;
   private final int maxWeight;

   @Override
   public MapCodec<WeightedPressurePlateBlock> codec() {
      return CODEC;
   }

   protected WeightedPressurePlateBlock(int var1, BlockSetType var2, BlockBehaviour.Properties var3) {
      super(var3, var2);
      this.registerDefaultState(this.stateDefinition.any().setValue(POWER, Integer.valueOf(0)));
      this.maxWeight = var1;
   }

   @Override
   protected int getSignalStrength(Level var1, BlockPos var2) {
      int var3 = Math.min(getEntityCount(var1, TOUCH_AABB.move(var2), Entity.class), this.maxWeight);
      if (var3 > 0) {
         float var4 = (float)Math.min(this.maxWeight, var3) / (float)this.maxWeight;
         return Mth.ceil(var4 * 15.0F);
      } else {
         return 0;
      }
   }

   @Override
   protected int getSignalForState(BlockState var1) {
      return var1.getValue(POWER);
   }

   @Override
   protected BlockState setSignalForState(BlockState var1, int var2) {
      return var1.setValue(POWER, Integer.valueOf(var2));
   }

   @Override
   protected int getPressedTime() {
      return 10;
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWER);
   }
}
