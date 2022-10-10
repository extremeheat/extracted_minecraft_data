package net.minecraft.client.renderer.debug;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugRendererWater implements DebugRenderer.IDebugRenderer {
   private final Minecraft field_188288_a;
   private EntityPlayer field_190062_b;
   private double field_190063_c;
   private double field_190064_d;
   private double field_190065_e;

   public DebugRendererWater(Minecraft var1) {
      super();
      this.field_188288_a = var1;
   }

   public void func_190060_a(float var1, long var2) {
      this.field_190062_b = this.field_188288_a.field_71439_g;
      this.field_190063_c = this.field_190062_b.field_70142_S + (this.field_190062_b.field_70165_t - this.field_190062_b.field_70142_S) * (double)var1;
      this.field_190064_d = this.field_190062_b.field_70137_T + (this.field_190062_b.field_70163_u - this.field_190062_b.field_70137_T) * (double)var1;
      this.field_190065_e = this.field_190062_b.field_70136_U + (this.field_190062_b.field_70161_v - this.field_190062_b.field_70136_U) * (double)var1;
      BlockPos var4 = this.field_188288_a.field_71439_g.func_180425_c();
      World var5 = this.field_188288_a.field_71439_g.field_70170_p;
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179131_c(0.0F, 1.0F, 0.0F, 0.75F);
      GlStateManager.func_179090_x();
      GlStateManager.func_187441_d(6.0F);
      Iterable var6 = BlockPos.func_177980_a(var4.func_177982_a(-10, -10, -10), var4.func_177982_a(10, 10, 10));
      Iterator var7 = var6.iterator();

      BlockPos var8;
      IFluidState var9;
      while(var7.hasNext()) {
         var8 = (BlockPos)var7.next();
         var9 = var5.func_204610_c(var8);
         if (var9.func_206884_a(FluidTags.field_206959_a)) {
            double var10 = (double)((float)var8.func_177956_o() + var9.func_206885_f());
            WorldRenderer.func_189696_b((new AxisAlignedBB((double)((float)var8.func_177958_n() + 0.01F), (double)((float)var8.func_177956_o() + 0.01F), (double)((float)var8.func_177952_p() + 0.01F), (double)((float)var8.func_177958_n() + 0.99F), var10, (double)((float)var8.func_177952_p() + 0.99F))).func_72317_d(-this.field_190063_c, -this.field_190064_d, -this.field_190065_e), 1.0F, 1.0F, 1.0F, 0.2F);
         }
      }

      var6 = BlockPos.func_177980_a(var4.func_177982_a(-10, -10, -10), var4.func_177982_a(10, 10, 10));
      var7 = var6.iterator();

      while(var7.hasNext()) {
         var8 = (BlockPos)var7.next();
         var9 = var5.func_204610_c(var8);
         if (var9.func_206884_a(FluidTags.field_206959_a)) {
            DebugRenderer.func_190076_a(String.valueOf(var9.func_206882_g()), (double)var8.func_177958_n() + 0.5D, (double)((float)var8.func_177956_o() + var9.func_206885_f()), (double)var8.func_177952_p() + 0.5D, var1, -16777216);
         }
      }

      GlStateManager.func_179098_w();
      GlStateManager.func_179084_k();
   }
}
