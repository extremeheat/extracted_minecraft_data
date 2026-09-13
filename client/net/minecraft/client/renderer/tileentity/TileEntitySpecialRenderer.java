package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class TileEntitySpecialRenderer {
   protected TileEntityRendererDispatcher field_147501_a;

   public TileEntitySpecialRenderer() {
      super();
   }

   public abstract void func_147500_a(TileEntity var1, double var2, double var4, double var6, float var8);

   protected void func_147499_a(ResourceLocation var1) {
      TextureManager var2 = this.field_147501_a.field_147553_e;
      if (var2 != null) {
         var2.func_110577_a(var1);
      }
   }

   public void func_147497_a(TileEntityRendererDispatcher var1) {
      this.field_147501_a = var1;
   }

   public void func_147496_a(World var1) {
   }

   public FontRenderer func_147498_b() {
      return this.field_147501_a.func_147548_a();
   }
}
