package net.minecraft.client.multiplayer;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity$Action;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings$GameType;

public class PlayerControllerMP {
   private final Minecraft field_78776_a;
   private final NetHandlerPlayClient field_78774_b;
   private int field_78775_c = -1;
   private int field_78772_d = -1;
   private int field_78773_e = -1;
   private ItemStack field_85183_f;
   private float field_78770_f;
   private float field_78780_h;
   private int field_78781_i;
   private boolean field_78778_j;
   private WorldSettings$GameType field_78779_k = WorldSettings$GameType.SURVIVAL;
   private int field_78777_l;

   public PlayerControllerMP(Minecraft var1, NetHandlerPlayClient var2) {
      super();
      this.field_78776_a = var1;
      this.field_78774_b = var2;
   }

   public static void func_78744_a(Minecraft var0, PlayerControllerMP var1, int var2, int var3, int var4, int var5) {
      if (!var0.field_71441_e.func_72886_a(var0.field_71439_g, var2, var3, var4, var5)) {
         var1.func_78751_a(var2, var3, var4, var5);
      }
   }

   public void func_78748_a(EntityPlayer var1) {
      this.field_78779_k.func_77147_a(var1.field_71075_bZ);
   }

   public boolean func_78747_a() {
      return false;
   }

   public void func_78746_a(WorldSettings$GameType var1) {
      this.field_78779_k = var1;
      this.field_78779_k.func_77147_a(this.field_78776_a.field_71439_g.field_71075_bZ);
   }

   public void func_78745_b(EntityPlayer var1) {
      var1.field_70177_z = -180.0F;
   }

   public boolean func_78755_b() {
      return this.field_78779_k.func_77144_e();
   }

   public boolean func_78751_a(int var1, int var2, int var3, int var4) {
      if (this.field_78779_k.func_82752_c() && !this.field_78776_a.field_71439_g.func_82246_f(var1, var2, var3)) {
         return false;
      } else if (this.field_78779_k.func_77145_d()
         && this.field_78776_a.field_71439_g.func_70694_bm() != null
         && this.field_78776_a.field_71439_g.func_70694_bm().func_77973_b() instanceof ItemSword) {
         return false;
      } else {
         WorldClient var5 = this.field_78776_a.field_71441_e;
         Block var6 = var5.func_147439_a(var1, var2, var3);
         if (var6.func_149688_o() == Material.field_151579_a) {
            return false;
         } else {
            var5.func_72926_e(2001, var1, var2, var3, Block.func_149682_b(var6) + (var5.func_72805_g(var1, var2, var3) << 12));
            int var7 = var5.func_72805_g(var1, var2, var3);
            boolean var8 = var5.func_147468_f(var1, var2, var3);
            if (var8) {
               var6.func_149664_b(var5, var1, var2, var3, var7);
            }

            this.field_78772_d = -1;
            if (!this.field_78779_k.func_77145_d()) {
               ItemStack var9 = this.field_78776_a.field_71439_g.func_71045_bC();
               if (var9 != null) {
                  var9.func_150999_a(var5, var6, var1, var2, var3, this.field_78776_a.field_71439_g);
                  if (var9.field_77994_a == 0) {
                     this.field_78776_a.field_71439_g.func_71028_bD();
                  }
               }
            }

            return var8;
         }
      }
   }

   public void func_78743_b(int var1, int var2, int var3, int var4) {
      if (!this.field_78779_k.func_82752_c() || this.field_78776_a.field_71439_g.func_82246_f(var1, var2, var3)) {
         if (this.field_78779_k.func_77145_d()) {
            this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(0, var1, var2, var3, var4));
            func_78744_a(this.field_78776_a, this, var1, var2, var3, var4);
            this.field_78781_i = 5;
         } else if (!this.field_78778_j || !this.func_85182_a(var1, var2, var3)) {
            if (this.field_78778_j) {
               this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(1, this.field_78775_c, this.field_78772_d, this.field_78773_e, var4));
            }

            this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(0, var1, var2, var3, var4));
            Block var5 = this.field_78776_a.field_71441_e.func_147439_a(var1, var2, var3);
            boolean var6 = var5.func_149688_o() != Material.field_151579_a;
            if (var6 && this.field_78770_f == 0.0F) {
               var5.func_149699_a(this.field_78776_a.field_71441_e, var1, var2, var3, this.field_78776_a.field_71439_g);
            }

