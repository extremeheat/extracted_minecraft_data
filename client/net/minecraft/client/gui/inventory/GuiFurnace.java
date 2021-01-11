package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer {
   private static final ResourceLocation field_147087_u = new ResourceLocation("textures/gui/container/furnace.png");
   private final InventoryPlayer field_175383_v;
   private IInventory field_147086_v;

   public GuiFurnace(InventoryPlayer var1, IInventory var2) {
      super(new ContainerFurnace(var1, var2));
      this.field_175383_v = var1;
      this.field_147086_v = var2;
   }

   protected void func_146979_b(int var1, int var2) {
      String var3 = this.field_147086_v.func_145748_c_().func_150260_c();
      this.field_146289_q.func_78276_b(var3, this.field_146999_f / 2 - this.field_146289_q.func_78256_a(var3) / 2, 6, 4210752);
      this.field_146289_q.func_78276_b(this.field_175383_v.func_145748_c_().func_150260_c(), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147087_u);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      int var6;
      if (TileEntityFurnace.func_174903_a(this.field_147086_v)) {
         var6 = this.func_175382_i(13);
         this.func_73729_b(var4 + 56, var5 + 36 + 12 - var6, 176, 12 - var6, 14, var6 + 1);
      }

      var6 = this.func_175381_h(24);
      this.func_73729_b(var4 + 79, var5 + 34, 176, 14, var6 + 1, 16);
   }

   private int func_175381_h(int var1) {
      int var2 = this.field_147086_v.func_174887_a_(2);
      int var3 = this.field_147086_v.func_174887_a_(3);
      return var3 != 0 && var2 != 0 ? var2 * var1 / var3 : 0;
   }

   private int func_175382_i(int var1) {
      int var2 = this.field_147086_v.func_174887_a_(1);
      if (var2 == 0) {
         var2 = 200;
      }

      return this.field_147086_v.func_174887_a_(0) * var1 / var2;
   }
}
