package net.minecraft.client.renderer;

import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class EntityRenderer implements IResourceManagerReloadListener {
   private static final Logger field_147710_q = LogManager.getLogger();
   private static final ResourceLocation field_110924_q = new ResourceLocation("textures/environment/rain.png");
   private static final ResourceLocation field_110923_r = new ResourceLocation("textures/environment/snow.png");
   public static boolean field_78517_a;
   public static int field_78515_b;
   private Minecraft field_78531_r;
   private float field_78530_s;
   public final ItemRenderer field_78516_c;
   private final MapItemRenderer field_147709_v;
   private int field_78529_t;
   private Entity field_78528_u;
   private MouseFilter field_78527_v = new MouseFilter();
   private MouseFilter field_78526_w = new MouseFilter();
   private MouseFilter field_78541_x = new MouseFilter();
   private MouseFilter field_78540_y = new MouseFilter();
   private MouseFilter field_78538_z = new MouseFilter();
   private MouseFilter field_78489_A = new MouseFilter();
   private float field_78490_B = 4.0F;
   private float field_78491_C = 4.0F;
   private float field_78485_D;
   private float field_78486_E;
   private float field_78487_F;
   private float field_78488_G;
   private float field_78496_H;
   private float field_78497_I;
   private float field_78498_J;
   private float field_78499_K;
   private float field_78492_L;
   private float field_78493_M;
   private float field_78494_N;
   private float field_78495_O;
   private float field_78505_P;
   private final DynamicTexture field_78513_d;
   private final int[] field_78504_Q;
   private final ResourceLocation field_110922_T;
   private float field_78507_R;
   private float field_78506_S;
   private float field_78501_T;
   private float field_82831_U;
   private float field_82832_V;
   private boolean field_78500_U;
   private final IResourceManager field_147711_ac;
   public ShaderGroup field_147707_d;
   private static final ResourceLocation[] field_147712_ad = new ResourceLocation[]{
      new ResourceLocation("shaders/post/notch.json"),
      new ResourceLocation("shaders/post/fxaa.json"),
      new ResourceLocation("shaders/post/art.json"),
      new ResourceLocation("shaders/post/bumpy.json"),
      new ResourceLocation("shaders/post/blobs2.json"),
      new ResourceLocation("shaders/post/pencil.json"),
      new ResourceLocation("shaders/post/color_convolve.json"),
      new ResourceLocation("shaders/post/deconverge.json"),
      new ResourceLocation("shaders/post/flip.json"),
      new ResourceLocation("shaders/post/invert.json"),
      new ResourceLocation("shaders/post/ntsc.json"),
      new ResourceLocation("shaders/post/outline.json"),
      new ResourceLocation("shaders/post/phosphor.json"),
      new ResourceLocation("shaders/post/scan_pincushion.json"),
      new ResourceLocation("shaders/post/sobel.json"),
      new ResourceLocation("shaders/post/bits.json"),
      new ResourceLocation("shaders/post/desaturate.json"),
      new ResourceLocation("shaders/post/green.json"),
      new ResourceLocation("shaders/post/blur.json"),
      new ResourceLocation("shaders/post/wobble.json"),
      new ResourceLocation("shaders/post/blobs.json"),
      new ResourceLocation("shaders/post/antialias.json")
   };
   public static final int field_147708_e = field_147712_ad.length;
   private int field_147713_ae = field_147708_e;
   private double field_78503_V = 1.0;
   private double field_78502_W;
   private double field_78509_X;
   private long field_78508_Y = Minecraft.func_71386_F();
   private long field_78510_Z;
   private boolean field_78536_aa;
   float field_78514_e;
   float field_78511_f;
   float field_78512_g;
   float field_78524_h;
   private Random field_78537_ab = new Random();
   private int field_78534_ac;
   float[] field_78525_i;
   float[] field_78522_j;
   FloatBuffer field_78521_m = GLAllocation.func_74529_h(16);
   float field_78518_n;
   float field_78519_o;
   float field_78533_p;
   private float field_78535_ad;
   private float field_78539_ae;
   public int field_78532_q;

   public EntityRenderer(Minecraft var1, IResourceManager var2) {
      super();
      this.field_78531_r = var1;
      this.field_147711_ac = var2;
      this.field_147709_v = new MapItemRenderer(var1.func_110434_K());
      this.field_78516_c = new ItemRenderer(var1);
      this.field_78513_d = new DynamicTexture(16, 16);
      this.field_110922_T = var1.func_110434_K().func_110578_a("lightMap", this.field_78513_d);
      this.field_78504_Q = this.field_78513_d.func_110565_c();
      this.field_147707_d = null;
   }

   public boolean func_147702_a() {
      return OpenGlHelper.field_148824_g && this.field_147707_d != null;
   }

   public void func_147703_b() {
      if (this.field_147707_d != null) {
         this.field_147707_d.func_148021_a();
      }

      this.field_147707_d = null;
      this.field_147713_ae = field_147708_e;
   }

   public void func_147705_c() {
      if (OpenGlHelper.field_148824_g) {
         if (this.field_147707_d != null) {
            this.field_147707_d.func_148021_a();
         }

         this.field_147713_ae = (this.field_147713_ae + 1) % (field_147712_ad.length + 1);
         if (this.field_147713_ae != field_147708_e) {
            try {
               field_147710_q.info("Selecting effect " + field_147712_ad[this.field_147713_ae]);
               this.field_147707_d = new ShaderGroup(
                  this.field_78531_r.func_110434_K(), this.field_147711_ac, this.field_78531_r.func_147110_a(), field_147712_ad[this.field_147713_ae]
               );
               this.field_147707_d.func_148026_a(this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
            } catch (IOException var2) {
               field_147710_q.warn("Failed to load shader: " + field_147712_ad[this.field_147713_ae], var2);
               this.field_147713_ae = field_147708_e;
            } catch (JsonSyntaxException var3) {
               field_147710_q.warn("Failed to load shader: " + field_147712_ad[this.field_147713_ae], var3);
               this.field_147713_ae = field_147708_e;
            }
         } else {
            this.field_147707_d = null;
            field_147710_q.info("No effect selected");
         }
      }
   }

   @Override
   public void func_110549_a(IResourceManager var1) {
      if (this.field_147707_d != null) {
         this.field_147707_d.func_148021_a();
      }

      if (this.field_147713_ae != field_147708_e) {
         try {
            this.field_147707_d = new ShaderGroup(
               this.field_78531_r.func_110434_K(), var1, this.field_78531_r.func_147110_a(), field_147712_ad[this.field_147713_ae]
            );
            this.field_147707_d.func_148026_a(this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
         } catch (IOException var3) {
            field_147710_q.warn("Failed to load shader: " + field_147712_ad[this.field_147713_ae], var3);
            this.field_147713_ae = field_147708_e;
         }
      }
   }

   public void func_78464_a() {
      if (OpenGlHelper.field_148824_g && ShaderLinkHelper.func_148074_b() == null) {
         ShaderLinkHelper.func_148076_a();
      }

      this.func_78477_e();
      this.func_78470_f();
      this.field_78535_ad = this.field_78539_ae;
      this.field_78491_C = this.field_78490_B;
      this.field_78486_E = this.field_78485_D;
      this.field_78488_G = this.field_78487_F;
      this.field_78494_N = this.field_78493_M;
      this.field_78505_P = this.field_78495_O;
      if (this.field_78531_r.field_71474_y.field_74326_T) {
         float var1 = this.field_78531_r.field_71474_y.field_74341_c * 0.6F + 0.2F;
         float var2 = var1 * var1 * var1 * 8.0F;
         this.field_78498_J = this.field_78527_v.func_76333_a(this.field_78496_H, 0.05F * var2);
         this.field_78499_K = this.field_78526_w.func_76333_a(this.field_78497_I, 0.05F * var2);
         this.field_78492_L = 0.0F;
         this.field_78496_H = 0.0F;
         this.field_78497_I = 0.0F;
      }

      if (this.field_78531_r.field_71451_h == null) {
         this.field_78531_r.field_71451_h = this.field_78531_r.field_71439_g;
      }

      float var4 = this.field_78531_r
         .field_71441_e
         .func_72801_o(
            MathHelper.func_76128_c(this.field_78531_r.field_71451_h.field_70165_t),
            MathHelper.func_76128_c(this.field_78531_r.field_71451_h.field_70163_u),
            MathHelper.func_76128_c(this.field_78531_r.field_71451_h.field_70161_v)
         );
      float var5 = (float)this.field_78531_r.field_71474_y.field_151451_c / 16.0F;
      float var3 = var4 * (1.0F - var5) + var5;
      this.field_78539_ae += (var3 - this.field_78539_ae) * 0.1F;
      ++this.field_78529_t;
      this.field_78516_c.func_78441_a();
      this.func_78484_h();
      this.field_82832_V = this.field_82831_U;
      if (BossStatus.field_82825_d) {
         this.field_82831_U += 0.05F;
         if (this.field_82831_U > 1.0F) {
            this.field_82831_U = 1.0F;
         }

         BossStatus.field_82825_d = false;
      } else if (this.field_82831_U > 0.0F) {
         this.field_82831_U -= 0.0125F;
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
      }
   }

   public void func_78473_a(float var1) {
      if (this.field_78531_r.field_71451_h != null) {
         if (this.field_78531_r.field_71441_e != null) {
            this.field_78531_r.field_147125_j = null;
            double var2 = (double)this.field_78531_r.field_71442_b.func_78757_d();
            this.field_78531_r.field_71476_x = this.field_78531_r.field_71451_h.func_70614_a(var2, var1);
            double var4 = var2;
            Vec3 var6 = this.field_78531_r.field_71451_h.func_70666_h(var1);
            if (this.field_78531_r.field_71442_b.func_78749_i()) {
               var2 = 6.0;
               var4 = 6.0;
            } else {
               if (var2 > 3.0) {
                  var4 = 3.0;
               }

               var2 = var4;
            }

            if (this.field_78531_r.field_71476_x != null) {
               var4 = this.field_78531_r.field_71476_x.field_72307_f.func_72438_d(var6);
            }

            Vec3 var7 = this.field_78531_r.field_71451_h.func_70676_i(var1);
            Vec3 var8 = var6.func_72441_c(var7.field_72450_a * var2, var7.field_72448_b * var2, var7.field_72449_c * var2);
            this.field_78528_u = null;
            Vec3 var9 = null;
            float var10 = 1.0F;
            List var11 = this.field_78531_r
               .field_71441_e
               .func_72839_b(
                  this.field_78531_r.field_71451_h,
                  this.field_78531_r
                     .field_71451_h
                     .field_70121_D
                     .func_72321_a(var7.field_72450_a * var2, var7.field_72448_b * var2, var7.field_72449_c * var2)
                     .func_72314_b((double)var10, (double)var10, (double)var10)
               );
            double var12 = var4;

            for(int var14 = 0; var14 < var11.size(); ++var14) {
               Entity var15 = (Entity)var11.get(var14);
               if (var15.func_70067_L()) {
                  float var16 = var15.func_70111_Y();
                  AxisAlignedBB var17 = var15.field_70121_D.func_72314_b((double)var16, (double)var16, (double)var16);
                  MovingObjectPosition var18 = var17.func_72327_a(var6, var8);
                  if (var17.func_72318_a(var6)) {
                     if (0.0 < var12 || var12 == 0.0) {
                        this.field_78528_u = var15;
                        var9 = var18 == null ? var6 : var18.field_72307_f;
                        var12 = 0.0;
                     }
                  } else if (var18 != null) {
                     double var19 = var6.func_72438_d(var18.field_72307_f);
                     if (var19 < var12 || var12 == 0.0) {
                        if (var15 == this.field_78531_r.field_71451_h.field_70154_o) {
                           if (var12 == 0.0) {
                              this.field_78528_u = var15;
                              var9 = var18.field_72307_f;
                           }
                        } else {
                           this.field_78528_u = var15;
                           var9 = var18.field_72307_f;
                           var12 = var19;
                        }
                     }
                  }
               }
            }

            if (this.field_78528_u != null && (var12 < var4 || this.field_78531_r.field_71476_x == null)) {
               this.field_78531_r.field_71476_x = new MovingObjectPosition(this.field_78528_u, var9);
               if (this.field_78528_u instanceof EntityLivingBase || this.field_78528_u instanceof EntityItemFrame) {
                  this.field_78531_r.field_147125_j = this.field_78528_u;
               }
            }
         }
      }
   }

   private void func_78477_e() {
      EntityPlayerSP var1 = (EntityPlayerSP)this.field_78531_r.field_71451_h;
      this.field_78501_T = var1.func_71151_f();
      this.field_78506_S = this.field_78507_R;
      this.field_78507_R += (this.field_78501_T - this.field_78507_R) * 0.5F;
      if (this.field_78507_R > 1.5F) {
         this.field_78507_R = 1.5F;
      }

      if (this.field_78507_R < 0.1F) {
         this.field_78507_R = 0.1F;
      }
   }

   private float func_78481_a(float var1, boolean var2) {
      if (this.field_78532_q > 0) {
         return 90.0F;
      } else {
         EntityPlayer var3 = (EntityPlayer)this.field_78531_r.field_71451_h;
         float var4 = 70.0F;
         if (var2) {
            var4 = this.field_78531_r.field_71474_y.field_74334_X;
            var4 *= this.field_78506_S + (this.field_78507_R - this.field_78506_S) * var1;
         }

         if (var3.func_110143_aJ() <= 0.0F) {
            float var5 = (float)var3.field_70725_aQ + var1;
            var4 /= (1.0F - 500.0F / (var5 + 500.0F)) * 2.0F + 1.0F;
         }

         Block var7 = ActiveRenderInfo.func_151460_a(this.field_78531_r.field_71441_e, var3, var1);
         if (var7.func_149688_o() == Material.field_151586_h) {
            var4 = var4 * 60.0F / 70.0F;
         }

         return var4 + this.field_78494_N + (this.field_78493_M - this.field_78494_N) * var1;
      }
   }

   private void func_78482_e(float var1) {
      EntityLivingBase var2 = this.field_78531_r.field_71451_h;
      float var3 = (float)var2.field_70737_aN - var1;
      if (var2.func_110143_aJ() <= 0.0F) {
         float var4 = (float)var2.field_70725_aQ + var1;
         GL11.glRotatef(40.0F - 8000.0F / (var4 + 200.0F), 0.0F, 0.0F, 1.0F);
      }

      if (!(var3 < 0.0F)) {
         var3 /= (float)var2.field_70738_aO;
         var3 = MathHelper.func_76126_a(var3 * var3 * var3 * var3 * 3.1415927F);
         float var7 = var2.field_70739_aP;
         GL11.glRotatef(-var7, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(-var3 * 14.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(var7, 0.0F, 1.0F, 0.0F);
      }
   }

   private void func_78475_f(float var1) {
      if (this.field_78531_r.field_71451_h instanceof EntityPlayer) {
         EntityPlayer var2 = (EntityPlayer)this.field_78531_r.field_71451_h;
         float var3 = var2.field_70140_Q - var2.field_70141_P;
         float var4 = -(var2.field_70140_Q + var3 * var1);
         float var5 = var2.field_71107_bF + (var2.field_71109_bG - var2.field_71107_bF) * var1;
         float var6 = var2.field_70727_aS + (var2.field_70726_aT - var2.field_70727_aS) * var1;
         GL11.glTranslatef(MathHelper.func_76126_a(var4 * 3.1415927F) * var5 * 0.5F, -Math.abs(MathHelper.func_76134_b(var4 * 3.1415927F) * var5), 0.0F);
         GL11.glRotatef(MathHelper.func_76126_a(var4 * 3.1415927F) * var5 * 3.0F, 0.0F, 0.0F, 1.0F);
         GL11.glRotatef(Math.abs(MathHelper.func_76134_b(var4 * 3.1415927F - 0.2F) * var5) * 5.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(var6, 1.0F, 0.0F, 0.0F);
      }
   }

   private void func_78467_g(float var1) {
      EntityLivingBase var2 = this.field_78531_r.field_71451_h;
      float var3 = var2.field_70129_M - 1.62F;
      double var4 = var2.field_70169_q + (var2.field_70165_t - var2.field_70169_q) * (double)var1;
      double var6 = var2.field_70167_r + (var2.field_70163_u - var2.field_70167_r) * (double)var1 - (double)var3;
      double var8 = var2.field_70166_s + (var2.field_70161_v - var2.field_70166_s) * (double)var1;
      GL11.glRotatef(this.field_78505_P + (this.field_78495_O - this.field_78505_P) * var1, 0.0F, 0.0F, 1.0F);
      if (var2.func_70608_bn()) {
         var3 = (float)((double)var3 + 1.0);
         GL11.glTranslatef(0.0F, 0.3F, 0.0F);
         if (!this.field_78531_r.field_71474_y.field_74325_U) {
            Block var10 = this.field_78531_r
               .field_71441_e
               .func_147439_a(
                  MathHelper.func_76128_c(var2.field_70165_t), MathHelper.func_76128_c(var2.field_70163_u), MathHelper.func_76128_c(var2.field_70161_v)
               );
            if (var10 == Blocks.field_150324_C) {
               int var11 = this.field_78531_r
                  .field_71441_e
                  .func_72805_g(
                     MathHelper.func_76128_c(var2.field_70165_t), MathHelper.func_76128_c(var2.field_70163_u), MathHelper.func_76128_c(var2.field_70161_v)
                  );
               int var12 = var11 & 3;
               GL11.glRotatef((float)(var12 * 90), 0.0F, 1.0F, 0.0F);
            }

            GL11.glRotatef(var2.field_70126_B + (var2.field_70177_z - var2.field_70126_B) * var1 + 180.0F, 0.0F, -1.0F, 0.0F);
            GL11.glRotatef(var2.field_70127_C + (var2.field_70125_A - var2.field_70127_C) * var1, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.field_78531_r.field_71474_y.field_74320_O > 0) {
         double var30 = (double)(this.field_78491_C + (this.field_78490_B - this.field_78491_C) * var1);
         if (this.field_78531_r.field_71474_y.field_74325_U) {
            float var31 = this.field_78486_E + (this.field_78485_D - this.field_78486_E) * var1;
            float var13 = this.field_78488_G + (this.field_78487_F - this.field_78488_G) * var1;
            GL11.glTranslatef(0.0F, 0.0F, (float)(-var30));
            GL11.glRotatef(var13, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var31, 0.0F, 1.0F, 0.0F);
         } else {
            float var32 = var2.field_70177_z;
            float var33 = var2.field_70125_A;
            if (this.field_78531_r.field_71474_y.field_74320_O == 2) {
               var33 += 180.0F;
            }

            double var14 = (double)(-MathHelper.func_76126_a(var32 / 180.0F * 3.1415927F) * MathHelper.func_76134_b(var33 / 180.0F * 3.1415927F)) * var30;
            double var16 = (double)(MathHelper.func_76134_b(var32 / 180.0F * 3.1415927F) * MathHelper.func_76134_b(var33 / 180.0F * 3.1415927F)) * var30;
            double var18 = (double)(-MathHelper.func_76126_a(var33 / 180.0F * 3.1415927F)) * var30;

            for(int var20 = 0; var20 < 8; ++var20) {
               float var21 = (float)((var20 & 1) * 2 - 1);
               float var22 = (float)((var20 >> 1 & 1) * 2 - 1);
               float var23 = (float)((var20 >> 2 & 1) * 2 - 1);
               var21 *= 0.1F;
               var22 *= 0.1F;
               var23 *= 0.1F;
               MovingObjectPosition var24 = this.field_78531_r
                  .field_71441_e
                  .func_72933_a(
                     Vec3.func_72443_a(var4 + (double)var21, var6 + (double)var22, var8 + (double)var23),
                     Vec3.func_72443_a(var4 - var14 + (double)var21 + (double)var23, var6 - var18 + (double)var22, var8 - var16 + (double)var23)
                  );
               if (var24 != null) {
                  double var25 = var24.field_72307_f.func_72438_d(Vec3.func_72443_a(var4, var6, var8));
                  if (var25 < var30) {
                     var30 = var25;
                  }
               }
            }

            if (this.field_78531_r.field_71474_y.field_74320_O == 2) {
               GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GL11.glRotatef(var2.field_70125_A - var33, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(var2.field_70177_z - var32, 0.0F, 1.0F, 0.0F);
            GL11.glTranslatef(0.0F, 0.0F, (float)(-var30));
            GL11.glRotatef(var32 - var2.field_70177_z, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(var33 - var2.field_70125_A, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GL11.glTranslatef(0.0F, 0.0F, -0.1F);
      }

      if (!this.field_78531_r.field_71474_y.field_74325_U) {
         GL11.glRotatef(var2.field_70127_C + (var2.field_70125_A - var2.field_70127_C) * var1, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(var2.field_70126_B + (var2.field_70177_z - var2.field_70126_B) * var1 + 180.0F, 0.0F, 1.0F, 0.0F);
      }

      GL11.glTranslatef(0.0F, var3, 0.0F);
      var4 = var2.field_70169_q + (var2.field_70165_t - var2.field_70169_q) * (double)var1;
      var6 = var2.field_70167_r + (var2.field_70163_u - var2.field_70167_r) * (double)var1 - (double)var3;
      var8 = var2.field_70166_s + (var2.field_70161_v - var2.field_70166_s) * (double)var1;
      this.field_78500_U = this.field_78531_r.field_71438_f.func_72721_a(var4, var6, var8, var1);
   }

   private void func_78479_a(float var1, int var2) {
      this.field_78530_s = (float)(this.field_78531_r.field_71474_y.field_151451_c * 16);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      float var3 = 0.07F;
      if (this.field_78531_r.field_71474_y.field_74337_g) {
         GL11.glTranslatef((float)(-(var2 * 2 - 1)) * var3, 0.0F, 0.0F);
      }

      if (this.field_78503_V != 1.0) {
         GL11.glTranslatef((float)this.field_78502_W, (float)(-this.field_78509_X), 0.0F);
         GL11.glScaled(this.field_78503_V, this.field_78503_V, 1.0);
      }

      Project.gluPerspective(
         this.func_78481_a(var1, true), (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d, 0.05F, this.field_78530_s * 2.0F
      );
      if (this.field_78531_r.field_71442_b.func_78747_a()) {
         float var4 = 0.6666667F;
         GL11.glScalef(1.0F, var4, 1.0F);
      }

      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      if (this.field_78531_r.field_71474_y.field_74337_g) {
         GL11.glTranslatef((float)(var2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.func_78482_e(var1);
      if (this.field_78531_r.field_71474_y.field_74336_f) {
         this.func_78475_f(var1);
      }

      float var7 = this.field_78531_r.field_71439_g.field_71080_cy
         + (this.field_78531_r.field_71439_g.field_71086_bY - this.field_78531_r.field_71439_g.field_71080_cy) * var1;
      if (var7 > 0.0F) {
         byte var5 = 20;
         if (this.field_78531_r.field_71439_g.func_70644_a(Potion.field_76431_k)) {
            var5 = 7;
         }

         float var6 = 5.0F / (var7 * var7 + 5.0F) - var7 * 0.04F;
         var6 *= var6;
         GL11.glRotatef(((float)this.field_78529_t + var1) * (float)var5, 0.0F, 1.0F, 1.0F);
         GL11.glScalef(1.0F / var6, 1.0F, 1.0F);
         GL11.glRotatef(-((float)this.field_78529_t + var1) * (float)var5, 0.0F, 1.0F, 1.0F);
      }

      this.func_78467_g(var1);
      if (this.field_78532_q > 0) {
         int var8 = this.field_78532_q - 1;
         if (var8 == 1) {
            GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var8 == 2) {
            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var8 == 3) {
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         }

         if (var8 == 4) {
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var8 == 5) {
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   private void func_78476_b(float var1, int var2) {
      if (this.field_78532_q <= 0) {
         GL11.glMatrixMode(5889);
         GL11.glLoadIdentity();
         float var3 = 0.07F;
         if (this.field_78531_r.field_71474_y.field_74337_g) {
            GL11.glTranslatef((float)(-(var2 * 2 - 1)) * var3, 0.0F, 0.0F);
         }

         if (this.field_78503_V != 1.0) {
            GL11.glTranslatef((float)this.field_78502_W, (float)(-this.field_78509_X), 0.0F);
            GL11.glScaled(this.field_78503_V, this.field_78503_V, 1.0);
         }

         Project.gluPerspective(
            this.func_78481_a(var1, false),
            (float)this.field_78531_r.field_71443_c / (float)this.field_78531_r.field_71440_d,
            0.05F,
            this.field_78530_s * 2.0F
         );
         if (this.field_78531_r.field_71442_b.func_78747_a()) {
            float var4 = 0.6666667F;
            GL11.glScalef(1.0F, var4, 1.0F);
         }

         GL11.glMatrixMode(5888);
         GL11.glLoadIdentity();
         if (this.field_78531_r.field_71474_y.field_74337_g) {
            GL11.glTranslatef((float)(var2 * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         GL11.glPushMatrix();
         this.func_78482_e(var1);
         if (this.field_78531_r.field_71474_y.field_74336_f) {
            this.func_78475_f(var1);
         }

         if (this.field_78531_r.field_71474_y.field_74320_O == 0
            && !this.field_78531_r.field_71451_h.func_70608_bn()
            && !this.field_78531_r.field_71474_y.field_74319_N
            && !this.field_78531_r.field_71442_b.func_78747_a()) {
            this.func_78463_b((double)var1);
            this.field_78516_c.func_78440_a(var1);
            this.func_78483_a((double)var1);
         }

         GL11.glPopMatrix();
         if (this.field_78531_r.field_71474_y.field_74320_O == 0 && !this.field_78531_r.field_71451_h.func_70608_bn()) {
            this.field_78516_c.func_78447_b(var1);
            this.func_78482_e(var1);
         }

         if (this.field_78531_r.field_71474_y.field_74336_f) {
            this.func_78475_f(var1);
         }
      }
   }

   public void func_78483_a(double var1) {
      OpenGlHelper.func_77473_a(OpenGlHelper.field_77476_b);
      GL11.glDisable(3553);
      OpenGlHelper.func_77473_a(OpenGlHelper.field_77478_a);
   }

   public void func_78463_b(double var1) {
      OpenGlHelper.func_77473_a(OpenGlHelper.field_77476_b);
      GL11.glMatrixMode(5890);
      GL11.glLoadIdentity();
      float var3 = 0.00390625F;
      GL11.glScalef(var3, var3, var3);
      GL11.glTranslatef(8.0F, 8.0F, 8.0F);
      GL11.glMatrixMode(5888);
      this.field_78531_r.func_110434_K().func_110577_a(this.field_110922_T);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 10496);
      GL11.glTexParameteri(3553, 10243, 10496);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glEnable(3553);
      OpenGlHelper.func_77473_a(OpenGlHelper.field_77478_a);
   }

   private void func_78470_f() {
      this.field_78511_f = (float)((double)this.field_78511_f + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.field_78524_h = (float)((double)this.field_78524_h + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.field_78511_f = (float)((double)this.field_78511_f * 0.9);
      this.field_78524_h = (float)((double)this.field_78524_h * 0.9);
      this.field_78514_e += (this.field_78511_f - this.field_78514_e) * 1.0F;
      this.field_78512_g += (this.field_78524_h - this.field_78512_g) * 1.0F;
      this.field_78536_aa = true;
   }

   private void func_78472_g(float var1) {
      WorldClient var2 = this.field_78531_r.field_71441_e;
      if (var2 != null) {
         for(int var3 = 0; var3 < 256; ++var3) {
            float var4 = var2.func_72971_b(1.0F) * 0.95F + 0.05F;
            float var5 = var2.field_73011_w.field_76573_f[var3 / 16] * var4;
            float var6 = var2.field_73011_w.field_76573_f[var3 % 16] * (this.field_78514_e * 0.1F + 1.5F);
            if (var2.field_73016_r > 0) {
               var5 = var2.field_73011_w.field_76573_f[var3 / 16];
            }

            float var7 = var5 * (var2.func_72971_b(1.0F) * 0.65F + 0.35F);
            float var8 = var5 * (var2.func_72971_b(1.0F) * 0.65F + 0.35F);
            float var11 = var6 * ((var6 * 0.6F + 0.4F) * 0.6F + 0.4F);
            float var12 = var6 * (var6 * var6 * 0.6F + 0.4F);
            float var13 = var7 + var6;
            float var14 = var8 + var11;
            float var15 = var5 + var12;
            var13 = var13 * 0.96F + 0.03F;
            var14 = var14 * 0.96F + 0.03F;
            var15 = var15 * 0.96F + 0.03F;
            if (this.field_82831_U > 0.0F) {
               float var16 = this.field_82832_V + (this.field_82831_U - this.field_82832_V) * var1;
               var13 = var13 * (1.0F - var16) + var13 * 0.7F * var16;
               var14 = var14 * (1.0F - var16) + var14 * 0.6F * var16;
               var15 = var15 * (1.0F - var16) + var15 * 0.6F * var16;
            }

            if (var2.field_73011_w.field_76574_g == 1) {
               var13 = 0.22F + var6 * 0.75F;
               var14 = 0.28F + var11 * 0.75F;
               var15 = 0.25F + var12 * 0.75F;
            }

            if (this.field_78531_r.field_71439_g.func_70644_a(Potion.field_76439_r)) {
               float var33 = this.func_82830_a(this.field_78531_r.field_71439_g, var1);
               float var17 = 1.0F / var13;
               if (var17 > 1.0F / var14) {
                  var17 = 1.0F / var14;
               }

               if (var17 > 1.0F / var15) {
                  var17 = 1.0F / var15;
               }

               var13 = var13 * (1.0F - var33) + var13 * var17 * var33;
               var14 = var14 * (1.0F - var33) + var14 * var17 * var33;
               var15 = var15 * (1.0F - var33) + var15 * var17 * var33;
            }

            if (var13 > 1.0F) {
               var13 = 1.0F;
            }

            if (var14 > 1.0F) {
               var14 = 1.0F;
            }

            if (var15 > 1.0F) {
               var15 = 1.0F;
            }

            float var34 = this.field_78531_r.field_71474_y.field_74333_Y;
            float var35 = 1.0F - var13;
            float var18 = 1.0F - var14;
            float var19 = 1.0F - var15;
            var35 = 1.0F - var35 * var35 * var35 * var35;
            var18 = 1.0F - var18 * var18 * var18 * var18;
            var19 = 1.0F - var19 * var19 * var19 * var19;
            var13 = var13 * (1.0F - var34) + var35 * var34;
            var14 = var14 * (1.0F - var34) + var18 * var34;
            var15 = var15 * (1.0F - var34) + var19 * var34;
            var13 = var13 * 0.96F + 0.03F;
            var14 = var14 * 0.96F + 0.03F;
            var15 = var15 * 0.96F + 0.03F;
            if (var13 > 1.0F) {
               var13 = 1.0F;
            }

            if (var14 > 1.0F) {
               var14 = 1.0F;
            }

            if (var15 > 1.0F) {
               var15 = 1.0F;
            }

            if (var13 < 0.0F) {
               var13 = 0.0F;
            }

            if (var14 < 0.0F) {
               var14 = 0.0F;
            }

            if (var15 < 0.0F) {
               var15 = 0.0F;
            }

            short var20 = 255;
            int var21 = (int)(var13 * 255.0F);
            int var22 = (int)(var14 * 255.0F);
            int var23 = (int)(var15 * 255.0F);
            this.field_78504_Q[var3] = var20 << 24 | var21 << 16 | var22 << 8 | var23;
         }

         this.field_78513_d.func_110564_a();
         this.field_78536_aa = false;
      }
   }

   private float func_82830_a(EntityPlayer var1, float var2) {
      int var3 = var1.func_70660_b(Potion.field_76439_r).func_76459_b();
      return var3 > 200 ? 1.0F : 0.7F + MathHelper.func_76126_a(((float)var3 - var2) * 3.1415927F * 0.2F) * 0.3F;
   }

   public void func_78480_b(float var1) {
      this.field_78531_r.field_71424_I.func_76320_a("lightTex");
      if (this.field_78536_aa) {
         this.func_78472_g(var1);
      }

      this.field_78531_r.field_71424_I.func_76319_b();
      boolean var2 = Display.isActive();
      if (!var2 && this.field_78531_r.field_71474_y.field_82881_y && (!this.field_78531_r.field_71474_y.field_85185_A || !Mouse.isButtonDown(1))) {
         if (Minecraft.func_71386_F() - this.field_78508_Y > 500L) {
            this.field_78531_r.func_71385_j();
         }
      } else {
         this.field_78508_Y = Minecraft.func_71386_F();
      }

      this.field_78531_r.field_71424_I.func_76320_a("mouse");
      if (this.field_78531_r.field_71415_G && var2) {
         this.field_78531_r.field_71417_B.func_74374_c();
         float var3 = this.field_78531_r.field_71474_y.field_74341_c * 0.6F + 0.2F;
         float var4 = var3 * var3 * var3 * 8.0F;
         float var5 = (float)this.field_78531_r.field_71417_B.field_74377_a * var4;
         float var6 = (float)this.field_78531_r.field_71417_B.field_74375_b * var4;
         byte var7 = 1;
         if (this.field_78531_r.field_71474_y.field_74338_d) {
            var7 = -1;
         }

         if (this.field_78531_r.field_71474_y.field_74326_T) {
            this.field_78496_H += var5;
            this.field_78497_I += var6;
            float var8 = var1 - this.field_78492_L;
            this.field_78492_L = var1;
            var5 = this.field_78498_J * var8;
            var6 = this.field_78499_K * var8;
            this.field_78531_r.field_71439_g.func_70082_c(var5, var6 * (float)var7);
         } else {
            this.field_78531_r.field_71439_g.func_70082_c(var5, var6 * (float)var7);
         }
      }

      this.field_78531_r.field_71424_I.func_76319_b();
      if (!this.field_78531_r.field_71454_w) {
         field_78517_a = this.field_78531_r.field_71474_y.field_74337_g;
         ScaledResolution var13 = new ScaledResolution(this.field_78531_r, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
         int var14 = var13.func_78326_a();
         int var16 = var13.func_78328_b();
         int var18 = Mouse.getX() * var14 / this.field_78531_r.field_71443_c;
         int var19 = var16 - Mouse.getY() * var16 / this.field_78531_r.field_71440_d - 1;
         int var20 = this.field_78531_r.field_71474_y.field_74350_i;
         if (this.field_78531_r.field_71441_e != null) {
            this.field_78531_r.field_71424_I.func_76320_a("level");
            if (this.field_78531_r.func_147107_h()) {
               this.func_78471_a(var1, this.field_78510_Z + (long)(1000000000 / var20));
            } else {
               this.func_78471_a(var1, 0L);
            }

            if (OpenGlHelper.field_148824_g) {
               if (this.field_147707_d != null) {
                  GL11.glMatrixMode(5890);
                  GL11.glPushMatrix();
                  GL11.glLoadIdentity();
                  this.field_147707_d.func_148018_a(var1);
                  GL11.glPopMatrix();
               }

               this.field_78531_r.func_147110_a().func_147610_a(true);
            }

            this.field_78510_Z = System.nanoTime();
            this.field_78531_r.field_71424_I.func_76318_c("gui");
            if (!this.field_78531_r.field_71474_y.field_74319_N || this.field_78531_r.field_71462_r != null) {
               GL11.glAlphaFunc(516, 0.1F);
               this.field_78531_r.field_71456_v.func_73830_a(var1, this.field_78531_r.field_71462_r != null, var18, var19);
            }

            this.field_78531_r.field_71424_I.func_76319_b();
         } else {
            GL11.glViewport(0, 0, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            this.func_78478_c();
            this.field_78510_Z = System.nanoTime();
         }

         if (this.field_78531_r.field_71462_r != null) {
            GL11.glClear(256);

            try {
               this.field_78531_r.field_71462_r.func_73863_a(var18, var19, var1);
            } catch (Throwable var12) {
               CrashReport var10 = CrashReport.func_85055_a(var12, "Rendering screen");
               CrashReportCategory var11 = var10.func_85058_a("Screen render details");
               var11.func_71500_a("Screen name", new EntityRenderer$1(this));
               var11.func_71500_a("Mouse location", new EntityRenderer$2(this, var18, var19));
               var11.func_71500_a("Screen size", new EntityRenderer$3(this, var13));
               throw new ReportedException(var10);
            }
         }
      }
   }

   public void func_152430_c(float var1) {
      this.func_78478_c();
      ScaledResolution var2 = new ScaledResolution(this.field_78531_r, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
      int var3 = var2.func_78326_a();
      int var4 = var2.func_78328_b();
      this.field_78531_r.field_71456_v.func_152126_a((float)var3, (float)var4);
   }

   public void func_78471_a(float var1, long var2) {
      this.field_78531_r.field_71424_I.func_76320_a("lightTex");
      if (this.field_78536_aa) {
         this.func_78472_g(var1);
      }

      GL11.glEnable(2884);
      GL11.glEnable(2929);
      GL11.glEnable(3008);
      GL11.glAlphaFunc(516, 0.5F);
      if (this.field_78531_r.field_71451_h == null) {
         this.field_78531_r.field_71451_h = this.field_78531_r.field_71439_g;
      }

      this.field_78531_r.field_71424_I.func_76318_c("pick");
      this.func_78473_a(var1);
      EntityLivingBase var4 = this.field_78531_r.field_71451_h;
      RenderGlobal var5 = this.field_78531_r.field_71438_f;
      EffectRenderer var6 = this.field_78531_r.field_71452_i;
      double var7 = var4.field_70142_S + (var4.field_70165_t - var4.field_70142_S) * (double)var1;
      double var9 = var4.field_70137_T + (var4.field_70163_u - var4.field_70137_T) * (double)var1;
      double var11 = var4.field_70136_U + (var4.field_70161_v - var4.field_70136_U) * (double)var1;
      this.field_78531_r.field_71424_I.func_76318_c("center");

      for(int var13 = 0; var13 < 2; ++var13) {
         if (this.field_78531_r.field_71474_y.field_74337_g) {
            field_78515_b = var13;
            if (field_78515_b == 0) {
               GL11.glColorMask(false, true, true, false);
            } else {
               GL11.glColorMask(true, false, false, false);
            }
         }

         this.field_78531_r.field_71424_I.func_76318_c("clear");
         GL11.glViewport(0, 0, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
         this.func_78466_h(var1);
         GL11.glClear(16640);
         GL11.glEnable(2884);
         this.field_78531_r.field_71424_I.func_76318_c("camera");
         this.func_78479_a(var1, var13);
         ActiveRenderInfo.func_74583_a(this.field_78531_r.field_71439_g, this.field_78531_r.field_71474_y.field_74320_O == 2);
         this.field_78531_r.field_71424_I.func_76318_c("frustrum");
         ClippingHelperImpl.func_78558_a();
         if (this.field_78531_r.field_71474_y.field_151451_c >= 4) {
            this.func_78468_a(-1, var1);
            this.field_78531_r.field_71424_I.func_76318_c("sky");
            var5.func_72714_a(var1);
         }

         GL11.glEnable(2912);
         this.func_78468_a(1, var1);
         if (this.field_78531_r.field_71474_y.field_74348_k != 0) {
            GL11.glShadeModel(7425);
         }

         this.field_78531_r.field_71424_I.func_76318_c("culling");
         Frustrum var14 = new Frustrum();
         var14.func_78547_a(var7, var9, var11);
         this.field_78531_r.field_71438_f.func_72729_a(var14, var1);
         if (var13 == 0) {
            this.field_78531_r.field_71424_I.func_76318_c("updatechunks");

            while(!this.field_78531_r.field_71438_f.func_72716_a(var4, false) && var2 != 0L) {
               long var15 = var2 - System.nanoTime();
               if (var15 < 0L || var15 > 1000000000L) {
                  break;
               }
            }
         }

         if (var4.field_70163_u < 128.0) {
            this.func_82829_a(var5, var1);
         }

         this.field_78531_r.field_71424_I.func_76318_c("prepareterrain");
         this.func_78468_a(0, var1);
         GL11.glEnable(2912);
         this.field_78531_r.func_110434_K().func_110577_a(TextureMap.field_110575_b);
         RenderHelper.func_74518_a();
         this.field_78531_r.field_71424_I.func_76318_c("terrain");
         GL11.glMatrixMode(5888);
         GL11.glPushMatrix();
         var5.func_72719_a(var4, 0, (double)var1);
         GL11.glShadeModel(7424);
         GL11.glAlphaFunc(516, 0.1F);
         if (this.field_78532_q == 0) {
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            RenderHelper.func_74519_b();
            this.field_78531_r.field_71424_I.func_76318_c("entities");
            var5.func_147589_a(var4, var14, var1);
            RenderHelper.func_74518_a();
            this.func_78483_a((double)var1);
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            if (this.field_78531_r.field_71476_x != null
               && var4.func_70055_a(Material.field_151586_h)
               && var4 instanceof EntityPlayer
               && !this.field_78531_r.field_71474_y.field_74319_N) {
               EntityPlayer var17 = (EntityPlayer)var4;
               GL11.glDisable(3008);
               this.field_78531_r.field_71424_I.func_76318_c("outline");
               var5.func_72731_b(var17, this.field_78531_r.field_71476_x, 0, var1);
               GL11.glEnable(3008);
            }
         }

         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         if (this.field_78503_V == 1.0
            && var4 instanceof EntityPlayer
            && !this.field_78531_r.field_71474_y.field_74319_N
            && this.field_78531_r.field_71476_x != null
            && !var4.func_70055_a(Material.field_151586_h)) {
            EntityPlayer var18 = (EntityPlayer)var4;
            GL11.glDisable(3008);
            this.field_78531_r.field_71424_I.func_76318_c("outline");
            var5.func_72731_b(var18, this.field_78531_r.field_71476_x, 0, var1);
            GL11.glEnable(3008);
         }

         this.field_78531_r.field_71424_I.func_76318_c("destroyProgress");
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 1, 1, 0);
         var5.func_72717_a(Tessellator.field_78398_a, (EntityPlayer)var4, var1);
         GL11.glDisable(3042);
         if (this.field_78532_q == 0) {
            this.func_78463_b((double)var1);
            this.field_78531_r.field_71424_I.func_76318_c("litParticles");
            var6.func_78872_b(var4, var1);
            RenderHelper.func_74518_a();
            this.func_78468_a(0, var1);
            this.field_78531_r.field_71424_I.func_76318_c("particles");
            var6.func_78874_a(var4, var1);
            this.func_78483_a((double)var1);
         }

         GL11.glDepthMask(false);
         GL11.glEnable(2884);
         this.field_78531_r.field_71424_I.func_76318_c("weather");
         this.func_78474_d(var1);
         GL11.glDepthMask(true);
         GL11.glDisable(3042);
         GL11.glEnable(2884);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         GL11.glAlphaFunc(516, 0.1F);
         this.func_78468_a(0, var1);
         GL11.glEnable(3042);
         GL11.glDepthMask(false);
         this.field_78531_r.func_110434_K().func_110577_a(TextureMap.field_110575_b);
         if (this.field_78531_r.field_71474_y.field_74347_j) {
            this.field_78531_r.field_71424_I.func_76318_c("water");
            if (this.field_78531_r.field_71474_y.field_74348_k != 0) {
               GL11.glShadeModel(7425);
            }

            GL11.glEnable(3042);
            OpenGlHelper.func_148821_a(770, 771, 1, 0);
            if (this.field_78531_r.field_71474_y.field_74337_g) {
               if (field_78515_b == 0) {
                  GL11.glColorMask(false, true, true, true);
               } else {
                  GL11.glColorMask(true, false, false, true);
               }

               var5.func_72719_a(var4, 1, (double)var1);
            } else {
               var5.func_72719_a(var4, 1, (double)var1);
            }

            GL11.glDisable(3042);
            GL11.glShadeModel(7424);
         } else {
            this.field_78531_r.field_71424_I.func_76318_c("water");
            var5.func_72719_a(var4, 1, (double)var1);
         }

         GL11.glDepthMask(true);
         GL11.glEnable(2884);
         GL11.glDisable(3042);
         GL11.glDisable(2912);
         if (var4.field_70163_u >= 128.0) {
            this.field_78531_r.field_71424_I.func_76318_c("aboveClouds");
            this.func_82829_a(var5, var1);
         }

         this.field_78531_r.field_71424_I.func_76318_c("hand");
         if (this.field_78503_V == 1.0) {
            GL11.glClear(256);
            this.func_78476_b(var1, var13);
         }

         if (!this.field_78531_r.field_71474_y.field_74337_g) {
            this.field_78531_r.field_71424_I.func_76319_b();
            return;
         }
      }

      GL11.glColorMask(true, true, true, false);
      this.field_78531_r.field_71424_I.func_76319_b();
   }

   private void func_82829_a(RenderGlobal var1, float var2) {
      if (this.field_78531_r.field_71474_y.func_74309_c()) {
         this.field_78531_r.field_71424_I.func_76318_c("clouds");
         GL11.glPushMatrix();
         this.func_78468_a(0, var2);
         GL11.glEnable(2912);
         var1.func_72718_b(var2);
         GL11.glDisable(2912);
         this.func_78468_a(1, var2);
         GL11.glPopMatrix();
      }
   }

   private void func_78484_h() {
      float var1 = this.field_78531_r.field_71441_e.func_72867_j(1.0F);
      if (!this.field_78531_r.field_71474_y.field_74347_j) {
         var1 /= 2.0F;
      }

      if (var1 != 0.0F) {
         this.field_78537_ab.setSeed((long)this.field_78529_t * 312987231L);
         EntityLivingBase var2 = this.field_78531_r.field_71451_h;
         WorldClient var3 = this.field_78531_r.field_71441_e;
         int var4 = MathHelper.func_76128_c(var2.field_70165_t);
         int var5 = MathHelper.func_76128_c(var2.field_70163_u);
         int var6 = MathHelper.func_76128_c(var2.field_70161_v);
         byte var7 = 10;
         double var8 = 0.0;
         double var10 = 0.0;
         double var12 = 0.0;
         int var14 = 0;
         int var15 = (int)(100.0F * var1 * var1);
         if (this.field_78531_r.field_71474_y.field_74362_aa == 1) {
            var15 >>= 1;
         } else if (this.field_78531_r.field_71474_y.field_74362_aa == 2) {
            var15 = 0;
         }

         for(int var16 = 0; var16 < var15; ++var16) {
            int var17 = var4 + this.field_78537_ab.nextInt(var7) - this.field_78537_ab.nextInt(var7);
            int var18 = var6 + this.field_78537_ab.nextInt(var7) - this.field_78537_ab.nextInt(var7);
            int var19 = var3.func_72874_g(var17, var18);
            Block var20 = var3.func_147439_a(var17, var19 - 1, var18);
            BiomeGenBase var21 = var3.func_72807_a(var17, var18);
            if (var19 <= var5 + var7 && var19 >= var5 - var7 && var21.func_76738_d() && var21.func_150564_a(var17, var19, var18) >= 0.15F) {
               float var22 = this.field_78537_ab.nextFloat();
               float var23 = this.field_78537_ab.nextFloat();
               if (var20.func_149688_o() == Material.field_151587_i) {
                  this.field_78531_r
                     .field_71452_i
                     .func_78873_a(
                        new EntitySmokeFX(
                           var3,
                           (double)((float)var17 + var22),
                           (double)((float)var19 + 0.1F) - var20.func_149665_z(),
                           (double)((float)var18 + var23),
                           0.0,
                           0.0,
                           0.0
                        )
                     );
               } else if (var20.func_149688_o() != Material.field_151579_a) {
                  if (this.field_78537_ab.nextInt(++var14) == 0) {
                     var8 = (double)((float)var17 + var22);
                     var10 = (double)((float)var19 + 0.1F) - var20.func_149665_z();
                     var12 = (double)((float)var18 + var23);
                  }

                  this.field_78531_r
                     .field_71452_i
                     .func_78873_a(
                        new EntityRainFX(
                           var3, (double)((float)var17 + var22), (double)((float)var19 + 0.1F) - var20.func_149665_z(), (double)((float)var18 + var23)
                        )
                     );
               }
            }
         }

         if (var14 > 0 && this.field_78537_ab.nextInt(3) < this.field_78534_ac++) {
            this.field_78534_ac = 0;
            if (var10 > var2.field_70163_u + 1.0
               && var3.func_72874_g(MathHelper.func_76128_c(var2.field_70165_t), MathHelper.func_76128_c(var2.field_70161_v))
                  > MathHelper.func_76128_c(var2.field_70163_u)) {
               this.field_78531_r.field_71441_e.func_72980_b(var8, var10, var12, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.field_78531_r.field_71441_e.func_72980_b(var8, var10, var12, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }
      }
   }

   protected void func_78474_d(float var1) {
      float var2 = this.field_78531_r.field_71441_e.func_72867_j(var1);
      if (!(var2 <= 0.0F)) {
         this.func_78463_b((double)var1);
         if (this.field_78525_i == null) {
            this.field_78525_i = new float[1024];
            this.field_78522_j = new float[1024];

            for(int var3 = 0; var3 < 32; ++var3) {
               for(int var4 = 0; var4 < 32; ++var4) {
                  float var5 = (float)(var4 - 16);
                  float var6 = (float)(var3 - 16);
                  float var7 = MathHelper.func_76129_c(var5 * var5 + var6 * var6);
                  this.field_78525_i[var3 << 5 | var4] = -var6 / var7;
                  this.field_78522_j[var3 << 5 | var4] = var5 / var7;
               }
            }
         }

         EntityLivingBase var41 = this.field_78531_r.field_71451_h;
         WorldClient var42 = this.field_78531_r.field_71441_e;
         int var43 = MathHelper.func_76128_c(var41.field_70165_t);
         int var44 = MathHelper.func_76128_c(var41.field_70163_u);
         int var45 = MathHelper.func_76128_c(var41.field_70161_v);
         Tessellator var8 = Tessellator.field_78398_a;
         GL11.glDisable(2884);
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         GL11.glAlphaFunc(516, 0.1F);
         double var9 = var41.field_70142_S + (var41.field_70165_t - var41.field_70142_S) * (double)var1;
         double var11 = var41.field_70137_T + (var41.field_70163_u - var41.field_70137_T) * (double)var1;
         double var13 = var41.field_70136_U + (var41.field_70161_v - var41.field_70136_U) * (double)var1;
         int var15 = MathHelper.func_76128_c(var11);
         byte var16 = 5;
         if (this.field_78531_r.field_71474_y.field_74347_j) {
            var16 = 10;
         }

         boolean var17 = false;
         byte var18 = -1;
         float var19 = (float)this.field_78529_t + var1;
         if (this.field_78531_r.field_71474_y.field_74347_j) {
            var16 = 10;
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         var17 = false;

         for(int var20 = var45 - var16; var20 <= var45 + var16; ++var20) {
            for(int var21 = var43 - var16; var21 <= var43 + var16; ++var21) {
               int var22 = (var20 - var45 + 16) * 32 + var21 - var43 + 16;
               float var23 = this.field_78525_i[var22] * 0.5F;
               float var24 = this.field_78522_j[var22] * 0.5F;
               BiomeGenBase var25 = var42.func_72807_a(var21, var20);
               if (var25.func_76738_d() || var25.func_76746_c()) {
                  int var26 = var42.func_72874_g(var21, var20);
                  int var27 = var44 - var16;
                  int var28 = var44 + var16;
                  if (var27 < var26) {
                     var27 = var26;
                  }

                  if (var28 < var26) {
                     var28 = var26;
                  }

                  float var29 = 1.0F;
                  int var30 = var26;
                  if (var26 < var15) {
                     var30 = var15;
                  }

                  if (var27 != var28) {
                     this.field_78537_ab.setSeed((long)(var21 * var21 * 3121 + var21 * 45238971 ^ var20 * var20 * 418711 + var20 * 13761));
                     float var31 = var25.func_150564_a(var21, var27, var20);
                     if (var42.func_72959_q().func_76939_a(var31, var26) >= 0.15F) {
                        if (var18 != 0) {
                           if (var18 >= 0) {
                              var8.func_78381_a();
                           }

                           var18 = 0;
                           this.field_78531_r.func_110434_K().func_110577_a(field_110924_q);
                           var8.func_78382_b();
                        }

                        float var32 = (
                              (float)(this.field_78529_t + var21 * var21 * 3121 + var21 * 45238971 + var20 * var20 * 418711 + var20 * 13761 & 31) + var1
                           )
                           / 32.0F
                           * (3.0F + this.field_78537_ab.nextFloat());
                        double var33 = (double)((float)var21 + 0.5F) - var41.field_70165_t;
                        double var35 = (double)((float)var20 + 0.5F) - var41.field_70161_v;
                        float var37 = MathHelper.func_76133_a(var33 * var33 + var35 * var35) / (float)var16;
                        float var38 = 1.0F;
                        var8.func_78380_c(var42.func_72802_i(var21, var30, var20, 0));
                        var8.func_78369_a(var38, var38, var38, ((1.0F - var37 * var37) * 0.5F + 0.5F) * var2);
                        var8.func_78373_b(-var9 * 1.0, -var11 * 1.0, -var13 * 1.0);
                        var8.func_78374_a(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var27,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var29),
                           (double)((float)var27 * var29 / 4.0F + var32 * var29)
                        );
                        var8.func_78374_a(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var27,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var29),
                           (double)((float)var27 * var29 / 4.0F + var32 * var29)
                        );
                        var8.func_78374_a(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var29),
                           (double)((float)var28 * var29 / 4.0F + var32 * var29)
                        );
                        var8.func_78374_a(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var29),
                           (double)((float)var28 * var29 / 4.0F + var32 * var29)
                        );
                        var8.func_78373_b(0.0, 0.0, 0.0);
                     } else {
                        if (var18 != 1) {
                           if (var18 >= 0) {
                              var8.func_78381_a();
                           }

                           var18 = 1;
                           this.field_78531_r.func_110434_K().func_110577_a(field_110923_r);
                           var8.func_78382_b();
                        }

                        float var47 = ((float)(this.field_78529_t & 511) + var1) / 512.0F;
                        float var48 = this.field_78537_ab.nextFloat() + var19 * 0.01F * (float)this.field_78537_ab.nextGaussian();
                        float var34 = this.field_78537_ab.nextFloat() + var19 * (float)this.field_78537_ab.nextGaussian() * 0.001F;
                        double var49 = (double)((float)var21 + 0.5F) - var41.field_70165_t;
                        double var50 = (double)((float)var20 + 0.5F) - var41.field_70161_v;
                        float var39 = MathHelper.func_76133_a(var49 * var49 + var50 * var50) / (float)var16;
                        float var40 = 1.0F;
                        var8.func_78380_c((var42.func_72802_i(var21, var30, var20, 0) * 3 + 15728880) / 4);
                        var8.func_78369_a(var40, var40, var40, ((1.0F - var39 * var39) * 0.3F + 0.5F) * var2);
                        var8.func_78373_b(-var9 * 1.0, -var11 * 1.0, -var13 * 1.0);
                        var8.func_78374_a(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var27,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var29 + var48),
                           (double)((float)var27 * var29 / 4.0F + var47 * var29 + var34)
                        );
                        var8.func_78374_a(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var27,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var29 + var48),
                           (double)((float)var27 * var29 / 4.0F + var47 * var29 + var34)
                        );
                        var8.func_78374_a(
                           (double)((float)var21 + var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 + var24) + 0.5,
                           (double)(1.0F * var29 + var48),
                           (double)((float)var28 * var29 / 4.0F + var47 * var29 + var34)
                        );
                        var8.func_78374_a(
                           (double)((float)var21 - var23) + 0.5,
                           (double)var28,
                           (double)((float)var20 - var24) + 0.5,
                           (double)(0.0F * var29 + var48),
                           (double)((float)var28 * var29 / 4.0F + var47 * var29 + var34)
                        );
                        var8.func_78373_b(0.0, 0.0, 0.0);
                     }
                  }
               }
            }
         }

         if (var18 >= 0) {
            var8.func_78381_a();
         }

         GL11.glEnable(2884);
         GL11.glDisable(3042);
         GL11.glAlphaFunc(516, 0.1F);
         this.func_78483_a((double)var1);
      }
   }

   public void func_78478_c() {
      ScaledResolution var1 = new ScaledResolution(this.field_78531_r, this.field_78531_r.field_71443_c, this.field_78531_r.field_71440_d);
      GL11.glClear(256);
      GL11.glMatrixMode(5889);
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0, var1.func_78327_c(), var1.func_78324_d(), 0.0, 1000.0, 3000.0);
      GL11.glMatrixMode(5888);
      GL11.glLoadIdentity();
      GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
   }

   private void func_78466_h(float var1) {
      WorldClient var2 = this.field_78531_r.field_71441_e;
      EntityLivingBase var3 = this.field_78531_r.field_71451_h;
      float var4 = 0.25F + 0.75F * (float)this.field_78531_r.field_71474_y.field_151451_c / 16.0F;
      var4 = 1.0F - (float)Math.pow((double)var4, 0.25);
      Vec3 var5 = var2.func_72833_a(this.field_78531_r.field_71451_h, var1);
      float var6 = (float)var5.field_72450_a;
      float var7 = (float)var5.field_72448_b;
      float var8 = (float)var5.field_72449_c;
      Vec3 var9 = var2.func_72948_g(var1);
      this.field_78518_n = (float)var9.field_72450_a;
      this.field_78519_o = (float)var9.field_72448_b;
      this.field_78533_p = (float)var9.field_72449_c;
      if (this.field_78531_r.field_71474_y.field_151451_c >= 4) {
         Vec3 var10 = MathHelper.func_76126_a(var2.func_72929_e(var1)) > 0.0F ? Vec3.func_72443_a(-1.0, 0.0, 0.0) : Vec3.func_72443_a(1.0, 0.0, 0.0);
         float var11 = (float)var3.func_70676_i(var1).func_72430_b(var10);
         if (var11 < 0.0F) {
            var11 = 0.0F;
         }

         if (var11 > 0.0F) {
            float[] var12 = var2.field_73011_w.func_76560_a(var2.func_72826_c(var1), var1);
            if (var12 != null) {
               var11 *= var12[3];
               this.field_78518_n = this.field_78518_n * (1.0F - var11) + var12[0] * var11;
               this.field_78519_o = this.field_78519_o * (1.0F - var11) + var12[1] * var11;
               this.field_78533_p = this.field_78533_p * (1.0F - var11) + var12[2] * var11;
            }
         }
      }

      this.field_78518_n += (var6 - this.field_78518_n) * var4;
      this.field_78519_o += (var7 - this.field_78519_o) * var4;
      this.field_78533_p += (var8 - this.field_78533_p) * var4;
      float var20 = var2.func_72867_j(var1);
      if (var20 > 0.0F) {
         float var22 = 1.0F - var20 * 0.5F;
         float var24 = 1.0F - var20 * 0.4F;
         this.field_78518_n *= var22;
         this.field_78519_o *= var22;
         this.field_78533_p *= var24;
      }

      float var23 = var2.func_72819_i(var1);
      if (var23 > 0.0F) {
         float var25 = 1.0F - var23 * 0.5F;
         this.field_78518_n *= var25;
         this.field_78519_o *= var25;
         this.field_78533_p *= var25;
      }

      Block var26 = ActiveRenderInfo.func_151460_a(this.field_78531_r.field_71441_e, var3, var1);
      if (this.field_78500_U) {
         Vec3 var13 = var2.func_72824_f(var1);
         this.field_78518_n = (float)var13.field_72450_a;
         this.field_78519_o = (float)var13.field_72448_b;
         this.field_78533_p = (float)var13.field_72449_c;
      } else if (var26.func_149688_o() == Material.field_151586_h) {
         float var27 = (float)EnchantmentHelper.func_77501_a(var3) * 0.2F;
         this.field_78518_n = 0.02F + var27;
         this.field_78519_o = 0.02F + var27;
         this.field_78533_p = 0.2F + var27;
      } else if (var26.func_149688_o() == Material.field_151587_i) {
         this.field_78518_n = 0.6F;
         this.field_78519_o = 0.1F;
         this.field_78533_p = 0.0F;
      }

      float var28 = this.field_78535_ad + (this.field_78539_ae - this.field_78535_ad) * var1;
      this.field_78518_n *= var28;
      this.field_78519_o *= var28;
      this.field_78533_p *= var28;
      double var14 = (var3.field_70137_T + (var3.field_70163_u - var3.field_70137_T) * (double)var1) * var2.field_73011_w.func_76565_k();
      if (var3.func_70644_a(Potion.field_76440_q)) {
         int var16 = var3.func_70660_b(Potion.field_76440_q).func_76459_b();
         if (var16 < 20) {
            var14 *= (double)(1.0F - (float)var16 / 20.0F);
         } else {
            var14 = 0.0;
         }
      }

      if (var14 < 1.0) {
         if (var14 < 0.0) {
            var14 = 0.0;
         }

         var14 *= var14;
         this.field_78518_n = (float)((double)this.field_78518_n * var14);
         this.field_78519_o = (float)((double)this.field_78519_o * var14);
         this.field_78533_p = (float)((double)this.field_78533_p * var14);
      }

      if (this.field_82831_U > 0.0F) {
         float var30 = this.field_82832_V + (this.field_82831_U - this.field_82832_V) * var1;
         this.field_78518_n = this.field_78518_n * (1.0F - var30) + this.field_78518_n * 0.7F * var30;
         this.field_78519_o = this.field_78519_o * (1.0F - var30) + this.field_78519_o * 0.6F * var30;
         this.field_78533_p = this.field_78533_p * (1.0F - var30) + this.field_78533_p * 0.6F * var30;
      }

      if (var3.func_70644_a(Potion.field_76439_r)) {
         float var31 = this.func_82830_a(this.field_78531_r.field_71439_g, var1);
         float var17 = 1.0F / this.field_78518_n;
         if (var17 > 1.0F / this.field_78519_o) {
            var17 = 1.0F / this.field_78519_o;
         }

         if (var17 > 1.0F / this.field_78533_p) {
            var17 = 1.0F / this.field_78533_p;
         }

         this.field_78518_n = this.field_78518_n * (1.0F - var31) + this.field_78518_n * var17 * var31;
         this.field_78519_o = this.field_78519_o * (1.0F - var31) + this.field_78519_o * var17 * var31;
         this.field_78533_p = this.field_78533_p * (1.0F - var31) + this.field_78533_p * var17 * var31;
      }

      if (this.field_78531_r.field_71474_y.field_74337_g) {
         float var32 = (this.field_78518_n * 30.0F + this.field_78519_o * 59.0F + this.field_78533_p * 11.0F) / 100.0F;
         float var33 = (this.field_78518_n * 30.0F + this.field_78519_o * 70.0F) / 100.0F;
         float var18 = (this.field_78518_n * 30.0F + this.field_78533_p * 70.0F) / 100.0F;
         this.field_78518_n = var32;
         this.field_78519_o = var33;
         this.field_78533_p = var18;
      }

      GL11.glClearColor(this.field_78518_n, this.field_78519_o, this.field_78533_p, 0.0F);
   }

   private void func_78468_a(int var1, float var2) {
      EntityLivingBase var3 = this.field_78531_r.field_71451_h;
      boolean var4 = false;
      if (var3 instanceof EntityPlayer) {
         var4 = ((EntityPlayer)var3).field_71075_bZ.field_75098_d;
      }

      if (var1 == 999) {
         GL11.glFog(2918, this.func_78469_a(0.0F, 0.0F, 0.0F, 1.0F));
         GL11.glFogi(2917, 9729);
         GL11.glFogf(2915, 0.0F);
         GL11.glFogf(2916, 8.0F);
         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            GL11.glFogi(34138, 34139);
         }

         GL11.glFogf(2915, 0.0F);
      } else {
         GL11.glFog(2918, this.func_78469_a(this.field_78518_n, this.field_78519_o, this.field_78533_p, 1.0F));
         GL11.glNormal3f(0.0F, -1.0F, 0.0F);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Block var5 = ActiveRenderInfo.func_151460_a(this.field_78531_r.field_71441_e, var3, var2);
         if (var3.func_70644_a(Potion.field_76440_q)) {
            float var6 = 5.0F;
            int var7 = var3.func_70660_b(Potion.field_76440_q).func_76459_b();
            if (var7 < 20) {
               var6 = 5.0F + (this.field_78530_s - 5.0F) * (1.0F - (float)var7 / 20.0F);
            }

            GL11.glFogi(2917, 9729);
            if (var1 < 0) {
               GL11.glFogf(2915, 0.0F);
               GL11.glFogf(2916, var6 * 0.8F);
            } else {
               GL11.glFogf(2915, var6 * 0.25F);
               GL11.glFogf(2916, var6);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
               GL11.glFogi(34138, 34139);
            }
         } else if (this.field_78500_U) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.1F);
         } else if (var5.func_149688_o() == Material.field_151586_h) {
            GL11.glFogi(2917, 2048);
            if (var3.func_70644_a(Potion.field_76427_o)) {
               GL11.glFogf(2914, 0.05F);
            } else {
               GL11.glFogf(2914, 0.1F - (float)EnchantmentHelper.func_77501_a(var3) * 0.03F);
            }
         } else if (var5.func_149688_o() == Material.field_151587_i) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 2.0F);
         } else {
            float var10 = this.field_78530_s;
            if (this.field_78531_r.field_71441_e.field_73011_w.func_76564_j() && !var4) {
               double var11 = (double)((var3.func_70070_b(var2) & 15728640) >> 20) / 16.0
                  + (var3.field_70137_T + (var3.field_70163_u - var3.field_70137_T) * (double)var2 + 4.0) / 32.0;
               if (var11 < 1.0) {
                  if (var11 < 0.0) {
                     var11 = 0.0;
                  }

                  var11 *= var11;
                  float var9 = 100.0F * (float)var11;
                  if (var9 < 5.0F) {
                     var9 = 5.0F;
                  }

                  if (var10 > var9) {
                     var10 = var9;
                  }
               }
            }

            GL11.glFogi(2917, 9729);
            if (var1 < 0) {
               GL11.glFogf(2915, 0.0F);
               GL11.glFogf(2916, var10);
            } else {
               GL11.glFogf(2915, var10 * 0.75F);
               GL11.glFogf(2916, var10);
            }

            if (GLContext.getCapabilities().GL_NV_fog_distance) {
               GL11.glFogi(34138, 34139);
            }

            if (this.field_78531_r.field_71441_e.field_73011_w.func_76568_b((int)var3.field_70165_t, (int)var3.field_70161_v)) {
               GL11.glFogf(2915, var10 * 0.05F);
               GL11.glFogf(2916, Math.min(var10, 192.0F) * 0.5F);
            }
         }

         GL11.glEnable(2903);
         GL11.glColorMaterial(1028, 4608);
      }
   }

   private FloatBuffer func_78469_a(float var1, float var2, float var3, float var4) {
      ((Buffer)this.field_78521_m).clear();
      this.field_78521_m.put(var1).put(var2).put(var3).put(var4);
      ((Buffer)this.field_78521_m).flip();
      return this.field_78521_m;
   }

   public MapItemRenderer func_147701_i() {
      return this.field_147709_v;
   }
}
