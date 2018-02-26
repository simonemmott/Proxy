package com.k2.Proxy;

import static org.junit.Assert.*;

import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import example.proxy.AbstractTestingLinkedInvocationHandler;
import example.proxy.AbstractTestingTargettedInvocationHandler;
import example.proxy.Bar;
import example.proxy.Foo;
import example.proxy.TestLoggerLinkedInvocationHandler;
import example.proxy.TestLoggerTargettedInvocationHandler;
import example.proxy.TestMutatorLinkedInvocationHandler;
import example.proxy.TestMutatorTargettedInvocationHandler;

/**
 * Unit test for simple App.
 */
public class ProxyTest 
{
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	@Test
    public void singleHandlerProxyTest()
    {
        Foo foo = new Foo();
        foo.setId(10L);
        foo.setName("Hello");
        foo.setDescription("World!");
        
        AbstractTestingTargettedInvocationHandler<Foo> tester = new TestLoggerTargettedInvocationHandler<Foo>(foo);
        
        Foo proxy = ProxyFactory.staticFactory().getProxy(Foo.class, tester);
        
        assertEquals(Long.valueOf(10), proxy.getId());
        assertEquals("Executing method getId on class example.proxy.Foo_Proxy with args []", tester.getResult());

        assertEquals("Hello", proxy.getName());
        assertEquals("Executing method getName on class example.proxy.Foo_Proxy with args []", tester.getResult());

        assertEquals("World!", proxy.getDescription());
        assertEquals("Executing method getDescription on class example.proxy.Foo_Proxy with args []", tester.getResult());
        
        proxy.setId(20L);
        assertEquals("Executing method setId on class example.proxy.Foo_Proxy with args [20]", tester.getResult());
        assertEquals(Long.valueOf(20), foo.getId());
        
        proxy.setName("Floozy");
        assertEquals("Executing method setName on class example.proxy.Foo_Proxy with args [Floozy]", tester.getResult());
        assertEquals("Floozy", foo.getName());
        
        proxy.setDescription("Woozy!");
        assertEquals("Executing method setDescription on class example.proxy.Foo_Proxy with args [Woozy!]", tester.getResult());
        assertEquals("Woozy!", foo.getDescription());
        

    }

	@Test
    public void singleHandlerExtendedProxyTest()
    {
        Bar bar = new Bar();
        bar.setId(10L);
        bar.setName("Hello");
        bar.setDescription("World!");
        bar.setMessage("Oh yeah!");
        
        AbstractTestingTargettedInvocationHandler<Bar> tester = new TestLoggerTargettedInvocationHandler<Bar>(bar);
        
        Bar proxy = ProxyFactory.staticFactory().getProxy(Bar.class, tester);
        
        assertEquals(Long.valueOf(10), proxy.getId());
        assertEquals("Executing method getId on class example.proxy.Bar_Proxy with args []", tester.getResult());

        assertEquals("Hello", proxy.getName());
        assertEquals("Executing method getName on class example.proxy.Bar_Proxy with args []", tester.getResult());

        assertEquals("World! From Bar", proxy.getDescription());
        assertEquals("Executing method getDescription on class example.proxy.Bar_Proxy with args []", tester.getResult());
        
        assertEquals("Oh yeah!", proxy.getMessage());
        assertEquals("Executing method getMessage on class example.proxy.Bar_Proxy with args []", tester.getResult());
        
        proxy.setId(20L);
        assertEquals("Executing method setId on class example.proxy.Bar_Proxy with args [20]", tester.getResult());
        assertEquals(Long.valueOf(20), bar.getId());
        
        proxy.setName("Floozy");
        assertEquals("Executing method setName on class example.proxy.Bar_Proxy with args [Floozy]", tester.getResult());
        assertEquals("Floozy", bar.getName());
        
        proxy.setDescription("Woozy!");
        assertEquals("Executing method setDescription on class example.proxy.Bar_Proxy with args [Woozy!]", tester.getResult());
        assertEquals("Woozy! From Bar", bar.getDescription());
        
        proxy.setMessage(null);
        assertEquals("Executing method setMessage on class example.proxy.Bar_Proxy with args [null]", tester.getResult());
        assertNull(bar.getMessage());
        

    }

