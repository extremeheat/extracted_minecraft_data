package net.minecraft.entity.item;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.world.World;

public class EntityPainting extends EntityHanging {
   public PaintingType field_70522_e;

   public EntityPainting(World var1) {
      super(EntityType.field_200782_V, var1);
   }

   public EntityPainting(World var1, BlockPos var2, EnumFacing var3) {
      super(EntityType.field_200782_V, var1, var2);
      ArrayList var4 = Lists.newArrayList();
      int var5 = 0;
      Iterator var6 = IRegistry.field_212620_i.iterator();

      PaintingType var7;
      while(var6.hasNext()) {
         var7 = (PaintingType)var6.next();
         this.field_70522_e = var7;
         this.func_174859_a(var3);
         if (this.func_70518_d()) {
            var4.add(var7);
            int var8 = var7.func_200834_b() * var7.func_200832_c();
            if (var8 > var5) {
               var5 = var8;
            }
         }
      }

      if (!var4.isEmpty()) {
         var6 = var4.iterator();

         while(var6.hasNext()) {
            var7 = (PaintingType)var6.next();
            if (var7.func_200834_b() * var7.func_200832_c() < var5) {
               var6.remove();
            }
         }

         this.field_70522_e = (PaintingType)var4.get(this.field_70146_Z.nextInt(var4.size()));
      }

      this.func_174859_a(var3);
   }

   public EntityPainting(World var1, BlockPos var2, EnumFacing var3, PaintingType var4) {
      this(var1, var2, var3);
      this.field_70522_e = var4;
      this.func_174859_a(var3);
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74778_a("Motive", IRegistry.field_212620_i.func_177774_c(this.field_70522_e).toString());
      super.func_70014_b(var1);
   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_70522_e = (PaintingType)IRegistry.field_212620_i.func_82594_a(ResourceLocation.func_208304_a(var1.func_74779_i("Motive")));
      super.func_70037_a(var1);
   }

   public int func_82329_d() {
      return this.field_70522_e.func_200834_b();
   }

   public int func_82330_g() {
      return this.field_70522_e.func_200832_c();
   }

   public void func_110128_b(@Nullable Entity var1) {
      if (this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         this.func_184185_a(SoundEvents.field_187691_dJ, 1.0F, 1.0F);
         if (var1 instanceof EntityPlayer) {
            EntityPlayer var2 = (EntityPlayer)var1;
            if (var2.field_71075_bZ.field_75098_d) {
               return;
            }
         }

         this.func_199703_a(Items.field_151159_an);
      }
   }

   public void func_184523_o() {
      this.func_184185_a(SoundEvents.field_187694_dK, 1.0F, 1.0F);
   }

   public void func_70012_b(double var1, double var3, double var5, float var7, float var8) {
      this.func_70107_b(var1, var3, var5);
   }

   public void func_180426_a(double var1, double var3, double var5, float var7, float var8, int var9, boolean var10) {
      BlockPos var11 = this.field_174861_a.func_177963_a(var1 - this.field_70165_t, var3 - this.field_70163_u, var5 - this.field_70161_v);
      this.func_70107_b((double)var11.func_177958_n(), (double)var11.func_177956_o(), (double)var11.func_177952_p());
   }
}
