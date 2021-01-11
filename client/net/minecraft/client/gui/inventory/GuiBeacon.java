package net.minecraft.client.gui.inventory;

import io.netty.buffer.Unpooled;
import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiBeacon extends GuiContainer {
   private static final Logger field_147026_u = LogManager.getLogger();
   private static final ResourceLocation field_147025_v = new ResourceLocation("textures/gui/container/beacon.png");
   private IInventory field_147024_w;
   private GuiBeacon.ConfirmButton field_147028_x;
   private boolean field_147027_y;

   public GuiBeacon(InventoryPlayer var1, IInventory var2) {
      super(new ContainerBeacon(var1, var2));
      this.field_147024_w = var2;
      this.field_146999_f = 230;
      this.field_147000_g = 219;
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      this.field_146292_n.add(this.field_147028_x = new GuiBeacon.ConfirmButton(-1, this.field_147003_i + 164, this.field_147009_r + 107));
      this.field_146292_n.add(new GuiBeacon.CancelButton(-2, this.field_147003_i + 190, this.field_147009_r + 107));
      this.field_147027_y = true;
      this.field_147028_x.field_146124_l = false;
   }

   public void func_73876_c() {
      super.func_73876_c();
      int var1 = this.field_147024_w.func_174887_a_(0);
      int var2 = this.field_147024_w.func_174887_a_(1);
      int var3 = this.field_147024_w.func_174887_a_(2);
      if (this.field_147027_y && var1 >= 0) {
         this.field_147027_y = false;

         int var5;
         int var6;
         int var7;
         int var8;
         GuiBeacon.PowerButton var9;
         for(int var4 = 0; var4 <= 2; ++var4) {
            var5 = TileEntityBeacon.field_146009_a[var4].length;
            var6 = var5 * 22 + (var5 - 1) * 2;

            for(var7 = 0; var7 < var5; ++var7) {
               var8 = TileEntityBeacon.field_146009_a[var4][var7].field_76415_H;
               var9 = new GuiBeacon.PowerButton(var4 << 8 | var8, this.field_147003_i + 76 + var7 * 24 - var6 / 2, this.field_147009_r + 22 + var4 * 25, var8, var4);
               this.field_146292_n.add(var9);
               if (var4 >= var1) {
                  var9.field_146124_l = false;
               } else if (var8 == var2) {
                  var9.func_146140_b(true);
               }
            }
         }

         byte var10 = 3;
         var5 = TileEntityBeacon.field_146009_a[var10].length + 1;
         var6 = var5 * 22 + (var5 - 1) * 2;

         for(var7 = 0; var7 < var5 - 1; ++var7) {
            var8 = TileEntityBeacon.field_146009_a[var10][var7].field_76415_H;
            var9 = new GuiBeacon.PowerButton(var10 << 8 | var8, this.field_147003_i + 167 + var7 * 24 - var6 / 2, this.field_147009_r + 47, var8, var10);
            this.field_146292_n.add(var9);
            if (var10 >= var1) {
               var9.field_146124_l = false;
            } else if (var8 == var3) {
               var9.func_146140_b(true);
            }
         }

         if (var2 > 0) {
            GuiBeacon.PowerButton var11 = new GuiBeacon.PowerButton(var10 << 8 | var2, this.field_147003_i + 167 + (var5 - 1) * 24 - var6 / 2, this.field_147009_r + 47, var2, var10);
            this.field_146292_n.add(var11);
            if (var10 >= var1) {
               var11.field_146124_l = false;
            } else if (var2 == var3) {
               var11.func_146140_b(true);
            }
         }
      }

      this.field_147028_x.field_146124_l = this.field_147024_w.func_70301_a(0) != null && var2 > 0;
   }

   protected void func_146284_a(GuiButton var1) {
      if (var1.field_146127_k == -2) {
         this.field_146297_k.func_147108_a((GuiScreen)null);
      } else if (var1.field_146127_k == -1) {
         String var2 = "MC|Beacon";
         PacketBuffer var3 = new PacketBuffer(Unpooled.buffer());
         var3.writeInt(this.field_147024_w.func_174887_a_(1));
         var3.writeInt(this.field_147024_w.func_174887_a_(2));
         this.field_146297_k.func_147114_u().func_147297_a(new C17PacketCustomPayload(var2, var3));
         this.field_146297_k.func_147108_a((GuiScreen)null);
      } else if (var1 instanceof GuiBeacon.PowerButton) {
         if (((GuiBeacon.PowerButton)var1).func_146141_c()) {
            return;
         }

         int var5 = var1.field_146127_k;
         int var6 = var5 & 255;
         int var4 = var5 >> 8;
         if (var4 < 3) {
            this.field_147024_w.func_174885_b(1, var6);
         } else {
            this.field_147024_w.func_174885_b(2, var6);
         }

         this.field_146292_n.clear();
         this.func_73866_w_();
         this.func_73876_c();
      }

   }

   protected void func_146979_b(int var1, int var2) {
      RenderHelper.func_74518_a();
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("tile.beacon.primary"), 62, 10, 14737632);
      this.func_73732_a(this.field_146289_q, I18n.func_135052_a("tile.beacon.secondary"), 169, 10, 14737632);
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

   class CancelButton extends GuiBeacon.Button {
      public CancelButton(int var2, int var3, int var4) {
         super(var2, var3, var4, GuiBeacon.field_147025_v, 112, 220);
      }

      public void func_146111_b(int var1, int var2) {
         GuiBeacon.this.func_146279_a(I18n.func_135052_a("gui.cancel"), var1, var2);
      }
   }

   class ConfirmButton extends GuiBeacon.Button {
      public ConfirmButton(int var2, int var3, int var4) {
         super(var2, var3, var4, GuiBeacon.field_147025_v, 90, 220);
      }

      public void func_146111_b(int var1, int var2) {
         GuiBeacon.this.func_146279_a(I18n.func_135052_a("gui.done"), var1, var2);
      }
   }

   class PowerButton extends GuiBeacon.Button {
      private final int field_146149_p;
      private final int field_146148_q;

      public PowerButton(int var2, int var3, int var4, int var5, int var6) {
         super(var2, var3, var4, GuiContainer.field_147001_a, 0 + Potion.field_76425_a[var5].func_76392_e() % 8 * 18, 198 + Potion.field_76425_a[var5].func_76392_e() / 8 * 18);
         this.field_146149_p = var5;
         this.field_146148_q = var6;
      }

      public void func_146111_b(int var1, int var2) {
         String var3 = I18n.func_135052_a(Potion.field_76425_a[this.field_146149_p].func_76393_a());
         if (this.field_146148_q >= 3 && this.field_146149_p != Potion.field_76428_l.field_76415_H) {
            var3 = var3 + " II";
         }

         GuiBeacon.this.func_146279_a(var3, var1, var2);
      }
   }

   static class Button extends GuiButton {
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

      public void func_146112_a(Minecraft var1, int var2, int var3) {
         if (this.field_146125_m) {
            var1.func_110434_K().func_110577_a(GuiBeacon.field_147025_v);
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            this.field_146123_n = var2 >= this.field_146128_h && var3 >= this.field_146129_i && var2 < this.field_146128_h + this.field_146120_f && var3 < this.field_146129_i + this.field_146121_g;
            short var4 = 219;
            int var5 = 0;
            if (!this.field_146124_l) {
               var5 += this.field_146120_f * 2;
            } else if (this.field_146142_r) {
               var5 += this.field_146120_f * 1;
            } else if (this.field_146123_n) {
               var5 += this.field_146120_f * 3;
            }

            this.func_73729_b(this.field_146128_h, this.field_146129_i, var5, var4, this.field_146120_f, this.field_146121_g);
            if (!GuiBeacon.field_147025_v.equals(this.field_146145_o)) {
               var1.func_110434_K().func_110577_a(this.field_146145_o);
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
