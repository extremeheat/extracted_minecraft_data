package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class FossilsFeature extends Feature<NoFeatureConfig> {
   private static final ResourceLocation field_189890_a = new ResourceLocation("fossil/spine_1");
   private static final ResourceLocation field_189891_b = new ResourceLocation("fossil/spine_2");
   private static final ResourceLocation field_189892_c = new ResourceLocation("fossil/spine_3");
   private static final ResourceLocation field_189893_d = new ResourceLocation("fossil/spine_4");
   private static final ResourceLocation field_189894_e = new ResourceLocation("fossil/spine_1_coal");
   private static final ResourceLocation field_189895_f = new ResourceLocation("fossil/spine_2_coal");
   private static final ResourceLocation field_189896_g = new ResourceLocation("fossil/spine_3_coal");
   private static final ResourceLocation field_189897_h = new ResourceLocation("fossil/spine_4_coal");
   private static final ResourceLocation field_189898_i = new ResourceLocation("fossil/skull_1");
   private static final ResourceLocation field_189899_j = new ResourceLocation("fossil/skull_2");
   private static final ResourceLocation field_189900_k = new ResourceLocation("fossil/skull_3");
   private static final ResourceLocation field_189901_l = new ResourceLocation("fossil/skull_4");
   private static final ResourceLocation field_189902_m = new ResourceLocation("fossil/skull_1_coal");
   private static final ResourceLocation field_189903_n = new ResourceLocation("fossil/skull_2_coal");
   private static final ResourceLocation field_189904_o = new ResourceLocation("fossil/skull_3_coal");
   private static final ResourceLocation field_189905_p = new ResourceLocation("fossil/skull_4_coal");
   private static final ResourceLocation[] field_189906_q;
   private static final ResourceLocation[] field_189907_r;

   public FossilsFeature() {
      super();
   }

   public boolean func_212245_a(IWorld var1, IChunkGenerator<? extends IChunkGenSettings> var2, Random var3, BlockPos var4, NoFeatureConfig var5) {
      Random var6 = var1.func_201674_k();
      Rotation[] var7 = Rotation.values();
      Rotation var8 = var7[var6.nextInt(var7.length)];
      int var9 = var6.nextInt(field_189906_q.length);
      TemplateManager var10 = var1.func_72860_G().func_186340_h();
      Template var11 = var10.func_200220_a(field_189906_q[var9]);
      Template var12 = var10.func_200220_a(field_189907_r[var9]);
      ChunkPos var13 = new ChunkPos(var4);
      MutableBoundingBox var14 = new MutableBoundingBox(var13.func_180334_c(), 0, var13.func_180333_d(), var13.func_180332_e(), 256, var13.func_180330_f());
      PlacementSettings var15 = (new PlacementSettings()).func_186220_a(var8).func_186223_a(var14).func_189950_a(var6);
      BlockPos var16 = var11.func_186257_a(var8);
      int var17 = var6.nextInt(16 - var16.func_177958_n());
      int var18 = var6.nextInt(16 - var16.func_177952_p());
      int var19 = 256;

      int var20;
      for(var20 = 0; var20 < var16.func_177958_n(); ++var20) {
         for(int var21 = 0; var21 < var16.func_177958_n(); ++var21) {
            var19 = Math.min(var19, var1.func_201676_a(Heightmap.Type.OCEAN_FLOOR_WG, var4.func_177958_n() + var20 + var17, var4.func_177952_p() + var21 + var18));
         }
      }

      var20 = Math.max(var19 - 15 - var6.nextInt(10), 10);
      BlockPos var22 = var11.func_189961_a(var4.func_177982_a(var17, var20, var18), Mirror.NONE, var8);
      var15.func_189946_a(0.9F);
      var11.func_189962_a(var1, var22, var15, 4);
      var15.func_189946_a(0.1F);
      var12.func_189962_a(var1, var22, var15, 4);
      return true;
   }

   static {
      field_189906_q = new ResourceLocation[]{field_189890_a, field_189891_b, field_189892_c, field_189893_d, field_189898_i, field_189899_j, field_189900_k, field_189901_l};
      field_189907_r = new ResourceLocation[]{field_189894_e, field_189895_f, field_189896_g, field_189897_h, field_189902_m, field_189903_n, field_189904_o, field_189905_p};
   }
}
