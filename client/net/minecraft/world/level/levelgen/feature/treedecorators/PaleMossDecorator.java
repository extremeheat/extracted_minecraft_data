package net.minecraft.world.level.levelgen.feature.treedecorators;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HangingMossBlock;
import net.minecraft.world.level.block.MossyCarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.apache.commons.lang3.mutable.MutableObject;

public class PaleMossDecorator extends TreeDecorator {
   public static final MapCodec<PaleMossDecorator> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("leaves_probability").forGetter((var0x) -> {
         return var0x.leavesProbability;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("trunk_probability").forGetter((var0x) -> {
         return var0x.trunkProbability;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("ground_probability").forGetter((var0x) -> {
         return var0x.groundProbability;
      })).apply(var0, PaleMossDecorator::new);
   });
   private final float leavesProbability;
   private final float trunkProbability;
   private final float groundProbability;

   protected TreeDecoratorType<?> type() {
      return TreeDecoratorType.PALE_MOSS;
   }

   public PaleMossDecorator(float var1, float var2, float var3) {
      super();
      this.leavesProbability = var1;
      this.trunkProbability = var2;
      this.groundProbability = var3;
   }

   public void place(TreeDecorator.Context var1) {
      RandomSource var2 = var1.random();
      WorldGenLevel var3 = (WorldGenLevel)var1.level();
      List var4 = Util.shuffledCopy(var1.logs(), var2);
      if (!var4.isEmpty()) {
         MutableObject var5 = new MutableObject((BlockPos)var4.getFirst());
         var4.forEach((var1x) -> {
            if (var1x.getY() < ((BlockPos)var5.getValue()).getY()) {
               var5.setValue(var1x);
            }

         });
         BlockPos var6 = (BlockPos)var5.getValue();
         if (var2.nextFloat() < this.groundProbability) {
            var3.registryAccess().lookup(Registries.CONFIGURED_FEATURE).flatMap((var0) -> {
               return var0.get(VegetationFeatures.PALE_MOSS_PATCH_BONEMEAL);
            }).ifPresent((var3x) -> {
               ((ConfiguredFeature)var3x.value()).place(var3, var3.getLevel().getChunkSource().getGenerator(), var2, var6.above());
            });
         }

         var1.logs().forEach((var3x) -> {
            BlockPos var4;
            if (var2.nextFloat() < this.trunkProbability) {
               var4 = var3x.below();
               if (var1.isAir(var4)) {
                  addMossHanger(var4, var1);
               }
            }

            if (var2.nextFloat() < this.trunkProbability) {
               var4 = var3x.above();
               if (var1.isAir(var4)) {
                  MossyCarpetBlock.placeAt((WorldGenLevel)var1.level(), var4, var1.random(), 3);
               }
            }

         });
         var1.leaves().forEach((var3x) -> {
            if (var2.nextFloat() < this.leavesProbability) {
               BlockPos var4 = var3x.below();
               if (var1.isAir(var4)) {
                  addMossHanger(var4, var1);
               }
            }

         });
      }
   }

   private static void addMossHanger(BlockPos var0, TreeDecorator.Context var1) {
      while(var1.isAir(var0.below()) && !((double)var1.random().nextFloat() < 0.5)) {
         var1.setBlock(var0, (BlockState)Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, false));
         var0 = var0.below();
      }

      var1.setBlock(var0, (BlockState)Blocks.PALE_HANGING_MOSS.defaultBlockState().setValue(HangingMossBlock.TIP, true));
   }
}
