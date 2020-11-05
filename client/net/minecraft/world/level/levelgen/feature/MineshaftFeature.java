package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.configurations.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class MineshaftFeature extends StructureFeature<MineshaftConfiguration> {
   public MineshaftFeature(Codec<MineshaftConfiguration> var1) {
      super(var1);
   }

   protected boolean isFeatureChunk(ChunkGenerator var1, BiomeSource var2, long var3, WorldgenRandom var5, int var6, int var7, Biome var8, ChunkPos var9, MineshaftConfiguration var10) {
      var5.setLargeFeatureSeed(var3, var6, var7);
      double var11 = (double)var10.probability;
      return var5.nextDouble() < var11;
   }

   public StructureFeature.StructureStartFactory<MineshaftConfiguration> getStartFactory() {
      return MineshaftFeature.MineShaftStart::new;
   }

   public static class MineShaftStart extends StructureStart<MineshaftConfiguration> {
      public MineShaftStart(StructureFeature<MineshaftConfiguration> var1, int var2, int var3, BoundingBox var4, int var5, long var6) {
         super(var1, var2, var3, var4, var5, var6);
      }

      public void generatePieces(RegistryAccess var1, ChunkGenerator var2, StructureManager var3, int var4, int var5, Biome var6, MineshaftConfiguration var7) {
         MineShaftPieces.MineShaftRoom var8 = new MineShaftPieces.MineShaftRoom(0, this.random, SectionPos.sectionToBlockCoord(var4, 2), SectionPos.sectionToBlockCoord(var5, 2), var7.type);
         this.pieces.add(var8);
         var8.addChildren(var8, this.pieces, this.random);
         this.calculateBoundingBox();
         if (var7.type == MineshaftFeature.Type.MESA) {
            boolean var9 = true;
            int var10 = var2.getSeaLevel() - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 - -5;
            this.boundingBox.move(0, var10, 0);
            Iterator var11 = this.pieces.iterator();

            while(var11.hasNext()) {
               StructurePiece var12 = (StructurePiece)var11.next();
               var12.move(0, var10, 0);
            }
         } else {
            this.moveBelowSeaLevel(var2.getSeaLevel(), this.random, 10);
         }

      }
   }

   public static enum Type implements StringRepresentable {
      NORMAL("normal"),
      MESA("mesa");

      public static final Codec<MineshaftFeature.Type> CODEC = StringRepresentable.fromEnum(MineshaftFeature.Type::values, MineshaftFeature.Type::byName);
      private static final Map<String, MineshaftFeature.Type> BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(MineshaftFeature.Type::getName, (var0) -> {
         return var0;
      }));
      private final String name;

      private Type(String var3) {
         this.name = var3;
      }

      public String getName() {
         return this.name;
      }

      private static MineshaftFeature.Type byName(String var0) {
         return (MineshaftFeature.Type)BY_NAME.get(var0);
      }

      public static MineshaftFeature.Type byId(int var0) {
         return var0 >= 0 && var0 < values().length ? values()[var0] : NORMAL;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
