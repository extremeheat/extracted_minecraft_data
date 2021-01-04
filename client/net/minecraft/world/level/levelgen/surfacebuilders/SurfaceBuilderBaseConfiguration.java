package net.minecraft.world.level.levelgen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class SurfaceBuilderBaseConfiguration implements SurfaceBuilderConfiguration {
   private final BlockState topMaterial;
   private final BlockState underMaterial;
   private final BlockState underwaterMaterial;

   public SurfaceBuilderBaseConfiguration(BlockState var1, BlockState var2, BlockState var3) {
      super();
      this.topMaterial = var1;
      this.underMaterial = var2;
      this.underwaterMaterial = var3;
   }

   public BlockState getTopMaterial() {
      return this.topMaterial;
   }

   public BlockState getUnderMaterial() {
      return this.underMaterial;
   }

   public BlockState getUnderwaterMaterial() {
      return this.underwaterMaterial;
   }

   public static SurfaceBuilderBaseConfiguration deserialize(Dynamic<?> var0) {
      BlockState var1 = (BlockState)var0.get("top_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      BlockState var2 = (BlockState)var0.get("under_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      BlockState var3 = (BlockState)var0.get("underwater_material").map(BlockState::deserialize).orElse(Blocks.AIR.defaultBlockState());
      return new SurfaceBuilderBaseConfiguration(var1, var2, var3);
   }
}
