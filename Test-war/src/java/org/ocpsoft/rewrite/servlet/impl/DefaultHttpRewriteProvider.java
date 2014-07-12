/*
 * Copyright 2013 <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ocpsoft.rewrite.servlet.impl;

import hu.farcsal.cms.util.Pages;
import hu.farcsal.util.UrlParameters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.servlet.ServletContext;
import org.ocpsoft.common.services.NonEnriching;
import org.ocpsoft.common.services.ServiceLoader;
import org.ocpsoft.common.util.Iterators;
import org.ocpsoft.logging.Logger;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationLoader;
import org.ocpsoft.rewrite.config.Operation;
import org.ocpsoft.rewrite.config.Rule;
import org.ocpsoft.rewrite.context.RewriteState;
import org.ocpsoft.rewrite.param.DefaultParameterValueStore;
import org.ocpsoft.rewrite.param.Parameter;
import org.ocpsoft.rewrite.param.ParameterStore;
import org.ocpsoft.rewrite.param.ParameterValueStore;
import org.ocpsoft.rewrite.servlet.event.BaseRewrite.ServletRewriteFlow;
import org.ocpsoft.rewrite.servlet.http.HttpRewriteProvider;
import org.ocpsoft.rewrite.servlet.http.event.HttpOutboundServletRewrite;
import org.ocpsoft.rewrite.servlet.http.event.HttpServletRewrite;
import org.ocpsoft.rewrite.spi.RuleCacheProvider;
import org.ocpsoft.rewrite.util.ParameterUtils;
import org.ocpsoft.rewrite.util.ServiceLogger;
import org.ocpsoft.urlbuilder.AddressBuilder;

/**
 * Original file: https://github.com/ocpsoft/rewrite/blob/07c653fb09fe362ec5b8196d9cecdef6b865add5/impl-servlet/src/main/java/org/ocpsoft/rewrite/servlet/impl/DefaultHttpRewriteProvider.java
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="mailto:f.zoli@mailbox.hu">Zoltan Farkas</a>
 */
public class DefaultHttpRewriteProvider extends HttpRewriteProvider implements NonEnriching
{
   private static final Logger log = Logger.getLogger(DefaultHttpRewriteProvider.class);
   private volatile ConfigurationLoader loader;
   private volatile List<RuleCacheProvider> ruleCacheProviders;

   private static final UrlParameters HELPER = Pages.getLanguageParameter();
   
   /**
    * Returns whether the rule should be performed.
    * If the rule will be performed, it removes the class parameter from the URL.
    * @return false if the rule is not filtered so can be performed; otherwise true
    */
   private boolean isRuleFiltered(HttpServletRewrite event, Rule rule) {
//       if (event instanceof HttpOutboundServletRewrite) {
//           String eventClass = getClass(event);
//           if (eventClass == null) {
//               removeClass((HttpOutboundServletRewrite)event);
//               return false;
//           }
//           String ruleClass = getClass(rule);
//           if (ruleClass == null) {
//               removeClass((HttpOutboundServletRewrite)event);
//               return false;
//           }
//           System.out.print(eventClass + " - " + ruleClass);
//           boolean enabled = eventClass.equals(ruleClass);
//           if (enabled) {
//               removeClass((HttpOutboundServletRewrite)event);
//           }
//           return !enabled;
//       }
       return false;
   }
   
   /**
    * Removes the class parameter from the URL.
    */
   private void removeClass(HttpOutboundServletRewrite event) {
       String url = event.getAddress().toString();
       System.out.println("removeClass: " + url);
       event.setOutboundAddress(AddressBuilder.create(HELPER.remove(url)));
   }
   
   /**
    * Returns the class of the rule.
    * Test method.
    */
   private String getClass(Rule rule) {
       try {
           String from = "UrlMapping [ id=";
           String k = rule.getId();
           k = k.substring(k.indexOf(from) + from.length());
           if (k.charAt(2) == '-') {
               k = k.substring(0, 2);
               return k;
           }
           return null;
       }
       catch (Exception ex) {
           return null;
       }
   }
   
