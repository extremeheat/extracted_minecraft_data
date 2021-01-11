package net.minecraft.block;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespacedDefaultedByKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class Block {
   private static final ResourceLocation field_176230_a = new ResourceLocation("air");
   public static final RegistryNamespacedDefaultedByKey<ResourceLocation, Block> field_149771_c;
   public static final ObjectIntIdentityMap<IBlockState> field_176229_d;
   private CreativeTabs field_149772_a;
   public static final Block.SoundType field_149769_e;
   public static final Block.SoundType field_149766_f;
   public static final Block.SoundType field_149767_g;
   public static final Block.SoundType field_149779_h;
   public static final Block.SoundType field_149780_i;
   public static final Block.SoundType field_149777_j;
   public static final Block.SoundType field_149778_k;
   public static final Block.SoundType field_149775_l;
   public static final Block.SoundType field_149776_m;
   public static final Block.SoundType field_149773_n;
   public static final Block.SoundType field_149774_o;
   public static final Block.SoundType field_149788_p;
   public static final Block.SoundType field_176231_q;
   protected boolean field_149787_q;
   protected int field_149786_r;
   protected boolean field_149785_s;
   protected int field_149784_t;
   protected boolean field_149783_u;
   protected float field_149782_v;
   protected float field_149781_w;
   protected boolean field_149790_y;
   protected boolean field_149789_z;
   protected boolean field_149758_A;
   protected double field_149759_B;
   protected double field_149760_C;
   protected double field_149754_D;
   protected double field_149755_E;
   protected double field_149756_F;
   protected double field_149757_G;
   public Block.SoundType field_149762_H;
   public float field_149763_I;
   protected final Material field_149764_J;
   protected final MapColor field_181083_K;
   public float field_149765_K;
   protected final BlockState field_176227_L;
   private IBlockState field_176228_M;
   private String field_149770_b;

   public static int func_149682_b(Block var0) {
      return field_149771_c.func_148757_b(var0);
   }

   public static int func_176210_f(IBlockState var0) {
      Block var1 = var0.func_177230_c();
      return func_149682_b(var1) + (var1.func_176201_c(var0) << 12);
   }

   public static Block func_149729_e(int var0) {
      return (Block)field_149771_c.func_148754_a(var0);
   }

   public static IBlockState func_176220_d(int var0) {
      int var1 = var0 & 4095;
      int var2 = var0 >> 12 & 15;
      return func_149729_e(var1).func_176203_a(var2);
   }

   public static Block func_149634_a(Item var0) {
      return var0 instanceof ItemBlock ? ((ItemBlock)var0).func_179223_d() : null;
   }

   public static Block func_149684_b(String var0) {
      ResourceLocation var1 = new ResourceLocation(var0);
      if (field_149771_c.func_148741_d(var1)) {
         return (Block)field_149771_c.func_82594_a(var1);
      } else {
         try {
            return (Block)field_149771_c.func_148754_a(Integer.parseInt(var0));
         } catch (NumberFormatException var3) {
            return null;
         }
      }
   }

   public boolean func_149730_j() {
      return this.field_149787_q;
   }

   public int func_149717_k() {
      return this.field_149786_r;
   }

   public boolean func_149751_l() {
      return this.field_149785_s;
   }

   public int func_149750_m() {
      return this.field_149784_t;
   }

   public boolean func_149710_n() {
      return this.field_149783_u;
   }

   public Material func_149688_o() {
      return this.field_149764_J;
   }

   public MapColor func_180659_g(IBlockState var1) {
      return this.field_181083_K;
   }

   public IBlockState func_176203_a(int var1) {
      return this.func_176223_P();
   }

   public int func_176201_c(IBlockState var1) {
      if (var1 != null && !var1.func_177227_a().isEmpty()) {
         throw new IllegalArgumentException("Don't know how to convert " + var1 + " back into data...");
      } else {
         return 0;
      }
   }

   public IBlockState func_176221_a(IBlockState var1, IBlockAccess var2, BlockPos var3) {
      return var1;
   }

   public Block(Material var1, MapColor var2) {
      super();
      this.field_149790_y = true;
      this.field_149762_H = field_149769_e;
      this.field_149763_I = 1.0F;
      this.field_149765_K = 0.6F;
      this.field_149764_J = var1;
      this.field_181083_K = var2;
      this.func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      this.field_149787_q = this.func_149662_c();
      this.field_149786_r = this.func_149662_c() ? 255 : 0;
      this.field_149785_s = !var1.func_76228_b();
      this.field_176227_L = this.func_180661_e();
      this.func_180632_j(this.field_176227_L.func_177621_b());
   }

   protected Block(Material var1) {
      this(var1, var1.func_151565_r());
   }

   protected Block func_149672_a(Block.SoundType var1) {
      this.field_149762_H = var1;
      return this;
   }

   protected Block func_149713_g(int var1) {
      this.field_149786_r = var1;
      return this;
   }

   protected Block func_149715_a(float var1) {
      this.field_149784_t = (int)(15.0F * var1);
      return this;
   }

   protected Block func_149752_b(float var1) {
      this.field_149781_w = var1 * 3.0F;
      return this;
   }

   public boolean func_149637_q() {
      return this.field_149764_J.func_76230_c() && this.func_149686_d();
   }

   public boolean func_149721_r() {
      return this.field_149764_J.func_76218_k() && this.func_149686_d() && !this.func_149744_f();
   }

   public boolean func_176214_u() {
      return this.field_149764_J.func_76230_c() && this.func_149686_d();
   }

   public boolean func_149686_d() {
      return true;
   }

   public boolean func_176205_b(IBlockAccess var1, BlockPos var2) {
      return !this.field_149764_J.func_76230_c();
   }

   public int func_149645_b() {
      return 3;
   }

   public boolean func_176200_f(World var1, BlockPos var2) {
      return false;
   }

   protected Block func_149711_c(float var1) {
      this.field_149782_v = var1;
      if (this.field_149781_w < var1 * 5.0F) {
         this.field_149781_w = var1 * 5.0F;
      }

      return this;
   }

   protected Block func_149722_s() {
      this.func_149711_c(-1.0F);
      return this;
   }

   public float func_176195_g(World var1, BlockPos var2) {
      return this.field_149782_v;
   }

   protected Block func_149675_a(boolean var1) {
      this.field_149789_z = var1;
      return this;
   }

   public boolean func_149653_t() {
      return this.field_149789_z;
   }

   public boolean func_149716_u() {
      return this.field_149758_A;
   }

   protected final void func_149676_a(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.field_149759_B = (double)var1;
      this.field_149760_C = (double)var2;
      this.field_149754_D = (double)var3;
      this.field_149755_E = (double)var4;
      this.field_149756_F = (double)var5;
      this.field_149757_G = (double)var6;
   }

   public int func_176207_c(IBlockAccess var1, BlockPos var2) {
      Block var3 = var1.func_180495_p(var2).func_177230_c();
      int var4 = var1.func_175626_b(var2, var3.func_149750_m());
      if (var4 == 0 && var3 instanceof BlockSlab) {
         var2 = var2.func_177977_b();
         var3 = var1.func_180495_p(var2).func_177230_c();
         return var1.func_175626_b(var2, var3.func_149750_m());
      } else {
         return var4;
      }
   }

   public boolean func_176225_a(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      if (var3 == EnumFacing.DOWN && this.field_149760_C > 0.0D) {
         return true;
      } else if (var3 == EnumFacing.UP && this.field_149756_F < 1.0D) {
         return true;
      } else if (var3 == EnumFacing.NORTH && this.field_149754_D > 0.0D) {
         return true;
      } else if (var3 == EnumFacing.SOUTH && this.field_149757_G < 1.0D) {
         return true;
      } else if (var3 == EnumFacing.WEST && this.field_149759_B > 0.0D) {
         return true;
      } else if (var3 == EnumFacing.EAST && this.field_149755_E < 1.0D) {
         return true;
      } else {
         return !var1.func_180495_p(var2).func_177230_c().func_149662_c();
      }
   }

   public boolean func_176212_b(IBlockAccess var1, BlockPos var2, EnumFacing var3) {
      return var1.func_180495_p(var2).func_177230_c().func_149688_o().func_76220_a();
   }

   public AxisAlignedBB func_180646_a(World var1, BlockPos var2) {
      return new AxisAlignedBB((double)var2.func_177958_n() + this.field_149759_B, (double)var2.func_177956_o() + this.field_149760_C, (double)var2.func_177952_p() + this.field_149754_D, (double)var2.func_177958_n() + this.field_149755_E, (double)var2.func_177956_o() + this.field_149756_F, (double)var2.func_177952_p() + this.field_149757_G);
   }

   public void func_180638_a(World var1, BlockPos var2, IBlockState var3, AxisAlignedBB var4, List<AxisAlignedBB> var5, Entity var6) {
      AxisAlignedBB var7 = this.func_180640_a(var1, var2, var3);
      if (var7 != null && var4.func_72326_a(var7)) {
         var5.add(var7);
      }

   }

   public AxisAlignedBB func_180640_a(World var1, BlockPos var2, IBlockState var3) {
      return new AxisAlignedBB((double)var2.func_177958_n() + this.field_149759_B, (double)var2.func_177956_o() + this.field_149760_C, (double)var2.func_177952_p() + this.field_149754_D, (double)var2.func_177958_n() + this.field_149755_E, (double)var2.func_177956_o() + this.field_149756_F, (double)var2.func_177952_p() + this.field_149757_G);
   }

   public boolean func_149662_c() {
      return true;
   }

   public boolean func_176209_a(IBlockState var1, boolean var2) {
      return this.func_149703_v();
   }

   public boolean func_149703_v() {
      return true;
   }

   public void func_180645_a(World var1, BlockPos var2, IBlockState var3, Random var4) {
      this.func_180650_b(var1, var2, var3, var4);
   }

   public void func_180650_b(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_180655_c(World var1, BlockPos var2, IBlockState var3, Random var4) {
   }

   public void func_176206_d(World var1, BlockPos var2, IBlockState var3) {
   }

   public void func_176204_a(World var1, BlockPos var2, IBlockState var3, Block var4) {
   }

   public int func_149738_a(World var1) {
      return 10;
   }

   public void func_176213_c(World var1, BlockPos var2, IBlockState var3) {
   }

   public void func_180663_b(World var1, BlockPos var2, IBlockState var3) {
   }

   public int func_149745_a(Random var1) {
      return 1;
   }

   public Item func_180660_a(IBlockState var1, Random var2, int var3) {
      return Item.func_150898_a(this);
   }

   public float func_180647_a(EntityPlayer var1, World var2, BlockPos var3) {
      float var4 = this.func_176195_g(var2, var3);
      if (var4 < 0.0F) {
         return 0.0F;
      } else {
         return !var1.func_146099_a(this) ? var1.func_180471_a(this) / var4 / 100.0F : var1.func_180471_a(this) / var4 / 30.0F;
      }
   }

   public final void func_176226_b(World var1, BlockPos var2, IBlockState var3, int var4) {
      this.func_180653_a(var1, var2, var3, 1.0F, var4);
   }

   public void func_180653_a(World var1, BlockPos var2, IBlockState var3, float var4, int var5) {
      if (!var1.field_72995_K) {
         int var6 = this.func_149679_a(var5, var1.field_73012_v);

         for(int var7 = 0; var7 < var6; ++var7) {
            if (var1.field_73012_v.nextFloat() <= var4) {
               Item var8 = this.func_180660_a(var3, var1.field_73012_v, var5);
               if (var8 != null) {
                  func_180635_a(var1, var2, new ItemStack(var8, 1, this.func_180651_a(var3)));
               }
            }
         }

      }
   }

   public static void func_180635_a(World var0, BlockPos var1, ItemStack var2) {
      if (!var0.field_72995_K && var0.func_82736_K().func_82766_b("doTileDrops")) {
         float var3 = 0.5F;
         double var4 = (double)(var0.field_73012_v.nextFloat() * var3) + (double)(1.0F - var3) * 0.5D;
         double var6 = (double)(var0.field_73012_v.nextFloat() * var3) + (double)(1.0F - var3) * 0.5D;
         double var8 = (double)(var0.field_73012_v.nextFloat() * var3) + (double)(1.0F - var3) * 0.5D;
         EntityItem var10 = new EntityItem(var0, (double)var1.func_177958_n() + var4, (double)var1.func_177956_o() + var6, (double)var1.func_177952_p() + var8, var2);
         var10.func_174869_p();
         var0.func_72838_d(var10);
      }
   }

   protected void func_180637_b(World var1, BlockPos var2, int var3) {
      if (!var1.field_72995_K) {
         while(var3 > 0) {
            int var4 = EntityXPOrb.func_70527_a(var3);
            var3 -= var4;
            var1.func_72838_d(new EntityXPOrb(var1, (double)var2.func_177958_n() + 0.5D, (double)var2.func_177956_o() + 0.5D, (double)var2.func_177952_p() + 0.5D, var4));
         }
      }

   }

   public int func_180651_a(IBlockState var1) {
      return 0;
   }

   public float func_149638_a(Entity var1) {
      return this.field_149781_w / 5.0F;
   }

   public MovingObjectPosition func_180636_a(World var1, BlockPos var2, Vec3 var3, Vec3 var4) {
      this.func_180654_a(var1, var2);
      var3 = var3.func_72441_c((double)(-var2.func_177958_n()), (double)(-var2.func_177956_o()), (double)(-var2.func_177952_p()));
      var4 = var4.func_72441_c((double)(-var2.func_177958_n()), (double)(-var2.func_177956_o()), (double)(-var2.func_177952_p()));
      Vec3 var5 = var3.func_72429_b(var4, this.field_149759_B);
      Vec3 var6 = var3.func_72429_b(var4, this.field_149755_E);
      Vec3 var7 = var3.func_72435_c(var4, this.field_149760_C);
      Vec3 var8 = var3.func_72435_c(var4, this.field_149756_F);
      Vec3 var9 = var3.func_72434_d(var4, this.field_149754_D);
      Vec3 var10 = var3.func_72434_d(var4, this.field_149757_G);
      if (!this.func_149654_a(var5)) {
         var5 = null;
      }

      if (!this.func_149654_a(var6)) {
         var6 = null;
      }

      if (!this.func_149687_b(var7)) {
         var7 = null;
      }

      if (!this.func_149687_b(var8)) {
         var8 = null;
      }

      if (!this.func_149661_c(var9)) {
         var9 = null;
      }

      if (!this.func_149661_c(var10)) {
         var10 = null;
      }

      Vec3 var11 = null;
      if (var5 != null && (var11 == null || var3.func_72436_e(var5) < var3.func_72436_e(var11))) {
         var11 = var5;
      }

      if (var6 != null && (var11 == null || var3.func_72436_e(var6) < var3.func_72436_e(var11))) {
         var11 = var6;
      }

      if (var7 != null && (var11 == null || var3.func_72436_e(var7) < var3.func_72436_e(var11))) {
         var11 = var7;
      }

      if (var8 != null && (var11 == null || var3.func_72436_e(var8) < var3.func_72436_e(var11))) {
         var11 = var8;
      }

      if (var9 != null && (var11 == null || var3.func_72436_e(var9) < var3.func_72436_e(var11))) {
         var11 = var9;
      }

      if (var10 != null && (var11 == null || var3.func_72436_e(var10) < var3.func_72436_e(var11))) {
         var11 = var10;
      }

      if (var11 == null) {
         return null;
      } else {
         EnumFacing var12 = null;
         if (var11 == var5) {
            var12 = EnumFacing.WEST;
         }

         if (var11 == var6) {
            var12 = EnumFacing.EAST;
         }

         if (var11 == var7) {
            var12 = EnumFacing.DOWN;
         }

         if (var11 == var8) {
            var12 = EnumFacing.UP;
         }

         if (var11 == var9) {
            var12 = EnumFacing.NORTH;
         }

         if (var11 == var10) {
            var12 = EnumFacing.SOUTH;
         }

         return new MovingObjectPosition(var11.func_72441_c((double)var2.func_177958_n(), (double)var2.func_177956_o(), (double)var2.func_177952_p()), var12, var2);
      }
   }

   private boolean func_149654_a(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72448_b >= this.field_149760_C && var1.field_72448_b <= this.field_149756_F && var1.field_72449_c >= this.field_149754_D && var1.field_72449_c <= this.field_149757_G;
      }
   }

   private boolean func_149687_b(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_149759_B && var1.field_72450_a <= this.field_149755_E && var1.field_72449_c >= this.field_149754_D && var1.field_72449_c <= this.field_149757_G;
      }
   }

   private boolean func_149661_c(Vec3 var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.field_72450_a >= this.field_149759_B && var1.field_72450_a <= this.field_149755_E && var1.field_72448_b >= this.field_149760_C && var1.field_72448_b <= this.field_149756_F;
      }
   }

   public void func_180652_a(World var1, BlockPos var2, Explosion var3) {
   }

   public EnumWorldBlockLayer func_180664_k() {
      return EnumWorldBlockLayer.SOLID;
   }

   public boolean func_176193_a(World var1, BlockPos var2, EnumFacing var3, ItemStack var4) {
      return this.func_176198_a(var1, var2, var3);
   }

   public boolean func_176198_a(World var1, BlockPos var2, EnumFacing var3) {
      return this.func_176196_c(var1, var2);
   }

   public boolean func_176196_c(World var1, BlockPos var2) {
      return var1.func_180495_p(var2).func_177230_c().field_149764_J.func_76222_j();
   }

   public boolean func_180639_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4, EnumFacing var5, float var6, float var7, float var8) {
      return false;
   }

   public void func_176199_a(World var1, BlockPos var2, Entity var3) {
   }

   public IBlockState func_180642_a(World var1, BlockPos var2, EnumFacing var3, float var4, float var5, float var6, int var7, EntityLivingBase var8) {
      return this.func_176203_a(var7);
   }

   public void func_180649_a(World var1, BlockPos var2, EntityPlayer var3) {
   }

   public Vec3 func_176197_a(World var1, BlockPos var2, Entity var3, Vec3 var4) {
      return var4;
   }

   public void func_180654_a(IBlockAccess var1, BlockPos var2) {
   }

   public final double func_149704_x() {
      return this.field_149759_B;
   }

   public final double func_149753_y() {
      return this.field_149755_E;
   }

   public final double func_149665_z() {
      return this.field_149760_C;
   }

   public final double func_149669_A() {
      return this.field_149756_F;
   }

   public final double func_149706_B() {
      return this.field_149754_D;
   }

   public final double func_149693_C() {
      return this.field_149757_G;
   }

   public int func_149635_D() {
      return 16777215;
   }

   public int func_180644_h(IBlockState var1) {
      return 16777215;
   }

   public int func_180662_a(IBlockAccess var1, BlockPos var2, int var3) {
      return 16777215;
   }

   public final int func_176202_d(IBlockAccess var1, BlockPos var2) {
      return this.func_180662_a(var1, var2, 0);
   }

   public int func_180656_a(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return 0;
   }

   public boolean func_149744_f() {
      return false;
   }

   public void func_180634_a(World var1, BlockPos var2, IBlockState var3, Entity var4) {
   }

   public int func_176211_b(IBlockAccess var1, BlockPos var2, IBlockState var3, EnumFacing var4) {
      return 0;
   }

   public void func_149683_g() {
   }

   public void func_180657_a(World var1, EntityPlayer var2, BlockPos var3, IBlockState var4, TileEntity var5) {
      var2.func_71029_a(StatList.field_75934_C[func_149682_b(this)]);
      var2.func_71020_j(0.025F);
      if (this.func_149700_E() && EnchantmentHelper.func_77502_d(var2)) {
         ItemStack var7 = this.func_180643_i(var4);
         if (var7 != null) {
            func_180635_a(var1, var3, var7);
         }
      } else {
         int var6 = EnchantmentHelper.func_77517_e(var2);
         this.func_176226_b(var1, var3, var4, var6);
      }

   }

   protected boolean func_149700_E() {
      return this.func_149686_d() && !this.field_149758_A;
   }

   protected ItemStack func_180643_i(IBlockState var1) {
      int var2 = 0;
      Item var3 = Item.func_150898_a(this);
      if (var3 != null && var3.func_77614_k()) {
         var2 = this.func_176201_c(var1);
      }

      return new ItemStack(var3, 1, var2);
   }

   public int func_149679_a(int var1, Random var2) {
      return this.func_149745_a(var2);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, EntityLivingBase var4, ItemStack var5) {
   }

   public boolean func_181623_g() {
      return !this.field_149764_J.func_76220_a() && !this.field_149764_J.func_76224_d();
   }

   public Block func_149663_c(String var1) {
      this.field_149770_b = var1;
      return this;
   }

   public String func_149732_F() {
      return StatCollector.func_74838_a(this.func_149739_a() + ".name");
   }

   public String func_149739_a() {
      return "tile." + this.field_149770_b;
   }

   public boolean func_180648_a(World var1, BlockPos var2, IBlockState var3, int var4, int var5) {
      return false;
   }

   public boolean func_149652_G() {
      return this.field_149790_y;
   }

   protected Block func_149649_H() {
      this.field_149790_y = false;
      return this;
   }

   public int func_149656_h() {
      return this.field_149764_J.func_76227_m();
   }

   public float func_149685_I() {
      return this.func_149637_q() ? 0.2F : 1.0F;
   }

   public void func_180658_a(World var1, BlockPos var2, Entity var3, float var4) {
      var3.func_180430_e(var4, 1.0F);
   }

   public void func_176216_a(World var1, Entity var2) {
      var2.field_70181_x = 0.0D;
   }

   public Item func_180665_b(World var1, BlockPos var2) {
      return Item.func_150898_a(this);
   }

   public int func_176222_j(World var1, BlockPos var2) {
      return this.func_180651_a(var1.func_180495_p(var2));
   }

   public void func_149666_a(Item var1, CreativeTabs var2, List<ItemStack> var3) {
      var3.add(new ItemStack(var1, 1, 0));
   }

   public CreativeTabs func_149708_J() {
      return this.field_149772_a;
   }

   public Block func_149647_a(CreativeTabs var1) {
      this.field_149772_a = var1;
      return this;
   }

   public void func_176208_a(World var1, BlockPos var2, IBlockState var3, EntityPlayer var4) {
   }

   public void func_176224_k(World var1, BlockPos var2) {
   }

   public boolean func_149648_K() {
      return false;
   }

   public boolean func_149698_L() {
      return true;
   }

   public boolean func_149659_a(Explosion var1) {
      return true;
   }

   public boolean func_149667_c(Block var1) {
      return this == var1;
   }

   public static boolean func_149680_a(Block var0, Block var1) {
      if (var0 != null && var1 != null) {
         return var0 == var1 ? true : var0.func_149667_c(var1);
      } else {
         return false;
      }
   }

   public boolean func_149740_M() {
      return false;
   }

   public int func_180641_l(World var1, BlockPos var2) {
      return 0;
   }

   public IBlockState func_176217_b(IBlockState var1) {
      return var1;
   }

   protected BlockState func_180661_e() {
      return new BlockState(this, new IProperty[0]);
   }

   public BlockState func_176194_O() {
      return this.field_176227_L;
   }

   protected final void func_180632_j(IBlockState var1) {
      this.field_176228_M = var1;
   }

   public final IBlockState func_176223_P() {
      return this.field_176228_M;
   }

   public Block.EnumOffsetType func_176218_Q() {
      return Block.EnumOffsetType.NONE;
   }

   public String toString() {
      return "Block{" + field_149771_c.func_177774_c(this) + "}";
   }

   public static void func_149671_p() {
      func_176215_a(0, field_176230_a, (new BlockAir()).func_149663_c("air"));
      func_176219_a(1, "stone", (new BlockStone()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stone"));
      func_176219_a(2, "grass", (new BlockGrass()).func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("grass"));
      func_176219_a(3, "dirt", (new BlockDirt()).func_149711_c(0.5F).func_149672_a(field_149767_g).func_149663_c("dirt"));
      Block var0 = (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stonebrick").func_149647_a(CreativeTabs.field_78030_b);
      func_176219_a(4, "cobblestone", var0);
      Block var1 = (new BlockPlanks()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("wood");
      func_176219_a(5, "planks", var1);
      func_176219_a(6, "sapling", (new BlockSapling()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("sapling"));
      func_176219_a(7, "bedrock", (new Block(Material.field_151576_e)).func_149722_s().func_149752_b(6000000.0F).func_149672_a(field_149780_i).func_149663_c("bedrock").func_149649_H().func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(8, "flowing_water", (new BlockDynamicLiquid(Material.field_151586_h)).func_149711_c(100.0F).func_149713_g(3).func_149663_c("water").func_149649_H());
      func_176219_a(9, "water", (new BlockStaticLiquid(Material.field_151586_h)).func_149711_c(100.0F).func_149713_g(3).func_149663_c("water").func_149649_H());
      func_176219_a(10, "flowing_lava", (new BlockDynamicLiquid(Material.field_151587_i)).func_149711_c(100.0F).func_149715_a(1.0F).func_149663_c("lava").func_149649_H());
      func_176219_a(11, "lava", (new BlockStaticLiquid(Material.field_151587_i)).func_149711_c(100.0F).func_149715_a(1.0F).func_149663_c("lava").func_149649_H());
      func_176219_a(12, "sand", (new BlockSand()).func_149711_c(0.5F).func_149672_a(field_149776_m).func_149663_c("sand"));
      func_176219_a(13, "gravel", (new BlockGravel()).func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("gravel"));
      func_176219_a(14, "gold_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreGold"));
      func_176219_a(15, "iron_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreIron"));
      func_176219_a(16, "coal_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreCoal"));
      func_176219_a(17, "log", (new BlockOldLog()).func_149663_c("log"));
      func_176219_a(18, "leaves", (new BlockOldLeaf()).func_149663_c("leaves"));
      func_176219_a(19, "sponge", (new BlockSponge()).func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("sponge"));
      func_176219_a(20, "glass", (new BlockGlass(Material.field_151592_s, false)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("glass"));
      func_176219_a(21, "lapis_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreLapis"));
      func_176219_a(22, "lapis_block", (new Block(Material.field_151573_f, MapColor.field_151652_H)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("blockLapis").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(23, "dispenser", (new BlockDispenser()).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("dispenser"));
      Block var2 = (new BlockSandStone()).func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("sandStone");
      func_176219_a(24, "sandstone", var2);
      func_176219_a(25, "noteblock", (new BlockNote()).func_149711_c(0.8F).func_149663_c("musicBlock"));
      func_176219_a(26, "bed", (new BlockBed()).func_149672_a(field_149766_f).func_149711_c(0.2F).func_149663_c("bed").func_149649_H());
      func_176219_a(27, "golden_rail", (new BlockRailPowered()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("goldenRail"));
      func_176219_a(28, "detector_rail", (new BlockRailDetector()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("detectorRail"));
      func_176219_a(29, "sticky_piston", (new BlockPistonBase(true)).func_149663_c("pistonStickyBase"));
      func_176219_a(30, "web", (new BlockWeb()).func_149713_g(1).func_149711_c(4.0F).func_149663_c("web"));
      func_176219_a(31, "tallgrass", (new BlockTallGrass()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("tallgrass"));
      func_176219_a(32, "deadbush", (new BlockDeadBush()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("deadbush"));
      func_176219_a(33, "piston", (new BlockPistonBase(false)).func_149663_c("pistonBase"));
      func_176219_a(34, "piston_head", (new BlockPistonExtension()).func_149663_c("pistonBase"));
      func_176219_a(35, "wool", (new BlockColored(Material.field_151580_n)).func_149711_c(0.8F).func_149672_a(field_149775_l).func_149663_c("cloth"));
      func_176219_a(36, "piston_extension", new BlockPistonMoving());
      func_176219_a(37, "yellow_flower", (new BlockYellowFlower()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("flower1"));
      func_176219_a(38, "red_flower", (new BlockRedFlower()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("flower2"));
      Block var3 = (new BlockMushroom()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149715_a(0.125F).func_149663_c("mushroom");
      func_176219_a(39, "brown_mushroom", var3);
      Block var4 = (new BlockMushroom()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("mushroom");
      func_176219_a(40, "red_mushroom", var4);
      func_176219_a(41, "gold_block", (new Block(Material.field_151573_f, MapColor.field_151647_F)).func_149711_c(3.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockGold").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(42, "iron_block", (new Block(Material.field_151573_f, MapColor.field_151668_h)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockIron").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(43, "double_stone_slab", (new BlockDoubleStoneSlab()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab"));
      func_176219_a(44, "stone_slab", (new BlockHalfStoneSlab()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab"));
      Block var5 = (new Block(Material.field_151576_e, MapColor.field_151645_D)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("brick").func_149647_a(CreativeTabs.field_78030_b);
      func_176219_a(45, "brick_block", var5);
      func_176219_a(46, "tnt", (new BlockTNT()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("tnt"));
      func_176219_a(47, "bookshelf", (new BlockBookshelf()).func_149711_c(1.5F).func_149672_a(field_149766_f).func_149663_c("bookshelf"));
      func_176219_a(48, "mossy_cobblestone", (new Block(Material.field_151576_e)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneMoss").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(49, "obsidian", (new BlockObsidian()).func_149711_c(50.0F).func_149752_b(2000.0F).func_149672_a(field_149780_i).func_149663_c("obsidian"));
      func_176219_a(50, "torch", (new BlockTorch()).func_149711_c(0.0F).func_149715_a(0.9375F).func_149672_a(field_149766_f).func_149663_c("torch"));
      func_176219_a(51, "fire", (new BlockFire()).func_149711_c(0.0F).func_149715_a(1.0F).func_149672_a(field_149775_l).func_149663_c("fire").func_149649_H());
      func_176219_a(52, "mob_spawner", (new BlockMobSpawner()).func_149711_c(5.0F).func_149672_a(field_149777_j).func_149663_c("mobSpawner").func_149649_H());
      func_176219_a(53, "oak_stairs", (new BlockStairs(var1.func_176223_P().func_177226_a(BlockPlanks.field_176383_a, BlockPlanks.EnumType.OAK))).func_149663_c("stairsWood"));
      func_176219_a(54, "chest", (new BlockChest(0)).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("chest"));
      func_176219_a(55, "redstone_wire", (new BlockRedstoneWire()).func_149711_c(0.0F).func_149672_a(field_149769_e).func_149663_c("redstoneDust").func_149649_H());
      func_176219_a(56, "diamond_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreDiamond"));
      func_176219_a(57, "diamond_block", (new Block(Material.field_151573_f, MapColor.field_151648_G)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockDiamond").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(58, "crafting_table", (new BlockWorkbench()).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("workbench"));
      func_176219_a(59, "wheat", (new BlockCrops()).func_149663_c("crops"));
      Block var6 = (new BlockFarmland()).func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("farmland");
      func_176219_a(60, "farmland", var6);
      func_176219_a(61, "furnace", (new BlockFurnace(false)).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("furnace").func_149647_a(CreativeTabs.field_78031_c));
      func_176219_a(62, "lit_furnace", (new BlockFurnace(true)).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149715_a(0.875F).func_149663_c("furnace"));
      func_176219_a(63, "standing_sign", (new BlockStandingSign()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("sign").func_149649_H());
      func_176219_a(64, "wooden_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorOak").func_149649_H());
      func_176219_a(65, "ladder", (new BlockLadder()).func_149711_c(0.4F).func_149672_a(field_149774_o).func_149663_c("ladder"));
      func_176219_a(66, "rail", (new BlockRail()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("rail"));
      func_176219_a(67, "stone_stairs", (new BlockStairs(var0.func_176223_P())).func_149663_c("stairsStone"));
      func_176219_a(68, "wall_sign", (new BlockWallSign()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("sign").func_149649_H());
      func_176219_a(69, "lever", (new BlockLever()).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("lever"));
      func_176219_a(70, "stone_pressure_plate", (new BlockPressurePlate(Material.field_151576_e, BlockPressurePlate.Sensitivity.MOBS)).func_149711_c(0.5F).func_149672_a(field_149780_i).func_149663_c("pressurePlateStone"));
      func_176219_a(71, "iron_door", (new BlockDoor(Material.field_151573_f)).func_149711_c(5.0F).func_149672_a(field_149777_j).func_149663_c("doorIron").func_149649_H());
      func_176219_a(72, "wooden_pressure_plate", (new BlockPressurePlate(Material.field_151575_d, BlockPressurePlate.Sensitivity.EVERYTHING)).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("pressurePlateWood"));
      func_176219_a(73, "redstone_ore", (new BlockRedstoneOre(false)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreRedstone").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(74, "lit_redstone_ore", (new BlockRedstoneOre(true)).func_149715_a(0.625F).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreRedstone"));
      func_176219_a(75, "unlit_redstone_torch", (new BlockRedstoneTorch(false)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("notGate"));
      func_176219_a(76, "redstone_torch", (new BlockRedstoneTorch(true)).func_149711_c(0.0F).func_149715_a(0.5F).func_149672_a(field_149766_f).func_149663_c("notGate").func_149647_a(CreativeTabs.field_78028_d));
      func_176219_a(77, "stone_button", (new BlockButtonStone()).func_149711_c(0.5F).func_149672_a(field_149780_i).func_149663_c("button"));
      func_176219_a(78, "snow_layer", (new BlockSnow()).func_149711_c(0.1F).func_149672_a(field_149773_n).func_149663_c("snow").func_149713_g(0));
      func_176219_a(79, "ice", (new BlockIce()).func_149711_c(0.5F).func_149713_g(3).func_149672_a(field_149778_k).func_149663_c("ice"));
      func_176219_a(80, "snow", (new BlockSnowBlock()).func_149711_c(0.2F).func_149672_a(field_149773_n).func_149663_c("snow"));
      func_176219_a(81, "cactus", (new BlockCactus()).func_149711_c(0.4F).func_149672_a(field_149775_l).func_149663_c("cactus"));
      func_176219_a(82, "clay", (new BlockClay()).func_149711_c(0.6F).func_149672_a(field_149767_g).func_149663_c("clay"));
      func_176219_a(83, "reeds", (new BlockReed()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("reeds").func_149649_H());
      func_176219_a(84, "jukebox", (new BlockJukebox()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("jukebox"));
      func_176219_a(85, "fence", (new BlockFence(Material.field_151575_d, BlockPlanks.EnumType.OAK.func_181070_c())).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("fence"));
      Block var7 = (new BlockPumpkin()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("pumpkin");
      func_176219_a(86, "pumpkin", var7);
      func_176219_a(87, "netherrack", (new BlockNetherrack()).func_149711_c(0.4F).func_149672_a(field_149780_i).func_149663_c("hellrock"));
      func_176219_a(88, "soul_sand", (new BlockSoulSand()).func_149711_c(0.5F).func_149672_a(field_149776_m).func_149663_c("hellsand"));
      func_176219_a(89, "glowstone", (new BlockGlowstone(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149715_a(1.0F).func_149663_c("lightgem"));
      func_176219_a(90, "portal", (new BlockPortal()).func_149711_c(-1.0F).func_149672_a(field_149778_k).func_149715_a(0.75F).func_149663_c("portal"));
      func_176219_a(91, "lit_pumpkin", (new BlockPumpkin()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149715_a(1.0F).func_149663_c("litpumpkin"));
      func_176219_a(92, "cake", (new BlockCake()).func_149711_c(0.5F).func_149672_a(field_149775_l).func_149663_c("cake").func_149649_H());
      func_176219_a(93, "unpowered_repeater", (new BlockRedstoneRepeater(false)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("diode").func_149649_H());
      func_176219_a(94, "powered_repeater", (new BlockRedstoneRepeater(true)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("diode").func_149649_H());
      func_176219_a(95, "stained_glass", (new BlockStainedGlass(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("stainedGlass"));
      func_176219_a(96, "trapdoor", (new BlockTrapDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("trapdoor").func_149649_H());
      func_176219_a(97, "monster_egg", (new BlockSilverfish()).func_149711_c(0.75F).func_149663_c("monsterStoneEgg"));
      Block var8 = (new BlockStoneBrick()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stonebricksmooth");
      func_176219_a(98, "stonebrick", var8);
      func_176219_a(99, "brown_mushroom_block", (new BlockHugeMushroom(Material.field_151575_d, MapColor.field_151664_l, var3)).func_149711_c(0.2F).func_149672_a(field_149766_f).func_149663_c("mushroom"));
      func_176219_a(100, "red_mushroom_block", (new BlockHugeMushroom(Material.field_151575_d, MapColor.field_151645_D, var4)).func_149711_c(0.2F).func_149672_a(field_149766_f).func_149663_c("mushroom"));
      func_176219_a(101, "iron_bars", (new BlockPane(Material.field_151573_f, true)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("fenceIron"));
      func_176219_a(102, "glass_pane", (new BlockPane(Material.field_151592_s, false)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("thinGlass"));
      Block var9 = (new BlockMelon()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("melon");
      func_176219_a(103, "melon_block", var9);
      func_176219_a(104, "pumpkin_stem", (new BlockStem(var7)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("pumpkinStem"));
      func_176219_a(105, "melon_stem", (new BlockStem(var9)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("pumpkinStem"));
      func_176219_a(106, "vine", (new BlockVine()).func_149711_c(0.2F).func_149672_a(field_149779_h).func_149663_c("vine"));
      func_176219_a(107, "fence_gate", (new BlockFenceGate(BlockPlanks.EnumType.OAK)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("fenceGate"));
      func_176219_a(108, "brick_stairs", (new BlockStairs(var5.func_176223_P())).func_149663_c("stairsBrick"));
      func_176219_a(109, "stone_brick_stairs", (new BlockStairs(var8.func_176223_P().func_177226_a(BlockStoneBrick.field_176249_a, BlockStoneBrick.EnumType.DEFAULT))).func_149663_c("stairsStoneBrickSmooth"));
      func_176219_a(110, "mycelium", (new BlockMycelium()).func_149711_c(0.6F).func_149672_a(field_149779_h).func_149663_c("mycel"));
      func_176219_a(111, "waterlily", (new BlockLilyPad()).func_149711_c(0.0F).func_149672_a(field_149779_h).func_149663_c("waterlily"));
      Block var10 = (new BlockNetherBrick()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("netherBrick").func_149647_a(CreativeTabs.field_78030_b);
      func_176219_a(112, "nether_brick", var10);
      func_176219_a(113, "nether_brick_fence", (new BlockFence(Material.field_151576_e, MapColor.field_151655_K)).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("netherFence"));
      func_176219_a(114, "nether_brick_stairs", (new BlockStairs(var10.func_176223_P())).func_149663_c("stairsNetherBrick"));
      func_176219_a(115, "nether_wart", (new BlockNetherWart()).func_149663_c("netherStalk"));
      func_176219_a(116, "enchanting_table", (new BlockEnchantmentTable()).func_149711_c(5.0F).func_149752_b(2000.0F).func_149663_c("enchantmentTable"));
      func_176219_a(117, "brewing_stand", (new BlockBrewingStand()).func_149711_c(0.5F).func_149715_a(0.125F).func_149663_c("brewingStand"));
      func_176219_a(118, "cauldron", (new BlockCauldron()).func_149711_c(2.0F).func_149663_c("cauldron"));
      func_176219_a(119, "end_portal", (new BlockEndPortal(Material.field_151567_E)).func_149711_c(-1.0F).func_149752_b(6000000.0F));
      func_176219_a(120, "end_portal_frame", (new BlockEndPortalFrame()).func_149672_a(field_149778_k).func_149715_a(0.125F).func_149711_c(-1.0F).func_149663_c("endPortalFrame").func_149752_b(6000000.0F).func_149647_a(CreativeTabs.field_78031_c));
      func_176219_a(121, "end_stone", (new Block(Material.field_151576_e, MapColor.field_151658_d)).func_149711_c(3.0F).func_149752_b(15.0F).func_149672_a(field_149780_i).func_149663_c("whiteStone").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(122, "dragon_egg", (new BlockDragonEgg()).func_149711_c(3.0F).func_149752_b(15.0F).func_149672_a(field_149780_i).func_149715_a(0.125F).func_149663_c("dragonEgg"));
      func_176219_a(123, "redstone_lamp", (new BlockRedstoneLight(false)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("redstoneLight").func_149647_a(CreativeTabs.field_78028_d));
      func_176219_a(124, "lit_redstone_lamp", (new BlockRedstoneLight(true)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("redstoneLight"));
      func_176219_a(125, "double_wooden_slab", (new BlockDoubleWoodSlab()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("woodSlab"));
      func_176219_a(126, "wooden_slab", (new BlockHalfWoodSlab()).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("woodSlab"));
      func_176219_a(127, "cocoa", (new BlockCocoa()).func_149711_c(0.2F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("cocoa"));
      func_176219_a(128, "sandstone_stairs", (new BlockStairs(var2.func_176223_P().func_177226_a(BlockSandStone.field_176297_a, BlockSandStone.EnumType.SMOOTH))).func_149663_c("stairsSandStone"));
      func_176219_a(129, "emerald_ore", (new BlockOre()).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("oreEmerald"));
      func_176219_a(130, "ender_chest", (new BlockEnderChest()).func_149711_c(22.5F).func_149752_b(1000.0F).func_149672_a(field_149780_i).func_149663_c("enderChest").func_149715_a(0.5F));
      func_176219_a(131, "tripwire_hook", (new BlockTripWireHook()).func_149663_c("tripWireSource"));
      func_176219_a(132, "tripwire", (new BlockTripWire()).func_149663_c("tripWire"));
      func_176219_a(133, "emerald_block", (new Block(Material.field_151573_f, MapColor.field_151653_I)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockEmerald").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(134, "spruce_stairs", (new BlockStairs(var1.func_176223_P().func_177226_a(BlockPlanks.field_176383_a, BlockPlanks.EnumType.SPRUCE))).func_149663_c("stairsWoodSpruce"));
      func_176219_a(135, "birch_stairs", (new BlockStairs(var1.func_176223_P().func_177226_a(BlockPlanks.field_176383_a, BlockPlanks.EnumType.BIRCH))).func_149663_c("stairsWoodBirch"));
      func_176219_a(136, "jungle_stairs", (new BlockStairs(var1.func_176223_P().func_177226_a(BlockPlanks.field_176383_a, BlockPlanks.EnumType.JUNGLE))).func_149663_c("stairsWoodJungle"));
      func_176219_a(137, "command_block", (new BlockCommandBlock()).func_149722_s().func_149752_b(6000000.0F).func_149663_c("commandBlock"));
      func_176219_a(138, "beacon", (new BlockBeacon()).func_149663_c("beacon").func_149715_a(1.0F));
      func_176219_a(139, "cobblestone_wall", (new BlockWall(var0)).func_149663_c("cobbleWall"));
      func_176219_a(140, "flower_pot", (new BlockFlowerPot()).func_149711_c(0.0F).func_149672_a(field_149769_e).func_149663_c("flowerPot"));
      func_176219_a(141, "carrots", (new BlockCarrot()).func_149663_c("carrots"));
      func_176219_a(142, "potatoes", (new BlockPotato()).func_149663_c("potatoes"));
      func_176219_a(143, "wooden_button", (new BlockButtonWood()).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("button"));
      func_176219_a(144, "skull", (new BlockSkull()).func_149711_c(1.0F).func_149672_a(field_149780_i).func_149663_c("skull"));
      func_176219_a(145, "anvil", (new BlockAnvil()).func_149711_c(5.0F).func_149672_a(field_149788_p).func_149752_b(2000.0F).func_149663_c("anvil"));
      func_176219_a(146, "trapped_chest", (new BlockChest(1)).func_149711_c(2.5F).func_149672_a(field_149766_f).func_149663_c("chestTrap"));
      func_176219_a(147, "light_weighted_pressure_plate", (new BlockPressurePlateWeighted(Material.field_151573_f, 15, MapColor.field_151647_F)).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("weightedPlate_light"));
      func_176219_a(148, "heavy_weighted_pressure_plate", (new BlockPressurePlateWeighted(Material.field_151573_f, 150)).func_149711_c(0.5F).func_149672_a(field_149766_f).func_149663_c("weightedPlate_heavy"));
      func_176219_a(149, "unpowered_comparator", (new BlockRedstoneComparator(false)).func_149711_c(0.0F).func_149672_a(field_149766_f).func_149663_c("comparator").func_149649_H());
      func_176219_a(150, "powered_comparator", (new BlockRedstoneComparator(true)).func_149711_c(0.0F).func_149715_a(0.625F).func_149672_a(field_149766_f).func_149663_c("comparator").func_149649_H());
      func_176219_a(151, "daylight_detector", new BlockDaylightDetector(false));
      func_176219_a(152, "redstone_block", (new BlockCompressedPowered(Material.field_151573_f, MapColor.field_151656_f)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149777_j).func_149663_c("blockRedstone").func_149647_a(CreativeTabs.field_78028_d));
      func_176219_a(153, "quartz_ore", (new BlockOre(MapColor.field_151655_K)).func_149711_c(3.0F).func_149752_b(5.0F).func_149672_a(field_149780_i).func_149663_c("netherquartz"));
      func_176219_a(154, "hopper", (new BlockHopper()).func_149711_c(3.0F).func_149752_b(8.0F).func_149672_a(field_149777_j).func_149663_c("hopper"));
      Block var11 = (new BlockQuartz()).func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("quartzBlock");
      func_176219_a(155, "quartz_block", var11);
      func_176219_a(156, "quartz_stairs", (new BlockStairs(var11.func_176223_P().func_177226_a(BlockQuartz.field_176335_a, BlockQuartz.EnumType.DEFAULT))).func_149663_c("stairsQuartz"));
      func_176219_a(157, "activator_rail", (new BlockRailPowered()).func_149711_c(0.7F).func_149672_a(field_149777_j).func_149663_c("activatorRail"));
      func_176219_a(158, "dropper", (new BlockDropper()).func_149711_c(3.5F).func_149672_a(field_149780_i).func_149663_c("dropper"));
      func_176219_a(159, "stained_hardened_clay", (new BlockColored(Material.field_151576_e)).func_149711_c(1.25F).func_149752_b(7.0F).func_149672_a(field_149780_i).func_149663_c("clayHardenedStained"));
      func_176219_a(160, "stained_glass_pane", (new BlockStainedGlassPane()).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149663_c("thinStainedGlass"));
      func_176219_a(161, "leaves2", (new BlockNewLeaf()).func_149663_c("leaves"));
      func_176219_a(162, "log2", (new BlockNewLog()).func_149663_c("log"));
      func_176219_a(163, "acacia_stairs", (new BlockStairs(var1.func_176223_P().func_177226_a(BlockPlanks.field_176383_a, BlockPlanks.EnumType.ACACIA))).func_149663_c("stairsWoodAcacia"));
      func_176219_a(164, "dark_oak_stairs", (new BlockStairs(var1.func_176223_P().func_177226_a(BlockPlanks.field_176383_a, BlockPlanks.EnumType.DARK_OAK))).func_149663_c("stairsWoodDarkOak"));
      func_176219_a(165, "slime", (new BlockSlime()).func_149663_c("slime").func_149672_a(field_176231_q));
      func_176219_a(166, "barrier", (new BlockBarrier()).func_149663_c("barrier"));
      func_176219_a(167, "iron_trapdoor", (new BlockTrapDoor(Material.field_151573_f)).func_149711_c(5.0F).func_149672_a(field_149777_j).func_149663_c("ironTrapdoor").func_149649_H());
      func_176219_a(168, "prismarine", (new BlockPrismarine()).func_149711_c(1.5F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("prismarine"));
      func_176219_a(169, "sea_lantern", (new BlockSeaLantern(Material.field_151592_s)).func_149711_c(0.3F).func_149672_a(field_149778_k).func_149715_a(1.0F).func_149663_c("seaLantern"));
      func_176219_a(170, "hay_block", (new BlockHay()).func_149711_c(0.5F).func_149672_a(field_149779_h).func_149663_c("hayBlock").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(171, "carpet", (new BlockCarpet()).func_149711_c(0.1F).func_149672_a(field_149775_l).func_149663_c("woolCarpet").func_149713_g(0));
      func_176219_a(172, "hardened_clay", (new BlockHardenedClay()).func_149711_c(1.25F).func_149752_b(7.0F).func_149672_a(field_149780_i).func_149663_c("clayHardened"));
      func_176219_a(173, "coal_block", (new Block(Material.field_151576_e, MapColor.field_151646_E)).func_149711_c(5.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("blockCoal").func_149647_a(CreativeTabs.field_78030_b));
      func_176219_a(174, "packed_ice", (new BlockPackedIce()).func_149711_c(0.5F).func_149672_a(field_149778_k).func_149663_c("icePacked"));
      func_176219_a(175, "double_plant", new BlockDoublePlant());
      func_176219_a(176, "standing_banner", (new BlockBanner.BlockBannerStanding()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("banner").func_149649_H());
      func_176219_a(177, "wall_banner", (new BlockBanner.BlockBannerHanging()).func_149711_c(1.0F).func_149672_a(field_149766_f).func_149663_c("banner").func_149649_H());
      func_176219_a(178, "daylight_detector_inverted", new BlockDaylightDetector(true));
      Block var12 = (new BlockRedSandstone()).func_149672_a(field_149780_i).func_149711_c(0.8F).func_149663_c("redSandStone");
      func_176219_a(179, "red_sandstone", var12);
      func_176219_a(180, "red_sandstone_stairs", (new BlockStairs(var12.func_176223_P().func_177226_a(BlockRedSandstone.field_176336_a, BlockRedSandstone.EnumType.SMOOTH))).func_149663_c("stairsRedSandStone"));
      func_176219_a(181, "double_stone_slab2", (new BlockDoubleStoneSlabNew()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab2"));
      func_176219_a(182, "stone_slab2", (new BlockHalfStoneSlabNew()).func_149711_c(2.0F).func_149752_b(10.0F).func_149672_a(field_149780_i).func_149663_c("stoneSlab2"));
      func_176219_a(183, "spruce_fence_gate", (new BlockFenceGate(BlockPlanks.EnumType.SPRUCE)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("spruceFenceGate"));
      func_176219_a(184, "birch_fence_gate", (new BlockFenceGate(BlockPlanks.EnumType.BIRCH)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("birchFenceGate"));
      func_176219_a(185, "jungle_fence_gate", (new BlockFenceGate(BlockPlanks.EnumType.JUNGLE)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("jungleFenceGate"));
      func_176219_a(186, "dark_oak_fence_gate", (new BlockFenceGate(BlockPlanks.EnumType.DARK_OAK)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("darkOakFenceGate"));
      func_176219_a(187, "acacia_fence_gate", (new BlockFenceGate(BlockPlanks.EnumType.ACACIA)).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("acaciaFenceGate"));
      func_176219_a(188, "spruce_fence", (new BlockFence(Material.field_151575_d, BlockPlanks.EnumType.SPRUCE.func_181070_c())).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("spruceFence"));
      func_176219_a(189, "birch_fence", (new BlockFence(Material.field_151575_d, BlockPlanks.EnumType.BIRCH.func_181070_c())).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("birchFence"));
      func_176219_a(190, "jungle_fence", (new BlockFence(Material.field_151575_d, BlockPlanks.EnumType.JUNGLE.func_181070_c())).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("jungleFence"));
      func_176219_a(191, "dark_oak_fence", (new BlockFence(Material.field_151575_d, BlockPlanks.EnumType.DARK_OAK.func_181070_c())).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("darkOakFence"));
      func_176219_a(192, "acacia_fence", (new BlockFence(Material.field_151575_d, BlockPlanks.EnumType.ACACIA.func_181070_c())).func_149711_c(2.0F).func_149752_b(5.0F).func_149672_a(field_149766_f).func_149663_c("acaciaFence"));
      func_176219_a(193, "spruce_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorSpruce").func_149649_H());
      func_176219_a(194, "birch_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorBirch").func_149649_H());
      func_176219_a(195, "jungle_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorJungle").func_149649_H());
      func_176219_a(196, "acacia_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorAcacia").func_149649_H());
      func_176219_a(197, "dark_oak_door", (new BlockDoor(Material.field_151575_d)).func_149711_c(3.0F).func_149672_a(field_149766_f).func_149663_c("doorDarkOak").func_149649_H());
      field_149771_c.func_177776_a();
      Iterator var13 = field_149771_c.iterator();

      while(true) {
         Block var14;
         while(var13.hasNext()) {
            var14 = (Block)var13.next();
            if (var14.field_149764_J == Material.field_151579_a) {
               var14.field_149783_u = false;
            } else {
               boolean var15 = false;
               boolean var16 = var14 instanceof BlockStairs;
               boolean var17 = var14 instanceof BlockSlab;
               boolean var18 = var14 == var6;
               boolean var19 = var14.field_149785_s;
               boolean var20 = var14.field_149786_r == 0;
               if (var16 || var17 || var18 || var19 || var20) {
                  var15 = true;
               }

               var14.field_149783_u = var15;
            }
         }

         var13 = field_149771_c.iterator();

         while(var13.hasNext()) {
            var14 = (Block)var13.next();
            Iterator var21 = var14.func_176194_O().func_177619_a().iterator();

            while(var21.hasNext()) {
               IBlockState var22 = (IBlockState)var21.next();
               int var23 = field_149771_c.func_148757_b(var14) << 4 | var14.func_176201_c(var22);
               field_176229_d.func_148746_a(var22, var23);
            }
         }

         return;
      }
   }

   private static void func_176215_a(int var0, ResourceLocation var1, Block var2) {
      field_149771_c.func_177775_a(var0, var1, var2);
   }

   private static void func_176219_a(int var0, String var1, Block var2) {
      func_176215_a(var0, new ResourceLocation(var1), var2);
   }

   static {
      field_149771_c = new RegistryNamespacedDefaultedByKey(field_176230_a);
      field_176229_d = new ObjectIntIdentityMap();
      field_149769_e = new Block.SoundType("stone", 1.0F, 1.0F);
      field_149766_f = new Block.SoundType("wood", 1.0F, 1.0F);
      field_149767_g = new Block.SoundType("gravel", 1.0F, 1.0F);
      field_149779_h = new Block.SoundType("grass", 1.0F, 1.0F);
      field_149780_i = new Block.SoundType("stone", 1.0F, 1.0F);
      field_149777_j = new Block.SoundType("stone", 1.0F, 1.5F);
      field_149778_k = new Block.SoundType("stone", 1.0F, 1.0F) {
         public String func_150495_a() {
            return "dig.glass";
         }

         public String func_150496_b() {
            return "step.stone";
         }
      };
      field_149775_l = new Block.SoundType("cloth", 1.0F, 1.0F);
      field_149776_m = new Block.SoundType("sand", 1.0F, 1.0F);
      field_149773_n = new Block.SoundType("snow", 1.0F, 1.0F);
      field_149774_o = new Block.SoundType("ladder", 1.0F, 1.0F) {
         public String func_150495_a() {
            return "dig.wood";
         }
      };
      field_149788_p = new Block.SoundType("anvil", 0.3F, 1.0F) {
         public String func_150495_a() {
            return "dig.stone";
         }

         public String func_150496_b() {
            return "random.anvil_land";
         }
      };
      field_176231_q = new Block.SoundType("slime", 1.0F, 1.0F) {
         public String func_150495_a() {
            return "mob.slime.big";
         }

         public String func_150496_b() {
            return "mob.slime.big";
         }

         public String func_150498_e() {
            return "mob.slime.small";
         }
      };
   }

   public static enum EnumOffsetType {
      NONE,
      XZ,
      XYZ;

      private EnumOffsetType() {
      }
   }

   public static class SoundType {
      public final String field_150501_a;
      public final float field_150499_b;
      public final float field_150500_c;

      public SoundType(String var1, float var2, float var3) {
         super();
         this.field_150501_a = var1;
         this.field_150499_b = var2;
         this.field_150500_c = var3;
      }

      public float func_150497_c() {
         return this.field_150499_b;
      }

      public float func_150494_d() {
         return this.field_150500_c;
      }

      public String func_150495_a() {
         return "dig." + this.field_150501_a;
      }

      public String func_150498_e() {
         return "step." + this.field_150501_a;
      }

      public String func_150496_b() {
         return this.func_150495_a();
      }
   }
}
