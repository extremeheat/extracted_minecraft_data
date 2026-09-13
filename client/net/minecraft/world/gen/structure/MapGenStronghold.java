package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenStronghold extends MapGenStructure {
   private List field_151546_e;
   private boolean field_75056_f;
   private ChunkCoordIntPair[] field_75057_g = new ChunkCoordIntPair[3];
   private double field_82671_h = 32.0;
   private int field_82672_i = 3;

   public MapGenStronghold() {
      super();
      this.field_151546_e = new ArrayList();

      for(BiomeGenBase var4 : BiomeGenBase.func_150565_n()) {
         if (var4 != null && var4.field_76748_D > 0.0F) {
            this.field_151546_e.add(var4);
         }
      }
   }

   public MapGenStronghold(Map var1) {
      this();

      for(Entry var3 : var1.entrySet()) {
         if (((String)var3.getKey()).equals("distance")) {
            this.field_82671_h = MathHelper.func_82713_a((String)var3.getValue(), this.field_82671_h, 1.0);
         } else if (((String)var3.getKey()).equals("count")) {
            this.field_75057_g = new ChunkCoordIntPair[MathHelper.func_82714_a((String)var3.getValue(), this.field_75057_g.length, 1)];
         } else if (((String)var3.getKey()).equals("spread")) {
            this.field_82672_i = MathHelper.func_82714_a((String)var3.getValue(), this.field_82672_i, 1);
         }
      }
   }

   @Override
   public String func_143025_a() {
      return "Stronghold";
   }

   @Override
   protected boolean func_75047_a(int var1, int var2) {
      if (!this.field_75056_f) {
         Random var3 = new Random();
         var3.setSeed(this.field_75039_c.func_72905_C());
         double var4 = var3.nextDouble() * 3.141592653589793 * 2.0;
         int var6 = 1;

         for(int var7 = 0; var7 < this.field_75057_g.length; ++var7) {
            double var8 = (1.25 * (double)var6 + var3.nextDouble()) * this.field_82671_h * (double)var6;
            int var10 = (int)Math.round(Math.cos(var4) * var8);
            int var11 = (int)Math.round(Math.sin(var4) * var8);
            ChunkPosition var12 = this.field_75039_c.func_72959_q().func_150795_a((var10 << 4) + 8, (var11 << 4) + 8, 112, this.field_151546_e, var3);
            if (var12 != null) {
               var10 = var12.field_151329_a >> 4;
               var11 = var12.field_151328_c >> 4;
            }

            this.field_75057_g[var7] = new ChunkCoordIntPair(var10, var11);
            var4 += 6.283185307179586 * (double)var6 / (double)this.field_82672_i;
            if (var7 == this.field_82672_i) {
               var6 += 2 + var3.nextInt(5);
               this.field_82672_i += 1 + var3.nextInt(2);
            }
         }

         this.field_75056_f = true;
      }

      for(ChunkCoordIntPair var15 : this.field_75057_g) {
         if (var1 == var15.field_77276_a && var2 == var15.field_77275_b) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected List func_75052_o_() {
      ArrayList var1 = new ArrayList();

      for(ChunkCoordIntPair var5 : this.field_75057_g) {
         if (var5 != null) {
            var1.add(var5.func_151349_a(64));
         }
      }

      return var1;
   }

   @Override
   protected StructureStart func_75049_b(int var1, int var2) {
      MapGenStronghold$Start var3 = new MapGenStronghold$Start(this.field_75039_c, this.field_75038_b, var1, var2);

      while(var3.func_75073_b().isEmpty() || ((StructureStrongholdPieces$Stairs2)var3.func_75073_b().get(0)).field_75025_b == null) {
         var3 = new MapGenStronghold$Start(this.field_75039_c, this.field_75038_b, var1, var2);
      }

      return var3;
   }
}
