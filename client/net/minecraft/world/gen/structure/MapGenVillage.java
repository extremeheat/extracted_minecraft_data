package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure {
   public static final List field_75055_e = Arrays.asList(BiomeGenBase.field_76772_c, BiomeGenBase.field_76769_d, BiomeGenBase.field_150588_X);
   private int field_75054_f;
   private int field_82665_g = 32;
   private int field_82666_h = 8;

   public MapGenVillage() {
      super();
   }

   public MapGenVillage(Map var1) {
      this();

      for(Entry var3 : var1.entrySet()) {
         if (((String)var3.getKey()).equals("size")) {
            this.field_75054_f = MathHelper.func_82714_a((String)var3.getValue(), this.field_75054_f, 0);
         } else if (((String)var3.getKey()).equals("distance")) {
            this.field_82665_g = MathHelper.func_82714_a((String)var3.getValue(), this.field_82665_g, this.field_82666_h + 1);
         }
      }
   }

   @Override
   public String func_143025_a() {
      return "Village";
   }

   @Override
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

   @Override
   protected StructureStart func_75049_b(int var1, int var2) {
      return new MapGenVillage$Start(this.field_75039_c, this.field_75038_b, var1, var2, this.field_75054_f);
   }
}
