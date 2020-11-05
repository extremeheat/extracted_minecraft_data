package net.minecraft.world.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.WeighedRandom;

public class SpawnData extends WeighedRandom.WeighedRandomItem {
   private final CompoundTag tag;

   public SpawnData() {
      super(1);
      this.tag = new CompoundTag();
      this.tag.putString("id", "minecraft:pig");
   }

   public SpawnData(CompoundTag var1) {
      this(var1.contains("Weight", 99) ? var1.getInt("Weight") : 1, var1.getCompound("Entity"));
   }

   public SpawnData(int var1, CompoundTag var2) {
      super(var1);
      this.tag = var2;
      ResourceLocation var3 = ResourceLocation.tryParse(var2.getString("id"));
      if (var3 != null) {
         var2.putString("id", var3.toString());
      } else {
         var2.putString("id", "minecraft:pig");
      }

   }

   public CompoundTag save() {
      CompoundTag var1 = new CompoundTag();
      var1.put("Entity", this.tag);
      var1.putInt("Weight", this.weight);
      return var1;
   }

   public CompoundTag getTag() {
      return this.tag;
   }
}
