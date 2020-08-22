package net.minecraft.world.level.storage.loot.parameters;

import net.minecraft.resources.ResourceLocation;

public class LootContextParams {
   public static final LootContextParam THIS_ENTITY = create("this_entity");
   public static final LootContextParam LAST_DAMAGE_PLAYER = create("last_damage_player");
   public static final LootContextParam DAMAGE_SOURCE = create("damage_source");
   public static final LootContextParam KILLER_ENTITY = create("killer_entity");
   public static final LootContextParam DIRECT_KILLER_ENTITY = create("direct_killer_entity");
   public static final LootContextParam BLOCK_POS = create("position");
   public static final LootContextParam BLOCK_STATE = create("block_state");
   public static final LootContextParam BLOCK_ENTITY = create("block_entity");
   public static final LootContextParam TOOL = create("tool");
   public static final LootContextParam EXPLOSION_RADIUS = create("explosion_radius");

   private static LootContextParam create(String var0) {
      return new LootContextParam(new ResourceLocation(var0));
   }
}
