package net.minecraft.client;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

public class KeyMapping implements Comparable<KeyMapping> {
   private static final Map<String, KeyMapping> ALL = Maps.newHashMap();
   private static final Map<InputConstants.Key, List<KeyMapping>> MAP = Maps.newHashMap();
   private final String name;
   private final InputConstants.Key defaultKey;
   private final Category category;
   protected InputConstants.Key key;
   private boolean isDown;
   private int clickCount;
   private final int order;

   public static void click(final InputConstants.Key key) {
      forAllKeyMappings(key, (keyMapping) -> ++keyMapping.clickCount);
   }

   public static void set(final InputConstants.Key key, final boolean state) {
      forAllKeyMappings(key, (keyMapping) -> keyMapping.setDown(state));
   }

   private static void forAllKeyMappings(final InputConstants.Key key, final Consumer<KeyMapping> operation) {
      List<KeyMapping> keyMappings = (List)MAP.get(key);
      if (keyMappings != null && !keyMappings.isEmpty()) {
         for(KeyMapping keyMapping : keyMappings) {
            operation.accept(keyMapping);
         }
      }

   }

   public static void setAll() {
      Window window = Minecraft.getInstance().getWindow();

      for(KeyMapping keyMapping : ALL.values()) {
         if (keyMapping.shouldSetOnIngameFocus()) {
            keyMapping.setDown(InputConstants.isKeyDown(window, keyMapping.key.getValue()));
         }
      }

   }

   public static void releaseAll() {
      for(KeyMapping keyMapping : ALL.values()) {
         keyMapping.release();
      }

   }

   public static void restoreToggleStatesOnScreenClosed() {
      for(KeyMapping keyMapping : ALL.values()) {
         if (keyMapping instanceof ToggleKeyMapping toggleKeyMapping) {
            if (toggleKeyMapping.shouldRestoreStateOnScreenClosed()) {
               toggleKeyMapping.setDown(true);
            }
         }
      }

   }

   public static void resetToggleKeys() {
      for(KeyMapping keyMapping : ALL.values()) {
         if (keyMapping instanceof ToggleKeyMapping toggleKeyMapping) {
            toggleKeyMapping.reset();
         }
      }

   }

   public static void resetMapping() {
      MAP.clear();

      for(KeyMapping keyMapping : ALL.values()) {
         keyMapping.registerMapping(keyMapping.key);
      }

   }

   public KeyMapping(final String name, final int keysym, final Category category) {
      this(name, InputConstants.Type.KEYSYM, keysym, category);
   }

   public KeyMapping(final String name, final InputConstants.Type type, final int value, final Category category) {
      this(name, type, value, category, 0);
   }

   public KeyMapping(final String name, final InputConstants.Type type, final int value, final Category category, final int order) {
      super();
      this.name = name;
      this.key = type.getOrCreate(value);
      this.defaultKey = this.key;
      this.category = category;
      this.order = order;
      ALL.put(name, this);
      this.registerMapping(this.key);
   }

   public boolean isDown() {
      return this.isDown;
   }

   public Category getCategory() {
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

   protected void release() {
      this.clickCount = 0;
      this.setDown(false);
   }

   protected boolean shouldSetOnIngameFocus() {
      return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() != InputConstants.UNKNOWN.getValue();
   }

   public String getName() {
      return this.name;
   }

   public InputConstants.Key getDefaultKey() {
      return this.defaultKey;
   }

   public void setKey(final InputConstants.Key key) {
      this.key = key;
   }

   public int compareTo(final KeyMapping o) {
      if (this.category == o.category) {
         return this.order == o.order ? I18n.get(this.name).compareTo(I18n.get(o.name)) : Integer.compare(this.order, o.order);
      } else {
         return Integer.compare(KeyMapping.Category.SORT_ORDER.indexOf(this.category), KeyMapping.Category.SORT_ORDER.indexOf(o.category));
      }
   }

   public static Supplier<Component> createNameSupplier(final String key) {
      KeyMapping map = (KeyMapping)ALL.get(key);
      if (map == null) {
         return () -> Component.translatable(key);
      } else {
         Objects.requireNonNull(map);
         return map::getTranslatedKeyMessage;
      }
   }

   public boolean same(final KeyMapping that) {
      return this.key.equals(that.key);
   }

   public boolean isUnbound() {
      return this.key.equals(InputConstants.UNKNOWN);
   }

   public boolean matches(final KeyEvent event) {
      if (event.key() == InputConstants.UNKNOWN.getValue()) {
         return this.key.getType() == InputConstants.Type.SCANCODE && this.key.getValue() == event.scancode();
      } else {
         return this.key.getType() == InputConstants.Type.KEYSYM && this.key.getValue() == event.key();
      }
   }

   public boolean matchesMouse(final MouseButtonEvent event) {
      return this.key.getType() == InputConstants.Type.MOUSE && this.key.getValue() == event.button();
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

   public void setDown(final boolean down) {
      this.isDown = down;
   }

   private void registerMapping(final InputConstants.Key key) {
      ((List)MAP.computeIfAbsent(key, (k) -> new ArrayList())).add(this);
   }

   public static @Nullable KeyMapping get(final String name) {
      return (KeyMapping)ALL.get(name);
   }

   public static record Category(Identifier id) {
      private static final List<Category> SORT_ORDER = new ArrayList();
      public static final Category MOVEMENT = register("movement");
      public static final Category MISC = register("misc");
      public static final Category MULTIPLAYER = register("multiplayer");
      public static final Category GAMEPLAY = register("gameplay");
      public static final Category INVENTORY = register("inventory");
      public static final Category CREATIVE = register("creative");
      public static final Category SPECTATOR = register("spectator");
      public static final Category DEBUG = register("debug");

      public Category {
         super();
      }

      private static Category register(final String name) {
         return register(Identifier.withDefaultNamespace(name));
      }

      public static Category register(final Identifier id) {
         Category category = new Category(id);
         if (SORT_ORDER.contains(category)) {
            throw new IllegalArgumentException(String.format(Locale.ROOT, "Category '%s' is already registered.", id));
         } else {
            SORT_ORDER.add(category);
            return category;
         }
      }

      public Component label() {
         return Component.translatable(this.id.toLanguageKey("key.category"));
      }
   }
}
