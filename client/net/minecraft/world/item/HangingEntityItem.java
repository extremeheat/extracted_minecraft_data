package net.minecraft.world.item;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class HangingEntityItem extends Item {
   private static final Component TOOLTIP_RANDOM_VARIANT;
   private final EntityType<? extends HangingEntity> type;

   public HangingEntityItem(EntityType<? extends HangingEntity> var1, Item.Properties var2) {
      super(var2);
      this.type = var1;
   }

   public InteractionResult useOn(UseOnContext var1) {
      BlockPos var2 = var1.getClickedPos();
      Direction var3 = var1.getClickedFace();
      BlockPos var4 = var2.relative(var3);
      Player var5 = var1.getPlayer();
      ItemStack var6 = var1.getItemInHand();
      if (var5 != null && !this.mayPlace(var5, var3, var6, var4)) {
         return InteractionResult.FAIL;
      } else {
         Level var7 = var1.getLevel();
         Object var8;
         if (this.type == EntityType.PAINTING) {
            Optional var9 = Painting.create(var7, var4, var3);
            if (var9.isEmpty()) {
               return InteractionResult.CONSUME;
            }

            var8 = (HangingEntity)var9.get();
         } else if (this.type == EntityType.ITEM_FRAME) {
            var8 = new ItemFrame(var7, var4, var3);
         } else {
            if (this.type != EntityType.GLOW_ITEM_FRAME) {
               return InteractionResult.SUCCESS;
            }

            var8 = new GlowItemFrame(var7, var4, var3);
         }

         EntityType.createDefaultStackConfig(var7, var6, var5).accept(var8);
         if (((HangingEntity)var8).survives()) {
            if (!var7.isClientSide) {
               ((HangingEntity)var8).playPlacementSound();
               var7.gameEvent(var5, GameEvent.ENTITY_PLACE, ((HangingEntity)var8).position());
               var7.addFreshEntity((Entity)var8);
            }

            var6.shrink(1);
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.CONSUME;
         }
      }
   }

   protected boolean mayPlace(Player var1, Direction var2, ItemStack var3, BlockPos var4) {
      return !var2.getAxis().isVertical() && var1.mayUseItemAt(var4, var2, var3);
   }

   public void appendHoverText(ItemStack var1, Item.TooltipContext var2, List<Component> var3, TooltipFlag var4) {
      super.appendHoverText(var1, var2, var3, var4);
      if (this.type == EntityType.PAINTING) {
         Holder var5 = (Holder)var1.get(DataComponents.PAINTING_VARIANT);
         if (var5 != null) {
            Optional var10000 = ((PaintingVariant)var5.value()).title();
            Objects.requireNonNull(var3);
            var10000.ifPresent(var3::add);
            var10000 = ((PaintingVariant)var5.value()).author();
            Objects.requireNonNull(var3);
            var10000.ifPresent(var3::add);
            var3.add(Component.translatable("painting.dimensions", ((PaintingVariant)var5.value()).width(), ((PaintingVariant)var5.value()).height()));
         } else if (var4.isCreative()) {
            var3.add(TOOLTIP_RANDOM_VARIANT);
         }
      }

   }

   static {
      TOOLTIP_RANDOM_VARIANT = Component.translatable("painting.random").withStyle(ChatFormatting.GRAY);
   }
}
