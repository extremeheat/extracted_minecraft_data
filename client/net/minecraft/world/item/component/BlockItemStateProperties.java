package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.BeehiveBlock;
import net.minecraft.world.level.block.MobTrophyBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public record BlockItemStateProperties(Map<String, String> properties) implements TooltipProvider {
   public static final BlockItemStateProperties EMPTY = new BlockItemStateProperties(Map.of());
   public static final Codec<BlockItemStateProperties> CODEC;
   private static final StreamCodec<ByteBuf, Map<String, String>> PROPERTIES_STREAM_CODEC;
   public static final StreamCodec<ByteBuf, BlockItemStateProperties> STREAM_CODEC;

   public BlockItemStateProperties(Map<String, String> var1) {
      super();
      this.properties = var1;
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> var1, T var2) {
      return new BlockItemStateProperties(Util.copyAndPut(this.properties, var1.getName(), var1.getName(var2)));
   }

   public <T extends Comparable<T>> BlockItemStateProperties with(Property<T> var1, BlockState var2) {
      return this.with(var1, var2.getValue(var1));
   }

   @Nullable
   public <T extends Comparable<T>> T get(Property<T> var1) {
      String var2 = (String)this.properties.get(var1.getName());
      return (T)(var2 == null ? null : (Comparable)var1.getValue(var2).orElse((Object)null));
   }

   public BlockState apply(BlockState var1) {
      StateDefinition var2 = var1.getBlock().getStateDefinition();

      for(Map.Entry var4 : this.properties.entrySet()) {
         Property var5 = var2.getProperty((String)var4.getKey());
         if (var5 != null) {
            var1 = updateState(var1, var5, (String)var4.getValue());
         }
      }

      return var1;
   }

   private static <T extends Comparable<T>> BlockState updateState(BlockState var0, Property<T> var1, String var2) {
      return (BlockState)var1.getValue(var2).map((var2x) -> (BlockState)var0.setValue(var1, var2x)).orElse(var0);
   }

   public boolean isEmpty() {
      return this.properties.isEmpty();
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      Integer var6 = (Integer)this.get(BeehiveBlock.HONEY_LEVEL);
      if (var6 != null) {
         var2.accept(Component.translatable("container.beehive.honey", var6, 5).withStyle(ChatFormatting.GRAY));
      }

      MobTrophyBlock.Grade var7 = (MobTrophyBlock.Grade)this.get(MobTrophyBlock.GRADE);
      if (var7 != null) {
         MutableComponent var8 = Component.translatable(var7.translationId()).withColor(var7.color());
         var2.accept(Component.translatable("item.minecraft.mob_trophy.grade", var8));
      }

   }

   static {
      CODEC = Codec.unboundedMap(Codec.STRING, Codec.STRING).xmap(BlockItemStateProperties::new, BlockItemStateProperties::properties);
      PROPERTIES_STREAM_CODEC = ByteBufCodecs.map(Object2ObjectOpenHashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8);
      STREAM_CODEC = PROPERTIES_STREAM_CODEC.map(BlockItemStateProperties::new, BlockItemStateProperties::properties);
   }
}
