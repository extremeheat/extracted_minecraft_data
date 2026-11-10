package net.minecraft.commands;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.arguments.ValidationWrapper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.server.permissions.PermissionSetSupplier;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.storage.loot.Validatable;

public interface ParseContext extends PermissionSetSupplier {
   default boolean allowsSelectors() {
      return this.permissions().hasPermission(Permissions.COMMANDS_ENTITY_SELECTORS);
   }

   HolderGetter.Provider holderGetters();

   FeatureFlagSet enabledFeatures();

   <T extends Validatable> ValidationWrapper<T> getOrCreateValidationWrapper(Holder<T> element, ContextKeySet contextKeySet) throws CommandSyntaxException;

   static ParseContext of(final CommandSourceStack sender) {
      FeatureFlagSet enabledFeatures = sender.enabledFeatures();
      return new Simple(sender.permissions(), sender.getServer().reloadableRegistries().lookup().filterFeatures(enabledFeatures), enabledFeatures, ValidationWrapper.Provider.IMMEDIATE);
   }

   static ParseContext of(final ServerPlayer sender) {
      FeatureFlagSet enabledFeatures = sender.level().enabledFeatures();
      return new Simple(sender.permissions(), sender.level().getServer().reloadableRegistries().lookup().filterFeatures(enabledFeatures), enabledFeatures, ValidationWrapper.Provider.IMMEDIATE);
   }

   public static record Simple(PermissionSet permissions, HolderGetter.Provider holderGetters, FeatureFlagSet enabledFeatures, ValidationWrapper.Provider validationWrapperProvider) implements ParseContext {
      public Simple {
         super();
      }

      public <T extends Validatable> ValidationWrapper<T> getOrCreateValidationWrapper(final Holder<T> element, final ContextKeySet contextKeySet) throws CommandSyntaxException {
         return this.validationWrapperProvider.<T>getOrCreate(element, contextKeySet);
      }
   }
}
