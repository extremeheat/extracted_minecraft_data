package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class TileEntityConduit extends TileEntity implements ITickable {
   private static final Block[] field_205042_e;
   public int field_205041_a;
   private float field_205043_f;
   private boolean field_205045_h;
   private boolean field_207738_h;
   private final List<BlockPos> field_205046_i;
   private EntityLivingBase field_205047_j;
   private UUID field_205048_k;
   private long field_205740_k;

   public TileEntityConduit() {
      this(TileEntityType.field_205166_z);
   }

   public TileEntityConduit(TileEntityType<?> var1) {
      super(var1);
      this.field_205046_i = Lists.newArrayList();
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      if (var1.func_74764_b("target_uuid")) {
         this.field_205048_k = NBTUtil.func_186860_b(var1.func_74775_l("target_uuid"));
      } else {
         this.field_205048_k = null;
      }

   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      if (this.field_205047_j != null) {
         var1.func_74782_a("target_uuid", NBTUtil.func_186862_a(this.field_205047_j.func_110124_au()));
      }

      return var1;
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 5, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      return this.func_189515_b(new NBTTagCompound());
   }

   public void func_73660_a() {
      ++this.field_205041_a;
      long var1 = this.field_145850_b.func_82737_E();
      if (var1 % 40L == 0L) {
         this.func_205739_a(this.func_205038_d());
         if (!this.field_145850_b.field_72995_K && this.func_205039_c()) {
            this.func_205030_f();
            this.func_205031_h();
         }
      }

      if (var1 % 80L == 0L && this.func_205039_c()) {
         this.func_205738_a(SoundEvents.field_206934_aN);
      }

      if (var1 > this.field_205740_k && this.func_205039_c()) {
         this.field_205740_k = var1 + 60L + (long)this.field_145850_b.func_201674_k().nextInt(40);
         this.func_205738_a(SoundEvents.field_206935_aO);
      }

      if (this.field_145850_b.field_72995_K) {
         this.func_205040_i();
         this.func_205037_l();
         if (this.func_205039_c()) {
            ++this.field_205043_f;
         }
      }

   }

   private boolean func_205038_d() {
      this.field_205046_i.clear();

      int var1;
      int var2;
      int var3;
      for(var1 = -1; var1 <= 1; ++var1) {
         for(var2 = -1; var2 <= 1; ++var2) {
            for(var3 = -1; var3 <= 1; ++var3) {
               BlockPos var4 = this.field_174879_c.func_177982_a(var1, var2, var3);
               if (!this.field_145850_b.func_201671_F(var4)) {
                  return false;
               }
            }
         }
      }

      for(var1 = -2; var1 <= 2; ++var1) {
         for(var2 = -2; var2 <= 2; ++var2) {
            for(var3 = -2; var3 <= 2; ++var3) {
               int var13 = Math.abs(var1);
               int var5 = Math.abs(var2);
               int var6 = Math.abs(var3);
               if ((var13 > 1 || var5 > 1 || var6 > 1) && (var1 == 0 && (var5 == 2 || var6 == 2) || var2 == 0 && (var13 == 2 || var6 == 2) || var3 == 0 && (var13 == 2 || var5 == 2))) {
                  BlockPos var7 = this.field_174879_c.func_177982_a(var1, var2, var3);
                  IBlockState var8 = this.field_145850_b.func_180495_p(var7);
                  Block[] var9 = field_205042_e;
                  int var10 = var9.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     Block var12 = var9[var11];
                     if (var8.func_177230_c() == var12) {
                        this.field_205046_i.add(var7);
                     }
                  }
               }
            }
         }
      }

      this.func_207736_b(this.field_205046_i.size() >= 42);
      return this.field_205046_i.size() >= 16;
   }

   private void func_205030_f() {
      int var1 = this.field_205046_i.size();
      int var2 = var1 / 7 * 16;
      int var3 = this.field_174879_c.func_177958_n();
      int var4 = this.field_174879_c.func_177956_o();
      int var5 = this.field_174879_c.func_177952_p();
      AxisAlignedBB var6 = (new AxisAlignedBB((double)var3, (double)var4, (double)var5, (double)(var3 + 1), (double)(var4 + 1), (double)(var5 + 1))).func_186662_g((double)var2).func_72321_a(0.0D, (double)this.field_145850_b.func_72800_K(), 0.0D);
      List var7 = this.field_145850_b.func_72872_a(EntityPlayer.class, var6);
      if (!var7.isEmpty()) {
         Iterator var8 = var7.iterator();

         while(var8.hasNext()) {
            EntityPlayer var9 = (EntityPlayer)var8.next();
            if (this.field_174879_c.func_196233_m(new BlockPos(var9)) <= (double)var2 && var9.func_70026_G()) {
               var9.func_195064_c(new PotionEffect(MobEffects.field_205136_C, 260, 0, true, true));
            }
         }

      }
   }

   private void func_205031_h() {
      EntityLivingBase var1 = this.field_205047_j;
      int var2 = this.field_205046_i.size();
      if (var2 < 42) {
         this.field_205047_j = null;
      } else if (this.field_205047_j == null && this.field_205048_k != null) {
         this.field_205047_j = this.func_205035_k();
         this.field_205048_k = null;
      } else if (this.field_205047_j == null) {
         List var3 = this.field_145850_b.func_175647_a(EntityLivingBase.class, this.func_205034_j(), (var0) -> {
            return var0 instanceof IMob && var0.func_70026_G();
         });
         if (!var3.isEmpty()) {
            this.field_205047_j = (EntityLivingBase)var3.get(this.field_145850_b.field_73012_v.nextInt(var3.size()));
         }
      } else if (!this.field_205047_j.func_70089_S() || this.field_174879_c.func_196233_m(new BlockPos(this.field_205047_j)) > 8.0D) {
         this.field_205047_j = null;
      }

      if (this.field_205047_j != null) {
         this.field_145850_b.func_184148_a((EntityPlayer)null, this.field_205047_j.field_70165_t, this.field_205047_j.field_70163_u, this.field_205047_j.field_70161_v, SoundEvents.field_206936_aP, SoundCategory.BLOCKS, 1.0F, 1.0F);
         this.field_205047_j.func_70097_a(DamageSource.field_76376_m, 4.0F);
      }

      if (var1 != this.field_205047_j) {
         IBlockState var4 = this.func_195044_w();
         this.field_145850_b.func_184138_a(this.field_174879_c, var4, var4, 2);
      }

   }

   private void func_205040_i() {
      if (this.field_205048_k == null) {
         this.field_205047_j = null;
      } else if (this.field_205047_j == null || !this.field_205047_j.func_110124_au().equals(this.field_205048_k)) {
         this.field_205047_j = this.func_205035_k();
         if (this.field_205047_j == null) {
            this.field_205048_k = null;
         }
      }

   }

   private AxisAlignedBB func_205034_j() {
      int var1 = this.field_174879_c.func_177958_n();
      int var2 = this.field_174879_c.func_177956_o();
      int var3 = this.field_174879_c.func_177952_p();
      return (new AxisAlignedBB((double)var1, (double)var2, (double)var3, (double)(var1 + 1), (double)(var2 + 1), (double)(var3 + 1))).func_186662_g(8.0D);
   }

   @Nullable
   private EntityLivingBase func_205035_k() {
      List var1 = this.field_145850_b.func_175647_a(EntityLivingBase.class, this.func_205034_j(), (var1x) -> {
         return var1x.func_110124_au().equals(this.field_205048_k);
      });
      return var1.size() == 1 ? (EntityLivingBase)var1.get(0) : null;
   }

   private void func_205037_l() {
      Random var1 = this.field_145850_b.field_73012_v;
      float var2 = MathHelper.func_76126_a((float)(this.field_205041_a + 35) * 0.1F) / 2.0F + 0.5F;
      var2 = (var2 * var2 + var2) * 0.3F;
      Vec3d var3 = new Vec3d((double)((float)this.field_174879_c.func_177958_n() + 0.5F), (double)((float)this.field_174879_c.func_177956_o() + 1.5F + var2), (double)((float)this.field_174879_c.func_177952_p() + 0.5F));
      Iterator var4 = this.field_205046_i.iterator();

      float var6;
      float var7;
      while(var4.hasNext()) {
         BlockPos var5 = (BlockPos)var4.next();
         if (var1.nextInt(50) == 0) {
            var6 = -0.5F + var1.nextFloat();
            var7 = -2.0F + var1.nextFloat();
            float var8 = -0.5F + var1.nextFloat();
            BlockPos var9 = var5.func_177973_b(this.field_174879_c);
            Vec3d var10 = (new Vec3d((double)var6, (double)var7, (double)var8)).func_72441_c((double)var9.func_177958_n(), (double)var9.func_177956_o(), (double)var9.func_177952_p());
            this.field_145850_b.func_195594_a(Particles.field_205167_W, var3.field_72450_a, var3.field_72448_b, var3.field_72449_c, var10.field_72450_a, var10.field_72448_b, var10.field_72449_c);
         }
      }

      if (this.field_205047_j != null) {
         Vec3d var11 = new Vec3d(this.field_205047_j.field_70165_t, this.field_205047_j.field_70163_u + (double)this.field_205047_j.func_70047_e(), this.field_205047_j.field_70161_v);
         float var12 = (-0.5F + var1.nextFloat()) * (3.0F + this.field_205047_j.field_70130_N);
         var6 = -1.0F + var1.nextFloat() * this.field_205047_j.field_70131_O;
         var7 = (-0.5F + var1.nextFloat()) * (3.0F + this.field_205047_j.field_70130_N);
         Vec3d var13 = new Vec3d((double)var12, (double)var6, (double)var7);
         this.field_145850_b.func_195594_a(Particles.field_205167_W, var11.field_72450_a, var11.field_72448_b, var11.field_72449_c, var13.field_72450_a, var13.field_72448_b, var13.field_72449_c);
      }

   }

   public boolean func_205039_c() {
      return this.field_205045_h;
   }

   public boolean func_207737_d() {
      return this.field_207738_h;
   }

   private void func_205739_a(boolean var1) {
      if (var1 != this.field_205045_h) {
         this.func_205738_a(var1 ? SoundEvents.field_206933_aM : SoundEvents.field_206937_aQ);
      }

      this.field_205045_h = var1;
   }

   private void func_207736_b(boolean var1) {
      this.field_207738_h = var1;
   }

   public float func_205036_a(float var1) {
      return (this.field_205043_f + var1) * -0.0375F;
   }

   public void func_205738_a(SoundEvent var1) {
      this.field_145850_b.func_184133_a((EntityPlayer)null, this.field_174879_c, var1, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   static {
      field_205042_e = new Block[]{Blocks.field_180397_cI, Blocks.field_196779_gQ, Blocks.field_180398_cJ, Blocks.field_196781_gR};
   }
}
