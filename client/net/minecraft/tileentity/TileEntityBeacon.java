package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class TileEntityBeacon extends TileEntityLockable implements ISidedInventory, ITickable {
   public static final Potion[][] field_146009_a;
   private static final Set<Potion> field_184280_f;
   private final List<TileEntityBeacon.BeamSegment> field_174909_f = Lists.newArrayList();
   private long field_146016_i;
   private float field_146014_j;
   private boolean field_146015_k;
   private boolean field_205737_j;
   private int field_146012_l = -1;
   @Nullable
   private Potion field_146013_m;
   @Nullable
   private Potion field_146010_n;
   private ItemStack field_146011_o;
   private ITextComponent field_146008_p;

   public TileEntityBeacon() {
      super(TileEntityType.field_200984_o);
      this.field_146011_o = ItemStack.field_190927_a;
   }

   public void func_73660_a() {
      if (this.field_145850_b.func_82737_E() % 80L == 0L) {
         this.func_174908_m();
         if (this.field_146015_k) {
            this.func_205736_a(SoundEvents.field_206939_L);
         }
      }

      if (!this.field_145850_b.field_72995_K && this.field_146015_k != this.field_205737_j) {
         this.field_205737_j = this.field_146015_k;
         this.func_205736_a(this.field_146015_k ? SoundEvents.field_206938_K : SoundEvents.field_206940_M);
      }

   }

   public void func_174908_m() {
      if (this.field_145850_b != null) {
         this.func_146003_y();
         this.func_146000_x();
      }

   }

   public void func_205736_a(SoundEvent var1) {
      this.field_145850_b.func_184133_a((EntityPlayer)null, this.field_174879_c, var1, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   private void func_146000_x() {
      if (this.field_146015_k && this.field_146012_l > 0 && !this.field_145850_b.field_72995_K && this.field_146013_m != null) {
         double var1 = (double)(this.field_146012_l * 10 + 10);
         byte var3 = 0;
         if (this.field_146012_l >= 4 && this.field_146013_m == this.field_146010_n) {
            var3 = 1;
         }

         int var4 = (9 + this.field_146012_l * 2) * 20;
         int var5 = this.field_174879_c.func_177958_n();
         int var6 = this.field_174879_c.func_177956_o();
         int var7 = this.field_174879_c.func_177952_p();
         AxisAlignedBB var8 = (new AxisAlignedBB((double)var5, (double)var6, (double)var7, (double)(var5 + 1), (double)(var6 + 1), (double)(var7 + 1))).func_186662_g(var1).func_72321_a(0.0D, (double)this.field_145850_b.func_72800_K(), 0.0D);
         List var9 = this.field_145850_b.func_72872_a(EntityPlayer.class, var8);
         Iterator var10 = var9.iterator();

         EntityPlayer var11;
         while(var10.hasNext()) {
            var11 = (EntityPlayer)var10.next();
            var11.func_195064_c(new PotionEffect(this.field_146013_m, var4, var3, true, true));
         }

         if (this.field_146012_l >= 4 && this.field_146013_m != this.field_146010_n && this.field_146010_n != null) {
            var10 = var9.iterator();

            while(var10.hasNext()) {
               var11 = (EntityPlayer)var10.next();
               var11.func_195064_c(new PotionEffect(this.field_146010_n, var4, 0, true, true));
            }
         }
      }

   }

   private void func_146003_y() {
      int var1 = this.field_174879_c.func_177958_n();
      int var2 = this.field_174879_c.func_177956_o();
      int var3 = this.field_174879_c.func_177952_p();
      int var4 = this.field_146012_l;
      this.field_146012_l = 0;
      this.field_174909_f.clear();
      this.field_146015_k = true;
      TileEntityBeacon.BeamSegment var5 = new TileEntityBeacon.BeamSegment(EnumDyeColor.WHITE.func_193349_f());
      this.field_174909_f.add(var5);
      boolean var6 = true;
      BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();

      int var8;
      for(var8 = var2 + 1; var8 < 256; ++var8) {
         IBlockState var9 = this.field_145850_b.func_180495_p(var7.func_181079_c(var1, var8, var3));
         Block var11 = var9.func_177230_c();
         float[] var10;
         if (var11 instanceof BlockStainedGlass) {
            var10 = ((BlockStainedGlass)var11).func_196457_d().func_193349_f();
         } else {
            if (!(var11 instanceof BlockStainedGlassPane)) {
               if (var9.func_200016_a(this.field_145850_b, var7) >= 15 && var11 != Blocks.field_150357_h) {
                  this.field_146015_k = false;
                  this.field_174909_f.clear();
                  break;
               }

               var5.func_177262_a();
               continue;
            }

            var10 = ((BlockStainedGlassPane)var11).func_196419_d().func_193349_f();
         }

         if (!var6) {
            var10 = new float[]{(var5.func_177263_b()[0] + var10[0]) / 2.0F, (var5.func_177263_b()[1] + var10[1]) / 2.0F, (var5.func_177263_b()[2] + var10[2]) / 2.0F};
         }

         if (Arrays.equals(var10, var5.func_177263_b())) {
            var5.func_177262_a();
         } else {
            var5 = new TileEntityBeacon.BeamSegment(var10);
            this.field_174909_f.add(var5);
         }

         var6 = false;
      }

      if (this.field_146015_k) {
         for(var8 = 1; var8 <= 4; this.field_146012_l = var8++) {
            int var15 = var2 - var8;
            if (var15 < 0) {
               break;
            }

            boolean var18 = true;

            for(int var17 = var1 - var8; var17 <= var1 + var8 && var18; ++var17) {
               for(int var12 = var3 - var8; var12 <= var3 + var8; ++var12) {
                  Block var13 = this.field_145850_b.func_180495_p(new BlockPos(var17, var15, var12)).func_177230_c();
                  if (var13 != Blocks.field_150475_bE && var13 != Blocks.field_150340_R && var13 != Blocks.field_150484_ah && var13 != Blocks.field_150339_S) {
                     var18 = false;
                     break;
                  }
               }
            }

            if (!var18) {
               break;
            }
         }

         if (this.field_146012_l == 0) {
            this.field_146015_k = false;
         }
      }

      if (!this.field_145850_b.field_72995_K && var4 < this.field_146012_l) {
         Iterator var14 = this.field_145850_b.func_72872_a(EntityPlayerMP.class, (new AxisAlignedBB((double)var1, (double)var2, (double)var3, (double)var1, (double)(var2 - 4), (double)var3)).func_72314_b(10.0D, 5.0D, 10.0D)).iterator();

         while(var14.hasNext()) {
            EntityPlayerMP var16 = (EntityPlayerMP)var14.next();
            CriteriaTriggers.field_192131_k.func_192180_a(var16, this);
         }
      }

   }

   public List<TileEntityBeacon.BeamSegment> func_174907_n() {
      return this.field_174909_f;
   }

   public float func_146002_i() {
      if (!this.field_146015_k) {
         return 0.0F;
      } else {
         int var1 = (int)(this.field_145850_b.func_82737_E() - this.field_146016_i);
         this.field_146016_i = this.field_145850_b.func_82737_E();
         if (var1 > 1) {
            this.field_146014_j -= (float)var1 / 40.0F;
            if (this.field_146014_j < 0.0F) {
               this.field_146014_j = 0.0F;
            }
         }

         this.field_146014_j += 0.025F;
         if (this.field_146014_j > 1.0F) {
            this.field_146014_j = 1.0F;
         }

         return this.field_146014_j;
      }
   }

   public int func_191979_s() {
      return this.field_146012_l;
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 3, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public double func_145833_n() {
      return 65536.0D;
   }

   @Nullable
   private static Potion func_184279_f(int var0) {
      Potion var1 = Potion.func_188412_a(var0);
      return field_184280_f.contains(var1) ? var1 : null;
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_146013_m = func_184279_f(var1.func_74762_e("Primary"));
      this.field_146010_n = func_184279_f(var1.func_74762_e("Secondary"));
      this.field_146012_l = var1.func_74762_e("Levels");
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      var1.func_74768_a("Primary", Potion.func_188409_a(this.field_146013_m));
      var1.func_74768_a("Secondary", Potion.func_188409_a(this.field_146010_n));
      var1.func_74768_a("Levels", this.field_146012_l);
      return var1;
   }

   public int func_70302_i_() {
      return 1;
   }

   public boolean func_191420_l() {
      return this.field_146011_o.func_190926_b();
   }

   public ItemStack func_70301_a(int var1) {
      return var1 == 0 ? this.field_146011_o : ItemStack.field_190927_a;
   }

   public ItemStack func_70298_a(int var1, int var2) {
      if (var1 == 0 && !this.field_146011_o.func_190926_b()) {
         if (var2 >= this.field_146011_o.func_190916_E()) {
            ItemStack var3 = this.field_146011_o;
            this.field_146011_o = ItemStack.field_190927_a;
            return var3;
         } else {
            return this.field_146011_o.func_77979_a(var2);
         }
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public ItemStack func_70304_b(int var1) {
      if (var1 == 0) {
         ItemStack var2 = this.field_146011_o;
         this.field_146011_o = ItemStack.field_190927_a;
         return var2;
      } else {
         return ItemStack.field_190927_a;
      }
   }

   public void func_70299_a(int var1, ItemStack var2) {
      if (var1 == 0) {
         this.field_146011_o = var2;
      }

   }

   public ITextComponent func_200200_C_() {
      return (ITextComponent)(this.field_146008_p != null ? this.field_146008_p : new TextComponentTranslation("container.beacon", new Object[0]));
   }

   public boolean func_145818_k_() {
      return this.field_146008_p != null;
   }

   @Nullable
   public ITextComponent func_200201_e() {
      return this.field_146008_p;
   }

   public void func_200227_a(@Nullable ITextComponent var1) {
      this.field_146008_p = var1;
   }

   public int func_70297_j_() {
      return 1;
   }

   public boolean func_70300_a(EntityPlayer var1) {
      if (this.field_145850_b.func_175625_s(this.field_174879_c) != this) {
         return false;
      } else {
         return var1.func_70092_e((double)this.field_174879_c.func_177958_n() + 0.5D, (double)this.field_174879_c.func_177956_o() + 0.5D, (double)this.field_174879_c.func_177952_p() + 0.5D) <= 64.0D;
      }
   }

   public void func_174889_b(EntityPlayer var1) {
   }

   public void func_174886_c(EntityPlayer var1) {
   }

   public boolean func_94041_b(int var1, ItemStack var2) {
      return var2.func_77973_b() == Items.field_151166_bC || var2.func_77973_b() == Items.field_151045_i || var2.func_77973_b() == Items.field_151043_k || var2.func_77973_b() == Items.field_151042_j;
   }

   public String func_174875_k() {
      return "minecraft:beacon";
   }

   public Container func_174876_a(InventoryPlayer var1, EntityPlayer var2) {
      return new ContainerBeacon(var1, this);
   }

   public int func_174887_a_(int var1) {
      switch(var1) {
      case 0:
         return this.field_146012_l;
      case 1:
         return Potion.func_188409_a(this.field_146013_m);
      case 2:
         return Potion.func_188409_a(this.field_146010_n);
      default:
         return 0;
      }
   }

   public void func_174885_b(int var1, int var2) {
      switch(var1) {
      case 0:
         this.field_146012_l = var2;
         break;
      case 1:
         this.field_146013_m = func_184279_f(var2);
         break;
      case 2:
         this.field_146010_n = func_184279_f(var2);
      }

      if (!this.field_145850_b.field_72995_K && var1 == 1 && this.field_146015_k) {
         this.func_205736_a(SoundEvents.field_206941_N);
      }

   }

   public int func_174890_g() {
      return 3;
   }

   public void func_174888_l() {
      this.field_146011_o = ItemStack.field_190927_a;
   }

   public boolean func_145842_c(int var1, int var2) {
      if (var1 == 1) {
         this.func_174908_m();
         return true;
      } else {
         return super.func_145842_c(var1, var2);
      }
   }

   public int[] func_180463_a(EnumFacing var1) {
      return new int[0];
   }

   public boolean func_180462_a(int var1, ItemStack var2, @Nullable EnumFacing var3) {
      return false;
   }

   public boolean func_180461_b(int var1, ItemStack var2, EnumFacing var3) {
      return false;
   }

   static {
      field_146009_a = new Potion[][]{{MobEffects.field_76424_c, MobEffects.field_76422_e}, {MobEffects.field_76429_m, MobEffects.field_76430_j}, {MobEffects.field_76420_g}, {MobEffects.field_76428_l}};
      field_184280_f = (Set)Arrays.stream(field_146009_a).flatMap(Arrays::stream).collect(Collectors.toSet());
   }

   public static class BeamSegment {
      private final float[] field_177266_a;
      private int field_177265_b;

      public BeamSegment(float[] var1) {
         super();
         this.field_177266_a = var1;
         this.field_177265_b = 1;
      }

      protected void func_177262_a() {
         ++this.field_177265_b;
      }

      public float[] func_177263_b() {
         return this.field_177266_a;
      }

      public int func_177264_c() {
         return this.field_177265_b;
      }
   }
}
