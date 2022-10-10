package net.minecraft.client.renderer.tileentity;

import java.util.Arrays;
import java.util.Comparator;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.model.ModelBed;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.state.properties.BedPart;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityBedRenderer extends TileEntityRenderer<TileEntityBed> {
   private static final ResourceLocation[] field_193848_a = (ResourceLocation[])Arrays.stream(EnumDyeColor.values()).sorted(Comparator.comparingInt(EnumDyeColor::func_196059_a)).map((var0) -> {
      return new ResourceLocation("textures/entity/bed/" + var0.func_176762_d() + ".png");
   }).toArray((var0) -> {
      return new ResourceLocation[var0];
   });
   private final ModelBed field_193849_d = new ModelBed();

   public TileEntityBedRenderer() {
      super();
   }

   public void func_199341_a(TileEntityBed var1, double var2, double var4, double var6, float var8, int var9) {
      if (var9 >= 0) {
         this.func_147499_a(field_178460_a[var9]);
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(4.0F, 4.0F, 1.0F);
         GlStateManager.func_179109_b(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.func_179128_n(5888);
      } else {
         ResourceLocation var10 = field_193848_a[var1.func_193048_a().func_196059_a()];
         if (var10 != null) {
            this.func_147499_a(var10);
         }
      }

      if (var1.func_145830_o()) {
         IBlockState var11 = var1.func_195044_w();
         this.func_199343_a(var11.func_177229_b(BlockBed.field_176472_a) == BedPart.HEAD, var2, var4, var6, (EnumFacing)var11.func_177229_b(BlockBed.field_185512_D));
      } else {
         this.func_199343_a(true, var2, var4, var6, EnumFacing.SOUTH);
         this.func_199343_a(false, var2, var4, var6 - 1.0D, EnumFacing.SOUTH);
      }

      if (var9 >= 0) {
         GlStateManager.func_179128_n(5890);
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5888);
      }

   }

   private void func_199343_a(boolean var1, double var2, double var4, double var6, EnumFacing var8) {
      this.field_193849_d.func_193769_a(var1);
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b((float)var2, (float)var4 + 0.5625F, (float)var6);
      GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179109_b(0.5F, 0.5F, 0.5F);
      GlStateManager.func_179114_b(180.0F + var8.func_185119_l(), 0.0F, 0.0F, 1.0F);
      GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
      GlStateManager.func_179091_B();
      this.field_193849_d.func_193771_b();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
   }
}
