package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {
   private IIcon[] field_150017_a;
   private IIcon[] field_150016_b;

   protected BlockDoor(Material var1) {
      super(var1);
      float var2 = 0.5F;
      float var3 = 1.0F;
      this.func_149676_a(0.5F - var2, 0.0F, 0.5F - var2, 0.5F + var2, var3, 0.5F + var2);
   }

   @Override
   public IIcon func_149691_a(int var1, int var2) {
      return this.field_150016_b[0];
   }

   @Override
   public IIcon func_149673_e(IBlockAccess var1, int var2, int var3, int var4, int var5) {
      if (var5 != 1 && var5 != 0) {
         int var6 = this.func_150012_g(var1, var2, var3, var4);
         int var7 = var6 & 3;
         boolean var8 = (var6 & 4) != 0;
         boolean var9 = false;
         boolean var10 = (var6 & 8) != 0;
         if (var8) {
            if (var7 == 0 && var5 == 2) {
               var9 = !var9;
            } else if (var7 == 1 && var5 == 5) {
               var9 = !var9;
            } else if (var7 == 2 && var5 == 3) {
               var9 = !var9;
            } else if (var7 == 3 && var5 == 4) {
               var9 = !var9;
            }
         } else {
            if (var7 == 0 && var5 == 5) {
               var9 = !var9;
            } else if (var7 == 1 && var5 == 3) {
               var9 = !var9;
            } else if (var7 == 2 && var5 == 4) {
               var9 = !var9;
            } else if (var7 == 3 && var5 == 2) {
               var9 = !var9;
            }

            if ((var6 & 16) != 0) {
               var9 = !var9;
            }
         }

         return var10 ? this.field_150017_a[var9 ? 1 : 0] : this.field_150016_b[var9 ? 1 : 0];
      } else {
         return this.field_150016_b[0];
      }
   }

   @Override
   public void func_149651_a(IIconRegister var1) {
      this.field_150017_a = new IIcon[2];
      this.field_150016_b = new IIcon[2];
      this.field_150017_a[0] = var1.func_94245_a(this.func_149641_N() + "_upper");
      this.field_150016_b[0] = var1.func_94245_a(this.func_149641_N() + "_lower");
      this.field_150017_a[1] = new IconFlipped(this.field_150017_a[0], true, false);
      this.field_150016_b[1] = new IconFlipped(this.field_150016_b[0], true, false);
   }

   @Override
   public boolean func_149662_c() {
      return false;
   }

   @Override
   public boolean func_149655_b(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = this.func_150012_g(var1, var2, var3, var4);
      return (var5 & 4) != 0;
   }

   @Override
   public boolean func_149686_d() {
      return false;
   }

   @Override
   public int func_149645_b() {
      return 7;
   }

   @Override
   public AxisAlignedBB func_149633_g(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149633_g(var1, var2, var3, var4);
   }

   @Override
   public AxisAlignedBB func_149668_a(World var1, int var2, int var3, int var4) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149668_a(var1, var2, var3, var4);
   }

   @Override
   public void func_149719_a(IBlockAccess var1, int var2, int var3, int var4) {
      this.func_150011_b(this.func_150012_g(var1, var2, var3, var4));
   }

   public int func_150013_e(IBlockAccess var1, int var2, int var3, int var4) {
      return this.func_150012_g(var1, var2, var3, var4) & 3;
   }

   public boolean func_150015_f(IBlockAccess var1, int var2, int var3, int var4) {
      return (this.func_150012_g(var1, var2, var3, var4) & 4) != 0;
   }

   private void func_150011_b(int var1) {
      float var2 = 0.1875F;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      int var3 = var1 & 3;
      boolean var4 = (var1 & 4) != 0;
      boolean var5 = (var1 & 16) != 0;
      if (var3 == 0) {
         if (var4) {
            if (!var5) {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            } else {
               this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            }
         } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
         }
      } else if (var3 == 1) {
         if (var4) {
            if (!var5) {
               this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            }
         } else {
            this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
         }
      } else if (var3 == 2) {
         if (var4) {
            if (!var5) {
               this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
            } else {
               this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, var2);
            }
         } else {
            this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
         }
      } else if (var3 == 3) {
         if (var4) {
            if (!var5) {
               this.func_149676_a(0.0F, 0.0F, 0.0F, var2, 1.0F, 1.0F);
            } else {
               this.func_149676_a(1.0F - var2, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         } else {
            this.func_149676_a(0.0F, 0.0F, 1.0F - var2, 1.0F, 1.0F, 1.0F);
         }
      }
   }

   @Override
   public void func_149699_a(World var1, int var2, int var3, int var4, EntityPlayer var5) {
   }

   @Override
   public boolean func_149727_a(World var1, int var2, int var3, int var4, EntityPlayer var5, int var6, float var7, float var8, float var9) {
      if (this.field_149764_J == Material.field_151573_f) {
         return true;
      } else {
         int var10 = this.func_150012_g(var1, var2, var3, var4);
         int var11 = var10 & 7;
         var11 ^= 4;
         if ((var10 & 8) == 0) {
            var1.func_72921_c(var2, var3, var4, var11, 2);
            var1.func_147458_c(var2, var3, var4, var2, var3, var4);
         } else {
            var1.func_72921_c(var2, var3 - 1, var4, var11, 2);
            var1.func_147458_c(var2, var3 - 1, var4, var2, var3, var4);
         }

         var1.func_72889_a(var5, 1003, var2, var3, var4, 0);
         return true;
      }
   }

   public void func_150014_a(World var1, int var2, int var3, int var4, boolean var5) {
      int var6 = this.func_150012_g(var1, var2, var3, var4);
      boolean var7 = (var6 & 4) != 0;
      if (var7 != var5) {
         int var8 = var6 & 7;
         var8 ^= 4;
         if ((var6 & 8) == 0) {
            var1.func_72921_c(var2, var3, var4, var8, 2);
            var1.func_147458_c(var2, var3, var4, var2, var3, var4);
         } else {
            var1.func_72921_c(var2, var3 - 1, var4, var8, 2);
            var1.func_147458_c(var2, var3 - 1, var4, var2, var3, var4);
         }

         var1.func_72889_a(null, 1003, var2, var3, var4, 0);
      }
   }

   @Override
   public void func_149695_a(World var1, int var2, int var3, int var4, Block var5) {
      int var6 = var1.func_72805_g(var2, var3, var4);
      if ((var6 & 8) == 0) {
         boolean var7 = false;
         if (var1.func_147439_a(var2, var3 + 1, var4) != this) {
            var1.func_147468_f(var2, var3, var4);
            var7 = true;
         }

         if (!World.func_147466_a(var1, var2, var3 - 1, var4)) {
            var1.func_147468_f(var2, var3, var4);
            var7 = true;
            if (var1.func_147439_a(var2, var3 + 1, var4) == this) {
               var1.func_147468_f(var2, var3 + 1, var4);
            }
         }

         if (var7) {
            if (!var1.field_72995_K) {
               this.func_149697_b(var1, var2, var3, var4, var6, 0);
            }
         } else {
            boolean var8 = var1.func_72864_z(var2, var3, var4) || var1.func_72864_z(var2, var3 + 1, var4);
            if ((var8 || var5.func_149744_f()) && var5 != this) {
               this.func_150014_a(var1, var2, var3, var4, var8);
            }
         }
      } else {
         if (var1.func_147439_a(var2, var3 - 1, var4) != this) {
            var1.func_147468_f(var2, var3, var4);
         }

         if (var5 != this) {
            this.func_149695_a(var1, var2, var3 - 1, var4, var5);
         }
      }
   }

   @Override
   public Item func_149650_a(int var1, Random var2, int var3) {
      if ((var1 & 8) != 0) {
         return null;
      } else {
         return this.field_149764_J == Material.field_151573_f ? Items.field_151139_aw : Items.field_151135_aq;
      }
   }

   @Override
   public MovingObjectPosition func_149731_a(World var1, int var2, int var3, int var4, Vec3 var5, Vec3 var6) {
      this.func_149719_a(var1, var2, var3, var4);
      return super.func_149731_a(var1, var2, var3, var4, var5, var6);
   }

   @Override
   public boolean func_149742_c(World var1, int var2, int var3, int var4) {
      if (var3 >= 255) {
         return false;
      } else {
         return World.func_147466_a(var1, var2, var3 - 1, var4)
            && super.func_149742_c(var1, var2, var3, var4)
            && super.func_149742_c(var1, var2, var3 + 1, var4);
      }
   }

   @Override
   public int func_149656_h() {
      return 1;
   }

   public int func_150012_g(IBlockAccess var1, int var2, int var3, int var4) {
      int var5 = var1.func_72805_g(var2, var3, var4);
      boolean var6 = (var5 & 8) != 0;
      int var7;
      int var8;
      if (var6) {
         var7 = var1.func_72805_g(var2, var3 - 1, var4);
         var8 = var5;
      } else {
         var7 = var5;
         var8 = var1.func_72805_g(var2, var3 + 1, var4);
      }

      boolean var9 = (var8 & 1) != 0;
      return var7 & 7 | (var6 ? 8 : 0) | (var9 ? 16 : 0);
   }

   @Override
   public Item func_149694_d(World var1, int var2, int var3, int var4) {
      return this.field_149764_J == Material.field_151573_f ? Items.field_151139_aw : Items.field_151135_aq;
   }

   @Override
   public void func_149681_a(World var1, int var2, int var3, int var4, int var5, EntityPlayer var6) {
      if (var6.field_71075_bZ.field_75098_d && (var5 & 8) != 0 && var1.func_147439_a(var2, var3 - 1, var4) == this) {
         var1.func_147468_f(var2, var3 - 1, var4);
      }
   }
}
