package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityPiston extends TileEntity implements ITickable {
   private IBlockState field_174932_a;
   private EnumFacing field_174931_f;
   private boolean field_145875_k;
   private boolean field_145872_l;
   private float field_145873_m;
   private float field_145870_n;
   private List<Entity> field_174933_k = Lists.newArrayList();

   public TileEntityPiston() {
      super();
   }

   public TileEntityPiston(IBlockState var1, EnumFacing var2, boolean var3, boolean var4) {
      super();
      this.field_174932_a = var1;
      this.field_174931_f = var2;
      this.field_145875_k = var3;
      this.field_145872_l = var4;
   }

   public IBlockState func_174927_b() {
      return this.field_174932_a;
   }

   public int func_145832_p() {
      return 0;
   }

   public boolean func_145868_b() {
      return this.field_145875_k;
   }

   public EnumFacing func_174930_e() {
      return this.field_174931_f;
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

   public float func_174929_b(float var1) {
      return this.field_145875_k ? (this.func_145860_a(var1) - 1.0F) * (float)this.field_174931_f.func_82601_c() : (1.0F - this.func_145860_a(var1)) * (float)this.field_174931_f.func_82601_c();
   }

   public float func_174928_c(float var1) {
      return this.field_145875_k ? (this.func_145860_a(var1) - 1.0F) * (float)this.field_174931_f.func_96559_d() : (1.0F - this.func_145860_a(var1)) * (float)this.field_174931_f.func_96559_d();
   }

   public float func_174926_d(float var1) {
      return this.field_145875_k ? (this.func_145860_a(var1) - 1.0F) * (float)this.field_174931_f.func_82599_e() : (1.0F - this.func_145860_a(var1)) * (float)this.field_174931_f.func_82599_e();
   }

   private void func_145863_a(float var1, float var2) {
      if (this.field_145875_k) {
         var1 = 1.0F - var1;
      } else {
         --var1;
      }

      AxisAlignedBB var3 = Blocks.field_180384_M.func_176424_a(this.field_145850_b, this.field_174879_c, this.field_174932_a, var1, this.field_174931_f);
      if (var3 != null) {
         List var4 = this.field_145850_b.func_72839_b((Entity)null, var3);
         if (!var4.isEmpty()) {
            this.field_174933_k.addAll(var4);
            Iterator var5 = this.field_174933_k.iterator();

            while(true) {
               while(var5.hasNext()) {
                  Entity var6 = (Entity)var5.next();
                  if (this.field_174932_a.func_177230_c() == Blocks.field_180399_cE && this.field_145875_k) {
                     switch(this.field_174931_f.func_176740_k()) {
                     case X:
                        var6.field_70159_w = (double)this.field_174931_f.func_82601_c();
                        break;
                     case Y:
                        var6.field_70181_x = (double)this.field_174931_f.func_96559_d();
                        break;
                     case Z:
                        var6.field_70179_y = (double)this.field_174931_f.func_82599_e();
                     }
                  } else {
                     var6.func_70091_d((double)(var2 * (float)this.field_174931_f.func_82601_c()), (double)(var2 * (float)this.field_174931_f.func_96559_d()), (double)(var2 * (float)this.field_174931_f.func_82599_e()));
                  }
               }

               this.field_174933_k.clear();
               break;
            }
         }
      }

   }

   public void func_145866_f() {
      if (this.field_145870_n < 1.0F && this.field_145850_b != null) {
         this.field_145870_n = this.field_145873_m = 1.0F;
         this.field_145850_b.func_175713_t(this.field_174879_c);
         this.func_145843_s();
         if (this.field_145850_b.func_180495_p(this.field_174879_c).func_177230_c() == Blocks.field_180384_M) {
            this.field_145850_b.func_180501_a(this.field_174879_c, this.field_174932_a, 3);
            this.field_145850_b.func_180496_d(this.field_174879_c, this.field_174932_a.func_177230_c());
         }
      }

   }

   public void func_73660_a() {
      this.field_145870_n = this.field_145873_m;
      if (this.field_145870_n >= 1.0F) {
         this.func_145863_a(1.0F, 0.25F);
         this.field_145850_b.func_175713_t(this.field_174879_c);
         this.func_145843_s();
         if (this.field_145850_b.func_180495_p(this.field_174879_c).func_177230_c() == Blocks.field_180384_M) {
            this.field_145850_b.func_180501_a(this.field_174879_c, this.field_174932_a, 3);
            this.field_145850_b.func_180496_d(this.field_174879_c, this.field_174932_a.func_177230_c());
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

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_174932_a = Block.func_149729_e(var1.func_74762_e("blockId")).func_176203_a(var1.func_74762_e("blockData"));
      this.field_174931_f = EnumFacing.func_82600_a(var1.func_74762_e("facing"));
      this.field_145870_n = this.field_145873_m = var1.func_74760_g("progress");
      this.field_145875_k = var1.func_74767_n("extending");
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      var1.func_74768_a("blockId", Block.func_149682_b(this.field_174932_a.func_177230_c()));
      var1.func_74768_a("blockData", this.field_174932_a.func_177230_c().func_176201_c(this.field_174932_a));
      var1.func_74768_a("facing", this.field_174931_f.func_176745_a());
      var1.func_74776_a("progress", this.field_145870_n);
      var1.func_74757_a("extending", this.field_145875_k);
   }
}
