package net.minecraft.world.gen.structure;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenScatteredFeature$Start extends StructureStart {
   public MapGenScatteredFeature$Start() {
      super();
   }

   public MapGenScatteredFeature$Start(World var1, Random var2, int var3, int var4) {
      super(var3, var4);
      BiomeGenBase var5 = var1.func_72807_a(var3 * 16 + 8, var4 * 16 + 8);
      if (var5 == BiomeGenBase.field_76782_w || var5 == BiomeGenBase.field_76792_x) {
         ComponentScatteredFeaturePieces$JunglePyramid var8 = new ComponentScatteredFeaturePieces$JunglePyramid(var2, var3 * 16, var4 * 16);
         this.field_75075_a.add(var8);
      } else if (var5 == BiomeGenBase.field_76780_h) {
         ComponentScatteredFeaturePieces$SwampHut var6 = new ComponentScatteredFeaturePieces$SwampHut(var2, var3 * 16, var4 * 16);
         this.field_75075_a.add(var6);
      } else {
         ComponentScatteredFeaturePieces$DesertPyramid var7 = new ComponentScatteredFeaturePieces$DesertPyramid(var2, var3 * 16, var4 * 16);
         this.field_75075_a.add(var7);
      }

      this.func_75072_c();
   }
}
