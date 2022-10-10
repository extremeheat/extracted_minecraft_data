package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class OceanRuinStructure extends ScatteredStructure<OceanRuinConfig> {
   public OceanRuinStructure() {
      super();
   }

   public String func_143025_a() {
      return "Ocean_Ruin";
   }

   public int func_202367_b() {
      return 3;
   }

   protected int func_204030_a(IChunkGenerator<?> var1) {
      return var1.func_201496_a_().func_204026_h();
   }

   protected int func_211745_b(IChunkGenerator<?> var1) {
      return var1.func_201496_a_().func_211727_m();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), (Biome)null);
      return new OceanRuinStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected int func_202382_c() {
      return 14357621;
   }

   public static enum Type {
      WARM,
      COLD;

      private Type() {
      }
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         OceanRuinConfig var7 = (OceanRuinConfig)var2.func_202087_b(var6, Feature.field_204029_o);
         int var8 = var4 * 16;
         int var9 = var5 * 16;
         BlockPos var10 = new BlockPos(var8, 90, var9);
         Rotation var11 = Rotation.values()[var3.nextInt(Rotation.values().length)];
         TemplateManager var12 = var1.func_72860_G().func_186340_h();
         OceanRuinPieces.func_204041_a(var12, var10, var11, this.field_75075_a, var3, var7);
         this.func_202500_a(var1);
      }
   }
}
