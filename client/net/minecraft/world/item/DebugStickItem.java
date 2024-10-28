package net.minecraft.world.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.DebugStickState;
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

   public boolean canAttackBlock(BlockState var1, Level var2, BlockPos var3, Player var4) {
      if (!var2.isClientSide) {
         this.handleInteraction(var4, var1, var2, var3, false, var4.getItemInHand(InteractionHand.MAIN_HAND));
      }

      return false;
   }

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
         Holder var7 = var2.getBlockHolder();
         StateDefinition var8 = ((Block)var7.value()).getStateDefinition();
         Collection var9 = var8.getProperties();
         if (var9.isEmpty()) {
            message(var1, Component.translatable(this.getDescriptionId() + ".empty", var7.getRegisteredName()));
            return false;
         } else {
            DebugStickState var10 = (DebugStickState)var6.get(DataComponents.DEBUG_STICK_STATE);
            if (var10 == null) {
               return false;
            } else {
               Property var11 = (Property)var10.properties().get(var7);
               if (var5) {
                  if (var11 == null) {
                     var11 = (Property)var9.iterator().next();
                  }

                  BlockState var12 = cycleState(var2, var11, var1.isSecondaryUseActive());
                  var3.setBlock(var4, var12, 18);
                  message(var1, Component.translatable(this.getDescriptionId() + ".update", var11.getName(), getNameHelper(var12, var11)));
               } else {
                  var11 = (Property)getRelative(var9, var11, var1.isSecondaryUseActive());
                  var6.set(DataComponents.DEBUG_STICK_STATE, var10.withProperty(var7, var11));
                  message(var1, Component.translatable(this.getDescriptionId() + ".select", var11.getName(), getNameHelper(var2, var11)));
               }

               return true;
            }
         }
      }
   }

   private static <T extends Comparable<T>> BlockState cycleState(BlockState var0, Property<T> var1, boolean var2) {
      return (BlockState)var0.setValue(var1, (Comparable)getRelative(var1.getPossibleValues(), var0.getValue(var1), var2));
   }

   private static <T> T getRelative(Iterable<T> var0, @Nullable T var1, boolean var2) {
      return var2 ? Util.findPreviousInIterable(var0, var1) : Util.findNextInIterable(var0, var1);
   }

   private static void message(Player var0, Component var1) {
      ((ServerPlayer)var0).sendSystemMessage(var1, true);
   }

   private static <T extends Comparable<T>> String getNameHelper(BlockState var0, Property<T> var1) {
      return var1.getName(var0.getValue(var1));
   }
}
