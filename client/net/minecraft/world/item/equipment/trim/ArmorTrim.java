package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public record ArmorTrim(Holder<TrimMaterial> material, Holder<TrimPattern> pattern, boolean showInTooltip) implements TooltipProvider {
   public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern), Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter((var0x) -> {
         return var0x.showInTooltip;
      })).apply(var0, ArmorTrim::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTrim> STREAM_CODEC;
   private static final Component UPGRADE_TITLE;

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2) {
      this(var1, var2, true);
   }

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2, boolean var3) {
      super();
      this.material = var1;
      this.pattern = var2;
      this.showInTooltip = var3;
   }

   public boolean hasPatternAndMaterial(Holder<TrimPattern> var1, Holder<TrimMaterial> var2) {
      return var1.equals(this.pattern) && var2.equals(this.material);
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (this.showInTooltip) {
         var2.accept(UPGRADE_TITLE);
         var2.accept(CommonComponents.space().append(((TrimPattern)this.pattern.value()).copyWithStyle(this.material)));
         var2.accept(CommonComponents.space().append(((TrimMaterial)this.material.value()).description()));
      }
   }

   public ArmorTrim withTooltip(boolean var1) {
      return new ArmorTrim(this.material, this.pattern, var1);
   }

   public Holder<TrimMaterial> material() {
      return this.material;
   }

   public Holder<TrimPattern> pattern() {
      return this.pattern;
   }

   public boolean showInTooltip() {
      return this.showInTooltip;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(TrimMaterial.STREAM_CODEC, ArmorTrim::material, TrimPattern.STREAM_CODEC, ArmorTrim::pattern, ByteBufCodecs.BOOL, (var0) -> {
         return var0.showInTooltip;
      }, ArmorTrim::new);
      UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
   }
}
