package net.minecraft.world.gen.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase$SpawnListEntry;

public class MapGenScatteredFeature extends MapGenStructure {
   private static List field_75061_e = Arrays.asList(
      BiomeGenBase.field_76769_d, BiomeGenBase.field_76786_s, BiomeGenBase.field_76782_w, BiomeGenBase.field_76792_x, BiomeGenBase.field_76780_h
   );
   private List field_82668_f = new ArrayList();
   private int field_82669_g = 32;
   private int field_82670_h = 8;

   public MapGenScatteredFeature() {
      super();
      this.field_82668_f.add(new BiomeGenBase$SpawnListEntry(EntityWitch.class, 1, 1, 1));
   }

   public MapGenScatteredFeature(Map var1) {
      this();

      for(Entry var3 : var1.entrySet()) {
         if (((String)var3.getKey()).equals("distance")) {
            this.field_82669_g = MathHelper.func_82714_a((String)var3.getValue(), this.field_82669_g, this.field_82670_h + 1);
         }
      }
   }

   @Override
   public String func_143025_a() {
      return "Temple";
   }

   @Override
   protected boolean func_75047_a(int var1, int var2) {
      int var3 = var1;
      int var4 = var2;
      if (var1 < 0) {
         var1 -= this.field_82669_g - 1;
      }

      if (var2 < 0) {
         var2 -= this.field_82669_g - 1;
      }

      int var5 = var1 / this.field_82669_g;
      int var6 = var2 / this.field_82669_g;
      Random var7 = this.field_75039_c.func_72843_D(var5, var6, 14357617);
      var5 *= this.field_82669_g;
      var6 *= this.field_82669_g;
      var5 += var7.nextInt(this.field_82669_g - this.field_82670_h);
      var6 += var7.nextInt(this.field_82669_g - this.field_82670_h);
      if (var3 == var5 && var4 == var6) {
         BiomeGenBase var8 = this.field_75039_c.func_72959_q().func_76935_a(var3 * 16 + 8, var4 * 16 + 8);

         for(BiomeGenBase var10 : field_75061_e) {
            if (var8 == var10) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   protected StructureStart func_75049_b(int var1, int var2) {
      return new MapGenScatteredFeature$Start(this.field_75039_c, this.field_75038_b, var1, var2);
   }

   public boolean func_143030_a(int var1, int var2, int var3) {
      StructureStart var4 = this.func_143028_c(var1, var2, var3);
      if (var4 != null && var4 instanceof MapGenScatteredFeature$Start && !var4.field_75075_a.isEmpty()) {
         StructureComponent var5 = (StructureComponent)var4.field_75075_a.getFirst();
         return var5 instanceof ComponentScatteredFeaturePieces$SwampHut;
      } else {
         return false;
      }
   }

   public List func_82667_a() {
      return this.field_82668_f;
   }
}
