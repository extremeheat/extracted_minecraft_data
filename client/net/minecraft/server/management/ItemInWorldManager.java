package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings$GameType;

public class ItemInWorldManager {
   public World field_73092_a;
   public EntityPlayerMP field_73090_b;
   private WorldSettings$GameType field_73091_c = WorldSettings$GameType.NOT_SET;
   private boolean field_73088_d;
   private int field_73089_e;
   private int field_73086_f;
   private int field_73087_g;
   private int field_73099_h;
   private int field_73100_i;
   private boolean field_73097_j;
   private int field_73098_k;
   private int field_73095_l;
   private int field_73096_m;
   private int field_73093_n;
   private int field_73094_o = -1;

   public ItemInWorldManager(World var1) {
      super();
      this.field_73092_a = var1;
   }

   public void func_73076_a(WorldSettings$GameType var1) {
      this.field_73091_c = var1;
      var1.func_77147_a(this.field_73090_b.field_71075_bZ);
      this.field_73090_b.func_71016_p();
   }

   public WorldSettings$GameType func_73081_b() {
      return this.field_73091_c;
   }

   public boolean func_73083_d() {
      return this.field_73091_c.func_77145_d();
   }

   public void func_73077_b(WorldSettings$GameType var1) {
      if (this.field_73091_c == WorldSettings$GameType.NOT_SET) {
         this.field_73091_c = var1;
      }

      this.func_73076_a(this.field_73091_c);
   }

