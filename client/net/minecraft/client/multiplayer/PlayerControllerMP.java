package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCommandBlock;
import net.minecraft.block.BlockStructure;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.util.RecipeBookClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

public class PlayerControllerMP {
   private final Minecraft field_78776_a;
   private final NetHandlerPlayClient field_78774_b;
   private BlockPos field_178895_c = new BlockPos(-1, -1, -1);
   private ItemStack field_85183_f;
   private float field_78770_f;
   private float field_78780_h;
   private int field_78781_i;
   private boolean field_78778_j;
   private GameType field_78779_k;
   private int field_78777_l;

   public PlayerControllerMP(Minecraft var1, NetHandlerPlayClient var2) {
      super();
      this.field_85183_f = ItemStack.field_190927_a;
      this.field_78779_k = GameType.SURVIVAL;
      this.field_78776_a = var1;
      this.field_78774_b = var2;
   }

   public static void func_178891_a(Minecraft var0, PlayerControllerMP var1, BlockPos var2, EnumFacing var3) {
      if (!var0.field_71441_e.func_175719_a(var0.field_71439_g, var2, var3)) {
         var1.func_187103_a(var2);
      }

   }

   public void func_78748_a(EntityPlayer var1) {
      this.field_78779_k.func_77147_a(var1.field_71075_bZ);
   }

   public void func_78746_a(GameType var1) {
      this.field_78779_k = var1;
      this.field_78779_k.func_77147_a(this.field_78776_a.field_71439_g.field_71075_bZ);
   }

   public void func_78745_b(EntityPlayer var1) {
      var1.field_70177_z = -180.0F;
   }

   public boolean func_78755_b() {
      return this.field_78779_k.func_77144_e();
   }

   public boolean func_187103_a(BlockPos var1) {
      if (this.field_78779_k.func_82752_c()) {
         if (this.field_78779_k == GameType.SPECTATOR) {
            return false;
         }

         if (!this.field_78776_a.field_71439_g.func_175142_cm()) {
            ItemStack var2 = this.field_78776_a.field_71439_g.func_184614_ca();
            if (var2.func_190926_b()) {
               return false;
            }

            BlockWorldState var3 = new BlockWorldState(this.field_78776_a.field_71441_e, var1, false);
            if (!var2.func_206848_a(this.field_78776_a.field_71441_e.func_205772_D(), var3)) {
               return false;
            }
         }
      }

      WorldClient var8 = this.field_78776_a.field_71441_e;
      IBlockState var9 = var8.func_180495_p(var1);
      if (!this.field_78776_a.field_71439_g.func_184614_ca().func_77973_b().func_195938_a(var9, var8, var1, this.field_78776_a.field_71439_g)) {
         return false;
      } else {
         Block var4 = var9.func_177230_c();
         if ((var4 instanceof BlockCommandBlock || var4 instanceof BlockStructure) && !this.field_78776_a.field_71439_g.func_195070_dx()) {
            return false;
         } else if (var9.func_196958_f()) {
            return false;
         } else {
            var4.func_176208_a(var8, var1, var9, this.field_78776_a.field_71439_g);
            IFluidState var5 = var8.func_204610_c(var1);
            boolean var6 = var8.func_180501_a(var1, var5.func_206883_i(), 11);
            if (var6) {
               var4.func_176206_d(var8, var1, var9);
            }

            this.field_178895_c = new BlockPos(this.field_178895_c.func_177958_n(), -1, this.field_178895_c.func_177952_p());
            if (!this.field_78779_k.func_77145_d()) {
               ItemStack var7 = this.field_78776_a.field_71439_g.func_184614_ca();
               if (!var7.func_190926_b()) {
                  var7.func_179548_a(var8, var9, var1, this.field_78776_a.field_71439_g);
                  if (var7.func_190926_b()) {
                     this.field_78776_a.field_71439_g.func_184611_a(EnumHand.MAIN_HAND, ItemStack.field_190927_a);
                  }
               }
            }

            return var6;
         }
      }
   }

