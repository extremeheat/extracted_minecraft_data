package net.minecraft.server;

import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.locale.Language;
import net.minecraft.tags.StaticTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.FireBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Bootstrap {
   public static final PrintStream STDOUT;
   private static boolean isBootstrapped;
   private static final Logger LOGGER;

   public static void bootStrap() {
      if (!isBootstrapped) {
         isBootstrapped = true;
         if (Registry.REGISTRY.keySet().isEmpty()) {
            throw new IllegalStateException("Unable to load registries");
         } else {
            FireBlock.bootStrap();
            ComposterBlock.bootStrap();
            if (EntityType.getKey(EntityType.PLAYER) == null) {
               throw new IllegalStateException("Failed loading EntityTypes");
            } else {
               PotionBrewing.bootStrap();
               EntitySelectorOptions.bootStrap();
               DispenseItemBehavior.bootStrap();
               CauldronInteraction.bootStrap();
               ArgumentTypes.bootStrap();
               StaticTags.bootStrap();
               wrapStreams();
            }
         }
      }
   }

   private static <T> void checkTranslations(Iterable<T> var0, Function<T, String> var1, Set<String> var2) {
      Language var3 = Language.getInstance();
      var0.forEach((var3x) -> {
         String var4 = (String)var1.apply(var3x);
         if (!var3.has(var4)) {
            var2.add(var4);
         }

      });
   }

   private static void checkGameruleTranslations(final Set<String> var0) {
      final Language var1 = Language.getInstance();
      GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
         public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1x, GameRules.Type<T> var2) {
            if (!var1.has(var1x.getDescriptionId())) {
               var0.add(var1x.getId());
            }

         }
      });
   }

   public static Set<String> getMissingTranslations() {
      TreeSet var0 = new TreeSet();
      checkTranslations(Registry.ATTRIBUTE, Attribute::getDescriptionId, var0);
      checkTranslations(Registry.ENTITY_TYPE, EntityType::getDescriptionId, var0);
      checkTranslations(Registry.MOB_EFFECT, MobEffect::getDescriptionId, var0);
      checkTranslations(Registry.ITEM, Item::getDescriptionId, var0);
      checkTranslations(Registry.ENCHANTMENT, Enchantment::getDescriptionId, var0);
      checkTranslations(Registry.BLOCK, Block::getDescriptionId, var0);
      checkTranslations(Registry.CUSTOM_STAT, (var0x) -> {
         return "stat." + var0x.toString().replace(':', '.');
      }, var0);
      checkGameruleTranslations(var0);
      return var0;
   }

   public static void validate() {
      if (!isBootstrapped) {
         throw new IllegalArgumentException("Not bootstrapped");
      } else {
         if (SharedConstants.IS_RUNNING_IN_IDE) {
            getMissingTranslations().forEach((var0) -> {
               LOGGER.error("Missing translations: {}", var0);
            });
            Commands.validate();
         }

         DefaultAttributes.validate();
      }
   }

   private static void wrapStreams() {
      if (LOGGER.isDebugEnabled()) {
         System.setErr(new DebugLoggedPrintStream("STDERR", System.err));
         System.setOut(new DebugLoggedPrintStream("STDOUT", STDOUT));
      } else {
         System.setErr(new LoggedPrintStream("STDERR", System.err));
         System.setOut(new LoggedPrintStream("STDOUT", STDOUT));
      }

   }

   public static void realStdoutPrintln(String var0) {
      STDOUT.println(var0);
   }

   static {
      STDOUT = System.out;
      LOGGER = LogManager.getLogger();
   }
}
