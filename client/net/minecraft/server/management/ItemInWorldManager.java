package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class ItemInWorldManager {
   public World field_73092_a;
   public EntityPlayerMP field_73090_b;
   private WorldSettings.GameType field_73091_c;
   private boolean field_73088_d;
   private int field_73089_e;
   private BlockPos field_180240_f;
   private int field_73100_i;
   private boolean field_73097_j;
   private BlockPos field_180241_i;
   private int field_73093_n;
   private int field_73094_o;

   public ItemInWorldManager(World var1) {
      super();
      this.field_73091_c = WorldSettings.GameType.NOT_SET;
      this.field_180240_f = BlockPos.field_177992_a;
      this.field_180241_i = BlockPos.field_177992_a;
      this.field_73094_o = -1;
      this.field_73092_a = var1;
   }

   public void func_73076_a(WorldSettings.GameType var1) {
      this.field_73091_c = var1;
      var1.func_77147_a(this.field_73090_b.field_71075_bZ);
      this.field_73090_b.func_71016_p();
      this.field_73090_b.field_71133_b.func_71203_ab().func_148540_a(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_GAME_MODE, new EntityPlayerMP[]{this.field_73090_b}));
   }

   public WorldSettings.GameType func_73081_b() {
      return this.field_73091_c;
   }

   public boolean func_180239_c() {
      return this.field_73091_c.func_77144_e();
   }

   public boolean func_73083_d() {
      return this.field_73091_c.func_77145_d();
   }

   public void func_73077_b(WorldSettings.GameType var1) {
      if (this.field_73091_c == WorldSettings.GameType.NOT_SET) {
         this.field_73091_c = var1;
      }

      this.func_73076_a(this.field_73091_c);
   }

   public void func_73075_a() {
      ++this.field_73100_i;
      float var3;
      int var4;
      if (this.field_73097_j) {
         int var1 = this.field_73100_i - this.field_73093_n;
         Block var2 = this.field_73092_a.func_180495_p(this.field_180241_i).func_177230_c();
         if (var2.func_149688_o() == Material.field_151579_a) {
            this.field_73097_j = false;
         } else {
            var3 = var2.func_180647_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_180241_i) * (float)(var1 + 1);
            var4 = (int)(var3 * 10.0F);
            if (var4 != this.field_73094_o) {
               this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), this.field_180241_i, var4);
               this.field_73094_o = var4;
            }

            if (var3 >= 1.0F) {
               this.field_73097_j = false;
               this.func_180237_b(this.field_180241_i);
            }
         }
      } else if (this.field_73088_d) {
         Block var5 = this.field_73092_a.func_180495_p(this.field_180240_f).func_177230_c();
         if (var5.func_149688_o() == Material.field_151579_a) {
            this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), this.field_180240_f, -1);
            this.field_73094_o = -1;
            this.field_73088_d = false;
         } else {
            int var6 = this.field_73100_i - this.field_73089_e;
            var3 = var5.func_180647_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_180241_i) * (float)(var6 + 1);
            var4 = (int)(var3 * 10.0F);
            if (var4 != this.field_73094_o) {
               this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), this.field_180240_f, var4);
               this.field_73094_o = var4;
            }
         }
      }

   }

   public void func_180784_a(BlockPos var1, EnumFacing var2) {
      if (this.func_73083_d()) {
         if (!this.field_73092_a.func_175719_a((EntityPlayer)null, var1, var2)) {
            this.func_180237_b(var1);
         }

      } else {
         Block var3 = this.field_73092_a.func_180495_p(var1).func_177230_c();
         if (this.field_73091_c.func_82752_c()) {
            if (this.field_73091_c == WorldSettings.GameType.SPECTATOR) {
               return;
            }

            if (!this.field_73090_b.func_175142_cm()) {
               ItemStack var4 = this.field_73090_b.func_71045_bC();
               if (var4 == null) {
                  return;
               }

               if (!var4.func_179544_c(var3)) {
                  return;
               }
            }
         }

         this.field_73092_a.func_175719_a((EntityPlayer)null, var1, var2);
         this.field_73089_e = this.field_73100_i;
         float var6 = 1.0F;
         if (var3.func_149688_o() != Material.field_151579_a) {
            var3.func_180649_a(this.field_73092_a, var1, this.field_73090_b);
            var6 = var3.func_180647_a(this.field_73090_b, this.field_73090_b.field_70170_p, var1);
         }

         if (var3.func_149688_o() != Material.field_151579_a && var6 >= 1.0F) {
            this.func_180237_b(var1);
         } else {
            this.field_73088_d = true;
            this.field_180240_f = var1;
            int var5 = (int)(var6 * 10.0F);
            this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), var1, var5);
            this.field_73094_o = var5;
         }

      }
   }

   public void func_180785_a(BlockPos var1) {
      if (var1.equals(this.field_180240_f)) {
         int var2 = this.field_73100_i - this.field_73089_e;
         Block var3 = this.field_73092_a.func_180495_p(var1).func_177230_c();
         if (var3.func_149688_o() != Material.field_151579_a) {
            float var4 = var3.func_180647_a(this.field_73090_b, this.field_73090_b.field_70170_p, var1) * (float)(var2 + 1);
            if (var4 >= 0.7F) {
               this.field_73088_d = false;
               this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), var1, -1);
               this.func_180237_b(var1);
            } else if (!this.field_73097_j) {
               this.field_73088_d = false;
               this.field_73097_j = true;
               this.field_180241_i = var1;
               this.field_73093_n = this.field_73089_e;
            }
         }
      }

   }

   public void func_180238_e() {
      this.field_73088_d = false;
      this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), this.field_180240_f, -1);
   }

   private boolean func_180235_c(BlockPos var1) {
      IBlockState var2 = this.field_73092_a.func_180495_p(var1);
      var2.func_177230_c().func_176208_a(this.field_73092_a, var1, var2, this.field_73090_b);
      boolean var3 = this.field_73092_a.func_175698_g(var1);
      if (var3) {
         var2.func_177230_c().func_176206_d(this.field_73092_a, var1, var2);
      }

      return var3;
   }

   public boolean func_180237_b(BlockPos var1) {
      if (this.field_73091_c.func_77145_d() && this.field_73090_b.func_70694_bm() != null && this.field_73090_b.func_70694_bm().func_77973_b() instanceof ItemSword) {
         return false;
      } else {
         IBlockState var2 = this.field_73092_a.func_180495_p(var1);
         TileEntity var3 = this.field_73092_a.func_175625_s(var1);
         if (this.field_73091_c.func_82752_c()) {
            if (this.field_73091_c == WorldSettings.GameType.SPECTATOR) {
               return false;
            }

            if (!this.field_73090_b.func_175142_cm()) {
               ItemStack var4 = this.field_73090_b.func_71045_bC();
               if (var4 == null) {
                  return false;
               }

               if (!var4.func_179544_c(var2.func_177230_c())) {
                  return false;
               }
            }
         }

         this.field_73092_a.func_180498_a(this.field_73090_b, 2001, var1, Block.func_176210_f(var2));
         boolean var7 = this.func_180235_c(var1);
         if (this.func_73083_d()) {
            this.field_73090_b.field_71135_a.func_147359_a(new S23PacketBlockChange(this.field_73092_a, var1));
         } else {
            ItemStack var5 = this.field_73090_b.func_71045_bC();
            boolean var6 = this.field_73090_b.func_146099_a(var2.func_177230_c());
            if (var5 != null) {
               var5.func_179548_a(this.field_73092_a, var2.func_177230_c(), var1, this.field_73090_b);
               if (var5.field_77994_a == 0) {
                  this.field_73090_b.func_71028_bD();
               }
            }

            if (var7 && var6) {
               var2.func_177230_c().func_180657_a(this.field_73092_a, this.field_73090_b, var1, var2, var3);
            }
         }

         return var7;
      }
   }

   public boolean func_73085_a(EntityPlayer var1, World var2, ItemStack var3) {
      if (this.field_73091_c == WorldSettings.GameType.SPECTATOR) {
         return false;
      } else {
         int var4 = var3.field_77994_a;
         int var5 = var3.func_77960_j();
         ItemStack var6 = var3.func_77957_a(var2, var1);
         if (var6 != var3 || var6 != null && (var6.field_77994_a != var4 || var6.func_77988_m() > 0 || var6.func_77960_j() != var5)) {
            var1.field_71071_by.field_70462_a[var1.field_71071_by.field_70461_c] = var6;
            if (this.func_73083_d()) {
               var6.field_77994_a = var4;
               if (var6.func_77984_f()) {
                  var6.func_77964_b(var5);
               }
            }

            if (var6.field_77994_a == 0) {
               var1.field_71071_by.field_70462_a[var1.field_71071_by.field_70461_c] = null;
            }

            if (!var1.func_71039_bw()) {
               ((EntityPlayerMP)var1).func_71120_a(var1.field_71069_bz);
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean func_180236_a(EntityPlayer var1, World var2, ItemStack var3, BlockPos var4, EnumFacing var5, float var6, float var7, float var8) {
      if (this.field_73091_c == WorldSettings.GameType.SPECTATOR) {
         TileEntity var13 = var2.func_175625_s(var4);
         if (var13 instanceof ILockableContainer) {
            Block var14 = var2.func_180495_p(var4).func_177230_c();
            ILockableContainer var15 = (ILockableContainer)var13;
            if (var15 instanceof TileEntityChest && var14 instanceof BlockChest) {
               var15 = ((BlockChest)var14).func_180676_d(var2, var4);
            }

            if (var15 != null) {
               var1.func_71007_a(var15);
               return true;
            }
         } else if (var13 instanceof IInventory) {
            var1.func_71007_a((IInventory)var13);
            return true;
         }

         return false;
      } else {
         if (!var1.func_70093_af() || var1.func_70694_bm() == null) {
            IBlockState var9 = var2.func_180495_p(var4);
            if (var9.func_177230_c().func_180639_a(var2, var4, var9, var1, var5, var6, var7, var8)) {
               return true;
            }
         }

         if (var3 == null) {
            return false;
         } else if (this.func_73083_d()) {
            int var12 = var3.func_77960_j();
            int var10 = var3.field_77994_a;
            boolean var11 = var3.func_179546_a(var1, var2, var4, var5, var6, var7, var8);
            var3.func_77964_b(var12);
            var3.field_77994_a = var10;
            return var11;
         } else {
            return var3.func_179546_a(var1, var2, var4, var5, var6, var7, var8);
         }
      }
   }

   public void func_73080_a(WorldServer var1) {
      this.field_73092_a = var1;
   }
}
