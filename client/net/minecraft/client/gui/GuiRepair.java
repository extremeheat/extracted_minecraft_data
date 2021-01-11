package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.input.Keyboard;

public class GuiRepair extends GuiContainer implements ICrafting {
   private static final ResourceLocation field_147093_u = new ResourceLocation("textures/gui/container/anvil.png");
   private ContainerRepair field_147092_v;
   private GuiTextField field_147091_w;
   private InventoryPlayer field_147094_x;

   public GuiRepair(InventoryPlayer var1, World var2) {
      super(new ContainerRepair(var1, var2, Minecraft.func_71410_x().field_71439_g));
      this.field_147094_x = var1;
      this.field_147092_v = (ContainerRepair)this.field_147002_h;
   }

   public void func_73866_w_() {
      super.func_73866_w_();
      Keyboard.enableRepeatEvents(true);
      int var1 = (this.field_146294_l - this.field_146999_f) / 2;
      int var2 = (this.field_146295_m - this.field_147000_g) / 2;
      this.field_147091_w = new GuiTextField(0, this.field_146289_q, var1 + 62, var2 + 24, 103, 12);
      this.field_147091_w.func_146193_g(-1);
      this.field_147091_w.func_146204_h(-1);
      this.field_147091_w.func_146185_a(false);
      this.field_147091_w.func_146203_f(30);
      this.field_147002_h.func_82847_b(this);
      this.field_147002_h.func_75132_a(this);
   }

   public void func_146281_b() {
      super.func_146281_b();
      Keyboard.enableRepeatEvents(false);
      this.field_147002_h.func_82847_b(this);
   }

   protected void func_146979_b(int var1, int var2) {
      GlStateManager.func_179140_f();
      GlStateManager.func_179084_k();
      this.field_146289_q.func_78276_b(I18n.func_135052_a("container.repair"), 60, 6, 4210752);
      if (this.field_147092_v.field_82854_e > 0) {
         int var3 = 8453920;
         boolean var4 = true;
         String var5 = I18n.func_135052_a("container.repair.cost", this.field_147092_v.field_82854_e);
         if (this.field_147092_v.field_82854_e >= 40 && !this.field_146297_k.field_71439_g.field_71075_bZ.field_75098_d) {
            var5 = I18n.func_135052_a("container.repair.expensive");
            var3 = 16736352;
         } else if (!this.field_147092_v.func_75139_a(2).func_75216_d()) {
            var4 = false;
         } else if (!this.field_147092_v.func_75139_a(2).func_82869_a(this.field_147094_x.field_70458_d)) {
            var3 = 16736352;
         }

         if (var4) {
            int var6 = -16777216 | (var3 & 16579836) >> 2 | var3 & -16777216;
            int var7 = this.field_146999_f - 8 - this.field_146289_q.func_78256_a(var5);
            byte var8 = 67;
            if (this.field_146289_q.func_82883_a()) {
               func_73734_a(var7 - 3, var8 - 2, this.field_146999_f - 7, var8 + 10, -16777216);
               func_73734_a(var7 - 2, var8 - 1, this.field_146999_f - 8, var8 + 9, -12895429);
            } else {
               this.field_146289_q.func_78276_b(var5, var7, var8 + 1, var6);
               this.field_146289_q.func_78276_b(var5, var7 + 1, var8, var6);
               this.field_146289_q.func_78276_b(var5, var7 + 1, var8 + 1, var6);
            }

            this.field_146289_q.func_78276_b(var5, var7, var8, var3);
         }
      }

      GlStateManager.func_179145_e();
   }

   protected void func_73869_a(char var1, int var2) {
      if (this.field_147091_w.func_146201_a(var1, var2)) {
         this.func_147090_g();
      } else {
         super.func_73869_a(var1, var2);
      }

   }

   private void func_147090_g() {
      String var1 = this.field_147091_w.func_146179_b();
      Slot var2 = this.field_147092_v.func_75139_a(0);
      if (var2 != null && var2.func_75216_d() && !var2.func_75211_c().func_82837_s() && var1.equals(var2.func_75211_c().func_82833_r())) {
         var1 = "";
      }

      this.field_147092_v.func_82850_a(var1);
      this.field_146297_k.field_71439_g.field_71174_a.func_147297_a(new C17PacketCustomPayload("MC|ItemName", (new PacketBuffer(Unpooled.buffer())).func_180714_a(var1)));
   }

   protected void func_73864_a(int var1, int var2, int var3) {
      super.func_73864_a(var1, var2, var3);
      this.field_147091_w.func_146192_a(var1, var2, var3);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      super.func_73863_a(var1, var2, var3);
      GlStateManager.func_179140_f();
      GlStateManager.func_179084_k();
      this.field_147091_w.func_146194_f();
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147093_u);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      this.func_73729_b(var4 + 59, var5 + 20, 0, this.field_147000_g + (this.field_147092_v.func_75139_a(0).func_75216_d() ? 0 : 16), 110, 16);
      if ((this.field_147092_v.func_75139_a(0).func_75216_d() || this.field_147092_v.func_75139_a(1).func_75216_d()) && !this.field_147092_v.func_75139_a(2).func_75216_d()) {
         this.func_73729_b(var4 + 99, var5 + 45, this.field_146999_f, 0, 28, 21);
      }

   }

   public void func_71110_a(Container var1, List<ItemStack> var2) {
      this.func_71111_a(var1, 0, var1.func_75139_a(0).func_75211_c());
   }

   public void func_71111_a(Container var1, int var2, ItemStack var3) {
      if (var2 == 0) {
         this.field_147091_w.func_146180_a(var3 == null ? "" : var3.func_82833_r());
         this.field_147091_w.func_146184_c(var3 != null);
         if (var3 != null) {
            this.func_147090_g();
         }
      }

   }

   public void func_71112_a(Container var1, int var2, int var3) {
   }

   public void func_175173_a(Container var1, IInventory var2) {
   }
}
