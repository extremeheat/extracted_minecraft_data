package net.minecraft.world.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.blocks.BlockPredicateArgument;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;

public class AdventureModeCheck {
   private final String tagName;
   @Nullable
   private BlockInWorld lastCheckedBlock;
   private boolean lastResult;
   private boolean checksBlockEntity;

   public AdventureModeCheck(String var1) {
      super();
      this.tagName = var1;
   }

   private static boolean areSameBlocks(BlockInWorld var0, @Nullable BlockInWorld var1, boolean var2) {
      if (var1 != null && var0.getState() == var1.getState()) {
         if (!var2) {
            return true;
         } else if (var0.getEntity() == null && var1.getEntity() == null) {
            return true;
         } else {
            return var0.getEntity() != null && var1.getEntity() != null ? Objects.equals(var0.getEntity().saveWithId(), var1.getEntity().saveWithId()) : false;
         }
      } else {
         return false;
      }
   }

   public boolean test(ItemStack var1, Registry<Block> var2, BlockInWorld var3) {
      if (areSameBlocks(var3, this.lastCheckedBlock, this.checksBlockEntity)) {
         return this.lastResult;
      } else {
         this.lastCheckedBlock = var3;
         this.checksBlockEntity = false;
         CompoundTag var4 = var1.getTag();
         if (var4 != null && var4.contains(this.tagName, 9)) {
            ListTag var5 = var4.getList(this.tagName, 8);

            for(int var6 = 0; var6 < var5.size(); ++var6) {
               String var7 = var5.getString(var6);

               try {
                  BlockPredicateArgument.Result var8 = BlockPredicateArgument.parse(HolderLookup.forRegistry(var2), new StringReader(var7));
                  this.checksBlockEntity |= var8.requiresNbt();
                  if (var8.test(var3)) {
                     this.lastResult = true;
                     return true;
                  }
               } catch (CommandSyntaxException var9) {
               }
            }
         }

         this.lastResult = false;
         return false;
      }
   }
}
