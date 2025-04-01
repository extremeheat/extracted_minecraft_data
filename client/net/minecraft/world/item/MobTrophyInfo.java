package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.TooltipProvider;

public record MobTrophyInfo(Holder<EntityType<?>> type, boolean shiny) implements TooltipProvider {
   public static final Codec<MobTrophyInfo> CODEC;
   public static final StreamCodec<RegistryFriendlyByteBuf, MobTrophyInfo> STREAM_CODEC;

   public MobTrophyInfo(Holder<EntityType<?>> var1, boolean var2) {
      super();
      this.type = var1;
      this.shiny = var2;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      var2.accept(Component.translatable("item.minecraft.mob_trophy.entity", ((EntityType)this.type.value()).getDescription()));
      if (this.shiny) {
         var2.accept(Component.translatable("item.minecraft.mob_trophy.shiny").withStyle(ChatFormatting.LIGHT_PURPLE));
      }

   }

   static {
      CODEC = Codec.withAlternative(RecordCodecBuilder.create((var0) -> var0.group(RegistryFixedCodec.create(Registries.ENTITY_TYPE).fieldOf("type").forGetter(MobTrophyInfo::type), Codec.BOOL.fieldOf("shiny").forGetter(MobTrophyInfo::shiny)).apply(var0, MobTrophyInfo::new)), RegistryFixedCodec.create(Registries.ENTITY_TYPE), (var0) -> new MobTrophyInfo(var0, false));
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE), MobTrophyInfo::type, ByteBufCodecs.BOOL, MobTrophyInfo::shiny, MobTrophyInfo::new);
   }
}
