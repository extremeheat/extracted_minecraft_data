package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityMobSpawner extends TileEntity {
   private final MobSpawnerBaseLogic field_145882_a = new TileEntityMobSpawner$1(this);

   public TileEntityMobSpawner() {
      super();
   }

   @Override
   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145882_a.func_98270_a(var1);
   }

   @Override
   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      this.field_145882_a.func_98280_b(var1);
   }

   @Override
   public void func_145845_h() {
      this.field_145882_a.func_98278_g();
      super.func_145845_h();
   }

   @Override
   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      var1.func_82580_o("SpawnPotentials");
      return new S35PacketUpdateTileEntity(this.field_145851_c, this.field_145848_d, this.field_145849_e, 1, var1);
   }

   @Override
   public boolean func_145842_c(int var1, int var2) {
      return this.field_145882_a.func_98268_b(var1) ? true : super.func_145842_c(var1, var2);
   }

   public MobSpawnerBaseLogic func_145881_a() {
      return this.field_145882_a;
   }
}
