package net.minecraft.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.client.resources.model.WeightedBakedModel;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;

public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private BlockModelShapes field_175028_a;
   private final GameSettings field_175026_b;
   private final BlockModelRenderer field_175027_c = new BlockModelRenderer();
   private final ChestRenderer field_175024_d = new ChestRenderer();
   private final BlockFluidRenderer field_175025_e = new BlockFluidRenderer();

   public BlockRendererDispatcher(BlockModelShapes var1, GameSettings var2) {
      super();
      this.field_175028_a = var1;
      this.field_175026_b = var2;
   }

   public BlockModelShapes func_175023_a() {
      return this.field_175028_a;
   }

   public void func_175020_a(IBlockState var1, BlockPos var2, TextureAtlasSprite var3, IBlockAccess var4) {
      Block var5 = var1.func_177230_c();
      int var6 = var5.func_149645_b();
      if (var6 == 3) {
         var1 = var5.func_176221_a(var1, var4, var2);
         IBakedModel var7 = this.field_175028_a.func_178125_b(var1);
         IBakedModel var8 = (new SimpleBakedModel.Builder(var7, var3)).func_177645_b();
         this.field_175027_c.func_178259_a(var4, var8, var1, var2, Tessellator.func_178181_a().func_178180_c());
      }
   }

   public boolean func_175018_a(IBlockState var1, BlockPos var2, IBlockAccess var3, WorldRenderer var4) {
      try {
         int var5 = var1.func_177230_c().func_149645_b();
         if (var5 == -1) {
            return false;
         } else {
            switch(var5) {
            case 1:
               return this.field_175025_e.func_178270_a(var3, var1, var2, var4);
            case 2:
               return false;
            case 3:
               IBakedModel var9 = this.func_175022_a(var1, var3, var2);
               return this.field_175027_c.func_178259_a(var3, var9, var1, var2, var4);
            default:
               return false;
            }
         }
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.func_85055_a(var8, "Tesselating block in world");
         CrashReportCategory var7 = var6.func_85058_a("Block being tesselated");
         CrashReportCategory.func_180523_a(var7, var2, var1.func_177230_c(), var1.func_177230_c().func_176201_c(var1));
         throw new ReportedException(var6);
      }
   }

   public BlockModelRenderer func_175019_b() {
      return this.field_175027_c;
   }

   private IBakedModel func_175017_a(IBlockState var1, BlockPos var2) {
      IBakedModel var3 = this.field_175028_a.func_178125_b(var1);
      if (var2 != null && this.field_175026_b.field_178880_u && var3 instanceof WeightedBakedModel) {
         var3 = ((WeightedBakedModel)var3).func_177564_a(MathHelper.func_180186_a(var2));
      }

      return var3;
   }

   public IBakedModel func_175022_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      Block var4 = var1.func_177230_c();
      if (var2.func_175624_G() != WorldType.field_180272_g) {
         try {
            var1 = var4.func_176221_a(var1, var2, var3);
         } catch (Exception var6) {
         }
      }

      IBakedModel var5 = this.field_175028_a.func_178125_b(var1);
      if (var3 != null && this.field_175026_b.field_178880_u && var5 instanceof WeightedBakedModel) {
         var5 = ((WeightedBakedModel)var5).func_177564_a(MathHelper.func_180186_a(var3));
      }

      return var5;
   }

   public void func_175016_a(IBlockState var1, float var2) {
      int var3 = var1.func_177230_c().func_149645_b();
      if (var3 != -1) {
         switch(var3) {
         case 1:
         default:
            break;
         case 2:
            this.field_175024_d.func_178175_a(var1.func_177230_c(), var2);
            break;
         case 3:
            IBakedModel var4 = this.func_175017_a(var1, (BlockPos)null);
            this.field_175027_c.func_178266_a(var4, var1, var2, true);
         }

      }
   }

   public boolean func_175021_a(Block var1, int var2) {
      if (var1 == null) {
         return false;
      } else {
         int var3 = var1.func_149645_b();
         if (var3 == 3) {
            return false;
         } else {
            return var3 == 2;
         }
      }
   }

   public void func_110549_a(IResourceManager var1) {
      this.field_175025_e.func_178268_a();
   }
}
