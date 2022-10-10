package net.minecraft.client.renderer;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemTransformVec3f;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;

public class ItemRenderer implements IResourceManagerReloadListener {
   public static final ResourceLocation field_110798_h = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   private static final Set<Item> field_195411_c;
   public float field_77023_b;
   private final ItemModelMesher field_175059_m;
   private final TextureManager field_175057_n;
   private final ItemColors field_184395_f;

   public ItemRenderer(TextureManager var1, ModelManager var2, ItemColors var3) {
      super();
      this.field_175057_n = var1;
      this.field_175059_m = new ItemModelMesher(var2);
      Iterator var4 = IRegistry.field_212630_s.iterator();

      while(var4.hasNext()) {
         Item var5 = (Item)var4.next();
         if (!field_195411_c.contains(var5)) {
            this.field_175059_m.func_199311_a(var5, new ModelResourceLocation(IRegistry.field_212630_s.func_177774_c(var5), "inventory"));
         }
      }

      this.field_184395_f = var3;
   }

   public ItemModelMesher func_175037_a() {
      return this.field_175059_m;
   }

   private void func_191961_a(IBakedModel var1, ItemStack var2) {
      this.func_191967_a(var1, -1, var2);
   }

   private void func_191965_a(IBakedModel var1, int var2) {
      this.func_191967_a(var1, var2, ItemStack.field_190927_a);
   }

   private void func_191967_a(IBakedModel var1, int var2, ItemStack var3) {
      Tessellator var4 = Tessellator.func_178181_a();
      BufferBuilder var5 = var4.func_178180_c();
      var5.func_181668_a(7, DefaultVertexFormats.field_176599_b);
      Random var6 = new Random();
      long var7 = 42L;
      EnumFacing[] var9 = EnumFacing.values();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         EnumFacing var12 = var9[var11];
         var6.setSeed(42L);
         this.func_191970_a(var5, var1.func_200117_a((IBlockState)null, var12, var6), var2, var3);
      }

