package net.minecraft.client;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class KeyMapping implements Comparable<KeyMapping> {
   private static final Map<String, KeyMapping> ALL = Maps.newHashMap();
   private static final Map<InputConstants.Key, KeyMapping> MAP = Maps.newHashMap();
   private static final Set<String> CATEGORIES = Sets.newHashSet();
   public static final String CATEGORY_MOVEMENT = "key.categories.movement";
   public static final String CATEGORY_MISC = "key.categories.misc";
   public static final String CATEGORY_MULTIPLAYER = "key.categories.multiplayer";
   public static final String CATEGORY_GAMEPLAY = "key.categories.gameplay";
   public static final String CATEGORY_INVENTORY = "key.categories.inventory";
   public static final String CATEGORY_INTERFACE = "key.categories.ui";
   public static final String CATEGORY_CREATIVE = "key.categories.creative";
   private static final Map<String, Integer> CATEGORY_SORT_ORDER = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put("key.categories.movement", 1);
      var0.put("key.categories.gameplay", 2);
      var0.put("key.categories.inventory", 3);
      var0.put("key.categories.creative", 4);
      var0.put("key.categories.multiplayer", 5);
      var0.put("key.categories.ui", 6);
      var0.put("key.categories.misc", 7);
   });
   private final String name;
   private final InputConstants.Key defaultKey;
   private final String category;
   private InputConstants.Key key;
   private boolean isDown;
   private int clickCount;

   public static void click(InputConstants.Key var0) {
      KeyMapping var1 = (KeyMapping)MAP.get(var0);
      if (var1 != null) {
         ++var1.clickCount;
      }

   }

   public static void set(InputConstants.Key var0, boolean var1) {
      KeyMapping var2 = (KeyMapping)MAP.get(var0);
      if (var2 != null) {
         var2.setDown(var1);
      }

   }

   public static void setAll() {
      Iterator var0 = ALL.values().iterator();

      while(var0.hasNext()) {
         KeyMapping var1 = (KeyMapping)var0.next();
         if (var1.key.getType() == InputConstants.Type.KEYSYM && var1.key.getValue() != InputConstants.UNKNOWN.getValue()) {
            var1.setDown(InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), var1.key.getValue()));
         }
      }

   }

   public static void releaseAll() {
      Iterator var0 = ALL.values().iterator();

      while(var0.hasNext()) {
         KeyMapping var1 = (KeyMapping)var0.next();
         var1.release();
      }

   }

   public static void resetMapping() {
      MAP.clear();
      Iterator var0 = ALL.values().iterator();

      while(var0.hasNext()) {
         KeyMapping var1 = (KeyMapping)var0.next();
         MAP.put(var1.key, var1);
      }

   }

   public KeyMapping(String var1, int var2, String var3) {
      this(var1, InputConstants.Type.KEYSYM, var2, var3);
   }

   public KeyMapping(String var1, InputConstants.Type var2, int var3, String var4) {
      super();
      this.name = var1;
      this.key = var2.getOrCreate(var3);
      this.defaultKey = this.key;
      this.category = var4;
      ALL.put(var1, this);
      MAP.put(this.key, this);
      CATEGORIES.add(var4);
   }

   public boolean isDown() {
      return this.isDown;
   }

   public String getCategory() {
      return this.category;
   }

   public boolean consumeClick() {
      if (this.clickCount == 0) {
         return false;
      } else {
         --this.clickCount;
         return true;
      }
   }

   private void release() {
      this.clickCount = 0;
      this.setDown(false);
   }

   public String getName() {
      return this.name;
   }

   public InputConstants.Key getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(InputConstants.Key var1) {
      this.key = var1;
   }

   public int compareTo(KeyMapping var1) {
      return this.category.equals(var1.category) ? I18n.get(this.name).compareTo(I18n.get(var1.name)) : ((Integer)CATEGORY_SORT_ORDER.get(this.category)).compareTo((Integer)CATEGORY_SORT_ORDER.get(var1.category));
   }

   public static Supplier<Component> createNameSupplier(String var0) {
      KeyMapping var1 = (KeyMapping)ALL.get(var0);
      if (var1 == null) {
         return () -> {
            return Component.translatable(var0);
         };
      } else {
         Objects.requireNonNull(var1);
         return var1::getTranslatedKeyMessage;
      }
   }

   public boolean same(KeyMapping var1) {
      return this.key.equals(var1.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputConstants.UNKNOWN);
   }

   public boolean matches(int var1, int var2) {
      if (var1 == InputConstants.UNKNOWN.getValue()) {
         return this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == var2;
      } else {
         return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == var1;
      }
   }

   public boolean matchesMouse(int var1) {
      return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == var1;
   }

   public Component getTranslatedKeyMessage() {
      return this.key.getDisplayName();
   }

   public boolean isDefault() {
      return this.key.equals(this.defaultKey);
   }

   public String saveString() {
      return this.key.getName();
   }

   public void setDown(boolean var1) {
      this.isDown = var1;
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((KeyMapping)var1);
   }
}
