package net.minecraft.world.gen.structure;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenBase;

public abstract class MapGenStructure extends MapGenBase {
   private MapGenStructureData field_143029_e;
   protected Map<Long, StructureStart> field_75053_d = Maps.newHashMap();

   public MapGenStructure() {
      super();
   }

   public abstract String func_143025_a();

   protected final void func_180701_a(World var1, final int var2, final int var3, int var4, int var5, ChunkPrimer var6) {
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
            var9.func_71500_a("Is feature chunk", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.func_75047_a(var2, var3) ? "True" : "False";
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            var9.func_71507_a("Chunk location", String.format("%d,%d", var2, var3));
            var9.func_71500_a("Chunk pos hash", new Callable<String>() {
               public String call() throws Exception {
                  return String.valueOf(ChunkCoordIntPair.func_77272_a(var2, var3));
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            var9.func_71500_a("Structure type", new Callable<String>() {
               public String call() throws Exception {
                  return MapGenStructure.this.getClass().getCanonicalName();
               }

               // $FF: synthetic method
               public Object call() throws Exception {
                  return this.call();
               }
            });
            throw new ReportedException(var8);
         }
      }
   }

   public boolean func_175794_a(World var1, Random var2, ChunkCoordIntPair var3) {
      this.func_143027_a(var1);
      int var4 = (var3.field_77276_a << 4) + 8;
      int var5 = (var3.field_77275_b << 4) + 8;
      boolean var6 = false;
      Iterator var7 = this.field_75053_d.values().iterator();

      while(var7.hasNext()) {
         StructureStart var8 = (StructureStart)var7.next();
         if (var8.func_75069_d() && var8.func_175788_a(var3) && var8.func_75071_a().func_78885_a(var4, var5, var4 + 15, var5 + 15)) {
            var8.func_75068_a(var1, var2, new StructureBoundingBox(var4, var5, var4 + 15, var5 + 15));
            var8.func_175787_b(var3);
            var6 = true;
            this.func_143026_a(var8.func_143019_e(), var8.func_143018_f(), var8);
         }
      }

      return var6;
   }

   public boolean func_175795_b(BlockPos var1) {
      this.func_143027_a(this.field_75039_c);
      return this.func_175797_c(var1) != null;
   }

   protected StructureStart func_175797_c(BlockPos var1) {
      Iterator var2 = this.field_75053_d.values().iterator();

      while(true) {
         StructureStart var3;
         do {
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               var3 = (StructureStart)var2.next();
            } while(!var3.func_75069_d());
         } while(!var3.func_75071_a().func_175898_b(var1));

         Iterator var4 = var3.func_75073_b().iterator();

         while(var4.hasNext()) {
            StructureComponent var5 = (StructureComponent)var4.next();
            if (var5.func_74874_b().func_175898_b(var1)) {
               return var3;
            }
         }
      }
   }

   public boolean func_175796_a(World var1, BlockPos var2) {
      this.func_143027_a(var1);
      Iterator var3 = this.field_75053_d.values().iterator();

      StructureStart var4;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         var4 = (StructureStart)var3.next();
      } while(!var4.func_75069_d() || !var4.func_75071_a().func_175898_b(var2));

      return true;
   }

   public BlockPos func_180706_b(World var1, BlockPos var2) {
      this.field_75039_c = var1;
      this.func_143027_a(var1);
      this.field_75038_b.setSeed(var1.func_72905_C());
      long var3 = this.field_75038_b.nextLong();
      long var5 = this.field_75038_b.nextLong();
      long var7 = (long)(var2.func_177958_n() >> 4) * var3;
      long var9 = (long)(var2.func_177952_p() >> 4) * var5;
      this.field_75038_b.setSeed(var7 ^ var9 ^ var1.func_72905_C());
      this.func_180701_a(var1, var2.func_177958_n() >> 4, var2.func_177952_p() >> 4, 0, 0, (ChunkPrimer)null);
      double var11 = 1.7976931348623157E308D;
      BlockPos var13 = null;
      Iterator var14 = this.field_75053_d.values().iterator();

      BlockPos var17;
      double var18;
      while(var14.hasNext()) {
         StructureStart var15 = (StructureStart)var14.next();
         if (var15.func_75069_d()) {
            StructureComponent var16 = (StructureComponent)var15.func_75073_b().get(0);
            var17 = var16.func_180776_a();
            var18 = var17.func_177951_i(var2);
            if (var18 < var11) {
               var11 = var18;
               var13 = var17;
            }
         }
      }

      if (var13 != null) {
         return var13;
      } else {
         List var20 = this.func_75052_o_();
         if (var20 != null) {
            BlockPos var21 = null;
            Iterator var22 = var20.iterator();

            while(var22.hasNext()) {
               var17 = (BlockPos)var22.next();
               var18 = var17.func_177951_i(var2);
               if (var18 < var11) {
                  var11 = var18;
                  var21 = var17;
               }
            }

            return var21;
         } else {
            return null;
         }
      }
   }

   protected List<BlockPos> func_75052_o_() {
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
            Iterator var3 = var2.func_150296_c().iterator();

            while(var3.hasNext()) {
               String var4 = (String)var3.next();
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
