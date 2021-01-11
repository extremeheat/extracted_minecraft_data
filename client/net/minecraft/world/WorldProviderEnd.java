package net.minecraft.world;

import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderEnd;

public class WorldProviderEnd extends WorldProvider {
   public WorldProviderEnd() {
      super();
   }

   public void func_76572_b() {
      this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.field_76779_k, 0.0F);
      this.field_76574_g = 1;
      this.field_76576_e = true;
   }

   public IChunkProvider func_76555_c() {
      return new ChunkProviderEnd(this.field_76579_a, this.field_76579_a.func_72905_C());
   }

   public float func_76563_a(long var1, float var3) {
      return 0.0F;
   }

   public float[] func_76560_a(float var1, float var2) {
      return null;
   }

   public Vec3 func_76562_b(float var1, float var2) {
      int var3 = 10518688;
      float var4 = MathHelper.func_76134_b(var1 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      var4 = MathHelper.func_76131_a(var4, 0.0F, 1.0F);
      float var5 = (float)(var3 >> 16 & 255) / 255.0F;
      float var6 = (float)(var3 >> 8 & 255) / 255.0F;
      float var7 = (float)(var3 & 255) / 255.0F;
      var5 *= var4 * 0.0F + 0.15F;
      var6 *= var4 * 0.0F + 0.15F;
      var7 *= var4 * 0.0F + 0.15F;
      return new Vec3((double)var5, (double)var6, (double)var7);
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

   public boolean func_76566_a(int var1, int var2) {
      return this.field_76579_a.func_175703_c(new BlockPos(var1, 0, var2)).func_149688_o().func_76230_c();
   }

   public BlockPos func_177496_h() {
      return new BlockPos(100, 50, 0);
   }

   public int func_76557_i() {
      return 50;
   }

   public boolean func_76568_b(int var1, int var2) {
      return true;
   }

   public String func_80007_l() {
      return "The End";
   }

   public String func_177498_l() {
      return "_end";
   }
}
