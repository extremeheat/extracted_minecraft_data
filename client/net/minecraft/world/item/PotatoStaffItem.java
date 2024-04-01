package net.minecraft.world.item;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;

public class PotatoStaffItem extends Item {
   public PotatoStaffItem(Item.Properties var1) {
      super(var1);
   }

   // $VF: Could not properly define all variable types!
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public InteractionResult useOn(UseOnContext var1) {
      Player var2 = var1.getPlayer();
      if (var2 instanceof ServerPlayer var3) {
         if (var3.gameMode.isSurvival()) {
            ServerAdvancementManager var4 = var3.getServer().getAdvancements();
            ResourceLocation var5 = new ResourceLocation("potato/enter_the_potato");
            AdvancementHolder var6 = var4.get(var5);
            if (var6 != null) {
               AdvancementProgress var7 = var3.getAdvancements().getOrStartProgress(var6);
               if (!var7.isDone()) {
                  Level var8 = var2.level();
                  var3.sendSystemMessage(Component.translatable("item.minecraft.potato_staff.unworthy", var2.getDisplayName()));
                  var8.explode(null, var8.damageSources().generic(), null, var2.position(), 5.0F, true, Level.ExplosionInteraction.TNT);
                  return InteractionResult.FAIL;
               }
            }
         }

         return this.place(new BlockPlaceContext(var1));
      } else {
         return InteractionResult.FAIL;
      }
   }

   public InteractionResult place(BlockPlaceContext var1) {
      if (!var1.canPlace()) {
         return InteractionResult.FAIL;
      } else {
         BlockState var2 = Blocks.POTATO_PORTAL.defaultBlockState();
         Player var3 = var1.getPlayer();
         CollisionContext var4 = var3 == null ? CollisionContext.empty() : CollisionContext.of(var3);
         if (!var2.canSurvive(var1.getLevel(), var1.getClickedPos())) {
            return InteractionResult.FAIL;
         } else if (!var1.getLevel().isUnobstructed(var2, var1.getClickedPos(), var4)) {
            return InteractionResult.FAIL;
         } else if (!var1.getLevel().setBlock(var1.getClickedPos(), var2, 11)) {
            return InteractionResult.FAIL;
         } else {
            BlockPos var5 = var1.getClickedPos();
            Level var6 = var1.getLevel();
            Player var7 = var1.getPlayer();
            var6.playSound(null, var5, SoundEvents.MEGASPUD_SUMMON, SoundSource.BLOCKS, 1.0F, 1.0F);
            var6.gameEvent(GameEvent.BLOCK_PLACE, var5, GameEvent.Context.of(var7, var6.getBlockState(var5)));
            return InteractionResult.sidedSuccess(var6.isClientSide);
         }
      }
   }
}
