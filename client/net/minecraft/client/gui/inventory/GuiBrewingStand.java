package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiBrewingStand extends GuiContainer {
   private static final ResourceLocation field_147014_u = new ResourceLocation("textures/gui/container/brewing_stand.png");
   private TileEntityBrewingStand field_147013_v;

   public GuiBrewingStand(InventoryPlayer var1, TileEntityBrewingStand var2) {
      super(new ContainerBrewingStand(var1, var2));
      this.field_147013_v = var2;
   }

   @Override
   protected void func_146979_b(int var1, int var2) {
      String var3 = this.field_147013_v.func_145818_k_() ? this.field_147013_v.func_145825_b() : I18n.func_135052_a(this.field_147013_v.func_145825_b());
      this.field_146289_q.func_78276_b(var3, this.field_146999_f / 2 - this.field_146289_q.func_78256_a(var3) / 2, 6, 4210752);
      this.field_146289_q.func_78276_b(I18n.func_135052_a("container.inventory"), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   @Override
   protected void func_146976_a(float var1, int var2, int var3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147014_u);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
      int var6 = this.field_147013_v.func_145935_i();
      if (var6 > 0) {
         int var7 = (int)(28.0F * (1.0F - (float)var6 / 400.0F));
         if (var7 > 0) {
            this.func_73729_b(var4 + 97, var5 + 16, 176, 0, 9, var7);
         }

         int var8 = var6 / 2 % 7;
         switch(var8) {
            case 0:
               var7 = 29;
               break;
            case 1:
               var7 = 24;
               break;
            case 2:
               var7 = 20;
               break;
            case 3:
               var7 = 16;
               break;
            case 4:
               var7 = 11;
               break;
            case 5:
               var7 = 6;
               break;
            case 6:
               var7 = 0;
         }

         if (var7 > 0) {
            this.func_73729_b(var4 + 65, var5 + 14 + 29 - var7, 185, 29 - var7, 12, var7);
         }
      }
   }
}