   /**
    * Returns the class of the event.
    * Test method.
    */
   private String getClass(HttpServletRewrite event) {
       String url = event.getAddress().toString();
       System.out.println("getClass: " + url);
       return HELPER.get(url);
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public void init(ServletContext context)
   {
      if (loader == null)
         synchronized (this) {
            if (loader == null)
               loader = ConfigurationLoader.create(context);
         }

      if (ruleCacheProviders == null)
         synchronized (this) {
            ruleCacheProviders = Iterators
                     .asList(ServiceLoader.load(RuleCacheProvider.class));

            ServiceLogger.logLoadedServices(log, RuleCacheProvider.class, ruleCacheProviders);
         }

      loader.loadConfiguration(context);

   }

   @Override
   public void rewriteHttp(final HttpServletRewrite event)
   {
      ServletContext servletContext = event.getServletContext();
      if (loader == null)
         synchronized (servletContext) {
            if (loader == null)
               loader = ConfigurationLoader.create(servletContext);
         }

      Configuration compiledConfiguration = loader.loadConfiguration(servletContext);
      List<Rule> rules = compiledConfiguration.getRules();

      final EvaluationContextImpl context = new EvaluationContextImpl();

      Object cacheKey = null;
      for (int i = 0; i < ruleCacheProviders.size(); i++) {
         RuleCacheProvider provider = ruleCacheProviders.get(i);

         cacheKey = provider.createKey(event, context);
         final List<Rule> list = provider.get(cacheKey);
         if (list != null && !list.isEmpty())
         {
            log.debug("Using cached ruleset for event [" + event + "] from provider [" + provider + "].");
            for (int j = 0; j < rules.size(); j++)
            {
               Rule rule = rules.get(j);

               context.clear();
               DefaultParameterValueStore values = new DefaultParameterValueStore();
               context.put(ParameterValueStore.class, values);
               context.setState(RewriteState.EVALUATING);

               if (rule.evaluate(event, context))
               {
                  if (handleBindings(event, context, values) && !isRuleFiltered(event, rule))
                  {
                     context.setState(RewriteState.PERFORMING);
                     log.debug("Rule [" + rule + "] matched and will be performed.");

                     List<Operation> preOperations = context.getPreOperations();
                     for (int k = 0; k < preOperations.size(); k++) {
                        preOperations.get(k).perform(event, context);
                     }

                     if (event.getFlow().is(ServletRewriteFlow.HANDLED))
                     {
                        return;
                     }

                     rule.perform(event, context);

                     if (event.getFlow().is(ServletRewriteFlow.HANDLED))
                     {
                        return;
                     }

                     List<Operation> postOperations = context.getPostOperations();
                     for (int k = 0; k < postOperations.size(); k++) {
                        postOperations.get(k).perform(event, context);
                     }

                     if (event.getFlow().is(ServletRewriteFlow.HANDLED))
                     {
                        return;
                     }
                  }
               }
               else
               {
                  break;
               }
            }
         }
      }

      /*
       * Highly optimized loop - for performance reasons. Think before you change this!
       */
      List<Rule> cacheable = new ArrayList<Rule>();
      for (int i = 0; i < rules.size(); i++)
      {
         Rule rule = rules.get(i);

         context.clear();
         DefaultParameterValueStore values = new DefaultParameterValueStore();
         context.put(ParameterValueStore.class, values);

         context.setState(RewriteState.EVALUATING);
         if (rule.evaluate(event, context))
         {
            if (handleBindings(event, context, values) && !isRuleFiltered(event, rule))
            {
               context.setState(RewriteState.PERFORMING);
               log.debug("Rule [" + rule + "] matched and will be performed.");
               cacheable.add(rule);
               List<Operation> preOperations = context.getPreOperations();
               for (int k = 0; k < preOperations.size(); k++) {
                  preOperations.get(k).perform(event, context);
               }

               if (event.getFlow().is(ServletRewriteFlow.HANDLED))
               {
                  break;
               }

               rule.perform(event, context);

               if (event.getFlow().is(ServletRewriteFlow.HANDLED))
               {
                  break;
               }

               List<Operation> postOperations = context.getPostOperations();
               for (int k = 0; k < postOperations.size(); k++) {
                  postOperations.get(k).perform(event, context);
               }

               if (event.getFlow().is(ServletRewriteFlow.HANDLED))
               {
                  break;
               }
            }
         }
      }

      if (!cacheable.isEmpty())
         for (int i = 0; i < ruleCacheProviders.size(); i++) {
            ruleCacheProviders.get(i).put(cacheKey, cacheable);
         }
   }

   private boolean handleBindings(final HttpServletRewrite event, final EvaluationContextImpl context,
            DefaultParameterValueStore values)
   {
      boolean result = true;
      ParameterStore store = (ParameterStore) context.get(ParameterStore.class);

      for (Entry<String, Parameter<?>> entry : store) {
         Parameter<?> parameter = entry.getValue();
         String value = values.get(parameter);

         if (!ParameterUtils.enqueueSubmission(event, context, parameter, value))
         {
            result = false;
            break;
         }
      }
      return result;
   }

   @Override
   public void shutdown(ServletContext context)
   {}

   @Override
   public int priority()
   {
      return 0;
   }
   
}