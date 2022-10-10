package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.command.arguments.ParticleArgument;
import net.minecraft.init.Particles;
import net.minecraft.init.PotionTypes;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityAreaEffectCloud extends Entity {
   private static final Logger field_195060_a = LogManager.getLogger();
   private static final DataParameter<Float> field_184498_a;
   private static final DataParameter<Integer> field_184499_b;
   private static final DataParameter<Boolean> field_184500_c;
   private static final DataParameter<IParticleData> field_184501_d;
   private PotionType field_184502_e;
   private final List<PotionEffect> field_184503_f;
   private final Map<Entity, Integer> field_184504_g;
   private int field_184505_h;
   private int field_184506_as;
   private int field_184507_at;
   private boolean field_184508_au;
   private int field_184509_av;
   private float field_184510_aw;
   private float field_184511_ax;
   private EntityLivingBase field_184512_ay;
   private UUID field_184513_az;

   public EntityAreaEffectCloud(World var1) {
      super(EntityType.field_200788_b, var1);
      this.field_184502_e = PotionTypes.field_185229_a;
      this.field_184503_f = Lists.newArrayList();
      this.field_184504_g = Maps.newHashMap();
      this.field_184505_h = 600;
      this.field_184506_as = 20;
      this.field_184507_at = 20;
      this.field_70145_X = true;
      this.field_70178_ae = true;
      this.func_184483_a(3.0F);
   }

   public EntityAreaEffectCloud(World var1, double var2, double var4, double var6) {
      this(var1);
      this.func_70107_b(var2, var4, var6);
   }

   protected void func_70088_a() {
      this.func_184212_Q().func_187214_a(field_184499_b, 0);
      this.func_184212_Q().func_187214_a(field_184498_a, 0.5F);
      this.func_184212_Q().func_187214_a(field_184500_c, false);
      this.func_184212_Q().func_187214_a(field_184501_d, Particles.field_197625_r);
   }

   public void func_184483_a(float var1) {
      double var2 = this.field_70165_t;
      double var4 = this.field_70163_u;
      double var6 = this.field_70161_v;
      this.func_70105_a(var1 * 2.0F, 0.5F);
      this.func_70107_b(var2, var4, var6);
      if (!this.field_70170_p.field_72995_K) {
         this.func_184212_Q().func_187227_b(field_184498_a, var1);
      }

   }

   public float func_184490_j() {
      return (Float)this.func_184212_Q().func_187225_a(field_184498_a);
   }

   public void func_184484_a(PotionType var1) {
      this.field_184502_e = var1;
      if (!this.field_184508_au) {
         this.func_190618_C();
      }

   }

   private void func_190618_C() {
      if (this.field_184502_e == PotionTypes.field_185229_a && this.field_184503_f.isEmpty()) {
         this.func_184212_Q().func_187227_b(field_184499_b, 0);
      } else {
         this.func_184212_Q().func_187227_b(field_184499_b, PotionUtils.func_185181_a(PotionUtils.func_185186_a(this.field_184502_e, this.field_184503_f)));
      }

   }

   public void func_184496_a(PotionEffect var1) {
      this.field_184503_f.add(var1);
      if (!this.field_184508_au) {
         this.func_190618_C();
      }

   }

   public int func_184492_k() {
      return (Integer)this.func_184212_Q().func_187225_a(field_184499_b);
   }

   public void func_184482_a(int var1) {
      this.field_184508_au = true;
      this.func_184212_Q().func_187227_b(field_184499_b, var1);
   }

   public IParticleData func_195058_l() {
      return (IParticleData)this.func_184212_Q().func_187225_a(field_184501_d);
   }

   public void func_195059_a(IParticleData var1) {
      this.func_184212_Q().func_187227_b(field_184501_d, var1);
   }

   protected void func_184488_a(boolean var1) {
      this.func_184212_Q().func_187227_b(field_184500_c, var1);
   }

   public boolean func_184497_n() {
      return (Boolean)this.func_184212_Q().func_187225_a(field_184500_c);
   }

   public int func_184489_o() {
      return this.field_184505_h;
   }

   public void func_184486_b(int var1) {
      this.field_184505_h = var1;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      boolean var1 = this.func_184497_n();
      float var2 = this.func_184490_j();
      if (this.field_70170_p.field_72995_K) {
         IParticleData var3 = this.func_195058_l();
         float var6;
         float var7;
         float var8;
         int var10;
         int var11;
         int var12;
         if (var1) {
            if (this.field_70146_Z.nextBoolean()) {
               for(int var4 = 0; var4 < 2; ++var4) {
                  float var5 = this.field_70146_Z.nextFloat() * 6.2831855F;
                  var6 = MathHelper.func_76129_c(this.field_70146_Z.nextFloat()) * 0.2F;
                  var7 = MathHelper.func_76134_b(var5) * var6;
                  var8 = MathHelper.func_76126_a(var5) * var6;
                  if (var3.func_197554_b() == Particles.field_197625_r) {
                     int var9 = this.field_70146_Z.nextBoolean() ? 16777215 : this.func_184492_k();
                     var10 = var9 >> 16 & 255;
                     var11 = var9 >> 8 & 255;
                     var12 = var9 & 255;
                     this.field_70170_p.func_195589_b(var3, this.field_70165_t + (double)var7, this.field_70163_u, this.field_70161_v + (double)var8, (double)((float)var10 / 255.0F), (double)((float)var11 / 255.0F), (double)((float)var12 / 255.0F));
                  } else {
                     this.field_70170_p.func_195589_b(var3, this.field_70165_t + (double)var7, this.field_70163_u, this.field_70161_v + (double)var8, 0.0D, 0.0D, 0.0D);
                  }
               }
            }
         } else {
            float var17 = 3.1415927F * var2 * var2;

            for(int var19 = 0; (float)var19 < var17; ++var19) {
               var6 = this.field_70146_Z.nextFloat() * 6.2831855F;
               var7 = MathHelper.func_76129_c(this.field_70146_Z.nextFloat()) * var2;
               var8 = MathHelper.func_76134_b(var6) * var7;
               float var28 = MathHelper.func_76126_a(var6) * var7;
               if (var3.func_197554_b() == Particles.field_197625_r) {
                  var10 = this.func_184492_k();
                  var11 = var10 >> 16 & 255;
                  var12 = var10 >> 8 & 255;
                  int var13 = var10 & 255;
                  this.field_70170_p.func_195589_b(var3, this.field_70165_t + (double)var8, this.field_70163_u, this.field_70161_v + (double)var28, (double)((float)var11 / 255.0F), (double)((float)var12 / 255.0F), (double)((float)var13 / 255.0F));
               } else {
                  this.field_70170_p.func_195589_b(var3, this.field_70165_t + (double)var8, this.field_70163_u, this.field_70161_v + (double)var28, (0.5D - this.field_70146_Z.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.field_70146_Z.nextDouble()) * 0.15D);
               }
            }
         }
      } else {
         if (this.field_70173_aa >= this.field_184506_as + this.field_184505_h) {
            this.func_70106_y();
            return;
         }

         boolean var16 = this.field_70173_aa < this.field_184506_as;
         if (var1 != var16) {
            this.func_184488_a(var16);
         }

         if (var16) {
            return;
         }

         if (this.field_184511_ax != 0.0F) {
            var2 += this.field_184511_ax;
            if (var2 < 0.5F) {
               this.func_70106_y();
               return;
            }

            this.func_184483_a(var2);
         }

         if (this.field_70173_aa % 5 == 0) {
            Iterator var18 = this.field_184504_g.entrySet().iterator();

            while(var18.hasNext()) {
               Entry var21 = (Entry)var18.next();
               if (this.field_70173_aa >= (Integer)var21.getValue()) {
                  var18.remove();
               }
            }

            ArrayList var20 = Lists.newArrayList();
            Iterator var22 = this.field_184502_e.func_185170_a().iterator();

            while(var22.hasNext()) {
               PotionEffect var23 = (PotionEffect)var22.next();
               var20.add(new PotionEffect(var23.func_188419_a(), var23.func_76459_b() / 4, var23.func_76458_c(), var23.func_82720_e(), var23.func_188418_e()));
            }

            var20.addAll(this.field_184503_f);
            if (var20.isEmpty()) {
               this.field_184504_g.clear();
            } else {
               List var24 = this.field_70170_p.func_72872_a(EntityLivingBase.class, this.func_174813_aQ());
               if (!var24.isEmpty()) {
                  Iterator var25 = var24.iterator();

                  while(true) {
                     EntityLivingBase var26;
                     double var30;
                     do {
                        do {
                           do {
                              if (!var25.hasNext()) {
                                 return;
                              }

                              var26 = (EntityLivingBase)var25.next();
                           } while(this.field_184504_g.containsKey(var26));
                        } while(!var26.func_184603_cC());

                        double var27 = var26.field_70165_t - this.field_70165_t;
                        double var29 = var26.field_70161_v - this.field_70161_v;
                        var30 = var27 * var27 + var29 * var29;
                     } while(var30 > (double)(var2 * var2));

                     this.field_184504_g.put(var26, this.field_70173_aa + this.field_184507_at);
                     Iterator var14 = var20.iterator();

                     while(var14.hasNext()) {
                        PotionEffect var15 = (PotionEffect)var14.next();
                        if (var15.func_188419_a().func_76403_b()) {
                           var15.func_188419_a().func_180793_a(this, this.func_184494_w(), var26, var15.func_76458_c(), 0.5D);
                        } else {
                           var26.func_195064_c(new PotionEffect(var15));
                        }
                     }

                     if (this.field_184510_aw != 0.0F) {
                        var2 += this.field_184510_aw;
                        if (var2 < 0.5F) {
                           this.func_70106_y();
                           return;
                        }

                        this.func_184483_a(var2);
                     }

                     if (this.field_184509_av != 0) {
                        this.field_184505_h += this.field_184509_av;
                        if (this.field_184505_h <= 0) {
                           this.func_70106_y();
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

   }

   public void func_184495_b(float var1) {
      this.field_184510_aw = var1;
   }

   public void func_184487_c(float var1) {
      this.field_184511_ax = var1;
   }

   public void func_184485_d(int var1) {
      this.field_184506_as = var1;
   }

   public void func_184481_a(@Nullable EntityLivingBase var1) {
      this.field_184512_ay = var1;
      this.field_184513_az = var1 == null ? null : var1.func_110124_au();
   }

   @Nullable
   public EntityLivingBase func_184494_w() {
      if (this.field_184512_ay == null && this.field_184513_az != null && this.field_70170_p instanceof WorldServer) {
         Entity var1 = ((WorldServer)this.field_70170_p).func_175733_a(this.field_184513_az);
         if (var1 instanceof EntityLivingBase) {
            this.field_184512_ay = (EntityLivingBase)var1;
         }
      }

      return this.field_184512_ay;
   }

   protected void func_70037_a(NBTTagCompound var1) {
      this.field_70173_aa = var1.func_74762_e("Age");
      this.field_184505_h = var1.func_74762_e("Duration");
      this.field_184506_as = var1.func_74762_e("WaitTime");
      this.field_184507_at = var1.func_74762_e("ReapplicationDelay");
      this.field_184509_av = var1.func_74762_e("DurationOnUse");
      this.field_184510_aw = var1.func_74760_g("RadiusOnUse");
      this.field_184511_ax = var1.func_74760_g("RadiusPerTick");
      this.func_184483_a(var1.func_74760_g("Radius"));
      this.field_184513_az = var1.func_186857_a("OwnerUUID");
      if (var1.func_150297_b("Particle", 8)) {
         try {
            this.func_195059_a(ParticleArgument.func_197189_a(new StringReader(var1.func_74779_i("Particle"))));
         } catch (CommandSyntaxException var5) {
            field_195060_a.warn("Couldn't load custom particle {}", var1.func_74779_i("Particle"), var5);
         }
      }

      if (var1.func_150297_b("Color", 99)) {
         this.func_184482_a(var1.func_74762_e("Color"));
      }

      if (var1.func_150297_b("Potion", 8)) {
         this.func_184484_a(PotionUtils.func_185187_c(var1));
      }

      if (var1.func_150297_b("Effects", 9)) {
         NBTTagList var2 = var1.func_150295_c("Effects", 10);
         this.field_184503_f.clear();

         for(int var3 = 0; var3 < var2.size(); ++var3) {
            PotionEffect var4 = PotionEffect.func_82722_b(var2.func_150305_b(var3));
            if (var4 != null) {
               this.func_184496_a(var4);
            }
         }
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      var1.func_74768_a("Age", this.field_70173_aa);
      var1.func_74768_a("Duration", this.field_184505_h);
      var1.func_74768_a("WaitTime", this.field_184506_as);
      var1.func_74768_a("ReapplicationDelay", this.field_184507_at);
      var1.func_74768_a("DurationOnUse", this.field_184509_av);
      var1.func_74776_a("RadiusOnUse", this.field_184510_aw);
      var1.func_74776_a("RadiusPerTick", this.field_184511_ax);
      var1.func_74776_a("Radius", this.func_184490_j());
      var1.func_74778_a("Particle", this.func_195058_l().func_197555_a());
      if (this.field_184513_az != null) {
         var1.func_186854_a("OwnerUUID", this.field_184513_az);
      }

      if (this.field_184508_au) {
         var1.func_74768_a("Color", this.func_184492_k());
      }

      if (this.field_184502_e != PotionTypes.field_185229_a && this.field_184502_e != null) {
         var1.func_74778_a("Potion", IRegistry.field_212621_j.func_177774_c(this.field_184502_e).toString());
      }

      if (!this.field_184503_f.isEmpty()) {
         NBTTagList var2 = new NBTTagList();
         Iterator var3 = this.field_184503_f.iterator();

         while(var3.hasNext()) {
            PotionEffect var4 = (PotionEffect)var3.next();
            var2.add((INBTBase)var4.func_82719_a(new NBTTagCompound()));
         }

         var1.func_74782_a("Effects", var2);
      }

   }

   public void func_184206_a(DataParameter<?> var1) {
      if (field_184498_a.equals(var1)) {
         this.func_184483_a(this.func_184490_j());
      }

      super.func_184206_a(var1);
   }

   public EnumPushReaction func_184192_z() {
      return EnumPushReaction.IGNORE;
   }

   static {
      field_184498_a = EntityDataManager.func_187226_a(EntityAreaEffectCloud.class, DataSerializers.field_187193_c);
      field_184499_b = EntityDataManager.func_187226_a(EntityAreaEffectCloud.class, DataSerializers.field_187192_b);
      field_184500_c = EntityDataManager.func_187226_a(EntityAreaEffectCloud.class, DataSerializers.field_187198_h);
      field_184501_d = EntityDataManager.func_187226_a(EntityAreaEffectCloud.class, DataSerializers.field_198166_i);
   }
}
