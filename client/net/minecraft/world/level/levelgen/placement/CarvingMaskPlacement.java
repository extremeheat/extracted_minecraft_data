package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.GenerationStep;

public class CarvingMaskPlacement extends PlacementModifier {
   public static final MapCodec<CarvingMaskPlacement> CODEC;
   private final GenerationStep.Carving step;

   private CarvingMaskPlacement(GenerationStep.Carving var1) {
      super();
      this.step = var1;
   }

   public static CarvingMaskPlacement forStep(GenerationStep.Carving var0) {
      return new CarvingMaskPlacement(var0);
   }

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      ChunkPos var4 = new ChunkPos(var3);
      return var1.getCarvingMask(var4, this.step).stream(var4);
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.CARVING_MASK_PLACEMENT;
   }

   static {
      CODEC = GenerationStep.Carving.CODEC.fieldOf("step").xmap(CarvingMaskPlacement::new, (var0) -> {
         return var0.step;
      });
   }
}
