package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.util.RandomSource;

public abstract class PlacementModifier {
   public static final Codec<PlacementModifier> CODEC;

   public PlacementModifier() {
      super();
   }

   public abstract Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3);

   public abstract PlacementModifierType<?> type();

   static {
      CODEC = Registry.PLACEMENT_MODIFIERS.byNameCodec().dispatch(PlacementModifier::type, PlacementModifierType::codec);
   }
}
