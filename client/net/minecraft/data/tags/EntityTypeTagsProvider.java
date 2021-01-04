package net.minecraft.data.tags;

import java.nio.file.Path;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.world.entity.EntityType;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
   public EntityTypeTagsProvider(DataGenerator var1) {
      super(var1, Registry.ENTITY_TYPE);
   }

   protected void addTags() {
      this.tag(EntityTypeTags.SKELETONS).add((Object[])(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON));
      this.tag(EntityTypeTags.RAIDERS).add((Object[])(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH));
   }

   protected Path getPath(ResourceLocation var1) {
      return this.generator.getOutputFolder().resolve("data/" + var1.getNamespace() + "/tags/entity_types/" + var1.getPath() + ".json");
   }

   public String getName() {
      return "Entity Type Tags";
   }

   protected void useTags(TagCollection<EntityType<?>> var1) {
      EntityTypeTags.reset(var1);
   }
}
