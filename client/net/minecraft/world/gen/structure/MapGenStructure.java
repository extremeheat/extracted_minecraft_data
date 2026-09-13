package net.minecraft.world.gen.structure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase {
   private MapGenStructureData field_143029_e;
   protected Map field_75053_d = new HashMap();

   public MapGenStructure() {
      super();
   }

   public abstract String func_143025_a();

   @Override
   protected final void func_151538_a(World var1, int var2, int var3, int var4, int var5, Block[] var6) {
      this.func_143027_a(var1);
      if (!this.field_75053_d.containsKey(ChunkCoordIntPair.func_77272_a(var2, var3))) {
         this.field_75038_b.nextInt();

         try {
            if (this.func_75047_a(var2, var3)) {
               StructureStart var7 = this.func_75049_b(var2, var3);
               this.field_75053_d.put(ChunkCoordIntPair.func_77272_a(var2, var3), var7);
               this.func_143026_a(var2, var3, var7);
            }
         } catch (Throwable var10) {
            CrashReport var8 = CrashReport.func_85055_a(var10, "Exception preparing structure feature");
            CrashReportCategory var9 = var8.func_85058_a("Feature being prepared");
            var9.func_71500_a("Is feature chunk", new MapGenStructure$1(this, var2, var3));
            var9.func_71507_a("Chunk location", String.format("%d,%d", var2, var3));
            var9.func_71500_a("Chunk pos hash", new MapGenStructure$2(this, var2, var3));
            var9.func_71500_a("Structure type", new MapGenStructure$3(this));
            throw new ReportedException(var8);
         }
      }
   }

   public boolean func_75051_a(World var1, Random var2, int var3, int var4) {
      this.func_143027_a(var1);
      int var5 = (var3 << 4) + 8;
      int var6 = (var4 << 4) + 8;
      boolean var7 = false;

      for(StructureStart var9 : this.field_75053_d.values()) {
         if (var9.func_75069_d() && var9.func_75071_a().func_78885_a(var5, var6, var5 + 15, var6 + 15)) {
            var9.func_75068_a(var1, var2, new StructureBoundingBox(var5, var6, var5 + 15, var6 + 15));
            var7 = true;
            this.func_143026_a(var9.func_143019_e(), var9.func_143018_f(), var9);
         }
      }

      return var7;
   }

   public boolean func_75048_a(int var1, int var2, int var3) {
      this.func_143027_a(this.field_75039_c);
      return this.func_143028_c(var1, var2, var3) != null;
   }

   protected StructureStart func_143028_c(int var1, int var2, int var3) {
      for(StructureStart var5 : this.field_75053_d.values()) {
         if (var5.func_75069_d() && var5.func_75071_a().func_78885_a(var1, var3, var1, var3)) {
            for(StructureComponent var7 : var5.func_75073_b()) {
               if (var7.func_74874_b().func_78890_b(var1, var2, var3)) {
                  return var5;
               }
            }
         }
      }

      return null;
   }

   public boolean func_142038_b(int var1, int var2, int var3) {
      this.func_143027_a(this.field_75039_c);

      for(StructureStart var5 : this.field_75053_d.values()) {
         if (var5.func_75069_d()) {
            return var5.func_75071_a().func_78885_a(var1, var3, var1, var3);
         }
      }

      return false;
   }

   public ChunkPosition func_151545_a(World var1, int var2, int var3, int var4) {
      this.field_75039_c = var1;
      this.func_143027_a(var1);
      this.field_75038_b.setSeed(var1.func_72905_C());
      long var5 = this.field_75038_b.nextLong();
      long var7 = this.field_75038_b.nextLong();
      long var9 = (long)(var2 >> 4) * var5;
      long var11 = (long)(var4 >> 4) * var7;
      this.field_75038_b.setSeed(var9 ^ var11 ^ var1.func_72905_C());
      this.func_151538_a(var1, var2 >> 4, var4 >> 4, 0, 0, null);
      double var13 = 1.7976931348623157E308;
      ChunkPosition var15 = null;

      for(StructureStart var17 : this.field_75053_d.values()) {
         if (var17.func_75069_d()) {
            StructureComponent var18 = (StructureComponent)var17.func_75073_b().get(0);
            ChunkPosition var19 = var18.func_151553_a();
            int var20 = var19.field_151329_a - var2;
            int var21 = var19.field_151327_b - var3;
            int var22 = var19.field_151328_c - var4;
            double var23 = (double)(var20 * var20 + var21 * var21 + var22 * var22);
            if (var23 < var13) {
               var13 = var23;
               var15 = var19;
            }
         }
      }

      if (var15 != null) {
         return var15;
      } else {
         List var25 = this.func_75052_o_();
         if (var25 != null) {
            ChunkPosition var26 = null;

            for(ChunkPosition var28 : var25) {
               int var29 = var28.field_151329_a - var2;
               int var30 = var28.field_151327_b - var3;
               int var31 = var28.field_151328_c - var4;
               double var32 = (double)(var29 * var29 + var30 * var30 + var31 * var31);
               if (var32 < var13) {
                  var13 = var32;
                  var26 = var28;
               }
            }

            return var26;
         } else {
            return null;
         }
      }
   }

   protected List func_75052_o_() {
      return null;
   }

   private void func_143027_a(World var1) {
      if (this.field_143029_e == null) {
         this.field_143029_e = (MapGenStructureData)var1.func_72943_a(MapGenStructureData.class, this.func_143025_a());
         if (this.field_143029_e == null) {
            this.field_143029_e = new MapGenStructureData(this.func_143025_a());
            var1.func_72823_a(this.func_143025_a(), this.field_143029_e);
         } else {
            NBTTagCompound var2 = this.field_143029_e.func_143041_a();

            for(String var4 : var2.func_150296_c()) {
               NBTBase var5 = var2.func_74781_a(var4);
               if (var5.func_74732_a() == 10) {
                  NBTTagCompound var6 = (NBTTagCompound)var5;
                  if (var6.func_74764_b("ChunkX") && var6.func_74764_b("ChunkZ")) {
                     int var7 = var6.func_74762_e("ChunkX");
                     int var8 = var6.func_74762_e("ChunkZ");
                     StructureStart var9 = MapGenStructureIO.func_143035_a(var6, var1);
                     if (var9 != null) {
                        this.field_75053_d.put(ChunkCoordIntPair.func_77272_a(var7, var8), var9);
                     }
                  }
               }
            }
         }
      }
   }

   private void func_143026_a(int var1, int var2, StructureStart var3) {
      this.field_143029_e.func_143043_a(var3.func_143021_a(var1, var2), var1, var2);
      this.field_143029_e.func_76185_a();
   }

   protected abstract boolean func_75047_a(int var1, int var2);

   protected abstract StructureStart func_75049_b(int var1, int var2);
}
