package net.minecraft.util.math.shapes;

import net.minecraft.util.AxisRotation;
import net.minecraft.util.EnumFacing;

public abstract class VoxelShapePart {
   private static final EnumFacing.Axis[] field_199626_e = EnumFacing.Axis.values();
   protected final int field_197838_b;
   protected final int field_197839_c;
   protected final int field_197840_d;

   protected VoxelShapePart(int var1, int var2, int var3) {
      super();
      this.field_197838_b = var1;
      this.field_197839_c = var2;
      this.field_197840_d = var3;
   }

   public boolean func_197824_a(AxisRotation var1, int var2, int var3, int var4) {
      return this.func_197818_c(var1.func_197517_a(var2, var3, var4, EnumFacing.Axis.X), var1.func_197517_a(var2, var3, var4, EnumFacing.Axis.Y), var1.func_197517_a(var2, var3, var4, EnumFacing.Axis.Z));
   }

   public boolean func_197818_c(int var1, int var2, int var3) {
      if (var1 >= 0 && var2 >= 0 && var3 >= 0) {
         return var1 < this.field_197838_b && var2 < this.field_197839_c && var3 < this.field_197840_d ? this.func_197835_b(var1, var2, var3) : false;
      } else {
         return false;
      }
   }

   public boolean func_197829_b(AxisRotation var1, int var2, int var3, int var4) {
      return this.func_197835_b(var1.func_197517_a(var2, var3, var4, EnumFacing.Axis.X), var1.func_197517_a(var2, var3, var4, EnumFacing.Axis.Y), var1.func_197517_a(var2, var3, var4, EnumFacing.Axis.Z));
   }

   public abstract boolean func_197835_b(int var1, int var2, int var3);

   public abstract void func_199625_a(int var1, int var2, int var3, boolean var4, boolean var5);

   public boolean func_197830_a() {
      EnumFacing.Axis[] var1 = field_199626_e;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         EnumFacing.Axis var4 = var1[var3];
         if (this.func_199623_a(var4) >= this.func_199624_b(var4)) {
            return true;
         }
      }