	@Test
    public void chainedHandlerProxyTest()
    {
        Foo foo = new Foo();
        foo.setId(10L);
        foo.setName("Hello");
        foo.setDescription("World!");
        
        
        Foo proxy = ProxyFactory.staticFactory().getProxy(foo, TestMutatorTargettedInvocationHandler.class, TestLoggerTargettedInvocationHandler.class);
        
        assertEquals(Long.valueOf(10), proxy.getId());
        assertEquals("Executing method getId on class example.proxy.Foo_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getId on class example.proxy.Foo_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));

        assertEquals("Hello", proxy.getName());
        assertEquals("Executing method getName on class example.proxy.Foo_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getName on class example.proxy.Foo_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));

        assertEquals("World!", proxy.getDescription());
        assertEquals("Executing method getDescription on class example.proxy.Foo_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getDescription on class example.proxy.Foo_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        
        proxy.setId(20L);
        assertEquals("Executing method setId on class example.proxy.Foo_Proxy with args [20]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setId on class example.proxy.Foo_Proxy Mutating 20 to 30", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals(Long.valueOf(30), foo.getId());
        
        proxy.setName("Floozy");
        assertEquals("Executing method setName on class example.proxy.Foo_Proxy with args [Floozy]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setName on class example.proxy.Foo_Proxy Mutating Floozy to Floozy Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Floozy Mutated!", foo.getName());
        
        proxy.setDescription("Woozy!");
        assertEquals("Executing method setDescription on class example.proxy.Foo_Proxy with args [Woozy!]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setDescription on class example.proxy.Foo_Proxy Mutating Woozy! to Woozy! Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Woozy! Mutated!", foo.getDescription());
        

    }

	@Test
    public void linkedHandlerProxyTest()
    {
        Foo foo = new Foo();
        foo.setId(10L);
        foo.setName("Hello");
        foo.setDescription("World!");
        
        
        Foo proxy = ProxyFactory.staticFactory().getProxy(foo, TestMutatorTargettedInvocationHandler.class, TestLoggerLinkedInvocationHandler.class);
        
        assertEquals(Long.valueOf(10), proxy.getId());
        assertEquals("Executing method getId on class example.proxy.Foo_Proxy with args []", 
        		AbstractTestingLinkedInvocationHandler.getResult(TestLoggerLinkedInvocationHandler.class));
        assertEquals("Executing method getId on class example.proxy.Foo_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));

        assertEquals("Hello", proxy.getName());
        assertEquals("Executing method getName on class example.proxy.Foo_Proxy with args []", 
        		AbstractTestingLinkedInvocationHandler.getResult(TestLoggerLinkedInvocationHandler.class));
        assertEquals("Executing method getName on class example.proxy.Foo_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));

        assertEquals("World!", proxy.getDescription());
        assertEquals("Executing method getDescription on class example.proxy.Foo_Proxy with args []", 
        		AbstractTestingLinkedInvocationHandler.getResult(TestLoggerLinkedInvocationHandler.class));
        assertEquals("Executing method getDescription on class example.proxy.Foo_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        
        proxy.setId(20L);
        assertEquals("Executing method setId on class example.proxy.Foo_Proxy with args [20]", 
        		AbstractTestingLinkedInvocationHandler.getResult(TestLoggerLinkedInvocationHandler.class));
        assertEquals("Executing method setId on class example.proxy.Foo_Proxy Mutating 20 to 30", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals(Long.valueOf(30), foo.getId());
        
        proxy.setName("Floozy");
        assertEquals("Executing method setName on class example.proxy.Foo_Proxy with args [Floozy]", 
        		AbstractTestingLinkedInvocationHandler.getResult(TestLoggerLinkedInvocationHandler.class));
        assertEquals("Executing method setName on class example.proxy.Foo_Proxy Mutating Floozy to Floozy Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Floozy Mutated!", foo.getName());
        
        proxy.setDescription("Woozy!");
        assertEquals("Executing method setDescription on class example.proxy.Foo_Proxy with args [Woozy!]", 
        		AbstractTestingLinkedInvocationHandler.getResult(TestLoggerLinkedInvocationHandler.class));
        assertEquals("Executing method setDescription on class example.proxy.Foo_Proxy Mutating Woozy! to Woozy! Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Woozy! Mutated!", foo.getDescription());
        

    }


	@Test
    public void chainedHandlerExtendedProxyTest()
    {
        Bar bar = new Bar();
        bar.setId(10L);
        bar.setName("Hello");
        bar.setDescription("World!");
        bar.setMessage("Oh yeah!");
        
        
        Bar proxy = ProxyFactory.staticFactory().getProxy(bar, TestMutatorTargettedInvocationHandler.class, TestLoggerTargettedInvocationHandler.class);
        
        assertEquals(Long.valueOf(10), proxy.getId());
        assertEquals("Executing method getId on class example.proxy.Bar_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getId on class example.proxy.Bar_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));

        assertEquals("Hello", proxy.getName());
        assertEquals("Executing method getName on class example.proxy.Bar_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getName on class example.proxy.Bar_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));

        assertEquals("World! From Bar", proxy.getDescription());
        assertEquals("Executing method getDescription on class example.proxy.Bar_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getDescription on class example.proxy.Bar_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        
        assertEquals("Oh yeah!", proxy.getMessage());
        assertEquals("Executing method getMessage on class example.proxy.Bar_Proxy with args []", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method getMessage on class example.proxy.Bar_Proxy no arrguments to Mutate", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        
        proxy.setId(20L);
        assertEquals("Executing method setId on class example.proxy.Bar_Proxy with args [20]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setId on class example.proxy.Bar_Proxy Mutating 20 to 30", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals(Long.valueOf(30), bar.getId());
        
        proxy.setName("Floozy");
        assertEquals("Executing method setName on class example.proxy.Bar_Proxy with args [Floozy]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setName on class example.proxy.Bar_Proxy Mutating Floozy to Floozy Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Floozy Mutated!", bar.getName());
        
        proxy.setDescription("Woozy!");
        assertEquals("Executing method setDescription on class example.proxy.Bar_Proxy with args [Woozy!]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setDescription on class example.proxy.Bar_Proxy Mutating Woozy! to Woozy! Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Woozy! Mutated! From Bar", bar.getDescription());
        
        proxy.setMessage("Ooo!");
        assertEquals("Executing method setMessage on class example.proxy.Bar_Proxy with args [Ooo!]", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestLoggerTargettedInvocationHandler.class));
        assertEquals("Executing method setMessage on class example.proxy.Bar_Proxy Mutating Ooo! to Ooo! Mutated!", 
        		AbstractTestingTargettedInvocationHandler.getResult(TestMutatorTargettedInvocationHandler.class));
        assertEquals("Ooo! Mutated!", bar.getMessage());
        

    }

}
