package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class StructureStart {
   protected LinkedList<StructureComponent> field_75075_a = new LinkedList();
   protected StructureBoundingBox field_75074_b;
   private int field_143024_c;
   private int field_143023_d;

   public StructureStart() {
      super();
   }

   public StructureStart(int var1, int var2) {
      super();
      this.field_143024_c = var1;
      this.field_143023_d = var2;
   }

   public StructureBoundingBox func_75071_a() {
      return this.field_75074_b;
   }

   public LinkedList<StructureComponent> func_75073_b() {
      return this.field_75075_a;
   }

   public void func_75068_a(World var1, Random var2, StructureBoundingBox var3) {
      Iterator var4 = this.field_75075_a.iterator();

      while(var4.hasNext()) {
         StructureComponent var5 = (StructureComponent)var4.next();
         if (var5.func_74874_b().func_78884_a(var3) && !var5.func_74875_a(var1, var2, var3)) {
            var4.remove();
         }
      }

   }

   protected void func_75072_c() {
      this.field_75074_b = StructureBoundingBox.func_78887_a();
      Iterator var1 = this.field_75075_a.iterator();

      while(var1.hasNext()) {
         StructureComponent var2 = (StructureComponent)var1.next();
         this.field_75074_b.func_78888_b(var2.func_74874_b());
      }

   }

   public NBTTagCompound func_143021_a(int var1, int var2) {
      NBTTagCompound var3 = new NBTTagCompound();
      var3.func_74778_a("id", MapGenStructureIO.func_143033_a(this));
      var3.func_74768_a("ChunkX", var1);
      var3.func_74768_a("ChunkZ", var2);
      var3.func_74782_a("BB", this.field_75074_b.func_151535_h());
      NBTTagList var4 = new NBTTagList();
      Iterator var5 = this.field_75075_a.iterator();

      while(var5.hasNext()) {
         StructureComponent var6 = (StructureComponent)var5.next();
         var4.func_74742_a(var6.func_143010_b());
      }

      var3.func_74782_a("Children", var4);
      this.func_143022_a(var3);
      return var3;
   }

   public void func_143022_a(NBTTagCompound var1) {
   }

   public void func_143020_a(World var1, NBTTagCompound var2) {
      this.field_143024_c = var2.func_74762_e("ChunkX");
      this.field_143023_d = var2.func_74762_e("ChunkZ");
      if (var2.func_74764_b("BB")) {
         this.field_75074_b = new StructureBoundingBox(var2.func_74759_k("BB"));
      }

      NBTTagList var3 = var2.func_150295_c("Children", 10);

      for(int var4 = 0; var4 < var3.func_74745_c(); ++var4) {
         this.field_75075_a.add(MapGenStructureIO.func_143032_b(var3.func_150305_b(var4), var1));
      }

      this.func_143017_b(var2);
   }

   public void func_143017_b(NBTTagCompound var1) {
   }

   protected void func_75067_a(World var1, Random var2, int var3) {
      int var4 = var1.func_181545_F() - var3;
      int var5 = this.field_75074_b.func_78882_c() + 1;
      if (var5 < var4) {
         var5 += var2.nextInt(var4 - var5);
      }

      int var6 = var5 - this.field_75074_b.field_78894_e;
      this.field_75074_b.func_78886_a(0, var6, 0);
      Iterator var7 = this.field_75075_a.iterator();

      while(var7.hasNext()) {
         StructureComponent var8 = (StructureComponent)var7.next();
         var8.func_181138_a(0, var6, 0);
      }

   }

   protected void func_75070_a(World var1, Random var2, int var3, int var4) {
      int var5 = var4 - var3 + 1 - this.field_75074_b.func_78882_c();
      boolean var6 = true;
      int var10;
      if (var5 > 1) {
         var10 = var3 + var2.nextInt(var5);
      } else {
         var10 = var3;
      }

      int var7 = var10 - this.field_75074_b.field_78895_b;
      this.field_75074_b.func_78886_a(0, var7, 0);
      Iterator var8 = this.field_75075_a.iterator();

      while(var8.hasNext()) {
         StructureComponent var9 = (StructureComponent)var8.next();
         var9.func_181138_a(0, var7, 0);
      }

   }

   public boolean func_75069_d() {
      return true;
   }

   public boolean func_175788_a(ChunkCoordIntPair var1) {
      return true;
   }

   public void func_175787_b(ChunkCoordIntPair var1) {
   }

   public int func_143019_e() {
      return this.field_143024_c;
   }

   public int func_143018_f() {
      return this.field_143023_d;
   }
}
