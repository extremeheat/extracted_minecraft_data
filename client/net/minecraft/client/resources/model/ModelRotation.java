package net.minecraft.client.resources.model;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public enum ModelRotation {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map<Integer, ModelRotation> field_177546_q = Maps.newHashMap();
   private final int field_177545_r;
   private final Matrix4f field_177544_s;
   private final int field_177543_t;
   private final int field_177542_u;

   private static int func_177521_b(int var0, int var1) {
      return var0 * 360 + var1;
   }

   private ModelRotation(int var3, int var4) {
      this.field_177545_r = func_177521_b(var3, var4);
      this.field_177544_s = new Matrix4f();
      Matrix4f var5 = new Matrix4f();
      var5.setIdentity();
      Matrix4f.rotate((float)(-var3) * 0.017453292F, new Vector3f(1.0F, 0.0F, 0.0F), var5, var5);
      this.field_177543_t = MathHelper.func_76130_a(var3 / 90);
      Matrix4f var6 = new Matrix4f();
      var6.setIdentity();
      Matrix4f.rotate((float)(-var4) * 0.017453292F, new Vector3f(0.0F, 1.0F, 0.0F), var6, var6);
      this.field_177542_u = MathHelper.func_76130_a(var4 / 90);
      Matrix4f.mul(var6, var5, this.field_177544_s);
   }

   public Matrix4f func_177525_a() {
      return this.field_177544_s;
   }

   public EnumFacing func_177523_a(EnumFacing var1) {
      EnumFacing var2 = var1;

      int var3;
      for(var3 = 0; var3 < this.field_177543_t; ++var3) {
         var2 = var2.func_176732_a(EnumFacing.Axis.X);
      }

      if (var2.func_176740_k() != EnumFacing.Axis.Y) {
         for(var3 = 0; var3 < this.field_177542_u; ++var3) {
            var2 = var2.func_176732_a(EnumFacing.Axis.Y);
         }
      }

      return var2;
   }

   public int func_177520_a(EnumFacing var1, int var2) {
      int var3 = var2;
      if (var1.func_176740_k() == EnumFacing.Axis.X) {
         var3 = (var2 + this.field_177543_t) % 4;
      }

      EnumFacing var4 = var1;

      for(int var5 = 0; var5 < this.field_177543_t; ++var5) {
         var4 = var4.func_176732_a(EnumFacing.Axis.X);
      }

      if (var4.func_176740_k() == EnumFacing.Axis.Y) {
         var3 = (var3 + this.field_177542_u) % 4;
      }

      return var3;
   }

   public static ModelRotation func_177524_a(int var0, int var1) {
      return (ModelRotation)field_177546_q.get(func_177521_b(MathHelper.func_180184_b(var0, 360), MathHelper.func_180184_b(var1, 360)));
   }

   static {
      ModelRotation[] var0 = values();
      int var1 = var0.length;

      for(int var2 = 0; var2 < var1; ++var2) {
         ModelRotation var3 = var0[var2];
         field_177546_q.put(var3.field_177545_r, var3);
      }

   }
}
