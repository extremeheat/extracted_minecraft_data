package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure {
   private List<BiomeGenBase> field_151546_e;
   private boolean field_75056_f;
   private ChunkCoordIntPair[] field_75057_g;
   private double field_82671_h;
   private int field_82672_i;

   public MapGenStronghold() {
      super();
      this.field_75057_g = new ChunkCoordIntPair[3];
      this.field_82671_h = 32.0D;
      this.field_82672_i = 3;
      this.field_151546_e = Lists.newArrayList();
      BiomeGenBase[] var1 = BiomeGenBase.func_150565_n();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         BiomeGenBase var4 = var1[var3];
         if (var4 != null && var4.field_76748_D > 0.0F) {
            this.field_151546_e.add(var4);
         }
      }

   }

   public MapGenStronghold(Map<String, String> var1) {
      this();
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((String)var3.getKey()).equals("distance")) {
            this.field_82671_h = MathHelper.func_82713_a((String)var3.getValue(), this.field_82671_h, 1.0D);
         } else if (((String)var3.getKey()).equals("count")) {
            this.field_75057_g = new ChunkCoordIntPair[MathHelper.func_82714_a((String)var3.getValue(), this.field_75057_g.length, 1)];
         } else if (((String)var3.getKey()).equals("spread")) {
            this.field_82672_i = MathHelper.func_82714_a((String)var3.getValue(), this.field_82672_i, 1);
         }
      }

   }

   public String func_143025_a() {
      return "Stronghold";
   }

   protected boolean func_75047_a(int var1, int var2) {
      if (!this.field_75056_f) {
         Random var3 = new Random();
         var3.setSeed(this.field_75039_c.func_72905_C());
         double var4 = var3.nextDouble() * 3.141592653589793D * 2.0D;
         int var6 = 1;

         for(int var7 = 0; var7 < this.field_75057_g.length; ++var7) {
            double var8 = (1.25D * (double)var6 + var3.nextDouble()) * this.field_82671_h * (double)var6;
            int var10 = (int)Math.round(Math.cos(var4) * var8);
            int var11 = (int)Math.round(Math.sin(var4) * var8);
            BlockPos var12 = this.field_75039_c.func_72959_q().func_180630_a((var10 << 4) + 8, (var11 << 4) + 8, 112, this.field_151546_e, var3);
            if (var12 != null) {
               var10 = var12.func_177958_n() >> 4;
               var11 = var12.func_177952_p() >> 4;
            }

            this.field_75057_g[var7] = new ChunkCoordIntPair(var10, var11);
            var4 += 6.283185307179586D * (double)var6 / (double)this.field_82672_i;
            if (var7 == this.field_82672_i) {
               var6 += 2 + var3.nextInt(5);
               this.field_82672_i += 1 + var3.nextInt(2);
            }
         }

         this.field_75056_f = true;
      }

      ChunkCoordIntPair[] var13 = this.field_75057_g;
      int var14 = var13.length;

      for(int var5 = 0; var5 < var14; ++var5) {
         ChunkCoordIntPair var15 = var13[var5];
         if (var1 == var15.field_77276_a && var2 == var15.field_77275_b) {
            return true;
         }
      }

      return false;
   }

   protected List<BlockPos> func_75052_o_() {
      ArrayList var1 = Lists.newArrayList();
      ChunkCoordIntPair[] var2 = this.field_75057_g;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ChunkCoordIntPair var5 = var2[var4];
         if (var5 != null) {
            var1.add(var5.func_180619_a(64));
         }
      }

      return var1;
   }

   protected StructureStart func_75049_b(int var1, int var2) {
      MapGenStronghold.Start var3;
      for(var3 = new MapGenStronghold.Start(this.field_75039_c, this.field_75038_b, var1, var2); var3.func_75073_b().isEmpty() || ((StructureStrongholdPieces.Stairs2)var3.func_75073_b().get(0)).field_75025_b == null; var3 = new MapGenStronghold.Start(this.field_75039_c, this.field_75038_b, var1, var2)) {
      }

      return var3;
   }

   public static class Start extends StructureStart {
      public Start() {
         super();
      }

      public Start(World var1, Random var2, int var3, int var4) {
         super(var3, var4);
         StructureStrongholdPieces.func_75198_a();
         StructureStrongholdPieces.Stairs2 var5 = new StructureStrongholdPieces.Stairs2(0, var2, (var3 << 4) + 2, (var4 << 4) + 2);
         this.field_75075_a.add(var5);
         var5.func_74861_a(var5, this.field_75075_a, var2);
         List var6 = var5.field_75026_c;

         while(!var6.isEmpty()) {
            int var7 = var2.nextInt(var6.size());
            StructureComponent var8 = (StructureComponent)var6.remove(var7);
            var8.func_74861_a(var5, this.field_75075_a, var2);
         }

         this.func_75072_c();
         this.func_75067_a(var1, var2, 10);
      }
   }
}
