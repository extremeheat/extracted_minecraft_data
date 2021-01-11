package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
   private final MobSpawnerBaseLogic field_145882_a = new MobSpawnerBaseLogic() {
      public void func_98267_a(int var1) {
         TileEntityMobSpawner.this.field_145850_b.func_175641_c(TileEntityMobSpawner.this.field_174879_c, Blocks.field_150474_ac, var1, 0);
      }

      public World func_98271_a() {
         return TileEntityMobSpawner.this.field_145850_b;
      }

      public BlockPos func_177221_b() {
         return TileEntityMobSpawner.this.field_174879_c;
      }

      public void func_98277_a(MobSpawnerBaseLogic.WeightedRandomMinecart var1) {
         super.func_98277_a(var1);
         if (this.func_98271_a() != null) {
            this.func_98271_a().func_175689_h(TileEntityMobSpawner.this.field_174879_c);
         }

      }
   };

   public TileEntityMobSpawner() {
      super();
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145882_a.func_98270_a(var1);
   }

   public void func_145841_b(NBTTagCompound var1) {
      super.func_145841_b(var1);
      this.field_145882_a.func_98280_b(var1);
   }

   public void func_73660_a() {
      this.field_145882_a.func_98278_g();
   }

   public Packet func_145844_m() {
      NBTTagCompound var1 = new NBTTagCompound();
      this.func_145841_b(var1);
      var1.func_82580_o("SpawnPotentials");
      return new S35PacketUpdateTileEntity(this.field_174879_c, 1, var1);
   }

   public boolean func_145842_c(int var1, int var2) {
      return this.field_145882_a.func_98268_b(var1) ? true : super.func_145842_c(var1, var2);
   }

   public boolean func_183000_F() {
      return true;
   }

   public MobSpawnerBaseLogic func_145881_a() {
      return this.field_145882_a;
   }
}
