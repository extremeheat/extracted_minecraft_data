package net.minecraft.world.level.block;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadBlock extends SkullBlock {
   protected PlayerHeadBlock(BlockBehaviour.Properties var1) {
      super(SkullBlock.Types.PLAYER, var1);
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      super.setPlacedBy(var1, var2, var3, var4, var5);
      BlockEntity var6 = var1.getBlockEntity(var2);
      if (var6 instanceof SkullBlockEntity) {
         SkullBlockEntity var7 = (SkullBlockEntity)var6;
         GameProfile var8 = null;
         if (var5.hasTag()) {
            CompoundTag var9 = var5.getTag();
            if (var9.contains("SkullOwner", 10)) {
               var8 = NbtUtils.readGameProfile(var9.getCompound("SkullOwner"));
            } else if (var9.contains("SkullOwner", 8) && !StringUtils.isBlank(var9.getString("SkullOwner"))) {
               var8 = new GameProfile((UUID)null, var9.getString("SkullOwner"));
            }
         }

         var7.setOwner(var8);
      }

   }
}
