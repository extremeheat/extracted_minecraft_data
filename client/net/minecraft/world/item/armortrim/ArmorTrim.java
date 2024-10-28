package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public class ArmorTrim implements TooltipProvider {
   public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern), Codec.BOOL.optionalFieldOf("show_in_tooltip", true).forGetter((var0x) -> {
         return var0x.showInTooltip;
      })).apply(var0, ArmorTrim::new);
   });
   public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTrim> STREAM_CODEC;
   private static final Component UPGRADE_TITLE;
   private final Holder<TrimMaterial> material;
   private final Holder<TrimPattern> pattern;
   private final boolean showInTooltip;
   private final Function<Holder<ArmorMaterial>, ResourceLocation> innerTexture;
   private final Function<Holder<ArmorMaterial>, ResourceLocation> outerTexture;

   private ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2, boolean var3, Function<Holder<ArmorMaterial>, ResourceLocation> var4, Function<Holder<ArmorMaterial>, ResourceLocation> var5) {
      super();
      this.material = var1;
      this.pattern = var2;
      this.showInTooltip = var3;
      this.innerTexture = var4;
      this.outerTexture = var5;
   }

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2, boolean var3) {
      super();
      this.material = var1;
      this.pattern = var2;
      this.innerTexture = Util.memoize((var2x) -> {
         ResourceLocation var3 = ((TrimPattern)var2.value()).assetId();
         String var4 = getColorPaletteSuffix(var1, var2x);
         return var3.withPath((var1x) -> {
            return "trims/models/armor/" + var1x + "_leggings_" + var4;
         });
      });
      this.outerTexture = Util.memoize((var2x) -> {
         ResourceLocation var3 = ((TrimPattern)var2.value()).assetId();
         String var4 = getColorPaletteSuffix(var1, var2x);
         return var3.withPath((var1x) -> {
            return "trims/models/armor/" + var1x + "_" + var4;
         });
      });
      this.showInTooltip = var3;
   }

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2) {
      this(var1, var2, true);
   }

   private static String getColorPaletteSuffix(Holder<TrimMaterial> var0, Holder<ArmorMaterial> var1) {
      Map var2 = ((TrimMaterial)var0.value()).overrideArmorMaterials();
      String var3 = (String)var2.get(var1);
      return var3 != null ? var3 : ((TrimMaterial)var0.value()).assetName();
   }

   public boolean hasPatternAndMaterial(Holder<TrimPattern> var1, Holder<TrimMaterial> var2) {
      return var1.equals(this.pattern) && var2.equals(this.material);
   }

   public Holder<TrimPattern> pattern() {
      return this.pattern;
   }

   public Holder<TrimMaterial> material() {
      return this.material;
   }

   public ResourceLocation innerTexture(Holder<ArmorMaterial> var1) {
      return (ResourceLocation)this.innerTexture.apply(var1);
   }

   public ResourceLocation outerTexture(Holder<ArmorMaterial> var1) {
      return (ResourceLocation)this.outerTexture.apply(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof ArmorTrim var2)) {
         return false;
      } else {
         return this.showInTooltip == var2.showInTooltip && this.pattern.equals(var2.pattern) && this.material.equals(var2.material);
      }
   }

   public int hashCode() {
      int var1 = this.material.hashCode();
      var1 = 31 * var1 + this.pattern.hashCode();
      var1 = 31 * var1 + (this.showInTooltip ? 1 : 0);
      return var1;
   }

   public void addToTooltip(Item.TooltipContext var1, Consumer<Component> var2, TooltipFlag var3) {
      if (this.showInTooltip) {
         var2.accept(UPGRADE_TITLE);
         var2.accept(CommonComponents.space().append(((TrimPattern)this.pattern.value()).copyWithStyle(this.material)));
         var2.accept(CommonComponents.space().append(((TrimMaterial)this.material.value()).description()));
      }
   }

   public ArmorTrim withTooltip(boolean var1) {
      return new ArmorTrim(this.material, this.pattern, var1, this.innerTexture, this.outerTexture);
   }

   static {
      STREAM_CODEC = StreamCodec.composite(TrimMaterial.STREAM_CODEC, ArmorTrim::material, TrimPattern.STREAM_CODEC, ArmorTrim::pattern, ByteBufCodecs.BOOL, (var0) -> {
         return var0.showInTooltip;
      }, ArmorTrim::new);
      UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", ResourceLocation.withDefaultNamespace("smithing_template.upgrade"))).withStyle(ChatFormatting.GRAY);
   }
}