   public boolean func_180511_b(BlockPos var1, EnumFacing var2) {
      if (this.field_78779_k.func_82752_c()) {
         if (this.field_78779_k == GameType.SPECTATOR) {
            return false;
         }

         if (!this.field_78776_a.field_71439_g.func_175142_cm()) {
            ItemStack var3 = this.field_78776_a.field_71439_g.func_184614_ca();
            if (var3.func_190926_b()) {
               return false;
            }

            BlockWorldState var4 = new BlockWorldState(this.field_78776_a.field_71441_e, var1, false);
            if (!var3.func_206848_a(this.field_78776_a.field_71441_e.func_205772_D(), var4)) {
               return false;
            }
         }
      }

      if (!this.field_78776_a.field_71441_e.func_175723_af().func_177746_a(var1)) {
         return false;
      } else {
         if (this.field_78779_k.func_77145_d()) {
            this.field_78776_a.func_193032_ao().func_193294_a(this.field_78776_a.field_71441_e, var1, this.field_78776_a.field_71441_e.func_180495_p(var1), 1.0F);
            this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
            func_178891_a(this.field_78776_a, this, var1, var2);
            this.field_78781_i = 5;
         } else if (!this.field_78778_j || !this.func_178893_a(var1)) {
            if (this.field_78778_j) {
               this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.field_178895_c, var2));
            }

            IBlockState var5 = this.field_78776_a.field_71441_e.func_180495_p(var1);
            this.field_78776_a.func_193032_ao().func_193294_a(this.field_78776_a.field_71441_e, var1, var5, 0.0F);
            this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
            boolean var6 = !var5.func_196958_f();
            if (var6 && this.field_78770_f == 0.0F) {
               var5.func_196942_a(this.field_78776_a.field_71441_e, var1, this.field_78776_a.field_71439_g);
            }

            if (var6 && var5.func_185903_a(this.field_78776_a.field_71439_g, this.field_78776_a.field_71439_g.field_70170_p, var1) >= 1.0F) {
               this.func_187103_a(var1);
            } else {
               this.field_78778_j = true;
               this.field_178895_c = var1;
               this.field_85183_f = this.field_78776_a.field_71439_g.func_184614_ca();
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
         this.field_78776_a.func_193032_ao().func_193294_a(this.field_78776_a.field_71441_e, this.field_178895_c, this.field_78776_a.field_71441_e.func_180495_p(this.field_178895_c), -1.0F);
         this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.field_178895_c, EnumFacing.DOWN));
         this.field_78778_j = false;
         this.field_78770_f = 0.0F;
         this.field_78776_a.field_71441_e.func_175715_c(this.field_78776_a.field_71439_g.func_145782_y(), this.field_178895_c, -1);
         this.field_78776_a.field_71439_g.func_184821_cY();
      }

   }

   public boolean func_180512_c(BlockPos var1, EnumFacing var2) {
      this.func_78750_j();
      if (this.field_78781_i > 0) {
         --this.field_78781_i;
         return true;
      } else if (this.field_78779_k.func_77145_d() && this.field_78776_a.field_71441_e.func_175723_af().func_177746_a(var1)) {
         this.field_78781_i = 5;
         this.field_78776_a.func_193032_ao().func_193294_a(this.field_78776_a.field_71441_e, var1, this.field_78776_a.field_71441_e.func_180495_p(var1), 1.0F);
         this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, var1, var2));
         func_178891_a(this.field_78776_a, this, var1, var2);
         return true;
      } else if (this.func_178893_a(var1)) {
         IBlockState var3 = this.field_78776_a.field_71441_e.func_180495_p(var1);
         Block var4 = var3.func_177230_c();
         if (var3.func_196958_f()) {
            this.field_78778_j = false;
            return false;
         } else {
            this.field_78770_f += var3.func_185903_a(this.field_78776_a.field_71439_g, this.field_78776_a.field_71439_g.field_70170_p, var1);
            if (this.field_78780_h % 4.0F == 0.0F) {
               SoundType var5 = var4.func_185467_w();
               this.field_78776_a.func_147118_V().func_147682_a(new SimpleSound(var5.func_185846_f(), SoundCategory.NEUTRAL, (var5.func_185843_a() + 1.0F) / 8.0F, var5.func_185847_b() * 0.5F, var1));
            }

            ++this.field_78780_h;
            this.field_78776_a.func_193032_ao().func_193294_a(this.field_78776_a.field_71441_e, var1, var3, MathHelper.func_76131_a(this.field_78770_f, 0.0F, 1.0F));
            if (this.field_78770_f >= 1.0F) {
               this.field_78778_j = false;
               this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, var1, var2));
               this.func_187103_a(var1);
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
      ItemStack var2 = this.field_78776_a.field_71439_g.func_184614_ca();
      boolean var3 = this.field_85183_f.func_190926_b() && var2.func_190926_b();
      if (!this.field_85183_f.func_190926_b() && !var2.func_190926_b()) {
         var3 = var2.func_77973_b() == this.field_85183_f.func_77973_b() && ItemStack.func_77970_a(var2, this.field_85183_f) && (var2.func_77984_f() || var2.func_77952_i() == this.field_85183_f.func_77952_i());
      }

      return var1.equals(this.field_178895_c) && var3;
   }

   private void func_78750_j() {
      int var1 = this.field_78776_a.field_71439_g.field_71071_by.field_70461_c;
      if (var1 != this.field_78777_l) {
         this.field_78777_l = var1;
         this.field_78774_b.func_147297_a(new CPacketHeldItemChange(this.field_78777_l));
      }

   }

   public EnumActionResult func_187099_a(EntityPlayerSP var1, WorldClient var2, BlockPos var3, EnumFacing var4, Vec3d var5, EnumHand var6) {
      this.func_78750_j();
      if (!this.field_78776_a.field_71441_e.func_175723_af().func_177746_a(var3)) {
         return EnumActionResult.FAIL;
      } else {
         ItemStack var7 = var1.func_184586_b(var6);
         float var8 = (float)(var5.field_72450_a - (double)var3.func_177958_n());
         float var9 = (float)(var5.field_72448_b - (double)var3.func_177956_o());
         float var10 = (float)(var5.field_72449_c - (double)var3.func_177952_p());
         if (this.field_78779_k == GameType.SPECTATOR) {
            this.field_78774_b.func_147297_a(new CPacketPlayerTryUseItemOnBlock(var3, var4, var6, var8, var9, var10));
            return EnumActionResult.SUCCESS;
         } else {
            boolean var11 = !var1.func_184614_ca().func_190926_b() || !var1.func_184592_cb().func_190926_b();
            boolean var12 = var1.func_70093_af() && var11;
            if (!var12 && var2.func_180495_p(var3).func_196943_a(var2, var3, var1, var6, var4, var8, var9, var10)) {
               this.field_78774_b.func_147297_a(new CPacketPlayerTryUseItemOnBlock(var3, var4, var6, var8, var9, var10));
               return EnumActionResult.SUCCESS;
            } else {
               this.field_78774_b.func_147297_a(new CPacketPlayerTryUseItemOnBlock(var3, var4, var6, var8, var9, var10));
               if (!var7.func_190926_b() && !var1.func_184811_cZ().func_185141_a(var7.func_77973_b())) {
                  ItemUseContext var14 = new ItemUseContext(var1, var1.func_184586_b(var6), var3, var4, var8, var9, var10);
                  EnumActionResult var13;
                  if (this.field_78779_k.func_77145_d()) {
                     int var15 = var7.func_190916_E();
                     var13 = var7.func_196084_a(var14);
                     var7.func_190920_e(var15);
                  } else {
                     var13 = var7.func_196084_a(var14);
                  }

                  return var13;
               } else {
                  return EnumActionResult.PASS;
               }
            }
         }
      }
   }

   public EnumActionResult func_187101_a(EntityPlayer var1, World var2, EnumHand var3) {
      if (this.field_78779_k == GameType.SPECTATOR) {
         return EnumActionResult.PASS;
      } else {
         this.func_78750_j();
         this.field_78774_b.func_147297_a(new CPacketPlayerTryUseItem(var3));
         ItemStack var4 = var1.func_184586_b(var3);
         if (var1.func_184811_cZ().func_185141_a(var4.func_77973_b())) {
            return EnumActionResult.PASS;
         } else {
            int var5 = var4.func_190916_E();
            ActionResult var6 = var4.func_77957_a(var2, var1, var3);
            ItemStack var7 = (ItemStack)var6.func_188398_b();
            if (var7 != var4 || var7.func_190916_E() != var5) {
               var1.func_184611_a(var3, var7);
            }

            return var6.func_188397_a();
         }
      }
   }

   public EntityPlayerSP func_199681_a(World var1, StatisticsManager var2, RecipeBookClient var3) {
      return new EntityPlayerSP(this.field_78776_a, var1, this.field_78774_b, var2, var3);
   }

   public void func_78764_a(EntityPlayer var1, Entity var2) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new CPacketUseEntity(var2));
      if (this.field_78779_k != GameType.SPECTATOR) {
         var1.func_71059_n(var2);
         var1.func_184821_cY();
      }

   }

   public EnumActionResult func_187097_a(EntityPlayer var1, Entity var2, EnumHand var3) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new CPacketUseEntity(var2, var3));
      return this.field_78779_k == GameType.SPECTATOR ? EnumActionResult.PASS : var1.func_190775_a(var2, var3);
   }

   public EnumActionResult func_187102_a(EntityPlayer var1, Entity var2, RayTraceResult var3, EnumHand var4) {
      this.func_78750_j();
      Vec3d var5 = new Vec3d(var3.field_72307_f.field_72450_a - var2.field_70165_t, var3.field_72307_f.field_72448_b - var2.field_70163_u, var3.field_72307_f.field_72449_c - var2.field_70161_v);
      this.field_78774_b.func_147297_a(new CPacketUseEntity(var2, var4, var5));
      return this.field_78779_k == GameType.SPECTATOR ? EnumActionResult.PASS : var2.func_184199_a(var1, var5, var4);
   }

   public ItemStack func_187098_a(int var1, int var2, int var3, ClickType var4, EntityPlayer var5) {
      short var6 = var5.field_71070_bA.func_75136_a(var5.field_71071_by);
      ItemStack var7 = var5.field_71070_bA.func_184996_a(var2, var3, var4, var5);
      this.field_78774_b.func_147297_a(new CPacketClickWindow(var1, var2, var3, var4, var7, var6));
      return var7;
   }

   public void func_203413_a(int var1, IRecipe var2, boolean var3) {
      this.field_78774_b.func_147297_a(new CPacketPlaceRecipe(var1, var2, var3));
   }

   public void func_78756_a(int var1, int var2) {
      this.field_78774_b.func_147297_a(new CPacketEnchantItem(var1, var2));
   }

   public void func_78761_a(ItemStack var1, int var2) {
      if (this.field_78779_k.func_77145_d()) {
         this.field_78774_b.func_147297_a(new CPacketCreativeInventoryAction(var2, var1));
      }

   }

   public void func_78752_a(ItemStack var1) {
      if (this.field_78779_k.func_77145_d() && !var1.func_190926_b()) {
         this.field_78774_b.func_147297_a(new CPacketCreativeInventoryAction(-1, var1));
      }

   }

   public void func_78766_c(EntityPlayer var1) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.field_177992_a, EnumFacing.DOWN));
      var1.func_184597_cx();
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
      return this.field_78776_a.field_71439_g.func_184218_aH() && this.field_78776_a.field_71439_g.func_184187_bx() instanceof AbstractHorse;
   }

   public boolean func_178887_k() {
      return this.field_78779_k == GameType.SPECTATOR;
   }

   public GameType func_178889_l() {
      return this.field_78779_k;
   }

   public boolean func_181040_m() {
      return this.field_78778_j;
   }

   public void func_187100_a(int var1) {
      this.field_78774_b.func_147297_a(new CPacketPickItem(var1));
   }
}
