package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.WeightedSpawnerEntity;
import net.minecraft.util.math.BlockPos;
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

      public void func_184993_a(WeightedSpawnerEntity var1) {
         super.func_184993_a(var1);
         if (this.func_98271_a() != null) {
            IBlockState var2 = this.func_98271_a().func_180495_p(this.func_177221_b());
            this.func_98271_a().func_184138_a(TileEntityMobSpawner.this.field_174879_c, var2, var2, 4);
         }

      }
   };

   public TileEntityMobSpawner() {
      super(TileEntityType.field_200979_j);
   }

   public void func_145839_a(NBTTagCompound var1) {
      super.func_145839_a(var1);
      this.field_145882_a.func_98270_a(var1);
   }

   public NBTTagCompound func_189515_b(NBTTagCompound var1) {
      super.func_189515_b(var1);
      this.field_145882_a.func_189530_b(var1);
      return var1;
   }

   public void func_73660_a() {
      this.field_145882_a.func_98278_g();
   }

   @Nullable
   public SPacketUpdateTileEntity func_189518_D_() {
      return new SPacketUpdateTileEntity(this.field_174879_c, 1, this.func_189517_E_());
   }

   public NBTTagCompound func_189517_E_() {
      NBTTagCompound var1 = this.func_189515_b(new NBTTagCompound());
      var1.func_82580_o("SpawnPotentials");
      return var1;
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
