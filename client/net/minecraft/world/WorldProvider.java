package net.minecraft.world;

import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderDebug;
import net.minecraft.world.gen.ChunkProviderFlat;
import net.minecraft.world.gen.ChunkProviderGenerate;
import net.minecraft.world.gen.FlatGeneratorInfo;

public abstract class WorldProvider {
   public static final float[] field_111203_a = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   protected World field_76579_a;
   private WorldType field_76577_b;
   private String field_82913_c;
   protected WorldChunkManager field_76578_c;
   protected boolean field_76575_d;
   protected boolean field_76576_e;
   protected final float[] field_76573_f = new float[16];
   protected int field_76574_g;
   private final float[] field_76580_h = new float[4];

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
      WorldType var1 = this.field_76579_a.func_72912_H().func_76067_t();
      if (var1 == WorldType.field_77138_c) {
         FlatGeneratorInfo var2 = FlatGeneratorInfo.func_82651_a(this.field_76579_a.func_72912_H().func_82571_y());
         this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.func_180276_a(var2.func_82648_a(), BiomeGenBase.field_180279_ad), 0.5F);
      } else if (var1 == WorldType.field_180272_g) {
         this.field_76578_c = new WorldChunkManagerHell(BiomeGenBase.field_76772_c, 0.0F);
      } else {
         this.field_76578_c = new WorldChunkManager(this.field_76579_a);
      }

   }

   public IChunkProvider func_76555_c() {
      if (this.field_76577_b == WorldType.field_77138_c) {
         return new ChunkProviderFlat(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r(), this.field_82913_c);
      } else if (this.field_76577_b == WorldType.field_180272_g) {
         return new ChunkProviderDebug(this.field_76579_a);
      } else {
         return this.field_76577_b == WorldType.field_180271_f ? new ChunkProviderGenerate(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r(), this.field_82913_c) : new ChunkProviderGenerate(this.field_76579_a, this.field_76579_a.func_72905_C(), this.field_76579_a.func_72912_H().func_76089_r(), this.field_82913_c);
      }
   }

   public boolean func_76566_a(int var1, int var2) {
      return this.field_76579_a.func_175703_c(new BlockPos(var1, 0, var2)) == Blocks.field_150349_c;
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

      float var6 = var5;
      var5 = 1.0F - (float)((Math.cos((double)var5 * 3.141592653589793D) + 1.0D) / 2.0D);
      var5 = var6 + (var5 - var6) / 3.0F;
      return var5;
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
      var3 = MathHelper.func_76131_a(var3, 0.0F, 1.0F);
      float var4 = 0.7529412F;
      float var5 = 0.84705883F;
      float var6 = 1.0F;
      var4 *= var3 * 0.94F + 0.06F;
      var5 *= var3 * 0.94F + 0.06F;
      var6 *= var3 * 0.91F + 0.09F;
      return new Vec3((double)var4, (double)var5, (double)var6);
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

   public BlockPos func_177496_h() {
      return null;
   }

   public int func_76557_i() {
      return this.field_76577_b == WorldType.field_77138_c ? 4 : this.field_76579_a.func_181545_F() + 1;
   }

   public double func_76565_k() {
      return this.field_76577_b == WorldType.field_77138_c ? 1.0D : 0.03125D;
   }

   public boolean func_76568_b(int var1, int var2) {
      return false;
   }

   public abstract String func_80007_l();

   public abstract String func_177498_l();

   public WorldChunkManager func_177499_m() {
      return this.field_76578_c;
   }

   public boolean func_177500_n() {
      return this.field_76575_d;
   }

   public boolean func_177495_o() {
      return this.field_76576_e;
   }

   public float[] func_177497_p() {
      return this.field_76573_f;
   }

   public int func_177502_q() {
      return this.field_76574_g;
   }

   public WorldBorder func_177501_r() {
      return new WorldBorder();
   }
}
