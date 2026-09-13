package net.minecraft.client.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C03PacketPlayer$C04PacketPlayerPosition;
import net.minecraft.network.play.client.C03PacketPlayer$C05PacketPlayerLook;
import net.minecraft.network.play.client.C03PacketPlayer$C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class EntityClientPlayerMP extends EntityPlayerSP {
   public final NetHandlerPlayClient field_71174_a;
   private final StatFileWriter field_146108_bO;
   private double field_71179_j;
   private double field_71177_cg;
   private double field_71178_ch;
   private double field_71175_ci;
   private float field_71176_cj;
   private float field_71172_ck;
   private boolean field_71173_cl;
   private boolean field_71170_cm;
   private boolean field_71171_cn;
   private int field_71168_co;
   private boolean field_71169_cp;
   private String field_142022_ce;

   public EntityClientPlayerMP(Minecraft var1, World var2, Session var3, NetHandlerPlayClient var4, StatFileWriter var5) {
      super(var1, var2, var3, 0);
      this.field_71174_a = var4;
      this.field_146108_bO = var5;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      return false;
   }

   @Override
   public void func_70691_i(float var1) {
   }

   @Override
   public void func_70078_a(Entity var1) {
      super.func_70078_a(var1);
      if (var1 instanceof EntityMinecart) {
         this.field_71159_c.func_147118_V().func_147682_a(new MovingSoundMinecartRiding(this, (EntityMinecart)var1));
      }
   }

   @Override
   public void func_70071_h_() {
      if (this.field_70170_p.func_72899_e(MathHelper.func_76128_c(this.field_70165_t), 0, MathHelper.func_76128_c(this.field_70161_v))) {
         super.func_70071_h_();
         if (this.func_70115_ae()) {
            this.field_71174_a.func_147297_a(new C03PacketPlayer$C05PacketPlayerLook(this.field_70177_z, this.field_70125_A, this.field_70122_E));
            this.field_71174_a
               .func_147297_a(new C0CPacketInput(this.field_70702_br, this.field_70701_bs, this.field_71158_b.field_78901_c, this.field_71158_b.field_78899_d));
         } else {
            this.func_71166_b();
         }
      }
   }

   public void func_71166_b() {
      boolean var1 = this.func_70051_ag();
      if (var1 != this.field_71171_cn) {
         if (var1) {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, 4));
         } else {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, 5));
         }

         this.field_71171_cn = var1;
      }

      boolean var2 = this.func_70093_af();
      if (var2 != this.field_71170_cm) {
         if (var2) {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, 1));
         } else {
            this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, 2));
         }

         this.field_71170_cm = var2;
      }

      double var3 = this.field_70165_t - this.field_71179_j;
      double var5 = this.field_70121_D.field_72338_b - this.field_71177_cg;
      double var7 = this.field_70161_v - this.field_71175_ci;
      double var9 = (double)(this.field_70177_z - this.field_71176_cj);
      double var11 = (double)(this.field_70125_A - this.field_71172_ck);
      boolean var13 = var3 * var3 + var5 * var5 + var7 * var7 > 9.0E-4 || this.field_71168_co >= 20;
      boolean var14 = var9 != 0.0 || var11 != 0.0;
      if (this.field_70154_o != null) {
         this.field_71174_a
            .func_147297_a(
               new C03PacketPlayer$C06PacketPlayerPosLook(
                  this.field_70159_w, -999.0, -999.0, this.field_70179_y, this.field_70177_z, this.field_70125_A, this.field_70122_E
               )
            );
         var13 = false;
      } else if (var13 && var14) {
         this.field_71174_a
            .func_147297_a(
               new C03PacketPlayer$C06PacketPlayerPosLook(
                  this.field_70165_t,
                  this.field_70121_D.field_72338_b,
                  this.field_70163_u,
                  this.field_70161_v,
                  this.field_70177_z,
                  this.field_70125_A,
                  this.field_70122_E
               )
            );
      } else if (var13) {
         this.field_71174_a
            .func_147297_a(
               new C03PacketPlayer$C04PacketPlayerPosition(
                  this.field_70165_t, this.field_70121_D.field_72338_b, this.field_70163_u, this.field_70161_v, this.field_70122_E
               )
            );
      } else if (var14) {
         this.field_71174_a.func_147297_a(new C03PacketPlayer$C05PacketPlayerLook(this.field_70177_z, this.field_70125_A, this.field_70122_E));
      } else {
         this.field_71174_a.func_147297_a(new C03PacketPlayer(this.field_70122_E));
      }

      ++this.field_71168_co;
      this.field_71173_cl = this.field_70122_E;
      if (var13) {
         this.field_71179_j = this.field_70165_t;
         this.field_71177_cg = this.field_70121_D.field_72338_b;
         this.field_71178_ch = this.field_70163_u;
         this.field_71175_ci = this.field_70161_v;
         this.field_71168_co = 0;
      }

      if (var14) {
         this.field_71176_cj = this.field_70177_z;
         this.field_71172_ck = this.field_70125_A;
      }
   }

   @Override
   public EntityItem func_71040_bB(boolean var1) {
      int var2 = var1 ? 3 : 4;
      this.field_71174_a.func_147297_a(new C07PacketPlayerDigging(var2, 0, 0, 0, 0));
      return null;
   }

   @Override
   protected void func_71012_a(EntityItem var1) {
   }

   public void func_71165_d(String var1) {
      this.field_71174_a.func_147297_a(new C01PacketChatMessage(var1));
   }

   @Override
   public void func_71038_i() {
      super.func_71038_i();
      this.field_71174_a.func_147297_a(new C0APacketAnimation(this, 1));
   }

   @Override
   public void func_71004_bE() {
      this.field_71174_a.func_147297_a(new C16PacketClientStatus(C16PacketClientStatus$EnumState.PERFORM_RESPAWN));
   }

   @Override
   protected void func_70665_d(DamageSource var1, float var2) {
      if (!this.func_85032_ar()) {
         this.func_70606_j(this.func_110143_aJ() - var2);
      }
   }

   @Override
   public void func_71053_j() {
      this.field_71174_a.func_147297_a(new C0DPacketCloseWindow(this.field_71070_bA.field_75152_c));
      this.func_92015_f();
   }

   public void func_92015_f() {
      this.field_71071_by.func_70437_b(null);
      super.func_71053_j();
   }

   @Override
   public void func_71150_b(float var1) {
      if (this.field_71169_cp) {
         super.func_71150_b(var1);
      } else {
         this.func_70606_j(var1);
         this.field_71169_cp = true;
      }
   }

   @Override
   public void func_71064_a(StatBase var1, int var2) {
      if (var1 != null) {
         if (var1.field_75972_f) {
            super.func_71064_a(var1, var2);
         }
      }
   }

   @Override
   public void func_71016_p() {
      this.field_71174_a.func_147297_a(new C13PacketPlayerAbilities(this.field_71075_bZ));
   }

   @Override
   protected void func_110318_g() {
      this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, 6, (int)(this.func_110319_bJ() * 100.0F)));
   }

   public void func_110322_i() {
      this.field_71174_a.func_147297_a(new C0BPacketEntityAction(this, 7));
   }

   public void func_142020_c(String var1) {
      this.field_142022_ce = var1;
   }

   public String func_142021_k() {
      return this.field_142022_ce;
   }

   public StatFileWriter func_146107_m() {
      return this.field_146108_bO;
   }
}
