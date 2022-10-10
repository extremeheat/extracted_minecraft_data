package net.minecraft.entity;

import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.passive.IAnimal;

public enum EnumCreatureType {
   MONSTER(IMob.class, 70, false, false),
   CREATURE(EntityAnimal.class, 10, true, true),
   AMBIENT(EntityAmbientCreature.class, 15, true, false),
   WATER_CREATURE(EntityWaterMob.class, 15, true, false);

   private final Class<? extends IAnimal> field_75605_d;
   private final int field_75606_e;
   private final boolean field_75604_g;
   private final boolean field_82707_i;

   private EnumCreatureType(Class<? extends IAnimal> var3, int var4, boolean var5, boolean var6) {
      this.field_75605_d = var3;
      this.field_75606_e = var4;
      this.field_75604_g = var5;
      this.field_82707_i = var6;
   }

   public Class<? extends IAnimal> func_75598_a() {
      return this.field_75605_d;
   }

   public int func_75601_b() {
      return this.field_75606_e;
   }

   public boolean func_75599_d() {
      return this.field_75604_g;
   }

   public boolean func_82705_e() {
      return this.field_82707_i;
   }
}
