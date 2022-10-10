package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.GameType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRenderer implements AutoCloseable, IResourceManagerReloadListener {
   private static final Logger field_147710_q = LogManager.getLogger();
   private static final ResourceLocation field_110924_q = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation field_110923_r = new ResourceLocation("textures/environment/snow.png");
   private final Minecraft field_78531_r;
   private final IResourceManager field_147711_ac;
   private final Random field_78537_ab = new Random();
   private float field_78530_s;
   public final FirstPersonRenderer field_78516_c;
   private final MapItemRenderer field_147709_v;
   private int field_78529_t;
   private Entity field_78528_u;
   private final float field_78490_B = 4.0F;
   private float field_78491_C = 4.0F;
   private float field_78507_R;
   private float field_78506_S;
   private float field_82831_U;
   private float field_82832_V;
   private boolean field_175074_C = true;
   private boolean field_175073_D = true;
   private long field_184374_E;
   private long field_78508_Y = Util.func_211177_b();
   private final LightTexture field_78513_d;
   private int field_78534_ac;
   private final float[] field_175076_N = new float[1024];
   private final float[] field_175077_O = new float[1024];
   private final FogRenderer field_205003_A;
   private boolean field_175078_W;
   private double field_78503_V = 1.0D;
   private double field_78502_W;
   private double field_78509_X;
   private ItemStack field_190566_ab;
   private int field_190567_ac;
   private float field_190568_ad;
   private float field_190569_ae;
   private ShaderGroup field_147707_d;
   private float field_203000_X;
   private float field_203001_Y;
   private static final ResourceLocation[] field_147712_ad = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json"), new ResourceLocation("shaders/post/creeper.json"), new ResourceLocation("shaders/post/spider.json")};
   public static final int field_147708_e;
   private int field_147713_ae;
   private boolean field_175083_ad;
   private int field_175084_ae;

   public GameRenderer(Minecraft var1, IResourceManager var2) {
      super();
      this.field_147713_ae = field_147708_e;
      this.field_78531_r = var1;
      this.field_147711_ac = var2;
      this.field_78516_c = var1.func_175597_ag();
      this.field_147709_v = new MapItemRenderer(var1.func_110434_K());
      this.field_78513_d = new LightTexture(this);
      this.field_205003_A = new FogRenderer(this);
      this.field_147707_d = null;

      for(int var3 = 0; var3 < 32; ++var3) {
         for(int var4 = 0; var4 < 32; ++var4) {
            float var5 = (float)(var4 - 16);
            float var6 = (float)(var3 - 16);
            float var7 = MathHelper.func_76129_c(var5 * var5 + var6 * var6);
            this.field_175076_N[var3 << 5 | var4] = -var6 / var7;
            this.field_175077_O[var3 << 5 | var4] = var5 / var7;
         }
      }

   }

   public void close() {
      this.field_78513_d.close();
      this.field_147709_v.close();
      this.func_181022_b();
   }

   public boolean func_147702_a() {
      return OpenGlHelper.field_148824_g && this.field_147707_d != null;
   }

   public void func_181022_b() {
      if (this.field_147707_d != null) {
         this.field_147707_d.close();
      }

      this.field_147707_d = null;
      this.field_147713_ae = field_147708_e;
   }

   public void func_175071_c() {
      this.field_175083_ad = !this.field_175083_ad;
   }

   public void func_175066_a(@Nullable Entity var1) {
      if (OpenGlHelper.field_148824_g) {
         if (this.field_147707_d != null) {
            this.field_147707_d.close();
         }

         this.field_147707_d = null;
         if (var1 instanceof EntityCreeper) {
            this.func_175069_a(new ResourceLocation("shaders/post/creeper.json"));
         } else if (var1 instanceof EntitySpider) {
            this.func_175069_a(new ResourceLocation("shaders/post/spider.json"));
         } else if (var1 instanceof EntityEnderman) {
            this.func_175069_a(new ResourceLocation("shaders/post/invert.json"));
         }

      }
   }

   private void func_175069_a(ResourceLocation var1) {
      if (this.field_147707_d != null) {
         this.field_147707_d.close();
      }

      try {
         this.field_147707_d = new ShaderGroup(this.field_78531_r.func_110434_K(), this.field_147711_ac, this.field_78531_r.func_147110_a(), var1);
         this.field_147707_d.func_148026_a(this.field_78531_r.field_195558_d.func_198109_k(), this.field_78531_r.field_195558_d.func_198091_l());
         this.field_175083_ad = true;
      } catch (IOException var3) {
         field_147710_q.warn("Failed to load shader: {}", var1, var3);
         this.field_147713_ae = field_147708_e;
         this.field_175083_ad = false;
      } catch (JsonSyntaxException var4) {
         field_147710_q.warn("Failed to load shader: {}", var1, var4);
         this.field_147713_ae = field_147708_e;
         this.field_175083_ad = false;
      }

   }

   public void func_195410_a(IResourceManager var1) {
      if (this.field_147707_d != null) {
         this.field_147707_d.close();
      }

      this.field_147707_d = null;
      if (this.field_147713_ae == field_147708_e) {
         this.func_175066_a(this.field_78531_r.func_175606_aa());
      } else {
         this.func_175069_a(field_147712_ad[this.field_147713_ae]);
      }

   }

   public void func_78464_a() {
      if (OpenGlHelper.field_148824_g && ShaderLinkHelper.func_148074_b() == null) {
         ShaderLinkHelper.func_148076_a();
      }

      this.func_78477_e();
      this.field_78513_d.func_205107_a();
      this.field_78491_C = 4.0F;
      if (this.field_78531_r.func_175606_aa() == null) {
         this.field_78531_r.func_175607_a(this.field_78531_r.field_71439_g);
      }

      this.field_203001_Y = this.field_203000_X;
      this.field_203000_X += (this.field_78531_r.func_175606_aa().func_70047_e() - this.field_203000_X) * 0.5F;
      ++this.field_78529_t;
      this.field_78516_c.func_78441_a();
      this.func_78484_h();
      this.field_82832_V = this.field_82831_U;
      if (this.field_78531_r.field_71456_v.func_184046_j().func_184053_e()) {
         this.field_82831_U += 0.05F;
         if (this.field_82831_U > 1.0F) {
            this.field_82831_U = 1.0F;
         }
      } else if (this.field_82831_U > 0.0F) {
         this.field_82831_U -= 0.0125F;
      }

      if (this.field_190567_ac > 0) {
         --this.field_190567_ac;
         if (this.field_190567_ac == 0) {
            this.field_190566_ab = null;
         }
      }

   }

   public ShaderGroup func_147706_e() {
      return this.field_147707_d;
   }

   public void func_147704_a(int var1, int var2) {
      if (OpenGlHelper.field_148824_g) {
         if (this.field_147707_d != null) {
            this.field_147707_d.func_148026_a(var1, var2);
         }

         this.field_78531_r.field_71438_f.func_72720_a(var1, var2);
      }
   }

   public void func_78473_a(float var1) {
      Entity var2 = this.field_78531_r.func_175606_aa();
      if (var2 != null) {
         if (this.field_78531_r.field_71441_e != null) {
            this.field_78531_r.field_71424_I.func_76320_a("pick");
            this.field_78531_r.field_147125_j = null;
            double var3 = (double)this.field_78531_r.field_71442_b.func_78757_d();
            this.field_78531_r.field_71476_x = var2.func_174822_a(var3, var1, RayTraceFluidMode.NEVER);
            Vec3d var5 = var2.func_174824_e(var1);
            boolean var6 = false;
            boolean var7 = true;
            double var8 = var3;
            if (this.field_78531_r.field_71442_b.func_78749_i()) {
               var8 = 6.0D;
               var3 = var8;
            } else {
               if (var3 > 3.0D) {
                  var6 = true;
               }

               var3 = var3;
            }

            if (this.field_78531_r.field_71476_x != null) {
               var8 = this.field_78531_r.field_71476_x.field_72307_f.func_72438_d(var5);
            }

            Vec3d var10 = var2.func_70676_i(1.0F);
            Vec3d var11 = var5.func_72441_c(var10.field_72450_a * var3, var10.field_72448_b * var3, var10.field_72449_c * var3);
            this.field_78528_u = null;
            Vec3d var12 = null;
            float var13 = 1.0F;
            List var14 = this.field_78531_r.field_71441_e.func_175674_a(var2, var2.func_174813_aQ().func_72321_a(var10.field_72450_a * var3, var10.field_72448_b * var3, var10.field_72449_c * var3).func_72314_b(1.0D, 1.0D, 1.0D), EntitySelectors.field_180132_d.and(Entity::func_70067_L));
            double var15 = var8;

            for(int var17 = 0; var17 < var14.size(); ++var17) {
               Entity var18 = (Entity)var14.get(var17);
               AxisAlignedBB var19 = var18.func_174813_aQ().func_186662_g((double)var18.func_70111_Y());
               RayTraceResult var20 = var19.func_72327_a(var5, var11);
               if (var19.func_72318_a(var5)) {
                  if (var15 >= 0.0D) {
                     this.field_78528_u = var18;
                     var12 = var20 == null ? var5 : var20.field_72307_f;
                     var15 = 0.0D;
                  }
               } else if (var20 != null) {
                  double var21 = var5.func_72438_d(var20.field_72307_f);
                  if (var21 < var15 || var15 == 0.0D) {
                     if (var18.func_184208_bv() == var2.func_184208_bv()) {
                        if (var15 == 0.0D) {
                           this.field_78528_u = var18;
                           var12 = var20.field_72307_f;
                        }
                     } else {
                        this.field_78528_u = var18;
                        var12 = var20.field_72307_f;
                        var15 = var21;
                     }
                  }
               }
            }

            if (this.field_78528_u != null && var6 && var5.func_72438_d(var12) > 3.0D) {
               this.field_78528_u = null;
               this.field_78531_r.field_71476_x = new RayTraceResult(RayTraceResult.Type.MISS, var12, (EnumFacing)null, new BlockPos(var12));
            }

            if (this.field_78528_u != null && (var15 < var8 || this.field_78531_r.field_71476_x == null)) {
               this.field_78531_r.field_71476_x = new RayTraceResult(this.field_78528_u, var12);
               if (this.field_78528_u instanceof EntityLivingBase || this.field_78528_u instanceof EntityItemFrame) {
                  this.field_78531_r.field_147125_j = this.field_78528_u;
               }
            }

            this.field_78531_r.field_71424_I.func_76319_b();
         }
      }
   }

   private void func_78477_e() {
      float var1 = 1.0F;
      if (this.field_78531_r.func_175606_aa() instanceof AbstractClientPlayer) {
         AbstractClientPlayer var2 = (AbstractClientPlayer)this.field_78531_r.func_175606_aa();
         var1 = var2.func_175156_o();
      }

      this.field_78506_S = this.field_78507_R;
      this.field_78507_R += (var1 - this.field_78507_R) * 0.5F;
      if (this.field_78507_R > 1.5F) {
         this.field_78507_R = 1.5F;
      }

      if (this.field_78507_R < 0.1F) {
         this.field_78507_R = 0.1F;
      }

   }

   private double func_195459_a(float var1, boolean var2) {
      if (this.field_175078_W) {
         return 90.0D;
      } else {
         Entity var3 = this.field_78531_r.func_175606_aa();
         double var4 = 70.0D;
         if (var2) {
            var4 = this.field_78531_r.field_71474_y.field_74334_X;
            var4 *= (double)(this.field_78506_S + (this.field_78507_R - this.field_78506_S) * var1);
         }

         if (var3 instanceof EntityLivingBase && ((EntityLivingBase)var3).func_110143_aJ() <= 0.0F) {
            float var6 = (float)((EntityLivingBase)var3).field_70725_aQ + var1;
            var4 /= (double)((1.0F - 500.0F / (var6 + 500.0F)) * 2.0F + 1.0F);
         }

         IFluidState var7 = ActiveRenderInfo.func_206243_b(this.field_78531_r.field_71441_e, var3, var1);
         if (!var7.func_206888_e()) {
            var4 = var4 * 60.0D / 70.0D;
         }

         return var4;
      }
   }

   private void func_78482_e(float var1) {
      if (this.field_78531_r.func_175606_aa() instanceof EntityLivingBase) {
         EntityLivingBase var2 = (EntityLivingBase)this.field_78531_r.func_175606_aa();
         float var3 = (float)var2.field_70737_aN - var1;
         float var4;
         if (var2.func_110143_aJ() <= 0.0F) {
            var4 = (float)var2.field_70725_aQ + var1;
            GlStateManager.func_179114_b(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (var3 < 0.0F) {
            return;
         }

         var3 /= (float)var2.field_70738_aO;
         var3 = MathHelper.func_76126_a(var3 * var3 * var3 * var3 * 3.1415927F);
         var4 = var2.field_70739_aP;
         GlStateManager.func_179114_b(-var4, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(var4, 0.0F, 1.0F, 0.0F);
      }

   }

   private void func_78475_f(float var1) {
      if (this.field_78531_r.func_175606_aa() instanceof EntityPlayer) {
         EntityPlayer var2 = (EntityPlayer)this.field_78531_r.func_175606_aa();
         float var3 = var2.field_70140_Q - var2.field_70141_P;
         float var4 = -(var2.field_70140_Q + var3 * var1);
         float var5 = var2.field_71107_bF + (var2.field_71109_bG - var2.field_71107_bF) * var1;
         float var6 = var2.field_70727_aS + (var2.field_70726_aT - var2.field_70727_aS) * var1;
         GlStateManager.func_179109_b(MathHelper.func_76126_a(var4 * 3.1415927F) * var5 * 0.5F, -Math.abs(MathHelper.func_76134_b(var4 * 3.1415927F) * var5), 0.0F);
         GlStateManager.func_179114_b(MathHelper.func_76126_a(var4 * 3.1415927F) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.func_179114_b(Math.abs(MathHelper.func_76134_b(var4 * 3.1415927F - 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(var6, 1.0F, 0.0F, 0.0F);
      }
   }

   private void func_78467_g(float var1) {
      Entity var2 = this.field_78531_r.func_175606_aa();
      float var3 = this.field_203001_Y + (this.field_203000_X - this.field_203001_Y) * var1;
      double var4 = var2.field_70169_q + (var2.field_70165_t - var2.field_70169_q) * (double)var1;
      double var6 = var2.field_70167_r + (var2.field_70163_u - var2.field_70167_r) * (double)var1 + (double)var2.func_70047_e();
      double var8 = var2.field_70166_s + (var2.field_70161_v - var2.field_70166_s) * (double)var1;
      if (var2 instanceof EntityLivingBase && ((EntityLivingBase)var2).func_70608_bn()) {
         var3 = (float)((double)var3 + 1.0D);
         GlStateManager.func_179109_b(0.0F, 0.3F, 0.0F);
         if (!this.field_78531_r.field_71474_y.field_74325_U) {
            BlockPos var27 = new BlockPos(var2);
            IBlockState var11 = this.field_78531_r.field_71441_e.func_180495_p(var27);
            Block var28 = var11.func_177230_c();
            if (var28 instanceof BlockBed) {
               GlStateManager.func_179114_b(((EnumFacing)var11.func_177229_b(BlockBed.field_185512_D)).func_185119_l(), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.func_179114_b(var2.field_70126_B + (var2.field_70177_z - var2.field_70126_B) * var1 + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.func_179114_b(var2.field_70127_C + (var2.field_70125_A - var2.field_70127_C) * var1, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.field_78531_r.field_71474_y.field_74320_O > 0) {
         double var10 = (double)(this.field_78491_C + (4.0F - this.field_78491_C) * var1);
         if (this.field_78531_r.field_71474_y.field_74325_U) {
            GlStateManager.func_179109_b(0.0F, 0.0F, (float)(-var10));
         } else {
            float var12 = var2.field_70177_z;
            float var13 = var2.field_70125_A;
            if (this.field_78531_r.field_71474_y.field_74320_O == 2) {
               var13 += 180.0F;
            }

            double var14 = (double)(-MathHelper.func_76126_a(var12 * 0.017453292F) * MathHelper.func_76134_b(var13 * 0.017453292F)) * var10;
            double var16 = (double)(MathHelper.func_76134_b(var12 * 0.017453292F) * MathHelper.func_76134_b(var13 * 0.017453292F)) * var10;
            double var18 = (double)(-MathHelper.func_76126_a(var13 * 0.017453292F)) * var10;

            for(int var20 = 0; var20 < 8; ++var20) {
               float var21 = (float)((var20 & 1) * 2 - 1);
               float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
               float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
               var21 *= 0.1F;
               var22 *= 0.1F;
               var23 *= 0.1F;
               RayTraceResult var24 = this.field_78531_r.field_71441_e.func_72933_a(new Vec3d(var4 + (double)var21, var6 + (double)var22, var8 + (double)var23), new Vec3d(var4 - var14 + (double)var21 + (double)var23, var6 - var18 + (double)var22, var8 - var16 + (double)var23));
               if (var24 != null) {
                  double var25 = var24.field_72307_f.func_72438_d(new Vec3d(var4, var6, var8));
                  if (var25 < var10) {
                     var10 = var25;
                  }
               }
            }

            if (this.field_78531_r.field_71474_y.field_74320_O == 2) {
               GlStateManager.func_179114_b(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.func_179114_b(var2.field_70125_A - var13, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(var2.field_70177_z - var12, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179109_b(0.0F, 0.0F, (float)(-var10));
            GlStateManager.func_179114_b(var12 - var2.field_70177_z, 0.0F, 1.0F, 0.0F);
            GlStateManager.func_179114_b(var13 - var2.field_70125_A, 1.0F, 0.0F, 0.0F);
         }
      } else if (!this.field_175078_W) {
         GlStateManager.func_179109_b(0.0F, 0.0F, 0.05F);
      }

      if (!this.field_78531_r.field_71474_y.field_74325_U) {
         GlStateManager.func_179114_b(var2.func_195050_f(var1), 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(var2.func_195046_g(var1) + 180.0F, 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.func_179109_b(0.0F, -var3, 0.0F);
   }

   private void func_195460_g(float var1) {
      this.field_78530_s = (float)(this.field_78531_r.field_71474_y.field_151451_c * 16);
      GlStateManager.func_179128_n(5889);
      GlStateManager.func_179096_D();
      if (this.field_78503_V != 1.0D) {
         GlStateManager.func_179109_b((float)this.field_78502_W, (float)(-this.field_78509_X), 0.0F);
         GlStateManager.func_179139_a(this.field_78503_V, this.field_78503_V, 1.0D);
      }

      GlStateManager.func_199294_a(Matrix4f.func_195876_a(this.func_195459_a(var1, true), (float)this.field_78531_r.field_195558_d.func_198109_k() / (float)this.field_78531_r.field_195558_d.func_198091_l(), 0.05F, this.field_78530_s * MathHelper.field_180189_a));
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179096_D();
      this.func_78482_e(var1);
      if (this.field_78531_r.field_71474_y.field_74336_f) {
         this.func_78475_f(var1);
      }

      float var2 = this.field_78531_r.field_71439_g.field_71080_cy + (this.field_78531_r.field_71439_g.field_71086_bY - this.field_78531_r.field_71439_g.field_71080_cy) * var1;
      if (var2 > 0.0F) {
         byte var3 = 20;
         if (this.field_78531_r.field_71439_g.func_70644_a(MobEffects.field_76431_k)) {
            var3 = 7;
         }

         float var4 = 5.0F / (var2 * var2 + 5.0F) - var2 * 0.04F;
         var4 *= var4;
         GlStateManager.func_179114_b(((float)this.field_78529_t + var1) * (float)var3, 0.0F, 1.0F, 1.0F);
         GlStateManager.func_179152_a(1.0F / var4, 1.0F, 1.0F);
         GlStateManager.func_179114_b(-((float)this.field_78529_t + var1) * (float)var3, 0.0F, 1.0F, 1.0F);
      }

      this.func_78467_g(var1);
   }

   private void func_195457_h(float var1) {
      if (!this.field_175078_W) {
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_199294_a(Matrix4f.func_195876_a(this.func_195459_a(var1, false), (float)this.field_78531_r.field_195558_d.func_198109_k() / (float)this.field_78531_r.field_195558_d.func_198091_l(), 0.05F, this.field_78530_s * 2.0F));
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179096_D();
         GlStateManager.func_179094_E();
         this.func_78482_e(var1);
         if (this.field_78531_r.field_71474_y.field_74336_f) {
            this.func_78475_f(var1);
         }

         boolean var2 = this.field_78531_r.func_175606_aa() instanceof EntityLivingBase && ((EntityLivingBase)this.field_78531_r.func_175606_aa()).func_70608_bn();
         if (this.field_78531_r.field_71474_y.field_74320_O == 0 && !var2 && !this.field_78531_r.field_71474_y.field_74319_N && this.field_78531_r.field_71442_b.func_178889_l() != GameType.SPECTATOR) {
            this.func_180436_i();
            this.field_78516_c.func_78440_a(var1);
            this.func_175072_h();
         }

         GlStateManager.func_179121_F();
         if (this.field_78531_r.field_71474_y.field_74320_O == 0 && !var2) {
            this.field_78516_c.func_78447_b(var1);
            this.func_78482_e(var1);
         }

         if (this.field_78531_r.field_71474_y.field_74336_f) {
            this.func_78475_f(var1);
         }

      }
   }

   public void func_175072_h() {
      this.field_78513_d.func_205108_b();
   }

   public void func_180436_i() {
      this.field_78513_d.func_205109_c();
   }

   public float func_180438_a(EntityLivingBase var1, float var2) {
      int var3 = var1.func_70660_b(MobEffects.field_76439_r).func_76459_b();
      return var3 > 200 ? 1.0F : 0.7F + MathHelper.func_76126_a(((float)var3 - var2) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void func_195458_a(float var1, long var2, boolean var4) {
      if (this.field_78531_r.func_195544_aj() || !this.field_78531_r.field_71474_y.field_82881_y || this.field_78531_r.field_71474_y.field_85185_A && this.field_78531_r.field_71417_B.func_198031_d()) {
         this.field_78508_Y = Util.func_211177_b();
      } else if (Util.func_211177_b() - this.field_78508_Y > 500L) {
         this.field_78531_r.func_71385_j();
      }

      if (!this.field_78531_r.field_71454_w) {
         int var5 = (int)(this.field_78531_r.field_71417_B.func_198024_e() * (double)this.field_78531_r.field_195558_d.func_198107_o() / (double)this.field_78531_r.field_195558_d.func_198105_m());
         int var6 = (int)(this.field_78531_r.field_71417_B.func_198026_f() * (double)this.field_78531_r.field_195558_d.func_198087_p() / (double)this.field_78531_r.field_195558_d.func_198083_n());
         int var7 = this.field_78531_r.field_71474_y.field_74350_i;
         if (var4 && this.field_78531_r.field_71441_e != null) {
            this.field_78531_r.field_71424_I.func_76320_a("level");
            int var8 = Math.min(Minecraft.func_175610_ah(), var7);
            var8 = Math.max(var8, 60);
            long var9 = Util.func_211178_c() - var2;
            long var11 = Math.max((long)(1000000000 / var8 / 4) - var9, 0L);
            this.func_78471_a(var1, Util.func_211178_c() + var11);
            if (this.field_78531_r.func_71356_B() && this.field_184374_E < Util.func_211177_b() - 1000L) {
               this.field_184374_E = Util.func_211177_b();
               if (!this.field_78531_r.func_71401_C().func_184106_y()) {
                  this.func_184373_n();
               }
            }

            if (OpenGlHelper.field_148824_g) {
               this.field_78531_r.field_71438_f.func_174975_c();
               if (this.field_147707_d != null && this.field_175083_ad) {
                  GlStateManager.func_179128_n(5890);
                  GlStateManager.func_179094_E();
                  GlStateManager.func_179096_D();
                  this.field_147707_d.func_148018_a(var1);
                  GlStateManager.func_179121_F();
               }

               this.field_78531_r.func_147110_a().func_147610_a(true);
            }

            this.field_78531_r.field_71424_I.func_76318_c("gui");
            if (!this.field_78531_r.field_71474_y.field_74319_N || this.field_78531_r.field_71462_r != null) {
               GlStateManager.func_179092_a(516, 0.1F);
               this.field_78531_r.field_195558_d.func_198094_a();
               this.func_190563_a(this.field_78531_r.field_195558_d.func_198107_o(), this.field_78531_r.field_195558_d.func_198087_p(), var1);
               this.field_78531_r.field_71456_v.func_175180_a(var1);
            }

            this.field_78531_r.field_71424_I.func_76319_b();
         } else {
            GlStateManager.func_179083_b(0, 0, this.field_78531_r.field_195558_d.func_198109_k(), this.field_78531_r.field_195558_d.func_198091_l());
            GlStateManager.func_179128_n(5889);
            GlStateManager.func_179096_D();
            GlStateManager.func_179128_n(5888);
            GlStateManager.func_179096_D();
            this.field_78531_r.field_195558_d.func_198094_a();
         }

         if (this.field_78531_r.field_71462_r != null) {
            GlStateManager.func_179086_m(256);

            try {
               this.field_78531_r.field_71462_r.func_73863_a(var5, var6, this.field_78531_r.func_193989_ak());
            } catch (Throwable var13) {
               CrashReport var14 = CrashReport.func_85055_a(var13, "Rendering screen");
               CrashReportCategory var10 = var14.func_85058_a("Screen render details");
               var10.func_189529_a("Screen name", () -> {
                  return this.field_78531_r.field_71462_r.getClass().getCanonicalName();
               });
               var10.func_189529_a("Mouse location", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%f, %f)", var5, var6, this.field_78531_r.field_71417_B.func_198024_e(), this.field_78531_r.field_71417_B.func_198026_f());
               });
               var10.func_189529_a("Screen size", () -> {
                  return String.format(Locale.ROOT, "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %f", this.field_78531_r.field_195558_d.func_198107_o(), this.field_78531_r.field_195558_d.func_198087_p(), this.field_78531_r.field_195558_d.func_198109_k(), this.field_78531_r.field_195558_d.func_198091_l(), this.field_78531_r.field_195558_d.func_198100_s());
               });
               throw new ReportedException(var14);
            }
         }

      }
   }

   private void func_184373_n() {
      if (this.field_78531_r.field_71438_f.func_184382_g() > 10 && this.field_78531_r.field_71438_f.func_184384_n() && !this.field_78531_r.func_71401_C().func_184106_y()) {
         NativeImage var1 = ScreenShotHelper.func_198052_a(this.field_78531_r.field_195558_d.func_198109_k(), this.field_78531_r.field_195558_d.func_198091_l(), this.field_78531_r.func_147110_a());
         SimpleResource.field_199031_a.execute(() -> {
            int var2 = var1.func_195702_a();
            int var3 = var1.func_195714_b();
            int var4 = 0;
            int var5 = 0;
            if (var2 > var3) {
               var4 = (var2 - var3) / 2;
               var2 = var3;
            } else {
               var5 = (var3 - var2) / 2;
               var3 = var2;
            }

            try {
               NativeImage var6 = new NativeImage(64, 64, false);
               Throwable var7 = null;

               try {
                  var1.func_195708_a(var4, var5, var2, var3, var6);
                  var6.func_209271_a(this.field_78531_r.func_71401_C().func_184109_z());
               } catch (Throwable var25) {
                  var7 = var25;
                  throw var25;
               } finally {
                  if (var6 != null) {
                     if (var7 != null) {
                        try {
                           var6.close();
                        } catch (Throwable var24) {
                           var7.addSuppressed(var24);
                        }
                     } else {
                        var6.close();
                     }
                  }

               }
            } catch (IOException var27) {
               field_147710_q.warn("Couldn't save auto screenshot", var27);
            } finally {
               var1.close();
            }

         });
      }

   }

   public void func_152430_c(float var1) {
      this.field_78531_r.field_195558_d.func_198094_a();
   }

   private boolean func_175070_n() {
      if (!this.field_175073_D) {
         return false;
      } else {
         Entity var1 = this.field_78531_r.func_175606_aa();
         boolean var2 = var1 instanceof EntityPlayer && !this.field_78531_r.field_71474_y.field_74319_N;
         if (var2 && !((EntityPlayer)var1).field_71075_bZ.field_75099_e) {
            ItemStack var3 = ((EntityPlayer)var1).func_184614_ca();
            if (this.field_78531_r.field_71476_x != null && this.field_78531_r.field_71476_x.field_72313_a == RayTraceResult.Type.BLOCK) {
               BlockPos var4 = this.field_78531_r.field_71476_x.func_178782_a();
               Block var5 = this.field_78531_r.field_71441_e.func_180495_p(var4).func_177230_c();
               if (this.field_78531_r.field_71442_b.func_178889_l() == GameType.SPECTATOR) {
                  var2 = var5.func_149716_u() && this.field_78531_r.field_71441_e.func_175625_s(var4) instanceof IInventory;
               } else {
                  BlockWorldState var6 = new BlockWorldState(this.field_78531_r.field_71441_e, var4, false);
                  var2 = !var3.func_190926_b() && (var3.func_206848_a(this.field_78531_r.field_71441_e.func_205772_D(), var6) || var3.func_206847_b(this.field_78531_r.field_71441_e.func_205772_D(), var6));
               }
            }
         }

         return var2;
      }
   }

   public void func_78471_a(float var1, long var2) {
      this.field_78513_d.func_205106_a(var1);
      if (this.field_78531_r.func_175606_aa() == null) {
         this.field_78531_r.func_175607_a(this.field_78531_r.field_71439_g);
      }

      this.func_78473_a(var1);
      GlStateManager.func_179126_j();
      GlStateManager.func_179141_d();
      GlStateManager.func_179092_a(516, 0.5F);
      this.field_78531_r.field_71424_I.func_76320_a("center");
      this.func_181560_a(var1, var2);
      this.field_78531_r.field_71424_I.func_76319_b();
   }

   private void func_181560_a(float var1, long var2) {
      WorldRenderer var4 = this.field_78531_r.field_71438_f;
      ParticleManager var5 = this.field_78531_r.field_71452_i;
      boolean var6 = this.func_175070_n();
      GlStateManager.func_179089_o();
      this.field_78531_r.field_71424_I.func_76318_c("clear");
      GlStateManager.func_179083_b(0, 0, this.field_78531_r.field_195558_d.func_198109_k(), this.field_78531_r.field_195558_d.func_198091_l());
      this.field_205003_A.func_78466_h(var1);
      GlStateManager.func_179086_m(16640);
      this.field_78531_r.field_71424_I.func_76318_c("camera");
      this.func_195460_g(var1);
      ActiveRenderInfo.func_197924_a(this.field_78531_r.field_71439_g, this.field_78531_r.field_71474_y.field_74320_O == 2, this.field_78530_s);
      this.field_78531_r.field_71424_I.func_76318_c("frustum");
      ClippingHelperImpl.func_78558_a();
      this.field_78531_r.field_71424_I.func_76318_c("culling");
      Frustum var7 = new Frustum();
      Entity var8 = this.field_78531_r.func_175606_aa();
      double var9 = var8.field_70142_S + (var8.field_70165_t - var8.field_70142_S) * (double)var1;
      double var11 = var8.field_70137_T + (var8.field_70163_u - var8.field_70137_T) * (double)var1;
      double var13 = var8.field_70136_U + (var8.field_70161_v - var8.field_70136_U) * (double)var1;
      var7.func_78547_a(var9, var11, var13);
      if (this.field_78531_r.field_71474_y.field_151451_c >= 4) {
         this.field_205003_A.func_78468_a(-1, var1);
         this.field_78531_r.field_71424_I.func_76318_c("sky");
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_199294_a(Matrix4f.func_195876_a(this.func_195459_a(var1, true), (float)this.field_78531_r.field_195558_d.func_198109_k() / (float)this.field_78531_r.field_195558_d.func_198091_l(), 0.05F, this.field_78530_s * 2.0F));
         GlStateManager.func_179128_n(5888);
         var4.func_195465_a(var1);
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_199294_a(Matrix4f.func_195876_a(this.func_195459_a(var1, true), (float)this.field_78531_r.field_195558_d.func_198109_k() / (float)this.field_78531_r.field_195558_d.func_198091_l(), 0.05F, this.field_78530_s * MathHelper.field_180189_a));
         GlStateManager.func_179128_n(5888);
      }

      this.field_205003_A.func_78468_a(0, var1);
      GlStateManager.func_179103_j(7425);
      if (var8.field_70163_u + (double)var8.func_70047_e() < 128.0D) {
         this.func_195456_a(var4, var1, var9, var11, var13);
      }

      this.field_78531_r.field_71424_I.func_76318_c("prepareterrain");
      this.field_205003_A.func_78468_a(0, var1);
      this.field_78531_r.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      RenderHelper.func_74518_a();
      this.field_78531_r.field_71424_I.func_76318_c("terrain_setup");
      var4.func_195473_a(var8, var1, var7, this.field_175084_ae++, this.field_78531_r.field_71439_g.func_175149_v());
      this.field_78531_r.field_71424_I.func_76318_c("updatechunks");
      this.field_78531_r.field_71438_f.func_174967_a(var2);
      this.field_78531_r.field_71424_I.func_76318_c("terrain");
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179094_E();
      GlStateManager.func_179118_c();
      var4.func_195464_a(BlockRenderLayer.SOLID, (double)var1, var8);
      GlStateManager.func_179141_d();
      var4.func_195464_a(BlockRenderLayer.CUTOUT_MIPPED, (double)var1, var8);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174936_b(false, false);
      var4.func_195464_a(BlockRenderLayer.CUTOUT, (double)var1, var8);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174935_a();
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179121_F();
      GlStateManager.func_179094_E();
      RenderHelper.func_74519_b();
      this.field_78531_r.field_71424_I.func_76318_c("entities");
      var4.func_180446_a(var8, var7, var1);
      RenderHelper.func_74518_a();
      this.func_175072_h();
      GlStateManager.func_179128_n(5888);
      GlStateManager.func_179121_F();
      if (var6 && this.field_78531_r.field_71476_x != null) {
         EntityPlayer var15 = (EntityPlayer)var8;
         GlStateManager.func_179118_c();
         this.field_78531_r.field_71424_I.func_76318_c("outline");
         var4.func_72731_b(var15, this.field_78531_r.field_71476_x, 0, var1);
         GlStateManager.func_179141_d();
      }

      if (this.field_78531_r.field_184132_p.func_190074_a()) {
         this.field_78531_r.field_184132_p.func_190073_a(var1, var2);
      }

      this.field_78531_r.field_71424_I.func_76318_c("destroyProgress");
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174936_b(false, false);
      var4.func_174981_a(Tessellator.func_178181_a(), Tessellator.func_178181_a().func_178180_c(), var8, var1);
      this.field_78531_r.func_110434_K().func_110581_b(TextureMap.field_110575_b).func_174935_a();
      GlStateManager.func_179084_k();
      this.func_180436_i();
      this.field_78531_r.field_71424_I.func_76318_c("litParticles");
      var5.func_78872_b(var8, var1);
      RenderHelper.func_74518_a();
      this.field_205003_A.func_78468_a(0, var1);
      this.field_78531_r.field_71424_I.func_76318_c("particles");
      var5.func_78874_a(var8, var1);
      this.func_175072_h();
      GlStateManager.func_179132_a(false);
      GlStateManager.func_179089_o();
      this.field_78531_r.field_71424_I.func_76318_c("weather");
      this.func_78474_d(var1);
      GlStateManager.func_179132_a(true);
      var4.func_180449_a(var8, var1);
      GlStateManager.func_179084_k();
      GlStateManager.func_179089_o();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179092_a(516, 0.1F);
      this.field_205003_A.func_78468_a(0, var1);
      GlStateManager.func_179147_l();
      GlStateManager.func_179132_a(false);
      this.field_78531_r.func_110434_K().func_110577_a(TextureMap.field_110575_b);
      GlStateManager.func_179103_j(7425);
      this.field_78531_r.field_71424_I.func_76318_c("translucent");
      var4.func_195464_a(BlockRenderLayer.TRANSLUCENT, (double)var1, var8);
      GlStateManager.func_179103_j(7424);
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179089_o();
      GlStateManager.func_179084_k();
      GlStateManager.func_179106_n();
      if (var8.field_70163_u + (double)var8.func_70047_e() >= 128.0D) {
         this.field_78531_r.field_71424_I.func_76318_c("aboveClouds");
         this.func_195456_a(var4, var1, var9, var11, var13);
      }

      this.field_78531_r.field_71424_I.func_76318_c("hand");
      if (this.field_175074_C) {
         GlStateManager.func_179086_m(256);
         this.func_195457_h(var1);
      }

   }

   private void func_195456_a(WorldRenderer var1, float var2, double var3, double var5, double var7) {
      if (this.field_78531_r.field_71474_y.func_181147_e() != 0) {
         this.field_78531_r.field_71424_I.func_76318_c("clouds");
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_199294_a(Matrix4f.func_195876_a(this.func_195459_a(var2, true), (float)this.field_78531_r.field_195558_d.func_198109_k() / (float)this.field_78531_r.field_195558_d.func_198091_l(), 0.05F, this.field_78530_s * 4.0F));
         GlStateManager.func_179128_n(5888);
         GlStateManager.func_179094_E();
         this.field_205003_A.func_78468_a(0, var2);
         var1.func_195466_a(var2, var3, var5, var7);
         GlStateManager.func_179106_n();
         GlStateManager.func_179121_F();
         GlStateManager.func_179128_n(5889);
         GlStateManager.func_179096_D();
         GlStateManager.func_199294_a(Matrix4f.func_195876_a(this.func_195459_a(var2, true), (float)this.field_78531_r.field_195558_d.func_198109_k() / (float)this.field_78531_r.field_195558_d.func_198091_l(), 0.05F, this.field_78530_s * MathHelper.field_180189_a));
         GlStateManager.func_179128_n(5888);
      }

   }

   private void func_78484_h() {
      float var1 = this.field_78531_r.field_71441_e.func_72867_j(1.0F);
      if (!this.field_78531_r.field_71474_y.field_74347_j) {
         var1 /= 2.0F;
      }

      if (var1 != 0.0F) {
         this.field_78537_ab.setSeed((long)this.field_78529_t * 312987231L);
         Entity var2 = this.field_78531_r.func_175606_aa();
         WorldClient var3 = this.field_78531_r.field_71441_e;
         BlockPos var4 = new BlockPos(var2);
         boolean var5 = true;
         double var6 = 0.0D;
         double var8 = 0.0D;
         double var10 = 0.0D;
         int var12 = 0;
         int var13 = (int)(100.0F * var1 * var1);
         if (this.field_78531_r.field_71474_y.field_74362_aa == 1) {
            var13 >>= 1;
         } else if (this.field_78531_r.field_71474_y.field_74362_aa == 2) {
            var13 = 0;
         }

         for(int var14 = 0; var14 < var13; ++var14) {
            BlockPos var15 = var3.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4.func_177982_a(this.field_78537_ab.nextInt(10) - this.field_78537_ab.nextInt(10), 0, this.field_78537_ab.nextInt(10) - this.field_78537_ab.nextInt(10)));
            Biome var16 = var3.func_180494_b(var15);
            BlockPos var17 = var15.func_177977_b();
            if (var15.func_177956_o() <= var4.func_177956_o() + 10 && var15.func_177956_o() >= var4.func_177956_o() - 10 && var16.func_201851_b() == Biome.RainType.RAIN && var16.func_180626_a(var15) >= 0.15F) {
               double var18 = this.field_78537_ab.nextDouble();
               double var20 = this.field_78537_ab.nextDouble();
               IBlockState var22 = var3.func_180495_p(var17);
               IFluidState var23 = var3.func_204610_c(var15);
               VoxelShape var24 = var22.func_196952_d(var3, var17);
               double var29 = var24.func_197760_b(EnumFacing.Axis.Y, var18, var20);
               double var31 = (double)var23.func_206885_f();
               double var25;
               double var27;
               if (var29 >= var31) {
                  var25 = var29;
                  var27 = var24.func_197764_a(EnumFacing.Axis.Y, var18, var20);
               } else {
                  var25 = 0.0D;
                  var27 = 0.0D;
               }

               if (var25 > -1.7976931348623157E308D) {
                  if (!var23.func_206884_a(FluidTags.field_206960_b) && var22.func_177230_c() != Blocks.field_196814_hQ) {
                     ++var12;
                     if (this.field_78537_ab.nextInt(var12) == 0) {
                        var6 = (double)var17.func_177958_n() + var18;
                        var8 = (double)((float)var17.func_177956_o() + 0.1F) + var25 - 1.0D;
                        var10 = (double)var17.func_177952_p() + var20;
                     }

                     this.field_78531_r.field_71441_e.func_195594_a(Particles.field_197600_K, (double)var17.func_177958_n() + var18, (double)((float)var17.func_177956_o() + 0.1F) + var25, (double)var17.func_177952_p() + var20, 0.0D, 0.0D, 0.0D);
                  } else {
                     this.field_78531_r.field_71441_e.func_195594_a(Particles.field_197601_L, (double)var15.func_177958_n() + var18, (double)((float)var15.func_177956_o() + 0.1F) - var27, (double)var15.func_177952_p() + var20, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         }

         if (var12 > 0 && this.field_78537_ab.nextInt(3) < this.field_78534_ac++) {
            this.field_78534_ac = 0;
            if (var8 > (double)(var4.func_177956_o() + 1) && var3.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var4).func_177956_o() > MathHelper.func_76141_d((float)var4.func_177956_o())) {
               this.field_78531_r.field_71441_e.func_184134_a(var6, var8, var10, SoundEvents.field_187919_gs, SoundCategory.WEATHER, 0.1F, 0.5F, false);
            } else {
               this.field_78531_r.field_71441_e.func_184134_a(var6, var8, var10, SoundEvents.field_187918_gr, SoundCategory.WEATHER, 0.2F, 1.0F, false);
            }
         }

      }
   }

   protected void func_78474_d(float var1) {
      float var2 = this.field_78531_r.field_71441_e.func_72867_j(var1);
      if (var2 > 0.0F) {
         this.func_180436_i();
         Entity var3 = this.field_78531_r.func_175606_aa();
         WorldClient var4 = this.field_78531_r.field_71441_e;
         int var5 = MathHelper.func_76128_c(var3.field_70165_t);
         int var6 = MathHelper.func_76128_c(var3.field_70163_u);
         int var7 = MathHelper.func_76128_c(var3.field_70161_v);
         Tessellator var8 = Tessellator.func_178181_a();
         BufferBuilder var9 = var8.func_178180_c();
         GlStateManager.func_179129_p();
         GlStateManager.func_187432_a(0.0F, 1.0F, 0.0F);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.func_179092_a(516, 0.1F);
         double var10 = var3.field_70142_S + (var3.field_70165_t - var3.field_70142_S) * (double)var1;
         double var12 = var3.field_70137_T + (var3.field_70163_u - var3.field_70137_T) * (double)var1;
         double var14 = var3.field_70136_U + (var3.field_70161_v - var3.field_70136_U) * (double)var1;
         int var16 = MathHelper.func_76128_c(var12);
         byte var17 = 5;
         if (this.field_78531_r.field_71474_y.field_74347_j) {
            var17 = 10;
         }

         byte var18 = -1;
         float var19 = (float)this.field_78529_t + var1;
         var9.func_178969_c(-var10, -var12, -var14);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         BlockPos.MutableBlockPos var20 = new BlockPos.MutableBlockPos();

         for(int var21 = var7 - var17; var21 <= var7 + var17; ++var21) {
            for(int var22 = var5 - var17; var22 <= var5 + var17; ++var22) {
               int var23 = (var21 - var7 + 16) * 32 + var22 - var5 + 16;
               double var24 = (double)this.field_175076_N[var23] * 0.5D;
               double var26 = (double)this.field_175077_O[var23] * 0.5D;
               var20.func_181079_c(var22, 0, var21);
               Biome var28 = var4.func_180494_b(var20);
               if (var28.func_201851_b() != Biome.RainType.NONE) {
                  int var29 = var4.func_205770_a(Heightmap.Type.MOTION_BLOCKING, var20).func_177956_o();
                  int var30 = var6 - var17;
                  int var31 = var6 + var17;
                  if (var30 < var29) {
                     var30 = var29;
                  }

                  if (var31 < var29) {
                     var31 = var29;
                  }

                  int var32 = var29;
                  if (var29 < var16) {
                     var32 = var16;
                  }

                  if (var30 != var31) {
                     this.field_78537_ab.setSeed((long)(var22 * var22 * 3121 + var22 * 45238971 ^ var21 * var21 * 418711 + var21 * 13761));
                     var20.func_181079_c(var22, var30, var21);
                     float var33 = var28.func_180626_a(var20);
                     double var34;
                     double var36;
                     double var38;
                     if (var33 >= 0.15F) {
                        if (var18 != 0) {
                           if (var18 >= 0) {
                              var8.func_78381_a();
                           }

                           var18 = 0;
                           this.field_78531_r.func_110434_K().func_110577_a(field_110924_q);
                           var9.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                        }

                        var34 = -((double)(this.field_78529_t + var22 * var22 * 3121 + var22 * 45238971 + var21 * var21 * 418711 + var21 * 13761 & 31) + (double)var1) / 32.0D * (3.0D + this.field_78537_ab.nextDouble());
                        var36 = (double)((float)var22 + 0.5F) - var3.field_70165_t;
                        var38 = (double)((float)var21 + 0.5F) - var3.field_70161_v;
                        float var40 = MathHelper.func_76133_a(var36 * var36 + var38 * var38) / (float)var17;
                        float var41 = ((1.0F - var40 * var40) * 0.5F + 0.5F) * var2;
                        var20.func_181079_c(var22, var32, var21);
                        int var42 = var4.func_175626_b(var20, 0);
                        int var43 = var42 >> 16 & '\uffff';
                        int var44 = var42 & '\uffff';
                        var9.func_181662_b((double)var22 - var24 + 0.5D, (double)var31, (double)var21 - var26 + 0.5D).func_187315_a(0.0D, (double)var30 * 0.25D + var34).func_181666_a(1.0F, 1.0F, 1.0F, var41).func_187314_a(var43, var44).func_181675_d();
                        var9.func_181662_b((double)var22 + var24 + 0.5D, (double)var31, (double)var21 + var26 + 0.5D).func_187315_a(1.0D, (double)var30 * 0.25D + var34).func_181666_a(1.0F, 1.0F, 1.0F, var41).func_187314_a(var43, var44).func_181675_d();
                        var9.func_181662_b((double)var22 + var24 + 0.5D, (double)var30, (double)var21 + var26 + 0.5D).func_187315_a(1.0D, (double)var31 * 0.25D + var34).func_181666_a(1.0F, 1.0F, 1.0F, var41).func_187314_a(var43, var44).func_181675_d();
                        var9.func_181662_b((double)var22 - var24 + 0.5D, (double)var30, (double)var21 - var26 + 0.5D).func_187315_a(0.0D, (double)var31 * 0.25D + var34).func_181666_a(1.0F, 1.0F, 1.0F, var41).func_187314_a(var43, var44).func_181675_d();
                     } else {
                        if (var18 != 1) {
                           if (var18 >= 0) {
                              var8.func_78381_a();
                           }

                           var18 = 1;
                           this.field_78531_r.func_110434_K().func_110577_a(field_110923_r);
                           var9.func_181668_a(7, DefaultVertexFormats.field_181704_d);
                        }

                        var34 = (double)(-((float)(this.field_78529_t & 511) + var1) / 512.0F);
                        var36 = this.field_78537_ab.nextDouble() + (double)var19 * 0.01D * (double)((float)this.field_78537_ab.nextGaussian());
                        var38 = this.field_78537_ab.nextDouble() + (double)(var19 * (float)this.field_78537_ab.nextGaussian()) * 0.001D;
                        double var51 = (double)((float)var22 + 0.5F) - var3.field_70165_t;
                        double var49 = (double)((float)var21 + 0.5F) - var3.field_70161_v;
                        float var50 = MathHelper.func_76133_a(var51 * var51 + var49 * var49) / (float)var17;
                        float var45 = ((1.0F - var50 * var50) * 0.3F + 0.5F) * var2;
                        var20.func_181079_c(var22, var32, var21);
                        int var46 = (var4.func_175626_b(var20, 0) * 3 + 15728880) / 4;
                        int var47 = var46 >> 16 & '\uffff';
                        int var48 = var46 & '\uffff';
                        var9.func_181662_b((double)var22 - var24 + 0.5D, (double)var31, (double)var21 - var26 + 0.5D).func_187315_a(0.0D + var36, (double)var30 * 0.25D + var34 + var38).func_181666_a(1.0F, 1.0F, 1.0F, var45).func_187314_a(var47, var48).func_181675_d();
                        var9.func_181662_b((double)var22 + var24 + 0.5D, (double)var31, (double)var21 + var26 + 0.5D).func_187315_a(1.0D + var36, (double)var30 * 0.25D + var34 + var38).func_181666_a(1.0F, 1.0F, 1.0F, var45).func_187314_a(var47, var48).func_181675_d();
                        var9.func_181662_b((double)var22 + var24 + 0.5D, (double)var30, (double)var21 + var26 + 0.5D).func_187315_a(1.0D + var36, (double)var31 * 0.25D + var34 + var38).func_181666_a(1.0F, 1.0F, 1.0F, var45).func_187314_a(var47, var48).func_181675_d();
                        var9.func_181662_b((double)var22 - var24 + 0.5D, (double)var30, (double)var21 - var26 + 0.5D).func_187315_a(0.0D + var36, (double)var31 * 0.25D + var34 + var38).func_181666_a(1.0F, 1.0F, 1.0F, var45).func_187314_a(var47, var48).func_181675_d();
                     }
                  }
               }
            }
         }

         if (var18 >= 0) {
            var8.func_78381_a();
         }

         var9.func_178969_c(0.0D, 0.0D, 0.0D);
         GlStateManager.func_179089_o();
         GlStateManager.func_179084_k();
         GlStateManager.func_179092_a(516, 0.1F);
         this.func_175072_h();
      }
   }

   public void func_191514_d(boolean var1) {
      this.field_205003_A.func_205090_a(var1);
   }

   public void func_190564_k() {
      this.field_190566_ab = null;
      this.field_147709_v.func_148249_a();
   }

   public MapItemRenderer func_147701_i() {
      return this.field_147709_v;
   }

   public static void func_189692_a(FontRenderer var0, String var1, float var2, float var3, float var4, int var5, float var6, float var7, boolean var8, boolean var9) {
      GlStateManager.func_179094_E();
      GlStateManager.func_179109_b(var2, var3, var4);
      GlStateManager.func_187432_a(0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b(-var6, 0.0F, 1.0F, 0.0F);
      GlStateManager.func_179114_b((float)(var8 ? -1 : 1) * var7, 1.0F, 0.0F, 0.0F);
      GlStateManager.func_179152_a(-0.025F, -0.025F, 0.025F);
      GlStateManager.func_179140_f();
      GlStateManager.func_179132_a(false);
      if (!var9) {
         GlStateManager.func_179097_i();
      }

      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      int var10 = var0.func_78256_a(var1) / 2;
      GlStateManager.func_179090_x();
      Tessellator var11 = Tessellator.func_178181_a();
      BufferBuilder var12 = var11.func_178180_c();
      var12.func_181668_a(7, DefaultVertexFormats.field_181706_f);
      var12.func_181662_b((double)(-var10 - 1), (double)(-1 + var5), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
      var12.func_181662_b((double)(-var10 - 1), (double)(8 + var5), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
      var12.func_181662_b((double)(var10 + 1), (double)(8 + var5), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
      var12.func_181662_b((double)(var10 + 1), (double)(-1 + var5), 0.0D).func_181666_a(0.0F, 0.0F, 0.0F, 0.25F).func_181675_d();
      var11.func_78381_a();
      GlStateManager.func_179098_w();
      if (!var9) {
         var0.func_211126_b(var1, (float)(-var0.func_78256_a(var1) / 2), (float)var5, 553648127);
         GlStateManager.func_179126_j();
      }

      GlStateManager.func_179132_a(true);
      var0.func_211126_b(var1, (float)(-var0.func_78256_a(var1) / 2), (float)var5, var9 ? 553648127 : -1);
      GlStateManager.func_179145_e();
      GlStateManager.func_179084_k();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179121_F();
   }

   public void func_190565_a(ItemStack var1) {
      this.field_190566_ab = var1;
      this.field_190567_ac = 40;
      this.field_190568_ad = this.field_78537_ab.nextFloat() * 2.0F - 1.0F;
      this.field_190569_ae = this.field_78537_ab.nextFloat() * 2.0F - 1.0F;
   }

   private void func_190563_a(int var1, int var2, float var3) {
      if (this.field_190566_ab != null && this.field_190567_ac > 0) {
         int var4 = 40 - this.field_190567_ac;
         float var5 = ((float)var4 + var3) / 40.0F;
         float var6 = var5 * var5;
         float var7 = var5 * var6;
         float var8 = 10.25F * var7 * var6 - 24.95F * var6 * var6 + 25.5F * var7 - 13.8F * var6 + 4.0F * var5;
         float var9 = var8 * 3.1415927F;
         float var10 = this.field_190568_ad * (float)(var1 / 4);
         float var11 = this.field_190569_ae * (float)(var2 / 4);
         GlStateManager.func_179141_d();
         GlStateManager.func_179094_E();
         GlStateManager.func_179123_a();
         GlStateManager.func_179126_j();
         GlStateManager.func_179129_p();
         RenderHelper.func_74519_b();
         GlStateManager.func_179109_b((float)(var1 / 2) + var10 * MathHelper.func_76135_e(MathHelper.func_76126_a(var9 * 2.0F)), (float)(var2 / 2) + var11 * MathHelper.func_76135_e(MathHelper.func_76126_a(var9 * 2.0F)), -50.0F);
         float var12 = 50.0F + 175.0F * MathHelper.func_76126_a(var9);
         GlStateManager.func_179152_a(var12, -var12, var12);
         GlStateManager.func_179114_b(900.0F * MathHelper.func_76135_e(MathHelper.func_76126_a(var9)), 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(6.0F * MathHelper.func_76134_b(var5 * 8.0F), 1.0F, 0.0F, 0.0F);
         GlStateManager.func_179114_b(6.0F * MathHelper.func_76134_b(var5 * 8.0F), 0.0F, 0.0F, 1.0F);
         this.field_78531_r.func_175599_af().func_181564_a(this.field_190566_ab, ItemCameraTransforms.TransformType.FIXED);
         GlStateManager.func_179099_b();
         GlStateManager.func_179121_F();
         RenderHelper.func_74518_a();
         GlStateManager.func_179089_o();
         GlStateManager.func_179097_i();
      }
   }

   public Minecraft func_205000_l() {
      return this.field_78531_r;
   }

   public float func_205002_d(float var1) {
      return this.field_82832_V + (this.field_82831_U - this.field_82832_V) * var1;
   }

   public float func_205001_m() {
      return this.field_78530_s;
   }

   static {
      field_147708_e = field_147712_ad.length;
   }
}
