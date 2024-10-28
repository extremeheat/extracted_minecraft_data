package net.minecraft.world.level.levelgen.placement;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;

public class FixedPlacement extends PlacementModifier {
   public static final MapCodec<FixedPlacement> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BlockPos.CODEC.listOf().fieldOf("positions").forGetter((var0x) -> {
         return var0x.positions;
      })).apply(var0, FixedPlacement::new);
   });
   private final List<BlockPos> positions;

   public static FixedPlacement of(BlockPos... var0) {
      return new FixedPlacement(List.of(var0));
   }

   private FixedPlacement(List<BlockPos> var1) {
      super();
      this.positions = var1;
   }

   public Stream<BlockPos> getPositions(PlacementContext var1, RandomSource var2, BlockPos var3) {
      int var4 = SectionPos.blockToSectionCoord(var3.getX());
      int var5 = SectionPos.blockToSectionCoord(var3.getZ());
      boolean var6 = false;
      Iterator var7 = this.positions.iterator();

      while(var7.hasNext()) {
         BlockPos var8 = (BlockPos)var7.next();
         if (isSameChunk(var4, var5, var8)) {
            var6 = true;
            break;
         }
      }

      return !var6 ? Stream.empty() : this.positions.stream().filter((var2x) -> {
         return isSameChunk(var4, var5, var2x);
      });
   }

   private static boolean isSameChunk(int var0, int var1, BlockPos var2) {
      return var0 == SectionPos.blockToSectionCoord(var2.getX()) && var1 == SectionPos.blockToSectionCoord(var2.getZ());
   }

   public PlacementModifierType<?> type() {
      return PlacementModifierType.FIXED_PLACEMENT;
   }
}
