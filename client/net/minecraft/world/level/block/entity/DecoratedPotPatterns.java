package net.minecraft.world.level.block.entity;

import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class DecoratedPotPatterns {
   public static final ResourceKey<DecoratedPotPattern> BLANK = create("blank");
   public static final ResourceKey<DecoratedPotPattern> ANGLER = create("angler");
   public static final ResourceKey<DecoratedPotPattern> ARCHER = create("archer");
   public static final ResourceKey<DecoratedPotPattern> ARMS_UP = create("arms_up");
   public static final ResourceKey<DecoratedPotPattern> BLADE = create("blade");
   public static final ResourceKey<DecoratedPotPattern> BREWER = create("brewer");
   public static final ResourceKey<DecoratedPotPattern> BURN = create("burn");
   public static final ResourceKey<DecoratedPotPattern> DANGER = create("danger");
   public static final ResourceKey<DecoratedPotPattern> EXPLORER = create("explorer");
   public static final ResourceKey<DecoratedPotPattern> FLOW = create("flow");
   public static final ResourceKey<DecoratedPotPattern> FRIEND = create("friend");
   public static final ResourceKey<DecoratedPotPattern> GUSTER = create("guster");
   public static final ResourceKey<DecoratedPotPattern> HEART = create("heart");
   public static final ResourceKey<DecoratedPotPattern> HEARTBREAK = create("heartbreak");
   public static final ResourceKey<DecoratedPotPattern> HOWL = create("howl");
   public static final ResourceKey<DecoratedPotPattern> MINER = create("miner");
   public static final ResourceKey<DecoratedPotPattern> MOURNER = create("mourner");
   public static final ResourceKey<DecoratedPotPattern> PLENTY = create("plenty");
   public static final ResourceKey<DecoratedPotPattern> PRIZE = create("prize");
   public static final ResourceKey<DecoratedPotPattern> SCRAPE = create("scrape");
   public static final ResourceKey<DecoratedPotPattern> SHEAF = create("sheaf");
   public static final ResourceKey<DecoratedPotPattern> SHELTER = create("shelter");
   public static final ResourceKey<DecoratedPotPattern> SKULL = create("skull");
   public static final ResourceKey<DecoratedPotPattern> SNORT = create("snort");
   private static final Map<Item, ResourceKey<DecoratedPotPattern>> ITEM_TO_POT_TEXTURE;

   public DecoratedPotPatterns() {
      super();
   }

   @Nullable
   public static ResourceKey<DecoratedPotPattern> getPatternFromItem(Item var0) {
      return (ResourceKey)ITEM_TO_POT_TEXTURE.get(var0);
   }

   private static ResourceKey<DecoratedPotPattern> create(String var0) {
      return ResourceKey.create(Registries.DECORATED_POT_PATTERN, ResourceLocation.withDefaultNamespace(var0));
   }

   public static DecoratedPotPattern bootstrap(Registry<DecoratedPotPattern> var0) {
      register(var0, ANGLER, "angler_pottery_pattern");
      register(var0, ARCHER, "archer_pottery_pattern");
      register(var0, ARMS_UP, "arms_up_pottery_pattern");
      register(var0, BLADE, "blade_pottery_pattern");
      register(var0, BREWER, "brewer_pottery_pattern");
      register(var0, BURN, "burn_pottery_pattern");
      register(var0, DANGER, "danger_pottery_pattern");
      register(var0, EXPLORER, "explorer_pottery_pattern");
      register(var0, FLOW, "flow_pottery_pattern");
      register(var0, FRIEND, "friend_pottery_pattern");
      register(var0, GUSTER, "guster_pottery_pattern");
      register(var0, HEART, "heart_pottery_pattern");
      register(var0, HEARTBREAK, "heartbreak_pottery_pattern");
      register(var0, HOWL, "howl_pottery_pattern");
      register(var0, MINER, "miner_pottery_pattern");
      register(var0, MOURNER, "mourner_pottery_pattern");
      register(var0, PLENTY, "plenty_pottery_pattern");
      register(var0, PRIZE, "prize_pottery_pattern");
      register(var0, SCRAPE, "scrape_pottery_pattern");
      register(var0, SHEAF, "sheaf_pottery_pattern");
      register(var0, SHELTER, "shelter_pottery_pattern");
      register(var0, SKULL, "skull_pottery_pattern");
      register(var0, SNORT, "snort_pottery_pattern");
      return register(var0, BLANK, "decorated_pot_side");
   }

   private static DecoratedPotPattern register(Registry<DecoratedPotPattern> var0, ResourceKey<DecoratedPotPattern> var1, String var2) {
      return (DecoratedPotPattern)Registry.register(var0, (ResourceKey)var1, new DecoratedPotPattern(ResourceLocation.withDefaultNamespace(var2)));
   }

   static {
      ITEM_TO_POT_TEXTURE = Map.ofEntries(Map.entry(Items.BRICK, BLANK), Map.entry(Items.ANGLER_POTTERY_SHERD, ANGLER), Map.entry(Items.ARCHER_POTTERY_SHERD, ARCHER), Map.entry(Items.ARMS_UP_POTTERY_SHERD, ARMS_UP), Map.entry(Items.BLADE_POTTERY_SHERD, BLADE), Map.entry(Items.BREWER_POTTERY_SHERD, BREWER), Map.entry(Items.BURN_POTTERY_SHERD, BURN), Map.entry(Items.DANGER_POTTERY_SHERD, DANGER), Map.entry(Items.EXPLORER_POTTERY_SHERD, EXPLORER), Map.entry(Items.FLOW_POTTERY_SHERD, FLOW), Map.entry(Items.FRIEND_POTTERY_SHERD, FRIEND), Map.entry(Items.GUSTER_POTTERY_SHERD, GUSTER), Map.entry(Items.HEART_POTTERY_SHERD, HEART), Map.entry(Items.HEARTBREAK_POTTERY_SHERD, HEARTBREAK), Map.entry(Items.HOWL_POTTERY_SHERD, HOWL), Map.entry(Items.MINER_POTTERY_SHERD, MINER), Map.entry(Items.MOURNER_POTTERY_SHERD, MOURNER), Map.entry(Items.PLENTY_POTTERY_SHERD, PLENTY), Map.entry(Items.PRIZE_POTTERY_SHERD, PRIZE), Map.entry(Items.SCRAPE_POTTERY_SHERD, SCRAPE), Map.entry(Items.SHEAF_POTTERY_SHERD, SHEAF), Map.entry(Items.SHELTER_POTTERY_SHERD, SHELTER), Map.entry(Items.SKULL_POTTERY_SHERD, SKULL), Map.entry(Items.SNORT_POTTERY_SHERD, SNORT));
   }
}
