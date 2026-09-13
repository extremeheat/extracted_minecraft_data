package net.minecraft.entity.item;

import java.util.ArrayList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging {
   public EntityPainting$EnumArt field_70522_e;

   public EntityPainting(World var1) {
      super(var1);
   }

   public EntityPainting(World var1, int var2, int var3, int var4, int var5) {
      super(var1, var2, var3, var4, var5);
      ArrayList var6 = new ArrayList();

      for(EntityPainting$EnumArt var10 : EntityPainting$EnumArt.values()) {
         this.field_70522_e = var10;
         this.func_82328_a(var5);
         if (this.func_70518_d()) {
            var6.add(var10);
         }
      }

      if (!var6.isEmpty()) {
         this.field_70522_e = (EntityPainting$EnumArt)var6.get(this.field_70146_Z.nextInt(var6.size()));
      }

      this.func_82328_a(var5);
   }

   public EntityPainting(World var1, int var2, int var3, int var4, int var5, String var6) {
      this(var1, var2, var3, var4, var5);

      for(EntityPainting$EnumArt var10 : EntityPainting$EnumArt.values()) {
         if (var10.field_75702_A.equals(var6)) {
            this.field_70522_e = var10;
            break;
         }
      }

      this.func_82328_a(var5);
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74778_a("Motive", this.field_70522_e.field_75702_A);
      super.func_70014_b(var1);
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
      String var2 = var1.func_74779_i("Motive");

      for(EntityPainting$EnumArt var6 : EntityPainting$EnumArt.values()) {
         if (var6.field_75702_A.equals(var2)) {
            this.field_70522_e = var6;
         }
      }

      if (this.field_70522_e == null) {
         this.field_70522_e = EntityPainting$EnumArt.Kebab;
      }

      super.func_70037_a(var1);
   }

   @Override
   public int func_82329_d() {
      return this.field_70522_e.field_75703_B;
   }

   @Override
   public int func_82330_g() {
      return this.field_70522_e.field_75704_C;
   }

   @Override
   public void func_110128_b(Entity var1) {
      if (var1 instanceof EntityPlayer) {
         EntityPlayer var2 = (EntityPlayer)var1;
         if (var2.field_71075_bZ.field_75098_d) {
            return;
         }
      }

      this.func_70099_a(new ItemStack(Items.field_151159_an), 0.0F);
   }
}
