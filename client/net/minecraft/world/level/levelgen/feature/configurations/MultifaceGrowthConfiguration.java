package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;

public class MultifaceGrowthConfiguration implements FeatureConfiguration {
   public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create((i) -> i.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success).orElse((MultifaceSpreadeableBlock)Blocks.GLOW_LICHEN).forGetter((c) -> c.placeBlock), Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter((c) -> c.searchRange), Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter((c) -> c.canPlaceOnFloor), Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter((c) -> c.canPlaceOnCeiling), Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter((c) -> c.canPlaceOnWall), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter((c) -> c.chanceOfSpreading), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_be_placed_on").forGetter((c) -> c.canBePlacedOn)).apply(i, MultifaceGrowthConfiguration::new));
   public final MultifaceSpreadeableBlock placeBlock;
   public final int searchRange;
   public final boolean canPlaceOnFloor;
   public final boolean canPlaceOnCeiling;
   public final boolean canPlaceOnWall;
   public final float chanceOfSpreading;
   public final HolderSet<Block> canBePlacedOn;
   private final ObjectArrayList<Direction> validDirections;

   private static DataResult<MultifaceSpreadeableBlock> apply(final Block block) {
      DataResult var10000;
      if (block instanceof MultifaceSpreadeableBlock multifaceBlock) {
         var10000 = DataResult.success(multifaceBlock);
      } else {
         var10000 = DataResult.error(() -> "Growth block should be a multiface spreadeable block");
      }

      return var10000;
   }

   public MultifaceGrowthConfiguration(final MultifaceSpreadeableBlock placeBlock, final int searchRange, final boolean canPlaceOnFloor, final boolean canPlaceOnCeiling, final boolean canPlaceOnWall, final float chanceOfSpreading, final HolderSet<Block> canBePlacedOn) {
      super();
      this.placeBlock = placeBlock;
      this.searchRange = searchRange;
      this.canPlaceOnFloor = canPlaceOnFloor;
      this.canPlaceOnCeiling = canPlaceOnCeiling;
      this.canPlaceOnWall = canPlaceOnWall;
      this.chanceOfSpreading = chanceOfSpreading;
      this.canBePlacedOn = canBePlacedOn;
      this.validDirections = new ObjectArrayList(6);
      if (canPlaceOnCeiling) {
         this.validDirections.add(Direction.UP);
      }

      if (canPlaceOnFloor) {
         this.validDirections.add(Direction.DOWN);
      }

      if (canPlaceOnWall) {
         Direction.Plane var10000 = Direction.Plane.HORIZONTAL;
         ObjectArrayList var10001 = this.validDirections;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
      }

   }

   public List<Direction> getShuffledDirectionsExcept(final RandomSource random, final Direction excludeDirection) {
      return Util.toShuffledList(this.validDirections.stream().filter((direction) -> direction != excludeDirection), random);
   }

   public List<Direction> getShuffledDirections(final RandomSource random) {
      return Util.shuffledCopy(this.validDirections, random);
   }
}
