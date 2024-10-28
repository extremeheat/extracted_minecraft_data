package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;

public class HeightmapPlacement extends PlacementModifier {
   public static final MapCodec<HeightmapPlacement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter((var0x) -> {
         return var0x.heightmap;
      })).apply(var0, HeightmapPlacement::new);
   });
   private final Heightmap.Types heightmap;

   private HeightmapPlacement(Heightmap.Types var1) {
      super();
      this.heightmap = var1;
   }

   public static HeightmapPlacement onHeightmap(Heightmap.Types var0) {
      return new HeightmapPlacement(var0);
   }

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      int var4 = var3.getX();
      int var5 = var3.getZ();
      int var6 = var1.getHeight(this.heightmap, var4, var5);
      return var6 > var1.getMinBuildHeight() ? Stream.of(new BlockPos(var4, var6, var5)) : Stream.of();
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.HEIGHTMAP;
   }
}
