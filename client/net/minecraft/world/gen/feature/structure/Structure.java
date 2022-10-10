package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Structure<C extends IFeatureConfig> extends Feature<C> {
   private static final Logger field_208204_b = LogManager.getLogger();
   public static final StructureStart field_202376_c = new StructureStart() {
      public boolean func_75069_d() {
         return false;
      }
   };

   public Structure() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, C var5) {
      if (!this.func_202365_a(var1)) {
         return false;
      } else {
         int var6 = this.func_202367_b();
         int var7 = var4.func_177958_n() >> 4;
         int var8 = var4.func_177952_p() >> 4;
         int var9 = var7 << 4;
         int var10 = var8 << 4;
         long var11 = ChunkPos.func_77272_a(var7, var8);
         boolean var13 = false;

         for(int var14 = var7 - var6; var14 <= var7 + var6; ++var14) {
            for(int var15 = var8 - var6; var15 <= var8 + var6; ++var15) {
               long var16 = ChunkPos.func_77272_a(var14, var15);
               StructureStart var18 = this.func_202373_a(var1, var2, (SharedSeedRandom)var3, var16);
               if (var18 != field_202376_c && var18.func_75071_a().func_78885_a(var9, var10, var9 + 15, var10 + 15)) {
                  ((LongSet)var2.func_203223_b(this).computeIfAbsent(var11, (var0) -> {
                     return new LongOpenHashSet();
                  })).add(var16);
                  var1.func_72863_F().func_201713_d(var7, var8, true).func_201583_a(this.func_143025_a(), var16);
                  var18.func_75068_a(var1, var3, new MutableBoundingBox(var9, var10, var9 + 15, var10 + 15), new ChunkPos(var7, var8));
                  var18.func_175787_b(new ChunkPos(var7, var8));
                  var13 = true;
               }
            }
         }

         return var13;
      }
   }

   protected StructureStart func_202364_a(IWorld var1, BlockPos var2) {
      List var3 = this.func_202371_a(var1, var2.func_177958_n() >> 4, var2.func_177952_p() >> 4);
      Iterator var4 = var3.iterator();

      while(true) {
         StructureStart var5;
         do {
            do {
               if (!var4.hasNext()) {
                  return field_202376_c;
               }

               var5 = (StructureStart)var4.next();
            } while(!var5.func_75069_d());
         } while(!var5.func_75071_a().func_175898_b(var2));

         Iterator var6 = var5.func_186161_c().iterator();

         while(var6.hasNext()) {
            StructurePiece var7 = (StructurePiece)var6.next();
            if (var7.func_74874_b().func_175898_b(var2)) {
               return var5;
            }
         }
      }
   }

   public boolean func_175796_a(IWorld var1, BlockPos var2) {
      List var3 = this.func_202371_a(var1, var2.func_177958_n() >> 4, var2.func_177952_p() >> 4);
      Iterator var4 = var3.iterator();

      StructureStart var5;
      do {
         if (!var4.hasNext()) {
            return false;
         }

         var5 = (StructureStart)var4.next();
      } while(!var5.func_75069_d() || !var5.func_75071_a().func_175898_b(var2));

      return true;
   }

   public boolean func_202366_b(IWorld var1, BlockPos var2) {
      return this.func_202364_a(var1, var2).func_75069_d();
   }

   @Nullable
   public BlockPos func_211405_a(World var1, IChunkGenerator<? extends IChunkGenSettings> var2, BlockPos var3, int var4, boolean var5) {
      if (!var2.func_202090_b().func_205004_a(this)) {
         return null;
      } else {
         int var6 = var3.func_177958_n() >> 4;
         int var7 = var3.func_177952_p() >> 4;
         int var8 = 0;

         for(SharedSeedRandom var9 = new SharedSeedRandom(); var8 <= var4; ++var8) {
            for(int var10 = -var8; var10 <= var8; ++var10) {
               boolean var11 = var10 == -var8 || var10 == var8;

               for(int var12 = -var8; var12 <= var8; ++var12) {
                  boolean var13 = var12 == -var8 || var12 == var8;
                  if (var11 || var13) {
                     ChunkPos var14 = this.func_211744_a(var2, var9, var6, var7, var10, var12);
                     StructureStart var15 = this.func_202373_a(var1, var2, var9, var14.func_201841_a());
                     if (var15 != field_202376_c) {
                        if (var5 && var15.func_212687_g()) {
                           var15.func_212685_h();
                           return var15.func_204294_a();
                        }

                        if (!var5) {
                           return var15.func_204294_a();
                        }
                     }

                     if (var8 == 0) {
                        break;
                     }
                  }
               }

               if (var8 == 0) {
                  break;
               }
            }
         }

         return null;
      }
   }

   private List<StructureStart> func_202371_a(IWorld var1, int var2, int var3) {
      ArrayList var4 = Lists.newArrayList();
      Long2ObjectMap var5 = var1.func_72863_F().func_201711_g().func_203224_a(this);
      Long2ObjectMap var6 = var1.func_72863_F().func_201711_g().func_203223_b(this);
      long var7 = ChunkPos.func_77272_a(var2, var3);
      LongSet var9 = (LongSet)var6.get(var7);
      if (var9 == null) {
         var9 = var1.func_72863_F().func_201713_d(var2, var3, true).func_201578_b(this.func_143025_a());
         var6.put(var7, var9);
      }

      LongIterator var10 = var9.iterator();

      while(var10.hasNext()) {
         Long var11 = (Long)var10.next();
         StructureStart var12 = (StructureStart)var5.get(var11);
         if (var12 != null) {
            var4.add(var12);
         } else {
            ChunkPos var13 = new ChunkPos(var11);
            IChunk var14 = var1.func_72863_F().func_201713_d(var13.field_77276_a, var13.field_77275_b, true);
            var12 = var14.func_201585_a(this.func_143025_a());
            if (var12 != null) {
               var5.put(var11, var12);
               var4.add(var12);
            }
         }
      }

      return var4;
   }

   private StructureStart func_202373_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, SharedSeedRandom var3, long var4) {
      if (!var2.func_202090_b().func_205004_a(this)) {
         return field_202376_c;
      } else {
         Long2ObjectMap var6 = var2.func_203224_a(this);
         StructureStart var7 = (StructureStart)var6.get(var4);
         if (var7 != null) {
            return var7;
         } else {
            ChunkPos var8 = new ChunkPos(var4);
            IChunk var9 = var1.func_72863_F().func_201713_d(var8.field_77276_a, var8.field_77275_b, false);
            if (var9 != null) {
               var7 = var9.func_201585_a(this.func_143025_a());
               if (var7 != null) {
                  var6.put(var4, var7);
                  return var7;
               }
            }

            if (this.func_202372_a(var2, var3, var8.field_77276_a, var8.field_77275_b)) {
               StructureStart var10 = this.func_202369_a(var1, var2, var3, var8.field_77276_a, var8.field_77275_b);
               var7 = var10.func_75069_d() ? var10 : field_202376_c;
            } else {
               var7 = field_202376_c;
            }

            if (var7.func_75069_d()) {
               var1.func_72863_F().func_201713_d(var8.field_77276_a, var8.field_77275_b, true).func_201584_a(this.func_143025_a(), var7);
            }

            var6.put(var4, var7);
            return var7;
         }
      }
   }

   protected ChunkPos func_211744_a(IChunkGenerator<?> var1, Random var2, int var3, int var4, int var5, int var6) {
      return new ChunkPos(var3 + var5, var4 + var6);
   }

   protected abstract boolean func_202372_a(IChunkGenerator<?> var1, Random var2, int var3, int var4);

   protected abstract boolean func_202365_a(IWorld var1);

   protected abstract StructureStart func_202369_a(IWorld var1, IChunkGenerator<?> var2, SharedSeedRandom var3, int var4, int var5);

   protected abstract String func_143025_a();

   public abstract int func_202367_b();
}
