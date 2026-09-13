package net.minecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;

public class TileEntityPiston extends TileEntity {
   private Block field_145869_a;
   private int field_145876_i;
   private int field_145874_j;
   private boolean field_145875_k;
   private boolean field_145872_l;
   private float field_145873_m;
   private float field_145870_n;
   private List field_145871_o = new ArrayList();

   public TileEntityPiston() {
      super();
   }

   public TileEntityPiston(Block var1, int var2, int var3, boolean var4, boolean var5) {
      super();
      this.field_145869_a = var1;
      this.field_145876_i = var2;
      this.field_145874_j = var3;
      this.field_145875_k = var4;
      this.field_145872_l = var5;
   }

   public Block func_145861_a() {
      return this.field_145869_a;
   }

   @Override
   public int func_145832_p() {
      return this.field_145876_i;
   }

   public boolean func_145868_b() {
      return this.field_145875_k;
   }

   public int func_145864_c() {
      return this.field_145874_j;
   }

   public boolean func_145867_d() {
      return this.field_145872_l;
   }

   public float func_145860_a(float var1) {
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return this.field_145870_n + (this.field_145873_m - this.field_145870_n) * var1;
   }

   public float func_145865_b(float var1) {
      return this.field_145875_k
         ? (this.func_145860_a(var1) - 1.0F) * (float)Facing.field_71586_b[this.field_145874_j]
         : (1.0F - this.func_145860_a(var1)) * (float)Facing.field_71586_b[this.field_145874_j];
   }

   public float func_145862_c(float var1) {
      return this.field_145875_k
         ? (this.func_145860_a(var1) - 1.0F) * (float)Facing.field_71587_c[this.field_145874_j]
         : (1.0F - this.func_145860_a(var1)) * (float)Facing.field_71587_c[this.field_145874_j];
   }

   public float func_145859_d(float var1) {
      return this.field_145875_k
         ? (this.func_145860_a(var1) - 1.0F) * (float)Facing.field_71585_d[this.field_145874_j]
         : (1.0F - this.func_145860_a(var1)) * (float)Facing.field_71585_d[this.field_145874_j];
   }

   private void func_145863_a(float var1, float var2) {
      if (this.field_145875_k) {
         var1 = 1.0F - var1;
      } else {
         --var1;
      }

      AxisAlignedBB var3 = Blocks.field_150326_M
         .func_149964_a(this.field_145850_b, this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a, var1, this.field_145874_j);
      if (var3 != null) {
         List var4 = this.field_145850_b.func_72839_b(null, var3);
         if (!var4.isEmpty()) {
            this.field_145871_o.addAll(var4);

            for(Entity var6 : this.field_145871_o) {
               var6.func_70091_d(
                  (double)(var2 * (float)Facing.field_71586_b[this.field_145874_j]),
                  (double)(var2 * (float)Facing.field_71587_c[this.field_145874_j]),
                  (double)(var2 * (float)Facing.field_71585_d[this.field_145874_j])
               );
            }

            this.field_145871_o.clear();
         }
      }
   }

   public void func_145866_f() {
      if (this.field_145870_n < 1.0F && this.field_145850_b != null) {
         this.field_145870_n = this.field_145873_m = 1.0F;
         this.field_145850_b.func_147475_p(this.field_145851_c, this.field_145848_d, this.field_145849_e);
         this.func_145843_s();
         if (this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e) == Blocks.field_150326_M) {
            this.field_145850_b.func_147465_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a, this.field_145876_i, 3);
            this.field_145850_b.func_147460_e(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a);
         }
      }
   }

   @Override
   public void func_145845_h() {
      this.field_145870_n = this.field_145873_m;
      if (this.field_145870_n >= 1.0F) {
         this.func_145863_a(1.0F, 0.25F);
         this.field_145850_b.func_147475_p(this.field_145851_c, this.field_145848_d, this.field_145849_e);
         this.func_145843_s();
         if (this.field_145850_b.func_147439_a(this.field_145851_c, this.field_145848_d, this.field_145849_e) == Blocks.field_150326_M) {
            this.field_145850_b.func_147465_d(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a, this.field_145876_i, 3);
            this.field_145850_b.func_147460_e(this.field_145851_c, this.field_145848_d, this.field_145849_e, this.field_145869_a);
         }
      } else {
         this.field_145873_m += 0.5F;
         if (this.field_145873_m >= 1.0F) {
            this.field_145873_m = 1.0F;
         }

         if (this.field_145875_k) {
            this.func_145863_a(this.field_145873_m, this.field_145873_m - this.field_145870_n + 0.0625F);
         }
      }
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145869_a = Block.func_149729_e(var1.func_74762_e("blockId"));
      this.field_145876_i = var1.func_74762_e("blockData");
      this.field_145874_j = var1.func_74762_e("facing");
      this.field_145870_n = this.field_145873_m = var1.func_74760_g("progress");
      this.field_145875_k = var1.func_74767_n("extending");
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74768_a("blockId", Block.func_149682_b(this.field_145869_a));
      var1.func_74768_a("blockData", this.field_145876_i);
      var1.func_74768_a("facing", this.field_145874_j);
      var1.func_74776_a("progress", this.field_145870_n);
      var1.func_74757_a("extending", this.field_145875_k);
   }
}
