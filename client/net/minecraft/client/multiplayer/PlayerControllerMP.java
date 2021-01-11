package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public class PlayerControllerMP {
   private final Minecraft field_78776_a;
   private final NetHandlerPlayClient field_78774_b;
   private BlockPos field_178895_c = new BlockPos(-1, -1, -1);
   private ItemStack field_85183_f;
   private float field_78770_f;
   private float field_78780_h;
   private int field_78781_i;
   private boolean field_78778_j;
   private WorldSettings.GameType field_78779_k;
   private int field_78777_l;

   public PlayerControllerMP(Minecraft var1, NetHandlerPlayClient var2) {
      super();
      this.field_78779_k = WorldSettings.GameType.SURVIVAL;
      this.field_78776_a = var1;
      this.field_78774_b = var2;
   }

   public static void func_178891_a(Minecraft var0, PlayerControllerMP var1, BlockPos var2, EnumFacing var3) {
      if (!var0.field_71441_e.func_175719_a(var0.field_71439_g, var2, var3)) {
         var1.func_178888_a(var2, var3);
      }

   }

   public void func_78748_a(EntityPlayer var1) {
      this.field_78779_k.func_77147_a(var1.field_71075_bZ);
   }

   public boolean func_78747_a() {
      return this.field_78779_k == WorldSettings.GameType.SPECTATOR;
   }

   public void func_78746_a(WorldSettings.GameType var1) {
      this.field_78779_k = var1;
      this.field_78779_k.func_77147_a(this.field_78776_a.field_71439_g.field_71075_bZ);
   }

   public void func_78745_b(EntityPlayer var1) {
      var1.field_70177_z = -180.0F;
   }

   public boolean func_78755_b() {
      return this.field_78779_k.func_77144_e();
   }

   public boolean func_178888_a(BlockPos var1, EnumFacing var2) {
      if (this.field_78779_k.func_82752_c()) {
         if (this.field_78779_k == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.field_78776_a.field_71439_g.func_175142_cm()) {
            Block var3 = this.field_78776_a.field_71441_e.func_180495_p(var1).func_177230_c();
            ItemStack var4 = this.field_78776_a.field_71439_g.func_71045_bC();
            if (var4 == null) {
               return false;
            }

            if (!var4.func_179544_c(var3)) {
               return false;
            }
         }
      }

      if (this.field_78779_k.func_77145_d() && this.field_78776_a.field_71439_g.func_70694_bm() != null && this.field_78776_a.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemSword) {
         return false;
      } else {
         WorldClient var8 = this.field_78776_a.field_71441_e;
         IBlockState var9 = var8.func_180495_p(var1);
         Block var5 = var9.func_177230_c();
         if (var5.func_149688_o() == Material.field_151579_a) {
            return false;
         } else {
            var8.func_175718_b(2001, var1, Block.func_176210_f(var9));
            boolean var6 = var8.func_175698_g(var1);
            if (var6) {
               var5.func_176206_d(var8, var1, var9);
            }

            this.field_178895_c = new BlockPos(this.field_178895_c.func_177958_n(), -1, this.field_178895_c.func_177952_p());
            if (!this.field_78779_k.func_77145_d()) {
               ItemStack var7 = this.field_78776_a.field_71439_g.func_71045_bC();
               if (var7 != null) {
                  var7.func_179548_a(var8, var5, var1, this.field_78776_a.field_71439_g);
                  if (var7.field_77994_a == 0) {
                     this.field_78776_a.field_71439_g.func_71028_bD();
                  }
               }
            }

            return var6;
         }
      }
   }

   public boolean func_180511_b(BlockPos var1, EnumFacing var2) {
      Block var3;
      if (this.field_78779_k.func_82752_c()) {
         if (this.field_78779_k == WorldSettings.GameType.SPECTATOR) {
            return false;
         }

         if (!this.field_78776_a.field_71439_g.func_175142_cm()) {
            var3 = this.field_78776_a.field_71441_e.func_180495_p(var1).func_177230_c();
            ItemStack var4 = this.field_78776_a.field_71439_g.func_71045_bC();
            if (var4 == null) {
               return false;
            }

            if (!var4.func_179544_c(var3)) {
               return false;
            }
         }
      }

      if (!this.field_78776_a.field_71441_e.func_175723_af().func_177746_a(var1)) {
         return false;
      } else {
         if (this.field_78779_k.func_77145_d()) {
            this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
            func_178891_a(this.field_78776_a, this, var1, var2);
            this.field_78781_i = 5;
         } else if (!this.field_78778_j || !this.func_178893_a(var1)) {
            if (this.field_78778_j) {
               this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.field_178895_c, var2));
            }

            this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
            var3 = this.field_78776_a.field_71441_e.func_180495_p(var1).func_177230_c();
            boolean var5 = var3.func_149688_o() != Material.field_151579_a;
            if (var5 && this.field_78770_f == 0.0F) {
               var3.func_180649_a(this.field_78776_a.field_71441_e, var1, this.field_78776_a.field_71439_g);
            }

            if (var5 && var3.func_180647_a(this.field_78776_a.field_71439_g, this.field_78776_a.field_71439_g.field_70170_p, var1) >= 1.0F) {
               this.func_178888_a(var1, var2);
            } else {
               this.field_78778_j = true;
               this.field_178895_c = var1;
               this.field_85183_f = this.field_78776_a.field_71439_g.func_70694_bm();
               this.field_78770_f = 0.0F;
               this.field_78780_h = 0.0F;
               this.field_78776_a.field_71441_e.func_175715_c(this.field_78776_a.field_71439_g.func_145782_y(), this.field_178895_c, (int)(this.field_78770_f * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void func_78767_c() {
      if (this.field_78778_j) {
         this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.field_178895_c, EnumFacing.DOWN));
         this.field_78778_j = false;
         this.field_78770_f = 0.0F;
         this.field_78776_a.field_71441_e.func_175715_c(this.field_78776_a.field_71439_g.func_145782_y(), this.field_178895_c, -1);
      }

   }

   public boolean func_180512_c(BlockPos var1, EnumFacing var2) {
      this.func_78750_j();
      if (this.field_78781_i > 0) {
         --this.field_78781_i;
         return true;
      } else if (this.field_78779_k.func_77145_d() && this.field_78776_a.field_71441_e.func_175723_af().func_177746_a(var1)) {
         this.field_78781_i = 5;
         this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
         func_178891_a(this.field_78776_a, this, var1, var2);
         return true;
      } else if (this.func_178893_a(var1)) {
         Block var3 = this.field_78776_a.field_71441_e.func_180495_p(var1).func_177230_c();
         if (var3.func_149688_o() == Material.field_151579_a) {
            this.field_78778_j = false;
            return false;
         } else {
            this.field_78770_f += var3.func_180647_a(this.field_78776_a.field_71439_g, this.field_78776_a.field_71439_g.field_70170_p, var1);
            if (this.field_78780_h % 4.0F == 0.0F) {
               this.field_78776_a.func_147118_V().func_147682_a(new PositionedSoundRecord(new ResourceLocation(var3.field_149762_H.func_150498_e()), (var3.field_149762_H.func_150497_c() + 1.0F) / 8.0F, var3.field_149762_H.func_150494_d() * 0.5F, (float)var1.func_177958_n() + 0.5F, (float)var1.func_177956_o() + 0.5F, (float)var1.func_177952_p() + 0.5F));
            }

            ++this.field_78780_h;
            if (this.field_78770_f >= 1.0F) {
               this.field_78778_j = false;
               this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, var1, var2));
               this.func_178888_a(var1, var2);
               this.field_78770_f = 0.0F;
               this.field_78780_h = 0.0F;
               this.field_78781_i = 5;
            }

            this.field_78776_a.field_71441_e.func_175715_c(this.field_78776_a.field_71439_g.func_145782_y(), this.field_178895_c, (int)(this.field_78770_f * 10.0F) - 1);
            return true;
         }
      } else {
         return this.func_180511_b(var1, var2);
      }
   }

   public float func_78757_d() {
      return this.field_78779_k.func_77145_d() ? 5.0F : 4.5F;
   }

   public void func_78765_e() {
      this.func_78750_j();
      if (this.field_78774_b.func_147298_b().func_150724_d()) {
         this.field_78774_b.func_147298_b().func_74428_b();
      } else {
         this.field_78774_b.func_147298_b().func_179293_l();
      }

   }

   private boolean func_178893_a(BlockPos var1) {
      ItemStack var2 = this.field_78776_a.field_71439_g.func_70694_bm();
      boolean var3 = this.field_85183_f == null && var2 == null;
      if (this.field_85183_f != null && var2 != null) {
         var3 = var2.func_77973_b() == this.field_85183_f.func_77973_b() && ItemStack.func_77970_a(var2, this.field_85183_f) && (var2.func_77984_f() || var2.func_77960_j() == this.field_85183_f.func_77960_j());
      }

      return var1.equals(this.field_178895_c) && var3;
   }

   private void func_78750_j() {
      int var1 = this.field_78776_a.field_71439_g.field_71071_by.field_70461_c;
      if (var1 != this.field_78777_l) {
         this.field_78777_l = var1;
         this.field_78774_b.func_147297_a(new C09PacketHeldItemChange(this.field_78777_l));
      }

   }

   public boolean func_178890_a(EntityPlayerSP var1, WorldClient var2, ItemStack var3, BlockPos var4, EnumFacing var5, Vec3 var6) {
      this.func_78750_j();
      float var7 = (float)(var6.field_72450_a - (double)var4.func_177958_n());
      float var8 = (float)(var6.field_72448_b - (double)var4.func_177956_o());
      float var9 = (float)(var6.field_72449_c - (double)var4.func_177952_p());
      boolean var10 = false;
      if (!this.field_78776_a.field_71441_e.func_175723_af().func_177746_a(var4)) {
         return false;
      } else {
         if (this.field_78779_k != WorldSettings.GameType.SPECTATOR) {
            IBlockState var11 = var2.func_180495_p(var4);
            if ((!var1.func_70093_af() || var1.func_70694_bm() == null) && var11.func_177230_c().func_180639_a(var2, var4, var11, var1, var5, var7, var8, var9)) {
               var10 = true;
            }

            if (!var10 && var3 != null && var3.func_77973_b() instanceof ItemBlock) {
               ItemBlock var12 = (ItemBlock)var3.func_77973_b();
               if (!var12.func_179222_a(var2, var4, var5, var1, var3)) {
                  return false;
               }
            }
         }

         this.field_78774_b.func_147297_a(new C08PacketPlayerBlockPlacement(var4, var5.func_176745_a(), var1.field_71071_by.func_70448_g(), var7, var8, var9));
         if (!var10 && this.field_78779_k != WorldSettings.GameType.SPECTATOR) {
            if (var3 == null) {
               return false;
            } else if (this.field_78779_k.func_77145_d()) {
               int var14 = var3.func_77960_j();
               int var15 = var3.field_77994_a;
               boolean var13 = var3.func_179546_a(var1, var2, var4, var5, var7, var8, var9);
               var3.func_77964_b(var14);
               var3.field_77994_a = var15;
               return var13;
            } else {
               return var3.func_179546_a(var1, var2, var4, var5, var7, var8, var9);
            }
         } else {
            return true;
         }
      }
   }

   public boolean func_78769_a(EntityPlayer var1, World var2, ItemStack var3) {
      if (this.field_78779_k == WorldSettings.GameType.SPECTATOR) {
         return false;
      } else {
         this.func_78750_j();
         this.field_78774_b.func_147297_a(new C08PacketPlayerBlockPlacement(var1.field_71071_by.func_70448_g()));
         int var4 = var3.field_77994_a;
         ItemStack var5 = var3.func_77957_a(var2, var1);
         if (var5 != var3 || var5 != null && var5.field_77994_a != var4) {
            var1.field_71071_by.field_70462_a[var1.field_71071_by.field_70461_c] = var5;
            if (var5.field_77994_a == 0) {
               var1.field_71071_by.field_70462_a[var1.field_71071_by.field_70461_c] = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public EntityPlayerSP func_178892_a(World var1, StatFileWriter var2) {
      return new EntityPlayerSP(this.field_78776_a, var1, this.field_78774_b, var2);
   }

   public void func_78764_a(EntityPlayer var1, Entity var2) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new C02PacketUseEntity(var2, C02PacketUseEntity.Action.ATTACK));
      if (this.field_78779_k != WorldSettings.GameType.SPECTATOR) {
         var1.func_71059_n(var2);
      }

   }

   public boolean func_78768_b(EntityPlayer var1, Entity var2) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new C02PacketUseEntity(var2, C02PacketUseEntity.Action.INTERACT));
      return this.field_78779_k != WorldSettings.GameType.SPECTATOR && var1.func_70998_m(var2);
   }

   public boolean func_178894_a(EntityPlayer var1, Entity var2, MovingObjectPosition var3) {
      this.func_78750_j();
      Vec3 var4 = new Vec3(var3.field_72307_f.field_72450_a - var2.field_70165_t, var3.field_72307_f.field_72448_b - var2.field_70163_u, var3.field_72307_f.field_72449_c - var2.field_70161_v);
      this.field_78774_b.func_147297_a(new C02PacketUseEntity(var2, var4));
      return this.field_78779_k != WorldSettings.GameType.SPECTATOR && var2.func_174825_a(var1, var4);
   }

   public ItemStack func_78753_a(int var1, int var2, int var3, int var4, EntityPlayer var5) {
      short var6 = var5.field_71070_bA.func_75136_a(var5.field_71071_by);
      ItemStack var7 = var5.field_71070_bA.func_75144_a(var2, var3, var4, var5);
      this.field_78774_b.func_147297_a(new C0EPacketClickWindow(var1, var2, var3, var4, var7, var6));
      return var7;
   }

   public void func_78756_a(int var1, int var2) {
      this.field_78774_b.func_147297_a(new C11PacketEnchantItem(var1, var2));
   }

   public void func_78761_a(ItemStack var1, int var2) {
      if (this.field_78779_k.func_77145_d()) {
         this.field_78774_b.func_147297_a(new C10PacketCreativeInventoryAction(var2, var1));
      }

   }

   public void func_78752_a(ItemStack var1) {
      if (this.field_78779_k.func_77145_d() && var1 != null) {
         this.field_78774_b.func_147297_a(new C10PacketCreativeInventoryAction(-1, var1));
      }

   }

   public void func_78766_c(EntityPlayer var1) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, EnumFacing.DOWN));
      var1.func_71034_by();
   }

   public boolean func_78763_f() {
      return this.field_78779_k.func_77144_e();
   }

   public boolean func_78762_g() {
      return !this.field_78779_k.func_77145_d();
   }

   public boolean func_78758_h() {
      return this.field_78779_k.func_77145_d();
   }

   public boolean func_78749_i() {
      return this.field_78779_k.func_77145_d();
   }

   public boolean func_110738_j() {
      return this.field_78776_a.field_71439_g.func_70115_ae() && this.field_78776_a.field_71439_g.field_70154_o instanceof EntityHorse;
   }

   public boolean func_178887_k() {
      return this.field_78779_k == WorldSettings.GameType.SPECTATOR;
   }

   public WorldSettings.GameType func_178889_l() {
      return this.field_78779_k;
   }

   public boolean func_181040_m() {
      return this.field_78778_j;
   }
}
