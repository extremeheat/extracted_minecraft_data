package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockItemUseContext extends ItemUseContext {
   private final BlockPos field_196014_j;
   protected boolean field_196013_a;

   public BlockItemUseContext(ItemUseContext var1) {
      this(var1.func_195991_k(), var1.func_195999_j(), var1.func_195996_i(), var1.func_195995_a(), var1.func_196000_l(), var1.func_195997_m(), var1.func_195993_n(), var1.func_195994_o());
   }

   protected BlockItemUseContext(World var1, @Nullable EntityPlayer var2, ItemStack var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      super(var1, var2, var3, var4, var5, var6, var7, var8);
      this.field_196013_a = true;
      this.field_196014_j = this.field_196008_i.func_177972_a(this.field_196005_f);
      this.field_196013_a = this.func_195991_k().func_180495_p(this.field_196008_i).func_196953_a(this);
   }

   public BlockPos func_195995_a() {
      return this.field_196013_a ? this.field_196008_i : this.field_196014_j;
   }

   public boolean func_196011_b() {
      return this.field_196013_a || this.func_195991_k().func_180495_p(this.func_195995_a()).func_196953_a(this);
   }

   public boolean func_196012_c() {
      return this.field_196013_a;
   }

   public EnumFacing func_196010_d() {
      return EnumFacing.func_196054_a(this.field_196001_b)[0];
   }

   public EnumFacing[] func_196009_e() {
      EnumFacing[] var1 = EnumFacing.func_196054_a(this.field_196001_b);
      if (this.field_196013_a) {
         return var1;
      } else {
         int var2;
         for(var2 = 0; var2 < var1.length && var1[var2] != this.field_196005_f.func_176734_d(); ++var2) {
         }

         if (var2 > 0) {
            System.arraycopy(var1, 0, var1, 1, var2);
            var1[0] = this.field_196005_f.func_176734_d();
         }

         return var1;
      }
   }
}
