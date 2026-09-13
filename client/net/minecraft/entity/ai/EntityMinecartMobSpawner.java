package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
   private final MobSpawnerBaseLogic field_98040_a = new EntityMinecartMobSpawner$1(this);

   public EntityMinecartMobSpawner(World var1) {
      super(var1);
   }

   public EntityMinecartMobSpawner(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   public int func_94087_l() {
      return 4;
   }

   @Override
   public Block func_145817_o() {
      return Blocks.field_150474_ac;
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_98040_a.func_98270_a(var1);
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      this.field_98040_a.func_98280_b(var1);
   }

   @Override
   public void func_70103_a(byte var1) {
      this.field_98040_a.func_98268_b(var1);
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      this.field_98040_a.func_98278_g();
   }

   public MobSpawnerBaseLogic func_98039_d() {
      return this.field_98040_a;
   }
}
