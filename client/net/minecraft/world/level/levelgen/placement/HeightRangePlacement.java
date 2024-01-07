package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.heightproviders.TrapezoidHeight;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;

public class HeightRangePlacement extends PlacementModifier {
   public static final Codec<HeightRangePlacement> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(HeightProvider.CODEC.fieldOf("height").forGetter(var0x -> var0x.height)).apply(var0, HeightRangePlacement::new)
   );
   private final HeightProvider height;

   private HeightRangePlacement(HeightProvider var1) {
      super();
      this.height = var1;
   }

   public static HeightRangePlacement of(HeightProvider var0) {
      return new HeightRangePlacement(var0);
   }

   public static HeightRangePlacement uniform(VerticalAnchor var0, VerticalAnchor var1) {
      return of(UniformHeight.of(var0, var1));
   }

   public static HeightRangePlacement triangle(VerticalAnchor var0, VerticalAnchor var1) {
      return of(TrapezoidHeight.of(var0, var1));
   }

   @Override
   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      return Stream.of(var3.atY(this.height.sample(var2, var1)));
   }

   @Override
   public PlacementModifierType<?> type() {
      return PlacementModifierType.HEIGHT_RANGE;
   }
}
