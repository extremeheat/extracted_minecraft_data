package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.apache.commons.lang3.mutable.MutableInt;

public class FossilFeature extends Feature<FossilFeatureConfiguration> {
   public FossilFeature(Codec<FossilFeatureConfiguration> var1) {
      super(var1);
   }

   @Override
   public boolean place(FeaturePlaceContext<FossilFeatureConfiguration> var1) {
      RandomSource var2 = var1.random();
      WorldGenLevel var3 = var1.level();
      BlockPos var4 = var1.origin();
      Rotation var5 = Rotation.getRandom(var2);
      FossilFeatureConfiguration var6 = (FossilFeatureConfiguration)var1.config();
      int var7 = var2.nextInt(var6.fossilStructures.size());
      StructureTemplateManager var8 = var3.getLevel().getServer().getStructureManager();
      StructureTemplate var9 = var8.getOrCreate(var6.fossilStructures.get(var7));
      StructureTemplate var10 = var8.getOrCreate(var6.overlayStructures.get(var7));
      ChunkPos var11 = new ChunkPos(var4);
      BoundingBox var12 = new BoundingBox(
         var11.getMinBlockX() - 16,
         var3.getMinBuildHeight(),
         var11.getMinBlockZ() - 16,
         var11.getMaxBlockX() + 16,
         var3.getMaxBuildHeight(),
         var11.getMaxBlockZ() + 16
      );
      StructurePlaceSettings var13 = new StructurePlaceSettings().setRotation(var5).setBoundingBox(var12).setRandom(var2);
      Vec3i var14 = var9.getSize(var5);
      BlockPos var15 = var4.offset(-var14.getX() / 2, 0, -var14.getZ() / 2);
      int var16 = var4.getY();

      for(int var17 = 0; var17 < var14.getX(); ++var17) {
         for(int var18 = 0; var18 < var14.getZ(); ++var18) {
            var16 = Math.min(var16, var3.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var15.getX() + var17, var15.getZ() + var18));
         }
      }

      int var19 = Math.max(var16 - 15 - var2.nextInt(10), var3.getMinBuildHeight() + 10);
      BlockPos var20 = var9.getZeroPositionWithTransform(var15.atY(var19), Mirror.NONE, var5);
      if (countEmptyCorners(var3, var9.getBoundingBox(var13, var20)) > var6.maxEmptyCornersAllowed) {
         return false;
      } else {
         var13.clearProcessors();
         var6.fossilProcessors.value().list().forEach(var13::addProcessor);
         var9.placeInWorld(var3, var20, var20, var13, var2, 4);
         var13.clearProcessors();
         var6.overlayProcessors.value().list().forEach(var13::addProcessor);
         var10.placeInWorld(var3, var20, var20, var13, var2, 4);
         return true;
      }
   }

   private static int countEmptyCorners(WorldGenLevel var0, BoundingBox var1) {
      MutableInt var2 = new MutableInt(0);
      var1.forAllCorners(var2x -> {
         BlockState var3 = var0.getBlockState(var2x);
         if (var3.isAir() || var3.is(Blocks.LAVA) || var3.is(Blocks.WATER)) {
            var2.add(1);
         }
      });
      return var2.getValue();
   }
}