      var6.setSeed(42L);
      this.func_191970_a(var5, var1.func_200117_a((IBlockState)null, (EnumFacing)null, var6), var2, var3);
      var4.func_78381_a();
   }

   public void func_180454_a(ItemStack var1, IBakedModel var2) {
      if (!var1.func_190926_b()) {
         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(-0.5F, -0.5F, -0.5F);
         if (var2.func_188618_c()) {
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179091_B();
            TileEntityItemStackRenderer.field_147719_a.func_179022_a(var1);
         } else {
            this.func_191961_a(var2, var1);
            if (var1.func_77962_s()) {
               func_211128_a(this.field_175057_n, () -> {
                  this.func_191965_a(var2, -8372020);
               }, 8);
            }
         }

         GlStateManager.func_179121_F();
      }
   }

   public static void func_211128_a(TextureManager var0, Runnable var1, int var2) {
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179143_c(514);
      GlStateManager.func_179140_f();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
      var0.func_110577_a(field_110798_h);
      GlStateManager.func_179128_n(5890);
      GlStateManager.func_179094_E();
      GlStateManager.func_179152_a((float)var2, (float)var2, (float)var2);
      float var3 = (float)(Util.func_211177_b() % 3000L) / 3000.0F / (float)var2;
      GlStateManager.func_179109_b(var3, 0.0F, 0.0F);
      GlStateManager.func_179114_b(-50.0F, 0.0F, 0.0F, 1.0F);
      var1.run();
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      GlStateManager.func_179152_a((float)var2, (float)var2, (float)var2);
      float var4 = (float)(Util.func_211177_b() % 4873L) / 4873.0F / (float)var2;
      GlStateManager.func_179109_b(-var4, 0.0F, 0.0F);
      GlStateManager.func_179114_b(10.0F, 0.0F, 0.0F, 1.0F);
      var1.run();
      GlStateManager.func_179121_F();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.func_179145_e();
      GlStateManager.func_179143_c(515);
      GlStateManager.func_179132_a(true);
      var0.func_110577_a(TextureMap.field_110575_b);
   }

   private void func_175038_a(BufferBuilder var1, BakedQuad var2) {
      Vec3i var3 = var2.func_178210_d().func_176730_m();
      var1.func_178975_e((float)var3.func_177958_n(), (float)var3.func_177956_o(), (float)var3.func_177952_p());
   }

   private void func_191969_a(BufferBuilder var1, BakedQuad var2, int var3) {
      var1.func_178981_a(var2.func_178209_a());
      var1.func_178968_d(var3);
      this.func_175038_a(var1, var2);
   }

   private void func_191970_a(BufferBuilder var1, List<BakedQuad> var2, int var3, ItemStack var4) {
      boolean var5 = var3 == -1 && !var4.func_190926_b();
      int var6 = 0;

      for(int var7 = var2.size(); var6 < var7; ++var6) {
         BakedQuad var8 = (BakedQuad)var2.get(var6);
         int var9 = var3;
         if (var5 && var8.func_178212_b()) {
            var9 = this.field_184395_f.func_186728_a(var4, var8.func_178211_c());
            var9 |= -16777216;
         }

         this.func_191969_a(var1, var8, var9);
      }

   }

   public boolean func_175050_a(ItemStack var1) {
      IBakedModel var2 = this.field_175059_m.func_178089_a(var1);
      return var2 == null ? false : var2.func_177556_c();
   }

   public void func_181564_a(ItemStack var1, ItemCameraTransforms.TransformType var2) {
      if (!var1.func_190926_b()) {
         IBakedModel var3 = this.func_204206_b(var1);
         this.func_184394_a(var1, var3, var2, false);
      }
   }

   public IBakedModel func_184393_a(ItemStack var1, @Nullable World var2, @Nullable EntityLivingBase var3) {
      IBakedModel var4 = this.field_175059_m.func_178089_a(var1);
      Item var5 = var1.func_77973_b();
      return !var5.func_185040_i() ? var4 : this.func_204207_a(var4, var1, var2, var3);
   }

   public IBakedModel func_204205_b(ItemStack var1, World var2, EntityLivingBase var3) {
      Item var5 = var1.func_77973_b();
      IBakedModel var4;
      if (var5 == Items.field_203184_eO) {
         var4 = this.field_175059_m.func_178083_a().func_174953_a(new ModelResourceLocation("minecraft:trident_in_hand#inventory"));
      } else {
         var4 = this.field_175059_m.func_178089_a(var1);
      }

      return !var5.func_185040_i() ? var4 : this.func_204207_a(var4, var1, var2, var3);
   }

   public IBakedModel func_204206_b(ItemStack var1) {
      return this.func_184393_a(var1, (World)null, (EntityLivingBase)null);
   }

   private IBakedModel func_204207_a(IBakedModel var1, ItemStack var2, @Nullable World var3, @Nullable EntityLivingBase var4) {
      IBakedModel var5 = var1.func_188617_f().func_209581_a(var1, var2, var3, var4);
      return var5 == null ? this.field_175059_m.func_178083_a().func_174951_a() : var5;
   }

   public void func_184392_a(ItemStack var1, EntityLivingBase var2, ItemCameraTransforms.TransformType var3, boolean var4) {
      if (!var1.func_190926_b() && var2 != null) {
         IBakedModel var5 = this.func_204205_b(var1, var2.field_70170_p, var2);
         this.func_184394_a(var1, var5, var3, var4);
      }
   }

   protected void func_184394_a(ItemStack var1, IBakedModel var2, ItemCameraTransforms.TransformType var3, boolean var4) {
      if (!var1.func_190926_b()) {
         this.field_175057_n.func_110577_a(TextureMap.field_110575_b);
         this.field_175057_n.func_110581_b(TextureMap.field_110575_b).func_174936_b(false, false);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179091_B();
         GlStateManager.func_179092_a(516, 0.1F);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.func_179094_E();
         ItemCameraTransforms var5 = var2.func_177552_f();
         ItemCameraTransforms.func_188034_a(var5.func_181688_b(var3), var4);
         if (this.func_183005_a(var5.func_181688_b(var3))) {
            GlStateManager.func_187407_a(GlStateManager.CullFace.FRONT);
         }

         this.func_180454_a(var1, var2);
         GlStateManager.func_187407_a(GlStateManager.CullFace.BACK);
         GlStateManager.func_179121_F();
         GlStateManager.func_179101_C();
         GlStateManager.func_179084_k();
         this.field_175057_n.func_110577_a(TextureMap.field_110575_b);
         this.field_175057_n.func_110581_b(TextureMap.field_110575_b).func_174935_a();
      }
   }

   private boolean func_183005_a(ItemTransformVec3f var1) {
      return var1.field_178363_d.func_195899_a() < 0.0F ^ var1.field_178363_d.func_195900_b() < 0.0F ^ var1.field_178363_d.func_195902_c() < 0.0F;
   }

   public void func_175042_a(ItemStack var1, int var2, int var3) {
      this.func_191962_a(var1, var2, var3, this.func_204206_b(var1));
   }

   protected void func_191962_a(ItemStack var1, int var2, int var3, IBakedModel var4) {
      GlStateManager.func_179094_E();
      this.field_175057_n.func_110577_a(TextureMap.field_110575_b);
      this.field_175057_n.func_110581_b(TextureMap.field_110575_b).func_174936_b(false, false);
      GlStateManager.func_179091_B();
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179147_l();
      GlStateManager.func_187401_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      this.func_180452_a(var2, var3, var4.func_177556_c());
      var4.func_177552_f().func_181689_a(ItemCameraTransforms.TransformType.GUI);
      this.func_180454_a(var1, var4);
      GlStateManager.func_179118_c();
      GlStateManager.func_179101_C();
      GlStateManager.func_179140_f();
      GlStateManager.func_179121_F();
      this.field_175057_n.func_110577_a(TextureMap.field_110575_b);
      this.field_175057_n.func_110581_b(TextureMap.field_110575_b).func_174935_a();
   }

   private void func_180452_a(int var1, int var2, boolean var3) {
      GlStateManager.func_179109_b((float)var1, (float)var2, 100.0F + this.field_77023_b);
      GlStateManager.func_179109_b(8.0F, 8.0F, 0.0F);
      GlStateManager.func_179152_a(1.0F, -1.0F, 1.0F);
      GlStateManager.func_179152_a(16.0F, 16.0F, 16.0F);
      if (var3) {
         GlStateManager.func_179145_e();
      } else {
         GlStateManager.func_179140_f();
      }

   }

   public void func_180450_b(ItemStack var1, int var2, int var3) {
      this.func_184391_a(Minecraft.func_71410_x().field_71439_g, var1, var2, var3);
   }

   public void func_184391_a(@Nullable EntityLivingBase var1, ItemStack var2, int var3, int var4) {
      if (!var2.func_190926_b()) {
         this.field_77023_b += 50.0F;

         try {
            this.func_191962_a(var2, var3, var4, this.func_184393_a(var2, (World)null, var1));
         } catch (Throwable var8) {
            CrashReport var6 = CrashReport.func_85055_a(var8, "Rendering item");
            CrashReportCategory var7 = var6.func_85058_a("Item being rendered");
            var7.func_189529_a("Item Type", () -> {
               return String.valueOf(var2.func_77973_b());
            });
            var7.func_189529_a("Item Damage", () -> {
               return String.valueOf(var2.func_77952_i());
            });
            var7.func_189529_a("Item NBT", () -> {
               return String.valueOf(var2.func_77978_p());
            });
            var7.func_189529_a("Item Foil", () -> {
               return String.valueOf(var2.func_77962_s());
            });
            throw new ReportedException(var6);
         }

         this.field_77023_b -= 50.0F;
      }
   }

   public void func_175030_a(FontRenderer var1, ItemStack var2, int var3, int var4) {
      this.func_180453_a(var1, var2, var3, var4, (String)null);
   }

   public void func_180453_a(FontRenderer var1, ItemStack var2, int var3, int var4, @Nullable String var5) {
      if (!var2.func_190926_b()) {
         if (var2.func_190916_E() != 1 || var5 != null) {
            String var6 = var5 == null ? String.valueOf(var2.func_190916_E()) : var5;
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            GlStateManager.func_179084_k();
            var1.func_175063_a(var6, (float)(var3 + 19 - 2 - var1.func_78256_a(var6)), (float)(var4 + 6 + 3), 16777215);
            GlStateManager.func_179147_l();
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
         }

         if (var2.func_77951_h()) {
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            GlStateManager.func_179090_x();
            GlStateManager.func_179118_c();
            GlStateManager.func_179084_k();
            Tessellator var13 = Tessellator.func_178181_a();
            BufferBuilder var7 = var13.func_178180_c();
            float var8 = (float)var2.func_77952_i();
            float var9 = (float)var2.func_77958_k();
            float var10 = Math.max(0.0F, (var9 - var8) / var9);
            int var11 = Math.round(13.0F - var8 * 13.0F / var9);
            int var12 = MathHelper.func_181758_c(var10 / 3.0F, 1.0F, 1.0F);
            this.func_181565_a(var7, var3 + 2, var4 + 13, 13, 2, 0, 0, 0, 255);
            this.func_181565_a(var7, var3 + 2, var4 + 13, var11, 1, var12 >> 16 & 255, var12 >> 8 & 255, var12 & 255, 255);
            GlStateManager.func_179147_l();
            GlStateManager.func_179141_d();
            GlStateManager.func_179098_w();
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
         }

         EntityPlayerSP var14 = Minecraft.func_71410_x().field_71439_g;
         float var15 = var14 == null ? 0.0F : var14.func_184811_cZ().func_185143_a(var2.func_77973_b(), Minecraft.func_71410_x().func_184121_ak());
         if (var15 > 0.0F) {
            GlStateManager.func_179140_f();
            GlStateManager.func_179097_i();
            GlStateManager.func_179090_x();
            Tessellator var16 = Tessellator.func_178181_a();
            BufferBuilder var17 = var16.func_178180_c();
            this.func_181565_a(var17, var3, var4 + MathHelper.func_76141_d(16.0F * (1.0F - var15)), 16, MathHelper.func_76123_f(16.0F * var15), 255, 255, 255, 127);
            GlStateManager.func_179098_w();
            GlStateManager.func_179145_e();
            GlStateManager.func_179126_j();
         }

      }
   }

   private void func_181565_a(BufferBuilder var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      var1.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      var1.func_181662_b((double)(var2 + 0), (double)(var3 + 0), 0.0D).func_181669_b(var6, var7, var8, var9).func_181675_d();
      var1.func_181662_b((double)(var2 + 0), (double)(var3 + var5), 0.0D).func_181669_b(var6, var7, var8, var9).func_181675_d();
      var1.func_181662_b((double)(var2 + var4), (double)(var3 + var5), 0.0D).func_181669_b(var6, var7, var8, var9).func_181675_d();
      var1.func_181662_b((double)(var2 + var4), (double)(var3 + 0), 0.0D).func_181669_b(var6, var7, var8, var9).func_181675_d();
      Tessellator.func_178181_a().func_78381_a();
   }

   public void func_195410_a(IResourceManager var1) {
      this.field_175059_m.func_178085_b();
   }

   static {
      field_195411_c = Sets.newHashSet(new Item[]{Items.field_190931_a});
   }
}
