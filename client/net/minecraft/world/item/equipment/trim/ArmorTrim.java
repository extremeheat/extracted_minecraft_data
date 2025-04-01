package net.minecraft.world.item.equipment.trim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.equipment.EquipmentAsset;

public record ArmorTrim(Holder<TrimMaterial> material, Holder<TrimPattern> pattern) implements TooltipProvider {
   public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((var0) -> var0.group(TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)).apply(var0, ArmorTrim::new));
   public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTrim> STREAM_CODEC;
   private static final Component UPGRADE_TITLE;

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2) {
      super();
      this.material = var1;
      this.pattern = var2;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3, @Nullable Player var4, ItemStack var5) {
      var2.accept(UPGRADE_TITLE);
      var2.accept(CommonComponents.space().append((this.pattern.value()).copyWithStyle(this.material)));
      var2.accept(CommonComponents.space().append(((TrimMaterial)this.material.value()).description()));
   }

   public ResourceLocation layerAssetId(String var1, ResourceKey<EquipmentAsset> var2) {
      MaterialAssetGroup.AssetInfo var3 = ((TrimMaterial)this.material().value()).assets().assetId(var2);
      return ((TrimPattern)this.pattern().value()).assetId().withPath((UnaryOperator)((var2x) -> var1 + "/" + var2x + "_" + var3.suffix()));
   }

   static {
      STREAM_CODEC = StreamCodec.composite(TrimMaterial.STREAM_CODEC, ArmorTrim::material, TrimPattern.STREAM_CODEC, ArmorTrim::pattern, ArmorTrim::new);
      UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
   }
}
