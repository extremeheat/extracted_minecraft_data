package net.minecraft.command.arguments;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.state.IProperty;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class BlockStateInput implements Predicate<BlockWorldState> {
   private final IBlockState field_197234_a;
   private final Set<IProperty<?>> field_197235_b;
   @Nullable
   private final NBTTagCompound field_197236_c;

   public BlockStateInput(IBlockState var1, Set<IProperty<?>> var2, @Nullable NBTTagCompound var3) {
      super();
      this.field_197234_a = var1;
      this.field_197235_b = var2;
      this.field_197236_c = var3;
   }

   public IBlockState func_197231_a() {
      return this.field_197234_a;
   }

   public boolean test(BlockWorldState var1) {
      IBlockState var2 = var1.func_177509_a();
      if (var2.func_177230_c() != this.field_197234_a.func_177230_c()) {
         return false;
      } else {
         Iterator var3 = this.field_197235_b.iterator();

         while(var3.hasNext()) {
            IProperty var4 = (IProperty)var3.next();
            if (var2.func_177229_b(var4) != this.field_197234_a.func_177229_b(var4)) {
               return false;
            }
         }

         if (this.field_197236_c == null) {
            return true;
         } else {
            TileEntity var5 = var1.func_177507_b();
            return var5 != null && NBTUtil.func_181123_a(this.field_197236_c, var5.func_189515_b(new NBTTagCompound()), true);
         }
      }
   }

   public boolean func_197230_a(WorldServer var1, BlockPos var2, int var3) {
      if (!var1.func_180501_a(var2, this.field_197234_a, var3)) {
         return false;
      } else {
         if (this.field_197236_c != null) {
            TileEntity var4 = var1.func_175625_s(var2);
            if (var4 != null) {
               NBTTagCompound var5 = this.field_197236_c.func_74737_b();
               var5.func_74768_a("x", var2.func_177958_n());
               var5.func_74768_a("y", var2.func_177956_o());
               var5.func_74768_a("z", var2.func_177952_p());
               var4.func_145839_a(var5);
            }
         }

         return true;
      }
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((BlockWorldState)var1);
   }
}
