package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

public class InSquarePlacement extends PlacementModifier {
   private static final InSquarePlacement INSTANCE = new InSquarePlacement();
   public static final MapCodec<InSquarePlacement> CODEC = MapCodec.unit(() -> {
      return INSTANCE;
   });

   public InSquarePlacement() {
      super();
   }

   public static InSquarePlacement spread() {
      return INSTANCE;
   }

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      int var4 = var2.nextInt(16) + var3.getX();
      int var5 = var2.nextInt(16) + var3.getZ();
      return Stream.of(new BlockPos(var4, var3.getY(), var5));
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.IN_SQUARE;
   }
}
