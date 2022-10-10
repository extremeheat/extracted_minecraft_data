package net.minecraft.world.gen;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;

public class ChunkGeneratorDebug extends AbstractChunkGenerator<DebugGenSettings> {
   private static final List<IBlockState> field_177464_a;
   private static final int field_177462_b;
   private static final int field_181039_c;
   protected static final IBlockState field_185934_a;
   protected static final IBlockState field_185935_b;
   private final DebugGenSettings field_202098_i;

   public ChunkGeneratorDebug(IWorld var1, BiomeProvider var2, DebugGenSettings var3) {
      super(var1, var2);
      this.field_202098_i = var3;
   }

   public void func_202088_a(IChunk var1) {
      ChunkPos var2 = var1.func_76632_l();
      int var3 = var2.field_77276_a;
      int var4 = var2.field_77275_b;
      Biome[] var5 = this.field_202097_c.func_201539_b(var3 * 16, var4 * 16, 16, 16);
      var1.func_201577_a(var5);
      var1.func_201588_a(Heightmap.Type.WORLD_SURFACE_WG, Heightmap.Type.OCEAN_FLOOR_WG);
      var1.func_201574_a(ChunkStatus.BASE);
   }

   public void func_202091_a(WorldGenRegion var1, GenerationStage.Carving var2) {
   }

   public DebugGenSettings func_201496_a_() {
      return this.field_202098_i;
   }

   public double[] func_205473_a(int var1, int var2) {
      return new double[0];
   }

   public int func_205470_d() {
      return this.field_202095_a.func_181545_F() + 1;
   }

   public void func_202092_b(WorldGenRegion var1) {
      BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();
      int var3 = var1.func_201679_a();
      int var4 = var1.func_201680_b();

      for(int var5 = 0; var5 < 16; ++var5) {
         for(int var6 = 0; var6 < 16; ++var6) {
            int var7 = (var3 << 4) + var5;
            int var8 = (var4 << 4) + var6;
            var1.func_180501_a(var2.func_181079_c(var7, 60, var8), field_185935_b, 2);
            IBlockState var9 = func_177461_b(var7, var8);
            if (var9 != null) {
               var1.func_180501_a(var2.func_181079_c(var7, 70, var8), var9, 2);
            }
         }
      }

   }

   public void func_202093_c(WorldGenRegion var1) {
   }

   public static IBlockState func_177461_b(int var0, int var1) {
      IBlockState var2 = field_185934_a;
      if (var0 > 0 && var1 > 0 && var0 % 2 != 0 && var1 % 2 != 0) {
         var0 /= 2;
         var1 /= 2;
         if (var0 <= field_177462_b && var1 <= field_181039_c) {
            int var3 = MathHelper.func_76130_a(var0 * field_177462_b + var1);
            if (var3 < field_177464_a.size()) {
               var2 = (IBlockState)field_177464_a.get(var3);
            }
         }
      }

      return var2;
   }

   public List<Biome.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      Biome var3 = this.field_202095_a.func_180494_b(var2);
      return var3.func_76747_a(var1);
   }

   public int func_203222_a(World var1, boolean var2, boolean var3) {
      return 0;
   }

   // $FF: synthetic method
   public IChunkGenSettings func_201496_a_() {
      return this.func_201496_a_();
   }

   static {
      field_177464_a = (List)StreamSupport.stream(IRegistry.field_212618_g.spliterator(), false).flatMap((var0) -> {
         return var0.func_176194_O().func_177619_a().stream();
      }).collect(Collectors.toList());
      field_177462_b = MathHelper.func_76123_f(MathHelper.func_76129_c((float)field_177464_a.size()));
      field_181039_c = MathHelper.func_76123_f((float)field_177464_a.size() / (float)field_177462_b);
      field_185934_a = Blocks.field_150350_a.func_176223_P();
      field_185935_b = Blocks.field_180401_cv.func_176223_P();
   }
}
