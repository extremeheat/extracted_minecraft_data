package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

public class PowerfulPotatoBlock extends Block {
   public static final MapCodec<PowerfulPotatoBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("roots").forGetter(var0x -> var0x.plant), propertiesCodec())
            .apply(var0, PowerfulPotatoBlock::new)
   );
   public static final IntegerProperty SPROUTS = BlockStateProperties.AGE_3;
   public static final int MAX_SPROUTS = 3;
   private final Block plant;

   @Override
   public MapCodec<PowerfulPotatoBlock> codec() {
      return CODEC;
   }

   protected PowerfulPotatoBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.plant = var1;
      this.registerDefaultState(this.stateDefinition.any().setValue(SPROUTS, Integer.valueOf(0)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(SPROUTS);
   }

   @Override
   protected boolean isRandomlyTicking(BlockState var1) {
      return var1.getValue(SPROUTS) < 3;
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      int var5 = var1.getValue(SPROUTS);
      if (var5 < 3) {
         StrongRootsBlock.GROWTH_DIRECTION.getRandomValue(var4).ifPresent(var5x -> {
            List var6 = StrongRootsBlock.tryPlace(var2, var3.relative(var5x), var4);
            if (var6 != null) {
               BlockPos var7 = var3.above();
               var6.forEach(var2xx -> popResource(var2, var7, var2xx));
               var2.setBlock(var3, var1.setValue(SPROUTS, Integer.valueOf(var5 + 1)), 4);
            }
         });
      }
   }
}
