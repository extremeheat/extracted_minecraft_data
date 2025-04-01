package net.minecraft.world.level.block;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public enum TrophyType implements StringRepresentable, TooltipProvider {
   GOLD("gold"),
   MEGA_SPUD("mega_spud"),
   NO_MEDAL("no_medal");

   public static final Codec<TrophyType> CODEC = StringRepresentable.<TrophyType>fromEnum(TrophyType::values);
   public static final StreamCodec<ByteBuf, TrophyType> STREAM_CODEC = ByteBufCodecs.idMapper((var0) -> values()[var0], Enum::ordinal);
   private final String name;

   private TrophyType(final String var3) {
      this.name = var3;
   }

   public String getSerializedName() {
      return this.name;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      var2.accept(Component.translatable("trophy." + this.name).withStyle(ChatFormatting.GRAY));
   }

   // $FF: synthetic method
   private static TrophyType[] $values() {
      return new TrophyType[]{GOLD, MEGA_SPUD, NO_MEDAL};
   }
}
