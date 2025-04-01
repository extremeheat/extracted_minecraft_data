package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.commons.lang3.StringUtils;

public record RoomerinoComponentino(ResourceLocation id) implements TooltipProvider {
   public static final Codec<RoomerinoComponentino> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("structure").forGetter(RoomerinoComponentino::id)).apply(var0, RoomerinoComponentino::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, RoomerinoComponentino> STREAM_CODEC;
   public static final String STRUCTURE_PREFIX = "hub/room/";
   public static final String LANGUAGE_PREFIX = "room";

   public RoomerinoComponentino(ResourceLocation var1) {
      super();
      this.id = var1;
   }

   public ResourceLocation structureId() {
      return idToStructure(this.id);
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      String var6 = this.id.toLanguageKey("room");
      var2.accept(Component.translatable("item.minecraft.shimmering_key.room", Component.translatable(var6)).withStyle(ChatFormatting.LIGHT_PURPLE));
   }

   public static ResourceLocation structureToId(ResourceLocation var0) {
      return var0.withPath((UnaryOperator)((var0x) -> StringUtils.removeStart(var0x, "hub/room/")));
   }

   public static ResourceLocation idToStructure(ResourceLocation var0) {
      return var0.withPrefix("hub/room/");
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ResourceLocation.STREAM_CODEC, RoomerinoComponentino::id, RoomerinoComponentino::new);
   }
}
