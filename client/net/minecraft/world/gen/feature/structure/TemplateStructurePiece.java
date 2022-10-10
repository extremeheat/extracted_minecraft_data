package net.minecraft.world.gen.feature.structure;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class TemplateStructurePiece extends StructurePiece {
   private static final PlacementSettings field_186179_d = new PlacementSettings();
   protected Template field_186176_a;
   protected PlacementSettings field_186177_b;
   protected BlockPos field_186178_c;

   public TemplateStructurePiece() {
      super();
      this.field_186177_b = field_186179_d.func_186222_a(true).func_186225_a(Blocks.field_150350_a);
   }

   public TemplateStructurePiece(int var1) {
      super(var1);
      this.field_186177_b = field_186179_d.func_186222_a(true).func_186225_a(Blocks.field_150350_a);
   }

   protected void func_186173_a(Template var1, BlockPos var2, PlacementSettings var3) {
      this.field_186176_a = var1;
      this.func_186164_a(EnumFacing.NORTH);
      this.field_186178_c = var2;
      this.field_186177_b = var3;
      this.func_186174_h();
   }

   protected void func_143012_a(NBTTagCompound var1) {
      var1.func_74768_a("TPX", this.field_186178_c.func_177958_n());
      var1.func_74768_a("TPY", this.field_186178_c.func_177956_o());
      var1.func_74768_a("TPZ", this.field_186178_c.func_177952_p());
   }

   protected void func_143011_b(NBTTagCompound var1, TemplateManager var2) {
      this.field_186178_c = new BlockPos(var1.func_74762_e("TPX"), var1.func_74762_e("TPY"), var1.func_74762_e("TPZ"));
   }

   public boolean func_74875_a(IWorld var1, Random var2, MutableBoundingBox var3, ChunkPos var4) {
      this.field_186177_b.func_186223_a(var3);
      if (this.field_186176_a.func_189962_a(var1, this.field_186178_c, this.field_186177_b, 2)) {
         Map var5 = this.field_186176_a.func_186258_a(this.field_186178_c, this.field_186177_b);
         Iterator var6 = var5.entrySet().iterator();

         while(var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            String var8 = (String)var7.getValue();
            this.func_186175_a(var8, (BlockPos)var7.getKey(), var1, var2, var3);
         }
      }

      return true;
   }

   protected abstract void func_186175_a(String var1, BlockPos var2, IWorld var3, Random var4, MutableBoundingBox var5);

   private void func_186174_h() {
      Rotation var1 = this.field_186177_b.func_186215_c();
      BlockPos var2 = this.field_186177_b.func_207664_d();
      BlockPos var3 = this.field_186176_a.func_186257_a(var1);
      Mirror var4 = this.field_186177_b.func_186212_b();
      int var5 = var2.func_177958_n();
      int var6 = var2.func_177952_p();
      int var7 = var3.func_177958_n() - 1;
      int var8 = var3.func_177956_o() - 1;
      int var9 = var3.func_177952_p() - 1;
      switch(var1) {
      case NONE:
         this.field_74887_e = new MutableBoundingBox(0, 0, 0, var7, var8, var9);
         break;
      case CLOCKWISE_180:
         this.field_74887_e = new MutableBoundingBox(var5 + var5 - var7, 0, var6 + var6 - var9, var5 + var5, var8, var6 + var6);
         break;
      case COUNTERCLOCKWISE_90:
         this.field_74887_e = new MutableBoundingBox(var5 - var6, 0, var5 + var6 - var9, var5 - var6 + var7, var8, var5 + var6);
         break;
      case CLOCKWISE_90:
         this.field_74887_e = new MutableBoundingBox(var5 + var6 - var7, 0, var6 - var5, var5 + var6, var8, var6 - var5 + var9);
      }

      BlockPos var10;
      switch(var4) {
      case NONE:
      default:
         break;
      case FRONT_BACK:
         var10 = BlockPos.field_177992_a;
         if (var1 != Rotation.CLOCKWISE_90 && var1 != Rotation.COUNTERCLOCKWISE_90) {
            if (var1 == Rotation.CLOCKWISE_180) {
               var10 = var10.func_177967_a(EnumFacing.EAST, var7);
            } else {
               var10 = var10.func_177967_a(EnumFacing.WEST, var7);
            }
         } else {
            var10 = var10.func_177967_a(var1.func_185831_a(EnumFacing.WEST), var9);
         }

         this.field_74887_e.func_78886_a(var10.func_177958_n(), 0, var10.func_177952_p());
         break;
      case LEFT_RIGHT:
         var10 = BlockPos.field_177992_a;
         if (var1 != Rotation.CLOCKWISE_90 && var1 != Rotation.COUNTERCLOCKWISE_90) {
            if (var1 == Rotation.CLOCKWISE_180) {
               var10 = var10.func_177967_a(EnumFacing.SOUTH, var9);
            } else {
               var10 = var10.func_177967_a(EnumFacing.NORTH, var9);
            }
         } else {
            var10 = var10.func_177967_a(var1.func_185831_a(EnumFacing.NORTH), var7);
         }

         this.field_74887_e.func_78886_a(var10.func_177958_n(), 0, var10.func_177952_p());
      }

      this.field_74887_e.func_78886_a(this.field_186178_c.func_177958_n(), this.field_186178_c.func_177956_o(), this.field_186178_c.func_177952_p());
   }

   public void func_181138_a(int var1, int var2, int var3) {
      super.func_181138_a(var1, var2, var3);
      this.field_186178_c = this.field_186178_c.func_177982_a(var1, var2, var3);
   }
}
