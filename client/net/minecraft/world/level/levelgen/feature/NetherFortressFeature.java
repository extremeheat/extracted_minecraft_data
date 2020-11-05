package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class NetherFortressFeature extends StructureFeature<NoneFeatureConfiguration> {
   private static final List<MobSpawnSettings.SpawnerData> FORTRESS_ENEMIES;

   public NetherFortressFeature(Codec<NoneFeatureConfiguration> var1) {
      super(var1);
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, NoneFeatureConfiguration var10) {
      return var5.nextInt(5) < 2;
   }

   public StructureFeature.StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
      return NetherFortressFeature.NetherBridgeStart::new;
   }

   public List<MobSpawnSettings.SpawnerData> getSpecialEnemies() {
      return FORTRESS_ENEMIES;
   }

   static {
      FORTRESS_ENEMIES = ImmutableList.of(new MobSpawnSettings.SpawnerData(EntityType.BLAZE, 10, 2, 3), new MobSpawnSettings.SpawnerData(EntityType.ZOMBIFIED_PIGLIN, 5, 4, 4), new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.SKELETON, 2, 5, 5), new MobSpawnSettings.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4));
   }

   public static class NetherBridgeStart extends StructureStart<NoneFeatureConfiguration> {
      public NetherBridgeStart(StructureFeature<NoneFeatureConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, NoneFeatureConfiguration var7) {
         NetherBridgePieces.StartPiece var8 = new NetherBridgePieces.StartPiece(this.random, SectionPos.sectionToBlockCoord(var4, 2), SectionPos.sectionToBlockCoord(var5, 2));
         this.pieces.add(var8);
         var8.addChildren(var8, this.pieces, this.random);
         List var9 = var8.pendingChildren;

         while(!var9.isEmpty()) {
            int var10 = this.random.nextInt(var9.size());
            StructurePiece var11 = (StructurePiece)var9.remove(var10);
            var11.addChildren(var8, this.pieces, this.random);
         }

         this.calculateBoundingBox();
         this.moveInsideHeights(this.random, 48, 70);
      }
   }
}
