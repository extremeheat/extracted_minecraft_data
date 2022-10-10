package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class FortressStructure extends Structure<FortressConfig> {
   private static final List<Biome.SpawnListEntry> field_202381_d;

   public FortressStructure() {
      super();
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      int var5 = var3 >> 4;
      int var6 = var4 >> 4;
      var2.setSeed((long)(var5 ^ var6 << 4) ^ var1.func_202089_c());
      var2.nextInt();
      if (var2.nextInt(3) != 0) {
         return false;
      } else if (var3 != (var5 << 4) + 4 + var2.nextInt(8)) {
         return false;
      } else if (var4 != (var6 << 4) + 4 + var2.nextInt(8)) {
         return false;
      } else {
         Biome var7 = var1.func_202090_b().func_180300_a(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9), Biomes.field_180279_ad);
         return var1.func_202094_a(var7, Feature.field_202337_o);
      }
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_180279_ad);
      return new FortressStructure.Start(var1, var3, var4, var5, var6);
   }

   protected String func_143025_a() {
      return "Fortress";
   }

   public int func_202367_b() {
      return 8;
   }

   public List<Biome.SpawnListEntry> func_202279_e() {
      return field_202381_d;
   }

   static {
      field_202381_d = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.field_200792_f, 10, 2, 3), new Biome.SpawnListEntry(EntityType.field_200785_Y, 5, 4, 4), new Biome.SpawnListEntry(EntityType.field_200722_aA, 8, 5, 5), new Biome.SpawnListEntry(EntityType.field_200741_ag, 2, 5, 5), new Biome.SpawnListEntry(EntityType.field_200771_K, 3, 4, 4)});
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(IWorld var1, SharedSeedRandom var2, int var3, int var4, Biome var5) {
         super(var3, var4, var5, var2, var1.func_72905_C());
         FortressPieces.Start var6 = new FortressPieces.Start(var2, (var3 << 4) + 2, (var4 << 4) + 2);
         this.field_75075_a.add(var6);
         var6.func_74861_a(var6, this.field_75075_a, var2);
         List var7 = var6.field_74967_d;

         while(!var7.isEmpty()) {
            int var8 = var2.nextInt(var7.size());
            StructurePiece var9 = (StructurePiece)var7.remove(var8);
            var9.func_74861_a(var6, this.field_75075_a, var2);
         }

         this.func_202500_a(var1);
         this.func_75070_a(var1, var2, 48, 70);
      }
   }
}
