package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo {
   public static float field_74592_a;
   public static float field_74590_b;
   public static float field_74591_c;
   private static IntBuffer field_74597_i = GLAllocation.func_74527_f(16);
   private static FloatBuffer field_74594_j = GLAllocation.func_74529_h(16);
   private static FloatBuffer field_74595_k = GLAllocation.func_74529_h(16);
   private static FloatBuffer field_74593_l = GLAllocation.func_74529_h(3);
   public static float field_74588_d;
   public static float field_74589_e;
   public static float field_74586_f;
   public static float field_74587_g;
   public static float field_74596_h;

   public static void func_74583_a(EntityPlayer var0, boolean var1) {
      GL11.glGetFloat(2982, field_74594_j);
      GL11.glGetFloat(2983, field_74595_k);
      GL11.glGetInteger(2978, field_74597_i);
      float var2 = (float)((field_74597_i.get(0) + field_74597_i.get(2)) / 2);
      float var3 = (float)((field_74597_i.get(1) + field_74597_i.get(3)) / 2);
      GLU.gluUnProject(var2, var3, 0.0F, field_74594_j, field_74595_k, field_74597_i, field_74593_l);
      field_74592_a = field_74593_l.get(0);
      field_74590_b = field_74593_l.get(1);
      field_74591_c = field_74593_l.get(2);
      int var4 = var1 ? 1 : 0;
      float var5 = var0.field_70125_A;
      float var6 = var0.field_70177_z;
      field_74588_d = MathHelper.func_76134_b(var6 * 3.1415927F / 180.0F) * (float)(1 - var4 * 2);
      field_74586_f = MathHelper.func_76126_a(var6 * 3.1415927F / 180.0F) * (float)(1 - var4 * 2);
      field_74587_g = -field_74586_f * MathHelper.func_76126_a(var5 * 3.1415927F / 180.0F) * (float)(1 - var4 * 2);
      field_74596_h = field_74588_d * MathHelper.func_76126_a(var5 * 3.1415927F / 180.0F) * (float)(1 - var4 * 2);
      field_74589_e = MathHelper.func_76134_b(var5 * 3.1415927F / 180.0F);
   }

   public static Vec3 func_74585_b(EntityLivingBase var0, double var1) {
      double var3 = var0.field_70169_q + (var0.field_70165_t - var0.field_70169_q) * var1;
      double var5 = var0.field_70167_r + (var0.field_70163_u - var0.field_70167_r) * var1 + (double)var0.func_70047_e();
      double var7 = var0.field_70166_s + (var0.field_70161_v - var0.field_70166_s) * var1;
      double var9 = var3 + (double)(field_74592_a * 1.0F);
      double var11 = var5 + (double)(field_74590_b * 1.0F);
      double var13 = var7 + (double)(field_74591_c * 1.0F);
      return Vec3.func_72443_a(var9, var11, var13);
   }

   public static Block func_151460_a(World var0, EntityLivingBase var1, float var2) {
      Vec3 var3 = func_74585_b(var1, (double)var2);
      ChunkPosition var4 = new ChunkPosition(var3);
      Block var5 = var0.func_147439_a(var4.field_151329_a, var4.field_151327_b, var4.field_151328_c);
      if (var5.func_149688_o().func_76224_d()) {
         float var6 = BlockLiquid.func_149801_b(var0.func_72805_g(var4.field_151329_a, var4.field_151327_b, var4.field_151328_c)) - 0.11111111F;
         float var7 = (float)(var4.field_151327_b + 1) - var6;
         if (var3.field_72448_b >= (double)var7) {
            var5 = var0.func_147439_a(var4.field_151329_a, var4.field_151327_b + 1, var4.field_151328_c);
         }
      }

      return var5;
   }
}
