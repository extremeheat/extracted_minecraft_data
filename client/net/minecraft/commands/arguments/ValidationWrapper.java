package net.minecraft.commands.arguments;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.Validatable;
import net.minecraft.world.level.storage.loot.ValidationContext;
import org.slf4j.Logger;

public class ValidationWrapper<T extends Validatable> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final SimpleCommandExceptionType ERROR_VALIDATION_FAILED = new SimpleCommandExceptionType(Component.translatable("argument.resource.validation_failed"));
   private final Holder<T> result;
   private final ContextKeySet contextKeySet;
   private boolean isValidated;

   private ValidationWrapper(final Holder<T> result, final ContextKeySet contextKeySet) {
      super();
      this.result = result;
      this.contextKeySet = contextKeySet;
   }

   public Optional<String> getRegisteredNameIfPresent() {
      return this.result.getRegisteredNameIfPresent();
   }

   public Holder<T> validateAndGet(final ContextKeySet expectedKeySet) throws CommandSyntaxException {
      if (expectedKeySet != this.contextKeySet) {
         throw new IllegalStateException("Invalid validation key set");
      } else {
         if (!this.isValidated) {
            this.validateStandAlone();
         }

         return this.result;
      }
   }

   private void validate(final ProblemReporter problems) {
      ValidationContext context = new ValidationContext(problems, this.contextKeySet);
      Optional<ResourceKey<T>> maybeKey = this.result.unwrapKey();
      if (maybeKey.isPresent()) {
         ResourceKey<T> key = (ResourceKey)maybeKey.get();
         context = context.enterElement(new ProblemReporter.RootElementPathElement(key), key);
      }

      ((Validatable)this.result.value()).validate(context);
   }

   private void validateStandAlone() throws CommandSyntaxException {
      ProblemReporter.Collector problems = new ProblemReporter.Collector(true);
      this.validate(problems);
      if (problems.hasErrors()) {
         LOGGER.warn("Failed to validate {}: {}", this.result.getRegisteredName(), problems.getTreeReport());
         throw ERROR_VALIDATION_FAILED.create();
      } else {
         this.isValidated = true;
      }
   }

   public interface Provider {
      Provider IMMEDIATE = new Provider() {
         public <V extends Validatable> ValidationWrapper<V> getOrCreate(final Holder<V> element, final ContextKeySet contextKeySet) throws CommandSyntaxException {
            ValidationWrapper<V> result = new ValidationWrapper<V>(element, contextKeySet);
            result.validateStandAlone();
            return result;
         }
      };
      Provider LAZY = ValidationWrapper::new;

      <V extends Validatable> ValidationWrapper<V> getOrCreate(Holder<V> element, ContextKeySet contextKeySet) throws CommandSyntaxException;

      public static class Collecting implements Provider {
         private final Map<Holder<? extends Validatable>, HolderValidators<?>> values = new HashMap();

         public Collecting() {
            super();
         }

         public <V extends Validatable> ValidationWrapper<V> getOrCreate(final Holder<V> element, final ContextKeySet contextKeySet) throws CommandSyntaxException {
            HolderValidators<V> wrappers = (HolderValidators)this.values.computeIfAbsent(element, HolderValidators::new);
            return wrappers.getOrCreateWrapper(contextKeySet);
         }

         public void validateAll(final ProblemReporter.Collector problemReporter) {
            for(HolderValidators<?> value : this.values.values()) {
               value.validateAll(problemReporter);
            }

            if (!problemReporter.hasErrors()) {
               for(HolderValidators<?> value : this.values.values()) {
                  value.markAllAsValidated();
               }
            }

         }

         private static class HolderValidators<V extends Validatable> {
            private final Holder<V> holder;
            private final Map<ContextKeySet, ValidationWrapper<V>> wrappers = new HashMap();

            private HolderValidators(final Holder<V> holder) {
               super();
               this.holder = holder;
            }

            public ValidationWrapper<V> getOrCreateWrapper(final ContextKeySet contextKeySet) {
               return (ValidationWrapper)this.wrappers.computeIfAbsent(contextKeySet, this::createWrapper);
            }

            private ValidationWrapper<V> createWrapper(final ContextKeySet contextKeySet) {
               return new ValidationWrapper<V>(this.holder, contextKeySet);
            }

            public void validateAll(final ProblemReporter problems) {
               for(ValidationWrapper<V> value : this.wrappers.values()) {
                  value.validate(problems);
               }

            }

            public void markAllAsValidated() {
               for(ValidationWrapper<V> value : this.wrappers.values()) {
                  value.isValidated = true;
               }

            }
         }
      }
   }
}
