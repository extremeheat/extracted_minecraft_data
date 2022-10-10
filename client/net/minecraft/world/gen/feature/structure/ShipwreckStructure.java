package net.minecraft.world.gen.feature.structure;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class ShipwreckStructure extends ScatteredStructure<ShipwreckConfig> {
   public ShipwreckStructure() {
      super();
   }

   protected String func_143025_a() {
      return "Shipwreck";
   }

   public int func_202367_b() {
      return 3;
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), (Biome)null);
      return new ShipwreckStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected int func_202382_c() {
      return 165745295;
   }

   protected int func_204030_a(IChunkGenerator<?> var1) {
      return var1.func_201496_a_().func_204748_h();
   }

   protected int func_211745_b(IChunkGenerator<?> var1) {
      return var1.func_201496_a_().func_211730_k();
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         ShipwreckConfig var7 = (ShipwreckConfig)var2.func_202087_b(var6, Feature.field_204751_l);
         Rotation var8 = Rotation.values()[var3.nextInt(Rotation.values().length)];
         BlockPos var9 = new BlockPos(var4 * 16, 90, var5 * 16);
         ShipwreckPieces.func_204760_a(var1.func_72860_G().func_186340_h(), var9, var8, this.field_75075_a, var3, var7);
         this.func_202500_a(var1);
      }
   }
}
