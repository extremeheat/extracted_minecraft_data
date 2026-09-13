package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandom$Item;

public class MobSpawnerBaseLogic$WeightedRandomMinecart extends WeightedRandom$Item {
   public final NBTTagCompound field_98222_b;
   public final String field_98223_c;

   public MobSpawnerBaseLogic$WeightedRandomMinecart(MobSpawnerBaseLogic var1, NBTTagCompound var2) {
      super(var2.func_74762_e("Weight"));
      this.field_98221_d = var1;
      NBTTagCompound var3 = var2.func_74775_l("Properties");
      String var4 = var2.func_74779_i("Type");
      if (var4.equals("Minecart")) {
         if (var3 != null) {
            switch(var3.func_74762_e("Type")) {
               case 0:
                  var4 = "MinecartRideable";
                  break;
               case 1:
                  var4 = "MinecartChest";
                  break;
               case 2:
                  var4 = "MinecartFurnace";
            }
         } else {
            var4 = "MinecartRideable";
         }
      }

      this.field_98222_b = var3;
      this.field_98223_c = var4;
   }

   public MobSpawnerBaseLogic$WeightedRandomMinecart(MobSpawnerBaseLogic var1, NBTTagCompound var2, String var3) {
      super(1);
      this.field_98221_d = var1;
      if (var3.equals("Minecart")) {
         if (var2 != null) {
            switch(var2.func_74762_e("Type")) {
               case 0:
                  var3 = "MinecartRideable";
                  break;
               case 1:
                  var3 = "MinecartChest";
                  break;
               case 2:
                  var3 = "MinecartFurnace";
            }
         } else {
            var3 = "MinecartRideable";
         }
      }

      this.field_98222_b = var2;
      this.field_98223_c = var3;
   }

   public NBTTagCompound func_98220_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74782_a("Properties", this.field_98222_b);
      var1.func_74778_a("Type", this.field_98223_c);
      var1.func_74768_a("Weight", this.field_76292_a);
      return var1;
   }
}
