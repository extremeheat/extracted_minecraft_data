package net.minecraft.world.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class DebugStickItem extends Item {
   public DebugStickItem(Item.Properties var1) {
      super(var1);
   }

   @Override
   public boolean isFoil(ItemStack var1) {
      return true;
   }

   @Override
   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      if (!var2.isClientSide) {
         this.handleInteraction(var4, var1, var2, var3, false, var4.getItemInHand(InteractionHand.MAIN_HAND));
      }

      return false;
   }

   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      Level var3 = var1.getLevel();
      if (!var3.isClientSide && var2 != null) {
         BlockPos var4 = var1.getClickedPos();
         if (!this.handleInteraction(var2, var3.getBlockState(var4), var3, var4, true, var1.getItemInHand())) {
            return InteractionResult.FAIL;
         }
      }

      return InteractionResult.sidedSuccess(var3.isClientSide);
   }

   private boolean handleInteraction(Player var1, BlockState var2, LevelAccessor var3, BlockPos var4, boolean var5, ItemStack var6) {
      if (!var1.canUseGameMasterBlocks()) {
         return false;
      } else {
         Block var7 = var2.getBlock();
         StateDefinition var8 = var7.getStateDefinition();
         Collection var9 = var8.getProperties();
         String var10 = Registry.BLOCK.getKey(var7).toString();
         if (var9.isEmpty()) {
            message(var1, Component.translatable(this.getDescriptionId() + ".empty", var10));
            return false;
         } else {
            CompoundTag var11 = var6.getOrCreateTagElement("DebugProperty");
            String var12 = var11.getString(var10);
            Property var13 = var8.getProperty(var12);
            if (var5) {
               if (var13 == null) {
                  var13 = (Property)var9.iterator().next();
               }

               BlockState var14 = cycleState(var2, var13, var1.isSecondaryUseActive());
               var3.setBlock(var4, var14, 18);
               message(var1, Component.translatable(this.getDescriptionId() + ".update", var13.getName(), getNameHelper(var14, var13)));
            } else {
               var13 = getRelative(var9, var13, var1.isSecondaryUseActive());
               String var16 = var13.getName();
               var11.putString(var10, var16);
               message(var1, Component.translatable(this.getDescriptionId() + ".select", var16, getNameHelper(var2, var13)));
            }

            return true;
         }
      }
   }

   private static <T extends Comparable<T>> BlockState cycleState(BlockState var0, Property<T> var1, boolean var2) {
      return var0.setValue(var1, getRelative(var1.getPossibleValues(), var0.getValue(var1), var2));
   }

   private static <T> T getRelative(Iterable<T> var0, @Nullable T var1, boolean var2) {
      return (T)(var2 ? Util.findPreviousInIterable(var0, (T)var1) : Util.findNextInIterable(var0, (T)var1));
   }

   private static void message(Player var0, Component var1) {
      ((ServerPlayer)var0).sendSystemMessage(var1, ChatType.GAME_INFO);
   }

   private static <T extends Comparable<T>> String getNameHelper(BlockState var0, Property<T> var1) {
      return var1.getName(var0.getValue(var1));
   }
}
