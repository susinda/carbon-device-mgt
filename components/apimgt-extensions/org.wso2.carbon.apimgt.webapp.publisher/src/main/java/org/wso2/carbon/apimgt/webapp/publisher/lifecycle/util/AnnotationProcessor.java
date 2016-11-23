/*
 * Copyright 2005-2015 WSO2, Inc. (http://wso2.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.apimgt.webapp.publisher.lifecycle.util;

import io.swagger.annotations.SwaggerDefinition;
import org.apache.catalina.core.StandardContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wso2.carbon.apimgt.webapp.publisher.APIPublisherUtil;
import org.wso2.carbon.apimgt.webapp.publisher.config.APIResource;
import org.wso2.carbon.apimgt.webapp.publisher.config.APIResourceConfiguration;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AnnotationProcessor {

    private static final Log log = LogFactory.getLog(AnnotationProcessor.class);

    private static final String AUTH_TYPE = "Any";
    private static final String STRING_ARR = "string_arr";
    private static final String STRING = "string";
    private static final String PACKAGE_ORG_APACHE = "org.apache";
    private static final String PACKAGE_ORG_CODEHAUS = "org.codehaus";
    private static final String PACKAGE_ORG_SPRINGFRAMEWORK = "org.springframework";
    private static final String WILD_CARD = "/*";

    private static final String SWAGGER_ANNOTATIONS_INFO = "info";
    private static final String SWAGGER_ANNOTATIONS_TAGS = "tags";
    private static final String SWAGGER_ANNOTATIONS_EXTENSIONS = "extensions";
    private static final String SWAGGER_ANNOTATIONS_PROPERTIES = "properties";
    private static final String SWAGGER_ANNOTATIONS_PROPERTIES_NAME = "name";
    private static final String SWAGGER_ANNOTATIONS_PROPERTIES_VERSION = "version";
    private static final String SWAGGER_ANNOTATIONS_PROPERTIES_CONTEXT = "context";
    private static final String SWAGGER_ANNOTATIONS_PROPERTIES_VALUE = "value";


    private StandardContext context;
    private Method[] pathClazzMethods;
    private Class<Path> pathClazz;
    private ClassLoader classLoader;
    private ServletContext servletContext;
    private Class<SwaggerDefinition> apiClazz;
    private Class<Consumes> consumesClass;
    private Class<Produces> producesClass;
    private Class<io.swagger.annotations.Info> infoClass;
    private Class<io.swagger.annotations.Tag> tagClass;
    private Class<io.swagger.annotations.Extension> extensionClass;
    private Class<io.swagger.annotations.ExtensionProperty> extensionPropertyClass;

    public AnnotationProcessor(final StandardContext context) {
        servletContext = context.getServletContext();
        classLoader = servletContext.getClassLoader();
        try {
            pathClazz = (Class<Path>) classLoader.loadClass(Path.class.getName());
            consumesClass = (Class<Consumes>) classLoader.loadClass(Consumes.class.getName());
            producesClass = (Class<Produces>) classLoader.loadClass(Produces.class.getName());
            apiClazz= (Class<SwaggerDefinition>)classLoader.loadClass((SwaggerDefinition.class.getName()));
            infoClass = (Class<io.swagger.annotations.Info>)classLoader
                    .loadClass((io.swagger.annotations.Info.class.getName()));
            tagClass = (Class<io.swagger.annotations.Tag>)classLoader
                    .loadClass((io.swagger.annotations.Tag.class.getName()));
            extensionClass = (Class<io.swagger.annotations.Extension>)classLoader
                    .loadClass((io.swagger.annotations.Extension.class.getName()));
            extensionPropertyClass = (Class<io.swagger.annotations.ExtensionProperty>)classLoader
                    .loadClass((io.swagger.annotations.ExtensionProperty.class.getName()));
        } catch (ClassNotFoundException e) {
            log.error("An error has occurred while loading classes ", e);
        }
    }

    public Set<String> scanStandardContext(String className) throws IOException {
        ExtendedAnnotationDB db = new ExtendedAnnotationDB();
        db.addIgnoredPackages(PACKAGE_ORG_APACHE);
        db.addIgnoredPackages(PACKAGE_ORG_CODEHAUS);
        db.addIgnoredPackages(PACKAGE_ORG_SPRINGFRAMEWORK);
        URL classPath = findWebInfClassesPath(servletContext);
        db.scanArchives(classPath);
        return db.getAnnotationIndex().get(className);
    }

    public List<APIResourceConfiguration> extractAPIInfo(final ServletContext servletContext, Set<String> entityClasses)
            throws ClassNotFoundException {
        List<APIResourceConfiguration> apiResourceConfigs = new ArrayList<APIResourceConfiguration>();
        if (entityClasses != null && !entityClasses.isEmpty()) {
            for (final String className : entityClasses) {
                APIResourceConfiguration apiResourceConfiguration =
                        AccessController.doPrivileged(new PrivilegedAction<APIResourceConfiguration>() {
                            public APIResourceConfiguration run() {
                                Class<?> clazz = null;
                                APIResourceConfiguration apiResourceConfig = null;
                                try {
                                    clazz = classLoader.loadClass(className);
                                    Annotation swaggerDefinition = clazz.getAnnotation(apiClazz);
                                    List<APIResource> resourceList;
                                    if (swaggerDefinition != null) {
                                        if (log.isDebugEnabled()) {
                                            log.debug("Application Context root = " + servletContext.getContextPath());
                                        }
                                        try {
                                            apiResourceConfig = processAPIAnnotation(swaggerDefinition);
                                            if(apiResourceConfig != null){
                                                String rootContext = servletContext.getContextPath();
                                                pathClazzMethods = pathClazz.getMethods();
                                                Annotation rootContectAnno = clazz.getAnnotation(pathClazz);
                                                String subContext;
                                                if (rootContectAnno != null) {
                                                    subContext = invokeMethod(pathClazzMethods[0], rootContectAnno
                                                            , STRING);
                                                    if (subContext != null && !subContext.isEmpty()) {
                                                        if (subContext.trim().startsWith("/")) {
                                                            rootContext = rootContext + subContext;
                                                        } else {
                                                            rootContext = rootContext + "/" + subContext;
                                                        }
                                                    }
                                                    if (log.isDebugEnabled()) {
                                                        log.debug("API Root  Context = " + rootContext);
                                                    }
                                                }
                                                Method[] annotatedMethods = clazz.getDeclaredMethods();
                                                resourceList = getApiResources(rootContext, annotatedMethods);
                                                apiResourceConfig.setResources(resourceList);
                                            }

                                        } catch (Throwable throwable) {
                                            log.error("Error encountered while scanning for annotations", throwable);
                                        }
                                    }
                                } catch (ClassNotFoundException e1) {
                                    String msg = "Failed to load service class " + className + " for publishing APIs." +
                                            " This API will not be published.";
                                    log.error(msg);
                                } catch (RuntimeException e) {
                                    log.error("Unexpected error has been occurred while publishing "+ className
                                            +"hence, this API will not be published.");
                                    throw new RuntimeException(e);
                                }
                                return apiResourceConfig;
                            }
                        });
                if(apiResourceConfiguration !=null)
                    apiResourceConfigs.add(apiResourceConfiguration);
            }
        }
        return apiResourceConfigs;
    }

    /**
     * Get Resources for each API
     *
     * @param resourceRootContext
     * @param annotatedMethods
     * @return
     * @throws Throwable
     */
    private List<APIResource> getApiResources(String resourceRootContext, Method[] annotatedMethods) throws Throwable {
        List<APIResource> resourceList = new ArrayList<>();
        String subCtx = null;
        for (Method method : annotatedMethods) {
            Annotation[] annotations = method.getDeclaredAnnotations();
            APIResource resource = new APIResource();
            if (isHttpMethodAvailable(annotations)) {
                Annotation methodContextAnno = method.getAnnotation(pathClazz);
                if (methodContextAnno != null) {
                    subCtx = invokeMethod(pathClazzMethods[0], methodContextAnno, STRING);
                } else {
                    subCtx = WILD_CARD;
                }
                resource.setUriTemplate(makeContextURLReady(subCtx));
                resource.setUri(APIPublisherUtil.getServerBaseUrl() + makeContextURLReady(resourceRootContext) +
                        makeContextURLReady(subCtx));
                resource.setAuthType(AUTH_TYPE);
                for (int i = 0; i < annotations.length; i++) {
                    processHTTPMethodAnnotation(resource, annotations[i]);
                    if (annotations[i].annotationType().getName().equals(Consumes.class.getName())) {
                        Method[] consumesClassMethods = consumesClass.getMethods();
                        Annotation consumesAnno = method.getAnnotation(consumesClass);
                        resource.setConsumes(invokeMethod(consumesClassMethods[0], consumesAnno, STRING_ARR));
                    }
                    if (annotations[i].annotationType().getName().equals(Produces.class.getName())) {
                        Method[] producesClassMethods = producesClass.getMethods();
                        Annotation producesAnno = method.getAnnotation(producesClass);
                        resource.setProduces(invokeMethod(producesClassMethods[0], producesAnno, STRING_ARR));
                    }
                }
                resourceList.add(resource);
            }
        }
        return resourceList;
    }

    /**
     * Read Method annotations indicating HTTP Methods
     *
     * @param resource
     * @param annotation
     */
    private void processHTTPMethodAnnotation(APIResource resource, Annotation annotation) {
        if (annotation.annotationType().getName().equals(GET.class.getName())) {
            resource.setHttpVerb(HttpMethod.GET);
        }
        if (annotation.annotationType().getName().equals(POST.class.getName())) {
            resource.setHttpVerb(HttpMethod.POST);
        }
        if (annotation.annotationType().getName().equals(OPTIONS.class.getName())) {
            resource.setHttpVerb(HttpMethod.OPTIONS);
        }
        if (annotation.annotationType().getName().equals(DELETE.class.getName())) {
            resource.setHttpVerb(HttpMethod.DELETE);
        }
        if (annotation.annotationType().getName().equals(PUT.class.getName())) {
            resource.setHttpVerb(HttpMethod.PUT);
        }
    }

    private boolean isHttpMethodAvailable(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().equals(GET.class.getName())) {
                return true;
            } else if (annotation.annotationType().getName().equals(POST.class.getName())) {
                return true;
            } else if (annotation.annotationType().getName().equals(OPTIONS.class.getName())) {
                return true;
            } else if (annotation.annotationType().getName().equals(DELETE.class.getName())) {
                return true;
            } else if (annotation.annotationType().getName().equals(PUT.class.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Iterate API annotation and build API Configuration
     *
     * @param annotation reading @SwaggerDefinition annotation
     * @return APIResourceConfiguration which compose with an API information which has its name, context,version,and tags
     * @throws Throwable
     */
    private APIResourceConfiguration processAPIAnnotation(Annotation annotation) throws Throwable {
        InvocationHandler methodHandler = Proxy.getInvocationHandler(annotation);
        Annotation info = (Annotation) methodHandler.invoke(annotation, apiClazz
                .getMethod(SWAGGER_ANNOTATIONS_INFO,null),null);
        Annotation[] tags = (Annotation[]) methodHandler.invoke(annotation, apiClazz
                .getMethod(SWAGGER_ANNOTATIONS_TAGS,null),null);
        String[] tagNames = new String[tags.length];
        for(int i=0; i<tags.length; i++){
            methodHandler = Proxy.getInvocationHandler(tags[i]);
            tagNames[i]=(String)methodHandler.invoke(tags[i], tagClass
                    .getMethod(SWAGGER_ANNOTATIONS_PROPERTIES_NAME, null),null);
        }
        methodHandler = Proxy.getInvocationHandler(info);
        String version = (String)methodHandler.invoke(info, infoClass
                .getMethod(SWAGGER_ANNOTATIONS_PROPERTIES_VERSION,null),null);
        if("".equals(version))return null;
        Annotation[] apiInfo = (Annotation[])methodHandler.invoke(info, infoClass
                .getMethod(SWAGGER_ANNOTATIONS_EXTENSIONS,null),null);
        methodHandler = Proxy.getInvocationHandler(apiInfo[0]);
        Annotation[] properties =  (Annotation[])methodHandler.invoke(apiInfo[0], extensionClass
                        .getMethod(SWAGGER_ANNOTATIONS_PROPERTIES,null), null);
        APIResourceConfiguration apiResourceConfig = new APIResourceConfiguration();
        for (Annotation property : properties) {
            methodHandler = Proxy.getInvocationHandler(property);
            String key = (String) methodHandler.invoke(property, extensionPropertyClass
                            .getMethod(SWAGGER_ANNOTATIONS_PROPERTIES_NAME, null),
                    null);
            String value = (String) methodHandler.invoke(property, extensionPropertyClass
                            .getMethod(SWAGGER_ANNOTATIONS_PROPERTIES_VALUE, null),null);
            if ("".equals(key)) return null;
            switch (key) {
                case SWAGGER_ANNOTATIONS_PROPERTIES_NAME:
                    if ("".equals(value)) return null;
                    apiResourceConfig.setName(value);
                    break;
                case SWAGGER_ANNOTATIONS_PROPERTIES_CONTEXT:
                    if ("".equals(value)) return null;
                    apiResourceConfig.setContext(value);
                    break;
                default:
                    break;
            }
        }
        apiResourceConfig.setVersion(version);
        apiResourceConfig.setTags(tagNames);
        return apiResourceConfig;
    }

    /**
     * Append '/' to the context and make it URL ready
     *
     * @param context
     * @return
     */
    private String makeContextURLReady(String context) {
        if (context != null && context.length() > 0) {
            if (context.startsWith("/")) {
                return context;
            } else {
                return "/" + context;
            }
        }
        return "";
    }

    /**
     * When an annotation and method is passed, this method invokes that executes said method against the annotation
     *
     * @param method
     * @param annotation
     * @param returnType
     * @return
     * @throws Throwable
     */
    private String invokeMethod(Method method, Annotation annotation, String returnType) throws Throwable {
        InvocationHandler methodHandler = Proxy.getInvocationHandler(annotation);
        switch (returnType) {
            case STRING:
                return (String) methodHandler.invoke(annotation, method, null);
            case STRING_ARR:
                return ((String[]) methodHandler.invoke(annotation, method, null))[0];
            default:
                return null;
        }
    }

    /**
     * Find the URL pointing to "/WEB-INF/classes"  This method may not work in conjunction with IteratorFactory
     * if your servlet container does not extract the /WEB-INF/classes into a real file-based directory
     *
     * @param servletContext
     * @return null if cannot determin /WEB-INF/classes
     */
    public static URL findWebInfClassesPath(ServletContext servletContext)
    {
        String path = servletContext.getRealPath("/WEB-INF/classes");
        if (path == null) return null;
        File fp = new File(path);
        if (fp.exists() == false) return null;
        try
        {
            URI uri = fp.toURI();
            return uri.toURL();
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
