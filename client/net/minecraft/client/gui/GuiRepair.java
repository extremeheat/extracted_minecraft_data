package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class GuiRepair extends GuiContainer implements IContainerListener {
   private static final ResourceLocation field_147093_u = new ResourceLocation("textures/gui/container/anvil.png");
   private final ContainerRepair field_147092_v;
   private GuiTextField field_147091_w;
   private final InventoryPlayer field_147094_x;

   public GuiRepair(InventoryPlayer var1, World var2) {
      super(new ContainerRepair(var1, var2, Minecraft.func_71410_x().field_71439_g));
      this.field_147094_x = var1;
      this.field_147092_v = (ContainerRepair)this.field_147002_h;
   }

   public IGuiEventListener getFocused() {
      return this.field_147091_w.func_146206_l() ? this.field_147091_w : null;
   }

   protected void func_73866_w_() {
      super.func_73866_w_();
      this.field_146297_k.field_195559_v.func_197967_a(true);
      int var1 = (this.field_146294_l - this.field_146999_f) / 2;
      int var2 = (this.field_146295_m - this.field_147000_g) / 2;
      this.field_147091_w = new GuiTextField(0, this.field_146289_q, var1 + 62, var2 + 24, 103, 12);
      this.field_147091_w.func_146193_g(-1);
      this.field_147091_w.func_146204_h(-1);
      this.field_147091_w.func_146185_a(false);
      this.field_147091_w.func_146203_f(35);
      this.field_147091_w.func_195609_a(this::func_195393_a);
      this.field_195124_j.add(this.field_147091_w);
      this.field_147002_h.func_82847_b(this);
      this.field_147002_h.func_75132_a(this);
   }

   public void func_175273_b(Minecraft var1, int var2, int var3) {
      String var4 = this.field_147091_w.func_146179_b();
      this.func_146280_a(var1, var2, var3);
      this.field_147091_w.func_146180_a(var4);
   }

   public void func_146281_b() {
      super.func_146281_b();
      this.field_146297_k.field_195559_v.func_197967_a(false);
      this.field_147002_h.func_82847_b(this);
   }

   protected void func_146979_b(int var1, int var2) {
      GlStateManager.func_179140_f();
      GlStateManager.func_179084_k();
      this.field_146289_q.func_211126_b(I18n.func_135052_a("container.repair"), 60.0F, 6.0F, 4210752);
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
            int var6 = this.field_146999_f - 8 - this.field_146289_q.func_78256_a(var5) - 2;
            boolean var7 = true;
            func_73734_a(var6 - 2, 67, this.field_146999_f - 8, 79, 1325400064);
            this.field_146289_q.func_175063_a(var5, (float)var6, 69.0F, var3);
         }
      }

      GlStateManager.func_179145_e();
   }

   private void func_195393_a(int var1, String var2) {
      if (!var2.isEmpty()) {
         String var3 = var2;
         Slot var4 = this.field_147092_v.func_75139_a(0);
         if (var4 != null && var4.func_75216_d() && !var4.func_75211_c().func_82837_s() && var2.equals(var4.func_75211_c().func_200301_q().getString())) {
            var3 = "";
         }

         this.field_147092_v.func_82850_a(var3);
         this.field_146297_k.field_71439_g.field_71174_a.func_147297_a(new CPacketRenameItem(var3));
      }
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      super.func_73863_a(var1, var2, var3);
      this.func_191948_b(var1, var2);
      GlStateManager.func_179140_f();
      GlStateManager.func_179084_k();
      this.field_147091_w.func_195608_a(var1, var2, var3);
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

   public void func_71110_a(Container var1, NonNullList<ItemStack> var2) {
      this.func_71111_a(var1, 0, var1.func_75139_a(0).func_75211_c());
   }

   public void func_71111_a(Container var1, int var2, ItemStack var3) {
      if (var2 == 0) {
         this.field_147091_w.func_146180_a(var3.func_190926_b() ? "" : var3.func_200301_q().getString());
         this.field_147091_w.func_146184_c(!var3.func_190926_b());
      }

   }

   public void func_71112_a(Container var1, int var2, int var3) {
   }

   public void func_175173_a(Container var1, IInventory var2) {
   }
}
