# Agent Injecteur

Agent Injecteur is a special agent, that infiltrates the JVM and performs dependency injection as a stealth operation.

Usual dependency injection frameworks like [Spring](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/beans.html) or [Guice](https://github.com/google/guice/) operate on POJOs, i. e. they do not require framework-specific stuff in your code, that participates in dependency injection. However they require you to use the framework for creating instances of "managed" objects, mandating framework-specific code yet again.
```java
Injector injector = Guice.createInjector(new FooModule());
Foo myFoo = injector.getInstance(Foo.class);
```

Until now.

Agent Injecteur aims to restore the good old Java `new` keyword, performing dependency injection under the hood.
```java
Foo myFoo = new FooImpl();
```

This is an experiment. Let's see where it takes us...
