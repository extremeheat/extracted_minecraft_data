package net.minecraft.village;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Village {
   private World field_75586_a;
   private final List<VillageDoorInfo> field_75584_b = Lists.newArrayList();
   private BlockPos field_75585_c;
   private BlockPos field_75582_d;
   private int field_75583_e;
   private int field_75580_f;
   private int field_75581_g;
   private int field_75588_h;
   private int field_82694_i;
   private TreeMap<String, Integer> field_82693_j;
   private List<Village.VillageAggressor> field_75589_i;
   private int field_75587_j;

   public Village() {
      super();
      this.field_75585_c = BlockPos.field_177992_a;
      this.field_75582_d = BlockPos.field_177992_a;
      this.field_82693_j = new TreeMap();
      this.field_75589_i = Lists.newArrayList();
   }

   public Village(World var1) {
      super();
      this.field_75585_c = BlockPos.field_177992_a;
      this.field_75582_d = BlockPos.field_177992_a;
      this.field_82693_j = new TreeMap();
      this.field_75589_i = Lists.newArrayList();
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
         Vec3 var3 = this.func_179862_a(this.field_75582_d, 2, 4, 2);
         if (var3 != null) {
            EntityIronGolem var4 = new EntityIronGolem(this.field_75586_a);
            var4.func_70107_b(var3.field_72450_a, var3.field_72448_b, var3.field_72449_c);
            this.field_75586_a.func_72838_d(var4);
            ++this.field_75587_j;
         }
      }

   }

   private Vec3 func_179862_a(BlockPos var1, int var2, int var3, int var4) {
      for(int var5 = 0; var5 < 10; ++var5) {
         BlockPos var6 = var1.func_177982_a(this.field_75586_a.field_73012_v.nextInt(16) - 8, this.field_75586_a.field_73012_v.nextInt(6) - 3, this.field_75586_a.field_73012_v.nextInt(16) - 8);
         if (this.func_179866_a(var6) && this.func_179861_a(new BlockPos(var2, var3, var4), var6)) {
            return new Vec3((double)var6.func_177958_n(), (double)var6.func_177956_o(), (double)var6.func_177952_p());
         }
      }

      return null;
   }

   private boolean func_179861_a(BlockPos var1, BlockPos var2) {
      if (!World.func_175683_a(this.field_75586_a, var2.func_177977_b())) {
         return false;
      } else {
         int var3 = var2.func_177958_n() - var1.func_177958_n() / 2;
         int var4 = var2.func_177952_p() - var1.func_177952_p() / 2;

         for(int var5 = var3; var5 < var3 + var1.func_177958_n(); ++var5) {
            for(int var6 = var2.func_177956_o(); var6 < var2.func_177956_o() + var1.func_177956_o(); ++var6) {
               for(int var7 = var4; var7 < var4 + var1.func_177952_p(); ++var7) {
                  if (this.field_75586_a.func_180495_p(new BlockPos(var5, var6, var7)).func_177230_c().func_149721_r()) {
                     return false;
                  }
               }
            }
         }

         return true;
      }
   }

   private void func_75579_h() {
      List var1 = this.field_75586_a.func_72872_a(EntityIronGolem.class, new AxisAlignedBB((double)(this.field_75582_d.func_177958_n() - this.field_75583_e), (double)(this.field_75582_d.func_177956_o() - 4), (double)(this.field_75582_d.func_177952_p() - this.field_75583_e), (double)(this.field_75582_d.func_177958_n() + this.field_75583_e), (double)(this.field_75582_d.func_177956_o() + 4), (double)(this.field_75582_d.func_177952_p() + this.field_75583_e)));
      this.field_75587_j = var1.size();
   }

   private void func_75572_i() {
      List var1 = this.field_75586_a.func_72872_a(EntityVillager.class, new AxisAlignedBB((double)(this.field_75582_d.func_177958_n() - this.field_75583_e), (double)(this.field_75582_d.func_177956_o() - 4), (double)(this.field_75582_d.func_177952_p() - this.field_75583_e), (double)(this.field_75582_d.func_177958_n() + this.field_75583_e), (double)(this.field_75582_d.func_177956_o() + 4), (double)(this.field_75582_d.func_177952_p() + this.field_75583_e)));
      this.field_75588_h = var1.size();
      if (this.field_75588_h == 0) {
         this.field_82693_j.clear();
      }

   }

   public BlockPos func_180608_a() {
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

   public boolean func_179866_a(BlockPos var1) {
      return this.field_75582_d.func_177951_i(var1) < (double)(this.field_75583_e * this.field_75583_e);
   }

   public List<VillageDoorInfo> func_75558_f() {
      return this.field_75584_b;
   }

   public VillageDoorInfo func_179865_b(BlockPos var1) {
      VillageDoorInfo var2 = null;
      int var3 = 2147483647;
      Iterator var4 = this.field_75584_b.iterator();

      while(var4.hasNext()) {
         VillageDoorInfo var5 = (VillageDoorInfo)var4.next();
         int var6 = var5.func_179848_a(var1);
         if (var6 < var3) {
            var2 = var5;
            var3 = var6;
         }
      }

      return var2;
   }

   public VillageDoorInfo func_179863_c(BlockPos var1) {
      VillageDoorInfo var2 = null;
      int var3 = 2147483647;
      Iterator var4 = this.field_75584_b.iterator();

      while(var4.hasNext()) {
         VillageDoorInfo var5 = (VillageDoorInfo)var4.next();
         int var6 = var5.func_179848_a(var1);
         if (var6 > 256) {
            var6 *= 1000;
         } else {
            var6 = var5.func_75468_f();
         }

         if (var6 < var3) {
            var2 = var5;
            var3 = var6;
         }
      }

      return var2;
   }

   public VillageDoorInfo func_179864_e(BlockPos var1) {
      if (this.field_75582_d.func_177951_i(var1) > (double)(this.field_75583_e * this.field_75583_e)) {
         return null;
      } else {
         Iterator var2 = this.field_75584_b.iterator();

         VillageDoorInfo var3;
         do {
            if (!var2.hasNext()) {
               return null;
            }

            var3 = (VillageDoorInfo)var2.next();
         } while(var3.func_179852_d().func_177958_n() != var1.func_177958_n() || var3.func_179852_d().func_177952_p() != var1.func_177952_p() || Math.abs(var3.func_179852_d().func_177956_o() - var1.func_177956_o()) > 1);

         return var3;
      }
   }

   public void func_75576_a(VillageDoorInfo var1) {
      this.field_75584_b.add(var1);
      this.field_75585_c = this.field_75585_c.func_177971_a(var1.func_179852_d());
      this.func_75573_l();
      this.field_75580_f = var1.func_75473_b();
   }

   public boolean func_75566_g() {
      return this.field_75584_b.isEmpty();
   }

   public void func_75575_a(EntityLivingBase var1) {
      Iterator var2 = this.field_75589_i.iterator();

      Village.VillageAggressor var3;
      do {
         if (!var2.hasNext()) {
            this.field_75589_i.add(new Village.VillageAggressor(var1, this.field_75581_g));
            return;
         }

         var3 = (Village.VillageAggressor)var2.next();
      } while(var3.field_75592_a != var1);

      var3.field_75590_b = this.field_75581_g;
   }

   public EntityLivingBase func_75571_b(EntityLivingBase var1) {
      double var2 = 1.7976931348623157E308D;
      Village.VillageAggressor var4 = null;

      for(int var5 = 0; var5 < this.field_75589_i.size(); ++var5) {
         Village.VillageAggressor var6 = (Village.VillageAggressor)this.field_75589_i.get(var5);
         double var7 = var6.field_75592_a.func_70068_e(var1);
         if (var7 <= var2) {
            var4 = var6;
            var2 = var7;
         }
      }

      return var4 != null ? var4.field_75592_a : null;
   }

   public EntityPlayer func_82685_c(EntityLivingBase var1) {
      double var2 = 1.7976931348623157E308D;
      EntityPlayer var4 = null;
      Iterator var5 = this.field_82693_j.keySet().iterator();

      while(var5.hasNext()) {
         String var6 = (String)var5.next();
         if (this.func_82687_d(var6)) {
            EntityPlayer var7 = this.field_75586_a.func_72924_a(var6);
            if (var7 != null) {
               double var8 = var7.func_70068_e(var1);
               if (var8 <= var2) {
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

      while(true) {
         Village.VillageAggressor var2;
         do {
            if (!var1.hasNext()) {
               return;
            }

            var2 = (Village.VillageAggressor)var1.next();
         } while(var2.field_75592_a.func_70089_S() && Math.abs(this.field_75581_g - var2.field_75590_b) <= 300);

         var1.remove();
      }
   }

   private void func_75557_k() {
      boolean var1 = false;
      boolean var2 = this.field_75586_a.field_73012_v.nextInt(50) == 0;
      Iterator var3 = this.field_75584_b.iterator();

      while(true) {
         VillageDoorInfo var4;
         do {
            if (!var3.hasNext()) {
               if (var1) {
                  this.func_75573_l();
               }

               return;
            }

            var4 = (VillageDoorInfo)var3.next();
            if (var2) {
               var4.func_75466_d();
            }
         } while(this.func_179860_f(var4.func_179852_d()) && Math.abs(this.field_75581_g - var4.func_75473_b()) <= 1200);

         this.field_75585_c = this.field_75585_c.func_177973_b(var4.func_179852_d());
         var1 = true;
         var4.func_179853_a(true);
         var3.remove();
      }
   }

   private boolean func_179860_f(BlockPos var1) {
      Block var2 = this.field_75586_a.func_180495_p(var1).func_177230_c();
      if (var2 instanceof BlockDoor) {
         return var2.func_149688_o() == Material.field_151575_d;
      } else {
         return false;
      }
   }

   private void func_75573_l() {
      int var1 = this.field_75584_b.size();
      if (var1 == 0) {
         this.field_75582_d = new BlockPos(0, 0, 0);
         this.field_75583_e = 0;
      } else {
         this.field_75582_d = new BlockPos(this.field_75585_c.func_177958_n() / var1, this.field_75585_c.func_177956_o() / var1, this.field_75585_c.func_177952_p() / var1);
         int var2 = 0;

         VillageDoorInfo var4;
         for(Iterator var3 = this.field_75584_b.iterator(); var3.hasNext(); var2 = Math.max(var4.func_179848_a(this.field_75582_d), var2)) {
            var4 = (VillageDoorInfo)var3.next();
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
      this.field_75582_d = new BlockPos(var1.func_74762_e("CX"), var1.func_74762_e("CY"), var1.func_74762_e("CZ"));
      this.field_75585_c = new BlockPos(var1.func_74762_e("ACX"), var1.func_74762_e("ACY"), var1.func_74762_e("ACZ"));
      NBTTagList var2 = var1.func_150295_c("Doors", 10);

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         VillageDoorInfo var5 = new VillageDoorInfo(new BlockPos(var4.func_74762_e("X"), var4.func_74762_e("Y"), var4.func_74762_e("Z")), var4.func_74762_e("IDX"), var4.func_74762_e("IDZ"), var4.func_74762_e("TS"));
         this.field_75584_b.add(var5);
      }

      NBTTagList var8 = var1.func_150295_c("Players", 10);

      for(int var9 = 0; var9 < var8.func_74745_c(); ++var9) {
         NBTTagCompound var10 = var8.func_150305_b(var9);
         if (var10.func_74764_b("UUID")) {
            PlayerProfileCache var6 = MinecraftServer.func_71276_C().func_152358_ax();
            GameProfile var7 = var6.func_152652_a(UUID.fromString(var10.func_74779_i("UUID")));
            if (var7 != null) {
               this.field_82693_j.put(var7.getName(), var10.func_74762_e("S"));
            }
         } else {
            this.field_82693_j.put(var10.func_74779_i("Name"), var10.func_74762_e("S"));
         }
      }

   }

   public void func_82689_b(NBTTagCompound var1) {
      var1.func_74768_a("PopSize", this.field_75588_h);
      var1.func_74768_a("Radius", this.field_75583_e);
      var1.func_74768_a("Golems", this.field_75587_j);
      var1.func_74768_a("Stable", this.field_75580_f);
      var1.func_74768_a("Tick", this.field_75581_g);
      var1.func_74768_a("MTick", this.field_82694_i);
      var1.func_74768_a("CX", this.field_75582_d.func_177958_n());
      var1.func_74768_a("CY", this.field_75582_d.func_177956_o());
      var1.func_74768_a("CZ", this.field_75582_d.func_177952_p());
      var1.func_74768_a("ACX", this.field_75585_c.func_177958_n());
      var1.func_74768_a("ACY", this.field_75585_c.func_177956_o());
      var1.func_74768_a("ACZ", this.field_75585_c.func_177952_p());
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = this.field_75584_b.iterator();

      while(var3.hasNext()) {
         VillageDoorInfo var4 = (VillageDoorInfo)var3.next();
         NBTTagCompound var5 = new NBTTagCompound();
         var5.func_74768_a("X", var4.func_179852_d().func_177958_n());
         var5.func_74768_a("Y", var4.func_179852_d().func_177956_o());
         var5.func_74768_a("Z", var4.func_179852_d().func_177952_p());
         var5.func_74768_a("IDX", var4.func_179847_f());
         var5.func_74768_a("IDZ", var4.func_179855_g());
         var5.func_74768_a("TS", var4.func_75473_b());
         var2.func_74742_a(var5);
      }

      var1.func_74782_a("Doors", var2);
      NBTTagList var9 = new NBTTagList();
      Iterator var10 = this.field_82693_j.keySet().iterator();

      while(var10.hasNext()) {
         String var11 = (String)var10.next();
         NBTTagCompound var6 = new NBTTagCompound();
         PlayerProfileCache var7 = MinecraftServer.func_71276_C().func_152358_ax();
         GameProfile var8 = var7.func_152655_a(var11);
         if (var8 != null) {
            var6.func_74778_a("UUID", var8.getId().toString());
            var6.func_74768_a("S", (Integer)this.field_82693_j.get(var11));
            var9.func_74742_a(var6);
         }
      }

      var1.func_74782_a("Players", var9);
   }

   public void func_82692_h() {
      this.field_82694_i = this.field_75581_g;
   }

   public boolean func_82686_i() {
      return this.field_82694_i == 0 || this.field_75581_g - this.field_82694_i >= 3600;
   }

   public void func_82683_b(int var1) {
      Iterator var2 = this.field_82693_j.keySet().iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         this.func_82688_a(var3, var1);
      }

   }

   class VillageAggressor {
      public EntityLivingBase field_75592_a;
      public int field_75590_b;

      VillageAggressor(EntityLivingBase var2, int var3) {
         super();
         this.field_75592_a = var2;
         this.field_75590_b = var3;
      }
   }
}
