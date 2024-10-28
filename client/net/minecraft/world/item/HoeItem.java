package net.minecraft.world.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public class HoeItem extends DiggerItem {
   protected static final Map<Block, Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>> TILLABLES;

   public HoeItem(Tier var1, Item.Properties var2) {
      super(var1, BlockTags.MINEABLE_WITH_HOE, var2);
   }

   public InteractionResult useOn(UseOnContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      Pair var4 = (Pair)TILLABLES.get(var2.getBlockState(var3).getBlock());
      if (var4 == null) {
         return InteractionResult.PASS;
      } else {
         Predicate var5 = (Predicate)var4.getFirst();
         Consumer var6 = (Consumer)var4.getSecond();
         if (var5.test(var1)) {
            Player var7 = var1.getPlayer();
            var2.playSound(var7, var3, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!var2.isClientSide) {
               var6.accept(var1);
               if (var7 != null) {
                  var1.getItemInHand().hurtAndBreak(1, var7, LivingEntity.getSlotForHand(var1.getHand()));
               }
            }

            return InteractionResult.sidedSuccess(var2.isClientSide);
         } else {
            return InteractionResult.PASS;
         }
      }
   }

   public static Consumer<UseOnContext> changeIntoState(BlockState var0) {
      return (var1) -> {
         var1.getLevel().setBlock(var1.getClickedPos(), var0, 11);
         var1.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, var1.getClickedPos(), GameEvent.Context.of(var1.getPlayer(), var0));
      };
   }

   public static Consumer<UseOnContext> changeIntoStateAndDropItem(BlockState var0, ItemLike var1) {
      return (var2) -> {
         var2.getLevel().setBlock(var2.getClickedPos(), var0, 11);
         var2.getLevel().gameEvent(GameEvent.BLOCK_CHANGE, var2.getClickedPos(), GameEvent.Context.of(var2.getPlayer(), var0));
         Block.popResourceFromFace(var2.getLevel(), var2.getClickedPos(), var2.getClickedFace(), new ItemStack(var1));
      };
   }

   public static boolean onlyIfAirAbove(UseOnContext var0) {
      return var0.getClickedFace() != Direction.DOWN && var0.getLevel().getBlockState(var0.getClickedPos().above()).isAir();
   }

   static {
      TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT_PATH, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.FARMLAND.defaultBlockState())), Blocks.COARSE_DIRT, Pair.of(HoeItem::onlyIfAirAbove, changeIntoState(Blocks.DIRT.defaultBlockState())), Blocks.ROOTED_DIRT, Pair.of((var0) -> {
         return true;
      }, changeIntoStateAndDropItem(Blocks.DIRT.defaultBlockState(), Items.HANGING_ROOTS))));
   }
}
