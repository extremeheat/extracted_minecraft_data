package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.JukeboxBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

public record JukeboxPlayable(Holder<JukeboxSong> song) implements TooltipProvider {
   public static final Codec<JukeboxPlayable> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxPlayable> STREAM_CODEC;

   public JukeboxPlayable {
      super();
   }

   public void addToTooltip(final Item.TooltipContext context, final Consumer<Component> consumer, final TooltipFlag flag, final DataComponentGetter components) {
      consumer.accept(ComponentUtils.mergeStyles(((JukeboxSong)this.song.value()).description(), Style.EMPTY.withColor(ChatFormatting.GRAY)));
   }

   public static InteractionResult tryInsertIntoJukebox(final Level level, final BlockPos pos, final ItemStack toInsert, final Player player) {
      JukeboxPlayable jukeboxPlayable = (JukeboxPlayable)toInsert.get(DataComponents.JUKEBOX_PLAYABLE);
      if (jukeboxPlayable == null) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         BlockState state = level.getBlockState(pos);
         if (state.is(Blocks.JUKEBOX) && !(Boolean)state.getValue(JukeboxBlock.HAS_RECORD)) {
            if (!level.isClientSide()) {
               ItemStack inserted = toInsert.consumeAndReturn(1, player);
               BlockEntity var8 = level.getBlockEntity(pos);
               if (var8 instanceof JukeboxBlockEntity) {
                  JukeboxBlockEntity jukebox = (JukeboxBlockEntity)var8;
                  jukebox.setTheItem(inserted);
                  level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
               }

               player.awardStat(Stats.PLAY_RECORD);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         }
      }
   }

   static {
      CODEC = JukeboxSong.CODEC.xmap(JukeboxPlayable::new, JukeboxPlayable::song);
      STREAM_CODEC = StreamCodec.composite(JukeboxSong.STREAM_CODEC, JukeboxPlayable::song, JukeboxPlayable::new);
   }
}
