package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.biome.provider.EndBiomeProviderSettings;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.EndGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

public class EndDimension extends Dimension {
   public static final BlockPos field_209958_g = new BlockPos(100, 50, 0);
   private DragonFightManager field_186064_g;

   public EndDimension() {
      super();
   }

   public void func_76572_b() {
      NBTTagCompound var1 = this.field_76579_a.func_72912_H().func_186347_a(DimensionType.THE_END);
      this.field_186064_g = this.field_76579_a instanceof WorldServer ? new DragonFightManager((WorldServer)this.field_76579_a, var1.func_74775_l("DragonFight")) : null;
      this.field_191067_f = false;
   }

   public IChunkGenerator<?> func_186060_c() {
      EndGenSettings var1 = (EndGenSettings)ChunkGeneratorType.field_206913_d.func_205483_a();
      var1.func_205535_a(Blocks.field_150377_bs.func_176223_P());
      var1.func_205534_b(Blocks.field_150350_a.func_176223_P());
      var1.func_205538_a(this.func_177496_h());
      return ChunkGeneratorType.field_206913_d.create(this.field_76579_a, BiomeProviderType.field_205463_e.func_205457_a(((EndBiomeProviderSettings)BiomeProviderType.field_205463_e.func_205458_a()).func_205446_a(this.field_76579_a.func_72905_C())), var1);
   }

   public float func_76563_a(long var1, float var3) {
      return 0.0F;
   }

   @Nullable
   public float[] func_76560_a(float var1, float var2) {
      return null;
   }

   public Vec3d func_76562_b(float var1, float var2) {
      int var3 = 10518688;
      float var4 = MathHelper.func_76134_b(var1 * 6.2831855F) * 2.0F + 0.5F;
      var4 = MathHelper.func_76131_a(var4, 0.0F, 1.0F);
      float var5 = 0.627451F;
      float var6 = 0.5019608F;
      float var7 = 0.627451F;
      var5 *= var4 * 0.0F + 0.15F;
      var6 *= var4 * 0.0F + 0.15F;
      var7 *= var4 * 0.0F + 0.15F;
      return new Vec3d((double)var5, (double)var6, (double)var7);
   }

   public boolean func_76561_g() {
      return false;
   }

   public boolean func_76567_e() {
      return false;
   }

   public boolean func_76569_d() {
      return false;
   }

   public float func_76571_f() {
      return 8.0F;
   }

   @Nullable
   public BlockPos func_206920_a(ChunkPos var1, boolean var2) {
      Random var3 = new Random(this.field_76579_a.func_72905_C());
      BlockPos var4 = new BlockPos(var1.func_180334_c() + var3.nextInt(15), 0, var1.func_180330_f() + var3.nextInt(15));
      return this.field_76579_a.func_184141_c(var4).func_185904_a().func_76230_c() ? var4 : null;
   }

   public BlockPos func_177496_h() {
      return field_209958_g;
   }

   @Nullable
   public BlockPos func_206921_a(int var1, int var2, boolean var3) {
      return this.func_206920_a(new ChunkPos(var1 >> 4, var2 >> 4), var3);
   }

   public boolean func_76568_b(int var1, int var2) {
      return false;
   }

   public DimensionType func_186058_p() {
      return DimensionType.THE_END;
   }

   public void func_186057_q() {
      NBTTagCompound var1 = new NBTTagCompound();
      if (this.field_186064_g != null) {
         var1.func_74782_a("DragonFight", this.field_186064_g.func_186088_a());
      }

      this.field_76579_a.func_72912_H().func_186345_a(DimensionType.THE_END, var1);
   }

   public void func_186059_r() {
      if (this.field_186064_g != null) {
         this.field_186064_g.func_186105_b();
      }

   }

   @Nullable
   public DragonFightManager func_186063_s() {
      return this.field_186064_g;
   }
}
