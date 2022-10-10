package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAbstractSkull;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListedRenderChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBoneMeal;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.state.properties.ChestType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldRenderer implements IWorldEventListener, AutoCloseable, IResourceManagerReloadListener {
   private static final Logger field_147599_m = LogManager.getLogger();
   private static final ResourceLocation field_110927_h = new ResourceLocation("textures/environment/moon_phases.png");
   private static final ResourceLocation field_110928_i = new ResourceLocation("textures/environment/sun.png");
   private static final ResourceLocation field_110925_j = new ResourceLocation("textures/environment/clouds.png");
   private static final ResourceLocation field_110926_k = new ResourceLocation("textures/environment/end_sky.png");
   private static final ResourceLocation field_175006_g = new ResourceLocation("textures/misc/forcefield.png");
   public static final EnumFacing[] field_200006_a = EnumFacing.values();
   private final Minecraft field_72777_q;
   private final TextureManager field_72770_i;
   private final RenderManager field_175010_j;
   private WorldClient field_72769_h;
   private Set<RenderChunk> field_175009_l = Sets.newLinkedHashSet();
   private List<WorldRenderer.ContainerLocalRenderInformation> field_72755_R = Lists.newArrayListWithCapacity(69696);
   private final Set<TileEntity> field_181024_n = Sets.newHashSet();
   private ViewFrustum field_175008_n;
   private int field_72772_v = -1;
   private int field_72771_w = -1;
   private int field_72781_x = -1;
   private final VertexFormat field_175014_r;
   private VertexBuffer field_175013_s;
   private VertexBuffer field_175012_t;
   private VertexBuffer field_175011_u;
   private final int field_204606_x = 28;
   private boolean field_204607_y = true;
   private int field_204608_z = -1;
   private VertexBuffer field_204601_A;
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
   private int field_204602_S = -2147483648;
   private int field_204603_T = -2147483648;
   private int field_204604_U = -2147483648;
   private Vec3d field_204605_V;
   private int field_204800_W;
   private ChunkRenderDispatcher field_174995_M;
   private ChunkRenderContainer field_174996_N;
   private int field_72739_F;
   private int field_72740_G;
   private int field_72748_H;
   private int field_72749_I;
   private int field_72750_J;
   private boolean field_175002_T;
   private ClippingHelper field_175001_U;
   private final Vector4f[] field_175004_V;
   private final Vector3d field_175003_W;
   private boolean field_175005_X;
   private IRenderChunkFactory field_175007_a;
   private double field_147596_f;
   private double field_147597_g;
   private double field_147602_h;
   private boolean field_147595_R;
   private boolean field_184386_ad;
   private final Set<BlockPos> field_184387_ae;

   public WorldRenderer(Minecraft var1) {
      super();
      this.field_204605_V = Vec3d.field_186680_a;
      this.field_204800_W = -1;
      this.field_72739_F = -1;
      this.field_72740_G = 2;
      this.field_175004_V = new Vector4f[8];
      this.field_175003_W = new Vector3d();
      this.field_147595_R = true;
      this.field_184387_ae = Sets.newHashSet();
      this.field_72777_q = var1;
      this.field_175010_j = var1.func_175598_ae();
      this.field_72770_i = var1.func_110434_K();
      this.field_72770_i.func_110577_a(field_175006_g);
      GlStateManager.func_187421_b(3553, 10242, 10497);
      GlStateManager.func_187421_b(3553, 10243, 10497);
      GlStateManager.func_179144_i(0);
      this.func_174971_n();
      this.field_175005_X = OpenGlHelper.func_176075_f();
      if (this.field_175005_X) {
         this.field_174996_N = new VboRenderList();
         this.field_175007_a = RenderChunk::new;
      } else {
         this.field_174996_N = new RenderList();
         this.field_175007_a = ListedRenderChunk::new;
      }

      this.field_175014_r = new VertexFormat();
      this.field_175014_r.func_181721_a(new VertexFormatElement(0, VertexFormatElement.EnumType.FLOAT, VertexFormatElement.EnumUsage.POSITION, 3));
      this.func_174963_q();
      this.func_174980_p();
      this.func_174964_o();
   }

   public void close() {
      if (this.field_174991_A != null) {
         this.field_174991_A.close();
      }

   }

   public void func_195410_a(IResourceManager var1) {
      this.func_174971_n();
   }

   private void func_174971_n() {
      TextureMap var1 = this.field_72777_q.func_147117_R();
      this.field_94141_F[0] = var1.func_195424_a(ModelBakery.field_207770_h);
      this.field_94141_F[1] = var1.func_195424_a(ModelBakery.field_207771_i);
      this.field_94141_F[2] = var1.func_195424_a(ModelBakery.field_207772_j);
      this.field_94141_F[3] = var1.func_195424_a(ModelBakery.field_207773_k);
      this.field_94141_F[4] = var1.func_195424_a(ModelBakery.field_207774_l);
      this.field_94141_F[5] = var1.func_195424_a(ModelBakery.field_207775_m);
      this.field_94141_F[6] = var1.func_195424_a(ModelBakery.field_207776_n);
      this.field_94141_F[7] = var1.func_195424_a(ModelBakery.field_207777_o);
      this.field_94141_F[8] = var1.func_195424_a(ModelBakery.field_207778_p);
      this.field_94141_F[9] = var1.func_195424_a(ModelBakery.field_207779_q);
   }

   public void func_174966_b() {
      if (OpenGlHelper.field_148824_g) {
         if (ShaderLinkHelper.func_148074_b() == null) {
            ShaderLinkHelper.func_148076_a();
         }

         ResourceLocation var1 = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.field_174991_A = new ShaderGroup(this.field_72777_q.func_110434_K(), this.field_72777_q.func_195551_G(), this.field_72777_q.func_147110_a(), var1);
            this.field_174991_A.func_148026_a(this.field_72777_q.field_195558_d.func_198109_k(), this.field_72777_q.field_195558_d.func_198091_l());
            this.field_175015_z = this.field_174991_A.func_177066_a("final");
         } catch (IOException var3) {
            field_147599_m.warn("Failed to load shader: {}", var1, var3);
            this.field_174991_A = null;
            this.field_175015_z = null;
         } catch (JsonSyntaxException var4) {
            field_147599_m.warn("Failed to load shader: {}", var1, var4);
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
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
         this.field_175015_z.func_178038_a(this.field_72777_q.field_195558_d.func_198109_k(), this.field_72777_q.field_195558_d.func_198091_l(), false);
         GlStateManager.func_179084_k();
      }

   }

   protected boolean func_174985_d() {
      return this.field_175015_z != null && this.field_174991_A != null && this.field_72777_q.field_71439_g != null;
   }

   private void func_174964_o() {
      Tessellator var1 = Tessellator.func_178181_a();
      BufferBuilder var2 = var1.func_178180_c();
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
         GlStateManager.func_187423_f(this.field_72781_x, 4864);
         this.func_174968_a(var2, -16.0F, true);
         var1.func_78381_a();
         GlStateManager.func_187415_K();
      }

   }

   private void func_174980_p() {
      Tessellator var1 = Tessellator.func_178181_a();
      BufferBuilder var2 = var1.func_178180_c();
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
         GlStateManager.func_187423_f(this.field_72771_w, 4864);
         this.func_174968_a(var2, 16.0F, false);
         var1.func_78381_a();
         GlStateManager.func_187415_K();
      }

   }

   private void func_174968_a(BufferBuilder var1, float var2, boolean var3) {
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
      BufferBuilder var2 = var1.func_178180_c();
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
         GlStateManager.func_187423_f(this.field_72772_v, 4864);
         this.func_180444_a(var2);
         var1.func_78381_a();
         GlStateManager.func_187415_K();
         GlStateManager.func_179121_F();
      }

   }

   private void func_180444_a(BufferBuilder var1) {
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

   public void func_72732_a(@Nullable WorldClient var1) {
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
      } else {
         this.field_175009_l.clear();
         this.field_72755_R.clear();
         if (this.field_175008_n != null) {
            this.field_175008_n.func_178160_a();
            this.field_175008_n = null;
         }

         if (this.field_174995_M != null) {
            this.field_174995_M.func_188244_g();
         }

         this.field_174995_M = null;
      }

   }

   public void func_72712_a() {
      if (this.field_72769_h != null) {
         if (this.field_174995_M == null) {
            this.field_174995_M = new ChunkRenderDispatcher();
         }

         this.field_147595_R = true;
         this.field_204607_y = true;
         BlockLeaves.func_196475_b(this.field_72777_q.field_71474_y.field_74347_j);
         this.field_72739_F = this.field_72777_q.field_71474_y.field_151451_c;
         boolean var1 = this.field_175005_X;
         this.field_175005_X = OpenGlHelper.func_176075_f();
         if (var1 && !this.field_175005_X) {
            this.field_174996_N = new RenderList();
            this.field_175007_a = ListedRenderChunk::new;
         } else if (!var1 && this.field_175005_X) {
            this.field_174996_N = new VboRenderList();
            this.field_175007_a = RenderChunk::new;
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
      this.func_174979_m();
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
         TileEntityRendererDispatcher.field_147556_a.func_190056_a(this.field_72769_h, this.field_72777_q.func_110434_K(), this.field_72777_q.field_71466_p, this.field_72777_q.func_175606_aa(), this.field_72777_q.field_71476_x, var3);
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
         this.field_72748_H = this.field_72769_h.func_212419_R();

         for(int var17 = 0; var17 < this.field_72769_h.field_73007_j.size(); ++var17) {
            Entity var18 = (Entity)this.field_72769_h.field_73007_j.get(var17);
            ++this.field_72749_I;
            if (var18.func_145770_h(var4, var6, var8)) {
               this.field_175010_j.func_188388_a(var18, var3, false);
            }
         }

         this.field_72769_h.field_72984_F.func_76318_c("entities");
         ArrayList var41 = Lists.newArrayList();
         ArrayList var42 = Lists.newArrayList();
         BlockPos.PooledMutableBlockPos var19 = BlockPos.PooledMutableBlockPos.func_185346_s();
         Throwable var20 = null;

         try {
            Iterator var21 = this.field_72755_R.iterator();

            label437:
            while(true) {
               ClassInheritanceMultiMap var24;
               do {
                  if (!var21.hasNext()) {
                     break label437;
                  }

                  WorldRenderer.ContainerLocalRenderInformation var22 = (WorldRenderer.ContainerLocalRenderInformation)var21.next();
                  Chunk var23 = this.field_72769_h.func_175726_f(var22.field_178036_a.func_178568_j());
                  var24 = var23.func_177429_s()[var22.field_178036_a.func_178568_j().func_177956_o() / 16];
               } while(var24.isEmpty());

               Iterator var25 = var24.iterator();

               while(true) {
                  Entity var26;
                  boolean var28;
                  do {
                     do {
                        boolean var27;
                        do {
                           if (!var25.hasNext()) {
                              continue label437;
                           }

                           var26 = (Entity)var25.next();
                           var27 = this.field_175010_j.func_178635_a(var26, var2, var4, var6, var8) || var26.func_184215_y(this.field_72777_q.field_71439_g);
                        } while(!var27);

                        var28 = this.field_72777_q.func_175606_aa() instanceof EntityLivingBase && ((EntityLivingBase)this.field_72777_q.func_175606_aa()).func_70608_bn();
                     } while(var26 == this.field_72777_q.func_175606_aa() && this.field_72777_q.field_71474_y.field_74320_O == 0 && !var28);
                  } while(var26.field_70163_u >= 0.0D && var26.field_70163_u < 256.0D && !this.field_72769_h.func_175667_e(var19.func_189535_a(var26)));

                  ++this.field_72749_I;
                  this.field_175010_j.func_188388_a(var26, var3, false);
                  if (this.func_184383_a(var26, var10, var2)) {
                     var41.add(var26);
                  }

                  if (this.field_175010_j.func_188390_b(var26)) {
                     var42.add(var26);
                  }
               }
            }
         } catch (Throwable var39) {
            var20 = var39;
            throw var39;
         } finally {
            if (var19 != null) {
               if (var20 != null) {
                  try {
                     var19.close();
                  } catch (Throwable var37) {
                     var20.addSuppressed(var37);
                  }
               } else {
                  var19.close();
               }
            }

         }

         Iterator var43;
         if (!var42.isEmpty()) {
            var43 = var42.iterator();

            while(var43.hasNext()) {
               Entity var45 = (Entity)var43.next();
               this.field_175010_j.func_188389_a(var45, var3);
            }
         }

         if (this.func_174985_d() && (!var41.isEmpty() || this.field_184386_ad)) {
            this.field_72769_h.field_72984_F.func_76318_c("entityOutlines");
            this.field_175015_z.func_147614_f();
            this.field_184386_ad = !var41.isEmpty();
            if (!var41.isEmpty()) {
               GlStateManager.func_179143_c(519);
               GlStateManager.func_179106_n();
               this.field_175015_z.func_147610_a(false);
               RenderHelper.func_74518_a();
               this.field_175010_j.func_178632_c(true);

               for(int var44 = 0; var44 < var41.size(); ++var44) {
                  this.field_175010_j.func_188388_a((Entity)var41.get(var44), var3, false);
               }

               this.field_175010_j.func_178632_c(false);
               RenderHelper.func_74519_b();
               GlStateManager.func_179132_a(false);
               this.field_174991_A.func_148018_a(var3);
               GlStateManager.func_179145_e();
               GlStateManager.func_179132_a(true);
               GlStateManager.func_179127_m();
               GlStateManager.func_179147_l();
               GlStateManager.func_179142_g();
               GlStateManager.func_179143_c(515);
               GlStateManager.func_179126_j();
               GlStateManager.func_179141_d();
            }

            this.field_72777_q.func_147110_a().func_147610_a(false);
         }

         this.field_72769_h.field_72984_F.func_76318_c("blockentities");
         RenderHelper.func_74519_b();
         var43 = this.field_72755_R.iterator();

         while(true) {
            List var47;
            TileEntity var54;
            do {
               if (!var43.hasNext()) {
                  synchronized(this.field_181024_n) {
                     Iterator var48 = this.field_181024_n.iterator();

                     while(true) {
                        if (!var48.hasNext()) {
                           break;
                        }

                        TileEntity var49 = (TileEntity)var48.next();
                        TileEntityRendererDispatcher.field_147556_a.func_180546_a(var49, var3, -1);
                     }
                  }

                  this.func_180443_s();
                  var43 = this.field_72738_E.values().iterator();

                  while(var43.hasNext()) {
                     DestroyBlockProgress var50 = (DestroyBlockProgress)var43.next();
                     BlockPos var51 = var50.func_180246_b();
                     IBlockState var53 = this.field_72769_h.func_180495_p(var51);
                     if (var53.func_177230_c().func_149716_u()) {
                        var54 = this.field_72769_h.func_175625_s(var51);
                        if (var54 instanceof TileEntityChest && var53.func_177229_b(BlockChest.field_196314_b) == ChestType.LEFT) {
                           var51 = var51.func_177972_a(((EnumFacing)var53.func_177229_b(BlockChest.field_176459_a)).func_176746_e());
                           var54 = this.field_72769_h.func_175625_s(var51);
                        }

                        if (var54 != null && var53.func_191057_i()) {
                           TileEntityRendererDispatcher.field_147556_a.func_180546_a(var54, var3, var50.func_73106_e());
                        }
                     }
                  }

                  this.func_174969_t();
                  this.field_72777_q.field_71460_t.func_175072_h();
                  this.field_72777_q.field_71424_I.func_76319_b();
                  return;
               }

               WorldRenderer.ContainerLocalRenderInformation var46 = (WorldRenderer.ContainerLocalRenderInformation)var43.next();
               var47 = var46.field_178036_a.func_178571_g().func_178485_b();
            } while(var47.isEmpty());

            Iterator var52 = var47.iterator();

            while(var52.hasNext()) {
               var54 = (TileEntity)var52.next();
               TileEntityRendererDispatcher.field_147556_a.func_180546_a(var54, var3, -1);
            }
         }
      }
   }

   private boolean func_184383_a(Entity var1, Entity var2, ICamera var3) {
      boolean var4 = var2 instanceof EntityLivingBase && ((EntityLivingBase)var2).func_70608_bn();
      if (var1 == var2 && this.field_72777_q.field_71474_y.field_74320_O == 0 && !var4) {
         return false;
      } else if (var1.func_184202_aL()) {
         return true;
      } else if (this.field_72777_q.field_71439_g.func_175149_v() && this.field_72777_q.field_71474_y.field_178883_an.func_151470_d() && var1 instanceof EntityPlayer) {
         return var1.field_70158_ak || var3.func_78546_a(var1.func_174813_aQ()) || var1.func_184215_y(this.field_72777_q.field_71439_g);
      } else {
         return false;
      }
   }

   public String func_72735_c() {
      int var1 = this.field_175008_n.field_178164_f.length;
      int var2 = this.func_184382_g();
      return String.format("C: %d/%d %sD: %d, L: %d, %s", var2, var1, this.field_72777_q.field_175612_E ? "(s) " : "", this.field_72739_F, this.field_184387_ae.size(), this.field_174995_M == null ? "null" : this.field_174995_M.func_178504_a());
   }

   protected int func_184382_g() {
      int var1 = 0;
      Iterator var2 = this.field_72755_R.iterator();

      while(var2.hasNext()) {
         WorldRenderer.ContainerLocalRenderInformation var3 = (WorldRenderer.ContainerLocalRenderInformation)var2.next();
         CompiledChunk var4 = var3.field_178036_a.field_178590_b;
         if (var4 != CompiledChunk.field_178502_a && !var4.func_178489_a()) {
            ++var1;
         }
      }

      return var1;
   }

   public String func_72723_d() {
      return "E: " + this.field_72749_I + "/" + this.field_72748_H + ", B: " + this.field_72750_J;
   }

   public void func_195473_a(Entity var1, float var2, ICamera var3, int var4, boolean var5) {
      if (this.field_72777_q.field_71474_y.field_151451_c != this.field_72739_F) {
         this.func_72712_a();
      }

      this.field_72769_h.field_72984_F.func_76320_a("camera");
      double var6 = var1.field_70165_t - this.field_174992_B;
      double var8 = var1.field_70163_u - this.field_174993_C;
      double var10 = var1.field_70161_v - this.field_174987_D;
      if (this.field_174988_E != var1.field_70176_ah || this.field_174989_F != var1.field_70162_ai || this.field_174990_G != var1.field_70164_aj || var6 * var6 + var8 * var8 + var10 * var10 > 16.0D) {
         this.field_174992_B = var1.field_70165_t;
         this.field_174993_C = var1.field_70163_u;
         this.field_174987_D = var1.field_70161_v;
         this.field_174988_E = var1.field_70176_ah;
         this.field_174989_F = var1.field_70162_ai;
         this.field_174990_G = var1.field_70164_aj;
         this.field_175008_n.func_178163_a(var1.field_70165_t, var1.field_70161_v);
      }

      this.field_72769_h.field_72984_F.func_76318_c("renderlistcamera");
      double var12 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
      double var14 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
      double var16 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
      this.field_174996_N.func_178004_a(var12, var14, var16);
      this.field_72769_h.field_72984_F.func_76318_c("cull");
      if (this.field_175001_U != null) {
         Frustum var18 = new Frustum(this.field_175001_U);
         var18.func_78547_a(this.field_175003_W.field_181059_a, this.field_175003_W.field_181060_b, this.field_175003_W.field_181061_c);
         var3 = var18;
      }

      this.field_72777_q.field_71424_I.func_76318_c("culling");
      BlockPos var35 = new BlockPos(var12, var14 + (double)var1.func_70047_e(), var16);
      RenderChunk var19 = this.field_175008_n.func_178161_a(var35);
      BlockPos var20 = new BlockPos(MathHelper.func_76128_c(var12 / 16.0D) * 16, MathHelper.func_76128_c(var14 / 16.0D) * 16, MathHelper.func_76128_c(var16 / 16.0D) * 16);
      float var21 = var1.func_195050_f(var2);
      float var22 = var1.func_195046_g(var2);
      this.field_147595_R = this.field_147595_R || !this.field_175009_l.isEmpty() || var1.field_70165_t != this.field_174997_H || var1.field_70163_u != this.field_174998_I || var1.field_70161_v != this.field_174999_J || (double)var21 != this.field_175000_K || (double)var22 != this.field_174994_L;
      this.field_174997_H = var1.field_70165_t;
      this.field_174998_I = var1.field_70163_u;
      this.field_174999_J = var1.field_70161_v;
      this.field_175000_K = (double)var21;
      this.field_174994_L = (double)var22;
      boolean var23 = this.field_175001_U != null;
      this.field_72777_q.field_71424_I.func_76318_c("update");
      WorldRenderer.ContainerLocalRenderInformation var39;
      RenderChunk var41;
      if (!var23 && this.field_147595_R) {
         this.field_147595_R = false;
         this.field_72755_R = Lists.newArrayList();
         ArrayDeque var24 = Queues.newArrayDeque();
         Entity.func_184227_b(MathHelper.func_151237_a((double)this.field_72777_q.field_71474_y.field_151451_c / 8.0D, 1.0D, 2.5D));
         boolean var25 = this.field_72777_q.field_175612_E;
         if (var19 != null) {
            boolean var38 = false;
            WorldRenderer.ContainerLocalRenderInformation var40 = new WorldRenderer.ContainerLocalRenderInformation(var19, (EnumFacing)null, 0);
            Set var42 = this.func_174978_c(var35);
            if (var42.size() == 1) {
               Vector3f var44 = this.func_195474_a(var1, (double)var2);
               EnumFacing var30 = EnumFacing.func_176737_a(var44.func_195899_a(), var44.func_195900_b(), var44.func_195902_c()).func_176734_d();
               var42.remove(var30);
            }

            if (var42.isEmpty()) {
               var38 = true;
            }

            if (var38 && !var5) {
               this.field_72755_R.add(var40);
            } else {
               if (var5 && this.field_72769_h.func_180495_p(var35).func_200015_d(this.field_72769_h, var35)) {
                  var25 = false;
               }

               var19.func_178577_a(var4);
               var24.add(var40);
            }
         } else {
            int var26 = var35.func_177956_o() > 0 ? 248 : 8;

            for(int var27 = -this.field_72739_F; var27 <= this.field_72739_F; ++var27) {
               for(int var28 = -this.field_72739_F; var28 <= this.field_72739_F; ++var28) {
                  RenderChunk var29 = this.field_175008_n.func_178161_a(new BlockPos((var27 << 4) + 8, var26, (var28 << 4) + 8));
                  if (var29 != null && ((ICamera)var3).func_78546_a(var29.field_178591_c)) {
                     var29.func_178577_a(var4);
                     var24.add(new WorldRenderer.ContainerLocalRenderInformation(var29, (EnumFacing)null, 0));
                  }
               }
            }
         }

         this.field_72777_q.field_71424_I.func_76320_a("iteration");

         while(!var24.isEmpty()) {
            var39 = (WorldRenderer.ContainerLocalRenderInformation)var24.poll();
            var41 = var39.field_178036_a;
            EnumFacing var43 = var39.field_178034_b;
            this.field_72755_R.add(var39);
            EnumFacing[] var46 = field_200006_a;
            int var48 = var46.length;

            for(int var31 = 0; var31 < var48; ++var31) {
               EnumFacing var32 = var46[var31];
               RenderChunk var33 = this.func_181562_a(var20, var41, var32);
               if ((!var25 || !var39.func_189560_a(var32.func_176734_d())) && (!var25 || var43 == null || var41.func_178571_g().func_178495_a(var43.func_176734_d(), var32)) && var33 != null && var33.func_178577_a(var4) && ((ICamera)var3).func_78546_a(var33.field_178591_c)) {
                  WorldRenderer.ContainerLocalRenderInformation var34 = new WorldRenderer.ContainerLocalRenderInformation(var33, var32, var39.field_178032_d + 1);
                  var34.func_189561_a(var39.field_178035_c, var32);
                  var24.add(var34);
               }
            }
         }

         this.field_72777_q.field_71424_I.func_76319_b();
      }

      this.field_72777_q.field_71424_I.func_76318_c("captureFrustum");
      if (this.field_175002_T) {
         this.func_174984_a(var12, var14, var16);
         this.field_175002_T = false;
      }

      this.field_72777_q.field_71424_I.func_76318_c("rebuildNear");
      Set var36 = this.field_175009_l;
      this.field_175009_l = Sets.newLinkedHashSet();
      Iterator var37 = this.field_72755_R.iterator();

      while(true) {
         while(true) {
            do {
               if (!var37.hasNext()) {
                  this.field_175009_l.addAll(var36);
                  this.field_72777_q.field_71424_I.func_76319_b();
                  return;
               }

               var39 = (WorldRenderer.ContainerLocalRenderInformation)var37.next();
               var41 = var39.field_178036_a;
            } while(!var41.func_178569_m() && !var36.contains(var41));

            this.field_147595_R = true;
            BlockPos var45 = var41.func_178568_j().func_177982_a(8, 8, 8);
            boolean var47 = var45.func_177951_i(var35) < 768.0D;
            if (!var41.func_188281_o() && !var47) {
               this.field_175009_l.add(var41);
            } else {
               this.field_72777_q.field_71424_I.func_76320_a("build near");
               this.field_174995_M.func_178505_b(var41);
               var41.func_188282_m();
               this.field_72777_q.field_71424_I.func_76319_b();
            }
         }
      }
   }

   private Set<EnumFacing> func_174978_c(BlockPos var1) {
      VisGraph var2 = new VisGraph();
      BlockPos var3 = new BlockPos(var1.func_177958_n() >> 4 << 4, var1.func_177956_o() >> 4 << 4, var1.func_177952_p() >> 4 << 4);
      Chunk var4 = this.field_72769_h.func_175726_f(var3);
      Iterator var5 = BlockPos.func_177975_b(var3, var3.func_177982_a(15, 15, 15)).iterator();

      while(var5.hasNext()) {
         BlockPos.MutableBlockPos var6 = (BlockPos.MutableBlockPos)var5.next();
         if (var4.func_180495_p(var6).func_200015_d(this.field_72769_h, var6)) {
            var2.func_178606_a(var6);
         }
      }

      return var2.func_178609_b(var1);
   }

   @Nullable
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
   }

   protected Vector3f func_195474_a(Entity var1, double var2) {
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

   public int func_195464_a(BlockRenderLayer var1, double var2, Entity var4) {
      RenderHelper.func_74518_a();
      if (var1 == BlockRenderLayer.TRANSLUCENT) {
         this.field_72777_q.field_71424_I.func_76320_a("translucent_sort");
         double var5 = var4.field_70165_t - this.field_147596_f;
         double var7 = var4.field_70163_u - this.field_147597_g;
         double var9 = var4.field_70161_v - this.field_147602_h;
         if (var5 * var5 + var7 * var7 + var9 * var9 > 1.0D) {
            this.field_147596_f = var4.field_70165_t;
            this.field_147597_g = var4.field_70163_u;
            this.field_147602_h = var4.field_70161_v;
            int var11 = 0;
            Iterator var12 = this.field_72755_R.iterator();

            while(var12.hasNext()) {
               WorldRenderer.ContainerLocalRenderInformation var13 = (WorldRenderer.ContainerLocalRenderInformation)var12.next();
               if (var13.field_178036_a.field_178590_b.func_178492_d(var1) && var11++ < 15) {
                  this.field_174995_M.func_178509_c(var13.field_178036_a);
               }
            }
         }

         this.field_72777_q.field_71424_I.func_76319_b();
      }

      this.field_72777_q.field_71424_I.func_76320_a("filterempty");
      int var14 = 0;
      boolean var6 = var1 == BlockRenderLayer.TRANSLUCENT;
      int var15 = var6 ? this.field_72755_R.size() - 1 : 0;
      int var8 = var6 ? -1 : this.field_72755_R.size();
      int var16 = var6 ? -1 : 1;

      for(int var10 = var15; var10 != var8; var10 += var16) {
         RenderChunk var17 = ((WorldRenderer.ContainerLocalRenderInformation)this.field_72755_R.get(var10)).field_178036_a;
         if (!var17.func_178571_g().func_178491_b(var1)) {
            ++var14;
            this.field_174996_N.func_178002_a(var17, var1);
         }
      }

      this.field_72777_q.field_71424_I.func_194339_b(() -> {
         return "render_" + var1;
      });
      this.func_174982_a(var1);
      this.field_72777_q.field_71424_I.func_76319_b();
      return var14;
   }

   private void func_174982_a(BlockRenderLayer var1) {
      this.field_72777_q.field_71460_t.func_180436_i();
      if (OpenGlHelper.func_176075_f()) {
         GlStateManager.func_187410_q(32884);
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
         GlStateManager.func_187410_q(32888);
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77476_b);
         GlStateManager.func_187410_q(32888);
         OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
         GlStateManager.func_187410_q(32886);
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
               GlStateManager.func_187429_p(32884);
               break;
            case UV:
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a + var6);
               GlStateManager.func_187429_p(32888);
               OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
               break;
            case COLOR:
               GlStateManager.func_187429_p(32886);
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

      if (!this.field_184387_ae.isEmpty() && !this.field_174995_M.func_188248_h() && this.field_175009_l.isEmpty()) {
         Iterator var1 = this.field_184387_ae.iterator();

         while(var1.hasNext()) {
            BlockPos var2 = (BlockPos)var1.next();
            var1.remove();
            int var3 = var2.func_177958_n();
            int var4 = var2.func_177956_o();
            int var5 = var2.func_177952_p();
            this.func_184385_a(var3 - 1, var4 - 1, var5 - 1, var3 + 1, var4 + 1, var5 + 1, false);
         }
      }

   }

   private void func_180448_r() {
      GlStateManager.func_179106_n();
      GlStateManager.func_179118_c();
      GlStateManager.func_179147_l();
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      RenderHelper.func_74518_a();
      GlStateManager.func_179132_a(false);
      this.field_72770_i.func_110577_a(field_110926_k);
      Tessellator var1 = Tessellator.func_178181_a();
      BufferBuilder var2 = var1.func_178180_c();

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
         var2.func_181662_b(-100.0D, -100.0D, -100.0D).func_187315_a(0.0D, 0.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var2.func_181662_b(-100.0D, -100.0D, 100.0D).func_187315_a(0.0D, 16.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var2.func_181662_b(100.0D, -100.0D, 100.0D).func_187315_a(16.0D, 16.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var2.func_181662_b(100.0D, -100.0D, -100.0D).func_187315_a(16.0D, 0.0D).func_181669_b(40, 40, 40, 255).func_181675_d();
         var1.func_78381_a();
         GlStateManager.func_179121_F();
      }

      GlStateManager.func_179132_a(true);
      GlStateManager.func_179098_w();
      GlStateManager.func_179141_d();
   }

   public void func_195465_a(float var1) {
      if (this.field_72777_q.field_71441_e.field_73011_w.func_186058_p() == DimensionType.THE_END) {
         this.func_180448_r();
      } else if (this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         GlStateManager.func_179090_x();
         Vec3d var2 = this.field_72769_h.func_72833_a(this.field_72777_q.func_175606_aa(), var1);
         float var3 = (float)var2.field_72450_a;
         float var4 = (float)var2.field_72448_b;
         float var5 = (float)var2.field_72449_c;
         GlStateManager.func_179124_c(var3, var4, var5);
         Tessellator var6 = Tessellator.func_178181_a();
         BufferBuilder var7 = var6.func_178180_c();
         GlStateManager.func_179132_a(false);
         GlStateManager.func_179127_m();
         GlStateManager.func_179124_c(var3, var4, var5);
         if (this.field_175005_X) {
            this.field_175012_t.func_177359_a();
            GlStateManager.func_187410_q(32884);
            GlStateManager.func_187420_d(3, 5126, 12, 0);
            this.field_175012_t.func_177358_a(7);
            this.field_175012_t.func_177361_b();
            GlStateManager.func_187429_p(32884);
         } else {
            GlStateManager.func_179148_o(this.field_72771_w);
         }

         GlStateManager.func_179106_n();
         GlStateManager.func_179118_c();
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         RenderHelper.func_74518_a();
         float[] var8 = this.field_72769_h.field_73011_w.func_76560_a(this.field_72769_h.func_72826_c(var1), var1);
         float var9;
         float var10;
         int var13;
         float var14;
         float var15;
         float var16;
         if (var8 != null) {
            GlStateManager.func_179090_x();
            GlStateManager.func_179103_j(7425);
            GlStateManager.func_179094_E();
            GlStateManager.func_179114_b(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.func_179114_b(MathHelper.func_76126_a(this.field_72769_h.func_72929_e(var1)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.func_179114_b(90.0F, 0.0F, 0.0F, 1.0F);
            var9 = var8[0];
            var10 = var8[1];
            float var11 = var8[2];
            var7.func_181668_a(6, DefaultVertexFormats.field_181706_f);
            var7.func_181662_b(0.0D, 100.0D, 0.0D).func_181666_a(var9, var10, var11, var8[3]).func_181675_d();
            boolean var12 = true;

            for(var13 = 0; var13 <= 16; ++var13) {
               var14 = (float)var13 * 6.2831855F / 16.0F;
               var15 = MathHelper.func_76126_a(var14);
               var16 = MathHelper.func_76134_b(var14);
               var7.func_181662_b((double)(var15 * 120.0F), (double)(var16 * 120.0F), (double)(-var16 * 40.0F * var8[3])).func_181666_a(var8[0], var8[1], var8[2], 0.0F).func_181675_d();
            }

            var6.func_78381_a();
            GlStateManager.func_179121_F();
            GlStateManager.func_179103_j(7424);
         }

         GlStateManager.func_179098_w();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.func_179094_E();
         var9 = 1.0F - this.field_72769_h.func_72867_j(var1);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, var9);
         GlStateManager.func_179114_b(-90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.func_179114_b(this.field_72769_h.func_72826_c(var1) * 360.0F, 1.0F, 0.0F, 0.0F);
         var10 = 30.0F;
         this.field_72770_i.func_110577_a(field_110928_i);
         var7.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var7.func_181662_b((double)(-var10), 100.0D, (double)(-var10)).func_187315_a(0.0D, 0.0D).func_181675_d();
         var7.func_181662_b((double)var10, 100.0D, (double)(-var10)).func_187315_a(1.0D, 0.0D).func_181675_d();
         var7.func_181662_b((double)var10, 100.0D, (double)var10).func_187315_a(1.0D, 1.0D).func_181675_d();
         var7.func_181662_b((double)(-var10), 100.0D, (double)var10).func_187315_a(0.0D, 1.0D).func_181675_d();
         var6.func_78381_a();
         var10 = 20.0F;
         this.field_72770_i.func_110577_a(field_110927_h);
         int var20 = this.field_72769_h.func_72853_d();
         int var21 = var20 % 4;
         var13 = var20 / 4 % 2;
         var14 = (float)(var21 + 0) / 4.0F;
         var15 = (float)(var13 + 0) / 2.0F;
         var16 = (float)(var21 + 1) / 4.0F;
         float var17 = (float)(var13 + 1) / 2.0F;
         var7.func_181668_a(7, DefaultVertexFormats.field_181707_g);
         var7.func_181662_b((double)(-var10), -100.0D, (double)var10).func_187315_a((double)var16, (double)var17).func_181675_d();
         var7.func_181662_b((double)var10, -100.0D, (double)var10).func_187315_a((double)var14, (double)var17).func_181675_d();
         var7.func_181662_b((double)var10, -100.0D, (double)(-var10)).func_187315_a((double)var14, (double)var15).func_181675_d();
         var7.func_181662_b((double)(-var10), -100.0D, (double)(-var10)).func_187315_a((double)var16, (double)var15).func_181675_d();
         var6.func_78381_a();
         GlStateManager.func_179090_x();
         float var18 = this.field_72769_h.func_72880_h(var1) * var9;
         if (var18 > 0.0F) {
            GlStateManager.func_179131_c(var18, var18, var18, var18);
            if (this.field_175005_X) {
               this.field_175013_s.func_177359_a();
               GlStateManager.func_187410_q(32884);
               GlStateManager.func_187420_d(3, 5126, 12, 0);
               this.field_175013_s.func_177358_a(7);
               this.field_175013_s.func_177361_b();
               GlStateManager.func_187429_p(32884);
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
         double var19 = this.field_72777_q.field_71439_g.func_174824_e(var1).field_72448_b - this.field_72769_h.func_72919_O();
         if (var19 < 0.0D) {
            GlStateManager.func_179094_E();
            GlStateManager.func_179109_b(0.0F, 12.0F, 0.0F);
            if (this.field_175005_X) {
               this.field_175011_u.func_177359_a();
               GlStateManager.func_187410_q(32884);
               GlStateManager.func_187420_d(3, 5126, 12, 0);
               this.field_175011_u.func_177358_a(7);
               this.field_175011_u.func_177361_b();
               GlStateManager.func_187429_p(32884);
            } else {
               GlStateManager.func_179148_o(this.field_72781_x);
            }

            GlStateManager.func_179121_F();
         }

         if (this.field_72769_h.field_73011_w.func_76561_g()) {
            GlStateManager.func_179124_c(var3 * 0.2F + 0.04F, var4 * 0.2F + 0.04F, var5 * 0.6F + 0.1F);
         } else {
            GlStateManager.func_179124_c(var3, var4, var5);
         }

         GlStateManager.func_179094_E();
         GlStateManager.func_179109_b(0.0F, -((float)(var19 - 16.0D)), 0.0F);
         GlStateManager.func_179148_o(this.field_72781_x);
         GlStateManager.func_179121_F();
         GlStateManager.func_179098_w();
         GlStateManager.func_179132_a(true);
      }
   }

   public void func_195466_a(float var1, double var2, double var4, double var6) {
      if (this.field_72777_q.field_71441_e.field_73011_w.func_76569_d()) {
         float var8 = 12.0F;
         float var9 = 4.0F;
         double var10 = 2.0E-4D;
         double var12 = (double)(((float)this.field_72773_u + var1) * 0.03F);
         double var14 = (var2 + var12) / 12.0D;
         double var16 = (double)(this.field_72769_h.field_73011_w.func_76571_f() - (float)var4 + 0.33F);
         double var18 = var6 / 12.0D + 0.33000001311302185D;
         var14 -= (double)(MathHelper.func_76128_c(var14 / 2048.0D) * 2048);
         var18 -= (double)(MathHelper.func_76128_c(var18 / 2048.0D) * 2048);
         float var20 = (float)(var14 - (double)MathHelper.func_76128_c(var14));
         float var21 = (float)(var16 / 4.0D - (double)MathHelper.func_76128_c(var16 / 4.0D)) * 4.0F;
         float var22 = (float)(var18 - (double)MathHelper.func_76128_c(var18));
         Vec3d var23 = this.field_72769_h.func_72824_f(var1);
         int var24 = (int)Math.floor(var14);
         int var25 = (int)Math.floor(var16 / 4.0D);
         int var26 = (int)Math.floor(var18);
         if (var24 != this.field_204602_S || var25 != this.field_204603_T || var26 != this.field_204604_U || this.field_72777_q.field_71474_y.func_181147_e() != this.field_204800_W || this.field_204605_V.func_72436_e(var23) > 2.0E-4D) {
            this.field_204602_S = var24;
            this.field_204603_T = var25;
            this.field_204604_U = var26;
            this.field_204605_V = var23;
            this.field_204800_W = this.field_72777_q.field_71474_y.func_181147_e();
            this.field_204607_y = true;
         }

         if (this.field_204607_y) {
            this.field_204607_y = false;
            Tessellator var27 = Tessellator.func_178181_a();
            BufferBuilder var28 = var27.func_178180_c();
            if (this.field_204601_A != null) {
               this.field_204601_A.func_177362_c();
            }

            if (this.field_204608_z >= 0) {
               GLAllocation.func_74523_b(this.field_204608_z);
               this.field_204608_z = -1;
            }

            if (this.field_175005_X) {
               this.field_204601_A = new VertexBuffer(DefaultVertexFormats.field_181712_l);
               this.func_204600_a(var28, var14, var16, var18, var23);
               var28.func_178977_d();
               var28.func_178965_a();
               this.field_204601_A.func_181722_a(var28.func_178966_f());
            } else {
               this.field_204608_z = GLAllocation.func_74526_a(1);
               GlStateManager.func_187423_f(this.field_204608_z, 4864);
               this.func_204600_a(var28, var14, var16, var18, var23);
               var27.func_78381_a();
               GlStateManager.func_187415_K();
            }
         }

         GlStateManager.func_179129_p();
         this.field_72770_i.func_110577_a(field_110925_j);
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
         GlStateManager.func_179094_E();
         GlStateManager.func_179152_a(12.0F, 1.0F, 12.0F);
         GlStateManager.func_179109_b(-var20, var21, -var22);
         int var29;
         int var30;
         if (this.field_175005_X && this.field_204601_A != null) {
            this.field_204601_A.func_177359_a();
            GlStateManager.func_187410_q(32884);
            GlStateManager.func_187410_q(32888);
            OpenGlHelper.func_77472_b(OpenGlHelper.field_77478_a);
            GlStateManager.func_187410_q(32886);
            GlStateManager.func_187410_q(32885);
            GlStateManager.func_187420_d(3, 5126, 28, 0);
            GlStateManager.func_187405_c(2, 5126, 28, 12);
            GlStateManager.func_187406_e(4, 5121, 28, 20);
            GlStateManager.func_204611_f(5120, 28, 24);
            var29 = this.field_204800_W == 2 ? 0 : 1;

            for(var30 = var29; var30 < 2; ++var30) {
               if (var30 == 0) {
                  GlStateManager.func_179135_a(false, false, false, false);
               } else {
                  GlStateManager.func_179135_a(true, true, true, true);
               }

               this.field_204601_A.func_177358_a(7);
            }

            this.field_204601_A.func_177361_b();
            GlStateManager.func_187429_p(32884);
            GlStateManager.func_187429_p(32888);
            GlStateManager.func_187429_p(32886);
            GlStateManager.func_187429_p(32885);
            OpenGlHelper.func_176072_g(OpenGlHelper.field_176089_P, 0);
         } else if (this.field_204608_z >= 0) {
            var29 = this.field_204800_W == 2 ? 0 : 1;

            for(var30 = var29; var30 < 2; ++var30) {
               if (var30 == 0) {
                  GlStateManager.func_179135_a(false, false, false, false);
               } else {
                  GlStateManager.func_179135_a(true, true, true, true);
               }

               GlStateManager.func_179148_o(this.field_204608_z);
            }
         }

         GlStateManager.func_179121_F();
         GlStateManager.func_179117_G();
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.func_179084_k();
         GlStateManager.func_179089_o();
      }
   }

   private void func_204600_a(BufferBuilder var1, double var2, double var4, double var6, Vec3d var8) {
      float var9 = 4.0F;
      float var10 = 0.00390625F;
      boolean var11 = true;
      boolean var12 = true;
      float var13 = 9.765625E-4F;
      float var14 = (float)MathHelper.func_76128_c(var2) * 0.00390625F;
      float var15 = (float)MathHelper.func_76128_c(var6) * 0.00390625F;
      float var16 = (float)var8.field_72450_a;
      float var17 = (float)var8.field_72448_b;
      float var18 = (float)var8.field_72449_c;
      float var19 = var16 * 0.9F;
      float var20 = var17 * 0.9F;
      float var21 = var18 * 0.9F;
      float var22 = var16 * 0.7F;
      float var23 = var17 * 0.7F;
      float var24 = var18 * 0.7F;
      float var25 = var16 * 0.8F;
      float var26 = var17 * 0.8F;
      float var27 = var18 * 0.8F;
      var1.func_181668_a(7, DefaultVertexFormats.field_181712_l);
      float var28 = (float)Math.floor(var4 / 4.0D) * 4.0F;
      if (this.field_204800_W == 2) {
         for(int var29 = -3; var29 <= 4; ++var29) {
            for(int var30 = -3; var30 <= 4; ++var30) {
               float var31 = (float)(var29 * 8);
               float var32 = (float)(var30 * 8);
               if (var28 > -5.0F) {
                  var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var22, var23, var24, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var22, var23, var24, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var22, var23, var24, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
                  var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var22, var23, var24, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
               }

               if (var28 <= 5.0F) {
                  var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
                  var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 4.0F - 9.765625E-4F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, 1.0F, 0.0F).func_181675_d();
               }

               int var33;
               if (var29 > -1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.func_181662_b((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + (float)var33 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(-1.0F, 0.0F, 0.0F).func_181675_d();
                  }
               }

               if (var29 <= 1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.func_181662_b((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 8.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 8.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 4.0F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + (float)var33 + 1.0F - 9.765625E-4F), (double)(var28 + 0.0F), (double)(var32 + 0.0F)).func_187315_a((double)((var31 + (float)var33 + 0.5F) * 0.00390625F + var14), (double)((var32 + 0.0F) * 0.00390625F + var15)).func_181666_a(var19, var20, var21, 0.8F).func_181663_c(1.0F, 0.0F, 0.0F).func_181675_d();
                  }
               }

               if (var30 > -1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 0.0F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 0.0F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 0.0F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 0.0F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, -1.0F).func_181675_d();
                  }
               }

               if (var30 <= 1) {
                  for(var33 = 0; var33 < 8; ++var33) {
                     var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 4.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + 8.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).func_187315_a((double)((var31 + 8.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                     var1.func_181662_b((double)(var31 + 0.0F), (double)(var28 + 0.0F), (double)(var32 + (float)var33 + 1.0F - 9.765625E-4F)).func_187315_a((double)((var31 + 0.0F) * 0.00390625F + var14), (double)((var32 + (float)var33 + 0.5F) * 0.00390625F + var15)).func_181666_a(var25, var26, var27, 0.8F).func_181663_c(0.0F, 0.0F, 1.0F).func_181675_d();
                  }
               }
            }
         }
      } else {
         boolean var34 = true;
         boolean var35 = true;

         for(int var36 = -32; var36 < 32; var36 += 32) {
            for(int var37 = -32; var37 < 32; var37 += 32) {
               var1.func_181662_b((double)(var36 + 0), (double)var28, (double)(var37 + 32)).func_187315_a((double)((float)(var36 + 0) * 0.00390625F + var14), (double)((float)(var37 + 32) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
               var1.func_181662_b((double)(var36 + 32), (double)var28, (double)(var37 + 32)).func_187315_a((double)((float)(var36 + 32) * 0.00390625F + var14), (double)((float)(var37 + 32) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
               var1.func_181662_b((double)(var36 + 32), (double)var28, (double)(var37 + 0)).func_187315_a((double)((float)(var36 + 32) * 0.00390625F + var14), (double)((float)(var37 + 0) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
               var1.func_181662_b((double)(var36 + 0), (double)var28, (double)(var37 + 0)).func_187315_a((double)((float)(var36 + 0) * 0.00390625F + var14), (double)((float)(var37 + 0) * 0.00390625F + var15)).func_181666_a(var16, var17, var18, 0.8F).func_181663_c(0.0F, -1.0F, 0.0F).func_181675_d();
            }
         }
      }

   }

   public void func_174967_a(long var1) {
      this.field_147595_R |= this.field_174995_M.func_178516_a(var1);
      if (!this.field_175009_l.isEmpty()) {
         Iterator var3 = this.field_175009_l.iterator();

         while(var3.hasNext()) {
            RenderChunk var4 = (RenderChunk)var3.next();
            boolean var5;
            if (var4.func_188281_o()) {
               var5 = this.field_174995_M.func_178505_b(var4);
            } else {
               var5 = this.field_174995_M.func_178507_a(var4);
            }

            if (!var5) {
               break;
            }

            var4.func_188282_m();
            var3.remove();
            long var6 = var1 - Util.func_211178_c();
            if (var6 < 0L) {
               break;
            }
         }
      }

   }

   public void func_180449_a(Entity var1, float var2) {
      Tessellator var3 = Tessellator.func_178181_a();
      BufferBuilder var4 = var3.func_178180_c();
      WorldBorder var5 = this.field_72769_h.func_175723_af();
      double var6 = (double)(this.field_72777_q.field_71474_y.field_151451_c * 16);
      if (var1.field_70165_t >= var5.func_177728_d() - var6 || var1.field_70165_t <= var5.func_177726_b() + var6 || var1.field_70161_v >= var5.func_177733_e() - var6 || var1.field_70161_v <= var5.func_177736_c() + var6) {
         double var8 = 1.0D - var5.func_177745_a(var1) / var6;
         var8 = Math.pow(var8, 4.0D);
         double var10 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var2;
         double var12 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var2;
         double var14 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var2;
         GlStateManager.func_179147_l();
         GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
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
         float var20 = (float)(Util.func_211177_b() % 3000L) / 3000.0F;
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
               var4.func_181662_b(var5.func_177728_d(), 256.0D, var29).func_187315_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177728_d(), 256.0D, var29 + var31).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177728_d(), 0.0D, var29 + var31).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177728_d(), 0.0D, var29).func_187315_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
               ++var29;
            }
         }

         if (var10 < var5.func_177726_b() + var6) {
            var28 = 0.0F;

            for(var29 = var24; var29 < var26; var28 += 0.5F) {
               var31 = Math.min(1.0D, var26 - var29);
               var33 = (float)var31 * 0.5F;
               var4.func_181662_b(var5.func_177726_b(), 256.0D, var29).func_187315_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177726_b(), 256.0D, var29 + var31).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177726_b(), 0.0D, var29 + var31).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var5.func_177726_b(), 0.0D, var29).func_187315_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
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
               var4.func_181662_b(var29, 256.0D, var5.func_177733_e()).func_187315_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 256.0D, var5.func_177733_e()).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 0.0D, var5.func_177733_e()).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var29, 0.0D, var5.func_177733_e()).func_187315_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
               ++var29;
            }
         }

         if (var14 < var5.func_177736_c() + var6) {
            var28 = 0.0F;

            for(var29 = var24; var29 < var26; var28 += 0.5F) {
               var31 = Math.min(1.0D, var26 - var29);
               var33 = (float)var31 * 0.5F;
               var4.func_181662_b(var29, 256.0D, var5.func_177736_c()).func_187315_a((double)(var20 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 256.0D, var5.func_177736_c()).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 0.0F)).func_181675_d();
               var4.func_181662_b(var29 + var31, 0.0D, var5.func_177736_c()).func_187315_a((double)(var20 + var33 + var28), (double)(var20 + 128.0F)).func_181675_d();
               var4.func_181662_b(var29, 0.0D, var5.func_177736_c()).func_187315_a((double)(var20 + var28), (double)(var20 + 128.0F)).func_181675_d();
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
      GlStateManager.func_187428_a(GlStateManager.SourceFactor.DST_COLOR, GlStateManager.DestFactor.SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.func_179147_l();
      GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.func_179136_a(-1.0F, -10.0F);
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

   public void func_174981_a(Tessellator var1, BufferBuilder var2, Entity var3, float var4) {
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
            Block var14 = this.field_72769_h.func_180495_p(var13).func_177230_c();
            if (!(var14 instanceof BlockChest) && !(var14 instanceof BlockEnderChest) && !(var14 instanceof BlockSign) && !(var14 instanceof BlockAbstractSkull)) {
               double var15 = (double)var13.func_177958_n() - var5;
               double var17 = (double)var13.func_177956_o() - var7;
               double var19 = (double)var13.func_177952_p() - var9;
               if (var15 * var15 + var17 * var17 + var19 * var19 > 1024.0D) {
                  var11.remove();
               } else {
                  IBlockState var21 = this.field_72769_h.func_180495_p(var13);
                  if (!var21.func_196958_f()) {
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

   public void func_72731_b(EntityPlayer var1, RayTraceResult var2, int var3, float var4) {
      if (var3 == 0 && var2.field_72313_a == RayTraceResult.Type.BLOCK) {
         BlockPos var5 = var2.func_178782_a();
         IBlockState var6 = this.field_72769_h.func_180495_p(var5);
         if (!var6.func_196958_f() && this.field_72769_h.func_175723_af().func_177746_a(var5)) {
            GlStateManager.func_179147_l();
            GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.func_187441_d(Math.max(2.5F, (float)this.field_72777_q.field_195558_d.func_198109_k() / 1920.0F * 2.5F));
            GlStateManager.func_179090_x();
            GlStateManager.func_179132_a(false);
            GlStateManager.func_179128_n(5889);
            GlStateManager.func_179094_E();
            GlStateManager.func_179152_a(1.0F, 1.0F, 0.999F);
            double var7 = var1.field_70142_S + (var1.field_70165_t - var1.field_70142_S) * (double)var4;
            double var9 = var1.field_70137_T + (var1.field_70163_u - var1.field_70137_T) * (double)var4;
            double var11 = var1.field_70136_U + (var1.field_70161_v - var1.field_70136_U) * (double)var4;
            func_195463_b(var6.func_196954_c(this.field_72769_h, var5), (double)var5.func_177958_n() - var7, (double)var5.func_177956_o() - var9, (double)var5.func_177952_p() - var11, 0.0F, 0.0F, 0.0F, 0.4F);
            GlStateManager.func_179121_F();
            GlStateManager.func_179128_n(5888);
            GlStateManager.func_179132_a(true);
            GlStateManager.func_179098_w();
            GlStateManager.func_179084_k();
         }
      }

   }

   public static void func_195470_a(VoxelShape var0, double var1, double var3, double var5, float var7, float var8, float var9, float var10) {
      List var11 = var0.func_197756_d();
      int var12 = MathHelper.func_76143_f((double)var11.size() / 3.0D);

      for(int var13 = 0; var13 < var11.size(); ++var13) {
         AxisAlignedBB var14 = (AxisAlignedBB)var11.get(var13);
         float var15 = ((float)var13 % (float)var12 + 1.0F) / (float)var12;
         float var16 = (float)(var13 / var12);
         float var17 = var15 * (float)(var16 == 0.0F ? 1 : 0);
         float var18 = var15 * (float)(var16 == 1.0F ? 1 : 0);
         float var19 = var15 * (float)(var16 == 2.0F ? 1 : 0);
         func_195463_b(VoxelShapes.func_197881_a(var14.func_72317_d(0.0D, 0.0D, 0.0D)), var1, var3, var5, var17, var18, var19, 1.0F);
      }

   }

   public static void func_195463_b(VoxelShape var0, double var1, double var3, double var5, float var7, float var8, float var9, float var10) {
      Tessellator var11 = Tessellator.func_178181_a();
      BufferBuilder var12 = var11.func_178180_c();
      var12.func_181668_a(1, DefaultVertexFormats.field_181706_f);
      var0.func_197754_a((var11x, var13, var15, var17, var19, var21) -> {
         var12.func_181662_b(var11x + var1, var13 + var3, var15 + var5).func_181666_a(var7, var8, var9, var10).func_181675_d();
         var12.func_181662_b(var17 + var1, var19 + var3, var21 + var5).func_181666_a(var7, var8, var9, var10).func_181675_d();
      });
      var11.func_78381_a();
   }

   public static void func_189697_a(AxisAlignedBB var0, float var1, float var2, float var3, float var4) {
      func_189694_a(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c, var0.field_72336_d, var0.field_72337_e, var0.field_72334_f, var1, var2, var3, var4);
   }

   public static void func_189694_a(double var0, double var2, double var4, double var6, double var8, double var10, float var12, float var13, float var14, float var15) {
      Tessellator var16 = Tessellator.func_178181_a();
      BufferBuilder var17 = var16.func_178180_c();
      var17.func_181668_a(3, DefaultVertexFormats.field_181706_f);
      func_189698_a(var17, var0, var2, var4, var6, var8, var10, var12, var13, var14, var15);
      var16.func_78381_a();
   }

   public static void func_189698_a(BufferBuilder var0, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16) {
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, 0.0F).func_181675_d();
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var11).func_181666_a(var13, var14, var15, 0.0F).func_181675_d();
      var0.func_181662_b(var1, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, 0.0F).func_181675_d();
      var0.func_181662_b(var7, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var5).func_181666_a(var13, var14, var15, 0.0F).func_181675_d();
      var0.func_181662_b(var7, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var5).func_181666_a(var13, var14, var15, 0.0F).func_181675_d();
   }

   public static void func_189696_b(AxisAlignedBB var0, float var1, float var2, float var3, float var4) {
      func_189695_b(var0.field_72340_a, var0.field_72338_b, var0.field_72339_c, var0.field_72336_d, var0.field_72337_e, var0.field_72334_f, var1, var2, var3, var4);
   }

   public static void func_189695_b(double var0, double var2, double var4, double var6, double var8, double var10, float var12, float var13, float var14, float var15) {
      Tessellator var16 = Tessellator.func_178181_a();
      BufferBuilder var17 = var16.func_178180_c();
      var17.func_181668_a(5, DefaultVertexFormats.field_181706_f);
      func_189693_b(var17, var0, var2, var4, var6, var8, var10, var12, var13, var14, var15);
      var16.func_78381_a();
   }

   public static void func_189693_b(BufferBuilder var0, double var1, double var3, double var5, double var7, double var9, double var11, float var13, float var14, float var15, float var16) {
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var3, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var1, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var5).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
      var0.func_181662_b(var7, var9, var11).func_181666_a(var13, var14, var15, var16).func_181675_d();
   }

   private void func_184385_a(int var1, int var2, int var3, int var4, int var5, int var6, boolean var7) {
      this.field_175008_n.func_187474_a(var1, var2, var3, var4, var5, var6, var7);
   }

   public void func_184376_a(IBlockReader var1, BlockPos var2, IBlockState var3, IBlockState var4, int var5) {
      int var6 = var2.func_177958_n();
      int var7 = var2.func_177956_o();
      int var8 = var2.func_177952_p();
      this.func_184385_a(var6 - 1, var7 - 1, var8 - 1, var6 + 1, var7 + 1, var8 + 1, (var5 & 8) != 0);
   }

   public void func_174959_b(BlockPos var1) {
      this.field_184387_ae.add(var1.func_185334_h());
   }

   public void func_147585_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.func_184385_a(var1 - 1, var2 - 1, var3 - 1, var4 + 1, var5 + 1, var6 + 1, false);
   }

   public void func_184377_a(@Nullable SoundEvent var1, BlockPos var2) {
      ISound var3 = (ISound)this.field_147593_P.get(var2);
      if (var3 != null) {
         this.field_72777_q.func_147118_V().func_147683_b(var3);
         this.field_147593_P.remove(var2);
      }

      if (var1 != null) {
         ItemRecord var4 = ItemRecord.func_185074_a(var1);
         if (var4 != null) {
            this.field_72777_q.field_71456_v.func_73833_a(var4.func_200299_h().func_150254_d());
         }

         SimpleSound var5 = SimpleSound.func_184372_a(var1, (float)var2.func_177958_n(), (float)var2.func_177956_o(), (float)var2.func_177952_p());
         this.field_147593_P.put(var2, var5);
         this.field_72777_q.func_147118_V().func_147682_a(var5);
      }

      this.func_193054_a(this.field_72769_h, var2, var1 != null);
   }

   private void func_193054_a(World var1, BlockPos var2, boolean var3) {
      List var4 = var1.func_72872_a(EntityLivingBase.class, (new AxisAlignedBB(var2)).func_186662_g(3.0D));
      Iterator var5 = var4.iterator();

      while(var5.hasNext()) {
         EntityLivingBase var6 = (EntityLivingBase)var5.next();
         var6.func_191987_a(var2, var3);
      }

   }

   public void func_184375_a(@Nullable EntityPlayer var1, SoundEvent var2, SoundCategory var3, double var4, double var6, double var8, float var10, float var11) {
   }

   public void func_195461_a(IParticleData var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      this.func_195462_a(var1, var2, false, var3, var5, var7, var9, var11, var13);
   }

   public void func_195462_a(IParticleData var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      try {
         this.func_195469_b(var1, var2, var3, var4, var6, var8, var10, var12, var14);
      } catch (Throwable var19) {
         CrashReport var17 = CrashReport.func_85055_a(var19, "Exception while adding particle");
         CrashReportCategory var18 = var17.func_85058_a("Particle being added");
         var18.func_71507_a("ID", var1.func_197554_b().func_197570_d());
         var18.func_71507_a("Parameters", var1.func_197555_a());
         var18.func_189529_a("Position", () -> {
            return CrashReportCategory.func_85074_a(var4, var6, var8);
         });
         throw new ReportedException(var17);
      }
   }

   private <T extends IParticleData> void func_195467_a(T var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this.func_195461_a(var1, var1.func_197554_b().func_197575_f(), var2, var4, var6, var8, var10, var12);
   }

   @Nullable
   private Particle func_195471_b(IParticleData var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13) {
      return this.func_195469_b(var1, var2, false, var3, var5, var7, var9, var11, var13);
   }

   @Nullable
   private Particle func_195469_b(IParticleData var1, boolean var2, boolean var3, double var4, double var6, double var8, double var10, double var12, double var14) {
      Entity var16 = this.field_72777_q.func_175606_aa();
      if (this.field_72777_q != null && var16 != null && this.field_72777_q.field_71452_i != null) {
         int var17 = this.func_190572_a(var3);
         double var18 = var16.field_70165_t - var4;
         double var20 = var16.field_70163_u - var6;
         double var22 = var16.field_70161_v - var8;
         if (var2) {
            return this.field_72777_q.field_71452_i.func_199280_a(var1, var4, var6, var8, var10, var12, var14);
         } else if (var18 * var18 + var20 * var20 + var22 * var22 > 1024.0D) {
            return null;
         } else {
            return var17 > 1 ? null : this.field_72777_q.field_71452_i.func_199280_a(var1, var4, var6, var8, var10, var12, var14);
         }
      } else {
         return null;
      }
   }

   private int func_190572_a(boolean var1) {
      int var2 = this.field_72777_q.field_71474_y.field_74362_aa;
      if (var1 && var2 == 2 && this.field_72769_h.field_73012_v.nextInt(10) == 0) {
         var2 = 1;
      }

      if (var2 == 1 && this.field_72769_h.field_73012_v.nextInt(3) == 0) {
         var2 = 2;
      }

      return var2;
   }

   public void func_72703_a(Entity var1) {
   }

   public void func_72709_b(Entity var1) {
   }

   public void func_72728_f() {
   }

   public void func_180440_a(int var1, BlockPos var2, int var3) {
      switch(var1) {
      case 1023:
      case 1028:
      case 1038:
         Entity var4 = this.field_72777_q.func_175606_aa();
         if (var4 != null) {
            double var5 = (double)var2.func_177958_n() - var4.field_70165_t;
            double var7 = (double)var2.func_177956_o() - var4.field_70163_u;
            double var9 = (double)var2.func_177952_p() - var4.field_70161_v;
            double var11 = Math.sqrt(var5 * var5 + var7 * var7 + var9 * var9);
            double var13 = var4.field_70165_t;
            double var15 = var4.field_70163_u;
            double var17 = var4.field_70161_v;
            if (var11 > 0.0D) {
               var13 += var5 / var11 * 2.0D;
               var15 += var7 / var11 * 2.0D;
               var17 += var9 / var11 * 2.0D;
            }

            if (var1 == 1023) {
               this.field_72769_h.func_184134_a(var13, var15, var17, SoundEvents.field_187855_gD, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
            } else if (var1 == 1038) {
               this.field_72769_h.func_184134_a(var13, var15, var17, SoundEvents.field_193782_bq, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
            } else {
               this.field_72769_h.func_184134_a(var13, var15, var17, SoundEvents.field_187522_aL, SoundCategory.HOSTILE, 5.0F, 1.0F, false);
            }
         }
      default:
      }
   }

   public void func_180439_a(EntityPlayer var1, int var2, BlockPos var3, int var4) {
      Random var5 = this.field_72769_h.field_73012_v;
      double var6;
      double var8;
      double var10;
      int var12;
      int var16;
      int var17;
      double var20;
      double var21;
      double var22;
      double var23;
      double var25;
      double var33;
      double var38;
      double var40;
      switch(var2) {
      case 1000:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187574_as, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1001:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187576_at, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1002:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187578_au, SoundCategory.BLOCKS, 1.0F, 1.2F, false);
         break;
      case 1003:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187528_aR, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1004:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187634_bp, SoundCategory.NEUTRAL, 1.0F, 1.2F, false);
         break;
      case 1005:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187611_cI, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1006:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187875_gN, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1007:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187879_gP, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1008:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187613_bi, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1009:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187646_bt, SoundCategory.BLOCKS, 0.5F, 2.6F + (var5.nextFloat() - var5.nextFloat()) * 0.8F, false);
         break;
      case 1010:
         if (Item.func_150899_d(var4) instanceof ItemRecord) {
            this.field_72769_h.func_184149_a(var3, ((ItemRecord)Item.func_150899_d(var4)).func_185075_h());
         } else {
            this.field_72769_h.func_184149_a(var3, (SoundEvent)null);
         }
         break;
      case 1011:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187608_cH, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1012:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187873_gM, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1013:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187877_gO, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1014:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187610_bh, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1015:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187559_bL, SoundCategory.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1016:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187557_bK, SoundCategory.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1017:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187527_aQ, SoundCategory.HOSTILE, 10.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1018:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187606_E, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1019:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187927_ha, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1020:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187928_hb, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1021:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187929_hc, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1022:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187926_gz, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1024:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187853_gC, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1025:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187744_z, SoundCategory.NEUTRAL, 0.05F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1026:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187945_hs, SoundCategory.HOSTILE, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1027:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187941_ho, SoundCategory.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1029:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187680_c, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1030:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187698_i, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1031:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187689_f, SoundCategory.BLOCKS, 0.3F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1032:
         this.field_72777_q.func_147118_V().func_147682_a(SimpleSound.func_184371_a(SoundEvents.field_187812_eh, var5.nextFloat() * 0.4F + 0.8F));
         break;
      case 1033:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187542_ac, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1034:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187540_ab, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1035:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187621_J, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
         break;
      case 1036:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187614_cJ, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1037:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187617_cK, SoundCategory.BLOCKS, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1039:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_203257_fu, SoundCategory.HOSTILE, 0.3F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 1040:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_204783_kG, SoundCategory.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 1041:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_207378_dT, SoundCategory.NEUTRAL, 2.0F, (var5.nextFloat() - var5.nextFloat()) * 0.2F + 1.0F, false);
         break;
      case 2000:
         EnumFacing var31 = EnumFacing.func_82600_a(var4);
         int var7 = var31.func_82601_c();
         int var32 = var31.func_96559_d();
         int var9 = var31.func_82599_e();
         var10 = (double)var3.func_177958_n() + (double)var7 * 0.6D + 0.5D;
         var33 = (double)var3.func_177956_o() + (double)var32 * 0.6D + 0.5D;
         double var35 = (double)var3.func_177952_p() + (double)var9 * 0.6D + 0.5D;

         for(var16 = 0; var16 < 10; ++var16) {
            var38 = var5.nextDouble() * 0.2D + 0.01D;
            var40 = var10 + (double)var7 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var9 * 0.5D;
            var21 = var33 + (double)var32 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var32 * 0.5D;
            var23 = var35 + (double)var9 * 0.01D + (var5.nextDouble() - 0.5D) * (double)var7 * 0.5D;
            var25 = (double)var7 * var38 + var5.nextGaussian() * 0.01D;
            double var41 = (double)var32 * var38 + var5.nextGaussian() * 0.01D;
            double var29 = (double)var9 * var38 + var5.nextGaussian() * 0.01D;
            this.func_195467_a(Particles.field_197601_L, var40, var21, var23, var25, var41, var29);
         }

         return;
      case 2001:
         IBlockState var36 = Block.func_196257_b(var4);
         if (!var36.func_196958_f()) {
            SoundType var39 = var36.func_177230_c().func_185467_w();
            this.field_72769_h.func_184156_a(var3, var39.func_185845_c(), SoundCategory.BLOCKS, (var39.func_185843_a() + 1.0F) / 2.0F, var39.func_185847_b() * 0.8F, false);
         }

         this.field_72777_q.field_71452_i.func_180533_a(var3, var36);
         break;
      case 2002:
      case 2007:
         var6 = (double)var3.func_177958_n();
         var8 = (double)var3.func_177956_o();
         var10 = (double)var3.func_177952_p();

         for(var12 = 0; var12 < 8; ++var12) {
            this.func_195467_a(new ItemParticleData(Particles.field_197591_B, new ItemStack(Items.field_185155_bH)), var6, var8, var10, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D);
         }

         float var34 = (float)(var4 >> 16 & 255) / 255.0F;
         float var13 = (float)(var4 >> 8 & 255) / 255.0F;
         float var14 = (float)(var4 >> 0 & 255) / 255.0F;
         BasicParticleType var15 = var2 == 2007 ? Particles.field_197590_A : Particles.field_197620_m;

         for(var16 = 0; var16 < 100; ++var16) {
            var38 = var5.nextDouble() * 4.0D;
            var40 = var5.nextDouble() * 3.141592653589793D * 2.0D;
            var21 = Math.cos(var40) * var38;
            var23 = 0.01D + var5.nextDouble() * 0.5D;
            var25 = Math.sin(var40) * var38;
            Particle var27 = this.func_195471_b(var15, var15.func_197554_b().func_197575_f(), var6 + var21 * 0.1D, var8 + 0.3D, var10 + var25 * 0.1D, var21, var23, var25);
            if (var27 != null) {
               float var28 = 0.75F + var5.nextFloat() * 0.25F;
               var27.func_70538_b(var34 * var28, var13 * var28, var14 * var28);
               var27.func_70543_e((float)var38);
            }
         }

         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187825_fO, SoundCategory.NEUTRAL, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 2003:
         var6 = (double)var3.func_177958_n() + 0.5D;
         var8 = (double)var3.func_177956_o();
         var10 = (double)var3.func_177952_p() + 0.5D;

         for(var12 = 0; var12 < 8; ++var12) {
            this.func_195467_a(new ItemParticleData(Particles.field_197591_B, new ItemStack(Items.field_151061_bv)), var6, var8, var10, var5.nextGaussian() * 0.15D, var5.nextDouble() * 0.2D, var5.nextGaussian() * 0.15D);
         }

         for(var33 = 0.0D; var33 < 6.283185307179586D; var33 += 0.15707963267948966D) {
            this.func_195467_a(Particles.field_197599_J, var6 + Math.cos(var33) * 5.0D, var8 - 0.4D, var10 + Math.sin(var33) * 5.0D, Math.cos(var33) * -5.0D, 0.0D, Math.sin(var33) * -5.0D);
            this.func_195467_a(Particles.field_197599_J, var6 + Math.cos(var33) * 5.0D, var8 - 0.4D, var10 + Math.sin(var33) * 5.0D, Math.cos(var33) * -7.0D, 0.0D, Math.sin(var33) * -7.0D);
         }

         return;
      case 2004:
         for(var17 = 0; var17 < 20; ++var17) {
            double var37 = (double)var3.func_177958_n() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            var20 = (double)var3.func_177956_o() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            var22 = (double)var3.func_177952_p() + 0.5D + ((double)this.field_72769_h.field_73012_v.nextFloat() - 0.5D) * 2.0D;
            this.field_72769_h.func_195594_a(Particles.field_197601_L, var37, var20, var22, 0.0D, 0.0D, 0.0D);
            this.field_72769_h.func_195594_a(Particles.field_197631_x, var37, var20, var22, 0.0D, 0.0D, 0.0D);
         }

         return;
      case 2005:
         ItemBoneMeal.func_195965_a(this.field_72769_h, var3, var4);
         break;
      case 2006:
         for(var17 = 0; var17 < 200; ++var17) {
            float var18 = var5.nextFloat() * 4.0F;
            float var19 = var5.nextFloat() * 6.2831855F;
            var20 = (double)(MathHelper.func_76134_b(var19) * var18);
            var22 = 0.01D + var5.nextDouble() * 0.5D;
            double var24 = (double)(MathHelper.func_76126_a(var19) * var18);
            Particle var26 = this.func_195471_b(Particles.field_197616_i, false, (double)var3.func_177958_n() + var20 * 0.1D, (double)var3.func_177956_o() + 0.3D, (double)var3.func_177952_p() + var24 * 0.1D, var20, var22, var24);
            if (var26 != null) {
               var26.func_70543_e(var18);
            }
         }

         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187523_aM, SoundCategory.HOSTILE, 1.0F, this.field_72769_h.field_73012_v.nextFloat() * 0.1F + 0.9F, false);
         break;
      case 3000:
         this.field_72769_h.func_195590_a(Particles.field_197626_s, true, (double)var3.func_177958_n() + 0.5D, (double)var3.func_177956_o() + 0.5D, (double)var3.func_177952_p() + 0.5D, 0.0D, 0.0D, 0.0D);
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187598_bd, SoundCategory.BLOCKS, 10.0F, (1.0F + (this.field_72769_h.field_73012_v.nextFloat() - this.field_72769_h.field_73012_v.nextFloat()) * 0.2F) * 0.7F, false);
         break;
      case 3001:
         this.field_72769_h.func_184156_a(var3, SoundEvents.field_187525_aO, SoundCategory.HOSTILE, 64.0F, 0.8F + this.field_72769_h.field_73012_v.nextFloat() * 0.3F, false);
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

   public boolean func_184384_n() {
      return this.field_175009_l.isEmpty() && this.field_174995_M.func_188247_f();
   }

   public void func_174979_m() {
      this.field_147595_R = true;
      this.field_204607_y = true;
   }

   public void func_181023_a(Collection<TileEntity> var1, Collection<TileEntity> var2) {
      synchronized(this.field_181024_n) {
         this.field_181024_n.removeAll(var1);
         this.field_181024_n.addAll(var2);
      }
   }

   class ContainerLocalRenderInformation {
      private final RenderChunk field_178036_a;
      private final EnumFacing field_178034_b;
      private byte field_178035_c;
      private final int field_178032_d;

      private ContainerLocalRenderInformation(RenderChunk var2, EnumFacing var3, @Nullable int var4) {
         super();
         this.field_178036_a = var2;
         this.field_178034_b = var3;
         this.field_178032_d = var4;
      }

      public void func_189561_a(byte var1, EnumFacing var2) {
         this.field_178035_c = (byte)(this.field_178035_c | var1 | 1 << var2.ordinal());
      }

      public boolean func_189560_a(EnumFacing var1) {
         return (this.field_178035_c & 1 << var1.ordinal()) > 0;
      }

      // $FF: synthetic method
      ContainerLocalRenderInformation(RenderChunk var2, EnumFacing var3, int var4, Object var5) {
         this(var2, var3, var4);
      }
   }
}
