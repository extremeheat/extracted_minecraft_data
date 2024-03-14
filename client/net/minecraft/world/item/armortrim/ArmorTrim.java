package net.minecraft.world.item.armortrim;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
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
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

public class ArmorTrim implements TooltipProvider {
   public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material),
               TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern),
               ExtraCodecs.strictOptionalField(Codec.BOOL, "show_in_tooltip", true).forGetter(var0x -> var0x.showInTooltip)
            )
            .apply(var0, ArmorTrim::new)
   );
   public static final StreamCodec<RegistryFriendlyByteBuf, ArmorTrim> STREAM_CODEC = StreamCodec.composite(
      TrimMaterial.STREAM_CODEC,
      ArmorTrim::material,
      TrimPattern.STREAM_CODEC,
      ArmorTrim::pattern,
      ByteBufCodecs.BOOL,
      var0 -> var0.showInTooltip,
      ArmorTrim::new
   );
   private static final Component UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade")))
      .withStyle(ChatFormatting.GRAY);
   private final Holder<TrimMaterial> material;
   private final Holder<TrimPattern> pattern;
   private final boolean showInTooltip;
   private final Function<Holder<ArmorMaterial>, ResourceLocation> innerTexture;
   private final Function<Holder<ArmorMaterial>, ResourceLocation> outerTexture;

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2, boolean var3) {
      super();
      this.material = var1;
      this.pattern = var2;
      this.innerTexture = Util.memoize(var2x -> {
         ResourceLocation var3xx = ((TrimPattern)var2.value()).assetId();
         String var4 = this.getColorPaletteSuffix(var2x);
         return var3xx.withPath(var1xx -> "trims/models/armor/" + var1xx + "_leggings_" + var4);
      });
      this.outerTexture = Util.memoize(var2x -> {
         ResourceLocation var3xx = ((TrimPattern)var2.value()).assetId();
         String var4 = this.getColorPaletteSuffix(var2x);
         return var3xx.withPath(var1xx -> "trims/models/armor/" + var1xx + "_" + var4);
      });
      this.showInTooltip = var3;
   }

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2) {
      this(var1, var2, true);
   }

   private String getColorPaletteSuffix(Holder<ArmorMaterial> var1) {
      Map var2 = ((TrimMaterial)this.material.value()).overrideArmorMaterials();
      String var3 = (String)var2.get(var1);
      return var3 != null ? var3 : ((TrimMaterial)this.material.value()).assetName();
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
      return this.innerTexture.apply(var1);
   }

   public ResourceLocation outerTexture(Holder<ArmorMaterial> var1) {
      return this.outerTexture.apply(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ArmorTrim)) {
         return false;
      } else {
         ArmorTrim var2 = (ArmorTrim)var1;
         return this.showInTooltip == var2.showInTooltip && this.pattern.equals(var2.pattern) && this.material.equals(var2.material);
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.material.hashCode();
      var1 = 31 * var1 + this.pattern.hashCode();
      return 31 * var1 + (this.showInTooltip ? 1 : 0);
   }

   @Override
   public void addToTooltip(Consumer<Component> var1, TooltipFlag var2) {
      if (this.showInTooltip) {
         var1.accept(UPGRADE_TITLE);
         var1.accept(CommonComponents.space().append(((TrimPattern)this.pattern.value()).copyWithStyle(this.material)));
         var1.accept(CommonComponents.space().append(((TrimMaterial)this.material.value()).description()));
      }
   }
}
