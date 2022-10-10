package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockReader;

public class ActiveRenderInfo {
   private static final FloatBuffer field_178812_b = GLAllocation.func_74529_h(16);
   private static Vec3d field_178811_e = new Vec3d(0.0D, 0.0D, 0.0D);
   private static float field_74588_d;
   private static float field_74589_e;
   private static float field_74586_f;
   private static float field_74587_g;
   private static float field_74596_h;

   public static void func_197924_a(EntityPlayer var0, boolean var1, float var2) {
      field_178812_b.clear();
      GlStateManager.func_179111_a(2982, field_178812_b);
      Matrix4f var3 = new Matrix4f();
      var3.func_195874_a(field_178812_b);
      var3.func_195887_c();
      float var4 = 0.05F;
      float var5 = var2 * MathHelper.field_180189_a;
      Vector4f var6 = new Vector4f(0.0F, 0.0F, -2.0F * var5 * 0.05F / (var5 + 0.05F), 1.0F);
      var6.func_195908_a(var3);
      field_178811_e = new Vec3d((double)var6.func_195910_a(), (double)var6.func_195913_b(), (double)var6.func_195914_c());
      float var7 = var0.field_70125_A;
      float var8 = var0.field_70177_z;
      int var9 = var1 ? -1 : 1;
      field_74588_d = MathHelper.func_76134_b(var8 * 0.017453292F) * (float)var9;
      field_74586_f = MathHelper.func_76126_a(var8 * 0.017453292F) * (float)var9;
      field_74587_g = -field_74586_f * MathHelper.func_76126_a(var7 * 0.017453292F) * (float)var9;
      field_74596_h = field_74588_d * MathHelper.func_76126_a(var7 * 0.017453292F) * (float)var9;
      field_74589_e = MathHelper.func_76134_b(var7 * 0.017453292F);
   }

   public static Vec3d func_178806_a(Entity var0, double var1) {
      double var3 = var0.field_70169_q + (var0.field_70165_t - var0.field_70169_q) * var1;
      double var5 = var0.field_70167_r + (var0.field_70163_u - var0.field_70167_r) * var1;
      double var7 = var0.field_70166_s + (var0.field_70161_v - var0.field_70166_s) * var1;
      double var9 = var3 + field_178811_e.field_72450_a;
      double var11 = var5 + field_178811_e.field_72448_b;
      double var13 = var7 + field_178811_e.field_72449_c;
      return new Vec3d(var9, var11, var13);
   }

   public static IBlockState func_186703_a(IBlockReader var0, Entity var1, float var2) {
      Vec3d var3 = func_178806_a(var1, (double)var2);
      BlockPos var4 = new BlockPos(var3);
      IBlockState var5 = var0.func_180495_p(var4);
      IFluidState var6 = var0.func_204610_c(var4);
      if (!var6.func_206888_e()) {
         float var7 = (float)var4.func_177956_o() + var6.func_206885_f() + 0.11111111F;
         if (var3.field_72448_b >= (double)var7) {
            var5 = var0.func_180495_p(var4.func_177984_a());
         }
      }

      return var5;
   }

   public static IFluidState func_206243_b(IBlockReader var0, Entity var1, float var2) {
      Vec3d var3 = func_178806_a(var1, (double)var2);
      BlockPos var4 = new BlockPos(var3);
      IFluidState var5 = var0.func_204610_c(var4);
      if (!var5.func_206888_e()) {
         float var6 = (float)var4.func_177956_o() + var5.func_206885_f() + 0.11111111F;
         if (var3.field_72448_b >= (double)var6) {
            var5 = var0.func_204610_c(var4.func_177984_a());
         }
      }

      return var5;
   }

   public static float func_178808_b() {
      return field_74588_d;
   }

   public static float func_178809_c() {
      return field_74589_e;
   }

   public static float func_178803_d() {
      return field_74586_f;
   }

   public static float func_178805_e() {
      return field_74587_g;
   }

   public static float func_178807_f() {
      return field_74596_h;
   }
}
