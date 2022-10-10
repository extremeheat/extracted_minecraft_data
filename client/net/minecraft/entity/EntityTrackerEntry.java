package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityDragonFireball;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.entity.projectile.EntityTrident;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   private static final Logger field_151262_p = LogManager.getLogger();
   private final Entity field_73132_a;
   private final int field_73130_b;
   private int field_187262_f;
   private final int field_73131_c;
   private long field_73128_d;
   private long field_73129_e;
   private long field_73126_f;
   private int field_73127_g;
   private int field_73139_h;
   private int field_73140_i;
   private double field_73137_j;
   private double field_73138_k;
   private double field_73135_l;
   public int field_73136_m;
   private double field_73147_p;
   private double field_73146_q;
   private double field_73145_r;
   private boolean field_73144_s;
   private final boolean field_73143_t;
   private int field_73142_u;
   private List<Entity> field_187263_w = Collections.emptyList();
   private boolean field_73141_v;
   private boolean field_180234_y;
   public boolean field_73133_n;
   private final Set<EntityPlayerMP> field_73134_o = Sets.newHashSet();

   public EntityTrackerEntry(Entity var1, int var2, int var3, int var4, boolean var5) {
      super();
      this.field_73132_a = var1;
      this.field_73130_b = var2;
      this.field_187262_f = var3;
      this.field_73131_c = var4;
      this.field_73143_t = var5;
      this.field_73128_d = EntityTracker.func_187253_a(var1.field_70165_t);
      this.field_73129_e = EntityTracker.func_187253_a(var1.field_70163_u);
      this.field_73126_f = EntityTracker.func_187253_a(var1.field_70161_v);
      this.field_73127_g = MathHelper.func_76141_d(var1.field_70177_z * 256.0F / 360.0F);
      this.field_73139_h = MathHelper.func_76141_d(var1.field_70125_A * 256.0F / 360.0F);
      this.field_73140_i = MathHelper.func_76141_d(var1.func_70079_am() * 256.0F / 360.0F);
      this.field_180234_y = var1.field_70122_E;
   }

   public boolean equals(Object var1) {
      if (var1 instanceof EntityTrackerEntry) {
         return ((EntityTrackerEntry)var1).field_73132_a.func_145782_y() == this.field_73132_a.func_145782_y();
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_73132_a.func_145782_y();
   }

   public void func_73122_a(List<EntityPlayer> var1) {
      this.field_73133_n = false;
      if (!this.field_73144_s || this.field_73132_a.func_70092_e(this.field_73147_p, this.field_73146_q, this.field_73145_r) > 16.0D) {
         this.field_73147_p = this.field_73132_a.field_70165_t;
         this.field_73146_q = this.field_73132_a.field_70163_u;
         this.field_73145_r = this.field_73132_a.field_70161_v;
         this.field_73144_s = true;
         this.field_73133_n = true;
         this.func_73125_b(var1);
      }

      List var2 = this.field_73132_a.func_184188_bt();
      if (!var2.equals(this.field_187263_w)) {
         this.field_187263_w = var2;
         this.func_151259_a(new SPacketSetPassengers(this.field_73132_a));
      }

      if (this.field_73132_a instanceof EntityItemFrame && this.field_73136_m % 10 == 0) {
         EntityItemFrame var3 = (EntityItemFrame)this.field_73132_a;
         ItemStack var4 = var3.func_82335_i();
         if (var4.func_77973_b() instanceof ItemMap) {
            MapData var5 = ItemMap.func_195950_a(var4, this.field_73132_a.field_70170_p);
            Iterator var6 = var1.iterator();

            while(var6.hasNext()) {
               EntityPlayer var7 = (EntityPlayer)var6.next();
               EntityPlayerMP var8 = (EntityPlayerMP)var7;
               var5.func_76191_a(var8, var4);
               Packet var9 = ((ItemMap)var4.func_77973_b()).func_150911_c(var4, this.field_73132_a.field_70170_p, var8);
               if (var9 != null) {
                  var8.field_71135_a.func_147359_a(var9);
               }
            }
         }

         this.func_111190_b();
      }

      if (this.field_73136_m % this.field_73131_c == 0 || this.field_73132_a.field_70160_al || this.field_73132_a.func_184212_Q().func_187223_a()) {
         int var32;
         if (this.field_73132_a.func_184218_aH()) {
            var32 = MathHelper.func_76141_d(this.field_73132_a.field_70177_z * 256.0F / 360.0F);
            int var33 = MathHelper.func_76141_d(this.field_73132_a.field_70125_A * 256.0F / 360.0F);
            boolean var35 = Math.abs(var32 - this.field_73127_g) >= 1 || Math.abs(var33 - this.field_73139_h) >= 1;
            if (var35) {
               this.func_151259_a(new SPacketEntity.Look(this.field_73132_a.func_145782_y(), (byte)var32, (byte)var33, this.field_73132_a.field_70122_E));
               this.field_73127_g = var32;
               this.field_73139_h = var33;
            }

            this.field_73128_d = EntityTracker.func_187253_a(this.field_73132_a.field_70165_t);
            this.field_73129_e = EntityTracker.func_187253_a(this.field_73132_a.field_70163_u);
            this.field_73126_f = EntityTracker.func_187253_a(this.field_73132_a.field_70161_v);
            this.func_111190_b();
            this.field_73141_v = true;
         } else {
            ++this.field_73142_u;
            long var31 = EntityTracker.func_187253_a(this.field_73132_a.field_70165_t);
            long var34 = EntityTracker.func_187253_a(this.field_73132_a.field_70163_u);
            long var36 = EntityTracker.func_187253_a(this.field_73132_a.field_70161_v);
            int var37 = MathHelper.func_76141_d(this.field_73132_a.field_70177_z * 256.0F / 360.0F);
            int var10 = MathHelper.func_76141_d(this.field_73132_a.field_70125_A * 256.0F / 360.0F);
            long var11 = var31 - this.field_73128_d;
            long var13 = var34 - this.field_73129_e;
            long var15 = var36 - this.field_73126_f;
            Object var17 = null;
            boolean var18 = var11 * var11 + var13 * var13 + var15 * var15 >= 128L || this.field_73136_m % 60 == 0;
            boolean var19 = Math.abs(var37 - this.field_73127_g) >= 1 || Math.abs(var10 - this.field_73139_h) >= 1;
            if (this.field_73136_m > 0 || this.field_73132_a instanceof EntityArrow) {
               if (var11 >= -32768L && var11 < 32768L && var13 >= -32768L && var13 < 32768L && var15 >= -32768L && var15 < 32768L && this.field_73142_u <= 400 && !this.field_73141_v && this.field_180234_y == this.field_73132_a.field_70122_E) {
                  if ((!var18 || !var19) && !(this.field_73132_a instanceof EntityArrow)) {
                     if (var18) {
                        var17 = new SPacketEntity.RelMove(this.field_73132_a.func_145782_y(), var11, var13, var15, this.field_73132_a.field_70122_E);
                     } else if (var19) {
                        var17 = new SPacketEntity.Look(this.field_73132_a.func_145782_y(), (byte)var37, (byte)var10, this.field_73132_a.field_70122_E);
                     }
                  } else {
                     var17 = new SPacketEntity.Move(this.field_73132_a.func_145782_y(), var11, var13, var15, (byte)var37, (byte)var10, this.field_73132_a.field_70122_E);
                  }
               } else {
                  this.field_180234_y = this.field_73132_a.field_70122_E;
                  this.field_73142_u = 0;
                  this.func_187261_c();
                  var17 = new SPacketEntityTeleport(this.field_73132_a);
               }
            }

            boolean var20 = this.field_73143_t || this.field_73132_a.field_70160_al;
            if (this.field_73132_a instanceof EntityLivingBase && ((EntityLivingBase)this.field_73132_a).func_184613_cA()) {
               var20 = true;
            }

            if (var20 && this.field_73136_m > 0) {
               double var21 = this.field_73132_a.field_70159_w - this.field_73137_j;
               double var23 = this.field_73132_a.field_70181_x - this.field_73138_k;
               double var25 = this.field_73132_a.field_70179_y - this.field_73135_l;
               double var27 = 0.02D;
               double var29 = var21 * var21 + var23 * var23 + var25 * var25;
               if (var29 > 4.0E-4D || var29 > 0.0D && this.field_73132_a.field_70159_w == 0.0D && this.field_73132_a.field_70181_x == 0.0D && this.field_73132_a.field_70179_y == 0.0D) {
                  this.field_73137_j = this.field_73132_a.field_70159_w;
                  this.field_73138_k = this.field_73132_a.field_70181_x;
                  this.field_73135_l = this.field_73132_a.field_70179_y;
                  this.func_151259_a(new SPacketEntityVelocity(this.field_73132_a.func_145782_y(), this.field_73137_j, this.field_73138_k, this.field_73135_l));
               }
            }

            if (var17 != null) {
               this.func_151259_a((Packet)var17);
            }

            this.func_111190_b();
            if (var18) {
               this.field_73128_d = var31;
               this.field_73129_e = var34;
               this.field_73126_f = var36;
            }

            if (var19) {
               this.field_73127_g = var37;
               this.field_73139_h = var10;
            }

            this.field_73141_v = false;
         }

         var32 = MathHelper.func_76141_d(this.field_73132_a.func_70079_am() * 256.0F / 360.0F);
         if (Math.abs(var32 - this.field_73140_i) >= 1) {
            this.func_151259_a(new SPacketEntityHeadLook(this.field_73132_a, (byte)var32));
            this.field_73140_i = var32;
         }

         this.field_73132_a.field_70160_al = false;
      }

      ++this.field_73136_m;
      if (this.field_73132_a.field_70133_I) {
         this.func_151261_b(new SPacketEntityVelocity(this.field_73132_a));
         this.field_73132_a.field_70133_I = false;
      }

   }

   private void func_111190_b() {
      EntityDataManager var1 = this.field_73132_a.func_184212_Q();
      if (var1.func_187223_a()) {
         this.func_151261_b(new SPacketEntityMetadata(this.field_73132_a.func_145782_y(), var1, false));
      }

      if (this.field_73132_a instanceof EntityLivingBase) {
         AttributeMap var2 = (AttributeMap)((EntityLivingBase)this.field_73132_a).func_110140_aT();
         Set var3 = var2.func_111161_b();
         if (!var3.isEmpty()) {
            this.func_151261_b(new SPacketEntityProperties(this.field_73132_a.func_145782_y(), var3));
         }

         var3.clear();
      }

   }

   public void func_151259_a(Packet<?> var1) {
      Iterator var2 = this.field_73134_o.iterator();

      while(var2.hasNext()) {
         EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
         var3.field_71135_a.func_147359_a(var1);
      }

   }

   public void func_151261_b(Packet<?> var1) {
      this.func_151259_a(var1);
      if (this.field_73132_a instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.field_73132_a).field_71135_a.func_147359_a(var1);
      }

   }

   public void func_73119_a() {
      Iterator var1 = this.field_73134_o.iterator();

      while(var1.hasNext()) {
         EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
         this.field_73132_a.func_184203_c(var2);
         var2.func_152339_d(this.field_73132_a);
      }

   }

   public void func_73118_a(EntityPlayerMP var1) {
      if (this.field_73134_o.contains(var1)) {
         this.field_73132_a.func_184203_c(var1);
         var1.func_152339_d(this.field_73132_a);
         this.field_73134_o.remove(var1);
      }

   }

   public void func_73117_b(EntityPlayerMP var1) {
      if (var1 != this.field_73132_a) {
         if (this.func_180233_c(var1)) {
            if (!this.field_73134_o.contains(var1) && (this.func_73121_d(var1) || this.field_73132_a.field_98038_p)) {
               this.field_73134_o.add(var1);
               Packet var2 = this.func_151260_c();
               var1.field_71135_a.func_147359_a(var2);
               if (!this.field_73132_a.func_184212_Q().func_187228_d()) {
                  var1.field_71135_a.func_147359_a(new SPacketEntityMetadata(this.field_73132_a.func_145782_y(), this.field_73132_a.func_184212_Q(), true));
               }

               boolean var3 = this.field_73143_t;
               if (this.field_73132_a instanceof EntityLivingBase) {
                  AttributeMap var4 = (AttributeMap)((EntityLivingBase)this.field_73132_a).func_110140_aT();
                  Collection var5 = var4.func_111160_c();
                  if (!var5.isEmpty()) {
                     var1.field_71135_a.func_147359_a(new SPacketEntityProperties(this.field_73132_a.func_145782_y(), var5));
                  }

                  if (((EntityLivingBase)this.field_73132_a).func_184613_cA()) {
                     var3 = true;
                  }
               }

               this.field_73137_j = this.field_73132_a.field_70159_w;
               this.field_73138_k = this.field_73132_a.field_70181_x;
               this.field_73135_l = this.field_73132_a.field_70179_y;
               if (var3 && !(var2 instanceof SPacketSpawnMob)) {
                  var1.field_71135_a.func_147359_a(new SPacketEntityVelocity(this.field_73132_a.func_145782_y(), this.field_73132_a.field_70159_w, this.field_73132_a.field_70181_x, this.field_73132_a.field_70179_y));
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  EntityEquipmentSlot[] var9 = EntityEquipmentSlot.values();
                  int var12 = var9.length;

                  for(int var6 = 0; var6 < var12; ++var6) {
                     EntityEquipmentSlot var7 = var9[var6];
                     ItemStack var8 = ((EntityLivingBase)this.field_73132_a).func_184582_a(var7);
                     if (!var8.func_190926_b()) {
                        var1.field_71135_a.func_147359_a(new SPacketEntityEquipment(this.field_73132_a.func_145782_y(), var7, var8));
                     }
                  }
               }

               if (this.field_73132_a instanceof EntityPlayer) {
                  EntityPlayer var10 = (EntityPlayer)this.field_73132_a;
                  if (var10.func_70608_bn()) {
                     var1.field_71135_a.func_147359_a(new SPacketUseBed(var10, new BlockPos(this.field_73132_a)));
                  }
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  EntityLivingBase var11 = (EntityLivingBase)this.field_73132_a;
                  Iterator var13 = var11.func_70651_bq().iterator();

                  while(var13.hasNext()) {
                     PotionEffect var14 = (PotionEffect)var13.next();
                     var1.field_71135_a.func_147359_a(new SPacketEntityEffect(this.field_73132_a.func_145782_y(), var14));
                  }
               }

               if (!this.field_73132_a.func_184188_bt().isEmpty()) {
                  var1.field_71135_a.func_147359_a(new SPacketSetPassengers(this.field_73132_a));
               }

               if (this.field_73132_a.func_184218_aH()) {
                  var1.field_71135_a.func_147359_a(new SPacketSetPassengers(this.field_73132_a.func_184187_bx()));
               }

               this.field_73132_a.func_184178_b(var1);
               var1.func_184848_d(this.field_73132_a);
            }
         } else if (this.field_73134_o.contains(var1)) {
            this.field_73134_o.remove(var1);
            this.field_73132_a.func_184203_c(var1);
            var1.func_152339_d(this.field_73132_a);
         }

      }
   }

   public boolean func_180233_c(EntityPlayerMP var1) {
      double var2 = var1.field_70165_t - (double)this.field_73128_d / 4096.0D;
      double var4 = var1.field_70161_v - (double)this.field_73126_f / 4096.0D;
      int var6 = Math.min(this.field_73130_b, this.field_187262_f);
      return var2 >= (double)(-var6) && var2 <= (double)var6 && var4 >= (double)(-var6) && var4 <= (double)var6 && this.field_73132_a.func_174827_a(var1);
   }

   private boolean func_73121_d(EntityPlayerMP var1) {
      return var1.func_71121_q().func_184164_w().func_72694_a(var1, this.field_73132_a.field_70176_ah, this.field_73132_a.field_70164_aj);
   }

   public void func_73125_b(List<EntityPlayer> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.func_73117_b((EntityPlayerMP)var1.get(var2));
      }

   }

   private Packet<?> func_151260_c() {
      if (this.field_73132_a.field_70128_L) {
         field_151262_p.warn("Fetching addPacket for removed entity");
      }

      if (this.field_73132_a instanceof EntityPlayerMP) {
         return new SPacketSpawnPlayer((EntityPlayer)this.field_73132_a);
      } else if (this.field_73132_a instanceof IAnimal) {
         this.field_73140_i = MathHelper.func_76141_d(this.field_73132_a.func_70079_am() * 256.0F / 360.0F);
         return new SPacketSpawnMob((EntityLivingBase)this.field_73132_a);
      } else if (this.field_73132_a instanceof EntityPainting) {
         return new SPacketSpawnPainting((EntityPainting)this.field_73132_a);
      } else if (this.field_73132_a instanceof EntityItem) {
         return new SPacketSpawnObject(this.field_73132_a, 2, 1);
      } else if (this.field_73132_a instanceof EntityMinecart) {
         EntityMinecart var10 = (EntityMinecart)this.field_73132_a;
         return new SPacketSpawnObject(this.field_73132_a, 10, var10.func_184264_v().func_184956_a());
      } else if (this.field_73132_a instanceof EntityBoat) {
         return new SPacketSpawnObject(this.field_73132_a, 1);
      } else if (this.field_73132_a instanceof EntityXPOrb) {
         return new SPacketSpawnExperienceOrb((EntityXPOrb)this.field_73132_a);
      } else if (this.field_73132_a instanceof EntityFishHook) {
         EntityPlayer var9 = ((EntityFishHook)this.field_73132_a).func_190619_l();
         return new SPacketSpawnObject(this.field_73132_a, 90, var9 == null ? this.field_73132_a.func_145782_y() : var9.func_145782_y());
      } else {
         Entity var8;
         if (this.field_73132_a instanceof EntitySpectralArrow) {
            var8 = ((EntitySpectralArrow)this.field_73132_a).func_212360_k();
            return new SPacketSpawnObject(this.field_73132_a, 91, 1 + (var8 == null ? this.field_73132_a.func_145782_y() : var8.func_145782_y()));
         } else if (this.field_73132_a instanceof EntityTippedArrow) {
            var8 = ((EntityArrow)this.field_73132_a).func_212360_k();
            return new SPacketSpawnObject(this.field_73132_a, 60, 1 + (var8 == null ? this.field_73132_a.func_145782_y() : var8.func_145782_y()));
         } else if (this.field_73132_a instanceof EntitySnowball) {
            return new SPacketSpawnObject(this.field_73132_a, 61);
         } else if (this.field_73132_a instanceof EntityTrident) {
            var8 = ((EntityArrow)this.field_73132_a).func_212360_k();
            return new SPacketSpawnObject(this.field_73132_a, 94, 1 + (var8 == null ? this.field_73132_a.func_145782_y() : var8.func_145782_y()));
         } else if (this.field_73132_a instanceof EntityLlamaSpit) {
            return new SPacketSpawnObject(this.field_73132_a, 68);
         } else if (this.field_73132_a instanceof EntityPotion) {
            return new SPacketSpawnObject(this.field_73132_a, 73);
         } else if (this.field_73132_a instanceof EntityExpBottle) {
            return new SPacketSpawnObject(this.field_73132_a, 75);
         } else if (this.field_73132_a instanceof EntityEnderPearl) {
            return new SPacketSpawnObject(this.field_73132_a, 65);
         } else if (this.field_73132_a instanceof EntityEnderEye) {
            return new SPacketSpawnObject(this.field_73132_a, 72);
         } else if (this.field_73132_a instanceof EntityFireworkRocket) {
            return new SPacketSpawnObject(this.field_73132_a, 76);
         } else if (this.field_73132_a instanceof EntityFireball) {
            EntityFireball var7 = (EntityFireball)this.field_73132_a;
            byte var2 = 63;
            if (this.field_73132_a instanceof EntitySmallFireball) {
               var2 = 64;
            } else if (this.field_73132_a instanceof EntityDragonFireball) {
               var2 = 93;
            } else if (this.field_73132_a instanceof EntityWitherSkull) {
               var2 = 66;
            }

            SPacketSpawnObject var3;
            if (var7.field_70235_a == null) {
               var3 = new SPacketSpawnObject(this.field_73132_a, var2, 0);
            } else {
               var3 = new SPacketSpawnObject(this.field_73132_a, var2, ((EntityFireball)this.field_73132_a).field_70235_a.func_145782_y());
            }

            var3.func_149003_d((int)(var7.field_70232_b * 8000.0D));
            var3.func_149000_e((int)(var7.field_70233_c * 8000.0D));
            var3.func_149007_f((int)(var7.field_70230_d * 8000.0D));
            return var3;
         } else if (this.field_73132_a instanceof EntityShulkerBullet) {
            SPacketSpawnObject var6 = new SPacketSpawnObject(this.field_73132_a, 67, 0);
            var6.func_149003_d((int)(this.field_73132_a.field_70159_w * 8000.0D));
            var6.func_149000_e((int)(this.field_73132_a.field_70181_x * 8000.0D));
            var6.func_149007_f((int)(this.field_73132_a.field_70179_y * 8000.0D));
            return var6;
         } else if (this.field_73132_a instanceof EntityEgg) {
            return new SPacketSpawnObject(this.field_73132_a, 62);
         } else if (this.field_73132_a instanceof EntityEvokerFangs) {
            return new SPacketSpawnObject(this.field_73132_a, 79);
         } else if (this.field_73132_a instanceof EntityTNTPrimed) {
            return new SPacketSpawnObject(this.field_73132_a, 50);
         } else if (this.field_73132_a instanceof EntityEnderCrystal) {
            return new SPacketSpawnObject(this.field_73132_a, 51);
         } else if (this.field_73132_a instanceof EntityFallingBlock) {
            EntityFallingBlock var5 = (EntityFallingBlock)this.field_73132_a;
            return new SPacketSpawnObject(this.field_73132_a, 70, Block.func_196246_j(var5.func_195054_l()));
         } else if (this.field_73132_a instanceof EntityArmorStand) {
            return new SPacketSpawnObject(this.field_73132_a, 78);
         } else if (this.field_73132_a instanceof EntityItemFrame) {
            EntityItemFrame var4 = (EntityItemFrame)this.field_73132_a;
            return new SPacketSpawnObject(this.field_73132_a, 71, var4.field_174860_b.func_176745_a(), var4.func_174857_n());
         } else if (this.field_73132_a instanceof EntityLeashKnot) {
            EntityLeashKnot var1 = (EntityLeashKnot)this.field_73132_a;
            return new SPacketSpawnObject(this.field_73132_a, 77, 0, var1.func_174857_n());
         } else if (this.field_73132_a instanceof EntityAreaEffectCloud) {
            return new SPacketSpawnObject(this.field_73132_a, 3);
         } else {
            throw new IllegalArgumentException("Don't know how to add " + this.field_73132_a.getClass() + "!");
         }
      }
   }

   public void func_73123_c(EntityPlayerMP var1) {
      if (this.field_73134_o.contains(var1)) {
         this.field_73134_o.remove(var1);
         this.field_73132_a.func_184203_c(var1);
         var1.func_152339_d(this.field_73132_a);
      }

   }

   public Entity func_187260_b() {
      return this.field_73132_a;
   }

   public void func_187259_a(int var1) {
      this.field_187262_f = var1;
   }

   public void func_187261_c() {
      this.field_73144_s = false;
   }
}
