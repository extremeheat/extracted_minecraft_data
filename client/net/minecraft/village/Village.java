package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {
   private World field_75586_a;
   private final List field_75584_b = new ArrayList();
   private final ChunkCoordinates field_75585_c = new ChunkCoordinates(0, 0, 0);
   private final ChunkCoordinates field_75582_d = new ChunkCoordinates(0, 0, 0);
   private int field_75583_e;
   private int field_75580_f;
   private int field_75581_g;
   private int field_75588_h;
   private int field_82694_i;
   private TreeMap field_82693_j = new TreeMap();
   private List field_75589_i = new ArrayList();
   private int field_75587_j;

   public Village() {
      super();
   }

   public Village(World var1) {
      super();
      this.field_75586_a = var1;
   }

   public void func_82691_a(World var1) {
      this.field_75586_a = var1;
   }

   public void func_75560_a(int var1) {
      this.field_75581_g = var1;
      this.func_75557_k();
      this.func_75565_j();
      if (var1 % 20 == 0) {
         this.func_75572_i();
      }

      if (var1 % 30 == 0) {
         this.func_75579_h();
      }

      int var2 = this.field_75588_h / 10;
      if (this.field_75587_j < var2 && this.field_75584_b.size() > 20 && this.field_75586_a.field_73012_v.nextInt(7000) == 0) {
         Vec3 var3 = this.func_75559_a(
            MathHelper.func_76141_d((float)this.field_75582_d.field_71574_a),
            MathHelper.func_76141_d((float)this.field_75582_d.field_71572_b),
            MathHelper.func_76141_d((float)this.field_75582_d.field_71573_c),
            2,
            4,
            2
         );
         if (var3 != null) {
            EntityIronGolem var4 = new EntityIronGolem(this.field_75586_a);
            var4.func_70107_b(var3.field_72450_a, var3.field_72448_b, var3.field_72449_c);
            this.field_75586_a.func_72838_d(var4);
            ++this.field_75587_j;
         }
      }
   }

   private Vec3 func_75559_a(int var1, int var2, int var3, int var4, int var5, int var6) {
      for(int var7 = 0; var7 < 10; ++var7) {
         int var8 = var1 + this.field_75586_a.field_73012_v.nextInt(16) - 8;
         int var9 = var2 + this.field_75586_a.field_73012_v.nextInt(6) - 3;
         int var10 = var3 + this.field_75586_a.field_73012_v.nextInt(16) - 8;
         if (this.func_75570_a(var8, var9, var10) && this.func_75563_b(var8, var9, var10, var4, var5, var6)) {
            return Vec3.func_72443_a((double)var8, (double)var9, (double)var10);
         }
      }

      return null;
   }

   private boolean func_75563_b(int var1, int var2, int var3, int var4, int var5, int var6) {
      if (!World.func_147466_a(this.field_75586_a, var1, var2 - 1, var3)) {
         return false;
      } else {
         int var7 = var1 - var4 / 2;
         int var8 = var3 - var6 / 2;

         for(int var9 = var7; var9 < var7 + var4; ++var9) {
            for(int var10 = var2; var10 < var2 + var5; ++var10) {
               for(int var11 = var8; var11 < var8 + var6; ++var11) {
                  if (this.field_75586_a.func_147439_a(var9, var10, var11).func_149721_r()) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_75579_h() {
      List var1 = this.field_75586_a
         .func_72872_a(
            EntityIronGolem.class,
            AxisAlignedBB.func_72330_a(
               (double)(this.field_75582_d.field_71574_a - this.field_75583_e),
               (double)(this.field_75582_d.field_71572_b - 4),
               (double)(this.field_75582_d.field_71573_c - this.field_75583_e),
               (double)(this.field_75582_d.field_71574_a + this.field_75583_e),
               (double)(this.field_75582_d.field_71572_b + 4),
               (double)(this.field_75582_d.field_71573_c + this.field_75583_e)
            )
         );
      this.field_75587_j = var1.size();
   }

   private void func_75572_i() {
      List var1 = this.field_75586_a
         .func_72872_a(
            EntityVillager.class,
            AxisAlignedBB.func_72330_a(
               (double)(this.field_75582_d.field_71574_a - this.field_75583_e),
               (double)(this.field_75582_d.field_71572_b - 4),
               (double)(this.field_75582_d.field_71573_c - this.field_75583_e),
               (double)(this.field_75582_d.field_71574_a + this.field_75583_e),
               (double)(this.field_75582_d.field_71572_b + 4),
               (double)(this.field_75582_d.field_71573_c + this.field_75583_e)
            )
         );
      this.field_75588_h = var1.size();
      if (this.field_75588_h == 0) {
         this.field_82693_j.clear();
      }
   }

   public ChunkCoordinates func_75577_a() {
      return this.field_75582_d;
   }

   public int func_75568_b() {
      return this.field_75583_e;
   }

   public int func_75567_c() {
      return this.field_75584_b.size();
   }

   public int func_75561_d() {
      return this.field_75581_g - this.field_75580_f;
   }

   public int func_75562_e() {
      return this.field_75588_h;
   }

   public boolean func_75570_a(int var1, int var2, int var3) {
      return this.field_75582_d.func_71569_e(var1, var2, var3) < (float)(this.field_75583_e * this.field_75583_e);
   }

   public List func_75558_f() {
      return this.field_75584_b;
   }

   public VillageDoorInfo func_75564_b(int var1, int var2, int var3) {
      VillageDoorInfo var4 = null;
      int var5 = 2147483647;

      for(VillageDoorInfo var7 : this.field_75584_b) {
         int var8 = var7.func_75474_b(var1, var2, var3);
         if (var8 < var5) {
            var4 = var7;
            var5 = var8;
         }
      }

      return var4;
   }

   public VillageDoorInfo func_75569_c(int var1, int var2, int var3) {
      VillageDoorInfo var4 = null;
      int var5 = 2147483647;

      for(VillageDoorInfo var7 : this.field_75584_b) {
         int var8 = var7.func_75474_b(var1, var2, var3);
         if (var8 > 256) {
            var8 *= 1000;
         } else {
            var8 = var7.func_75468_f();
         }

         if (var8 < var5) {
            var4 = var7;
            var5 = var8;
         }
      }

      return var4;
   }

   public VillageDoorInfo func_75578_e(int var1, int var2, int var3) {
      if (this.field_75582_d.func_71569_e(var1, var2, var3) > (float)(this.field_75583_e * this.field_75583_e)) {
         return null;
      } else {
         for(VillageDoorInfo var5 : this.field_75584_b) {
            if (var5.field_75481_a == var1 && var5.field_75480_c == var3 && Math.abs(var5.field_75479_b - var2) <= 1) {
               return var5;
            }
         }

         return null;
      }
   }

   public void func_75576_a(VillageDoorInfo var1) {
      this.field_75584_b.add(var1);
      this.field_75585_c.field_71574_a += var1.field_75481_a;
      this.field_75585_c.field_71572_b += var1.field_75479_b;
      this.field_75585_c.field_71573_c += var1.field_75480_c;
      this.func_75573_l();
      this.field_75580_f = var1.field_75475_f;
   }

   public boolean func_75566_g() {
      return this.field_75584_b.isEmpty();
   }

   public void func_75575_a(EntityLivingBase var1) {
      for(Village$VillageAgressor var3 : this.field_75589_i) {
         if (var3.field_75592_a == var1) {
            var3.field_75590_b = this.field_75581_g;
            return;
         }
      }

      this.field_75589_i.add(new Village$VillageAgressor(this, var1, this.field_75581_g));
   }

   public EntityLivingBase func_75571_b(EntityLivingBase var1) {
      double var2 = 1.7976931348623157E308;
      Village$VillageAgressor var4 = null;

      for(int var5 = 0; var5 < this.field_75589_i.size(); ++var5) {
         Village$VillageAgressor var6 = (Village$VillageAgressor)this.field_75589_i.get(var5);
         double var7 = var6.field_75592_a.func_70068_e(var1);
         if (!(var7 > var2)) {
            var4 = var6;
            var2 = var7;
         }
      }

      return var4 != null ? var4.field_75592_a : null;
   }

   public EntityPlayer func_82685_c(EntityLivingBase var1) {
      double var2 = 1.7976931348623157E308;
      EntityPlayer var4 = null;

      for(String var6 : this.field_82693_j.keySet()) {
         if (this.func_82687_d(var6)) {
            EntityPlayer var7 = this.field_75586_a.func_72924_a(var6);
            if (var7 != null) {
               double var8 = var7.func_70068_e(var1);
               if (!(var8 > var2)) {
                  var4 = var7;
                  var2 = var8;
               }
            }
         }
      }

      return var4;
   }

   private void func_75565_j() {
      Iterator var1 = this.field_75589_i.iterator();

      while(var1.hasNext()) {
         Village$VillageAgressor var2 = (Village$VillageAgressor)var1.next();
         if (!var2.field_75592_a.func_70089_S() || Math.abs(this.field_75581_g - var2.field_75590_b) > 300) {
            var1.remove();
         }
      }
   }

   private void func_75557_k() {
      boolean var1 = false;
      boolean var2 = this.field_75586_a.field_73012_v.nextInt(50) == 0;
      Iterator var3 = this.field_75584_b.iterator();

      while(var3.hasNext()) {
         VillageDoorInfo var4 = (VillageDoorInfo)var3.next();
         if (var2) {
            var4.func_75466_d();
         }

         if (!this.func_75574_f(var4.field_75481_a, var4.field_75479_b, var4.field_75480_c) || Math.abs(this.field_75581_g - var4.field_75475_f) > 1200) {
            this.field_75585_c.field_71574_a -= var4.field_75481_a;
            this.field_75585_c.field_71572_b -= var4.field_75479_b;
            this.field_75585_c.field_71573_c -= var4.field_75480_c;
            var1 = true;
            var4.field_75476_g = true;
            var3.remove();
         }
      }

      if (var1) {
         this.func_75573_l();
      }
   }

   private boolean func_75574_f(int var1, int var2, int var3) {
      return this.field_75586_a.func_147439_a(var1, var2, var3) == Blocks.field_150466_ao;
   }

   private void func_75573_l() {
      int var1 = this.field_75584_b.size();
      if (var1 == 0) {
         this.field_75582_d.func_71571_b(0, 0, 0);
         this.field_75583_e = 0;
      } else {
         this.field_75582_d
            .func_71571_b(this.field_75585_c.field_71574_a / var1, this.field_75585_c.field_71572_b / var1, this.field_75585_c.field_71573_c / var1);
         int var2 = 0;

         for(VillageDoorInfo var4 : this.field_75584_b) {
            var2 = Math.max(var4.func_75474_b(this.field_75582_d.field_71574_a, this.field_75582_d.field_71572_b, this.field_75582_d.field_71573_c), var2);
         }

         this.field_75583_e = Math.max(32, (int)Math.sqrt((double)var2) + 1);
      }
   }

   public int func_82684_a(String var1) {
      Integer var2 = (Integer)this.field_82693_j.get(var1);
      return var2 != null ? var2 : 0;
   }

   public int func_82688_a(String var1, int var2) {
      int var3 = this.func_82684_a(var1);
      int var4 = MathHelper.func_76125_a(var3 + var2, -30, 10);
      this.field_82693_j.put(var1, var4);
      return var4;
   }

   public boolean func_82687_d(String var1) {
      return this.func_82684_a(var1) <= -15;
   }

   public void func_82690_a(NBTTagCompound var1) {
      this.field_75588_h = var1.func_74762_e("PopSize");
      this.field_75583_e = var1.func_74762_e("Radius");
      this.field_75587_j = var1.func_74762_e("Golems");
      this.field_75580_f = var1.func_74762_e("Stable");
      this.field_75581_g = var1.func_74762_e("Tick");
      this.field_82694_i = var1.func_74762_e("MTick");
      this.field_75582_d.field_71574_a = var1.func_74762_e("CX");
      this.field_75582_d.field_71572_b = var1.func_74762_e("CY");
      this.field_75582_d.field_71573_c = var1.func_74762_e("CZ");
      this.field_75585_c.field_71574_a = var1.func_74762_e("ACX");
      this.field_75585_c.field_71572_b = var1.func_74762_e("ACY");
      this.field_75585_c.field_71573_c = var1.func_74762_e("ACZ");
      NBTTagList var2 = var1.func_150295_c("Doors", 10);

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         VillageDoorInfo var5 = new VillageDoorInfo(
            var4.func_74762_e("X"),
            var4.func_74762_e("Y"),
            var4.func_74762_e("Z"),
            var4.func_74762_e("IDX"),
            var4.func_74762_e("IDZ"),
            var4.func_74762_e("TS")
         );
         this.field_75584_b.add(var5);
      }

      NBTTagList var6 = var1.func_150295_c("Players", 10);

      for(int var7 = 0; var7 < var6.func_74745_c(); ++var7) {
         NBTTagCompound var8 = var6.func_150305_b(var7);
         this.field_82693_j.put(var8.func_74779_i("Name"), var8.func_74762_e("S"));
      }
   }

   public void func_82689_b(NBTTagCompound var1) {
      var1.func_74768_a("PopSize", this.field_75588_h);
      var1.func_74768_a("Radius", this.field_75583_e);
      var1.func_74768_a("Golems", this.field_75587_j);
      var1.func_74768_a("Stable", this.field_75580_f);
      var1.func_74768_a("Tick", this.field_75581_g);
      var1.func_74768_a("MTick", this.field_82694_i);
      var1.func_74768_a("CX", this.field_75582_d.field_71574_a);
      var1.func_74768_a("CY", this.field_75582_d.field_71572_b);
      var1.func_74768_a("CZ", this.field_75582_d.field_71573_c);
      var1.func_74768_a("ACX", this.field_75585_c.field_71574_a);
      var1.func_74768_a("ACY", this.field_75585_c.field_71572_b);
      var1.func_74768_a("ACZ", this.field_75585_c.field_71573_c);
      NBTTagList var2 = new NBTTagList();

      for(VillageDoorInfo var4 : this.field_75584_b) {
         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74768_a("X", var4.field_75481_a);
         var5.func_74768_a("Y", var4.field_75479_b);
         var5.func_74768_a("Z", var4.field_75480_c);
         var5.func_74768_a("IDX", var4.field_75477_d);
         var5.func_74768_a("IDZ", var4.field_75478_e);
         var5.func_74768_a("TS", var4.field_75475_f);
         var2.func_74742_a(var5);
      }

      var1.func_74782_a("Doors", var2);
      NBTTagList var7 = new NBTTagList();

      for(String var9 : this.field_82693_j.keySet()) {
         NBTTagCompound var6 = new NBTTagCompound();
         var6.func_74778_a("Name", var9);
         var6.func_74768_a("S", this.field_82693_j.get(var9));
         var7.func_74742_a(var6);
      }

      var1.func_74782_a("Players", var7);
   }

   public void func_82692_h() {
      this.field_82694_i = this.field_75581_g;
   }

   public boolean func_82686_i() {
      return this.field_82694_i == 0 || this.field_75581_g - this.field_82694_i >= 3600;
   }

   public void func_82683_b(int var1) {
      for(String var3 : this.field_82693_j.keySet()) {
         this.func_82688_a(var3, var1);
      }
   }
}
