package net.minecraft.world.level.levelgen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class MineshaftFeature extends StructureFeature<MineshaftConfiguration> {
   public MineshaftFeature(Function<Dynamic<?>, ? extends MineshaftConfiguration> var1) {
      super(var1);
   }

   public boolean isFeatureChunk(ChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ((WorldgenRandom)var2).setLargeFeatureSeed(var1.getSeed(), var3, var4);
      Biome var5 = var1.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
      if (var1.isBiomeValidStartForStructure(var5, Feature.MINESHAFT)) {
         MineshaftConfiguration var6 = (MineshaftConfiguration)var1.getStructureConfiguration(var5, Feature.MINESHAFT);
         double var7 = var6.probability;
         return var2.nextDouble() < var7;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return MineshaftFeature.MineShaftStart::new;
   }

   public String getFeatureName() {
      return "Mineshaft";
   }

   public int getLookupRange() {
      return 8;
   }

   public static class MineShaftStart extends StructureStart {
      public MineShaftStart(StructureFeature<?> var1, int var2, int var3, Biome var4, BoundingBox var5, int var6, long var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
      }

      public void generatePieces(ChunkGenerator<?> var1, StructureManager var2, int var3, int var4, Biome var5) {
         MineshaftConfiguration var6 = (MineshaftConfiguration)var1.getStructureConfiguration(var5, Feature.MINESHAFT);
         MineShaftPieces.MineShaftRoom var7 = new MineShaftPieces.MineShaftRoom(0, this.random, (var3 << 4) + 2, (var4 << 4) + 2, var6.type);
         this.pieces.add(var7);
         var7.addChildren(var7, this.pieces, this.random);
         this.calculateBoundingBox();
         if (var6.type == MineshaftFeature.Type.MESA) {
            boolean var8 = true;
            int var9 = var1.getSeaLevel() - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 - -5;
            this.boundingBox.move(0, var9, 0);
            Iterator var10 = this.pieces.iterator();

            while(var10.hasNext()) {
               StructurePiece var11 = (StructurePiece)var10.next();
               var11.move(0, var9, 0);
            }
         } else {
            this.moveBelowSeaLevel(var1.getSeaLevel(), this.random, 10);
         }

      }
   }

   public static enum Type {
      NORMAL("normal"),
      MESA("mesa");

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

      public static MineshaftFeature.Type byName(String var0) {
         return (MineshaftFeature.Type)BY_NAME.get(var0);
      }

      public static MineshaftFeature.Type byId(int var0) {
         return var0 >= 0 && var0 < values().length ? values()[var0] : NORMAL;
      }
   }
}
