package com.github.oreissig.agentinjecteur

import com.github.oreissig.agentinjecteur.rt.MembersInjector
import spock.lang.Specification

import javax.inject.Inject

class AgentSpec extends Specification {
    def "agent is loaded"() {
        given:
        MembersInjector.register(String, "foo");

        when:
        def foo = new TestFoo()

        then:
        foo.inject1
        foo.inject2
        foo.inject0
        !foo.dontInject
    }
}

class TestFoo extends TestBar {
    @Inject
    String inject1;
    @Inject
    String inject2;

    String dontInject;
}

class TestBar {
    @Inject
    String inject0;
}