      return false;
   }

   public abstract int func_199623_a(EnumFacing.Axis var1);

   public abstract int func_199624_b(EnumFacing.Axis var1);

   public int func_197826_a(EnumFacing.Axis var1, int var2, int var3) {
      int var4 = this.func_197819_a(var1);
      if (var2 >= 0 && var3 >= 0) {
         EnumFacing.Axis var5 = AxisRotation.FORWARD.func_197513_a(var1);
         EnumFacing.Axis var6 = AxisRotation.BACKWARD.func_197513_a(var1);
         if (var2 < this.func_197819_a(var5) && var3 < this.func_197819_a(var6)) {
            AxisRotation var7 = AxisRotation.func_197516_a(EnumFacing.Axis.X, var1);

            for(int var8 = 0; var8 < var4; ++var8) {
               if (this.func_197829_b(var7, var8, var2, var3)) {
                  return var8;
               }
            }

            return var4;
         } else {
            return var4;
         }
      } else {
         return var4;
      }
   }

   public int func_197836_b(EnumFacing.Axis var1, int var2, int var3) {
      if (var2 >= 0 && var3 >= 0) {
         EnumFacing.Axis var4 = AxisRotation.FORWARD.func_197513_a(var1);
         EnumFacing.Axis var5 = AxisRotation.BACKWARD.func_197513_a(var1);
         if (var2 < this.func_197819_a(var4) && var3 < this.func_197819_a(var5)) {
            int var6 = this.func_197819_a(var1);
            AxisRotation var7 = AxisRotation.func_197516_a(EnumFacing.Axis.X, var1);

            for(int var8 = var6 - 1; var8 >= 0; --var8) {
               if (this.func_197829_b(var7, var8, var2, var3)) {
                  return var8 + 1;
               }
            }

            return 0;
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }

   public int func_197819_a(EnumFacing.Axis var1) {
      return var1.func_196052_a(this.field_197838_b, this.field_197839_c, this.field_197840_d);
   }

   public int func_197823_b() {
      return this.func_197819_a(EnumFacing.Axis.X);
   }

   public int func_197820_c() {
      return this.func_197819_a(EnumFacing.Axis.Y);
   }

   public int func_197821_d() {
      return this.func_197819_a(EnumFacing.Axis.Z);
   }

   public void func_197828_a(VoxelShapePart.LineConsumer var1, boolean var2) {
      this.func_197832_a(var1, AxisRotation.NONE, var2);
      this.func_197832_a(var1, AxisRotation.FORWARD, var2);
      this.func_197832_a(var1, AxisRotation.BACKWARD, var2);
   }

   private void func_197832_a(VoxelShapePart.LineConsumer var1, AxisRotation var2, boolean var3) {
      AxisRotation var5 = var2.func_197514_a();
      int var6 = this.func_197819_a(var5.func_197513_a(EnumFacing.Axis.X));
      int var7 = this.func_197819_a(var5.func_197513_a(EnumFacing.Axis.Y));
      int var8 = this.func_197819_a(var5.func_197513_a(EnumFacing.Axis.Z));

      for(int var9 = 0; var9 <= var6; ++var9) {
         for(int var10 = 0; var10 <= var7; ++var10) {
            int var4 = -1;

            for(int var11 = 0; var11 <= var8; ++var11) {
               int var12 = 0;
               int var13 = 0;

               for(int var14 = 0; var14 <= 1; ++var14) {
                  for(int var15 = 0; var15 <= 1; ++var15) {
                     if (this.func_197824_a(var5, var9 + var14 - 1, var10 + var15 - 1, var11)) {
                        ++var12;
                        var13 ^= var14 ^ var15;
                     }
                  }
               }

               if (var12 == 1 || var12 == 3 || var12 == 2 && (var13 & 1) == 0) {
                  if (var3) {
                     if (var4 == -1) {
                        var4 = var11;
                     }
                  } else {
                     var1.consume(var5.func_197517_a(var9, var10, var11, EnumFacing.Axis.X), var5.func_197517_a(var9, var10, var11, EnumFacing.Axis.Y), var5.func_197517_a(var9, var10, var11, EnumFacing.Axis.Z), var5.func_197517_a(var9, var10, var11 + 1, EnumFacing.Axis.X), var5.func_197517_a(var9, var10, var11 + 1, EnumFacing.Axis.Y), var5.func_197517_a(var9, var10, var11 + 1, EnumFacing.Axis.Z));
                  }
               } else if (var4 != -1) {
                  var1.consume(var5.func_197517_a(var9, var10, var4, EnumFacing.Axis.X), var5.func_197517_a(var9, var10, var4, EnumFacing.Axis.Y), var5.func_197517_a(var9, var10, var4, EnumFacing.Axis.Z), var5.func_197517_a(var9, var10, var11, EnumFacing.Axis.X), var5.func_197517_a(var9, var10, var11, EnumFacing.Axis.Y), var5.func_197517_a(var9, var10, var11, EnumFacing.Axis.Z));
                  var4 = -1;
               }
            }
         }
      }

   }

   protected boolean func_197833_a(int var1, int var2, int var3, int var4) {
      for(int var5 = var1; var5 < var2; ++var5) {
         if (!this.func_197818_c(var3, var4, var5)) {
            return false;
         }
      }

      return true;
   }

   protected void func_197834_a(int var1, int var2, int var3, int var4, boolean var5) {
      for(int var6 = var1; var6 < var2; ++var6) {
         this.func_199625_a(var3, var4, var6, false, var5);
      }

   }

   protected boolean func_197827_a(int var1, int var2, int var3, int var4, int var5) {
      for(int var6 = var1; var6 < var2; ++var6) {
         if (!this.func_197833_a(var3, var4, var6, var5)) {
            return false;
         }
      }

      return true;
   }

   public void func_197831_b(VoxelShapePart.LineConsumer var1, boolean var2) {
      VoxelShapePartBitSet var3 = new VoxelShapePartBitSet(this);

      for(int var4 = 0; var4 <= this.field_197838_b; ++var4) {
         for(int var5 = 0; var5 <= this.field_197839_c; ++var5) {
            int var6 = -1;

            for(int var7 = 0; var7 <= this.field_197840_d; ++var7) {
               if (var3.func_197818_c(var4, var5, var7)) {
                  if (var2) {
                     if (var6 == -1) {
                        var6 = var7;
                     }
                  } else {
                     var1.consume(var4, var5, var7, var4 + 1, var5 + 1, var7 + 1);
                  }
               } else if (var6 != -1) {
                  int var8 = var4;
                  int var9 = var4;
                  int var10 = var5;
                  int var11 = var5;
                  var3.func_197834_a(var6, var7, var4, var5, false);

                  while(var3.func_197833_a(var6, var7, var8 - 1, var10)) {
                     var3.func_197834_a(var6, var7, var8 - 1, var10, false);
                     --var8;
                  }

                  while(var3.func_197833_a(var6, var7, var9 + 1, var10)) {
                     var3.func_197834_a(var6, var7, var9 + 1, var10, false);
                     ++var9;
                  }

                  int var12;
                  while(var3.func_197827_a(var8, var9 + 1, var6, var7, var10 - 1)) {
                     for(var12 = var8; var12 <= var9; ++var12) {
                        var3.func_197834_a(var6, var7, var12, var10 - 1, false);
                     }

                     --var10;
                  }

                  while(var3.func_197827_a(var8, var9 + 1, var6, var7, var11 + 1)) {
                     for(var12 = var8; var12 <= var9; ++var12) {
                        var3.func_197834_a(var6, var7, var12, var11 + 1, false);
                     }

                     ++var11;
                  }

                  var1.consume(var8, var10, var6, var9 + 1, var11 + 1, var7);
                  var6 = -1;
               }
            }
         }
      }

   }

   public void func_211540_a(VoxelShapePart.FaceConsumer var1) {
      this.func_211541_a(var1, AxisRotation.NONE);
      this.func_211541_a(var1, AxisRotation.FORWARD);
      this.func_211541_a(var1, AxisRotation.BACKWARD);
   }

   private void func_211541_a(VoxelShapePart.FaceConsumer var1, AxisRotation var2) {
      AxisRotation var3 = var2.func_197514_a();
      EnumFacing.Axis var4 = var3.func_197513_a(EnumFacing.Axis.Z);
      int var5 = this.func_197819_a(var3.func_197513_a(EnumFacing.Axis.X));
      int var6 = this.func_197819_a(var3.func_197513_a(EnumFacing.Axis.Y));
      int var7 = this.func_197819_a(var4);
      EnumFacing var8 = EnumFacing.func_211699_a(var4, EnumFacing.AxisDirection.NEGATIVE);
      EnumFacing var9 = EnumFacing.func_211699_a(var4, EnumFacing.AxisDirection.POSITIVE);

      for(int var10 = 0; var10 < var5; ++var10) {
         for(int var11 = 0; var11 < var6; ++var11) {
            boolean var12 = false;

            for(int var13 = 0; var13 <= var7; ++var13) {
               boolean var14 = var13 != var7 && this.func_197829_b(var3, var10, var11, var13);
               if (!var12 && var14) {
                  var1.consume(var8, var3.func_197517_a(var10, var11, var13, EnumFacing.Axis.X), var3.func_197517_a(var10, var11, var13, EnumFacing.Axis.Y), var3.func_197517_a(var10, var11, var13, EnumFacing.Axis.Z));
               }

               if (var12 && !var14) {
                  var1.consume(var9, var3.func_197517_a(var10, var11, var13 - 1, EnumFacing.Axis.X), var3.func_197517_a(var10, var11, var13 - 1, EnumFacing.Axis.Y), var3.func_197517_a(var10, var11, var13 - 1, EnumFacing.Axis.Z));
               }

               var12 = var14;
            }
         }
      }

   }

   public interface FaceConsumer {
      void consume(EnumFacing var1, int var2, int var3, int var4);
   }

   public interface LineConsumer {
      void consume(int var1, int var2, int var3, int var4, int var5, int var6);
   }
}
