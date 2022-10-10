package net.minecraft.client.renderer;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.SimpleBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.IFluidState;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private final BlockModelShapes field_175028_a;
   private final BlockModelRenderer field_175027_c;
   private final ChestRenderer field_175024_d = new ChestRenderer();
   private final BlockFluidRenderer field_175025_e;
   private final Random field_195476_e = new Random();

   public BlockRendererDispatcher(BlockModelShapes var1, BlockColors var2) {
      super();
      this.field_175028_a = var1;
      this.field_175027_c = new BlockModelRenderer(var2);
      this.field_175025_e = new BlockFluidRenderer();
   }

   public BlockModelShapes func_175023_a() {
      return this.field_175028_a;
   }

   public void func_175020_a(IBlockState var1, BlockPos var2, TextureAtlasSprite var3, IWorldReader var4) {
      if (var1.func_185901_i() == EnumBlockRenderType.MODEL) {
         IBakedModel var5 = this.field_175028_a.func_178125_b(var1);
         long var6 = var1.func_209533_a(var2);
         IBakedModel var8 = (new SimpleBakedModel.Builder(var1, var5, var3, this.field_195476_e, var6)).func_177645_b();
         this.field_175027_c.func_199324_a(var4, var8, var1, var2, Tessellator.func_178181_a().func_178180_c(), true, this.field_195476_e, var6);
      }
   }

   public boolean func_195475_a(IBlockState var1, BlockPos var2, IWorldReader var3, BufferBuilder var4, Random var5) {
      try {
         EnumBlockRenderType var6 = var1.func_185901_i();
         if (var6 == EnumBlockRenderType.INVISIBLE) {
            return false;
         } else {
            switch(var6) {
            case MODEL:
               return this.field_175027_c.func_199324_a(var3, this.func_184389_a(var1), var1, var2, var4, true, var5, var1.func_209533_a(var2));
            case ENTITYBLOCK_ANIMATED:
               return false;
            default:
               return false;
            }
         }
      } catch (Throwable var9) {
         CrashReport var7 = CrashReport.func_85055_a(var9, "Tesselating block in world");
         CrashReportCategory var8 = var7.func_85058_a("Block being tesselated");
         CrashReportCategory.func_175750_a(var8, var2, var1);
         throw new ReportedException(var7);
      }
   }

   public boolean func_205318_a(BlockPos var1, IWorldReader var2, BufferBuilder var3, IFluidState var4) {
      try {
         return this.field_175025_e.func_205346_a(var2, var1, var3, var4);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.func_85055_a(var8, "Tesselating liquid in world");
         CrashReportCategory var7 = var6.func_85058_a("Block being tesselated");
         CrashReportCategory.func_175750_a(var7, var1, (IBlockState)null);
         throw new ReportedException(var6);
      }
   }

   public BlockModelRenderer func_175019_b() {
      return this.field_175027_c;
   }

   public IBakedModel func_184389_a(IBlockState var1) {
      return this.field_175028_a.func_178125_b(var1);
   }

   public void func_175016_a(IBlockState var1, float var2) {
      EnumBlockRenderType var3 = var1.func_185901_i();
      if (var3 != EnumBlockRenderType.INVISIBLE) {
         switch(var3) {
         case MODEL:
            IBakedModel var4 = this.func_184389_a(var1);
            this.field_175027_c.func_178266_a(var4, var1, var2, true);
            break;
         case ENTITYBLOCK_ANIMATED:
            this.field_175024_d.func_178175_a(var1.func_177230_c(), var2);
         }

      }
   }

   public void func_195410_a(IResourceManager var1) {
      this.field_175025_e.func_178268_a();
   }
}
