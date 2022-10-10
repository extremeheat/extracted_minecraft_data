package net.minecraft.world.gen.feature.structure;

import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;

public class JunglePyramidStructure extends ScatteredStructure<JunglePyramidConfig> {
   public JunglePyramidStructure() {
      super();
   }

   protected String func_143025_a() {
      return "Jungle_Pyramid";
   }

   public int func_202367_b() {
      return 3;
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_76772_c);
      return new JunglePyramidStructure.Start(var1, var3, var4, var5, var6);
   }

   protected int func_202382_c() {
      return 14357619;
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, SharedSeedRandom var2, int var3, int var4, Biome var5) {
         super(var3, var4, var5, var2, var1.func_72905_C());
         JunglePyramidPiece var6 = new JunglePyramidPiece(var2, var3 * 16, var4 * 16);
         this.field_75075_a.add(var6);
         this.func_202500_a(var1);
      }
   }
}
