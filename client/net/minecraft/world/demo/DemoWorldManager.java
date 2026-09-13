package net.minecraft.world.demo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager {
   private boolean field_73105_c;
   private boolean field_73103_d;
   private int field_73104_e;
   private int field_73102_f;

   public DemoWorldManager(World var1) {
      super(var1);
   }

   @Override
   public void func_73075_a() {
      super.func_73075_a();
      ++this.field_73102_f;
      long var1 = this.field_73092_a.func_82737_E();
      long var3 = var1 / 24000L + 1L;
      if (!this.field_73105_c && this.field_73102_f > 20) {
         this.field_73105_c = true;
         this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 0.0F));
      }

      this.field_73103_d = var1 > 120500L;
      if (this.field_73103_d) {
         ++this.field_73104_e;
      }

      if (var1 % 24000L == 500L) {
         if (var3 <= 6L) {
            this.field_73090_b.func_145747_a(new ChatComponentTranslation("demo.day." + var3));
         }
      } else if (var3 == 1L) {
         if (var1 == 100L) {
            this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 101.0F));
         } else if (var1 == 175L) {
            this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 102.0F));
         } else if (var1 == 250L) {
            this.field_73090_b.field_71135_a.func_147359_a(new S2BPacketChangeGameState(5, 103.0F));
         }
      } else if (var3 == 5L && var1 % 24000L == 22000L) {
         this.field_73090_b.func_145747_a(new ChatComponentTranslation("demo.day.warning"));
      }
   }

   private void func_73101_e() {
      if (this.field_73104_e > 100) {
         this.field_73090_b.func_145747_a(new ChatComponentTranslation("demo.reminder"));
         this.field_73104_e = 0;
      }
   }

   @Override
   public void func_73074_a(int var1, int var2, int var3, int var4) {
      if (this.field_73103_d) {
         this.func_73101_e();
      } else {
         super.func_73074_a(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_73082_a(int var1, int var2, int var3) {
      if (!this.field_73103_d) {
         super.func_73082_a(var1, var2, var3);
      }
   }

   @Override
   public boolean func_73084_b(int var1, int var2, int var3) {
      return this.field_73103_d ? false : super.func_73084_b(var1, var2, var3);
   }

   @Override
   public boolean func_73085_a(EntityPlayer var1, World var2, ItemStack var3) {
      if (this.field_73103_d) {
         this.func_73101_e();
         return false;
      } else {
         return super.func_73085_a(var1, var2, var3);
      }
   }

   @Override
   public boolean func_73078_a(EntityPlayer var1, World var2, ItemStack var3, int var4, int var5, int var6, int var7, float var8, float var9, float var10) {
      if (this.field_73103_d) {
         this.func_73101_e();
         return false;
      } else {
         return super.func_73078_a(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }
   }
}
