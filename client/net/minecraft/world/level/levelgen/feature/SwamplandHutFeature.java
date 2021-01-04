package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class SwamplandHutFeature extends RandomScatteredFeature<NoneFeatureConfiguration> {
   private static final List<Biome.SpawnerData> SWAMPHUT_ENEMIES;
   private static final List<Biome.SpawnerData> SWAMPHUT_ANIMALS;

   public SwamplandHutFeature(Function<Dynamic<?>, ? extends NoneFeatureConfiguration> var1) {
      super(var1);
   }

   public String getFeatureName() {
      return "Swamp_Hut";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return SwamplandHutFeature.FeatureStart::new;
   }

   protected int getRandomSalt() {
      return 14357620;
   }

   public List<Biome.SpawnerData> getSpecialEnemies() {
      return SWAMPHUT_ENEMIES;
   }

   public List<Biome.SpawnerData> getSpecialAnimals() {
      return SWAMPHUT_ANIMALS;
   }

   public boolean isSwamphut(LevelAccessor var1, BlockPos var2) {
      StructureStart var3 = this.getStructureAt(var1, var2, true);
      if (var3 != StructureStart.INVALID_START && var3 instanceof SwamplandHutFeature.FeatureStart && !var3.getPieces().isEmpty()) {
         StructurePiece var4 = (StructurePiece)var3.getPieces().get(0);
         return var4 instanceof SwamplandHutPiece;
      } else {
         return false;
      }
   }

   static {
      SWAMPHUT_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.WITCH, 1, 1, 1)});
      SWAMPHUT_ANIMALS = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.CAT, 1, 1, 1)});
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         SwamplandHutPiece var6 = new SwamplandHutPiece(this.random, var3 * 16, var4 * 16);
         this.pieces.add(var6);
         this.calculateBoundingBox();
      }
   }
}
