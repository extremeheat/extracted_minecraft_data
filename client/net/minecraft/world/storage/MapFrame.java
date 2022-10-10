package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.BlockPos;

public class MapFrame {
   private final BlockPos field_212771_a;
   private int field_212772_b;
   private int field_212773_c;

   public MapFrame(BlockPos var1, int var2, int var3) {
      super();
      this.field_212771_a = var1;
      this.field_212772_b = var2;
      this.field_212773_c = var3;
   }

   public static MapFrame func_212765_a(NBTTagCompound var0) {
      BlockPos var1 = NBTUtil.func_186861_c(var0.func_74775_l("Pos"));
      int var2 = var0.func_74762_e("Rotation");
      int var3 = var0.func_74762_e("EntityId");
      return new MapFrame(var1, var2, var3);
   }

   public NBTTagCompound func_212770_a() {
      NBTTagCompound var1 = new NBTTagCompound();
      var1.func_74782_a("Pos", NBTUtil.func_186859_a(this.field_212771_a));
      var1.func_74768_a("Rotation", this.field_212772_b);
      var1.func_74768_a("EntityId", this.field_212773_c);
      return var1;
   }

   public BlockPos func_212764_b() {
      return this.field_212771_a;
   }

   public int func_212768_c() {
      return this.field_212772_b;
   }

   public int func_212769_d() {
      return this.field_212773_c;
   }

   public String func_212767_e() {
      return func_212766_a(this.field_212771_a);
   }

   public static String func_212766_a(BlockPos var0) {
      return "frame-" + var0.func_177958_n() + "," + var0.func_177956_o() + "," + var0.func_177952_p();
   }
}
