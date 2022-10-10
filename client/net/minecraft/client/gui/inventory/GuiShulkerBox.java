package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiShulkerBox extends GuiContainer {
   private static final ResourceLocation field_190778_u = new ResourceLocation("textures/gui/container/shulker_box.png");
   private final IInventory field_190779_v;
   private final InventoryPlayer field_190780_w;

   public GuiShulkerBox(InventoryPlayer var1, IInventory var2) {
      super(new ContainerShulkerBox(var1, var2, Minecraft.func_71410_x().field_71439_g));
      this.field_190780_w = var1;
      this.field_190779_v = var2;
      ++this.field_147000_g;
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.func_146276_q_();
      super.func_73863_a(var1, var2, var3);
      this.func_191948_b(var1, var2);
   }

   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_211126_b(this.field_190779_v.func_145748_c_().func_150254_d(), 8.0F, 6.0F, 4210752);
      this.field_146289_q.func_211126_b(this.field_190780_w.func_145748_c_().func_150254_d(), 8.0F, (float)(this.field_147000_g - 96 + 2), 4210752);
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_190778_u);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
   }
}
