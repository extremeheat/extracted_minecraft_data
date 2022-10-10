package net.minecraft.village;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.WorldSavedData;

public class VillageCollection extends WorldSavedData {
   private World field_75556_a;
   private final List<BlockPos> field_75554_b = Lists.newArrayList();
   private final List<VillageDoorInfo> field_75555_c = Lists.newArrayList();
   private final List<Village> field_75552_d = Lists.newArrayList();
   private int field_75553_e;

   public VillageCollection(String var1) {
      super(var1);
   }

   public VillageCollection(World var1) {
      super(func_176062_a(var1.field_73011_w));
      this.field_75556_a = var1;
      this.func_76185_a();
   }

   public void func_82566_a(World var1) {
      this.field_75556_a = var1;
      Iterator var2 = this.field_75552_d.iterator();

      while(var2.hasNext()) {
         Village var3 = (Village)var2.next();
         var3.func_82691_a(var1);
      }

   }

   public void func_176060_a(BlockPos var1) {
      if (this.field_75554_b.size() <= 64) {
         if (!this.func_176057_e(var1)) {
            this.field_75554_b.add(var1);
         }

      }
   }

   public void func_75544_a() {
      ++this.field_75553_e;
      Iterator var1 = this.field_75552_d.iterator();

      while(var1.hasNext()) {
         Village var2 = (Village)var1.next();
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

   public List<Village> func_75540_b() {
      return this.field_75552_d;
   }

   public Village func_176056_a(BlockPos var1, int var2) {
      Village var3 = null;
      double var4 = 3.4028234663852886E38D;
      Iterator var6 = this.field_75552_d.iterator();

      while(var6.hasNext()) {
         Village var7 = (Village)var6.next();
         double var8 = var7.func_180608_a().func_177951_i(var1);
         if (var8 < var4) {
            float var10 = (float)(var2 + var7.func_75568_b());
            if (var8 <= (double)(var10 * var10)) {
               var3 = var7;
               var4 = var8;
            }
         }
      }

      return var3;
   }

   private void func_75543_d() {
      if (!this.field_75554_b.isEmpty()) {
         this.func_180609_b((BlockPos)this.field_75554_b.remove(0));
      }
   }

   private void func_75545_e() {
      for(int var1 = 0; var1 < this.field_75555_c.size(); ++var1) {
         VillageDoorInfo var2 = (VillageDoorInfo)this.field_75555_c.get(var1);
         Village var3 = this.func_176056_a(var2.func_179852_d(), 32);
         if (var3 == null) {
            var3 = new Village(this.field_75556_a);
            this.field_75552_d.add(var3);
            this.func_76185_a();
         }

         var3.func_75576_a(var2);
      }

      this.field_75555_c.clear();
   }

   private void func_180609_b(BlockPos var1) {
      boolean var2 = true;
      boolean var3 = true;
      boolean var4 = true;
      BlockPos.MutableBlockPos var5 = new BlockPos.MutableBlockPos();

      for(int var6 = -16; var6 < 16; ++var6) {
         for(int var7 = -4; var7 < 4; ++var7) {
            for(int var8 = -16; var8 < 16; ++var8) {
               var5.func_189533_g(var1).func_196234_d(var6, var7, var8);
               IBlockState var9 = this.field_75556_a.func_180495_p(var5);
               if (this.func_195928_a(var9)) {
                  VillageDoorInfo var10 = this.func_176055_c(var5);
                  if (var10 == null) {
                     this.func_195927_a(var9, var5);
                  } else {
                     var10.func_179849_a(this.field_75553_e);
                  }
               }
            }
         }
      }

   }

   @Nullable
   private VillageDoorInfo func_176055_c(BlockPos var1) {
      Iterator var2 = this.field_75555_c.iterator();

      VillageDoorInfo var3;
      do {
         if (!var2.hasNext()) {
            var2 = this.field_75552_d.iterator();

            VillageDoorInfo var4;
            do {
               if (!var2.hasNext()) {
                  return null;
               }

               Village var5 = (Village)var2.next();
               var4 = var5.func_179864_e(var1);
            } while(var4 == null);

            return var4;
         }

         var3 = (VillageDoorInfo)var2.next();
      } while(var3.func_179852_d().func_177958_n() != var1.func_177958_n() || var3.func_179852_d().func_177952_p() != var1.func_177952_p() || Math.abs(var3.func_179852_d().func_177956_o() - var1.func_177956_o()) > 1);

      return var3;
   }

   private void func_195927_a(IBlockState var1, BlockPos var2) {
      EnumFacing var3 = (EnumFacing)var1.func_177229_b(BlockDoor.field_176520_a);
      EnumFacing var4 = var3.func_176734_d();
      int var5 = this.func_176061_a(var2, var3, 5);
      int var6 = this.func_176061_a(var2, var4, var5 + 1);
      if (var5 != var6) {
         this.field_75555_c.add(new VillageDoorInfo(var2, var5 < var6 ? var3 : var4, this.field_75553_e));
      }

   }

   private int func_176061_a(BlockPos var1, EnumFacing var2, int var3) {
      int var4 = 0;

      for(int var5 = 1; var5 <= 5; ++var5) {
         if (this.field_75556_a.func_175678_i(var1.func_177967_a(var2, var5))) {
            ++var4;
            if (var4 >= var3) {
               return var4;
            }
         }
      }

      return var4;
   }

   private boolean func_176057_e(BlockPos var1) {
      Iterator var2 = this.field_75554_b.iterator();

      BlockPos var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (BlockPos)var2.next();
      } while(!var3.equals(var1));

      return true;
   }

   private boolean func_195928_a(IBlockState var1) {
      return var1.func_177230_c() instanceof BlockDoor && var1.func_185904_a() == Material.field_151575_d;
   }

   public void func_76184_a(NBTTagCompound var1) {
      this.field_75553_e = var1.func_74762_e("Tick");
      NBTTagList var2 = var1.func_150295_c("Villages", 10);

      for(int var3 = 0; var3 < var2.size(); ++var3) {
         NBTTagCompound var4 = var2.func_150305_b(var3);
         Village var5 = new Village();
         var5.func_82690_a(var4);
         this.field_75552_d.add(var5);
      }

   }

   public NBTTagCompound func_189551_b(NBTTagCompound var1) {
      var1.func_74768_a("Tick", this.field_75553_e);
      NBTTagList var2 = new NBTTagList();
      Iterator var3 = this.field_75552_d.iterator();

      while(var3.hasNext()) {
         Village var4 = (Village)var3.next();
         NBTTagCompound var5 = new NBTTagCompound();
         var4.func_82689_b(var5);
         var2.add((INBTBase)var5);
      }

      var1.func_74782_a("Villages", var2);
      return var1;
   }

   public static String func_176062_a(Dimension var0) {
      return "villages" + var0.func_186058_p().func_186067_c();
   }
}
