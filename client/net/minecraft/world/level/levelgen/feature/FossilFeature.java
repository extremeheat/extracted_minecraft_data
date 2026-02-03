package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
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
   public FossilFeature(final Codec<FossilFeatureConfiguration> codec) {
      super(codec);
   }

   public boolean place(final FeaturePlaceContext<FossilFeatureConfiguration> context) {
      RandomSource random = context.random();
      WorldGenLevel level = context.level();
      BlockPos origin = context.origin();
      Rotation rotation = Rotation.getRandom(random);
      FossilFeatureConfiguration config = context.config();
      int fossilIndex = random.nextInt(config.fossilStructures.size());
      StructureTemplateManager structureTemplateManager = level.getLevel().getServer().getStructureManager();
      StructureTemplate fossilBase = structureTemplateManager.getOrCreate((Identifier)config.fossilStructures.get(fossilIndex));
      StructureTemplate fossilOverlay = structureTemplateManager.getOrCreate((Identifier)config.overlayStructures.get(fossilIndex));
      ChunkPos chunkPos = ChunkPos.containing(origin);
      BoundingBox boundingBox = new BoundingBox(chunkPos.getMinBlockX() - 16, level.getMinY(), chunkPos.getMinBlockZ() - 16, chunkPos.getMaxBlockX() + 16, level.getMaxY(), chunkPos.getMaxBlockZ() + 16);
      StructurePlaceSettings settings = (new StructurePlaceSettings()).setRotation(rotation).setBoundingBox(boundingBox).setRandom(random);
      Vec3i size = fossilBase.getSize(rotation);
      BlockPos lowCorner = origin.offset(-size.getX() / 2, 0, -size.getZ() / 2);
      int lowestSurfaceY = origin.getY();

      for(int xscan = 0; xscan < size.getX(); ++xscan) {
         for(int zscan = 0; zscan < size.getZ(); ++zscan) {
            lowestSurfaceY = Math.min(lowestSurfaceY, level.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, lowCorner.getX() + xscan, lowCorner.getZ() + zscan));
         }
      }

      int targetY = Math.max(lowestSurfaceY - 15 - random.nextInt(10), level.getMinY() + 10);
      BlockPos targetPos = fossilBase.getZeroPositionWithTransform(lowCorner.atY(targetY), Mirror.NONE, rotation);
      if (countEmptyCorners(level, fossilBase.getBoundingBox(settings, targetPos)) > config.maxEmptyCornersAllowed) {
         return false;
      } else {
         settings.clearProcessors();
         List var10000 = (config.fossilProcessors.value()).list();
         Objects.requireNonNull(settings);
         var10000.forEach(settings::addProcessor);
         fossilBase.placeInWorld(level, targetPos, targetPos, settings, random, 260);
         settings.clearProcessors();
         var10000 = (config.overlayProcessors.value()).list();
         Objects.requireNonNull(settings);
         var10000.forEach(settings::addProcessor);
         fossilOverlay.placeInWorld(level, targetPos, targetPos, settings, random, 260);
         return true;
      }
   }

   private static int countEmptyCorners(final WorldGenLevel level, final BoundingBox structureBounds) {
      MutableInt count = new MutableInt(0);
      structureBounds.forAllCorners((pos) -> {
         BlockState state = level.getBlockState(pos);
         if (state.isAir() || state.is(Blocks.LAVA) || state.is(Blocks.WATER)) {
            count.add(1);
         }

      });
      return count.intValue();
   }
}