            if (var6 && var5.func_149737_a(this.field_78776_a.field_71439_g, this.field_78776_a.field_71439_g.field_70170_p, var1, var2, var3) >= 1.0F) {
               this.func_78751_a(var1, var2, var3, var4);
            } else {
               this.field_78778_j = true;
               this.field_78775_c = var1;
               this.field_78772_d = var2;
               this.field_78773_e = var3;
               this.field_85183_f = this.field_78776_a.field_71439_g.func_70694_bm();
               this.field_78770_f = 0.0F;
               this.field_78780_h = 0.0F;
               this.field_78776_a
                  .field_71441_e
                  .func_147443_d(
                     this.field_78776_a.field_71439_g.func_145782_y(),
                     this.field_78775_c,
                     this.field_78772_d,
                     this.field_78773_e,
                     (int)(this.field_78770_f * 10.0F) - 1
                  );
            }
         }
      }
   }

   public void func_78767_c() {
      if (this.field_78778_j) {
         this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(1, this.field_78775_c, this.field_78772_d, this.field_78773_e, -1));
      }

      this.field_78778_j = false;
      this.field_78770_f = 0.0F;
      this.field_78776_a
         .field_71441_e
         .func_147443_d(this.field_78776_a.field_71439_g.func_145782_y(), this.field_78775_c, this.field_78772_d, this.field_78773_e, -1);
   }

   public void func_78759_c(int var1, int var2, int var3, int var4) {
      this.func_78750_j();
      if (this.field_78781_i > 0) {
         --this.field_78781_i;
      } else if (this.field_78779_k.func_77145_d()) {
         this.field_78781_i = 5;
         this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(0, var1, var2, var3, var4));
         func_78744_a(this.field_78776_a, this, var1, var2, var3, var4);
      } else {
         if (this.func_85182_a(var1, var2, var3)) {
            Block var5 = this.field_78776_a.field_71441_e.func_147439_a(var1, var2, var3);
            if (var5.func_149688_o() == Material.field_151579_a) {
               this.field_78778_j = false;
               return;
            }

            this.field_78770_f += var5.func_149737_a(this.field_78776_a.field_71439_g, this.field_78776_a.field_71439_g.field_70170_p, var1, var2, var3);
            if (this.field_78780_h % 4.0F == 0.0F) {
               this.field_78776_a
                  .func_147118_V()
                  .func_147682_a(
                     new PositionedSoundRecord(
                        new ResourceLocation(var5.field_149762_H.func_150498_e()),
                        (var5.field_149762_H.func_150497_c() + 1.0F) / 8.0F,
                        var5.field_149762_H.func_150494_d() * 0.5F,
                        (float)var1 + 0.5F,
                        (float)var2 + 0.5F,
                        (float)var3 + 0.5F
                     )
                  );
            }

            ++this.field_78780_h;
            if (this.field_78770_f >= 1.0F) {
               this.field_78778_j = false;
               this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(2, var1, var2, var3, var4));
               this.func_78751_a(var1, var2, var3, var4);
               this.field_78770_f = 0.0F;
               this.field_78780_h = 0.0F;
               this.field_78781_i = 5;
            }

            this.field_78776_a
               .field_71441_e
               .func_147443_d(
                  this.field_78776_a.field_71439_g.func_145782_y(),
                  this.field_78775_c,
                  this.field_78772_d,
                  this.field_78773_e,
                  (int)(this.field_78770_f * 10.0F) - 1
               );
         } else {
            this.func_78743_b(var1, var2, var3, var4);
         }
      }
   }

   public float func_78757_d() {
      return this.field_78779_k.func_77145_d() ? 5.0F : 4.5F;
   }

   public void func_78765_e() {
      this.func_78750_j();
      if (this.field_78774_b.func_147298_b().func_150724_d()) {
         this.field_78774_b.func_147298_b().func_74428_b();
      } else if (this.field_78774_b.func_147298_b().func_150730_f() != null) {
         this.field_78774_b.func_147298_b().func_150729_e().func_147231_a(this.field_78774_b.func_147298_b().func_150730_f());
      } else {
         this.field_78774_b.func_147298_b().func_150729_e().func_147231_a(new ChatComponentText("Disconnected from server"));
      }
   }

   private boolean func_85182_a(int var1, int var2, int var3) {
      ItemStack var4 = this.field_78776_a.field_71439_g.func_70694_bm();
      boolean var5 = this.field_85183_f == null && var4 == null;
      if (this.field_85183_f != null && var4 != null) {
         var5 = var4.func_77973_b() == this.field_85183_f.func_77973_b()
            && ItemStack.func_77970_a(var4, this.field_85183_f)
            && (var4.func_77984_f() || var4.func_77960_j() == this.field_85183_f.func_77960_j());
      }

      return var1 == this.field_78775_c && var2 == this.field_78772_d && var3 == this.field_78773_e && var5;
   }

   private void func_78750_j() {
      int var1 = this.field_78776_a.field_71439_g.field_71071_by.field_70461_c;
      if (var1 != this.field_78777_l) {
         this.field_78777_l = var1;
         this.field_78774_b.func_147297_a(new C09PacketHeldItemChange(this.field_78777_l));
      }
   }

   public boolean func_78760_a(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7, Vec3 var8) {
      this.func_78750_j();
      float var9 = (float)var8.field_72450_a - (float)var4;
      float var10 = (float)var8.field_72448_b - (float)var5;
      float var11 = (float)var8.field_72449_c - (float)var6;
      boolean var12 = false;
      if ((!var1.func_70093_af() || var1.func_70694_bm() == null)
         && var2.func_147439_a(var4, var5, var6).func_149727_a(var2, var4, var5, var6, var1, var7, var9, var10, var11)) {
         var12 = true;
      }

      if (!var12 && var3 != null && var3.func_77973_b() instanceof ItemBlock) {
         ItemBlock var13 = (ItemBlock)var3.func_77973_b();
         if (!var13.func_150936_a(var2, var4, var5, var6, var7, var1, var3)) {
            return false;
         }
      }

      this.field_78774_b.func_147297_a(new C08PacketPlayerBlockPlacement(var4, var5, var6, var7, var1.field_71071_by.func_70448_g(), var9, var10, var11));
      if (var12) {
         return true;
      } else if (var3 == null) {
         return false;
      } else if (this.field_78779_k.func_77145_d()) {
         int var16 = var3.func_77960_j();
         int var14 = var3.field_77994_a;
         boolean var15 = var3.func_77943_a(var1, var2, var4, var5, var6, var7, var9, var10, var11);
         var3.func_77964_b(var16);
         var3.field_77994_a = var14;
         return var15;
      } else {
         return var3.func_77943_a(var1, var2, var4, var5, var6, var7, var9, var10, var11);
      }
   }

   public boolean func_78769_a(EntityPlayer var1, World var2, ItemStack var3) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, var1.field_71071_by.func_70448_g(), 0.0F, 0.0F, 0.0F));
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

   public EntityClientPlayerMP func_147493_a(World var1, StatFileWriter var2) {
      return new EntityClientPlayerMP(this.field_78776_a, var1, this.field_78776_a.func_110432_I(), this.field_78774_b, var2);
   }

   public void func_78764_a(EntityPlayer var1, Entity var2) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new C02PacketUseEntity(var2, C02PacketUseEntity$Action.ATTACK));
      var1.func_71059_n(var2);
   }

   public boolean func_78768_b(EntityPlayer var1, Entity var2) {
      this.func_78750_j();
      this.field_78774_b.func_147297_a(new C02PacketUseEntity(var2, C02PacketUseEntity$Action.INTERACT));
      return var1.func_70998_m(var2);
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
      this.field_78774_b.func_147297_a(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
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
}
