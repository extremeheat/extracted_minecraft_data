package net.minecraft.util;

import net.minecraft.nbt.NBTTagCompound;

public class WeightedSpawnerEntity extends WeightedRandom.Item {
   private final NBTTagCompound field_185279_b;

   public WeightedSpawnerEntity() {
      super(1);
      this.field_185279_b = new NBTTagCompound();
      this.field_185279_b.func_74778_a("id", "minecraft:pig");
   }

   public WeightedSpawnerEntity(NBTTagCompound var1) {
      this(var1.func_150297_b("Weight", 99) ? var1.func_74762_e("Weight") : 1, var1.func_74775_l("Entity"));
   }

   public WeightedSpawnerEntity(int var1, NBTTagCompound var2) {
      super(var1);
      this.field_185279_b = var2;
   }

   public NBTTagCompound func_185278_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      if (!this.field_185279_b.func_150297_b("id", 8)) {
         this.field_185279_b.func_74778_a("id", "minecraft:pig");
      } else if (!this.field_185279_b.func_74779_i("id").contains(":")) {
         this.field_185279_b.func_74778_a("id", (new ResourceLocation(this.field_185279_b.func_74779_i("id"))).toString());
      }

      var1.func_74782_a("Entity", this.field_185279_b);
      var1.func_74768_a("Weight", this.field_76292_a);
      return var1;
   }

   public NBTTagCompound func_185277_b() {
      return this.field_185279_b;
   }
}
