package net.minecraft.world.item.armortrim;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public class ArmorTrim {
   public static final Codec<ArmorTrim> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               TrimMaterial.CODEC.fieldOf("material").forGetter(ArmorTrim::material), TrimPattern.CODEC.fieldOf("pattern").forGetter(ArmorTrim::pattern)
            )
            .apply(var0, ArmorTrim::new)
   );
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final String TAG_TRIM_ID = "Trim";
   private static final Component UPGRADE_TITLE = Component.translatable(Util.makeDescriptionId("item", new ResourceLocation("smithing_template.upgrade")))
      .withStyle(ChatFormatting.GRAY);
   private final Holder<TrimMaterial> material;
   private final Holder<TrimPattern> pattern;
   private final Function<ArmorMaterial, ResourceLocation> innerTexture;
   private final Function<ArmorMaterial, ResourceLocation> outerTexture;

   public ArmorTrim(Holder<TrimMaterial> var1, Holder<TrimPattern> var2) {
      super();
      this.material = var1;
      this.pattern = var2;
      this.innerTexture = Util.memoize(var2x -> {
         ResourceLocation var3 = ((TrimPattern)var2.value()).assetId();
         String var4 = this.getColorPaletteSuffix(var2x);
         return var3.withPath(var1xx -> "trims/models/armor/" + var1xx + "_leggings_" + var4);
      });
      this.outerTexture = Util.memoize(var2x -> {
         ResourceLocation var3 = ((TrimPattern)var2.value()).assetId();
         String var4 = this.getColorPaletteSuffix(var2x);
         return var3.withPath(var1xx -> "trims/models/armor/" + var1xx + "_" + var4);
      });
   }

   private String getColorPaletteSuffix(ArmorMaterial var1) {
      Map var2 = this.material.value().overrideArmorMaterials();
      return var1 instanceof ArmorMaterials && var2.containsKey(var1) ? (String)var2.get(var1) : this.material.value().assetName();
   }

   public boolean hasPatternAndMaterial(Holder<TrimPattern> var1, Holder<TrimMaterial> var2) {
      return var1 == this.pattern && var2 == this.material;
   }

   public Holder<TrimPattern> pattern() {
      return this.pattern;
   }

   public Holder<TrimMaterial> material() {
      return this.material;
   }

   public ResourceLocation innerTexture(ArmorMaterial var1) {
      return this.innerTexture.apply(var1);
   }

   public ResourceLocation outerTexture(ArmorMaterial var1) {
      return this.outerTexture.apply(var1);
   }

   @Override
   public boolean equals(Object var1) {
      if (!(var1 instanceof ArmorTrim)) {
         return false;
      } else {
         ArmorTrim var2 = (ArmorTrim)var1;
         return var2.pattern == this.pattern && var2.material == this.material;
      }
   }

   public static boolean setTrim(RegistryAccess var0, ItemStack var1, ArmorTrim var2) {
      if (var1.is(ItemTags.TRIMMABLE_ARMOR)) {
         var1.getOrCreateTag().put("Trim", (Tag)CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, var0), var2).result().orElseThrow());
         return true;
      } else {
         return false;
      }
   }

   public static Optional<ArmorTrim> getTrim(RegistryAccess var0, ItemStack var1, boolean var2) {
      if (var1.is(ItemTags.TRIMMABLE_ARMOR) && var1.getTag() != null && var1.getTag().contains("Trim")) {
         CompoundTag var3 = var1.getTagElement("Trim");
         ArmorTrim var4 = (ArmorTrim)CODEC.parse(RegistryOps.create(NbtOps.INSTANCE, var0), var3).resultOrPartial(var1x -> {
            if (!var2) {
               LOGGER.warn(var1x);
            }
         }).orElse(null);
         return Optional.ofNullable(var4);
      } else {
         return Optional.empty();
      }
   }

   public static void appendUpgradeHoverText(ItemStack var0, RegistryAccess var1, List<Component> var2) {
      Optional var3 = getTrim(var1, var0, true);
      if (var3.isPresent()) {
         ArmorTrim var4 = (ArmorTrim)var3.get();
         var2.add(UPGRADE_TITLE);
         var2.add(CommonComponents.space().append(var4.pattern().value().copyWithStyle(var4.material())));
         var2.add(CommonComponents.space().append(var4.material().value().description()));
      }
   }
}
