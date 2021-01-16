package org.apache.logging.log4j.core.appender.db.jpa;

import java.lang.reflect.Constructor;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.db.AbstractDatabaseManager;

public final class JpaDatabaseManager extends AbstractDatabaseManager {
   private static final JpaDatabaseManager.JPADatabaseManagerFactory FACTORY = new JpaDatabaseManager.JPADatabaseManagerFactory();
   private final String entityClassName;
   private final Constructor<? extends AbstractLogEventWrapperEntity> entityConstructor;
   private final String persistenceUnitName;
   private EntityManagerFactory entityManagerFactory;
   private EntityManager entityManager;
   private EntityTransaction transaction;

   private JpaDatabaseManager(String var1, int var2, Class<? extends AbstractLogEventWrapperEntity> var3, Constructor<? extends AbstractLogEventWrapperEntity> var4, String var5) {
      super(var1, var2);
      this.entityClassName = var3.getName();
      this.entityConstructor = var4;
      this.persistenceUnitName = var5;
   }

   protected void startupInternal() {
      this.entityManagerFactory = Persistence.createEntityManagerFactory(this.persistenceUnitName);
   }

   protected boolean shutdownInternal() {
      boolean var1 = true;
      if (this.entityManager != null || this.transaction != null) {
         var1 &= this.commitAndClose();
      }

      if (this.entityManagerFactory != null && this.entityManagerFactory.isOpen()) {
         this.entityManagerFactory.close();
      }

      return var1;
   }

   protected void connectAndStart() {
      try {
         this.entityManager = this.entityManagerFactory.createEntityManager();
         this.transaction = this.entityManager.getTransaction();
         this.transaction.begin();
      } catch (Exception var2) {
         throw new AppenderLoggingException("Cannot write logging event or flush buffer; manager cannot create EntityManager or transaction.", var2);
      }
   }

   protected void writeInternal(LogEvent var1) {
      if (this.isRunning() && this.entityManagerFactory != null && this.entityManager != null && this.transaction != null) {
         AbstractLogEventWrapperEntity var2;
         try {
            var2 = (AbstractLogEventWrapperEntity)this.entityConstructor.newInstance(var1);
         } catch (Exception var4) {
            throw new AppenderLoggingException("Failed to instantiate entity class [" + this.entityClassName + "].", var4);
         }

         try {
            this.entityManager.persist(var2);
         } catch (Exception var5) {
            if (this.transaction != null && this.transaction.isActive()) {
               this.transaction.rollback();
               this.transaction = null;
            }

            throw new AppenderLoggingException("Failed to insert record for log event in JPA manager: " + var5.getMessage(), var5);
         }
      } else {
         throw new AppenderLoggingException("Cannot write logging event; JPA manager not connected to the database.");
      }
   }

   protected boolean commitAndClose() {
      boolean var1 = true;

      try {
         if (this.transaction != null && this.transaction.isActive()) {
            this.transaction.commit();
         }
      } catch (Exception var42) {
         if (this.transaction != null && this.transaction.isActive()) {
            this.transaction.rollback();
         }
      } finally {
         this.transaction = null;

         try {
            if (this.entityManager != null && this.entityManager.isOpen()) {
               this.entityManager.close();
            }
         } catch (Exception var40) {
            this.logWarn("Failed to close entity manager while logging event or flushing buffer", var40);
            var1 = false;
         } finally {
            this.entityManager = null;
         }

      }

      return var1;
   }

   public static JpaDatabaseManager getJPADatabaseManager(String var0, int var1, Class<? extends AbstractLogEventWrapperEntity> var2, Constructor<? extends AbstractLogEventWrapperEntity> var3, String var4) {
      return (JpaDatabaseManager)AbstractDatabaseManager.getManager(var0, new JpaDatabaseManager.FactoryData(var1, var2, var3, var4), FACTORY);
   }

   // $FF: synthetic method
   JpaDatabaseManager(String var1, int var2, Class var3, Constructor var4, String var5, Object var6) {
      this(var1, var2, var3, var4, var5);
   }

   private static final class JPADatabaseManagerFactory implements ManagerFactory<JpaDatabaseManager, JpaDatabaseManager.FactoryData> {
      private JPADatabaseManagerFactory() {
         super();
      }

      public JpaDatabaseManager createManager(String var1, JpaDatabaseManager.FactoryData var2) {
         return new JpaDatabaseManager(var1, var2.getBufferSize(), var2.entityClass, var2.entityConstructor, var2.persistenceUnitName);
      }

      // $FF: synthetic method
      JPADatabaseManagerFactory(Object var1) {
         this();
      }
   }

   private static final class FactoryData extends AbstractDatabaseManager.AbstractFactoryData {
      private final Class<? extends AbstractLogEventWrapperEntity> entityClass;
      private final Constructor<? extends AbstractLogEventWrapperEntity> entityConstructor;
      private final String persistenceUnitName;

      protected FactoryData(int var1, Class<? extends AbstractLogEventWrapperEntity> var2, Constructor<? extends AbstractLogEventWrapperEntity> var3, String var4) {
         super(var1);
         this.entityClass = var2;
         this.entityConstructor = var3;
         this.persistenceUnitName = var4;
      }
   }
}