   public void func_73075_a() {
      ++this.field_73100_i;
      if (this.field_73097_j) {
         int var1 = this.field_73100_i - this.field_73093_n;
         Block var2 = this.field_73092_a.func_147439_a(this.field_73098_k, this.field_73095_l, this.field_73096_m);
         if (var2.func_149688_o() == Material.field_151579_a) {
            this.field_73097_j = false;
         } else {
            float var3 = var2.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_73098_k, this.field_73095_l, this.field_73096_m)
               * (float)(var1 + 1);
            int var4 = (int)(var3 * 10.0F);
            if (var4 != this.field_73094_o) {
               this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73098_k, this.field_73095_l, this.field_73096_m, var4);
               this.field_73094_o = var4;
            }

            if (var3 >= 1.0F) {
               this.field_73097_j = false;
               this.func_73084_b(this.field_73098_k, this.field_73095_l, this.field_73096_m);
            }
         }
      } else if (this.field_73088_d) {
         Block var5 = this.field_73092_a.func_147439_a(this.field_73086_f, this.field_73087_g, this.field_73099_h);
         if (var5.func_149688_o() == Material.field_151579_a) {
            this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73086_f, this.field_73087_g, this.field_73099_h, -1);
            this.field_73094_o = -1;
            this.field_73088_d = false;
         } else {
            int var6 = this.field_73100_i - this.field_73089_e;
            float var7 = var5.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, this.field_73086_f, this.field_73087_g, this.field_73099_h)
               * (float)(var6 + 1);
            int var8 = (int)(var7 * 10.0F);
            if (var8 != this.field_73094_o) {
               this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73086_f, this.field_73087_g, this.field_73099_h, var8);
               this.field_73094_o = var8;
            }
         }
      }
   }

   public void func_73074_a(int var1, int var2, int var3, int var4) {
      if (!this.field_73091_c.func_82752_c() || this.field_73090_b.func_82246_f(var1, var2, var3)) {
         if (this.func_73083_d()) {
            if (!this.field_73092_a.func_72886_a(null, var1, var2, var3, var4)) {
               this.func_73084_b(var1, var2, var3);
            }
         } else {
            this.field_73092_a.func_72886_a(null, var1, var2, var3, var4);
            this.field_73089_e = this.field_73100_i;
            float var5 = 1.0F;
            Block var6 = this.field_73092_a.func_147439_a(var1, var2, var3);
            if (var6.func_149688_o() != Material.field_151579_a) {
               var6.func_149699_a(this.field_73092_a, var1, var2, var3, this.field_73090_b);
               var5 = var6.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, var1, var2, var3);
            }

            if (var6.func_149688_o() != Material.field_151579_a && var5 >= 1.0F) {
               this.func_73084_b(var1, var2, var3);
            } else {
               this.field_73088_d = true;
               this.field_73086_f = var1;
               this.field_73087_g = var2;
               this.field_73099_h = var3;
               int var7 = (int)(var5 * 10.0F);
               this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), var1, var2, var3, var7);
               this.field_73094_o = var7;
            }
         }
      }
   }

   public void func_73082_a(int var1, int var2, int var3) {
      if (var1 == this.field_73086_f && var2 == this.field_73087_g && var3 == this.field_73099_h) {
         int var4 = this.field_73100_i - this.field_73089_e;
         Block var5 = this.field_73092_a.func_147439_a(var1, var2, var3);
         if (var5.func_149688_o() != Material.field_151579_a) {
            float var6 = var5.func_149737_a(this.field_73090_b, this.field_73090_b.field_70170_p, var1, var2, var3) * (float)(var4 + 1);
            if (var6 >= 0.7F) {
               this.field_73088_d = false;
               this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), var1, var2, var3, -1);
               this.func_73084_b(var1, var2, var3);
            } else if (!this.field_73097_j) {
               this.field_73088_d = false;
               this.field_73097_j = true;
               this.field_73098_k = var1;
               this.field_73095_l = var2;
               this.field_73096_m = var3;
               this.field_73093_n = this.field_73089_e;
            }
         }
      }
   }

   public void func_73073_c(int var1, int var2, int var3) {
      this.field_73088_d = false;
      this.field_73092_a.func_147443_d(this.field_73090_b.func_145782_y(), this.field_73086_f, this.field_73087_g, this.field_73099_h, -1);
   }

   private boolean func_73079_d(int var1, int var2, int var3) {
      Block var4 = this.field_73092_a.func_147439_a(var1, var2, var3);
      int var5 = this.field_73092_a.func_72805_g(var1, var2, var3);
      var4.func_149681_a(this.field_73092_a, var1, var2, var3, var5, this.field_73090_b);
      boolean var6 = this.field_73092_a.func_147468_f(var1, var2, var3);
      if (var6) {
         var4.func_149664_b(this.field_73092_a, var1, var2, var3, var5);
      }

      return var6;
   }

   public boolean func_73084_b(int var1, int var2, int var3) {
      if (this.field_73091_c.func_82752_c() && !this.field_73090_b.func_82246_f(var1, var2, var3)) {
         return false;
      } else if (this.field_73091_c.func_77145_d()
         && this.field_73090_b.func_70694_bm() != null
         && this.field_73090_b.func_70694_bm().func_77973_b() instanceof ItemSword) {
         return false;
      } else {
         Block var4 = this.field_73092_a.func_147439_a(var1, var2, var3);
         int var5 = this.field_73092_a.func_72805_g(var1, var2, var3);
         this.field_73092_a
            .func_72889_a(this.field_73090_b, 2001, var1, var2, var3, Block.func_149682_b(var4) + (this.field_73092_a.func_72805_g(var1, var2, var3) << 12));
         boolean var6 = this.func_73079_d(var1, var2, var3);
         if (this.func_73083_d()) {
            this.field_73090_b.field_71135_a.func_147359_a(new S23PacketBlockChange(var1, var2, var3, this.field_73092_a));
         } else {
            ItemStack var7 = this.field_73090_b.func_71045_bC();
            boolean var8 = this.field_73090_b.func_146099_a(var4);
            if (var7 != null) {
               var7.func_150999_a(this.field_73092_a, var4, var1, var2, var3, this.field_73090_b);
               if (var7.field_77994_a == 0) {
                  this.field_73090_b.func_71028_bD();
               }
            }

            if (var6 && var8) {
               var4.func_149636_a(this.field_73092_a, this.field_73090_b, var1, var2, var3, var5);
            }
         }

         return var6;
      }
   }

   public boolean func_73085_a(EntityPlayer var1, World var2, ItemStack var3) {
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

   public boolean func_73078_a(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if ((!var1.func_70093_af() || var1.func_70694_bm() == null)
         && var2.func_147439_a(var4, var5, var6).func_149727_a(var2, var4, var5, var6, var1, var7, var8, var9, var10)) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (this.func_73083_d()) {
         int var11 = var3.func_77960_j();
         int var12 = var3.field_77994_a;
         boolean var13 = var3.func_77943_a(var1, var2, var4, var5, var6, var7, var8, var9, var10);
         var3.func_77964_b(var11);
         var3.field_77994_a = var12;
         return var13;
      } else {
         return var3.func_77943_a(var1, var2, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   public void func_73080_a(WorldServer var1) {
      this.field_73092_a = var1;
   }
}
