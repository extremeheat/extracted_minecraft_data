package net.minecraft.world.level.block.entity.vault;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.trialspawner.PlayerDetector;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootTable;

public record VaultConfig(
   ResourceKey<LootTable> lootTable,
   double activationRange,
   double deactivationRange,
   ItemStack keyItem,
   Optional<ResourceKey<LootTable>> overrideLootTableToDisplay,
   PlayerDetector playerDetector,
   PlayerDetector.EntitySelector entitySelector
) {
   static final String TAG_NAME = "config";
   static VaultConfig DEFAULT = new VaultConfig();
   static Codec<VaultConfig> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  ResourceKey.codec(Registries.LOOT_TABLE).lenientOptionalFieldOf("loot_table", DEFAULT.lootTable()).forGetter(VaultConfig::lootTable),
                  Codec.DOUBLE.lenientOptionalFieldOf("activation_range", DEFAULT.activationRange()).forGetter(VaultConfig::activationRange),
                  Codec.DOUBLE.lenientOptionalFieldOf("deactivation_range", DEFAULT.deactivationRange()).forGetter(VaultConfig::deactivationRange),
                  ItemStack.lenientOptionalFieldOf("key_item").forGetter(VaultConfig::keyItem),
                  ResourceKey.codec(Registries.LOOT_TABLE)
                     .lenientOptionalFieldOf("override_loot_table_to_display")
                     .forGetter(VaultConfig::overrideLootTableToDisplay)
               )
               .apply(var0, VaultConfig::new)
      )
      .validate(VaultConfig::validate);

   private VaultConfig() {
      this(
         BuiltInLootTables.TRIAL_CHAMBERS_REWARD,
         4.0,
         4.5,
         new ItemStack(Items.TRIAL_KEY),
         Optional.empty(),
         PlayerDetector.INCLUDING_CREATIVE_PLAYERS,
         PlayerDetector.EntitySelector.SELECT_FROM_LEVEL
      );
   }

   public VaultConfig(ResourceKey<LootTable> var1, double var2, double var4, ItemStack var6, Optional<ResourceKey<LootTable>> var7) {
      this(var1, var2, var4, var6, var7, DEFAULT.playerDetector(), DEFAULT.entitySelector());
   }

   public VaultConfig(
      ResourceKey<LootTable> lootTable,
      double activationRange,
      double deactivationRange,
      ItemStack keyItem,
      Optional<ResourceKey<LootTable>> overrideLootTableToDisplay,
      PlayerDetector playerDetector,
      PlayerDetector.EntitySelector entitySelector
   ) {
      super();
      this.lootTable = lootTable;
      this.activationRange = activationRange;
      this.deactivationRange = deactivationRange;
      this.keyItem = keyItem;
      this.overrideLootTableToDisplay = overrideLootTableToDisplay;
      this.playerDetector = playerDetector;
      this.entitySelector = entitySelector;
   }

   private DataResult<VaultConfig> validate() {
      return this.activationRange > this.deactivationRange
         ? DataResult.error(
            () -> "Activation range must (" + this.activationRange + ") be less or equal to deactivation range (" + this.deactivationRange + ")"
         )
         : DataResult.success(this);
   }
}