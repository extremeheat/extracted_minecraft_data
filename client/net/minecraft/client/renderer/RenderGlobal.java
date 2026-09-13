package net.minecraft.client.renderer;

import com.google.common.collect.Maps;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityFishWakeFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition$MovingObjectType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;

public class RenderGlobal implements IWorldAccess {
   private static final Logger field_147599_m = LogManager.getLogger();
   private static final ResourceLocation field_110927_h = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation field_110928_i = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation field_110925_j = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation field_110926_k = new ResourceLocation("textures/environment/end_sky.png");
   public List field_147598_a = new ArrayList();
   private WorldClient field_72769_h;
   private final TextureManager field_72770_i;
   private List field_72767_j = new ArrayList();
   private WorldRenderer[] field_72768_k;
   private WorldRenderer[] field_72765_l;
   private int field_72766_m;
   private int field_72763_n;
   private int field_72764_o;
   private int field_72778_p;
   private Minecraft field_72777_q;
   private RenderBlocks field_147592_B;
   private IntBuffer field_72775_s;
   private boolean field_72774_t;
   private int field_72773_u;
   private int field_72772_v;
   private int field_72771_w;
   private int field_72781_x;
   private int field_72780_y;
   private int field_72779_z;
   private int field_72741_A;
   private int field_72742_B;
   private int field_72743_C;
   private int field_72737_D;
   private final Map field_72738_E = new HashMap();
   private final Map field_147593_P = Maps.newHashMap();
   private IIcon[] field_94141_F;
   private boolean field_147595_R;
   private int field_147594_S;
   private int field_72739_F = -1;
   private int field_72740_G = 2;
   private int field_72748_H;
   private int field_72749_I;
   private int field_72750_J;
   IntBuffer field_72761_c = GLAllocation.func_74527_f(64);
   private int field_72751_K;
   private int field_72744_L;
   private int field_72745_M;
   private int field_72746_N;
   private int field_72747_O;
   private int field_72753_P;
   private int field_72752_Q;
   private List field_72755_R = new ArrayList();
   private RenderList[] field_72754_S = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
   double field_72758_d = -9999.0;
   double field_72759_e = -9999.0;
   double field_72756_f = -9999.0;
   double field_147596_f = -9999.0;
   double field_147597_g = -9999.0;
   double field_147602_h = -9999.0;
   int field_147603_i = -999;
   int field_147600_j = -999;
   int field_147601_k = -999;
   int field_72757_g;

   public RenderGlobal(Minecraft var1) {
      super();
      this.field_72777_q = var1;
      this.field_72770_i = var1.func_110434_K();
      byte var2 = 34;
      byte var3 = 16;
      this.field_72778_p = GLAllocation.func_74526_a(var2 * var2 * var3 * 3);
      this.field_147595_R = false;
      this.field_147594_S = GLAllocation.func_74526_a(1);
      this.field_72774_t = OpenGlCapsChecker.func_74371_a();
      if (this.field_72774_t) {
         ((Buffer)this.field_72761_c).clear();
         this.field_72775_s = GLAllocation.func_74527_f(var2 * var2 * var3);
         ((Buffer)this.field_72775_s).clear();
         ((Buffer)this.field_72775_s).position(0);
         ((Buffer)this.field_72775_s).limit(var2 * var2 * var3);
         ARBOcclusionQuery.glGenQueriesARB(this.field_72775_s);
      }

      this.field_72772_v = GLAllocation.func_74526_a(3);
      GL11.glPushMatrix();
      GL11.glNewList(this.field_72772_v, 4864);
      this.func_72730_g();
      GL11.glEndList();
      GL11.glPopMatrix();
      Tessellator var4 = Tessellator.field_78398_a;
      this.field_72771_w = this.field_72772_v + 1;
      GL11.glNewList(this.field_72771_w, 4864);
      byte var6 = 64;
      int var7 = 256 / var6 + 2;
      float var5 = 16.0F;

      for(int var8 = -var6 * var7; var8 <= var6 * var7; var8 += var6) {
         for(int var9 = -var6 * var7; var9 <= var6 * var7; var9 += var6) {
            var4.func_78382_b();
            var4.func_78377_a((double)(var8 + 0), (double)var5, (double)(var9 + 0));
            var4.func_78377_a((double)(var8 + var6), (double)var5, (double)(var9 + 0));
            var4.func_78377_a((double)(var8 + var6), (double)var5, (double)(var9 + var6));
            var4.func_78377_a((double)(var8 + 0), (double)var5, (double)(var9 + var6));
            var4.func_78381_a();
         }
      }

      GL11.glEndList();
      this.field_72781_x = this.field_72772_v + 2;
      GL11.glNewList(this.field_72781_x, 4864);
      var5 = -16.0F;
      var4.func_78382_b();

      for(int var11 = -var6 * var7; var11 <= var6 * var7; var11 += var6) {
         for(int var12 = -var6 * var7; var12 <= var6 * var7; var12 += var6) {
            var4.func_78377_a((double)(var11 + var6), (double)var5, (double)(var12 + 0));
            var4.func_78377_a((double)(var11 + 0), (double)var5, (double)(var12 + 0));
            var4.func_78377_a((double)(var11 + 0), (double)var5, (double)(var12 + var6));
            var4.func_78377_a((double)(var11 + var6), (double)var5, (double)(var12 + var6));
         }
      }

      var4.func_78381_a();
      GL11.glEndList();
   }

