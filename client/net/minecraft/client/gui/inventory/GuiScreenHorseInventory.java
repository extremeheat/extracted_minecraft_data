package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory extends GuiContainer {
   private static final ResourceLocation field_147031_u = new ResourceLocation("textures/gui/container/horse.png");
   private IInventory field_147030_v;
   private IInventory field_147029_w;
   private EntityHorse field_147034_x;
   private float field_147033_y;
   private float field_147032_z;

   public GuiScreenHorseInventory(IInventory var1, IInventory var2, EntityHorse var3) {
      super(new ContainerHorseInventory(var1, var2, var3, Minecraft.func_71410_x().field_71439_g));
      this.field_147030_v = var1;
      this.field_147029_w = var2;
      this.field_147034_x = var3;
      this.field_146291_p = false;
   }

   protected void func_146979_b(int var1, int var2) {
      this.field_146289_q.func_78276_b(this.field_147029_w.func_145748_c_().func_150260_c(), 8, 6, 4210752);
      this.field_146289_q.func_78276_b(this.field_147030_v.func_145748_c_().func_150260_c(), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   protected void func_146976_a(float var1, int var2, int var3) {
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147031_u);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      if (this.field_147034_x.func_110261_ca()) {
         this.func_73729_b(var4 + 79, var5 + 17, 0, this.field_147000_g, 90, 54);
      }

      if (this.field_147034_x.func_110259_cr()) {
         this.func_73729_b(var4 + 7, var5 + 35, 0, this.field_147000_g + 54, 18, 18);
      }

      GuiInventory.func_147046_a(var4 + 51, var5 + 60, 17, (float)(var4 + 51) - this.field_147033_y, (float)(var5 + 75 - 50) - this.field_147032_z, this.field_147034_x);
   }

   public void func_73863_a(int var1, int var2, float var3) {
      this.field_147033_y = (float)var1;
      this.field_147032_z = (float)var2;
      super.func_73863_a(var1, var2, var3);
   }
}
