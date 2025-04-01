package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
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

public record JukeboxPlayable(EitherHolder<JukeboxSong> song) implements TooltipProvider {
   public static final Codec<JukeboxPlayable> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, JukeboxPlayable> STREAM_CODEC;

   public JukeboxPlayable(EitherHolder<JukeboxSong> var1) {
      super();
      this.song = var1;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      HolderLookup.Provider var6 = var1.registries();
      if (var6 != null) {
         this.song.unwrap(var6).ifPresent((var1x) -> {
            MutableComponent var2x = ((JukeboxSong)var1x.value()).description().copy();
            ComponentUtils.mergeStyles(var2x, Style.EMPTY.withColor(ChatFormatting.GRAY));
            var2.accept(var2x);
         });
      }

   }

   public static InteractionResult tryInsertIntoJukebox(Level var0, BlockPos var1, ItemStack var2, Player var3) {
      JukeboxPlayable var4 = (JukeboxPlayable)var2.get(DataComponents.JUKEBOX_PLAYABLE);
      if (var4 == null) {
         return InteractionResult.TRY_WITH_EMPTY_HAND;
      } else {
         BlockState var5 = var0.getBlockState(var1);
         if (var5.is(Blocks.JUKEBOX) && !(Boolean)var5.getValue(JukeboxBlock.HAS_RECORD)) {
            if (!var0.isClientSide) {
               ItemStack var6 = var2.consumeAndReturn(1, var3);
               BlockEntity var8 = var0.getBlockEntity(var1);
               if (var8 instanceof JukeboxBlockEntity) {
                  JukeboxBlockEntity var7 = (JukeboxBlockEntity)var8;
                  var7.setTheItem(var6);
                  var0.gameEvent(GameEvent.BLOCK_CHANGE, var1, GameEvent.Context.of(var3, var5));
               }

               var3.awardStat(Stats.PLAY_RECORD);
            }

            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
         }
      }
   }

   static {
      CODEC = EitherHolder.codec(Registries.JUKEBOX_SONG, JukeboxSong.CODEC).xmap(JukeboxPlayable::new, JukeboxPlayable::song);
      STREAM_CODEC = StreamCodec.composite(EitherHolder.streamCodec(Registries.JUKEBOX_SONG, JukeboxSong.STREAM_CODEC), JukeboxPlayable::song, JukeboxPlayable::new);
   }
}
