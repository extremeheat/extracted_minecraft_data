package net.minecraft.server.management;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class DemoPlayerInteractionManager extends PlayerInteractionManager {
   private boolean field_73105_c;
   private boolean field_73103_d;
   private int field_73104_e;
   private int field_73102_f;

   public DemoPlayerInteractionManager(World var1) {
      super(var1);
   }

   public void func_73075_a() {
      super.func_73075_a();
      ++this.field_73102_f;
      long var1 = this.field_73092_a.func_82737_E();
      long var3 = var1 / 24000L + 1L;
      if (!this.field_73105_c && this.field_73102_f > 20) {
         this.field_73105_c = true;
         this.field_73090_b.field_71135_a.func_147359_a(new SPacketChangeGameState(5, 0.0F));
      }

      this.field_73103_d = var1 > 120500L;
      if (this.field_73103_d) {
         ++this.field_73104_e;
      }

      if (var1 % 24000L == 500L) {
         if (var3 <= 6L) {
            if (var3 == 6L) {
               this.field_73090_b.field_71135_a.func_147359_a(new SPacketChangeGameState(5, 104.0F));
            } else {
               this.field_73090_b.func_145747_a(new TextComponentTranslation("demo.day." + var3, new Object[0]));
            }
         }
      } else if (var3 == 1L) {
         if (var1 == 100L) {
            this.field_73090_b.field_71135_a.func_147359_a(new SPacketChangeGameState(5, 101.0F));
         } else if (var1 == 175L) {
            this.field_73090_b.field_71135_a.func_147359_a(new SPacketChangeGameState(5, 102.0F));
         } else if (var1 == 250L) {
            this.field_73090_b.field_71135_a.func_147359_a(new SPacketChangeGameState(5, 103.0F));
         }
      } else if (var3 == 5L && var1 % 24000L == 22000L) {
         this.field_73090_b.func_145747_a(new TextComponentTranslation("demo.day.warning", new Object[0]));
      }

   }

   private void func_73101_e() {
      if (this.field_73104_e > 100) {
         this.field_73090_b.func_145747_a(new TextComponentTranslation("demo.reminder", new Object[0]));
         this.field_73104_e = 0;
      }

   }

   public void func_180784_a(BlockPos var1, EnumFacing var2) {
      if (this.field_73103_d) {
         this.func_73101_e();
      } else {
         super.func_180784_a(var1, var2);
      }
   }

   public void func_180785_a(BlockPos var1) {
      if (!this.field_73103_d) {
         super.func_180785_a(var1);
      }
   }

   public boolean func_180237_b(BlockPos var1) {
      return this.field_73103_d ? false : super.func_180237_b(var1);
   }

   public EnumActionResult func_187250_a(EntityPlayer var1, World var2, ItemStack var3, EnumHand var4) {
      if (this.field_73103_d) {
         this.func_73101_e();
         return EnumActionResult.PASS;
      } else {
         return super.func_187250_a(var1, var2, var3, var4);
      }
   }

   public EnumActionResult func_187251_a(EntityPlayer var1, World var2, ItemStack var3, EnumHand var4, BlockPos var5, EnumFacing var6, float var7, float var8, float var9) {
      if (this.field_73103_d) {
         this.func_73101_e();
         return EnumActionResult.PASS;
      } else {
         return super.func_187251_a(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }
}
