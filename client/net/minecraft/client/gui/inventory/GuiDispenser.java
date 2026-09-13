package net.minecraft.client.gui.inventory;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiDispenser extends GuiContainer {
   private static final ResourceLocation field_147088_v = new ResourceLocation("textures/gui/container/dispenser.png");
   public TileEntityDispenser field_147089_u;

   public GuiDispenser(InventoryPlayer var1, TileEntityDispenser var2) {
      super(new ContainerDispenser(var1, var2));
      this.field_147089_u = var2;
   }

   @Override
   protected void func_146979_b(int var1, int var2) {
      String var3 = this.field_147089_u.func_145818_k_() ? this.field_147089_u.func_145825_b() : I18n.func_135052_a(this.field_147089_u.func_145825_b());
      this.field_146289_q.func_78276_b(var3, this.field_146999_f / 2 - this.field_146289_q.func_78256_a(var3) / 2, 6, 4210752);
      this.field_146289_q.func_78276_b(I18n.func_135052_a("container.inventory"), 8, this.field_147000_g - 96 + 2, 4210752);
   }

   @Override
   protected void func_146976_a(float var1, int var2, int var3) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_146297_k.func_110434_K().func_110577_a(field_147088_v);
      int var4 = (this.field_146294_l - this.field_146999_f) / 2;
      int var5 = (this.field_146295_m - this.field_147000_g) / 2;
      this.func_73729_b(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
   }
}
