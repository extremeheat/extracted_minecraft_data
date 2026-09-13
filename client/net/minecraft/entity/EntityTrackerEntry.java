package net.minecraft.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
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
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity$S15PacketEntityRelMove;
import net.minecraft.network.play.server.S14PacketEntity$S16PacketEntityLook;
import net.minecraft.network.play.server.S14PacketEntity$S17PacketEntityLookMove;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.potion.PotionEffect;
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
   public boolean field_73133_n;
   public Set field_73134_o = new HashSet();

   public EntityTrackerEntry(Entity var1, int var2, int var3, boolean var4) {
      super();
      this.field_73132_a = var1;
      this.field_73130_b = var2;
      this.field_73131_c = var3;
      this.field_73143_t = var4;
      this.field_73128_d = MathHelper.func_76128_c(var1.field_70165_t * 32.0);
      this.field_73129_e = MathHelper.func_76128_c(var1.field_70163_u * 32.0);
      this.field_73126_f = MathHelper.func_76128_c(var1.field_70161_v * 32.0);
      this.field_73127_g = MathHelper.func_76141_d(var1.field_70177_z * 256.0F / 360.0F);
      this.field_73139_h = MathHelper.func_76141_d(var1.field_70125_A * 256.0F / 360.0F);
      this.field_73140_i = MathHelper.func_76141_d(var1.func_70079_am() * 256.0F / 360.0F);
   }

   @Override
   public boolean equals(Object var1) {
      if (var1 instanceof EntityTrackerEntry) {
         return ((EntityTrackerEntry)var1).field_73132_a.func_145782_y() == this.field_73132_a.func_145782_y();
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.field_73132_a.func_145782_y();
   }

   public void func_73122_a(List var1) {
      this.field_73133_n = false;
      if (!this.field_73144_s || this.field_73132_a.func_70092_e(this.field_73147_p, this.field_73146_q, this.field_73145_r) > 16.0) {
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
         EntityItemFrame var25 = (EntityItemFrame)this.field_73132_a;
         ItemStack var27 = var25.func_82335_i();
         if (var27 != null && var27.func_77973_b() instanceof ItemMap) {
            MapData var29 = Items.field_151098_aY.func_77873_a(var27, this.field_73132_a.field_70170_p);

            for(EntityPlayer var31 : var1) {
               EntityPlayerMP var32 = (EntityPlayerMP)var31;
               var29.func_76191_a(var32, var27);
               Packet var33 = Items.field_151098_aY.func_150911_c(var27, this.field_73132_a.field_70170_p, var32);
               if (var33 != null) {
                  var32.field_71135_a.func_147359_a(var33);
               }
            }
         }

         this.func_111190_b();
      } else if (this.field_73136_m % this.field_73131_c == 0 || this.field_73132_a.field_70160_al || this.field_73132_a.func_70096_w().func_75684_a()) {
         if (this.field_73132_a.field_70154_o == null) {
            ++this.field_73142_u;
            int var23 = this.field_73132_a.field_70168_am.func_75630_a(this.field_73132_a.field_70165_t);
            int var26 = MathHelper.func_76128_c(this.field_73132_a.field_70163_u * 32.0);
            int var28 = this.field_73132_a.field_70168_am.func_75630_a(this.field_73132_a.field_70161_v);
            int var5 = MathHelper.func_76141_d(this.field_73132_a.field_70177_z * 256.0F / 360.0F);
            int var6 = MathHelper.func_76141_d(this.field_73132_a.field_70125_A * 256.0F / 360.0F);
            int var7 = var23 - this.field_73128_d;
            int var8 = var26 - this.field_73129_e;
            int var9 = var28 - this.field_73126_f;
            Object var10 = null;
            boolean var11 = Math.abs(var7) >= 4 || Math.abs(var8) >= 4 || Math.abs(var9) >= 4 || this.field_73136_m % 60 == 0;
            boolean var12 = Math.abs(var5 - this.field_73127_g) >= 4 || Math.abs(var6 - this.field_73139_h) >= 4;
            if (this.field_73136_m > 0 || this.field_73132_a instanceof EntityArrow) {
               if (var7 < -128 || var7 >= 128 || var8 < -128 || var8 >= 128 || var9 < -128 || var9 >= 128 || this.field_73142_u > 400 || this.field_73141_v) {
                  this.field_73142_u = 0;
                  var10 = new S18PacketEntityTeleport(this.field_73132_a.func_145782_y(), var23, var26, var28, (byte)var5, (byte)var6);
               } else if (var11 && var12) {
                  var10 = new S14PacketEntity$S17PacketEntityLookMove(
                     this.field_73132_a.func_145782_y(), (byte)var7, (byte)var8, (byte)var9, (byte)var5, (byte)var6
                  );
               } else if (var11) {
                  var10 = new S14PacketEntity$S15PacketEntityRelMove(this.field_73132_a.func_145782_y(), (byte)var7, (byte)var8, (byte)var9);
               } else if (var12) {
                  var10 = new S14PacketEntity$S16PacketEntityLook(this.field_73132_a.func_145782_y(), (byte)var5, (byte)var6);
               }
            }

            if (this.field_73143_t) {
               double var13 = this.field_73132_a.field_70159_w - this.field_73137_j;
               double var15 = this.field_73132_a.field_70181_x - this.field_73138_k;
               double var17 = this.field_73132_a.field_70179_y - this.field_73135_l;
               double var19 = 0.02;
               double var21 = var13 * var13 + var15 * var15 + var17 * var17;
               if (var21 > var19 * var19
                  || var21 > 0.0
                     && this.field_73132_a.field_70159_w == 0.0
                     && this.field_73132_a.field_70181_x == 0.0
                     && this.field_73132_a.field_70179_y == 0.0) {
                  this.field_73137_j = this.field_73132_a.field_70159_w;
                  this.field_73138_k = this.field_73132_a.field_70181_x;
                  this.field_73135_l = this.field_73132_a.field_70179_y;
                  this.func_151259_a(
                     new S12PacketEntityVelocity(this.field_73132_a.func_145782_y(), this.field_73137_j, this.field_73138_k, this.field_73135_l)
                  );
               }
            }

            if (var10 != null) {
               this.func_151259_a((Packet)var10);
            }

            this.func_111190_b();
            if (var11) {
               this.field_73128_d = var23;
               this.field_73129_e = var26;
               this.field_73126_f = var28;
            }

            if (var12) {
               this.field_73127_g = var5;
               this.field_73139_h = var6;
            }

            this.field_73141_v = false;
         } else {
            int var2 = MathHelper.func_76141_d(this.field_73132_a.field_70177_z * 256.0F / 360.0F);
            int var3 = MathHelper.func_76141_d(this.field_73132_a.field_70125_A * 256.0F / 360.0F);
            boolean var4 = Math.abs(var2 - this.field_73127_g) >= 4 || Math.abs(var3 - this.field_73139_h) >= 4;
            if (var4) {
               this.func_151259_a(new S14PacketEntity$S16PacketEntityLook(this.field_73132_a.func_145782_y(), (byte)var2, (byte)var3));
               this.field_73127_g = var2;
               this.field_73139_h = var3;
            }

            this.field_73128_d = this.field_73132_a.field_70168_am.func_75630_a(this.field_73132_a.field_70165_t);
            this.field_73129_e = MathHelper.func_76128_c(this.field_73132_a.field_70163_u * 32.0);
            this.field_73126_f = this.field_73132_a.field_70168_am.func_75630_a(this.field_73132_a.field_70161_v);
            this.func_111190_b();
            this.field_73141_v = true;
         }

         int var24 = MathHelper.func_76141_d(this.field_73132_a.func_70079_am() * 256.0F / 360.0F);
         if (Math.abs(var24 - this.field_73140_i) >= 4) {
            this.func_151259_a(new S19PacketEntityHeadLook(this.field_73132_a, (byte)var24));
            this.field_73140_i = var24;
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
      for(EntityPlayerMP var3 : this.field_73134_o) {
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
      for(EntityPlayerMP var2 : this.field_73134_o) {
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
         double var2 = var1.field_70165_t - (double)(this.field_73128_d / 32);
         double var4 = var1.field_70161_v - (double)(this.field_73126_f / 32);
         if (var2 >= (double)(-this.field_73130_b)
            && var2 <= (double)this.field_73130_b
            && var4 >= (double)(-this.field_73130_b)
            && var4 <= (double)this.field_73130_b) {
            if (!this.field_73134_o.contains(var1) && (this.func_73121_d(var1) || this.field_73132_a.field_98038_p)) {
               this.field_73134_o.add(var1);
               Packet var6 = this.func_151260_c();
               var1.field_71135_a.func_147359_a(var6);
               if (!this.field_73132_a.func_70096_w().func_92085_d()) {
                  var1.field_71135_a.func_147359_a(new S1CPacketEntityMetadata(this.field_73132_a.func_145782_y(), this.field_73132_a.func_70096_w(), true));
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  ServersideAttributeMap var7 = (ServersideAttributeMap)((EntityLivingBase)this.field_73132_a).func_110140_aT();
                  Collection var8 = var7.func_111160_c();
                  if (!var8.isEmpty()) {
                     var1.field_71135_a.func_147359_a(new S20PacketEntityProperties(this.field_73132_a.func_145782_y(), var8));
                  }
               }

               this.field_73137_j = this.field_73132_a.field_70159_w;
               this.field_73138_k = this.field_73132_a.field_70181_x;
               this.field_73135_l = this.field_73132_a.field_70179_y;
               if (this.field_73143_t && !(var6 instanceof S0FPacketSpawnMob)) {
                  var1.field_71135_a
                     .func_147359_a(
                        new S12PacketEntityVelocity(
                           this.field_73132_a.func_145782_y(),
                           this.field_73132_a.field_70159_w,
                           this.field_73132_a.field_70181_x,
                           this.field_73132_a.field_70179_y
                        )
                     );
               }

               if (this.field_73132_a.field_70154_o != null) {
                  var1.field_71135_a.func_147359_a(new S1BPacketEntityAttach(0, this.field_73132_a, this.field_73132_a.field_70154_o));
               }

               if (this.field_73132_a instanceof EntityLiving && ((EntityLiving)this.field_73132_a).func_110166_bE() != null) {
                  var1.field_71135_a.func_147359_a(new S1BPacketEntityAttach(1, this.field_73132_a, ((EntityLiving)this.field_73132_a).func_110166_bE()));
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  for(int var10 = 0; var10 < 5; ++var10) {
                     ItemStack var13 = ((EntityLivingBase)this.field_73132_a).func_71124_b(var10);
                     if (var13 != null) {
                        var1.field_71135_a.func_147359_a(new S04PacketEntityEquipment(this.field_73132_a.func_145782_y(), var10, var13));
                     }
                  }
               }

               if (this.field_73132_a instanceof EntityPlayer) {
                  EntityPlayer var11 = (EntityPlayer)this.field_73132_a;
                  if (var11.func_70608_bn()) {
                     var1.field_71135_a
                        .func_147359_a(
                           new S0APacketUseBed(
                              var11,
                              MathHelper.func_76128_c(this.field_73132_a.field_70165_t),
                              MathHelper.func_76128_c(this.field_73132_a.field_70163_u),
                              MathHelper.func_76128_c(this.field_73132_a.field_70161_v)
                           )
                        );
                  }
               }

               if (this.field_73132_a instanceof EntityLivingBase) {
                  EntityLivingBase var12 = (EntityLivingBase)this.field_73132_a;

                  for(PotionEffect var9 : var12.func_70651_bq()) {
                     var1.field_71135_a.func_147359_a(new S1DPacketEntityEffect(this.field_73132_a.func_145782_y(), var9));
                  }
               }
            }
         } else if (this.field_73134_o.contains(var1)) {
            this.field_73134_o.remove(var1);
            var1.func_152339_d(this.field_73132_a);
         }
      }
   }

   private boolean func_73121_d(EntityPlayerMP var1) {
      return var1.func_71121_q().func_73040_p().func_72694_a(var1, this.field_73132_a.field_70176_ah, this.field_73132_a.field_70164_aj);
   }

   public void func_73125_b(List var1) {
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
         return new S0EPacketSpawnObject(this.field_73132_a, 10, var9.func_94087_l());
      } else if (this.field_73132_a instanceof EntityBoat) {
         return new S0EPacketSpawnObject(this.field_73132_a, 1);
      } else if (this.field_73132_a instanceof IAnimals || this.field_73132_a instanceof EntityDragon) {
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
      } else if (this.field_73132_a instanceof EntityFireball) {
         EntityFireball var6 = (EntityFireball)this.field_73132_a;
         S0EPacketSpawnObject var11 = null;
         byte var3 = 63;
         if (this.field_73132_a instanceof EntitySmallFireball) {
            var3 = 64;
         } else if (this.field_73132_a instanceof EntityWitherSkull) {
            var3 = 66;
         }

         if (var6.field_70235_a != null) {
            var11 = new S0EPacketSpawnObject(this.field_73132_a, var3, ((EntityFireball)this.field_73132_a).field_70235_a.func_145782_y());
         } else {
            var11 = new S0EPacketSpawnObject(this.field_73132_a, var3, 0);
         }

         var11.func_149003_d((int)(var6.field_70232_b * 8000.0));
         var11.func_149000_e((int)(var6.field_70233_c * 8000.0));
         var11.func_149007_f((int)(var6.field_70230_d * 8000.0));
         return var11;
      } else if (this.field_73132_a instanceof EntityEgg) {
         return new S0EPacketSpawnObject(this.field_73132_a, 62);
      } else if (this.field_73132_a instanceof EntityTNTPrimed) {
         return new S0EPacketSpawnObject(this.field_73132_a, 50);
      } else if (this.field_73132_a instanceof EntityEnderCrystal) {
         return new S0EPacketSpawnObject(this.field_73132_a, 51);
      } else if (this.field_73132_a instanceof EntityFallingBlock) {
         EntityFallingBlock var5 = (EntityFallingBlock)this.field_73132_a;
         return new S0EPacketSpawnObject(this.field_73132_a, 70, Block.func_149682_b(var5.func_145805_f()) | var5.field_145814_a << 16);
      } else if (this.field_73132_a instanceof EntityPainting) {
         return new S10PacketSpawnPainting((EntityPainting)this.field_73132_a);
      } else if (this.field_73132_a instanceof EntityItemFrame) {
         EntityItemFrame var4 = (EntityItemFrame)this.field_73132_a;
         S0EPacketSpawnObject var10 = new S0EPacketSpawnObject(this.field_73132_a, 71, var4.field_82332_a);
         var10.func_148996_a(MathHelper.func_76141_d((float)(var4.field_146063_b * 32)));
         var10.func_148995_b(MathHelper.func_76141_d((float)(var4.field_146064_c * 32)));
         var10.func_149005_c(MathHelper.func_76141_d((float)(var4.field_146062_d * 32)));
         return var10;
      } else if (this.field_73132_a instanceof EntityLeashKnot) {
         EntityLeashKnot var1 = (EntityLeashKnot)this.field_73132_a;
         S0EPacketSpawnObject var2 = new S0EPacketSpawnObject(this.field_73132_a, 77);
         var2.func_148996_a(MathHelper.func_76141_d((float)(var1.field_146063_b * 32)));
         var2.func_148995_b(MathHelper.func_76141_d((float)(var1.field_146064_c * 32)));
         var2.func_149005_c(MathHelper.func_76141_d((float)(var1.field_146062_d * 32)));
         return var2;
      } else if (this.field_73132_a instanceof EntityXPOrb) {
         return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.field_73132_a);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.field_73132_a.getClass() + "!");
      }
   }

   public void func_73123_c(EntityPlayerMP var1) {
      if (this.field_73134_o.contains(var1)) {
         this.field_73134_o.remove(var1);
         var1.func_152339_d(this.field_73132_a);
      }
   }
}
