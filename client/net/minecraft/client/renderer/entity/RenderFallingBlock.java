package net.minecraft.client.renderer.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.BlockDragonEgg;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public class RenderFallingBlock extends Render {
   private final RenderBlocks field_147920_a = new RenderBlocks();

   public RenderFallingBlock() {
      super();
      this.field_76989_e = 0.5F;
   }

   public void func_76986_a(EntityFallingBlock var1, double var2, double var4, double var6, float var8, float var9) {
      World var10 = var1.func_145807_e();
      Block var11 = var1.func_145805_f();
      int var12 = MathHelper.func_76128_c(var1.field_70165_t);
      int var13 = MathHelper.func_76128_c(var1.field_70163_u);
      int var14 = MathHelper.func_76128_c(var1.field_70161_v);
      if (var11 != null && var11 != var10.func_147439_a(var12, var13, var14)) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)var2, (float)var4, (float)var6);
         this.func_110777_b(var1);
         GL11.glDisable(2896);
         if (var11 instanceof BlockAnvil) {
            this.field_147920_a.field_147845_a = var10;
            Tessellator var15 = Tessellator.field_78398_a;
            var15.func_78382_b();
            var15.func_78373_b((double)((float)(-var12) - 0.5F), (double)((float)(-var13) - 0.5F), (double)((float)(-var14) - 0.5F));
            this.field_147920_a.func_147780_a((BlockAnvil)var11, var12, var13, var14, var1.field_145814_a);
            var15.func_78373_b(0.0, 0.0, 0.0);
            var15.func_78381_a();
         } else if (var11 instanceof BlockDragonEgg) {
            this.field_147920_a.field_147845_a = var10;
            Tessellator var16 = Tessellator.field_78398_a;
            var16.func_78382_b();
            var16.func_78373_b((double)((float)(-var12) - 0.5F), (double)((float)(-var13) - 0.5F), (double)((float)(-var14) - 0.5F));
            this.field_147920_a.func_147802_a((BlockDragonEgg)var11, var12, var13, var14);
            var16.func_78373_b(0.0, 0.0, 0.0);
            var16.func_78381_a();
         } else {
            this.field_147920_a.func_147775_a(var11);
            this.field_147920_a.func_147749_a(var11, var10, var12, var13, var14, var1.field_145814_a);
         }

         GL11.glEnable(2896);
         GL11.glPopMatrix();
      }
   }

   protected ResourceLocation func_110775_a(EntityFallingBlock var1) {
      return TextureMap.field_110575_b;
   }
}
