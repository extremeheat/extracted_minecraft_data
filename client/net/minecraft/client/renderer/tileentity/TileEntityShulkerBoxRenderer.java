package net.minecraft.client.renderer.tileentity;

import net.minecraft.block.BlockShulkerBox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderShulker;
import net.minecraft.client.renderer.entity.model.ModelShulker;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumFacing;

public class TileEntityShulkerBoxRenderer extends TileEntityRenderer<TileEntityShulkerBox> {
   private final ModelShulker field_191285_a;

   public TileEntityShulkerBoxRenderer(ModelShulker var1) {
      super();
      this.field_191285_a = var1;
   }

   public void func_199341_a(TileEntityShulkerBox var1, double var2, double var4, double var6, float var8, int var9) {
      EnumFacing var10 = EnumFacing.UP;
      if (var1.func_145830_o()) {
         IBlockState var11 = this.func_178459_a().func_180495_p(var1.func_174877_v());
         if (var11.func_177230_c() instanceof BlockShulkerBox) {
            var10 = (EnumFacing)var11.func_177229_b(BlockShulkerBox.field_190957_a);
         }
      }

      GlStateManager.func_179126_j();
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179129_p();
      if (var9 >= 0) {
         this.func_147499_a(field_178460_a[var9]);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(4.0F, 4.0F, 1.0F);
         GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.func_179128_n(5888);
      } else {
         EnumDyeColor var12 = var1.func_190592_s();
         if (var12 == null) {
            this.func_147499_a(RenderShulker.field_204402_a);
         } else {
            this.func_147499_a(RenderShulker.field_188342_a[var12.func_196059_a()]);
         }
      }

      GlStateManager.func_179094_E();
      GlStateManager.func_179091_B();
      if (var9 < 0) {
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.func_179109_b((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      GlStateManager.func_179152_a(1.0F, -1.0F, -1.0F);
      GlStateManager.func_179109_b(0.0F, 1.0F, 0.0F);
      float var13 = 0.9995F;
      GlStateManager.func_179152_a(0.9995F, 0.9995F, 0.9995F);
      GlStateManager.func_179109_b(0.0F, -1.0F, 0.0F);
      switch(var10) {
      case DOWN:
         GlStateManager.func_179109_b(0.0F, 2.0F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
      case UP:
      default:
         break;
      case NORTH:
         GlStateManager.func_179109_b(0.0F, 1.0F, 1.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(180.0F, 0.0F, 0.0F, 1.0F);
         break;
      case SOUTH:
         GlStateManager.func_179109_b(0.0F, 1.0F, -1.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.func_179109_b(-1.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
         break;
      case EAST:
         GlStateManager.func_179109_b(1.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
      }

      this.field_191285_a.func_205069_a().func_78785_a(0.0625F);
      GlStateManager.func_179109_b(0.0F, -var1.func_190585_a(var8) * 0.5F, 0.0F);
      GlStateManager.func_179114_b(270.0F * var1.func_190585_a(var8), 0.0F, 1.0F, 0.0F);
      this.field_191285_a.func_205068_b().func_78785_a(0.0625F);
      GlStateManager.func_179089_o();
      GlStateManager.func_179101_C();
      GlStateManager.func_179121_F();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      if (var9 >= 0) {
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
      }

   }
}
