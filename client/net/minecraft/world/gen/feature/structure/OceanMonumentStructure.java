package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;

public class OceanMonumentStructure extends Structure<OceanMonumentConfig> {
   private static final List<Biome.SpawnListEntry> field_175803_h;

   public OceanMonumentStructure() {
      super();
   }

   protected ChunkPos func_211744_a(IChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      int var7 = var1.func_201496_a_().func_202174_b();
      int var8 = var1.func_201496_a_().func_202171_c();
      int var9 = var3 + var7 * var5;
      int var10 = var4 + var7 * var6;
      int var11 = var9 < 0 ? var9 - var7 + 1 : var9;
      int var12 = var10 < 0 ? var10 - var7 + 1 : var10;
      int var13 = var11 / var7;
      int var14 = var12 / var7;
      ((SharedSeedRandom)var2).func_202427_a(var1.func_202089_c(), var13, var14, 10387313);
      var13 *= var7;
      var14 *= var7;
      var13 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      var14 += (var2.nextInt(var7 - var8) + var2.nextInt(var7 - var8)) / 2;
      return new ChunkPos(var13, var14);
   }

   protected boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4) {
      ChunkPos var5 = this.func_211744_a(var1, var2, var3, var4, 0, 0);
      if (var3 == var5.field_77276_a && var4 == var5.field_77275_b) {
         Set var6 = var1.func_202090_b().func_201538_a(var3 * 16 + 9, var4 * 16 + 9, 16);
         Iterator var7 = var6.iterator();

         Biome var8;
         do {
            if (!var7.hasNext()) {
               Set var10 = var1.func_202090_b().func_201538_a(var3 * 16 + 9, var4 * 16 + 9, 29);
               Iterator var11 = var10.iterator();

               Biome var9;
               do {
                  if (!var11.hasNext()) {
                     return true;
                  }

                  var9 = (Biome)var11.next();
               } while(var9.func_201856_r() == Biome.Category.OCEAN || var9.func_201856_r() == Biome.Category.RIVER);

               return false;
            }

            var8 = (Biome)var7.next();
         } while(var1.func_202094_a(var8, Feature.field_202336_n));

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
      return new OceanMonumentStructure.Start(var1, var3, var4, var5, var6);
   }

   protected String func_143025_a() {
      return "Monument";
   }

   public int func_202367_b() {
      return 8;
   }

   public List<Biome.SpawnListEntry> func_202279_e() {
      return field_175803_h;
   }

   static {
      field_175803_h = Lists.newArrayList(new Biome.SpawnListEntry[]{new Biome.SpawnListEntry(EntityType.field_200761_A, 1, 2, 4)});
   }

   public static class Start extends StructureStart {
      private final Set<ChunkPos> field_175791_c = Sets.newHashSet();
      private boolean field_175790_d;

      public Start() {
         super();
      }

      public Start(IWorld var1, SharedSeedRandom var2, int var3, int var4, Biome var5) {
         super(var3, var4, var5, var2, var1.func_72905_C());
         this.func_175789_b(var1, var2, var3, var4);
      }

      private void func_175789_b(IBlockReader var1, Random var2, int var3, int var4) {
         int var5 = var3 * 16 - 29;
         int var6 = var4 * 16 - 29;
         EnumFacing var7 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
         this.field_75075_a.add(new OceanMonumentPieces.MonumentBuilding(var2, var5, var6, var7));
         this.func_202500_a(var1);
         this.field_175790_d = true;
      }

      public void func_75068_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
         if (!this.field_175790_d) {
            this.field_75075_a.clear();
            this.func_175789_b(var1, var2, this.func_143019_e(), this.func_143018_f());
         }

         super.func_75068_a(var1, var2, var3, var4);
      }

      public void func_175787_b(ChunkPos var1) {
         super.func_175787_b(var1);
         this.field_175791_c.add(var1);
      }

      public void func_143022_a(NBTTagCompound var1) {
         super.func_143022_a(var1);
         NBTTagList var2 = new NBTTagList();
         Iterator var3 = this.field_175791_c.iterator();

         while(var3.hasNext()) {
            ChunkPos var4 = (ChunkPos)var3.next();
            NBTTagCompound var5 = new NBTTagCompound();
            var5.func_74768_a("X", var4.field_77276_a);
            var5.func_74768_a("Z", var4.field_77275_b);
            var2.add((INBTBase)var5);
         }

         var1.func_74782_a("Processed", var2);
      }

      public void func_143017_b(NBTTagCompound var1) {
         super.func_143017_b(var1);
         if (var1.func_150297_b("Processed", 9)) {
            NBTTagList var2 = var1.func_150295_c("Processed", 10);

            for(int var3 = 0; var3 < var2.size(); ++var3) {
               NBTTagCompound var4 = var2.func_150305_b(var3);
               this.field_175791_c.add(new ChunkPos(var4.func_74762_e("X"), var4.func_74762_e("Z")));
            }
         }

      }
   }
}
