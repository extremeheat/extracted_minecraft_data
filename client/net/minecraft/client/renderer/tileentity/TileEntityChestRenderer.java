package net.minecraft.client.renderer.tileentity;

import java.util.Calendar;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelChest;
import net.minecraft.client.renderer.entity.model.ModelLargeChest;
import net.minecraft.init.Blocks;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.IChestLid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityTrappedChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityChestRenderer<T extends TileEntity & IChestLid> extends TileEntityRenderer<T> {
   private static final ResourceLocation field_147507_b = new ResourceLocation("textures/entity/chest/trapped_double.png");
   private static final ResourceLocation field_147508_c = new ResourceLocation("textures/entity/chest/christmas_double.png");
   private static final ResourceLocation field_147505_d = new ResourceLocation("textures/entity/chest/normal_double.png");
   private static final ResourceLocation field_147506_e = new ResourceLocation("textures/entity/chest/trapped.png");
   private static final ResourceLocation field_147503_f = new ResourceLocation("textures/entity/chest/christmas.png");
   private static final ResourceLocation field_147504_g = new ResourceLocation("textures/entity/chest/normal.png");
   private static final ResourceLocation field_199348_i = new ResourceLocation("textures/entity/chest/ender.png");
   private final ModelChest field_147510_h = new ModelChest();
   private final ModelChest field_147511_i = new ModelLargeChest();
   private boolean field_147509_j;

   public TileEntityChestRenderer() {
      super();
      Calendar var1 = Calendar.getInstance();
      if (var1.get(2) + 1 == 12 && var1.get(5) >= 24 && var1.get(5) <= 26) {
         this.field_147509_j = true;
      }

   }

   public void func_199341_a(T var1, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.func_179126_j();
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179132_a(true);
      IBlockState var10 = var1.func_145830_o() ? var1.func_195044_w() : (IBlockState)Blocks.field_150486_ae.func_176223_P().func_206870_a(BlockChest.field_176459_a, EnumFacing.SOUTH);
      ChestType var11 = var10.func_196959_b(BlockChest.field_196314_b) ? (ChestType)var10.func_177229_b(BlockChest.field_196314_b) : ChestType.SINGLE;
      if (var11 != ChestType.LEFT) {
         boolean var12 = var11 != ChestType.SINGLE;
         ModelChest var13 = this.func_199347_a(var1, var9, var12);
         if (var9 >= 0) {
            GlStateManager.func_179128_n(5890);
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a(var12 ? 8.0F : 4.0F, 4.0F, 1.0F);
            GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.func_179128_n(5888);
         } else {
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179091_B();
         GlStateManager.func_179109_b((float)var2, (float)var4 + 1.0F, (float)var6 + 1.0F);
         GlStateManager.func_179152_a(1.0F, -1.0F, -1.0F);
         float var14 = ((EnumFacing)var10.func_177229_b(BlockChest.field_176459_a)).func_185119_l();
         if ((double)Math.abs(var14) > 1.0E-5D) {
            GlStateManager.func_179109_b(0.5F, 0.5F, 0.5F);
            GlStateManager.func_179114_b(var14, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
         }

         this.func_199346_a(var1, var8, var13);
         var13.func_78231_a();
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

   private ModelChest func_199347_a(T var1, int var2, boolean var3) {
      ResourceLocation var4;
      if (var2 >= 0) {
         var4 = field_178460_a[var2];
      } else if (this.field_147509_j) {
         var4 = var3 ? field_147508_c : field_147503_f;
      } else if (var1 instanceof TileEntityTrappedChest) {
         var4 = var3 ? field_147507_b : field_147506_e;
      } else if (var1 instanceof TileEntityEnderChest) {
         var4 = field_199348_i;
      } else {
         var4 = var3 ? field_147505_d : field_147504_g;
      }

      this.func_147499_a(var4);
      return var3 ? this.field_147511_i : this.field_147510_h;
   }

   private void func_199346_a(T var1, float var2, ModelChest var3) {
      float var4 = ((IChestLid)var1).func_195480_a(var2);
      var4 = 1.0F - var4;
      var4 = 1.0F - var4 * var4 * var4;
      var3.func_205058_b().field_78795_f = -(var4 * 1.5707964F);
   }
}
