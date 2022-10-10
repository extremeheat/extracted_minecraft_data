package net.minecraft.client.renderer.model;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;

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

   private static final Map<Integer, ModelRotation> field_177546_q = (Map)Arrays.stream(values()).sorted(Comparator.comparingInt((var0) -> {
      return var0.field_177545_r;
   })).collect(Collectors.toMap((var0) -> {
      return var0.field_177545_r;
   }, (var0) -> {
      return var0;
   }));
   private final int field_177545_r;
   private final Quaternion field_177544_s;
   private final int field_177543_t;
   private final int field_177542_u;

   private static int func_177521_b(int var0, int var1) {
      return var0 * 360 + var1;
   }

   private ModelRotation(int var3, int var4) {
      this.field_177545_r = func_177521_b(var3, var4);
      Quaternion var5 = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-var4), true);
      var5.func_195890_a(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-var3), true));
      this.field_177544_s = var5;
      this.field_177543_t = MathHelper.func_76130_a(var3 / 90);
      this.field_177542_u = MathHelper.func_76130_a(var4 / 90);
   }

   public Quaternion func_195820_a() {
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
}
