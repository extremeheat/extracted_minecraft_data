package net.minecraft.client.gui.inventory;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiBeacon extends GuiContainer {
   private static final Logger field_147026_u = LogManager.getLogger();
   private static final ResourceLocation field_147025_v = new ResourceLocation("textures/gui/container/beacon.png");
   private final IInventory field_147024_w;
   private GuiBeacon.ConfirmButton field_147028_x;
   private boolean field_147027_y;

   public GuiBeacon(InventoryPlayer var1, IInventory var2) {
      super(new ContainerBeacon(var1, var2));
      this.field_147024_w = var2;
      this.field_146999_f = 230;
      this.field_147000_g = 219;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_147028_x = new GuiBeacon.ConfirmButton(-1, this.field_147003_i + 164, this.field_147009_r + 107);
      this.func_189646_b(this.field_147028_x);
      this.func_189646_b(new GuiBeacon.CancelButton(-2, this.field_147003_i + 190, this.field_147009_r + 107));
      this.field_147027_y = true;
      this.field_147028_x.field_146124_l = false;
   }

   public void func_73876_c() {
      super.func_73876_c();
      int var1 = this.field_147024_w.func_174887_a_(0);
      Potion var2 = Potion.func_188412_a(this.field_147024_w.func_174887_a_(1));
      Potion var3 = Potion.func_188412_a(this.field_147024_w.func_174887_a_(2));
      if (this.field_147027_y && var1 >= 0) {
         this.field_147027_y = false;
         int var4 = 100;

         int var6;
         int var7;
         int var8;
         Potion var9;
         GuiBeacon.PowerButton var10;
         for(int var5 = 0; var5 <= 2; ++var5) {
            var6 = TileEntityBeacon.field_146009_a[var5].length;
            var7 = var6 * 22 + (var6 - 1) * 2;

            for(var8 = 0; var8 < var6; ++var8) {
               var9 = TileEntityBeacon.field_146009_a[var5][var8];
               var10 = new GuiBeacon.PowerButton(var4++, this.field_147003_i + 76 + var8 * 24 - var7 / 2, this.field_147009_r + 22 + var5 * 25, var9, var5);
               this.func_189646_b(var10);
               if (var5 >= var1) {
                  var10.field_146124_l = false;
               } else if (var9 == var2) {
                  var10.func_146140_b(true);
               }
            }
         }

         boolean var11 = true;
         var6 = TileEntityBeacon.field_146009_a[3].length + 1;
         var7 = var6 * 22 + (var6 - 1) * 2;

         for(var8 = 0; var8 < var6 - 1; ++var8) {
            var9 = TileEntityBeacon.field_146009_a[3][var8];
            var10 = new GuiBeacon.PowerButton(var4++, this.field_147003_i + 167 + var8 * 24 - var7 / 2, this.field_147009_r + 47, var9, 3);
            this.func_189646_b(var10);
            if (3 >= var1) {
               var10.field_146124_l = false;
            } else if (var9 == var3) {
               var10.func_146140_b(true);
            }
         }

         if (var2 != null) {
            GuiBeacon.PowerButton var12 = new GuiBeacon.PowerButton(var4++, this.field_147003_i + 167 + (var6 - 1) * 24 - var7 / 2, this.field_147009_r + 47, var2, 3);
            this.func_189646_b(var12);
            if (3 >= var1) {
               var12.field_146124_l = false;
            } else if (var2 == var3) {
               var12.func_146140_b(true);
            }
         }
      }

      this.field_147028_x.field_146124_l = !this.field_147024_w.func_70301_a(0).func_190926_b() && var2 != null;
   }

   protected void func_146979_b(int var1, int var2) {
      RenderHelper.func_74518_a();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("block.minecraft.beacon.primary"), 62, 10, 14737632);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("block.minecraft.beacon.secondary"), 169, 10, 14737632);
      Iterator var3 = this.field_146292_n.iterator();

      while(var3.hasNext()) {
         GuiButton var4 = (GuiButton)var3.next();
         if (var4.func_146115_a()) {
            var4.func_146111_b(var1 - this.field_147003_i, var2 - this.field_147009_r);
            break;
         }
      }

      RenderHelper.func_74520_c();
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147025_v);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      this.field_146296_j.field_77023_b = 100.0F;
      this.field_146296_j.func_180450_b(new ItemStack(Items.field_151166_bC), var4 + 42, var5 + 109);
      this.field_146296_j.func_180450_b(new ItemStack(Items.field_151045_i), var4 + 42 + 22, var5 + 109);
      this.field_146296_j.func_180450_b(new ItemStack(Items.field_151043_k), var4 + 42 + 44, var5 + 109);
      this.field_146296_j.func_180450_b(new ItemStack(Items.field_151042_j), var4 + 42 + 66, var5 + 109);
      this.field_146296_j.field_77023_b = 0.0F;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      super.func_73863_a(var1, var2, var3);
      this.func_191948_b(var1, var2);
   }

   class CancelButton extends GuiBeacon.Button {
      public CancelButton(int var2, int var3, int var4) {
         super(var2, var3, var4, GuiBeacon.field_147025_v, 112, 220);
      }

      public void func_194829_a(double var1, double var3) {
         GuiBeacon.this.field_146297_k.field_71439_g.field_71174_a.func_147297_a(new CPacketCloseWindow(GuiBeacon.this.field_146297_k.field_71439_g.field_71070_bA.field_75152_c));
         GuiBeacon.this.field_146297_k.func_147108_a((GuiScreen)null);
      }

      public void func_146111_b(int var1, int var2) {
         GuiBeacon.this.func_146279_a(I18n.func_135052_a("gui.cancel"), var1, var2);
      }
   }

   class ConfirmButton extends GuiBeacon.Button {
      public ConfirmButton(int var2, int var3, int var4) {
         super(var2, var3, var4, GuiBeacon.field_147025_v, 90, 220);
      }

      public void func_194829_a(double var1, double var3) {
         GuiBeacon.this.field_146297_k.func_147114_u().func_147297_a(new CPacketUpdateBeacon(GuiBeacon.this.field_147024_w.func_174887_a_(1), GuiBeacon.this.field_147024_w.func_174887_a_(2)));
         GuiBeacon.this.field_146297_k.field_71439_g.field_71174_a.func_147297_a(new CPacketCloseWindow(GuiBeacon.this.field_146297_k.field_71439_g.field_71070_bA.field_75152_c));
         GuiBeacon.this.field_146297_k.func_147108_a((GuiScreen)null);
      }

      public void func_146111_b(int var1, int var2) {
         GuiBeacon.this.func_146279_a(I18n.func_135052_a("gui.done"), var1, var2);
      }
   }

   class PowerButton extends GuiBeacon.Button {
      private final Potion field_184066_p;
      private final int field_146148_q;

      public PowerButton(int var2, int var3, int var4, Potion var5, int var6) {
         super(var2, var3, var4, GuiContainer.field_147001_a, var5.func_76392_e() % 12 * 18, 198 + var5.func_76392_e() / 12 * 18);
         this.field_184066_p = var5;
         this.field_146148_q = var6;
      }

      public void func_194829_a(double var1, double var3) {
         if (!this.func_146141_c()) {
            int var5 = Potion.func_188409_a(this.field_184066_p);
            if (this.field_146148_q < 3) {
               GuiBeacon.this.field_147024_w.func_174885_b(1, var5);
            } else {
               GuiBeacon.this.field_147024_w.func_174885_b(2, var5);
            }

            GuiBeacon.this.field_146292_n.clear();
            GuiBeacon.this.field_195124_j.clear();
            GuiBeacon.this.func_73866_w_();
            GuiBeacon.this.func_73876_c();
         }
      }

      public void func_146111_b(int var1, int var2) {
         String var3 = I18n.func_135052_a(this.field_184066_p.func_76393_a());
         if (this.field_146148_q >= 3 && this.field_184066_p != MobEffects.field_76428_l) {
            var3 = var3 + " II";
         }

         GuiBeacon.this.func_146279_a(var3, var1, var2);
      }
   }

   abstract static class Button extends GuiButton {
      private final ResourceLocation field_146145_o;
      private final int field_146144_p;
      private final int field_146143_q;
      private boolean field_146142_r;

      protected Button(int var1, int var2, int var3, ResourceLocation var4, int var5, int var6) {
         super(var1, var2, var3, 22, 22, "");
         this.field_146145_o = var4;
         this.field_146144_p = var5;
         this.field_146143_q = var6;
      }

      public void func_194828_a(int var1, int var2, float var3) {
         if (this.field_146125_m) {
            Minecraft.func_71410_x().func_110434_K().func_110577_a(GuiBeacon.field_147025_v);
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = var1 >= this.field_146128_h && var2 >= this.field_146129_i && var1 < this.field_146128_h + this.field_146120_f && var2 < this.field_146129_i + this.field_146121_g;
            boolean var4 = true;
            int var5 = 0;
            if (!this.field_146124_l) {
               var5 += this.field_146120_f * 2;
            } else if (this.field_146142_r) {
               var5 += this.field_146120_f * 1;
            } else if (this.field_146123_n) {
               var5 += this.field_146120_f * 3;
            }

            this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, 219, this.field_146120_f, this.field_146121_g);
            if (!GuiBeacon.field_147025_v.equals(this.field_146145_o)) {
               Minecraft.func_71410_x().func_110434_K().func_110577_a(this.field_146145_o);
            }

            this.func_73729_b(this.field_146128_h + 2, this.field_146129_i + 2, this.field_146144_p, this.field_146143_q, 18, 18);
         }
      }

      public boolean func_146141_c() {
         return this.field_146142_r;
      }

      public void func_146140_b(boolean var1) {
         this.field_146142_r = var1;
      }
   }
}
