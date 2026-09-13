package net.minecraft.village;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private World field_75556_a;
   private final List field_75554_b = new ArrayList();
   private final List field_75555_c = new ArrayList();
   private final List field_75552_d = new ArrayList();
   private int field_75553_e;

   public VillageCollection(String var1) {
      super(var1);
   }

   public VillageCollection(World var1) {
      super("villages");
      this.field_75556_a = var1;
      this.func_76185_a();
   }

   public void func_82566_a(World var1) {
      this.field_75556_a = var1;

      for(Village var3 : this.field_75552_d) {
         var3.func_82691_a(var1);
      }
   }

   public void func_75551_a(int var1, int var2, int var3) {
      if (this.field_75554_b.size() <= 64) {
         if (!this.func_75548_d(var1, var2, var3)) {
            this.field_75554_b.add(new ChunkCoordinates(var1, var2, var3));
         }
      }
   }

   public void func_75544_a() {
      ++this.field_75553_e;

      for(Village var2 : this.field_75552_d) {
         var2.func_75560_a(this.field_75553_e);
      }

      this.func_75549_c();
      this.func_75543_d();
      this.func_75545_e();
      if (this.field_75553_e % 400 == 0) {
         this.func_76185_a();
      }
   }

   private void func_75549_c() {
      Iterator var1 = this.field_75552_d.iterator();

      while(var1.hasNext()) {
         Village var2 = (Village)var1.next();
         if (var2.func_75566_g()) {
            var1.remove();
            this.func_76185_a();
         }
      }
   }

   public List func_75540_b() {
      return this.field_75552_d;
   }

   public Village func_75550_a(int var1, int var2, int var3, int var4) {
      Village var5 = null;
      float var6 = 3.4028235E38F;

      for(Village var8 : this.field_75552_d) {
         float var9 = var8.func_75577_a().func_71569_e(var1, var2, var3);
         if (!(var9 >= var6)) {
            float var10 = (float)(var4 + var8.func_75568_b());
            if (!(var9 > var10 * var10)) {
               var5 = var8;
               var6 = var9;
            }
         }
      }

      return var5;
   }

   private void func_75543_d() {
      if (!this.field_75554_b.isEmpty()) {
         this.func_75546_a((ChunkCoordinates)this.field_75554_b.remove(0));
      }
   }

   private void func_75545_e() {
      for(int var1 = 0; var1 < this.field_75555_c.size(); ++var1) {
         VillageDoorInfo var2 = (VillageDoorInfo)this.field_75555_c.get(var1);
         boolean var3 = false;

         for(Village var5 : this.field_75552_d) {
            int var6 = (int)var5.func_75577_a().func_71569_e(var2.field_75481_a, var2.field_75479_b, var2.field_75480_c);
            int var7 = 32 + var5.func_75568_b();
            if (var6 <= var7 * var7) {
               var5.func_75576_a(var2);
               var3 = true;
               break;
            }
         }

         if (!var3) {
            Village var8 = new Village(this.field_75556_a);
            var8.func_75576_a(var2);
            this.field_75552_d.add(var8);
            this.func_76185_a();
         }
      }

      this.field_75555_c.clear();
   }

   private void func_75546_a(ChunkCoordinates var1) {
      byte var2 = 16;
      byte var3 = 4;
      byte var4 = 16;

      for(int var5 = var1.field_71574_a - var2; var5 < var1.field_71574_a + var2; ++var5) {
         for(int var6 = var1.field_71572_b - var3; var6 < var1.field_71572_b + var3; ++var6) {
            for(int var7 = var1.field_71573_c - var4; var7 < var1.field_71573_c + var4; ++var7) {
               if (this.func_75541_e(var5, var6, var7)) {
                  VillageDoorInfo var8 = this.func_75547_b(var5, var6, var7);
                  if (var8 == null) {
                     this.func_75542_c(var5, var6, var7);
                  } else {
                     var8.field_75475_f = this.field_75553_e;
                  }
               }
            }
         }
      }
   }

   private VillageDoorInfo func_75547_b(int var1, int var2, int var3) {
      for(VillageDoorInfo var5 : this.field_75555_c) {
         if (var5.field_75481_a == var1 && var5.field_75480_c == var3 && Math.abs(var5.field_75479_b - var2) <= 1) {
            return var5;
         }
      }

      for(Village var8 : this.field_75552_d) {
         VillageDoorInfo var6 = var8.func_75578_e(var1, var2, var3);
         if (var6 != null) {
            return var6;
         }
      }

      return null;
   }

   private void func_75542_c(int var1, int var2, int var3) {
      int var4 = ((BlockDoor)Blocks.field_150466_ao).func_150013_e(this.field_75556_a, var1, var2, var3);
      if (var4 != 0 && var4 != 2) {
         int var7 = 0;

         for(int var9 = -5; var9 < 0; ++var9) {
            if (this.field_75556_a.func_72937_j(var1, var2, var3 + var9)) {
               --var7;
            }
         }

         for(int var10 = 1; var10 <= 5; ++var10) {
            if (this.field_75556_a.func_72937_j(var1, var2, var3 + var10)) {
               ++var7;
            }
         }

         if (var7 != 0) {
            this.field_75555_c.add(new VillageDoorInfo(var1, var2, var3, 0, var7 > 0 ? -2 : 2, this.field_75553_e));
         }
      } else {
         int var5 = 0;

         for(int var6 = -5; var6 < 0; ++var6) {
            if (this.field_75556_a.func_72937_j(var1 + var6, var2, var3)) {
               --var5;
            }
         }

         for(int var8 = 1; var8 <= 5; ++var8) {
            if (this.field_75556_a.func_72937_j(var1 + var8, var2, var3)) {
               ++var5;
            }
         }

         if (var5 != 0) {
            this.field_75555_c.add(new VillageDoorInfo(var1, var2, var3, var5 > 0 ? -2 : 2, 0, this.field_75553_e));
         }
      }
   }

   private boolean func_75548_d(int var1, int var2, int var3) {
      for(ChunkCoordinates var5 : this.field_75554_b) {
         if (var5.field_71574_a == var1 && var5.field_71572_b == var2 && var5.field_71573_c == var3) {
            return true;
         }
      }

      return false;
   }

   private boolean func_75541_e(int var1, int var2, int var3) {
      return this.field_75556_a.func_147439_a(var1, var2, var3) == Blocks.field_150466_ao;
   }

   @Override
   public void func_76184_a(NBTTagCompound var1) {
      this.field_75553_e = var1.func_74762_e("Tick");
      NBTTagList var2 = var1.func_150295_c("Villages", 10);

      for(int var3 = 0; var3 < var2.func_74745_c(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         Village var5 = new Village();
         var5.func_82690_a(var4);
         this.field_75552_d.add(var5);
      }
   }

   @Override
   public void func_76187_b(NBTTagCompound var1) {
      var1.func_74768_a("Tick", this.field_75553_e);
      NBTTagList var2 = new NBTTagList();

      for(Village var4 : this.field_75552_d) {
         NBTTagCompound var5 = new NBTTagCompound();
         var4.func_82689_b(var5);
         var2.func_74742_a(var5);
      }

      var1.func_74782_a("Villages", var2);
   }
}
