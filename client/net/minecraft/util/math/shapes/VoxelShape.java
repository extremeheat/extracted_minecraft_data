package net.minecraft.util.math.shapes;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public abstract class VoxelShape {
   protected final VoxelShapePart field_197768_g;

   VoxelShape(VoxelShapePart var1) {
      super();
      this.field_197768_g = var1;
   }

   public double func_197762_b(EnumFacing.Axis var1) {
      int var2 = this.field_197768_g.func_199623_a(var1);
      return var2 >= this.field_197768_g.func_197819_a(var1) ? 1.0D / 0.0 : this.func_197759_b(var1, var2);
   }

   public double func_197758_c(EnumFacing.Axis var1) {
      int var2 = this.field_197768_g.func_199624_b(var1);
      return var2 <= 0 ? -1.0D / 0.0 : this.func_197759_b(var1, var2);
   }

   public AxisAlignedBB func_197752_a() {
      if (this.func_197766_b()) {
         throw new UnsupportedOperationException("No bounds for empty shape.");
      } else {
         return new AxisAlignedBB(this.func_197762_b(EnumFacing.Axis.X), this.func_197762_b(EnumFacing.Axis.Y), this.func_197762_b(EnumFacing.Axis.Z), this.func_197758_c(EnumFacing.Axis.X), this.func_197758_c(EnumFacing.Axis.Y), this.func_197758_c(EnumFacing.Axis.Z));
      }
   }

   protected double func_197759_b(EnumFacing.Axis var1, int var2) {
      return this.func_197757_a(var1).getDouble(var2);
   }

   protected abstract DoubleList func_197757_a(EnumFacing.Axis var1);

   public boolean func_197766_b() {
      return this.field_197768_g.func_197830_a();
   }

   public VoxelShape func_197751_a(double var1, double var3, double var5) {
      return (VoxelShape)(this.func_197766_b() ? VoxelShapes.func_197880_a() : new VoxelShapeArray(this.field_197768_g, new OffsetDoubleList(this.func_197757_a(EnumFacing.Axis.X), var1), new OffsetDoubleList(this.func_197757_a(EnumFacing.Axis.Y), var3), new OffsetDoubleList(this.func_197757_a(EnumFacing.Axis.Z), var5)));
   }

   public VoxelShape func_197753_c() {
      VoxelShape[] var1 = new VoxelShape[]{VoxelShapes.func_197880_a()};
      this.func_197755_b((var1x, var3, var5, var7, var9, var11) -> {
         var1[0] = VoxelShapes.func_197882_b(var1[0], VoxelShapes.func_197873_a(var1x, var3, var5, var7, var9, var11), IBooleanFunction.OR);
      });
      return var1[0];
   }

   public void func_197754_a(VoxelShapes.LineConsumer var1) {
      this.field_197768_g.func_197828_a((var2, var3, var4, var5, var6, var7) -> {
         var1.consume(this.func_197759_b(EnumFacing.Axis.X, var2), this.func_197759_b(EnumFacing.Axis.Y, var3), this.func_197759_b(EnumFacing.Axis.Z, var4), this.func_197759_b(EnumFacing.Axis.X, var5), this.func_197759_b(EnumFacing.Axis.Y, var6), this.func_197759_b(EnumFacing.Axis.Z, var7));
      }, true);
   }

   public void func_197755_b(VoxelShapes.LineConsumer var1) {
      this.field_197768_g.func_197831_b((var2, var3, var4, var5, var6, var7) -> {
         var1.consume(this.func_197759_b(EnumFacing.Axis.X, var2), this.func_197759_b(EnumFacing.Axis.Y, var3), this.func_197759_b(EnumFacing.Axis.Z, var4), this.func_197759_b(EnumFacing.Axis.X, var5), this.func_197759_b(EnumFacing.Axis.Y, var6), this.func_197759_b(EnumFacing.Axis.Z, var7));
      }, true);
   }

   public List<AxisAlignedBB> func_197756_d() {
      ArrayList var1 = Lists.newArrayList();
      this.func_197755_b((var1x, var3, var5, var7, var9, var11) -> {
         var1.add(new AxisAlignedBB(var1x, var3, var5, var7, var9, var11));
      });
      return var1;
   }

   public double func_197764_a(EnumFacing.Axis var1, double var2, double var4) {
      EnumFacing.Axis var6 = AxisRotation.FORWARD.func_197513_a(var1);
      EnumFacing.Axis var7 = AxisRotation.BACKWARD.func_197513_a(var1);
      int var8 = this.func_197749_a(var6, var2);
      int var9 = this.func_197749_a(var7, var4);
      int var10 = this.field_197768_g.func_197826_a(var1, var8, var9);
      return var10 >= this.field_197768_g.func_197819_a(var1) ? 1.0D / 0.0 : this.func_197759_b(var1, var10);
   }

   public double func_197760_b(EnumFacing.Axis var1, double var2, double var4) {
      EnumFacing.Axis var6 = AxisRotation.FORWARD.func_197513_a(var1);
      EnumFacing.Axis var7 = AxisRotation.BACKWARD.func_197513_a(var1);
      int var8 = this.func_197749_a(var6, var2);
      int var9 = this.func_197749_a(var7, var4);
      int var10 = this.field_197768_g.func_197836_b(var1, var8, var9);
      return var10 <= 0 ? -1.0D / 0.0 : this.func_197759_b(var1, var10);
   }

   protected int func_197749_a(EnumFacing.Axis var1, double var2) {
      return MathHelper.func_199093_a(0, this.field_197768_g.func_197819_a(var1) + 1, (var4) -> {
         if (var4 < 0) {
            return false;
         } else if (var4 > this.field_197768_g.func_197819_a(var1)) {
            return true;
         } else {
            return var2 < this.func_197759_b(var1, var4);
         }
      }) - 1;
   }

   protected boolean func_211542_b(double var1, double var3, double var5) {
      return this.field_197768_g.func_197818_c(this.func_197749_a(EnumFacing.Axis.X, var1), this.func_197749_a(EnumFacing.Axis.Y, var3), this.func_197749_a(EnumFacing.Axis.Z, var5));
   }

   @Nullable
   public RayTraceResult func_212433_a(Vec3d var1, Vec3d var2, BlockPos var3) {
      if (this.func_197766_b()) {
         return null;
      } else {
         Vec3d var4 = var2.func_178788_d(var1);
         if (var4.func_189985_c() < 1.0E-7D) {
            return null;
         } else {
            Vec3d var5 = var1.func_178787_e(var4.func_186678_a(0.001D));
            Vec3d var6 = var1.func_178787_e(var4.func_186678_a(0.001D)).func_178786_a((double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p());
            return this.func_211542_b(var6.field_72450_a, var6.field_72448_b, var6.field_72449_c) ? new RayTraceResult(var5, EnumFacing.func_210769_a(var4.field_72450_a, var4.field_72448_b, var4.field_72449_c), var3) : AxisAlignedBB.func_197743_a(this.func_197756_d(), var1, var2, var3);
         }
      }
   }

   public VoxelShape func_212434_a(EnumFacing var1) {
      if (!this.func_197766_b() && this != VoxelShapes.func_197868_b()) {
         EnumFacing.Axis var2 = var1.func_176740_k();
         EnumFacing.AxisDirection var3 = var1.func_176743_c();
         DoubleList var4 = this.func_197757_a(var2);
         if (var4.size() == 2 && DoubleMath.fuzzyEquals(var4.getDouble(0), 0.0D, 1.0E-7D) && DoubleMath.fuzzyEquals(var4.getDouble(1), 1.0D, 1.0E-7D)) {
            return this;
         } else {
            int var5 = this.func_197749_a(var2, var3 == EnumFacing.AxisDirection.POSITIVE ? 0.9999999D : 1.0E-7D);
            return new VoxelShapeSplit(this, var2, var5);
         }
      } else {
         return this;
      }
   }

   public double func_212430_a(EnumFacing.Axis var1, AxisAlignedBB var2, double var3) {
      return this.func_212431_a(AxisRotation.func_197516_a(var1, EnumFacing.Axis.X), var2, var3);
   }

   protected double func_212431_a(AxisRotation var1, AxisAlignedBB var2, double var3) {
      if (this.func_197766_b()) {
         return var3;
      } else if (Math.abs(var3) < 1.0E-7D) {
         return 0.0D;
      } else {
         AxisRotation var5 = var1.func_197514_a();
         EnumFacing.Axis var6 = var5.func_197513_a(EnumFacing.Axis.X);
         EnumFacing.Axis var7 = var5.func_197513_a(EnumFacing.Axis.Y);
         EnumFacing.Axis var8 = var5.func_197513_a(EnumFacing.Axis.Z);
         double var9 = var2.func_197742_b(var6);
         double var11 = var2.func_197745_a(var6);
         int var13 = this.func_197749_a(var6, var11 + 1.0E-7D);
         int var14 = this.func_197749_a(var6, var9 - 1.0E-7D);
         int var15 = Math.max(0, this.func_197749_a(var7, var2.func_197745_a(var7) + 1.0E-7D));
         int var16 = Math.min(this.field_197768_g.func_197819_a(var7), this.func_197749_a(var7, var2.func_197742_b(var7) - 1.0E-7D) + 1);
         int var17 = Math.max(0, this.func_197749_a(var8, var2.func_197745_a(var8) + 1.0E-7D));
         int var18 = Math.min(this.field_197768_g.func_197819_a(var8), this.func_197749_a(var8, var2.func_197742_b(var8) - 1.0E-7D) + 1);
         int var19 = this.field_197768_g.func_197819_a(var6);
         int var20;
         int var21;
         int var22;
         double var23;
         if (var3 > 0.0D) {
            for(var20 = var14 + 1; var20 < var19; ++var20) {
               for(var21 = var15; var21 < var16; ++var21) {
                  for(var22 = var17; var22 < var18; ++var22) {
                     if (this.field_197768_g.func_197824_a(var5, var20, var21, var22)) {
                        var23 = this.func_197759_b(var6, var20) - var9;
                        if (var23 >= -1.0E-7D) {
                           var3 = Math.min(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         } else if (var3 < 0.0D) {
            for(var20 = var13 - 1; var20 >= 0; --var20) {
               for(var21 = var15; var21 < var16; ++var21) {
                  for(var22 = var17; var22 < var18; ++var22) {
                     if (this.field_197768_g.func_197824_a(var5, var20, var21, var22)) {
                        var23 = this.func_197759_b(var6, var20 + 1) - var11;
                        if (var23 <= 1.0E-7D) {
                           var3 = Math.max(var3, var23);
                        }

                        return var3;
                     }
                  }
               }
            }
         }

         return var3;
      }
   }

   public String toString() {
      return this.func_197766_b() ? "EMPTY" : "VoxelShape[" + this.func_197752_a() + "]";
   }
}
