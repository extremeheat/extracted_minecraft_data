package net.minecraft.data.tags;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

public class UpdateOneTwentyOneItemTagsProvider extends ItemTagsProvider {
   public UpdateOneTwentyOneItemTagsProvider(PackOutput var1, CompletableFuture<HolderLookup.Provider> var2, CompletableFuture<TagsProvider.TagLookup<Item>> var3, CompletableFuture<TagsProvider.TagLookup<Block>> var4) {
      super(var1, var2, var3, var4);
   }

   protected void addTags(HolderLookup.Provider var1) {
      this.tag(ItemTags.STAIRS).add((Object[])(Items.TUFF_STAIRS, Items.POLISHED_TUFF_STAIRS, Items.TUFF_BRICK_STAIRS));
      this.tag(ItemTags.SLABS).add((Object[])(Items.TUFF_SLAB, Items.POLISHED_TUFF_SLAB, Items.TUFF_BRICK_SLAB));
      this.tag(ItemTags.WALLS).add((Object[])(Items.TUFF_WALL, Items.POLISHED_TUFF_WALL, Items.TUFF_BRICK_WALL));
      this.tag(ItemTags.DOORS).add((Object[])(Items.COPPER_DOOR, Items.EXPOSED_COPPER_DOOR, Items.WEATHERED_COPPER_DOOR, Items.OXIDIZED_COPPER_DOOR, Items.WAXED_COPPER_DOOR, Items.WAXED_EXPOSED_COPPER_DOOR, Items.WAXED_WEATHERED_COPPER_DOOR, Items.WAXED_OXIDIZED_COPPER_DOOR));
      this.tag(ItemTags.TRAPDOORS).add((Object[])(Items.COPPER_TRAPDOOR, Items.EXPOSED_COPPER_TRAPDOOR, Items.WEATHERED_COPPER_TRAPDOOR, Items.OXIDIZED_COPPER_TRAPDOOR, Items.WAXED_COPPER_TRAPDOOR, Items.WAXED_EXPOSED_COPPER_TRAPDOOR, Items.WAXED_WEATHERED_COPPER_TRAPDOOR, Items.WAXED_OXIDIZED_COPPER_TRAPDOOR));
      this.tag(ItemTags.MACE_ENCHANTABLE).add((Object)Items.MACE);
      this.tag(ItemTags.DECORATED_POT_SHERDS).add((Object[])(Items.FLOW_POTTERY_SHERD, Items.GUSTER_POTTERY_SHERD, Items.SCRAPE_POTTERY_SHERD));
      this.tag(ItemTags.DECORATED_POT_INGREDIENTS).add((Object)Items.FLOW_POTTERY_SHERD).add((Object)Items.GUSTER_POTTERY_SHERD).add((Object)Items.SCRAPE_POTTERY_SHERD);
      this.tag(ItemTags.TRIM_TEMPLATES).add((Object)Items.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE).add((Object)Items.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE);
      this.tag(ItemTags.DURABILITY_ENCHANTABLE).add((Object)Items.MACE);
      this.tag(ItemTags.WEAPON_ENCHANTABLE).add((Object)Items.MACE);
      this.tag(ItemTags.FIRE_ASPECT_ENCHANTABLE).add((Object)Items.MACE);
      this.tag(ItemTags.VANISHING_ENCHANTABLE).add((Object)Items.MACE);
   }
}
