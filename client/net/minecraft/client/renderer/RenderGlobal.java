package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
   private static final Logger field_147599_m = LogManager.getLogger();
   private static final ResourceLocation field_110927_h = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation field_110928_i = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation field_110925_j = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation field_110926_k = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation field_175006_g = new ResourceLocation("textures/misc/forcefield.png");
   private final Minecraft field_72777_q;
   private final TextureManager field_72770_i;
   private final RenderManager field_175010_j;
   private WorldClient field_72769_h;
   private Set<RenderChunk> field_175009_l = Sets.newLinkedHashSet();
   private List<RenderGlobal.ContainerLocalRenderInformation> field_72755_R = Lists.newArrayListWithCapacity(69696);
   private final Set<TileEntity> field_181024_n = Sets.newHashSet();
   private ViewFrustum field_175008_n;
   private int field_72772_v = -1;
   private int field_72771_w = -1;
   private int field_72781_x = -1;
   private VertexFormat field_175014_r;
   private VertexBuffer field_175013_s;
   private VertexBuffer field_175012_t;
   private VertexBuffer field_175011_u;
   private int field_72773_u;
   private final Map<Integer, DestroyBlockProgress> field_72738_E = Maps.newHashMap();
   private final Map<BlockPos, ISound> field_147593_P = Maps.newHashMap();
   private final TextureAtlasSprite[] field_94141_F = new TextureAtlasSprite[10];
   private Framebuffer field_175015_z;
   private ShaderGroup field_174991_A;
   private double field_174992_B = 4.9E-324D;
   private double field_174993_C = 4.9E-324D;
   private double field_174987_D = 4.9E-324D;
   private int field_174988_E = -2147483648;
   private int field_174989_F = -2147483648;
   private int field_174990_G = -2147483648;
   private double field_174997_H = 4.9E-324D;
   private double field_174998_I = 4.9E-324D;
   private double field_174999_J = 4.9E-324D;
   private double field_175000_K = 4.9E-324D;
   private double field_174994_L = 4.9E-324D;
   private final ChunkRenderDispatcher field_174995_M = new ChunkRenderDispatcher();
   private ChunkRenderContainer field_174996_N;
   private int field_72739_F = -1;
   private int field_72740_G = 2;
   private int field_72748_H;
   private int field_72749_I;
   private int field_72750_J;
   private boolean field_175002_T = false;
   private ClippingHelper field_175001_U;
   private final Vector4f[] field_175004_V = new Vector4f[8];
   private final Vector3d field_175003_W = new Vector3d();
   private boolean field_175005_X = false;
   IRenderChunkFactory field_175007_a;
   private double field_147596_f;
   private double field_147597_g;
   private double field_147602_h;
   private boolean field_147595_R = true;

   public RenderGlobal(Minecraft var1) {
      super();
      this.field_72777_q = var1;
      this.field_175010_j = var1.func_175598_ae();
      this.field_72770_i = var1.func_110434_K();
      this.field_72770_i.func_110577_a(field_175006_g);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GlStateManager.func_179144_i(0);
      this.func_174971_n();
      this.field_175005_X = OpenGlHelper.func_176075_f();
      if (this.field_175005_X) {
         this.field_174996_N = new VboRenderList();
         this.field_175007_a = new VboChunkFactory();
      } else {
         this.field_174996_N = new RenderList();
         this.field_175007_a = new ListChunkFactory();
      }

      this.field_175014_r = new VertexFormat();
      this.field_175014_r.func_181721_a(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
      this.func_174963_q();
      this.func_174980_p();
      this.func_174964_o();
   }

   public void func_110549_a(IResourceManager var1) {
      this.func_174971_n();
   }

   private void func_174971_n() {
      TextureMap var1 = this.field_72777_q.func_147117_R();

      for(int var2 = 0; var2 < this.field_94141_F.length; ++var2) {
         this.field_94141_F[var2] = var1.func_110572_b("minecraft:blocks/destroy_stage_" + var2);
      }

   }

   public void func_174966_b() {
      if (OpenGlHelper.field_148824_g) {
         if (ShaderLinkHelper.func_148074_b() == null) {
            ShaderLinkHelper.func_148076_a();
         }

         ResourceLocation var1 = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.field_174991_A = new ShaderGroup(this.field_72777_q.func_110434_K(), this.field_72777_q.func_110442_L(), this.field_72777_q.func_147110_a(), var1);
            this.field_174991_A.func_148026_a(this.field_72777_q.field_71443_c, this.field_72777_q.field_71440_d);
            this.field_175015_z = this.field_174991_A.func_177066_a("final");
         } catch (IOException var3) {
            field_147599_m.warn("Failed to load shader: " + var1, var3);
            this.field_174991_A = null;
            this.field_175015_z = null;
         } catch (JsonSyntaxException var4) {
            field_147599_m.warn("Failed to load shader: " + var1, var4);
            this.field_174991_A = null;
            this.field_175015_z = null;
         }
      } else {
         this.field_174991_A = null;
         this.field_175015_z = null;
      }

   }

   public void func_174975_c() {
      if (this.func_174985_d()) {
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 0, 1);
         this.field_175015_z.func_178038_a(this.field_72777_q.field_71443_c, this.field_72777_q.field_71440_d, false);
         GlStateManager.func_179084_k();
      }

   }

   protected boolean func_174985_d() {
      return this.field_175015_z != null && this.field_174991_A != null && this.field_72777_q.field_71439_g != null && this.field_72777_q.field_71439_g.func_175149_v() && this.field_72777_q.field_71474_y.field_178883_an.func_151470_d();
   }

   private void func_174964_o() {
      Tessellator var1 = Tessellator.func_178181_a();
      WorldRenderer var2 = var1.func_178180_c();
      if (this.field_175011_u != null) {
         this.field_175011_u.func_177362_c();
      }

      if (this.field_72781_x >= 0) {
         GLAllocation.func_74523_b(this.field_72781_x);
         this.field_72781_x = -1;
      }

      if (this.field_175005_X) {
         this.field_175011_u = new VertexBuffer(this.field_175014_r);
         this.func_174968_a(var2, -16.0F, true);
         var2.func_178977_d();
         var2.func_178965_a();
         this.field_175011_u.func_181722_a(var2.func_178966_f());
      } else {
         this.field_72781_x = GLAllocation.func_74526_a(1);
         GL11.glNewList(this.field_72781_x, 4864);
         this.func_174968_a(var2, -16.0F, true);
         var1.func_78381_a();
         GL11.glEndList();
      }

   }

   private void func_174980_p() {
      Tessellator var1 = Tessellator.func_178181_a();
      WorldRenderer var2 = var1.func_178180_c();
      if (this.field_175012_t != null) {
         this.field_175012_t.func_177362_c();
      }

      if (this.field_72771_w >= 0) {
         GLAllocation.func_74523_b(this.field_72771_w);
         this.field_72771_w = -1;
      }

      if (this.field_175005_X) {
         this.field_175012_t = new VertexBuffer(this.field_175014_r);
         this.func_174968_a(var2, 16.0F, false);
         var2.func_178977_d();
         var2.func_178965_a();
         this.field_175012_t.func_181722_a(var2.func_178966_f());
      } else {
         this.field_72771_w = GLAllocation.func_74526_a(1);
         GL11.glNewList(this.field_72771_w, 4864);
         this.func_174968_a(var2, 16.0F, false);
         var1.func_78381_a();
         GL11.glEndList();
      }

   }

   private void func_174968_a(WorldRenderer var1, float var2, boolean var3) {
      boolean var4 = true;
      boolean var5 = true;
      var1.func_181668_a(7, DefaultVertexFormats.field_181705_e);

      for(int var6 = -384; var6 <= 384; var6 += 64) {
         for(int var7 = -384; var7 <= 384; var7 += 64) {
            float var8 = (float)var6;
            float var9 = (float)(var6 + 64);
            if (var3) {
               var9 = (float)var6;
               var8 = (float)(var6 + 64);
            }

            var1.func_181662_b((double)var8, (double)var2, (double)var7).func_181675_d();
            var1.func_181662_b((double)var9, (double)var2, (double)var7).func_181675_d();
            var1.func_181662_b((double)var9, (double)var2, (double)(var7 + 64)).func_181675_d();
            var1.func_181662_b((double)var8, (double)var2, (double)(var7 + 64)).func_181675_d();
         }
      }

   }

   private void func_174963_q() {
      Tessellator var1 = Tessellator.func_178181_a();
      WorldRenderer var2 = var1.func_178180_c();
      if (this.field_175013_s != null) {
         this.field_175013_s.func_177362_c();
      }

      if (this.field_72772_v >= 0) {
         GLAllocation.func_74523_b(this.field_72772_v);
         this.field_72772_v = -1;
      }

      if (this.field_175005_X) {
         this.field_175013_s = new VertexBuffer(this.field_175014_r);
         this.func_180444_a(var2);
         var2.func_178977_d();
         var2.func_178965_a();
         this.field_175013_s.func_181722_a(var2.func_178966_f());
      } else {
         this.field_72772_v = GLAllocation.func_74526_a(1);
         GlStateManager.func_179094_E();
         GL11.glNewList(this.field_72772_v, 4864);
         this.func_180444_a(var2);
         var1.func_78381_a();
         GL11.glEndList();
         GlStateManager.func_179121_F();
      }

   }

   private void func_180444_a(WorldRenderer var1) {
      Random var2 = new Random(10842L);
      var1.func_181668_a(7, DefaultVertexFormats.field_181705_e);

      for(int var3 = 0; var3 < 1500; ++var3) {
         double var4 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var6 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var8 = (double)(var2.nextFloat() * 2.0F - 1.0F);
         double var10 = (double)(0.15F + var2.nextFloat() * 0.1F);
         double var12 = var4 * var4 + var6 * var6 + var8 * var8;
         if (var12 < 1.0D && var12 > 0.01D) {
            var12 = 1.0D / Math.sqrt(var12);
            var4 *= var12;
            var6 *= var12;
            var8 *= var12;
            double var14 = var4 * 100.0D;
            double var16 = var6 * 100.0D;
            double var18 = var8 * 100.0D;
            double var20 = Math.atan2(var4, var8);
            double var22 = Math.sin(var20);
            double var24 = Math.cos(var20);
            double var26 = Math.atan2(Math.sqrt(var4 * var4 + var8 * var8), var6);
            double var28 = Math.sin(var26);
            double var30 = Math.cos(var26);
            double var32 = var2.nextDouble() * 3.141592653589793D * 2.0D;
            double var34 = Math.sin(var32);
            double var36 = Math.cos(var32);

            for(int var38 = 0; var38 < 4; ++var38) {
               double var39 = 0.0D;
               double var41 = (double)((var38 & 2) - 1) * var10;
               double var43 = (double)((var38 + 1 & 2) - 1) * var10;
               double var45 = 0.0D;
               double var47 = var41 * var36 - var43 * var34;
               double var49 = var43 * var36 + var41 * var34;
               double var53 = var47 * var28 + 0.0D * var30;
               double var55 = 0.0D * var28 - var47 * var30;
               double var57 = var55 * var22 - var49 * var24;
               double var61 = var49 * var22 + var55 * var24;
               var1.func_181662_b(var14 + var57, var16 + var53, var18 + var61).func_181675_d();
            }
         }
      }

   }

   public void func_72732_a(WorldClient var1) {
      if (this.field_72769_h != null) {
         this.field_72769_h.func_72848_b(this);
      }

      this.field_174992_B = 4.9E-324D;
      this.field_174993_C = 4.9E-324D;
      this.field_174987_D = 4.9E-324D;
      this.field_174988_E = -2147483648;
      this.field_174989_F = -2147483648;
      this.field_174990_G = -2147483648;
      this.field_175010_j.func_78717_a(var1);
      this.field_72769_h = var1;
      if (var1 != null) {
         var1.func_72954_a(this);
         this.func_72712_a();
      }

   }

   public void func_72712_a() {
      if (this.field_72769_h != null) {
         this.field_147595_R = true;
         Blocks.field_150362_t.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
         Blocks.field_150361_u.func_150122_b(this.field_72777_q.field_71474_y.field_74347_j);
         this.field_72739_F = this.field_72777_q.field_71474_y.field_151451_c;
         boolean var1 = this.field_175005_X;
         this.field_175005_X = OpenGlHelper.func_176075_f();
         if (var1 && !this.field_175005_X) {
            this.field_174996_N = new RenderList();
            this.field_175007_a = new ListChunkFactory();
         } else if (!var1 && this.field_175005_X) {
            this.field_174996_N = new VboRenderList();
            this.field_175007_a = new VboChunkFactory();
         }

         if (var1 != this.field_175005_X) {
            this.func_174963_q();
            this.func_174980_p();
            this.func_174964_o();
         }

         if (this.field_175008_n != null) {
            this.field_175008_n.func_178160_a();
         }

         this.func_174986_e();
         synchronized(this.field_181024_n) {
            this.field_181024_n.clear();
         }

         this.field_175008_n = new ViewFrustum(this.field_72769_h, this.field_72777_q.field_71474_y.field_151451_c, this, this.field_175007_a);
         if (this.field_72769_h != null) {
            Entity var2 = this.field_72777_q.func_175606_aa();
            if (var2 != null) {
               this.field_175008_n.func_178163_a(var2.field_70165_t, var2.field_70161_v);
            }
         }

         this.field_72740_G = 2;
      }
   }

   protected void func_174986_e() {
      this.field_175009_l.clear();
      this.field_174995_M.func_178514_b();
   }

   public void func_72720_a(int var1, int var2) {
      if (OpenGlHelper.field_148824_g) {
         if (this.field_174991_A != null) {
            this.field_174991_A.func_148026_a(var1, var2);
         }

      }
   }

   public void func_180446_a(Entity var1, ICamera var2, float var3) {
      if (this.field_72740_G > 0) {
         --this.field_72740_G;
      } else {
         double var4 = var1.field_70169_q + (var1.field_70165_t - var1.field_70169_q) * (double)var3;
         double var6 = var1.field_70167_r + (var1.field_70163_u - var1.field_70167_r) * (double)var3;
         double var8 = var1.field_70166_s + (var1.field_70161_v - var1.field_70166_s) * (double)var3;
         this.field_72769_h.field_72984_F.func_76320_a("prepare");
         TileEntityRendererDispatcher.field_147556_a.func_178470_a(this.field_72769_h, this.field_72777_q.func_110434_K(), this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), var3);
         this.field_175010_j.func_180597_a(this.field_72769_h, this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), this.field_72777_q.field_147125_j, this.field_72777_q.field_71474_y, var3);
         this.field_72748_H = 0;
         this.field_72749_I = 0;
         this.field_72750_J = 0;
         Entity var10 = this.field_72777_q.func_175606_aa();
         double var11 = var10.field_70142_S + (var10.field_70165_t - var10.field_70142_S) * (double)var3;
         double var13 = var10.field_70137_T + (var10.field_70163_u - var10.field_70137_T) * (double)var3;
         double var15 = var10.field_70136_U + (var10.field_70161_v - var10.field_70136_U) * (double)var3;
         TileEntityRendererDispatcher.field_147554_b = var11;
         TileEntityRendererDispatcher.field_147555_c = var13;
         TileEntityRendererDispatcher.field_147552_d = var15;
         this.field_175010_j.func_178628_a(var11, var13, var15);
         this.field_72777_q.field_71460_t.func_180436_i();
         this.field_72769_h.field_72984_F.func_76318_c("global");
         List var17 = this.field_72769_h.func_72910_y();
         this.field_72748_H = var17.size();

         int var18;
         Entity var19;
         for(var18 = 0; var18 < this.field_72769_h.field_73007_j.size(); ++var18) {
            var19 = (Entity)this.field_72769_h.field_73007_j.get(var18);
            ++this.field_72749_I;
            if (var19.func_145770_h(var4, var6, var8)) {
               this.field_175010_j.func_147937_a(var19, var3);
            }
         }

         if (this.func_174985_d()) {
            GlStateManager.func_179143_c(519);
            GlStateManager.func_179106_n();
            this.field_175015_z.func_147614_f();
            this.field_175015_z.func_147610_a(false);
            this.field_72769_h.field_72984_F.func_76318_c("entityOutlines");
            RenderHelper.func_74518_a();
            this.field_175010_j.func_178632_c(true);

            for(var18 = 0; var18 < var17.size(); ++var18) {
               var19 = (Entity)var17.get(var18);
               boolean var20 = this.field_72777_q.func_175606_aa() instanceof EntityLivingBase && ((EntityLivingBase)this.field_72777_q.func_175606_aa()).func_70608_bn();
               boolean var21 = var19.func_145770_h(var4, var6, var8) && (var19.field_70158_ak || var2.func_78546_a(var19.func_174813_aQ()) || var19.field_70153_n == this.field_72777_q.field_71439_g) && var19 instanceof EntityPlayer;
               if ((var19 != this.field_72777_q.func_175606_aa() || this.field_72777_q.field_71474_y.field_74320_O != 0 || var20) && var21) {
                  this.field_175010_j.func_147937_a(var19, var3);
               }
            }

            this.field_175010_j.func_178632_c(false);
            RenderHelper.func_74519_b();
            GlStateManager.func_179132_a(false);
            this.field_174991_A.func_148018_a(var3);
            GlStateManager.func_179145_e();
            GlStateManager.func_179132_a(true);
            this.field_72777_q.func_147110_a().func_147610_a(false);
            GlStateManager.func_179127_m();
            GlStateManager.func_179147_l();
            GlStateManager.func_179142_g();
            GlStateManager.func_179143_c(515);
            GlStateManager.func_179126_j();
            GlStateManager.func_179141_d();
         }

         this.field_72769_h.field_72984_F.func_76318_c("entities");
         Iterator var28 = this.field_72755_R.iterator();

         label175:
         while(true) {
            ClassInheritanceMultiMap var35;
            do {
               RenderGlobal.ContainerLocalRenderInformation var29;
               if (!var28.hasNext()) {
                  this.field_72769_h.field_72984_F.func_76318_c("blockentities");
                  RenderHelper.func_74519_b();
                  var28 = this.field_72755_R.iterator();

                  while(true) {
                     List var32;
                     do {
                        if (!var28.hasNext()) {
                           synchronized(this.field_181024_n) {
                              Iterator var31 = this.field_181024_n.iterator();

                              while(true) {
                                 if (!var31.hasNext()) {
                                    break;
                                 }

                                 TileEntity var34 = (TileEntity)var31.next();
                                 TileEntityRendererDispatcher.field_147556_a.func_180546_a(var34, var3, -1);
                              }
                           }

                           this.func_180443_s();
                           var28 = this.field_72738_E.values().iterator();

                           while(true) {
                              DestroyBlockProgress var33;
                              TileEntity var38;
                              Block var41;
                              do {
                                 do {
                                    if (!var28.hasNext()) {
                                       this.func_174969_t();
                                       this.field_72777_q.field_71460_t.func_175072_h();
                                       this.field_72777_q.field_71424_I.func_76319_b();
                                       return;
                                    }

                                    var33 = (DestroyBlockProgress)var28.next();
                                    BlockPos var36 = var33.func_180246_b();
                                    var38 = this.field_72769_h.func_175625_s(var36);
                                    if (var38 instanceof TileEntityChest) {
                                       TileEntityChest var40 = (TileEntityChest)var38;
                                       if (var40.field_145991_k != null) {
                                          var36 = var36.func_177972_a(EnumFacing.WEST);
                                          var38 = this.field_72769_h.func_175625_s(var36);
                                       } else if (var40.field_145992_i != null) {
                                          var36 = var36.func_177972_a(EnumFacing.NORTH);
                                          var38 = this.field_72769_h.func_175625_s(var36);
                                       }
                                    }

                                    var41 = this.field_72769_h.func_180495_p(var36).func_177230_c();
                                 } while(var38 == null);
                              } while(!(var41 instanceof BlockChest) && !(var41 instanceof BlockEnderChest) && !(var41 instanceof BlockSign) && !(var41 instanceof BlockSkull));

                              TileEntityRendererDispatcher.field_147556_a.func_180546_a(var38, var3, var33.func_73106_e());
                           }
                        }

                        var29 = (RenderGlobal.ContainerLocalRenderInformation)var28.next();
                        var32 = var29.field_178036_a.func_178571_g().func_178485_b();
                     } while(var32.isEmpty());

                     Iterator var37 = var32.iterator();

                     while(var37.hasNext()) {
                        TileEntity var39 = (TileEntity)var37.next();
                        TileEntityRendererDispatcher.field_147556_a.func_180546_a(var39, var3, -1);
                     }
                  }
               }

               var29 = (RenderGlobal.ContainerLocalRenderInformation)var28.next();
               Chunk var30 = this.field_72769_h.func_175726_f(var29.field_178036_a.func_178568_j());
               var35 = var30.func_177429_s()[var29.field_178036_a.func_178568_j().func_177956_o() / 16];
            } while(var35.isEmpty());

            Iterator var22 = var35.iterator();

            while(true) {
               Entity var23;
               boolean var24;
               while(true) {
                  if (!var22.hasNext()) {
                     continue label175;
                  }

                  var23 = (Entity)var22.next();
                  var24 = this.field_175010_j.func_178635_a(var23, var2, var4, var6, var8) || var23.field_70153_n == this.field_72777_q.field_71439_g;
                  if (!var24) {
                     break;
                  }

                  boolean var25 = this.field_72777_q.func_175606_aa() instanceof EntityLivingBase ? ((EntityLivingBase)this.field_72777_q.func_175606_aa()).func_70608_bn() : false;
                  if ((var23 != this.field_72777_q.func_175606_aa() || this.field_72777_q.field_71474_y.field_74320_O != 0 || var25) && (var23.field_70163_u < 0.0D || var23.field_70163_u >= 256.0D || this.field_72769_h.func_175667_e(new BlockPos(var23)))) {
                     ++this.field_72749_I;
                     this.field_175010_j.func_147937_a(var23, var3);
                     break;
                  }
               }

               if (!var24 && var23 instanceof EntityWitherSkull) {
                  this.field_72777_q.func_175598_ae().func_178630_b(var23, var3);
               }
            }
         }
      }
   }

   public String func_72735_c() {
      int var1 = this.field_175008_n.field_178164_f.length;
      int var2 = 0;
      Iterator var3 = this.field_72755_R.iterator();

      while(var3.hasNext()) {
         RenderGlobal.ContainerLocalRenderInformation var4 = (RenderGlobal.ContainerLocalRenderInformation)var3.next();
         CompiledChunk var5 = var4.field_178036_a.field_178590_b;
         if (var5 != CompiledChunk.field_178502_a && !var5.func_178489_a()) {
            ++var2;
         }
      }

      return String.format("C: %d/%d %sD: %d, %s", var2, var1, this.field_72777_q.field_175612_E ? "(s) " : "", this.field_72739_F, this.field_174995_M.func_178504_a());
   }

   public String func_72723_d() {
      return "E: " + this.field_72749_I + "/" + this.field_72748_H + ", B: " + this.field_72750_J + ", I: " + (this.field_72748_H - this.field_72750_J - this.field_72749_I);
   }

   public void func_174970_a(Entity var1, double var2, ICamera var4, int var5, boolean var6) {
      if (this.field_72777_q.field_71474_y.field_151451_c != this.field_72739_F) {
         this.func_72712_a();
      }

      this.field_72769_h.field_72984_F.func_76320_a("camera");
      double var7 = var1.field_70165_t - this.field_174992_B;
      double var9 = var1.field_70163_u - this.field_174993_C;
      double var11 = var1.field_70161_v - this.field_174987_D;
      if (this.field_174988_E != var1.field_70176_ah || this.field_174989_F != var1.field_70162_ai || this.field_174990_G != var1.field_70164_aj || var7 * var7 + var9 * var9 + var11 * var11 > 16.0D) {
         this.field_174992_B = var1.field_70165_t;
         this.field_174993_C = var1.field_70163_u;
         this.field_174987_D = var1.field_70161_v;
         this.field_174988_E = var1.field_70176_ah;
         this.field_174989_F = var1.field_70162_ai;
         this.field_174990_G = var1.field_70164_aj;
         this.field_175008_n.func_178163_a(var1.field_70165_t, var1.field_70161_v);
      }

      this.field_72769_h.field_72984_F.func_76318_c("renderlistcamera");
      double var13 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * var2;
      double var15 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * var2;
      double var17 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * var2;
      this.field_174996_N.func_178004_a(var13, var15, var17);
      this.field_72769_h.field_72984_F.func_76318_c("cull");
      if (this.field_175001_U != null) {
         Frustum var19 = new Frustum(this.field_175001_U);
         var19.func_78547_a(this.field_175003_W.field_181059_a, this.field_175003_W.field_181060_b, this.field_175003_W.field_181061_c);
         var4 = var19;
      }

      this.field_72777_q.field_71424_I.func_76318_c("culling");
      BlockPos var35 = new BlockPos(var13, var15 + (double)var1.func_70047_e(), var17);
      RenderChunk var20 = this.field_175008_n.func_178161_a(var35);
      BlockPos var21 = new BlockPos(MathHelper.func_76128_c(var13 / 16.0D) * 16, MathHelper.func_76128_c(var15 / 16.0D) * 16, MathHelper.func_76128_c(var17 / 16.0D) * 16);
      this.field_147595_R = this.field_147595_R || !this.field_175009_l.isEmpty() || var1.field_70165_t != this.field_174997_H || var1.field_70163_u != this.field_174998_I || var1.field_70161_v != this.field_174999_J || (double)var1.field_70125_A != this.field_175000_K || (double)var1.field_70177_z != this.field_174994_L;
      this.field_174997_H = var1.field_70165_t;
      this.field_174998_I = var1.field_70163_u;
      this.field_174999_J = var1.field_70161_v;
      this.field_175000_K = (double)var1.field_70125_A;
      this.field_174994_L = (double)var1.field_70177_z;
      boolean var22 = this.field_175001_U != null;
      RenderGlobal.ContainerLocalRenderInformation var39;
      RenderChunk var41;
      if (!var22 && this.field_147595_R) {
         this.field_147595_R = false;
         this.field_72755_R = Lists.newArrayList();
         LinkedList var23 = Lists.newLinkedList();
         boolean var24 = this.field_72777_q.field_175612_E;
         if (var20 != null) {
            boolean var38 = false;
            RenderGlobal.ContainerLocalRenderInformation var40 = new RenderGlobal.ContainerLocalRenderInformation(var20, (EnumFacing)null, 0);
            Set var42 = this.func_174978_c(var35);
            if (var42.size() == 1) {
               Vector3f var44 = this.func_174962_a(var1, var2);
               EnumFacing var29 = EnumFacing.func_176737_a(var44.x, var44.y, var44.z).func_176734_d();
               var42.remove(var29);
            }

            if (var42.isEmpty()) {
               var38 = true;
            }

            if (var38 && !var6) {
               this.field_72755_R.add(var40);
            } else {
               if (var6 && this.field_72769_h.func_180495_p(var35).func_177230_c().func_149662_c()) {
                  var24 = false;
               }

               var20.func_178577_a(var5);
               var23.add(var40);
            }
         } else {
            int var25 = var35.func_177956_o() > 0 ? 248 : 8;

            for(int var26 = -this.field_72739_F; var26 <= this.field_72739_F; ++var26) {
               for(int var27 = -this.field_72739_F; var27 <= this.field_72739_F; ++var27) {
                  RenderChunk var28 = this.field_175008_n.func_178161_a(new BlockPos((var26 << 4) + 8, var25, (var27 << 4) + 8));
                  if (var28 != null && ((ICamera)var4).func_78546_a(var28.field_178591_c)) {
                     var28.func_178577_a(var5);
                     var23.add(new RenderGlobal.ContainerLocalRenderInformation(var28, (EnumFacing)null, 0));
                  }
               }
            }
         }

         while(!var23.isEmpty()) {
            var39 = (RenderGlobal.ContainerLocalRenderInformation)var23.poll();
            var41 = var39.field_178036_a;
            EnumFacing var43 = var39.field_178034_b;
            BlockPos var45 = var41.func_178568_j();
            this.field_72755_R.add(var39);
            EnumFacing[] var46 = EnumFacing.values();
            int var30 = var46.length;

            for(int var31 = 0; var31 < var30; ++var31) {
               EnumFacing var32 = var46[var31];
               RenderChunk var33 = this.func_181562_a(var21, var41, var32);
               if ((!var24 || !var39.field_178035_c.contains(var32.func_176734_d())) && (!var24 || var43 == null || var41.func_178571_g().func_178495_a(var43.func_176734_d(), var32)) && var33 != null && var33.func_178577_a(var5) && ((ICamera)var4).func_78546_a(var33.field_178591_c)) {
                  RenderGlobal.ContainerLocalRenderInformation var34 = new RenderGlobal.ContainerLocalRenderInformation(var33, var32, var39.field_178032_d + 1);
                  var34.field_178035_c.addAll(var39.field_178035_c);
                  var34.field_178035_c.add(var32);
                  var23.add(var34);
               }
            }
         }
      }

      if (this.field_175002_T) {
         this.func_174984_a(var13, var15, var17);
         this.field_175002_T = false;
      }

      this.field_174995_M.func_178513_e();
      Set var36 = this.field_175009_l;
      this.field_175009_l = Sets.newLinkedHashSet();
      Iterator var37 = this.field_72755_R.iterator();

      while(true) {
         do {
            if (!var37.hasNext()) {
               this.field_175009_l.addAll(var36);
               this.field_72777_q.field_71424_I.func_76319_b();
               return;
            }

            var39 = (RenderGlobal.ContainerLocalRenderInformation)var37.next();
            var41 = var39.field_178036_a;
         } while(!var41.func_178569_m() && !var36.contains(var41));

         this.field_147595_R = true;
         if (this.func_174983_a(var21, var39.field_178036_a)) {
            this.field_72777_q.field_71424_I.func_76320_a("build near");
            this.field_174995_M.func_178505_b(var41);
            var41.func_178575_a(false);
            this.field_72777_q.field_71424_I.func_76319_b();
         } else {
            this.field_175009_l.add(var41);
         }
      }
   }

   private boolean func_174983_a(BlockPos var1, RenderChunk var2) {
      BlockPos var3 = var2.func_178568_j();
      if (MathHelper.func_76130_a(var1.func_177958_n() - var3.func_177958_n()) > 16) {
         return false;
      } else if (MathHelper.func_76130_a(var1.func_177956_o() - var3.func_177956_o()) > 16) {
         return false;
      } else {
         return MathHelper.func_76130_a(var1.func_177952_p() - var3.func_177952_p()) <= 16;
      }
   }

   private Set<EnumFacing> func_174978_c(BlockPos var1) {
      VisGraph var2 = new VisGraph();
      BlockPos var3 = new BlockPos(var1.func_177958_n() >> 4 << 4, var1.func_177956_o() >> 4 << 4, var1.func_177952_p() >> 4 << 4);
      Chunk var4 = this.field_72769_h.func_175726_f(var3);
      Iterator var5 = BlockPos.func_177975_b(var3, var3.func_177982_a(15, 15, 15)).iterator();

      while(var5.hasNext()) {
         BlockPos.MutableBlockPos var6 = (BlockPos.MutableBlockPos)var5.next();
         if (var4.func_177428_a(var6).func_149662_c()) {
            var2.func_178606_a(var6);
         }
      }

      return var2.func_178609_b(var1);
   }

   private RenderChunk func_181562_a(BlockPos var1, RenderChunk var2, EnumFacing var3) {
      BlockPos var4 = var2.func_181701_a(var3);
      if (MathHelper.func_76130_a(var1.func_177958_n() - var4.func_177958_n()) > this.field_72739_F * 16) {
         return null;
      } else if (var4.func_177956_o() >= 0 && var4.func_177956_o() < 256) {
         return MathHelper.func_76130_a(var1.func_177952_p() - var4.func_177952_p()) > this.field_72739_F * 16 ? null : this.field_175008_n.func_178161_a(var4);
      } else {
         return null;
      }
   }

   private void func_174984_a(double var1, double var3, double var5) {
      this.field_175001_U = new ClippingHelperImpl();
      ((ClippingHelperImpl)this.field_175001_U).func_78560_b();
      Matrix4f var7 = new Matrix4f(this.field_175001_U.field_178626_c);
      var7.transpose();
      Matrix4f var8 = new Matrix4f(this.field_175001_U.field_178625_b);
      var8.transpose();
      Matrix4f var9 = new Matrix4f();
      Matrix4f.mul(var8, var7, var9);
      var9.invert();
      this.field_175003_W.field_181059_a = var1;
      this.field_175003_W.field_181060_b = var3;
      this.field_175003_W.field_181061_c = var5;
      this.field_175004_V[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.field_175004_V[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.field_175004_V[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.field_175004_V[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.field_175004_V[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.field_175004_V[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.field_175004_V[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.field_175004_V[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for(int var10 = 0; var10 < 8; ++var10) {
         Matrix4f.transform(var9, this.field_175004_V[var10], this.field_175004_V[var10]);
         Vector4f var10000 = this.field_175004_V[var10];
         var10000.x /= this.field_175004_V[var10].w;
         var10000 = this.field_175004_V[var10];
         var10000.y /= this.field_175004_V[var10].w;
         var10000 = this.field_175004_V[var10];
         var10000.z /= this.field_175004_V[var10].w;
         this.field_175004_V[var10].w = 1.0F;
      }

   }

   protected Vector3f func_174962_a(Entity var1, double var2) {
      float var4 = (float)((double)var1.field_70127_C + (double)(var1.field_70125_A - var1.field_70127_C) * var2);
      float var5 = (float)((double)var1.field_70126_B + (double)(var1.field_70177_z - var1.field_70126_B) * var2);
      if (Minecraft.func_71410_x().field_71474_y.field_74320_O == 2) {
         var4 += 180.0F;
      }

      float var6 = MathHelper.func_76134_b(-var5 * 0.017453292F - 3.1415927F);
      float var7 = MathHelper.func_76126_a(-var5 * 0.017453292F - 3.1415927F);
      float var8 = -MathHelper.func_76134_b(-var4 * 0.017453292F);
      float var9 = MathHelper.func_76126_a(-var4 * 0.017453292F);
      return new Vector3f(var7 * var8, var9, var6 * var8);
   }

   public int func_174977_a(EnumWorldBlockLayer var1, double var2, int var4, Entity var5) {
      RenderHelper.func_74518_a();
      if (var1 == EnumWorldBlockLayer.TRANSLUCENT) {
         this.field_72777_q.field_71424_I.func_76320_a("translucent_sort");
         double var6 = var5.field_70165_t - this.field_147596_f;
         double var8 = var5.field_70163_u - this.field_147597_g;
         double var10 = var5.field_70161_v - this.field_147602_h;
         if (var6 * var6 + var8 * var8 + var10 * var10 > 1.0D) {
            this.field_147596_f = var5.field_70165_t;
            this.field_147597_g = var5.field_70163_u;
            this.field_147602_h = var5.field_70161_v;
            int var12 = 0;
            Iterator var13 = this.field_72755_R.iterator();

            while(var13.hasNext()) {
               RenderGlobal.ContainerLocalRenderInformation var14 = (RenderGlobal.ContainerLocalRenderInformation)var13.next();
               if (var14.field_178036_a.field_178590_b.func_178492_d(var1) && var12++ < 15) {
                  this.field_174995_M.func_178509_c(var14.field_178036_a);
               }
            }
         }

         this.field_72777_q.field_71424_I.func_76319_b();
      }

      this.field_72777_q.field_71424_I.func_76320_a("filterempty");
      int var15 = 0;
      boolean var7 = var1 == EnumWorldBlockLayer.TRANSLUCENT;
      int var16 = var7 ? this.field_72755_R.size() - 1 : 0;
      int var9 = var7 ? -1 : this.field_72755_R.size();
      int var17 = var7 ? -1 : 1;

      for(int var11 = var16; var11 != var9; var11 += var17) {
         RenderChunk var18 = ((RenderGlobal.ContainerLocalRenderInformation)this.field_72755_R.get(var11)).field_178036_a;
         if (!var18.func_178571_g().func_178491_b(var1)) {
            ++var15;
            this.field_174996_N.func_178002_a(var18, var1);
         }
      }

      this.field_72777_q.field_71424_I.func_76318_c("render_" + var1);
      this.func_174982_a(var1);
      this.field_72777_q.field_71424_I.func_76319_b();
      return var15;
   }

   private void func_174982_a(EnumWorldBlockLayer var1) {
      this.field_72777_q.field_71460_t.func_180436_i();
      if (OpenGlHelper.func_176075_f()) {
         GL11.glEnableClientState(32884);
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
         GL11.glEnableClientState(32888);
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
         GL11.glEnableClientState(32888);
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
         GL11.glEnableClientState(32886);
      }

      this.field_174996_N.func_178001_a(var1);
      if (OpenGlHelper.func_176075_f()) {
         List var2 = DefaultVertexFormats.field_176600_a.func_177343_g();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            VertexFormatElement var4 = (VertexFormatElement)var3.next();
            VertexFormatElement.EnumUsage var5 = var4.func_177375_c();
            int var6 = var4.func_177369_e();
            switch(var5) {
            case POSITION:
               GL11.glDisableClientState(32884);
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + var6);
               GL11.glDisableClientState(32888);
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GL11.glDisableClientState(32886);
               GlStateManager.func_179117_G();
            }
         }
      }

      this.field_72777_q.field_71460_t.func_175072_h();
   }

   private void func_174965_a(Iterator<DestroyBlockProgress> var1) {
      while(var1.hasNext()) {
         DestroyBlockProgress var2 = (DestroyBlockProgress)var1.next();
         int var3 = var2.func_82743_f();
         if (this.field_72773_u - var3 > 400) {
            var1.remove();
         }
      }

   }

   public void func_72734_e() {
      ++this.field_72773_u;
      if (this.field_72773_u % 20 == 0) {
         this.func_174965_a(this.field_72738_E.values().iterator());
      }

   }

   private void func_180448_r() {
      GlStateManager.func_179106_n();
      GlStateManager.func_179118_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      RenderHelper.func_74518_a();
      GlStateManager.func_179132_a(false);
      this.field_72770_i.func_110577_a(field_110926_k);
      Tessellator var1 = Tessellator.func_178181_a();
      WorldRenderer var2 = var1.func_178180_c();

      for(int var3 = 0; var3 < 6; ++var3) {
         GlStateManager.func_179094_E();
         if (var3 == 1) {
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var3 == 2) {
            GlStateManager.func_179114_b(-90.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var3 == 3) {
            GlStateManager.func_179114_b(180.0F, 1.0F, 0.0F, 0.0F);
         }

         if (var3 == 4) {
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
         }

         if (var3 == 5) {
            GlStateManager.func_179114_b(-90.0F, 0.0F, 0.0F, 1.0F);
         }

         var2.func_181668_a(7, DefaultVertexFormats.field_181709_i);
         var2.func_181662_b(-100.0D, -100.0D, -100.0D).func_181673_a(0.0D, 0.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var2.func_181662_b(-100.0D, -100.0D, 100.0D).func_181673_a(0.0D, 16.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var2.func_181662_b(100.0D, -100.0D, 100.0D).func_181673_a(16.0D, 16.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var2.func_181662_b(100.0D, -100.0D, -100.0D).func_181673_a(16.0D, 0.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var1.func_78381_a();
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179098_w();
      GlStateManager.func_179141_d();
   }

   public void func_174976_a(float var1, int var2) {
      if (this.field_72777_q.field_71441_e.field_73011_w.func_177502_q() == 1) {
         this.func_180448_r();
      } else if (this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         GlStateManager.func_179090_x();
         Vec3 var3 = this.field_72769_h.func_72833_a(this.field_72777_q.func_175606_aa(), var1);
         float var4 = (float)var3.field_72450_a;
         float var5 = (float)var3.field_72448_b;
         float var6 = (float)var3.field_72449_c;
         if (var2 != 2) {
            float var7 = (var4 * 30.0F + var5 * 59.0F + var6 * 11.0F) / 100.0F;
            float var8 = (var4 * 30.0F + var5 * 70.0F) / 100.0F;
            float var9 = (var4 * 30.0F + var6 * 70.0F) / 100.0F;
            var4 = var7;
            var5 = var8;
            var6 = var9;
         }

         GlStateManager.func_179124_c(var4, var5, var6);
         Tessellator var20 = Tessellator.func_178181_a();
         WorldRenderer var21 = var20.func_178180_c();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179127_m();
         GlStateManager.func_179124_c(var4, var5, var6);
         if (this.field_175005_X) {
            this.field_175012_t.func_177359_a();
            GL11.glEnableClientState(32884);
            GL11.glVertexPointer(3, 5126, 12, 0L);
            this.field_175012_t.func_177358_a(7);
            this.field_175012_t.func_177361_b();
            GL11.glDisableClientState(32884);
         } else {
            GlStateManager.func_179148_o(this.field_72771_w);
         }

         GlStateManager.func_179106_n();
         GlStateManager.func_179118_c();
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         RenderHelper.func_74518_a();
         float[] var22 = this.field_72769_h.field_73011_w.func_76560_a(this.field_72769_h.func_72826_c(var1), var1);
         float var10;
         float var11;
         float var12;
         float var13;
         float var14;
         float var15;
         float var16;
         float var17;
         int var27;
         if (var22 != null) {
            GlStateManager.func_179090_x();
            GlStateManager.func_179103_j(7425);
            GlStateManager.func_179094_E();
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(MathHelper.func_76126_a(this.field_72769_h.func_72929_e(var1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            var10 = var22[0];
            var11 = var22[1];
            var12 = var22[2];
            if (var2 != 2) {
               var13 = (var10 * 30.0F + var11 * 59.0F + var12 * 11.0F) / 100.0F;
               var14 = (var10 * 30.0F + var11 * 70.0F) / 100.0F;
               var15 = (var10 * 30.0F + var12 * 70.0F) / 100.0F;
               var10 = var13;
               var11 = var14;
               var12 = var15;
            }

            var21.func_181668_a(6, DefaultVertexFormats.field_181706_f);
            var21.func_181662_b(0.0D, 100.0D, 0.0D).func_181666_a(var10, var11, var12, var22[3]).func_181675_d();
            boolean var25 = true;

            for(var27 = 0; var27 <= 16; ++var27) {
               var15 = (float)var27 * 3.1415927F * 2.0F / 16.0F;
               var16 = MathHelper.func_76126_a(var15);
               var17 = MathHelper.func_76134_b(var15);
               var21.func_181662_b((double)(var16 * 120.0F), (double)(var17 * 120.0F), (double)(-var17 * 40.0F * var22[3])).func_181666_a(var22[0], var22[1], var22[2], 0.0F).func_181675_d();
            }

            var20.func_78381_a();
            GlStateManager.func_179121_F();
            GlStateManager.func_179103_j(7424);
         }

         GlStateManager.func_179098_w();
         GlStateManager.func_179120_a(770, 1, 1, 0);
         GlStateManager.func_179094_E();
         var10 = 1.0F - this.field_72769_h.func_72867_j(var1);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var10);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.field_72769_h.func_72826_c(var1) * 360.0F, 1.0F, 0.0F, 0.0F);
         var11 = 30.0F;
         this.field_72770_i.func_110577_a(field_110928_i);
         var21.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var21.func_181662_b((double)(-var11), 100.0D, (double)(-var11)).func_181673_a(0.0D, 0.0D).func_181675_d();
         var21.func_181662_b((double)var11, 100.0D, (double)(-var11)).func_181673_a(1.0D, 0.0D).func_181675_d();
         var21.func_181662_b((double)var11, 100.0D, (double)var11).func_181673_a(1.0D, 1.0D).func_181675_d();
         var21.func_181662_b((double)(-var11), 100.0D, (double)var11).func_181673_a(0.0D, 1.0D).func_181675_d();
         var20.func_78381_a();
         var11 = 20.0F;
         this.field_72770_i.func_110577_a(field_110927_h);
         int var24 = this.field_72769_h.func_72853_d();
         int var26 = var24 % 4;
         var27 = var24 / 4 % 2;
         var15 = (float)(var26 + 0) / 4.0F;
         var16 = (float)(var27 + 0) / 2.0F;
         var17 = (float)(var26 + 1) / 4.0F;
         float var18 = (float)(var27 + 1) / 2.0F;
         var21.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var21.func_181662_b((double)(-var11), -100.0D, (double)var11).func_181673_a((double)var17, (double)var18).func_181675_d();
         var21.func_181662_b((double)var11, -100.0D, (double)var11).func_181673_a((double)var15, (double)var18).func_181675_d();
         var21.func_181662_b((double)var11, -100.0D, (double)(-var11)).func_181673_a((double)var15, (double)var16).func_181675_d();
         var21.func_181662_b((double)(-var11), -100.0D, (double)(-var11)).func_181673_a((double)var17, (double)var16).func_181675_d();
         var20.func_78381_a();
         GlStateManager.func_179090_x();
         float var19 = this.field_72769_h.func_72880_h(var1) * var10;
         if (var19 > 0.0F) {
            GlStateManager.func_179131_c(var19, var19, var19, var19);
            if (this.field_175005_X) {
               this.field_175013_s.func_177359_a();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.field_175013_s.func_177358_a(7);
               this.field_175013_s.func_177361_b();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.func_179148_o(this.field_72772_v);
            }
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179084_k();
         GlStateManager.func_179141_d();
         GlStateManager.func_179127_m();
         GlStateManager.func_179121_F();
         GlStateManager.func_179090_x();
         GlStateManager.func_179124_c(0.0F, 0.0F, 0.0F);
         double var23 = this.field_72777_q.field_71439_g.func_174824_e(var1).field_72448_b - this.field_72769_h.func_72919_O();
         if (var23 < 0.0D) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(0.0F, 12.0F, 0.0F);
            if (this.field_175005_X) {
               this.field_175011_u.func_177359_a();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.field_175011_u.func_177358_a(7);
               this.field_175011_u.func_177361_b();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.func_179148_o(this.field_72781_x);
            }

            GlStateManager.func_179121_F();
            var12 = 1.0F;
            var13 = -((float)(var23 + 65.0D));
            var14 = -1.0F;
            var21.func_181668_a(7, DefaultVertexFormats.field_181706_f);
            var21.func_181662_b(-1.0D, (double)var13, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, (double)var13, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, (double)var13, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, (double)var13, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, (double)var13, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, (double)var13, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, (double)var13, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, (double)var13, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(-1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, -1.0D, 1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var21.func_181662_b(1.0D, -1.0D, -1.0D).func_181669_b(0, 0, 0, 255).func_181675_d();
            var20.func_78381_a();
         }

         if (this.field_72769_h.field_73011_w.func_76561_g()) {
            GlStateManager.func_179124_c(var4 * 0.2F + 0.04F, var5 * 0.2F + 0.04F, var6 * 0.6F + 0.1F);
         } else {
            GlStateManager.func_179124_c(var4, var5, var6);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, -((float)(var23 - 16.0D)), 0.0F);
         GlStateManager.func_179148_o(this.field_72781_x);
         GlStateManager.func_179121_F();
         GlStateManager.func_179098_w();
         GlStateManager.func_179132_a(true);
      }
   }

   public void func_180447_b(float var1, int var2) {
      if (this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         if (this.field_72777_q.field_71474_y.func_181147_e() == 2) {
            this.func_180445_c(var1, var2);
         } else {
            GlStateManager.func_179129_p();
            float var3 = (float)(this.field_72777_q.func_175606_aa().field_70137_T + (this.field_72777_q.func_175606_aa().field_70163_u - this.field_72777_q.func_175606_aa().field_70137_T) * (double)var1);
            boolean var4 = true;
            boolean var5 = true;
            Tessellator var6 = Tessellator.func_178181_a();
            WorldRenderer var7 = var6.func_178180_c();
            this.field_72770_i.func_110577_a(field_110925_j);
            GlStateManager.func_179147_l();
            GlStateManager.func_179120_a(770, 771, 1, 0);
            Vec3 var8 = this.field_72769_h.func_72824_f(var1);
            float var9 = (float)var8.field_72450_a;
            float var10 = (float)var8.field_72448_b;
            float var11 = (float)var8.field_72449_c;
            float var12;
            if (var2 != 2) {
               var12 = (var9 * 30.0F + var10 * 59.0F + var11 * 11.0F) / 100.0F;
               float var13 = (var9 * 30.0F + var10 * 70.0F) / 100.0F;
               float var14 = (var9 * 30.0F + var11 * 70.0F) / 100.0F;
               var9 = var12;
               var10 = var13;
               var11 = var14;
            }

            var12 = 4.8828125E-4F;
            double var26 = (double)((float)this.field_72773_u + var1);
            double var15 = this.field_72777_q.func_175606_aa().field_70169_q + (this.field_72777_q.func_175606_aa().field_70165_t - this.field_72777_q.func_175606_aa().field_70169_q) * (double)var1 + var26 * 0.029999999329447746D;
            double var17 = this.field_72777_q.func_175606_aa().field_70166_s + (this.field_72777_q.func_175606_aa().field_70161_v - this.field_72777_q.func_175606_aa().field_70166_s) * (double)var1;
            int var19 = MathHelper.func_76128_c(var15 / 2048.0D);
            int var20 = MathHelper.func_76128_c(var17 / 2048.0D);
            var15 -= (double)(var19 * 2048);
            var17 -= (double)(var20 * 2048);
            float var21 = this.field_72769_h.field_73011_w.func_76571_f() - var3 + 0.33F;
            float var22 = (float)(var15 * 4.8828125E-4D);
            float var23 = (float)(var17 * 4.8828125E-4D);
            var7.func_181668_a(7, DefaultVertexFormats.field_181709_i);

            for(int var24 = -256; var24 < 256; var24 += 32) {
               for(int var25 = -256; var25 < 256; var25 += 32) {
                  var7.func_181662_b((double)(var24 + 0), (double)var21, (double)(var25 + 32)).func_181673_a((double)((float)(var24 + 0) * 4.8828125E-4F + var22), (double)((float)(var25 + 32) * 4.8828125E-4F + var23)).func_181666_a(var9, var10, var11, 0.8F).func_181675_d();
                  var7.func_181662_b((double)(var24 + 32), (double)var21, (double)(var25 + 32)).func_181673_a((double)((float)(var24 + 32) * 4.8828125E-4F + var22), (double)((float)(var25 + 32) * 4.8828125E-4F + var23)).func_181666_a(var9, var10, var11, 0.8F).func_181675_d();
                  var7.func_181662_b((double)(var24 + 32), (double)var21, (double)(var25 + 0)).func_181673_a((double)((float)(var24 + 32) * 4.8828125E-4F + var22), (double)((float)(var25 + 0) * 4.8828125E-4F + var23)).func_181666_a(var9, var10, var11, 0.8F).func_181675_d();
                  var7.func_181662_b((double)(var24 + 0), (double)var21, (double)(var25 + 0)).func_181673_a((double)((float)(var24 + 0) * 4.8828125E-4F + var22), (double)((float)(var25 + 0) * 4.8828125E-4F + var23)).func_181666_a(var9, var10, var11, 0.8F).func_181675_d();
               }
            }

            var6.func_78381_a();
            GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.func_179084_k();
            GlStateManager.func_179089_o();
         }
      }
   }

   public boolean func_72721_a(double var1, double var3, double var5, float var7) {
      return false;
   }

   private void func_180445_c(float var1, int var2) {
      GlStateManager.func_179129_p();
      float var3 = (float)(this.field_72777_q.func_175606_aa().field_70137_T + (this.field_72777_q.func_175606_aa().field_70163_u - this.field_72777_q.func_175606_aa().field_70137_T) * (double)var1);
      Tessellator var4 = Tessellator.func_178181_a();
      WorldRenderer var5 = var4.func_178180_c();
      float var6 = 12.0F;
      float var7 = 4.0F;
      double var8 = (double)((float)this.field_72773_u + var1);
      double var10 = (this.field_72777_q.func_175606_aa().field_70169_q + (this.field_72777_q.func_175606_aa().field_70165_t - this.field_72777_q.func_175606_aa().field_70169_q) * (double)var1 + var8 * 0.029999999329447746D) / 12.0D;
      double var12 = (this.field_72777_q.func_175606_aa().field_70166_s + (this.field_72777_q.func_175606_aa().field_70161_v - this.field_72777_q.func_175606_aa().field_70166_s) * (double)var1) / 12.0D + 0.33000001311302185D;
      float var14 = this.field_72769_h.field_73011_w.func_76571_f() - var3 + 0.33F;
      int var15 = MathHelper.func_76128_c(var10 / 2048.0D);
      int var16 = MathHelper.func_76128_c(var12 / 2048.0D);
      var10 -= (double)(var15 * 2048);
      var12 -= (double)(var16 * 2048);
      this.field_72770_i.func_110577_a(field_110925_j);
      GlStateManager.func_179147_l();
      GlStateManager.func_179120_a(770, 771, 1, 0);
      Vec3 var17 = this.field_72769_h.func_72824_f(var1);
      float var18 = (float)var17.field_72450_a;
      float var19 = (float)var17.field_72448_b;
      float var20 = (float)var17.field_72449_c;
      float var21;
      float var22;
      float var23;
      if (var2 != 2) {
         var21 = (var18 * 30.0F + var19 * 59.0F + var20 * 11.0F) / 100.0F;
         var22 = (var18 * 30.0F + var19 * 70.0F) / 100.0F;
         var23 = (var18 * 30.0F + var20 * 70.0F) / 100.0F;
         var18 = var21;
         var19 = var22;
         var20 = var23;
      }

      var21 = var18 * 0.9F;
      var22 = var19 * 0.9F;
      var23 = var20 * 0.9F;
      float var24 = var18 * 0.7F;
      float var25 = var19 * 0.7F;
      float var26 = var20 * 0.7F;
      float var27 = var18 * 0.8F;
      float var28 = var19 * 0.8F;
      float var29 = var20 * 0.8F;
      float var30 = 0.00390625F;
      float var31 = (float)MathHelper.func_76128_c(var10) * 0.00390625F;
      float var32 = (float)MathHelper.func_76128_c(var12) * 0.00390625F;
      float var33 = (float)(var10 - (double)MathHelper.func_76128_c(var10));
      float var34 = (float)(var12 - (double)MathHelper.func_76128_c(var12));
      boolean var35 = true;
      boolean var36 = true;
      float var37 = 9.765625E-4F;
      GlStateManager.func_179152_a(12.0F, 1.0F, 12.0F);

      for(int var38 = 0; var38 < 2; ++var38) {
         if (var38 == 0) {
            GlStateManager.func_179135_a(false, false, false, false);
         } else {
            switch(var2) {
            case 0:
               GlStateManager.func_179135_a(false, true, true, true);
               break;
            case 1:
               GlStateManager.func_179135_a(true, false, false, true);
               break;
            case 2:
               GlStateManager.func_179135_a(true, true, true, true);
            }
         }

         for(int var39 = -3; var39 <= 4; ++var39) {
            for(int var40 = -3; var40 <= 4; ++var40) {
               var5.func_181668_a(7, DefaultVertexFormats.field_181712_l);
               float var41 = (float)(var39 * 8);
               float var42 = (float)(var40 * 8);
               float var43 = var41 - var33;
               float var44 = var42 - var34;
               if (var14 > -5.0F) {
                  var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 0.0F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var24, var25, var26, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 0.0F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var24, var25, var26, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 0.0F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var24, var25, var26, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 0.0F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var24, var25, var26, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
               }

               if (var14 <= 5.0F) {
                  var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 4.0F - 9.765625E-4F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var18, var19, var20, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 4.0F - 9.765625E-4F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var18, var19, var20, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 4.0F - 9.765625E-4F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var18, var19, var20, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 4.0F - 9.765625E-4F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var18, var19, var20, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
               }

               int var45;
               if (var39 > -1) {
                  for(var45 = 0; var45 < 8; ++var45) {
                     var5.func_181662_b((double)(var43 + (float)var45 + 0.0F), (double)(var14 + 0.0F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + (float)var45 + 0.0F), (double)(var14 + 4.0F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + (float)var45 + 0.0F), (double)(var14 + 4.0F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + (float)var45 + 0.0F), (double)(var14 + 0.0F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                  }
               }

               if (var39 <= 1) {
                  for(var45 = 0; var45 < 8; ++var45) {
                     var5.func_181662_b((double)(var43 + (float)var45 + 1.0F - 9.765625E-4F), (double)(var14 + 0.0F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + (float)var45 + 1.0F - 9.765625E-4F), (double)(var14 + 4.0F), (double)(var44 + 8.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 8.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + (float)var45 + 1.0F - 9.765625E-4F), (double)(var14 + 4.0F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + (float)var45 + 1.0F - 9.765625E-4F), (double)(var14 + 0.0F), (double)(var44 + 0.0F)).func_181673_a((double)((var41 + (float)var45 + 0.5F) * 0.00390625F + var31), (double)((var42 + 0.0F) * 0.00390625F + var32)).func_181666_a(var21, var22, var23, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                  }
               }

               if (var40 > -1) {
                  for(var45 = 0; var45 < 8; ++var45) {
                     var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 4.0F), (double)(var44 + (float)var45 + 0.0F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 4.0F), (double)(var44 + (float)var45 + 0.0F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 0.0F), (double)(var44 + (float)var45 + 0.0F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 0.0F), (double)(var44 + (float)var45 + 0.0F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                  }
               }

               if (var40 <= 1) {
                  for(var45 = 0; var45 < 8; ++var45) {
                     var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 4.0F), (double)(var44 + (float)var45 + 1.0F - 9.765625E-4F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 4.0F), (double)(var44 + (float)var45 + 1.0F - 9.765625E-4F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + 8.0F), (double)(var14 + 0.0F), (double)(var44 + (float)var45 + 1.0F - 9.765625E-4F)).func_181673_a((double)((var41 + 8.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     var5.func_181662_b((double)(var43 + 0.0F), (double)(var14 + 0.0F), (double)(var44 + (float)var45 + 1.0F - 9.765625E-4F)).func_181673_a((double)((var41 + 0.0F) * 0.00390625F + var31), (double)((var42 + (float)var45 + 0.5F) * 0.00390625F + var32)).func_181666_a(var27, var28, var29, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                  }
               }

               var4.func_78381_a();
            }
         }
      }

      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.func_179084_k();
      GlStateManager.func_179089_o();
   }

   public void func_174967_a(long var1) {
      this.field_147595_R |= this.field_174995_M.func_178516_a(var1);
      if (!this.field_175009_l.isEmpty()) {
         Iterator var3 = this.field_175009_l.iterator();

         while(var3.hasNext()) {
            RenderChunk var4 = (RenderChunk)var3.next();
            if (!this.field_174995_M.func_178507_a(var4)) {
               break;
            }

            var4.func_178575_a(false);
            var3.remove();
            long var5 = var1 - System.nanoTime();
            if (var5 < 0L) {
               break;
            }
         }
      }

   }

   public void func_180449_a(Entity var1, float var2) {
      Tessellator var3 = Tessellator.func_178181_a();
      WorldRenderer var4 = var3.func_178180_c();
      WorldBorder var5 = this.field_72769_h.func_175723_af();
      double var6 = (double)(this.field_72777_q.field_71474_y.field_151451_c * 16);
      if (var1.field_70165_t >= var5.func_177728_d() - var6 || var1.field_70165_t <= var5.func_177726_b() + var6 || var1.field_70161_v >= var5.func_177733_e() - var6 || var1.field_70161_v <= var5.func_177736_c() + var6) {
         double var8 = 1.0D - var5.func_177745_a(var1) / var6;
         var8 = Math.pow(var8, 4.0D);
         double var10 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
         double var12 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
         double var14 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 1, 1, 0);
         this.field_72770_i.func_110577_a(field_175006_g);
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179094_E();
         int var16 = var5.func_177734_a().func_177766_a();
         float var17 = (float)(var16 >> 16 & 255) / 255.0F;
         float var18 = (float)(var16 >> 8 & 255) / 255.0F;
         float var19 = (float)(var16 & 255) / 255.0F;
         GlStateManager.func_179131_c(var17, var18, var19, (float)var8);
         GlStateManager.func_179136_a(-3.0F, -3.0F);
         GlStateManager.func_179088_q();
         GlStateManager.func_179092_a(516, 0.1F);
         GlStateManager.func_179141_d();
         GlStateManager.func_179129_p();
         float var20 = (float)(Minecraft.func_71386_F() % 3000L) / 3000.0F;
         float var21 = 0.0F;
         float var22 = 0.0F;
         float var23 = 128.0F;
         var4.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var4.func_178969_c(-var10, -var12, -var14);
         double var24 = Math.max((double)MathHelper.func_76128_c(var14 - var6), var5.func_177736_c());
         double var26 = Math.min((double)MathHelper.func_76143_f(var14 + var6), var5.func_177733_e());
         float var28;
         double var29;
         double var31;
         float var33;
         if (var10 > var5.func_177728_d() - var6) {
            var28 = 0.0F;

            for(var29 = var24; var29 < var26; var28 += 0.5F) {
               var31 = Math.min(1.0D, var26 - var29);
               var33 = (float)var31 * 0.5F;
               var4.func_181662_b(var5.func_177728_d(), 256.0D, var29).func_181673_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177728_d(), 256.0D, var29 + var31).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177728_d(), 0.0D, var29 + var31).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177728_d(), 0.0D, var29).func_181673_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
               ++var29;
            }
         }

         if (var10 < var5.func_177726_b() + var6) {
            var28 = 0.0F;

            for(var29 = var24; var29 < var26; var28 += 0.5F) {
               var31 = Math.min(1.0D, var26 - var29);
               var33 = (float)var31 * 0.5F;
               var4.func_181662_b(var5.func_177726_b(), 256.0D, var29).func_181673_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177726_b(), 256.0D, var29 + var31).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177726_b(), 0.0D, var29 + var31).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177726_b(), 0.0D, var29).func_181673_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
               ++var29;
            }
         }

         var24 = Math.max((double)MathHelper.func_76128_c(var10 - var6), var5.func_177726_b());
         var26 = Math.min((double)MathHelper.func_76143_f(var10 + var6), var5.func_177728_d());
         if (var14 > var5.func_177733_e() - var6) {
            var28 = 0.0F;

            for(var29 = var24; var29 < var26; var28 += 0.5F) {
               var31 = Math.min(1.0D, var26 - var29);
               var33 = (float)var31 * 0.5F;
               var4.func_181662_b(var29, 256.0D, var5.func_177733_e()).func_181673_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 256.0D, var5.func_177733_e()).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 0.0D, var5.func_177733_e()).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var29, 0.0D, var5.func_177733_e()).func_181673_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
               ++var29;
            }
         }

         if (var14 < var5.func_177736_c() + var6) {
            var28 = 0.0F;

            for(var29 = var24; var29 < var26; var28 += 0.5F) {
               var31 = Math.min(1.0D, var26 - var29);
               var33 = (float)var31 * 0.5F;
               var4.func_181662_b(var29, 256.0D, var5.func_177736_c()).func_181673_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 256.0D, var5.func_177736_c()).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 0.0D, var5.func_177736_c()).func_181673_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var29, 0.0D, var5.func_177736_c()).func_181673_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
               ++var29;
            }
         }

         var3.func_78381_a();
         var4.func_178969_c(0.0D, 0.0D, 0.0D);
         GlStateManager.func_179089_o();
         GlStateManager.func_179118_c();
         GlStateManager.func_179136_a(0.0F, 0.0F);
         GlStateManager.func_179113_r();
         GlStateManager.func_179141_d();
         GlStateManager.func_179084_k();
         GlStateManager.func_179121_F();
         GlStateManager.func_179132_a(true);
      }
   }

   private void func_180443_s() {
      GlStateManager.func_179120_a(774, 768, 1, 0);
      GlStateManager.func_179147_l();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.func_179136_a(-3.0F, -3.0F);
      GlStateManager.func_179088_q();
      GlStateManager.func_179092_a(516, 0.1F);
      GlStateManager.func_179141_d();
      GlStateManager.func_179094_E();
   }

   private void func_174969_t() {
      GlStateManager.func_179118_c();
      GlStateManager.func_179136_a(0.0F, 0.0F);
      GlStateManager.func_179113_r();
      GlStateManager.func_179141_d();
      GlStateManager.func_179132_a(true);
      GlStateManager.func_179121_F();
   }

   public void func_174981_a(Tessellator var1, WorldRenderer var2, Entity var3, float var4) {
      double var5 = var3.field_70142_S + (var3.field_70165_t - var3.field_70142_S) * (double)var4;
      double var7 = var3.field_70137_T + (var3.field_70163_u - var3.field_70137_T) * (double)var4;
      double var9 = var3.field_70136_U + (var3.field_70161_v - var3.field_70136_U) * (double)var4;
      if (!this.field_72738_E.isEmpty()) {
         this.field_72770_i.func_110577_a(TextureMap.field_110575_b);
         this.func_180443_s();
         var2.func_181668_a(7, DefaultVertexFormats.field_176600_a);
         var2.func_178969_c(-var5, -var7, -var9);
         var2.func_78914_f();
         Iterator var11 = this.field_72738_E.values().iterator();

         while(var11.hasNext()) {
            DestroyBlockProgress var12 = (DestroyBlockProgress)var11.next();
            BlockPos var13 = var12.func_180246_b();
            double var14 = (double)var13.func_177958_n() - var5;
            double var16 = (double)var13.func_177956_o() - var7;
            double var18 = (double)var13.func_177952_p() - var9;
            Block var20 = this.field_72769_h.func_180495_p(var13).func_177230_c();
            if (!(var20 instanceof BlockChest) && !(var20 instanceof BlockEnderChest) && !(var20 instanceof BlockSign) && !(var20 instanceof BlockSkull)) {
               if (var14 * var14 + var16 * var16 + var18 * var18 > 1024.0D) {
                  var11.remove();
               } else {
                  IBlockState var21 = this.field_72769_h.func_180495_p(var13);
                  if (var21.func_177230_c().func_149688_o() != Material.field_151579_a) {
                     int var22 = var12.func_73106_e();
                     TextureAtlasSprite var23 = this.field_94141_F[var22];
                     BlockRendererDispatcher var24 = this.field_72777_q.func_175602_ab();
                     var24.func_175020_a(var21, var13, var23, this.field_72769_h);
                  }
               }
            }
         }

         var1.func_78381_a();
         var2.func_178969_c(0.0D, 0.0D, 0.0D);
         this.func_174969_t();
      }

   }

   public void func_72731_b(EntityPlayer var1, MovingObjectPosition var2, int var3, float var4) {
      if (var3 == 0 && var2.field_72313_a == MovingObjectPosition.MovingObjectType.BLOCK) {
         GlStateManager.func_179147_l();
         GlStateManager.func_179120_a(770, 771, 1, 0);
         GlStateManager.func_179131_c(0.0F, 0.0F, 0.0F, 0.4F);
         GL11.glLineWidth(2.0F);
         GlStateManager.func_179090_x();
         GlStateManager.func_179132_a(false);
         float var5 = 0.002F;
         BlockPos var6 = var2.func_178782_a();
         Block var7 = this.field_72769_h.func_180495_p(var6).func_177230_c();
         if (var7.func_149688_o() != Material.field_151579_a && this.field_72769_h.func_175723_af().func_177746_a(var6)) {
            var7.func_180654_a(this.field_72769_h, var6);
            double var8 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var4;
            double var10 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var4;
            double var12 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var4;
            func_181561_a(var7.func_180646_a(this.field_72769_h, var6).func_72314_b(0.0020000000949949026D, 0.0020000000949949026D, 0.0020000000949949026D).func_72317_d(-var8, -var10, -var12));
         }

         GlStateManager.func_179132_a(true);
         GlStateManager.func_179098_w();
         GlStateManager.func_179084_k();
      }

   }

   public static void func_181561_a(AxisAlignedBB var0) {
      Tessellator var1 = Tessellator.func_178181_a();
      WorldRenderer var2 = var1.func_178180_c();
      var2.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      var2.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181675_d();
      var1.func_78381_a();
      var2.func_181668_a(3, DefaultVertexFormats.field_181705_e);
      var2.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181675_d();
      var1.func_78381_a();
      var2.func_181668_a(1, DefaultVertexFormats.field_181705_e);
      var2.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181675_d();
      var2.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181675_d();
      var1.func_78381_a();
   }

   public static void func_181563_a(AxisAlignedBB var0, int var1, int var2, int var3, int var4) {
      Tessellator var5 = Tessellator.func_178181_a();
      WorldRenderer var6 = var5.func_178180_c();
      var6.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      var6.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var5.func_78381_a();
      var6.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      var6.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var5.func_78381_a();
      var6.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      var6.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72339_c).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72338_b, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72336_d, var0.field_72337_e, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72338_b, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var6.func_181662_b(var0.field_72340_a, var0.field_72337_e, var0.field_72334_f).func_181669_b(var1, var2, var3, var4).func_181675_d();
      var5.func_78381_a();
   }

   private void func_72725_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.field_175008_n.func_178162_a(var1, var2, var3, var4, var5, var6);
   }

   public void func_174960_a(BlockPos var1) {
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      this.func_72725_b(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
   }

   public void func_174959_b(BlockPos var1) {
      int var2 = var1.func_177958_n();
      int var3 = var1.func_177956_o();
      int var4 = var1.func_177952_p();
      this.func_72725_b(var2 - 1, var3 - 1, var4 - 1, var2 + 1, var3 + 1, var4 + 1);
   }

   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.func_72725_b(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1);
   }

   public void func_174961_a(String var1, BlockPos var2) {
      ISound var3 = (ISound)this.field_147593_P.get(var2);
      if (var3 != null) {
         this.field_72777_q.func_147118_V().func_147683_b(var3);
         this.field_147593_P.remove(var2);
      }

      if (var1 != null) {
         ItemRecord var4 = ItemRecord.func_150926_b(var1);
         if (var4 != null) {
            this.field_72777_q.field_71456_v.func_73833_a(var4.func_150927_i());
         }

         PositionedSoundRecord var5 = PositionedSoundRecord.func_147675_a(new ResourceLocation(var1), (float)var2.func_177958_n(), (float)var2.func_177956_o(), (float)var2.func_177952_p());
         this.field_147593_P.put(var2, var5);
         this.field_72777_q.func_147118_V().func_147682_a(var5);
      }

   }

   public void func_72704_a(String var1, double var2, double var4, double var6, float var8, float var9) {
   }

   public void func_85102_a(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10) {
   }

   public void func_180442_a(int var1, boolean var2, final double var3, final double var5, final double var7, double var9, double var11, double var13, int... var15) {
      try {
         this.func_174974_b(var1, var2, var3, var5, var7, var9, var11, var13, var15);
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.func_85055_a(var19, "Exception while adding particle");
         CrashReportCategory var18 = var17.func_85058_a("Particle being added");
         var18.func_71507_a("ID", var1);
         if (var15 != null) {
            var18.func_71507_a("Parameters", var15);
         }

         var18.func_71500_a("Position", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.func_85074_a(var3, var5, var7);
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         throw new ReportedException(var17);
      }
   }

   private void func_174972_a(EnumParticleTypes var1, double var2, double var4, double var6, double var8, double var10, double var12, int... var14) {
      this.func_180442_a(var1.func_179348_c(), var1.func_179344_e(), var2, var4, var6, var8, var10, var12, var14);
   }

   private EntityFX func_174974_b(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int... var15) {
      if (this.field_72777_q != null && this.field_72777_q.func_175606_aa() != null && this.field_72777_q.field_71452_i != null) {
         int var16 = this.field_72777_q.field_71474_y.field_74362_aa;
         if (var16 == 1 && this.field_72769_h.field_73012_v.nextInt(3) == 0) {
            var16 = 2;
         }

         double var17 = this.field_72777_q.func_175606_aa().field_70165_t - var3;
         double var19 = this.field_72777_q.func_175606_aa().field_70163_u - var5;
         double var21 = this.field_72777_q.func_175606_aa().field_70161_v - var7;
         if (var2) {
            return this.field_72777_q.field_71452_i.func_178927_a(var1, var3, var5, var7, var9, var11, var13, var15);
         } else {
            double var23 = 16.0D;
            if (var17 * var17 + var19 * var19 + var21 * var21 > 256.0D) {
               return null;
            } else {
               return var16 > 1 ? null : this.field_72777_q.field_71452_i.func_178927_a(var1, var3, var5, var7, var9, var11, var13, var15);
            }
         }
      } else {
         return null;
      }
   }

   public void func_72703_a(Entity var1) {
   }

   public void func_72709_b(Entity var1) {
   }

   public void func_72728_f() {
   }

   public void func_180440_a(int var1, BlockPos var2, int var3) {
      switch(var1) {
      case 1013:
      case 1018:
         if (this.field_72777_q.func_175606_aa() != null) {
            double var4 = (double)var2.func_177958_n() - this.field_72777_q.func_175606_aa().field_70165_t;
            double var6 = (double)var2.func_177956_o() - this.field_72777_q.func_175606_aa().field_70163_u;
            double var8 = (double)var2.func_177952_p() - this.field_72777_q.func_175606_aa().field_70161_v;
            double var10 = Math.sqrt(var4 * var4 + var6 * var6 + var8 * var8);
            double var12 = this.field_72777_q.func_175606_aa().field_70165_t;
            double var14 = this.field_72777_q.func_175606_aa().field_70163_u;
            double var16 = this.field_72777_q.func_175606_aa().field_70161_v;
            if (var10 > 0.0D) {
               var12 += var4 / var10 * 2.0D;
               var14 += var6 / var10 * 2.0D;
               var16 += var8 / var10 * 2.0D;
            }

            if (var1 == 1013) {
               this.field_72769_h.func_72980_b(var12, var14, var16, "mob.wither.spawn", 1.0F, 1.0F, false);
            } else {
               this.field_72769_h.func_72980_b(var12, var14, var16, "mob.enderdragon.end", 5.0F, 1.0F, false);
            }
         }
      default:
      }
   }

   public void func_180439_a(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      Random var5 = this.field_72769_h.field_73012_v;
      double var7;
      double var9;
      double var11;
      int var13;
      int var18;
      double var19;
      double var21;
      double var23;
      double var32;
      switch(var2) {
      case 1000:
         this.field_72769_h.func_175731_a(var3, "random.click", 1.0F, 1.0F, false);
         break;
      case 1001:
         this.field_72769_h.func_175731_a(var3, "random.click", 1.0F, 1.2F, false);
         break;
      case 1002:
         this.field_72769_h.func_175731_a(var3, "random.bow", 1.0F, 1.2F, false);
         break;
      case 1003:
         this.field_72769_h.func_175731_a(var3, "random.door_open", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1004:
         this.field_72769_h.func_175731_a(var3, "random.fizz", 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);
         break;
      case 1005:
         if (Item.func_150899_d(var4) instanceof ItemRecord) {
            this.field_72769_h.func_175717_a(var3, "records." + ((ItemRecord)Item.func_150899_d(var4)).field_150929_a);
         } else {
            this.field_72769_h.func_175717_a(var3, (String)null);
         }
         break;
      case 1006:
         this.field_72769_h.func_175731_a(var3, "random.door_close", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.field_72769_h.func_175731_a(var3, "mob.ghast.charge", 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1008:
         this.field_72769_h.func_175731_a(var3, "mob.ghast.fireball", 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1009:
         this.field_72769_h.func_175731_a(var3, "mob.ghast.fireball", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1010:
         this.field_72769_h.func_175731_a(var3, "mob.zombie.wood", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1011:
         this.field_72769_h.func_175731_a(var3, "mob.zombie.metal", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1012:
         this.field_72769_h.func_175731_a(var3, "mob.zombie.woodbreak", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1014:
         this.field_72769_h.func_175731_a(var3, "mob.wither.shoot", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1015:
         this.field_72769_h.func_175731_a(var3, "mob.bat.takeoff", 0.05F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.field_72769_h.func_175731_a(var3, "mob.zombie.infect", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.field_72769_h.func_175731_a(var3, "mob.zombie.unfect", 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.field_72769_h.func_175731_a(var3, "random.anvil_break", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1021:
         this.field_72769_h.func_175731_a(var3, "random.anvil_use", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1022:
         this.field_72769_h.func_175731_a(var3, "random.anvil_land", 0.3F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2000:
         int var31 = var4 % 3 - 1;
         int var8 = var4 / 3 % 3 - 1;
         var9 = (double)var3.func_177958_n() + (double)var31 * 0.6D + 0.5D;
         var11 = (double)var3.func_177956_o() + 0.5D;
         var32 = (double)var3.func_177952_p() + (double)var8 * 0.6D + 0.5D;

         for(int var33 = 0; var33 < 10; ++var33) {
            double var34 = var5.nextDouble() * 0.2D + 0.01D;
            double var35 = var9 + (double)var31 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var8 * 0.5D;
            double var20 = var11 + (var5.nextDouble() - 0.5D) * 0.5D;
            double var22 = var32 + (double)var8 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var31 * 0.5D;
            double var24 = (double)var31 * var34 + var5.nextGaussian() * 0.01D;
            double var26 = -0.03D + var5.nextGaussian() * 0.01D;
            double var28 = (double)var8 * var34 + var5.nextGaussian() * 0.01D;
            this.func_174972_a(EnumParticleTypes.SMOKE_NORMAL, var35, var20, var22, var24, var26, var28);
         }

         return;
      case 2001:
         Block var6 = Block.func_149729_e(var4 & 4095);
         if (var6.func_149688_o() != Material.field_151579_a) {
            this.field_72777_q.func_147118_V().func_147682_a(new PositionedSoundRecord(new ResourceLocation(var6.field_149762_H.func_150495_a()), (var6.field_149762_H.func_150497_c() + 1.0F) / 2.0F, var6.field_149762_H.func_150494_d() * 0.8F, (float)var3.func_177958_n() + 0.5F, (float)var3.func_177956_o() + 0.5F, (float)var3.func_177952_p() + 0.5F));
         }

         this.field_72777_q.field_71452_i.func_180533_a(var3, var6.func_176203_a(var4 >> 12 & 255));
         break;
      case 2002:
         var7 = (double)var3.func_177958_n();
         var9 = (double)var3.func_177956_o();
         var11 = (double)var3.func_177952_p();

         for(var13 = 0; var13 < 8; ++var13) {
            this.func_174972_a(EnumParticleTypes.ITEM_CRACK, var7, var9, var11, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D, Item.func_150891_b(Items.field_151068_bn), var4);
         }

         var13 = Items.field_151068_bn.func_77620_a(var4);
         float var14 = (float)(var13 >> 16 & 255) / 255.0F;
         float var15 = (float)(var13 >> 8 & 255) / 255.0F;
         float var16 = (float)(var13 >> 0 & 255) / 255.0F;
         EnumParticleTypes var17 = EnumParticleTypes.SPELL;
         if (Items.field_151068_bn.func_77833_h(var4)) {
            var17 = EnumParticleTypes.SPELL_INSTANT;
         }

         for(var18 = 0; var18 < 100; ++var18) {
            var19 = var5.nextDouble() * 4.0D;
            var21 = var5.nextDouble() * 3.141592653589793D * 2.0D;
            var23 = Math.cos(var21) * var19;
            double var25 = 0.01D + var5.nextDouble() * 0.5D;
            double var27 = Math.sin(var21) * var19;
            EntityFX var29 = this.func_174974_b(var17.func_179348_c(), var17.func_179344_e(), var7 + var23 * 0.1D, var9 + 0.3D, var11 + var27 * 0.1D, var23, var25, var27);
            if (var29 != null) {
               float var30 = 0.75F + var5.nextFloat() * 0.25F;
               var29.func_70538_b(var14 * var30, var15 * var30, var16 * var30);
               var29.func_70543_e((float)var19);
            }
         }

         this.field_72769_h.func_175731_a(var3, "game.potion.smash", 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         var7 = (double)var3.func_177958_n() + 0.5D;
         var9 = (double)var3.func_177956_o();
         var11 = (double)var3.func_177952_p() + 0.5D;

         for(var13 = 0; var13 < 8; ++var13) {
            this.func_174972_a(EnumParticleTypes.ITEM_CRACK, var7, var9, var11, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D, Item.func_150891_b(Items.field_151061_bv));
         }

         for(var32 = 0.0D; var32 < 6.283185307179586D; var32 += 0.15707963267948966D) {
            this.func_174972_a(EnumParticleTypes.PORTAL, var7 + Math.cos(var32) * 5.0D, var9 - 0.4D, var11 + Math.sin(var32) * 5.0D, Math.cos(var32) * -5.0D, 0.0D, Math.sin(var32) * -5.0D);
            this.func_174972_a(EnumParticleTypes.PORTAL, var7 + Math.cos(var32) * 5.0D, var9 - 0.4D, var11 + Math.sin(var32) * 5.0D, Math.cos(var32) * -7.0D, 0.0D, Math.sin(var32) * -7.0D);
         }

         return;
      case 2004:
         for(var18 = 0; var18 < 20; ++var18) {
            var19 = (double)var3.func_177958_n() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            var21 = (double)var3.func_177956_o() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            var23 = (double)var3.func_177952_p() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            this.field_72769_h.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, var19, var21, var23, 0.0D, 0.0D, 0.0D, new int[0]);
            this.field_72769_h.func_175688_a(EnumParticleTypes.FLAME, var19, var21, var23, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         return;
      case 2005:
         ItemDye.func_180617_a(this.field_72769_h, var3, var4);
      }

   }

   public void func_180441_b(int var1, BlockPos var2, int var3) {
      if (var3 >= 0 && var3 < 10) {
         DestroyBlockProgress var4 = (DestroyBlockProgress)this.field_72738_E.get(var1);
         if (var4 == null || var4.func_180246_b().func_177958_n() != var2.func_177958_n() || var4.func_180246_b().func_177956_o() != var2.func_177956_o() || var4.func_180246_b().func_177952_p() != var2.func_177952_p()) {
            var4 = new DestroyBlockProgress(var1, var2);
            this.field_72738_E.put(var1, var4);
         }

         var4.func_73107_a(var3);
         var4.func_82744_b(this.field_72773_u);
      } else {
         this.field_72738_E.remove(var1);
      }

   }

   public void func_174979_m() {
      this.field_147595_R = true;
   }

   public void func_181023_a(Collection<TileEntity> var1, Collection<TileEntity> var2) {
      synchronized(this.field_181024_n) {
         this.field_181024_n.removeAll(var1);
         this.field_181024_n.addAll(var2);
      }
   }

   class ContainerLocalRenderInformation {
      final RenderChunk field_178036_a;
      final EnumFacing field_178034_b;
      final Set<EnumFacing> field_178035_c;
      final int field_178032_d;

      private ContainerLocalRenderInformation(RenderChunk var2, EnumFacing var3, int var4) {
         super();
         this.field_178035_c = EnumSet.noneOf(EnumFacing.class);
         this.field_178036_a = var2;
         this.field_178034_b = var3;
         this.field_178032_d = var4;
      }

      // $FF: synthetic method
      ContainerLocalRenderInformation(RenderChunk var2, EnumFacing var3, int var4, Object var5) {
         this(var2, var3, var4);
      }
   }
}
