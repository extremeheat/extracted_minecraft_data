package net.minecraft.world.gen.feature.structure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class StructureIndexesSavedData extends WorldSavedData {
   private LongSet field_208026_a = new LongOpenHashSet();
   private LongSet field_208027_b = new LongOpenHashSet();

   public StructureIndexesSavedData(String var1) {
      super(var1);
   }

   public void func_76184_a(NBTTagCompound var1) {
      this.field_208026_a = new LongOpenHashSet(var1.func_197645_o("All"));
      this.field_208027_b = new LongOpenHashSet(var1.func_197645_o("Remaining"));
   }

   public NBTTagCompound func_189551_b(NBTTagCompound var1) {
      var1.func_197644_a("All", this.field_208026_a.toLongArray());
      var1.func_197644_a("Remaining", this.field_208027_b.toLongArray());
      return var1;
   }

   public void func_201763_a(long var1) {
      this.field_208026_a.add(var1);
      this.field_208027_b.add(var1);
   }

   public boolean func_208024_b(long var1) {
      return this.field_208026_a.contains(var1);
   }

   public boolean func_208023_c(long var1) {
      return this.field_208027_b.contains(var1);
   }

   public void func_201762_c(long var1) {
      this.field_208027_b.remove(var1);
   }

   public LongSet func_208025_a() {
      return this.field_208026_a;
   }
}
