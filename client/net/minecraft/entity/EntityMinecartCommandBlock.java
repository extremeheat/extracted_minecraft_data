package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent$Serializer;
import net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {
   private final CommandBlockLogic field_145824_a = new EntityMinecartCommandBlock$1(this);
   private int field_145823_b = 0;

   public EntityMinecartCommandBlock(World var1) {
      super(var1);
   }

   public EntityMinecartCommandBlock(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.func_70096_w().func_75682_a(23, "");
      this.func_70096_w().func_75682_a(24, "");
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_145824_a.func_145759_b(var1);
      this.func_70096_w().func_75692_b(23, this.func_145822_e().func_145753_i());
      this.func_70096_w().func_75692_b(24, IChatComponent$Serializer.func_150696_a(this.func_145822_e().func_145749_h()));
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      this.field_145824_a.func_145758_a(var1);
   }

   @Override
   public int func_94087_l() {
      return 6;
   }

   @Override
   public Block func_145817_o() {
      return Blocks.field_150483_bI;
   }

   public CommandBlockLogic func_145822_e() {
      return this.field_145824_a;
   }

   @Override
   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.field_70173_aa - this.field_145823_b >= 4) {
         this.func_145822_e().func_145755_a(this.field_70170_p);
         this.field_145823_b = this.field_70173_aa;
      }
   }

   @Override
   public boolean func_130002_c(EntityPlayer var1) {
      if (this.field_70170_p.field_72995_K) {
         var1.func_146095_a(this.func_145822_e());
      }

      return super.func_130002_c(var1);
   }

   @Override
   public void func_145781_i(int var1) {
      super.func_145781_i(var1);
      if (var1 == 24) {
         try {
            this.field_145824_a.func_145750_b(IChatComponent$Serializer.func_150699_a(this.func_70096_w().func_75681_e(24)));
         } catch (Throwable var3) {
         }
      } else if (var1 == 23) {
         this.field_145824_a.func_145752_a(this.func_70096_w().func_75681_e(23));
      }
   }
}
