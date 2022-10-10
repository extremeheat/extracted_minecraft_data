package net.minecraft.world.gen.feature.structure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class VillageStructure extends Structure<VillageConfig> {
   public VillageStructure() {
      super();
   }

   public String func_143025_a() {
      return "Village";
   }

   public int func_202367_b() {
      return 8;
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected ChunkPos func_211744_a(IChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.func_201496_a_().func_202173_a();
      int var8 = var1.func_201496_a_().func_211729_b();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((SharedSeedRandom)var2).func_202427_a(var1.func_202089_c(), var13, var14, 10387312);
      var13 *= var7;
      var14 *= var7;
      var13 += var2.nextInt(var7 - var8);
      var14 += var2.nextInt(var7 - var8);
      return new ChunkPos(var13, var14);
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.func_211744_a(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.field_77276_a && var4 == var5.field_77275_b) {
         Biome var6 = var1.func_202090_b().func_180300_a(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9), Biomes.field_180279_ad);
         return var1.func_202094_a(var6, Feature.field_202328_f);
      } else {
         return false;
      }
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_180279_ad);
      return new VillageStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   public static class Start extends StructureStart {
      private boolean field_75076_c;

      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         VillageConfig var7 = (VillageConfig)var2.func_202087_b(var6, Feature.field_202328_f);
         List var8 = VillagePieces.func_75084_a(var3, var7.field_202461_a);
         VillagePieces.Start var9 = new VillagePieces.Start(0, var3, (var4 << 4) + 2, (var5 << 4) + 2, var8, var7);
         this.field_75075_a.add(var9);
         var9.func_74861_a(var9, this.field_75075_a, var3);
         List var10 = var9.field_74930_j;
         List var11 = var9.field_74932_i;

         int var12;
         while(!var10.isEmpty() || !var11.isEmpty()) {
            StructurePiece var13;
            if (var10.isEmpty()) {
               var12 = var3.nextInt(var11.size());
               var13 = (StructurePiece)var11.remove(var12);
               var13.func_74861_a(var9, this.field_75075_a, var3);
            } else {
               var12 = var3.nextInt(var10.size());
               var13 = (StructurePiece)var10.remove(var12);
               var13.func_74861_a(var9, this.field_75075_a, var3);
            }
         }

         this.func_202500_a(var1);
         var12 = 0;
         Iterator var15 = this.field_75075_a.iterator();

         while(var15.hasNext()) {
            StructurePiece var14 = (StructurePiece)var15.next();
            if (!(var14 instanceof VillagePieces.Road)) {
               ++var12;
            }
         }

         this.field_75076_c = var12 > 2;
      }

      public boolean func_75069_d() {
         return this.field_75076_c;
      }

      public void func_143022_a(NBTTagCompound var1) {
         super.func_143022_a(var1);
         var1.func_74757_a("Valid", this.field_75076_c);
      }

      public void func_143017_b(NBTTagCompound var1) {
         super.func_143017_b(var1);
         this.field_75076_c = var1.func_74767_n("Valid");
      }
   }
}
