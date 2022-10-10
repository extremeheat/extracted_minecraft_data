package net.minecraft.block;

import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.trees.AcaciaTree;
import net.minecraft.block.trees.BirchTree;
import net.minecraft.block.trees.DarkOakTree;
import net.minecraft.block.trees.JungleTree;
import net.minecraft.block.trees.OakTree;
import net.minecraft.block.trees.SpruceTree;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Fluids;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.stats.StatList;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements IItemProvider {
   protected static final Logger field_196273_d = LogManager.getLogger();
   public static final ObjectIntIdentityMap<IBlockState> field_176229_d = new ObjectIntIdentityMap();
   private static final EnumFacing[] field_212556_a;
   protected final int field_149784_t;
   protected final float field_149782_v;
   protected final float field_149781_w;
   protected final boolean field_149789_z;
   protected final SoundType field_149762_H;
   protected final Material field_149764_J;
   protected final MaterialColor field_181083_K;
   private final float field_149765_K;
   protected final StateContainer<Block, IBlockState> field_176227_L;
   private IBlockState field_196275_y;
   protected final boolean field_196274_w;
   private final boolean field_208621_p;
   @Nullable
   private String field_149770_b;
   private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>> field_210300_r;

   public static int func_196246_j(@Nullable IBlockState var0) {
      if (var0 == null) {
         return 0;
      } else {
         int var1 = field_176229_d.func_148747_b(var0);
         return var1 == -1 ? 0 : var1;
      }
   }

   public static IBlockState func_196257_b(int var0) {
      IBlockState var1 = (IBlockState)field_176229_d.func_148745_a(var0);
      return var1 == null ? Blocks.field_150350_a.func_176223_P() : var1;
   }

   public static Block func_149634_a(@Nullable Item var0) {
      return var0 instanceof ItemBlock ? ((ItemBlock)var0).func_179223_d() : Blocks.field_150350_a;
   }

   public static IBlockState func_199601_a(IBlockState var0, IBlockState var1, World var2, BlockPos var3) {
      VoxelShape var4 = VoxelShapes.func_197882_b(var0.func_196952_d(var2, var3), var1.func_196952_d(var2, var3), IBooleanFunction.ONLY_SECOND).func_197751_a((double)var3.func_177958_n(), (double)var3.func_177956_o(), (double)var3.func_177952_p());
      List var5 = var2.func_72839_b((Entity)null, var4.func_197752_a());
      Iterator var6 = var5.iterator();

      while(var6.hasNext()) {
         Entity var7 = (Entity)var6.next();
         double var8 = VoxelShapes.func_212437_a(EnumFacing.Axis.Y, var7.func_174813_aQ().func_72317_d(0.0D, 1.0D, 0.0D), Stream.of(var4), -1.0D);
         var7.func_70634_a(var7.field_70165_t, var7.field_70163_u + 1.0D + var8, var7.field_70161_v);
      }

      return var1;
   }

   public static VoxelShape func_208617_a(double var0, double var2, double var4, double var6, double var8, double var10) {
      return VoxelShapes.func_197873_a(var0 / 16.0D, var2 / 16.0D, var4 / 16.0D, var6 / 16.0D, var8 / 16.0D, var10 / 16.0D);
   }

   @Deprecated
   public boolean func_189872_a(IBlockState var1, Entity var2) {
      return true;
   }

   @Deprecated
   public boolean func_196261_e(IBlockState var1) {
      return false;
   }

   @Deprecated
   public int func_149750_m(IBlockState var1) {
      return this.field_149784_t;
   }

   @Deprecated
   public Material func_149688_o(IBlockState var1) {
      return this.field_149764_J;
   }

   @Deprecated
   public MaterialColor func_180659_g(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.field_181083_K;
   }

   @Deprecated
   public void func_196242_c(IBlockState var1, IWorld var2, BlockPos var3, int var4) {
      BlockPos.PooledMutableBlockPos var5 = BlockPos.PooledMutableBlockPos.func_185346_s();
      Throwable var6 = null;

      try {
         EnumFacing[] var7 = field_212556_a;
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            EnumFacing var10 = var7[var9];
            var5.func_189533_g(var3).func_189536_c(var10);
            IBlockState var11 = var2.func_180495_p(var5);
            IBlockState var12 = var11.func_196956_a(var10.func_176734_d(), var1, var2, var5, var3);
            func_196263_a(var11, var12, var2, var5, var4);
         }
      } catch (Throwable var20) {
         var6 = var20;
         throw var20;
      } finally {
         if (var5 != null) {
            if (var6 != null) {
               try {
                  var5.close();
               } catch (Throwable var19) {
                  var6.addSuppressed(var19);
               }
            } else {
               var5.close();
            }
         }

      }

   }

   public boolean func_203417_a(Tag<Block> var1) {
      return var1.func_199685_a_(this);
   }

   public static IBlockState func_199770_b(IBlockState var0, IWorld var1, BlockPos var2) {
      IBlockState var3 = var0;
      BlockPos.MutableBlockPos var4 = new BlockPos.MutableBlockPos();
      EnumFacing[] var5 = field_212556_a;
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         EnumFacing var8 = var5[var7];
         var4.func_189533_g(var2).func_189536_c(var8);
         var3 = var3.func_196956_a(var8, var1.func_180495_p(var4), var1, var2, var4);
      }

      return var3;
   }

   public static void func_196263_a(IBlockState var0, IBlockState var1, IWorld var2, BlockPos var3, int var4) {
      if (var1 != var0) {
         if (var1.func_196958_f()) {
            if (!var2.func_201670_d()) {
               var2.func_175655_b(var3, (var4 & 32) == 0);
            }
         } else {
            var2.func_180501_a(var3, var1, var4 & -33);
         }
      }

   }

   @Deprecated
   public void func_196248_b(IBlockState var1, IWorld var2, BlockPos var3, int var4) {
   }

   @Deprecated
   public IBlockState func_196271_a(IBlockState var1, EnumFacing var2, IBlockState var3, IWorld var4, BlockPos var5, BlockPos var6) {
      return var1;
   }

   @Deprecated
   public IBlockState func_185499_a(IBlockState var1, Rotation var2) {
      return var1;
   }

   @Deprecated
   public IBlockState func_185471_a(IBlockState var1, Mirror var2) {
      return var1;
   }

   public Block(Block.Properties var1) {
      super();
      StateContainer.Builder var2 = new StateContainer.Builder(this);
      this.func_206840_a(var2);
      this.field_176227_L = var2.func_206893_a(BlockState::new);
      this.func_180632_j((IBlockState)this.field_176227_L.func_177621_b());
      this.field_149764_J = var1.field_200953_a;
      this.field_181083_K = var1.field_200954_b;
      this.field_196274_w = var1.field_200955_c;
      this.field_149762_H = var1.field_200956_d;
      this.field_149784_t = var1.field_200957_e;
      this.field_149781_w = var1.field_200958_f;
      this.field_149782_v = var1.field_200959_g;
      this.field_149789_z = var1.field_200960_h;
      this.field_149765_K = var1.field_200961_i;
      this.field_208621_p = var1.field_208772_j;
   }

   protected static boolean func_193384_b(Block var0) {
      return var0 instanceof BlockShulkerBox || var0 instanceof BlockLeaves || var0.func_203417_a(BlockTags.field_212185_E) || var0 instanceof BlockStainedGlass || var0 == Blocks.field_150461_bJ || var0 == Blocks.field_150383_bp || var0 == Blocks.field_150359_w || var0 == Blocks.field_150426_aN || var0 == Blocks.field_150432_aD || var0 == Blocks.field_180398_cJ || var0 == Blocks.field_205165_jY;
   }

   public static boolean func_193382_c(Block var0) {
      return func_193384_b(var0) || var0 == Blocks.field_150331_J || var0 == Blocks.field_150320_F || var0 == Blocks.field_150332_K;
   }

   @Deprecated
   public boolean func_149637_q(IBlockState var1) {
      return var1.func_185904_a().func_76230_c() && var1.func_185917_h();
   }

   @Deprecated
   public boolean func_149721_r(IBlockState var1) {
      return var1.func_185904_a().func_76218_k() && var1.func_185917_h() && !var1.func_185897_m();
   }

   @Deprecated
   public boolean func_176214_u(IBlockState var1) {
      return this.field_149764_J.func_76230_c() && var1.func_185917_h();
   }

   @Deprecated
   public boolean func_149686_d(IBlockState var1) {
      return true;
   }

   @Deprecated
   public boolean func_185481_k(IBlockState var1) {
      return var1.func_185904_a().func_76218_k() && var1.func_185917_h();
   }

   @Deprecated
   public boolean func_190946_v(IBlockState var1) {
      return false;
   }

   @Deprecated
   public boolean func_196266_a(IBlockState var1, IBlockReader var2, BlockPos var3, PathType var4) {
      switch(var4) {
      case LAND:
         return !func_208062_a(this.func_196268_f(var1, var2, var3));
      case WATER:
         return var2.func_204610_c(var3).func_206884_a(FluidTags.field_206959_a);
      case AIR:
         return !func_208062_a(this.func_196268_f(var1, var2, var3));
      default:
         return false;
      }
   }

   @Deprecated
   public EnumBlockRenderType func_149645_b(IBlockState var1) {
      return EnumBlockRenderType.MODEL;
   }

   @Deprecated
   public boolean func_196253_a(IBlockState var1, BlockItemUseContext var2) {
      return this.field_149764_J.func_76222_j() && var2.func_195996_i().func_77973_b() != this.func_199767_j();
   }

   @Deprecated
   public float func_176195_g(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.field_149782_v;
   }

   public boolean func_149653_t(IBlockState var1) {
      return this.field_149789_z;
   }

   public boolean func_149716_u() {
      return this instanceof ITileEntityProvider;
   }

   @Deprecated
   public boolean func_201783_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return false;
   }

   @Deprecated
   public int func_185484_c(IBlockState var1, IWorldReader var2, BlockPos var3) {
      int var4 = var2.func_175626_b(var3, var1.func_185906_d());
      if (var4 == 0 && var1.func_177230_c() instanceof BlockSlab) {
         var3 = var3.func_177977_b();
         var1 = var2.func_180495_p(var3);
         return var2.func_175626_b(var3, var1.func_185906_d());
      } else {
         return var4;
      }
   }

   public static boolean func_176225_a(IBlockState var0, IBlockReader var1, BlockPos var2, EnumFacing var3) {
      BlockPos var4 = var2.func_177972_a(var3);
      IBlockState var5 = var1.func_180495_p(var4);
      if (var0.func_200017_a(var5, var3)) {
         return false;
      } else if (var5.func_200132_m()) {
         Block.RenderSideCacheKey var6 = new Block.RenderSideCacheKey(var0, var5, var3);
         Object2ByteLinkedOpenHashMap var7 = (Object2ByteLinkedOpenHashMap)field_210300_r.get();
         byte var8 = var7.getAndMoveToFirst(var6);
         if (var8 != 127) {
            return var8 != 0;
         } else {
            VoxelShape var9 = var0.func_196951_e(var1, var2);
            VoxelShape var10 = var5.func_196951_e(var1, var4);
            boolean var11 = !VoxelShapes.func_197875_a(var9, var10, var3);
            if (var7.size() == 200) {
               var7.removeLastByte();
            }

            var7.putAndMoveToFirst(var6, (byte)(var11 ? 1 : 0));
            return var11;
         }
      } else {
         return true;
      }
   }

   @Deprecated
   public boolean func_200124_e(IBlockState var1) {
      return this.field_196274_w && var1.func_177230_c().func_180664_k() == BlockRenderLayer.SOLID;
   }

   @Deprecated
   public boolean func_200122_a(IBlockState var1, IBlockState var2, EnumFacing var3) {
      return false;
   }

   @Deprecated
   public BlockFaceShape func_193383_a(IBlockReader var1, IBlockState var2, BlockPos var3, EnumFacing var4) {
      return BlockFaceShape.SOLID;
   }

   @Deprecated
   public VoxelShape func_196244_b(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197868_b();
   }

   @Deprecated
   public VoxelShape func_196268_f(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return this.field_196274_w ? var1.func_196954_c(var2, var3) : VoxelShapes.func_197880_a();
   }

   @Deprecated
   public VoxelShape func_196247_c(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return var1.func_196954_c(var2, var3);
   }

   @Deprecated
   public VoxelShape func_199600_g(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return VoxelShapes.func_197880_a();
   }

   public static boolean func_208061_a(VoxelShape var0, EnumFacing var1) {
      VoxelShape var2 = var0.func_212434_a(var1);
      return func_208062_a(var2);
   }

   public static boolean func_208062_a(VoxelShape var0) {
      return !VoxelShapes.func_197879_c(VoxelShapes.func_197868_b(), var0, IBooleanFunction.ONLY_FIRST);
   }

   @Deprecated
   public final boolean func_200012_i(IBlockState var1, IBlockReader var2, BlockPos var3) {
      boolean var4 = var1.func_200132_m();
      VoxelShape var5 = var4 ? var1.func_196951_e(var2, var3) : VoxelShapes.func_197880_a();
      return func_208062_a(var5);
   }

   public boolean func_200123_i(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return !func_208062_a(var1.func_196954_c(var2, var3)) && var1.func_204520_s().func_206888_e();
   }

   @Deprecated
   public int func_200011_d(IBlockState var1, IBlockReader var2, BlockPos var3) {
      if (var1.func_200015_d(var2, var3)) {
         return var2.func_201572_C();
      } else {
         return var1.func_200131_a(var2, var3) ? 0 : 1;
      }
   }

   @Deprecated
   public final boolean func_200125_k(IBlockState var1, IBlockReader var2, BlockPos var3) {
      return !var1.func_200015_d(var2, var3) && var1.func_200016_a(var2, var3) == var2.func_201572_C();
   }

   public boolean func_200293_a(IBlockState var1) {
      return this.func_149703_v();
   }

   public boolean func_149703_v() {
      return true;
   }

   @Deprecated
   public void func_196265_a(IBlockState var1, World var2, BlockPos var3, Random var4) {
      this.func_196267_b(var1, var2, var3, var4);
   }

   @Deprecated
   public void func_196267_b(IBlockState var1, World var2, BlockPos var3, Random var4) {
   }

   public void func_180655_c(IBlockState var1, World var2, BlockPos var3, Random var4) {
   }

   public void func_176206_d(IWorld var1, BlockPos var2, IBlockState var3) {
   }

   @Deprecated
   public void func_189540_a(IBlockState var1, World var2, BlockPos var3, Block var4, BlockPos var5) {
   }

   public int func_149738_a(IWorldReaderBase var1) {
      return 10;
   }

   @Deprecated
   public void func_196259_b(IBlockState var1, World var2, BlockPos var3, IBlockState var4) {
   }

   @Deprecated
   public void func_196243_a(IBlockState var1, World var2, BlockPos var3, IBlockState var4, boolean var5) {
   }

   public int func_196264_a(IBlockState var1, Random var2) {
      return 1;
   }

   public IItemProvider func_199769_a(IBlockState var1, World var2, BlockPos var3, int var4) {
      return this;
   }

   @Deprecated
   public float func_180647_a(IBlockState var1, EntityPlayer var2, IBlockReader var3, BlockPos var4) {
      float var5 = var1.func_185887_b(var3, var4);
      if (var5 == -1.0F) {
         return 0.0F;
      } else {
         int var6 = var2.func_184823_b(var1) ? 30 : 100;
         return var2.func_184813_a(var1) / var5 / (float)var6;
      }
   }

   @Deprecated
   public void func_196255_a(IBlockState var1, World var2, BlockPos var3, float var4, int var5) {
      if (!var2.field_72995_K) {
         int var6 = this.func_196251_a(var1, var5, var2, var3, var2.field_73012_v);

         for(int var7 = 0; var7 < var6; ++var7) {
            if (var4 >= 1.0F || var2.field_73012_v.nextFloat() <= var4) {
               Item var8 = this.func_199769_a(var1, var2, var3, var5).func_199767_j();
               if (var8 != Items.field_190931_a) {
                  func_180635_a(var2, var3, new ItemStack(var8));
               }
            }
         }

      }
   }

   public static void func_180635_a(World var0, BlockPos var1, ItemStack var2) {
      if (!var0.field_72995_K && !var2.func_190926_b() && var0.func_82736_K().func_82766_b("doTileDrops")) {
         float var3 = 0.5F;
         double var4 = (double)(var0.field_73012_v.nextFloat() * 0.5F) + 0.25D;
         double var6 = (double)(var0.field_73012_v.nextFloat() * 0.5F) + 0.25D;
         double var8 = (double)(var0.field_73012_v.nextFloat() * 0.5F) + 0.25D;
         EntityItem var10 = new EntityItem(var0, (double)var1.func_177958_n() + var4, (double)var1.func_177956_o() + var6, (double)var1.func_177952_p() + var8, var2);
         var10.func_174869_p();
         var0.func_72838_d(var10);
      }
   }

   protected void func_180637_b(World var1, BlockPos var2, int var3) {
      if (!var1.field_72995_K && var1.func_82736_K().func_82766_b("doTileDrops")) {
         while(var3 > 0) {
            int var4 = EntityXPOrb.func_70527_a(var3);
            var3 -= var4;
            var1.func_72838_d(new EntityXPOrb(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, var4));
         }
      }

   }

   public float func_149638_a() {
      return this.field_149781_w;
   }

   @Nullable
   public static RayTraceResult func_180636_a(IBlockState var0, World var1, BlockPos var2, Vec3d var3, Vec3d var4) {
      RayTraceResult var5 = var0.func_196954_c(var1, var2).func_212433_a(var3, var4, var2);
      if (var5 != null) {
         RayTraceResult var6 = var0.func_199611_f(var1, var2).func_212433_a(var3, var4, var2);
         if (var6 != null && var6.field_72307_f.func_178788_d(var3).func_189985_c() < var5.field_72307_f.func_178788_d(var3).func_189985_c()) {
            var5.field_178784_b = var6.field_178784_b;
         }
      }

      return var5;
   }

   public void func_180652_a(World var1, BlockPos var2, Explosion var3) {
   }

   public BlockRenderLayer func_180664_k() {
      return BlockRenderLayer.SOLID;
   }

   @Deprecated
   public boolean func_196260_a(IBlockState var1, IWorldReaderBase var2, BlockPos var3) {
      return true;
   }

   @Deprecated
   public boolean func_196250_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4, EnumHand var5, EnumFacing var6, float var7, float var8, float var9) {
      return false;
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
   }

   @Nullable
   public IBlockState func_196258_a(BlockItemUseContext var1) {
      return this.func_176223_P();
   }

   @Deprecated
   public void func_196270_a(IBlockState var1, World var2, BlockPos var3, EntityPlayer var4) {
   }

   @Deprecated
   public int func_180656_a(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return 0;
   }

   @Deprecated
   public boolean func_149744_f(IBlockState var1) {
      return false;
   }

   @Deprecated
   public void func_196262_a(IBlockState var1, World var2, BlockPos var3, Entity var4) {
   }

   @Deprecated
   public int func_176211_b(IBlockState var1, IBlockReader var2, BlockPos var3, EnumFacing var4) {
      return 0;
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, @Nullable TileEntity var5, ItemStack var6) {
      var2.func_71029_a(StatList.field_188065_ae.func_199076_b(this));
      var2.func_71020_j(0.005F);
      if (this.func_149700_E() && EnchantmentHelper.func_77506_a(Enchantments.field_185306_r, var6) > 0) {
         ItemStack var8 = this.func_180643_i(var4);
         func_180635_a(var1, var3, var8);
      } else {
         int var7 = EnchantmentHelper.func_77506_a(Enchantments.field_185308_t, var6);
         var4.func_196949_c(var1, var3, var7);
      }

   }

   protected boolean func_149700_E() {
      return this.func_176223_P().func_185917_h() && !this.func_149716_u();
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      return new ItemStack(this);
   }

   public int func_196251_a(IBlockState var1, int var2, World var3, BlockPos var4, Random var5) {
      return this.func_196264_a(var1, var5);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
   }

   public boolean func_181623_g() {
      return !this.field_149764_J.func_76220_a() && !this.field_149764_J.func_76224_d();
   }

   public ITextComponent func_200291_n() {
      return new TextComponentTranslation(this.func_149739_a(), new Object[0]);
   }

   public String func_149739_a() {
      if (this.field_149770_b == null) {
         this.field_149770_b = Util.func_200697_a("block", IRegistry.field_212618_g.func_177774_c(this));
      }

      return this.field_149770_b;
   }

   @Deprecated
   public boolean func_189539_a(IBlockState var1, World var2, BlockPos var3, int var4, int var5) {
      return false;
   }

   @Deprecated
   public EnumPushReaction func_149656_h(IBlockState var1) {
      return this.field_149764_J.func_186274_m();
   }

   @Deprecated
   public float func_185485_f(IBlockState var1) {
      return var1.func_185898_k() ? 0.2F : 1.0F;
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      var3.func_180430_e(var4, 1.0F);
   }

   public void func_176216_a(IBlockReader var1, Entity var2) {
      var2.field_70181_x = 0.0D;
   }

   public ItemStack func_185473_a(IBlockReader var1, BlockPos var2, IBlockState var3) {
      return new ItemStack(this);
   }

   public void func_149666_a(ItemGroup var1, NonNullList<ItemStack> var2) {
      var2.add(new ItemStack(this));
   }

   @Deprecated
   public IFluidState func_204507_t(IBlockState var1) {
      return Fluids.field_204541_a.func_207188_f();
   }

   public float func_208618_m() {
      return this.field_149765_K;
   }

   @Deprecated
   public long func_209900_a(IBlockState var1, BlockPos var2) {
      return MathHelper.func_180186_a(var2);
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
      var1.func_180498_a(var4, 2001, var2, func_196246_j(var3));
   }

   public void func_176224_k(World var1, BlockPos var2) {
   }

   public boolean func_149659_a(Explosion var1) {
      return true;
   }

   @Deprecated
   public boolean func_149740_M(IBlockState var1) {
      return false;
   }

   @Deprecated
   public int func_180641_l(IBlockState var1, World var2, BlockPos var3) {
      return 0;
   }

   protected void func_206840_a(StateContainer.Builder<Block, IBlockState> var1) {
   }

   public StateContainer<Block, IBlockState> func_176194_O() {
      return this.field_176227_L;
   }

   protected final void func_180632_j(IBlockState var1) {
      this.field_196275_y = var1;
   }

   public final IBlockState func_176223_P() {
      return this.field_196275_y;
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.NONE;
   }

   @Deprecated
   public Vec3d func_190949_e(IBlockState var1, IBlockReader var2, BlockPos var3) {
      Block.EnumOffsetType var4 = this.func_176218_Q();
      if (var4 == Block.EnumOffsetType.NONE) {
         return Vec3d.field_186680_a;
      } else {
         long var5 = MathHelper.func_180187_c(var3.func_177958_n(), 0, var3.func_177952_p());
         return new Vec3d(((double)((float)(var5 & 15L) / 15.0F) - 0.5D) * 0.5D, var4 == Block.EnumOffsetType.XYZ ? ((double)((float)(var5 >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double)((float)(var5 >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
      }
   }

   public SoundType func_185467_w() {
      return this.field_149762_H;
   }

   public Item func_199767_j() {
      return Item.func_150898_a(this);
   }

   public boolean func_208619_r() {
      return this.field_208621_p;
   }

   public String toString() {
      return "Block{" + IRegistry.field_212618_g.func_177774_c(this) + "}";
   }

   public void func_190948_a(ItemStack var1, @Nullable IBlockReader var2, List<ITextComponent> var3, ITooltipFlag var4) {
   }

   public static boolean func_196252_e(Block var0) {
      return var0 == Blocks.field_150348_b || var0 == Blocks.field_196650_c || var0 == Blocks.field_196654_e || var0 == Blocks.field_196656_g;
   }

   public static boolean func_196245_f(Block var0) {
      return var0 == Blocks.field_150346_d || var0 == Blocks.field_196660_k || var0 == Blocks.field_196661_l;
   }

   public static void func_149671_p() {
      BlockAir var0 = new BlockAir(Block.Properties.func_200945_a(Material.field_151579_a).func_200942_a());
      func_196249_a(IRegistry.field_212618_g.func_212609_b(), var0);
      BlockStone var1 = new BlockStone(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(1.5F, 6.0F));
      func_196254_a("stone", var1);
      func_196254_a("granite", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151664_l).func_200948_a(1.5F, 6.0F)));
      func_196254_a("polished_granite", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151664_l).func_200948_a(1.5F, 6.0F)));
      func_196254_a("diorite", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200948_a(1.5F, 6.0F)));
      func_196254_a("polished_diorite", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200948_a(1.5F, 6.0F)));
      func_196254_a("andesite", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(1.5F, 6.0F)));
      func_196254_a("polished_andesite", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(1.5F, 6.0F)));
      func_196254_a("grass_block", new BlockGrass(Block.Properties.func_200945_a(Material.field_151577_b).func_200944_c().func_200943_b(0.6F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("dirt", new Block(Block.Properties.func_200949_a(Material.field_151578_c, MaterialColor.field_151664_l).func_200943_b(0.5F).func_200947_a(SoundType.field_185849_b)));
      func_196254_a("coarse_dirt", new Block(Block.Properties.func_200949_a(Material.field_151578_c, MaterialColor.field_151664_l).func_200943_b(0.5F).func_200947_a(SoundType.field_185849_b)));
      func_196254_a("podzol", new BlockDirtSnowy(Block.Properties.func_200949_a(Material.field_151578_c, MaterialColor.field_151654_J).func_200943_b(0.5F).func_200947_a(SoundType.field_185849_b)));
      Block var2 = new Block(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(2.0F, 6.0F));
      func_196254_a("cobblestone", var2);
      Block var3 = new Block(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151663_o).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a));
      Block var4 = new Block(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a));
      Block var5 = new Block(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151658_d).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a));
      Block var6 = new Block(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a));
      Block var7 = new Block(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151676_q).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a));
      Block var8 = new Block(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a));
      func_196254_a("oak_planks", var3);
      func_196254_a("spruce_planks", var4);
      func_196254_a("birch_planks", var5);
      func_196254_a("jungle_planks", var6);
      func_196254_a("acacia_planks", var7);
      func_196254_a("dark_oak_planks", var8);
      BlockSapling var9 = new BlockSapling(new OakTree(), Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockSapling var10 = new BlockSapling(new SpruceTree(), Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockSapling var11 = new BlockSapling(new BirchTree(), Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockSapling var12 = new BlockSapling(new JungleTree(), Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockSapling var13 = new BlockSapling(new AcaciaTree(), Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockSapling var14 = new BlockSapling(new DarkOakTree(), Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      func_196254_a("oak_sapling", var9);
      func_196254_a("spruce_sapling", var10);
      func_196254_a("birch_sapling", var11);
      func_196254_a("jungle_sapling", var12);
      func_196254_a("acacia_sapling", var13);
      func_196254_a("dark_oak_sapling", var14);
      func_196254_a("bedrock", new BlockEmptyDrops(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("water", new BlockFlowingFluid(Fluids.field_204546_a, Block.Properties.func_200945_a(Material.field_151586_h).func_200942_a().func_200943_b(100.0F)));
      func_196254_a("lava", new BlockFlowingFluid(Fluids.field_204547_b, Block.Properties.func_200945_a(Material.field_151587_i).func_200942_a().func_200944_c().func_200943_b(100.0F).func_200951_a(15)));
      func_196254_a("sand", new BlockSand(14406560, Block.Properties.func_200949_a(Material.field_151595_p, MaterialColor.field_151658_d).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("red_sand", new BlockSand(11098145, Block.Properties.func_200949_a(Material.field_151595_p, MaterialColor.field_151676_q).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("gravel", new BlockGravel(Block.Properties.func_200949_a(Material.field_151595_p, MaterialColor.field_151665_m).func_200943_b(0.6F).func_200947_a(SoundType.field_185849_b)));
      func_196254_a("gold_ore", new BlockOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(3.0F, 3.0F)));
      func_196254_a("iron_ore", new BlockOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(3.0F, 3.0F)));
      func_196254_a("coal_ore", new BlockOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(3.0F, 3.0F)));
      func_196254_a("oak_log", new BlockLog(MaterialColor.field_151663_o, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_log", new BlockLog(MaterialColor.field_151654_J, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_log", new BlockLog(MaterialColor.field_151658_d, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151677_p).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_log", new BlockLog(MaterialColor.field_151664_l, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_log", new BlockLog(MaterialColor.field_151676_q, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151665_m).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_log", new BlockLog(MaterialColor.field_151650_B, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_spruce_log", new BlockLog(MaterialColor.field_151654_J, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_birch_log", new BlockLog(MaterialColor.field_151658_d, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151658_d).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_jungle_log", new BlockLog(MaterialColor.field_151664_l, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_acacia_log", new BlockLog(MaterialColor.field_151676_q, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151676_q).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_dark_oak_log", new BlockLog(MaterialColor.field_151650_B, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_oak_log", new BlockLog(MaterialColor.field_151663_o, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151663_o).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("oak_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151663_o).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151658_d).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151676_q).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_oak_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151663_o).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_spruce_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_birch_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151658_d).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_jungle_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_acacia_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151676_q).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stripped_dark_oak_wood", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200943_b(2.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("oak_leaves", new BlockLeaves(Block.Properties.func_200945_a(Material.field_151584_j).func_200943_b(0.2F).func_200944_c().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("spruce_leaves", new BlockLeaves(Block.Properties.func_200945_a(Material.field_151584_j).func_200943_b(0.2F).func_200944_c().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("birch_leaves", new BlockLeaves(Block.Properties.func_200945_a(Material.field_151584_j).func_200943_b(0.2F).func_200944_c().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("jungle_leaves", new BlockLeaves(Block.Properties.func_200945_a(Material.field_151584_j).func_200943_b(0.2F).func_200944_c().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("acacia_leaves", new BlockLeaves(Block.Properties.func_200945_a(Material.field_151584_j).func_200943_b(0.2F).func_200944_c().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("dark_oak_leaves", new BlockLeaves(Block.Properties.func_200945_a(Material.field_151584_j).func_200943_b(0.2F).func_200944_c().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("sponge", new BlockSponge(Block.Properties.func_200945_a(Material.field_151583_m).func_200943_b(0.6F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("wet_sponge", new BlockWetSponge(Block.Properties.func_200945_a(Material.field_151583_m).func_200943_b(0.6F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("glass", new BlockGlass(Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("lapis_ore", new BlockOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(3.0F, 3.0F)));
      func_196254_a("lapis_block", new Block(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151652_H).func_200948_a(3.0F, 3.0F)));
      func_196254_a("dispenser", new BlockDispenser(Block.Properties.func_200945_a(Material.field_151576_e).func_200943_b(3.5F)));
      Block var15 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200943_b(0.8F));
      func_196254_a("sandstone", var15);
      func_196254_a("chiseled_sandstone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200943_b(0.8F)));
      func_196254_a("cut_sandstone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200943_b(0.8F)));
      func_196254_a("note_block", new BlockNote(Block.Properties.func_200945_a(Material.field_151575_d).func_200947_a(SoundType.field_185848_a).func_200943_b(0.8F)));
      func_196254_a("white_bed", new BlockBed(EnumDyeColor.WHITE, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("orange_bed", new BlockBed(EnumDyeColor.ORANGE, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("magenta_bed", new BlockBed(EnumDyeColor.MAGENTA, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("light_blue_bed", new BlockBed(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("yellow_bed", new BlockBed(EnumDyeColor.YELLOW, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("lime_bed", new BlockBed(EnumDyeColor.LIME, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("pink_bed", new BlockBed(EnumDyeColor.PINK, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("gray_bed", new BlockBed(EnumDyeColor.GRAY, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("light_gray_bed", new BlockBed(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("cyan_bed", new BlockBed(EnumDyeColor.CYAN, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("purple_bed", new BlockBed(EnumDyeColor.PURPLE, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("blue_bed", new BlockBed(EnumDyeColor.BLUE, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("brown_bed", new BlockBed(EnumDyeColor.BROWN, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("green_bed", new BlockBed(EnumDyeColor.GREEN, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("red_bed", new BlockBed(EnumDyeColor.RED, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("black_bed", new BlockBed(EnumDyeColor.BLACK, Block.Properties.func_200945_a(Material.field_151580_n).func_200947_a(SoundType.field_185848_a).func_200943_b(0.2F)));
      func_196254_a("powered_rail", new BlockRailPowered(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.7F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("detector_rail", new BlockRailDetector(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.7F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("sticky_piston", new BlockPistonBase(true, Block.Properties.func_200945_a(Material.field_76233_E).func_200943_b(0.5F)));
      func_196254_a("cobweb", new BlockWeb(Block.Properties.func_200945_a(Material.field_151569_G).func_200942_a().func_200943_b(4.0F)));
      BlockTallGrass var16 = new BlockTallGrass(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockTallGrass var17 = new BlockTallGrass(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockDeadBush var18 = new BlockDeadBush(Block.Properties.func_200949_a(Material.field_151582_l, MaterialColor.field_151663_o).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      func_196254_a("grass", var16);
      func_196254_a("fern", var17);
      func_196254_a("dead_bush", var18);
      BlockSeaGrass var19 = new BlockSeaGrass(Block.Properties.func_200945_a(Material.field_204868_h).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m));
      func_196254_a("seagrass", var19);
      func_196254_a("tall_seagrass", new BlockSeaGrassTall(var19, Block.Properties.func_200945_a(Material.field_204868_h).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("piston", new BlockPistonBase(false, Block.Properties.func_200945_a(Material.field_76233_E).func_200943_b(0.5F)));
      func_196254_a("piston_head", new BlockPistonExtension(Block.Properties.func_200945_a(Material.field_76233_E).func_200943_b(0.5F)));
      func_196254_a("white_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151666_j).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("orange_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151676_q).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("magenta_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151675_r).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("light_blue_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151674_s).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("yellow_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151673_t).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("lime_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151672_u).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("pink_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151671_v).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("gray_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151670_w).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("light_gray_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_197656_x).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("cyan_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151679_y).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("purple_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151678_z).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("blue_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151649_A).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("brown_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151650_B).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("green_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151651_C).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("red_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151645_D).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("black_wool", new Block(Block.Properties.func_200949_a(Material.field_151580_n, MaterialColor.field_151646_E).func_200943_b(0.8F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("moving_piston", new BlockPistonMoving(Block.Properties.func_200945_a(Material.field_76233_E).func_200943_b(-1.0F).func_208770_d()));
      BlockFlower var20 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var21 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var22 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var23 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var24 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var25 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var26 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var27 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var28 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      BlockFlower var29 = new BlockFlower(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c));
      func_196254_a("dandelion", var20);
      func_196254_a("poppy", var21);
      func_196254_a("blue_orchid", var22);
      func_196254_a("allium", var23);
      func_196254_a("azure_bluet", var24);
      func_196254_a("red_tulip", var25);
      func_196254_a("orange_tulip", var26);
      func_196254_a("white_tulip", var27);
      func_196254_a("pink_tulip", var28);
      func_196254_a("oxeye_daisy", var29);
      BlockMushroom var30 = new BlockMushroom(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c).func_200951_a(1));
      BlockMushroom var31 = new BlockMushroom(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c));
      func_196254_a("brown_mushroom", var30);
      func_196254_a("red_mushroom", var31);
      func_196254_a("gold_block", new Block(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151647_F).func_200948_a(3.0F, 6.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("iron_block", new Block(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h).func_200948_a(5.0F, 6.0F).func_200947_a(SoundType.field_185852_e)));
      Block var32 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151645_D).func_200948_a(2.0F, 6.0F));
      func_196254_a("bricks", var32);
      func_196254_a("tnt", new BlockTNT(Block.Properties.func_200945_a(Material.field_151590_u).func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("bookshelf", new BlockBookshelf(Block.Properties.func_200945_a(Material.field_151575_d).func_200943_b(1.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("mossy_cobblestone", new Block(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(2.0F, 6.0F)));
      func_196254_a("obsidian", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151646_E).func_200948_a(50.0F, 1200.0F)));
      func_196254_a("torch", new BlockTorch(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200946_b().func_200951_a(14).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("wall_torch", new BlockTorchWall(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200946_b().func_200951_a(14).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("fire", new BlockFire(Block.Properties.func_200949_a(Material.field_151581_o, MaterialColor.field_151656_f).func_200942_a().func_200944_c().func_200946_b().func_200951_a(15).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("spawner", new BlockMobSpawner(Block.Properties.func_200945_a(Material.field_151576_e).func_200943_b(5.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("oak_stairs", new BlockStairs(var3.func_176223_P(), Block.Properties.func_200950_a(var3)));
      func_196254_a("chest", new BlockChest(Block.Properties.func_200945_a(Material.field_151575_d).func_200943_b(2.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("redstone_wire", new BlockRedstoneWire(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200946_b()));
      func_196254_a("diamond_ore", new BlockOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(3.0F, 3.0F)));
      func_196254_a("diamond_block", new Block(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151648_G).func_200948_a(5.0F, 6.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("crafting_table", new BlockWorkbench(Block.Properties.func_200945_a(Material.field_151575_d).func_200943_b(2.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("wheat", new BlockCrops(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      BlockFarmland var33 = new BlockFarmland(Block.Properties.func_200945_a(Material.field_151578_c).func_200944_c().func_200943_b(0.6F).func_200947_a(SoundType.field_185849_b));
      func_196254_a("farmland", var33);
      func_196254_a("furnace", new BlockFurnace(Block.Properties.func_200945_a(Material.field_151576_e).func_200943_b(3.5F).func_200951_a(13)));
      func_196254_a("sign", new BlockStandingSign(Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("oak_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151575_d, var3.field_181083_K).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("ladder", new BlockLadder(Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(0.4F).func_200947_a(SoundType.field_185857_j)));
      func_196254_a("rail", new BlockRail(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.7F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("cobblestone_stairs", new BlockStairs(var2.func_176223_P(), Block.Properties.func_200950_a(var2)));
      func_196254_a("wall_sign", new BlockWallSign(Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("lever", new BlockLever(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stone_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.MOBS, Block.Properties.func_200945_a(Material.field_151576_e).func_200942_a().func_200943_b(0.5F)));
      func_196254_a("iron_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151668_h).func_200943_b(5.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("oak_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.func_200949_a(Material.field_151575_d, var3.field_181083_K).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.func_200949_a(Material.field_151575_d, var4.field_181083_K).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.func_200949_a(Material.field_151575_d, var5.field_181083_K).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.func_200949_a(Material.field_151575_d, var6.field_181083_K).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.func_200949_a(Material.field_151575_d, var7.field_181083_K).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_pressure_plate", new BlockPressurePlate(BlockPressurePlate.Sensitivity.EVERYTHING, Block.Properties.func_200949_a(Material.field_151575_d, var8.field_181083_K).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("redstone_ore", new BlockRedstoneOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200944_c().func_200951_a(9).func_200948_a(3.0F, 3.0F)));
      func_196254_a("redstone_torch", new BlockRedstoneTorch(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200946_b().func_200951_a(7).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("redstone_wall_torch", new BlockRedstoneTorchWall(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200946_b().func_200951_a(7).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stone_button", new BlockButtonStone(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F)));
      func_196254_a("snow", new BlockSnowLayer(Block.Properties.func_200945_a(Material.field_151597_y).func_200944_c().func_200943_b(0.1F).func_200947_a(SoundType.field_185856_i)));
      func_196254_a("ice", new BlockIce(Block.Properties.func_200945_a(Material.field_151588_w).func_200941_a(0.98F).func_200944_c().func_200943_b(0.5F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("snow_block", new BlockSnow(Block.Properties.func_200945_a(Material.field_151596_z).func_200944_c().func_200943_b(0.2F).func_200947_a(SoundType.field_185856_i)));
      BlockCactus var34 = new BlockCactus(Block.Properties.func_200945_a(Material.field_151570_A).func_200944_c().func_200943_b(0.4F).func_200947_a(SoundType.field_185854_g));
      func_196254_a("cactus", var34);
      func_196254_a("clay", new BlockClay(Block.Properties.func_200945_a(Material.field_151571_B).func_200943_b(0.6F).func_200947_a(SoundType.field_185849_b)));
      func_196254_a("sugar_cane", new BlockReed(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("jukebox", new BlockJukebox(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200948_a(2.0F, 6.0F)));
      func_196254_a("oak_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151575_d, var3.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      BlockPumpkin var35 = new BlockPumpkin(Block.Properties.func_200949_a(Material.field_151572_C, MaterialColor.field_151676_q).func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a));
      func_196254_a("pumpkin", var35);
      func_196254_a("netherrack", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200943_b(0.4F)));
      func_196254_a("soul_sand", new BlockSoulSand(Block.Properties.func_200949_a(Material.field_151595_p, MaterialColor.field_151650_B).func_200944_c().func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("glowstone", new BlockGlowstone(Block.Properties.func_200949_a(Material.field_151592_s, MaterialColor.field_151658_d).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f).func_200951_a(15)));
      func_196254_a("nether_portal", new BlockPortal(Block.Properties.func_200945_a(Material.field_151567_E).func_200942_a().func_200944_c().func_200943_b(-1.0F).func_200947_a(SoundType.field_185853_f).func_200951_a(11)));
      func_196254_a("carved_pumpkin", new BlockCarvedPumpkin(Block.Properties.func_200949_a(Material.field_151572_C, MaterialColor.field_151676_q).func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jack_o_lantern", new BlockCarvedPumpkin(Block.Properties.func_200949_a(Material.field_151572_C, MaterialColor.field_151676_q).func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a).func_200951_a(15)));
      func_196254_a("cake", new BlockCake(Block.Properties.func_200945_a(Material.field_151568_F).func_200943_b(0.5F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("repeater", new BlockRedstoneRepeater(Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b().func_200947_a(SoundType.field_185848_a)));
      func_196254_a("white_stained_glass", new BlockStainedGlass(EnumDyeColor.WHITE, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.WHITE).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("orange_stained_glass", new BlockStainedGlass(EnumDyeColor.ORANGE, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.ORANGE).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("magenta_stained_glass", new BlockStainedGlass(EnumDyeColor.MAGENTA, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.MAGENTA).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("light_blue_stained_glass", new BlockStainedGlass(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.LIGHT_BLUE).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("yellow_stained_glass", new BlockStainedGlass(EnumDyeColor.YELLOW, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.YELLOW).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("lime_stained_glass", new BlockStainedGlass(EnumDyeColor.LIME, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.LIME).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("pink_stained_glass", new BlockStainedGlass(EnumDyeColor.PINK, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.PINK).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("gray_stained_glass", new BlockStainedGlass(EnumDyeColor.GRAY, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.GRAY).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("light_gray_stained_glass", new BlockStainedGlass(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.LIGHT_GRAY).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("cyan_stained_glass", new BlockStainedGlass(EnumDyeColor.CYAN, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.CYAN).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("purple_stained_glass", new BlockStainedGlass(EnumDyeColor.PURPLE, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.PURPLE).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("blue_stained_glass", new BlockStainedGlass(EnumDyeColor.BLUE, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.BLUE).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("brown_stained_glass", new BlockStainedGlass(EnumDyeColor.BROWN, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.BROWN).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("green_stained_glass", new BlockStainedGlass(EnumDyeColor.GREEN, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.GREEN).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("red_stained_glass", new BlockStainedGlass(EnumDyeColor.RED, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.RED).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("black_stained_glass", new BlockStainedGlass(EnumDyeColor.BLACK, Block.Properties.func_200952_a(Material.field_151592_s, EnumDyeColor.BLACK).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("oak_trapdoor", new BlockTrapDoor(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151663_o).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_trapdoor", new BlockTrapDoor(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_trapdoor", new BlockTrapDoor(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151658_d).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_trapdoor", new BlockTrapDoor(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_trapdoor", new BlockTrapDoor(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151676_q).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_trapdoor", new BlockTrapDoor(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      Block var36 = new Block(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(1.5F, 6.0F));
      Block var37 = new Block(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(1.5F, 6.0F));
      Block var38 = new Block(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(1.5F, 6.0F));
      Block var39 = new Block(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(1.5F, 6.0F));
      func_196254_a("infested_stone", new BlockSilverfish(var1, Block.Properties.func_200945_a(Material.field_151571_B).func_200948_a(0.0F, 0.75F)));
      func_196254_a("infested_cobblestone", new BlockSilverfish(var2, Block.Properties.func_200945_a(Material.field_151571_B).func_200948_a(0.0F, 0.75F)));
      func_196254_a("infested_stone_bricks", new BlockSilverfish(var36, Block.Properties.func_200945_a(Material.field_151571_B).func_200948_a(0.0F, 0.75F)));
      func_196254_a("infested_mossy_stone_bricks", new BlockSilverfish(var37, Block.Properties.func_200945_a(Material.field_151571_B).func_200948_a(0.0F, 0.75F)));
      func_196254_a("infested_cracked_stone_bricks", new BlockSilverfish(var38, Block.Properties.func_200945_a(Material.field_151571_B).func_200948_a(0.0F, 0.75F)));
      func_196254_a("infested_chiseled_stone_bricks", new BlockSilverfish(var39, Block.Properties.func_200945_a(Material.field_151571_B).func_200948_a(0.0F, 0.75F)));
      func_196254_a("stone_bricks", var36);
      func_196254_a("mossy_stone_bricks", var37);
      func_196254_a("cracked_stone_bricks", var38);
      func_196254_a("chiseled_stone_bricks", var39);
      BlockHugeMushroom var40 = new BlockHugeMushroom(var30, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200943_b(0.2F).func_200947_a(SoundType.field_185848_a));
      func_196254_a("brown_mushroom_block", var40);
      BlockHugeMushroom var41 = new BlockHugeMushroom(var31, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151645_D).func_200943_b(0.2F).func_200947_a(SoundType.field_185848_a));
      func_196254_a("red_mushroom_block", var41);
      func_196254_a("mushroom_stem", new BlockHugeMushroom((Block)null, Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_193561_M).func_200943_b(0.2F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("iron_bars", new BlockPane(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151660_b).func_200948_a(5.0F, 6.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("glass_pane", new BlockGlassPane(Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      BlockMelon var42 = new BlockMelon(Block.Properties.func_200949_a(Material.field_151572_C, MaterialColor.field_151672_u).func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a));
      func_196254_a("melon", var42);
      func_196254_a("attached_pumpkin_stem", new BlockAttachedStem(var35, Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185848_a)));
      func_196254_a("attached_melon_stem", new BlockAttachedStem(var42, Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185848_a)));
      func_196254_a("pumpkin_stem", new BlockStem(var35, Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185848_a)));
      func_196254_a("melon_stem", new BlockStem(var42, Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185848_a)));
      func_196254_a("vine", new BlockVine(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200944_c().func_200943_b(0.2F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("oak_fence_gate", new BlockFenceGate(Block.Properties.func_200949_a(Material.field_151575_d, var3.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("brick_stairs", new BlockStairs(var32.func_176223_P(), Block.Properties.func_200950_a(var32)));
      func_196254_a("stone_brick_stairs", new BlockStairs(var36.func_176223_P(), Block.Properties.func_200950_a(var36)));
      func_196254_a("mycelium", new BlockMycelium(Block.Properties.func_200949_a(Material.field_151577_b, MaterialColor.field_151678_z).func_200944_c().func_200943_b(0.6F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("lily_pad", new BlockLilyPad(Block.Properties.func_200945_a(Material.field_151585_k).func_200946_b().func_200947_a(SoundType.field_185850_c)));
      Block var43 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200948_a(2.0F, 6.0F));
      func_196254_a("nether_bricks", var43);
      func_196254_a("nether_brick_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200948_a(2.0F, 6.0F)));
      func_196254_a("nether_brick_stairs", new BlockStairs(var43.func_176223_P(), Block.Properties.func_200950_a(var43)));
      func_196254_a("nether_wart", new BlockNetherWart(Block.Properties.func_200949_a(Material.field_151585_k, MaterialColor.field_151645_D).func_200942_a().func_200944_c()));
      func_196254_a("enchanting_table", new BlockEnchantmentTable(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151645_D).func_200948_a(5.0F, 1200.0F)));
      func_196254_a("brewing_stand", new BlockBrewingStand(Block.Properties.func_200945_a(Material.field_151573_f).func_200943_b(0.5F).func_200951_a(1)));
      func_196254_a("cauldron", new BlockCauldron(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151665_m).func_200943_b(2.0F)));
      func_196254_a("end_portal", new BlockEndPortal(Block.Properties.func_200949_a(Material.field_151567_E, MaterialColor.field_151646_E).func_200942_a().func_200951_a(15).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("end_portal_frame", new BlockEndPortalFrame(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151651_C).func_200947_a(SoundType.field_185853_f).func_200951_a(1).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("end_stone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200948_a(3.0F, 9.0F)));
      func_196254_a("dragon_egg", new BlockDragonEgg(Block.Properties.func_200949_a(Material.field_151566_D, MaterialColor.field_151646_E).func_200948_a(3.0F, 9.0F).func_200951_a(1)));
      func_196254_a("redstone_lamp", new BlockRedstoneLamp(Block.Properties.func_200945_a(Material.field_151591_t).func_200951_a(15).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("cocoa", new BlockCocoa(Block.Properties.func_200945_a(Material.field_151585_k).func_200944_c().func_200948_a(0.2F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("sandstone_stairs", new BlockStairs(var15.func_176223_P(), Block.Properties.func_200950_a(var15)));
      func_196254_a("emerald_ore", new BlockOre(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(3.0F, 3.0F)));
      func_196254_a("ender_chest", new BlockEnderChest(Block.Properties.func_200945_a(Material.field_151576_e).func_200948_a(22.5F, 600.0F).func_200951_a(7)));
      BlockTripWireHook var44 = new BlockTripWireHook(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a());
      func_196254_a("tripwire_hook", var44);
      func_196254_a("tripwire", new BlockTripWire(var44, Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a()));
      func_196254_a("emerald_block", new Block(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151653_I).func_200948_a(5.0F, 6.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("spruce_stairs", new BlockStairs(var4.func_176223_P(), Block.Properties.func_200950_a(var4)));
      func_196254_a("birch_stairs", new BlockStairs(var5.func_176223_P(), Block.Properties.func_200950_a(var5)));
      func_196254_a("jungle_stairs", new BlockStairs(var6.func_176223_P(), Block.Properties.func_200950_a(var6)));
      func_196254_a("command_block", new BlockCommandBlock(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151650_B).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("beacon", new BlockBeacon(Block.Properties.func_200949_a(Material.field_151592_s, MaterialColor.field_151648_G).func_200943_b(3.0F).func_200951_a(15)));
      func_196254_a("cobblestone_wall", new BlockWall(Block.Properties.func_200950_a(var2)));
      func_196254_a("mossy_cobblestone_wall", new BlockWall(Block.Properties.func_200950_a(var2)));
      func_196254_a("flower_pot", new BlockFlowerPot(var0, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_oak_sapling", new BlockFlowerPot(var9, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_spruce_sapling", new BlockFlowerPot(var10, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_birch_sapling", new BlockFlowerPot(var11, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_jungle_sapling", new BlockFlowerPot(var12, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_acacia_sapling", new BlockFlowerPot(var13, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_dark_oak_sapling", new BlockFlowerPot(var14, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_fern", new BlockFlowerPot(var17, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_dandelion", new BlockFlowerPot(var20, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_poppy", new BlockFlowerPot(var21, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_blue_orchid", new BlockFlowerPot(var22, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_allium", new BlockFlowerPot(var23, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_azure_bluet", new BlockFlowerPot(var24, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_red_tulip", new BlockFlowerPot(var25, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_orange_tulip", new BlockFlowerPot(var26, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_white_tulip", new BlockFlowerPot(var27, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_pink_tulip", new BlockFlowerPot(var28, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_oxeye_daisy", new BlockFlowerPot(var29, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_red_mushroom", new BlockFlowerPot(var31, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_brown_mushroom", new BlockFlowerPot(var30, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_dead_bush", new BlockFlowerPot(var18, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("potted_cactus", new BlockFlowerPot(var34, Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b()));
      func_196254_a("carrots", new BlockCarrot(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("potatoes", new BlockPotato(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("oak_button", new BlockButtonWood(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_button", new BlockButtonWood(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_button", new BlockButtonWood(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_button", new BlockButtonWood(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_button", new BlockButtonWood(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_button", new BlockButtonWood(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("skeleton_wall_skull", new BlockSkullWall(BlockSkull.Types.SKELETON, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("skeleton_skull", new BlockSkull(BlockSkull.Types.SKELETON, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("wither_skeleton_wall_skull", new BlockSkullWitherWall(Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("wither_skeleton_skull", new BlockSkullWither(Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("zombie_wall_head", new BlockSkullWall(BlockSkull.Types.ZOMBIE, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("zombie_head", new BlockSkull(BlockSkull.Types.ZOMBIE, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("player_wall_head", new BlockSkullWallPlayer(Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("player_head", new BlockSkullPlayer(Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("creeper_wall_head", new BlockSkullWall(BlockSkull.Types.CREEPER, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("creeper_head", new BlockSkull(BlockSkull.Types.CREEPER, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("dragon_wall_head", new BlockSkullWall(BlockSkull.Types.DRAGON, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("dragon_head", new BlockSkull(BlockSkull.Types.DRAGON, Block.Properties.func_200945_a(Material.field_151594_q).func_200943_b(1.0F)));
      func_196254_a("anvil", new BlockAnvil(Block.Properties.func_200949_a(Material.field_151574_g, MaterialColor.field_151668_h).func_200948_a(5.0F, 1200.0F).func_200947_a(SoundType.field_185858_k)));
      func_196254_a("chipped_anvil", new BlockAnvil(Block.Properties.func_200949_a(Material.field_151574_g, MaterialColor.field_151668_h).func_200948_a(5.0F, 1200.0F).func_200947_a(SoundType.field_185858_k)));
      func_196254_a("damaged_anvil", new BlockAnvil(Block.Properties.func_200949_a(Material.field_151574_g, MaterialColor.field_151668_h).func_200948_a(5.0F, 1200.0F).func_200947_a(SoundType.field_185858_k)));
      func_196254_a("trapped_chest", new BlockTrappedChest(Block.Properties.func_200945_a(Material.field_151575_d).func_200943_b(2.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("light_weighted_pressure_plate", new BlockPressurePlateWeighted(15, Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151647_F).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("heavy_weighted_pressure_plate", new BlockPressurePlateWeighted(150, Block.Properties.func_200945_a(Material.field_151573_f).func_200942_a().func_200943_b(0.5F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("comparator", new BlockRedstoneComparator(Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b().func_200947_a(SoundType.field_185848_a)));
      func_196254_a("daylight_detector", new BlockDaylightDetector(Block.Properties.func_200945_a(Material.field_151575_d).func_200943_b(0.2F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("redstone_block", new BlockRedstone(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151656_f).func_200948_a(5.0F, 6.0F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("nether_quartz_ore", new BlockOre(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200948_a(3.0F, 3.0F)));
      func_196254_a("hopper", new BlockHopper(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151665_m).func_200948_a(3.0F, 4.8F).func_200947_a(SoundType.field_185852_e)));
      Block var45 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200943_b(0.8F));
      func_196254_a("quartz_block", var45);
      func_196254_a("chiseled_quartz_block", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200943_b(0.8F)));
      func_196254_a("quartz_pillar", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200943_b(0.8F)));
      func_196254_a("quartz_stairs", new BlockStairs(var45.func_176223_P(), Block.Properties.func_200950_a(var45)));
      func_196254_a("activator_rail", new BlockRailPowered(Block.Properties.func_200945_a(Material.field_151594_q).func_200942_a().func_200943_b(0.7F).func_200947_a(SoundType.field_185852_e)));
      func_196254_a("dropper", new BlockDropper(Block.Properties.func_200945_a(Material.field_151576_e).func_200943_b(3.5F)));
      func_196254_a("white_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193561_M).func_200948_a(1.25F, 4.2F)));
      func_196254_a("orange_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193562_N).func_200948_a(1.25F, 4.2F)));
      func_196254_a("magenta_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193563_O).func_200948_a(1.25F, 4.2F)));
      func_196254_a("light_blue_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193564_P).func_200948_a(1.25F, 4.2F)));
      func_196254_a("yellow_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193565_Q).func_200948_a(1.25F, 4.2F)));
      func_196254_a("lime_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193566_R).func_200948_a(1.25F, 4.2F)));
      func_196254_a("pink_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193567_S).func_200948_a(1.25F, 4.2F)));
      func_196254_a("gray_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193568_T).func_200948_a(1.25F, 4.2F)));
      func_196254_a("light_gray_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_197655_T).func_200948_a(1.25F, 4.2F)));
      func_196254_a("cyan_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193570_V).func_200948_a(1.25F, 4.2F)));
      func_196254_a("purple_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193571_W).func_200948_a(1.25F, 4.2F)));
      func_196254_a("blue_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193572_X).func_200948_a(1.25F, 4.2F)));
      func_196254_a("brown_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193573_Y).func_200948_a(1.25F, 4.2F)));
      func_196254_a("green_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193574_Z).func_200948_a(1.25F, 4.2F)));
      func_196254_a("red_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193559_aa).func_200948_a(1.25F, 4.2F)));
      func_196254_a("black_terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193560_ab).func_200948_a(1.25F, 4.2F)));
      func_196254_a("white_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.WHITE, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("orange_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.ORANGE, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("magenta_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.MAGENTA, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("light_blue_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("yellow_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.YELLOW, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("lime_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIME, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("pink_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.PINK, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("gray_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.GRAY, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("light_gray_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("cyan_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.CYAN, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("purple_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.PURPLE, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("blue_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BLUE, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("brown_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BROWN, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("green_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.GREEN, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("red_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.RED, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("black_stained_glass_pane", new BlockStainedGlassPane(EnumDyeColor.BLACK, Block.Properties.func_200945_a(Material.field_151592_s).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("acacia_stairs", new BlockStairs(var7.func_176223_P(), Block.Properties.func_200950_a(var7)));
      func_196254_a("dark_oak_stairs", new BlockStairs(var8.func_176223_P(), Block.Properties.func_200950_a(var8)));
      func_196254_a("slime_block", new BlockSlime(Block.Properties.func_200949_a(Material.field_151571_B, MaterialColor.field_151661_c).func_200941_a(0.8F).func_200947_a(SoundType.field_185859_l)));
      func_196254_a("barrier", new BlockBarrier(Block.Properties.func_200945_a(Material.field_175972_I).func_200948_a(-1.0F, 3600000.8F)));
      func_196254_a("iron_trapdoor", new BlockTrapDoor(Block.Properties.func_200945_a(Material.field_151573_f).func_200943_b(5.0F).func_200947_a(SoundType.field_185852_e)));
      Block var46 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151679_y).func_200948_a(1.5F, 6.0F));
      func_196254_a("prismarine", var46);
      Block var47 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G).func_200948_a(1.5F, 6.0F));
      func_196254_a("prismarine_bricks", var47);
      Block var48 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G).func_200948_a(1.5F, 6.0F));
      func_196254_a("dark_prismarine", var48);
      func_196254_a("prismarine_stairs", new BlockStairs(var46.func_176223_P(), Block.Properties.func_200950_a(var46)));
      func_196254_a("prismarine_brick_stairs", new BlockStairs(var47.func_176223_P(), Block.Properties.func_200950_a(var47)));
      func_196254_a("dark_prismarine_stairs", new BlockStairs(var48.func_176223_P(), Block.Properties.func_200950_a(var48)));
      func_196254_a("prismarine_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151679_y).func_200948_a(1.5F, 6.0F)));
      func_196254_a("prismarine_brick_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G).func_200948_a(1.5F, 6.0F)));
      func_196254_a("dark_prismarine_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151648_G).func_200948_a(1.5F, 6.0F)));
      func_196254_a("sea_lantern", new BlockSeaLantern(Block.Properties.func_200949_a(Material.field_151592_s, MaterialColor.field_151677_p).func_200943_b(0.3F).func_200947_a(SoundType.field_185853_f).func_200951_a(15)));
      func_196254_a("hay_block", new BlockHay(Block.Properties.func_200949_a(Material.field_151577_b, MaterialColor.field_151673_t).func_200943_b(0.5F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("white_carpet", new BlockCarpet(EnumDyeColor.WHITE, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151666_j).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("orange_carpet", new BlockCarpet(EnumDyeColor.ORANGE, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151676_q).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("magenta_carpet", new BlockCarpet(EnumDyeColor.MAGENTA, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151675_r).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("light_blue_carpet", new BlockCarpet(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151674_s).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("yellow_carpet", new BlockCarpet(EnumDyeColor.YELLOW, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151673_t).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("lime_carpet", new BlockCarpet(EnumDyeColor.LIME, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151672_u).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("pink_carpet", new BlockCarpet(EnumDyeColor.PINK, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151671_v).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("gray_carpet", new BlockCarpet(EnumDyeColor.GRAY, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151670_w).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("light_gray_carpet", new BlockCarpet(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_197656_x).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("cyan_carpet", new BlockCarpet(EnumDyeColor.CYAN, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151679_y).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("purple_carpet", new BlockCarpet(EnumDyeColor.PURPLE, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151678_z).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("blue_carpet", new BlockCarpet(EnumDyeColor.BLUE, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151649_A).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("brown_carpet", new BlockCarpet(EnumDyeColor.BROWN, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151650_B).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("green_carpet", new BlockCarpet(EnumDyeColor.GREEN, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151651_C).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("red_carpet", new BlockCarpet(EnumDyeColor.RED, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151645_D).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("black_carpet", new BlockCarpet(EnumDyeColor.BLACK, Block.Properties.func_200949_a(Material.field_151593_r, MaterialColor.field_151646_E).func_200943_b(0.1F).func_200947_a(SoundType.field_185854_g)));
      func_196254_a("terracotta", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200948_a(1.25F, 4.2F)));
      func_196254_a("coal_block", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151646_E).func_200948_a(5.0F, 6.0F)));
      func_196254_a("packed_ice", new BlockPackedIce(Block.Properties.func_200945_a(Material.field_151598_x).func_200941_a(0.98F).func_200943_b(0.5F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("sunflower", new BlockTallFlower(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("lilac", new BlockTallFlower(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("rose_bush", new BlockTallFlower(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("peony", new BlockTallFlower(Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("tall_grass", new BlockShearableDoublePlant(var16, Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("large_fern", new BlockShearableDoublePlant(var17, Block.Properties.func_200945_a(Material.field_151582_l).func_200942_a().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      func_196254_a("white_banner", new BlockBanner(EnumDyeColor.WHITE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("orange_banner", new BlockBanner(EnumDyeColor.ORANGE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("magenta_banner", new BlockBanner(EnumDyeColor.MAGENTA, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("light_blue_banner", new BlockBanner(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("yellow_banner", new BlockBanner(EnumDyeColor.YELLOW, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("lime_banner", new BlockBanner(EnumDyeColor.LIME, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("pink_banner", new BlockBanner(EnumDyeColor.PINK, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("gray_banner", new BlockBanner(EnumDyeColor.GRAY, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("light_gray_banner", new BlockBanner(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("cyan_banner", new BlockBanner(EnumDyeColor.CYAN, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("purple_banner", new BlockBanner(EnumDyeColor.PURPLE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("blue_banner", new BlockBanner(EnumDyeColor.BLUE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("brown_banner", new BlockBanner(EnumDyeColor.BROWN, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("green_banner", new BlockBanner(EnumDyeColor.GREEN, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("red_banner", new BlockBanner(EnumDyeColor.RED, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("black_banner", new BlockBanner(EnumDyeColor.BLACK, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("white_wall_banner", new BlockBannerWall(EnumDyeColor.WHITE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("orange_wall_banner", new BlockBannerWall(EnumDyeColor.ORANGE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("magenta_wall_banner", new BlockBannerWall(EnumDyeColor.MAGENTA, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("light_blue_wall_banner", new BlockBannerWall(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("yellow_wall_banner", new BlockBannerWall(EnumDyeColor.YELLOW, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("lime_wall_banner", new BlockBannerWall(EnumDyeColor.LIME, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("pink_wall_banner", new BlockBannerWall(EnumDyeColor.PINK, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("gray_wall_banner", new BlockBannerWall(EnumDyeColor.GRAY, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("light_gray_wall_banner", new BlockBannerWall(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("cyan_wall_banner", new BlockBannerWall(EnumDyeColor.CYAN, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("purple_wall_banner", new BlockBannerWall(EnumDyeColor.PURPLE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("blue_wall_banner", new BlockBannerWall(EnumDyeColor.BLUE, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("brown_wall_banner", new BlockBannerWall(EnumDyeColor.BROWN, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("green_wall_banner", new BlockBannerWall(EnumDyeColor.GREEN, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("red_wall_banner", new BlockBannerWall(EnumDyeColor.RED, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("black_wall_banner", new BlockBannerWall(EnumDyeColor.BLACK, Block.Properties.func_200945_a(Material.field_151575_d).func_200942_a().func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      Block var49 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200943_b(0.8F));
      func_196254_a("red_sandstone", var49);
      func_196254_a("chiseled_red_sandstone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200943_b(0.8F)));
      func_196254_a("cut_red_sandstone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200943_b(0.8F)));
      func_196254_a("red_sandstone_stairs", new BlockStairs(var49.func_176223_P(), Block.Properties.func_200950_a(var49)));
      func_196254_a("oak_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151663_o).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151654_J).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151658_d).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151664_l).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151676_q).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151575_d, MaterialColor.field_151650_B).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("stone_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(2.0F, 6.0F)));
      func_196254_a("sandstone_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200948_a(2.0F, 6.0F)));
      func_196254_a("petrified_oak_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151663_o).func_200948_a(2.0F, 6.0F)));
      func_196254_a("cobblestone_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(2.0F, 6.0F)));
      func_196254_a("brick_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151645_D).func_200948_a(2.0F, 6.0F)));
      func_196254_a("stone_brick_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(2.0F, 6.0F)));
      func_196254_a("nether_brick_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200948_a(2.0F, 6.0F)));
      func_196254_a("quartz_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200948_a(2.0F, 6.0F)));
      func_196254_a("red_sandstone_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200948_a(2.0F, 6.0F)));
      func_196254_a("purpur_slab", new BlockSlab(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151675_r).func_200948_a(2.0F, 6.0F)));
      func_196254_a("smooth_stone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(2.0F, 6.0F)));
      func_196254_a("smooth_sandstone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200948_a(2.0F, 6.0F)));
      func_196254_a("smooth_quartz", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151677_p).func_200948_a(2.0F, 6.0F)));
      func_196254_a("smooth_red_sandstone", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200948_a(2.0F, 6.0F)));
      func_196254_a("spruce_fence_gate", new BlockFenceGate(Block.Properties.func_200949_a(Material.field_151575_d, var4.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_fence_gate", new BlockFenceGate(Block.Properties.func_200949_a(Material.field_151575_d, var5.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_fence_gate", new BlockFenceGate(Block.Properties.func_200949_a(Material.field_151575_d, var6.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_fence_gate", new BlockFenceGate(Block.Properties.func_200949_a(Material.field_151575_d, var7.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_fence_gate", new BlockFenceGate(Block.Properties.func_200949_a(Material.field_151575_d, var8.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151575_d, var4.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151575_d, var5.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151575_d, var6.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151575_d, var7.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_fence", new BlockFence(Block.Properties.func_200949_a(Material.field_151575_d, var8.field_181083_K).func_200948_a(2.0F, 3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("spruce_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151575_d, var4.field_181083_K).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("birch_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151575_d, var5.field_181083_K).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("jungle_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151575_d, var6.field_181083_K).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("acacia_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151575_d, var7.field_181083_K).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("dark_oak_door", new BlockDoor(Block.Properties.func_200949_a(Material.field_151575_d, var8.field_181083_K).func_200943_b(3.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("end_rod", new BlockEndRod(Block.Properties.func_200945_a(Material.field_151594_q).func_200946_b().func_200951_a(14).func_200947_a(SoundType.field_185848_a)));
      BlockChorusPlant var50 = new BlockChorusPlant(Block.Properties.func_200949_a(Material.field_151585_k, MaterialColor.field_151678_z).func_200943_b(0.4F).func_200947_a(SoundType.field_185848_a));
      func_196254_a("chorus_plant", var50);
      func_196254_a("chorus_flower", new BlockChorusFlower(var50, Block.Properties.func_200949_a(Material.field_151585_k, MaterialColor.field_151678_z).func_200944_c().func_200943_b(0.4F).func_200947_a(SoundType.field_185848_a)));
      Block var51 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151675_r).func_200948_a(1.5F, 6.0F));
      func_196254_a("purpur_block", var51);
      func_196254_a("purpur_pillar", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151675_r).func_200948_a(1.5F, 6.0F)));
      func_196254_a("purpur_stairs", new BlockStairs(var51.func_176223_P(), Block.Properties.func_200950_a(var51)));
      func_196254_a("end_stone_bricks", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200943_b(0.8F)));
      func_196254_a("beetroots", new BlockBeetroot(Block.Properties.func_200945_a(Material.field_151585_k).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_185850_c)));
      BlockGrassPath var52 = new BlockGrassPath(Block.Properties.func_200945_a(Material.field_151578_c).func_200943_b(0.65F).func_200947_a(SoundType.field_185850_c));
      func_196254_a("grass_path", var52);
      func_196254_a("end_gateway", new BlockEndGateway(Block.Properties.func_200949_a(Material.field_151567_E, MaterialColor.field_151646_E).func_200942_a().func_200951_a(15).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("repeating_command_block", new BlockCommandBlock(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151678_z).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("chain_command_block", new BlockCommandBlock(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_151651_C).func_200948_a(-1.0F, 3600000.0F)));
      func_196254_a("frosted_ice", new BlockFrostedIce(Block.Properties.func_200945_a(Material.field_151588_w).func_200941_a(0.98F).func_200944_c().func_200943_b(0.5F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("magma_block", new BlockMagma(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200951_a(3).func_200944_c().func_200943_b(0.5F)));
      func_196254_a("nether_wart_block", new Block(Block.Properties.func_200949_a(Material.field_151577_b, MaterialColor.field_151645_D).func_200943_b(1.0F).func_200947_a(SoundType.field_185848_a)));
      func_196254_a("red_nether_bricks", new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151655_K).func_200948_a(2.0F, 6.0F)));
      func_196254_a("bone_block", new BlockRotatedPillar(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151658_d).func_200943_b(2.0F)));
      func_196254_a("structure_void", new BlockStructureVoid(Block.Properties.func_200945_a(Material.field_189963_J).func_200942_a()));
      func_196254_a("observer", new BlockObserver(Block.Properties.func_200945_a(Material.field_151576_e).func_200943_b(3.0F)));
      func_196254_a("shulker_box", new BlockShulkerBox((EnumDyeColor)null, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151678_z).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("white_shulker_box", new BlockShulkerBox(EnumDyeColor.WHITE, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151666_j).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("orange_shulker_box", new BlockShulkerBox(EnumDyeColor.ORANGE, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151676_q).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("magenta_shulker_box", new BlockShulkerBox(EnumDyeColor.MAGENTA, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151675_r).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("light_blue_shulker_box", new BlockShulkerBox(EnumDyeColor.LIGHT_BLUE, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151674_s).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("yellow_shulker_box", new BlockShulkerBox(EnumDyeColor.YELLOW, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151673_t).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("lime_shulker_box", new BlockShulkerBox(EnumDyeColor.LIME, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151672_u).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("pink_shulker_box", new BlockShulkerBox(EnumDyeColor.PINK, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151671_v).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("gray_shulker_box", new BlockShulkerBox(EnumDyeColor.GRAY, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("light_gray_shulker_box", new BlockShulkerBox(EnumDyeColor.LIGHT_GRAY, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_197656_x).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("cyan_shulker_box", new BlockShulkerBox(EnumDyeColor.CYAN, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151679_y).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("purple_shulker_box", new BlockShulkerBox(EnumDyeColor.PURPLE, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_193571_W).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("blue_shulker_box", new BlockShulkerBox(EnumDyeColor.BLUE, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151649_A).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("brown_shulker_box", new BlockShulkerBox(EnumDyeColor.BROWN, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151650_B).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("green_shulker_box", new BlockShulkerBox(EnumDyeColor.GREEN, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151651_C).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("red_shulker_box", new BlockShulkerBox(EnumDyeColor.RED, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151645_D).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("black_shulker_box", new BlockShulkerBox(EnumDyeColor.BLACK, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151646_E).func_200943_b(2.0F).func_208770_d()));
      func_196254_a("white_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.WHITE).func_200943_b(1.4F)));
      func_196254_a("orange_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.ORANGE).func_200943_b(1.4F)));
      func_196254_a("magenta_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.MAGENTA).func_200943_b(1.4F)));
      func_196254_a("light_blue_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.LIGHT_BLUE).func_200943_b(1.4F)));
      func_196254_a("yellow_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.YELLOW).func_200943_b(1.4F)));
      func_196254_a("lime_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.LIME).func_200943_b(1.4F)));
      func_196254_a("pink_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.PINK).func_200943_b(1.4F)));
      func_196254_a("gray_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.GRAY).func_200943_b(1.4F)));
      func_196254_a("light_gray_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.LIGHT_GRAY).func_200943_b(1.4F)));
      func_196254_a("cyan_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.CYAN).func_200943_b(1.4F)));
      func_196254_a("purple_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.PURPLE).func_200943_b(1.4F)));
      func_196254_a("blue_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.BLUE).func_200943_b(1.4F)));
      func_196254_a("brown_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.BROWN).func_200943_b(1.4F)));
      func_196254_a("green_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.GREEN).func_200943_b(1.4F)));
      func_196254_a("red_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.RED).func_200943_b(1.4F)));
      func_196254_a("black_glazed_terracotta", new BlockGlazedTerracotta(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.BLACK).func_200943_b(1.4F)));
      Block var53 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.WHITE).func_200943_b(1.8F));
      Block var54 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.ORANGE).func_200943_b(1.8F));
      Block var55 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.MAGENTA).func_200943_b(1.8F));
      Block var56 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.LIGHT_BLUE).func_200943_b(1.8F));
      Block var57 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.YELLOW).func_200943_b(1.8F));
      Block var58 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.LIME).func_200943_b(1.8F));
      Block var59 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.PINK).func_200943_b(1.8F));
      Block var60 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.GRAY).func_200943_b(1.8F));
      Block var61 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.LIGHT_GRAY).func_200943_b(1.8F));
      Block var62 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.CYAN).func_200943_b(1.8F));
      Block var63 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.PURPLE).func_200943_b(1.8F));
      Block var64 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.BLUE).func_200943_b(1.8F));
      Block var65 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.BROWN).func_200943_b(1.8F));
      Block var66 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.GREEN).func_200943_b(1.8F));
      Block var67 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.RED).func_200943_b(1.8F));
      Block var68 = new Block(Block.Properties.func_200952_a(Material.field_151576_e, EnumDyeColor.BLACK).func_200943_b(1.8F));
      func_196254_a("white_concrete", var53);
      func_196254_a("orange_concrete", var54);
      func_196254_a("magenta_concrete", var55);
      func_196254_a("light_blue_concrete", var56);
      func_196254_a("yellow_concrete", var57);
      func_196254_a("lime_concrete", var58);
      func_196254_a("pink_concrete", var59);
      func_196254_a("gray_concrete", var60);
      func_196254_a("light_gray_concrete", var61);
      func_196254_a("cyan_concrete", var62);
      func_196254_a("purple_concrete", var63);
      func_196254_a("blue_concrete", var64);
      func_196254_a("brown_concrete", var65);
      func_196254_a("green_concrete", var66);
      func_196254_a("red_concrete", var67);
      func_196254_a("black_concrete", var68);
      func_196254_a("white_concrete_powder", new BlockConcretePowder(var53, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.WHITE).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("orange_concrete_powder", new BlockConcretePowder(var54, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.ORANGE).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("magenta_concrete_powder", new BlockConcretePowder(var55, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.MAGENTA).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("light_blue_concrete_powder", new BlockConcretePowder(var56, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.LIGHT_BLUE).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("yellow_concrete_powder", new BlockConcretePowder(var57, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.YELLOW).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("lime_concrete_powder", new BlockConcretePowder(var58, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.LIME).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("pink_concrete_powder", new BlockConcretePowder(var59, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.PINK).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("gray_concrete_powder", new BlockConcretePowder(var60, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.GRAY).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("light_gray_concrete_powder", new BlockConcretePowder(var61, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.LIGHT_GRAY).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("cyan_concrete_powder", new BlockConcretePowder(var62, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.CYAN).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("purple_concrete_powder", new BlockConcretePowder(var63, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.PURPLE).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("blue_concrete_powder", new BlockConcretePowder(var64, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.BLUE).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("brown_concrete_powder", new BlockConcretePowder(var65, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.BROWN).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("green_concrete_powder", new BlockConcretePowder(var66, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.GREEN).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("red_concrete_powder", new BlockConcretePowder(var67, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.RED).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      func_196254_a("black_concrete_powder", new BlockConcretePowder(var68, Block.Properties.func_200952_a(Material.field_151595_p, EnumDyeColor.BLACK).func_200943_b(0.5F).func_200947_a(SoundType.field_185855_h)));
      BlockKelpTop var69 = new BlockKelpTop(Block.Properties.func_200945_a(Material.field_203243_f).func_200942_a().func_200944_c().func_200946_b().func_200947_a(SoundType.field_211382_m));
      func_196254_a("kelp", var69);
      func_196254_a("kelp_plant", new BlockKelp(var69, Block.Properties.func_200945_a(Material.field_203243_f).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("dried_kelp_block", new Block(Block.Properties.func_200949_a(Material.field_151577_b, MaterialColor.field_151650_B).func_200948_a(0.5F, 2.5F).func_200947_a(SoundType.field_185850_c)));
      func_196254_a("turtle_egg", new BlockTurtleEgg(Block.Properties.func_200949_a(Material.field_151566_D, MaterialColor.field_197656_x).func_200943_b(0.5F).func_200947_a(SoundType.field_185852_e).func_200944_c()));
      Block var70 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200948_a(1.5F, 6.0F));
      Block var71 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200948_a(1.5F, 6.0F));
      Block var72 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200948_a(1.5F, 6.0F));
      Block var73 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200948_a(1.5F, 6.0F));
      Block var74 = new Block(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200948_a(1.5F, 6.0F));
      func_196254_a("dead_tube_coral_block", var70);
      func_196254_a("dead_brain_coral_block", var71);
      func_196254_a("dead_bubble_coral_block", var72);
      func_196254_a("dead_fire_coral_block", var73);
      func_196254_a("dead_horn_coral_block", var74);
      func_196254_a("tube_coral_block", new BlockCoral(var70, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151649_A).func_200948_a(1.5F, 6.0F).func_200947_a(SoundType.field_211383_n)));
      func_196254_a("brain_coral_block", new BlockCoral(var71, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151671_v).func_200948_a(1.5F, 6.0F).func_200947_a(SoundType.field_211383_n)));
      func_196254_a("bubble_coral_block", new BlockCoral(var72, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151678_z).func_200948_a(1.5F, 6.0F).func_200947_a(SoundType.field_211383_n)));
      func_196254_a("fire_coral_block", new BlockCoral(var73, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151645_D).func_200948_a(1.5F, 6.0F).func_200947_a(SoundType.field_211383_n)));
      func_196254_a("horn_coral_block", new BlockCoral(var74, Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151673_t).func_200948_a(1.5F, 6.0F).func_200947_a(SoundType.field_211383_n)));
      BlockCoralPlantDead var75 = new BlockCoralPlantDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralPlantDead var76 = new BlockCoralPlantDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralPlantDead var77 = new BlockCoralPlantDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralPlantDead var78 = new BlockCoralPlantDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralPlantDead var79 = new BlockCoralPlantDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      func_196254_a("dead_tube_coral", var75);
      func_196254_a("dead_brain_coral", var76);
      func_196254_a("dead_bubble_coral", var77);
      func_196254_a("dead_fire_coral", var78);
      func_196254_a("dead_horn_coral", var79);
      func_196254_a("tube_coral", new BlockCoralPlant(var75, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151649_A).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("brain_coral", new BlockCoralPlant(var76, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151671_v).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("bubble_coral", new BlockCoralPlant(var77, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151678_z).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("fire_coral", new BlockCoralPlant(var78, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151645_D).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("horn_coral", new BlockCoralPlant(var79, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151673_t).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      BlockCoralWallFanDead var80 = new BlockCoralWallFanDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralWallFanDead var81 = new BlockCoralWallFanDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralWallFanDead var82 = new BlockCoralWallFanDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralWallFanDead var83 = new BlockCoralWallFanDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralWallFanDead var84 = new BlockCoralWallFanDead(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      func_196254_a("dead_tube_coral_wall_fan", var80);
      func_196254_a("dead_brain_coral_wall_fan", var81);
      func_196254_a("dead_bubble_coral_wall_fan", var82);
      func_196254_a("dead_fire_coral_wall_fan", var83);
      func_196254_a("dead_horn_coral_wall_fan", var84);
      func_196254_a("tube_coral_wall_fan", new BlockCoralWallFan(var80, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151649_A).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("brain_coral_wall_fan", new BlockCoralWallFan(var81, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151671_v).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("bubble_coral_wall_fan", new BlockCoralWallFan(var82, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151678_z).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("fire_coral_wall_fan", new BlockCoralWallFan(var83, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151645_D).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("horn_coral_wall_fan", new BlockCoralWallFan(var84, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151673_t).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      BlockCoralFan var85 = new BlockCoralFan(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralFan var86 = new BlockCoralFan(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralFan var87 = new BlockCoralFan(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralFan var88 = new BlockCoralFan(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      BlockCoralFan var89 = new BlockCoralFan(Block.Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151670_w).func_200942_a().func_200946_b());
      func_196254_a("dead_tube_coral_fan", var85);
      func_196254_a("dead_brain_coral_fan", var86);
      func_196254_a("dead_bubble_coral_fan", var87);
      func_196254_a("dead_fire_coral_fan", var88);
      func_196254_a("dead_horn_coral_fan", var89);
      func_196254_a("tube_coral_fan", new BlockCoralFin(var85, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151649_A).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("brain_coral_fan", new BlockCoralFin(var86, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151671_v).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("bubble_coral_fan", new BlockCoralFin(var87, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151678_z).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("fire_coral_fan", new BlockCoralFin(var88, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151645_D).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("horn_coral_fan", new BlockCoralFin(var89, Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151673_t).func_200942_a().func_200946_b().func_200947_a(SoundType.field_211382_m)));
      func_196254_a("sea_pickle", new BlockSeaPickle(Block.Properties.func_200949_a(Material.field_203243_f, MaterialColor.field_151651_C).func_200951_a(3).func_200947_a(SoundType.field_185859_l)));
      func_196254_a("blue_ice", new BlockBlueIce(Block.Properties.func_200945_a(Material.field_151598_x).func_200943_b(2.8F).func_200941_a(0.989F).func_200947_a(SoundType.field_185853_f)));
      func_196254_a("conduit", new BlockConduit(Block.Properties.func_200949_a(Material.field_151592_s, MaterialColor.field_151648_G).func_200943_b(3.0F).func_200951_a(15)));
      func_196254_a("void_air", new BlockAir(Block.Properties.func_200945_a(Material.field_151579_a).func_200942_a()));
      func_196254_a("cave_air", new BlockAir(Block.Properties.func_200945_a(Material.field_151579_a).func_200942_a()));
      func_196254_a("bubble_column", new BlockBubbleColumn(Block.Properties.func_200945_a(Material.field_203244_i).func_200942_a()));
      func_196254_a("structure_block", new BlockStructure(Block.Properties.func_200949_a(Material.field_151573_f, MaterialColor.field_197656_x).func_200948_a(-1.0F, 3600000.0F)));
      Iterator var90 = IRegistry.field_212618_g.iterator();

      while(var90.hasNext()) {
         Block var91 = (Block)var90.next();
         UnmodifiableIterator var92 = var91.func_176194_O().func_177619_a().iterator();

         while(var92.hasNext()) {
            IBlockState var93 = (IBlockState)var92.next();
            field_176229_d.func_195867_b(var93);
         }
      }

   }

   private static void func_196249_a(ResourceLocation var0, Block var1) {
      IRegistry.field_212618_g.func_82595_a(var0, var1);
   }

   private static void func_196254_a(String var0, Block var1) {
      func_196249_a(new ResourceLocation(var0), var1);
   }

   static {
      field_212556_a = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.DOWN, EnumFacing.UP};
      field_210300_r = ThreadLocal.withInitial(() -> {
         Object2ByteLinkedOpenHashMap var0 = new Object2ByteLinkedOpenHashMap<Block.RenderSideCacheKey>(200) {
            protected void rehash(int var1) {
            }
         };
         var0.defaultReturnValue((byte)127);
         return var0;
      });
   }

   public static enum EnumOffsetType {
      NONE,
      XZ,
      XYZ;

      private EnumOffsetType() {
      }
   }

   public static class Properties {
      private Material field_200953_a;
      private MaterialColor field_200954_b;
      private boolean field_200955_c = true;
      private SoundType field_200956_d;
      private int field_200957_e;
      private float field_200958_f;
      private float field_200959_g;
      private boolean field_200960_h;
      private float field_200961_i;
      private boolean field_208772_j;

      private Properties(Material var1, MaterialColor var2) {
         super();
         this.field_200956_d = SoundType.field_185851_d;
         this.field_200961_i = 0.6F;
         this.field_200953_a = var1;
         this.field_200954_b = var2;
      }

      public static Block.Properties func_200945_a(Material var0) {
         return func_200949_a(var0, var0.func_151565_r());
      }

      public static Block.Properties func_200952_a(Material var0, EnumDyeColor var1) {
         return func_200949_a(var0, var1.func_196055_e());
      }

      public static Block.Properties func_200949_a(Material var0, MaterialColor var1) {
         return new Block.Properties(var0, var1);
      }

      public static Block.Properties func_200950_a(Block var0) {
         Block.Properties var1 = new Block.Properties(var0.field_149764_J, var0.field_181083_K);
         var1.field_200953_a = var0.field_149764_J;
         var1.field_200959_g = var0.field_149782_v;
         var1.field_200958_f = var0.field_149781_w;
         var1.field_200955_c = var0.field_196274_w;
         var1.field_200960_h = var0.field_149789_z;
         var1.field_200957_e = var0.field_149784_t;
         var1.field_200953_a = var0.field_149764_J;
         var1.field_200954_b = var0.field_181083_K;
         var1.field_200956_d = var0.field_149762_H;
         var1.field_200961_i = var0.func_208618_m();
         var1.field_208772_j = var0.field_208621_p;
         return var1;
      }

      public Block.Properties func_200942_a() {
         this.field_200955_c = false;
         return this;
      }

      public Block.Properties func_200941_a(float var1) {
         this.field_200961_i = var1;
         return this;
      }

      protected Block.Properties func_200947_a(SoundType var1) {
         this.field_200956_d = var1;
         return this;
      }

      protected Block.Properties func_200951_a(int var1) {
         this.field_200957_e = var1;
         return this;
      }

      public Block.Properties func_200948_a(float var1, float var2) {
         this.field_200959_g = var1;
         this.field_200958_f = Math.max(0.0F, var2);
         return this;
      }

      protected Block.Properties func_200946_b() {
         return this.func_200943_b(0.0F);
      }

      protected Block.Properties func_200943_b(float var1) {
         this.func_200948_a(var1, var1);
         return this;
      }

      protected Block.Properties func_200944_c() {
         this.field_200960_h = true;
         return this;
      }

      protected Block.Properties func_208770_d() {
         this.field_208772_j = true;
         return this;
      }
   }

   public static final class RenderSideCacheKey {
      private final IBlockState field_212164_a;
      private final IBlockState field_212165_b;
      private final EnumFacing field_212166_c;

      public RenderSideCacheKey(IBlockState var1, IBlockState var2, EnumFacing var3) {
         super();
         this.field_212164_a = var1;
         this.field_212165_b = var2;
         this.field_212166_c = var3;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (!(var1 instanceof Block.RenderSideCacheKey)) {
            return false;
         } else {
            Block.RenderSideCacheKey var2 = (Block.RenderSideCacheKey)var1;
            return this.field_212164_a == var2.field_212164_a && this.field_212165_b == var2.field_212165_b && this.field_212166_c == var2.field_212166_c;
         }
      }

      public int hashCode() {
         return Objects.hash(new Object[]{this.field_212164_a, this.field_212165_b, this.field_212166_c});
      }
   }
}
