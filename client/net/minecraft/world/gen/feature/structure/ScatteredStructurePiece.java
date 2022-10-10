package net.minecraft.world.gen.feature.structure;

import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class ScatteredStructurePiece extends StructurePiece {
   protected int field_202581_a;
   protected int field_202582_b;
   protected int field_202583_c;
   protected int field_202584_d = -1;

   public ScatteredStructurePiece() {
      super();
   }

   protected ScatteredStructurePiece(Random var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      super(0);
      this.field_202581_a = var5;
      this.field_202582_b = var6;
      this.field_202583_c = var7;
      this.func_186164_a(EnumFacing.Plane.HORIZONTAL.func_179518_a(var1));
      if (this.func_186165_e().func_176740_k() == EnumFacing.Axis.Z) {
         this.field_74887_e = new MutableBoundingBox(var2, var3, var4, var2 + var5 - 1, var3 + var6 - 1, var4 + var7 - 1);
      } else {
         this.field_74887_e = new MutableBoundingBox(var2, var3, var4, var2 + var7 - 1, var3 + var6 - 1, var4 + var5 - 1);
      }

   }

   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74768_a("Width", this.field_202581_a);
      var1.func_74768_a("Height", this.field_202582_b);
      var1.func_74768_a("Depth", this.field_202583_c);
      var1.func_74768_a("HPos", this.field_202584_d);
   }

   protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      this.field_202581_a = var1.func_74762_e("Width");
      this.field_202582_b = var1.func_74762_e("Height");
      this.field_202583_c = var1.func_74762_e("Depth");
      this.field_202584_d = var1.func_74762_e("HPos");
   }

   protected boolean func_202580_a(IWorld var1, MutableBoundingBox var2, int var3) {
      if (this.field_202584_d >= 0) {
         return true;
      } else {
         int var4 = 0;
         int var5 = 0;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for(int var7 = this.field_74887_e.field_78896_c; var7 <= this.field_74887_e.field_78892_f; ++var7) {
            for(int var8 = this.field_74887_e.field_78897_a; var8 <= this.field_74887_e.field_78893_d; ++var8) {
               var6.func_181079_c(var8, 64, var7);
               if (var2.func_175898_b(var6)) {
                  var4 += var1.func_205770_a(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, var6).func_177956_o();
                  ++var5;
               }
            }
         }

         if (var5 == 0) {
            return false;
         } else {
            this.field_202584_d = var4 / var5;
            this.field_74887_e.func_78886_a(0, this.field_202584_d - this.field_74887_e.field_78895_b + var3, 0);
            return true;
         }
      }
   }
}
