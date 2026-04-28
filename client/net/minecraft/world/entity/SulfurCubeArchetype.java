package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;

public record SulfurCubeArchetype(HolderSet<Item> items, List<AttributeEntry> attributeModifiers, boolean buoyant, Optional<Integer> explosionFuse) {
   public static final Codec<SulfurCubeArchetype> DIRECT_CODEC = RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("items").forGetter(SulfurCubeArchetype::items), SulfurCubeArchetype.AttributeEntry.CODEC.listOf().fieldOf("attribute_modifiers").forGetter(SulfurCubeArchetype::attributeModifiers), Codec.BOOL.optionalFieldOf("buoyant", false).forGetter(SulfurCubeArchetype::buoyant), Codec.INT.optionalFieldOf("explosion_fuse").forGetter(SulfurCubeArchetype::explosionFuse)).apply(i, SulfurCubeArchetype::new));

   public SulfurCubeArchetype {
      super();
   }

   public static record AttributeEntry(Holder<Attribute> attribute, AttributeModifier modifier) {
      public static final Codec<AttributeEntry> CODEC = RecordCodecBuilder.create((i) -> i.group(Attribute.CODEC.fieldOf("attribute").forGetter(AttributeEntry::attribute), AttributeModifier.MAP_CODEC.forGetter(AttributeEntry::modifier)).apply(i, AttributeEntry::new));

      public AttributeEntry {
         super();
      }

      public static AttributeEntry add(final Holder<Attribute> attribute, final double amount, final ResourceKey<SulfurCubeArchetype> archetype) {
         String var10005 = archetype.identifier().getPath();
         return new AttributeEntry(attribute, new AttributeModifier(Identifier.withDefaultNamespace(var10005 + "_add_" + ((ResourceKey)attribute.unwrapKey().get()).identifier().getPath()), amount, AttributeModifier.Operation.ADD_VALUE));
      }

      public static AttributeEntry multiply(final Holder<Attribute> attribute, final double amount, final ResourceKey<SulfurCubeArchetype> archetype) {
         String var10005 = archetype.identifier().getPath();
         return new AttributeEntry(attribute, new AttributeModifier(Identifier.withDefaultNamespace(var10005 + "_mul_" + ((ResourceKey)attribute.unwrapKey().get()).identifier().getPath()), amount - 1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
      }
   }
}
