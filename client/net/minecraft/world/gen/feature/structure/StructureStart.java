package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.init.Biomes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.biome.Biome;

public abstract class StructureStart {
   protected final List<StructurePiece> field_75075_a = Lists.newArrayList();
   protected MutableBoundingBox field_75074_b;
   protected int field_143024_c;
   protected int field_143023_d;
   private Biome field_202505_e;
   private int field_212688_f;

   public StructureStart() {
      super();
   }

   public StructureStart(int var1, int var2, Biome var3, SharedSeedRandom var4, long var5) {
      super();
      this.field_143024_c = var1;
      this.field_143023_d = var2;
      this.field_202505_e = var3;
      var4.func_202425_c(var5, this.field_143024_c, this.field_143023_d);
   }

   public MutableBoundingBox func_75071_a() {
      return this.field_75074_b;
   }

   public List<StructurePiece> func_186161_c() {
      return this.field_75075_a;
   }

   public void func_75068_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
      synchronized(this.field_75075_a) {
         Iterator var6 = this.field_75075_a.iterator();

         while(var6.hasNext()) {
            StructurePiece var7 = (StructurePiece)var6.next();
            if (var7.func_74874_b().func_78884_a(var3) && !var7.func_74875_a(var1, var2, var3, var4)) {
               var6.remove();
            }
         }

         this.func_202500_a(var1);
      }
   }

   protected void func_202500_a(IBlockReader var1) {
      this.field_75074_b = MutableBoundingBox.func_78887_a();
      Iterator var2 = this.field_75075_a.iterator();

      while(var2.hasNext()) {
         StructurePiece var3 = (StructurePiece)var2.next();
         this.field_75074_b.func_78888_b(var3.func_74874_b());
      }

   }

   public NBTTagCompound func_143021_a(int var1, int var2) {
      NBTTagCompound var3 = new NBTTagCompound();
      if (this.func_75069_d()) {
         var3.func_74778_a("id", StructureIO.func_143033_a(this));
         var3.func_74778_a("biome", IRegistry.field_212624_m.func_177774_c(this.field_202505_e).toString());
         var3.func_74768_a("ChunkX", var1);
         var3.func_74768_a("ChunkZ", var2);
         var3.func_74768_a("references", this.field_212688_f);
         var3.func_74782_a("BB", this.field_75074_b.func_151535_h());
         NBTTagList var4 = new NBTTagList();
         synchronized(this.field_75075_a) {
            Iterator var6 = this.field_75075_a.iterator();

            while(true) {
               if (!var6.hasNext()) {
                  break;
               }

               StructurePiece var7 = (StructurePiece)var6.next();
               var4.add((INBTBase)var7.func_143010_b());
            }
         }

         var3.func_74782_a("Children", var4);
         this.func_143022_a(var3);
         return var3;
      } else {
         var3.func_74778_a("id", "INVALID");
         return var3;
      }
   }

   public void func_143022_a(NBTTagCompound var1) {
   }

   public void func_143020_a(IWorld var1, NBTTagCompound var2) {
      this.field_143024_c = var2.func_74762_e("ChunkX");
      this.field_143023_d = var2.func_74762_e("ChunkZ");
      this.field_212688_f = var2.func_74762_e("references");
      this.field_202505_e = var2.func_74764_b("biome") ? (Biome)IRegistry.field_212624_m.func_212608_b(new ResourceLocation(var2.func_74779_i("biome"))) : var1.func_72863_F().func_201711_g().func_202090_b().func_180300_a(new BlockPos((this.field_143024_c << 4) + 9, 0, (this.field_143023_d << 4) + 9), Biomes.field_76772_c);
      if (var2.func_74764_b("BB")) {
         this.field_75074_b = new MutableBoundingBox(var2.func_74759_k("BB"));
      }

      NBTTagList var3 = var2.func_150295_c("Children", 10);

      for(int var4 = 0; var4 < var3.size(); ++var4) {
         this.field_75075_a.add(StructureIO.func_143032_b(var3.func_150305_b(var4), var1));
      }

      this.func_143017_b(var2);
   }

   public void func_143017_b(NBTTagCompound var1) {
   }

   protected void func_75067_a(IWorldReaderBase var1, Random var2, int var3) {
      int var4 = var1.func_181545_F() - var3;
      int var5 = this.field_75074_b.func_78882_c() + 1;
      if (var5 < var4) {
         var5 += var2.nextInt(var4 - var5);
      }

      int var6 = var5 - this.field_75074_b.field_78894_e;
      this.field_75074_b.func_78886_a(0, var6, 0);
      Iterator var7 = this.field_75075_a.iterator();

      while(var7.hasNext()) {
         StructurePiece var8 = (StructurePiece)var7.next();
         var8.func_181138_a(0, var6, 0);
      }

   }

   protected void func_75070_a(IBlockReader var1, Random var2, int var3, int var4) {
      int var5 = var4 - var3 + 1 - this.field_75074_b.func_78882_c();
      int var6;
      if (var5 > 1) {
         var6 = var3 + var2.nextInt(var5);
      } else {
         var6 = var3;
      }

      int var7 = var6 - this.field_75074_b.field_78895_b;
      this.field_75074_b.func_78886_a(0, var7, 0);
      Iterator var8 = this.field_75075_a.iterator();

      while(var8.hasNext()) {
         StructurePiece var9 = (StructurePiece)var8.next();
         var9.func_181138_a(0, var7, 0);
      }

   }

   public boolean func_75069_d() {
      return true;
   }

   public void func_175787_b(ChunkPos var1) {
   }

   public int func_143019_e() {
      return this.field_143024_c;
   }

   public int func_143018_f() {
      return this.field_143023_d;
   }

   public BlockPos func_204294_a() {
      return new BlockPos(this.field_143024_c << 4, 0, this.field_143023_d << 4);
   }

   public boolean func_212687_g() {
      return this.field_212688_f < this.func_212686_i();
   }

   public void func_212685_h() {
      ++this.field_212688_f;
   }

   protected int func_212686_i() {
      return 1;
   }
}
