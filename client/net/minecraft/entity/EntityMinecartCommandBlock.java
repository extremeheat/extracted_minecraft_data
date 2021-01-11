package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {
   private final CommandBlockLogic field_145824_a = new CommandBlockLogic() {
      public void func_145756_e() {
         EntityMinecartCommandBlock.this.func_70096_w().func_75692_b(23, this.func_145753_i());
         EntityMinecartCommandBlock.this.func_70096_w().func_75692_b(24, IChatComponent.Serializer.func_150696_a(this.func_145749_h()));
      }

      public int func_145751_f() {
         return 1;
      }

      public void func_145757_a(ByteBuf var1) {
         var1.writeInt(EntityMinecartCommandBlock.this.func_145782_y());
      }

      public BlockPos func_180425_c() {
         return new BlockPos(EntityMinecartCommandBlock.this.field_70165_t, EntityMinecartCommandBlock.this.field_70163_u + 0.5D, EntityMinecartCommandBlock.this.field_70161_v);
      }

      public Vec3 func_174791_d() {
         return new Vec3(EntityMinecartCommandBlock.this.field_70165_t, EntityMinecartCommandBlock.this.field_70163_u, EntityMinecartCommandBlock.this.field_70161_v);
      }

      public World func_130014_f_() {
         return EntityMinecartCommandBlock.this.field_70170_p;
      }

      public Entity func_174793_f() {
         return EntityMinecartCommandBlock.this;
      }
   };
   private int field_145823_b = 0;

   public EntityMinecartCommandBlock(World var1) {
      super(var1);
   }

   public EntityMinecartCommandBlock(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   protected void func_70088_a() {
      super.func_70088_a();
      this.func_70096_w().func_75682_a(23, "");
      this.func_70096_w().func_75682_a(24, "");
   }

   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_145824_a.func_145759_b(var1);
      this.func_70096_w().func_75692_b(23, this.func_145822_e().func_145753_i());
      this.func_70096_w().func_75692_b(24, IChatComponent.Serializer.func_150696_a(this.func_145822_e().func_145749_h()));
   }

   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      this.field_145824_a.func_145758_a(var1);
   }

   public EntityMinecart.EnumMinecartType func_180456_s() {
      return EntityMinecart.EnumMinecartType.COMMAND_BLOCK;
   }

   public IBlockState func_180457_u() {
      return Blocks.field_150483_bI.func_176223_P();
   }

   public CommandBlockLogic func_145822_e() {
      return this.field_145824_a;
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.field_70173_aa - this.field_145823_b >= 4) {
         this.func_145822_e().func_145755_a(this.field_70170_p);
         this.field_145823_b = this.field_70173_aa;
      }

   }

   public boolean func_130002_c(EntityPlayer var1) {
      this.field_145824_a.func_175574_a(var1);
      return false;
   }

   public void func_145781_i(int var1) {
      super.func_145781_i(var1);
      if (var1 == 24) {
         try {
            this.field_145824_a.func_145750_b(IChatComponent.Serializer.func_150699_a(this.func_70096_w().func_75681_e(24)));
         } catch (Throwable var3) {
         }
      } else if (var1 == 23) {
         this.field_145824_a.func_145752_a(this.func_70096_w().func_75681_e(23));
      }

   }
}
