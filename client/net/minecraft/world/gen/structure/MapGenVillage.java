package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure {
   public static final List<BiomeGenBase> field_75055_e;
   private int field_75054_f;
   private int field_82665_g;
   private int field_82666_h;

   public MapGenVillage() {
      super();
      this.field_82665_g = 32;
      this.field_82666_h = 8;
   }

   public MapGenVillage(Map<String, String> var1) {
      this();
      Iterator var2 = var1.entrySet().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         if (((String)var3.getKey()).equals("size")) {
            this.field_75054_f = MathHelper.func_82714_a((String)var3.getValue(), this.field_75054_f, 0);
         } else if (((String)var3.getKey()).equals("distance")) {
            this.field_82665_g = MathHelper.func_82714_a((String)var3.getValue(), this.field_82665_g, this.field_82666_h + 1);
         }
      }

   }

   public String func_143025_a() {
      return "Village";
   }

   protected boolean func_75047_a(int var1, int var2) {
      int var3 = var1;
      int var4 = var2;
      if (var1 < 0) {
         var1 -= this.field_82665_g - 1;
      }

      if (var2 < 0) {
         var2 -= this.field_82665_g - 1;
      }

      int var5 = var1 / this.field_82665_g;
      int var6 = var2 / this.field_82665_g;
      Random var7 = this.field_75039_c.func_72843_D(var5, var6, 10387312);
      var5 *= this.field_82665_g;
      var6 *= this.field_82665_g;
      var5 += var7.nextInt(this.field_82665_g - this.field_82666_h);
      var6 += var7.nextInt(this.field_82665_g - this.field_82666_h);
      if (var3 == var5 && var4 == var6) {
         boolean var8 = this.field_75039_c.func_72959_q().func_76940_a(var3 * 16 + 8, var4 * 16 + 8, 0, field_75055_e);
         if (var8) {
            return true;
         }
      }

      return false;
   }

   protected StructureStart func_75049_b(int var1, int var2) {
      return new MapGenVillage.Start(this.field_75039_c, this.field_75038_b, var1, var2, this.field_75054_f);
   }

   static {
      field_75055_e = Arrays.asList(BiomeGenBase.field_76772_c, BiomeGenBase.field_76769_d, BiomeGenBase.field_150588_X);
   }

   public static class Start extends StructureStart {
      private boolean field_75076_c;

      public Start() {
         super();
      }

      public Start(World var1, Random var2, int var3, int var4, int var5) {
         super(var3, var4);
         List var6 = StructureVillagePieces.func_75084_a(var2, var5);
         StructureVillagePieces.Start var7 = new StructureVillagePieces.Start(var1.func_72959_q(), 0, var2, (var3 << 4) + 2, (var4 << 4) + 2, var6, var5);
         this.field_75075_a.add(var7);
         var7.func_74861_a(var7, this.field_75075_a, var2);
         List var8 = var7.field_74930_j;
         List var9 = var7.field_74932_i;

         int var10;
         while(!var8.isEmpty() || !var9.isEmpty()) {
            StructureComponent var11;
            if (var8.isEmpty()) {
               var10 = var2.nextInt(var9.size());
               var11 = (StructureComponent)var9.remove(var10);
               var11.func_74861_a(var7, this.field_75075_a, var2);
            } else {
               var10 = var2.nextInt(var8.size());
               var11 = (StructureComponent)var8.remove(var10);
               var11.func_74861_a(var7, this.field_75075_a, var2);
            }
         }

         this.func_75072_c();
         var10 = 0;
         Iterator var13 = this.field_75075_a.iterator();

         while(var13.hasNext()) {
            StructureComponent var12 = (StructureComponent)var13.next();
            if (!(var12 instanceof StructureVillagePieces.Road)) {
               ++var10;
            }
         }

         this.field_75076_c = var10 > 2;
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
