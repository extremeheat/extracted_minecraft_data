package net.minecraft.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;

public class BlockSkullPlayer extends BlockSkull {
   protected BlockSkullPlayer(Block.Properties var1) {
      super(BlockSkull.Types.PLAYER, var1);
   }

   public void func_180633_a(World var1, BlockPos var2, IBlockState var3, @Nullable EntityLivingBase var4, ItemStack var5) {
      super.func_180633_a(var1, var2, var3, var4, var5);
      TileEntity var6 = var1.func_175625_s(var2);
      if (var6 instanceof TileEntitySkull) {
         TileEntitySkull var7 = (TileEntitySkull)var6;
         GameProfile var8 = null;
         if (var5.func_77942_o()) {
            NBTTagCompound var9 = var5.func_77978_p();
            if (var9.func_150297_b("SkullOwner", 10)) {
               var8 = NBTUtil.func_152459_a(var9.func_74775_l("SkullOwner"));
            } else if (var9.func_150297_b("SkullOwner", 8) && !StringUtils.isBlank(var9.func_74779_i("SkullOwner"))) {
               var8 = new GameProfile((UUID)null, var9.func_74779_i("SkullOwner"));
            }
         }

         var7.func_195485_a(var8);
      }

   }
}
