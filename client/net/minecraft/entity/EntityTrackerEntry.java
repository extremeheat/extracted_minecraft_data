package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
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
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   private static final Logger field_151262_p = LogManager.getLogger();
   public Entity field_73132_a;
   public int field_73130_b;
   public int field_73131_c;
   public int field_73128_d;
   public int field_73129_e;
   public int field_73126_f;
   public int field_73127_g;
   public int field_73139_h;
   public int field_73140_i;
   public double field_73137_j;
   public double field_73138_k;
   public double field_73135_l;
   public int field_73136_m;
   private double field_73147_p;
   private double field_73146_q;
   private double field_73145_r;
   private boolean field_73144_s;
   private boolean field_73143_t;
   private int field_73142_u;
   private Entity field_85178_v;
   private boolean field_73141_v;
   private boolean field_180234_y;
   public boolean field_73133_n;
   public Set<EntityPlayerMP> field_73134_o = Sets.newHashSet();

   public EntityTrackerEntry(Entity var1, int var2, int var3, boolean var4) {
      super();
      this.field_73132_a = var1;
      this.field_73130_b = var2;
      this.field_73131_c = var3;
      this.field_73143_t = var4;
      this.field_73128_d = MathHelper.func_76128_c(var1.field_70165_t * 32.0D);
      this.field_73129_e = MathHelper.func_76128_c(var1.field_70163_u * 32.0D);
      this.field_73126_f = MathHelper.func_76128_c(var1.field_70161_v * 32.0D);
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

      if (this.field_85178_v != this.field_73132_a.field_70154_o || this.field_73132_a.field_70154_o != null && this.field_73136_m % 60 == 0) {
         this.field_85178_v = this.field_73132_a.field_70154_o;
         this.func_151259_a(new S1BPacketEntityAttach(0, this.field_73132_a, this.field_73132_a.field_70154_o));
      }

      if (this.field_73132_a instanceof EntityItemFrame && this.field_73136_m % 10 == 0) {
         EntityItemFrame var2 = (EntityItemFrame)this.field_73132_a;
         ItemStack var3 = var2.func_82335_i();
         if (var3 != null && var3.func_77973_b() instanceof ItemMap) {
            MapData var4 = Items.field_151098_aY.func_77873_a(var3, this.field_73132_a.field_70170_p);
            Iterator var5 = var1.iterator();

            while(var5.hasNext()) {
               EntityPlayer var6 = (EntityPlayer)var5.next();
               EntityPlayerMP var7 = (EntityPlayerMP)var6;
               var4.func_76191_a(var7, var3);
               Packet var8 = Items.field_151098_aY.func_150911_c(var3, this.field_73132_a.field_70170_p, var7);
               if (var8 != null) {
                  var7.field_71135_a.func_147359_a(var8);
               }
            }
         }

         this.func_111190_b();
      }

      if (this.field_73136_m % this.field_73131_c == 0 || this.field_73132_a.field_70160_al || this.field_73132_a.func_70096_w().func_75684_a()) {
         int var23;
         int var24;
         if (this.field_73132_a.field_70154_o == null) {
            ++this.field_73142_u;
            var23 = MathHelper.func_76128_c(this.field_73132_a.field_70165_t * 32.0D);
            var24 = MathHelper.func_76128_c(this.field_73132_a.field_70163_u * 32.0D);
            int var26 = MathHelper.func_76128_c(this.field_73132_a.field_70161_v * 32.0D);
            int var27 = MathHelper.func_76141_d(this.field_73132_a.field_70177_z * 256.0F / 360.0F);
            int var28 = MathHelper.func_76141_d(this.field_73132_a.field_70125_A * 256.0F / 360.0F);
            int var29 = var23 - this.field_73128_d;
            int var30 = var24 - this.field_73129_e;
            int var9 = var26 - this.field_73126_f;
            Object var10 = null;
            boolean var11 = Math.abs(var29) >= 4 || Math.abs(var30) >= 4 || Math.abs(var9) >= 4 || this.field_73136_m % 60 == 0;
            boolean var12 = Math.abs(var27 - this.field_73127_g) >= 4 || Math.abs(var28 - this.field_73139_h) >= 4;
            if (this.field_73136_m > 0 || this.field_73132_a instanceof EntityArrow) {
               if (var29 >= -128 && var29 < 128 && var30 >= -128 && var30 < 128 && var9 >= -128 && var9 < 128 && this.field_73142_u <= 400 && !this.field_73141_v && this.field_180234_y == this.field_73132_a.field_70122_E) {
                  if ((!var11 || !var12) && !(this.field_73132_a instanceof EntityArrow)) {
                     if (var11) {
                        var10 = new S14PacketEntity.S15PacketEntityRelMove(this.field_73132_a.func_145782_y(), (byte)var29, (byte)var30, (byte)var9, this.field_73132_a.field_70122_E);
                     } else if (var12) {
                        var10 = new S14PacketEntity.S16PacketEntityLook(this.field_73132_a.func_145782_y(), (byte)var27, (byte)var28, this.field_73132_a.field_70122_E);
                     }
                  } else {
                     var10 = new S14PacketEntity.S17PacketEntityLookMove(this.field_73132_a.func_145782_y(), (byte)var29, (byte)var30, (byte)var9, (byte)var27, (byte)var28, this.field_73132_a.field_70122_E);
                  }
               } else {
                  this.field_180234_y = this.field_73132_a.field_70122_E;
                  this.field_73142_u = 0;
                  var10 = new S18PacketEntityTeleport(this.field_73132_a.func_145782_y(), var23, var24, var26, (byte)var27, (byte)var28, this.field_73132_a.field_70122_E);
               }
            }

            if (this.field_73143_t) {
               double var13 = this.field_73132_a.field_70159_w - this.field_73137_j;
               double var15 = this.field_73132_a.field_70181_x - this.field_73138_k;
               double var17 = this.field_73132_a.field_70179_y - this.field_73135_l;
               double var19 = 0.02D;
               double var21 = var13 * var13 + var15 * var15 + var17 * var17;
               if (var21 > var19 * var19 || var21 > 0.0D && this.field_73132_a.field_70159_w == 0.0D && this.field_73132_a.field_70181_x == 0.0D && this.field_73132_a.field_70179_y == 0.0D) {
                  this.field_73137_j = this.field_73132_a.field_70159_w;
                  this.field_73138_k = this.field_73132_a.field_70181_x;
                  this.field_73135_l = this.field_73132_a.field_70179_y;
                  this.func_151259_a(new S12PacketEntityVelocity(this.field_73132_a.func_145782_y(), this.field_73137_j, this.field_73138_k, this.field_73135_l));
               }
            }

            if (var10 != null) {
               this.func_151259_a((Packet)var10);
            }

            this.func_111190_b();
            if (var11) {
               this.field_73128_d = var23;
               this.field_73129_e = var24;
               this.field_73126_f = var26;
            }

            if (var12) {
               this.field_73127_g = var27;
               this.field_73139_h = var28;
            }

            this.field_73141_v = false;
         } else {
            var23 = MathHelper.func_76141_d(this.field_73132_a.field_70177_z * 256.0F / 360.0F);
            var24 = MathHelper.func_76141_d(this.field_73132_a.field_70125_A * 256.0F / 360.0F);
            boolean var25 = Math.abs(var23 - this.field_73127_g) >= 4 || Math.abs(var24 - this.field_73139_h) >= 4;
            if (var25) {
               this.func_151259_a(new S14PacketEntity.S16PacketEntityLook(this.field_73132_a.func_145782_y(), (byte)var23, (byte)var24, this.field_73132_a.field_70122_E));
               this.field_73127_g = var23;
               this.field_73139_h = var24;
            }

            this.field_73128_d = MathHelper.func_76128_c(this.field_73132_a.field_70165_t * 32.0D);
            this.field_73129_e = MathHelper.func_76128_c(this.field_73132_a.field_70163_u * 32.0D);
            this.field_73126_f = MathHelper.func_76128_c(this.field_73132_a.field_70161_v * 32.0D);
            this.func_111190_b();
            this.field_73141_v = true;
         }

         var23 = MathHelper.func_76141_d(this.field_73132_a.func_70079_am() * 256.0F / 360.0F);
         if (Math.abs(var23 - this.field_73140_i) >= 4) {
            this.func_151259_a(new S19PacketEntityHeadLook(this.field_73132_a, (byte)var23));
            this.field_73140_i = var23;
         }

         this.field_73132_a.field_70160_al = false;
      }

      ++this.field_73136_m;
      if (this.field_73132_a.field_70133_I) {
         this.func_151261_b(new S12PacketEntityVelocity(this.field_73132_a));
         this.field_73132_a.field_70133_I = false;
      }

   }

   private void func_111190_b() {
      DataWatcher var1 = this.field_73132_a.func_70096_w();
      if (var1.func_75684_a()) {
         this.func_151261_b(new S1CPacketEntityMetadata(this.field_73132_a.func_145782_y(), var1, false));
      }

      if (this.field_73132_a instanceof EntityLivingBase) {
         ServersideAttributeMap var2 = (ServersideAttributeMap)((EntityLivingBase)this.field_73132_a).func_110140_aT();
         Set var3 = var2.func_111161_b();
         if (!var3.isEmpty()) {
            this.func_151261_b(new S20PacketEntityProperties(this.field_73132_a.func_145782_y(), var3));
         }

         var3.clear();
      }

   }

   public void func_151259_a(Packet var1) {
      Iterator var2 = this.field_73134_o.iterator();

      while(var2.hasNext()) {
         EntityPlayerMP var3 = (EntityPlayerMP)var2.next();
         var3.field_71135_a.func_147359_a(var1);
      }

   }

   public void func_151261_b(Packet var1) {
      this.func_151259_a(var1);
      if (this.field_73132_a instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.field_73132_a).field_71135_a.func_147359_a(var1);
      }

   }

   public void func_73119_a() {
      Iterator var1 = this.field_73134_o.iterator();

      while(var1.hasNext()) {
         EntityPlayerMP var2 = (EntityPlayerMP)var1.next();
         var2.func_152339_d(this.field_73132_a);
      }

   }

   public void func_73118_a(EntityPlayerMP var1) {
      if (this.field_73134_o.contains(var1)) {
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
               if (!this.field_73132_a.func_70096_w().func_92085_d()) {
                  var1.field_71135_a.func_147359_a(new S1CPacketEntityMetadata(this.field_73132_a.func_145782_y(), this.field_73132_a.func_70096_w(), true));
               }

               NBTTagCompound var3 = this.field_73132_a.func_174819_aU();
               if (var3 != null) {
                  var1.field_71135_a.func_147359_a(new S49PacketUpdateEntityNBT(this.field_73132_a.func_145782_y(), var3));
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  ServersideAttributeMap var4 = (ServersideAttributeMap)((EntityLivingBase)this.field_73132_a).func_110140_aT();
                  Collection var5 = var4.func_111160_c();
                  if (!var5.isEmpty()) {
                     var1.field_71135_a.func_147359_a(new S20PacketEntityProperties(this.field_73132_a.func_145782_y(), var5));
                  }
               }

               this.field_73137_j = this.field_73132_a.field_70159_w;
               this.field_73138_k = this.field_73132_a.field_70181_x;
               this.field_73135_l = this.field_73132_a.field_70179_y;
               if (this.field_73143_t && !(var2 instanceof S0FPacketSpawnMob)) {
                  var1.field_71135_a.func_147359_a(new S12PacketEntityVelocity(this.field_73132_a.func_145782_y(), this.field_73132_a.field_70159_w, this.field_73132_a.field_70181_x, this.field_73132_a.field_70179_y));
               }

               if (this.field_73132_a.field_70154_o != null) {
                  var1.field_71135_a.func_147359_a(new S1BPacketEntityAttach(0, this.field_73132_a, this.field_73132_a.field_70154_o));
               }

               if (this.field_73132_a instanceof EntityLiving && ((EntityLiving)this.field_73132_a).func_110166_bE() != null) {
                  var1.field_71135_a.func_147359_a(new S1BPacketEntityAttach(1, this.field_73132_a, ((EntityLiving)this.field_73132_a).func_110166_bE()));
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  for(int var7 = 0; var7 < 5; ++var7) {
                     ItemStack var9 = ((EntityLivingBase)this.field_73132_a).func_71124_b(var7);
                     if (var9 != null) {
                        var1.field_71135_a.func_147359_a(new S04PacketEntityEquipment(this.field_73132_a.func_145782_y(), var7, var9));
                     }
                  }
               }

               if (this.field_73132_a instanceof EntityPlayer) {
                  EntityPlayer var8 = (EntityPlayer)this.field_73132_a;
                  if (var8.func_70608_bn()) {
                     var1.field_71135_a.func_147359_a(new S0APacketUseBed(var8, new BlockPos(this.field_73132_a)));
                  }
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  EntityLivingBase var10 = (EntityLivingBase)this.field_73132_a;
                  Iterator var11 = var10.func_70651_bq().iterator();

                  while(var11.hasNext()) {
                     PotionEffect var6 = (PotionEffect)var11.next();
                     var1.field_71135_a.func_147359_a(new S1DPacketEntityEffect(this.field_73132_a.func_145782_y(), var6));
                  }
               }
            }
         } else if (this.field_73134_o.contains(var1)) {
            this.field_73134_o.remove(var1);
            var1.func_152339_d(this.field_73132_a);
         }

      }
   }

   public boolean func_180233_c(EntityPlayerMP var1) {
      double var2 = var1.field_70165_t - (double)(this.field_73128_d / 32);
      double var4 = var1.field_70161_v - (double)(this.field_73126_f / 32);
      return var2 >= (double)(-this.field_73130_b) && var2 <= (double)this.field_73130_b && var4 >= (double)(-this.field_73130_b) && var4 <= (double)this.field_73130_b && this.field_73132_a.func_174827_a(var1);
   }

   private boolean func_73121_d(EntityPlayerMP var1) {
      return var1.func_71121_q().func_73040_p().func_72694_a(var1, this.field_73132_a.field_70176_ah, this.field_73132_a.field_70164_aj);
   }

   public void func_73125_b(List<EntityPlayer> var1) {
      for(int var2 = 0; var2 < var1.size(); ++var2) {
         this.func_73117_b((EntityPlayerMP)var1.get(var2));
      }

   }

   private Packet func_151260_c() {
      if (this.field_73132_a.field_70128_L) {
         field_151262_p.warn("Fetching addPacket for removed entity");
      }

      if (this.field_73132_a instanceof EntityItem) {
         return new S0EPacketSpawnObject(this.field_73132_a, 2, 1);
      } else if (this.field_73132_a instanceof EntityPlayerMP) {
         return new S0CPacketSpawnPlayer((EntityPlayer)this.field_73132_a);
      } else if (this.field_73132_a instanceof EntityMinecart) {
         EntityMinecart var9 = (EntityMinecart)this.field_73132_a;
         return new S0EPacketSpawnObject(this.field_73132_a, 10, var9.func_180456_s().func_180039_a());
      } else if (this.field_73132_a instanceof EntityBoat) {
         return new S0EPacketSpawnObject(this.field_73132_a, 1);
      } else if (this.field_73132_a instanceof IAnimals) {
         this.field_73140_i = MathHelper.func_76141_d(this.field_73132_a.func_70079_am() * 256.0F / 360.0F);
         return new S0FPacketSpawnMob((EntityLivingBase)this.field_73132_a);
      } else if (this.field_73132_a instanceof EntityFishHook) {
         EntityPlayer var8 = ((EntityFishHook)this.field_73132_a).field_146042_b;
         return new S0EPacketSpawnObject(this.field_73132_a, 90, var8 != null ? var8.func_145782_y() : this.field_73132_a.func_145782_y());
      } else if (this.field_73132_a instanceof EntityArrow) {
         Entity var7 = ((EntityArrow)this.field_73132_a).field_70250_c;
         return new S0EPacketSpawnObject(this.field_73132_a, 60, var7 != null ? var7.func_145782_y() : this.field_73132_a.func_145782_y());
      } else if (this.field_73132_a instanceof EntitySnowball) {
         return new S0EPacketSpawnObject(this.field_73132_a, 61);
      } else if (this.field_73132_a instanceof EntityPotion) {
         return new S0EPacketSpawnObject(this.field_73132_a, 73, ((EntityPotion)this.field_73132_a).func_70196_i());
      } else if (this.field_73132_a instanceof EntityExpBottle) {
         return new S0EPacketSpawnObject(this.field_73132_a, 75);
      } else if (this.field_73132_a instanceof EntityEnderPearl) {
         return new S0EPacketSpawnObject(this.field_73132_a, 65);
      } else if (this.field_73132_a instanceof EntityEnderEye) {
         return new S0EPacketSpawnObject(this.field_73132_a, 72);
      } else if (this.field_73132_a instanceof EntityFireworkRocket) {
         return new S0EPacketSpawnObject(this.field_73132_a, 76);
      } else {
         S0EPacketSpawnObject var2;
         if (this.field_73132_a instanceof EntityFireball) {
            EntityFireball var6 = (EntityFireball)this.field_73132_a;
            var2 = null;
            byte var10 = 63;
            if (this.field_73132_a instanceof EntitySmallFireball) {
               var10 = 64;
            } else if (this.field_73132_a instanceof EntityWitherSkull) {
               var10 = 66;
            }

            if (var6.field_70235_a != null) {
               var2 = new S0EPacketSpawnObject(this.field_73132_a, var10, ((EntityFireball)this.field_73132_a).field_70235_a.func_145782_y());
            } else {
               var2 = new S0EPacketSpawnObject(this.field_73132_a, var10, 0);
            }

            var2.func_149003_d((int)(var6.field_70232_b * 8000.0D));
            var2.func_149000_e((int)(var6.field_70233_c * 8000.0D));
            var2.func_149007_f((int)(var6.field_70230_d * 8000.0D));
            return var2;
         } else if (this.field_73132_a instanceof EntityEgg) {
            return new S0EPacketSpawnObject(this.field_73132_a, 62);
         } else if (this.field_73132_a instanceof EntityTNTPrimed) {
            return new S0EPacketSpawnObject(this.field_73132_a, 50);
         } else if (this.field_73132_a instanceof EntityEnderCrystal) {
            return new S0EPacketSpawnObject(this.field_73132_a, 51);
         } else if (this.field_73132_a instanceof EntityFallingBlock) {
            EntityFallingBlock var5 = (EntityFallingBlock)this.field_73132_a;
            return new S0EPacketSpawnObject(this.field_73132_a, 70, Block.func_176210_f(var5.func_175131_l()));
         } else if (this.field_73132_a instanceof EntityArmorStand) {
            return new S0EPacketSpawnObject(this.field_73132_a, 78);
         } else if (this.field_73132_a instanceof EntityPainting) {
            return new S10PacketSpawnPainting((EntityPainting)this.field_73132_a);
         } else {
            BlockPos var3;
            if (this.field_73132_a instanceof EntityItemFrame) {
               EntityItemFrame var4 = (EntityItemFrame)this.field_73132_a;
               var2 = new S0EPacketSpawnObject(this.field_73132_a, 71, var4.field_174860_b.func_176736_b());
               var3 = var4.func_174857_n();
               var2.func_148996_a(MathHelper.func_76141_d((float)(var3.func_177958_n() * 32)));
               var2.func_148995_b(MathHelper.func_76141_d((float)(var3.func_177956_o() * 32)));
               var2.func_149005_c(MathHelper.func_76141_d((float)(var3.func_177952_p() * 32)));
               return var2;
            } else if (this.field_73132_a instanceof EntityLeashKnot) {
               EntityLeashKnot var1 = (EntityLeashKnot)this.field_73132_a;
               var2 = new S0EPacketSpawnObject(this.field_73132_a, 77);
               var3 = var1.func_174857_n();
               var2.func_148996_a(MathHelper.func_76141_d((float)(var3.func_177958_n() * 32)));
               var2.func_148995_b(MathHelper.func_76141_d((float)(var3.func_177956_o() * 32)));
               var2.func_149005_c(MathHelper.func_76141_d((float)(var3.func_177952_p() * 32)));
               return var2;
            } else if (this.field_73132_a instanceof EntityXPOrb) {
               return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.field_73132_a);
            } else {
               throw new IllegalArgumentException("Don't know how to add " + this.field_73132_a.getClass() + "!");
            }
         }
      }
   }

   public void func_73123_c(EntityPlayerMP var1) {
      if (this.field_73134_o.contains(var1)) {
         this.field_73134_o.remove(var1);
         var1.func_152339_d(this.field_73132_a);
      }

   }
}
