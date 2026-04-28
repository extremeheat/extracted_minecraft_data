package net.minecraft.world.entity;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;

public class SulfurCubeArchetypes {
   public static final ResourceKey<SulfurCubeArchetype> REGULAR = createKey(Identifier.withDefaultNamespace("regular"));
   public static final ResourceKey<SulfurCubeArchetype> BOUNCY = createKey(Identifier.withDefaultNamespace("bouncy"));
   public static final ResourceKey<SulfurCubeArchetype> SLOW_FLAT = createKey(Identifier.withDefaultNamespace("slow_flat"));
   public static final ResourceKey<SulfurCubeArchetype> FAST_FLAT = createKey(Identifier.withDefaultNamespace("fast_flat"));
   public static final ResourceKey<SulfurCubeArchetype> LIGHT = createKey(Identifier.withDefaultNamespace("light"));
   public static final ResourceKey<SulfurCubeArchetype> FAST_SLIDING = createKey(Identifier.withDefaultNamespace("fast_sliding"));
   public static final ResourceKey<SulfurCubeArchetype> SLOW_SLIDING = createKey(Identifier.withDefaultNamespace("slow_sliding"));
   public static final ResourceKey<SulfurCubeArchetype> HIGH_RESISTANCE = createKey(Identifier.withDefaultNamespace("high_resistance"));
   public static final ResourceKey<SulfurCubeArchetype> STICKY = createKey(Identifier.withDefaultNamespace("sticky"));
   public static final ResourceKey<SulfurCubeArchetype> EXPLOSIVE = createKey(Identifier.withDefaultNamespace("explosive"));

   public SulfurCubeArchetypes() {
      super();
   }

   public static void bootstrap(final BootstrapContext<SulfurCubeArchetype> context) {
      register(context, REGULAR, ItemTags.SULFUR_CUBE_ARCHETYPE_REGULAR, archetype(1.0F, 0.5F, 0.3F, 0.1F), true, Optional.empty());
      register(context, BOUNCY, ItemTags.SULFUR_CUBE_ARCHETYPE_BOUNCY, archetype(2.0F, 0.9F, 0.3F, 0.01F), true, Optional.empty());
      register(context, SLOW_FLAT, ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_FLAT, archetype(-0.7F, 0.2F, 0.3F, 0.1F), false, Optional.empty());
      register(context, FAST_FLAT, ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_FLAT, archetype(2.0F, 0.2F, 0.1F, 0.01F), false, Optional.empty());
      register(context, LIGHT, ItemTags.SULFUR_CUBE_ARCHETYPE_LIGHT, archetype(1.0F, 1.0F, 0.3F, 1.8F), true, Optional.empty());
      register(context, FAST_SLIDING, ItemTags.SULFUR_CUBE_ARCHETYPE_FAST_SLIDING, archetype(-0.5F, 0.1F, 0.05F, 0.01F), false, Optional.empty());
      register(context, SLOW_SLIDING, ItemTags.SULFUR_CUBE_ARCHETYPE_SLOW_SLIDING, archetype(-0.8F, 0.1F, 0.05F, 0.01F), false, Optional.empty());
      register(context, STICKY, ItemTags.SULFUR_CUBE_ARCHETYPE_STICKY, archetype(2.0F, 0.0F, 2.0F, 0.01F), false, Optional.empty());
      register(context, HIGH_RESISTANCE, ItemTags.SULFUR_CUBE_ARCHETYPE_HIGH_RESISTANCE, archetype(-0.7F, 0.2F, 1.0F, 0.01F), false, Optional.empty());
      register(context, EXPLOSIVE, ItemTags.SULFUR_CUBE_ARCHETYPE_EXPLOSIVE, archetype(1.0F, 0.5F, 0.3F, 0.3F), true, Optional.of(120));
   }

   private static ResourceKey<SulfurCubeArchetype> createKey(final Identifier id) {
      return ResourceKey.create(Registries.SULFUR_CUBE_ARCHETYPE, id);
   }

   private static Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry> add(final Holder<Attribute> attribute, final double amount) {
      return (key) -> SulfurCubeArchetype.AttributeEntry.add(attribute, amount, key);
   }

   private static Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry> multiply(final Holder<Attribute> attribute, final double amount) {
      return (key) -> SulfurCubeArchetype.AttributeEntry.multiply(attribute, amount, key);
   }

   private static List<Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry>> archetype(final float speed, final float bounce, final float friction, final float drag) {
      return List.of(add(Attributes.KNOCKBACK_RESISTANCE, (double)(-speed)), add(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE, (double)(-speed)), add(Attributes.BOUNCINESS, (double)bounce), multiply(Attributes.FRICTION_MODIFIER, (double)friction), multiply(Attributes.AIR_DRAG_MODIFIER, (double)drag));
   }

   private static void register(final BootstrapContext<SulfurCubeArchetype> context, final ResourceKey<SulfurCubeArchetype> name, final TagKey<Item> blocks, final List<Function<ResourceKey<SulfurCubeArchetype>, SulfurCubeArchetype.AttributeEntry>> modifiers, final boolean floats, final Optional<Integer> maxFuse) {
      context.register(name, new SulfurCubeArchetype(context.lookup(Registries.ITEM).getOrThrow(blocks), modifiers.stream().map((f) -> (SulfurCubeArchetype.AttributeEntry)f.apply(name)).toList(), floats, maxFuse));
   }
}
