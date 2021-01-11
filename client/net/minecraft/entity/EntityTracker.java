package net.minecraft.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
   private static final Logger field_151249_a = LogManager.getLogger();
   private final WorldServer field_72795_a;
   private Set<EntityTrackerEntry> field_72793_b = Sets.newHashSet();
   private IntHashMap<EntityTrackerEntry> field_72794_c = new IntHashMap();
   private int field_72792_d;

   public EntityTracker(WorldServer var1) {
      super();
      this.field_72795_a = var1;
      this.field_72792_d = var1.func_73046_m().func_71203_ab().func_72372_a();
   }

   public void func_72786_a(Entity var1) {
      if (var1 instanceof EntityPlayerMP) {
         this.func_72791_a(var1, 512, 2);
         EntityPlayerMP var2 = (EntityPlayerMP)var1;
         Iterator var3 = this.field_72793_b.iterator();

         while(var3.hasNext()) {
            EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
            if (var4.field_73132_a != var2) {
               var4.func_73117_b(var2);
            }
         }
      } else if (var1 instanceof EntityFishHook) {
         this.func_72785_a(var1, 64, 5, true);
      } else if (var1 instanceof EntityArrow) {
         this.func_72785_a(var1, 64, 20, false);
      } else if (var1 instanceof EntitySmallFireball) {
         this.func_72785_a(var1, 64, 10, false);
      } else if (var1 instanceof EntityFireball) {
         this.func_72785_a(var1, 64, 10, false);
      } else if (var1 instanceof EntitySnowball) {
         this.func_72785_a(var1, 64, 10, true);
      } else if (var1 instanceof EntityEnderPearl) {
         this.func_72785_a(var1, 64, 10, true);
      } else if (var1 instanceof EntityEnderEye) {
         this.func_72785_a(var1, 64, 4, true);
      } else if (var1 instanceof EntityEgg) {
         this.func_72785_a(var1, 64, 10, true);
      } else if (var1 instanceof EntityPotion) {
         this.func_72785_a(var1, 64, 10, true);
      } else if (var1 instanceof EntityExpBottle) {
         this.func_72785_a(var1, 64, 10, true);
      } else if (var1 instanceof EntityFireworkRocket) {
         this.func_72785_a(var1, 64, 10, true);
      } else if (var1 instanceof EntityItem) {
         this.func_72785_a(var1, 64, 20, true);
      } else if (var1 instanceof EntityMinecart) {
         this.func_72785_a(var1, 80, 3, true);
      } else if (var1 instanceof EntityBoat) {
         this.func_72785_a(var1, 80, 3, true);
      } else if (var1 instanceof EntitySquid) {
         this.func_72785_a(var1, 64, 3, true);
      } else if (var1 instanceof EntityWither) {
         this.func_72785_a(var1, 80, 3, false);
      } else if (var1 instanceof EntityBat) {
         this.func_72785_a(var1, 80, 3, false);
      } else if (var1 instanceof EntityDragon) {
         this.func_72785_a(var1, 160, 3, true);
      } else if (var1 instanceof IAnimals) {
         this.func_72785_a(var1, 80, 3, true);
      } else if (var1 instanceof EntityTNTPrimed) {
         this.func_72785_a(var1, 160, 10, true);
      } else if (var1 instanceof EntityFallingBlock) {
         this.func_72785_a(var1, 160, 20, true);
      } else if (var1 instanceof EntityHanging) {
         this.func_72785_a(var1, 160, 2147483647, false);
      } else if (var1 instanceof EntityArmorStand) {
         this.func_72785_a(var1, 160, 3, true);
      } else if (var1 instanceof EntityXPOrb) {
         this.func_72785_a(var1, 160, 20, true);
      } else if (var1 instanceof EntityEnderCrystal) {
         this.func_72785_a(var1, 256, 2147483647, false);
      }

   }

   public void func_72791_a(Entity var1, int var2, int var3) {
      this.func_72785_a(var1, var2, var3, false);
   }

   public void func_72785_a(Entity var1, int var2, final int var3, boolean var4) {
      if (var2 > this.field_72792_d) {
         var2 = this.field_72792_d;
      }

      try {
         if (this.field_72794_c.func_76037_b(var1.func_145782_y())) {
            throw new IllegalStateException("Entity is already tracked!");
         }

         EntityTrackerEntry var5 = new EntityTrackerEntry(var1, var2, var3, var4);
         this.field_72793_b.add(var5);
         this.field_72794_c.func_76038_a(var1.func_145782_y(), var5);
         var5.func_73125_b(this.field_72795_a.field_73010_i);
      } catch (Throwable var11) {
         CrashReport var6 = CrashReport.func_85055_a(var11, "Adding entity to track");
         CrashReportCategory var7 = var6.func_85058_a("Entity To Track");
         var7.func_71507_a("Tracking range", var2 + " blocks");
         var7.func_71500_a("Update interval", new Callable<String>() {
            public String call() throws Exception {
               String var1 = "Once per " + var3 + " ticks";
               if (var3 == 2147483647) {
                  var1 = "Maximum (" + var1 + ")";
               }

               return var1;
            }

            // $FF: synthetic method
            public Object call() throws Exception {
               return this.call();
            }
         });
         var1.func_85029_a(var7);
         CrashReportCategory var8 = var6.func_85058_a("Entity That Is Already Tracked");
         ((EntityTrackerEntry)this.field_72794_c.func_76041_a(var1.func_145782_y())).field_73132_a.func_85029_a(var8);

         try {
            throw new ReportedException(var6);
         } catch (ReportedException var10) {
            field_151249_a.error("\"Silently\" catching entity tracking error.", var10);
         }
      }

   }

   public void func_72790_b(Entity var1) {
      if (var1 instanceof EntityPlayerMP) {
         EntityPlayerMP var2 = (EntityPlayerMP)var1;
         Iterator var3 = this.field_72793_b.iterator();

         while(var3.hasNext()) {
            EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
            var4.func_73118_a(var2);
         }
      }

      EntityTrackerEntry var5 = (EntityTrackerEntry)this.field_72794_c.func_76049_d(var1.func_145782_y());
      if (var5 != null) {
         this.field_72793_b.remove(var5);
         var5.func_73119_a();
      }

   }

   public void func_72788_a() {
      ArrayList var1 = Lists.newArrayList();
      Iterator var2 = this.field_72793_b.iterator();

      while(var2.hasNext()) {
         EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
         var3.func_73122_a(this.field_72795_a.field_73010_i);
         if (var3.field_73133_n && var3.field_73132_a instanceof EntityPlayerMP) {
            var1.add((EntityPlayerMP)var3.field_73132_a);
         }
      }

      for(int var6 = 0; var6 < var1.size(); ++var6) {
         EntityPlayerMP var7 = (EntityPlayerMP)var1.get(var6);
         Iterator var4 = this.field_72793_b.iterator();

         while(var4.hasNext()) {
            EntityTrackerEntry var5 = (EntityTrackerEntry)var4.next();
            if (var5.field_73132_a != var7) {
               var5.func_73117_b(var7);
            }
         }
      }

   }

   public void func_180245_a(EntityPlayerMP var1) {
      Iterator var2 = this.field_72793_b.iterator();

      while(var2.hasNext()) {
         EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
         if (var3.field_73132_a == var1) {
            var3.func_73125_b(this.field_72795_a.field_73010_i);
         } else {
            var3.func_73117_b(var1);
         }
      }

   }

   public void func_151247_a(Entity var1, Packet var2) {
      EntityTrackerEntry var3 = (EntityTrackerEntry)this.field_72794_c.func_76041_a(var1.func_145782_y());
      if (var3 != null) {
         var3.func_151259_a(var2);
      }

   }

   public void func_151248_b(Entity var1, Packet var2) {
      EntityTrackerEntry var3 = (EntityTrackerEntry)this.field_72794_c.func_76041_a(var1.func_145782_y());
      if (var3 != null) {
         var3.func_151261_b(var2);
      }

   }

   public void func_72787_a(EntityPlayerMP var1) {
      Iterator var2 = this.field_72793_b.iterator();

      while(var2.hasNext()) {
         EntityTrackerEntry var3 = (EntityTrackerEntry)var2.next();
         var3.func_73123_c(var1);
      }

   }

   public void func_85172_a(EntityPlayerMP var1, Chunk var2) {
      Iterator var3 = this.field_72793_b.iterator();

      while(var3.hasNext()) {
         EntityTrackerEntry var4 = (EntityTrackerEntry)var3.next();
         if (var4.field_73132_a != var1 && var4.field_73132_a.field_70176_ah == var2.field_76635_g && var4.field_73132_a.field_70164_aj == var2.field_76647_h) {
            var4.func_73117_b(var1);
         }
      }

   }
}
