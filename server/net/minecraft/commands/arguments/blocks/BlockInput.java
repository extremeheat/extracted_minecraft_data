package net.minecraft.commands.arguments.blocks;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.properties.Property;

public class BlockInput implements Predicate<BlockInWorld> {
   private final BlockState state;
   private final Set<Property<?>> properties;
   @Nullable
   private final CompoundTag tag;

   public BlockInput(BlockState var1, Set<Property<?>> var2, @Nullable CompoundTag var3) {
      super();
      this.state = var1;
      this.properties = var2;
      this.tag = var3;
   }

   public BlockState getState() {
      return this.state;
   }

   public boolean test(BlockInWorld var1) {
      BlockState var2 = var1.getState();
      if (!var2.is(this.state.getBlock())) {
         return false;
      } else {
         Iterator var3 = this.properties.iterator();

         while(var3.hasNext()) {
            Property var4 = (Property)var3.next();
            if (var2.getValue(var4) != this.state.getValue(var4)) {
               return false;
            }
         }

         if (this.tag == null) {
            return true;
         } else {
            BlockEntity var5 = var1.getEntity();
            return var5 != null && NbtUtils.compareNbt(this.tag, var5.save(new CompoundTag()), true);
         }
      }
   }

   public boolean place(ServerLevel var1, BlockPos var2, int var3) {
      BlockState var4 = Block.updateFromNeighbourShapes(this.state, var1, var2);
      if (var4.isAir()) {
         var4 = this.state;
      }

      if (!var1.setBlock(var2, var4, var3)) {
         return false;
      } else {
         if (this.tag != null) {
            BlockEntity var5 = var1.getBlockEntity(var2);
            if (var5 != null) {
               CompoundTag var6 = this.tag.copy();
               var6.putInt("x", var2.getX());
               var6.putInt("y", var2.getY());
               var6.putInt("z", var2.getZ());
               var5.load(var4, var6);
            }
         }

         return true;
      }
   }

   // $FF: synthetic method
   public boolean test(Object var1) {
      return this.test((BlockInWorld)var1);
   }
}
