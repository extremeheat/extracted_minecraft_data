package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.UpgradeData;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class WoodlandMansionStructure extends Structure<WoodlandMansionConfig> {
   public WoodlandMansionStructure() {
      super();
   }

   protected ChunkPos func_211744_a(IChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.func_201496_a_().func_202179_i();
      int var8 = var1.func_201496_a_().func_211726_q();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((SharedSeedRandom)var2).func_202427_a(var1.func_202089_c(), var13, var14, 10387319);
      var13 *= var7;
      var14 *= var7;
      var13 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      var14 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.func_211744_a(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.field_77276_a && var4 == var5.field_77275_b) {
         Set var6 = var1.func_202090_b().func_201538_a(var3 * 16 + 9, var4 * 16 + 9, 32);
         Iterator var7 = var6.iterator();

         Biome var8;
         do {
            if (!var7.hasNext()) {
               return true;
            }

            var8 = (Biome)var7.next();
         } while(var1.func_202094_a(var8, Feature.field_202330_h));

         return false;
      } else {
         return false;
      }
   }

   protected boolean func_202365_a(IWorld var1) {
      return var1.func_72912_H().func_76089_r();
   }

   protected StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5) {
      Biome var6 = var2.func_202090_b().func_180300_a(new BlockPos((var4 << 4) + 9, 0, (var5 << 4) + 9), Biomes.field_180279_ad);
      return new WoodlandMansionStructure.Start(var1, var2, var3, var4, var5, var6);
   }

   protected String func_143025_a() {
      return "Mansion";
   }

   public int func_202367_b() {
      return 8;
   }

   public static class Start extends StructureStart {
      private boolean field_191093_c;

      public Start() {
         super();
      }

      public Start(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5, Biome var6) {
         super(var4, var5, var6, var3, var1.func_72905_C());
         Rotation var7 = Rotation.values()[var3.nextInt(Rotation.values().length)];
         byte var8 = 5;
         byte var9 = 5;
         if (var7 == Rotation.CLOCKWISE_90) {
            var8 = -5;
         } else if (var7 == Rotation.CLOCKWISE_180) {
            var8 = -5;
            var9 = -5;
         } else if (var7 == Rotation.COUNTERCLOCKWISE_90) {
            var9 = -5;
         }

         ChunkPrimer var10 = new ChunkPrimer(new ChunkPos(var4, var5), UpgradeData.field_196994_a);
         var2.func_202088_a(var10);
         int var11 = var10.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7, 7);
         int var12 = var10.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7, 7 + var9);
         int var13 = var10.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7 + var8, 7);
         int var14 = var10.func_201576_a(Heightmap.Type.MOTION_BLOCKING, 7 + var8, 7 + var9);
         int var15 = Math.min(Math.min(var11, var12), Math.min(var13, var14));
         if (var15 < 60) {
            this.field_191093_c = false;
         } else {
            BlockPos var16 = new BlockPos(var4 * 16 + 8, var15 + 1, var5 * 16 + 8);
            LinkedList var17 = Lists.newLinkedList();
            WoodlandMansionPieces.func_191152_a(var1.func_72860_G().func_186340_h(), var16, var7, var17, var3);
            this.field_75075_a.addAll(var17);
            this.func_202500_a(var1);
            this.field_191093_c = true;
         }
      }

      public void func_75068_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         super.func_75068_a(var1, var2, var3, var4);
         int var5 = this.field_75074_b.field_78895_b;

         for(int var6 = var3.field_78897_a; var6 <= var3.field_78893_d; ++var6) {
            for(int var7 = var3.field_78896_c; var7 <= var3.field_78892_f; ++var7) {
               BlockPos var8 = new BlockPos(var6, var5, var7);
               if (!var1.func_175623_d(var8) && this.field_75074_b.func_175898_b(var8)) {
                  boolean var9 = false;
                  Iterator var10 = this.field_75075_a.iterator();

                  while(var10.hasNext()) {
                     StructurePiece var11 = (StructurePiece)var10.next();
                     if (var11.func_74874_b().func_175898_b(var8)) {
                        var9 = true;
                        break;
                     }
                  }

                  if (var9) {
                     for(int var12 = var5 - 1; var12 > 1; --var12) {
                        BlockPos var13 = new BlockPos(var6, var12, var7);
                        if (!var1.func_175623_d(var13) && !var1.func_180495_p(var13).func_185904_a().func_76224_d()) {
                           break;
                        }

                        var1.func_180501_a(var13, Blocks.field_150347_e.func_176223_P(), 2);
                     }
                  }
               }
            }
         }

      }

      public boolean func_75069_d() {
         return this.field_191093_c;
      }
   }
}
