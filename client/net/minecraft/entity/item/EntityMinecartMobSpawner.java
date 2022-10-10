package net.minecraft.entity.item;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
   private final MobSpawnerBaseLogic field_98040_a = new MobSpawnerBaseLogic() {
      public void func_98267_a(int var1) {
         EntityMinecartMobSpawner.this.field_70170_p.func_72960_a(EntityMinecartMobSpawner.this, (byte)var1);
      }

      public World func_98271_a() {
         return EntityMinecartMobSpawner.this.field_70170_p;
      }

      public BlockPos func_177221_b() {
         return new BlockPos(EntityMinecartMobSpawner.this);
      }
   };

   public EntityMinecartMobSpawner(World var1) {
      super(EntityType.field_200777_Q, var1);
   }

   public EntityMinecartMobSpawner(World var1, double var2, double var4, double var6) {
      super(EntityType.field_200777_Q, var1, var2, var4, var6);
   }

   public EntityMinecart.Type func_184264_v() {
      return EntityMinecart.Type.SPAWNER;
   }

   public IBlockState func_180457_u() {
      return Blocks.field_150474_ac.func_176223_P();
   }

   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_98040_a.func_98270_a(var1);
   }

   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      this.field_98040_a.func_189530_b(var1);
   }

   public void func_70103_a(byte var1) {
      this.field_98040_a.func_98268_b(var1);
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      this.field_98040_a.func_98278_g();
   }

   public boolean func_184213_bq() {
      return true;
   }
}
