package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class PlainFlowerProvider extends BlockStateProvider {
   private static final BlockState[] LOW_NOISE_FLOWERS;
   private static final BlockState[] HIGH_NOISE_FLOWERS;

   public PlainFlowerProvider() {
      super(BlockStateProviderType.PLAIN_FLOWER_PROVIDER);
   }

   public PlainFlowerProvider(Dynamic var1) {
      this();
   }

   public BlockState getState(Random var1, BlockPos var2) {
      double var3 = Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 200.0D, (double)var2.getZ() / 200.0D, false);
      if (var3 < -0.8D) {
         return LOW_NOISE_FLOWERS[var1.nextInt(LOW_NOISE_FLOWERS.length)];
      } else {
         return var1.nextInt(3) > 0 ? HIGH_NOISE_FLOWERS[var1.nextInt(HIGH_NOISE_FLOWERS.length)] : Blocks.DANDELION.defaultBlockState();
      }
   }

   public Object serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("type"), var1.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.type).toString()));
      return (new Dynamic(var1, var1.createMap(var2.build()))).getValue();
   }

   static {
      LOW_NOISE_FLOWERS = new BlockState[]{Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState()};
      HIGH_NOISE_FLOWERS = new BlockState[]{Blocks.POPPY.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState()};
   }
}
