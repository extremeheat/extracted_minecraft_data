package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ForestFlowerProvider extends BlockStateProvider {
   private static final BlockState[] FLOWERS;

   public ForestFlowerProvider() {
      super(BlockStateProviderType.FOREST_FLOWER_PROVIDER);
   }

   public ForestFlowerProvider(Dynamic var1) {
      this();
   }

   public BlockState getState(Random var1, BlockPos var2) {
      double var3 = Mth.clamp((1.0D + Biome.BIOME_INFO_NOISE.getValue((double)var2.getX() / 48.0D, (double)var2.getZ() / 48.0D, false)) / 2.0D, 0.0D, 0.9999D);
      return FLOWERS[(int)(var3 * (double)FLOWERS.length)];
   }

   public Object serialize(DynamicOps var1) {
      Builder var2 = ImmutableMap.builder();
      var2.put(var1.createString("type"), var1.createString(Registry.BLOCKSTATE_PROVIDER_TYPES.getKey(this.type).toString()));
      return (new Dynamic(var1, var1.createMap(var2.build()))).getValue();
   }

   static {
      FLOWERS = new BlockState[]{Blocks.DANDELION.defaultBlockState(), Blocks.POPPY.defaultBlockState(), Blocks.ALLIUM.defaultBlockState(), Blocks.AZURE_BLUET.defaultBlockState(), Blocks.RED_TULIP.defaultBlockState(), Blocks.ORANGE_TULIP.defaultBlockState(), Blocks.WHITE_TULIP.defaultBlockState(), Blocks.PINK_TULIP.defaultBlockState(), Blocks.OXEYE_DAISY.defaultBlockState(), Blocks.CORNFLOWER.defaultBlockState(), Blocks.LILY_OF_THE_VALLEY.defaultBlockState()};
   }
}
