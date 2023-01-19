package net.minecraft.server;

import com.mojang.logging.LogUtils;
import java.io.PrintStream;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.SharedConstants;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.locale.Language;
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
import org.slf4j.Logger;

public class Bootstrap {
   public static final PrintStream STDOUT = System.out;
   private static volatile boolean isBootstrapped;
   private static final Logger LOGGER = LogUtils.getLogger();

   public Bootstrap() {
      super();
   }

   public static void bootStrap() {
      if (!isBootstrapped) {
         isBootstrapped = true;
         if (BuiltInRegistries.REGISTRY.keySet().isEmpty()) {
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
               BuiltInRegistries.bootStrap();
               wrapStreams();
            }
         }
      }
   }

   private static <T> void checkTranslations(Iterable<T> var0, Function<T, String> var1, Set<String> var2) {
      Language var3 = Language.getInstance();
      var0.forEach(var3x -> {
         String var4 = (String)var1.apply(var3x);
         if (!var3.has(var4)) {
            var2.add(var4);
         }
      });
   }

   private static void checkGameruleTranslations(final Set<String> var0) {
      final Language var1 = Language.getInstance();
      GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
         @Override
         public <T extends GameRules.Value<T>> void visit(GameRules.Key<T> var1x, GameRules.Type<T> var2) {
            if (!var1.has(var1x.getDescriptionId())) {
               var0.add(var1x.getId());
            }
         }
      });
   }

   public static Set<String> getMissingTranslations() {
      TreeSet var0 = new TreeSet();
      checkTranslations(BuiltInRegistries.ATTRIBUTE, Attribute::getDescriptionId, var0);
      checkTranslations(BuiltInRegistries.ENTITY_TYPE, EntityType::getDescriptionId, var0);
      checkTranslations(BuiltInRegistries.MOB_EFFECT, MobEffect::getDescriptionId, var0);
      checkTranslations(BuiltInRegistries.ITEM, Item::getDescriptionId, var0);
      checkTranslations(BuiltInRegistries.ENCHANTMENT, Enchantment::getDescriptionId, var0);
      checkTranslations(BuiltInRegistries.BLOCK, Block::getDescriptionId, var0);
      checkTranslations(BuiltInRegistries.CUSTOM_STAT, var0x -> "stat." + var0x.toString().replace(':', '.'), var0);
      checkGameruleTranslations(var0);
      return var0;
   }

   public static void checkBootstrapCalled(Supplier<String> var0) {
      if (!isBootstrapped) {
         throw createBootstrapException(var0);
      }
   }

   private static RuntimeException createBootstrapException(Supplier<String> var0) {
      try {
         String var1 = (String)var0.get();
         return new IllegalArgumentException("Not bootstrapped (called from " + var1 + ")");
      } catch (Exception var3) {
         IllegalArgumentException var2 = new IllegalArgumentException("Not bootstrapped (failed to resolve location)");
         var2.addSuppressed(var3);
         return var2;
      }
   }

   public static void validate() {
      checkBootstrapCalled(() -> "validate");
      if (SharedConstants.IS_RUNNING_IN_IDE) {
         getMissingTranslations().forEach(var0 -> LOGGER.error("Missing translations: {}", var0));
         Commands.validate();
      }

      DefaultAttributes.validate();
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
}
