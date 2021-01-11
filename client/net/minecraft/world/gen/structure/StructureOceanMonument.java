package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure {
   private int field_175800_f;
   private int field_175801_g;
   public static final List<BiomeGenBase> field_175802_d;
   private static final List<BiomeGenBase.SpawnListEntry> field_175803_h;

   public StructureOceanMonument() {
      super();
      this.field_175800_f = 32;
      this.field_175801_g = 5;
   }

   public StructureOceanMonument(Map<String, String> var1) {
      this();
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((String)var3.getKey()).equals("spacing")) {
            this.field_175800_f = MathHelper.func_82714_a((String)var3.getValue(), this.field_175800_f, 1);
         } else if (((String)var3.getKey()).equals("separation")) {
            this.field_175801_g = MathHelper.func_82714_a((String)var3.getValue(), this.field_175801_g, 1);
         }
      }

   }

   public String func_143025_a() {
      return "Monument";
   }

   protected boolean func_75047_a(int var1, int var2) {
      int var3 = var1;
      int var4 = var2;
      if (var1 < 0) {
         var1 -= this.field_175800_f - 1;
      }

      if (var2 < 0) {
         var2 -= this.field_175800_f - 1;
      }

      int var5 = var1 / this.field_175800_f;
      int var6 = var2 / this.field_175800_f;
      Random var7 = this.field_75039_c.func_72843_D(var5, var6, 10387313);
      var5 *= this.field_175800_f;
      var6 *= this.field_175800_f;
      var5 += (var7.nextInt(this.field_175800_f - this.field_175801_g) + var7.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
      var6 += (var7.nextInt(this.field_175800_f - this.field_175801_g) + var7.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
      if (var3 == var5 && var4 == var6) {
         if (this.field_75039_c.func_72959_q().func_180300_a(new BlockPos(var3 * 16 + 8, 64, var4 * 16 + 8), (BiomeGenBase)null) != BiomeGenBase.field_150575_M) {
            return false;
         }

         boolean var8 = this.field_75039_c.func_72959_q().func_76940_a(var3 * 16 + 8, var4 * 16 + 8, 29, field_175802_d);
         if (var8) {
            return true;
         }
      }

      return false;
   }

   protected StructureStart func_75049_b(int var1, int var2) {
      return new StructureOceanMonument.StartMonument(this.field_75039_c, this.field_75038_b, var1, var2);
   }

   public List<BiomeGenBase.SpawnListEntry> func_175799_b() {
      return field_175803_h;
   }

   static {
      field_175802_d = Arrays.asList(BiomeGenBase.field_76771_b, BiomeGenBase.field_150575_M, BiomeGenBase.field_76781_i, BiomeGenBase.field_76776_l, BiomeGenBase.field_76777_m);
      field_175803_h = Lists.newArrayList();
      field_175803_h.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
   }

   public static class StartMonument extends StructureStart {
      private Set<ChunkCoordIntPair> field_175791_c = Sets.newHashSet();
      private boolean field_175790_d;

      public StartMonument() {
         super();
      }

      public StartMonument(World var1, Random var2, int var3, int var4) {
         super(var3, var4);
         this.func_175789_b(var1, var2, var3, var4);
      }

      private void func_175789_b(World var1, Random var2, int var3, int var4) {
         var2.setSeed(var1.func_72905_C());
         long var5 = var2.nextLong();
         long var7 = var2.nextLong();
         long var9 = (long)var3 * var5;
         long var11 = (long)var4 * var7;
         var2.setSeed(var9 ^ var11 ^ var1.func_72905_C());
         int var13 = var3 * 16 + 8 - 29;
         int var14 = var4 * 16 + 8 - 29;
         EnumFacing var15 = EnumFacing.Plane.HORIZONTAL.func_179518_a(var2);
         this.field_75075_a.add(new StructureOceanMonumentPieces.MonumentBuilding(var2, var13, var14, var15));
         this.func_75072_c();
         this.field_175790_d = true;
      }

      public void func_75068_a(World var1, Random var2, StructureBoundingBox var3) {
         if (!this.field_175790_d) {
            this.field_75075_a.clear();
            this.func_175789_b(var1, var2, this.func_143019_e(), this.func_143018_f());
         }

         super.func_75068_a(var1, var2, var3);
      }

      public boolean func_175788_a(ChunkCoordIntPair var1) {
         return this.field_175791_c.contains(var1) ? false : super.func_175788_a(var1);
      }

      public void func_175787_b(ChunkCoordIntPair var1) {
         super.func_175787_b(var1);
         this.field_175791_c.add(var1);
      }

      public void func_143022_a(NBTTagCompound var1) {
         super.func_143022_a(var1);
         NBTTagList var2 = new NBTTagList();
         Iterator var3 = this.field_175791_c.iterator();

         while(var3.hasNext()) {
            ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
            NBTTagCompound var5 = new NBTTagCompound();
            var5.func_74768_a("X", var4.field_77276_a);
            var5.func_74768_a("Z", var4.field_77275_b);
            var2.func_74742_a(var5);
         }

         var1.func_74782_a("Processed", var2);
      }

      public void func_143017_b(NBTTagCompound var1) {
         super.func_143017_b(var1);
         if (var1.func_150297_b("Processed", 9)) {
            NBTTagList var2 = var1.func_150295_c("Processed", 10);

            for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
               NBTTagCompound var4 = var2.func_150305_b(var3);
               this.field_175791_c.add(new ChunkCoordIntPair(var4.func_74762_e("X"), var4.func_74762_e("Z")));
            }
         }

      }
   }
}
