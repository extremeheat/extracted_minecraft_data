package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class PlayerInteractionManager {
   public World field_73092_a;
   public EntityPlayerMP field_73090_b;
   private GameType field_73091_c;
   private boolean field_73088_d;
   private int field_73089_e;
   private BlockPos field_180240_f;
   private int field_73100_i;
   private boolean field_73097_j;
   private BlockPos field_180241_i;
   private int field_73093_n;
   private int field_73094_o;

   public PlayerInteractionManager(World var1) {
      super();
      this.field_73091_c = GameType.NOT_SET;
      this.field_180240_f = BlockPos.field_177992_a;
      this.field_180241_i = BlockPos.field_177992_a;
      this.field_73094_o = -1;
      this.field_73092_a = var1;
   }

   public void func_73076_a(GameType var1) {
      this.field_73091_c = var1;
      var1.func_77147_a(this.field_73090_b.field_71075_bZ);
      this.field_73090_b.func_71016_p();
      this.field_73090_b.field_71133_b.func_184103_al().func_148540_a(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_GAME_MODE, new EntityPlayerMP[]{this.field_73090_b}));
      this.field_73092_a.func_72854_c();
   }

   public GameType func_73081_b() {
      return this.field_73091_c;
   }

   public boolean func_180239_c() {
      return this.field_73091_c.func_77144_e();
   }

   public boolean func_73083_d() {
      return this.field_73091_c.func_77145_d();
   }

   public void func_73077_b(GameType var1) {
      if (this.field_73091_c == GameType.NOT_SET) {
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
         IBlockState var2 = this.field_73092_a.func_180495_p(this.field_180241_i);
         if (var2.func_196958_f()) {
            this.field_73097_j = false;
         } else {
            var3 = var2.func_185903_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_180241_i) * (float)(var1 + 1);
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
         IBlockState var5 = this.field_73092_a.func_180495_p(this.field_180240_f);
         if (var5.func_196958_f()) {
            this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), this.field_180240_f, -1);
            this.field_73094_o = -1;
            this.field_73088_d = false;
         } else {
            int var6 = this.field_73100_i - this.field_73089_e;
            var3 = var5.func_185903_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_180241_i) * (float)(var6 + 1);
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
         if (this.field_73091_c.func_82752_c()) {
            if (this.field_73091_c == GameType.SPECTATOR) {
               return;
            }

            if (!this.field_73090_b.func_175142_cm()) {
               ItemStack var3 = this.field_73090_b.func_184614_ca();
               if (var3.func_190926_b()) {
                  return;
               }

               BlockWorldState var4 = new BlockWorldState(this.field_73092_a, var1, false);
               if (!var3.func_206848_a(this.field_73092_a.func_205772_D(), var4)) {
                  return;
               }
            }
         }

         this.field_73092_a.func_175719_a((EntityPlayer)null, var1, var2);
         this.field_73089_e = this.field_73100_i;
         float var6 = 1.0F;
         IBlockState var7 = this.field_73092_a.func_180495_p(var1);
         if (!var7.func_196958_f()) {
            var7.func_196942_a(this.field_73092_a, var1, this.field_73090_b);
            var6 = var7.func_185903_a(this.field_73090_b, this.field_73090_b.field_70170_p, var1);
         }

         if (!var7.func_196958_f() && var6 >= 1.0F) {
            this.func_180237_b(var1);
         } else {
            this.field_73088_d = true;
            this.field_180240_f = var1;
            int var5 = (int)(var6 * 10.0F);
            this.field_73092_a.func_175715_c(this.field_73090_b.func_145782_y(), var1, var5);
            this.field_73090_b.field_71135_a.func_147359_a(new SPacketBlockChange(this.field_73092_a, var1));
            this.field_73094_o = var5;
         }

      }
   }

   public void func_180785_a(BlockPos var1) {
      if (var1.equals(this.field_180240_f)) {
         int var2 = this.field_73100_i - this.field_73089_e;
         IBlockState var3 = this.field_73092_a.func_180495_p(var1);
         if (!var3.func_196958_f()) {
            float var4 = var3.func_185903_a(this.field_73090_b, this.field_73090_b.field_70170_p, var1) * (float)(var2 + 1);
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
      IBlockState var2 = this.field_73092_a.func_180495_p(var1);
      if (!this.field_73090_b.func_184614_ca().func_77973_b().func_195938_a(var2, this.field_73092_a, var1, this.field_73090_b)) {
         return false;
      } else {
         TileEntity var3 = this.field_73092_a.func_175625_s(var1);
         Block var4 = var2.func_177230_c();
         if ((var4 instanceof BlockCommandBlock || var4 instanceof BlockStructure) && !this.field_73090_b.func_195070_dx()) {
            this.field_73092_a.func_184138_a(var1, var2, var2, 3);
            return false;
         } else {
            if (this.field_73091_c.func_82752_c()) {
               if (this.field_73091_c == GameType.SPECTATOR) {
                  return false;
               }

               if (!this.field_73090_b.func_175142_cm()) {
                  ItemStack var5 = this.field_73090_b.func_184614_ca();
                  if (var5.func_190926_b()) {
                     return false;
                  }

                  BlockWorldState var6 = new BlockWorldState(this.field_73092_a, var1, false);
                  if (!var5.func_206848_a(this.field_73092_a.func_205772_D(), var6)) {
                     return false;
                  }
               }
            }

            boolean var9 = this.func_180235_c(var1);
            if (!this.func_73083_d()) {
               ItemStack var10 = this.field_73090_b.func_184614_ca();
               boolean var7 = this.field_73090_b.func_184823_b(var2);
               var10.func_179548_a(this.field_73092_a, var2, var1, this.field_73090_b);
               if (var9 && var7) {
                  ItemStack var8 = var10.func_190926_b() ? ItemStack.field_190927_a : var10.func_77946_l();
                  var2.func_177230_c().func_180657_a(this.field_73092_a, this.field_73090_b, var1, var2, var3, var8);
               }
            }

            return var9;
         }
      }
   }

   public EnumActionResult func_187250_a(EntityPlayer var1, World var2, ItemStack var3, EnumHand var4) {
      if (this.field_73091_c == GameType.SPECTATOR) {
         return EnumActionResult.PASS;
      } else if (var1.func_184811_cZ().func_185141_a(var3.func_77973_b())) {
         return EnumActionResult.PASS;
      } else {
         int var5 = var3.func_190916_E();
         int var6 = var3.func_77952_i();
         ActionResult var7 = var3.func_77957_a(var2, var1, var4);
         ItemStack var8 = (ItemStack)var7.func_188398_b();
         if (var8 == var3 && var8.func_190916_E() == var5 && var8.func_77988_m() <= 0 && var8.func_77952_i() == var6) {
            return var7.func_188397_a();
         } else if (var7.func_188397_a() == EnumActionResult.FAIL && var8.func_77988_m() > 0 && !var1.func_184587_cr()) {
            return var7.func_188397_a();
         } else {
            var1.func_184611_a(var4, var8);
            if (this.func_73083_d()) {
               var8.func_190920_e(var5);
               if (var8.func_77984_f()) {
                  var8.func_196085_b(var6);
               }
            }

            if (var8.func_190926_b()) {
               var1.func_184611_a(var4, ItemStack.field_190927_a);
            }

            if (!var1.func_184587_cr()) {
               ((EntityPlayerMP)var1).func_71120_a(var1.field_71069_bz);
            }

            return var7.func_188397_a();
         }
      }
   }

   public EnumActionResult func_187251_a(EntityPlayer var1, World var2, ItemStack var3, EnumHand var4, BlockPos var5, EnumFacing var6, float var7, float var8, float var9) {
      IBlockState var10 = var2.func_180495_p(var5);
      if (this.field_73091_c == GameType.SPECTATOR) {
         TileEntity var16 = var2.func_175625_s(var5);
         if (var16 instanceof ILockableContainer) {
            Block var17 = var10.func_177230_c();
            ILockableContainer var18 = (ILockableContainer)var16;
            if (var18 instanceof TileEntityChest && var17 instanceof BlockChest) {
               var18 = ((BlockChest)var17).func_196309_a(var10, var2, var5, false);
            }

            if (var18 != null) {
               var1.func_71007_a(var18);
               return EnumActionResult.SUCCESS;
            }
         } else if (var16 instanceof IInventory) {
            var1.func_71007_a((IInventory)var16);
            return EnumActionResult.SUCCESS;
         }

         return EnumActionResult.PASS;
      } else {
         boolean var11 = !var1.func_184614_ca().func_190926_b() || !var1.func_184592_cb().func_190926_b();
         boolean var12 = var1.func_70093_af() && var11;
         if (!var12 && var10.func_196943_a(var2, var5, var1, var4, var6, var7, var8, var9)) {
            return EnumActionResult.SUCCESS;
         } else if (!var3.func_190926_b() && !var1.func_184811_cZ().func_185141_a(var3.func_77973_b())) {
            ItemUseContext var13 = new ItemUseContext(var1, var1.func_184586_b(var4), var5, var6, var7, var8, var9);
            if (this.func_73083_d()) {
               int var14 = var3.func_190916_E();
               EnumActionResult var15 = var3.func_196084_a(var13);
               var3.func_190920_e(var14);
               return var15;
            } else {
               return var3.func_196084_a(var13);
            }
         } else {
            return EnumActionResult.PASS;
         }
      }
   }

   public void func_73080_a(WorldServer var1) {
      this.field_73092_a = var1;
   }
}
