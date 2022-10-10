package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.SingleBiomeProviderSettings;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.NetherGenSettings;

public class NetherDimension extends Dimension {
   public NetherDimension() {
      super();
   }

   public void func_76572_b() {
      this.field_76575_d = true;
      this.field_76576_e = true;
      this.field_191067_f = false;
   }

   public Vec3d func_76562_b(float var1, float var2) {
      return new Vec3d(0.20000000298023224D, 0.029999999329447746D, 0.029999999329447746D);
   }

   protected void func_76556_a() {
      float var1 = 0.1F;

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = 1.0F - (float)var2 / 15.0F;
         this.field_76573_f[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * 0.9F + 0.1F;
      }

   }

   public IChunkGenerator<?> func_186060_c() {
      NetherGenSettings var1 = (NetherGenSettings)ChunkGeneratorType.field_206912_c.func_205483_a();
      var1.func_205535_a(Blocks.field_150424_aL.func_176223_P());
      var1.func_205534_b(Blocks.field_150353_l.func_176223_P());
      return ChunkGeneratorType.field_206912_c.create(this.field_76579_a, BiomeProviderType.field_205461_c.func_205457_a(((SingleBiomeProviderSettings)BiomeProviderType.field_205461_c.func_205458_a()).func_205436_a(Biomes.field_76778_j)), var1);
   }

   public boolean func_76569_d() {
      return false;
   }

   @Nullable
   public BlockPos func_206920_a(ChunkPos var1, boolean var2) {
      return null;
   }

   @Nullable
   public BlockPos func_206921_a(int var1, int var2, boolean var3) {
      return null;
   }

   public float func_76563_a(long var1, float var3) {
      return 0.5F;
   }

   public boolean func_76567_e() {
      return false;
   }

   public boolean func_76568_b(int var1, int var2) {
      return true;
   }

   public WorldBorder func_177501_r() {
      return new WorldBorder() {
         public double func_177731_f() {
            return super.func_177731_f() / 8.0D;
         }

         public double func_177721_g() {
            return super.func_177721_g() / 8.0D;
         }
      };
   }

   public DimensionType func_186058_p() {
      return DimensionType.NETHER;
   }
}
