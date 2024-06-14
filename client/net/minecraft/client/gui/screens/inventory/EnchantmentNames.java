package net.minecraft.client.gui.screens.inventory;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;

public class EnchantmentNames {
   private static final ResourceLocation ALT_FONT = ResourceLocation.withDefaultNamespace("alt");
   private static final Style ROOT_STYLE = Style.EMPTY.withFont(ALT_FONT);
   private static final EnchantmentNames INSTANCE = new EnchantmentNames();
   private final RandomSource random = RandomSource.create();
   private final String[] words = new String[]{
      "the",
      "elder",
      "scrolls",
      "klaatu",
      "berata",
      "niktu",
      "xyzzy",
      "bless",
      "curse",
      "light",
      "darkness",
      "fire",
      "air",
      "earth",
      "water",
      "hot",
      "dry",
      "cold",
      "wet",
      "ignite",
      "snuff",
      "embiggen",
      "twist",
      "shorten",
      "stretch",
      "fiddle",
      "destroy",
      "imbue",
      "galvanize",
      "enchant",
      "free",
      "limited",
      "range",
      "of",
      "towards",
      "inside",
      "sphere",
      "cube",
      "self",
      "other",
      "ball",
      "mental",
      "physical",
      "grow",
      "shrink",
      "demon",
      "elemental",
      "spirit",
      "animal",
      "creature",
      "beast",
      "humanoid",
      "undead",
      "fresh",
      "stale",
      "phnglui",
      "mglwnafh",
      "cthulhu",
      "rlyeh",
      "wgahnagl",
      "fhtagn",
      "baguette"
   };

   private EnchantmentNames() {
      super();
   }

   public static EnchantmentNames getInstance() {
      return INSTANCE;
   }

   public FormattedText getRandomName(Font var1, int var2) {
      StringBuilder var3 = new StringBuilder();
      int var4 = this.random.nextInt(2) + 3;

      for (int var5 = 0; var5 < var4; var5++) {
         if (var5 != 0) {
            var3.append(" ");
         }

         var3.append(Util.getRandom(this.words, this.random));
      }

      return var1.getSplitter().headByWidth(Component.literal(var3.toString()).withStyle(ROOT_STYLE), var2, Style.EMPTY);
   }

   public void initSeed(long var1) {
      this.random.setSeed(var1);
   }
}
