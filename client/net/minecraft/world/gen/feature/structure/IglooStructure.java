package net.minecraft.world.gen.feature.structure;

import net.minecraft.init.Biomes;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class IglooStructure extends ScatteredStructure<IglooConfig> {
   public IglooStructure() {
      super();
   }

   protected String func_143025_a() {
      return "Igloo";
   }

   public int func_202367_b() {
      return 3;
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_76772_c);
      return new IglooStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected int func_202382_c() {
      return 14357618;
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         IglooConfig var7 = (IglooConfig)var2.func_202087_b(var6, Feature.field_202333_k);
         int var8 = var4 * 16;
         int var9 = var5 * 16;
         BlockPos var10 = new BlockPos(var8, 90, var9);
         Rotation var11 = Rotation.values()[var3.nextInt(Rotation.values().length)];
         TemplateManager var12 = var1.func_72860_G().func_186340_h();
         IglooPieces.func_207617_a(var12, var10, var11, this.field_75075_a, var3, var7);
         this.func_202500_a(var1);
      }
   }
}