   private void func_72730_g() {
      Random var1 = new Random(10842L);
      Tessellator var2 = Tessellator.field_78398_a;
      var2.func_78382_b();

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var1.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var1.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var1.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var1.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var12 < 1.0 && var12 > 0.01) {
            var12 = 1.0 / Math.sqrt(var12);
            var4 *= var12;
            var6 *= var12;
            var8 *= var12;
            double var14 = var4 * 100.0;
            double var16 = var6 * 100.0;
            double var18 = var8 * 100.0;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var1.nextDouble() * 3.141592653589793 * 2.0;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + var39 * var30;
               double var55 = var39 * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               var2.func_78377_a(var14 + var57, var16 + var53, var18 + var61);
            }
         }
      }

      var2.func_78381_a();
   }

   public void func_72732_a(WorldClient var1) {
      if (this.field_72769_h != null) {
         this.field_72769_h.func_72848_b(this);
      }

      this.field_72758_d = -9999.0;
      this.field_72759_e = -9999.0;
      this.field_72756_f = -9999.0;
      this.field_147596_f = -9999.0;
      this.field_147597_g = -9999.0;
      this.field_147602_h = -9999.0;
      this.field_147603_i = -9999;
      this.field_147600_j = -9999;
      this.field_147601_k = -9999;
      RenderManager.field_78727_a.func_78717_a(var1);
      this.field_72769_h = var1;
      this.field_147592_B = new RenderBlocks(var1);
      if (var1 != null) {
         var1.func_72954_a(this);
         this.func_72712_a();
      }
   }

   public void func_72712_a() {
      if (this.field_72769_h != null) {
         Blocks.field_150362_t.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
         Blocks.field_150361_u.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
         this.field_72739_F = this.field_72777_q.field_71474_y.field_151451_c;
         if (this.field_72765_l != null) {
            for(int var1 = 0; var1 < this.field_72765_l.length; ++var1) {
               this.field_72765_l[var1].func_78911_c();
            }
         }

         int var7 = this.field_72739_F * 2 + 1;
         this.field_72766_m = var7;
         this.field_72763_n = 16;
         this.field_72764_o = var7;
         this.field_72765_l = new WorldRenderer[this.field_72766_m * this.field_72763_n * this.field_72764_o];
         this.field_72768_k = new WorldRenderer[this.field_72766_m * this.field_72763_n * this.field_72764_o];
         int var2 = 0;
         int var3 = 0;
         this.field_72780_y = 0;
         this.field_72779_z = 0;
         this.field_72741_A = 0;
         this.field_72742_B = this.field_72766_m;
         this.field_72743_C = this.field_72763_n;
         this.field_72737_D = this.field_72764_o;

         for(int var4 = 0; var4 < this.field_72767_j.size(); ++var4) {
            ((WorldRenderer)this.field_72767_j.get(var4)).field_78939_q = false;
         }

         this.field_72767_j.clear();
         this.field_147598_a.clear();
         this.func_147584_b();

         for(int var8 = 0; var8 < this.field_72766_m; ++var8) {
            for(int var5 = 0; var5 < this.field_72763_n; ++var5) {
               for(int var6 = 0; var6 < this.field_72764_o; ++var6) {
                  this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8] = new WorldRenderer(
                     this.field_72769_h, this.field_147598_a, var8 * 16, var5 * 16, var6 * 16, this.field_72778_p + var2
                  );
                  if (this.field_72774_t) {
                     this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8].field_78934_v = this.field_72775_s.get(var3);
                  }

                  this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8].field_78935_u = false;
                  this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8].field_78936_t = true;
                  this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8].field_78927_l = true;
                  this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8].field_78937_s = var3++;
                  this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8].func_78914_f();
                  this.field_72768_k[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8] = this.field_72765_l[(var6 * this.field_72763_n + var5)
                        * this.field_72766_m
                     + var8];
                  this.field_72767_j.add(this.field_72765_l[(var6 * this.field_72763_n + var5) * this.field_72766_m + var8]);
                  var2 += 3;
               }
            }
         }

         if (this.field_72769_h != null) {
            EntityLivingBase var9 = this.field_72777_q.field_71451_h;
            if (var9 != null) {
               this.func_72722_c(
                  MathHelper.func_76128_c(var9.field_70165_t), MathHelper.func_76128_c(var9.field_70163_u), MathHelper.func_76128_c(var9.field_70161_v)
               );
               Arrays.sort(this.field_72768_k, new EntitySorter(var9));
            }
         }

         this.field_72740_G = 2;
      }
   }

   public void func_147589_a(EntityLivingBase var1, ICamera var2, float var3) {
      if (this.field_72740_G > 0) {
         --this.field_72740_G;
      } else {
         double var4 = var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var3;
         double var6 = var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var3;
         double var8 = var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var3;
         this.field_72769_h.field_72984_F.func_76320_a("prepare");
         TileEntityRendererDispatcher.field_147556_a
            .func_147542_a(this.field_72769_h, this.field_72777_q.func_110434_K(), this.field_72777_q.field_71466_p, this.field_72777_q.field_71451_h, var3);
         RenderManager.field_78727_a
            .func_147938_a(
               this.field_72769_h,
               this.field_72777_q.func_110434_K(),
               this.field_72777_q.field_71466_p,
               this.field_72777_q.field_71451_h,
               this.field_72777_q.field_147125_j,
               this.field_72777_q.field_71474_y,
               var3
            );
         this.field_72748_H = 0;
         this.field_72749_I = 0;
         this.field_72750_J = 0;
         EntityLivingBase var10 = this.field_72777_q.field_71451_h;
         double var11 = var10.field_70142_S + (var10.field_70165_t - var10.field_70142_S) * (double)var3;
         double var13 = var10.field_70137_T + (var10.field_70163_u - var10.field_70137_T) * (double)var3;
         double var15 = var10.field_70136_U + (var10.field_70161_v - var10.field_70136_U) * (double)var3;
         TileEntityRendererDispatcher.field_147554_b = var11;
         TileEntityRendererDispatcher.field_147555_c = var13;
         TileEntityRendererDispatcher.field_147552_d = var15;
         this.field_72769_h.field_72984_F.func_76318_c("staticentities");
         if (this.field_147595_R) {
            RenderManager.field_78725_b = 0.0;
            RenderManager.field_78726_c = 0.0;
            RenderManager.field_78723_d = 0.0;
            this.func_147591_f();
         }

         GL11.glMatrixMode(5888);
         GL11.glPushMatrix();
         GL11.glTranslated(-var11, -var13, -var15);
         GL11.glCallList(this.field_147594_S);
         GL11.glPopMatrix();
         RenderManager.field_78725_b = var11;
         RenderManager.field_78726_c = var13;
         RenderManager.field_78723_d = var15;
         this.field_72777_q.field_71460_t.func_78463_b((double)var3);
         this.field_72769_h.field_72984_F.func_76318_c("global");
         List var17 = this.field_72769_h.func_72910_y();
         this.field_72748_H = var17.size();

         for(int var18 = 0; var18 < this.field_72769_h.field_73007_j.size(); ++var18) {
            Entity var19 = (Entity)this.field_72769_h.field_73007_j.get(var18);
            ++this.field_72749_I;
            if (var19.func_145770_h(var4, var6, var8)) {
               RenderManager.field_78727_a.func_147937_a(var19, var3);
            }
         }

         this.field_72769_h.field_72984_F.func_76318_c("entities");

         for(int var23 = 0; var23 < var17.size(); ++var23) {
            Entity var25 = (Entity)var17.get(var23);
            boolean var20 = var25.func_145770_h(var4, var6, var8)
               && (var25.field_70158_ak || var2.func_78546_a(var25.field_70121_D) || var25.field_70153_n == this.field_72777_q.field_71439_g);
            if (!var20 && var25 instanceof EntityLiving) {
               EntityLiving var21 = (EntityLiving)var25;
               if (var21.func_110167_bD() && var21.func_110166_bE() != null) {
                  Entity var22 = var21.func_110166_bE();
                  var20 = var2.func_78546_a(var22.field_70121_D);
               }
            }

            if (var20
               && (
                  var25 != this.field_72777_q.field_71451_h
                     || this.field_72777_q.field_71474_y.field_74320_O != 0
                     || this.field_72777_q.field_71451_h.func_70608_bn()
               )
               && this.field_72769_h.func_72899_e(MathHelper.func_76128_c(var25.field_70165_t), 0, MathHelper.func_76128_c(var25.field_70161_v))) {
               ++this.field_72749_I;
               RenderManager.field_78727_a.func_147937_a(var25, var3);
            }
         }

         this.field_72769_h.field_72984_F.func_76318_c("blockentities");
         RenderHelper.func_74519_b();

         for(int var24 = 0; var24 < this.field_147598_a.size(); ++var24) {
            TileEntityRendererDispatcher.field_147556_a.func_147544_a((TileEntity)this.field_147598_a.get(var24), var3);
         }

         this.field_72777_q.field_71460_t.func_78483_a((double)var3);
         this.field_72769_h.field_72984_F.func_76319_b();
      }
   }

   public String func_72735_c() {
      return "C: "
         + this.field_72746_N
         + "/"
         + this.field_72751_K
         + ". F: "
         + this.field_72744_L
         + ", O: "
         + this.field_72745_M
         + ", E: "
         + this.field_72747_O;
   }

   public String func_72723_d() {
      return "E: "
         + this.field_72749_I
         + "/"
         + this.field_72748_H
         + ". B: "
         + this.field_72750_J
         + ", I: "
         + (this.field_72748_H - this.field_72750_J - this.field_72749_I);
   }

   @Override
   public void func_147584_b() {
      this.field_147595_R = true;
   }

   public void func_147591_f() {
      this.field_72769_h.field_72984_F.func_76320_a("staticentityrebuild");
      GL11.glPushMatrix();
      GL11.glNewList(this.field_147594_S, 4864);
      List var1 = this.field_72769_h.func_72910_y();
      this.field_147595_R = false;

      for(int var2 = 0; var2 < var1.size(); ++var2) {
         Entity var3 = (Entity)var1.get(var2);
         if (RenderManager.field_78727_a.func_78713_a(var3).func_147905_a()) {
            this.field_147595_R = this.field_147595_R || !RenderManager.field_78727_a.func_147936_a(var3, 0.0F, true);
         }
      }

      GL11.glEndList();
      GL11.glPopMatrix();
      this.field_72769_h.field_72984_F.func_76319_b();
   }

   private void func_72722_c(int var1, int var2, int var3) {
      var1 -= 8;
      var2 -= 8;
      var3 -= 8;
      this.field_72780_y = 2147483647;
      this.field_72779_z = 2147483647;
      this.field_72741_A = 2147483647;
      this.field_72742_B = -2147483648;
      this.field_72743_C = -2147483648;
      this.field_72737_D = -2147483648;
      int var4 = this.field_72766_m * 16;
      int var5 = var4 / 2;

      for(int var6 = 0; var6 < this.field_72766_m; ++var6) {
         int var7 = var6 * 16;
         int var8 = var7 + var5 - var1;
         if (var8 < 0) {
            var8 -= var4 - 1;
         }

         var8 /= var4;
         var7 -= var8 * var4;
         if (var7 < this.field_72780_y) {
            this.field_72780_y = var7;
         }

         if (var7 > this.field_72742_B) {
            this.field_72742_B = var7;
         }

         for(int var9 = 0; var9 < this.field_72764_o; ++var9) {
            int var10 = var9 * 16;
            int var11 = var10 + var5 - var3;
            if (var11 < 0) {
               var11 -= var4 - 1;
            }

            var11 /= var4;
            var10 -= var11 * var4;
            if (var10 < this.field_72741_A) {
               this.field_72741_A = var10;
            }

            if (var10 > this.field_72737_D) {
               this.field_72737_D = var10;
            }

            for(int var12 = 0; var12 < this.field_72763_n; ++var12) {
               int var13 = var12 * 16;
               if (var13 < this.field_72779_z) {
                  this.field_72779_z = var13;
               }

               if (var13 > this.field_72743_C) {
                  this.field_72743_C = var13;
               }

               WorldRenderer var14 = this.field_72765_l[(var9 * this.field_72763_n + var12) * this.field_72766_m + var6];
               boolean var15 = var14.field_78939_q;
               var14.func_78913_a(var7, var13, var10);
               if (!var15 && var14.field_78939_q) {
                  this.field_72767_j.add(var14);
               }
            }
         }
      }
   }

   public int func_72719_a(EntityLivingBase var1, int var2, double var3) {
      this.field_72769_h.field_72984_F.func_76320_a("sortchunks");

      for(int var5 = 0; var5 < 10; ++var5) {
         this.field_72752_Q = (this.field_72752_Q + 1) % this.field_72765_l.length;
         WorldRenderer var6 = this.field_72765_l[this.field_72752_Q];
         if (var6.field_78939_q && !this.field_72767_j.contains(var6)) {
            this.field_72767_j.add(var6);
         }
      }

      if (this.field_72777_q.field_71474_y.field_151451_c != this.field_72739_F) {
         this.func_72712_a();
      }

      if (var2 == 0) {
         this.field_72751_K = 0;
         this.field_72753_P = 0;
         this.field_72744_L = 0;
         this.field_72745_M = 0;
         this.field_72746_N = 0;
         this.field_72747_O = 0;
      }

      double var39 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * var3;
      double var7 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * var3;
      double var9 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * var3;
      double var11 = var1.field_70165_t - this.field_72758_d;
      double var13 = var1.field_70163_u - this.field_72759_e;
      double var15 = var1.field_70161_v - this.field_72756_f;
      if (this.field_147603_i != var1.field_70176_ah
         || this.field_147600_j != var1.field_70162_ai
         || this.field_147601_k != var1.field_70164_aj
         || var11 * var11 + var13 * var13 + var15 * var15 > 16.0) {
         this.field_72758_d = var1.field_70165_t;
         this.field_72759_e = var1.field_70163_u;
         this.field_72756_f = var1.field_70161_v;
         this.field_147603_i = var1.field_70176_ah;
         this.field_147600_j = var1.field_70162_ai;
         this.field_147601_k = var1.field_70164_aj;
         this.func_72722_c(
            MathHelper.func_76128_c(var1.field_70165_t), MathHelper.func_76128_c(var1.field_70163_u), MathHelper.func_76128_c(var1.field_70161_v)
         );
         Arrays.sort(this.field_72768_k, new EntitySorter(var1));
      }

      double var17 = var1.field_70165_t - this.field_147596_f;
      double var19 = var1.field_70163_u - this.field_147597_g;
      double var21 = var1.field_70161_v - this.field_147602_h;
      if (var17 * var17 + var19 * var19 + var21 * var21 > 1.0) {
         this.field_147596_f = var1.field_70165_t;
         this.field_147597_g = var1.field_70163_u;
         this.field_147602_h = var1.field_70161_v;

         for(int var23 = 0; var23 < 27; ++var23) {
            this.field_72768_k[var23].func_147889_b(var1);
         }
      }

      RenderHelper.func_74518_a();
      int var40 = 0;
      if (this.field_72774_t && this.field_72777_q.field_71474_y.field_74349_h && !this.field_72777_q.field_71474_y.field_74337_g && var2 == 0) {
         int var24 = 0;
         int var25 = 16;
         this.func_72720_a(var24, var25);

         for(int var26 = var24; var26 < var25; ++var26) {
            this.field_72768_k[var26].field_78936_t = true;
         }

         this.field_72769_h.field_72984_F.func_76318_c("render");
         var40 += this.func_72724_a(var24, var25, var2, var3);

         do {
            this.field_72769_h.field_72984_F.func_76318_c("occ");
            var24 = var25;
            var25 *= 2;
            if (var25 > this.field_72768_k.length) {
               var25 = this.field_72768_k.length;
            }

            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(3008);
            GL11.glDisable(2912);
            GL11.glColorMask(false, false, false, false);
            GL11.glDepthMask(false);
            this.field_72769_h.field_72984_F.func_76320_a("check");
            this.func_72720_a(var24, var25);
            this.field_72769_h.field_72984_F.func_76319_b();
            GL11.glPushMatrix();
            float var43 = 0.0F;
            float var27 = 0.0F;
            float var28 = 0.0F;

            for(int var29 = var24; var29 < var25; ++var29) {
               if (this.field_72768_k[var29].func_78906_e()) {
                  this.field_72768_k[var29].field_78927_l = false;
               } else {
                  if (!this.field_72768_k[var29].field_78927_l) {
                     this.field_72768_k[var29].field_78936_t = true;
                  }

                  if (this.field_72768_k[var29].field_78927_l && !this.field_72768_k[var29].field_78935_u) {
                     float var30 = MathHelper.func_76129_c(this.field_72768_k[var29].func_78912_a(var1));
                     int var31 = (int)(1.0F + var30 / 128.0F);
                     if (this.field_72773_u % var31 == var29 % var31) {
                        WorldRenderer var32 = this.field_72768_k[var29];
                        float var33 = (float)((double)var32.field_78918_f - var39);
                        float var34 = (float)((double)var32.field_78919_g - var7);
                        float var35 = (float)((double)var32.field_78931_h - var9);
                        float var36 = var33 - var43;
                        float var37 = var34 - var27;
                        float var38 = var35 - var28;
                        if (var36 != 0.0F || var37 != 0.0F || var38 != 0.0F) {
                           GL11.glTranslatef(var36, var37, var38);
                           var43 += var36;
                           var27 += var37;
                           var28 += var38;
                        }

                        this.field_72769_h.field_72984_F.func_76320_a("bb");
                        ARBOcclusionQuery.glBeginQueryARB(35092, this.field_72768_k[var29].field_78934_v);
                        this.field_72768_k[var29].func_78904_d();
                        ARBOcclusionQuery.glEndQueryARB(35092);
                        this.field_72769_h.field_72984_F.func_76319_b();
                        this.field_72768_k[var29].field_78935_u = true;
                     }
                  }
               }
            }

            GL11.glPopMatrix();
            if (this.field_72777_q.field_71474_y.field_74337_g) {
               if (EntityRenderer.field_78515_b == 0) {
                  GL11.glColorMask(false, true, true, true);
               } else {
                  GL11.glColorMask(true, false, false, true);
               }
            } else {
               GL11.glColorMask(true, true, true, true);
            }

            GL11.glDepthMask(true);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glEnable(2912);
            this.field_72769_h.field_72984_F.func_76318_c("render");
            var40 += this.func_72724_a(var24, var25, var2, var3);
         } while(var25 < this.field_72768_k.length);
      } else {
         this.field_72769_h.field_72984_F.func_76318_c("render");
         var40 += this.func_72724_a(0, this.field_72768_k.length, var2, var3);
      }

      this.field_72769_h.field_72984_F.func_76319_b();
      return var40;
   }

   private void func_72720_a(int var1, int var2) {
      for(int var3 = var1; var3 < var2; ++var3) {
         if (this.field_72768_k[var3].field_78935_u) {
            ((Buffer)this.field_72761_c).clear();
            ARBOcclusionQuery.glGetQueryObjectuARB(this.field_72768_k[var3].field_78934_v, 34919, this.field_72761_c);
            if (this.field_72761_c.get(0) != 0) {
               this.field_72768_k[var3].field_78935_u = false;
               ((Buffer)this.field_72761_c).clear();
               ARBOcclusionQuery.glGetQueryObjectuARB(this.field_72768_k[var3].field_78934_v, 34918, this.field_72761_c);
               this.field_72768_k[var3].field_78936_t = this.field_72761_c.get(0) != 0;
            }
         }
      }
   }

   private int func_72724_a(int var1, int var2, int var3, double var4) {
      this.field_72755_R.clear();
      int var6 = 0;
      int var7 = var1;
      int var8 = var2;
      byte var9 = 1;
      if (var3 == 1) {
         var7 = this.field_72768_k.length - 1 - var1;
         var8 = this.field_72768_k.length - 1 - var2;
         var9 = -1;
      }

      for(int var10 = var7; var10 != var8; var10 += var9) {
         if (var3 == 0) {
            ++this.field_72751_K;
            if (this.field_72768_k[var10].field_78928_m[var3]) {
               ++this.field_72747_O;
            } else if (!this.field_72768_k[var10].field_78927_l) {
               ++this.field_72744_L;
            } else if (this.field_72774_t && !this.field_72768_k[var10].field_78936_t) {
               ++this.field_72745_M;
            } else {
               ++this.field_72746_N;
            }
         }

         if (!this.field_72768_k[var10].field_78928_m[var3]
            && this.field_72768_k[var10].field_78927_l
            && (!this.field_72774_t || this.field_72768_k[var10].field_78936_t)) {
            int var11 = this.field_72768_k[var10].func_78909_a(var3);
            if (var11 >= 0) {
               this.field_72755_R.add(this.field_72768_k[var10]);
               ++var6;
            }
         }
      }

      EntityLivingBase var22 = this.field_72777_q.field_71451_h;
      double var23 = var22.field_70142_S + (var22.field_70165_t - var22.field_70142_S) * var4;
      double var13 = var22.field_70137_T + (var22.field_70163_u - var22.field_70137_T) * var4;
      double var15 = var22.field_70136_U + (var22.field_70161_v - var22.field_70136_U) * var4;
      int var17 = 0;

      for(int var18 = 0; var18 < this.field_72754_S.length; ++var18) {
         this.field_72754_S[var18].func_78421_b();
      }

      for(int var24 = 0; var24 < this.field_72755_R.size(); ++var24) {
         WorldRenderer var19 = (WorldRenderer)this.field_72755_R.get(var24);
         int var20 = -1;

         for(int var21 = 0; var21 < var17; ++var21) {
            if (this.field_72754_S[var21].func_78418_a(var19.field_78918_f, var19.field_78919_g, var19.field_78931_h)) {
               var20 = var21;
            }
         }

         if (var20 < 0) {
            var20 = var17++;
            this.field_72754_S[var20].func_78422_a(var19.field_78918_f, var19.field_78919_g, var19.field_78931_h, var23, var13, var15);
         }

         this.field_72754_S[var20].func_78420_a(var19.func_78909_a(var3));
      }

      int var25 = MathHelper.func_76128_c(var23);
      int var26 = MathHelper.func_76128_c(var15);
      int var27 = var25 - (var25 & 1023);
      int var28 = var26 - (var26 & 1023);
      Arrays.sort(this.field_72754_S, new RenderDistanceSorter(var27, var28));
      this.func_72733_a(var3, var4);
      return var6;
   }

   public void func_72733_a(int var1, double var2) {
      this.field_72777_q.field_71460_t.func_78463_b(var2);

      for(int var4 = 0; var4 < this.field_72754_S.length; ++var4) {
         this.field_72754_S[var4].func_78419_a();
      }

      this.field_72777_q.field_71460_t.func_78483_a(var2);
   }

   public void func_72734_e() {
      ++this.field_72773_u;
      if (this.field_72773_u % 20 == 0) {
         Iterator var1 = this.field_72738_E.values().iterator();

         while(var1.hasNext()) {
            DestroyBlockProgress var2 = (DestroyBlockProgress)var1.next();
            int var3 = var2.func_82743_f();
            if (this.field_72773_u - var3 > 400) {
               var1.remove();
            }
         }
      }
   }

   public void func_72714_a(float var1) {
      if (this.field_72777_q.field_71441_e.field_73011_w.field_76574_g == 1) {
         GL11.glDisable(2912);
         GL11.glDisable(3008);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         RenderHelper.func_74518_a();
         GL11.glDepthMask(false);
         this.field_72770_i.func_110577_a(field_110926_k);
         Tessellator var21 = Tessellator.field_78398_a;

         for(int var22 = 0; var22 < 6; ++var22) {
            GL11.glPushMatrix();
            if (var22 == 1) {
               GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 2) {
               GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 3) {
               GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (var22 == 4) {
               GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (var22 == 5) {
               GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            var21.func_78382_b();
            var21.func_78378_d(2631720);
            var21.func_78374_a(-100.0, -100.0, -100.0, 0.0, 0.0);
            var21.func_78374_a(-100.0, -100.0, 100.0, 0.0, 16.0);
            var21.func_78374_a(100.0, -100.0, 100.0, 16.0, 16.0);
            var21.func_78374_a(100.0, -100.0, -100.0, 16.0, 0.0);
            var21.func_78381_a();
            GL11.glPopMatrix();
         }

         GL11.glDepthMask(true);
         GL11.glEnable(3553);
         GL11.glEnable(3008);
      } else if (this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         GL11.glDisable(3553);
         Vec3 var2 = this.field_72769_h.func_72833_a(this.field_72777_q.field_71451_h, var1);
         float var3 = (float)var2.field_72450_a;
         float var4 = (float)var2.field_72448_b;
         float var5 = (float)var2.field_72449_c;
         if (this.field_72777_q.field_71474_y.field_74337_g) {
            float var6 = (var3 * 30.0F + var4 * 59.0F + var5 * 11.0F) / 100.0F;
            float var7 = (var3 * 30.0F + var4 * 70.0F) / 100.0F;
            float var8 = (var3 * 30.0F + var5 * 70.0F) / 100.0F;
            var3 = var6;
            var4 = var7;
            var5 = var8;
         }

         GL11.glColor3f(var3, var4, var5);
         Tessellator var23 = Tessellator.field_78398_a;
         GL11.glDepthMask(false);
         GL11.glEnable(2912);
         GL11.glColor3f(var3, var4, var5);
         GL11.glCallList(this.field_72771_w);
         GL11.glDisable(2912);
         GL11.glDisable(3008);
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         RenderHelper.func_74518_a();
         float[] var24 = this.field_72769_h.field_73011_w.func_76560_a(this.field_72769_h.func_72826_c(var1), var1);
         if (var24 != null) {
            GL11.glDisable(3553);
            GL11.glShadeModel(7425);
            GL11.glPushMatrix();
            GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(MathHelper.func_76126_a(this.field_72769_h.func_72929_e(var1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
            float var25 = var24[0];
            float var9 = var24[1];
            float var10 = var24[2];
            if (this.field_72777_q.field_71474_y.field_74337_g) {
               float var11 = (var25 * 30.0F + var9 * 59.0F + var10 * 11.0F) / 100.0F;
               float var12 = (var25 * 30.0F + var9 * 70.0F) / 100.0F;
               float var13 = (var25 * 30.0F + var10 * 70.0F) / 100.0F;
               var25 = var11;
               var9 = var12;
               var10 = var13;
            }

            var23.func_78371_b(6);
            var23.func_78369_a(var25, var9, var10, var24[3]);
            var23.func_78377_a(0.0, 100.0, 0.0);
            byte var31 = 16;
            var23.func_78369_a(var24[0], var24[1], var24[2], 0.0F);

            for(int var34 = 0; var34 <= var31; ++var34) {
               float var38 = (float)var34 * 3.1415927F * 2.0F / (float)var31;
               float var14 = MathHelper.func_76126_a(var38);
               float var15 = MathHelper.func_76134_b(var38);
               var23.func_78377_a((double)(var14 * 120.0F), (double)(var15 * 120.0F), (double)(-var15 * 40.0F * var24[3]));
            }

            var23.func_78381_a();
            GL11.glPopMatrix();
            GL11.glShadeModel(7424);
         }

         GL11.glEnable(3553);
         OpenGlHelper.func_148821_a(770, 1, 1, 0);
         GL11.glPushMatrix();
         float var26 = 1.0F - this.field_72769_h.func_72867_j(var1);
         float var28 = 0.0F;
         float var29 = 0.0F;
         float var32 = 0.0F;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, var26);
         GL11.glTranslatef(var28, var29, var32);
         GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
         GL11.glRotatef(this.field_72769_h.func_72826_c(var1) * 360.0F, 1.0F, 0.0F, 0.0F);
         float var35 = 30.0F;
         this.field_72770_i.func_110577_a(field_110928_i);
         var23.func_78382_b();
         var23.func_78374_a((double)(-var35), 100.0, (double)(-var35), 0.0, 0.0);
         var23.func_78374_a((double)var35, 100.0, (double)(-var35), 1.0, 0.0);
         var23.func_78374_a((double)var35, 100.0, (double)var35, 1.0, 1.0);
         var23.func_78374_a((double)(-var35), 100.0, (double)var35, 0.0, 1.0);
         var23.func_78381_a();
         var35 = 20.0F;
         this.field_72770_i.func_110577_a(field_110927_h);
         int var39 = this.field_72769_h.func_72853_d();
         int var40 = var39 % 4;
         int var41 = var39 / 4 % 2;
         float var16 = (float)(var40 + 0) / 4.0F;
         float var17 = (float)(var41 + 0) / 2.0F;
         float var18 = (float)(var40 + 1) / 4.0F;
         float var19 = (float)(var41 + 1) / 2.0F;
         var23.func_78382_b();
         var23.func_78374_a((double)(-var35), -100.0, (double)var35, (double)var18, (double)var19);
         var23.func_78374_a((double)var35, -100.0, (double)var35, (double)var16, (double)var19);
         var23.func_78374_a((double)var35, -100.0, (double)(-var35), (double)var16, (double)var17);
         var23.func_78374_a((double)(-var35), -100.0, (double)(-var35), (double)var18, (double)var17);
         var23.func_78381_a();
         GL11.glDisable(3553);
         float var20 = this.field_72769_h.func_72880_h(var1) * var26;
         if (var20 > 0.0F) {
            GL11.glColor4f(var20, var20, var20, var20);
            GL11.glCallList(this.field_72772_v);
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glDisable(3042);
         GL11.glEnable(3008);
         GL11.glEnable(2912);
         GL11.glPopMatrix();
         GL11.glDisable(3553);
         GL11.glColor3f(0.0F, 0.0F, 0.0F);
         double var27 = this.field_72777_q.field_71439_g.func_70666_h(var1).field_72448_b - this.field_72769_h.func_72919_O();
         if (var27 < 0.0) {
            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 12.0F, 0.0F);
            GL11.glCallList(this.field_72781_x);
            GL11.glPopMatrix();
            var29 = 1.0F;
            var32 = -((float)(var27 + 65.0));
            var35 = -var29;
            var23.func_78382_b();
            var23.func_78384_a(0, 255);
            var23.func_78377_a((double)(-var29), (double)var32, (double)var29);
            var23.func_78377_a((double)var29, (double)var32, (double)var29);
            var23.func_78377_a((double)var29, (double)var35, (double)var29);
            var23.func_78377_a((double)(-var29), (double)var35, (double)var29);
            var23.func_78377_a((double)(-var29), (double)var35, (double)(-var29));
            var23.func_78377_a((double)var29, (double)var35, (double)(-var29));
            var23.func_78377_a((double)var29, (double)var32, (double)(-var29));
            var23.func_78377_a((double)(-var29), (double)var32, (double)(-var29));
            var23.func_78377_a((double)var29, (double)var35, (double)(-var29));
            var23.func_78377_a((double)var29, (double)var35, (double)var29);
            var23.func_78377_a((double)var29, (double)var32, (double)var29);
            var23.func_78377_a((double)var29, (double)var32, (double)(-var29));
            var23.func_78377_a((double)(-var29), (double)var32, (double)(-var29));
            var23.func_78377_a((double)(-var29), (double)var32, (double)var29);
            var23.func_78377_a((double)(-var29), (double)var35, (double)var29);
            var23.func_78377_a((double)(-var29), (double)var35, (double)(-var29));
            var23.func_78377_a((double)(-var29), (double)var35, (double)(-var29));
            var23.func_78377_a((double)(-var29), (double)var35, (double)var29);
            var23.func_78377_a((double)var29, (double)var35, (double)var29);
            var23.func_78377_a((double)var29, (double)var35, (double)(-var29));
            var23.func_78381_a();
         }

         if (this.field_72769_h.field_73011_w.func_76561_g()) {
            GL11.glColor3f(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
         } else {
            GL11.glColor3f(var3, var4, var5);
         }

         GL11.glPushMatrix();
         GL11.glTranslatef(0.0F, -((float)(var27 - 16.0)), 0.0F);
         GL11.glCallList(this.field_72781_x);
         GL11.glPopMatrix();
         GL11.glEnable(3553);
         GL11.glDepthMask(true);
      }
   }

   public void func_72718_b(float var1) {
      if (this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         if (this.field_72777_q.field_71474_y.field_74347_j) {
            this.func_72736_c(var1);
         } else {
            GL11.glDisable(2884);
            float var2 = (float)(
               this.field_72777_q.field_71451_h.field_70137_T
                  + (this.field_72777_q.field_71451_h.field_70163_u - this.field_72777_q.field_71451_h.field_70137_T) * (double)var1
            );
            byte var3 = 32;
            int var4 = 256 / var3;
            Tessellator var5 = Tessellator.field_78398_a;
            this.field_72770_i.func_110577_a(field_110925_j);
            GL11.glEnable(3042);
            OpenGlHelper.func_148821_a(770, 771, 1, 0);
            Vec3 var6 = this.field_72769_h.func_72824_f(var1);
            float var7 = (float)var6.field_72450_a;
            float var8 = (float)var6.field_72448_b;
            float var9 = (float)var6.field_72449_c;
            if (this.field_72777_q.field_71474_y.field_74337_g) {
               float var10 = (var7 * 30.0F + var8 * 59.0F + var9 * 11.0F) / 100.0F;
               float var11 = (var7 * 30.0F + var8 * 70.0F) / 100.0F;
               float var12 = (var7 * 30.0F + var9 * 70.0F) / 100.0F;
               var7 = var10;
               var8 = var11;
               var9 = var12;
            }

            float var24 = 4.8828125E-4F;
            double var25 = (double)((float)this.field_72773_u + var1);
            double var13 = this.field_72777_q.field_71451_h.field_70169_q
               + (this.field_72777_q.field_71451_h.field_70165_t - this.field_72777_q.field_71451_h.field_70169_q) * (double)var1
               + var25 * 0.029999999329447746;
            double var15 = this.field_72777_q.field_71451_h.field_70166_s
               + (this.field_72777_q.field_71451_h.field_70161_v - this.field_72777_q.field_71451_h.field_70166_s) * (double)var1;
            int var17 = MathHelper.func_76128_c(var13 / 2048.0);
            int var18 = MathHelper.func_76128_c(var15 / 2048.0);
            var13 -= (double)(var17 * 2048);
            var15 -= (double)(var18 * 2048);
            float var19 = this.field_72769_h.field_73011_w.func_76571_f() - var2 + 0.33F;
            float var20 = (float)(var13 * (double)var24);
            float var21 = (float)(var15 * (double)var24);
            var5.func_78382_b();
            var5.func_78369_a(var7, var8, var9, 0.8F);

            for(int var22 = -var3 * var4; var22 < var3 * var4; var22 += var3) {
               for(int var23 = -var3 * var4; var23 < var3 * var4; var23 += var3) {
                  var5.func_78374_a(
                     (double)(var22 + 0),
                     (double)var19,
                     (double)(var23 + var3),
                     (double)((float)(var22 + 0) * var24 + var20),
                     (double)((float)(var23 + var3) * var24 + var21)
                  );
                  var5.func_78374_a(
                     (double)(var22 + var3),
                     (double)var19,
                     (double)(var23 + var3),
                     (double)((float)(var22 + var3) * var24 + var20),
                     (double)((float)(var23 + var3) * var24 + var21)
                  );
                  var5.func_78374_a(
                     (double)(var22 + var3),
                     (double)var19,
                     (double)(var23 + 0),
                     (double)((float)(var22 + var3) * var24 + var20),
                     (double)((float)(var23 + 0) * var24 + var21)
                  );
                  var5.func_78374_a(
                     (double)(var22 + 0),
                     (double)var19,
                     (double)(var23 + 0),
                     (double)((float)(var22 + 0) * var24 + var20),
                     (double)((float)(var23 + 0) * var24 + var21)
                  );
               }
            }

            var5.func_78381_a();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(2884);
         }
      }
   }

   public boolean func_72721_a(double var1, double var3, double var5, float var7) {
      return false;
   }

   public void func_72736_c(float var1) {
      GL11.glDisable(2884);
      float var2 = (float)(
         this.field_72777_q.field_71451_h.field_70137_T
            + (this.field_72777_q.field_71451_h.field_70163_u - this.field_72777_q.field_71451_h.field_70137_T) * (double)var1
      );
      Tessellator var3 = Tessellator.field_78398_a;
      float var4 = 12.0F;
      float var5 = 4.0F;
      double var6 = (double)((float)this.field_72773_u + var1);
      double var8 = (
            this.field_72777_q.field_71451_h.field_70169_q
               + (this.field_72777_q.field_71451_h.field_70165_t - this.field_72777_q.field_71451_h.field_70169_q) * (double)var1
               + var6 * 0.029999999329447746
         )
         / (double)var4;
      double var10 = (
               this.field_72777_q.field_71451_h.field_70166_s
                  + (this.field_72777_q.field_71451_h.field_70161_v - this.field_72777_q.field_71451_h.field_70166_s) * (double)var1
            )
            / (double)var4
         + 0.33000001311302185;
      float var12 = this.field_72769_h.field_73011_w.func_76571_f() - var2 + 0.33F;
      int var13 = MathHelper.func_76128_c(var8 / 2048.0);
      int var14 = MathHelper.func_76128_c(var10 / 2048.0);
      var8 -= (double)(var13 * 2048);
      var10 -= (double)(var14 * 2048);
      this.field_72770_i.func_110577_a(field_110925_j);
      GL11.glEnable(3042);
      OpenGlHelper.func_148821_a(770, 771, 1, 0);
      Vec3 var15 = this.field_72769_h.func_72824_f(var1);
      float var16 = (float)var15.field_72450_a;
      float var17 = (float)var15.field_72448_b;
      float var18 = (float)var15.field_72449_c;
      if (this.field_72777_q.field_71474_y.field_74337_g) {
         float var19 = (var16 * 30.0F + var17 * 59.0F + var18 * 11.0F) / 100.0F;
         float var20 = (var16 * 30.0F + var17 * 70.0F) / 100.0F;
         float var21 = (var16 * 30.0F + var18 * 70.0F) / 100.0F;
         var16 = var19;
         var17 = var20;
         var18 = var21;
      }

      float var37 = (float)(var8 * 0.0);
      float var39 = (float)(var10 * 0.0);
      float var41 = 0.00390625F;
      var37 = (float)MathHelper.func_76128_c(var8) * var41;
      var39 = (float)MathHelper.func_76128_c(var10) * var41;
      float var22 = (float)(var8 - (double)MathHelper.func_76128_c(var8));
      float var23 = (float)(var10 - (double)MathHelper.func_76128_c(var10));
      byte var24 = 8;
      byte var25 = 4;
      float var26 = 9.765625E-4F;
      GL11.glScalef(var4, 1.0F, var4);

      for(int var27 = 0; var27 < 2; ++var27) {
         if (var27 == 0) {
            GL11.glColorMask(false, false, false, false);
         } else if (this.field_72777_q.field_71474_y.field_74337_g) {
            if (EntityRenderer.field_78515_b == 0) {
               GL11.glColorMask(false, true, true, true);
            } else {
               GL11.glColorMask(true, false, false, true);
            }
         } else {
            GL11.glColorMask(true, true, true, true);
         }

         for(int var28 = -var25 + 1; var28 <= var25; ++var28) {
            for(int var29 = -var25 + 1; var29 <= var25; ++var29) {
               var3.func_78382_b();
               float var30 = (float)(var28 * var24);
               float var31 = (float)(var29 * var24);
               float var32 = var30 - var22;
               float var33 = var31 - var23;
               if (var12 > -var5 - 1.0F) {
                  var3.func_78369_a(var16 * 0.7F, var17 * 0.7F, var18 * 0.7F, 0.8F);
                  var3.func_78375_b(0.0F, -1.0F, 0.0F);
                  var3.func_78374_a(
                     (double)(var32 + 0.0F),
                     (double)(var12 + 0.0F),
                     (double)(var33 + (float)var24),
                     (double)((var30 + 0.0F) * var41 + var37),
                     (double)((var31 + (float)var24) * var41 + var39)
                  );
                  var3.func_78374_a(
                     (double)(var32 + (float)var24),
                     (double)(var12 + 0.0F),
                     (double)(var33 + (float)var24),
                     (double)((var30 + (float)var24) * var41 + var37),
                     (double)((var31 + (float)var24) * var41 + var39)
                  );
                  var3.func_78374_a(
                     (double)(var32 + (float)var24),
                     (double)(var12 + 0.0F),
                     (double)(var33 + 0.0F),
                     (double)((var30 + (float)var24) * var41 + var37),
                     (double)((var31 + 0.0F) * var41 + var39)
                  );
                  var3.func_78374_a(
                     (double)(var32 + 0.0F),
                     (double)(var12 + 0.0F),
                     (double)(var33 + 0.0F),
                     (double)((var30 + 0.0F) * var41 + var37),
                     (double)((var31 + 0.0F) * var41 + var39)
                  );
               }

               if (var12 <= var5 + 1.0F) {
                  var3.func_78369_a(var16, var17, var18, 0.8F);
                  var3.func_78375_b(0.0F, 1.0F, 0.0F);
                  var3.func_78374_a(
                     (double)(var32 + 0.0F),
                     (double)(var12 + var5 - var26),
                     (double)(var33 + (float)var24),
                     (double)((var30 + 0.0F) * var41 + var37),
                     (double)((var31 + (float)var24) * var41 + var39)
                  );
                  var3.func_78374_a(
                     (double)(var32 + (float)var24),
                     (double)(var12 + var5 - var26),
                     (double)(var33 + (float)var24),
                     (double)((var30 + (float)var24) * var41 + var37),
                     (double)((var31 + (float)var24) * var41 + var39)
                  );
                  var3.func_78374_a(
                     (double)(var32 + (float)var24),
                     (double)(var12 + var5 - var26),
                     (double)(var33 + 0.0F),
                     (double)((var30 + (float)var24) * var41 + var37),
                     (double)((var31 + 0.0F) * var41 + var39)
                  );
                  var3.func_78374_a(
                     (double)(var32 + 0.0F),
                     (double)(var12 + var5 - var26),
                     (double)(var33 + 0.0F),
                     (double)((var30 + 0.0F) * var41 + var37),
                     (double)((var31 + 0.0F) * var41 + var39)
                  );
               }

               var3.func_78369_a(var16 * 0.9F, var17 * 0.9F, var18 * 0.9F, 0.8F);
               if (var28 > -1) {
                  var3.func_78375_b(-1.0F, 0.0F, 0.0F);

                  for(int var34 = 0; var34 < var24; ++var34) {
                     var3.func_78374_a(
                        (double)(var32 + (float)var34 + 0.0F),
                        (double)(var12 + 0.0F),
                        (double)(var33 + (float)var24),
                        (double)((var30 + (float)var34 + 0.5F) * var41 + var37),
                        (double)((var31 + (float)var24) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var34 + 0.0F),
                        (double)(var12 + var5),
                        (double)(var33 + (float)var24),
                        (double)((var30 + (float)var34 + 0.5F) * var41 + var37),
                        (double)((var31 + (float)var24) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var34 + 0.0F),
                        (double)(var12 + var5),
                        (double)(var33 + 0.0F),
                        (double)((var30 + (float)var34 + 0.5F) * var41 + var37),
                        (double)((var31 + 0.0F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var34 + 0.0F),
                        (double)(var12 + 0.0F),
                        (double)(var33 + 0.0F),
                        (double)((var30 + (float)var34 + 0.5F) * var41 + var37),
                        (double)((var31 + 0.0F) * var41 + var39)
                     );
                  }
               }

               if (var28 <= 1) {
                  var3.func_78375_b(1.0F, 0.0F, 0.0F);

                  for(int var42 = 0; var42 < var24; ++var42) {
                     var3.func_78374_a(
                        (double)(var32 + (float)var42 + 1.0F - var26),
                        (double)(var12 + 0.0F),
                        (double)(var33 + (float)var24),
                        (double)((var30 + (float)var42 + 0.5F) * var41 + var37),
                        (double)((var31 + (float)var24) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var42 + 1.0F - var26),
                        (double)(var12 + var5),
                        (double)(var33 + (float)var24),
                        (double)((var30 + (float)var42 + 0.5F) * var41 + var37),
                        (double)((var31 + (float)var24) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var42 + 1.0F - var26),
                        (double)(var12 + var5),
                        (double)(var33 + 0.0F),
                        (double)((var30 + (float)var42 + 0.5F) * var41 + var37),
                        (double)((var31 + 0.0F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var42 + 1.0F - var26),
                        (double)(var12 + 0.0F),
                        (double)(var33 + 0.0F),
                        (double)((var30 + (float)var42 + 0.5F) * var41 + var37),
                        (double)((var31 + 0.0F) * var41 + var39)
                     );
                  }
               }

               var3.func_78369_a(var16 * 0.8F, var17 * 0.8F, var18 * 0.8F, 0.8F);
               if (var29 > -1) {
                  var3.func_78375_b(0.0F, 0.0F, -1.0F);

                  for(int var43 = 0; var43 < var24; ++var43) {
                     var3.func_78374_a(
                        (double)(var32 + 0.0F),
                        (double)(var12 + var5),
                        (double)(var33 + (float)var43 + 0.0F),
                        (double)((var30 + 0.0F) * var41 + var37),
                        (double)((var31 + (float)var43 + 0.5F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var24),
                        (double)(var12 + var5),
                        (double)(var33 + (float)var43 + 0.0F),
                        (double)((var30 + (float)var24) * var41 + var37),
                        (double)((var31 + (float)var43 + 0.5F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var24),
                        (double)(var12 + 0.0F),
                        (double)(var33 + (float)var43 + 0.0F),
                        (double)((var30 + (float)var24) * var41 + var37),
                        (double)((var31 + (float)var43 + 0.5F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + 0.0F),
                        (double)(var12 + 0.0F),
                        (double)(var33 + (float)var43 + 0.0F),
                        (double)((var30 + 0.0F) * var41 + var37),
                        (double)((var31 + (float)var43 + 0.5F) * var41 + var39)
                     );
                  }
               }

               if (var29 <= 1) {
                  var3.func_78375_b(0.0F, 0.0F, 1.0F);

                  for(int var44 = 0; var44 < var24; ++var44) {
                     var3.func_78374_a(
                        (double)(var32 + 0.0F),
                        (double)(var12 + var5),
                        (double)(var33 + (float)var44 + 1.0F - var26),
                        (double)((var30 + 0.0F) * var41 + var37),
                        (double)((var31 + (float)var44 + 0.5F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var24),
                        (double)(var12 + var5),
                        (double)(var33 + (float)var44 + 1.0F - var26),
                        (double)((var30 + (float)var24) * var41 + var37),
                        (double)((var31 + (float)var44 + 0.5F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + (float)var24),
                        (double)(var12 + 0.0F),
                        (double)(var33 + (float)var44 + 1.0F - var26),
                        (double)((var30 + (float)var24) * var41 + var37),
                        (double)((var31 + (float)var44 + 0.5F) * var41 + var39)
                     );
                     var3.func_78374_a(
                        (double)(var32 + 0.0F),
                        (double)(var12 + 0.0F),
                        (double)(var33 + (float)var44 + 1.0F - var26),
                        (double)((var30 + 0.0F) * var41 + var37),
                        (double)((var31 + (float)var44 + 0.5F) * var41 + var39)
                     );
                  }
               }

               var3.func_78381_a();
            }
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glDisable(3042);
      GL11.glEnable(2884);
   }

   public boolean func_72716_a(EntityLivingBase var1, boolean var2) {
      byte var3 = 2;
      RenderSorter var4 = new RenderSorter(var1);
      WorldRenderer[] var5 = new WorldRenderer[var3];
      ArrayList var6 = null;
      int var7 = this.field_72767_j.size();
      int var8 = 0;
      this.field_72769_h.field_72984_F.func_76320_a("nearChunksSearch");

      for(int var9 = 0; var9 < var7; ++var9) {
         WorldRenderer var10 = (WorldRenderer)this.field_72767_j.get(var9);
         if (var10 != null) {
            if (!var2) {
               if (var10.func_78912_a(var1) > 272.0F) {
                  int var11 = 0;

                  while(var11 < var3 && (var5[var11] == null || var4.compare(var5[var11], var10) <= 0)) {
                     ++var11;
                  }

                  if (--var11 <= 0) {
                     continue;
                  }

                  int var12 = var11;

                  while(--var12 != 0) {
                     var5[var12 - 1] = var5[var12];
                  }

                  var5[var11] = var10;
                  continue;
               }
            } else if (!var10.field_78927_l) {
               continue;
            }

            if (var6 == null) {
               var6 = new ArrayList();
            }

            ++var8;
            var6.add(var10);
            this.field_72767_j.set(var9, null);
         }
      }

      this.field_72769_h.field_72984_F.func_76319_b();
      this.field_72769_h.field_72984_F.func_76320_a("sort");
      if (var6 != null) {
         if (var6.size() > 1) {
            Collections.sort(var6, var4);
         }

         for(int var16 = var6.size() - 1; var16 >= 0; --var16) {
            WorldRenderer var18 = (WorldRenderer)var6.get(var16);
            var18.func_147892_a(var1);
            var18.field_78939_q = false;
         }
      }

      this.field_72769_h.field_72984_F.func_76319_b();
      int var17 = 0;
      this.field_72769_h.field_72984_F.func_76320_a("rebuild");

      for(int var19 = var3 - 1; var19 >= 0; --var19) {
         WorldRenderer var22 = var5[var19];
         if (var22 != null) {
            if (!var22.field_78927_l && var19 != var3 - 1) {
               var5[var19] = null;
               var5[0] = null;
               break;
            }

            var5[var19].func_147892_a(var1);
            var5[var19].field_78939_q = false;
            ++var17;
         }
      }

      this.field_72769_h.field_72984_F.func_76319_b();
      this.field_72769_h.field_72984_F.func_76320_a("cleanup");
      int var20 = 0;
      int var23 = 0;

      for(int var24 = this.field_72767_j.size(); var20 != var24; ++var20) {
         WorldRenderer var13 = (WorldRenderer)this.field_72767_j.get(var20);
         if (var13 != null) {
            boolean var14 = false;

            for(int var15 = 0; var15 < var3 && !var14; ++var15) {
               if (var13 == var5[var15]) {
                  var14 = true;
               }
            }

            if (!var14) {
               if (var23 != var20) {
                  this.field_72767_j.set(var23, var13);
               }

               ++var23;
            }
         }
      }

      this.field_72769_h.field_72984_F.func_76319_b();
      this.field_72769_h.field_72984_F.func_76320_a("trim");

      while(--var20 >= var23) {
         this.field_72767_j.remove(var20);
      }

      this.field_72769_h.field_72984_F.func_76319_b();
      return var7 == var8 + var17;
   }

   public void func_72717_a(Tessellator var1, EntityPlayer var2, float var3) {
      double var4 = var2.field_70142_S + (var2.field_70165_t - var2.field_70142_S) * (double)var3;
      double var6 = var2.field_70137_T + (var2.field_70163_u - var2.field_70137_T) * (double)var3;
      double var8 = var2.field_70136_U + (var2.field_70161_v - var2.field_70136_U) * (double)var3;
      if (!this.field_72738_E.isEmpty()) {
         OpenGlHelper.func_148821_a(774, 768, 1, 0);
         this.field_72770_i.func_110577_a(TextureMap.field_110575_b);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
         GL11.glPushMatrix();
         GL11.glPolygonOffset(-3.0F, -3.0F);
         GL11.glEnable(32823);
         GL11.glAlphaFunc(516, 0.1F);
         GL11.glEnable(3008);
         var1.func_78382_b();
         var1.func_78373_b(-var4, -var6, -var8);
         var1.func_78383_c();
         Iterator var10 = this.field_72738_E.values().iterator();

         while(var10.hasNext()) {
            DestroyBlockProgress var11 = (DestroyBlockProgress)var10.next();
            double var12 = (double)var11.func_73110_b() - var4;
            double var14 = (double)var11.func_73109_c() - var6;
            double var16 = (double)var11.func_73108_d() - var8;
            if (var12 * var12 + var14 * var14 + var16 * var16 > 1024.0) {
               var10.remove();
            } else {
               Block var18 = this.field_72769_h.func_147439_a(var11.func_73110_b(), var11.func_73109_c(), var11.func_73108_d());
               if (var18.func_149688_o() != Material.field_151579_a) {
                  this.field_147592_B
                     .func_147792_a(var18, var11.func_73110_b(), var11.func_73109_c(), var11.func_73108_d(), this.field_94141_F[var11.func_73106_e()]);
               }
            }
         }

         var1.func_78381_a();
         var1.func_78373_b(0.0, 0.0, 0.0);
         GL11.glDisable(3008);
         GL11.glPolygonOffset(0.0F, 0.0F);
         GL11.glDisable(32823);
         GL11.glEnable(3008);
         GL11.glDepthMask(true);
         GL11.glPopMatrix();
      }
   }

   public void func_72731_b(EntityPlayer var1, MovingObjectPosition var2, int var3, float var4) {
      if (var3 == 0 && var2.field_72313_a == MovingObjectPosition$MovingObjectType.BLOCK) {
         GL11.glEnable(3042);
         OpenGlHelper.func_148821_a(770, 771, 1, 0);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GL11.glDisable(3553);
         GL11.glDepthMask(false);
         float var5 = 0.002F;
         Block var6 = this.field_72769_h.func_147439_a(var2.field_72311_b, var2.field_72312_c, var2.field_72309_d);
         if (var6.func_149688_o() != Material.field_151579_a) {
            var6.func_149719_a(this.field_72769_h, var2.field_72311_b, var2.field_72312_c, var2.field_72309_d);
            double var7 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var4;
            double var9 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var4;
            double var11 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var4;
            func_147590_a(
               var6.func_149633_g(this.field_72769_h, var2.field_72311_b, var2.field_72312_c, var2.field_72309_d)
                  .func_72314_b((double)var5, (double)var5, (double)var5)
                  .func_72325_c(-var7, -var9, -var11),
               -1
            );
         }

         GL11.glDepthMask(true);
         GL11.glEnable(3553);
         GL11.glDisable(3042);
      }
   }

   public static void func_147590_a(AxisAlignedBB var0, int var1) {
      Tessellator var2 = Tessellator.field_78398_a;
      var2.func_78371_b(3);
      if (var1 != -1) {
         var2.func_78378_d(var1);
      }

      var2.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var2.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var2.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var2.func_78381_a();
      var2.func_78371_b(3);
      if (var1 != -1) {
         var2.func_78378_d(var1);
      }

      var2.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var2.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var2.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var2.func_78381_a();
      var2.func_78371_b(1);
      if (var1 != -1) {
         var2.func_78378_d(var1);
      }

      var2.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c);
      var2.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c);
      var2.func_78377_a(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f);
      var2.func_78377_a(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f);
      var2.func_78377_a(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f);
      var2.func_78377_a(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f);
      var2.func_78381_a();
   }

   public void func_72725_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      int var7 = MathHelper.func_76137_a(var1, 16);
      int var8 = MathHelper.func_76137_a(var2, 16);
      int var9 = MathHelper.func_76137_a(var3, 16);
      int var10 = MathHelper.func_76137_a(var4, 16);
      int var11 = MathHelper.func_76137_a(var5, 16);
      int var12 = MathHelper.func_76137_a(var6, 16);

      for(int var13 = var7; var13 <= var10; ++var13) {
         int var14 = var13 % this.field_72766_m;
         if (var14 < 0) {
            var14 += this.field_72766_m;
         }

         for(int var15 = var8; var15 <= var11; ++var15) {
            int var16 = var15 % this.field_72763_n;
            if (var16 < 0) {
               var16 += this.field_72763_n;
            }

            for(int var17 = var9; var17 <= var12; ++var17) {
               int var18 = var17 % this.field_72764_o;
               if (var18 < 0) {
                  var18 += this.field_72764_o;
               }

               int var19 = (var18 * this.field_72763_n + var16) * this.field_72766_m + var14;
               WorldRenderer var20 = this.field_72765_l[var19];
               if (var20 != null && !var20.field_78939_q) {
                  this.field_72767_j.add(var20);
                  var20.func_78914_f();
               }
            }
         }
      }
   }

   @Override
   public void func_147586_a(int var1, int var2, int var3) {
      this.func_72725_b(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
   }

   @Override
   public void func_147588_b(int var1, int var2, int var3) {
      this.func_72725_b(var1 - 1, var2 - 1, var3 - 1, var1 + 1, var2 + 1, var3 + 1);
   }

   @Override
   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.func_72725_b(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1);
   }

   public void func_72729_a(ICamera var1, float var2) {
      for(int var3 = 0; var3 < this.field_72765_l.length; ++var3) {
         if (!this.field_72765_l[var3].func_78906_e() && (!this.field_72765_l[var3].field_78927_l || (var3 + this.field_72757_g & 15) == 0)) {
            this.field_72765_l[var3].func_78908_a(var1);
         }
      }

      ++this.field_72757_g;
   }

   @Override
   public void func_72702_a(String var1, int var2, int var3, int var4) {
      ChunkCoordinates var5 = new ChunkCoordinates(var2, var3, var4);
      ISound var6 = (ISound)this.field_147593_P.get(var5);
      if (var6 != null) {
         this.field_72777_q.func_147118_V().func_147683_b(var6);
         this.field_147593_P.remove(var5);
      }

      if (var1 != null) {
         ItemRecord var7 = ItemRecord.func_150926_b(var1);
         if (var7 != null) {
            this.field_72777_q.field_71456_v.func_73833_a(var7.func_150927_i());
         }

         PositionedSoundRecord var8 = PositionedSoundRecord.func_147675_a(new ResourceLocation(var1), (float)var2, (float)var3, (float)var4);
         this.field_147593_P.put(var5, var8);
         this.field_72777_q.func_147118_V().func_147682_a(var8);
      }
   }

   @Override
   public void func_72704_a(String var1, double var2, double var4, double var6, float var8, float var9) {
   }

   @Override
   public void func_85102_a(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
   }

   @Override
   public void func_72708_a(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      try {
         this.func_72726_b(var1, var2, var4, var6, var8, var10, var12);
      } catch (Throwable var17) {
         CrashReport var15 = CrashReport.func_85055_a(var17, "Exception while adding particle");
         CrashReportCategory var16 = var15.func_85058_a("Particle being added");
         var16.func_71507_a("Name", var1);
         var16.func_71500_a("Position", new RenderGlobal$1(this, var2, var4, var6));
         throw new ReportedException(var15);
      }
   }

   public EntityFX func_72726_b(String var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      if (this.field_72777_q != null && this.field_72777_q.field_71451_h != null && this.field_72777_q.field_71452_i != null) {
         int var14 = this.field_72777_q.field_71474_y.field_74362_aa;
         if (var14 == 1 && this.field_72769_h.field_73012_v.nextInt(3) == 0) {
            var14 = 2;
         }

         double var15 = this.field_72777_q.field_71451_h.field_70165_t - var2;
         double var17 = this.field_72777_q.field_71451_h.field_70163_u - var4;
         double var19 = this.field_72777_q.field_71451_h.field_70161_v - var6;
         Object var21 = null;
         if (var1.equals("hugeexplosion")) {
            this.field_72777_q
               .field_71452_i
               .func_78873_a((EntityFX)(var21 = new EntityHugeExplodeFX(this.field_72769_h, var2, var4, var6, var8, var10, var12)));
         } else if (var1.equals("largeexplode")) {
            this.field_72777_q
               .field_71452_i
               .func_78873_a((EntityFX)(var21 = new EntityLargeExplodeFX(this.field_72770_i, this.field_72769_h, var2, var4, var6, var8, var10, var12)));
         } else if (var1.equals("fireworksSpark")) {
            this.field_72777_q
               .field_71452_i
               .func_78873_a(
                  (EntityFX)(var21 = new EntityFireworkSparkFX(this.field_72769_h, var2, var4, var6, var8, var10, var12, this.field_72777_q.field_71452_i))
               );
         }

         if (var21 != null) {
            return (EntityFX)var21;
         } else {
            double var22 = 16.0;
            if (var15 * var15 + var17 * var17 + var19 * var19 > var22 * var22) {
               return null;
            } else if (var14 > 1) {
               return null;
            } else {
               if (var1.equals("bubble")) {
                  var21 = new EntityBubbleFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("suspended")) {
                  var21 = new EntitySuspendFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("depthsuspend")) {
                  var21 = new EntityAuraFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("townaura")) {
                  var21 = new EntityAuraFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("crit")) {
                  var21 = new EntityCritFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("magicCrit")) {
                  var21 = new EntityCritFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
                  ((EntityFX)var21).func_70538_b(
                     ((EntityFX)var21).func_70534_d() * 0.3F, ((EntityFX)var21).func_70542_f() * 0.8F, ((EntityFX)var21).func_70535_g()
                  );
                  ((EntityFX)var21).func_94053_h();
               } else if (var1.equals("smoke")) {
                  var21 = new EntitySmokeFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("mobSpell")) {
                  var21 = new EntitySpellParticleFX(this.field_72769_h, var2, var4, var6, 0.0, 0.0, 0.0);
                  ((EntityFX)var21).func_70538_b((float)var8, (float)var10, (float)var12);
               } else if (var1.equals("mobSpellAmbient")) {
                  var21 = new EntitySpellParticleFX(this.field_72769_h, var2, var4, var6, 0.0, 0.0, 0.0);
                  ((EntityFX)var21).func_82338_g(0.15F);
                  ((EntityFX)var21).func_70538_b((float)var8, (float)var10, (float)var12);
               } else if (var1.equals("spell")) {
                  var21 = new EntitySpellParticleFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("instantSpell")) {
                  var21 = new EntitySpellParticleFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
                  ((EntitySpellParticleFX)var21).func_70589_b(144);
               } else if (var1.equals("witchMagic")) {
                  var21 = new EntitySpellParticleFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
                  ((EntitySpellParticleFX)var21).func_70589_b(144);
                  float var24 = this.field_72769_h.field_73012_v.nextFloat() * 0.5F + 0.35F;
                  ((EntityFX)var21).func_70538_b(1.0F * var24, 0.0F * var24, 1.0F * var24);
               } else if (var1.equals("note")) {
                  var21 = new EntityNoteFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("portal")) {
                  var21 = new EntityPortalFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("enchantmenttable")) {
                  var21 = new EntityEnchantmentTableParticleFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("explode")) {
                  var21 = new EntityExplodeFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("flame")) {
                  var21 = new EntityFlameFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("lava")) {
                  var21 = new EntityLavaFX(this.field_72769_h, var2, var4, var6);
               } else if (var1.equals("footstep")) {
                  var21 = new EntityFootStepFX(this.field_72770_i, this.field_72769_h, var2, var4, var6);
               } else if (var1.equals("splash")) {
                  var21 = new EntitySplashFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("wake")) {
                  var21 = new EntityFishWakeFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("largesmoke")) {
                  var21 = new EntitySmokeFX(this.field_72769_h, var2, var4, var6, var8, var10, var12, 2.5F);
               } else if (var1.equals("cloud")) {
                  var21 = new EntityCloudFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("reddust")) {
                  var21 = new EntityReddustFX(this.field_72769_h, var2, var4, var6, (float)var8, (float)var10, (float)var12);
               } else if (var1.equals("snowballpoof")) {
                  var21 = new EntityBreakingFX(this.field_72769_h, var2, var4, var6, Items.field_151126_ay);
               } else if (var1.equals("dripWater")) {
                  var21 = new EntityDropParticleFX(this.field_72769_h, var2, var4, var6, Material.field_151586_h);
               } else if (var1.equals("dripLava")) {
                  var21 = new EntityDropParticleFX(this.field_72769_h, var2, var4, var6, Material.field_151587_i);
               } else if (var1.equals("snowshovel")) {
                  var21 = new EntitySnowShovelFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("slime")) {
                  var21 = new EntityBreakingFX(this.field_72769_h, var2, var4, var6, Items.field_151123_aH);
               } else if (var1.equals("heart")) {
                  var21 = new EntityHeartFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
               } else if (var1.equals("angryVillager")) {
                  var21 = new EntityHeartFX(this.field_72769_h, var2, var4 + 0.5, var6, var8, var10, var12);
                  ((EntityFX)var21).func_70536_a(81);
                  ((EntityFX)var21).func_70538_b(1.0F, 1.0F, 1.0F);
               } else if (var1.equals("happyVillager")) {
                  var21 = new EntityAuraFX(this.field_72769_h, var2, var4, var6, var8, var10, var12);
                  ((EntityFX)var21).func_70536_a(82);
                  ((EntityFX)var21).func_70538_b(1.0F, 1.0F, 1.0F);
               } else if (var1.startsWith("iconcrack_")) {
                  String[] var27 = var1.split("_", 3);
                  int var25 = Integer.parseInt(var27[1]);
                  if (var27.length > 2) {
                     int var26 = Integer.parseInt(var27[2]);
                     var21 = new EntityBreakingFX(this.field_72769_h, var2, var4, var6, var8, var10, var12, Item.func_150899_d(var25), var26);
                  } else {
                     var21 = new EntityBreakingFX(this.field_72769_h, var2, var4, var6, var8, var10, var12, Item.func_150899_d(var25), 0);
                  }
               } else if (var1.startsWith("blockcrack_")) {
                  String[] var28 = var1.split("_", 3);
                  Block var30 = Block.func_149729_e(Integer.parseInt(var28[1]));
                  int var32 = Integer.parseInt(var28[2]);
                  var21 = new EntityDiggingFX(this.field_72769_h, var2, var4, var6, var8, var10, var12, var30, var32).func_90019_g(var32);
               } else if (var1.startsWith("blockdust_")) {
                  String[] var29 = var1.split("_", 3);
                  Block var31 = Block.func_149729_e(Integer.parseInt(var29[1]));
                  int var33 = Integer.parseInt(var29[2]);
                  var21 = new EntityBlockDustFX(this.field_72769_h, var2, var4, var6, var8, var10, var12, var31, var33).func_90019_g(var33);
               }

               if (var21 != null) {
                  this.field_72777_q.field_71452_i.func_78873_a((EntityFX)var21);
               }

               return (EntityFX)var21;
            }
         }
      } else {
         return null;
      }
   }

   @Override
   public void func_72703_a(Entity var1) {
   }

   @Override
   public void func_72709_b(Entity var1) {
   }

   public void func_72728_f() {
      GLAllocation.func_74523_b(this.field_72778_p);
   }

   @Override
   public void func_82746_a(int var1, int var2, int var3, int var4, int var5) {
      Random var6 = this.field_72769_h.field_73012_v;
      switch(var1) {
         case 1013:
         case 1018:
            if (this.field_72777_q.field_71451_h != null) {
               double var7 = (double)var2 - this.field_72777_q.field_71451_h.field_70165_t;
               double var9 = (double)var3 - this.field_72777_q.field_71451_h.field_70163_u;
               double var11 = (double)var4 - this.field_72777_q.field_71451_h.field_70161_v;
               double var13 = Math.sqrt(var7 * var7 + var9 * var9 + var11 * var11);
               double var15 = this.field_72777_q.field_71451_h.field_70165_t;
               double var17 = this.field_72777_q.field_71451_h.field_70163_u;
               double var19 = this.field_72777_q.field_71451_h.field_70161_v;
               if (var13 > 0.0) {
                  var15 += var7 / var13 * 2.0;
                  var17 += var9 / var13 * 2.0;
                  var19 += var11 / var13 * 2.0;
               }

               if (var1 == 1013) {
                  this.field_72769_h.func_72980_b(var15, var17, var19, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else if (var1 == 1018) {
                  this.field_72769_h.func_72980_b(var15, var17, var19, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
      }
   }

   @Override
   public void func_72706_a(EntityPlayer var1, int var2, int var3, int var4, int var5, int var6) {
      Random var7 = this.field_72769_h.field_73012_v;
      Object var8 = null;
      switch(var2) {
         case 1000:
            this.field_72769_h.func_72980_b((double)var3, (double)var4, (double)var5, "random.click", 1.0F, 1.0F, false);
            break;
         case 1001:
            this.field_72769_h.func_72980_b((double)var3, (double)var4, (double)var5, "random.click", 1.0F, 1.2F, false);
            break;
         case 1002:
            this.field_72769_h.func_72980_b((double)var3, (double)var4, (double)var5, "random.bow", 1.0F, 1.2F, false);
            break;
         case 1003:
            if (Math.random() < 0.5) {
               this.field_72769_h
                  .func_72980_b(
                     (double)var3 + 0.5,
                     (double)var4 + 0.5,
                     (double)var5 + 0.5,
                     "random.door_open",
                     1.0F,
                     this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F,
                     false
                  );
            } else {
               this.field_72769_h
                  .func_72980_b(
                     (double)var3 + 0.5,
                     (double)var4 + 0.5,
                     (double)var5 + 0.5,
                     "random.door_close",
                     1.0F,
                     this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F,
                     false
                  );
            }
            break;
         case 1004:
            this.field_72769_h
               .func_72980_b(
                  (double)((float)var3 + 0.5F),
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  "random.fizz",
                  0.5F,
                  2.6F + (var7.nextFloat() - var7.nextFloat()) * 0.8F,
                  false
               );
            break;
         case 1005:
            if (Item.func_150899_d(var6) instanceof ItemRecord) {
               this.field_72769_h.func_72934_a("records." + ((ItemRecord)Item.func_150899_d(var6)).field_150929_a, var3, var4, var5);
            } else {
               this.field_72769_h.func_72934_a(null, var3, var4, var5);
            }
            break;
         case 1007:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.ghast.charge",
                  10.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1008:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.ghast.fireball",
                  10.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1009:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.ghast.fireball",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1010:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.zombie.wood",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1011:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.zombie.metal",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1012:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.zombie.woodbreak",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1014:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.wither.shoot",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1015:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.bat.takeoff",
                  0.05F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1016:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.zombie.infect",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1017:
            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "mob.zombie.unfect",
                  2.0F,
                  (var7.nextFloat() - var7.nextFloat()) * 0.2F + 1.0F,
                  false
               );
            break;
         case 1020:
            this.field_72769_h
               .func_72980_b(
                  (double)((float)var3 + 0.5F),
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  "random.anvil_break",
                  1.0F,
                  this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F,
                  false
               );
            break;
         case 1021:
            this.field_72769_h
               .func_72980_b(
                  (double)((float)var3 + 0.5F),
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  "random.anvil_use",
                  1.0F,
                  this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F,
                  false
               );
            break;
         case 1022:
            this.field_72769_h
               .func_72980_b(
                  (double)((float)var3 + 0.5F),
                  (double)((float)var4 + 0.5F),
                  (double)((float)var5 + 0.5F),
                  "random.anvil_land",
                  0.3F,
                  this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F,
                  false
               );
            break;
         case 2000:
            int var37 = var6 % 3 - 1;
            int var10 = var6 / 3 % 3 - 1;
            double var39 = (double)var3 + (double)var37 * 0.6 + 0.5;
            double var41 = (double)var4 + 0.5;
            double var43 = (double)var5 + (double)var10 * 0.6 + 0.5;

            for(int var47 = 0; var47 < 10; ++var47) {
               double var48 = var7.nextDouble() * 0.2 + 0.01;
               double var49 = var39 + (double)var37 * 0.01 + (var7.nextDouble() - 0.5) * (double)var10 * 0.5;
               double var53 = var41 + (var7.nextDouble() - 0.5) * 0.5;
               double var56 = var43 + (double)var10 * 0.01 + (var7.nextDouble() - 0.5) * (double)var37 * 0.5;
               double var59 = (double)var37 * var48 + var7.nextGaussian() * 0.01;
               double var61 = -0.03 + var7.nextGaussian() * 0.01;
               double var63 = (double)var10 * var48 + var7.nextGaussian() * 0.01;
               this.func_72708_a("smoke", var49, var53, var56, var59, var61, var63);
            }
            break;
         case 2001:
            Block var35 = Block.func_149729_e(var6 & 4095);
            if (var35.func_149688_o() != Material.field_151579_a) {
               this.field_72777_q
                  .func_147118_V()
                  .func_147682_a(
                     new PositionedSoundRecord(
                        new ResourceLocation(var35.field_149762_H.func_150495_a()),
                        (var35.field_149762_H.func_150497_c() + 1.0F) / 2.0F,
                        var35.field_149762_H.func_150494_d() * 0.8F,
                        (float)var3 + 0.5F,
                        (float)var4 + 0.5F,
                        (float)var5 + 0.5F
                     )
                  );
            }

            this.field_72777_q.field_71452_i.func_147215_a(var3, var4, var5, var35, var6 >> 12 & 0xFF);
            break;
         case 2002:
            double var36 = (double)var3;
            double var38 = (double)var4;
            double var40 = (double)var5;
            String var42 = "iconcrack_" + Item.func_150891_b(Items.field_151068_bn) + "_" + var6;

            for(int var45 = 0; var45 < 8; ++var45) {
               this.func_72708_a(var42, var36, var38, var40, var7.nextGaussian() * 0.15, var7.nextDouble() * 0.2, var7.nextGaussian() * 0.15);
            }

            int var46 = Items.field_151068_bn.func_77620_a(var6);
            float var17 = (float)(var46 >> 16 & 0xFF) / 255.0F;
            float var18 = (float)(var46 >> 8 & 0xFF) / 255.0F;
            float var19 = (float)(var46 >> 0 & 0xFF) / 255.0F;
            String var20 = "spell";
            if (Items.field_151068_bn.func_77833_h(var6)) {
               var20 = "instantSpell";
            }

            for(int var51 = 0; var51 < 100; ++var51) {
               double var52 = var7.nextDouble() * 4.0;
               double var55 = var7.nextDouble() * 3.141592653589793 * 2.0;
               double var58 = Math.cos(var55) * var52;
               double var60 = 0.01 + var7.nextDouble() * 0.5;
               double var62 = Math.sin(var55) * var52;
               EntityFX var64 = this.func_72726_b(var20, var36 + var58 * 0.1, var38 + 0.3, var40 + var62 * 0.1, var58, var60, var62);
               if (var64 != null) {
                  float var33 = 0.75F + var7.nextFloat() * 0.25F;
                  var64.func_70538_b(var17 * var33, var18 * var33, var19 * var33);
                  var64.func_70543_e((float)var52);
               }
            }

            this.field_72769_h
               .func_72980_b(
                  (double)var3 + 0.5,
                  (double)var4 + 0.5,
                  (double)var5 + 0.5,
                  "game.potion.smash",
                  1.0F,
                  this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F,
                  false
               );
            break;
         case 2003:
            double var9 = (double)var3 + 0.5;
            double var11 = (double)var4;
            double var13 = (double)var5 + 0.5;
            String var15 = "iconcrack_" + Item.func_150891_b(Items.field_151061_bv);

            for(int var16 = 0; var16 < 8; ++var16) {
               this.func_72708_a(var15, var9, var11, var13, var7.nextGaussian() * 0.15, var7.nextDouble() * 0.2, var7.nextGaussian() * 0.15);
            }

            for(double var44 = 0.0; var44 < 6.283185307179586; var44 += 0.15707963267948966) {
               this.func_72708_a(
                  "portal", var9 + Math.cos(var44) * 5.0, var11 - 0.4, var13 + Math.sin(var44) * 5.0, Math.cos(var44) * -5.0, 0.0, Math.sin(var44) * -5.0
               );
               this.func_72708_a(
                  "portal", var9 + Math.cos(var44) * 5.0, var11 - 0.4, var13 + Math.sin(var44) * 5.0, Math.cos(var44) * -7.0, 0.0, Math.sin(var44) * -7.0
               );
            }
            break;
         case 2004:
            for(int var50 = 0; var50 < 20; ++var50) {
               double var22 = (double)var3 + 0.5 + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5) * 2.0;
               double var54 = (double)var4 + 0.5 + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5) * 2.0;
               double var57 = (double)var5 + 0.5 + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5) * 2.0;
               this.field_72769_h.func_72869_a("smoke", var22, var54, var57, 0.0, 0.0, 0.0);
               this.field_72769_h.func_72869_a("flame", var22, var54, var57, 0.0, 0.0, 0.0);
            }
            break;
         case 2005:
            ItemDye.func_150918_a(this.field_72769_h, var3, var4, var5, var6);
            break;
         case 2006:
            Block var34 = this.field_72769_h.func_147439_a(var3, var4, var5);
            if (var34.func_149688_o() != Material.field_151579_a) {
               double var21 = (double)Math.min(0.2F + (float)var6 / 15.0F, 10.0F);
               if (var21 > 2.5) {
                  var21 = 2.5;
               }

               int var23 = (int)(150.0 * var21);

               for(int var24 = 0; var24 < var23; ++var24) {
                  float var25 = MathHelper.func_151240_a(var7, 0.0F, 6.2831855F);
                  double var26 = (double)MathHelper.func_151240_a(var7, 0.75F, 1.0F);
                  double var28 = 0.20000000298023224 + var21 / 100.0;
                  double var30 = (double)(MathHelper.func_76134_b(var25) * 0.2F) * var26 * var26 * (var21 + 0.2);
                  double var32 = (double)(MathHelper.func_76126_a(var25) * 0.2F) * var26 * var26 * (var21 + 0.2);
                  this.field_72769_h
                     .func_72869_a(
                        "blockdust_" + Block.func_149682_b(var34) + "_" + this.field_72769_h.func_72805_g(var3, var4, var5),
                        (double)((float)var3 + 0.5F),
                        (double)((float)var4 + 1.0F),
                        (double)((float)var5 + 0.5F),
                        var30,
                        var28,
                        var32
                     );
               }
            }
      }
   }

   @Override
   public void func_147587_b(int var1, int var2, int var3, int var4, int var5) {
      if (var5 >= 0 && var5 < 10) {
         DestroyBlockProgress var6 = (DestroyBlockProgress)this.field_72738_E.get(var1);
         if (var6 == null || var6.func_73110_b() != var2 || var6.func_73109_c() != var3 || var6.func_73108_d() != var4) {
            var6 = new DestroyBlockProgress(var1, var2, var3, var4);
            this.field_72738_E.put(var1, var6);
         }

         var6.func_73107_a(var5);
         var6.func_82744_b(this.field_72773_u);
      } else {
         this.field_72738_E.remove(var1);
      }
   }

   public void func_94140_a(IIconRegister var1) {
      this.field_94141_F = new IIcon[10];

      for(int var2 = 0; var2 < this.field_94141_F.length; ++var2) {
         this.field_94141_F[var2] = var1.func_94245_a("destroy_stage_" + var2);
      }
   }
}
