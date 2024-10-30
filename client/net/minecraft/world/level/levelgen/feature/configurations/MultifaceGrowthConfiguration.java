package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MultifaceSpreadeableBlock;

public class MultifaceGrowthConfiguration implements FeatureConfiguration {
   public static final Codec<MultifaceGrowthConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("block").flatXmap(MultifaceGrowthConfiguration::apply, DataResult::success).orElse((MultifaceSpreadeableBlock)Blocks.GLOW_LICHEN).forGetter((var0x) -> {
         return var0x.placeBlock;
      }), Codec.intRange(1, 64).fieldOf("search_range").orElse(10).forGetter((var0x) -> {
         return var0x.searchRange;
      }), Codec.BOOL.fieldOf("can_place_on_floor").orElse(false).forGetter((var0x) -> {
         return var0x.canPlaceOnFloor;
      }), Codec.BOOL.fieldOf("can_place_on_ceiling").orElse(false).forGetter((var0x) -> {
         return var0x.canPlaceOnCeiling;
      }), Codec.BOOL.fieldOf("can_place_on_wall").orElse(false).forGetter((var0x) -> {
         return var0x.canPlaceOnWall;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance_of_spreading").orElse(0.5F).forGetter((var0x) -> {
         return var0x.chanceOfSpreading;
      }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("can_be_placed_on").forGetter((var0x) -> {
         return var0x.canBePlacedOn;
      })).apply(var0, MultifaceGrowthConfiguration::new);
   });
   public final MultifaceSpreadeableBlock placeBlock;
   public final int searchRange;
   public final boolean canPlaceOnFloor;
   public final boolean canPlaceOnCeiling;
   public final boolean canPlaceOnWall;
   public final float chanceOfSpreading;
   public final HolderSet<Block> canBePlacedOn;
   private final ObjectArrayList<Direction> validDirections;

   private static DataResult<MultifaceSpreadeableBlock> apply(Block var0) {
      DataResult var10000;
      if (var0 instanceof MultifaceSpreadeableBlock var1) {
         var10000 = DataResult.success(var1);
      } else {
         var10000 = DataResult.error(() -> {
            return "Growth block should be a multiface spreadeable block";
         });
      }

      return var10000;
   }

   public MultifaceGrowthConfiguration(MultifaceSpreadeableBlock var1, int var2, boolean var3, boolean var4, boolean var5, float var6, HolderSet<Block> var7) {
      super();
      this.placeBlock = var1;
      this.searchRange = var2;
      this.canPlaceOnFloor = var3;
      this.canPlaceOnCeiling = var4;
      this.canPlaceOnWall = var5;
      this.chanceOfSpreading = var6;
      this.canBePlacedOn = var7;
      this.validDirections = new ObjectArrayList(6);
      if (var4) {
         this.validDirections.add(Direction.UP);
      }

      if (var3) {
         this.validDirections.add(Direction.DOWN);
      }

      if (var5) {
         Direction.Plane var10000 = Direction.Plane.HORIZONTAL;
         ObjectArrayList var10001 = this.validDirections;
         Objects.requireNonNull(var10001);
         var10000.forEach(var10001::add);
      }

   }

   public List<Direction> getShuffledDirectionsExcept(RandomSource var1, Direction var2) {
      return Util.toShuffledList(this.validDirections.stream().filter((var1x) -> {
         return var1x != var2;
      }), var1);
   }

   public List<Direction> getShuffledDirections(RandomSource var1) {
      return Util.shuffledCopy(this.validDirections, var1);
   }
}
