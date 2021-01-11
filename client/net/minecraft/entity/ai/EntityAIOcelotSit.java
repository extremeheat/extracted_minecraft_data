package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityAIOcelotSit extends EntityAIMoveToBlock {
   private final EntityOcelot field_151493_a;

   public EntityAIOcelotSit(EntityOcelot var1, double var2) {
      super(var1, var2, 8);
      this.field_151493_a = var1;
   }

   public boolean func_75250_a() {
      return this.field_151493_a.func_70909_n() && !this.field_151493_a.func_70906_o() && super.func_75250_a();
   }

   public boolean func_75253_b() {
      return super.func_75253_b();
   }

   public void func_75249_e() {
      super.func_75249_e();
      this.field_151493_a.func_70907_r().func_75270_a(false);
   }

   public void func_75251_c() {
      super.func_75251_c();
      this.field_151493_a.func_70904_g(false);
   }

   public void func_75246_d() {
      super.func_75246_d();
      this.field_151493_a.func_70907_r().func_75270_a(false);
      if (!this.func_179487_f()) {
         this.field_151493_a.func_70904_g(false);
      } else if (!this.field_151493_a.func_70906_o()) {
         this.field_151493_a.func_70904_g(true);
      }

   }

   protected boolean func_179488_a(World var1, BlockPos var2) {
      if (!var1.func_175623_d(var2.func_177984_a())) {
         return false;
      } else {
         IBlockState var3 = var1.func_180495_p(var2);
         Block var4 = var3.func_177230_c();
         if (var4 == Blocks.field_150486_ae) {
            TileEntity var5 = var1.func_175625_s(var2);
            if (var5 instanceof TileEntityChest && ((TileEntityChest)var5).field_145987_o < 1) {
               return true;
            }
         } else {
            if (var4 == Blocks.field_150470_am) {
               return true;
            }

            if (var4 == Blocks.field_150324_C && var3.func_177229_b(BlockBed.field_176472_a) != BlockBed.EnumPartType.HEAD) {
               return true;
            }
         }

         return false;
      }
   }
}
