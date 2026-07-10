package com.utp.myapp.shared.infraestructure.config;

// NOTE: With the Angular bundle now copied to src/main/resources/static/ by Maven,
// Spring Boot's default static resource handler serves it automatically at the root path.
// No additional configuration is required here.
//
// Kept this file as a marker so the wiring is documented and discovered.
// The actual mapping is done by Boot's WebMvcAutoConfiguration (ResourceHttpRequestHandler
// for classpath:/static/, classpath:/public/, classpath:/resources/, and classpath:/META-INF/resources/).
