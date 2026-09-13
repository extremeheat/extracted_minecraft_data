package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class TileEntityRendererPiston extends TileEntitySpecialRenderer {
   private RenderBlocks field_147516_b;

   public TileEntityRendererPiston() {
      super();
   }

   public void func_147500_a(TileEntityPiston var1, double var2, double var4, double var6, float var8) {
      Block var9 = var1.func_145861_a();
      if (var9.func_149688_o() != Material.field_151579_a && !(var1.func_145860_a(var8) >= 1.0F)) {
         Tessellator var10 = Tessellator.field_78398_a;
         this.func_147499_a(TextureMap.field_110575_b);
         RenderHelper.func_74518_a();
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3042);
         GL11.glDisable(2884);
         if (Minecraft.func_71379_u()) {
            GL11.glShadeModel(7425);
         } else {
            GL11.glShadeModel(7424);
         }

         var10.func_78382_b();
         var10.func_78373_b(
            (double)((float)var2 - (float)var1.field_145851_c + var1.func_145865_b(var8)),
            (double)((float)var4 - (float)var1.field_145848_d + var1.func_145862_c(var8)),
            (double)((float)var6 - (float)var1.field_145849_e + var1.func_145859_d(var8))
         );
         var10.func_78386_a(1.0F, 1.0F, 1.0F);
         if (var9 == Blocks.field_150332_K && var1.func_145860_a(var8) < 0.5F) {
            this.field_147516_b.func_147750_a(var9, var1.field_145851_c, var1.field_145848_d, var1.field_145849_e, false);
         } else if (var1.func_145867_d() && !var1.func_145868_b()) {
            Blocks.field_150332_K.func_150086_a(((BlockPistonBase)var9).func_150073_e());
            this.field_147516_b
               .func_147750_a(Blocks.field_150332_K, var1.field_145851_c, var1.field_145848_d, var1.field_145849_e, var1.func_145860_a(var8) < 0.5F);
            Blocks.field_150332_K.func_150087_e();
            var10.func_78373_b(
               (double)((float)var2 - (float)var1.field_145851_c),
               (double)((float)var4 - (float)var1.field_145848_d),
               (double)((float)var6 - (float)var1.field_145849_e)
            );
            this.field_147516_b.func_147804_d(var9, var1.field_145851_c, var1.field_145848_d, var1.field_145849_e);
         } else {
            this.field_147516_b.func_147769_a(var9, var1.field_145851_c, var1.field_145848_d, var1.field_145849_e);
         }

         var10.func_78373_b(0.0, 0.0, 0.0);
         var10.func_78381_a();
         RenderHelper.func_74519_b();
      }
   }

   @Override
   public void func_147496_a(World var1) {
      this.field_147516_b = new RenderBlocks(var1);
   }
}
