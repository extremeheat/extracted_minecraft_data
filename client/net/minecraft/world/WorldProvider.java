package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider {
   public static final float[] field_111203_a = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   public World field_76579_a;
   public WorldType field_76577_b;
   public String field_82913_c;
   public WorldChunkManager field_76578_c;
   public boolean field_76575_d;
   public boolean field_76576_e;
   public float[] field_76573_f = new float[16];
   public int field_76574_g;
   private float[] field_76580_h = new float[4];

   public WorldProvider() {
      super();
   }

   public final void func_76558_a(World var1) {
      this.field_76579_a = var1;
      this.field_76577_b = var1.func_72912_H().func_76067_t();
      this.field_82913_c = var1.func_72912_H().func_82571_y();
      this.func_76572_b();
      this.func_76556_a();
   }

   protected void func_76556_a() {
      float var1 = 0.0F;

      for(int var2 = 0; var2 <= 15; ++var2) {
         float var3 = 1.0F - (float)var2 / 15.0F;
         this.field_76573_f[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
      }
   }

   protected void func_76572_b() {
      if (this.field_76579_a.func_72912_H().func_76067_t() == WorldType.field_77138_c) {
         FlatGeneratorInfo var1 = FlatGeneratorInfo.func_82651_a(this.field_76579_a.func_72912_H().func_82571_y());
         this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.func_150568_d(var1.func_82648_a()), 0.5F);
      } else {
         this.field_76578_c = new WorldChunkManager(this.field_76579_a);
      }
   }

   public IChunkProvider func_76555_c() {
      return (IChunkProvider)(this.field_76577_b == WorldType.field_77138_c
         ? new ChunkProviderFlat(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r(), this.field_82913_c)
         : new ChunkProviderGenerate(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r()));
   }

   public boolean func_76566_a(int var1, int var2) {
      return this.field_76579_a.func_147474_b(var1, var2) == Blocks.field_150349_c;
   }

   public float func_76563_a(long var1, float var3) {
      int var4 = (int)(var1 % 24000L);
      float var5 = ((float)var4 + var3) / 24000.0F - 0.25F;
      if (var5 < 0.0F) {
         ++var5;
      }

      if (var5 > 1.0F) {
         --var5;
      }

      float var7 = 1.0F - (float)((Math.cos((double)var5 * 3.141592653589793) + 1.0) / 2.0);
      return var5 + (var7 - var5) / 3.0F;
   }

   public int func_76559_b(long var1) {
      return (int)(var1 / 24000L % 8L + 8L) % 8;
   }

   public boolean func_76569_d() {
      return true;
   }

   public float[] func_76560_a(float var1, float var2) {
      float var3 = 0.4F;
      float var4 = MathHelper.func_76134_b(var1 * 3.1415927F * 2.0F) - 0.0F;
      float var5 = -0.0F;
      if (var4 >= var5 - var3 && var4 <= var5 + var3) {
         float var6 = (var4 - var5) / var3 * 0.5F + 0.5F;
         float var7 = 1.0F - (1.0F - MathHelper.func_76126_a(var6 * 3.1415927F)) * 0.99F;
         var7 *= var7;
         this.field_76580_h[0] = var6 * 0.3F + 0.7F;
         this.field_76580_h[1] = var6 * var6 * 0.7F + 0.2F;
         this.field_76580_h[2] = var6 * var6 * 0.0F + 0.2F;
         this.field_76580_h[3] = var7;
         return this.field_76580_h;
      } else {
         return null;
      }
   }

   public Vec3 func_76562_b(float var1, float var2) {
      float var3 = MathHelper.func_76134_b(var1 * 3.1415927F * 2.0F) * 2.0F + 0.5F;
      if (var3 < 0.0F) {
         var3 = 0.0F;
      }

      if (var3 > 1.0F) {
         var3 = 1.0F;
      }

      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return Vec3.func_72443_a((double)var4, (double)var5, (double)var6);
   }

   public boolean func_76567_e() {
      return true;
   }

   public static WorldProvider func_76570_a(int var0) {
      if (var0 == -1) {
         return new WorldProviderHell();
      } else if (var0 == 0) {
         return new WorldProviderSurface();
      } else {
         return var0 == 1 ? new WorldProviderEnd() : null;
      }
   }

   public float func_76571_f() {
      return 128.0F;
   }

   public boolean func_76561_g() {
      return true;
   }

   public ChunkCoordinates func_76554_h() {
      return null;
   }

   public int func_76557_i() {
      return this.field_76577_b == WorldType.field_77138_c ? 4 : 64;
   }

   public boolean func_76564_j() {
      return this.field_76577_b != WorldType.field_77138_c && !this.field_76576_e;
   }

   public double func_76565_k() {
      return this.field_76577_b == WorldType.field_77138_c ? 1.0 : 0.03125;
   }

   public boolean func_76568_b(int var1, int var2) {
      return false;
   }

   public abstract String func_80007_l();
}
