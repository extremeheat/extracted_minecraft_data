package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.mines.WorldEffect;

public record WorldModifiers(List<WorldEffect> effects, boolean includeDescription) implements TooltipProvider {
   public static final Codec<WorldModifiers> CODEC = RecordCodecBuilder.create((var0) -> var0.group(WorldEffect.CODEC.listOf().fieldOf("effects").forGetter(WorldModifiers::effects), Codec.BOOL.fieldOf("include_description").forGetter(WorldModifiers::includeDescription)).apply(var0, WorldModifiers::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, WorldModifiers> STREAM_CODEC;
   public static final WorldModifiers EMPTY;
   private static final Component LOCKED_DESCRIPTION;
   private static final Component WON;
   private static final Component LOST;
   private static final Style HINT_STYLE;

   public WorldModifiers(List<WorldEffect> var1, boolean var2) {
      super();
      this.effects = var1;
      this.includeDescription = var2;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      Boolean var6 = (Boolean)var5.get(DataComponents.MINE_COMPLETED);
      if (var6 != null) {
         var2.accept(Component.literal("Status: ").append(var6 ? WON : LOST).withStyle(ChatFormatting.GRAY));
         var2.accept(CommonComponents.EMPTY);
      }

      boolean var7 = var5.get(DataComponents.WORLD_EFFECT_HINT) != null;
      boolean var8 = var5.get(DataComponents.WORLD_EFFECT_UNLOCK) != null;
      if (this.includeDescription) {
         for(WorldEffect var10 : this.effects) {
            var2.accept(wrapWithModifier(var10, this.describeEffect(var4, var10, var7, var8)));
         }
      } else {
         var2.accept(Component.literal("Effects:"));

         for(WorldEffect var13 : this.effects) {
            Component var11 = wrapWithModifier(var13, var13.name());
            var2.accept(Component.literal("  ").append(var11));
         }
      }

   }

   private Component describeEffect(@Nullable Player var1, WorldEffect var2, boolean var3, boolean var4) {
      boolean var5 = var1 != null && var1.level().isEffectUnlocked(var2);
      if (!var5 && var4) {
         return LOCKED_DESCRIPTION;
      } else if (var3) {
         MutableComponent var6 = ComponentUtils.mergeStyles(var2.unlockHint().copy(), HINT_STYLE);
         return Component.translatable("unlocks.screen.hint", var6);
      } else {
         return var2.description();
      }
   }

   private static Component wrapWithModifier(WorldEffect var0, Component var1) {
      float var2 = var0.experienceModifier();
      return (Component)(var2 != 1.0F ? Component.translatable("world.effect.experience_modifier", var1, var2) : var1);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.registry(Registries.WORLD_EFFECT).apply(ByteBufCodecs.list()), WorldModifiers::effects, ByteBufCodecs.BOOL, WorldModifiers::includeDescription, WorldModifiers::new);
      EMPTY = new WorldModifiers(List.of(), false);
      LOCKED_DESCRIPTION = Component.literal("???");
      WON = Component.literal("Won").withStyle(ChatFormatting.GREEN);
      LOST = Component.literal("Lost").withStyle(ChatFormatting.RED);
      HINT_STYLE = Style.EMPTY.withColor(ChatFormatting.GRAY);
   }
}
